package org.firstinspires.ftc.teamcode.module

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.switchboard.core.Activity
import org.firstinspires.ftc.teamcode.switchboard.core.Configuration
import org.firstinspires.ftc.teamcode.switchboard.core.Frame
import org.firstinspires.ftc.teamcode.switchboard.core.Logger
import org.firstinspires.ftc.teamcode.switchboard.hardware.EncoderImpl
import org.firstinspires.ftc.teamcode.switchboard.hardware.Motor
import org.firstinspires.ftc.teamcode.switchboard.hardware.MotorImpl
import kotlin.math.sign

class HeightController(config: Configuration, val logger: Logger) : Activity {
    val motor = config.motors["aim"] as MotorImpl
    val enc = config.encoders["aim"]
    var busy = false

    override fun load() {
        motor.zpb = Motor.ZeroPowerBehavior.BRAKE
        reset()
    }

    override fun update(frame: Frame) {
        //logger.out["AIM"] = enc.position
        logger.out["AIM"] = height.state.name
        logger.err["AIM_pos"] = motor.m.currentPosition
        logger.err["AIM_busy"] = busy

        //if (busy && !motor.m.isBusy) {
            //motor.power = 0.0
            //busy = false
        //}
        if (busy && enc.position == motor.m.targetPosition) {
            motor.power = 0.0
            motor.m.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
            busy = false
        }
    }

    fun reset() {
        enc.stopAndReset()
        busy = false
    }

    enum class Height(val pos: Int, val power: Shooter.State = Shooter.State.FULL) {
        //POWER(495, Shooter.State.POWER), THIRD_POWER(490, Shooter.State.POWER), HIGH(375), ZERO(0), WALL(292), EDGEPS(188), STACK(274);
        //POWER(350, Shooter.State.POWER),  HIGH(274), ZERO(0), WALL(75), STACK(75);
        //POWER(350, Shooter.State.POWER),  THREE(197), ZERO(0), WALL(75), STACK(75);
        THREE(265), TWO(276), MID(159), ZERO(0), MANUAL(0)
    }

    val power = object : Module<Double> {
        override var state: Double = 0.0
            set(value) {
                height(Height.MANUAL)
                motor.power = value
                field = value
            }
    }

    val height = object : Module<HeightController.Height> {
        override var state: Height = Height.ZERO
            set(value) {
                if (field == value) return
                if (value == Height.MANUAL) {
                    motor.m.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
                    busy = false
                    field = value
                    return
                }
                if (!busy) {
                    enc.stopAndReset()
                    val d = value.pos - field.pos
                    logger.out["AIM_d"] = d
                    motor.m.targetPosition = d
                    motor.m.mode = DcMotor.RunMode.RUN_TO_POSITION
                    motor.power = 0.5
                    busy = true
                    field = value
                }
            }
    }
}