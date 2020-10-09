package org.firstinspires.ftc.teamcode.fsm

import org.firstinspires.ftc.teamcode.dsl.*
import org.firstinspires.ftc.teamcode.module.OpMode
import org.firstinspires.ftc.teamcode.module.Robot

@RobotDsl
data class MachineDSL(
        val map: MutableMap<State, Robot.() -> State> =
                emptyMap<State, Robot.() -> State>().toMutableMap()
): OpModeContext() {
    override operator fun <T: State, R: State> T.invoke(state: Robot.() -> R): MachineDSL {
        map[this] = state
        return this@MachineDSL
    }

    override fun onInit(init: Robot.() -> Unit): IMachine =
            this.apply {
                this.fnInit = init
            }

    override fun onRun(run: Robot.() -> Unit): IMachine =
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

    override fun onLoop(loop: Robot.() -> Unit): IMachine =
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

    override fun onPeriodic(periodic: Robot.() -> Unit): IMachine =
            this.apply {
                this.fnPeriodic = periodic
            }

    override fun onStop(stop: Robot.() -> Unit): IMachine =
            this.apply {
                this.fnStop = stop
            }

    override fun task(taskName: String, task: Robot.() -> Unit): IMachine =
            this.apply { this.tasks[taskName] = task }

    companion object
}

fun emptyMachine(): MachineDSL = MachineDSL()

typealias Machine = MachineDSL
