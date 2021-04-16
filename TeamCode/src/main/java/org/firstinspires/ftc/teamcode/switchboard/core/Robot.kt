package org.firstinspires.ftc.teamcode.switchboard.core

import org.firstinspires.ftc.teamcode.switchboard.scheduler.HardwareScheduler
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time
import org.firstinspires.ftc.teamcode.switchboard.stores.StartPoint
import org.firstinspires.ftc.teamcode.switchboard.stores.*

abstract class Robot(val logger: Logger, val config: Config, val name: String) {
    val frame = StartPoint<Frame>(Frame(0, Time.zero, Time.zero)).tap { this.log(logger.out, "Frame") }
    var startTime: Time? = null
    abstract val activities: List<Activity>
    abstract val scheduler: HardwareScheduler

    fun setup() {
        activities.forEach { it.load() }
    }

    fun update() {
        config.read()
        frame.value = Frame.from(frame.value)
        scheduler.output()
        logger.update()
    }

    fun cleanup() {
        activities.forEach { it.cleanup() }
        scheduler.output(true)
    }

    override fun toString(): String = name
}