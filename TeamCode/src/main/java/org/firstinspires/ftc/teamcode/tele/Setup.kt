package org.firstinspires.ftc.teamcode.tele

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.module.*
import org.firstinspires.ftc.teamcode.switchboard.command.makeLinear
import org.firstinspires.ftc.teamcode.switchboard.command.toActivity
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time
import kotlin.math.abs

@TeleOp(name="Setup")
class Setup: OpMode(Mode.AUTO, Alliance.BLUE, SetupPosition.INNER) {
    override fun initHook() {
        bot.wob.elbow(Wobble.ElbowState.STORE)
        bot.wob.claw(Wobble.ClawState.CLOSED)
        bot.ink.hook(Intake.HookPosition.LOCKED)

        bot.dt.poseEstimate = Pose2d()

        bot.vis.stack.start()

        bot.prependActivity(makeLinear {
            task { bot.aim.power(-.5) }
            delay(1000)
            task { bot.aim.power(0.0) }

            task {
                bot.dt.poseEstimate = Pose2d()
                bot.logger.out["S_INSTRUCTION"] = "move the bot forward 1 tile"
                bot.prependActivity(Controllers.Luke(gamepad1, bot))
            }

            awaitUntil(Time.seconds(2)) { gamepad1.b }

            task {
                if (abs(bot.dt.poseEstimate.x - 24.0) < 2.0 && abs(bot.dt.poseEstimate.y) < 4.0)
                    bot.logger.out["S_RESULT"] = "good :)"
                else
                    bot.logger.out["S_RESULT"] = "bad :("
            }

            task {
                bot.dt.poseEstimate = Pose2d()
                bot.logger.out["S_INSTRUCTION"] = "move the bot right 1 tile"
            }

            awaitUntil(Time.seconds(2)) { gamepad1.b }

            task {
                if (abs(bot.dt.poseEstimate.x) < 4.0 && abs(bot.dt.poseEstimate.y - 24.0) < 2.0)
                    bot.logger.out["S_RESULT"] = "good :)"
                else
                    bot.logger.out["S_RESULT"] = "bad :("
            }

            task {
                bot.dt.poseEstimate = Pose2d()
                bot.logger.out["S_INSTRUCTION"] = "watch"
            }

            val e = 12.0
            go(Pose2d()) { lineTo(Vector2d(e, 0.0)) }
            go(Pose2d(e, 0.0)) { lineTo(Vector2d(e, e)) }
            go(Pose2d(e, e)) { lineTo(Vector2d(0.0, e)) }
            go(Pose2d(0.0, e)) { lineTo(Vector2d(0.0, 0.0)) }
        }.toActivity())
    }
}
