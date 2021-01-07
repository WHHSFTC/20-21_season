package org.firstinspires.ftc.teamcode.auto

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.acmerobotics.roadrunner.trajectory.Trajectory
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.firstinspires.ftc.teamcode.cmd.*
import org.firstinspires.ftc.teamcode.dsl.*
import org.firstinspires.ftc.teamcode.module.*
import java.lang.Math.toRadians

@Autonomous
class WobbleBlue: DslOpMode(mode = Mode.AUTO) {
    init {
        runBlocking {
            dsl {
                val start = Pose2d(Vector2d(-63.0, 48.0), 0.0)
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
                            var traj: Trajectory =
                                    when (vis!!.pipeline.height) {
                                        VisionPipeline.Height.ZERO -> {
                                            dt.trajectoryBuilder(dt.poseEstimate)
                                                    .splineToConstantHeading(Vector2d(-39.0, 56.0), 0.0)
                                                    .splineToConstantHeading(Vector2d(-3.0, 57.0), 0.0)
                                                    .build()
                                        }
                                        VisionPipeline.Height.ONE -> {
                                            dt.trajectoryBuilder(dt.poseEstimate)
                                                    .splineToConstantHeading(Vector2d(-39.0, 56.0), 0.0)
                                                    .splineToConstantHeading(Vector2d(-3.0, 57.0), 0.0)
                                                    .splineToConstantHeading(Vector2d(21.0, 33.0), 0.0)
                                                    .build()
                                        }
                                        VisionPipeline.Height.FOUR -> {
                                            dt.trajectoryBuilder(dt.poseEstimate)
                                                    .splineToConstantHeading(Vector2d(-39.0, 56.0), 0.0)
                                                    .splineToConstantHeading(Vector2d(-3.0, 57.0), 0.0)
                                                    .splineToConstantHeading(Vector2d(45.0, 57.0), 0.0)
                                                    .build()
                                        }
                                    }
                            dt.followTrajectory(traj)
                            dt.waitForIdle()
                            wob.elbow(Wobble.ElbowState.DROP)
                            delay(500)
                            wob.claw(Wobble.ClawState.OPEN)
                            delay(500)
                            wob.elbow(Wobble.ElbowState.STORE)

                            traj = dt.trajectoryBuilder(traj.end(), true)
                                    .lineTo(Vector2d(-3.0, 28.0))
                                    .build()
                            dt.followTrajectory(traj)
                            aim.height(HeightController.Height.HIGH)
                            feed.height(Indexer.Height.HIGH)
                            dt.waitForIdle()
                            out(Shooter.State.FULL)
                            delay(1000)
                            feed.burst()
                            aim.height(HeightController.Height.ZERO)
                            wob.elbow(Wobble.ElbowState.INTAKE)
                            wob.claw(Wobble.ClawState.OPEN)
                            traj = dt.trajectoryBuilder(traj.end().plus(Pose2d(0.0, 0.0, Math.toRadians(180.0))))
                                    .splineToConstantHeading(Vector2d(-12.0, 24.0), Math.toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                    .splineToConstantHeading(Vector2d(-36.0, 24.0), Math.toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                    .splineToConstantHeading(Vector2d(-37.0, 26.0), Math.toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                    .splineToConstantHeading(Vector2d(-37.0, 31.0), Math.toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                    .build()
                            dt.followTrajectory(traj)
                            dt.waitForIdle()
                            wob.claw(Wobble.ClawState.CLOSED)
                            delay(500)
                            wob.elbow(Wobble.ElbowState.CARRY)

                            val pose: Pose2d =
                                    when (vis!!.pipeline.height) {
                                        VisionPipeline.Height.ZERO -> {
                                            Pose2d(12.0, 50.0, toRadians(90.0))
                                        }
                                        VisionPipeline.Height.ONE -> {
                                            Pose2d(36.0, 26.0, toRadians(90.0))
                                        }
                                        VisionPipeline.Height.FOUR -> {
                                            Pose2d(60.0, 50.0, toRadians(90.0))
                                        }
                                    }

                            traj = dt.trajectoryBuilder(traj.end())
                                    .splineToConstantHeading(Vector2d(-39.0, 26.0), 0.0)
                                    .splineToConstantHeading(Vector2d(-36.0, 24.0), 0.0)
                                    .splineToConstantHeading(Vector2d(-12.0, 24.0), 0.0)
                                    .splineToSplineHeading(pose, Math.toRadians(90.0))
                                    .build()

                            dt.followTrajectory(traj)
                            dt.waitForIdle()

                            wob.elbow(Wobble.ElbowState.DROP)
                            delay(500)
                            wob.claw(Wobble.ClawState.OPEN)
                            delay(500)
                            wob.elbow(Wobble.ElbowState.STORE)
                            wob.claw(Wobble.ClawState.CLOSED)

                            traj = dt.trajectoryBuilder(traj.end())
                                    .lineToLinearHeading(Pose2d(12.0, 26.0, 0.0))
                                    .build()
                            dt.followTrajectory(traj)

                            dt.waitForIdle()
                            wob.elbow(Wobble.ElbowState.DROP)
                            delay(1000)
                            wob.claw(Wobble.ClawState.OPEN)
                            delay(1000)
                            wob.elbow(Wobble.ElbowState.STORE)

                            traj = dt.trajectoryBuilder(traj.end(), true)
                                    .lineTo(Vector2d(0.0, 30.0))
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
