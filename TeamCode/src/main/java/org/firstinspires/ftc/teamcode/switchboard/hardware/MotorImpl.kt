package org.firstinspires.ftc.teamcode.switchboard.hardware

import com.qualcomm.robotcore.hardware.DcMotorEx
import org.firstinspires.ftc.teamcode.switchboard.core.Logger
import org.firstinspires.ftc.teamcode.switchboard.stores.*

class MotorImpl(val m: DcMotorEx, val name: String, val logger: Logger): Motor {
    override val power: Subject<Double, Double> = entry<Double>()
    override val zpb: Subject<Motor.ZeroPowerBehavior, Motor.ZeroPowerBehavior> = entry<Motor.ZeroPowerBehavior>()
    private val arbiter = entry<Unit>()

    init {
        power.turnstile(arbiter).dedup(0.0).map {
            m.power = it
        }

        zpb.turnstile(arbiter).dedup(Motor.ZeroPowerBehavior.BRAKE).map {
            m.zeroPowerBehavior = it.mirror
        }

        power.log(logger.err, "$name power")
        zpb.log(logger.err, "$name zpb")
    }

    override fun output(all: Boolean) {
        arbiter.next(Unit)
    }
}