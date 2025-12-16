package com.kotikov.technicalTask.forDrWeb.presentation.AppCardScreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.kotikov.technicalTask.forDrWeb.data.ApkLookUpImpl
import com.kotikov.technicalTask.forDrWeb.data.GetAllInstalledAppsRepositoryImpl
import com.kotikov.technicalTask.forDrWeb.data.HashCalculatorImpl
import com.kotikov.technicalTask.forDrWeb.ui.theme.compose.screens.AppCardScreen.launchAppByPackageName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppCardViewModel(
    private val application: Application,
    savedStateHandle: SavedStateHandle,
) : AndroidViewModel(application) {

    companion object{
        const val PACKAGE_NAME_KEY = "packageName"
        private const val DEFAULT_ERROR = "error"
    }

    private val packageName = savedStateHandle
        .getStateFlow<String?>(PACKAGE_NAME_KEY, null)

    private val allApks = GetAllInstalledAppsRepositoryImpl(application.applicationContext)
    private val appLookUp = ApkLookUpImpl(application.applicationContext)
    private val hashCalc = HashCalculatorImpl


    private val _appInfo = MutableStateFlow<AppInfoResult>(AppInfoResult.Loading)
    val appInfo: StateFlow<AppInfoResult> = _appInfo.asStateFlow()

    private val _appHash = MutableStateFlow<ApkDetails>(ApkDetails.Loading)
    val appHash: StateFlow<ApkDetails> = _appHash.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val uiEvent = _uiEvent.asSharedFlow()


    fun fillInAppCard() {
        viewModelScope.launch(Dispatchers.IO) {
            val packageTempName = packageName.value
            if (packageTempName == null) {
                _appInfo.value = AppInfoResult.Error
                return@launch
            }

            val lookupResult = allApks.appLookup(packageTempName)
            lookupResult.fold(
                onSuccess = {
                    _appInfo.value = AppInfoResult.Success(it)
                    updateHash(packageTempName)
                },
                onFailure = {
                    _appInfo.value = AppInfoResult.Error
                }
            )
        }
    }


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


    private fun updateHash(packageTempName: String) {
        _appHash.value = ApkDetails.Loading

        val appInformation = appLookUp.getApkInfo(packageTempName).getOrElse {
            _appHash.value = ApkDetails.Error(it.message ?: DEFAULT_ERROR)
            return
        }

        val hash = hashCalc.getFileHashSHA_256(appInformation.baseAPK.apkPath).getOrElse {
            _appHash.value = ApkDetails.Error(it.message ?: DEFAULT_ERROR)
            return
        }

        _appHash.value =  ApkDetails.Success(
            APKsInfoWithHash(hash,appInformation)
        )
    }
}