package com.kotikov.technicalTask.forDrWeb.presentation.HashInfoViewModel

import com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen.StatedTarget

sealed class HashCard {
    object Loading : HashCard()
    data class Success(val payload: StatedTarget) : HashCard()
    object Error : HashCard()
    object DoTakeSnapshot : HashCard()
}