package galaxyspace.systems.SolarSystem.planets.overworld.items.modules.armor

import galaxyspace.core.prefab.items.modules.ItemModule
import galaxyspace.core.util.GSUtils.Module_Type
import micdoodle8.mods.galacticraft.core.GCItems
import net.minecraft.world.entity.EquipmentSlot as EntityEquipmentSlot
import net.minecraft.world.item.ItemStack

open class SensorLens : ItemModule() {
    override fun getName(): String {
        return "sensor"
    }

    override fun getIcon(): ItemStack {
        return ItemStack(GCItems.sensorGlasses)
    }

    override fun getEquipmentSlot(): EntityEquipmentSlot {
        return EntityEquipmentSlot.HEAD
    }

    override fun isActiveModule(): Boolean {
        return true
    }

    override fun getItemsForModule(): Array<ItemStack?> {
        return ((arrayOf<ItemStack>(ItemStack(GCItems.sensorGlasses, 1, 0)) as Array<ItemStack?>?))!!
    }

    override fun getForrbidenModules(): Array<ItemModule?> {
        return ((arrayOf<ItemModule>(Nightvision()) as Array<ItemModule?>?))!!
    }

    override fun getType(): Module_Type {
        return Module_Type.SPACESUIT
    }

    override fun getDischargeCount(): Int {
        return 2
    }
}
