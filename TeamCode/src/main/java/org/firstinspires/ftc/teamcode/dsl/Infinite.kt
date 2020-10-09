package org.firstinspires.ftc.teamcode.dsl

import org.firstinspires.ftc.teamcode.module.Robot

open class Infinite(private val periodic: Robot.() -> Unit): DslOpMode(
        {
            mode = Mode.TELE
            onLoop {
                periodic()
            }
        }
)