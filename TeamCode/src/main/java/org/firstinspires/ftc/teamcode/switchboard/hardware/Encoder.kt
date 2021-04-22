package org.firstinspires.ftc.teamcode.switchboard.hw

interface Encoder: HardwareInput {
    val position: Int
    val velocity: Double
}