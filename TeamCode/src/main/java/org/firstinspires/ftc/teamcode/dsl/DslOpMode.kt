package org.firstinspires.ftc.teamcode.dsl

import org.firstinspires.ftc.teamcode.cmd.Command
import org.firstinspires.ftc.teamcode.cmd.CommandScheduler
import org.firstinspires.ftc.teamcode.cmd.LambdaCommand
import org.firstinspires.ftc.teamcode.cmd.dsl
import org.firstinspires.ftc.teamcode.dsl.DslOpMode.Companion.dsl
import org.firstinspires.ftc.teamcode.module.OpMode
import org.firstinspires.ftc.teamcode.module.Robot
import org.firstinspires.ftc.teamcode.module.withContext

open class DslOpMode(protected var commands: CommandScheduler = CommandScheduler(), mode: Mode = Mode.TELE): OpMode(mode) { // TODO (Mode)
    constructor(commandScheduler: CommandScheduler, mode: Mode, build: suspend CommandScheduler.() -> CommandScheduler):
            this(CommandScheduler(), mode = mode) {
                suspend {
                    commands = commandScheduler.dsl(build)
                }
            }
    constructor(mode: Mode, build: suspend CommandScheduler.() -> CommandScheduler): this(CommandScheduler(), mode, build)

    var builder: suspend CommandScheduler.() -> CommandScheduler = {this}

    override suspend fun onInit() {
        commands = commands.dsl(builder)
        commands.fnInit(this.bot.withContext(Command.Context.INIT))
    }

    override suspend fun onRun() {
        commands.fnRun(this.bot.withContext(Command.Context.RUN))
    }
    override suspend fun onLoop() {
        commands.fnLoop(this.bot.withContext(Command.Context.LOOP))
    }

    override suspend fun onStop() {
        commands.fnStop(this.bot.withContext(Command.Context.STOP))
    }

    companion object {
        suspend fun DslOpMode.dsl(commandScheduler: CommandScheduler = CommandScheduler(), build: suspend CommandScheduler.() -> CommandScheduler) {
            this.builder = build
        }

        fun DslOpMode.task(task: suspend Robot.() -> Unit): Command = LambdaCommand(task)
    }
}