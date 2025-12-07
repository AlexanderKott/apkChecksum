package com.kotikov.technicalTask.forDrWeb.presentation.WorkAreaScreen

data class StatedTarget(
    val target: Target,
    val hashAssertResult: CheckingVerdict = CheckingVerdict.NEW,
) : Comparable<StatedTarget> {
    override fun compareTo(other: StatedTarget): Int {
        return this.target.packageName.compareTo(other.target.packageName)
    }
}