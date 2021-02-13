package org.firstinspires.ftc.teamcode.module.rings

import com.acmerobotics.dashboard.FtcDashboard
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.teamcode.module.Robot
import org.firstinspires.ftc.teamcode.module.VisionPipeline
import org.openftc.easyopencv.OpenCvCamera
import org.openftc.easyopencv.OpenCvCameraFactory
import org.openftc.easyopencv.OpenCvCameraRotation

class RingFinder(val bot: Robot) {
    val camId: Int = bot.hwmap.appContext.resources.getIdentifier("cameraMonitorViewId", "id", bot.hwmap.appContext.packageName)
    val cam: OpenCvCamera = OpenCvCameraFactory.getInstance().createWebcam(bot.hwmap.get(WebcamName::class.java, "Webcam 1"), camId)
    val pipeline: Pipeline = Pipeline(bot, bot.log, 640, 480)
    init {
        bot.log.addData("camId", camId)
        bot.log.addData("cam", cam)
        cam.setPipeline(pipeline)
        cam.openCameraDeviceAsync { cam.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT) }
        FtcDashboard.getInstance().startCameraStream(cam, 20.0);
        bot.log.addLine("Vision initialized")
        bot.log.update()
        //bot.opMode.waitForStart()
    }
    fun halt() {
        cam.stopStreaming()
        cam.closeCameraDevice()
    }
}