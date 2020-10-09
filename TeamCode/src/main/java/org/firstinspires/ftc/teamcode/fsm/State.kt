package org.firstinspires.ftc.teamcode.fsm

import org.firstinspires.ftc.teamcode.module.Robot

fun interface State {
    operator fun invoke(robot: Robot)

//    fun <T: Any> Telemetry.logData(value: T) {
//        this.addData("${this@State}", value)
//    }
//
//    fun <T: Any, E: Any> Telemetry.logData(caption: T, value: E) {
//        this.addData("${this@State}", "$caption: $value")
//    }
//
//    fun <T: Any> Telemetry.logData(valueProducer: () -> T) {
//        this.addData("${this@State}", valueProducer)
//    }
//
//    fun <T: Any> Telemetry.logError(errorMsg: T) {
//        this.addData("[ERROR]", "$errorMsg")
//    }
}

fun state(block: Robot.() -> Unit = {}): State = State { block(it) }

val Robot.end: State
    get() = state()