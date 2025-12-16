package com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.kotikov.technicalTask.forDrWeb.R
import com.kotikov.technicalTask.forDrWeb.data.ApkLookUpImpl
import com.kotikov.technicalTask.forDrWeb.data.GetAllInstalledAppsRepositoryImpl
import com.kotikov.technicalTask.forDrWeb.data.GetSystemInfoImpl
import com.kotikov.technicalTask.forDrWeb.data.HashCalculatorImpl
import com.kotikov.technicalTask.forDrWeb.data.SnapshotsStorageImpl
import com.kotikov.technicalTask.forDrWeb.domain.MarkApkAsTargetUseCase
import com.kotikov.technicalTask.forDrWeb.domain.RecordSnapshotUseCase
import com.kotikov.technicalTask.forDrWeb.domain.reports.generateHtmlReport
import com.kotikov.technicalTask.forDrWeb.presentation.FileSharer
import com.kotikov.technicalTask.forDrWeb.presentation.models.AppsFilter
import com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.AppCardScreen.saveReportToInternalStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

//диай сюда
class WorkAreaViewModel(application: Application) : AndroidViewModel(application) {


    private val filterTrigger: MutableStateFlow<AppsFilter> = MutableStateFlow(AppsFilter.ALL)
    private val isLoading = MutableStateFlow(false)
    private val hasAnyErrors = MutableStateFlow(false)
    private val _targets = MutableStateFlow<MutableList<StatedTarget>>(
        mutableListOf<StatedTarget>()
    )
    val targets: StateFlow<List<StatedTarget>> = _targets.asStateFlow()
    private val allApks = MutableSharedFlow<List<FullAppInfo>>(replay = 1)


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


    val apksList = combine(allApks, filterTrigger, isLoading, hasAnyErrors)
    { allApps, filter, loading, anyErrors ->
        when {
            anyErrors -> UIStatus.Error(R.string.error_unknown)
            loading -> UIStatus.Loading
            filter == AppsFilter.MY_TARGETS -> UIStatus.Ready(listOf())
            allApps.isEmpty() -> UIStatus.EmptyList
            else -> UIStatus.Ready(allApps.filterApps(filter))
        }
    }.distinctUntilChanged()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UIStatus.Loading
        )


    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            hasAnyErrors.value = false
            isLoading.value = true
            allApks.emit(apksRepository.getFullAppList())
            isLoading.value = false
        }
    }


    fun filter(filter: AppsFilter) {
        viewModelScope.launch(Dispatchers.IO) {
            filterTrigger.emit(filter)
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
                    viewModelScope.launch(Dispatchers.Main) {
                        _targets.value = value
                    }
                }
            )
        }
    }

    fun deleteTargets() {
        _targets.value = ArrayList<StatedTarget>()
        snapshots.clearSnapshots()
    }

    fun markApkAsTarget(packageName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = MarkApkAsTargetUseCase(apkLookupRepositoryImpl, apksRepository)
                .invoke(
                    packageName,
                    _targets.value
                )

            result.fold(
                onSuccess = {
                    viewModelScope.launch(Dispatchers.Main) {
                        _targets.value.clear()
                        _targets.value.addAll(it)
                    }
                },
                onFailure = {
                    hasAnyErrors.value = true
                },
            )
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
            AppsFilter.MY_TARGETS -> filter { false }
        }
    }
}