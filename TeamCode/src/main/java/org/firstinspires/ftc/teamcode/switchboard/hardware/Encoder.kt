package org.firstinspires.ftc.teamcode.switchboard.hardware

interface Encoder: HardwareInput {
    var position: Int
    val velocity: Double

    fun stopAndReset()
}