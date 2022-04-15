package de.d151l.place.plugin.listener

import de.d151l.place.plugin.Place
import de.d151l.place.plugin.material.MaterialChecker
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPhysicsEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerArmorStandManipulateEvent
import org.bukkit.event.player.PlayerInteractEvent

/**
 * @created 15/04/2022 - 22:05
 * @project R-Place
 * @author  D151l
 */
class ProtectionListener(
    private val place: Place
): Listener {

    @EventHandler
    fun onArmorStand(event: PlayerArmorStandManipulateEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun onPhysics(event: BlockPhysicsEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun onEntitySpawn(event: EntitySpawnEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val clickedBlock = event.clickedBlock
        if (clickedBlock != null)
            return
        event.isCancelled = true
    }
}