package org.firstinspires.ftc.teamcode.switchboard.scheduler

class AllScheduler(val list: List<HardwareScheduler>): HardwareScheduler {
    override fun output(all: Boolean) {
        list.forEach { it.output() }
    }
}