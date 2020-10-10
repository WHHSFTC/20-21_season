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
                this.fnRun = run
            }

    override fun onLoop(loop: Robot.() -> Unit): IMachine =
            this.apply {
                this.fnLoop = loop
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
