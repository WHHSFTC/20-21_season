package org.firstinspires.ftc.teamcode.dsl

import org.firstinspires.ftc.teamcode.cmd.Command
import org.firstinspires.ftc.teamcode.cmd.CommandScheduler
import org.firstinspires.ftc.teamcode.cmd.LambdaCommand
import org.firstinspires.ftc.teamcode.cmd.dsl
import org.firstinspires.ftc.teamcode.module.OpMode
import org.firstinspires.ftc.teamcode.module.Robot
import org.firstinspires.ftc.teamcode.module.withContext

open class DslOpMode(protected var commands: CommandScheduler = CommandScheduler()): OpMode() {
    constructor(commandScheduler: CommandScheduler, build: CommandScheduler.() -> CommandScheduler):
            this(commandScheduler.dsl(build))
    constructor(build: CommandScheduler.() -> CommandScheduler): this(CommandScheduler(), build)

    override fun onInit() {
        commands.fnInit(this.bot.withContext(Command.Context.INIT))
    }

    override fun onRun() {
        commands.fnRun(this.bot.withContext(Command.Context.RUN))
    }
    override fun onLoop() {
        commands.fnLoop(this.bot.withContext(Command.Context.LOOP))
    }

    override fun onStop() {
        commands.fnStop(this.bot.withContext(Command.Context.STOP))
    }

    companion object {
        fun DslOpMode.dsl(commandScheduler: CommandScheduler = CommandScheduler(), build: CommandScheduler.() -> CommandScheduler) {
            this.commands = commandScheduler.dsl(build)
        }

        fun DslOpMode.task(task: Robot.() -> Unit): Command = LambdaCommand(task)
    }
}