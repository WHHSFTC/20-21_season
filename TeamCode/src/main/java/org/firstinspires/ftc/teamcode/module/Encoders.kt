package org.firstinspires.ftc.teamcode.module

import com.qualcomm.robotcore.hardware.DcMotor

data class Encoders(val left: DcMotor, val right: DcMotor, val back: DcMotor) {
    constructor(bot: Robot) : this(bot.hwmap.dcMotor["odoL"], bot.hwmap.dcMotor["odoR"], bot.hwmap.dcMotor["odoB"])
}
