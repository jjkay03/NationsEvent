package com.jjkay03.nationsevent.economy

import org.bukkit.Material

enum class EconomyItemsValues(
    val item: Material,
    val value: Long
) {
    GOLD_INGOT(Material.GOLD_INGOT, 9),
    GOLD_BLOCK(Material.GOLD_BLOCK, 81),
    GOLD_NUGGET(Material.GOLD_NUGGET, 1),

    DIAMOND(Material.DIAMOND, 5),
    DIAMOND_BLOCK(Material.DIAMOND_BLOCK, 45),

    IRON_INGOT(Material.IRON_INGOT, 1),
    IRON_BLOCK(Material.IRON_BLOCK, 9),

    EMERALD(Material.EMERALD, 20),
    EMERALD_BLOCK(Material.EMERALD_BLOCK, 180)
}