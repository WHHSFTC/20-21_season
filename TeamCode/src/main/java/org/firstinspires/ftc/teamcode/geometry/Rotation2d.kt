package org.firstinspires.ftc.teamcode.geometry

import kotlin.math.*

data class Rotation2d(
        val value: Double,
        val cos: Double,
        val sin: Double
) {
    constructor(angle: Number): this(m_val = angle.clipIntoDomain())
    constructor(x: Number, y: Number): this(cosSinPair = {
        val magnitude = hypot(x.toDouble(), y.toDouble())
        if (magnitude > 1.0e-6) {
            (x.toDouble() / magnitude) to (y.toDouble() / magnitude)
        } else {
            1.0 to 0.0
        }
    }())
    constructor(): this(0.0, 1.0, 0.0)

    private constructor(m_val: Double):
            this(value = m_val, cos = cos(m_val), sin = sin(m_val))
    private constructor(cos: Double, sin: Double):
            this(value = atan2(sin, cos), cos = cos, sin = sin)
    private constructor(cosSinPair: Pair<Double, Double>):
            this(cos = cosSinPair.first, sin = cosSinPair.second)

    val radians: Double
        get() = value

    val degrees: Double
        get() = value.toDegrees()

    val tan: Double
        get() = sin / cos

    operator fun unaryPlus() = Rotation2d(angle = +value)

    operator fun unaryMinus() = Rotation2d(angle = -value)

    operator fun plus(other: Rotation2d) = this.rotateBy(other)

    operator fun minus(other: Rotation2d) = this.rotateBy(-other)

    operator fun times(scalar: Number) = Rotation2d(angle = value * scalar.toDouble())

    fun rotateBy(other: Rotation2d) =
            Rotation2d(
                    x = cos * other.cos - sin * other.sin,
                    y = cos * other.sin + sin * other.cos
            )

    override fun toString(): String =
            "(Rads: ${value.decimalFormat(2)}, Deg: ${value.toDegrees().decimalFormat(2)})"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Rotation2d) return false

        return (other.value - value).absoluteValue < 1.0E-9
    }

    private fun Double.decimalFormat(precision: Int) =
            "%${precision}f".format(this)

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + cos.hashCode()
        result = 31 * result + sin.hashCode()
        return result
    }

    companion object {
        @JvmStatic
        fun fromDegrees(degrees: Number): Rotation2d =
                Rotation2d(angle = degrees.toRadians())
    }
}

private fun <T: Number> T.clipIntoDomain(bottom: Double = -Math.PI, top: Double = Math.PI): Double {
    var ret = this.toDouble()
    while(ret > top) ret -= 2 * Math.PI
    while(ret < bottom) ret += 2 * Math.PI
    return ret
}

private fun Number.toRadians() =
        Math.toRadians(this.toDouble())

private fun Number.toDegrees() =
        Math.toDegrees(this.toDouble())
