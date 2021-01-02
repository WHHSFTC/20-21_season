package org.firstinspires.ftc.teamcode.cmd

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder
import com.acmerobotics.roadrunner.trajectory.constraints.DriveConstraints
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.firstinspires.ftc.teamcode.dsl.RobotDsl
import org.firstinspires.ftc.teamcode.module.DriveConstants
import org.firstinspires.ftc.teamcode.module.Module
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
        if (blockCoroutine) {
            Thread.sleep(delay.milliSeconds)
        } else {
            delay(delay.milliSeconds)
        }
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

class SwitchCommand<T: Any?>(
        val supp: Robot.() -> T,
        val cases: List<SwitchCommand.Case<T>>,
): Command() {
    data class Case<T: Any?>(val supp: Robot.() -> T, val com: Command)
    override suspend fun execute(bot: Robot) {
        for (c in cases) {
            if (supp(bot) == c.supp(bot))
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
