package com.jjkay03.nationsevent.specific.ng5.hangman_gonkas

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.entity.Zombie
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team

class NG5_Hanged(private val owner: Player, private val leashed: Player, private val ghost: Zombie) {

    private var time: Int = 0

    init { getOrCreateGhostsTeam()?.addEntities(ghost) }

    fun getOwner(): Player { return owner }
    fun getLeashed(): Player { return leashed }
    fun getGhost(): Zombie { return ghost }

    // Function to check if a player is hanging
    private fun isHanging(): Boolean { return getBlockUnderPlayer(leashed).type == Material.AIR }

    // Function teleports leashed to ghost's location while still allowing head movement
    private fun teleport() { leashed.teleport(Location(ghost.world, ghost.x, ghost.y, ghost.z, leashed.yaw, leashed.pitch)) }

    // Tick function
    fun tick() {
        if (isHanging()) { if (time >= 100 && time % 20 == 0) { leashed.addPotionEffect(withering) }; time++ }
        else { leashed.removePotionEffect(PotionEffectType.WITHER); time = 0 }
        if (!ghost.isDead) { teleport() }
    }

    // Function to delete ghost entity
    fun delete() {
        ghosts!!.removeEntities(ghost)
        ghost.remove()
    }

    companion object {

        private var scoreboard: Scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        var ghosts: Team? = scoreboard.getTeam("HangManGhosts")
        val withering: PotionEffect = PotionEffect(PotionEffectType.WITHER, 40, 4, false, false, false)

        // Function to get or create ghost team
        fun getOrCreateGhostsTeam(): Team? {
            if (ghosts != null) return ghosts

            ghosts = scoreboard.registerNewTeam("HangManGhosts")
            ghosts!!.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER)
            return ghosts
        }

        // Function to get blow bellow player
        fun getBlockUnderPlayer(player: Player): Block {
            return player.world.getBlockAt(Location(player.world, player.location.x, player.location.y - 1, player.location.z))
        }
    }
}