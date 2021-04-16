package org.firstinspires.ftc.teamcode.summum

import org.firstinspires.ftc.teamcode.geometry.Pose2d
import org.firstinspires.ftc.teamcode.switchboard.stores.Observable

interface Localizer {
    val pose: Observable<Pose2d>
    val twist: Observable<Pose2d>
}