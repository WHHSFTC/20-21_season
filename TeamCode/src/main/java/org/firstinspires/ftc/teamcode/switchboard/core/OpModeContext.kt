package org.firstinspires.ftc.teamcode.switchboard.core

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.switchboard.event.Envelope
import org.firstinspires.ftc.teamcode.switchboard.scheduler.Activity
import org.firstinspires.ftc.teamcode.switchboard.shapes.BackingList

class OpModeContext(telemetry: Telemetry, hardwareMap: HardwareMap) {
    val eventBus: BackingList<Envelope<*>> = BackingList<Envelope<*>>()
    val log: Log = Log(telemetry)
    val config: Config = Config(hardwareMap, log)
    val activities: MutableList<Activity> = mutableListOf()

    fun update() {
        activities.forEach { it.update() }
        log.update()
        eventBus.refresh()
    }
}
