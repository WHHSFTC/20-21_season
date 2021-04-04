package org.firstinspires.ftc.teamcode.switchboard.core

import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time

class Log(
        val telemetry: Telemetry,

        // true if all hardware values should be logged to telemetry
        var displayDebug: Boolean = false,

        // map of state for telemetry to be updated each cycle
        val data: MutableMap<String, Any> = hashMapOf()
) : MutableMap<String, Any> by data {

    val debug: MutableMap<String, Any> = hashMapOf()

    // list of one-off messages to display
    val messages: MutableList<Pair<String, Time>> = mutableListOf()

    fun addMessage(text: String, duration: Time) {
        messages += Pair(text, Time.now() + duration)
    }

    fun update() {
        val now = Time.now()
        messages.removeIf { it.second < now }
        data.forEach { (k, v) -> telemetry.addData(k, v) }
        if (displayDebug)
            debug.forEach { (k, v) -> telemetry.addData(k, v) }
        telemetry.update()
    }
}