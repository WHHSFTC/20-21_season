package org.firstinspires.ftc.teamcode.module

import com.acmerobotics.roadrunner.geometry.Vector2d
import kotlin.math.PI

enum class SetupPosition(val vec: Vector2d) {
    INNER(Vector2d(-72.0 + Summum.LENGTH / 2.0, 24.0 - Summum.WIDTH / 2.0)),
    OUTER(Vector2d(-72.0 + Summum.LENGTH / 2.0, 48.0 + Summum.WIDTH / 2.0)),
}