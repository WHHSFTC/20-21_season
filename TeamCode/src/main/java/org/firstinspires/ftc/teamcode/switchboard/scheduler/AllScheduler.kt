package org.firstinspires.ftc.teamcode.switchboard.scheduler

import org.firstinspires.ftc.teamcode.switchboard.shapes.Time
import java.util.*

class AllScheduler(val list: List<HardwareScheduler>): HardwareScheduler {
    override fun update() {
        list.forEach { it.update() }
    }
}