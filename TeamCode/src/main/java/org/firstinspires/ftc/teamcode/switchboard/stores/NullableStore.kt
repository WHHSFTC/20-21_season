package org.firstinspires.ftc.teamcode.switchboard.stores

class NullableStore<P>: Subject<P, P>() {
    var value: P? = null

    override fun next(x: P) {
        this.value = x
        update(x)
    }
}