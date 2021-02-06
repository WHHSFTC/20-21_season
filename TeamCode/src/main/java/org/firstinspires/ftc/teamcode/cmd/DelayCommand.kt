package org.firstinspires.ftc.teamcode.cmd

import kotlinx.coroutines.delay
import org.firstinspires.ftc.teamcode.module.Robot
import org.firstinspires.ftc.teamcode.util.Time

class DelayCommand(val delay: Time, val blockCoroutine: Boolean = false): Command() {
    override suspend fun execute(bot: Robot) {
        if (blockCoroutine) {
            Thread.sleep(delay.milliSeconds)
        } else {
            delay(delay.milliSeconds)
        }
    }
}
