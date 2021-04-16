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


    val positions = zipAll(odos.unzip().first.map { enc -> enc.position })

    val deltas = positions.pairwise().map { (new, old) -> (new zip old).map { (n, o) -> n - o } }

    val velocities = zipAll(odos.unzip().first.map { enc -> enc.velocity })

    //val pv = (deltas zip velocities).tap { log(logger.out, "odos") }
            //.scan(Pose2d() to Pose2d()) { (pose, twist), (deltas, velos) -> update(pose, deltas, velos) }

//    private val unzipped = pv.unzip()

//    val pose = unzipped.first.tap { log(logger.out, "pose") }
//    val velo = unzipped.second.tap { log(logger.out, "velo") }

    val stepTwist = deltas.map { solve(it.map { ticksToDistance(it).inches }) }

    val pose = stepTwist.scan(Pose2d()) { prev, twist -> prev.exp(twist) }.taplog(logger.out, "!Pose")

    val velo = velocities.map { solve(it) }

    fun solve(vector: List<Double>): Pose2d {
        val ve = forwardSolver.solve(MatrixUtils.createRealVector(vector.toDoubleArray()))
        return Pose2d(ve.getEntry(0), ve.getEntry(1), ve.getEntry(2))
    }

    abstract fun ticksToDistance(n: Int): Distance
}