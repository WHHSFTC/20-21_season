package org.firstinspires.ftc.teamcode.switchboard.command

import org.firstinspires.ftc.teamcode.switchboard.core.Frame
import org.firstinspires.ftc.teamcode.switchboard.shapes.Time

class DelayCommand(val duration: Time) : Command {
    var start = Time.zero
    override var done: Boolean = false
    
    override fun load(frame: Frame) {
        start = frame.time
    }

    override fun update(frame: Frame) {
        if (!done && frame.time - start > duration)
            done = true
    }
}