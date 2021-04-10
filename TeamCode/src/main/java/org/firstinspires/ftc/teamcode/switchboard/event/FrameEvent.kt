package org.firstinspires.ftc.teamcode.switchboard.event

import org.firstinspires.ftc.teamcode.switchboard.shapes.Time

data class FrameEvent(val cycle: Long, val time: Time, val step: Time) : Event
