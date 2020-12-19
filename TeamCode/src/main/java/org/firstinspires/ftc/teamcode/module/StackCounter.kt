package org.firstinspires.ftc.teamcode.module

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.openftc.easyopencv.OpenCvCamera
import org.openftc.easyopencv.OpenCvCameraFactory
import org.openftc.easyopencv.OpenCvCameraRotation

class StackCounter(val bot: Robot) {
    val camId: Int = bot.hwmap.appContext.resources.getIdentifier("cameraMonitorViewId", "id", bot.hwmap.appContext.packageName)
    val cam: OpenCvCamera = OpenCvCameraFactory.getInstance().createWebcam(bot.hwmap.get(WebcamName::class.java, "Webcam 1"), camId)
    val pipeline: VisionPipeline = VisionPipeline(bot.log, 320, 240)
    init {
        bot.log.addData("camId", camId)
        bot.log.addData("cam", cam)
        cam.setPipeline(pipeline)
        cam.openCameraDeviceAsync { cam.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT) }
        FtcDashboard.getInstance().startCameraStream(cam, 20.0);
        bot.log.addLine("Vision initialized")
        bot.log.update()
        //bot.opMode.waitForStart()
    }
    val height: VisionPipeline.Height
        get() {
            return pipeline.height
        }
    fun halt() {
        cam.stopStreaming()
        cam.closeCameraDevice()
    }
}