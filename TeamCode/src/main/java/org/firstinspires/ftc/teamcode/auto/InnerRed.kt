package org.firstinspires.ftc.teamcode.auto

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.teamcode.module.*
import org.firstinspires.ftc.teamcode.module.vision.StackProcessor
import org.firstinspires.ftc.teamcode.switchboard.command.makeLinear
import org.firstinspires.ftc.teamcode.switchboard.command.toActivity
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time
import kotlin.math.PI

@Autonomous(name="INNER RED - ALPHAGO, BEACHBOTS")
class InnerRed: OpMode(Mode.AUTO, Alliance.RED, SetupPosition.INNER) {
    override fun initHook() {
        bot.wob.elbow(Wobble.ElbowState.STORE)
        bot.wob.claw(Wobble.ClawState.CLOSED)
        bot.ink.hook(Intake.HookPosition.LOCKED)

        bot.dt.poseEstimate = startPose

        bot.vis.stack.start()

        bot.prependActivity(inner().toActivity())
    }
}
