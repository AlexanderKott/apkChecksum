package com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.kotikov.technicalTask.forDrWeb.data.ApkLookUpImpl
import com.kotikov.technicalTask.forDrWeb.data.AppChangeObserverRepository
import com.kotikov.technicalTask.forDrWeb.data.GetAllInstalledAppsRepositoryImpl
import com.kotikov.technicalTask.forDrWeb.data.GetSystemInfoImpl
import com.kotikov.technicalTask.forDrWeb.data.HashCalculatorImpl
import com.kotikov.technicalTask.forDrWeb.data.SnapshotsStorageImpl
import com.kotikov.technicalTask.forDrWeb.data.models.AppChangeEvent
import com.kotikov.technicalTask.forDrWeb.domain.MarkApkAsTargetUseCase
import com.kotikov.technicalTask.forDrWeb.domain.RecordSnapshotUseCase
import com.kotikov.technicalTask.forDrWeb.domain.reports.generateHtmlReport
import com.kotikov.technicalTask.forDrWeb.presentation.FileSharer
import com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen.UIStatus.EmptyList
import com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen.UIStatus.Error
import com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen.UIStatus.Loading
import com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen.UIStatus.Ready
import com.kotikov.technicalTask.forDrWeb.presentation.models.AppAction
import com.kotikov.technicalTask.forDrWeb.presentation.models.AppsFilter
import com.kotikov.technicalTask.forDrWeb.presentation.models.ItemsState
import com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.AppCardScreen.saveReportToInternalStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


//диай сюда
class WorkAreaViewModel(application: Application) : AndroidViewModel(application) {

    private val userFilter: MutableStateFlow<AppsFilter> = MutableStateFlow(AppsFilter.ALL)
    private val _targets = MutableStateFlow<List<StatedTarget>>(mutableListOf())
    val targets: StateFlow<List<StatedTarget>> = _targets.asStateFlow()

    private val apksRepository = GetAllInstalledAppsRepositoryImpl(
        application
            .applicationContext
    )

    private val apkLookupRepositoryImpl = ApkLookUpImpl(
        application
            .applicationContext
    )

    //диай
    private val snapshots = SnapshotsStorageImpl
    private val hasCalculator = HashCalculatorImpl
    private val sysInfo by lazy { GetSystemInfoImpl }
    private val shareFiles by lazy { FileSharer(application) }

    private val manualTrigger = MutableSharedFlow<Unit>()

    private val apkChangesTrigger = AppChangeObserverRepository(application.applicationContext)
        .appChanges
        .flowOn(Dispatchers.IO)


    private val statedItems = merge(
        manualTrigger.map { AppAction.RefreshAll },
        apkChangesTrigger.map { changes -> AppAction.Update(changes) })
        .onStart { emit(AppAction.RefreshAll) }
        .scan<AppAction, ItemsState>(ItemsState.Loading) { memState, element ->
            when (element) {
                is AppAction.RefreshAll ->
                    apksRepository
                        .getFullAppList().fold(
                            onSuccess = { list ->
                                ItemsState.Success(list)
                            },
                            onFailure = { e ->
                                ItemsState.Error(e.toString())
                            }
                        )

                is AppAction.Update -> {
                    val packgName = element.change.packageName
                    val oldList = (memState as? ItemsState.Success)?.appsList
                        ?: return@scan memState

                    when (element.change) {
                        is AppChangeEvent.Added -> {
                            apksRepository.appLookup(packgName).fold(
                                onSuccess = { newApp ->
                                    ItemsState.Success(oldList + newApp)
                                },
                                onFailure = { e ->
                                    ItemsState.Error(e.toString())
                                }
                            )
                        }

                        is AppChangeEvent.Changed -> {
                            apksRepository.appLookup(packgName).fold(
                                onSuccess = { updatedApp ->
                                    ItemsState.Success(
                                        oldList
                                            .map {
                                                if (it.packageName == updatedApp.packageName)
                                                    updatedApp
                                                else
                                                    it
                                            })
                                },
                                onFailure = { e ->
                                    ItemsState.Error(e.toString())
                                }
                            )
                        }

                        is AppChangeEvent.Removed -> {
                            ItemsState.Success(
                                oldList
                                    .filter { it.packageName != packgName }
                            )
                        }

                    }
                }
            }

        }.flowOn(Dispatchers.IO)


    val apksList = combine(statedItems, userFilter)
    { statedItems, filter ->
        when (statedItems) {
            is ItemsState.Error -> Error(statedItems.message)
            is ItemsState.Loading -> Loading
            is ItemsState.Success -> {
                if (statedItems.appsList.isEmpty()) {
                    EmptyList
                } else {
                    Ready(statedItems.appsList.filterApps(filter))
                }
            }
        }
    }.flowOn(Dispatchers.IO)
        .distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Loading
        )


    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            manualTrigger.emit(Unit)
        }
    }

    fun filter(filter: AppsFilter) {
        viewModelScope.launch(Dispatchers.IO) {
            userFilter.emit(filter)
        }
    }

    fun recordTargetsSnapshot() {
        viewModelScope.launch(Dispatchers.IO) {
            RecordSnapshotUseCase(
                snapshotsRepository = snapshots,
                hashCalculator = hasCalculator

            ).invoke(
                targetApks = _targets.value,
                onTargetsUpdated = { value ->
                        _targets.value = value
                }
            )
        }
    }

    fun deleteTargets() {
        _targets.value = ArrayList()
        snapshots.clearSnapshots()
    }

    fun markApkAsTarget(packageName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = MarkApkAsTargetUseCase(apkLookupRepositoryImpl, apksRepository)
                .invoke(
                    packageName,
                    _targets.value
                )

            result.onSuccess { targets ->
                _targets.value = targets.toList()
            }
        }
    }

    fun prepareReport() {
        viewModelScope.launch(Dispatchers.IO) {
            val snapshotsStore = snapshots.getSnapshots()
            if (snapshotsStore.isEmpty()) return@launch
            val targetsList = snapshotsStore[0].map { it.target }

            val reportContent = generateHtmlReport(
                sysInfo.getDeviceInfo(),
                targetsList
            )

            saveReportToInternalStorage(
                context = application,
                reportContent = reportContent
            ).getOrNull()?.let { fileToShare ->
                shareFiles.shareFile(fileToShare)
            }
        }
    }


    private fun List<FullAppInfo>.filterApps(filter: AppsFilter): List<FullAppInfo> {
        return when (filter) {
            AppsFilter.USER_ONLY -> filter { !it.isSystemApp && !it.isTechnicalName }
            AppsFilter.ALL -> this
            AppsFilter.SYSTEM_ONLY -> filter { it.isSystemApp && !it.isTechnicalName }
            AppsFilter.SERVICE -> filter { it.isTechnicalName }
            AppsFilter.DEBUG -> filter { it.isDebuggable }
            AppsFilter.MY_TARGETS -> emptyList()
        }
    }
}