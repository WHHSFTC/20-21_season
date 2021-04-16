package org.firstinspires.ftc.teamcode.switchboard.drive

import org.firstinspires.ftc.teamcode.geometry.epsilonEquals
import kotlin.math.sign

data class FeedforwardCoef(val kV: Double, val kA: Double, val kS: Double) {
    fun calculate(velo: Double, accel: Double): Double {
        val va = velo * kV + accel * kA
        return if (va epsilonEquals 0.0)
            0.0
        else
            va + sign(va) * kS
    }

    fun calculateList(velo: List<Double>, accel: List<Double>): List<Double>
        = (velo zip accel).map { (v, a) -> calculate(v, a) }
}

