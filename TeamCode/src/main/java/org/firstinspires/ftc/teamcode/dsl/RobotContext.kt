package org.firstinspires.ftc.teamcode.dsl

import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.module.Robot

@RobotDsl
data class OpModeContext(
        var telemetry: Telemetry? = null,
        var fnInit: Robot.() -> Unit = {},
        var fnRun: Robot.() -> Unit = {},
        var fnLoop: Robot.() -> Unit = {},
        var fnStop: Robot.() -> Unit = {},
) {
    enum class Context {
        BASE,
        INIT,
        RUN,
        LOOP,
        STOP;

        override fun toString(): String = "[${this.name}]"
    }
}

inline fun opMode(b: OpModeContext.() -> Unit): OpModeContext =
        OpModeContext().apply { this.b() }

fun OpModeContext.onInit(init: Robot.() -> Unit): OpModeContext =
        this.apply {
            this.fnInit = init
        }

fun OpModeContext.onRun(run: Robot.() -> Unit): OpModeContext =
        this.apply {
            this.fnRun = run
        }

fun OpModeContext.onLoop(loop: Robot.() -> Unit): OpModeContext =
        this.apply {
            this.fnLoop = loop
        }

fun OpModeContext.onStop(stop: Robot.() -> Unit): OpModeContext =
        this.apply {
            this.fnStop = stop
        }
