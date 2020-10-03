package org.firstinspires.ftc.teamcode.dsl

import org.firstinspires.ftc.teamcode.module.OpMode

open class DslOpMode(private val command: OpModeContext): OpMode() {
    init {
        command.telemetry = this.telemetry
    }

    override fun onInit() {
        command.fnInit(this.bot.apply { context = OpModeContext.Context.INIT })
    }

    override fun onRun() {
        command.fnRun(this.bot.apply { context = OpModeContext.Context.RUN })
    }

    override fun onLoop() {
        command.fnLoop(this.bot.apply { context = OpModeContext.Context.LOOP })
    }

    override fun onStop() {
        command.fnStop(this.bot.apply { context = OpModeContext.Context.STOP})
    }
}