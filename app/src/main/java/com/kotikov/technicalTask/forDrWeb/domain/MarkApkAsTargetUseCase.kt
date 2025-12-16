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
        targets:List<StatedTarget>,
    ) : Result<TreeSet<StatedTarget>> {
        val apkInfo = apkLookupRepository.getApkInfo(packageName)
        val appLookup = apksRepository.appLookup(packageName)

        val apk = apkInfo.getOrElse {
            return Result.failure(it)
        }

        val app = appLookup.getOrElse {
            return Result.failure(it)
        }

        val target = Target(
            name = app.appName,
            packageName = packageName,
            apkPath = apk.baseAPK.apkPath,
            icon = app.icon
        )

        val sortedTargets = TreeSet<StatedTarget>()
        sortedTargets.addAll(targets)
        sortedTargets.add(StatedTarget(target))

      return Result.success(sortedTargets)
    }
}
