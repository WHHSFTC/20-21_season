package org.firstinspires.ftc.teamcode.cmd

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder
import org.firstinspires.ftc.teamcode.dsl.RobotDsl
import org.firstinspires.ftc.teamcode.module.DriveConstants
import org.firstinspires.ftc.teamcode.module.Module
import org.firstinspires.ftc.teamcode.module.Robot
import org.firstinspires.ftc.teamcode.util.Time
import org.firstinspires.ftc.teamcode.util.TimeUnit
import org.firstinspires.ftc.teamcode.util.milliseconds
import org.firstinspires.ftc.teamcode.util.seconds

@RobotDsl
class CommandListContext: DSLContext() {
    private val commands: MutableList<Command> = mutableListOf()

    fun build(): List<Command> {
        return commands
    }

    operator fun plusAssign(command: Command) {
        commands += command
    }

    operator fun Command.unaryPlus(): CommandListContext {
        commands += this
        return this@CommandListContext
    }
}

fun CommandListContext.cmd(exec: suspend Robot.() -> Unit) {
    this += LambdaCommand(exec)
}

fun CommandListContext.execute(command: Command) {
    this += command
}

suspend fun CommandListContext.condition(
        c: Condition,
        sequential: Boolean = true,
        ifC: suspend CommandListContext.() -> CommandListContext,
): ConditionalCommand {
    val conditional = ConditionalCommand(
            c,
            sequential,
            if (sequential)
                SequentialCommand(CommandListContext().ifC().build())
            else
                ParallelCommand(CommandListContext().ifC().build()),
    )

    this += conditional
    return conditional
}

suspend fun CommandListContext.onPress(
        c: Condition,
        sequential: Boolean = true,
        commandBlock: suspend CommandListContext.() -> CommandListContext
) {
    this += DebounceCommand(
            c,
            sequential,
            if (sequential)
                SequentialCommand(CommandListContext().commandBlock().build())
            else
                ParallelCommand(CommandListContext().commandBlock().build()),
    )
}

suspend fun CommandListContext.onRelease(
        c: Condition,
        sequential: Boolean = true,
        commandBlock: suspend CommandListContext.() -> CommandListContext
) {
    this += DebounceCommand(
            { !c() },
            sequential,
            if (sequential)
                SequentialCommand(CommandListContext().commandBlock().build())
            else
                ParallelCommand(CommandListContext().commandBlock().build()),
    )
}

suspend fun CommandListContext.whileHeld(
        c: Condition,
        sequential: Boolean = true,
        heldCommandBlock: suspend CommandListContext.() -> CommandListContext,
) {
    this += HeldCommand(
            c,
            sequential,
            if (sequential)
                SequentialCommand(CommandListContext().heldCommandBlock().build())
            else
                ParallelCommand(CommandListContext().heldCommandBlock().build()),
            EmptyCommand(),
    )
}

fun CommandListContext.delayFor(value: Time, block: Boolean = false) {
    this += DelayCommand(value, blockCoroutine = block)
}

fun CommandListContext.delayFor(millis: Long, block: Boolean = false) {
    this += DelayCommand(millis.milliseconds, blockCoroutine = block)
}

fun CommandListContext.delayFor(time: Long, unit: TimeUnit, block: Boolean = false) {
    this += DelayCommand(
            when (unit) {
                TimeUnit.SECONDS -> time.seconds
                TimeUnit.MILLISECONDS -> time.milliseconds
            },
            blockCoroutine = block,
    )
}

fun CommandListContext.pass() = EmptyCommand()

fun <T> CommandListContext.switch(supp: Robot.() -> T, c: List<SwitchCommand.Case<T>>) {
    this += SwitchCommand<T>(supp, c)
}

fun <T> CommandListContext.switch(supp: Robot.() -> T, c: SwitchCommand.SwitchCaseBuilder<T>.() -> Unit) {
    this += SwitchCommand<T>(supp, SwitchCommand.SwitchCaseBuilder<T>().apply(c).build())
}

fun <T> CommandListContext.case(supp: Robot.() -> T, com: Command): SwitchCommand.Case<T> = SwitchCommand.Case(supp, com)

fun CommandListContext.go(pose: Pose2d, reversed: Boolean = false, async: Boolean = false, b: TrajectoryBuilder.() -> TrajectoryBuilder) {
    val traj = TrajectoryBuilder(pose, reversed = reversed, constraints = DriveConstants.MECANUM_CONSTRAINTS).b().build()
    this += if (async)
        LambdaCommand { dt.followTrajectoryAsync(traj) }
    else
        LambdaCommand { dt.followTrajectory(traj) }
}

fun<T> CommandListContext.setState(module: Module<T>, stateSupplier: () -> T) {
    this += LambdaCommand {module(stateSupplier())}
}