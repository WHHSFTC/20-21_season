package org.firstinspires.ftc.teamcode.switchboard.hardware

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.switchboard.stores.Observer

interface Motor: HardwareOutput {
    enum class ZeroPowerBehavior(val mirror: DcMotor.ZeroPowerBehavior) {
        BRAKE(DcMotor.ZeroPowerBehavior.BRAKE), FLOAT(DcMotor.ZeroPowerBehavior.FLOAT)
    }
    val power: Observer<Double>
    val zpb: Observer<ZeroPowerBehavior>
}