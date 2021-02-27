package org.firstinspires.ftc.teamcode.auto

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import kotlinx.coroutines.runBlocking
import org.firstinspires.ftc.teamcode.cmd.*
import org.firstinspires.ftc.teamcode.dsl.*
import org.firstinspires.ftc.teamcode.module.*
import org.firstinspires.ftc.teamcode.module.vision.StackPipeline
import kotlin.math.PI

@Autonomous
class WobbleWallOld: DslOpMode(mode = Mode.AUTO) {
    init {
        runBlocking {dsl {
            val start: Pose2d = Pose2d(Vector2d(-64.0, 16.75), 0.0)

            onInit {
                seq {
                    +autoInit
                    +setState(bot.wob.elbow) { Wobble.ElbowState.STORE }
                    +setState(bot.wob.claw) { Wobble.ClawState.CLOSED }
                    +cmd {dt.poseEstimate = start; StackPipeline.StackConstants.MIN_WIDTH = 35}
                }
            }

            val autoBurst = CommandContext.seq {
                +cmd {
                    telemetry.addData("aim", aim.motor.currentPosition)
                    telemetry.update()
                }
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

            val dropWob = CommandContext.seq {
                +setState(bot.wob.claw) { Wobble.ClawState.OPEN }
                +delayC(500)
                +setState(bot.wob.elbow) { Wobble.ElbowState.STORE }
            }

            val takeWob = CommandContext.seq {
                +setState(bot.wob.claw) { Wobble.ClawState.CLOSED }
                +delayC(500)
                +setState(bot.wob.elbow) { Wobble.ElbowState.CARRY }
            }

            val shootPose = Pose2d(-3.0, 24.0, 0.0)

            onRun {
                seq {
                    +cmd { vis!!.halt() }
                    +wallShoot
                    +autoBurst
                    +switch({ vis!!.pipeline.height }, listOf(
                            case({ StackPipeline.Height.ONE }, CommandContext.seq {
                                +setState(bot.ink) { Intake.Power.IN }
                                +go(start) {
                                    splineTo(Vector2d(-24.0, 36.0), 0.0)
                                    splineTo(Vector2d(0.0, 36.0), 0.0)
                                    addDisplacementMarker {
                                        bot.ink(Intake.Power.OFF)
                                        bot.wob.elbow(Wobble.ElbowState.DROP)
                                    }
                                    splineTo(Vector2d(24.0, 36.0), 0.0)
                                }
                                +dropWob
                                +go(Pose2d(24.0, 36.0, 0.0)) {
                                    lineToSplineHeading(shootPose)
                                }
                                +lineShoot
                                +autoBurst
                            }),
                            case({ StackPipeline.Height.FOUR }, CommandContext.seq {
                                //+setState(bot.wob.elbow) { Wobble.ElbowState.RING }

                                //var pose = Pose2d(-40.0, 36.0, 0.0)
                                var pose = Pose2d(-36.0, 36.0, 0.0)

                                +go(start) {
                                    //splineToConstantHeading(pose.vec(),  PI/2.0)
                                    splineTo(pose.vec(),  0.0)
                                }

                                +setState(bot.wob.elbow) { Wobble.ElbowState.CARRY }

                                +setState(bot.feed.height) { Indexer.Height.IN }

                                repeat(3) {
                                    +delayC(500)
                                    +go(pose) { forward(6.0) }
                                    +setState(bot.ink) { Intake.Power.IN }
                                    +go(pose + Pose2d(6.0, 0.0, 0.0)) { forward(4.0) }
                                    +delayC(500)
                                    +setState(bot.ink) { Intake.Power.OFF }
                                    +setState(bot.feed.height) { Indexer.Height.HIGH }
                                    +delayC(500)
                                    +setState(bot.feed.height) { Indexer.Height.IN }

                                    pose += Pose2d(10.0, 0.0, 0.0)
                                }

                                +setState(bot.ink) { Intake.Power.OUT }

                                +go(pose) {
                                    splineToConstantHeading(pose.vec() + Vector2d(-24.0, -12.0), 3.0 * PI / 2.0)
                                    splineToConstantHeading(shootPose.vec(), 0.0)
                                }
                                +setState(bot.ink) { Intake.Power.OFF }
                                +lineShoot
                                +autoBurst

                                +setState(bot.wob.elbow) {Wobble.ElbowState.DROP }
                                +go(shootPose) {
                                    splineTo(Vector2d(52.0, 60.0), 0.0)
                                }
                                // drop first wobble
                                +dropWob

                                +cmd {dt.turn(-PI)}

                                +go(Pose2d(48.0, 57.0, PI)) {
                                    splineTo(Vector2d(24.0, 36.0), PI)
                                    addDisplacementMarker {
                                        bot.ink(Intake.Power.IN)
                                    }
                                    splineTo(Vector2d(-24.0, 36.0), PI)
                                    addDisplacementMarker {
                                        bot.ink(Intake.Power.OFF)
                                    }
                                    splineToConstantHeading(Vector2d(-34.0, 48.0), PI/2.0)
                                    addDisplacementMarker {
                                        bot.wob.elbow(Wobble.ElbowState.INTAKE)
                                        bot.feed.height(Indexer.Height.HIGH)
                                    }
                                    splineToConstantHeading(Vector2d(-37.0, 57.0), PI/2.0, constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                }

                                +takeWob

                                +go(Pose2d(-37.0, 31.0, PI)) {
                                    lineToLinearHeading(shootPose)
                                }

                                +lineShoot
                                +autoBurst

                                +setState(bot.wob.elbow) { Wobble.ElbowState.DROP }
                                +go(shootPose) {
                                    splineTo(Vector2d(43.0, 60.0), 0.0)
                                }
                                //drop second wobble
                                +dropWob

                                +go(Pose2d(43.0, 60.0, 0.0), reversed = true) {
                                    splineTo(Vector2d(12.0, 24.0), PI)
                                }
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