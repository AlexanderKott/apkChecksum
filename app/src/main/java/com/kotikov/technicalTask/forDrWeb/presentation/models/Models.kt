package com.kotikov.technicalTask.forDrWeb.presentation.models

import com.kotikov.technicalTask.forDrWeb.data.models.AppChangeEvent
import com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen.FullAppInfo

enum class AppsFilter {
    MY_TARGETS,
    ALL,
    USER_ONLY,
    SYSTEM_ONLY,
    SERVICE,
    DEBUG
}


sealed class AppAction {
    object RefreshAll : AppAction()
    data class Update(val change: AppChangeEvent) : AppAction()
}

sealed class ItemsState {
    object Loading : ItemsState()
    data class Success(val appsList: List<FullAppInfo>) : ItemsState()
    data class Error(val message: String) : ItemsState()
}