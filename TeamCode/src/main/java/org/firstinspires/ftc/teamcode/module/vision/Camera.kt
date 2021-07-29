package org.firstinspires.ftc.teamcode.module.vision

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.roadrunner.geometry.Pose2d
import org.opencv.core.Mat
import org.openftc.easyopencv.OpenCvCamera
import org.openftc.easyopencv.OpenCvCameraRotation
import org.openftc.easyopencv.OpenCvPipeline

class Camera(private val cam: OpenCvCamera, val pose: Pose2d, val height: Double, val verticalAngle: Double = 0.0, val trigger: () -> Boolean) {
    private val pipelines: MutableList<Pipeline> = mutableListOf()
    private val views: MutableList<View> = mutableListOf()
    private var i = -1
    private var input: Mat? = null

    private val pipelineSplitter = object : OpenCvPipeline() {
        override fun init(mat: Mat?) {
            input = mat!!
            pipelines.forEach { mat.copyTo(it.input); it.initialize(this@Camera) }
        }

        override fun processFrame(mat: Mat?): Mat {
            if (mat == null) return Mat()
            pipelines.forEach { mat.copyTo(it.input); it.processFrame(this@Camera) }
            checkButton()
            return if (i in 0..views.size) views[i].labeledMat else mat
        }

        override fun onViewportTapped() {
            if (i >= views.size - 1)
                i = -1
            else
                i++
        }

        private var last = false
        fun checkButton() {
            val s = trigger()
            if (s && !last)
                onViewportTapped()
            last = s
        }
    }

    init {
        cam.setPipeline(pipelineSplitter)
    }

    fun addPipeline(p: Pipeline) {
        if (pipelines.isEmpty()) {
            startStreaming()
        }
        if (p !in pipelines) {
            pipelines.add(p)
            input?.let { it.copyTo(p.input); p.initialize(this) }
        }
    }

    fun removePipeline(p: Pipeline) {
        if (p in pipelines) {
            pipelines.remove(p)
            if (pipelines.isEmpty()) {
                stopStreaming()
            }
        }
    }

    fun addView(v: View) {
        if (v !in views)
            views.add(v)
    }

    fun removeView(v: View) {
        views.remove(v)
    }

    private fun startStreaming() {
        //cam.openCameraDevice();
        cam.openCameraDeviceAsync { cam.startStreaming(Vision.FRAME_WIDTH, Vision.FRAME_HEIGHT, OpenCvCameraRotation.UPRIGHT) }
    }

    private fun stopStreaming() {
        //cam.stopStreaming()
    }

    fun startDebug() {
        FtcDashboard.getInstance().startCameraStream(cam, 10.0);
    }

    fun stopDebug() {
        FtcDashboard.getInstance().stopCameraStream()
    }
}