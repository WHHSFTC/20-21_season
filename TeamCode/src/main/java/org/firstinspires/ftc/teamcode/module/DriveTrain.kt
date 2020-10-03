package org.firstinspires.ftc.teamcode.module

import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.teamcode.module.delegate.Module

class DriveTrain(val opmode: OpMode) {
    data class Position(
        var x: Double = .0,
        var y: Double = .0,
        var theta: Double = .0
    )

    var position: Position by Module<Position> (getter = {position}, setter = { v ->
        // fancy shit
        position = v
    })

    val lf: DcMotor = opmode.hardwareMap.dcMotor["motorLF"]
    val lb: DcMotor = opmode.hardwareMap.dcMotor["motorLB"]
    val rf: DcMotor = opmode.hardwareMap.dcMotor["motorRF"]
    val rb: DcMotor = opmode.hardwareMap.dcMotor["motorRB"]

    var zeroPowerBehavior: DcMotor.ZeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        set(value) {
            lf.zeroPowerBehavior = value
            lb.zeroPowerBehavior = value
            rf.zeroPowerBehavior = value
            rb.zeroPowerBehavior = value
            field = value
        }

    //var powers: Powers by Module<Powers> (getter = {powers}, setter = { v ->
        //opmode.telemetry.addData("Powers", "%d, %d, %d, %d", v.rf, v.lf, v.lb, v.rb)
        //lf.power = v.lf
        //lb.power = v.lb
        //rf.power = v.rf
        //rb.power = v.rb
        //powers = v
    //})

    var powers: Powers = Powers()
        set(v) {
            opmode.telemetry.addData("Powers", "%f, %f, %f, %f", v.rf, v.lf, v.lb, v.rb)
            lf.power = v.lf
            lb.power = v.lb
            rf.power = v.rf
            rb.power = v.rb
            field = v
        }

    init {
        zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
    }

    data class Powers(
        var lf: Double = .0,
        var rf: Double = .0,
        var lb: Double = .0,
        var rb: Double = .0
    )
}