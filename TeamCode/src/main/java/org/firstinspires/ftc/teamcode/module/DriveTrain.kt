package org.firstinspires.ftc.teamcode.module

import com.qualcomm.robotcore.hardware.DcMotor

class DriveTrain(val bot: Robot) {
    data class Position(
        var x: Double = .0,
        var y: Double = .0,
        var theta: Double = .0
    )

    var position: Position = Position()
        set(value) {
            bot.log.addData("Position: ", value)
            field = value
        }

    val lf: DcMotor = bot.hwmap.dcMotor["motorLF"]
    val lb: DcMotor = bot.hwmap.dcMotor["motorLB"]
    val rf: DcMotor = bot.hwmap.dcMotor["motorRF"]
    val rb: DcMotor = bot.hwmap.dcMotor["motorRB"]

    var zeroPowerBehavior: DcMotor.ZeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        set(value) {
            lf.zeroPowerBehavior = value
            lb.zeroPowerBehavior = value
            rf.zeroPowerBehavior = value
            rb.zeroPowerBehavior = value
            field = value
        }

    var powers: Powers = Powers()
        set(v) {
            bot.log.addData("Powers", "%f, %f, %f, %f", v.rf, v.lf, v.lb, v.rb)
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
