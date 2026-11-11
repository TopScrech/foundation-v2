package micdoodle8.mods.galacticraft.planets.mars.items

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

open class ItemArmorMars constructor(private val material: ArmorMaterial, par3: Int, par4: EntityEquipmentSlot) :
    ItemArmor(material, par3, par4), ISortableItem {
    /*@Override
    public Item setUnlocalizedName(String par1Str)
    {
//        super.setTextureName(par1Str);
        super.setUnlocalizedName(par1Str);
        return this;
    }*/
    override fun getArmorTexture(stack: ItemStack, entity: Entity, slot: EntityEquipmentSlot, type: String): String? {
        if (this.material == MarsItems.ARMORDESH) {
            if (stack.getItem() === MarsItems.deshHelmet) {
                return GalacticraftPlanets.TEXTURE_PREFIX + "textures/model/armor/desh_1.png"
            } else if (stack.getItem() === MarsItems.deshChestplate || stack.getItem() === MarsItems.deshBoots) {
                return GalacticraftPlanets.TEXTURE_PREFIX + "textures/model/armor/desh_2.png"
            } else if (stack.getItem() === MarsItems.deshLeggings) {
                return GalacticraftPlanets.TEXTURE_PREFIX + "textures/model/armor/desh_3.png"
            }
        }

        return null
    }

    @OnlyIn(Dist.CLIENT)
    override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftItemsTab
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.ARMOR
    }

    override fun getIsRepairable(toRepair: ItemStack, repair: ItemStack): Boolean {
        return repair.getItem() === MarsItems.marsItemBasic && repair.legacyItemValue == 2
    }
}
