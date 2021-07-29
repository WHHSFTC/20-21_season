package org.firstinspires.ftc.teamcode.module.vision

import com.acmerobotics.dashboard.FtcDashboard
import com.acmerobotics.dashboard.config.Config
import com.acmerobotics.dashboard.telemetry.TelemetryPacket
import com.acmerobotics.roadrunner.geometry.Vector2d
import org.firstinspires.ftc.teamcode.module.Alliance
import org.firstinspires.ftc.teamcode.module.Summum
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.tan

class RingProcessor(val bot: Summum, val leftCam: Camera, val rightCam: Camera) {
    private val dashboard = FtcDashboard.getInstance()
    private val leftPipe: RingPipeline = RingPipeline(bot)
    private val rightPipe: RingPipeline = RingPipeline(bot)
    var running = false
        private set

    fun start() {
        if (!running) {
            leftCam.addPipeline(leftPipe)
            rightCam.addPipeline(rightPipe)
        }
        running = true
    }

    fun stop() {
        if (running) {
            leftCam.removePipeline(leftPipe)
            rightCam.removePipeline(leftPipe)
        }
        running = false
    }

    val commonPoints: List<Vector2d>
        get() = (leftPipe.vectors cartesian rightPipe.vectors)
            .asSequence()
            .map { (a, b) ->  global((a + b)/2.0) to (a distTo b) }
            .sortedBy { it.second }
            .filter { abs(it.first.x) < 72.0 && abs(it.first.y) < 72.0 }
            .filter { bot.alliance.contains(it.first) }
            .filter { it.first.x > 0.0 }
            .map { it.first }
            .toList()
            .cap(3)

    private infix fun <T> List<T>.cartesian(other: List<T>)
            = this.flatMap { a -> other.map { b -> a to b } }
    private fun <T> List<T>.cap(n: Int)
            = if (this.size > n) subList(0, n) else this

    private fun global(botframe: Vector2d)
            = (bot.dt.poseEstimate.vec() + botframe.rotated(bot.dt.poseEstimate.heading))//.takeIf { abs(it.x) < 72.0 && abs(it.y) < 72.0 }

    private fun drawRing(v: Vector2d) {
        val packet = TelemetryPacket()
        packet.put("ringX", v.x)
        packet.put("ringY", v.y)

        val fieldOverlay = packet.fieldOverlay()
        fieldOverlay.setFill("#dda277")
        fieldOverlay.strokeCircle(v.x, v.y, 2.5)
        fieldOverlay.setFill("#e1e1e1")
        fieldOverlay.fillCircle(v.x, v.y, 1.5)

        dashboard.sendTelemetryPacket(packet)
    }


    class RingPipeline(val bot: Summum): Pipeline {
        var telemetry = bot.logger

        override val input = View("Stack - Input", Scalar(255.0, 0.0, 0.0))
        val yCrCb: View = View("Rings - YCrCb")
        val mask: View = View("Rings - Mask")
        val ret: View = View("Rings - Ret")
        val hierarchy = Mat()

        override fun initialize(camera: Camera) {
            camera.addView(yCrCb)
            camera.addView(mask)
            camera.addView(ret)
            //camera.addView(input)
        }

        var beams: List<AngleRect> = listOf()
        var vectors: List<Vector2d> = listOf()

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
            var uR = 170.0
            @JvmField
            var uB = 110.0
            @JvmField
            var MIN_WIDTH = .05
            @JvmField
            var HORIZON = .5

            const val RING_RADIUS = 2.5
        }

        val focalLength: Double get() = VisionConstants.FOCAL_RATIO * Vision.FRAME_WIDTH.toDouble() / 2.0
        val horizon: Int get() = (RingConstants.HORIZON * Vision.FRAME_HEIGHT).toInt()
        val minWidth: Int get() = (RingConstants.MIN_WIDTH * Vision.FRAME_WIDTH).toInt()

        companion object {
            val lowerOrange get() = Scalar(RingConstants.lY, RingConstants.lR, RingConstants.lB)
            val upperOrange get() = Scalar(RingConstants.uY, RingConstants.uR, RingConstants.uB)
        }

        //fun ctransform(x: Int, y: Int): Pair<Int, Int> = Pair(x - Vision.FRAME_WIDTH / 2, Vision.FRAME_HEIGHT / 2 - y)
        fun angle(n: Int): Double = atan2(n.toDouble(), focalLength)
        fun delta(n1: Int, n2: Int): Double = angle(n2) - angle(n1)

        fun range(rect: Rect): AngleRect // todo angular instead of linear interpolation for midpoint
                = AngleRect(
            left = angle(rect.x - Vision.FRAME_WIDTH / 2),
            right = angle(rect.x + rect.width - Vision.FRAME_WIDTH / 2),
            top = angle(Vision.FRAME_HEIGHT / 2 - rect.y),
            bottom = angle(Vision.FRAME_HEIGHT / 2 - rect.y - rect.height)
        )

        data class AngleRect(val top: Double = 0.0, val bottom: Double = 0.0, val left: Double = 0.0, val right: Double = 0.0) {
            val alpha: Double get() = (left + right) / 2.0
            val beta: Double get() = (top + bottom) / 2.0

            val width: Double get() = right - left
            val height: Double get() = top - bottom
        }

        override fun processFrame(camera: Camera) {
            var boxes: List<Rect> = listOf()
            try {
//            mat.release()

                /**converting from RGB color space to HSV color space**/
                Imgproc.cvtColor(input, yCrCb, Imgproc.COLOR_RGB2YCrCb)

                /**checking if any pixel is within the yellow bounds to make a black and white mask**/
                //val mask = Mat(yCrCb.rows(), yCrCb.cols(), CvType.CV_8UC1)
                Core.inRange(yCrCb, lowerOrange, upperOrange, mask)

                ret.setTo(Scalar(0.0, 0.0, 0.0))
                /**applying to input and putting it on ret in black or yellow**/
                Core.bitwise_and(input, input, ret, mask)

                Imgproc.GaussianBlur(mask, mask, Size(5.0, 15.0), 0.00);

                /**finding contours on mask**/
                val contours: List<MatOfPoint> = ArrayList()
                //val hierarchy = Mat()
                Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE)

                Imgproc.drawContours(ret, contours, -1, Scalar(0.0, 255.0, 0.0), 3)

                Imgproc.line(ret, Point(.0, RingConstants.HORIZON.toDouble()), Point(Vision.FRAME_WIDTH.toDouble(), RingConstants.HORIZON.toDouble()), Scalar(255.0, .0, 255.0))

                boxes = contours.mapNotNull {
                    val copy = MatOfPoint2f(*it.toArray())
                    val box: Rect = Imgproc.boundingRect(copy)

                    val w = box.width
                    var r: Rect? = null
                    if (w > minWidth && box.y + box.height > horizon) {
                        Imgproc.rectangle(ret, box, Scalar(0.0, 0.0, 255.0), 2)
                        r = box
                    }
                    it.release()
                    copy.release()
                    r
                }.sortedByDescending { it.width }.filter { it.width < Vision.FRAME_WIDTH * 0.80 }

            } catch (e: Exception) {
                /**error handling, prints stack trace for specific debug**/
                telemetry.err["[VISION ERROR]"] = e
                e.stackTrace.toList().stream().forEach { x -> telemetry.addMessage(x.toString(), Time.seconds(60)) }
            }

            // return boxes
            beams = boxes.map { range(it) }

            vectors = beams.mapNotNull { estimateVector(it, camera) }.map { botframe(it, camera) }
        }

        fun botframe(v: Vector2d, camera: Camera)
                = camera.pose.vec() + v.rotated(camera.pose.heading)

        //fun estimateDistance(da: Double): Double = RingPipeline.RingConstants.RING_RADIUS / sin(da / 2.0)

        //private fun sizeEstimate(range: RingPipeline.AngleRect): Vector2d
        //= Vector2d(cos(-range.alpha), sin(-range.alpha)) * estimateDistance(range.width)

        private fun estimateVector(range: RingPipeline.AngleRect, camera: Camera)
                = (Vector2d(1.0, tan(-range.alpha)) * camera.height / tan(camera.verticalAngle - range.bottom)).takeIf { it.x > 0.0 }
    }
}