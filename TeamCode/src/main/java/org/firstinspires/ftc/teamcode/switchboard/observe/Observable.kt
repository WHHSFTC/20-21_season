package org.firstinspires.ftc.teamcode.switchboard.observe

abstract class Observable<T> {
    //private val subscribers: MutableList<(T) -> Unit> = mutableListOf()
    private val subscribers: MutableMap<Token, (T) -> Unit> = mutableMapOf()

    class Token private constructor(val id: Int) {
        companion object {
            private var next = 0
            val new get() = Token(next).also { next++ }
        }
    }

    fun subscribe(handler: (T) -> Unit): Any
        = Token.new.also { subscribers += it to handler}

    fun remove(token: Any): ((T) -> Unit)?
        = if (token is Token) subscribers.remove(token) else null

    fun refresh(v: T) {
        subscribers.forEach { it.value(v) }
    }

    infix fun <H> zip(that: Observable<H>): Observable<Pair<T, H>> = Zip(this, that)

    class Zip<F, S>(f: Observable<F>, s: Observable<S>) : Observable<Pair<F, S>>() {
        var first: F? = null
        var second: S? = null

        init {
            f.subscribe {
                val se = second
                if (se != null) {
                    refresh(it to se)
                    first = null
                    second = null
                } else {
                    first = it
                }
            }

            s.subscribe {
                val fi = first
                if (fi != null) {
                    refresh(fi to it)
                    first = null
                    second = null
                } else {
                    second = it
                }
            }
        }
    }
}

