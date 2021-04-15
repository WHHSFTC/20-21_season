package org.firstinspires.ftc.teamcode.switchboard.motion

import org.firstinspires.ftc.teamcode.geometry.Pose2d

interface Path {
    fun update(pose: Pose2d, velo: Pose2d): Pose2d
}