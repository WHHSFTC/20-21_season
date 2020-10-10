package org.firstinspires.ftc.teamcode.fsm

import org.firstinspires.ftc.teamcode.module.Robot

interface IMachine {
    fun onInit(init: Robot.() -> Unit): IMachine
    fun onRun(run: Robot.() -> Unit): IMachine
    fun onLoop(loop: Robot.() -> Unit): IMachine
    fun onStop(stop: Robot.() -> Unit): IMachine
    fun task(taskName: String, task: Robot.() -> Unit): IMachine
    operator fun <T: State, R: State> T.invoke(state: Robot.() -> R): IMachine
}
