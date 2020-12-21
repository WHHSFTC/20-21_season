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
    abstract suspend fun execute(bot: Robot)
    suspend operator fun invoke(bot: Robot) {
        execute(bot)
    }

    enum class Context { BASE, INIT, RUN, LOOP, STOP; }
}

object EmptyCommand : Command() {
    override suspend fun execute(bot: Robot) {}

    operator fun invoke() = EmptyCommand
}

open class LambdaCommand(val f: suspend Robot.() -> Unit) : Command() {
    override suspend fun execute(bot: Robot) {
        bot.f()
    }
}

class SequentialCommand(private val commands: List<Command>) : Command() {
    override suspend fun execute(bot: Robot) {
        for (c: Command in commands) {
            c.execute(bot)
        }
    }
}

class ParallelCommand(private val commands: List<Command>) : Command() {
    override suspend fun execute(bot: Robot) {
        runBlocking {
            for (c: Command in commands) {
                launch {
                    c.execute(bot)
                }
            }
        }
    }
}

class DelayCommand(val delay: Time, val blockCoroutine: Boolean = false): Command() {
    override suspend fun execute(bot: Robot) {
        delay(delay.milliSeconds)
    }
}

class ConditionalCommand(
        val condition: Condition,
        val sequential: Boolean,
        val onTrue: Command,
        val onFalse: Command,
): Command() {
    override suspend fun execute(bot: Robot) {
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

class DebounceCommand(
        val condition: Condition,
        val sequential: Boolean,
        val command: Command,
): Command() {
    private var previous: Boolean = false

    override suspend fun execute(bot: Robot) {
        val current = condition()

        if (!previous && current) {
            command.execute(bot)
        }

        previous = current
    }
}

class HeldCommand(
        val condition: Condition,
        val sequential: Boolean,
        val heldCommand: Command,
        var exitCommand: Command,
): Command() {
    private var previous: Boolean = false

    override suspend fun execute(bot: Robot) {
        val current = condition()

        if (current) {
            heldCommand.execute(bot)
        } else if (previous) {
            exitCommand.execute(bot)
        }

        previous = current
    }
}

suspend infix fun HeldCommand.onExit(block: suspend CommandListContext.() -> CommandListContext) = this.apply {
    exitCommand = if (sequential)
        SequentialCommand(CommandListContext().block().build())
    else
        ParallelCommand(CommandListContext().block().build())
}

class ToggleCommand(
        val condition: Condition,
        val sequential: Boolean,
        val toggleCommand: Command,
): Command() {
    private var previous: Boolean = false
    private var toggleState: Boolean = false

    override suspend fun execute(bot: Robot) {
        val current = condition()
        toggleState = if(!previous && current) !toggleState else toggleState

        if(toggleState) {
            toggleCommand.execute(bot)
        }

        previous = current
    }
}

@RobotDsl
open class DSLContext

@RobotDsl
object CommandContext: DSLContext()

fun DSLContext.cmd(exec: suspend Robot.() -> Unit): LambdaCommand = LambdaCommand(exec)

suspend fun CommandContext.seq(b: suspend CommandListContext.() -> CommandListContext): SequentialCommand =
            SequentialCommand(CommandListContext().b().build())

suspend fun CommandContext.par(b: suspend CommandListContext.() -> CommandListContext): ParallelCommand =
            ParallelCommand(CommandListContext().b().build())

typealias Condition = () -> Boolean

suspend fun DSLContext.condition(
        c: Condition,
        sequential: Boolean = true,
        ifC: suspend CommandListContext.() -> CommandListContext,
): ConditionalCommand = ConditionalCommand(
        c,
        sequential,
        if (sequential)
            SequentialCommand(CommandListContext().ifC().build())
        else
            ParallelCommand(CommandListContext().ifC().build()),
        EmptyCommand()
)

suspend infix fun ConditionalCommand.orElse(
        elseC: suspend CommandListContext.() -> CommandListContext,
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

suspend fun DSLContext.onPress(
        c: Condition,
        sequential: Boolean = true,
        commandBlock: suspend CommandListContext.() -> CommandListContext
): DebounceCommand =
        DebounceCommand(
                c,
                sequential,
                if (sequential)
                    SequentialCommand(CommandListContext().commandBlock().build())
                else
                    ParallelCommand(CommandListContext().commandBlock().build())
        )

suspend fun DSLContext.onRelease(
        c: Condition,
        sequential: Boolean = true,
        commandBlock: suspend CommandListContext.() -> CommandListContext
): DebounceCommand =
        DebounceCommand(
                {!c()},
                sequential,
                if(sequential)
                    SequentialCommand(CommandListContext().commandBlock().build())
                else
                    ParallelCommand(CommandListContext().commandBlock().build())
        )

suspend fun DSLContext.whileHeld(
        c: Condition,
        sequential: Boolean = true,
        heldCommandBlock: suspend CommandListContext.() -> CommandListContext,
): HeldCommand =
        HeldCommand(
                c,
                sequential,
                if(sequential)
                    SequentialCommand(CommandListContext().heldCommandBlock().build())
                else
                    ParallelCommand(CommandListContext().heldCommandBlock().build()),
                EmptyCommand(),
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

infix fun Condition.and(other: Condition): Condition = { this() && other() }

infix fun Condition.or(other: Condition): Condition = { this() || other() }

infix fun Condition.xor(other: Condition): Condition = { this() xor other() }

operator fun Condition.not(): Condition = { !this() }
