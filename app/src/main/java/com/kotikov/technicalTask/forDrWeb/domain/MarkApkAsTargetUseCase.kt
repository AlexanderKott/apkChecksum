package com.kotikov.technicalTask.forDrWeb.domain

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
    ) : Result<TreeSet<StatedTarget>> {
        val apkInfo = apkLookupRepository.getApkInfo(packageName)
        val appLookup = apksRepository.appLookup(packageName)

        val app = apkInfo.getOrElse {
            return Result.failure(it)
        }

        val apk = appLookup.getOrElse {
            return Result.failure(it)
        }

        val target = Target(
            name = apk.appName,
            packageName = packageName,
            apkPath = app.baseAPK.apkPath,
            icon = apk.icon
        )

        val sortedTargets = TreeSet<StatedTarget>()
        sortedTargets.addAll(targets)
        sortedTargets.add(StatedTarget(target))

      return Result.success(sortedTargets)
    }
}
