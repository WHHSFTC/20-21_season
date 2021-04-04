package org.firstinspires.ftc.teamcode.switchboard.core

import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.switchboard.hardware.*
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time

class Config(val hwMap: HardwareMap, val log: Log) {
    val motors = object {
        operator fun get(key: String): Motor
                = getObject(::MotorImpl, ::MotorStub, key)
    }

    val encoders = object {
        operator fun get(key: String): Encoder
                = getObject(::EncoderImpl, ::EncoderStub, key)
    }

    val servos = object {
        operator fun get(key: String): Servo
                = getObject(::ServoImpl, ::ServoStub, key)
    }

    val analogOutput = object {
        operator fun get(key: String): AnalogOutput
                = getObject(::AnalogOutputImpl, ::AnalogOutputStub, key)
    }

    val analogInput = object {
        operator fun get(key: String): AnalogInput
                = getObject(::AnalogInputImpl, ::AnalogInputStub, key)
    }

    val digitalOutput = object {
        operator fun get(key: String): DigitalOutput
                = getObject(::DigitalOutputImpl, ::DigitalOutputStub, key)
    }

    val digitalInput = object {
        operator fun get(key: String): DigitalInput
                = getObject(::DigitalInputImpl, ::DigitalInputStub, key)
    }

    private fun announceStub(key: String) {
        log.addMessage("MISSING HARDWARE DEVICE: $key", Time.seconds(60))
    }

    private inline fun <reified BASE, WRAPPER> getObject(impl: (BASE, String, Log) -> WRAPPER, stub: (String, Log) -> WRAPPER, key: String): WRAPPER {
        val iter = hwMap.iterator()
        while (iter.hasNext()) {
            val d = iter.next()
            if (d is BASE && key in d.deviceName.split('+'))
                return impl(d, key, log)
        }
        announceStub(key)
        return stub(key, log)
    }
}