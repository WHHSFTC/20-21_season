package org.firstinspires.ftc.teamcode.switchboard.event

data class ChannelResponse(val re: Envelope<ChannelRequest>, val channel: Channel<*>) : Event
