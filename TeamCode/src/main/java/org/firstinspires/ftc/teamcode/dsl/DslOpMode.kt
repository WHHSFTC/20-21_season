package org.firstinspires.ftc.teamcode.dsl

import org.firstinspires.ftc.teamcode.fsm.Machine
import org.firstinspires.ftc.teamcode.module.OpMode

open class DslOpMode(private var machine: Machine): OpMode() {
    init {
        this.bot.machine = machine
    }

    override fun onInit() {
        machine.fnInit(this.bot.apply { context = OpModeContext.Context.INIT })
    }

    override fun onRun() {
        machine.fnRun(this.bot.apply { context = OpModeContext.Context.RUN })
    }

    override fun onLoop() {
        machine.fnLoop(this.bot.apply { context = OpModeContext.Context.LOOP })
    }

    override fun onStop() {
        machine.fnStop(this.bot.apply { context = OpModeContext.Context.STOP})
    }
}