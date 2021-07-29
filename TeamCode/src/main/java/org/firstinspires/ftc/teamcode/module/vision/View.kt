package org.firstinspires.ftc.teamcode.module.vision

import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

class View (
    val name: String,
    val color: Scalar = Scalar(255.0, 0.0, 0.0)
) : Mat() {
    var labeledMat: Mat = Mat()
        get() {
            this.copyTo(field)
            Imgproc.putText(field, name, Point(0.0, 0.0), Imgproc.FONT_HERSHEY_PLAIN, 20.0, color)
            return field
        }
}

