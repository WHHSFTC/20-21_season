package org.firstinspires.ftc.teamcode.module.vision

import com.acmerobotics.dashboard.FtcDashboard
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.teamcode.module.Module
import org.firstinspires.ftc.teamcode.module.Robot
import org.openftc.easyopencv.OpenCvCamera
import org.openftc.easyopencv.OpenCvCameraFactory
import org.openftc.easyopencv.OpenCvCameraRotation
import org.openftc.easyopencv.OpenCvPipeline

class PipelineRunner(val bot: Robot, val w: Int, val h: Int): Module<Pipeline> {
    val camId: Int = bot.hwmap.appContext.resources.getIdentifier("cameraMonitorViewId", "id", bot.hwmap.appContext.packageName)
    val cam: OpenCvCamera = OpenCvCameraFactory.getInstance().createWebcam(bot.hwmap.get(WebcamName::class.java, "Webcam 1"), camId)

    val stack = StackPipeline(bot, w, h)
    val ring = RingPipeline(bot, w, h)
    val wob = WobblePipeline(bot, w, h)

    override var state: Pipeline = stack
        set(value) {
            cam.setPipeline(value)
            field = value
        }

    init {
        bot.log.addData("camId", camId)
        bot.log.addData("cam", cam)
    }

    fun start() {
        cam.openCameraDeviceAsync { cam.startStreaming(w, h, OpenCvCameraRotation.UPRIGHT) }
        FtcDashboard.getInstance().startCameraStream(cam, 20.0);
        bot.log.addLine("Camera initialized")
        bot.log.update()
    }

    fun load(pipeline: OpenCvPipeline) {
        cam.setPipeline(pipeline)
    }

    fun halt() {
        cam.stopStreaming()
        //cam.closeCameraDevice()
        FtcDashboard.getInstance().stopCameraStream()
    }
}