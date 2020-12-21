package org.firstinspires.ftc.teamcode.cmd

import kotlinx.coroutines.delay
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

open class LambdaCommand(val f: Robot.() -> Unit) : Command() {
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
                launch {
                    when {
                        c is DelayCommand && !c.blockCoroutine -> delay(c.delay.milliSeconds)
                        else -> c.execute(bot)
                    }
                }
            }
        }
    }
}

class DelayCommand(val delay: Time, val blockCoroutine: Boolean = false): Command() {
    override fun execute(bot: Robot) {
        Thread.sleep(delay.milliSeconds)
    }
}

class ConditionalCommand(
        val condition: Condition,
        val sequential: Boolean,
        val onTrue: Command,
        val onFalse: Command,
): Command() {
    override fun execute(bot: Robot) {
        if (condition()) {
            onTrue.execute(bot)
        } else {
            onFalse.execute(bot)
        }
    }
}

class SwitchCommand(
        val cases: List<SwitchCommand.Case>,
): Command() {
    data class Case(val cond: Condition, val com: Command)
    override fun execute(bot: Robot) {
        for (c in cases) {
            if (c.cond())
                return c.com.execute(bot)
        }
    }
}

class ToggleCommand(
        val condition: Condition,
        val sequential: Boolean,
        val command: Command,
): Command() {
    private var previous: Boolean = false

    override fun execute(bot: Robot) {
        val current = condition()

        if (!previous && current) {
            command.execute(bot)
        }

        previous = current
    }
}


@RobotDsl
open class DSLContext

@RobotDsl
object CommandContext: DSLContext()

fun DSLContext.cmd(exec: Robot.() -> Unit): LambdaCommand = LambdaCommand(exec)

fun CommandContext.seq(b: CommandListContext.() -> CommandListContext): SequentialCommand =
            SequentialCommand(CommandListContext().b().build())

fun CommandContext.par(b: CommandListContext.() -> CommandListContext): ParallelCommand =
            ParallelCommand(CommandListContext().b().build())

typealias Condition = () -> Boolean

fun DSLContext.condition(
        c: Condition,
        sequential: Boolean = true,
        ifC: CommandListContext.() -> CommandListContext,
): ConditionalCommand = ConditionalCommand(
        c,
        sequential,
        if (sequential)
            SequentialCommand(CommandListContext().ifC().build())
        else
            ParallelCommand(CommandListContext().ifC().build()),
        EmptyCommand()
)

infix fun ConditionalCommand.orElse(
        elseC: CommandListContext.() -> CommandListContext,
): ConditionalCommand =
        ConditionalCommand(
                condition = condition,
                sequential = sequential,
                onTrue = onTrue,
                onFalse = if(sequential)
                    SequentialCommand(CommandListContext().elseC().build())
                else
                    ParallelCommand(CommandListContext().elseC().build())
        )

fun DSLContext.onPress(
        c: Condition,
        sequential: Boolean = true,
        commandBlock: CommandListContext.() -> CommandListContext
): ToggleCommand =
        ToggleCommand(
                c,
                sequential,
                if (sequential)
                    SequentialCommand(CommandListContext().commandBlock().build())
                else
                    ParallelCommand(CommandListContext().commandBlock().build())
        )

fun DSLContext.delay(value: Time, block: Boolean = false) = DelayCommand(value, blockCoroutine = block)

fun DSLContext.delay(millis: Long, block: Boolean = false) = DelayCommand(millis.milliseconds, blockCoroutine = block)

fun DSLContext.delay(time: Long, unit: TimeUnit, block: Boolean = false) = DelayCommand(when (unit) {
    TimeUnit.SECONDS -> time.seconds
    TimeUnit.MILLISECONDS -> time.milliseconds
}, blockCoroutine = block)

fun DSLContext.pass(): EmptyCommand = EmptyCommand()

fun DSLContext.switch(c: List<SwitchCommand.Case>): SwitchCommand = SwitchCommand(c)
fun DSLContext.case(cond: Condition, com: Command): SwitchCommand.Case = SwitchCommand.Case(cond, com)

@RobotDsl
class CommandListContext: DSLContext() {
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
        var fnRun: Command = EmptyCommand(),
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

infix fun Condition.and(other: Condition): Condition = { this() && other() }

infix fun Condition.or(other: Condition): Condition = { this() || other() }

infix fun Condition.xor(other: Condition): Condition = { this() xor other() }

operator fun Condition.not(): Condition = { !this() }
