package org.firstinspires.ftc.teamcode.tele

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.module.*

@TeleOp
class RedTele: OpMode(Mode.TELE, Alliance.RED) {
    override fun startHook() {
        bot.prependActivity(Controllers.Luke(gamepad1, bot))
        bot.prependActivity(Controllers.Kaylaa(gamepad2, bot))
        bot.feed.feed(Indexer.Shoot.PRE)
    }
}