package org.firstinspires.ftc.teamcode.switchboard.scheduler

interface Schedule {
    fun select(f: (Process) -> Unit)

    fun recurse(f: (Process) -> Unit)
}
