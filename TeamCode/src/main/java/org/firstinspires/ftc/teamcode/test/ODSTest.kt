package org.firstinspires.ftc.teamcode.test

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.hardware.DistanceSensor
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.firstinspires.ftc.teamcode.module.OpMode
import org.firstinspires.ftc.teamcode.module.Shooter

@Autonomous
class ODSTest: OpMode(Mode.TELE) {
    lateinit var ods: OpticalDistanceSensor

    override suspend fun onInit() {
        ods = hardwareMap.opticalDistanceSensor["frontods"]
    }

    override suspend fun onRun() {
    }

    override suspend fun onLoop() {
        telemetry.addData("rawLightDetected", ods.rawLightDetected)
        telemetry.addData("rawLightDetectedMax", ods.rawLightDetectedMax)
        telemetry.addData("lightDetected", ods.lightDetected)
        telemetry.addData("battery", bot.dt.batteryVoltageSensor.voltage)
    }

    override suspend fun onStop() { }
}