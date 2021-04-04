package org.firstinspires.ftc.teamcode.switchboard.scheduler

interface Schedule {
    fun select(f: (Activity) -> Unit)

    fun recurse(f: (Activity) -> Unit)
    fun makeList(): List<Activity>
}
