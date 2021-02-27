package org.firstinspires.ftc.teamcode.auto

import com.acmerobotics.roadrunner.geometry.Vector2d
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.firstinspires.ftc.teamcode.cmd.*
import org.firstinspires.ftc.teamcode.dsl.*
import org.firstinspires.ftc.teamcode.module.CustomMecanumDrive

@Disabled
@Autonomous(name = "Blue - Park")
class BluePark: DslOpMode(mode = Mode.TELE) {
    init {
        runBlocking {
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

                onLoop {
                    cmd {
                        var traj = dt.trajectoryBuilder(dt.poseEstimate)
                                .lineTo(Vector2d(72.0, 0.0))
                                .build()
                        dt.followTrajectory(traj)

                        delay(1000)

                        traj = dt.trajectoryBuilder(traj.end())
                                .lineTo(Vector2d(0.0, 0.0))
                                .build()
                        dt.followTrajectory(traj)
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
