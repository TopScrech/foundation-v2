package micdoodle8.mods.galacticraft.planets.venus.event

import foundation.item.legacyItemValue

import net.minecraftforge.fml.common.gameevent.TickEvent.Phase

import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerHandler
import micdoodle8.mods.galacticraft.core.util.DamageSourceGC
import micdoodle8.mods.galacticraft.planets.venus.VenusItems
import micdoodle8.mods.galacticraft.planets.venus.VenusModule
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraftforge.eventbus.api.listener.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.LogicalSide as Side
import java.util.ArrayList

open class EventHandlerVenus {
    @SubscribeEvent
    open fun onWorldTick(event: TickEvent.WorldTickEvent) {
        if (event.side == Side.SERVER && event.phase == TickEvent.Phase.START) {
            val list: ArrayList<Entity> = ArrayList<Entity>(event.world.loadedEntityList)
            for (e: Entity in list) {
                if (e.tickCount % 20 == 1 && e is EntityLivingBase) {
                    if (event.world.isMaterialInBB(
                            e.getBoundingBox().grow(-0.1, -0.4, -0.1),
                            VenusModule.acidMaterial
                        )
                    ) {
                        foundation.damage.FoundationDamageTypes.hurt(e, DamageSourceGC.acid, 3.0f)
                    }
                }
            }
        }
    }

    @SubscribeEvent
    open fun onThermalArmorEvent(event: GCPlayerHandler.ThermalArmorEvent) {
        if ((event.armorStack)!!.isEmpty()) {
            event.setArmorAddResult(GCPlayerHandler.ThermalArmorEvent.ArmorAddResult.REMOVE)
        } else if ((event.armorStack)!!.getItem() === VenusItems.thermalPaddingTier2 && (event.armorStack)!!.legacyItemValue == event.armorIndex) {
            event.setArmorAddResult(GCPlayerHandler.ThermalArmorEvent.ArmorAddResult.ADD)
        }
    }
}
