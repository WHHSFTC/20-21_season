package org.firstinspires.ftc.teamcode.test

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.geometry.Pose2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.hardware.DcMotor
import kotlinx.coroutines.runBlocking
import org.firstinspires.ftc.teamcode.auto.autoInit
import org.firstinspires.ftc.teamcode.cmd.*
import org.firstinspires.ftc.teamcode.dsl.DslOpMode
import org.firstinspires.ftc.teamcode.module.*

@Config
@Autonomous
class PowerShotTest: DslOpMode(mode = Mode.TELE) {
    init { runBlocking { dsl {
        onInit {
            seq {
                +autoInit
                +setState(bot.wob.elbow) { Wobble.ElbowState.STORE }
                +setState(bot.wob.claw) { Wobble.ClawState.CLOSED }
                +cmd {dt.poseEstimate = start}
            }
        }

        onRun {
            seq {
                if (move)
                    +go(start) {
                        lineToLinearHeading(pspose)
                    }

                +setState(bot.aim.height) { HeightController.Height.POWER }
                +cmd {
                    aim.motor.targetPosition = height
                    aim.motor.mode = DcMotor.RunMode.RUN_TO_POSITION
                    aim.motor.power = 0.5
                }
                +cmd {
                    out.motor1.power = velo
                }
                +setState(bot.feed.height) { Indexer.Height.POWER }
                +delayC(1000)

                var new = pspose
                repeat(n) {
                    +cmd {feed.shoot()}
                    val old = new
                    +delayC(1000)
                    new += Pose2d(0.0,  between, 0.0)
                    if (move)
                        +go(old) {
                            lineTo(new.vec())
                        }
                }

                +setState(bot.aim.height) { HeightController.Height.ZERO }
                +cmd {
                    out.motor1.power = 0.0
                }
                +setState(bot.feed.height) { Indexer.Height.IN }

                if (move)
                    +go(new) {
                        lineToLinearHeading(start)
                    }

                +delayC(0)
            }
        }
    } } }
    companion object {
        @JvmField var startx = -64.0
        @JvmField var starty = 21.75
        @JvmField var startth = 0.0
        val start get() = Pose2d(startx, starty, startth)

        @JvmField var psposex = 0.0
        @JvmField var psposey = 0.0
        @JvmField var psposeth = 0.0
        val pspose get() = Pose2d(psposex, psposey, psposeth)

        @JvmField var between = 7.5
        @JvmField var velo = 1.0

        @JvmField var move = true
        @JvmField var n = 3

        @JvmField var height = HeightController.Height.POWER.pos
    }
}