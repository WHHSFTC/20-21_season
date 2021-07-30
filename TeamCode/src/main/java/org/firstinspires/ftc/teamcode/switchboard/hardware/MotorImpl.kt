package org.firstinspires.ftc.teamcode.switchboard.hardware

import com.acmerobotics.roadrunner.util.epsilonEquals
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import org.firstinspires.ftc.teamcode.switchboard.core.Logger

class MotorImpl(val m: DcMotorEx, val name: String, val log: Logger): Motor {
    //var conf: Power = Power(0.0)
    //open class Power(val pow: Double, val zpb: DcMotorEx.ZeroPowerBehavior = DcMotorEx.ZeroPowerBehavior.BRAKE) {
    //object BRAKE : Power(0.0, DcMotorEx.ZeroPowerBehavior.BRAKE)
    //object FLOAT : Power(0.0, DcMotorEx.ZeroPowerBehavior.FLOAT)
    //}
    init {
        m.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        m.power = 0.0
        m.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
        m.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
    }

    override var power: Double = 0.0
    override var zpb: Motor.ZeroPowerBehavior = Motor.ZeroPowerBehavior.BRAKE
    var n = 0
        private set
    override fun output(all: Boolean) {
        val g = m.power
        if (!(g epsilonEquals power)) {
            m.power = power
            n++
        }
        if (m.zeroPowerBehavior != zpb.mirror)
            m.zeroPowerBehavior = zpb.mirror

        log.err["$name power"] = power
        log.err["$name count"] = n
        log.err["$name get"] = g
        log.err["$name zpb"] = zpb
    }
}