package org.firstinspires.ftc.teamcode.fsm

import org.firstinspires.ftc.teamcode.module.OpMode
import org.firstinspires.ftc.teamcode.module.Robot

typealias State = Robot.() -> String

class MachineDSL(val map: MutableMap<String, State> = HashMap()): OpMode(), MutableMap<String, State> by map {
    private var fnInit: Robot.() -> Unit = {}
    private var fnRun: Robot.() -> Unit = {}
    private var fnLoop: Robot.() -> Unit = {}
    private var fnStop: Robot.() -> Unit = {}
    override fun onInit() {bot.fnInit()}
    override fun onRun() {bot.fnRun()}
    override fun onLoop() {bot.fnLoop()}
    override fun onStop() {bot.fnStop()}

    // dsl setters
    fun onInit(f: Robot.() -> Unit): MachineDSL {fnInit = f; return this}
    fun onRun(f: Robot.() -> Unit): MachineDSL {fnRun = f; return this}
    fun onLoop(f: Robot.() -> Unit): MachineDSL {fnLoop = f; return this}
    fun onStop(f: Robot.() -> Unit): MachineDSL {fnStop = f; return this}
    operator fun String.invoke(state: State): MachineDSL {
        put(this, state)
        return this@MachineDSL
    }

    fun enter(initkey: String) {
        //bot.telemetry.addLine("State Machine: Running $initkey")
        bot.log.addLine("State Machine: Running $initkey")
        var state: State? = map[initkey]
        var key: String = initkey
        while (state != null) {
            key = bot.state()
            bot.log.addLine("State Machine: Running $key")
            state = map[key]
        }
        bot.log.addLine("State Machine: Exited at $key")
    }
}
