package org.firstinspires.ftc.teamcode.auto

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import kotlinx.coroutines.runBlocking
import org.firstinspires.ftc.teamcode.cmd.*
import org.firstinspires.ftc.teamcode.dsl.*
import org.firstinspires.ftc.teamcode.module.*
import kotlin.math.PI

@Autonomous
class WobbleWall: DslOpMode(mode = Mode.AUTO) {
    init {
        runBlocking {dsl {
            val start: Pose2d = Pose2d(Vector2d(-64.0, 16.75), 0.0)

            onInit {
                seq {
                    +setState(bot.wob.elbow) { Wobble.ElbowState.STORE }
                    +setState(bot.wob.claw) { Wobble.ClawState.CLOSED }
                    +cmd {dt.poseEstimate = start}
                }
            }

            val autoBurst = CommandContext.seq {
                +setState(bot.out) {Shooter.State.FULL}
                +delayC(1000)
                +cmd {feed.shoot()}
                +delayC(500)
                +cmd {feed.shoot()}
                +delayC(500)
                +cmd {feed.shoot()}
                +delayC(500)
                +setState(bot.feed.height) {Indexer.Height.IN}
                +delayC(500)
                +setState(bot.out) {Shooter.State.OFF}
            }

            val wallShoot = CommandContext.seq {
                +setState(bot.feed.height) {Indexer.Height.POWER}
                +setState(bot.aim.height) {HeightController.Height.WALL}
            }

            val lineShoot = CommandContext.seq {
                +setState(bot.feed.height) {Indexer.Height.HIGH}
                +setState(bot.aim.height) {HeightController.Height.HIGH}
            }

            onRun {
                seq {
                    +cmd { vis!!.halt() }
                    +wallShoot
                    +autoBurst
                    +switch({ vis!!.height }, listOf(
                            case({ VisionPipeline.Height.ONE }, CommandContext.seq {
                                +go(start) {
                                    addDisplacementMarker {
                                        bot.ink(Intake.Power.IN)
                                    }
                                    splineTo(Vector2d(-24.0, 36.0), 0.0)
                                    splineTo(Vector2d(0.0, 36.0), 0.0)
                                    addDisplacementMarker {
                                        bot.ink(Intake.Power.OFF)
                                        bot.wob.elbow(Wobble.ElbowState.DROP)
                                    }
                                    splineTo(Vector2d(24.0, 36.0), 0.0)
                                }
                                +setState(bot.wob.claw) {Wobble.ClawState.OPEN}
                                +delayC(500)
                                +setState(bot.wob.elbow) { Wobble.ElbowState.STORE }
                                +go(Pose2d(12.0, 36.0, 0.0)) {
                                    lineToSplineHeading(Pose2d(-3.0, 24.0, 0.0))
                                }
                                +lineShoot
                                +autoBurst
                            })
                    ))
                }
            }

            onStop {
                seq {
                    +setState(bot.aim.height) { HeightController.Height.ZERO }
                    +cmd {
                        dt.powers = CustomMecanumDrive.Powers()
                        if (DEBUG)
                            dt.followTrajectory(dt.trajectoryBuilder(dt.poseEstimate).lineToSplineHeading(start).build())
                    }
                }
            }
        }}
    }
}