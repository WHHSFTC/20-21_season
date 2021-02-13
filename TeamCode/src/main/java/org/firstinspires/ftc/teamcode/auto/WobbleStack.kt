package org.firstinspires.ftc.teamcode.auto

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import kotlinx.coroutines.runBlocking
import org.firstinspires.ftc.teamcode.cmd.*
import org.firstinspires.ftc.teamcode.dsl.*
import org.firstinspires.ftc.teamcode.module.*
import org.firstinspires.ftc.teamcode.module.vision.StackPipeline
import java.lang.Math.toRadians

@Autonomous(name = "_WobbleStack")
class WobbleStack: DslOpMode(mode = Mode.AUTO) {
    init {
        runBlocking {
            dsl {
                val start: Pose2d = Pose2d(Vector2d(-63.0, 48.0), 0.0)
                onInit {
                    seq {
                        +autoInit
                        +cmd {
                            wob.elbow(Wobble.ElbowState.STORE)
                            wob.claw(Wobble.ClawState.CLOSED)
                            log.logData("Init")
                            log.logData("...")
                            log.logData("Done")
                            dt.poseEstimate = start
                        }
                    }
                }

                onRun {
                    seq {
                        +cmd {
                            vis!!.halt()
                        }
                        +switch({ vis!!.pipeline.height }, listOf(
                                case({ StackPipeline.Height.ZERO }, go(start) {
                                    splineToConstantHeading(Vector2d(-24.0, 52.0), 0.0)
                                    addDisplacementMarker {bot.wob.elbow(Wobble.ElbowState.DROP)}
                                    splineToConstantHeading(Vector2d(1.0, 57.0), 0.0)
                                }),
                                case({ StackPipeline.Height.ONE }, go(start) {
                                    splineToConstantHeading(Vector2d(-24.0, 52.0), 0.0)
                                    addDisplacementMarker {bot.wob.elbow(Wobble.ElbowState.DROP)}
                                    splineToConstantHeading(Vector2d(27.0, 32.0), 0.0)
                                }),
                                case({ StackPipeline.Height.FOUR }, go(start) {
                                    splineToConstantHeading(Vector2d(-24.0, 52.0), 0.0)
                                    addDisplacementMarker {bot.wob.elbow(Wobble.ElbowState.DROP)}
                                    splineToConstantHeading(Vector2d(52.0, 57.0), 0.0)
                                })
                        ))

                        +setState(bot.wob.claw) { Wobble.ClawState.OPEN }
                        +delayC(500)
                        +setState(bot.wob.elbow) { Wobble.ElbowState.STORE }

                        +switch({ vis!!.pipeline.height }, listOf(
                                case({ StackPipeline.Height.ZERO }, go(Pose2d(1.0, 57.0), true) {
                                    lineTo(Vector2d(-3.0, 24.0))
                                }),
                                case({ StackPipeline.Height.ONE }, go(Pose2d(27.0, 32.0), true) {
                                    lineTo(Vector2d(-3.0, 24.0))
                                }),
                                case({ StackPipeline.Height.FOUR }, go(Pose2d(52.0, 57.0), true) {
                                    lineTo(Vector2d(-3.0, 24.0))
                                })
                        ))

                        +setState(bot.aim.height) { HeightController.Height.HIGH }
                        +setState(bot.feed.height) { Indexer.Height.HIGH }
                        +setState(bot.out) { Shooter.State.FULL }

                        +delayC(1000)

                        +cmd { feed.burst() }

                        +setState(bot.aim.height) { HeightController.Height.ZERO }

                        //traj = dt.trajectoryBuilder(traj.end().plus(Pose2d(0.0, 0.0, Math.toRadians(180.0))))
                        //.splineToConstantHeading(Vector2d(-12.0, 24.0), Math.toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                        //.splineToConstantHeading(Vector2d(-36.0, 24.0), Math.toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                        //.splineToConstantHeading(Vector2d(-37.0, 26.0), Math.toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                        //.splineToConstantHeading(Vector2d(-37.0, 31.0), Math.toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                        //.build()

                        val zero = CommandContext.seq {
                            +setState(bot.wob.elbow) { Wobble.ElbowState.INTAKE }
                            +setState(bot.wob.claw) { Wobble.ClawState.OPEN }
                            +go(Pose2d(-3.0, 28.0)) {
                                splineToSplineHeading(Pose2d(-12.0, 24.0, toRadians(180.0)), toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                splineToConstantHeading(Vector2d(-34.0, 29.0), toRadians(90.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                            }
                        }

                        val onefour = CommandContext.seq{
                            +setState(bot.wob.elbow) { Wobble.ElbowState.CARRY }
                            +setState(bot.wob.claw) { Wobble.ClawState.OPEN }
                            +setState(bot.ink) { Intake.Power.IN }
                            +go(Pose2d(-3.0280,.0)) {
                            //+go(Pose2d(-3.0, 28.0)) {
                                splineToSplineHeading(Pose2d(-12.0, 33.0, toRadians(180.0)), toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                splineToConstantHeading(Vector2d(-20.0, 33.0), toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                //splineToConstantHeading(Vector2d(-24.0, 22.0), toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                addDisplacementMarker {
                                    bot.ink(Intake.Power.OUT)
                                }
                                //splineToConstantHeading(Vector2d(-34.0, 32.0), toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                splineToConstantHeading(Vector2d(-26.0, 21.0), toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                addDisplacementMarker {
                                    bot.wob.elbow(Wobble.ElbowState.INTAKE)
                                    bot.feed.height(Indexer.Height.HIGH)
                                }
                                //splineToConstantHeading(Vector2d(-34.0, 25.0), toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                splineToConstantHeading(Vector2d(-34.0, 29.0), toRadians(90.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                            }
                            +setState(bot.ink) { Intake.Power.OFF }
                        }

                        +switch({ vis!!.pipeline.height }, listOf(
                                case({ StackPipeline.Height.ZERO }, zero),
                                case({ StackPipeline.Height.FOUR }, onefour),
                                case({ StackPipeline.Height.ONE }, onefour)
                        ))

                        +setState(bot.wob.claw) { Wobble.ClawState.CLOSED }
                        +delayC(1000)
                        +setState(bot.wob.elbow) { Wobble.ElbowState.CARRY }


                        val lastpose = Pose2d(-37.0, 29.0, toRadians(180.0))

                        +switch({ vis!!.pipeline.height }, listOf(
                                case({ StackPipeline.Height.ZERO }, go(lastpose) {
                                    splineToConstantHeading(Vector2d(-39.0, 26.0), 0.0)
                                    splineToConstantHeading(Vector2d(-36.0, 24.0), 0.0)
                                    splineToConstantHeading(Vector2d(-12.0, 24.0), 0.0)
                                    addDisplacementMarker {
                                        bot.wob.elbow(Wobble.ElbowState.DROP)
                                    }
                                    splineToSplineHeading(Pose2d(16.0, 47.0, toRadians(90.0)), toRadians(90.0))
                                }),
                                case({ StackPipeline.Height.FOUR }, CommandContext.seq {
                                    +setState(bot.aim.height) { HeightController.Height.HIGH }
                                    +setState(bot.feed.height) { Indexer.Height.HIGH }
                                    +setState(bot.out) { Shooter.State.FULL }
                                    val nextpose = Pose2d(-3.0, 28.0, 0.0)
                                    +go(lastpose) {
                                        lineToSplineHeading(nextpose)
                                    }
                                    +cmd { feed.burst() }
                                    +setState(bot.wob.elbow) { Wobble.ElbowState.DROP }
                                    +go(nextpose) {
                                        lineToSplineHeading(Pose2d(52.0, 50.0, toRadians(45.0)))
                                    }

                                    +setState(bot.aim.height) { HeightController.Height.ZERO }
                                    +setState(bot.feed.height) { Indexer.Height.IN }
                                }),
                                case({ StackPipeline.Height.ONE }, CommandContext.seq {
                                    +setState(bot.aim.height) { HeightController.Height.HIGH }
                                    +setState(bot.feed.height) { Indexer.Height.HIGH }
                                    +setState(bot.out) { Shooter.State.FULL }
                                    val nextpose = Pose2d(-3.0, 28.0, 0.0)
                                    +go(lastpose) {
                                        lineToSplineHeading(nextpose)
                                    }
                                    +cmd { feed.shoot() }
                                    +delayC(150)
                                    +setState(bot.wob.elbow) { Wobble.ElbowState.DROP }
                                    +setState(bot.out) { Shooter.State.OFF }
                                    +go(nextpose) {
                                        lineTo(Vector2d(19.0, 36.0))
                                    }
                                    +setState(bot.aim.height) { HeightController.Height.ZERO }
                                    +setState(bot.feed.height) { Indexer.Height.IN }
                                })
                        ))

                        +setState(bot.wob.claw) { Wobble.ClawState.OPEN }
                        +delayC(500)
                        +setState(bot.wob.elbow) { Wobble.ElbowState.STORE }

                        +switch({ bot.vis!!.pipeline.height }, listOf(
                                case({ StackPipeline.Height.ZERO }, go(Pose2d(16.0, 47.0, toRadians(90.0))) {
                                    lineToSplineHeading(Pose2d(12.0, 26.0, 0.0))
                                }),
                                case({ StackPipeline.Height.FOUR }, go(Pose2d(52.0, 50.0, toRadians(45.0))) {
                                    lineToSplineHeading(Pose2d(12.0, 26.0, 0.0))
                                }),
                                case({ StackPipeline.Height.ONE }, go(Pose2d(19.0, 36.0, 0.0)) {
                                    lineToSplineHeading(Pose2d(12.0, 26.0, 0.0))
                                })
                        ))

                        +setState(bot.wob.claw) { Wobble.ClawState.CLOSED }
                    }
                }

                onStop {
                    cmd {
                        dt.powers = CustomMecanumDrive.Powers()
                        aim.height(HeightController.Height.ZERO) // todo uhh
                        if (DEBUG)
                            dt.followTrajectory(dt.trajectoryBuilder(dt.poseEstimate).lineToSplineHeading(start).build())
                    }
                }
            }
        }
    }
}
