package org.firstinspires.ftc.teamcode.switchboard.core

import com.qualcomm.hardware.lynx.LynxModule
import org.firstinspires.ftc.teamcode.switchboard.observe.Channel
import org.firstinspires.ftc.teamcode.switchboard.scheduler.HardwareScheduler
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time

abstract class Robot(val log: Log, val config: Config, val name: String) {
    val frame = Channel<Frame>(Frame(0, Time.zero))
    abstract val activities: List<Activity>
    abstract val scheduler: HardwareScheduler

    fun setup() {
        config.revHubs.forEach { it.bulkCachingMode = LynxModule.BulkCachingMode.MANUAL }
        activities.forEach { it.load() }
    }

    fun update() {
        config.revHubs.forEach { it.clearBulkCache() }
        frame.set(Frame(frame.get().n + 1, Time.now()))
        scheduler.update()
    }

    override fun toString(): String = name
}