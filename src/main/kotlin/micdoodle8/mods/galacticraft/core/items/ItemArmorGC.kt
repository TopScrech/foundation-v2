package micdoodle8.mods.galacticraft.core.items

import foundation.item.legacyItemValue

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot as EntityEquipmentSlot
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemArmor
import net.minecraft.world.item.ItemStack
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class ItemArmorGC constructor(armorIndex: EntityEquipmentSlot, assetSuffix: String?) :
    ItemArmor(GCItems.ARMOR_STEEL, 0, armorIndex), ISortableItem {
    init {
        this.setTranslationKey("steel_" + assetSuffix)
        //this.setTextureName(Constants.TEXTURE_PREFIX + "steel_" + assetSuffix);
    }

    override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftItemsTab
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    override fun getArmorTexture(stack: ItemStack, entity: Entity, slot: EntityEquipmentSlot, type: String): String? {
        if (this.getArmorMaterial() == GCItems.ARMOR_STEEL) {
            if (stack.getItem() === GCItems.steelHelmet) {
                return Constants.TEXTURE_PREFIX + "textures/model/armor/steel_1.png"
            } else if (stack.getItem() === GCItems.steelChestplate || stack.getItem() === GCItems.steelBoots) {
                return Constants.TEXTURE_PREFIX + "textures/model/armor/steel_2.png"
            } else if (stack.getItem() === GCItems.steelLeggings) {
                return Constants.TEXTURE_PREFIX + "textures/model/armor/steel_3.png"
            }
        }

        return null
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.ARMOR
    }

    override fun getIsRepairable(toRepair: ItemStack, repair: ItemStack): Boolean {
        return repair.getItem() === GCItems.basicItem && repair.legacyItemValue == 9
    }
}
