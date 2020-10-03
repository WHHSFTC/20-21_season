package org.firstinspires.ftc.teamcode.dsl

import org.firstinspires.ftc.teamcode.module.Robot

@RobotDsl
open class RobotContext(): Context {
    lateinit var bot: Robot

    constructor(b: Robot): this() {
        bot = b
    }

    override val context: String
        get() = "Robot"

    val isBotInitialized: Boolean get() = ::bot.isInitialized
}

@RobotDsl
class InitContext(c: RobotContext): RobotContext(c.bot)

@RobotDsl
class RunContext(c: RobotContext): RobotContext(c.bot)

@RobotDsl
class LoopContext(c: RobotContext): RobotContext(c.bot)

@RobotDsl
class StopContext(c: RobotContext): RobotContext(c.bot)

data class OpModeContext(
    var fnInit: InitContext.() -> Unit = {},
    var fnRun: RunContext.() -> Unit = {},
    var fnLoop: LoopContext.() -> Unit = {},
    var fnStop: StopContext.() -> Unit = {},
)

inline fun opMode(b: OpModeContext.() -> Unit): OpModeContext =
        OpModeContext().apply { this.b() }

fun OpModeContext.onInit(init: InitContext.() -> Unit): OpModeContext =
        this.apply { this.fnInit = init }

fun OpModeContext.onRun(run: RunContext.() -> Unit): OpModeContext =
        this.apply { this.fnRun = run }

fun OpModeContext.onLoop(loop: LoopContext.() -> Unit): OpModeContext =
        this.apply { this.fnLoop = loop }

fun OpModeContext.onStop(stop: StopContext.() -> Unit): OpModeContext =
        this.apply { this.fnStop = stop }