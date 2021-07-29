package org.firstinspires.ftc.teamcode.switchboard.command

import org.firstinspires.ftc.teamcode.switchboard.core.Frame

interface Command {
    fun load(frame: Frame)
    fun update(frame: Frame)
    val done: Boolean

    object stall : Command {
        override fun load(frame: Frame) { }
        override fun update(frame: Frame) { }
        override val done: Boolean = false
    }

    object nop : Command {
        override fun load(frame: Frame) { }
        override fun update(frame: Frame) { }
        override val done: Boolean = false
    }
}

