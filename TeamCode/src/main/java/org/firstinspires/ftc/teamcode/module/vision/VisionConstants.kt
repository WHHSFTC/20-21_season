package org.firstinspires.ftc.teamcode.module.vision

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.geometry.Pose2d

@Config
object VisionConstants {
    @JvmField
    var FOCAL_RATIO = 92.5 / 46.0

    @JvmField
    var CAMERA_HEIGHT = 5.0

    @JvmField
    var CAMERA_X = 9.0
    @JvmField
    var CAMERA_Y = -5.0
    @JvmField
    var CAMERA_THETA = 0.0

    @JvmField
    var CAMERA_VERTICAL_ANGLE = 0.0

    val cameraPose get() = Pose2d(CAMERA_X, CAMERA_Y, CAMERA_THETA)
}