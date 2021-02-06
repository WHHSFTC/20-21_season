package org.firstinspires.ftc.teamcode.test

import com.acmerobotics.dashboard.config.Config
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.ServoImplEx

@Autonomous
class IndexerTest: LinearOpMode() {
    val indexerServo by lazy {
        hardwareMap.servo["indexer"] as ServoImplEx
    }

    override fun runOpMode() {
        telemetry.addData("[INIT]", "Initializing")
        telemetry.addData("[INIT]", "setting servo to 0 position")
        telemetry.update()

        indexerServo.position = IndexerConstants.START_POSITION_DEGREES / 270.0

        telemetry.addData("[INIT]", "DONE")
        telemetry.update()

        waitForStart()

        for (i in 0 until 3) {
            indexerServo.position = (IndexerConstants.START_POSITION_DEGREES + 90.0) / 270.0

            sleep(IndexerConstants.DELAY_TIME.toLong())

            indexerServo.position = IndexerConstants.START_POSITION_DEGREES / 270.0

            sleep(IndexerConstants.DELAY_TIME.toLong())
        }
    }
}

@Config
object IndexerConstants {
    @JvmField var START_POSITION_DEGREES = 45
    @JvmField var DELAY_TIME = 125
}
