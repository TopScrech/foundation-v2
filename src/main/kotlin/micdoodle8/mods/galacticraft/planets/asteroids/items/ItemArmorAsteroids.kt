package micdoodle8.mods.galacticraft.planets.asteroids.items

import foundation.item.legacyItemValue

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot as EntityEquipmentSlot
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemArmor
import net.minecraft.world.item.ItemStack
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class ItemArmorAsteroids constructor(armorIndex: EntityEquipmentSlot, assetSuffix: String?) :
    ItemArmor(AsteroidsItems.ARMOR_TITANIUM, 0, armorIndex), ISortableItem {
    init {
        this.setTranslationKey("titanium_" + assetSuffix)
        //this.setTextureName(GalacticraftPlanets.TEXTURE_PREFIX + "titanium_" + assetSuffix);
    }

    override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftItemsTab
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    override fun getArmorTexture(stack: ItemStack, entity: Entity, slot: EntityEquipmentSlot, type: String): String? {
        if (this.getArmorMaterial() == AsteroidsItems.ARMOR_TITANIUM) {
            if (stack.getItem() === AsteroidsItems.titaniumHelmet) {
                return GalacticraftPlanets.TEXTURE_PREFIX + "textures/model/armor/titanium_1.png"
            } else if (stack.getItem() === AsteroidsItems.titaniumChestplate || stack.getItem() === AsteroidsItems.titaniumBoots) {
                return GalacticraftPlanets.TEXTURE_PREFIX + "textures/model/armor/titanium_2.png"
            } else if (stack.getItem() === AsteroidsItems.titaniumLeggings) {
                return GalacticraftPlanets.TEXTURE_PREFIX + "textures/model/armor/titanium_3.png"
            }
        }

        return null
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.ARMOR
    }

    override fun getIsRepairable(toRepair: ItemStack, repair: ItemStack): Boolean {
        return repair.getItem() === AsteroidsItems.basicItem && repair.legacyItemValue == 6
    }
}
