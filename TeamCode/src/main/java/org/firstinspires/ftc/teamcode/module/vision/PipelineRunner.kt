package org.firstinspires.ftc.teamcode.module.vision

import com.acmerobotics.dashboard.FtcDashboard
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.teamcode.module.Robot
import org.openftc.easyopencv.OpenCvCamera
import org.openftc.easyopencv.OpenCvCameraFactory
import org.openftc.easyopencv.OpenCvCameraRotation

class PipelineRunner<T: Pipeline>(val bot: Robot, val pipeline: T, w: Int, h: Int) {
    companion object {
        inline operator fun <reified T: Pipeline> invoke(bot: Robot, w: Int = 640, h: Int = 480): PipelineRunner<T>
            = PipelineRunner(bot, T::class.java.getDeclaredConstructor(Robot::class.java, Int::class.java, Int::class.java).newInstance(bot, w, h), w, h)
    }

    val camId: Int = bot.hwmap.appContext.resources.getIdentifier("cameraMonitorViewId", "id", bot.hwmap.appContext.packageName)
    val cam: OpenCvCamera = OpenCvCameraFactory.getInstance().createWebcam(bot.hwmap.get(WebcamName::class.java, "Webcam 1"), camId)
    init {
        bot.log.addData("camId", camId)
        bot.log.addData("cam", cam)
        cam.setPipeline(pipeline)
        cam.openCameraDeviceAsync { cam.startStreaming(w, h, OpenCvCameraRotation.UPRIGHT) }
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