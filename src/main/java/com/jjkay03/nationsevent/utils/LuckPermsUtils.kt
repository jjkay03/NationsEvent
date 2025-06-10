package com.jjkay03.nationsevent.utils

import com.jjkay03.nationsevent.NationsEvent
import net.luckperms.api.model.group.Group
import net.luckperms.api.node.Node
import net.luckperms.api.node.NodeEqualityPredicate
import net.luckperms.api.node.types.InheritanceNode
import org.bukkit.entity.Player

// UTILITY CLASS FOR LUCKPERMS RELATED FUNCTIONS
// https://luckperms.net/

object LuckPermsUtils {

    // GROUP RELATED FUNCTIONS

    // Function that gets a group by name (returns null if not found)
    fun getGroup(name: String): Group? {
        return NationsEvent.LP_GROUP_MANAGER.getGroup(name)
    }

    // Function that checks if a group has a permission
    fun groupHasPermission(group: Group?, permission: String): Boolean {
        if (group == null) return false // Return false if the group is null
        return group.nodes.any { it.key == permission && it.value }
    }

    // Function that adds a permission to a group (returns true if perm is added)
    fun groupAddPermission(group: Group?, permission: String, value: Boolean = true): Boolean {
        if (group == null) return false // Return false if the group is null
        val node = Node.builder(permission).value(value).build()
        if (group.nodes.contains(node)) return false // Return false if already has node
        group.data().add(node)
        NationsEvent.LP_GROUP_MANAGER.saveGroup(group)
        return true
    }

    // Function that removes a permission from a group (returns true if perm was removed)
    fun groupRemovePermission(group: Group?, permission: String, value: Boolean = true): Boolean {
        if (group == null) return false // Return false if the group is null
        val node = Node.builder(permission).value(value).build()
        if (!group.nodes.contains(node)) return false // Return false if already has node
        group.data().remove(node)
        NationsEvent.LP_GROUP_MANAGER.saveGroup(group)
        return true
    }


    // PLAYER RELATED FUNCTIONS

    // Function that give a player a permission
    fun playerAddPermission(player: Player, permission: String, value: Boolean = true) {
        val lpPlayer = NationsEvent.LP_USER_MANAGER.getUser(player.uniqueId) ?: return
        val node = Node.builder(permission).value(value).build()
        lpPlayer.data().add(node)
        NationsEvent.LP_USER_MANAGER.saveUser(lpPlayer)
    }

    // Function that remove a player a permission
    fun playerRemovePermission(player: Player, permission: String) {
        val lpPlayer = NationsEvent.LP_USER_MANAGER.getUser(player.uniqueId) ?: return
        val node = Node.builder(permission).value(true).build()
        lpPlayer.data().remove(node)
        NationsEvent.LP_USER_MANAGER.saveUser(lpPlayer)
    }

    // Function to check if a player is in a specific group (returns true if player in group)
    fun isPlayerInGroup(player: Player, group: Group?): Boolean {
        val lpPlayer = NationsEvent.LP_USER_MANAGER.getUser(player.uniqueId) ?: return false
        if (group == null) return false
        val inheritanceNode = InheritanceNode.builder(group.name).build()
        return lpPlayer.data().contains(inheritanceNode, NodeEqualityPredicate.IGNORE_EXPIRY_TIME).asBoolean()
    }

    // Function that adds a player to a group
    fun playerSetGroup(player: Player, group: Group?) {
        val lpPlayer = NationsEvent.LP_USER_MANAGER.getUser(player.uniqueId) ?: return
        if (group == null) return
        if (isPlayerInGroup(player, group)) return

        // Add the group as a parent to the player and save
        val inheritanceNode = InheritanceNode.builder(group.name).build()
        lpPlayer.data().add(inheritanceNode)
        NationsEvent.LP_USER_MANAGER.saveUser(lpPlayer)
    }

    // Function that removes a player from a group
    fun playerRemoveGroup(player: Player, group: Group?) {
        val lpPlayer = NationsEvent.LP_USER_MANAGER.getUser(player.uniqueId) ?: return
        if (group == null) return
        if (!isPlayerInGroup(player, group)) return

        // Remove the group from the player and save
        val inheritanceNode = InheritanceNode.builder(group.name).build()
        lpPlayer.data().remove(inheritanceNode)
        NationsEvent.LP_USER_MANAGER.saveUser(lpPlayer)
    }

}