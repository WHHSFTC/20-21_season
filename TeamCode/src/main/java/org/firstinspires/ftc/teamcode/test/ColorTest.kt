package org.firstinspires.ftc.teamcode.test

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.hardware.ColorSensor
import com.qualcomm.robotcore.hardware.DistanceSensor
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.firstinspires.ftc.teamcode.module.OpMode
import org.firstinspires.ftc.teamcode.module.Shooter

@Autonomous
class ColorTest: OpMode(Mode.TELE) {
    lateinit var color: ColorSensor

    override suspend fun onInit() {
        color = hardwareMap.colorSensor["wobcolor"]
    }

    override suspend fun onRun() {
    }

    override suspend fun onLoop() {
        telemetry.addData("alpha", color.alpha())
        telemetry.addData("red", color.red())
        telemetry.addData("green", color.green())
        telemetry.addData("blue", color.blue())
        telemetry.addData("battery", bot.dt.batteryVoltageSensor.voltage)

        if (gamepad1.a)
            color.enableLed(true)
        else if (gamepad1.b)
            color.enableLed(false)
    }

    override suspend fun onStop() { }
}