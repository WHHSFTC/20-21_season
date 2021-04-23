package org.firstinspires.ftc.teamcode.module

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.util.epsilonEquals
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.switchboard.core.Activity
import org.firstinspires.ftc.teamcode.switchboard.core.Configuration
import org.firstinspires.ftc.teamcode.switchboard.core.Frame
import org.firstinspires.ftc.teamcode.switchboard.core.Logger
import org.firstinspires.ftc.teamcode.switchboard.hardware.Encoder
import org.firstinspires.ftc.teamcode.switchboard.hardware.Motor
import org.firstinspires.ftc.teamcode.switchboard.hardware.MotorImpl

@Config
class Shooter(config: Configuration, val logger: Logger): Module<Shooter.State>, Activity {
    val motor1: MotorImpl = config.motors["shoot"] as MotorImpl
    val enc: Encoder = config.encoders["shoot"]

    override fun load() {
        motor1.m.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        motor1.m.mode = DcMotor.RunMode.RUN_USING_ENCODER
        motor1.zpb = Motor.ZeroPowerBehavior.FLOAT

        //motor1.m.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
    }

    //val controller = PIDController(PID_COEFFICIENTS, { motor1.power = it }, { enc.position })

    override fun update(frame: Frame) {
        //if (controller.state epsilonEquals 0.0)
            //controller.update(frame.step.seconds)
        logger.out["Flywheel CPS"] = enc.velocity
    }

    enum class State(val vel: Double) {
        OFF(0.0), FULL(FULL_POWER), REVERSE(-1.0), POWER(.80)
    }

    override var state: State = State.OFF
        set(value) {
            if (value != field) {
                motor1.power = value.vel * coef
                // controller.state = value.vel * coef
            }
            field = value
        }

    companion object {
        @JvmField var coef = 1.0
        @JvmField var FULL_POWER = 1.0

        @JvmField var kP = 4.0
        @JvmField var kI = 0.0
        @JvmField var kD = 0.0

        val PID_COEFFICIENTS get() = PIDCoefficients(kP, kI, kD)
    }
}