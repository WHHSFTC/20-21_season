package org.firstinspires.ftc.teamcode.module.vision

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.module.Robot
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.openftc.easyopencv.OpenCvPipeline
import kotlin.math.min

class StackPipeline(bot: Robot, val cwidth: Int, val cheight: Int): Pipeline() {
    var height: Height = Height.ZERO
    var telemetry: Telemetry = bot.log
    var mat: Mat
    var ret: Mat

    enum class Height {
        ZERO, ONE, FOUR
    }

    @Config
    object StackConstants {
        @JvmField var lY = 0.0
        @JvmField var lR = 141.0
        @JvmField var lB = 0.0
        @JvmField var uY = 255.0
        @JvmField var uR = 230.0
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

    val horizon get() = (cheight * StackConstants.HORIZON).toInt()
    val minWidth get() = (cwidth * StackConstants.MIN_WIDTH).toInt()

    /**
     * default init call, constructor
     */
    init {
        ret = Mat()
        mat = Mat()
    }


    override fun processFrame(input: Mat?): Mat {
        ret.release()
        ret = Mat()
        try {
//            mat.release()

            /**converting from RGB color space to HSV color space**/
            Imgproc.cvtColor(input, mat, Imgproc.COLOR_RGB2YCrCb)

            /**checking if any pixel is within the yellow bounds to make a black and white mask**/
            val mask = Mat(mat.rows(), mat.cols(), CvType.CV_8UC1)
            Core.inRange(mat, lowerOrange, upperOrange, mask)

            /**applying to input and putting it on ret in black or yellow**/
            Core.bitwise_and(input, input, ret, mask)

            Imgproc.GaussianBlur(mask, mask, Size(5.0, 15.0), 0.00);

            /**finding contours on mask**/
            val contours: List<MatOfPoint> = ArrayList()
            val hierarchy = Mat()
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
                if (w > maxW && rect.y + rect.height > horizon) {
                    //maxC = c;
                    maxW = w;
                    maxR = rect;
                }
                c.release()
                copy.release()
            }

            //maxC.release()

            Imgproc.rectangle(ret, maxR, Scalar(0.0, 0.0, 255.0), 2)
            Imgproc.line(ret, Point(.0, horizon.toDouble()), Point(cwidth.toDouble(), horizon.toDouble()), Scalar(255.0, .0, 255.0))

            telemetry.addData("Vision: maxW", maxW)
            height = if (maxW >= minWidth) {
                val aspectRatio: Double = maxR.height.toDouble() / maxR.width.toDouble()
                telemetry.addData("Vision: Aspect Ratio", aspectRatio)
                if (aspectRatio > StackConstants.BOUND_RATIO) Height.FOUR else Height.ONE
            } else {
                Height.ZERO
            }

            telemetry.addData("Vision: Height", height)

            mat.release()
            mask.release()
            hierarchy.release()
        } catch (e: Exception) {
            /**error handling, prints stack trace for specific debug**/
            telemetry.addData("[ERROR]", e)
            e.stackTrace.toList().stream().forEach { x -> telemetry.addLine(x.toString()) }
        }
        telemetry.update()

        /**returns the black and yellow mask with contours drawn to see logic in action**/
        return ret
    }
}
