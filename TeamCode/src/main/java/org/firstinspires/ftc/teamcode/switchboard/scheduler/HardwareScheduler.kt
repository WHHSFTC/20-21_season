package org.firstinspires.ftc.teamcode.switchboard.scheduler

interface HardwareScheduler {
    fun update()

    object idle : HardwareScheduler {
        override fun update() { }
    }
}
