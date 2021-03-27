package org.firstinspires.ftc.teamcode.switchboard.hardware

import com.qualcomm.robotcore.hardware.DcMotorEx
import org.firstinspires.ftc.teamcode.switchboard.hardware.Motor

class MotorImpl(val m: DcMotorEx): Motor {
    //var conf: Power = Power(0.0)
    //open class Power(val pow: Double, val zpb: DcMotorEx.ZeroPowerBehavior = DcMotorEx.ZeroPowerBehavior.BRAKE) {
    //object BRAKE : Power(0.0, DcMotorEx.ZeroPowerBehavior.BRAKE)
    //object FLOAT : Power(0.0, DcMotorEx.ZeroPowerBehavior.FLOAT)
    //}
    override var power: Double = 0.0
    override var zpb: Motor.ZeroPowerBehavior = Motor.ZeroPowerBehavior.BRAKE
    override fun output() {
        if (m.power != power)
            m.power = power
        if (m.zeroPowerBehavior != zpb.mirror)
            m.zeroPowerBehavior = zpb.mirror
    }
}