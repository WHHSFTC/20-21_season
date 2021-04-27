package org.firstinspires.ftc.teamcode.switchboard.command

import org.firstinspires.ftc.teamcode.switchboard.core.Frame

class LinearCommand(val list: List<Command>) : Command {
    var i = 0
    var fresh = true
    override var done: Boolean = false

    override fun load(frame: Frame) { }

    override fun update(frame: Frame) {
        if (done) return

        if (i < list.size) {
            when {
                list[i].done -> {
                    fresh = true
                    i++
                }
                fresh -> {
                    list[i].load(frame)
                    fresh = false
                }
                else -> {
                    list[i].update(frame)
                }
            }
        } else {
            done = true
        }
    }
}