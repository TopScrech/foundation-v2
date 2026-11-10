package galaxyspace.systems.SolarSystem.planets.overworld.items

import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.ResourceLocation

import galaxyspace.GalaxySpace
import galaxyspace.core.GSItems
import galaxyspace.core.configs.GSConfigCore
import galaxyspace.core.util.GSCreativeTabs
import micdoodle8.mods.galacticraft.api.recipe.ISchematicItem
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.items.ItemSchematic
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

// These static methods intentionally hide methods inherited from Java classes
@Suppress("ACCIDENTAL_OVERRIDE")
open class ItemSchematics constructor() : ItemSchematic("schematics"), ISchematicItem, ISortableItem {
    override fun getCreativeTab(): CreativeTabs? {
        return GSCreativeTabs.GSItemsTab
    }

    override fun onItemUse(
        playerIn: EntityPlayer,
        worldIn: World,
        pos: BlockPos,
        hand: EnumHand,
        facing: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): EnumActionResult {
        return EnumActionResult.FAIL
    }

    override fun getSubItems(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        if (tab === GSCreativeTabs.GSItemsTab || tab === CreativeTabs.SEARCH) {
            var i: Int

            i = if (GSConfigCore.enableAdvancedRocketCraft) 0 else 5
            while (i < 7) {
                list.add(ItemStack(this, 1, i))
                i++
            }
        }
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, world: World?, list: MutableList<String?>, flagIn: ITooltipFlag) {
        val meta: Int = stack.getItemDamage()

        when (meta) {
            0 -> list.add(GCCoreUtil.translate("item.rocket_cone.name"))
            1 -> list.add(GCCoreUtil.translate("item.rocket_body.name"))
            2 -> list.add(GCCoreUtil.translate("item.rocket_engine.name"))
            3 -> list.add(GCCoreUtil.translate("item.rocket_booster.name"))
            4 -> list.add(GCCoreUtil.translate("item.rocket_stabiliser.name"))
            5 -> list.add(GCCoreUtil.translate("item.oxygen_tank_epp_1.name"))
            6 -> list.add(GCCoreUtil.translate("tile.port_nuclear_reactor.name"))
        }
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.SCHEMATIC
    }

    override fun getIndex(damage: Int): Int {
        return damage + indexOffset
    }

    companion object {
        private const val indexOffset = 0

        @JvmStatic
        fun registerSchematicItems() {
            SchematicRegistry.registerSchematicItem(ItemStack(GSItems.SCHEMATICS, 1, 0))
            SchematicRegistry.registerSchematicItem(ItemStack(GSItems.SCHEMATICS, 1, 1))
            SchematicRegistry.registerSchematicItem(ItemStack(GSItems.SCHEMATICS, 1, 2))
            SchematicRegistry.registerSchematicItem(ItemStack(GSItems.SCHEMATICS, 1, 3))
            SchematicRegistry.registerSchematicItem(ItemStack(GSItems.SCHEMATICS, 1, 4))
            SchematicRegistry.registerSchematicItem(ItemStack(GSItems.SCHEMATICS, 1, 5))
            SchematicRegistry.registerSchematicItem(ItemStack(GSItems.SCHEMATICS, 1, 6))
        }

        @JvmStatic
        @SideOnly(value = Side.CLIENT)
        fun registerTextures() {
            SchematicRegistry.registerTexture(
                ResourceLocation(
                    GalaxySpace.ASSET_PREFIX,
                    "textures/items/schematics/schematic_1.png"
                )
            )
            SchematicRegistry.registerTexture(
                ResourceLocation(
                    GalaxySpace.ASSET_PREFIX,
                    "textures/items/schematics/schematic_2.png"
                )
            )
            SchematicRegistry.registerTexture(
                ResourceLocation(
                    GalaxySpace.ASSET_PREFIX,
                    "textures/items/schematics/schematic_3.png"
                )
            )
            SchematicRegistry.registerTexture(
                ResourceLocation(
                    GalaxySpace.ASSET_PREFIX,
                    "textures/items/schematics/schematic_4.png"
                )
            )
            SchematicRegistry.registerTexture(
                ResourceLocation(
                    GalaxySpace.ASSET_PREFIX,
                    "textures/items/schematics/schematic_5.png"
                )
            )
            SchematicRegistry.registerTexture(
                ResourceLocation(
                    GalaxySpace.ASSET_PREFIX,
                    "textures/items/schematics/schematic_6.png"
                )
            )
            SchematicRegistry.registerTexture(
                ResourceLocation(
                    GalaxySpace.ASSET_PREFIX,
                    "textures/items/schematics/schematic_7.png"
                )
            )
        }
    }
}
