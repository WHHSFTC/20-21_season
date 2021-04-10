package org.firstinspires.ftc.teamcode.switchboard.core

abstract class Robot(val log: Log, val config: Config, val name: String) {
    override fun toString(): String = name
}