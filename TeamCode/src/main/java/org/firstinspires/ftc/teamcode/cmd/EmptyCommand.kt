package org.firstinspires.ftc.teamcode.cmd

import org.firstinspires.ftc.teamcode.module.Robot

object EmptyCommand : Command() {
    override suspend fun execute(bot: Robot) {}

    operator fun invoke() = EmptyCommand
}
