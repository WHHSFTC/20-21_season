package org.firstinspires.ftc.teamcode.module

import com.acmerobotics.dashboard.config.Config
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.openftc.easyopencv.OpenCvPipeline


class VisionPipeline(tl: Telemetry? = null): OpenCvPipeline() {
    var height: Height
    var telemetry: Telemetry
    var mat: Mat
    var ret: Mat

    enum class Height {
        ZERO, ONE, FOUR
    }

    @Config
    object VisionConstants {
        @JvmField var lY = 0.0
        @JvmField var lR = 141.0
        @JvmField var lB = 0.0
        @JvmField var uY = 255.0
        @JvmField var uR = 230.0
        @JvmField var uB = 110.0
        @JvmField var MIN_WIDTH = 50
        @JvmField var BOUND_RATIO = 0.7
    }

    companion object {
        val lowerOrange get() = Scalar(VisionConstants.lY, VisionConstants.lR, VisionConstants.lB)
        val upperOrange get() = Scalar(VisionConstants.uY, VisionConstants.uR, VisionConstants.uB)
    }

    /**
     * default init call, constructor
     */
    init {
        height = Height.ZERO
        telemetry = tl!!
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
                if (w > maxW) {
                    //maxC = c;
                    maxW = w;
                    maxR = rect;
                }
                c.release()
                copy.release()
            }

            //maxC.release()

            Imgproc.rectangle(ret, maxR, Scalar(0.0, 0.0, 255.0), 2)

            telemetry.addData("Vision: maxW", maxW)
            height = if (maxW >= VisionConstants.MIN_WIDTH) {
                val aspectRatio: Double = maxR.height.toDouble() / maxR.width.toDouble()
                telemetry.addData("Vision: Aspect Ratio", aspectRatio)
                if (aspectRatio > VisionConstants.BOUND_RATIO) Height.FOUR else Height.ONE
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