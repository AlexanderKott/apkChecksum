package com.kotikov.technicalTask.forDrWeb.data

import com.kotikov.technicalTask.forDrWeb.domain.repositories.HashCalculator
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class HashCalculationException(
    val errorCode: Int,
    cause: Throwable? = null
) : IOException(cause)


object HashCalculatorImpl : HashCalculator {

    private const val ALGORITHM = "SHA-256"
    private const val BUFFER_SIZE_B = 4096
    private const val END_OF_FILE = -1
    private const val OFFSET = 0

    private const val DOUBLE_COEFFICIENT = 2
    private const val MASK_255 = 255
    private const val LINE_LENGTH = 1

    const val ERROR_CODE_FILE_NOT_FOUND = 1
    const val ERROR_CODE_ALGORITHM_NOT_FOUND = 2
    const val ERROR_CODE_FNF_EXCEPTION = 3
    const val ERROR_CODE_SECURITY_EXCEPTION = 4
    const val ERROR_CODE_IO_EXCEPTION = 5
    const val ERROR_CODE_OOM_EXCEPTION = 6
    const val ERROR_CODE_UNKNOWN_EXCEPTION = 7


    override fun getFileHashSHA_256(filePath: String): String {
        val file = File(filePath)

        if (!file.exists()) {
            throw HashCalculationException(ERROR_CODE_FILE_NOT_FOUND)
        }

        val messageDigest: MessageDigest = try {
            MessageDigest.getInstance(ALGORITHM)
        } catch (e: NoSuchAlgorithmException) {
            throw HashCalculationException(ERROR_CODE_ALGORITHM_NOT_FOUND, e)
        }

        val buffer = ByteArray(BUFFER_SIZE_B)

        try {
            FileInputStream(file).use { fis ->
                var numRead: Int
                while (fis.read(buffer).also { numRead = it } != END_OF_FILE) {
                    messageDigest.update(buffer, OFFSET, numRead)
                }
            }
        } catch (e: FileNotFoundException) {
            throw HashCalculationException(ERROR_CODE_FNF_EXCEPTION, e)
        } catch (e: SecurityException) {
            throw HashCalculationException(ERROR_CODE_SECURITY_EXCEPTION, e)
        } catch (e: IOException) {
            throw HashCalculationException(ERROR_CODE_IO_EXCEPTION, e)
        } catch (e: OutOfMemoryError) {
            throw HashCalculationException(ERROR_CODE_OOM_EXCEPTION, e)
        } catch (e: Exception) {
            throw HashCalculationException(ERROR_CODE_UNKNOWN_EXCEPTION, e)
        }

        val bytes = messageDigest.digest()
        return bytesToHex(bytes)
    }


    private fun bytesToHex(bytes: ByteArray): String {
        val hexString = StringBuilder(DOUBLE_COEFFICIENT * bytes.size)
        for (byte in bytes) {
            val hex = Integer.toHexString(MASK_255 and byte.toInt())
            if (hex.length == LINE_LENGTH) {
                hexString.append('0')
            }
            hexString.append(hex)
        }
        return hexString.toString()
    }
}