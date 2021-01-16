package org.firstinspires.ftc.teamcode.module

import com.qualcomm.robotcore.hardware.DcMotor
import java.lang.Thread.sleep

class Shooter(val bot: Robot): Module<Shooter.State> {
    val motor1: DcMotor = bot.hwmap.dcMotor["shoot"].also { it.power = -1.0; sleep(200); it.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER; it.mode = DcMotor.RunMode.RUN_USING_ENCODER }
    enum class State(val vel: Double) {
        OFF(0.0), FULL(1.0), REVERSE(1.0)
    }

    override var state: State = State.OFF
        set(value) {
            if (value != field)
                motor1.power = value.vel
            field = value
        }
}