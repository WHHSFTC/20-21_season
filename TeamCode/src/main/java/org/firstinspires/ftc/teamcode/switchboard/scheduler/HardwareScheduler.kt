package org.firstinspires.ftc.teamcode.switchboard.scheduler

interface HardwareScheduler {
    fun output(all: Boolean = false)

    object idle : HardwareScheduler {
        override fun output(all: Boolean) { }
    }
}
