package org.firstinspires.ftc.teamcode.auto

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.DcMotor

class Scuffed : LinearOpMode() {
    override fun runOpMode() {
        val lf: DcMotor = hardwareMap.dcMotor["motorLF"]
        val lb: DcMotor = hardwareMap.dcMotor["motorLB"]
        val rf: DcMotor = hardwareMap.dcMotor["motorRF"]
        val rb: DcMotor = hardwareMap.dcMotor["motorRB"]

        waitForStart()

        lf.power = 1.0
        lb.power = 1.0
        rf.power = 1.0
        rb.power = 1.0
    }

}