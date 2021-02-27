package org.firstinspires.ftc.teamcode.cmd

import org.firstinspires.ftc.teamcode.dsl.RobotDsl
import org.firstinspires.ftc.teamcode.module.Robot

class SwitchCommand<T>(
        val supp: Robot.() -> T,
        val cases: List<Case<T>>,
): Command() {
    @RobotDsl
    class SwitchCaseBuilder<T>: DSLContext() {
        private val list: MutableList<Case<T>> = mutableListOf()

        fun case(supp: Robot.() -> T, com: Command) {
            list += Case(supp, com)
        }

        fun case(supp: Robot.() -> T, cmdBlock: suspend Robot.() -> Unit) {
            list += Case(supp, LambdaCommand(cmdBlock))
        }

        fun build(): List<Case<T>> = list
    }

    data class Case<T>(val supp: Robot.() -> T, val com: Command)
    
    override suspend fun execute(bot: Robot) {
        for (c in cases) {
            if (supp(bot) == c.supp(bot))
                return c.com.execute(bot)
        }
    }
}
