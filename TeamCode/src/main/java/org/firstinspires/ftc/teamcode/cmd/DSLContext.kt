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
open class DSLContext

fun DSLContext.cmd(exec: suspend Robot.() -> Unit): LambdaCommand = LambdaCommand(exec)

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

fun DSLContext.delayC(value: Time, block: Boolean = false) = DelayCommand(value, blockCoroutine = block)

fun DSLContext.delayC(millis: Long, block: Boolean = false) = DelayCommand(millis.milliseconds, blockCoroutine = block)

fun DSLContext.delayC(time: Long, unit: TimeUnit, block: Boolean = false) = DelayCommand(when (unit) {
    TimeUnit.SECONDS -> time.seconds
    TimeUnit.MILLISECONDS -> time.milliseconds
}, blockCoroutine = block)

fun DSLContext.pass(): EmptyCommand = EmptyCommand()

fun <T> DSLContext.switch(supp: Robot.() -> T, c: List<SwitchCommand.Case<T>>): SwitchCommand<T> = SwitchCommand<T>(supp, c)

fun <T> DSLContext.case(supp: Robot.() -> T, com: Command): SwitchCommand.Case<T> = SwitchCommand.Case(supp, com)

fun DSLContext.go(pose: Pose2d, reversed: Boolean = false, async: Boolean = false, b: TrajectoryBuilder.() -> TrajectoryBuilder): LambdaCommand {
    val traj = TrajectoryBuilder(pose, reversed = reversed, constraints = DriveConstants.MECANUM_CONSTRAINTS).b().build()
    return if (async) LambdaCommand { dt.followTrajectoryAsync(traj) } else LambdaCommand { dt.followTrajectory(traj) }
}

fun DSLContext.go(pose: Pose2d, startTangent: Double, async: Boolean = false, b: TrajectoryBuilder.() -> TrajectoryBuilder): LambdaCommand {
    val traj = TrajectoryBuilder(pose, startTangent = startTangent, constraints = DriveConstants.MECANUM_CONSTRAINTS).b().build()
    return if (async) LambdaCommand { dt.followTrajectoryAsync(traj) } else LambdaCommand { dt.followTrajectory(traj) }
}

fun<T> DSLContext.setState(module: Module<T>, stateSupplier: () -> T): LambdaCommand
        = LambdaCommand {module(stateSupplier())}
