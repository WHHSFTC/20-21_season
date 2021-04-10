package org.firstinspires.ftc.teamcode.summum

import org.firstinspires.ftc.teamcode.geometry.*
import org.firstinspires.ftc.teamcode.switchboard.hardware.Encoder
import org.firstinspires.ftc.teamcode.switchboard.core.Activity
import org.apache.commons.math3.linear.Array2DRowRealMatrix
import org.apache.commons.math3.linear.DecompositionSolver
import org.apache.commons.math3.linear.LUDecomposition
import org.apache.commons.math3.linear.MatrixUtils
import org.firstinspires.ftc.teamcode.switchboard.core.Logger
import org.firstinspires.ftc.teamcode.switchboard.observe.*
import org.firstinspires.ftc.teamcode.switchboard.shapes.Distance

abstract class DeadWheelLocalizer(log: Logger, val odos: List<Pair<Encoder, Pose2d>>) : Activity {
    private val forwardSolver: DecompositionSolver

    private var prevPositions: List<Int> = odos.map { 0 }

    val pose = Channel(Pose2d(0.0, 0.0, 0.0), "Pose", log.out)
    private var _pose: Pose2d by pose.delegate

    val velo = Channel(Pose2d(0.0, 0.0, 0.0), "PoseVelocity", log.out)
    private var _velo: Pose2d by velo.delegate

    init {
        require(odos.size >= 3) { "Localizer requires at least 3 odometry wheels" }

        // At = n where A = inverseMatrix, t = twist (v_x, v_y, omega), n = encoder deltas
        val inverseMatrix = Array2DRowRealMatrix(odos.size, 3)
        odos.forEachIndexed { i, p -> inverseMatrix.setRow(i, arrayOf(
                p.second.theta.cos(),
                p.second.theta.sin(),
                p.second.x * p.second.theta.sin() + p.second.y * p.second.theta.cos()
        ).toDoubleArray()) }
        forwardSolver = LUDecomposition(inverseMatrix).solver
        require(forwardSolver.isNonSingular) { "Wheel configuration is singular, ie is underconstrained" }
    }

    fun update() {
        val positions = odos.map { it.first.position }

        val deltas = positions.zip(prevPositions).map { ticksToDistance(it.first - it.second).inches }

        val twistVector = forwardSolver.solve(MatrixUtils.createRealVector(deltas.toDoubleArray()))
        val twist = Pose2d(twistVector.getEntry(0), twistVector.getEntry(1), twistVector.getEntry(2))
        _pose = _pose.exp(twist)

        val odoVelo = odos.map { it.first.velocity }
        val veloVector = forwardSolver.solve(MatrixUtils.createRealVector(odoVelo.toDoubleArray()))
        _velo = Pose2d(veloVector.getEntry(0), veloVector.getEntry(1), veloVector.getEntry(2))

        prevPositions = positions
    }

    abstract fun ticksToDistance(n: Int): Distance
}