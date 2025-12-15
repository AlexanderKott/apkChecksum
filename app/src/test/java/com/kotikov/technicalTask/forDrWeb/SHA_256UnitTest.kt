package com.kotikov.technicalTask.forDrWeb

import com.kotikov.technicalTask.forDrWeb.data.HashCalculatorImpl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.fail
import org.junit.Test
import java.io.File
import java.io.FileNotFoundException


private val hashCalculator = HashCalculatorImpl

@Suppress("NonAsciiCharacters")
class SHA_256UnitTest {
    @Test
    fun `тест SHA-256 простой текст`() {
        val testContent = "Test data for hash calculation."
        val expectedHash = "d738423b9a18fe6b0de66ddd999308c82975014fd9ce71368ed3f975e425ba1f"
        val tempFile = File.createTempFile("hash_test", ".tmp")

        try {
            tempFile.writeText(testContent)
            val filePath = tempFile.absolutePath

            val actualHashResult = hashCalculator.getFileHashSHA_256(filePath)
            val actualValue = actualHashResult.getOrElse {
                fail()
            }

            assertEquals(expectedHash, actualValue)
        } finally {
            if (tempFile.exists()) {
                tempFile.delete()
            }
        }
    }


    @Test
    fun `тест SHA-256 массив из нулевых байтов`() {
        val nullBytes = ByteArray(100) { 0x00 }
        val expectedHash = "cd00e292c5970d3c5e2f0ffa5171e555bc46bfc4faddfb4a418b6840b86e79a3"

        val tempFile = File.createTempFile("hash_null_bytes_test", ".tmp")
        try {
            tempFile.writeBytes(nullBytes)
            val filePath = tempFile.absolutePath

            val actualHashResult = hashCalculator.getFileHashSHA_256(filePath)
            val actualValue = actualHashResult.getOrElse {
                fail()
            }

            assertEquals(expectedHash, actualValue)

        } finally {
            if (tempFile.exists()) tempFile.delete()
        }
    }

    @Test
    fun `тест SHA-256 массив начала PNG файла`() {
        val pngSignatureBytes = byteArrayOf(
            0x89.toByte(), 0x50.toByte(), 0x4E.toByte(), 0x47.toByte(),
            0x0D.toByte(), 0x0A.toByte(), 0x1A.toByte(), 0x0A.toByte()
        )

        val expectedHash = "4c4b6a3be1314ab86138bef4314dde022e600960d8689a2c8f8631802d20dab6"

        val tempFile = File.createTempFile("hash_png_bytes_test", ".tmp")
        try {
            tempFile.writeBytes(pngSignatureBytes)
            val filePath = tempFile.absolutePath

            val actualHashResult = hashCalculator.getFileHashSHA_256(filePath)
            val actualValue = actualHashResult.getOrElse {
                fail()
            }

            assertEquals(expectedHash, actualValue)

        } finally {
            if (tempFile.exists()) tempFile.delete()
        }
    }


    @Test
    fun `тест SHA-256 пустого файла`() {
        val emptyBytes = ByteArray(0)
        val expectedHash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"

        val tempFile = File.createTempFile("hash_empty_test", ".tmp")
        try {
            tempFile.writeBytes(emptyBytes)

            val filePath = tempFile.absolutePath

            val actualHashResult = hashCalculator.getFileHashSHA_256(filePath)
            val actualValue = actualHashResult.getOrElse {
                fail()
            }

            assertEquals(expectedHash, actualValue)

        } finally {
            if (tempFile.exists()) tempFile.delete()
        }
    }


    @Test
    fun `тест SHA-256 разные файлы дают разные хэши`() {
        val contentA = "This is APK content A."
        val contentB = "This is APK content B."

        val tempFileA = File.createTempFile("hash_A", ".tmp")
        val tempFileB = File.createTempFile("hash_B", ".tmp")

        try {
            tempFileA.writeText(contentA)
            tempFileB.writeText(contentB)

            val hashA = hashCalculator.getFileHashSHA_256(tempFileA.absolutePath)
            val hashB = hashCalculator.getFileHashSHA_256(tempFileB.absolutePath)

            val actualValue1 = hashA.getOrElse {
                fail()
            }

            val actualValue2 = hashB.getOrElse {
                fail()
            }

            assertNotEquals(
                "Хеши должны отличаться для разного содержимого",
                actualValue1, actualValue2
            )

        } finally {
            if (tempFileA.exists()) tempFileA.delete()
            if (tempFileB.exists()) tempFileB.delete()
        }
    }

    @Test
    fun `тест SHA-256 выбрасывание исключения`() {
        val nonExistentPath = "/non/existent/path/to/apk.apk"

        assertThrows(FileNotFoundException::class.java) {
            hashCalculator.getFileHashSHA_256(nonExistentPath).getOrThrow()
        }
    }
    //и другие тесты...

}