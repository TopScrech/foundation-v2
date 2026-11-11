package micdoodle8.mods.galacticraft.planets.mars.items

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.item.EnumRarity
import net.minecraft.world.item.ItemStack
import net.minecraft.item.ItemSword
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class ItemSwordMars constructor(par2EnumToolMaterial: ToolMaterial) : ItemSword(par2EnumToolMaterial),
    ISortableItem {
    @OnlyIn(Dist.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    @OnlyIn(Dist.CLIENT)
    override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftItemsTab
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.TOOLS
    }

    override fun onBlockDestroyed(
        stack: ItemStack,
        worldIn: World,
        state: IBlockState,
        pos: BlockPos,
        entityLiving: EntityLivingBase
    ): Boolean {
        if (state.getBlockHardness(worldIn, pos) > 0.2001f) {
            stack.damageItem(2, entityLiving)
        }

        return true
    }
}
