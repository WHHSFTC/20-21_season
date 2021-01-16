package org.firstinspires.ftc.teamcode.auto

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.acmerobotics.roadrunner.path.EmptyPathSegmentException
import com.acmerobotics.roadrunner.trajectory.constraints.MecanumConstraints
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.firstinspires.ftc.teamcode.cmd.*
import org.firstinspires.ftc.teamcode.dsl.*
import org.firstinspires.ftc.teamcode.module.*


@Autonomous
class IntakeStack: DslOpMode(mode = Mode.AUTO) {
    init {
        runBlocking {
            dsl {
                val start: Pose2d = Pose2d(Vector2d(-63.0, 48.0), 0.0)
                onInit {
                    cmd {
                        wob.elbow(Wobble.ElbowState.STORE)
                        wob.claw(Wobble.ClawState.CLOSED)
                        log.logData("Init")
                        log.logData("...")
                        log.logData("Done")
                        dt.poseEstimate = start
                    }
                }

                onRun {
                    seq {
                        +cmd {
                            vis!!.halt()
                            var traj = dt.trajectoryBuilder(start)
                                    .lineTo(Vector2d(-36.0, 36.0))
                                    .build()
                            dt.followTrajectory(traj)
                            dt.waitForIdle()
                            when (vis!!.pipeline.height) {
                                VisionPipeline.Height.ZERO -> {
                                }
                                VisionPipeline.Height.ONE -> {
                                    traj = dt.trajectoryBuilder(traj.end())
                                            .lineTo(Vector2d(-1.0, 36.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                            .build()
                                    ink(Intake.Power.IN)
                                    dt.followTrajectory(traj)
                                    dt.waitForIdle()
                                    ink(Intake.Power.OFF)
                                    feed.shake()
                                }
                                VisionPipeline.Height.FOUR -> {
                                    traj = dt.trajectoryBuilder(traj.end())
                                            .lineTo(Vector2d(-25.0, 36.0))
                                            .build()
                                    dt.followTrajectory(traj)
                                    dt.waitForIdle()
//
                                    val constr = DriveConstants.BASE_CONSTRAINTS.copy().apply { maxVel /= 3.0 }
//
                                    traj = dt.trajectoryBuilder(traj.end())
                                            .lineTo(Vector2d(-24.0, 36.0), constraintsOverride = constr)
                                            .build()
                                    ink(Intake.Power.IN)
                                    dt.followTrajectory(traj)
                                    dt.waitForIdle()
                                    delay(1000)
                                    ink(Intake.Power.OFF)
                                    delay(1000)
                                    feed.shake()
                                    delay(2000)
//
                                    traj = dt.trajectoryBuilder(traj.end())
                                            .lineTo(Vector2d(-22.0, 36.0), constraintsOverride = constr)
                                            .build()
                                    ink(Intake.Power.IN)
                                    dt.followTrajectory(traj)
                                    dt.waitForIdle()
                                    delay(1000)
                                    ink(Intake.Power.OFF)
                                    delay(1000)
                                    feed.shake()
                                    delay(2000)
//
                                    traj = dt.trajectoryBuilder(traj.end())
                                          .lineTo(Vector2d(-20.0, 36.0), constraintsOverride = constr)
                                          .build()
                                    ink(Intake.Power.IN)
                                    dt.followTrajectory(traj)
                                    dt.waitForIdle()
                                    delay(1000)
                                    ink(Intake.Power.OFF)
                                    delay(1000)
                                    feed.shake()
                                    delay(2000)
//
                                    traj = dt.trajectoryBuilder(traj.end())
                                          .lineTo(Vector2d(-20.0, 20.0))
                                          .build()
                                    dt.followTrajectory(traj)
                                    dt.waitForIdle()
                                }
                            }

                            traj = dt.trajectoryBuilder(traj.end())
                                    .lineTo(Vector2d(-1.0, 28.0))
                                    .build()
                            dt.followTrajectory(traj)
                            aim.height(HeightController.Height.HIGH)
                            feed.height(Indexer.Height.HIGH)
                            dt.waitForIdle()
                            out(Shooter.State.FULL)
                            delay(1000)
                            feed.burst()
                            delay(150)
                            out(Shooter.State.OFF)
                            feed.height(Indexer.Height.IN)
                            aim.height(HeightController.Height.ZERO)
                            traj = dt.trajectoryBuilder(traj.end())
                                    .lineTo(Vector2d(9.0, 30.0))
                                    .build()
                            dt.followTrajectory(traj)
                        }
                    }
                }

                onStop {
                    cmd {
                        dt.powers = CustomMecanumDrive.Powers()
                    }
                }
            }
        }
    }
}
