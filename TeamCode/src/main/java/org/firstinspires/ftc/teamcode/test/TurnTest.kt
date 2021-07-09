package org.firstinspires.ftc.teamcode.test

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.module.OpMode
import java.lang.Math.toRadians

@Config
@Autonomous(group = "test")
class TurnTest : OpMode(Mode.AUTO) {
    override fun startHook() {
        bot.dt.turnAsync(toRadians(ANGLE))
    }

    companion object  {
        var ANGLE: Double = 180.0 // deg
    }
}