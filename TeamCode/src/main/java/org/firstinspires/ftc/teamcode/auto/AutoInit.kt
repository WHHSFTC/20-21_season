package org.firstinspires.ftc.teamcode.auto

import com.qualcomm.robotcore.hardware.DcMotor
import kotlinx.coroutines.runBlocking
import org.firstinspires.ftc.teamcode.cmd.*
import org.firstinspires.ftc.teamcode.dsl.DslOpMode

val DslOpMode.autoInit: Command get() = runBlocking {
    CommandContext.seq {
        setState(bot.aim.power) { -0.5 }
        delayC(500)
        setState(bot.aim.power) { .0 }
        cmd {
            aim.motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        }
    }
}

