package galaxyspace.systems.SolarSystem.planets.overworld.items.armor

import foundation.item.legacyItemValue

import galaxyspace.GalaxySpace
import galaxyspace.core.util.GSCreativeTabs
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot as EntityEquipmentSlot
import net.minecraft.item.ItemArmor
import net.minecraft.world.item.ItemStack
import java.util.*

open class ItemArmorGS constructor(
    assetSuffix: String,
    material: ArmorMaterial,
    equipmentSlotIn: EntityEquipmentSlot
) : ItemArmor(material, 0, equipmentSlotIn), ISortableItem {
    @JvmField
    protected var textureName: String

    init {
        this.setTranslationKey(assetSuffix)
        this.textureName =
            GalaxySpace.ASSET_PREFIX + ":" + "textures/model/armor/" + (if (equipmentSlotIn == EntityEquipmentSlot.HEAD || equipmentSlotIn == EntityEquipmentSlot.CHEST || equipmentSlotIn == EntityEquipmentSlot.FEET) material.toString()
                .lowercase(Locale.getDefault()) + "_1.png" else material.toString()
                .lowercase(Locale.getDefault()) + "_2.png")
    }

    override fun getCreativeTab(): CreativeTabs? {
        return GSCreativeTabs.GSArmorTab
    }

    override fun getArmorTexture(stack: ItemStack, entity: Entity, slot: EntityEquipmentSlot, type: String): String? {
        return textureName
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.ARMOR
    }

    override fun getIsRepairable(toRepair: ItemStack, repair: ItemStack): Boolean {
        return repair.getItem() === GCItems.basicItem && repair.legacyItemValue == 9
    }
}
