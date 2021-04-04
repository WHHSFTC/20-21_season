package org.firstinspires.ftc.teamcode.switchboard.event

import org.firstinspires.ftc.teamcode.switchboard.scheduler.Activity

class Envelope(val event: Event, val sender: Activity, val cycle: Long, val nanoseconds: Long)
