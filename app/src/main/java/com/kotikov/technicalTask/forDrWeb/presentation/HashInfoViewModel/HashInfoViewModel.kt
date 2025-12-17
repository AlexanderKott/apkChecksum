package com.kotikov.technicalTask.forDrWeb.presentation.HashInfoViewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotikov.technicalTask.forDrWeb.data.SnapshotsStorageImpl
import com.kotikov.technicalTask.forDrWeb.data.models.TargetsResult
import com.kotikov.technicalTask.forDrWeb.presentation.AppCardScreen.AppCardViewModel.Companion.PACKAGE_NAME_KEY
import com.kotikov.technicalTask.forDrWeb.presentation.HashInfoViewModel.HashCard.Success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn

class HashInfoViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val packageNameFlow = savedStateHandle
        .getStateFlow<String?>(PACKAGE_NAME_KEY, null)

    private val targetsRepository = SnapshotsStorageImpl

    @OptIn(ExperimentalCoroutinesApi::class)
    val appHash: StateFlow<HashCard> = packageNameFlow.flatMapLatest { packageName ->
        flow {
            if (packageName == null) {
                emit(HashCard.Error)
                return@flow
            }
            when (val target = targetsRepository.findAppByPackage(packageName)) {
                is TargetsResult.Found -> emit(Success(target.target))
                is TargetsResult.NotFound -> emit(HashCard.DoTakeSnapshot)
            }
        }
    }.flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HashCard.Loading
        )
}