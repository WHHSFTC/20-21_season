package org.firstinspires.ftc.teamcode.switchboard.command

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder
import com.acmerobotics.roadrunner.trajectory.constraints.DriveConstraints
import org.firstinspires.ftc.teamcode.switchboard.core.Frame
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time

class CommandListContext {
    val list: MutableList<Command> = mutableListOf()

    fun await(predicate: (Frame) -> Boolean) {
        list += AwaitCommand(predicate)
    }

    fun delay(duration: Time) {
        list += DelayCommand(duration)
    }

    fun delay(millis: Long) {
        this.delay(Time.milli(millis))
    }

    fun task(f: (Frame) -> Unit) {
        list += SimpleCommand(f)
    }

    fun linear(b: CommandListContext.() -> Unit) {
        list += makeLinear(b)
    }

    fun concurrent(b: CommandListContext.() -> Unit) {
        list += makeConcurrent(b)
    }

    fun build() = list
}

fun makeLinear(b: CommandListContext.() -> Unit)
        = LinearCommand(CommandListContext().apply(b).build())

fun makeConcurrent(b: CommandListContext.() -> Unit)
        = ConcurrentCommand(CommandListContext().apply(b).build())
