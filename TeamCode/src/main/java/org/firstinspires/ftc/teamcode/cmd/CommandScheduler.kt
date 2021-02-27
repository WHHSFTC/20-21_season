package org.firstinspires.ftc.teamcode.cmd

import org.firstinspires.ftc.teamcode.dsl.RobotDsl
import org.firstinspires.ftc.teamcode.module.Robot

@RobotDsl
data class CommandScheduler(
        var fnInit: Command = EmptyCommand(),
        var fnRun: Command = EmptyCommand(),
        var fnLoop: Command = EmptyCommand(),
        var fnStop: Command = EmptyCommand(),
)

suspend fun CommandScheduler.onInit(init: suspend CommandContext.() -> Command): CommandScheduler =
        this.apply {
            fnInit = CommandContext.init()
        }

suspend fun CommandScheduler.onRun(run: suspend CommandContext.() -> Command): CommandScheduler =
        this.apply {
            fnRun = CommandContext.run()
        }

suspend fun CommandScheduler.onLoop(loop: suspend CommandContext.() -> Command): CommandScheduler =
        this.apply {
            fnLoop = CommandContext.loop()
        }

suspend fun CommandScheduler.onStop(stop: suspend CommandContext.() -> Command): CommandScheduler =
        this.apply {
            fnStop = CommandContext.stop()
        }

suspend fun CommandScheduler.dsl(build: suspend CommandScheduler.() -> CommandScheduler): CommandScheduler =
        this.build()