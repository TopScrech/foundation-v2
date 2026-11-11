package micdoodle8.mods.galacticraft.core.items

import micdoodle8.mods.galacticraft.api.item.ISensorGlassesArmor
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.gui.overlay.OverlaySensorGlasses
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.EquipmentSlot as EntityEquipmentSlot
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemArmor
import net.minecraft.world.item.ItemStack
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class ItemSensorGlasses constructor(assetName: String?) :
    ItemArmor(GCItems.ARMOR_SENSOR_GLASSES, 0, EntityEquipmentSlot.HEAD), ISortableItem, ISensorGlassesArmor {
    init {
        this.setTranslationKey(assetName)
        //this.setTextureName(Constants.TEXTURE_PREFIX + assetName);
    }

    override fun isEnchantable(stack: ItemStack): Boolean {
        return false
    }

    override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftItemsTab
    }

    override fun getArmorTexture(stack: ItemStack, entity: Entity, slot: EntityEquipmentSlot, type: String): String? {
        return Constants.TEXTURE_PREFIX + "textures/model/armor/sensor_1.png"
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    @OnlyIn(Dist.CLIENT)
    override fun renderHelmetOverlay(
        stack: ItemStack,
        player: EntityPlayer,
        resolution: ScaledResolution,
        partialTicks: Float
    ) {
        OverlaySensorGlasses.renderSensorGlassesMain(stack, player, resolution, partialTicks)
        OverlaySensorGlasses.renderSensorGlassesValueableBlocks(stack, player, resolution, partialTicks)
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.GEAR
    }
}
