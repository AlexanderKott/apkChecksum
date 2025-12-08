package com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException


//диай сюда
class WorkAreaViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val REPORT_FILE_NAME = "snapshots_report.html"
    }


    private val filterTrigger: MutableStateFlow<AppsFilter> = MutableStateFlow(AppsFilter.ALL)
    private val isLoading = MutableStateFlow(false)
    private val _targets = MutableStateFlow<MutableList<StatedTarget>>(
        mutableListOf<StatedTarget>()
    )
    val targets: StateFlow<List<StatedTarget>> = _targets.asStateFlow()
    private val allApps = MutableSharedFlow<Result<List<FullAppInfo>>>(replay = 1)


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


    val apksList = combine(allApps, filterTrigger, isLoading)
    { allApps, filter, loading ->
        when {
            loading -> UIStatus.Loading
            filter == AppsFilter.MY_TARGETS -> UIStatus.Ready(listOf())
            allApps.isSuccess -> {
                val values = allApps.getOrNull()
                when {
                    values == null -> UIStatus.Error(0)
                    values.isEmpty() -> UIStatus.EmptyList
                    else -> UIStatus.Ready(values.filterApps(filter))
                }
            }

            else -> UIStatus.Error(0)
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
            isLoading.value = true
            val res = try {
                val apps = apksRepository.getFullAppList()
                Result.success(apps)
            } catch (e: Exception) {
                Result.failure(e)
            } finally {
                isLoading.value = false
            }
            allApps.emit(res)
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
            MarkApkAsTargetUseCase(apkLookupRepositoryImpl, apksRepository)
                .invoke(
                    packageName,
                    _targets.value,
                    { value ->
                        viewModelScope.launch(Dispatchers.Main) {
                            _targets.value.clear()
                            _targets.value.addAll(value)
                        }
                    }
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

            val fileToShare = saveReportToInternalStorage(
                application,
                reportContent
            )
            shareFiles.shareFile(fileToShare)
        }
    }

    private suspend fun saveReportToInternalStorage(
        context: Context,
        reportContent: String
    ): File =
        withContext(Dispatchers.IO) {
            val cacheDir = context.cacheDir
            val file = File(cacheDir, REPORT_FILE_NAME)

            try {
                file.writeText(reportContent, Charsets.UTF_8)
                return@withContext file
            } catch (e: IOException) {
                e.printStackTrace()
                throw e
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