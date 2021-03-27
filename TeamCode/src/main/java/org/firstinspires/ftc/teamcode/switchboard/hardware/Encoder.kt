package org.firstinspires.ftc.teamcode.switchboard.hardware

interface Encoder: HardwareInput {
    val position: Int
    val velocity: Double
}