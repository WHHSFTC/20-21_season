package org.firstinspires.ftc.teamcode.module

import org.firstinspires.ftc.robotcore.external.Telemetry

open class Robot(val opmode: OpMode) {
    val dt: DriveTrain = DriveTrain(opmode)
    val telem: Telemetry = opmode.telemetry
}