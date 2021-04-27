package org.firstinspires.ftc.teamcode.switchboard.command

import org.firstinspires.ftc.teamcode.switchboard.core.Frame
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time

class LinearContext {
    val list: MutableList<Command> = mutableListOf()

    fun await(predicate: (Frame) -> Boolean) {
        list += AwaitCommand(predicate)
    }

    fun delay(duration: Time) {
        list += DelayCommand(duration)
    }

    fun delay(millis: Long) {
        this.delay(Time.milli(millis))
    }

    fun task(f: (Frame) -> Unit) {
        list += SimpleCommand(f)
    }

    fun build() = LinearCommand(list)
}

fun linear(b: LinearContext.() -> Unit)
    = LinearContext().apply(b).build()