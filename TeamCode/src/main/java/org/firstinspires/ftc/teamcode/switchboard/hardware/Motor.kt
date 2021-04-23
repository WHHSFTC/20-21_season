package org.firstinspires.ftc.teamcode.switchboard.hardware

import com.qualcomm.robotcore.hardware.DcMotor

interface Motor: HardwareOutput {
    enum class ZeroPowerBehavior(val mirror: DcMotor.ZeroPowerBehavior) {
        BRAKE(DcMotor.ZeroPowerBehavior.BRAKE), FLOAT(DcMotor.ZeroPowerBehavior.FLOAT);

        companion object {
            fun mirrorOf(mode: DcMotor.ZeroPowerBehavior)
                = when (mode) {
                    DcMotor.ZeroPowerBehavior.BRAKE -> BRAKE
                    DcMotor.ZeroPowerBehavior.FLOAT -> FLOAT
                    DcMotor.ZeroPowerBehavior.UNKNOWN -> FLOAT
            }
        }
    }
    var power: Double
    var zpb: ZeroPowerBehavior
}