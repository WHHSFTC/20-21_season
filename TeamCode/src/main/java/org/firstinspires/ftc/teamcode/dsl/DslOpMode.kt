package org.firstinspires.ftc.teamcode.dsl

import org.firstinspires.ftc.teamcode.module.OpMode

open class DslOpMode(private val command: OpModeContext, private val robotContext: RobotContext = RobotContext()): OpMode() {
    init {
        if (!robotContext.isBotInitialized) {
            robotContext.bot = this.bot
        }
    }
    override fun onInit() {
        command.fnInit(InitContext(robotContext))
    }

    override fun onRun() {
        command.fnRun(RunContext(robotContext))
    }

    override fun onLoop() {
        command.fnLoop(LoopContext(robotContext))
    }

    override fun onStop() {
        command.fnStop(StopContext(robotContext))
    }
}