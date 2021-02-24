package org.firstinspires.ftc.teamcode.auto

import com.qualcomm.robotcore.hardware.DcMotor
import kotlinx.coroutines.runBlocking
import org.firstinspires.ftc.teamcode.cmd.*
import org.firstinspires.ftc.teamcode.dsl.DslOpMode
import org.firstinspires.ftc.teamcode.module.Indexer
import org.firstinspires.ftc.teamcode.module.Wobble

val DslOpMode.autoInit: Command get() = runBlocking {
    CommandContext.seq {
        +setState(bot.aim.power) { -0.5 }
        +delayC(500)
        +setState(bot.aim.power) { .0 }
        +setState(bot.feed.feed) { Indexer.Shoot.PRE }
        +setState(bot.feed.height) { Indexer.Height.HIGH }
        +cmd {
            aim.motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        }
        +setState(bot.wob.claw) { Wobble.ClawState.CLOSED }
        +delayC(1000)
        +setState(bot.wob.elbow) { Wobble.ElbowState.STORE }
    }
}

