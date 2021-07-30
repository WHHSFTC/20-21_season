package org.firstinspires.ftc.teamcode.module.vision

import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.roadrunner.geometry.Pose2d
import org.firstinspires.ftc.teamcode.module.OpMode.Companion.DEBUG
import org.firstinspires.ftc.teamcode.module.Summum
import org.firstinspires.ftc.teamcode.switchboard.core.Activity
import org.firstinspires.ftc.teamcode.switchboard.core.Frame
import org.openftc.easyopencv.OpenCvCameraFactory
import org.openftc.easyopencv.OpenCvCameraRotation

@Config
class Vision(val bot: Summum) : Activity {
    val id: Int = bot.hwmap.appContext.resources.getIdentifier("cameraMonitorViewId", "id", bot.hwmap.appContext.packageName)
    val subIds = OpenCvCameraFactory.getInstance().splitLayoutForMultipleViewports(id, 2, OpenCvCameraFactory.ViewportSplitMethod.HORIZONTALLY)

    val leftCam = Camera(
        OpenCvCameraFactory.getInstance().createWebcam(bot.config.webcamNames["leftCam"], subIds[0]),
        Pose2d(Summum.LENGTH/2.0, 13.125/2.0, 0.0),
        7.0,
        0.0,
        bot.opMode.gamepad1::left_bumper,
        OpenCvCameraRotation.UPSIDE_DOWN
    )
    val rightCam = Camera(
        OpenCvCameraFactory.getInstance().createWebcam(bot.config.webcamNames["rightCam"], subIds[1]),
        Pose2d(Summum.LENGTH/2.0, -13.125/2.0, 0.0),
        7.0,
        0.0,
        bot.opMode.gamepad1::right_bumper,
        OpenCvCameraRotation.UPRIGHT
    )

    val stack = StackProcessor(bot, leftCam, rightCam)
    val rings = RingProcessor(bot, leftCam, rightCam)

    override fun update(frame: Frame) { }

    override fun load() {
        //if (DEBUG)
            //rightCam.startDebug()
    }

    override fun cleanup() {
        //if (DEBUG)
            //rightCam.stopDebug()
        stack.stop()
        rings.stop()
    }

    companion object {
        @JvmField var FRAME_WIDTH: Int = 640
        @JvmField var FRAME_HEIGHT: Int = 480
    }
}