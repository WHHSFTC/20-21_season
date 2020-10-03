package org.firstinspires.ftc.teamcode.dsl

import org.firstinspires.ftc.teamcode.fsm.MachineDSL
import org.firstinspires.ftc.teamcode.module.Robot

@RobotDsl
open class OpModeContext(
        var fnInit: Robot.() -> Unit = {},
        var fnRun: Robot.() -> Unit = {},
        var fnLoop: Robot.() -> Unit = {},
        var fnStop: Robot.() -> Unit = {},
) {
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
inline fun FSM.fsm(b: MachineDSL.() -> Unit): MachineDSL =
        MachineDSL().apply { this.b() }

fun MachineDSL.onInit(init: Robot.() -> Unit): MachineDSL =
        this.apply {
            this.fnInit = init
        }

fun MachineDSL.onRun(run: Robot.() -> Unit): MachineDSL =
        this.apply {
            this.fnRun = run
        }

fun MachineDSL.onLoop(loop: Robot.() -> Unit): MachineDSL =
        this.apply {
            this.fnLoop = loop
        }

fun MachineDSL.onStop(stop: Robot.() -> Unit): MachineDSL =
        this.apply {
            this.fnStop = stop
        }
