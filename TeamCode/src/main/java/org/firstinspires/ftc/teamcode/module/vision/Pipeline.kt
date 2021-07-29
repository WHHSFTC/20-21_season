package org.firstinspires.ftc.teamcode.module.vision

import org.opencv.core.Mat

interface Pipeline {
    val input: Mat
    fun processFrame(camera: Camera)
    fun initialize(camera: Camera) { }
}

