package org.firstinspires.ftc.teamcode.cmd

import org.firstinspires.ftc.teamcode.dsl.RobotDsl
import org.firstinspires.ftc.teamcode.module.Robot

@RobotDsl
open class DSLContext {
    companion object {
        lateinit var bot: Robot
    }
}
