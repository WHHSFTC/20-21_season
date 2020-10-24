package org.firstinspires.ftc.teamcode.module

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.localization.ThreeTrackingWheelLocalizer
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.acme.util.Encoder
import java.util.*

/*
* Sample tracking wheel localizer implementation assuming the standard configuration:
*
*    /--------------\
*    |     ____     |
*    |     ----     |
*    | ||        || |
*    | ||        || |
*    |              |
*    |              |
*    \--------------/
*
*/
class CustomLocalizer(val encoders: Encoders) : ThreeTrackingWheelLocalizer(Arrays.asList(
        Pose2d(0.0, LATERAL_DISTANCE / 2, 0.0),  // left
        Pose2d(0.0 -LATERAL_DISTANCE / 2, 0.0),  // right
        Pose2d(-BACK_OFFSET, LATERAL_OFFSET, Math.toRadians(90.0)) // front
)) {
    private val leftEncoder: Encoder = Encoder(encoders.left)
    private val rightEncoder: Encoder = Encoder(encoders.right)
    private val backEncoder: Encoder = Encoder(encoders.back)

    override fun getWheelPositions(): List<Double> {
        return listOf(
                encoderTicksToInches(leftEncoder.currentPosition.toDouble()) * X_MULT,
                encoderTicksToInches(rightEncoder.currentPosition.toDouble()) * X_MULT,
                encoderTicksToInches(backEncoder.currentPosition.toDouble()) * Y_MULT
        )
    }

    override fun getWheelVelocities(): List<Double> {
        // TODO: If your encoder velocity can exceed 32767 counts / second (such as the REV Through Bore and other
        //  competing magnetic encoders), change Encoder.getRawVelocity() to Encoder.getCorrectedVelocity() to enable a
        //  compensation method
        return listOf(
                encoderTicksToInches(leftEncoder.correctedVelocity),
                encoderTicksToInches(rightEncoder.correctedVelocity),
                encoderTicksToInches(backEncoder.correctedVelocity)
        )
    }

    @Config
    companion object {
        // TODO: config
        @JvmField var TICKS_PER_REV = 0.0
        @JvmField var WHEEL_RADIUS = 2.0 // in
        @JvmField var GEAR_RATIO = 1.0 // output (wheel) speed / input (encoder) speed
        @JvmField var LATERAL_DISTANCE = 10.0 // in; distance between the left and right wheels
        @JvmField var BACK_OFFSET = 4.0 // in; offset of the lateral wheel
        @JvmField var LATERAL_OFFSET = 4.0 // in; offset of the lateral wheel
        @JvmField var X_MULT = 4.0 // in; offset of the lateral wheel
        @JvmField var Y_MULT = 4.0 // in; offset of the lateral wheel
        fun encoderTicksToInches(ticks: Double): Double {
            return WHEEL_RADIUS * 2 * Math.PI * GEAR_RATIO * ticks / TICKS_PER_REV
        }
    }
}