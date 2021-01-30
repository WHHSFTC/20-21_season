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
class WobbleFork: DslOpMode(mode = Mode.AUTO) {
    init {
        runBlocking {dsl {
            val start: Pose2d = Pose2d(Vector2d(-64.0, 21.75), 0.0)

            onInit {
                seq {
                    +autoInit
                    +setState(bot.wob.elbow) { Wobble.ElbowState.STORE }
                    +setState(bot.wob.claw) { Wobble.ClawState.CLOSED }
                    +cmd {dt.poseEstimate = start; VisionPipeline.VisionConstants.MIN_WIDTH = 35}
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
                +delayC(750)
                +cmd {feed.shoot()}
                +delayC(750)
                +cmd {feed.shoot()}
                +delayC(750)
                +setState(bot.feed.height) {Indexer.Height.IN}
                +delayC(500)
                +setState(bot.out) {Shooter.State.OFF}
            }

            val singleShot = CommandContext.seq {
                +cmd {feed.shoot()}
                +delayC(750)
                +setState(bot.feed.height) {Indexer.Height.IN}
                +delayC(500)
                +setState(bot.out) {Shooter.State.OFF}
            }

            val stackShoot = CommandContext.seq {
                +setState(bot.feed.height) {Indexer.Height.POWER}
                +setState(bot.aim.height) {HeightController.Height.STACK}
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

            val linePose = Pose2d(-3.0, 24.0, 0.0)

            onRun {
                seq {
                    +cmd { vis!!.halt() }
                    +switch({ vis!!.height }, listOf(
                            case({ VisionPipeline.Height.ZERO }, CommandContext.seq {
                                // shoot from line
                                +go(start) { lineToConstantHeading(linePose.vec() ) }
                                +lineShoot
                                +autoBurst

                                // drop wobble at A
                                +setState(bot.wob.elbow) { Wobble.ElbowState.DROP }
                                +go(linePose) { splineTo(Vector2d(24.0, 48.0), PI/2.0) }
                                +dropWob

                                // intake wobble
                                +go(Pose2d(24.0, 48.0, PI)) {
                                    splineToConstantHeading(Vector2d(-36.0, 36.0), PI, constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                    addDisplacementMarker {
                                        bot.wob.elbow(Wobble.ElbowState.INTAKE)
                                    }
                                    splineToConstantHeading(Vector2d(-36.0, 48.0), Math.toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                    splineToConstantHeading(Vector2d(-38.0, 55.0), Math.toRadians(90.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                }
                                +takeWob

                                // drop wobble 2 at A
                                +setState(bot.wob.elbow) { Wobble.ElbowState.DROP }
                                +go(Pose2d(-37.0, 55.0, PI)) { lineToLinearHeading(Pose2d(18.0, 48.0, PI/2.0)) }
                                +dropWob

                                +go(Pose2d(18.0, 48.0, PI/2.0)) {
                                    lineToLinearHeading(Pose2d(12.0, 24.0, 0.0))
                                }
                            }),
                            case({ VisionPipeline.Height.ONE }, CommandContext.seq {
                                // shoot from line
                                +go(start) { lineToConstantHeading(linePose.vec() ) }
                                +lineShoot
                                +autoBurst

                                // drop wobble at B
                                +setState(bot.wob.elbow) { Wobble.ElbowState.DROP }
                                +go(linePose) { lineToConstantHeading(Vector2d(24.0, 36.0)) }
                                +dropWob

                                // intake
                                +setState(bot.ink) { Intake.Power.IN }
                                +go(Pose2d(24.0, 36.0, PI)) {
                                    splineToConstantHeading(Vector2d(-36.0, 36.0), PI, constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                    addDisplacementMarker {
                                        bot.wob.elbow(Wobble.ElbowState.INTAKE)
                                    }
                                    splineToConstantHeading(Vector2d(-36.0, 48.0), Math.toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                    splineToConstantHeading(Vector2d(-38.0, 55.0), Math.toRadians(90.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                }
                                +setState(bot.ink) { Intake.Power.OFF }
                                +setState(bot.feed.height) { Indexer.Height.HIGH }
                                +takeWob

                                // drop wobble 2 at B
                                +setState(bot.wob.elbow) { Wobble.ElbowState.DROP }
                                +go(Pose2d(-37.0, 55.0, 3.0 * PI / 2.0)) { lineToLinearHeading(Pose2d(18.0, 36.0, 0.0)) }
                                +dropWob

                                +setState(bot.out) { Shooter.State.FULL }
                                +go(Pose2d(18.0, 36.0, 0.0)) { lineToConstantHeading(linePose.vec()) }

                                +lineShoot
                                +singleShot

                                +go(linePose) {
                                    lineToConstantHeading(Vector2d(12.0, 24.0))
                                }
                            }),
                            case({ VisionPipeline.Height.FOUR }, CommandContext.seq {
                                val firstShot = Pose2d(-36.0, 21.75, 0.0)

                                +go(start) {
                                    lineToConstantHeading(firstShot.vec())
                                }
                                +stackShoot
                                +autoBurst
                                //+setState(bot.wob.elbow) { Wobble.ElbowState.RING }

                                //var pose = Pose2d(-40.0, 36.0, 0.0)
                                var pose = Pose2d(-36.0, 36.0, 0.0)

                                +go(firstShot) {
                                    //splineToConstantHeading(pose.vec(),  PI/2.0)
                                    //splineTo(pose.vec(),  0.0)
                                    lineToConstantHeading(pose.vec()  )
                                }

                                +setState(bot.wob.elbow) { Wobble.ElbowState.CARRY }

                                +setState(bot.feed.height) { Indexer.Height.IN }

                                repeat(3) {
                                    //+delayC(500)
                                    +go(pose) { forward(6.0) }
                                    +setState(bot.ink) { Intake.Power.IN }
                                    +go(pose + Pose2d(6.0, 0.0, 0.0)) { forward(4.0) }
                                    +delayC(750)
                                    +setState(bot.ink) { Intake.Power.OFF }

                                    if (it == 1) {
                                        +setState(bot.feed.height) { Indexer.Height.HIGH }
                                        +delayC(500)
                                        +setState(bot.feed.height) { Indexer.Height.IN }
                                        +delayC(500)
                                    }

                                    pose += Pose2d(10.0, 0.0, 0.0)
                                }

                                +setState(bot.ink) { Intake.Power.OUT }

                                +go(pose) {
                                    splineToConstantHeading(pose.vec() + Vector2d(-12.0, -24.0), 3.0 * PI / 2.0)
                                    splineToConstantHeading(linePose.vec(), 0.0)
                                }
                                +setState(bot.ink) { Intake.Power.OFF }
                                +lineShoot
                                +autoBurst

                                +setState(bot.wob.elbow) {Wobble.ElbowState.DROP }
                                +go(linePose) {
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
                                    lineToLinearHeading(linePose)
                                }

                                +lineShoot
                                +autoBurst

                                +setState(bot.wob.elbow) { Wobble.ElbowState.DROP }
                                +go(linePose) {
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