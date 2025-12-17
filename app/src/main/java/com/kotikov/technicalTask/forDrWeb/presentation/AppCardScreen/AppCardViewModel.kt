package com.kotikov.technicalTask.forDrWeb.presentation.AppCardScreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.kotikov.technicalTask.forDrWeb.data.ApkLookUpImpl
import com.kotikov.technicalTask.forDrWeb.data.AppChangeObserverRepository
import com.kotikov.technicalTask.forDrWeb.data.GetAllInstalledAppsRepositoryImpl
import com.kotikov.technicalTask.forDrWeb.data.HashCalculatorImpl
import com.kotikov.technicalTask.forDrWeb.data.models.AppChangeEvent
import com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.AppCardScreen.launchAppByPackageName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppCardViewModel(
    private val application: Application,
    savedStateHandle: SavedStateHandle,
) : AndroidViewModel(application) {

    companion object {
        const val PACKAGE_NAME_KEY = "packageName"
        private const val DEFAULT_ERROR = "error"
    }

    private val packageName = savedStateHandle
        .getStateFlow<String?>(PACKAGE_NAME_KEY, null)

    private val allAppsRepository =
        GetAllInstalledAppsRepositoryImpl(application.applicationContext)
    private val apkLookUpRepository = ApkLookUpImpl(application.applicationContext)

    private val appEventsRepository = AppChangeObserverRepository(application.applicationContext)
        .appChanges
        .filter { it.packageName == packageName.value }
        .mapNotNull { value ->
            when (value) {
                is AppChangeEvent.Removed -> CurrentAppUpdate.DELETED
                is AppChangeEvent.Changed -> CurrentAppUpdate.CHANGED
                else -> null
            }
        }
        .flowOn(Dispatchers.IO)

    private val hashCalcRepository = HashCalculatorImpl

    private val _uiEvent = MutableSharedFlow<UiEvent>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val uiEvent = _uiEvent.asSharedFlow()

    private val triggerFlow = MutableSharedFlow<CurrentAppUpdate>(replay = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val appInfo: StateFlow<AppInfoResult> = merge(
        triggerFlow,
        appEventsRepository
    ).onStart {
        emit(CurrentAppUpdate.INIT)
    }.flatMapLatest { value ->
        flow {
            val tempPackageName = packageName.value
            when {
                tempPackageName == null -> {
                    emit(AppInfoResult.Error)
                }

                value == CurrentAppUpdate.INIT ||
                        value == CurrentAppUpdate.CHANGED -> {
                    val appLookupResult = allAppsRepository
                        .appLookup(tempPackageName)

                    appLookupResult.fold(
                        onSuccess = { result ->
                            emit(AppInfoResult.DataReady(result))
                        },
                        onFailure = {
                            emit(AppInfoResult.Error)
                        })
                }

                value == CurrentAppUpdate.DELETED -> {
                    emit(AppInfoResult.AppHasBeenDeleted)
                }
            }
        }.flowOn(Dispatchers.IO)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppInfoResult.Loading
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val appHash: StateFlow<ApkDetails> = appInfo
        .flatMapLatest { result ->
            flow {
                if (result !is AppInfoResult.DataReady) {
                    emit(ApkDetails.Loading)
                    return@flow
                }

                emit(ApkDetails.Loading)

                val pkgName = result.data.packageName

                val details = calcApkHash(pkgName)
                emit(details)
            }.flowOn(Dispatchers.IO)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ApkDetails.Loading
        )


    fun onLaunchAppClicked(packageName: String) {
        viewModelScope.launch {
            val result = launchAppByPackageName(
                context = application.applicationContext,
                packageName = packageName
            )
            result.onSuccess { launched ->
                if (!launched) {
                    _uiEvent.emit(UiEvent.ShowToast("Не удалось найти запускаемую активность."))
                }
            }.onFailure { throwable ->
                _uiEvent.emit(UiEvent.ShowToast("Ошибка запуска: ${throwable.message}"))
            }
        }
    }

    private fun calcApkHash(packageName: String): ApkDetails {
        val appData = apkLookUpRepository.getApkInfo(packageName).getOrElse {
            return ApkDetails.Error(it.message ?: DEFAULT_ERROR)
        }

        val hashStr = hashCalcRepository.getFileHashSHA_256(
            appData
                .baseAPK
                .apkPath
        ).getOrElse {
            return ApkDetails.Error(it.message ?: DEFAULT_ERROR)
        }
        return ApkDetails.Success(APKsInfoWithHash(hashStr, appData))
    }
}