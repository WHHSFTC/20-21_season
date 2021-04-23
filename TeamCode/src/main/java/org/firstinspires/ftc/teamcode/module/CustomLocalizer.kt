package org.firstinspires.ftc.teamcode.module

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.localization.ThreeTrackingWheelLocalizer
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.switchboard.core.Activity
import org.firstinspires.ftc.teamcode.switchboard.core.Configuration
import org.firstinspires.ftc.teamcode.switchboard.core.Frame
import org.firstinspires.ftc.teamcode.switchboard.core.Logger
import org.firstinspires.ftc.teamcode.switchboard.hardware.Encoder
import org.firstinspires.ftc.teamcode.switchboard.shapes.Distance
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
class CustomLocalizer(config: Configuration, val logger: Logger) : ThreeTrackingWheelLocalizer(Arrays.asList(
        Pose2d(FRONT_X, LATERAL_DISTANCE / 2, 0.0),  // left
        Pose2d(FRONT_X, -LATERAL_DISTANCE / 2, Math.toRadians(180.0)),  // right
        Pose2d(BACK_X, -LATERAL_DISTANCE / 2, Math.toRadians(90.0)) // back
)), Activity {
    private val leftEncoder: Encoder = config.encoders["leftOdo"]
    private val rightEncoder: Encoder = config.encoders["rightOdo"]
    private val backEncoder: Encoder = config.encoders["backOdo"]

    override fun load() { }

    override fun update(frame: Frame) {
        update()
        logger.out["Pose"] = poseEstimate
        logger.out["Velo"] = poseVelocity
    }

    override fun getWheelPositions(): List<Double> {
        return listOf(
                encoderTicksToInches(leftEncoder.position.toDouble()) * X_MULT,
                encoderTicksToInches(rightEncoder.position.toDouble()) * X_MULT,
                encoderTicksToInches(backEncoder.position.toDouble()) * Y_MULT
        )
    }

    override fun getWheelVelocities(): List<Double> {
        // TODO: If your encoder velocity can exceed 32767 counts / second (such as the REV Through Bore and other
        //  competing magnetic encoders), change Encoder.getRawVelocity() to Encoder.getCorrectedVelocity() to enable a
        //  compensation method
        return listOf(
                encoderTicksToInches(leftEncoder.velocity),
                encoderTicksToInches(rightEncoder.velocity),
                encoderTicksToInches(backEncoder.velocity)
        )
    }

    @Config
    companion object {
        // Rotunda
//        @JvmField var TICKS_PER_REV = 8192.0
//        @JvmField var WHEEL_RADIUS = 1.0 // in
//        @JvmField var GEAR_RATIO = 1.0 // output (wheel) speed / input (encoder) speed
//        @JvmField var LATERAL_DISTANCE = 14.75 // in; distance between the left and right wheels
//        @JvmField var BACK_X = -2.41 // in; x of the back
//        @JvmField var FRONT_X = 1.71 // in; x of the fronts
//        @JvmField var X_MULT = .98473
//        @JvmField var Y_MULT = .99794

        // Summum
        @JvmField var TICKS_PER_REV = 8192.0
        @JvmField var WHEEL_RADIUS = Distance.mm(30.0).inches
        @JvmField var GEAR_RATIO = 1.0 // output (wheel) speed / input (encoder) speed
        @JvmField var LATERAL_DISTANCE = 14.75 // in; distance between the left and right wheels
        @JvmField var BACK_X = -2.41 // in; x of the back
        @JvmField var FRONT_X = 1.71 // in; x of the fronts
        @JvmField var X_MULT = .98473
        @JvmField var Y_MULT = .99794
        fun encoderTicksToInches(ticks: Double): Double {
            return WHEEL_RADIUS * 2 * Math.PI * GEAR_RATIO * ticks / TICKS_PER_REV
        }
    }
}