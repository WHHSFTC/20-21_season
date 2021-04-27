package org.firstinspires.ftc.teamcode.auto

import android.transition.TransitionManager.go
import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.acmerobotics.roadrunner.trajectory.Trajectory
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import kotlinx.coroutines.runBlocking
import org.firstinspires.ftc.teamcode.cmd.*
import org.firstinspires.ftc.teamcode.dsl.*
import org.firstinspires.ftc.teamcode.module.*
import org.firstinspires.ftc.teamcode.module.vision.PipelineRunner
import org.firstinspires.ftc.teamcode.module.vision.RingPipeline
import org.firstinspires.ftc.teamcode.module.vision.StackPipeline
import kotlin.math.*

@Disabled
@Config
@Autonomous(name = "Blue - High Goal 6 rings :((((")
class ForAndrew: DslOpMode(mode = Mode.AUTO) {
    init {
        runBlocking {dsl {
            val start: Pose2d = Pose2d(Vector2d(-62.0, 15.5), 0.0)

            onInit {
                seq {
                    +autoInit
                    +cmd {dt.poseEstimate = start; StackPipeline.StackConstants.MIN_WIDTH = StackPipeline.StackConstants.FAR_MIN_WIDTH}
                }
            }
            val prepFull = CommandContext.seq {+setState(bot.out) { Shooter.State.FULL }}

            suspend fun autoBurst(t: Long): Command = CommandContext.seq {
                +cmd {
                    telemetry.addData("aim", aim.motor.currentPosition)
                    telemetry.update()
                }
                +cmd {feed.shoot()}
                +delayC(t)
                +cmd {feed.shoot()}
                +delayC(t)
                +cmd {feed.shoot()}
                +delayC(150)
                +setState(bot.feed.height) {Indexer.Height.IN}
                +delayC(500)
                +setState(bot.out) {Shooter.State.OFF}
            }

            val lineBurst = autoBurst(150)
            val slowBurst = autoBurst(750)

            val singleShot = CommandContext.seq {
                +cmd {feed.shoot()}
                +delayC(750)
                +setState(bot.feed.height) {Indexer.Height.IN}
                +delayC(500)
                +setState(bot.out) {Shooter.State.OFF}
            }

            val stackSet = CommandContext.seq {
                +setState(bot.feed.height) {Indexer.Height.POWER}
                +setState(bot.aim.height) {HeightController.Height.STACK}
            }

            val lineSet = CommandContext.seq {
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
            }

            val linePose = Pose2d(0.0, 24.0, 0.0)

            val psPose = Pose2d(0.0, PS_Y, 0.0)
            val psBetween = 7.5
            val psEndPose = psPose + Pose2d(0.0, 2 * psBetween, 0.0)
            val powerShots = CommandContext.seq {
                +cmd { feed.shoot() }
                +delayC(500)
                +go(psPose) {
                    strafeLeft(psBetween)
                }
                +cmd { feed.shoot() }
                +delayC(500)
                +go(psPose + Pose2d(0.0, psBetween, 0.0)) {
                    strafeLeft(psBetween)
                }
                +cmd { feed.shoot() }
                +delayC(500)
            }

            onRun {
                seq {
                    //+cmd { vis!!.halt() }
                    +switch({ vis!!.stack.height }, listOf(
                            case({ StackPipeline.Height.ZERO }, CommandContext.seq {
                                // shoot from line
                                +prepFull
                                +lineSet
                                +go(start) { lineToConstantHeading(linePose.vec() + Vector2d(0.0, 4.0)) }
                                +slowBurst

                                // drop wobble at A
                                +setState(bot.wob.elbow) { Wobble.ElbowState.DROP }
                                +go(linePose + Pose2d(0.0, 4.0)) { splineTo(Vector2d(18.0, 50.0), PI/2.0) }
                                +dropWob

                                +go(Pose2d(18.0, 50.0, PI/2.0)) { lineToConstantHeading(Vector2d(18.0, 24.0)) }

                                //+cmd {dt.turn(PI/2.0)}

                                // intake wobble
                                +go(Pose2d(18.0, 24.0, PI)) {
                                    addDisplacementMarker {
                                        bot.wob.elbow(Wobble.ElbowState.INTAKE)
                                    }
                                    splineToConstantHeading(Vector2d(-24.0, 52.0), Math.toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                    splineToConstantHeading(Vector2d(-37.0, 54.0), Math.toRadians(90.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                }
                                +takeWob

                                // drop wobble 2 at A
                                +go(Pose2d(-36.0, 59.0, PI)) { lineToLinearHeading(Pose2d(12.0, 44.0, PI/2.0)) }
                                +dropWob

                                +go(Pose2d(12.0, 44.0, PI/2.0)) {
                                    lineToLinearHeading(Pose2d(12.0, 24.0, 0.0))
                                }
                            }),
                            case({ StackPipeline.Height.ONE }, CommandContext.seq {
                                // shoot from line
                                +prepFull
                                +lineSet
                                +go(start) { lineToConstantHeading(linePose.vec()+Vector2d(0.0, 4.0) ) }
                                +slowBurst

                                // drop wobble at B
                                +setState(bot.wob.elbow) { Wobble.ElbowState.DROP }
                                +go(linePose + Pose2d(0.0, 4.0)) { lineToConstantHeading(Vector2d(26.0, 32.0)) }
                                +dropWob

                                +go(Pose2d(26.0, 32.0, )) { lineToConstantHeading(Vector2d(10.0, 36.0)) }

                                //+cmd {dt.turn(PI)}
                                +go(Pose2d(10.0, 36.0, 0.0)) {
                                    lineToLinearHeading(Pose2d(0.0, 12.0, PI/2.0))
                                }

                                // intake
                                +setState(bot.ink) { Intake.Power.IN }
                                +go(Pose2d(0.0, 12.0, PI/2.0)) {
                                    splineTo(Vector2d(-24.0, 36.0), PI, constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                    addDisplacementMarker {
                                        bot.wob.elbow(Wobble.ElbowState.INTAKE)
                                    }
                                    splineToConstantHeading(Vector2d(-34.0, 48.0), Math.toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                    splineToConstantHeading(Vector2d(-37.0, 59.0), Math.toRadians(90.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                }
                                +setState(bot.ink) { Intake.Power.OFF }
                                +setState(bot.feed.height) { Indexer.Height.HIGH }
                                +takeWob

                                // drop wobble 2 at B
                                +go(Pose2d(-37.0, 59.0, 3.0 * PI / 2.0)) { lineToLinearHeading(Pose2d(18.0, 32.0, 0.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS) }
                                +dropWob
                                +setState(bot.wob.elbow) {Wobble.ElbowState.DROP}

                                +prepFull
                                +lineSet
                                +go(Pose2d(18.0, 32.0, 0.0)) { lineToConstantHeading(linePose.vec() - Vector2d(8.0)) }

                                +slowBurst
                                +setState(bot.wob.elbow) {Wobble.ElbowState.STORE}

                                +go(linePose - Pose2d(8.0)) {
                                    //lineToConstantHeading(Vector2d(12.0, 24.0))
                                    forward(18.0)
                                }
                            }),
                            case({ StackPipeline.Height.FOUR }, CommandContext.seq {
                                // shoot from line
                                +prepFull
                                +lineSet
                                +go(start) { lineToConstantHeading(linePose.vec()+Vector2d(0.0, 4.0) ) }
                                +slowBurst

                                // drop wobble at C
                                +setState(bot.wob.elbow) { Wobble.ElbowState.DROP }
                                +go(linePose + Pose2d(0.0, 4.0)) {
                                    strafeTo(Vector2d(46.0, 44.0))
                                }
                                +dropWob

                                +go(Pose2d(46.0, 44.0)) { lineToConstantHeading(Vector2d(10.0, 36.0)) }

                                //+cmd {dt.turn(PI)}
                                +go(Pose2d(10.0, 36.0, 0.0)) {
                                    lineToLinearHeading(Pose2d(0.0, 12.0, PI/2.0))
                                }

                                // intake
                                +setState(bot.ink) { Intake.Power.IN }
                                +go(Pose2d(0.0, 12.0, PI/2.0)) {
                                    splineTo(Vector2d(-24.0, 36.0), PI, constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                    addDisplacementMarker {
                                        bot.wob.elbow(Wobble.ElbowState.INTAKE)
                                    }
                                    splineToConstantHeading(Vector2d(-34.0, 48.0), Math.toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                    splineToConstantHeading(Vector2d(-37.0, 59.0), Math.toRadians(90.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                }
                                +setState(bot.ink) { Intake.Power.OFF }
                                +setState(bot.feed.height) { Indexer.Height.HIGH }
                                +takeWob

                                // drop wobble 2 at C
                                +go(Pose2d(-37.0, 59.0, 3.0 * PI / 2.0)) { lineToLinearHeading(Pose2d(18.0, 32.0, 0.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS) }
                                +go(Pose2d(-37.0, 58.0, 3.0*PI/2.0)) {
                                    splineTo(Vector2d(42.0, 36.0), PI/4.0)
                                    addDisplacementMarker {
                                        bot.wob.claw(Wobble.ClawState.OPEN)
                                        bot.wob.elbow(Wobble.ElbowState.CARRY)
                                    }
                                    splineToConstantHeading(Vector2d(12.0, 36.0), -PI/2.0)
                                }
                                +dropWob
                                +setState(bot.wob.elbow) {Wobble.ElbowState.DROP}

                                +prepFull
                                +lineSet
                                +go(Pose2d(18.0, 32.0, 0.0)) { lineToConstantHeading(linePose.vec() - Vector2d(8.0)) }

                                +slowBurst
                                +setState(bot.wob.elbow) {Wobble.ElbowState.STORE}

                                +go(linePose - Pose2d(8.0)) {
                                    //lineToConstantHeading(Vector2d(12.0, 24.0))
                                    forward(18.0)
                                }
                            })
                    ))
                }
            }

            onStop {
                seq {
                    //+setState(bot.aim.height) { HeightController.Height.ZERO }
                    +cmd {
                        dt.powers = CustomMecanumDrive.Powers()
                        //if (DEBUG)
                            //dt.followTrajectory(dt.trajectoryBuilder(dt.poseEstimate).lineToSplineHeading(start).build())
                    }
                }
            }
        }}
    }
    companion object {
        @JvmField var PS_Y = -5.0
    }
}