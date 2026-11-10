package micdoodle8.mods.galacticraft.core.items

import net.minecraft.util.NonNullList
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation

import micdoodle8.mods.galacticraft.api.recipe.ISchematicItem
import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.entities.EntityHangingSchematic
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemHangingEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemSchematic constructor(assetName: String?) : ItemHangingEntity(EntityHangingSchematic::class.java),
    ISchematicItem, ISortableItem {
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
        this.setMaxStackSize(1)
        this.setTranslationKey(assetName)
    }

    override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftItemsTab
    }

    override fun getSubItems(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        if (tab === GalacticraftCore.galacticraftItemsTab || tab === CreativeTabs.SEARCH) {
            for (i: Int in 0..1) {
                list.add(ItemStack(this, 1, i))
            }
        }
    }

    @SideOnly(Side.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    override fun getMetadata(par1: Int): Int {
        return par1
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(
        par1ItemStack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<String?>,
        flagIn: ITooltipFlag
    ) {
        when (par1ItemStack.getItemDamage()) {
            0 -> tooltip.add(GCCoreUtil.translate("schematic.moonbuggy.name"))
            1 -> {
                tooltip.add(GCCoreUtil.translate("schematic.rocket_t2.name"))

                if (!GalacticraftCore.isPlanetsLoaded) {
                    tooltip.add(EnumColor.DARK_AQUA.toString() + "\"Galacticraft: Planets\" Not Installed!")
                }
            }
        }
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.SCHEMATIC
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
        val stack: ItemStack = playerIn.getHeldItem(hand)
        val blockpos: BlockPos = pos.offset(facing)

        if (facing != EnumFacing.DOWN && facing != EnumFacing.UP && playerIn.canPlayerEdit(blockpos, facing, stack)) {
            val entityhanging: EntityHangingSchematic =
                this.createEntity(worldIn, blockpos, facing, this.getIndex(stack.getItemDamage()))

            if (entityhanging != null && entityhanging.onValidSurface()) {
                if (!worldIn.isRemote) {
                    entityhanging.playPlaceSound()
                    worldIn.spawnEntity(entityhanging)
                    entityhanging.sendToClient(worldIn, blockpos)
                }

                stack.shrink(1)
            }

            return EnumActionResult.SUCCESS
        } else {
            return EnumActionResult.FAIL
        }
    }

    private fun createEntity(
        worldIn: World,
        pos: BlockPos,
        clickedSide: EnumFacing,
        index: Int
    ): EntityHangingSchematic {
        return EntityHangingSchematic(worldIn, pos, clickedSide, index)
    }

    /**
     * Higher tiers should override - see ItemSchematicTier2 for example
     */
    protected open fun getIndex(damage: Int): Int {
        return damage
    }

    companion object {
        /**
         * Make sure the number of these will match the index values
         */
        @JvmStatic
        fun registerSchematicItems() {
            SchematicRegistry.registerSchematicItem(ItemStack(GCItems.schematic, 1, 0))
            SchematicRegistry.registerSchematicItem(ItemStack(GCItems.schematic, 1, 1))
        }

        /**
         * Make sure the order of these will match the index values
         */
        @JvmStatic
        @SideOnly(value = Side.CLIENT)
        fun registerTextures() {
            SchematicRegistry.registerTexture(
                ResourceLocation(
                    Constants.ASSET_PREFIX,
                    "textures/items/schematic_buggy.png"
                )
            )
            SchematicRegistry.registerTexture(
                ResourceLocation(
                    Constants.ASSET_PREFIX,
                    "textures/items/schematic_rocket_t2.png"
                )
            )
        }
    }
}
