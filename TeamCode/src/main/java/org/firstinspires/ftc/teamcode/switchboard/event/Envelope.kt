package org.firstinspires.ftc.teamcode.switchboard.event

import org.firstinspires.ftc.teamcode.switchboard.scheduler.Activity
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time

class Envelope<out E : Event>(val event: E, val sender: Activity, val cycle: Long, val time: Time = Time.now())
