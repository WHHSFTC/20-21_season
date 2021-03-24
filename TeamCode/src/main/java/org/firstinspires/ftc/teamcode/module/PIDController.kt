package org.firstinspires.ftc.teamcode.module

import kotlin.math.abs

data class PIDCoefficients(val kP: Double = 0.0, val kI: Double = 0.0, val kD: Double = 0.0)

class PIDController(var coef: PIDCoefficients, val write: NConsumer<Double>, val read: NSupplier<Double>): Module<Double> {
    private var target = 0.0
    var prevError = target - read()
    var integral = 0.0
    var derivative = 0.0
    override var state: Double
        get() = target
        set(value) {target = value}

    fun update(timestep: Double) {
        val error = target - read()
        derivative = (error - prevError)/timestep
        integral += timestep * error
        write(coef.kP * error + coef.kI * integral + coef.kD * derivative)
        prevError = error
    }

    fun reset() {
        prevError = target - read()
        integral = 0.0
        derivative = 0.0
    }

    fun stable(threshold: Double) = abs(prevError) < threshold && abs(derivative) < threshold
}