package com.kotikov.technicalTask.forDrWeb.data

import com.kotikov.technicalTask.forDrWeb.domain.repositories.HashCalculator
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


object HashCalculatorImpl : HashCalculator {

    private const val ALGORITHM = "SHA-256"
    private const val BUFFER_SIZE_B = 4096
    private const val END_OF_FILE = -1
    private const val OFFSET = 0

    private const val DOUBLE_COEFFICIENT = 2
    private const val MASK_255 = 255
    private const val LINE_LENGTH = 1

    override fun getFileHashSHA_256(filePath: String): Result<String> {
        val messageDigest: MessageDigest = try {
            MessageDigest.getInstance(ALGORITHM)
        } catch (e: NoSuchAlgorithmException) {
            return Result.failure(e)
        }

        return try {
            val file = File(filePath)
            val buffer = ByteArray(BUFFER_SIZE_B)

            FileInputStream(file).use { fis ->
                var numRead: Int
                while (fis.read(buffer).also { numRead = it } != END_OF_FILE) {
                    messageDigest.update(buffer, OFFSET, numRead)
                }
            }

            val bytes = messageDigest.digest()
            Result.success(bytesToHex(bytes))
        } catch (e: FileNotFoundException) {
            Result.failure(e)
        } catch (e: SecurityException) {
            Result.failure(e)
        } catch (e: IOException) {
            Result.failure(e)
        }
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