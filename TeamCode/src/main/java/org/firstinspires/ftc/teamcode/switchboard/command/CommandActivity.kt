package org.firstinspires.ftc.teamcode.switchboard.command

import org.firstinspires.ftc.teamcode.switchboard.core.Activity
import org.firstinspires.ftc.teamcode.switchboard.core.Frame

class CommandActivity(val cmd: Command, startFrame: Frame? = null) : Activity {
    init {
        startFrame?.let {
            cmd.load(it)
        }
    }

    override fun update(frame: Frame) {
        if (!cmd.done)
            cmd.update(frame)
    }

}

fun Command.toActivity(now: Frame? = null) = CommandActivity(this, now)