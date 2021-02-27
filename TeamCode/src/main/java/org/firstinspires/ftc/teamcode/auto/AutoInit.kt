package org.firstinspires.ftc.teamcode.auto

import com.qualcomm.robotcore.hardware.DcMotor
import kotlinx.coroutines.runBlocking
import org.firstinspires.ftc.teamcode.cmd.*
import org.firstinspires.ftc.teamcode.dsl.DslOpMode

private val autoInitCommand: Command = runBlocking {
    CommandContext.seq {
        setState(bot.aim.power) { -0.5 }
        delayFor(500)
        setState(bot.aim.power) { +0.0 }
        cmd {
            aim.motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        }
    }
}

val DSLContext.autoInit: Command
    get() = autoInitCommand

val DslOpMode.autoInit: Command
    get() = autoInitCommand