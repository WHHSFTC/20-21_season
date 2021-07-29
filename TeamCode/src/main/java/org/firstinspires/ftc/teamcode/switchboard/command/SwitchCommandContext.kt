package org.firstinspires.ftc.teamcode.switchboard.command

import org.firstinspires.ftc.teamcode.switchboard.core.Frame
import org.firstinspires.ftc.teamcode.switchboard.scheduler.surelyList
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time

class SwitchCommandContext<T> {
    private val list: MutableList<SwitchCommand.Case<T>> = mutableListOf()

    fun satisfies(predicate: (T) -> Boolean, b: CommandListContext.() -> Unit) {
        list += SwitchCommand.Case<T>(predicate, makeLinear(b))
    }

    fun matches(supplier: () -> T, b: CommandListContext.() -> Unit) {
        satisfies({ it == supplier()}, b)
    }

    fun value(v: T, b: CommandListContext.() -> Unit) {
        matches({v}, b)
    }

    fun fallback(b: CommandListContext.() -> Unit) {
        satisfies({ true }, b)
    }

    fun build() = list
}

fun <T> makeSwitch(supplier: () -> T, b: SwitchCommandContext<T>.() -> Unit)
        = SwitchCommand<T>(supplier, SwitchCommandContext<T>().apply(b).build())
