package org.firstinspires.ftc.teamcode.switchboard.core

import org.firstinspires.ftc.teamcode.switchboard.observe.Channel
import org.firstinspires.ftc.teamcode.switchboard.observe.Frame
import org.firstinspires.ftc.teamcode.switchboard.scheduler.HardwareScheduler
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time

abstract class Robot(val log: Logger, val config: Config, val name: String) {
    val frame = Channel<Frame>(Frame(0, Time.zero, Time.zero), "Frame", log.out)
    var startTime: Time? = null
    abstract val activities: List<Activity>
    abstract val scheduler: HardwareScheduler

    fun setup() {
        activities.forEach { it.load() }
    }

    fun update() {
        config.read()
        frame.set(Frame.from(frame.get()))
        //scheduler.output()
        log.update()
    }

    fun cleanup() {
        activities.forEach { it.cleanup() }
        scheduler.output(true)
    }

    override fun toString(): String = name
}