package com.kotikov.technicalTask.forDrWeb.presentation.AppCardScreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.kotikov.technicalTask.forDrWeb.R
import com.kotikov.technicalTask.forDrWeb.data.ApkLookUpImpl
import com.kotikov.technicalTask.forDrWeb.data.GetAllInstalledAppsRepositoryImpl
import com.kotikov.technicalTask.forDrWeb.data.HashCalculationException
import com.kotikov.technicalTask.forDrWeb.data.HashCalculatorImpl
import com.kotikov.technicalTask.forDrWeb.data.models.APKLookUpResult
import com.kotikov.technicalTask.forDrWeb.presentation.mapErrorCodeToResourceId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppCardViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle,
) : AndroidViewModel(application) {

    private val packageName = savedStateHandle
        .getStateFlow<String?>("packageName", null)

    private val allApks = GetAllInstalledAppsRepositoryImpl(application.applicationContext)
    private val appLookUp = ApkLookUpImpl(application.applicationContext)
    private val hashCalc = HashCalculatorImpl


    private val _appInfo = MutableStateFlow<AppInfoResult>(AppInfoResult.Loading)
    val appInfo: StateFlow<AppInfoResult> = _appInfo.asStateFlow()

    private val _appHash = MutableStateFlow<ApkDetails>(ApkDetails.Loading)
    val appHash: StateFlow<ApkDetails> = _appHash.asStateFlow()

    init {
        fillInAppCard()
    }

    private fun fillInAppCard() {
        viewModelScope.launch(Dispatchers.IO) {
            val packageTempName = packageName.value
            if (packageTempName == null) {
                _appInfo.value = AppInfoResult.Error
                return@launch
            }
            val lookupResult = allApks.appLookup(packageTempName)
            _appInfo.value = AppInfoResult.Success(lookupResult)

            updateHash(packageTempName)
        }
    }

    private fun updateHash(packageTempName: String) {
        _appHash.value = ApkDetails.Loading

        val appInformation = appLookUp.getApkInfo(packageTempName)

        _appHash.value = when (appInformation) {
            is APKLookUpResult.FoundAPKs -> {
                try {
                    val hash = hashCalc.getFileHashSHA_256(appInformation.baseAPK.apkPath)
                    ApkDetails.Success(
                        APKsInfoWithHash(hash, appInformation)
                    )
                } catch (e: HashCalculationException) {
                    ApkDetails.Error(mapErrorCodeToResourceId(e.errorCode))
                }
            }

            is APKLookUpResult.Loading -> {
                ApkDetails.Loading
            }

            is APKLookUpResult.Error -> {
                ApkDetails.Error(R.string.error_nameNotFoundException)
            }
        }
    }
}