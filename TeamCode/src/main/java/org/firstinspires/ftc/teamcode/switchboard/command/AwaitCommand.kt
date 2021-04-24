package org.firstinspires.ftc.teamcode.switchboard.command

import org.firstinspires.ftc.teamcode.switchboard.core.Frame

class AwaitCommand(val predicate: (Frame) -> Boolean) : Command {
    override var done: Boolean = false

    override fun load(frame: Frame) { }

    override fun update(frame: Frame) {
        if (!done && predicate(frame))
            done = true
    }
}