package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.hardware.DcMotor

class DriveTrain(val opmode: OpMode) {
    inner class Position(
        var x: Double = .0,
        var y: Double = .0,
        var theta: Double = .0
    )

    var position: Position by Module<Position> (getter = {position}, setter = {v ->
        // fancy shit
        position = v
    })

    var powers: Powers by Module<Powers> (getter = {powers}, setter = {v ->
        lf.power = v.lf
        lb.power = v.lb
        rf.power = v.rf
        rb.power = v.rb
        powers = v
    })

    val lf: DcMotor = opmode.hardwareMap.dcMotor["motorLF"]
    val lb: DcMotor = opmode.hardwareMap.dcMotor["motorLB"]
    val rf: DcMotor = opmode.hardwareMap.dcMotor["motorRF"]
    val rb: DcMotor = opmode.hardwareMap.dcMotor["motorRB"]

    data class Powers(
        var lf: Double = .0,
        var rf: Double = .0,
        var lb: Double = .0,
        var rb: Double = .0
    )
}