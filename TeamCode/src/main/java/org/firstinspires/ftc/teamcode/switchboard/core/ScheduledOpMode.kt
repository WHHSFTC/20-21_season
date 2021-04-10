package org.firstinspires.ftc.teamcode.switchboard.core

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.switchboard.scheduler.Schedule
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time

abstract class ScheduledOpMode(val displayDebug: Boolean) : OpMode() {
    lateinit var ctx: OpModeContext
    private lateinit var switchboard: Switchboard

    var initTime: Time = Time.zero
        private set
    var startTime: Time = Time.zero
        private set

    abstract fun makeSchedule(): Schedule

    override fun init() {
        initTime = Time.now()

        ctx = OpModeContext(telemetry, hardwareMap)

        switchboard = Switchboard(makeSchedule())

        ctx.log["!lifecycle"] = "INIT"
        ctx.log["!time"] = Time.zero

        switchboard.init()
    }

    override fun start() {
        startTime = Time.now()
        ctx.log["!lifecycle"] = "RUN"
    }

    override fun loop() {
        switchboard.update()
        ctx.log["!time"] = Time.now() - startTime
        ctx.log.update()
    }

    override fun stop() {
        switchboard.cleanup()
    }
}