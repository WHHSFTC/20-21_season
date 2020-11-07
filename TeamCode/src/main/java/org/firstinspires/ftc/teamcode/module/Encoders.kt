package org.firstinspires.ftc.teamcode.module

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx

data class Encoders(val left: DcMotorEx, val right: DcMotorEx, val back: DcMotorEx) {
    constructor(bot: Robot) : this(
            left = bot.hwmap.get(DcMotorEx::class.java, "motorLF"),
            right = bot.hwmap.get(DcMotorEx::class.java, "motorRF"),
            back = bot.hwmap.get(DcMotorEx::class.java, "motorRB"),
    )
}
