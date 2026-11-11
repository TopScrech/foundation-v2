package galaxyspace.systems.SolarSystem.planets.overworld.items.modules.armor

import galaxyspace.core.prefab.items.modules.ItemModule
import galaxyspace.core.util.GSUtils.Module_Type
import galaxyspace.systems.SolarSystem.planets.overworld.items.armor.ItemSpaceSuit
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.Items
import net.minecraft.world.entity.EquipmentSlot as EntityEquipmentSlot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level as World

open class Stepassist : ItemModule() {
    private var energy_cost = 0

    override fun getName(): String {
        return "stepassist"
    }

    override fun getIcon(): ItemStack {
        return ItemStack(Items.SLIME_BALL, 1, 0)
    }

    override fun getEquipmentSlot(): EntityEquipmentSlot {
        return EntityEquipmentSlot.FEET
    }

    override fun isActiveModule(): Boolean {
        return false
    }

    override fun getItemsForModule(): Array<ItemStack?> {
        return ((arrayOf<ItemStack>(ItemStack(Items.SLIME_BALL, 4, 0)) as Array<ItemStack?>?))!!
    }

    override fun getForrbidenModules(): Array<ItemModule?>? {
        return null
    }

    override fun getType(): Module_Type {
        return Module_Type.SPACESUIT
    }

    override fun getDischargeCount(): Int {
        return energy_cost
    }

    override fun onUpdate(world: World?, player: EntityPlayer?, itemStack: ItemStack?, enable: Boolean) {
        var step: Float = 0f

        if (ItemSpaceSuit.Companion.pressedKey[1]) {
            step = 0.5f
            energy_cost = 4
        } else {
            step = 0f
            energy_cost = 0
        }


        (player)!!.stepHeight += step
    }
}
