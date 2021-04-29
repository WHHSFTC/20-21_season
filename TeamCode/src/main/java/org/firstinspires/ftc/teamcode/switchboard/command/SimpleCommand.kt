package org.firstinspires.ftc.teamcode.switchboard.command

import org.firstinspires.ftc.teamcode.switchboard.core.Frame

class SimpleCommand(val lambda: (Frame) -> Unit) : Command {
    override var done: Boolean = false

    override fun load(frame: Frame) {
        lambda(frame)
        done = true
    }

    override fun update(frame: Frame) {
//        if (!done) {
//            lambda(frame)
//            done = true
//        }
    }
}