package org.firstinspires.ftc.teamcode.dsl

import org.firstinspires.ftc.teamcode.fsm.IMachine
import org.firstinspires.ftc.teamcode.fsm.State
import org.firstinspires.ftc.teamcode.module.OpMode
import org.firstinspires.ftc.teamcode.module.Robot

class Pass: OpModeContext(fnInit = {machine.mode=OpMode.Mode.NULL}) {
    override fun onInit(init: Robot.() -> Unit): IMachine {
        return this
    }

    override fun onRun(run: Robot.() -> Unit): IMachine {
        return this
    }

    override fun onLoop(loop: Robot.() -> Unit): IMachine {
        return this
    }

    override fun onPeriodic(periodic: Robot.() -> Unit): IMachine {
        return this
    }

    override fun onStop(stop: Robot.() -> Unit): IMachine {
        return this
    }

    override fun task(taskName: String, task: Robot.() -> Unit): IMachine {
        return this
    }

    override fun <T : State, R : State> T.invoke(state: Robot.() -> R): IMachine {
        return this@Pass
    }

}