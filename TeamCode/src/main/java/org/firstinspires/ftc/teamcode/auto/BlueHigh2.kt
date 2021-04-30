package org.firstinspires.ftc.teamcode.auto

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import kotlinx.coroutines.runBlocking
import org.firstinspires.ftc.teamcode.cmd.*
import org.firstinspires.ftc.teamcode.dsl.*
import org.firstinspires.ftc.teamcode.module.*
import org.firstinspires.ftc.teamcode.module.vision.StackPipeline
import kotlin.math.*

@Config
@Autonomous(name = "Blue - High Goal 2")
class BlueHigh2: DslOpMode(mode = Mode.AUTO) {
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

            val singleShotAndPark = CommandContext.seq {
                +cmd {feed.shoot()}
                +delayC(750)
                +setState(bot.feed.height) {Indexer.Height.IN}
                +delayC(150)
                +setState(bot.wob.elbow) {Wobble.ElbowState.INTAKE}
                +delayC(350)
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
                //+setState(bot.wob.elbow) { Wobble.ElbowState.STORE }
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
                    +cmd { vis!!.halt() }
                    +switch({ vis!!.stack.height }, listOf(
                            case({ StackPipeline.Height.ZERO }, CommandContext.seq {
                                // shoot from line
                                +prepFull
                                +lineSet
                                +go(start) { splineTo(linePose.vec() + Vector2d(4.0), 0.0) }
                                +slowBurst
                                +setState(bot.ink) { Intake.Power.IN }

                                // drop wobble at A
                                +setState(bot.wob.elbow) { Wobble.ElbowState.DROP }
                                +go(Pose2d(linePose.vec() + Vector2d(4.0), 0.0)) { splineTo(Vector2d(18.0, 54.0), PI/2.0) }
                                +dropWob

                                +go(Pose2d(18.0, 4.0, PI/2.0)) { lineToConstantHeading(Vector2d(18.0, 24.0)) }

                                //+cmd {dt.turn(PI/2.0)}

                                // intake wobble
                                +go(Pose2d(18.0, 24.0, PI)) {
                                    addDisplacementMarker {
                                        bot.wob.elbow(Wobble.ElbowState.INTAKE)
                                        bot.wob.claw(Wobble.ClawState.WIDE)
                                    }
                                    splineToConstantHeading(Vector2d(-24.0, 52.0), Math.toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                    splineToConstantHeading(Vector2d(-37.0, 54.0), Math.toRadians(90.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                }
                                +takeWob

                                // drop wobble 2 at A
                                +go(Pose2d(-37.0, 54.0, PI)) { lineToLinearHeading(Pose2d(12.0, 48.0, PI/2.0)) }
                                +dropWob

                                //+setState(bot.wob.elbow) { Wobble.ElbowState.DROP }
                                +setState(bot.wob.claw) {Wobble.ClawState.WIDE}
                                +setState(bot.wob.elbow) {Wobble.ElbowState.STORE}

                                +go(Pose2d(12.0, 48.0, PI/2.0)) {
                                    lineToConstantHeading(Vector2d(12.0, 24.0))
                                }
                            }),
                            case({ StackPipeline.Height.ONE }, CommandContext.seq {
                                // shoot from line
                                +prepFull
                                +lineSet
                                +go(start) { splineTo(linePose.vec()+Vector2d(0.0, 4.0), 0.0) }
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
                                        bot.wob.claw(Wobble.ClawState.WIDE)
                                    }
                                    splineToConstantHeading(Vector2d(-22.0, 53.0), Math.toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                    splineToConstantHeading(Vector2d(-36.0, 53.0), Math.toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                }
                                +setState(bot.ink) { Intake.Power.OFF }
                                +setState(bot.feed.height) { Indexer.Height.HIGH }
                                +takeWob
                                +delayC(500)

                                // drop wobble 2 at B
                                +go(Pose2d(-34.0, 53.0, 3.0 * PI / 2.0)) { lineToLinearHeading(Pose2d(18.0, 28.0, 0.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS) }
                                +dropWob
                                //+setState(bot.wob.elbow) {Wobble.ElbowState.DROP}

                                +prepFull
                                +lineSet
                                +go(Pose2d(18.0, 32.0, 0.0)) { lineToLinearHeading(linePose - Pose2d(4.0, 2.0)) }

                                +setState(bot.wob.elbow) {Wobble.ElbowState.STORE}
                                +delayC(500)
                                +singleShotAndPark

                                //+go(linePose - Pose2d(8.0)) {
                                    //lineToConstantHeading(Vector2d(12.0, 24.0))
                                    //forward(18.0)
                                //}
                            }),
                            case({ StackPipeline.Height.FOUR }, CommandContext.seq {
                                //val firstShot = Pose2d(-36.0, 21.75, 0.0)
                                val firstShot = start

                                +prepFull
                                +stackSet

                                //+go(start) {
                                    //lineToConstantHeading(firstShot.vec())
                                //}

                                +await(100) { timer.milliseconds() > 2000 }
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
                                    splineToConstantHeading(pose.vec() + Vector2d(12.0, 0.0), 0.0, constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                    addDisplacementMarker {
                                        bot.ink(Intake.Power.OUT)
                                    }
                                    splineToConstantHeading(pose.vec() + Vector2d(2.0, 12.0), PI/2.0)
                                    splineToConstantHeading(pose.vec() + Vector2d(24.0, 22.0), 0.0)
                                    addDisplacementMarker {
                                        bot.ink(Intake.Power.IN)
                                        //bot.ink(Intake.Power.OFF)
                                        //bot.ink(Intake.Power.IN)
                                        bot.wob.elbow(Wobble.ElbowState.DROP)
                                    }
                                    splineToConstantHeading(Vector2d(40.0, 48.0), 0.0)
                                    addDisplacementMarker {
                                        bot.wob.claw(Wobble.ClawState.OPEN)
                                        bot.feed.height(Indexer.Height.HIGH)
                                        bot.out(Shooter.State.FULL)
                                    }
                                    splineToConstantHeading(linePose.vec() , PI)
                                }

                                +setState(bot.wob.elbow) { Wobble.ElbowState.STORE }
                                +setState(bot.ink) { Intake.Power.OFF }
                                +lineBurst

                                +go(linePose) {
                                    addDisplacementMarker {
                                        bot.ink(Intake.Power.IN)
                                    }
                                    splineTo(Vector2d(-24.0, 45.0), PI)
                                    addDisplacementMarker {
                                        bot.wob.elbow(Wobble.ElbowState.INTAKE)
                                        bot.wob.claw(Wobble.ClawState.WIDE)
                                    }
                                    // get wob
                                    // get wob
                                    //splineToConstantHeading(Vector2d(-24.0, 52.0), -PI / 2.0, constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                    //splineToConstantHeading(Vector2d(-37.0, 50.0), PI / 2.0, constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                    splineToConstantHeading(Vector2d(-35.0, 48.0), PI / 2.0, constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                    //splineToConstantHeading(Vector2d(-35.0, 54.0), PI / 2.0, constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                    addDisplacementMarker {
                                        bot.wob.claw(Wobble.ClawState.CLOSED)
                                    }
                                }

                                +delayC(750)

                                +setState(bot.wob.elbow) { Wobble.ElbowState.CARRY }
                                +setState(bot.ink) { Intake.Power.OUT }

                                +go(Pose2d(-35.0,48.0, PI)) {
                                    splineTo(Vector2d(-54.0, 40.0), -PI/2.0)
                                    addDisplacementMarker {
                                        bot.ink(Intake.Power.IN)
                                        bot.out(Shooter.State.FULL)
                                    }
                                    splineTo(Vector2d(-36.0, 30.0), 0.0)
                                    // intake rings:
                                    splineToConstantHeading(Vector2d(-12.0,30.0), 0.0, constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                    splineToConstantHeading(linePose.vec() + Vector2d(0.0, -10.0), -PI/2.0)
                                }

                                +setState(bot.wob.elbow) { Wobble.ElbowState.STORE }
                                +setState(bot.ink) {Intake.Power.OFF}
                                +setState(bot.feed.height) { Indexer.Height.HIGH }
                                +delayC(200)

                                +lineBurst

                                +setState(bot.park) { Parker.Power.OUT }

                                +setState(bot.wob.elbow) { Wobble.ElbowState.DROP }
                                +setState(bot.ink) { Intake.Power.IN }
                                +go(linePose + Pose2d(0.0, -10.0)) {
                                    splineTo(Vector2d(45.0, 36.0), PI/4.0)
                                    addDisplacementMarker {
                                        bot.wob.claw(Wobble.ClawState.WIDE)
                                        bot.wob.elbow(Wobble.ElbowState.STORE)
                                    }
                                    splineToConstantHeading(Vector2d(12.0, 36.0), -PI/2.0)
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