package org.firstinspires.ftc.teamcode.gamepad

class TriggerReader(
        private val triggerState: () -> Boolean,
        private var lastState: Boolean = false,
        private var currentState: Boolean = false,
): KeyReader {
    constructor(gamepad: GamepadEx, trigger: GamepadKeys.Trigger): this(triggerState = {
        gamepad[trigger] > 0.5
    })

    constructor(buttonValue: () -> Boolean): this(triggerState = buttonValue)

    init {
        currentState = triggerState()
        lastState = currentState
    }

    override fun readValue() {
        lastState = currentState
        currentState = triggerState()
    }

    override fun isDown(): Boolean = triggerState()

    override fun wasJustPressed(): Boolean = !lastState && currentState

    override fun wasJustReleased(): Boolean = lastState && !currentState

    override fun stateJustChanged(): Boolean = lastState != currentState
}