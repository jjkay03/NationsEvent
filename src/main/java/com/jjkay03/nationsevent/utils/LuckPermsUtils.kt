package com.jjkay03.nationsevent.utils

import com.jjkay03.nationsevent.NationsEvent
import net.luckperms.api.model.group.Group
import net.luckperms.api.node.Node
import org.bukkit.entity.Player

object LuckPermsUtils {

    // Function that checks if a LuckPerms group has a permission
    fun groupHasPermission(group: Group?, permission: String): Boolean {
        if (group == null) return false // Return false if the group is null
        return group.nodes.any { it.key == permission && it.value }
    }

    // Function that give a player a permission using LuckPerms
    fun playerAddPermission(player: Player, permission: String) {
        val lpPlayer = NationsEvent.LP_USER_MANAGER.getUser(player.uniqueId)
        if (lpPlayer == null) return
        val node = Node.builder(permission).value(true).build()
        lpPlayer.data().add(node)
        NationsEvent.LP_USER_MANAGER.saveUser(lpPlayer)
    }

    // Function that remove a player a permission using LuckPerms
    fun playerRemovePermission(player: Player, permission: String) {
        val lpPlayer = NationsEvent.LP_USER_MANAGER.getUser(player.uniqueId)
        if (lpPlayer == null) return
        val node = Node.builder(permission).value(true).build()
        lpPlayer.data().remove(node)
        NationsEvent.LP_USER_MANAGER.saveUser(lpPlayer)
    }

}