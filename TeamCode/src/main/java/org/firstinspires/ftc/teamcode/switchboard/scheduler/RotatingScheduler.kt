package org.firstinspires.ftc.teamcode.switchboard.scheduler

import org.firstinspires.ftc.teamcode.switchboard.shapes.Time
import java.util.*

class RotatingScheduler(val duration: Time, val list: List<HardwareScheduler>): HardwareScheduler {
    val queue: Queue<HardwareScheduler> = LinkedList(list)
    override fun update() {
        val start = Time.now()
        val end = start + duration
        val tmp = LinkedList<HardwareScheduler>()
        while (Time.now() < end && queue.isNotEmpty())
            tmp.add(queue.element().also { it.update() })
        queue.addAll(tmp)
    }
}