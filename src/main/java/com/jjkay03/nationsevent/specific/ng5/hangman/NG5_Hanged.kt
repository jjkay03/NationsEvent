package com.jjkay03.nationsevent.specific.ng5.hangman

import org.bukkit.GameMode
import org.bukkit.entity.Pig
import org.bukkit.entity.Player

class NG5_Hanged(private val owner: Player, private val leashed: Player, private val ghost: Pig) {

    private var timer: NG5_HangTimer = NG5_HangTimer()

    fun getOwner(): Player {return owner}
    fun getLeashed(): Player {return leashed}
    fun getGhost(): Pig {return ghost}
    fun getTimer(): NG5_HangTimer {return timer}

    fun isHanging(): Boolean { return leashed.isFlying && leashed.gameMode == GameMode.SURVIVAL }

    fun damage() { leashed.damage(timer.calculateDamage(), NG5_HangMan.getHangedDamageSource()) }
    fun teleport() { leashed.teleport(ghost) }

    fun tick() {
        if (isHanging()) {
            timer.incrementTimer()
            damage()
        }
        teleport()
    }

    fun delete() {
        leashed.remove()
        ghost.remove()
    }
}