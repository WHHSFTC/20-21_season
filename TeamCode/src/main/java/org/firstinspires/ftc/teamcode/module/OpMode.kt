package org.firstinspires.ftc.teamcode.module

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.teamcode.fsm.emptyMachine
import org.firstinspires.ftc.teamcode.module.Robot

abstract class OpMode : LinearOpMode() {
    lateinit var bot: Robot
    override fun runOpMode() {
        bot = Robot(this, emptyMachine())
        onInit()
        waitForStart()
        onRun()
        while (opModeIsActive()) onLoop()
        onStop()
    }

    abstract fun onInit()
    abstract fun onRun()
    abstract fun onLoop()
    abstract fun onStop()
}
