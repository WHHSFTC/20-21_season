package org.firstinspires.ftc.teamcode.switchboard.scheduler

import java.util.*

class CyclicalProcess(val list: List<Process>) : Process() {
    private lateinit var q: Queue<Process>

    override fun load() {
        q = LinkedList(list)
    }

    override fun run() {
        q.add(q.element().also { it.run() })
    }
}