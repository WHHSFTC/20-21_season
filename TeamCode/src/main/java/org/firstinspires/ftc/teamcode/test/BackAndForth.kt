package org.firstinspires.ftc.teamcode.test

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.trajectory.Trajectory
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.module.CustomMecanumDrive
import org.firstinspires.ftc.teamcode.module.OpMode

/*
 * Op mode for preliminary tuning of the follower PID coefficients (located in the drive base
 * classes). The robot drives back and forth in a straight line indefinitely. Utilization of the
 * dashboard is recommended for this tuning routine. To access the dashboard, connect your computer
 * to the RC's WiFi network. In your browser, navigate to https://192.168.49.1:8080/dash if you're
 * using the RC phone or https://193.168.43.1:8080/dash if you are using the Control Hub. Once
 * you've successfully connected, start the program, and your robot will begin moving forward and
 * backward. You should observe the target position (green) and your pose estimate (blue) and adjust
 * your follower PID coefficients such that you follow the target position as accurately as possible.
 * If you are using SampleMecanumDrive, you should be tuning TRANSLATIONAL_PID and HEADING_PID.
 * If you are using SampleTankDrive, you should be tuning AXIAL_PID, CROSS_TRACK_PID, and HEADING_PID.
 * These coefficients can be tuned live in dashboard.
 *
 * This opmode is designed as a convenient, coarse tuning for the follower PID coefficients. It
 * is recommended that you use the FollowerPIDTuner opmode for further fine tuning.
 */
@Config
@Autonomous(group = "drive")
class BackAndForth : OpMode(Mode.TELE) {
    private lateinit var drive: CustomMecanumDrive
    private lateinit var trajectoryForward: Trajectory
    private lateinit var trajectoryBackward: Trajectory
    override fun onInit() {
        drive = CustomMecanumDrive(bot)
        trajectoryForward = drive.trajectoryBuilder(Pose2d())
                .forward(BackAndForth.Companion.DISTANCE)
                .build()
        trajectoryBackward = drive.trajectoryBuilder(trajectoryForward.end())
                .back(BackAndForth.Companion.DISTANCE)
                .build()
    }

    override fun onLoop() {
        drive.followTrajectory(trajectoryForward)
        drive.followTrajectory(trajectoryBackward)
    }

    override fun onRun() {}
    override fun onStop() {}

    companion object {
        @JvmField var DISTANCE = 50.0
    }
}