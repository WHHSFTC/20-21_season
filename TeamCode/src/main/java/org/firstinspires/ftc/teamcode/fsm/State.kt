package org.firstinspires.ftc.teamcode.fsm

import org.firstinspires.ftc.teamcode.module.Robot

fun interface State {
    operator fun invoke(robot: Robot)
}

fun state(block: Robot.() -> Unit = {}): State = State { block(it) }

val Robot.end: State
    get() = state()
