package org.firstinspires.ftc.teamcode.implementation

import org.firstinspires.ftc.robotcore.external.Telemetry
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.openftc.easyopencv.OpenCvPipeline


class Vision(tl: Telemetry? = null): OpenCvPipeline() {
    lateinit var height: Height
    var telemetry: Telemetry
    var mat: Mat

    enum class Height {
        ZERO, ONE, FOUR
    }

    companion object {
        val lowerOrange = Scalar(0.0, 141.0, 0.0)
        val upperOrange = Scalar(255.0, 230.0, 150.0)
    }

    /**
     * default init call, constructor
     */
    init {
        height = Height.ZERO
        telemetry = tl!!
        mat = Mat()
    }

    override fun processFrame(input: Mat?): Mat {
        val ret = Mat()
        try {
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
            var maxC: MatOfPoint
            var maxW: Int = 0
            var maxR: Rect = Rect()
            for (c: MatOfPoint in contours) {
                val copy = MatOfPoint2f(*c.toArray())
                val rect: Rect = Imgproc.boundingRect(copy)

                val w = rect.width
                if (w > maxW) {
                    maxC = c;
                    maxW = w;
                    maxR = rect;
                }
            }

            Imgproc.rectangle(ret, maxR, Scalar(0.0, 0.0, 255.0), 2)

            height = if (maxW >= 200) {
                val aspectRatio = maxR.height / maxR.width
                telemetry.addData("Vision: Aspect Ratio", aspectRatio)
                if (aspectRatio > .5) Height.FOUR else Height.ONE
            } else {
                Height.ZERO
            }

            telemetry.addData("Vision: Height", height)

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