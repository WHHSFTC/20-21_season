package org.firstinspires.ftc.teamcode.summum

import org.firstinspires.ftc.teamcode.geometry.Pose2d
import org.firstinspires.ftc.teamcode.geometry.rad
import org.firstinspires.ftc.teamcode.switchboard.core.OpModeContext

class SummumLocalizer(ctx: OpModeContext) : DeadWheelLocalizer(
        odos = listOf(
                ctx.config.encoders["leftOdo"] to Pose2d(0.0, 8.0, 0.0),
                ctx.config.encoders["rightOdo"] to Pose2d(0.0, -8.0, 0.0),
                ctx.config.encoders["backOdo"] to Pose2d(-4.0, -8.0, 90.0.rad())
        ),
        ctx
) {
}