package org.firstinspires.ftc.teamcode.switchboard.scheduler.dsl

class ListBuilderContext<T> {
    val list: MutableList<T> = mutableListOf()

    operator fun T.unaryPlus() {
        list += this
    }

    fun build() = list
}