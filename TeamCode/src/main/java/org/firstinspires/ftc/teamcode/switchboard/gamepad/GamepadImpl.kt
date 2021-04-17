package org.firstinspires.ftc.teamcode.switchboard.gamepad

class GamepadImpl(
        gamepad: com.qualcomm.robotcore.hardware.Gamepad,
        triggerDeadZone: Double = 0.5
) : Gamepad {
    override val keys = KeysImpl(gamepad)

    override val dpad = DPadImpl(gamepad)

    override val leftStick = JoystickImpl(
            gamepad::left_stick_x,
            gamepad::left_stick_y
    )

    override val rightStick = JoystickImpl(
            gamepad::right_stick_x,
            gamepad::right_stick_y
    )

    override val bumpers = BumpersImpl(gamepad)

    override val triggers = TriggersImpl(gamepad, triggerDeadZone)

    private val parts: List<GamepadPart> = listOf(keys, dpad, leftStick, rightStick, bumpers, triggers)

    override fun update() {
        if (!parts.map { it.same() }.reduce { acc, e -> acc and e })
            parts.forEach { it.updatePart() }
    }
}