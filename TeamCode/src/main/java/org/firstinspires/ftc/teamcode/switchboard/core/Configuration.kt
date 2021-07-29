package org.firstinspires.ftc.teamcode.switchboard.core

import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.teamcode.switchboard.hardware.*
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time

class Configuration(val hwMap: HardwareMap, val logger: Logger) {
    interface DeviceMap<T> {
        operator fun get(key: String): T
    }

    val motors = object : DeviceMap<Motor> {
        override operator fun get(key: String): Motor
                = getOutput(::MotorImpl, ::MotorStub, key)
    }


    val servos = object : DeviceMap<Servo> {
        override operator fun get(key: String): Servo
                = getOutput(::ServoImpl, ::ServoStub, key)
    }

    val analogOutputs = object : DeviceMap<AnalogOutput> {
        override operator fun get(key: String): AnalogOutput
                = getOutput(::AnalogOutputImpl, ::AnalogOutputStub, key)
    }

    val digitalOutputs = object : DeviceMap<DigitalOutput> {
        override operator fun get(key: String): DigitalOutput
                = getOutput(::DigitalOutputImpl, ::DigitalOutputStub, key)
    }

    val encoders = object : DeviceMap<Encoder> {
        override operator fun get(key: String): Encoder
                = getInput(::EncoderImpl, ::EncoderStub, key)
    }

    val analogInputs = object : DeviceMap<AnalogInput> {
        override operator fun get(key: String): AnalogInput
                = getInput(::AnalogInputImpl, ::AnalogInputStub, key)
    }

    val digitalInputs = object : DeviceMap<DigitalInput> {
        override operator fun get(key: String): DigitalInput
                = getInput(::DigitalInputImpl, ::DigitalInputStub, key)
    }

    val webcamNames = object : DeviceMap<WebcamName?> {
        override operator fun get(key: String): WebcamName? {
            val iter = hwMap.iterator()
            while (iter.hasNext()) {
                val d = iter.next()
                if (d is WebcamName && key in hwMap.getNamesOf(d).fold(listOf<String>()) { acc, s -> acc + s.split('+') })
                    return d
            }
            return null
        }
    }

    val revHubs get() = hwMap.getAll(LynxModule::class.java)

    private val sensors: MutableSet<HardwareInput> = mutableSetOf()

    init {
        revHubs.forEach { it.bulkCachingMode = LynxModule.BulkCachingMode.MANUAL }
    }

    fun read() {
        revHubs.forEach { it.clearBulkCache() }
        sensors.forEach { it.input() }
    }

    private fun announceStub(key: String) {
        logger.addMessage("MISSING HARDWARE DEVICE: $key", Time.seconds(60))
    }


    private inline fun <reified BASE, WRAPPER : HardwareInput> getInput(impl: (BASE, String, Logger) -> WRAPPER, stub: (String, Logger) -> WRAPPER, key: String): WRAPPER {
        val iter = hwMap.iterator()
        while (iter.hasNext()) {
            val d = iter.next()
            if (d is BASE && key in hwMap.getNamesOf(d).fold(listOf<String>()) { acc, s -> acc + s.split('+') })
                return impl(d, key, logger).also { sensors += it }
        }
        announceStub(key)
        return stub(key, logger)
    }

    private inline fun <reified BASE, WRAPPER : HardwareOutput> getOutput(impl: (BASE, String, Logger) -> WRAPPER, stub: (String, Logger) -> WRAPPER, key: String): WRAPPER {
        val iter = hwMap.iterator()
        while (iter.hasNext()) {
            val d = iter.next()
            if (d is BASE && key in hwMap.getNamesOf(d).fold(listOf<String>()) { acc, s -> acc + s.split('+') })
                return impl(d, key, logger)
        }
        announceStub(key)
        return stub(key, logger)
    }
}