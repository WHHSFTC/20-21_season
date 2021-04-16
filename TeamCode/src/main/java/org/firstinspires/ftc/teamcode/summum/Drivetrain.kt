package org.firstinspires.ftc.teamcode.summum

import org.apache.commons.math3.linear.Array2DRowRealMatrix
import org.firstinspires.ftc.teamcode.geometry.*
import org.firstinspires.ftc.teamcode.switchboard.core.Activity
import org.firstinspires.ftc.teamcode.switchboard.drive.FeedforwardCoef
import org.firstinspires.ftc.teamcode.switchboard.observe.Channel
import org.firstinspires.ftc.teamcode.switchboard.shapes.Distance
import kotlin.math.absoluteValue
import kotlin.math.max

class Drivetrain(val bot: Summum, val ff: FeedforwardCoef) : Activity {
    val wheels = listOf(
            "RF" to Pose2d(WHEELX, -WHEELY, -135.0.rad()),
            "LF" to Pose2d(WHEELX, WHEELY, -45.0.rad()),
            "LB" to Pose2d(-WHEELX, WHEELY, 45.0.rad()),
            "RB" to Pose2d(-WHEELX, -WHEELY, 135.0.rad())
    ).map { bot.config.motors["motor${it.first}"] to it.second }

    val inverseMatrix: Array2DRowRealMatrix = Array2DRowRealMatrix(wheels.size, 3)

    init {
        // At = n where A = inverseMatrix, t = twist (v_x, v_y, omega), n = wheel velocities
        wheels.forEachIndexed { i, p -> inverseMatrix.setRow(i, arrayOf(
                p.second.theta.cos(),
                p.second.theta.sin(),
                p.second.x * p.second.theta.sin() - p.second.y * p.second.theta.cos()
        ).toDoubleArray()) }
    }

    val loadFollower = Channel<Follower>(Follower.idle, "Follower", bot.logger.out)
    private val follower: Follower by loadFollower.delegate

    override fun load() {
        (bot.loc.pose zip bot.loc.velo).subscribe {
            setMotorPowers(twistToPowers(follower.update(it.first, it.second)))
        }

        loadFollower.subscribe { it.load() }
    }

    data class Profile(val maxVelo: Double, val maxAccel: Double, val maxJerk: Double)

    sealed class Follower {
        abstract fun update(pose: Pose2d, velo: Pose2d): Pose2d
        abstract fun load()

        object idle : Follower() {
            override fun load() {

            }

            override fun update(pose: Pose2d, velo: Pose2d): Pose2d = Pose2d(0.0, 0.0, 0.0)
        }

        data class Approach(val point: Vector2d) : Follower() {
            override fun load() {

            }

            override fun update(pose: Pose2d, velo: Pose2d): Pose2d {
                val e = point - pose.vec
                return Pose2d((e rotateBy -pose.theta) * 0.05, 0.0)
            }
        }
    }

    override fun cleanup() {
        setMotorPowers(listOf(0.0, 0.0, 0.0, 0.0))
    }

    fun feedforwardToPowers(twist: Pose2d, accel: Pose2d): List<Double>
        = (twistToPowers(twist) zip twistToPowers(accel)).map { (velo, accel) -> ff(velo, accel) }

    fun twistToPowers(twist: Pose2d): List<Double>
        = inverseMatrix
                .multiply(Array2DRowRealMatrix(
                        arrayOf(twist.x, twist.y, twist.theta).toDoubleArray()
                ))
                .getColumn(0).toList().also { bot.logger.out["Powers"] = it }

    fun setMotorPowers(powers: List<Double>) {
        val m = powers.map { it.absoluteValue }.maxOrNull()
        require(m != null && powers.size == 4) { "Powers list must contain 4 Doubles" }
        val n = max(m, 1.0)
        wheels.zip(powers)
                .forEach { it.first.first.power = it.second/n }
    }

    companion object {
        val WHEELX = 12.75/2
        val WHEELY = 15.1863/2

        val WHEEL_RADIUS = Distance.inch(2.0)
        val GEAR_RATIO = 1.0

        val CIRCUMFERENCE = WHEEL_RADIUS * TAU
    }
}