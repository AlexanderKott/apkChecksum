package com.kotikov.technicalTask.forDrWeb.data

import com.kotikov.technicalTask.forDrWeb.data.models.TargetsResult
import com.kotikov.technicalTask.forDrWeb.domain.repositories.SnapshotsStorage
import com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen.StatedTarget

object SnapshotsStorageImpl : SnapshotsStorage {
    private var snapshots = mutableListOf<MutableList<StatedTarget>>()

    override fun getSnapshots() = snapshots.toList()

    override fun addSnapshots(snapshots: MutableList<StatedTarget>) {
        this.snapshots.add(snapshots)
    }

    override fun setFirstSnapshot(targets: MutableList<StatedTarget>) {
        snapshots[0] = targets  //пока поддерживатся только один кадр
    }

    override fun clearSnapshots() = this.snapshots.clear()

    override fun findAppByPackage(packageName: String): TargetsResult {
        if (snapshots.isEmpty()) return TargetsResult.NotFound

        val firstSnapshot = snapshots[0]
        val target = firstSnapshot.find { it.target.packageName == packageName }
        return if (target == null) {
            TargetsResult.NotFound
        } else {
            TargetsResult.Found(target)
        }


    }

}