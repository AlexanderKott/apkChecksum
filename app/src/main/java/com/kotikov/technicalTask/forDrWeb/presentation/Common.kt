package com.kotikov.technicalTask.forDrWeb.presentation

import com.kotikov.technicalTask.forDrWeb.R
import com.kotikov.technicalTask.forDrWeb.data.HashCalculatorImpl.ERROR_CODE_ALGORITHM_NOT_FOUND
import com.kotikov.technicalTask.forDrWeb.data.HashCalculatorImpl.ERROR_CODE_FILE_NOT_FOUND
import com.kotikov.technicalTask.forDrWeb.data.HashCalculatorImpl.ERROR_CODE_IO_EXCEPTION
import com.kotikov.technicalTask.forDrWeb.data.HashCalculatorImpl.ERROR_CODE_OOM_EXCEPTION
import com.kotikov.technicalTask.forDrWeb.data.HashCalculatorImpl.ERROR_CODE_SECURITY_EXCEPTION
import com.kotikov.technicalTask.forDrWeb.data.HashCalculatorImpl.ERROR_CODE_UNKNOWN_EXCEPTION

fun mapErrorCodeToResourceId(errorCode: Int): Int {
    return when (errorCode) {
        ERROR_CODE_FILE_NOT_FOUND -> R.string.error_file_not_found
        ERROR_CODE_ALGORITHM_NOT_FOUND -> R.string.error_algorithm_not_found
        ERROR_CODE_IO_EXCEPTION -> R.string.error_io_exception
        ERROR_CODE_SECURITY_EXCEPTION -> R.string.error_permission_denied
        ERROR_CODE_OOM_EXCEPTION -> R.string.error_out_of_memory
        ERROR_CODE_UNKNOWN_EXCEPTION -> R.string.error_unknown_hash_error
        else -> R.string.error_unknown_hash_error
    }
}