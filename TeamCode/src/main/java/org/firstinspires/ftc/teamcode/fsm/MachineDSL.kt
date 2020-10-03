package org.firstinspires.ftc.teamcode.fsm

import org.firstinspires.ftc.teamcode.dsl.OpModeContext
import org.firstinspires.ftc.teamcode.dsl.RobotDsl
import org.firstinspires.ftc.teamcode.module.Robot

@RobotDsl
data class MachineDSL(
        val map: MutableMap<String, Robot.() -> String> =
                emptyMap<String, Robot.() -> String>().toMutableMap()
): OpModeContext(), MutableMap<String, Robot.() -> String> by map {
    operator fun String.invoke(state: Robot.() -> String): MachineDSL {
        put(this, state)
        return this@MachineDSL
    }
}
fun emptyMachine(): MachineDSL = MachineDSL()

typealias Machine = MachineDSL
