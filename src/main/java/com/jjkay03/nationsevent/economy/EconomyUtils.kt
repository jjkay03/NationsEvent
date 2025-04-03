package com.jjkay03.nationsevent.economy

import com.jjkay03.nationsevent.NationsEvent
import com.jjkay03.nationsevent.Saves
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File

object EconomyUtils {

    // Function to create player yml containing player balance
    fun createPlayerBalanceFile(player: Player, startingBalance: Int) {
        NationsEvent.INSTANCE.logger.info("Creating player balance file for ${player.name} with starting balance $startingBalance")
        val file = File(Saves.DIR_ECONOMY_BALANCES,"${player.uniqueId}.yml")
        if (file.exists()) return // End if file exist
        val config = YamlConfiguration()
        config.set(Economy.PLAYER_BALANCE_FILE_KEY_IGN, player.name)
        config.set(Economy.PLAYER_BALANCE_FILE_KEY_BALANCE, startingBalance)
        config.save(file)
    }

}