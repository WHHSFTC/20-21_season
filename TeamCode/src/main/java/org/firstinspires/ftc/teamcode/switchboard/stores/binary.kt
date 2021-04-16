package org.firstinspires.ftc.teamcode.switchboard.stores

class AlterObservable<F, S>(initial: Pair<F, S>): PushObservable<Pair<F, S>>() {
    val first: EndPoint<F> = EndPoint(initial.first) {
        update(it to second.value)
    }

    val second: EndPoint<S> = EndPoint(initial.second) {
        update(first.value to it)
    }
}

class ZipObservable<F, S>: PushObservable<Pair<F, S>>() {
    private var f: F? = null
    private var s: S? = null

    val first = Observer<F> {
        val t = s
        f = if (t != null) {
            update(it to t)
            s = null
            null
        } else it
    }

    val second = Observer<S> {
        val t = f
        s = if (t != null) {
            update(t to it)
            f = null
            null
        } else it
    }
}

typealias KV<T> = Pair<Observable<T>, T>

infix fun <F, S> KV<F>.alt(that : KV<S>): Observable<Pair<F, S>>
    = AlterObservable<F, S>(this.second to that.second).also { this.first.subscribe(it.first); that.first.subscribe(it.second) }

infix fun <F, S> Observable<F>.zip(that: Observable<S>): Observable<Pair<F, S>>
    = ZipObservable<F, S>().also { this.subscribe(it.first); that.subscribe(it.second) }

fun <F, S> Observable<Pair<F, S>>.unzip(): Pair<Observable<F>, Observable<S>> {

}

// (gamepad1.a to false) alt (gamepad1.b to false)
// gamepad1.a zip gamepad1.b

// logical operators
infix fun Observable<Boolean>.and(that: Observable<Boolean>)
    = ((this to false) alt (that to false)).map { (f, s) -> f and s }

infix fun Observable<Boolean>.or(that: Observable<Boolean>)
        = ((this to false) alt (that to false)).map { (f, s) -> f or s }

infix fun Observable<Boolean>.xor(that: Observable<Boolean>)
        = ((this to false) alt (that to false)).map { (f, s) -> f xor s }

operator fun Observable<Boolean>.not()
        = this.map { b -> !b }

// arithmetic operators
operator fun Observable<Double>.plus(that: Observable<Double>)
        = ((this to 0.0) alt (that to 0.0)).map { (f, s) -> f + s }

operator fun Observable<Double>.minus(that: Observable<Double>)
        = ((this to 0.0) alt (that to 0.0)).map { (f, s) -> f - s }

operator fun Observable<Double>.times(that: Observable<Double>)
        = ((this to 0.0) alt (that to 0.0)).map { (f, s) -> f * s }

operator fun Observable<Double>.div(that: Observable<Double>)
        = ((this to 0.0) alt (that to 0.0)).map { (f, s) -> f / s }

operator fun Observable<Double>.rem(that: Observable<Double>)
        = ((this to 0.0) alt (that to 0.0)).map { (f, s) -> f % s }

operator fun <T> Observable<T>.unaryPlus()
        = this.map { d -> d }

operator fun Observable<Double>.unaryMinus()
        = this.map { d -> -d }


fun <T> Observable<T?>.notNull(): Observable<T>
    = this.filter { it != null }.map { it!! }