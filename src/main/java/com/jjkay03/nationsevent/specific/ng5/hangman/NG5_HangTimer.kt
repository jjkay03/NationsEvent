package com.jjkay03.nationsevent.specific.ng5.hangman

import kotlin.math.pow

class NG5_HangTimer {

    private var time: Int = 0

    fun getTime(): Int { return time }

    fun incrementTimer() { time++ }
    fun calculateDamage(): Double {
        if (time >= 100 && time % 20 == 0) { return (time / 100).toDouble().pow(1.4) }
        return 0.0
    }

    fun resetTimer() { time = 0 }
}