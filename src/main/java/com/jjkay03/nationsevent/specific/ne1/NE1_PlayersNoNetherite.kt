package com.jjkay03.nationsevent.specific.ne1

import com.jjkay03.nationsevent.Saves
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.SmithItemEvent
import org.bukkit.inventory.ItemStack

class NE1_PlayersNoNetherite : Listener {

    private val disabledNetheriteItems = setOf(
        Material.NETHERITE_HELMET,
        Material.NETHERITE_CHESTPLATE,
        Material.NETHERITE_LEGGINGS,
        Material.NETHERITE_BOOTS
    )

    @EventHandler
    fun onSmithItem(event: SmithItemEvent) {
        val player = event.view.player

        // Allow players with specific permissions to bypass restriction
        if (player.hasPermission(Saves.PERM_PROD)) return
        if (player.hasPermission(NE1_SeasonSpecific.PERM_CLOWNPIERCE)) return

        val result: ItemStack = event.inventory.result ?: return
        if (result.type in disabledNetheriteItems) {
            event.isCancelled = true
            player.sendMessage("§8✖ Netherite armor crafting is disabled!")
        }
    }
}
