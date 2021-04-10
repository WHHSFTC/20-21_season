package org.firstinspires.ftc.teamcode.switchboard.hardware

import org.firstinspires.ftc.teamcode.switchboard.scheduler.HardwareScheduler
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time

interface HardwareOutput: HardwareScheduler {
    fun output()

    override fun update(stop: Time) {
        output()
    }
}