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

@Config
@Autonomous(name = "Blue - Power Shots")
class BluePower: DslOpMode(mode = Mode.AUTO) {
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
                                +setState(bot.out) { Shooter.State.POWER }
                                +setState(bot.aim.height) { HeightController.Height.POWER }
                                +setState(bot.feed.height) { Indexer.Height.POWER }
                                +go(start) {
                                    splineTo(psPose.vec(), 0.0)
                                }
                                +powerShots
                                +setState(bot.out) { Shooter.State.OFF }
                                +setState(bot.aim.height) { HeightController.Height.HIGH }
                                +setState(bot.feed.height) { Indexer.Height.IN }
                                +setState(bot.vis!!) { bot.vis!!.ring }
                                //+cmd { vis!!.start() }
                                +delayC(1000)
                                +cmd {
                                    val rings = vis!!.ring.absolutes.filter {it.y > -24.0 + 5.0}.sortedBy { it.x }
                                    vis!!.halt()
                                    if (rings.isNotEmpty()) {
                                        val builder = dt.trajectoryBuilder(psEndPose)
                                        var last = psEndPose.vec()
                                        for (r in rings.subList(0, min(rings.size, 2))) {
                                            if (r.x - last.x > 14.0) {
                                                builder.splineToConstantHeading(r - Vector2d(6.0, 0.0), 0.0)
                                            }
                                            builder.splineToConstantHeading(r.copy(x = min(r.x, 68.0 - Robot.LENGTH / 2.0)), 0.0, constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                            last = r
                                            //builder.splineTo(psEndPose.vec(), PI)
                                        }
                                        //builder.splineTo(psEndPose.vec(), PI)
                                        builder.addDisplacementMarker {
                                            out(Shooter.State.FULL)
                                            aim.height(HeightController.Height.HIGH)
                                        }
                                        builder.splineToSplineHeading(linePose, PI)
                                        builder.addDisplacementMarker {
                                            ink(Intake.Power.OFF)
                                            feed.height(Indexer.Height.HIGH)
                                        }
                                        feed.height(Indexer.Height.IN)
                                        ink(Intake.Power.IN)
                                        dt.followTrajectory(builder.build())
                                        slowBurst(bot)
                                    } else {
                                        this@seq.go(psEndPose) {
                                            strafeTo(linePose.vec())
                                        }(bot)
                                    }
                                }

                                // drop wobble 1 at A
                                +setState(bot.wob.elbow) { Wobble.ElbowState.DROP }
                                +go(linePose) {
                                    splineTo(Vector2d(28.0, 50.0), PI/2.0)
                                    //splineTo(Vector2d(16.0, 48.0), PI/2.0)
                                    splineToConstantHeading(Vector2d(16.0, 55.0), PI/2.0, constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                    addDisplacementMarker {
                                        bot.wob.claw(Wobble.ClawState.OPEN)
                                        bot.wob.elbow(Wobble.ElbowState.STORE)
                                    }
                                    splineToConstantHeading(Vector2d(16.0, 48.0), -PI/2.0, constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                //}
                                //+dropWob

                                // intake wobble 2
                                //+go(Pose2d(18.0, 55.0, PI/2.0), startTangent = -PI/2.0) {
                                    splineToConstantHeading(Vector2d(-40.0, 36.0), Math.toRadians(180.0))
                                    addDisplacementMarker {
                                        bot.wob.elbow(Wobble.ElbowState.INTAKE)
                                    }
                                    splineToConstantHeading(Vector2d(-40.0, 50.0), Math.toRadians(0.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                    splineToConstantHeading(Vector2d(-36.0, 50.0), Math.toRadians(0.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                    addDisplacementMarker {
                                        bot.wob.claw(Wobble.ClawState.CLOSED)
                                    }
                                    //splineToConstantHeading(Vector2d(-36.0, 44.0), 0.0)
                                    //addDisplacementMarker {
                                        //bot.wob.elbow(Wobble.ElbowState.DROP)
                                    //}
                                    splineToConstantHeading(Vector2d(-24.0, 40.0), 0.0, constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                    splineToConstantHeading(Vector2d(4.0, 47.0), 0.0)
                                    splineToConstantHeading(Vector2d(18.0, 47.0), 0.0, constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                    addDisplacementMarker {
                                        bot.wob.claw(Wobble.ClawState.OPEN)
                                        bot.wob.elbow(Wobble.ElbowState.STORE)
                                    }
                                    splineToConstantHeading(Vector2d(12.0, 24.0), -PI/2.0, constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                }
                            }),
                            case({ StackPipeline.Height.ONE }, CommandContext.seq {
                                // shoot from line
                                +prepFull
                                +lineSet
                                +go(start) { lineToConstantHeading(linePose.vec() ) }
                                +slowBurst

                                // drop wobble at B
                                +setState(bot.wob.elbow) { Wobble.ElbowState.DROP }
                                +go(linePose) { lineToConstantHeading(Vector2d(26.0, 32.0)) }
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

                                +singleShot
                                +setState(bot.wob.elbow) {Wobble.ElbowState.STORE}

                                +go(linePose - Pose2d(8.0)) {
                                    lineToConstantHeading(Vector2d(12.0, 24.0))
                                }
                            }),
                            case({ StackPipeline.Height.FOUR }, CommandContext.seq {
                                //val firstShot = Pose2d(-36.0, 21.75, 0.0)
                                val firstShot = start

                                +prepFull
                                +stackSet

                                //+go(start) {
                                    //lineToConstantHeading(firstShot.vec())
                                //}

                                +await(100) { timer.milliseconds() > 1500 }
                                //+singleShot
                                +autoBurst(750)
                                //+setState(bot.wob.elbow) { Wobble.ElbowState.RING }

                                //var pose = Pose2d(-40.0, 36.0, 0.0)
                                var pose = Pose2d(-36.0, 36.0, 0.0)


                                +setState(bot.feed.height) { Indexer.Height.IN }
                                +setState(bot.aim.height) { HeightController.Height.HIGH }
                                +setState(bot.ink) { Intake.Power.IN }
                                +go(firstShot) {
                                    // intake
                                    splineToConstantHeading(pose.vec(), 0.0)
                                    // exit to left
                                    splineToConstantHeading(pose.vec() + Vector2d(9.0, 0.0), 0.0, constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                    splineToConstantHeading(pose.vec() + Vector2d(4.0, 12.0), -PI/2.0)
                                    splineToConstantHeading(pose.vec() + Vector2d(24.0, 24.0), 0.0)
                                    addDisplacementMarker {
                                        bot.ink(Intake.Power.OUT)
                                        bot.wob.elbow(Wobble.ElbowState.DROP)
                                    }
                                    splineToConstantHeading(Vector2d(40.0, 52.0), 0.0)
                                    addDisplacementMarker {
                                        bot.wob.claw(Wobble.ClawState.OPEN)
                                        bot.feed.height(Indexer.Height.HIGH)
                                        bot.out(Shooter.State.FULL)
                                        bot.ink(Intake.Power.OFF)
                                    }
                                    splineToConstantHeading(linePose.vec(), -PI/2.0)
                                }

                                +lineBurst

                                +go(linePose) {
                                    splineTo(Vector2d(-24.0, 52.0), PI)
                                    addDisplacementMarker {
                                        bot.wob.elbow(Wobble.ElbowState.INTAKE)
                                    }
                                    // get wob
                                    // get wob
                                    splineToConstantHeading(Vector2d(-24.0, 50.0), -PI / 2.0, constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                    //splineToConstantHeading(Vector2d(-37.0, 50.0), PI / 2.0, constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                    splineToConstantHeading(Vector2d(-37.0, 53.0), PI / 2.0, constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                    addDisplacementMarker {
                                        bot.wob.claw(Wobble.ClawState.CLOSED)
                                    }
                                }
                                +go(Pose2d(-37.0, 55.0, PI)) {
                                    addDisplacementMarker {
                                        bot.ink(Intake.Power.OUT)
                                    }
                                    splineTo(Vector2d(-54.0, 48.0), -PI/2.0)
                                    addDisplacementMarker {
                                        bot.wob.elbow(Wobble.ElbowState.CARRY)
                                        bot.ink(Intake.Power.IN)
                                        bot.out(Shooter.State.FULL)
                                    }
                                    splineTo(Vector2d(-36.0, 32.0), 0.0)
                                    // intake rings:
                                    splineToConstantHeading(Vector2d(-12.0, 32.0), 0.0, constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                    splineToConstantHeading(linePose.vec() + Vector2d(0.0, -8.0), -PI/2.0)
                                }

                                +setState(bot.ink) {Intake.Power.OFF}
                                +setState(bot.feed.height) { Indexer.Height.HIGH }
                                +delayC(200)

                                +lineBurst

                                +setState(bot.wob.elbow) { Wobble.ElbowState.DROP }
                                +go(linePose + Pose2d(0.0, -8.0)) {
                                    splineTo(Vector2d(54.0, 44.0), PI/4.0)
                                    addDisplacementMarker {
                                        bot.wob.claw(Wobble.ClawState.OPEN)
                                    }
                                    splineToConstantHeading(Vector2d(12.0, 44.0), -PI/2.0)
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
    companion object {
        @JvmField var PS_Y = -5.0
    }
}