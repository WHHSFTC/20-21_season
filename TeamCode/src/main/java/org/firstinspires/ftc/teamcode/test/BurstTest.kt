package org.firstinspires.ftc.teamcode.test

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import kotlinx.coroutines.delay
import org.firstinspires.ftc.teamcode.module.*

@Autonomous
@Config
class BurstTest: OpMode(Mode.TELE) {
    override suspend fun onInit() { }

    override suspend fun onRun() {
        bot.out(Shooter.State.FULL)
        delay(RAMPUP.toLong())
        repeat(N) {
            bot.feed.shoot()
            delay(BURST.toLong())
        }
        delay(AFTER.toLong())
        bot.out(Shooter.State.OFF)
    }

    override suspend fun onLoop() { }

    override suspend fun onStop() {
        bot.out(Shooter.State.OFF)
    }

    companion object {
        @JvmField var N: Int = 3
        @JvmField var RAMPUP: Int = 1000
        @JvmField var BURST: Int = 750
        @JvmField var AFTER: Int = 500
    }
}