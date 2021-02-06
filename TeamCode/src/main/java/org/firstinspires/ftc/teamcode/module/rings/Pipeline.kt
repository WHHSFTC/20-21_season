package org.firstinspires.ftc.teamcode.module.rings

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.openftc.easyopencv.OpenCvPipeline
import kotlin.math.*


class Pipeline(tl: Telemetry? = null, val cwidth: Int, val cheight: Int): OpenCvPipeline() {
    var telemetry: Telemetry
    var mat: Mat
    var ret: Mat

    var alpha: Double
    var distance: Double

    @Config
    object RingConstants {
        @JvmField var lY = 0.0
        @JvmField var lR = 141.0
        @JvmField var lB = 0.0
        @JvmField var uY = 255.0
        @JvmField var uR = 230.0
        @JvmField var uB = 110.0
        @JvmField var MIN_WIDTH = 35
        @JvmField var HORIZON = 80
        @JvmField var DFOV = 1.3881
        @JvmField var FOCAL_SCALAR = 0.7
        const val RING_RADIUS = 5.0
    }

    val focalLength: Double get() = RingConstants.FOCAL_SCALAR * hypot(cwidth.toDouble(), cheight.toDouble()) / 2.0 / tan(RingConstants.DFOV / 2.0)

    companion object {
        val lowerOrange get() = Scalar(RingConstants.lY, RingConstants.lR, RingConstants.lB)
        val upperOrange get() = Scalar(RingConstants.uY, RingConstants.uR, RingConstants.uB)
    }

    /**
     * default init call, constructor
     */
    init {
        telemetry = tl!!
        ret = Mat()
        mat = Mat()
        alpha = 0.0
        distance = 0.0
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
                if (w > maxW && rect.y + rect.height > RingConstants.HORIZON) {
                    //maxC = c;
                    maxW = w;
                    maxR = rect;
                }
                c.release()
                copy.release()
            }

            //maxC.release()

            Imgproc.rectangle(ret, maxR, Scalar(0.0, 0.0, 255.0), 2)
            Imgproc.line(ret, Point(.0, RingConstants.HORIZON.toDouble()), Point(cwidth.toDouble(), RingConstants.HORIZON.toDouble()), Scalar(255.0, .0, 255.0))

            telemetry.addData("Vision: maxW", maxW)

            //fun ctransform(x: Int, y: Int): Pair<Int, Int> = Pair(x - cwidth / 2, cheight / 2 - y)
            fun theta(n: Int): Double = atan2(n.toDouble(), focalLength)
            fun delta(n1: Int, n2: Int): Double = theta(n2) - theta(n1)
            fun estimateDistance(da: Double): Double = RingConstants.RING_RADIUS / sin(da/2.0)

            if (maxR.width > RingConstants.MIN_WIDTH) {
                alpha = theta(maxR.x + (maxR.width - cwidth) / 2)
                val da = delta(maxR.x - cwidth / 2, maxR.x + maxR.width - cwidth / 2)
                distance = estimateDistance(da)
            } else {
                alpha = 0.0
                distance = 0.0
            }

            telemetry.addData("alpha", alpha)
            telemetry.addData("dist", distance)

            telemetry.addData("x", distance * cos(alpha))
            telemetry.addData("y", distance * sin(alpha))

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
