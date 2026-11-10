package micdoodle8.mods.galacticraft.planets.mars.items

import micdoodle8.mods.galacticraft.api.recipe.ISchematicItem
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.items.ItemSchematic
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemSchematicTier2 constructor() : ItemSchematic("schematic"), ISchematicItem, ISortableItem {
    @SideOnly(Side.CLIENT)
    override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftItemsTab
    }

    override fun getSubItems(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        if (tab === GalacticraftCore.galacticraftItemsTab || tab === CreativeTabs.SEARCH) {
            for (i: Int in 0..2) {
                list.add(ItemStack(this, 1, i))
            }
        }
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(
        par1ItemStack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<String?>,
        flagIn: ITooltipFlag
    ) {
        when (par1ItemStack.getItemDamage()) {
            0 -> tooltip.add(GCCoreUtil.translate("schematic.rocket_t3.name"))
            1 -> tooltip.add(GCCoreUtil.translate("schematic.cargo_rocket.name"))
            2 -> tooltip.add(GCCoreUtil.translate("schematic.astro_miner.name"))
        }
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.SCHEMATIC
    }

    /**
     * Higher tiers should use this form and make sure they have set up the
     * indexOffset correctly in registerSchematicItems()
     */
    override fun getIndex(damage: Int): Int {
        return damage + indexOffset
    }

    companion object {
        private var indexOffset = 0

        /**
         * Make sure the number of these will match the index values
         */
        @JvmStatic
        fun registerSchematicItems() {
            indexOffset = SchematicRegistry.registerSchematicItem(ItemStack(MarsItems.schematic, 1, 0))
            SchematicRegistry.registerSchematicItem(ItemStack(MarsItems.schematic, 1, 1))
            SchematicRegistry.registerSchematicItem(ItemStack(MarsItems.schematic, 1, 2))
        }

        /**
         * Make sure the order of these will match the index values
         */
        @JvmStatic
        @SideOnly(value = Side.CLIENT)
        fun registerTextures() {
            SchematicRegistry.registerTexture(
                ResourceLocation(
                    "galacticraftplanets",
                    "textures/items/schematic_rocket_t3.png"
                )
            )
            SchematicRegistry.registerTexture(
                ResourceLocation(
                    "galacticraftplanets",
                    "textures/items/schematic_rocket_cargo.png"
                )
            )
            SchematicRegistry.registerTexture(
                ResourceLocation(
                    "galacticraftplanets",
                    "textures/items/schematic_astro_miner.png"
                )
            )
        }
    }
}
