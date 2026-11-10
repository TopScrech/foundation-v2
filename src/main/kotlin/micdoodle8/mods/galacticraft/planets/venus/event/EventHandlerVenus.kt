package micdoodle8.mods.galacticraft.planets.venus.event

import net.minecraftforge.fml.common.gameevent.TickEvent.Phase

import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerHandler
import micdoodle8.mods.galacticraft.core.util.DamageSourceGC
import micdoodle8.mods.galacticraft.planets.venus.VenusItems
import micdoodle8.mods.galacticraft.planets.venus.VenusModule
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.relauncher.Side
import java.util.ArrayList

open class EventHandlerVenus {
    @SubscribeEvent
    open fun onWorldTick(event: TickEvent.WorldTickEvent) {
        if (event.side == Side.SERVER && event.phase == TickEvent.Phase.START) {
            val list: ArrayList<Entity> = ArrayList<Entity>(event.world.loadedEntityList)
            for (e: Entity in list) {
                if (e.ticksExisted % 20 == 1 && e is EntityLivingBase) {
                    if (event.world.isMaterialInBB(
                            e.getEntityBoundingBox().grow(-0.1, -0.4, -0.1),
                            VenusModule.acidMaterial
                        )
                    ) {
                        e.attackEntityFrom(DamageSourceGC.acid, 3.0f)
                    }
                }
            }
        }
    }

    @SubscribeEvent
    open fun onThermalArmorEvent(event: GCPlayerHandler.ThermalArmorEvent) {
        if ((event.armorStack)!!.isEmpty()) {
            event.setArmorAddResult(GCPlayerHandler.ThermalArmorEvent.ArmorAddResult.REMOVE)
        } else if ((event.armorStack)!!.getItem() === VenusItems.thermalPaddingTier2 && (event.armorStack)!!.getItemDamage() == event.armorIndex) {
            event.setArmorAddResult(GCPlayerHandler.ThermalArmorEvent.ArmorAddResult.ADD)
        }
    }
}
