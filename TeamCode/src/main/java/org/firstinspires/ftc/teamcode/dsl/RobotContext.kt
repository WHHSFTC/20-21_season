package org.firstinspires.ftc.teamcode.dsl

import org.firstinspires.ftc.teamcode.fsm.Machine
import org.firstinspires.ftc.teamcode.module.OpMode
import org.firstinspires.ftc.teamcode.module.Robot

@RobotDsl
open class OpModeContext(
        mode_: OpMode.Mode = OpMode.Mode.NULL,
        val tasks: MutableMap<String, Robot.() -> Unit> = mutableMapOf(),
        var fnInit: Robot.() -> Unit = {},
        var fnPeriodic: Robot.() -> Unit = {},
        var fnStop: Robot.() -> Unit = {},
) {
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
            if (mode != OpMode.Mode.NULL && mode == OpMode.Mode.TELE) {
                val currentMode = mode
                val errorTask: Robot.() -> Unit = {
                    log.logError("Current Mode $currentMode is not ${OpMode.Mode.AUTO}")
                }
                this.mode = OpMode.Mode.NULL
                this.fnPeriodic = errorTask
                this.fnStop = {
                    while(true)
                        errorTask()
                }
            } else {
                this.mode = OpMode.Mode.AUTO
                this.fnPeriodic = run
            }
        }

fun Machine.onLoop(loop: Robot.() -> Unit): Machine =
        this.apply {
            if (mode != OpMode.Mode.NULL && mode == OpMode.Mode.AUTO) {
                val currentMode = mode
                val errorTask: Robot.() -> Unit = {
                    log.logError("Current Mode $currentMode is not ${OpMode.Mode.TELE}")
                }
                this.mode = OpMode.Mode.NULL
                this.fnPeriodic = errorTask
                this.fnStop = {
                    while(true)
                        errorTask()
                }
            } else {
                this.mode = OpMode.Mode.TELE
                this.fnPeriodic = loop
            }
        }

fun Machine.onPeriodic(periodic: Robot.() -> Unit): Machine =
        this.apply {
            this.fnPeriodic = periodic
        }

fun Machine.onStop(stop: Robot.() -> Unit): Machine =
        this.apply {
            this.fnStop = stop
        }

fun Machine.task(taskName: String, task: Robot.() -> Unit): Machine =
        this.apply { this.tasks[taskName] = task }
