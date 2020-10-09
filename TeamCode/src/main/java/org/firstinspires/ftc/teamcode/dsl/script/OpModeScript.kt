package org.firstinspires.ftc.teamcode.dsl.script

import org.firstinspires.ftc.teamcode.fsm.IMachine
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.*
import kotlin.script.experimental.jvm.dependenciesFromClassContext
import kotlin.script.experimental.jvm.jvm

@KotlinScript(
        displayName = "OpMode Script",
        fileExtension = "opmode.kts",
        compilationConfiguration = OpModeScriptConfiguration::class
)
abstract class OpModeScript(machine: IMachine): IMachine by machine

internal object OpModeScriptConfiguration: ScriptCompilationConfiguration({
    jvm {
        dependenciesFromClassContext(
                OpModeScript::class,
                "kotlin-stdlib"
        )
    }

    ide {
        acceptedLocations(ScriptAcceptedLocation.Everywhere)
    }

    defaultImports(
            com.qualcomm.robotcore.eventloop.opmode.TeleOp::class,
            com.qualcomm.robotcore.eventloop.opmode.Autonomous::class,
            org.firstinspires.ftc.teamcode.dsl.OpModeContext::class,
            org.firstinspires.ftc.teamcode.dsl.Pass::class,
            org.firstinspires.ftc.teamcode.dsl.DslOpMode::class,
            org.firstinspires.ftc.teamcode.dsl.RobotDsl::class,
            org.firstinspires.ftc.teamcode.dsl.FSM::class,
            org.firstinspires.ftc.teamcode.dsl.Context::class,
            org.firstinspires.ftc.teamcode.dsl.AutoOnly::class,
            org.firstinspires.ftc.teamcode.dsl.Concurrent::class,
            org.firstinspires.ftc.teamcode.dsl.Execute::class,
            org.firstinspires.ftc.teamcode.dsl.Infinite::class,
            org.firstinspires.ftc.teamcode.dsl.LinearSeries::class,
            org.firstinspires.ftc.teamcode.dsl.WaitFor::class
    )
})