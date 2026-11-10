package galaxyspace.systems.SolarSystem.planets.overworld.items.armor

import galaxyspace.systems.SolarSystem.planets.overworld.items.modules.armor.Autofeeding
import galaxyspace.systems.SolarSystem.planets.overworld.items.modules.armor.Jump
import galaxyspace.systems.SolarSystem.planets.overworld.items.modules.armor.Nightvision
import galaxyspace.systems.SolarSystem.planets.overworld.items.modules.armor.SensorLens
import galaxyspace.systems.SolarSystem.planets.overworld.items.modules.armor.Speed
import galaxyspace.systems.SolarSystem.planets.overworld.items.modules.armor.WaterBreathing

import asmodeuscore.api.item.IItemRadiation
import galaxyspace.GalaxySpace
import galaxyspace.core.prefab.items.modules.ItemModule
import galaxyspace.systems.SolarSystem.planets.overworld.items.modules.Energy
import galaxyspace.systems.SolarSystem.planets.overworld.items.modules.armor.*
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.model.ModelBiped
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*

open class ItemSpaceSuitLight constructor(materialIn: ArmorMaterial, armorIndex: EntityEquipmentSlot?) :
    ItemSpaceSuit("space_suit_light", materialIn, (armorIndex)!!), IItemRadiation {
    @JvmField
    protected var textureName: String

    init {
        this.textureName =
            GalaxySpace.ASSET_PREFIX + ":" + "textures/model/armor/" + (if (armorIndex == EntityEquipmentSlot.HEAD || armorIndex == EntityEquipmentSlot.CHEST || armorIndex == EntityEquipmentSlot.FEET) materialIn.toString()
                .lowercase(Locale.getDefault()) + "_1.png" else materialIn.toString()
                .lowercase(Locale.getDefault()) + "_2.png")
    }

    override fun getAvailableModules(): Array<ItemModule?> {
        return ((arrayOf<ItemModule>(
            Autofeeding(),
            WaterBreathing(),
            SensorLens(),
            Nightvision(),
            Jump(),
            Energy(),
            Speed()
        ) as Array<ItemModule?>?))!!
    }

    public override fun getModificationCount(stack: ItemStack?): Int {
        return 1
    }

    @SideOnly(Side.CLIENT)
    public override fun getArmorModel(
        entityLiving: EntityLivingBase,
        itemStack: ItemStack,
        armorSlot: EntityEquipmentSlot,
        model: ModelBiped
    ): ModelBiped? {
        return null
    }

    override fun getArmorTexture(stack: ItemStack, entity: Entity, slot: EntityEquipmentSlot, type: String): String? {
        return textureName
    }

    override fun getSolarRadiationProtectModify(): Float {
        return 1.0f
    }

    override fun addInfo(stack: ItemStack?, world: World?, list: MutableList<String?>?, flagIn: ITooltipFlag?) {
        (list)!!.add(GCCoreUtil.translate("gui.spacesuit.type") + " " + EnumColor.YELLOW + GCCoreUtil.translate("gui.spacesuit.type.1"))
        (list)!!.addAll(
            FMLClientHandler.instance().getClient().fontRenderer.listFormattedStringToWidth(
                GCCoreUtil.translate("gui.spacesuit.desc.light"),
                250
            )
        )
    }

    public override fun getMaxElectricityStored(stack: ItemStack?): Float {
        val energy: Boolean = (stack)!!.hasTagCompound() && (stack)!!.getTagCompound()!!.hasKey("energy")
        return if (energy) 120000.0f else 60000.0f
    }
}
