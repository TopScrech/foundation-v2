package galaxyspace.systems.SolarSystem.planets.overworld.items.modules.armor

import galaxyspace.core.prefab.items.modules.ItemModule
import galaxyspace.core.util.GSUtils.Module_Type
import micdoodle8.mods.galacticraft.core.GCItems
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemFood
import net.minecraft.item.ItemStack
import net.minecraft.world.World

open class Autofeeding : ItemModule() {
    override fun getName(): String {
        return "autofeeding"
    }

    override fun getIcon(): ItemStack {
        return ItemStack(GCItems.foodItem, 1, 0)
    }

    override fun getEquipmentSlot(): EntityEquipmentSlot {
        return EntityEquipmentSlot.HEAD
    }

    override fun isActiveModule(): Boolean {
        return false
    }

    override fun getItemsForModule(): Array<ItemStack?> {
        return ((arrayOf<ItemStack>(ItemStack(GCItems.foodItem, 1, 0)) as Array<ItemStack?>?))!!
    }

    override fun getForrbidenModules(): Array<ItemModule?> {
        return arrayOf<ItemModule?>()
    }

    override fun getDischargeCount(): Int {
        return 4
    }

    override fun onUpdate(world: World?, player: EntityPlayer?, itemStack: ItemStack?, enable: Boolean) {
        if (!(world)!!.isRemote && (player)!!.getFoodStats().needFood()) {
            for (stack: ItemStack in (player)!!.inventory.mainInventory) {
                if (stack.getItem() is ItemFood) {
                    (player)!!.getFoodStats().addStats(stack.getItem() as ItemFood, stack)
                    stack.shrink(1)
                    break
                }
            }
        }
    }

    override fun getType(): Module_Type {
        return Module_Type.SPACESUIT
    }
}
