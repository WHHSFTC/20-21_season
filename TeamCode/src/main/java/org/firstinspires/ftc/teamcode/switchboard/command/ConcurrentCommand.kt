package org.firstinspires.ftc.teamcode.switchboard.command

import org.firstinspires.ftc.teamcode.switchboard.core.Frame

class ConcurrentCommand(val list: List<Command>, val awaitAll: Boolean = true) : Command {
    override var done: Boolean = false

    override fun load(frame: Frame) { }

    override fun update(frame: Frame) {
        if (done) return

        val busy = list.filter { !it.done }

        if (busy.isEmpty() || (awaitAll && busy.size == list.size))
            done = true
        else
            busy.forEach { it.update(frame) }
    }
}