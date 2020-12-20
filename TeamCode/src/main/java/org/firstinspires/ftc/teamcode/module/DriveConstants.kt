package org.firstinspires.ftc.teamcode.module

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.trajectory.constraints.DriveConstraints
import com.qualcomm.robotcore.hardware.PIDFCoefficients

@Config
object DriveConstants {
    /*
     * These are motor constants that should be listed online for your motors.
     */
    @JvmField var TICKS_PER_REV = 1.0
    @JvmField var MAX_RPM = 340.0

    /*
     * Set RUN_USING_ENCODER to true to enable built-in hub velocity control using drive encoders.
     * Set this flag to false if drive encoders are not present and an alternative localization
     * method is in use (e.g., tracking wheels).
     *
     * If using the built-in motor velocity PID, update MOTOR_VELO_PID with the tuned coefficients
     * from DriveVelocityPIDTuner.
     */
    @JvmField var RUN_USING_ENCODER = false
    @JvmField var MOTOR_VELO_PID = PIDFCoefficients(0.0, 0.0, 0.0, getMotorVelocityF(MAX_RPM / 60 * TICKS_PER_REV))

    /*
     * These are physical constants that can be determined from your robot (including the track
     * width; it will be tune empirically later although a rough estimate is important). Users are
     * free to chose whichever linear distance unit they would like so long as it is consistently
     * used. The default values were selected with inches in mind. Road runner uses radians for
     * angular distances although most angular parameters are wrapped in Math.toRadians() for
     * convenience. Make sure to exclude any gear ratio included in MOTOR_CONFIG from GEAR_RATIO.
     */
    @JvmField var WHEEL_RADIUS = 1.9685039 // in
    @JvmField var GEAR_RATIO = 1.0 // output (wheel) speed / input (motor) speed
    @JvmField var TRACK_WIDTH =  15.51 // in

    /*
     * These are the feedforward parameters used to model the drive motor behavior. If you are using
     * the built-in velocity PID, *these values are fine as is*. However, if you do not have drive
     * motor encoders or have elected not to use them for velocity control, these values should be
     * empirically tuned.
     */
    @JvmField var kV = 0.0159
    @JvmField var kA = 0.0024
    @JvmField var kStatic = 0.0

    /*
     * These values are used to generate the trajectories for you robot. To ensure proper operation,
     * the constraints should never exceed ~80% of the robot's actual capabilities. While Road
     * Runner is designed to enable faster autonomous motion, it is a good idea for testing to start
     * small and gradually increase them later after everything is working. The velocity and
     * acceleration values are required, and the jerk values are optional (setting a jerk of 0.0
     * forces acceleration-limited profiling). All distance units are inches.
     */
    @JvmField var BASE_CONSTRAINTS = DriveConstraints(
            30.0, 30.0, 0.0,
            Math.toRadians(180.0), Math.toRadians(180.0), 0.0
    )

    val SLOW_CONSTRAINTS = DriveConstants.BASE_CONSTRAINTS.copy().apply { maxVel /= 3.0 }

    fun encoderTicksToInches(ticks: Double): Double {
        return WHEEL_RADIUS * 2 * Math.PI * GEAR_RATIO * ticks / TICKS_PER_REV
    }

    fun rpmToVelocity(rpm: Double): Double {
        return rpm * GEAR_RATIO * 2 * Math.PI * WHEEL_RADIUS / 60.0
    }

    fun getMotorVelocityF(ticksPerSecond: Double): Double {
        // see https://docs.google.com/document/d/1tyWrXDfMidwYyP_5H4mZyVgaEswhOC35gvdmP-V-5hA/edit#heading=h.61g9ixenznbx
        return 32767 / ticksPerSecond
    }
}

fun DriveConstraints.copy() = DriveConstraints(
        maxVel = maxVel,
        maxAccel = maxAccel,
        maxJerk = maxJerk,
        maxAngVel = maxAngVel,
        maxAngAccel = maxAngAccel,
        maxAngJerk = maxAngJerk
)
