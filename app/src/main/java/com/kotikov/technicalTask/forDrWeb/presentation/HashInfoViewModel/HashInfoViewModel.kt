package com.kotikov.technicalTask.forDrWeb.presentation.HashInfoViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import com.kotikov.technicalTask.forDrWeb.data.SnapshotsStorageImpl
import com.kotikov.technicalTask.forDrWeb.presentation.AppCardScreen.AppCardViewModel.Companion.PACKAGE_NAME_KEY
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HashInfoViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle,
) : AndroidViewModel(application) {

    private val packageName = savedStateHandle
        .getStateFlow<String?>(PACKAGE_NAME_KEY, null)

    private val target = SnapshotsStorageImpl
        .findByPackage(packageName.value ?: "-")

    private val _appHash = MutableStateFlow<HashCard>(HashCard.Loading)
    val appHash: StateFlow<HashCard> = _appHash.asStateFlow()

    fun fillInHashCard() {
        if (target == null) {
            _appHash.value = HashCard.DoTakeSnapshot
            return
        }

        _appHash.value = HashCard.Success(target)
    }
}