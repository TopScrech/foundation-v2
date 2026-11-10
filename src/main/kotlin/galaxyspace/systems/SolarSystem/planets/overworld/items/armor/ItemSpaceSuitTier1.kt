package galaxyspace.systems.SolarSystem.planets.overworld.items.armor

import galaxyspace.systems.SolarSystem.planets.overworld.items.modules.armor.Autofeeding
import galaxyspace.systems.SolarSystem.planets.overworld.items.modules.armor.Gravity
import galaxyspace.systems.SolarSystem.planets.overworld.items.modules.armor.Jetpack
import galaxyspace.systems.SolarSystem.planets.overworld.items.modules.armor.Nightvision
import galaxyspace.systems.SolarSystem.planets.overworld.items.modules.armor.Protection
import galaxyspace.systems.SolarSystem.planets.overworld.items.modules.armor.SensorLens
import galaxyspace.systems.SolarSystem.planets.overworld.items.modules.armor.Stepassist
import galaxyspace.systems.SolarSystem.planets.overworld.items.modules.armor.WaterBreathing

import asmodeuscore.api.item.IItemPressurized
import asmodeuscore.api.item.IItemRadiation
import galaxyspace.core.prefab.items.modules.ItemModule
import galaxyspace.systems.SolarSystem.planets.overworld.items.modules.Energy
import galaxyspace.systems.SolarSystem.planets.overworld.items.modules.armor.*
import micdoodle8.mods.galacticraft.api.item.IArmorCorrosionResistant
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.fml.client.FMLClientHandler

open class ItemSpaceSuitTier1 constructor(materialIn: ArmorMaterial?, armorIndex: EntityEquipmentSlot?) :
    ItemSpaceSuit("space_suit", materialIn, (armorIndex)!!), IArmorCorrosionResistant, IItemPressurized, IItemRadiation {
    override fun getAvailableModules(): Array<ItemModule?> {
        return ((arrayOf<ItemModule>(
            Autofeeding(),
            SensorLens(),
            WaterBreathing(),
            Nightvision(),
            Jetpack(),
            Gravity(),
            Stepassist(),
            Energy(),
            Protection()
        ) as Array<ItemModule?>?))!!
    }

    override fun getSolarRadiationProtectModify(): Float {
        return 1.0f
    }

    override fun addInfo(stack: ItemStack?, world: World?, list: MutableList<String?>?, flagIn: ITooltipFlag?) {
        (list)!!.add(GCCoreUtil.translate("gui.spacesuit.type") + " " + EnumColor.YELLOW + GCCoreUtil.translate("gui.spacesuit.type.2"))
        (list)!!.addAll(
            FMLClientHandler.instance().getClient().fontRenderer.listFormattedStringToWidth(
                GCCoreUtil.translate("gui.spacesuit.desc.heavy"),
                250
            )
        )
    }

    public override fun getMaxElectricityStored(stack: ItemStack?): Float {
        val energy: Boolean = (stack)!!.hasTagCompound() && (stack)!!.getTagCompound()!!.hasKey("energy")
        return if (energy) 180000.0f else 90000.0f
    }
}
