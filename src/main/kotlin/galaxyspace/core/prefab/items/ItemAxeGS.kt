package galaxyspace.core.prefab.items

import galaxyspace.core.util.GSCreativeTabs
import com.google.common.collect.Sets
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.init.Blocks
import net.minecraft.item.ItemAxe
import net.minecraft.item.ItemStack

open class ItemAxeGS constructor(assetName: String, material: ToolMaterial) :
    ItemAxe(material, material.getAttackDamage(), -3.0f), ISortableItem {
    init {
        this.setTranslationKey(assetName)
    }

    override fun getCreativeTab(): CreativeTabs? {
        return GSCreativeTabs.GSArmorTab
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.TOOLS
    }

    override fun getDestroySpeed(stack: ItemStack, state: IBlockState): Float {
        val material: Material = state.getMaterial()
        return if (material !== Material.WOOD && material !== Material.PLANTS && material !== Material.VINE)
            super.getDestroySpeed(stack, state)
        else
            this.efficiency
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
