package org.firstinspires.ftc.teamcode.switchboard

import com.qualcomm.hardware.lynx.LynxModule
import org.firstinspires.ftc.teamcode.switchboard.hardware.Motor
import org.firstinspires.ftc.teamcode.switchboard.hardware.Servo

class RevHub(val lynx: LynxModule) {
    val data: MutableMap<String, Any> = mutableMapOf()

    var motors: List<Motor> = listOf()
    var servos: List<Servo> = listOf()
}

interface HardwareInput {
    fun input()
}
interface HardwareOutput {
    fun output()
}

class HardwareGroup {
    var motors: List<Motor> = listOf()
    var servos: List<Servo> = listOf()
}