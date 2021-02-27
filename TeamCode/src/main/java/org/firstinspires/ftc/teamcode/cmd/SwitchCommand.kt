package org.firstinspires.ftc.teamcode.cmd

import com.acmerobotics.roadrunner.geometry.Pose2d
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder
import org.firstinspires.ftc.teamcode.dsl.RobotDsl
import org.firstinspires.ftc.teamcode.module.DriveConstants
import org.firstinspires.ftc.teamcode.module.Robot

class SwitchCommand<T>(
        val supp: Robot.() -> T,
        val cases: List<Case<T>>,
): Command() {
    @RobotDsl
    class SwitchCaseBuilder<T>: DSLContext() {
        private val list: MutableList<Case<T>> = mutableListOf()

//        fun case(supp: Robot.() -> T, com: Command) {
//            list += Case(supp, com)
//        }

        fun caseCommand(vararg listOfSupp: Robot.() -> T, com: () -> Command) {
            for (supp in listOfSupp) {
                list += Case(supp, com())
            }
        }

//        fun case(supp: Robot.() -> T, cmdBlock: suspend Robot.() -> Unit) {
//            list += Case(supp, LambdaCommand(cmdBlock))
//        }

        fun caseBlock(vararg listOfSupp: Robot.() -> T, cmdBlock: suspend Robot.() -> Unit) {
            for (supp in listOfSupp) {
                list += Case(supp, LambdaCommand(cmdBlock))
            }
        }

//        suspend fun caseSeq(supp: Robot.() -> T, sequentialBlock: suspend CommandListContext.() -> Unit) {
//            list += Case(supp, CommandContext.seq(sequentialBlock))
//        }

        suspend fun caseSeq(vararg listOfSupp: Robot.() -> T, sequentialBlock: suspend CommandListContext.() -> Unit) {
            for (supp in listOfSupp) {
                list += Case(supp, CommandContext.seq(sequentialBlock))
            }
        }

//        suspend fun casePar(supp: Robot.() -> T, parallelBlock: suspend CommandListContext.() -> Unit) {
//            list += Case(supp, CommandContext.par(parallelBlock))
//        }

        suspend fun casePar(vararg listOfSupp: Robot.() -> T, parallelBlock: suspend CommandListContext.() -> Unit) {
            for (supp in listOfSupp) {
                list += Case(supp, CommandContext.par(parallelBlock))
            }
        }

//        fun caseGo(supp: Robot.() -> T, pose: Pose2d, reversed: Boolean = false, async: Boolean = false, b: TrajectoryBuilder.() -> TrajectoryBuilder) {
//            val traj = TrajectoryBuilder(pose, reversed = reversed, constraints = DriveConstants.MECANUM_CONSTRAINTS).b().build()
//            list += Case(supp,
//                    if (async)
//                        LambdaCommand { dt.followTrajectoryAsync(traj) }
//                    else
//                        LambdaCommand { dt.followTrajectory(traj) },
//            )
//        }

        fun caseGo(vararg listOfSupp: Robot.() -> T, startPose: Pose2d = Pose2d(), reversed: Boolean = false, async: Boolean = false, b: TrajectoryBuilder.() -> TrajectoryBuilder) {
            val traj = TrajectoryBuilder(startPose, reversed = reversed, constraints = DriveConstants.MECANUM_CONSTRAINTS).b().build()
            for (supp in listOfSupp) {
                list += Case(
                        supp,
                        if (async)
                            LambdaCommand { dt.followTrajectoryAsync(traj) }
                        else
                            LambdaCommand { dt.followTrajectory(traj) },
                )
            }
        }

        fun build(): List<Case<T>> = list
    }

    data class Case<T>(val supp: Robot.() -> T, val com: Command)
    
    override suspend fun execute(bot: Robot) {
        for (c in cases) {
            if (supp(bot) == c.supp(bot))
                return c.com.execute(bot)
        }
    }
}
