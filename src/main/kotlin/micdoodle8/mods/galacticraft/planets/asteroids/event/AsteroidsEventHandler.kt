package micdoodle8.mods.galacticraft.planets.asteroids.event

import foundation.item.legacyItemValue

import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerHandler.ThermalArmorEvent

import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerHandler
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import net.minecraftforge.eventbus.api.listener.SubscribeEvent

open class AsteroidsEventHandler {
    @SubscribeEvent
    open fun onThermalArmorEvent(event: GCPlayerHandler.ThermalArmorEvent) {
        if ((event.armorStack)!!.isEmpty()) {
            event.setArmorAddResult(GCPlayerHandler.ThermalArmorEvent.ArmorAddResult.REMOVE)
        } else if ((event.armorStack)!!.getItem() === AsteroidsItems.thermalPadding && (event.armorStack)!!.legacyItemValue == event.armorIndex) {
            event.setArmorAddResult(GCPlayerHandler.ThermalArmorEvent.ArmorAddResult.ADD)
        }
    }
}
