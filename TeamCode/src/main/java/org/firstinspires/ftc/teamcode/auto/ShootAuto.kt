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
class ShootAuto: DslOpMode(mode = Mode.TELE) {
    init {
        dsl {
            //val start: Pose2d = Pose2d(Vector2d(-63.0, 33.0), 0.0)
            onInit {
                cmd {
                    log.logData("Init")
                    log.logData("...")
                    log.logData("Done")
                    //dt.poseEstimate = Pose2d(Vector2d(), Math.toRadians(90.0))
                    //dt.poseEstimate = Pose2d(Vector2d(-33.0, -63.0), Math.toRadians(90.0))
                    //dt.poseEstimate = start
                }
            }

            onRun {
                seq {
                    +cmd {
                        var traj = dt.trajectoryBuilder(dt.poseEstimate)
                                .splineToConstantHeading(Vector2d(48.0, 0.0), 0.0)
                                .splineToConstantHeading(Vector2d(64.0, -28.0), 0.0)
                                .build()
                        dt.followTrajectory(traj)
                        dt.waitForIdle()
                        aim.height(HeightController.Height.HIGH)
                        feed.height(Indexer.Height.HIGH)
                        out(Shooter.State.FULL)
                        sleep(1000)
                        feed.burst()
                        sleep(150)
                        out(Shooter.State.OFF)
                        feed.height(Indexer.Height.IN)
                        aim.height(HeightController.Height.ZERO)
                        traj = dt.trajectoryBuilder(traj.end())
                                .lineTo(Vector2d(72.0, -28.0))
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
