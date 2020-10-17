package org.firstinspires.ftc.teamcode.module

import org.firstinspires.ftc.teamcode.geometry.Pose2d
import org.firstinspires.ftc.teamcode.geometry.rotate

abstract class Odometry(
        protected var robotPosition: Pose2d,
        protected var trackWidth: Double
) {
    constructor(robotPosition: Pose2d, trackWidth: Number):
            this(robotPosition = robotPosition, trackWidth = trackWidth.toDouble())
    constructor(robotPosition: Pose2d): this(robotPosition = robotPosition, trackWidth = 18.0)

    val position: Pose2d
        get() = robotPosition

    abstract fun updatePose(newPose: Pose2d)

    abstract fun updatePose()

    operator fun invoke(newPose: Pose2d) = updatePose(newPose = newPose)

    operator fun invoke() = updatePose()

    fun rotatePose(byAngle: Double) = Unit.apply { robotPosition = robotPosition rotate byAngle }
}