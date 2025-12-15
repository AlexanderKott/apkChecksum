package com.kotikov.technicalTask.forDrWeb.domain.repositories

interface HashCalculator {
    fun getFileHashSHA_256(filePath: String): Result<String>
}