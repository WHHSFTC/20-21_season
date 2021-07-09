package org.firstinspires.ftc.teamcode.tele

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.teamcode.module.*

@TeleOp
class RedTele: OpMode(Mode.TELE, Alliance.RED) {
    override fun initHook() {
        //bot.wings(Wings.State.UP)
        bot.feed.height(Indexer.Height.HIGH)
        bot.feed.feed(Indexer.Shoot.PRE)
    }

    override fun startHook() {
        bot.prependActivity(Controllers.Luke(gamepad1, bot))
        bot.prependActivity(Controllers.Kaylaa(gamepad2, bot))
    }
}