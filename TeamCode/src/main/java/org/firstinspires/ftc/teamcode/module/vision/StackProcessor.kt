package org.firstinspires.ftc.teamcode.module.vision

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.acmerobotics.roadrunner.geometry.Vector2d
import org.firstinspires.ftc.teamcode.module.Alliance
import org.firstinspires.ftc.teamcode.module.OpMode.Companion.DEBUG
import org.firstinspires.ftc.teamcode.module.Summum
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.tan

class StackProcessor(val bot: Summum, val leftCam: Camera, val rightCam: Camera) {
    private val dashboard = FtcDashboard.getInstance()
    private val pipe: StackPipeline = StackPipeline(bot)
    val cam = listOf(leftCam, rightCam).sortedBy { (bot.opMode.startPose.vec() + it.pose.vec()) distTo bot.alliance.stack }.first()
    var running = false
        private set

    fun start() {
        if (!running) {
            cam.addPipeline(pipe)
            if (DEBUG)
                cam.startDebug()
        }
        running = true
    }

    fun stop() {
        if (running) {
            cam.removePipeline(pipe)
            if (DEBUG)
                cam.stopDebug()
        }
        running = false
    }

    val height: StackPipeline.Height
        get() = pipe.height

    class StackPipeline(bot: Summum): Pipeline {
        var height: Height = Height.ZERO
        val telemetry = bot.logger
        override val input = View("Stack - Input", Scalar(255.0, 0.0, 0.0))
        val yCrCb = View("Stack - YCrCb", Scalar(255.0, 0.0, 0.0))
        val mask = View("Stack - Mask", Scalar(255.0, 0.0, 0.0))
        val ret = View("Stack - Contours", Scalar(255.0, 0.0, 0.0))
        val hierarchy = Mat()

        enum class Height {
            ZERO, ONE, FOUR
        }

        @Config
        object StackConstants {
            @JvmField var lY = 0.0
            @JvmField var lR = 141.0
            @JvmField var lB = 0.0
            @JvmField var uY = 255.0
            @JvmField var uR = 170.0
            @JvmField var uB = 110.0
            @JvmField var MIN_WIDTH = .05
            @JvmField var FAR_MIN_WIDTH = .05
            @JvmField var BOUND_RATIO = 0.7
            @JvmField var HORIZON = .5
        }

        companion object {
            val lowerOrange get() = Scalar(StackConstants.lY, StackConstants.lR, StackConstants.lB)
            val upperOrange get() = Scalar(StackConstants.uY, StackConstants.uR, StackConstants.uB)
        }

        val horizon get() = (Vision.FRAME_HEIGHT * StackConstants.HORIZON).toInt()
        val minWidth get() = (Vision.FRAME_WIDTH * StackConstants.MIN_WIDTH).toInt()

        val target = Rect(
            Point(

            ),
            Point(

            )
        )

        override fun initialize(camera: Camera) {
            camera.addView(yCrCb)
            camera.addView(mask)
            camera.addView(ret)
            //camera.addView(input)
        }

        override fun processFrame(camera: Camera) {
            try {
//            mat.release()

                /**converting from RGB color space to HSV color space**/
                Imgproc.cvtColor(input, yCrCb, Imgproc.COLOR_RGB2YCrCb)

                /**checking if any pixel is within the yellow bounds to make a black and white mask**/
                // val mask = Mat(yCrCb.rows(), yCrCb.cols(), CvType.CV_8UC1)
                Core.inRange(yCrCb, lowerOrange, upperOrange, mask)

                /**applying to input and putting it on ret in black or yellow**/
                ret.setTo(Scalar(0.0, 0.0, 0.0))
                Core.bitwise_and(input, input, ret, mask)

                Imgproc.GaussianBlur(mask, mask, Size(5.0, 15.0), 0.00);

                /**finding contours on mask**/
                val contours: List<MatOfPoint> = ArrayList()
                //val hierarchy = Mat()
                Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE)

                Imgproc.drawContours(ret, contours, -1, Scalar(0.0, 255.0, 0.0), 3)

                /**finding widths of each contour**/
                //var widths: MutableList<Int> = ArrayList()
                //var maxC: MatOfPoint
                var maxW: Int = 0
                var maxR: Rect = Rect()
                for (c: MatOfPoint in contours) {
                    val copy = MatOfPoint2f(*c.toArray())
                    val rect: Rect = Imgproc.boundingRect(copy)

                    val w = rect.width
                    if (w > maxW && rect.y + rect.height > horizon /*&& rect within target*/) {
                        //maxC = c;
                        maxW = w;
                        maxR = rect;
                    }
                    c.release()
                    copy.release()
                }

                //maxC.release()

                Imgproc.rectangle(ret, maxR, Scalar(0.0, 0.0, 255.0), 2)
                Imgproc.line(ret, Point(.0, horizon.toDouble()), Point(Vision.FRAME_HEIGHT.toDouble(), horizon.toDouble()), Scalar(255.0, .0, 255.0))

                telemetry.err["Vision: maxW"] = maxW
                height = if (maxW >= minWidth) {
                    val aspectRatio: Double = maxR.height.toDouble() / maxR.width.toDouble()
                    telemetry.err["Vision: Aspect Ratio"] = aspectRatio
                    if (aspectRatio > StackConstants.BOUND_RATIO) Height.FOUR else Height.ONE
                } else {
                    Height.ZERO
                }

                telemetry.out["Vision: Height"] = height

                // yCrCb.release()
                // mask.release()
                //hierarchy.release()
            } catch (e: Exception) {
                /**error handling, prints stack trace for specific debug**/
                telemetry.err["[VISION ERROR]"] = e
                e.stackTrace.toList().stream().forEach { x -> telemetry.addMessage(x.toString(), Time.seconds(60)) }
            }
            telemetry.update()

            /**returns the black and yellow mask with contours drawn to see logic in action**/
            // return ret
        }
    }
}