package org.firstinspires.ftc.teamcode.switchboard.scheduler

import java.util.*

class CyclicalSchedule(val list: List<Process>) : Schedule {
    private var q: Queue<Process> = LinkedList(list)

    override fun recurse(f: (Process) -> Unit) {
        list.forEach(f)
    }

    override fun select(f: (Process) -> Unit) {
        q.add(q.element().also(f))
    }
}
