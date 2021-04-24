package org.firstinspires.ftc.teamcode.switchboard.command

import org.firstinspires.ftc.teamcode.switchboard.core.Frame

class LinearCommand(val list: List<Command>) : Command {
    var i = 0
    override var done: Boolean = false

    override fun load(frame: Frame) { }

    override fun update(frame: Frame) {
        if (done) return

        if (list[i].done)
            i++

        if (i < list.size)
            list[i].update(frame)
        else
            done = true
    }
}