package org.firstinspires.ftc.teamcode.dsl

import org.firstinspires.ftc.teamcode.fsm.IMachine
import org.firstinspires.ftc.teamcode.fsm.Machine
import org.firstinspires.ftc.teamcode.fsm.emptyMachine
import org.firstinspires.ftc.teamcode.module.OpMode
import org.firstinspires.ftc.teamcode.module.Robot

@RobotDsl
abstract class OpModeContext(
        mode_: OpMode.Mode = OpMode.Mode.NULL,
        val tasks: MutableMap<String, Robot.() -> Unit> = mutableMapOf(),
        var fnInit: Robot.() -> Unit = {},
        var fnRun: Robot.() -> Unit = {},
        var fnLoop: Robot.() -> Unit = {},
        var fnStop: Robot.() -> Unit = {},
): IMachine {
    var mode: OpMode.Mode = mode_

    enum class Context {
        BASE,
        INIT,
        RUN,
        LOOP,
        STOP;

        override fun toString(): String = "[${this.name}]"
    }
}

object FSM
inline fun FSM.fsm(b: IMachine.() -> Unit): IMachine =
        emptyMachine().apply { this.b() }

inline fun DslOpMode.fsm(b: IMachine.() -> Unit): IMachine =
        emptyMachine().apply { this.b() }
