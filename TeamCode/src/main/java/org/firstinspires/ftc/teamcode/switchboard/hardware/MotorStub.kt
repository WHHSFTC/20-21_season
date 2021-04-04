package org.firstinspires.ftc.teamcode.switchboard.hardware

import com.qualcomm.robotcore.hardware.DcMotorEx
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.switchboard.core.Log
import org.firstinspires.ftc.teamcode.switchboard.core.Switchboard
import org.firstinspires.ftc.teamcode.switchboard.hardware.Motor

class MotorStub(val name: String, val log: Log): Motor {
    override var power: Double = 0.0
    override var zpb: Motor.ZeroPowerBehavior = Motor.ZeroPowerBehavior.BRAKE

    override fun output() {
        log["[STUB] $name power"] = power
        log["[STUB] $name zpb"] = zpb
    }
}