package org.firstinspires.ftc.teamcode.summum

import org.apache.commons.math3.linear.Array2DRowRealMatrix
import org.firstinspires.ftc.teamcode.geometry.*
import org.firstinspires.ftc.teamcode.switchboard.core.Activity
import org.firstinspires.ftc.teamcode.switchboard.drive.FeedforwardCoef
import org.firstinspires.ftc.teamcode.switchboard.shapes.Distance
import org.firstinspires.ftc.teamcode.switchboard.stores.*
import kotlin.math.absoluteValue
import kotlin.math.max

class Drivetrain(val bot: Summum, val ff: FeedforwardCoef) : Activity {
    val wheels = listOf(
            "RF" to Pose2d(WHEELX, -WHEELY, -135.0.rad()),
            "LF" to Pose2d(WHEELX, WHEELY, -45.0.rad()),
            "LB" to Pose2d(-WHEELX, WHEELY, 45.0.rad()),
            "RB" to Pose2d(-WHEELX, -WHEELY, 135.0.rad())
    ).map { bot.config.motors["motor${it.first}"] to it.second }

    val follower = SimpleSubject<Observable<Signal>>()
    private val driveSignal = follower.flatten().inject()

    private val velo = driveSignal.map { it.velo }
    private val accel = driveSignal.map { it.accel }

    private val powers = (velo.map { twistToPowers(it) } zip accel.map { twistToPowers(it) })
            .map { (velo, accel) -> ff.calculateList(velo, accel) }.map { adjustPowers(it) }

    private val inverseMatrix: Array2DRowRealMatrix = Array2DRowRealMatrix(wheels.size, 3)
    init {
        wheels.forEachIndexed { i, (motor, pose) ->
            // bind motor powers
            powers.map { it[i] } bind motor.power

            // At = n where A = inverseMatrix, t = twist (v_x, v_y, omega), n = wheel velocities
            inverseMatrix.setRow(i, arrayOf(
                pose.theta.cos(),
                pose.theta.sin(),
                pose.x * pose.theta.sin() - pose.y * pose.theta.cos()
            ).toDoubleArray())
        }
    }

    override fun load() {
        follower.next(makeApproach(Vector2d(0.0, 0.0)))
    }

    override fun cleanup() {
        follower.next(makeStop())
        driveSignal.next(Signal(
                velo = Pose2d(0.0, 0.0, 0.0),
                accel = Pose2d(0.0, 0.0, 0.0)
        ))
    }

    data class Profile(val maxVelo: Double, val maxAccel: Double, val maxJerk: Double)
    data class Signal(val velo: Pose2d, val accel: Pose2d)

    fun makeStop(): Observable<Signal>
        = (bot.loc.pose zip bot.loc.velo).map {
            Signal(
                    velo = Pose2d(0.0, 0.0, 0.0),
                    accel = Pose2d(0.0, 0.0, 0.0)
            )
        }

    fun makeApproach(point: Vector2d): Observable<Signal>
        = (bot.loc.pose zip bot.loc.velo).map { (pose, velo) ->
            val e = point - pose.vec
            Signal(
                    velo = Pose2d((e rotateBy -pose.theta) * 0.05, 0.0),
                    accel = Pose2d(0.0, 0.0, 0.0)
            )
        }

    fun twistToPowers(twist: Pose2d): List<Double>
        = inverseMatrix
                .multiply(Array2DRowRealMatrix(
                        arrayOf(twist.x, twist.y, twist.theta).toDoubleArray()
                ))
                .getColumn(0).toList().also { bot.logger.out["Powers"] = it }

    fun adjustPowers(powers: List<Double>): List<Double> {
        val m = powers.map { it.absoluteValue }.maxOrNull()
        require(m != null && powers.size == 4) { "Powers list must contain 4 Doubles" }
        val n = max(m, 1.0)
        return powers.map { it/n }
    }

    companion object {
        val WHEELX = 12.75/2
        val WHEELY = 15.1863/2

        val WHEEL_RADIUS = Distance.inch(2.0)
        val GEAR_RATIO = 1.0

        val CIRCUMFERENCE = WHEEL_RADIUS * TAU
    }
}