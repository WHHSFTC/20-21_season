package org.firstinspires.ftc.teamcode.module.rings

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.acmerobotics.roadrunner.geometry.Vector2d
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.module.Robot
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import org.openftc.easyopencv.OpenCvPipeline
import kotlin.math.*


class Pipeline(val bot: Robot, tl: Telemetry? = null, val cwidth: Int, val cheight: Int): OpenCvPipeline() {
    private val dashboard: FtcDashboard = FtcDashboard.getInstance()
    var telemetry: Telemetry
    var mat: Mat
    var ret: Mat

    var widest: AngleRect = AngleRect()
    var estimate: Vector2d = Vector2d()

    @Config
    object RingConstants {
        @JvmField
        var lY = 0.0
        @JvmField
        var lR = 141.0
        @JvmField
        var lB = 0.0
        @JvmField
        var uY = 255.0
        @JvmField
        var uR = 230.0
        @JvmField
        var uB = 110.0
        @JvmField
        var MIN_WIDTH = .1
        @JvmField
        var HORIZON = .33

        //@JvmField var DFOV = 1.3881
        @JvmField
        var FOCAL_RATIO = 92.5 / 46.0
        const val RING_RADIUS = 2.5
        @JvmField
        var CAMERA_HEIGHT = 3.7
    }

    val focalLength: Double get() = RingConstants.FOCAL_RATIO * cwidth.toDouble() / 2.0
    val horizon: Int get() = (RingConstants.HORIZON * cheight).toInt()
    val minWidth: Int get() = (RingConstants.MIN_WIDTH * cwidth).toInt()

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
    }

    //fun ctransform(x: Int, y: Int): Pair<Int, Int> = Pair(x - cwidth / 2, cheight / 2 - y)
    fun angle(n: Int): Double = atan2(n.toDouble(), focalLength)
    fun delta(n1: Int, n2: Int): Double = angle(n2) - angle(n1)
    fun estimateDistance(da: Double): Double = RingConstants.RING_RADIUS / sin(da / 2.0)

    fun range(rect: Rect): AngleRect // todo angular instead of linear interpolation for midpoint
        = AngleRect(
            left = angle(rect.x - cwidth / 2),
            right = angle(rect.x + rect.width - cwidth / 2),
            top = angle(cheight / 2 - rect.y),
            bottom = angle(cheight / 2 - rect.y - rect.height)
        )

    data class AngleRect(val top: Double = 0.0, val bottom: Double = 0.0, val left: Double = 0.0, val right: Double = 0.0) {
        val alpha: Double get() = (left + right) / 2.0
        val beta: Double get() = (top + bottom) / 2.0

        val width: Double get() = right - left
        val height: Double get() = top - bottom
    }

    fun getBoxes(input: Mat?): Pair<List<Rect>, Mat> {
        ret.release()

        var boxes: List<Rect> = listOf()
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

            Imgproc.line(ret, Point(.0, RingConstants.HORIZON.toDouble()), Point(cwidth.toDouble(), RingConstants.HORIZON.toDouble()), Scalar(255.0, .0, 255.0))

            boxes = contours.mapNotNull {
                val copy = MatOfPoint2f(*it.toArray())
                val box: Rect = Imgproc.boundingRect(copy)

                val w = box.width
                var r: Rect? = null
                if (w > minWidth && box.y + box.height > RingConstants.HORIZON) {
                    Imgproc.rectangle(ret, box, Scalar(0.0, 0.0, 255.0), 2)
                    r = box
                }
                it.release()
                copy.release()
                r
            }.sortedByDescending { it.width }

            mat.release()
            mask.release()
            hierarchy.release()
        } catch (e: Exception) {
            /**error handling, prints stack trace for specific debug**/
            telemetry.addData("[ERROR]", e)
            e.stackTrace.toList().stream().forEach { x -> telemetry.addLine(x.toString()) }
        }

        return Pair(boxes, ret)
    }

    fun sizeEstimate(range: AngleRect): Vector2d
        = Vector2d(cos(-range.alpha), sin(-range.alpha)) * estimateDistance(range.width)

    fun horizEstimate(range: AngleRect): Vector2d
        = Vector2d(1.0, tan(-range.alpha)) * RingConstants.CAMERA_HEIGHT / tan(-range.bottom)

    override fun processFrame(input: Mat?): Mat {
        //telemetry.update()
        val (boxes, retImage) = getBoxes(input)

        val ranges = boxes.map { range(it) }

        if (ranges.isNotEmpty()) {
            widest = ranges[0]
            estimate = horizEstimate(ranges[0])
        } else {
            widest = AngleRect()
            estimate = Vector2d()
        }

        drawRing()

        return retImage
    }

    private fun drawRing() {
        val p1 = bot.dt.poseEstimate.vec() + (Vector2d(9.0, -5.0) + sizeEstimate(widest)).rotated(bot.dt.poseEstimate.heading)
        val p2 = bot.dt.poseEstimate.vec() + (Vector2d(9.0, -5.0) + horizEstimate(widest)).rotated(bot.dt.poseEstimate.heading)

        val packet = TelemetryPacket()
        packet.put("x1", p1.x)
        packet.put("y1", p1.y)
        packet.put("x2", p2.x)
        packet.put("y2", p2.y)

        val fieldOverlay = packet.fieldOverlay()
        fieldOverlay.setFill("#dda277")
        fieldOverlay.strokeCircle(p1.x, p1.y, 2.5)
        fieldOverlay.setFill("#e1e1e1")
        fieldOverlay.fillCircle(p1.x, p1.y, 1.5)

        fieldOverlay.setFill("#dda277")
        fieldOverlay.strokeCircle(p2.x, p2.y, 2.5)
        fieldOverlay.setFill("#e1e1e1")
        fieldOverlay.fillCircle(p2.x, p2.y, 1.5)

        dashboard.sendTelemetryPacket(packet)
    }
}
