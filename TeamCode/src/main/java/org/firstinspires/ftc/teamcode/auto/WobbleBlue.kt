package org.firstinspires.ftc.teamcode.auto

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.acmerobotics.roadrunner.path.EmptyPathSegmentException
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.cmd.*
import org.firstinspires.ftc.teamcode.dsl.*
import org.firstinspires.ftc.teamcode.module.*

@Autonomous
class WobbleBlue: DslOpMode(mode = Mode.AUTO) {
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
                        var traj = dt.trajectoryBuilder(dt.poseEstimate)
                                        .splineToConstantHeading(Vector2d(-39.0, 56.0), 0.0)
                                        .splineToConstantHeading(Vector2d(-3.0, 57.0), 0.0)
                                        .build()
                        dt.followTrajectory(traj)
                        when (vis!!.pipeline.height) {
                            VisionPipeline.Height.ZERO -> {}
                            VisionPipeline.Height.ONE -> {
                                traj = dt.trajectoryBuilder(traj.end())
                                        .lineTo(Vector2d(21.0, 33.0))
                                        .build()
                                dt.followTrajectory(traj)
                            }
                            VisionPipeline.Height.FOUR -> {
                                traj = dt.trajectoryBuilder(traj.end())
                                        .lineTo(Vector2d(45.0, 57.0))
                                        .build()
                                dt.followTrajectory(traj)
                            }
                        }
                        dt.waitForIdle()
                        wob.elbow(Wobble.ElbowState.DROP)
                        sleep(1000)
                        wob.claw(Wobble.ClawState.OPEN)
                        sleep(1000)
                        wob.elbow(Wobble.ElbowState.STORE)

                        traj = dt.trajectoryBuilder(traj.end(), true)
                                .lineTo(Vector2d(0.0, 30.0))
                                .build()
                        dt.followTrajectory(traj)
                        aim.height(HeightController.Height.HIGH)
                        feed.height(Indexer.Height.HIGH)
                        dt.waitForIdle()
                        out(Shooter.State.FULL)
                        sleep(1000)
                        feed.burst()
                        sleep(150)
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
