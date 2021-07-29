package org.firstinspires.ftc.teamcode.switchboard.command

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder
import com.acmerobotics.roadrunner.trajectory.constraints.DriveConstraints
import org.firstinspires.ftc.teamcode.switchboard.core.Frame
import org.firstinspires.ftc.teamcode.switchboard.scheduler.surelyList
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time

class CommandListContext {
    private val list: MutableList<Command> = mutableListOf()

    fun <T> switch(supplier: () -> T, block: SwitchCommandContext<T>.() -> Unit) {
        list += makeSwitch(supplier, block)
    }

    fun await(predicate: (Frame) -> Boolean) {
        list += AwaitCommand(predicate)
    }

    fun awaitAll(vararg predicate: (Frame) -> Boolean) {
        list += ConcurrentCommand(predicate.surelyList().map {AwaitCommand(it)}, awaitAll = true)
    }

    fun awaitAny(vararg predicate: (Frame) -> Boolean) {
        list += ConcurrentCommand(predicate.surelyList().map {AwaitCommand(it)}, awaitAll = false)
    }

    fun awaitUntil(timeout: Time, predicate: (Frame) -> Boolean) {
        this.concurrent(awaitAll = true) {
            await(predicate)
            delay(timeout)
        }
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

    fun sub(c: Command) {
        list += c
    }

    fun linear(b: CommandListContext.() -> Unit) {
        list += makeLinear(b)
    }

    fun concurrent(awaitAll: Boolean = true, b: CommandListContext.() -> Unit) {
        list += makeConcurrent(awaitAll, b)
    }

    fun build() = list
}

fun makeLinear(b: CommandListContext.() -> Unit)
        = LinearCommand(CommandListContext().apply(b).build())

fun makeConcurrent(awaitAll: Boolean = true, b: CommandListContext.() -> Unit)
        = ConcurrentCommand(CommandListContext().apply(b).build(), awaitAll)
