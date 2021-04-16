package org.firstinspires.ftc.teamcode.switchboard.hardware

import com.qualcomm.robotcore.hardware.DcMotorEx
import org.firstinspires.ftc.teamcode.switchboard.core.Logger
import org.firstinspires.ftc.teamcode.switchboard.stores.*

class MotorImpl(val m: DcMotorEx, val name: String, val logger: Logger): Motor {
    override val power: Observer<Double> = entry<Double>()
    override val zpb: Observer<Motor.ZeroPowerBehavior> = entry<Motor.ZeroPowerBehavior>()

    val powerStore: Store<Double>
    val zpbStore: Store<Motor.ZeroPowerBehavior>

    init {
        powerStore = (power as Subject<Double, Double>).dedup(0.0).store(0.0)
        zpbStore = (zpb as Subject<Motor.ZeroPowerBehavior, Motor.ZeroPowerBehavior>).dedup(Motor.ZeroPowerBehavior.BRAKE).store(Motor.ZeroPowerBehavior.BRAKE)

        powerStore.log(logger.err, "$name power")
        zpbStore.log(logger.err, "$name zpb")
    }

    override fun output(all: Boolean) {
        if (m.power != powerStore.value)
            m.power = powerStore.value

        if (m.zeroPowerBehavior != zpbStore.value.mirror)
            m.zeroPowerBehavior = zpbStore.value.mirror
    }
}