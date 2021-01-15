package org.firstinspires.ftc.teamcode.test

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.firstinspires.ftc.teamcode.module.OpMode
import org.firstinspires.ftc.teamcode.module.Shooter

@Autonomous
class RPMTest: OpMode(Mode.TELE) {
    var target = Shooter.State.OFF
    var omega = 0
    var last = 0
    var now = 0

    override suspend fun onInit() { }

    override suspend fun onRun() {
        GlobalScope.launch {
            while (opModeIsActive()) {
                target = Shooter.State.FULL
                delay(8000)
                target = Shooter.State.OFF
                delay(8000)
            }
        }
    }

    override suspend fun onLoop() {
        bot.out(target)
        now = bot.out.motor1.currentPosition
        omega = now - last
        last = now
        telemetry.addData("target", target.vel)
        telemetry.addData("omega", omega)
        telemetry.addData("battery", bot.dt.batteryVoltageSensor.voltage)
    }

    override suspend fun onStop() {
        bot.out(Shooter.State.OFF)
    }
}