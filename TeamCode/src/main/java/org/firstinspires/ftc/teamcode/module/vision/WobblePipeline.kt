package org.firstinspires.ftc.teamcode.module.vision

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.acmerobotics.roadrunner.geometry.Vector2d
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.teamcode.module.Summum
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import kotlin.math.*


class WobblePipeline(val bot: Summum, val cwidth: Int, val cheight: Int): Pipeline() {

    private val dashboard: FtcDashboard = FtcDashboard.getInstance()
    var telemetry = bot.logger
    var mat: Mat
    var ret: Mat

    var tallest: AngleRect = AngleRect()
    var estimate: Vector2d = Vector2d()

    @Config
    object WobbleConstants {
        @JvmField
        var lH = 100.0
        @JvmField
        var lS = 170.0
        @JvmField
        var lV = 70.0
        @JvmField
        var uH = 120.0
        @JvmField
        var uS = 255.0
        @JvmField
        var uV = 255.0

        @JvmField
        var MIN_HEIGHT = .2
        @JvmField
        var MAX_WIDTH = .1
        @JvmField
        var HORIZON = .33

        @JvmField
        var WOB_BASE_HEIGHT = 3.0

        @JvmField
        var WOB_TOP_HEIGHT = 12.5
    }

    val focalLength: Double get() = VisionConstants.FOCAL_RATIO * cwidth.toDouble() / 2.0
    val horizon: Int get() = (WobbleConstants.HORIZON * cheight).toInt()
    val minHeight: Int get() = (WobbleConstants.MIN_HEIGHT * cheight).toInt()
    val maxWidth: Int get() = (WobbleConstants.MAX_WIDTH * cwidth).toInt()

    val baseHeightDiff: Double get() = VisionConstants.CAMERA_HEIGHT - WobbleConstants.WOB_BASE_HEIGHT
    val topHeightDiff: Double get() = VisionConstants.CAMERA_HEIGHT - WobbleConstants.WOB_BASE_HEIGHT

    companion object {
        val lowerOrange get() = Scalar(WobbleConstants.lH, WobbleConstants.lS, WobbleConstants.lV)
        val upperOrange get() = Scalar(WobbleConstants.uH, WobbleConstants.uS, WobbleConstants.uV)
    }

    /**
     * default init call, constructor
     */
    init {
        ret = Mat()
        mat = Mat()
    }

    //fun ctransform(x: Int, y: Int): Pair<Int, Int> = Pair(x - cwidth / 2, cheight / 2 - y)
    fun angle(n: Int): Double = atan2(n.toDouble(), focalLength)
    fun delta(n1: Int, n2: Int): Double = angle(n2) - angle(n1)

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
            Imgproc.cvtColor(input, mat, Imgproc.COLOR_RGB2HSV)

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

            Imgproc.line(ret, Point(.0, horizon.toDouble()), Point(cwidth.toDouble(), horizon.toDouble()), Scalar(255.0, .0, 255.0))

            boxes = contours.mapNotNull {
                val copy = MatOfPoint2f(*it.toArray())
                val box: Rect = Imgproc.boundingRect(copy)

                var r: Rect? = null
                if (box.height > minHeight && box.width < maxWidth && box.y + box.height > horizon) {
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
            telemetry.err["[VISION ERROR]"] = e
            e.stackTrace.toList().stream().forEach { x -> telemetry.addMessage(x.toString(), Time.seconds(60)) }
        }

        return Pair(boxes, ret)
    }

    fun horizEstimate(range: AngleRect): Vector2d
        = Vector2d(1.0, tan(-range.alpha)) * baseHeightDiff / tan(-range.bottom)

    fun topEstimate(range: AngleRect): Vector2d
            = Vector2d(1.0, tan(-range.alpha)) * topHeightDiff / tan(-range.top)

    override fun processFrame(input: Mat?): Mat {
        //telemetry.update()
        val (boxes, retImage) = getBoxes(input)

        val top3 = boxes.subList(0, min(boxes.size, 3))

        val ranges = top3.map { range(it) }

        if (ranges.isNotEmpty()) {
            tallest = ranges[0]
            estimate = horizEstimate(ranges[0])
        } else {
            tallest = AngleRect()
            estimate = Vector2d()
        }

        drawRing()

        return retImage
    }

    private fun drawRing() {
        val botEst = bot.dt.poseEstimate.vec() + (Vector2d(9.0, -5.0) + estimate).rotated(bot.dt.poseEstimate.heading)
        //val topEst = bot.dt.poseEstimate.vec() + (Vector2d(9.0, -5.0) + topEstimate(tallest)).rotated(bot.dt.poseEstimate.heading)

        val packet = TelemetryPacket()
        packet.put("xBot", botEst.x)
        packet.put("yBot", botEst.y)

        val fieldOverlay = packet.fieldOverlay()
        fieldOverlay.setStroke("#0000ff")
        fieldOverlay.strokeCircle(botEst.x, botEst.y, 4.0)

        dashboard.sendTelemetryPacket(packet)
    }
}
