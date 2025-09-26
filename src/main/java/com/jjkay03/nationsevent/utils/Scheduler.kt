package com.jjkay03.nationsevent.utils

import com.jjkay03.nationsevent.NationsEvent
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import io.papermc.paper.threadedregions.scheduler.ScheduledTask

object Scheduler {
    private val INSTANCE = NationsEvent.INSTANCE

    enum class SchedulerType {
        GLOBAL,
        PLAYER,
        REGION,
        ENTITY
    }

    // Function to schedule immediate task
    fun task(type: SchedulerType, task: () -> Unit, player: Player? = null, location: Location? = null, entity: Entity? = null): Any? {
        return schedule(type, task, player, location, entity)
    }

    // Function to schedule delayed task
    fun taskDelayed(type: SchedulerType, delayTicks: Long, task: () -> Unit, player: Player? = null, location: Location? = null, entity: Entity? = null): Any? {
        return schedule(type, task, player, location, entity, delayTicks)
    }

    // Function to schedule repeating task
    fun taskRepeating(type: SchedulerType, delayTicks: Long, periodTicks: Long, task: () -> Unit, player: Player? = null, location: Location? = null, entity: Entity? = null): Any? {
        return schedule(type, task, player, location, entity, delayTicks, periodTicks)
    }

    // Helper function to handle all scheduling logic
    private fun schedule(type: SchedulerType, task: () -> Unit, player: Player?, location: Location?, entity: Entity?, delayTicks: Long = 0, periodTicks: Long = 0): Any? {
        return if (ServerType.SERVER_TYPE == ServerType.ThreadingType.MULTI_THREADED_FOLIA) {
            when (type) {
                SchedulerType.GLOBAL -> scheduleGlobal(task, delayTicks, periodTicks)
                SchedulerType.PLAYER -> schedulePlayer(requireNotNull(player) { "Player cannot be null" }, task, delayTicks, periodTicks)
                SchedulerType.REGION -> scheduleRegion(requireNotNull(location) { "Location cannot be null" }, task, delayTicks, periodTicks)
                SchedulerType.ENTITY -> scheduleEntity(requireNotNull(entity) { "Entity cannot be null" }, task, delayTicks, periodTicks)
            }
        } else {
            scheduleBasicBukkit(task, delayTicks, periodTicks)
        }
    }

    // Helper function to handle global region scheduling
    private fun scheduleGlobal(task: () -> Unit, delay: Long, period: Long): ScheduledTask {
        val scheduler = Bukkit.getGlobalRegionScheduler()
        return when {
            period > 0 -> scheduler.runAtFixedRate(INSTANCE, { task() }, delay, period)
            delay > 0 -> scheduler.runDelayed(INSTANCE, { task() }, delay)
            else -> scheduler.run(INSTANCE) { task() }
        }
    }

    // Helper function to handle player scheduling
    private fun schedulePlayer(player: Player, task: () -> Unit, delay: Long, period: Long): ScheduledTask? {
        val scheduler = player.scheduler
        return when {
            period > 0 -> scheduler.runAtFixedRate(INSTANCE, { task() }, null, delay, period)
            delay > 0 -> scheduler.runDelayed(INSTANCE, { task() }, null, delay)
            else -> scheduler.run(INSTANCE, { task() }, null)
        }
    }

    // Helper function to handle region scheduling
    private fun scheduleRegion(location: Location, task: () -> Unit, delay: Long, period: Long): ScheduledTask {
        val scheduler = Bukkit.getRegionScheduler()
        return when {
            period > 0 -> scheduler.runAtFixedRate(INSTANCE, location, { task() }, delay, period)
            delay > 0 -> scheduler.runDelayed(INSTANCE, location, { task() }, delay)
            else -> scheduler.run(INSTANCE, location) { task() }
        }
    }

    // Helper function to handle entity scheduling
    private fun scheduleEntity(entity: Entity, task: () -> Unit, delay: Long, period: Long): ScheduledTask? {
        val scheduler = entity.scheduler
        return when {
            period > 0 -> scheduler.runAtFixedRate(INSTANCE, { task() }, null, delay, period)
            delay > 0 -> scheduler.runDelayed(INSTANCE, { task() }, null, delay)
            else -> scheduler.run(INSTANCE, { task() }, null)
        }
    }

    // Helper function to handle basic Bukkit scheduling
    private fun scheduleBasicBukkit(task: () -> Unit, delay: Long, period: Long): BukkitTask {
        val scheduler = Bukkit.getScheduler()
        val runnable = Runnable { task() }
        return when {
            period > 0 -> scheduler.runTaskTimer(INSTANCE, runnable, delay, period)
            delay > 0 -> scheduler.runTaskLater(INSTANCE, runnable, delay)
            else -> scheduler.runTask(INSTANCE, runnable)
        }
    }

    // Helper function to cancel tasks
    fun cancelTask(task: Any?) {
        when (task) {
            is ScheduledTask -> task.cancel()
            is BukkitTask -> task.cancel()
        }
    }
}
