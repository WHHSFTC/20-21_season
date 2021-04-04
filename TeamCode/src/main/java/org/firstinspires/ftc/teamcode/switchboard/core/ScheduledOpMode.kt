package org.firstinspires.ftc.teamcode.switchboard.core

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.teamcode.switchboard.scheduler.Schedule
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time

abstract class ScheduledOpMode<R: Robot>(displayDebug: Boolean, val robotSupplier: (Log, Config) -> R, val scheduleSupplier: R.() -> Schedule) : OpMode() {
    val log: Log = Log(telemetry, displayDebug)
    lateinit var robot: R
    lateinit var config: Config
    lateinit var switchboard: Switchboard

    var initTime: Time = Time.zero
        private set
    var startTime: Time = Time.zero
        private set

    override fun init() {
        initTime = Time.now()
        config = Config(hardwareMap, log)
        robot = robotSupplier(log, config)
        switchboard = Switchboard(robot.scheduleSupplier())

        log["!lifecycle"] = "INIT"
        log["!time"] = Time.zero
        log["!robot"] = robot

        switchboard.init()
    }

    override fun init_loop() {
    }

    override fun start() {
        startTime = Time.now()
        log["!lifecycle"] = "RUN"
    }

    override fun loop() {
        switchboard.update()
        log["!time"] = Time.now() - startTime
        log.update()
    }

    override fun stop() {
        switchboard.cleanup()
    }
}