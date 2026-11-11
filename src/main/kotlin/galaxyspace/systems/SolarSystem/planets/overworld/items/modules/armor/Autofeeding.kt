package galaxyspace.systems.SolarSystem.planets.overworld.items.modules.armor

import foundation.inventory.mainItems
import galaxyspace.core.prefab.items.modules.ItemModule
import galaxyspace.core.util.GSUtils.Module_Type
import micdoodle8.mods.galacticraft.core.GCItems
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.EquipmentSlot as EntityEquipmentSlot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level as World

open class Autofeeding : ItemModule() {
    override fun getName(): String {
        return "autofeeding"
    }

    override fun getIcon(): ItemStack {
        return foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.foodItem), 1, 0)
    }

    override fun getEquipmentSlot(): EntityEquipmentSlot {
        return EntityEquipmentSlot.HEAD
    }

    override fun isActiveModule(): Boolean {
        return false
    }

    override fun getItemsForModule(): Array<ItemStack?> {
        return ((arrayOf<ItemStack>(foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.foodItem), 1, 0)) as Array<ItemStack?>?))!!
    }

    override fun getForrbidenModules(): Array<ItemModule?> {
        return arrayOf<ItemModule?>()
    }

    override fun getDischargeCount(): Int {
        return 4
    }

    override fun onUpdate(world: World?, player: EntityPlayer?, itemStack: ItemStack?, enable: Boolean) {
        val level = world ?: return
        val wearer = player ?: return
        if (!level.isClientSide && foundation.item.FoundationFoods.consumeFirst(wearer.inventory.mainItems, wearer.foodData)) {
            wearer.inventory.setChanged()
        }
    }

    override fun getType(): Module_Type {
        return Module_Type.SPACESUIT
    }
}
