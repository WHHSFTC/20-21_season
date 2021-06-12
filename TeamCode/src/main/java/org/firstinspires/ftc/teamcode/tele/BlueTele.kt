package org.firstinspires.ftc.teamcode.tele

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.module.*

@TeleOp
class BlueTele: OpMode(Mode.TELE, Alliance.BLUE) {
    override fun startHook() {
        bot.prependActivity(Controllers.Andrew(gamepad1, bot))
        bot.prependActivity(Controllers.Adham(gamepad2, bot))
    }
}