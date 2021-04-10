package org.firstinspires.ftc.teamcode.switchboard.core

import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time

class Logger(
        val telemetry: Telemetry,

        // true if all hardware values should be logged to telemetry
        var displayDebug: Boolean = false,
) {
    // map of state for telemetry to be updated each cycle
    val out = LogStream("out")
    val err = LogStream("err")

    // list of one-off messages to display
    val messages: MutableList<Pair<String, Time>> = mutableListOf()
    fun addMessage(text: String, duration: Time) {
        messages += text to Time.now() + duration
    }

    fun update() {
        val now = Time.now()
        messages.removeIf { it.second < now }

        telemetry.addLine("messages")
        telemetry.addLine("---")
        messages.forEach { telemetry.addLine(it.first) }

        out.print()

        if (displayDebug)
            err.print()

        telemetry.update()
    }

    inner class LogStream(
            val name: String,
            val mutableMap: MutableMap<String, Any?> = mutableMapOf()
    ): MutableMap<String, Any?> by mutableMap {

        fun print() {
            telemetry.addLine(name)
            telemetry.addLine("---")
            mutableMap.forEach { (k, v) -> telemetry.addData(k, v) }
        }
    }
}