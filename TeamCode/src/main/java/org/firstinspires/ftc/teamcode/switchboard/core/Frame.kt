package org.firstinspires.ftc.teamcode.switchboard.core

import org.firstinspires.ftc.teamcode.switchboard.shapes.Time

data class Frame(val n: Long, val time: Time = Time.now())
