package org.firstinspires.ftc.teamcode.module

import org.firstinspires.ftc.teamcode.geometry.Pose2d
import org.firstinspires.ftc.teamcode.geometry.*
import org.firstinspires.ftc.teamcode.geometry.Rotation2d
import org.firstinspires.ftc.teamcode.geometry.Twist2d


class HolonomicOdometry(
        initialPosition: Pose2d,
        trackWidth: Number,
        private val centerWheelOffset: Double
): Odometry(robotPosition = initialPosition, trackWidth = trackWidth.toDouble()) {
    private var prevLeftEncoder = 0.0
    private var prevRightEncoder = 0.0
    private var prevHorizontalEncoder = 0.0
    private var previousAngle: Rotation2d

    private lateinit var left: Supplier<Double>
    private lateinit var right: Supplier<Double>
    private lateinit var horizontal: Supplier<Double>

    constructor(
            leftEncoder: Supplier<Double>,
            rightEncoder: Supplier<Double>,
            horizontalEncoder: Supplier<Double>,
            trackWidth: Number,
            centerWheelOffset: Number
    ): this(trackWidth = trackWidth.toDouble(), centerWheelOffset = centerWheelOffset.toDouble()) {
        left = leftEncoder
        right = rightEncoder
        horizontal = horizontalEncoder
    }

    constructor(
            trackWidth: Number,
            centerWheelOffset: Number
    ) : this(Pose2d(), trackWidth.toDouble(), centerWheelOffset.toDouble())

    init {
        previousAngle = initialPosition.rotation
    }

    override fun updatePose() {
        update(left(), right(), horizontal())
    }

    override fun updatePose(newPose: Pose2d) {
        previousAngle = newPose.rotation
        robotPosition = newPose
        prevLeftEncoder = 0.0
        prevRightEncoder = 0.0
        prevHorizontalEncoder = 0.0
    }

    fun update(leftEncoderPos: Double, rightEncoderPos: Double, horizontalEncoderPos: Double) {
        val deltaLeftEncoder = leftEncoderPos - prevLeftEncoder
        val deltaRightEncoder = rightEncoderPos - prevRightEncoder
        val deltaHorizontalEncoder = horizontalEncoderPos - prevHorizontalEncoder

        val angle = previousAngle + Rotation2d(
                angle = (deltaLeftEncoder - deltaRightEncoder) / trackWidth
        )

        prevLeftEncoder = leftEncoderPos
        prevRightEncoder = rightEncoderPos
        prevHorizontalEncoder = horizontalEncoderPos

        val dw = (angle - previousAngle).radians
        val (dx, dy) = (deltaLeftEncoder + deltaRightEncoder) / 2.0 to
                deltaHorizontalEncoder - centerWheelOffset * dw

        val twist = Twist2d(dx = dx, dy = dy, dTheta = dw)

        val (translation, _) = (robotPosition exp twist)

        previousAngle = angle

        robotPosition = Pose2d(translation, angle)
    }

}

private typealias Supplier<T> = () -> T