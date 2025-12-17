package com.kotikov.technicalTask.forDrWeb.domain.repositories

import com.kotikov.technicalTask.forDrWeb.data.models.TargetsResult
import com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen.StatedTarget

interface SnapshotsStorage {
    fun getSnapshots(): List<MutableList<StatedTarget>>
    fun addSnapshots(snapshots: MutableList<StatedTarget>)
    fun setFirstSnapshot(targets: MutableList<StatedTarget>)
    fun clearSnapshots()
    fun findAppByPackage(packageName: String): TargetsResult
}