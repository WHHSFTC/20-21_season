package org.firstinspires.ftc.teamcode.switchboard.hw

import com.qualcomm.robotcore.hardware.DcMotor

interface Motor: HardwareOutput {
    enum class ZeroPowerBehavior(val mirror: DcMotor.ZeroPowerBehavior) {
        BRAKE(DcMotor.ZeroPowerBehavior.BRAKE), FLOAT(DcMotor.ZeroPowerBehavior.FLOAT)
    }
    var power: Double
    var zpb: ZeroPowerBehavior
}