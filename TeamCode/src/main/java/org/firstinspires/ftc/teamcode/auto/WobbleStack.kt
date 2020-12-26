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
import kotlin.math.PI

@Autonomous
class WobbleStack: DslOpMode(mode = Mode.AUTO) {
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
                            var traj: Trajectory =
                                    when (vis!!.pipeline.height) {
                                        VisionPipeline.Height.ZERO -> {
                                            dt.trajectoryBuilder(dt.poseEstimate)
                                                    .splineToConstantHeading(Vector2d(-24.0, 52.0), 0.0)
                                                    .splineToConstantHeading(Vector2d(-3.0, 57.0), 0.0)
                                                    .build()
                                        }
                                        VisionPipeline.Height.ONE -> {
                                            dt.trajectoryBuilder(dt.poseEstimate)
                                                    .splineToConstantHeading(Vector2d(-24.0, 52.0), 0.0)
                                                    //.splineToConstantHeading(Vector2d(-3.0, 57.0), 0.0)
                                                    .splineToConstantHeading(Vector2d(21.0, 33.0), 0.0)
                                                    .build()
                                        }
                                        VisionPipeline.Height.FOUR -> {
                                            dt.trajectoryBuilder(dt.poseEstimate)
                                                    .splineToConstantHeading(Vector2d(-24.0, 52.0), 0.0)
                                                    //.splineToConstantHeading(Vector2d(-3.0, 57.0), 0.0)
                                                    .splineToConstantHeading(Vector2d(45.0, 57.0), 0.0)
                                                    .build()
                                        }
                                    }
                            dt.followTrajectory(traj)
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
                            out(Shooter.State.FULL)
                            delay(1000)
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
                            delay(500)
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
                                VisionPipeline.Height.ZERO -> {
                                    traj = dt.trajectoryBuilder(traj.end())
                                            .splineToConstantHeading(Vector2d(-39.0, 26.0), 0.0)
                                            .splineToConstantHeading(Vector2d(-36.0, 24.0), 0.0)
                                            .splineToConstantHeading(Vector2d(-12.0, 24.0), 0.0)
                                            .addDisplacementMarker {
                                                wob.elbow(Wobble.ElbowState.DROP)
                                            }
                                            .splineToSplineHeading(Pose2d(12.0, 50.0, toRadians(90.0)), Math.toRadians(90.0))
                                            .build()
                                    dt.followTrajectory(traj)
                                }
                                VisionPipeline.Height.FOUR -> {
                                    traj = dt.trajectoryBuilder(traj.end())
                                            .splineToConstantHeading(Vector2d(-39.0, 26.0), 0.0)
                                            .splineToConstantHeading(Vector2d(-36.0, 24.0), 0.0)
                                            .splineToConstantHeading(Vector2d(-12.0, 24.0), 0.0)
                                            .addDisplacementMarker {
                                                wob.elbow(Wobble.ElbowState.DROP)
                                            }
                                            .splineToSplineHeading(Pose2d(42.0, 50.0, toRadians(45.0)), Math.toRadians(45.0))
                                            .build()
                                    dt.followTrajectory(traj)
                                }
                                VisionPipeline.Height.ONE -> {
                                    aim.height(HeightController.Height.HIGH)
                                    feed.height(Indexer.Height.HIGH)
                                    out(Shooter.State.FULL)
                                    traj = dt.trajectoryBuilder(traj.end())
                                            .lineToSplineHeading(Pose2d(-3.0, 28.0, 0.0))
                                            .build()
                                    dt.followTrajectory(traj)
                                    feed.shoot()

                                    traj = dt.trajectoryBuilder(traj.end())
                                            .lineTo(Vector2d(21.0, 28.0))
                                            .build()
                                    delay(150)
                                    wob.elbow(Wobble.ElbowState.DROP)
                                    dt.followTrajectory(traj)
                                    out(Shooter.State.OFF)
                                    aim.height(HeightController.Height.ZERO)
                                    feed.height(Indexer.Height.IN)
                                }
                            }

                            wob.claw(Wobble.ClawState.OPEN)
                            delay(500)
                            wob.elbow(Wobble.ElbowState.STORE)

                            traj = dt.trajectoryBuilder(traj.end())
                                    .lineToSplineHeading(Pose2d(12.0, 26.0, 0.0))
                                    .build()
                            dt.followTrajectory(traj)
                            wob.claw(Wobble.ClawState.CLOSED)
                        }
                    }
                }

                onStop {
                    cmd {
                        dt.powers = CustomMecanumDrive.Powers()
                        aim.height(HeightController.Height.ZERO) // todo uhh
                        if (OpMode.DEBUG)
                            dt.followTrajectory(dt.trajectoryBuilder(dt.poseEstimate).lineToSplineHeading(start).build())
                    }
                }
            }
        }
    }
}
