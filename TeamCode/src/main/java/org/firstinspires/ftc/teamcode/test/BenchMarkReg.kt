package org.firstinspires.ftc.teamcode.test

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.ElapsedTime
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.firstinspires.ftc.teamcode.module.CustomMecanumDrive
import org.firstinspires.ftc.teamcode.module.OpMode
import org.firstinspires.ftc.teamcode.module.Robot

@TeleOp(group = "Benchmark")
class BenchMarkReg: OpMode(Mode.TELE) {
    override fun runOpMode() {
        bot = Robot(this)

        var power = 0.0

        var powers: CustomMecanumDrive.Powers

        val timer = ElapsedTime()
        val times = emptyList<Double>().toMutableList()

        waitForStart()

        timer.reset()

        runBlocking {
            for (i in 0 until 20) {
                launch {
                    timer.reset()
                    powers = bot.dt.powers
                    bot.dt.powers = CustomMecanumDrive.Powers(power, power, power, power)

                    power += 0.1

                    times.add(timer.milliseconds())
                }
            }

            bot.dt.powers = CustomMecanumDrive.Powers()

            bot.log.clear()
            bot.log.addData("Average:", "${times.average()} ms")
            bot.log.update()
        }
    }

    override suspend fun onInit() {}

    override suspend fun onRun() {}

    override suspend fun onLoop() {}

    override suspend fun onStop() {}
}