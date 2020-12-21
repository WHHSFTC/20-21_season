package org.firstinspires.ftc.teamcode.auto

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import com.acmerobotics.roadrunner.path.EmptyPathSegmentException
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import kotlinx.coroutines.runBlocking
import org.firstinspires.ftc.teamcode.cmd.*
import org.firstinspires.ftc.teamcode.dsl.*
import org.firstinspires.ftc.teamcode.module.CustomMecanumDrive
import org.firstinspires.ftc.teamcode.module.VisionPipeline

@Autonomous
class VisionMove: DslOpMode(mode = Mode.AUTO) {
    init {
        runBlocking {
            dsl {
                val start: Pose2d = Pose2d(Vector2d(-63.0, 33.0), 0.0)
                onInit {
                    cmd {
                        log.logData("Init")
                        log.logData("...")
                        log.logData("Done")
                        //dt.poseEstimate = Pose2d(Vector2d(), Math.toRadians(90.0))
                        //dt.poseEstimate = Pose2d(Vector2d(-33.0, -63.0), Math.toRadians(90.0))
                        dt.poseEstimate = start
                    }
                }

                onRun {
                    seq {
                        +cmd {
                            vis!!.halt()
                            var traj = dt.trajectoryBuilder(dt.poseEstimate)
                                    //.splineToConstantHeading(Vector2d(-9.0, 24.0), Math.toRadians(180.0))
                                    //.splineToConstantHeading(Vector2d(-24.0, 72.0), Math.toRadians(90.0))
                                    //.splineToConstantHeading(Vector2d(0.0, 96.0), Math.toRadians(90.0))
                                    //.splineToConstantHeading(Vector2d(-24.0, 120.0), Math.toRadians(90.0))

                                    //.splineToConstantHeading(Vector2d(-42.0, -39.0), Math.toRadians(90.0))
                                    //.splineToConstantHeading(Vector2d(-57.0, 9.0), Math.toRadians(90.0))
                                    //.splineToConstantHeading(Vector2d(-33.0, 33.0), Math.toRadians(90.0))
                                    //.splineToConstantHeading(Vector2d(-57.0, 57.0), Math.toRadians(90.0))

                                    //.splineToConstantHeading(Vector2d(-39.0, 42.0), 0.0)
                                    //.splineToConstantHeading(Vector2d(9.0, 57.0), 0.0)
                                    //.splineToConstantHeading(Vector2d(33.0, 33.0), 0.0)
                                    //.splineToConstantHeading(Vector2d(57.0, 57.0), 0.0)

                                    .splineToConstantHeading(Vector2d(-39.0, 56.0), 0.0)
                                    .splineToConstantHeading(Vector2d(9.0, 57.0), 0.0)
                                    //.splineToConstantHeading(Vector2d(33.0, 33.0), 0.0)
                                    //.splineToConstantHeading(Vector2d(57.0, 57.0), 0.0)
                                    .build()
                            dt.followTrajectory(traj)
                            when (vis!!.pipeline.height) {
                                VisionPipeline.Height.ZERO -> {
                                }
                                VisionPipeline.Height.ONE -> {
                                    traj = dt.trajectoryBuilder(traj.end())
                                            .lineTo(Vector2d(33.0, 33.0))
                                            .build()
                                    dt.followTrajectory(traj)
                                }
                                VisionPipeline.Height.FOUR -> {
                                    traj = dt.trajectoryBuilder(traj.end())
                                            .lineTo(Vector2d(57.0, 57.0))
                                            .build()
                                    dt.followTrajectory(traj)
                                }
                            }
                            //traj = dt.trajectoryBuilder(traj.end())
                            //.lineTo(Vector2d(9.0, 57.0)
                            //.build()
                            //dt.followTrajectory(traj)
                            traj = dt.trajectoryBuilder(traj.end(), true)
                                    .splineToConstantHeading(Vector2d(-39.0, 56.0), Math.toRadians(180.0))
                                    .splineToConstantHeading(Vector2d(start.x, start.y), Math.toRadians(180.0))
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
