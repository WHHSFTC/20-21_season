package org.firstinspires.ftc.teamcode.auto

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.acmerobotics.roadrunner.path.EmptyPathSegmentException
import com.acmerobotics.roadrunner.trajectory.MarkerCallback
import com.acmerobotics.roadrunner.trajectory.Trajectory
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.cmd.*
import org.firstinspires.ftc.teamcode.dsl.*
import org.firstinspires.ftc.teamcode.module.*
import java.lang.Math.toRadians
import kotlin.math.PI

@Autonomous
class WobbleStack: DslOpMode(mode = Mode.AUTO) {
    init {
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
                        wob.elbow(Wobble.ElbowState.DROP)
                        sleep(500)
                        wob.claw(Wobble.ClawState.OPEN)
                        sleep(500)
                        wob.elbow(Wobble.ElbowState.STORE)

                        traj = dt.trajectoryBuilder(traj.end(), true)
                                .lineTo(Vector2d(-3.0, 28.0))
                                .build()
                        dt.followTrajectory(traj)
                        aim.height(HeightController.Height.HIGH)
                        feed.height(Indexer.Height.HIGH)
                        out(Shooter.State.FULL)
                        sleep(1000)
                        feed.burst() // turns off shooter and lowers indexer
                        aim.height(HeightController.Height.ZERO)
                        //traj = dt.trajectoryBuilder(traj.end().plus(Pose2d(0.0, 0.0, Math.toRadians(180.0))))
                                //.splineToConstantHeading(Vector2d(-12.0, 24.0), Math.toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                //.splineToConstantHeading(Vector2d(-36.0, 24.0), Math.toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                //.splineToConstantHeading(Vector2d(-37.0, 26.0), Math.toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                //.splineToConstantHeading(Vector2d(-37.0, 31.0), Math.toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                //.build()
                        when (vis!!.height) {
                            VisionPipeline.Height.ZERO, VisionPipeline.Height.FOUR -> {
                                wob.elbow(Wobble.ElbowState.INTAKE)
                                wob.claw(Wobble.ClawState.OPEN)
                                traj = dt.trajectoryBuilder(traj.end())
                                        .splineToSplineHeading(Pose2d(-12.0, 24.0, Math.toRadians(180.0)), Math.toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                        .splineToConstantHeading(Vector2d(-36.0, 24.0), Math.toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                        .splineToConstantHeading(Vector2d(-37.0, 26.0), Math.toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                        .splineToConstantHeading(Vector2d(-37.0, 31.0), Math.toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                        .build()
                                dt.followTrajectory(traj)
                            }
                            VisionPipeline.Height.ONE -> {
                                wob.elbow(Wobble.ElbowState.DROP)
                                wob.claw(Wobble.ClawState.OPEN)
                                ink(Intake.Power.IN)
                                traj = dt.trajectoryBuilder(traj.end())
                                        .splineToSplineHeading(Pose2d(-12.0, 36.0, Math.toRadians(180.0)), Math.toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                        .splineToConstantHeading(Vector2d(-32.0, 36.0), Math.toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                        .splineToConstantHeading(Vector2d(-36.0, 24.0), Math.toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                        .addDisplacementMarker {
                                            wob.elbow(Wobble.ElbowState.INTAKE)
                                            ink(Intake.Power.OFF)
                                        }
                                        .splineToConstantHeading(Vector2d(-37.0, 26.0), Math.toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                        .splineToConstantHeading(Vector2d(-37.0, 31.0), Math.toRadians(180.0), constraintsOverride = DriveConstants.SLOW_CONSTRAINTS)
                                        .build()
                                dt.followTrajectory(traj)
                            }
                        }
                        wob.claw(Wobble.ClawState.CLOSED)
                        sleep(500)
                        wob.elbow(Wobble.ElbowState.CARRY)

                        //val pose: Pose2d =
                            //when (vis!!.pipeline.height) {
                                //VisionPipeline.Height.ZERO -> {
                                        //Pose2d(12.0, 50.0, toRadians(90.0))
                                //}
                                //VisionPipeline.Height.ONE -> {
                                        //Pose2d(36.0, 26.0, toRadians(90.0))
                                //}
                                //VisionPipeline.Height.FOUR -> {
                                        //Pose2d(60.0, 50.0, toRadians(90.0))
                                //}
                            //}

                        when (vis!!.height) {
                            VisionPipeline.Height.ZERO, VisionPipeline.Height.FOUR -> {
                                traj = dt.trajectoryBuilder(traj.end())
                                        .splineToConstantHeading(Vector2d(-39.0, 26.0), 0.0)
                                        .splineToConstantHeading(Vector2d(-36.0, 24.0), 0.0)
                                        .splineToConstantHeading(Vector2d(-12.0, 24.0), 0.0)
                                        .splineToSplineHeading(Pose2d(if (vis!!.height == VisionPipeline.Height.ZERO) 12.0 else 60.0, 50.0, toRadians(90.0)), Math.toRadians(90.0))
                                        .build()
                                dt.followTrajectory(traj)
                            }
                            VisionPipeline.Height.ONE -> {
                                traj = dt.trajectoryBuilder(traj.end())
                                        .lineTo(Vector2d(-3.0, 28.0))
                                        .build()
                                dt.followTrajectory(traj)
                                aim.height(HeightController.Height.HIGH)
                                feed.height(Indexer.Height.HIGH)
                                out(Shooter.State.FULL)
                                sleep(1000)
                                feed.burst() // turns off shooter and lowers indexer
                            }
                        }

                        wob.elbow(Wobble.ElbowState.DROP)
                        sleep(500)
                        wob.claw(Wobble.ClawState.OPEN)
                        sleep(500)
                        wob.elbow(Wobble.ElbowState.STORE)
                        wob.claw(Wobble.ClawState.CLOSED)

                        traj = dt.trajectoryBuilder(traj.end())
                                .lineToLinearHeading(Pose2d(12.0, 26.0, 0.0))
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
