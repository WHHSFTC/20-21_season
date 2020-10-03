package org.firstinspires.ftc.teamcode.dsl

import org.firstinspires.ftc.teamcode.fsm.Machine
import org.firstinspires.ftc.teamcode.fsm.MachineDSL
import org.firstinspires.ftc.teamcode.module.Robot

@RobotDsl
open class OpModeContext(
        val tasks: MutableMap<String, Robot.() -> Unit> = mutableMapOf(),
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
inline fun FSM.fsm(b: Machine.() -> Unit): Machine =
        Machine().apply { this.b() }

inline fun DslOpMode.fsm(b: Machine.() -> Unit): Machine =
        Machine().apply { this.b() }

fun Machine.onInit(init: Robot.() -> Unit): Machine =
        this.apply {
            this.fnInit = init
        }

fun Machine.onRun(run: Robot.() -> Unit): Machine =
        this.apply {
            this.fnRun = run
        }

fun Machine.onLoop(loop: Robot.() -> Unit): Machine =
        this.apply {
            this.fnLoop = loop
        }

fun Machine.onStop(stop: Robot.() -> Unit): Machine =
        this.apply {
            this.fnStop = stop
        }

fun Machine.task(taskName: String, task: Robot.() -> Unit): Machine =
        this.apply { this.tasks[taskName] = task }
