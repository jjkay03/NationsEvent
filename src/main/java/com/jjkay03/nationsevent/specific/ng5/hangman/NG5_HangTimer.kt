package com.jjkay03.nationsevent.specific.ng5.hangman

import kotlin.math.pow

class NG5_HangTimer {

    private var time: Int = 0

    fun getTime(): Int { return time }

    fun incrementTimer() { time++ }
    fun calculateDamage(): Double {
        if (time > 5) { return time.toDouble().pow(1.3) }
        return 0.0
    }
}