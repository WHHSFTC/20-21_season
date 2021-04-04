package org.firstinspires.ftc.teamcode.switchboard.scheduler

import org.firstinspires.ftc.teamcode.switchboard.shapes.Time
import java.util.*

class CyclicalSchedule(val limit: Time = Time.milli(2), val list: List<Schedule>) : Schedule {
    private var q: Queue<Schedule> = LinkedList(list)

    override fun recurse(f: (Activity) -> Unit) {
        list.forEach { it.recurse(f) }
    }

    override fun select(f: (Activity) -> Unit) {
        val end = Time.now() + limit
        while (Time.now() < end) {
            q.add(q.element().also { it.select(f) })
        }
    }

    override fun makeList(): List<Activity> = list.fold(listOf()) { acc, schedule ->  acc + schedule.makeList() }
}
