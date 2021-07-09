package org.firstinspires.ftc.teamcode.switchboard.core

import org.firstinspires.ftc.teamcode.switchboard.scheduler.HardwareScheduler
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time

abstract class Robot(val logger: Logger, val config: Configuration, val name: String) {
    var startTime: Time? = null
    protected abstract val activities: MutableList<Activity>
    protected abstract val scheduler: HardwareScheduler
    private var frame = Frame(0, Time.zero, Time.milli(1))

    fun appendActivity(a: Activity): Boolean {
        if (a in activities)
            return false
        activities.add(a)
        a.load()
        return true
    }

    fun prependActivity(a: Activity): Boolean {
        if (a in activities)
            return false
        activities.add(0, a)
        a.load()
        return true
    }

    fun setup() {
        activities.forEach { it.load() }
        scheduler.output(all = true)
        logger.update()
    }

    fun update() {
        frame = Frame.from(frame)
        logger.out["frame"] = frame
        config.read()
        activities.forEach { it.update(frame) }
        scheduler.output()
        logger.update()
    }

    fun cleanup() {
        activities.forEach { it.cleanup() }
        scheduler.output(all = true)
        logger.update()
    }

    override fun toString(): String = name
}