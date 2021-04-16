package org.firstinspires.ftc.teamcode.summum

import org.firstinspires.ftc.teamcode.geometry.*
import org.firstinspires.ftc.teamcode.switchboard.hardware.Encoder
import org.firstinspires.ftc.teamcode.switchboard.core.Activity
import org.apache.commons.math3.linear.Array2DRowRealMatrix
import org.apache.commons.math3.linear.DecompositionSolver
import org.apache.commons.math3.linear.LUDecomposition
import org.apache.commons.math3.linear.MatrixUtils
import org.firstinspires.ftc.teamcode.switchboard.core.Logger
import org.firstinspires.ftc.teamcode.switchboard.shapes.Distance
import org.firstinspires.ftc.teamcode.switchboard.stores.*

abstract class DeadWheelLocalizer(logger: Logger, val odos: List<Pair<Encoder, Pose2d>>) : Activity {
    private val forwardSolver: DecompositionSolver

    var prevPositions: List<Int>? = null
    var prevPose: Pose2d? = null

    val dependencies = odos.unzip().first.fold(listOf<Observable<Pair<Int, Double>>>()) { acc, enc ->
        acc + (enc.position zip enc.velocity)
    }

    val pv = zipAll(dependencies).map { readings -> update(readings) }

    val _unz = pv.unzip()

    val pose = _unz.first
    val velo = _unz.second

    init {
        require(odos.size >= 3) { "Localizer requires at least 3 odometry wheels" }

        // At = n where A = inverseMatrix, t = twist (v_x, v_y, omega), n = encoder deltas
        val inverseMatrix = Array2DRowRealMatrix(odos.size, 3)
        odos.forEachIndexed { i, p -> inverseMatrix.setRow(i, arrayOf(
                p.second.theta.cos(),
                p.second.theta.sin(),
                p.second.x * p.second.theta.sin() - p.second.y * p.second.theta.cos()
        ).toDoubleArray()) }
        forwardSolver = LUDecomposition(inverseMatrix).solver
        require(forwardSolver.isNonSingular) { "Wheel configuration is singular, ie is underconstrained" }
    }

    fun update(readings: List<Pair<Int, Double>>): Pair<Pose2d, Pose2d> {
        val positions = readings.map { it.first }

        val prev = prevPositions ?: positions

        val deltas = positions.zip(prev).map { ticksToDistance(it.first - it.second).inches }

        val twistVector = forwardSolver.solve(MatrixUtils.createRealVector(deltas.toDoubleArray()))
        val twist = Pose2d(twistVector.getEntry(0), twistVector.getEntry(1), twistVector.getEntry(2))
        val pose = (prevPose ?: Pose2d(0.0, 0.0, 0.0)).exp(twist)

        val odoVelo = readings.map { it.second }
        val veloVector = forwardSolver.solve(MatrixUtils.createRealVector(odoVelo.toDoubleArray()))
        val velo = Pose2d(veloVector.getEntry(0), veloVector.getEntry(1), veloVector.getEntry(2))

        prevPositions = positions
        prevPose = pose

        return pose to velo
    }

    abstract fun ticksToDistance(n: Int): Distance
}