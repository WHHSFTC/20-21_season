package org.firstinspires.ftc.teamcode.module

import com.acmerobotics.roadrunner.util.NanoClock
import com.qualcomm.robotcore.hardware.DcMotorEx

class Revcoder {
    private val CPS_STEP = 0x10000

    private fun inverseOverflow(input: Double, estimate: Double): Double {
        var real = input
        while (Math.abs(estimate - real) > CPS_STEP / 2.0) {
            real += Math.signum(estimate - real) * CPS_STEP
        }
        return real
    }

    enum class Direction(val multiplier: Int) {
        FORWARD(1), REVERSE(-1);

    }

    private var motor: DcMotorEx? = null
    private var clock: NanoClock? = null

    private var direction: Direction? = null

    private var lastPosition = 0
    private var velocityEstimate = 0.0
    private var lastUpdateTime = 0.0

    constructor(motor: DcMotorEx?, clock: NanoClock) {
        this.motor = motor
        this.clock = clock
        direction = Direction.FORWARD
        lastPosition = 0
        velocityEstimate = 0.0
        lastUpdateTime = clock.seconds()
    }

    constructor(motor: DcMotorEx?) : this(motor, NanoClock.system())

    fun getDirection(): Direction? {
        return direction
    }

    /**
     * Allows you to set the direction of the counts and velocity without modifying the motor's direction state
     * @param direction either reverse or forward depending on if encoder counts should be negated
     */
    fun setDirection(direction: Direction?) {
        this.direction = direction
    }

    fun getCurrentPosition(): Int {
        val multiplier = direction!!.multiplier
        val currentPosition = motor!!.currentPosition * multiplier
        if (currentPosition != lastPosition) {
            val currentTime = clock!!.seconds()
            val dt = currentTime - lastUpdateTime
            velocityEstimate = (currentPosition - lastPosition) / dt
            lastPosition = currentPosition
            lastUpdateTime = currentTime
        }
        return currentPosition
    }

    fun getRawVelocity(): Double {
        val multiplier = direction!!.multiplier
        return motor!!.velocity * multiplier
    }

    fun getCorrectedVelocity(): Double {
        return inverseOverflow(getRawVelocity(), velocityEstimate)
    }
}