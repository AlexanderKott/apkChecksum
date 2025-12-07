package com.kotikov.technicalTask.forDrWeb.domain

import com.kotikov.technicalTask.forDrWeb.data.HashCalculationException
import com.kotikov.technicalTask.forDrWeb.domain.repositories.HashCalculator
import com.kotikov.technicalTask.forDrWeb.domain.repositories.SnapshotsStorage
import com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen.CheckingVerdict
import com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen.StatedTarget

class RecordSnapshotUseCase(
    private val snapshotsRepository: SnapshotsStorage,
    private val hashCalculator: HashCalculator
) {
    private enum class HashTakes {
        REFERENCE_RECORD, ACTUAL_RECORD
    }

    operator fun invoke(
        targetApks: List<StatedTarget>,
        onTargetsUpdated: (MutableList<StatedTarget>) -> Unit
    ) {
        if (targetApks.isEmpty()) return

        if (snapshotsRepository.getSnapshots().isEmpty()) {
            val targets = targetApks.toMutableList()
            val updatedTargets = updateHashesInfo(
                targets,
                HashTakes.REFERENCE_RECORD
            )
            snapshotsRepository.addSnapshots(updatedTargets)

        } else {
            val updatedTargets = updateHashesInfo(
                snapshotsRepository.getSnapshots()[0],
                HashTakes.ACTUAL_RECORD
            )
            val assertedTargets = assertHashes(updatedTargets)
            snapshotsRepository.setFirstSnapshot(assertedTargets)
            onTargetsUpdated(assertedTargets)
        }
    }

    private fun assertHashes(targets: MutableList<StatedTarget>): MutableList<StatedTarget> {
        val resultSet = mutableListOf<StatedTarget>()
        for (target in targets) {

            val tempTarget = if (target.target.referenceHash == null
                || target.target.actualHash == null
            ) {
                target.copy(hashAssertResult = CheckingVerdict.MISSING_DATA)
            } else if (target.target.referenceHash == target.target.actualHash) {
                target.copy(hashAssertResult = CheckingVerdict.MATCH)
            } else {
                target.copy(hashAssertResult = CheckingVerdict.DIFFERENT)
            }
            resultSet.add(tempTarget)
        }
        return resultSet
    }

    private fun updateHashesInfo(targets: MutableList<StatedTarget>, record: HashTakes)
            : MutableList<StatedTarget> {
        val resultSet = mutableListOf<StatedTarget>()

        for (i in targets.indices) {
            val hash = try {
                hashCalculator.getFileHashSHA_256(targets[i].target.apkPath)
            } catch (e: HashCalculationException) {
                e.printStackTrace()
                null
            }

            var tempTarget = if (record == HashTakes.REFERENCE_RECORD) {
                targets[i].target.copy(
                    referenceHashTimeStamp = System.currentTimeMillis(),
                    referenceHash = hash
                )
            } else {
                targets[i].target.copy(
                    actualHashTimeStamp = System.currentTimeMillis(),
                    actualHash = hash
                )
            }
            resultSet.add(StatedTarget(tempTarget))
        }
        return resultSet
    }
}
