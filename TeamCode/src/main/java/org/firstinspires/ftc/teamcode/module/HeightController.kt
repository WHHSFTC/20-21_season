package org.firstinspires.ftc.teamcode.module

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.switchboard.core.Activity
import org.firstinspires.ftc.teamcode.switchboard.core.Configuration
import org.firstinspires.ftc.teamcode.switchboard.core.Frame
import org.firstinspires.ftc.teamcode.switchboard.core.Logger
import org.firstinspires.ftc.teamcode.switchboard.hardware.Motor
import org.firstinspires.ftc.teamcode.switchboard.hardware.MotorImpl

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
        logger.out["AIM"] = motor.m.currentPosition

        if (busy && !motor.m.isBusy) {
            motor.power = 0.0
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
        POWER(350, Shooter.State.POWER),  HIGH(197), ZERO(0), WALL(75), STACK(75);
    }

    val power = object : Module<Double> {
        override var state: Double = 0.0
            set(value) {
                motor.m.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
                motor.power = value
                busy = false
                field = value
            }
    }

    val height = object : Module<HeightController.Height> {
        override var state: Height = Height.ZERO
            set(value) {
                motor.m.targetPosition = value.pos
                motor.m.mode = DcMotor.RunMode.RUN_TO_POSITION
                motor.power = 0.5
                busy = true
                field = value
            }
    }
}