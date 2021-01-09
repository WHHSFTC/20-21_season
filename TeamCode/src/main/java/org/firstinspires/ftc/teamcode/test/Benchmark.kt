package org.firstinspires.ftc.teamcode.test

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.ElapsedTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.firstinspires.ftc.teamcode.module.CustomMecanumDrive
import org.firstinspires.ftc.teamcode.module.OpMode

@TeleOp(group = "Benchmark")
class Benchmark : OpMode(Mode.TELE) {
    val IOTimer = ElapsedTime()
    val regTimer = ElapsedTime()

    val IOTimes = emptyList<Double>().toMutableList()
    val regTimes = emptyList<Double>().toMutableList()

    override suspend fun onInit() {}

    override suspend fun onRun() {}

    override suspend fun onLoop() {}

    override suspend fun onStop() {}

    override fun runOpMode() {
        var storePowers: CustomMecanumDrive.Powers
        var power = 0.0
        GlobalScope.launch(Dispatchers.IO) {
            for (i in 0 until 10) {
                IOTimer.reset()
                storePowers = bot.dt.powers
                bot.dt.powers = CustomMecanumDrive.Powers(power, power, power, power)
                power += 0.1

                IOTimes.add(IOTimer.milliseconds())
            }
        }
        power = 0.0
        for (i in 0 until 10) {
            regTimer.reset()
            storePowers = bot.dt.powers
            bot.dt.powers = CustomMecanumDrive.Powers(power, power, power, power)
            power += 0.1

            regTimes.add(regTimer.milliseconds())
        }

        val regAvg = regTimes.average()
        val IOAvg = IOTimes.average()

        telemetry.addData("Regular Thread:", "$regAvg ms")
        telemetry.addData("Dispatchers.IO:", "$IOAvg ms")

        telemetry.update()

        Thread.sleep(5000)
    }
}