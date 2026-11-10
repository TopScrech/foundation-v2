package galaxyspace.systems.SolarSystem.planets.overworld.items.modules.armor

import galaxyspace.core.prefab.items.modules.ItemModule
import galaxyspace.core.util.GSUtils.Module_Type
import galaxyspace.systems.SolarSystem.planets.overworld.items.armor.ItemSpaceSuit
import micdoodle8.mods.galacticraft.core.GCItems
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.world.World

open class Speed : ItemModule() {
    override fun getName(): String {
        return "speed"
    }

    override fun getIcon(): ItemStack {
        return ItemStack(GCItems.rocketEngine, 1, 1)
    }

    override fun getEquipmentSlot(): EntityEquipmentSlot {
        return EntityEquipmentSlot.LEGS
    }

    override fun isActiveModule(): Boolean {
        return true
    }

    override fun getItemsForModule(): Array<ItemStack?> {
        return ((arrayOf<ItemStack>(ItemStack(GCItems.rocketEngine, 2, 1)) as Array<ItemStack?>?))!!
    }

    override fun getForrbidenModules(): Array<ItemModule?>? {
        return null
    }

    override fun getType(): Module_Type {
        return Module_Type.SPACESUIT
    }

    override fun getDischargeCount(): Int {
        return 4
    }

    override fun onUpdate(world: World?, player: EntityPlayer?, itemStack: ItemStack?, enable: Boolean) {
        if (enable && ItemSpaceSuit.Companion.pressedKey[1]) {
            val speed: Float = 0.12f
            if ((player)!!.onGround && !(player)!!.isInWater()) (player)!!.moveRelative(0.0f, 0.0f, 1.0f, speed)
        }
    }
}
