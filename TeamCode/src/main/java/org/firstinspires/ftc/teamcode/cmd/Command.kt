package org.firstinspires.ftc.teamcode.cmd

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.firstinspires.ftc.teamcode.dsl.RobotDsl
import org.firstinspires.ftc.teamcode.module.Robot
import org.firstinspires.ftc.teamcode.util.Time
import org.firstinspires.ftc.teamcode.util.TimeUnit
import org.firstinspires.ftc.teamcode.util.milliseconds
import org.firstinspires.ftc.teamcode.util.seconds

sealed class Command {
    abstract fun execute(bot: Robot)
    operator fun invoke(bot: Robot) {
        execute(bot)
    }

    enum class Context { BASE, INIT, RUN, LOOP, STOP; }
}

object EmptyCommand : Command() {
    override fun execute(bot: Robot) {

    }

    operator fun invoke() = EmptyCommand
}

class LambdaCommand(val f: Robot.() -> Unit) : Command() {
    override fun execute(bot: Robot) {
        bot.f()
    }
}

class SequentialCommand(private val commands: List<Command>) : Command() {
    override fun execute(bot: Robot) {
        for (c: Command in commands) {
            c.execute(bot)
        }
    }
}

class ParallelCommand(private val commands: List<Command>) : Command() {
    override fun execute(bot: Robot) {
        runBlocking {
            for (c: Command in commands) {
                launch { c.execute(bot) }
            }
        }
    }
}

class DelayCommand(val delay: Time): Command() {
    override fun execute(bot: Robot) {
        Thread.sleep(delay.milliSeconds)
    }
}

@RobotDsl
object CommandContext

fun CommandContext.cmd(exec: Robot.() -> Unit): LambdaCommand = LambdaCommand(exec)

fun CommandContext.seq(b: CommandListContext.() -> CommandListContext): SequentialCommand =
            SequentialCommand(CommandListContext().b().build())

fun CommandContext.par(b: CommandListContext.() -> CommandListContext): ParallelCommand =
            ParallelCommand(CommandListContext().b().build())

fun CommandContext.delay(value: Time) = DelayCommand(value)

fun CommandContext.delay(millis: Long) = DelayCommand(millis.milliseconds)

fun CommandContext.delay(time: Long, unit: TimeUnit) = DelayCommand(when(unit) {
    TimeUnit.SECONDS -> time.seconds
    TimeUnit.MILLISECONDS -> time.milliseconds
})

fun CommandContext.pass(): EmptyCommand = EmptyCommand()

@RobotDsl
class CommandListContext {
    private val commands: MutableList<Command> = mutableListOf()
    fun build(): List<Command> {
        return commands
    }
    operator fun Command.unaryPlus(): CommandListContext {
        commands += this
        return this@CommandListContext
    }
}

@RobotDsl
data class CommandScheduler(
        var fnInit: Command = EmptyCommand(),
        var fnRun:  Command = EmptyCommand(),
        var fnLoop: Command = EmptyCommand(),
        var fnStop: Command = EmptyCommand(),
)

fun CommandScheduler.onInit(init: CommandContext.() -> Command): CommandScheduler =
        this.apply {
            fnInit = CommandContext.init()
        }

fun CommandScheduler.onRun(run: CommandContext.() -> Command): CommandScheduler =
        this.apply {
            fnRun = CommandContext.run()
        }

fun CommandScheduler.onLoop(loop: CommandContext.() -> Command): CommandScheduler =
        this.apply {
            fnLoop = CommandContext.loop()
        }

fun CommandScheduler.onStop(stop: CommandContext.() -> Command): CommandScheduler =
        this.apply {
            fnStop = CommandContext.stop()
        }

fun CommandScheduler.dsl(build: CommandScheduler.() -> CommandScheduler): CommandScheduler =
        this.build()
