package org.firstinspires.ftc.teamcode.fsm

import org.firstinspires.ftc.teamcode.dsl.OpModeContext
import org.firstinspires.ftc.teamcode.dsl.RobotDsl
import org.firstinspires.ftc.teamcode.module.Robot

@RobotDsl
data class MachineDSL(
        val map: MutableMap<State, Robot.() -> State> =
                emptyMap<State, Robot.() -> State>().toMutableMap()
): OpModeContext() {
    operator fun <T: State, R: State> T.invoke(state: Robot.() -> R): Machine {
        map[this] = state
        return this@MachineDSL
    }

    companion object
}

fun emptyMachine(): MachineDSL = MachineDSL()

typealias Machine = MachineDSL
