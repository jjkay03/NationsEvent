package com.jjkay03.nationsevent.specific.ng5.hangman

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.entity.Zombie
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team

class NG5_Hanged(private val owner: Player, private val leashed: Player, private val ghost: Zombie) {

    private var timer: NG5_HangTimer = NG5_HangTimer()

    init { getOrCreateGhostsTeam()?.addEntities(ghost) }

    fun getOwner(): Player {return owner}
    fun getLeashed(): Player {return leashed}
    fun getGhost(): Zombie {return ghost}
    fun getTimer(): NG5_HangTimer {return timer}

    fun isHanging(): Boolean { return getBlockUnderPlayer(leashed).type == Material.AIR }

    fun damageHanged() { leashed.damage(timer.calculateDamage(), NG5_HangMan.getHangedDamageSource()) }

    // Teleports leashed to ghost's location while still allowing head movement
    fun teleport() { leashed.teleport(ghost.location.toVector().toLocation(ghost.world, leashed.yaw, leashed.pitch)) }

    fun tick() {
        if (isHanging()) {
            timer.incrementTimer()
            damageHanged()
        } else { timer.resetTimer() }
        teleport()
    }

    fun delete() {
        ghosts?.removeEntities(ghost)
        ghost.remove()
    }

    companion object {

        var scoreboard: Scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        var ghosts: Team? = scoreboard.getTeam("HangManGhosts")

        fun getOrCreateGhostsTeam(): Team? {
            if (ghosts != null) return ghosts

            ghosts = scoreboard.registerNewTeam("HangManGhosts")
            ghosts!!.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER)
            return ghosts
        }

        fun getBlockUnderPlayer(player: Player): Block {
            return player.world.getBlockAt(Location(player.world, player.location.x, player.location.y - 1, player.location.z))
        }
    }
}