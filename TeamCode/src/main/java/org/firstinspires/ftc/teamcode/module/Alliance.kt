package org.firstinspires.ftc.teamcode.module

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.geometry.Vector2d
import kotlin.math.PI

enum class Alliance(val direction: Double, val ysign: Int) {
    BLUE(PI/2.0, 1), RED(PI * 3.0/2.0, -1);

    val b = -(ysign + 1) / 2
    val r = -(-ysign + 1) / 2

    val stack = Vector2d(-24.0, 36.0 * ysign)

    val arc = -.1413
    val woffset = 0.125

    val innerShoot3 = Pose2d(-12.0, 12.0 * ysign, r * PI/8.0)
    val centerShoot3 = Pose2d(-12.0, 36.0 * ysign, arc)
    val outerShoot3 = Pose2d(-12.0, 60.0 * ysign, b * PI/8.0)

    val innerShoot2 = Pose2d(-36.0, 12.0 * ysign, r * PI/8.0)
    val centerShoot2 = Pose2d(-36.0, 36.0 * ysign, arc)
    val outerShoot2 = Pose2d(-36.0, 60.0 * ysign, b * PI/8.0)

    val target0 = Vector2d(12.0, 60.0 * ysign)
    val target1 = Vector2d(36.0, 36.0 * ysign)
    val target4 = Vector2d(60.0, 60.0 * ysign)

    fun contains(vec: Vector2d)
        = this.ysign * vec.y > 0
    infix fun at(setupPosition: SetupPosition)
        = Pose2d(pos = Vector2d(setupPosition.vec.x, setupPosition.vec.y * ysign), heading = 0.0)
}