package org.firstinspires.ftc.teamcode.switchboard.core

import org.firstinspires.ftc.teamcode.switchboard.scheduler.HardwareScheduler
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time

abstract class Robot(val logger: Logger, val config: Config, val name: String) {
    var startTime: Time? = null
    abstract val activities: List<Activity>
    abstract val scheduler: HardwareScheduler

    fun setup() {
        activities.forEach { it.load() }
    }

    fun update() {
        config.read()
        scheduler.output()
        logger.update()
    }

    fun cleanup() {
        activities.forEach { it.cleanup() }
        scheduler.output(true)
    }

    override fun toString(): String = name
}