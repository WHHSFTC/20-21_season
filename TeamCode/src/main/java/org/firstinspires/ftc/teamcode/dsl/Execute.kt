package org.firstinspires.ftc.teamcode.dsl

import org.firstinspires.ftc.teamcode.module.Robot

open class Execute(onExecution: Robot.() -> Unit): DslOpMode(
        {
            mode = Mode.NULL
            onInit(onExecution)
        }
)