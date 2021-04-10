package org.firstinspires.ftc.teamcode.geometry

import kotlin.math.*

data class Pose2d(
        val x: Double, val y: Double, val theta: Double
) {
    constructor(vec: Vector2d, theta: Double = 0.0) : this(vec.x, vec.y, theta)

    val vec: Vector2d by lazy { Vector2d(x, y) }

    infix fun transformBy(other: Pose2d): Pose2d =
            Pose2d(
                    vec = vec + (other.vec rotateBy theta),
                    theta = (theta + other.theta).angleWrap()
            )

    infix fun relativeTo(other: Pose2d): Pose2d =
            Pose2d(
                    vec = (vec - other.vec) rotateBy (-other.theta),
                    theta = (theta - other.theta).angleWrap()
            )

    fun rotate(deltaTheta: Double) =
            this.copy(theta = theta + deltaTheta)

    operator fun plus(other: Pose2d) = this transformBy other

    operator fun minus(other: Pose2d) = this relativeTo other

    fun exp(twist: Pose2d): Pose2d {
        val (dx, dy, dTheta) = twist

        val (sinTheta, cosTheta) = sin(dTheta) to cos(dTheta)

        val (s, c) = if (dTheta.absoluteValue < 1E-9) {
            1.0 - 1.0 / 6.0 * dTheta * dTheta to 0.5 * dTheta
        } else {
            sinTheta / dTheta to (1 - cosTheta) / dTheta
        }
        val transform = Pose2d(Vector2d(x = dx * s - dy * c, y = dx * c + dy * s), dTheta)

        return this + transform
    }

    fun log(end: Pose2d): Pose2d {
        val transform: Pose2d = end relativeTo this
        val dTheta: Double = transform.theta
        val halfDTheta = dTheta / 2.0

        val cosMinusOne: Double = cos(transform.theta) - 1

        val halfThetaByTanOfHalfDTheta: Double
        halfThetaByTanOfHalfDTheta = if (cosMinusOne.absoluteValue < 1E-9) {
            1.0 - 1.0 / 12.0 * dTheta * dTheta
        } else {
            -(halfDTheta * sin(transform.theta)) / cosMinusOne
        }

        val (dX, dY) = Vector2d.polar(r = hypot(halfThetaByTanOfHalfDTheta, halfDTheta), theta = transform.theta + atan2(-halfDTheta, halfThetaByTanOfHalfDTheta))

        return Pose2d(dX, dY, dTheta)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Pose2d) return false

        return vec == other.vec
            && theta == other.theta
    }

    override fun hashCode(): Int {
        var result = vec.hashCode()
        result = 31 * result + theta.hashCode()
        return result
    }
}