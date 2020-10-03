package org.firstinspires.ftc.teamcode.module

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.dsl.Context
import org.firstinspires.ftc.teamcode.dsl.OpModeContext
import org.firstinspires.ftc.teamcode.dsl.RobotDsl

@RobotDsl
open class Robot(val opmode: OpMode): Context<OpModeContext.Context> {
    val log: Telemetry = opmode.telemetry
    val hwmap: HardwareMap = opmode.hardwareMap
    val dt: DriveTrain = DriveTrain(this)
    val ink: Intake = Intake(this)

    override var context: OpModeContext.Context = OpModeContext.Context.BASE
        set(value) {
            opmode.telemetry.addData("$field", "Switching to $value context")
            field = value
        }

    fun <T: Any> Telemetry.addData(value: T) {
        this.addData("$context", value)
    }

    fun <T: Any> Telemetry.addData(valueProducer: () -> T) {
        this.addData("$context", valueProducer)
    }
}