package org.firstinspires.ftc.teamcode.switchboard.core

import com.qualcomm.hardware.lynx.LynxModule
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.teamcode.switchboard.hardware.*
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time

class Config(val hwMap: HardwareMap, val log: Logger) {
    interface DeviceMap<T> {
        operator fun get(key: String): T
    }

    val motors = object : DeviceMap<Motor> {
        override operator fun get(key: String): Motor
                = getObject(::MotorImpl, ::MotorStub, key)
    }


    val servos = object : DeviceMap<Servo> {
        override operator fun get(key: String): Servo
                = getObject(::ServoImpl, ::ServoStub, key)
    }

    val analogOutputs = object : DeviceMap<AnalogOutput> {
        override operator fun get(key: String): AnalogOutput
                = getObject(::AnalogOutputImpl, ::AnalogOutputStub, key)
    }

    val digitalOutputs = object : DeviceMap<DigitalOutput> {
        override operator fun get(key: String): DigitalOutput
                = getObject(::DigitalOutputImpl, ::DigitalOutputStub, key)
    }

    val encoders = object : DeviceMap<Encoder> {
        override operator fun get(key: String): Encoder
                = getObjectAndRead(::EncoderImpl, ::EncoderStub, key)
    }

    val analogInputs = object : DeviceMap<AnalogInput> {
        override operator fun get(key: String): AnalogInput
                = getObjectAndRead(::AnalogInputImpl, ::AnalogInputStub, key)
    }

    val digitalInputs = object : DeviceMap<DigitalInput> {
        override operator fun get(key: String): DigitalInput
                = getObjectAndRead(::DigitalInputImpl, ::DigitalInputStub, key)
    }

    val revHubs get() = hwMap.getAll(LynxModule::class.java)

    val sensors: MutableList<HardwareInput> = mutableListOf()

    init {
        revHubs.forEach { it.bulkCachingMode = LynxModule.BulkCachingMode.MANUAL }
    }

    fun read() {
        revHubs.forEach { it.clearBulkCache() }
        sensors.forEach { it.input() }
    }

    private fun announceStub(key: String) {
        log.addMessage("MISSING HARDWARE DEVICE: $key", Time.seconds(60))
    }

    private inline fun <reified BASE, WRAPPER : HardwareInput> getObjectAndRead(impl: (BASE, String, Logger) -> WRAPPER, stub: (String, Logger) -> WRAPPER, key: String): WRAPPER
        = getObject(impl, stub, key).also { sensors += it }

    private inline fun <reified BASE, WRAPPER> getObject(impl: (BASE, String, Logger) -> WRAPPER, stub: (String, Logger) -> WRAPPER, key: String): WRAPPER {
        val iter = hwMap.iterator()
        while (iter.hasNext()) {
            val d = iter.next()
            if (d is BASE && key in hwMap.getNamesOf(d).fold(listOf<String>()) { acc, s -> acc + s.split('+') })
                return impl(d, key, log)
        }
        announceStub(key)
        return stub(key, log)
    }
}