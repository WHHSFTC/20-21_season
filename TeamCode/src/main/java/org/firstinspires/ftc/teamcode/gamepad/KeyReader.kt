package org.firstinspires.ftc.teamcode.gamepad

interface KeyReader {
    fun readValue()

    fun isDown(): Boolean

    fun wasJustPressed(): Boolean

    fun wasJustReleased(): Boolean

    fun stateJustChanged(): Boolean
}