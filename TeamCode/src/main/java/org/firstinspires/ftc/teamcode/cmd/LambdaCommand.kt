package org.firstinspires.ftc.teamcode.cmd

import org.firstinspires.ftc.teamcode.module.Robot

open class LambdaCommand(val f: suspend Robot.() -> Unit) : Command() {
    override suspend fun execute(bot: Robot) {
        bot.f()
    }
}
