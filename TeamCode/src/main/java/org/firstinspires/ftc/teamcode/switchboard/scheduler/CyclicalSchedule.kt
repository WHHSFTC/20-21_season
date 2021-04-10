package org.firstinspires.ftc.teamcode.switchboard.scheduler

import org.firstinspires.ftc.teamcode.switchboard.shapes.Time
import java.util.*

class CyclicalSchedule(val limit: Time = Time.milli(1), val list: List<Schedule>) : Schedule {
    private var q: Queue<Schedule> = LinkedList(list)

    override fun recurse(f: (Activity) -> Unit) {
        list.forEach { it.recurse(f) }
    }

    override fun select(f: (Activity) -> Unit) {
        val end = Time.now() + limit
        val tmp: Queue<Schedule> = LinkedList()
        // perform as many as possible within the allotted time cycle, without repeating
        while (Time.now() < end && q.isNotEmpty()) {
            tmp.add(q.element().also { it.select(f) })
        }
        tmp.forEach { q.add(it) }
    }

    override fun makeList(): List<Activity> = list.fold(listOf()) { acc, schedule ->  acc + schedule.makeList() }
}
