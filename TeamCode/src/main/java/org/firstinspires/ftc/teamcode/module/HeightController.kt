package org.firstinspires.ftc.teamcode.module

import com.qualcomm.robotcore.hardware.DcMotor

class HeightController(val bot: Robot): Module<HeightController.State> {
    val motor = bot.hwmap.dcMotor["aim"].also { it.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER }

    enum class State(val pos: Int) {
        POWER(2919), HIGH(0);
    }

    override var state: State = State.HIGH
        set(value) {
            motor.targetPosition = value.pos
            motor.mode = DcMotor.RunMode.RUN_TO_POSITION
            motor.power = 1.0
            field = value
        }
}