package galaxyspace.systems.SolarSystem.planets.overworld.items.modules.armor

import galaxyspace.core.prefab.items.modules.ItemModule
import galaxyspace.core.util.GSUtils.Module_Type
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.init.MobEffects
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.potion.PotionEffect
import net.minecraft.potion.PotionType
import net.minecraft.potion.PotionUtils
import net.minecraft.world.World

open class Nightvision : ItemModule() {
    override fun getName(): String {
        return "nightvision"
    }

    override fun getIcon(): ItemStack {
        val potion: ItemStack = ItemStack(Items.POTIONITEM)
        PotionUtils.addPotionToItemStack(potion, PotionType.getPotionTypeForName("long_night_vision"))

        return potion
    }

    override fun getEquipmentSlot(): EntityEquipmentSlot {
        return EntityEquipmentSlot.HEAD
    }

    override fun isActiveModule(): Boolean {
        return true
    }

    override fun getItemsForModule(): Array<ItemStack?> {
        val potion: ItemStack = ItemStack(Items.POTIONITEM)
        PotionUtils.addPotionToItemStack(potion, PotionType.getPotionTypeForName("long_night_vision"))
        return ((arrayOf<ItemStack>(potion) as Array<ItemStack?>?))!!
    }

    override fun getForrbidenModules(): Array<ItemModule?> {
        return ((arrayOf<ItemModule>(SensorLens()) as Array<ItemModule?>?))!!
    }

    override fun getType(): Module_Type {
        return Module_Type.SPACESUIT
    }

    override fun getDischargeCount(): Int {
        return 4
    }

    override fun onUpdate(world: World?, player: EntityPlayer?, itemStack: ItemStack?, enable: Boolean) {
        if (enable) (player)!!.addPotionEffect(PotionEffect(MobEffects.NIGHT_VISION, 20 * 15, 0, true, false))
        else {
            if ((player)!!.isPotionActive(MobEffects.NIGHT_VISION)) (player)!!.removeActivePotionEffect(MobEffects.NIGHT_VISION)
        }
    }
}
