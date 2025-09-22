package com.jjkay03.nationsevent.utils

import org.bukkit.Bukkit

object ServerType {

    var SERVER_TYPE: ThreadingType = ThreadingType.SINGLE_THREADED
    var MINECRAFT_VERSION: MinecraftVersion = MinecraftVersion.UNKNOWN
    var CHECK_FOLIA_CLASS = "io.papermc.paper.threadedregions.RegionizedServer"

    enum class ThreadingType { SINGLE_THREADED, MULTI_THREADED }
    enum class MinecraftVersion { V1_20, V1_21, UNKNOWN }

    // Function to run detections
    fun detect() {
        SERVER_TYPE = detectThreadingType()
        MINECRAFT_VERSION = detectMinecraftVersion()
    }

    // Function to detect server threading type
    private fun detectThreadingType(): ThreadingType {
        return try { Class.forName(CHECK_FOLIA_CLASS); ThreadingType.MULTI_THREADED }
        catch (e: ClassNotFoundException) { ThreadingType.SINGLE_THREADED }
    }

    // Function to detect server Minecraft version
    private fun detectMinecraftVersion(): MinecraftVersion {
        return try {
            val version = Bukkit.getVersion()
            when {
                version.contains("1.20") -> MinecraftVersion.V1_20
                version.contains("1.21") -> MinecraftVersion.V1_21
                else -> MinecraftVersion.UNKNOWN
            }
        } catch (e: Exception) {
            MinecraftVersion.UNKNOWN
        }
    }
}
