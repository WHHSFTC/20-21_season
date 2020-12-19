package org.firstinspires.ftc.teamcode.gamepad

open class ButtonReader(
        private val buttonState: () -> Boolean,
        private var lastState: Boolean = false,
        private var currentState: Boolean = false,
): KeyReader {
    constructor(gamepad: GamepadEx, button: GamepadKeys.Button): this(buttonState = {
        gamepad[button]
    })

    constructor(buttonValue: () -> Boolean): this(buttonState = buttonValue)

    init {
        currentState = buttonState()
        lastState = currentState
    }

    override fun readValue() {
        lastState = currentState
        currentState = buttonState()
    }

    override fun isDown(): Boolean = buttonState()

    override fun wasJustPressed(): Boolean = !lastState && currentState

    override fun wasJustReleased(): Boolean = lastState && !currentState

    override fun stateJustChanged(): Boolean = lastState != currentState
}