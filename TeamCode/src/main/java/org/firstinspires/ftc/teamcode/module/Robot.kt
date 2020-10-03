package org.firstinspires.ftc.teamcode.module

import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.dsl.Context
import org.firstinspires.ftc.teamcode.dsl.OpModeContext
import org.firstinspires.ftc.teamcode.dsl.RobotDsl

@RobotDsl
open class Robot(val opmode: OpMode): Context<OpModeContext.Context> {
    val dt: DriveTrain = DriveTrain(opmode)
    val telem: Telemetry = opmode.telemetry

    override var context: OpModeContext.Context = OpModeContext.Context.BASE
        set(value) {
            opmode.telemetry.addData("$field", "Switching to $value context")
            field = value
        }

    fun <T: Any> Telemetry.addData(value: T) {
        telem.addData("$context", value)
    }

    fun <T: Any> Telemetry.addData(valueProducer: () -> T) {
        telem.addData("$context", valueProducer)
    }
}