package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode

abstract class OpMode : LinearOpMode() {
    lateinit var bot: Robot;
    override fun runOpMode() {
        bot = Robot(this)
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