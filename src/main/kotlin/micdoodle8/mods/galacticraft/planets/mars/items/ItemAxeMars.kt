package micdoodle8.mods.galacticraft.planets.mars.items

import com.google.common.collect.Sets
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.world.level.block.Block
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.level.block.Blocks
import net.minecraft.item.EnumRarity
import net.minecraft.world.item.ItemStack
import net.minecraft.item.ItemTool
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class ItemAxeMars constructor(material: ToolMaterial) : ItemTool(material, EFFECTIVE_ON), ISortableItem {
    init {
        this.attackDamage = 6.0f
        this.attackSpeed = -3.0f
    }

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

    override fun getDestroySpeed(stack: ItemStack, state: IBlockState): Float {
        val material: Material = state.getMaterial()
        return if (material !== Material.WOOD && material !== Material.PLANTS && material !== Material.VINE) super.getDestroySpeed(
            stack,
            state
        ) else this.efficiency
    }

    companion object {
        private val EFFECTIVE_ON: MutableSet<Block?> = Sets.newHashSet<Block?>(
            *arrayOf<Block?>(
                Blocks.PLANKS,
                Blocks.BOOKSHELF,
                Blocks.LOG,
                Blocks.LOG2,
                Blocks.CHEST,
                Blocks.PUMPKIN,
                Blocks.LIT_PUMPKIN,
                Blocks.MELON_BLOCK,
                Blocks.LADDER,
                Blocks.WOODEN_BUTTON,
                Blocks.WOODEN_PRESSURE_PLATE
            )
        )
    }
}
