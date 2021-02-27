package org.firstinspires.ftc.teamcode.test

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import kotlinx.coroutines.runBlocking
import org.firstinspires.ftc.teamcode.cmd.*
import org.firstinspires.ftc.teamcode.dsl.DslOpMode
import org.firstinspires.ftc.teamcode.module.HeightController
import org.firstinspires.ftc.teamcode.module.Indexer
import org.firstinspires.ftc.teamcode.module.Shooter
import org.firstinspires.ftc.teamcode.module.Wobble

@Autonomous
class WallShoot: DslOpMode(mode = Mode.AUTO) {
    init {
        runBlocking {
            dsl {
                val start: Pose2d = Pose2d(Vector2d(-63.0, 24.0), 0.0)
                onInit {
                    cmd {
                        wob.elbow(Wobble.ElbowState.STORE)
                        wob.claw(Wobble.ClawState.CLOSED)
                        dt.poseEstimate = start
                        dt.turn(-0.077)
                        log.logData("Init")
                        log.logData("...")
                        log.logData("Done")
                    }
                }

                onRun {
                    seq {
                        +cmd {
                            vis!!.halt()
                        }

                        +setState(bot.aim.height) { HeightController.Height.WALL }
                        +setState(bot.feed.height) { Indexer.Height.HIGH }
                        +setState(bot.out) { Shooter.State.FULL }
                        +delayC(1000)

                        +cmd { feed.slowBurst() }

                        //+setState(bot.aim.height) { HeightController.Height.ZERO }
                    }
                }
            }
        }
    }
}