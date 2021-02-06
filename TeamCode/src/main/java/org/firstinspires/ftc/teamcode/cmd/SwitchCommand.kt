package org.firstinspires.ftc.teamcode.cmd

import org.firstinspires.ftc.teamcode.module.Robot

class SwitchCommand<T: Any?>(
        val supp: Robot.() -> T,
        val cases: List<SwitchCommand.Case<T>>,
): Command() {
    data class Case<T: Any?>(val supp: Robot.() -> T, val com: Command)
    override suspend fun execute(bot: Robot) {
        for (c in cases) {
            if (supp(bot) == c.supp(bot))
                return c.com.execute(bot)
        }
    }
}
