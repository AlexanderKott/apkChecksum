package com.kotikov.technicalTask.forDrWeb.domain

import com.kotikov.technicalTask.forDrWeb.data.models.APKLookUpResult
import com.kotikov.technicalTask.forDrWeb.domain.repositories.ApkLookUp
import com.kotikov.technicalTask.forDrWeb.domain.repositories.GetAllInstalledAppsRepository
import com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen.StatedTarget
import com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen.Target
import java.util.TreeSet

class MarkApkAsTargetUseCase(
    private val apkLookupRepository: ApkLookUp,
    private val apksRepository: GetAllInstalledAppsRepository
) {

    operator fun invoke(
        packageName: String,
        targets: MutableList<StatedTarget>,
        onTargetsUpdated: (TreeSet<StatedTarget>) -> Unit,
    ) {
        val info = apkLookupRepository.getApkInfo(packageName)
        val apk = (info as APKLookUpResult.FoundAPKs).baseAPK
        val lookup = apksRepository.appLookup(packageName)

        val target = Target(
            name = lookup.appName,
            packageName = packageName,
            apkPath = apk.apkPath,
            icon = lookup.icon
        )

        val sortedTargets = TreeSet<StatedTarget>()
        sortedTargets.addAll(targets)
        sortedTargets.add(StatedTarget(target))

        onTargetsUpdated(sortedTargets)
    }
}
