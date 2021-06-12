package org.firstinspires.ftc.teamcode.switchboard.core

interface Activity {
    fun load() {}
    fun update(frame: Frame)
    fun cleanup() {}
 }