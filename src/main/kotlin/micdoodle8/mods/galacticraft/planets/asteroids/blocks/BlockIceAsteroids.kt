package micdoodle8.mods.galacticraft.planets.asteroids.blocks

import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.BlockBreakable
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.EnumPushReaction
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.item.ItemStack
import net.minecraft.stats.StatList
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.util.BlockRenderLayer
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.BlockPos
import net.minecraft.world.EnumSkyBlock
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraftforge.event.ForgeEventFactory
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.ArrayList
import java.util.Random

open class BlockIceAsteroids constructor(assetName: String?) : BlockBreakable(Material.ICE, false),
    ISortableBlock {
    init {
        this.slipperiness = 0.98f
        this.setTickRandomly(true)
        this.setHardness(0.5f)
        this.setTranslationKey(assetName)
        this.setSoundType(SoundType.GLASS)
    }

    @OnlyIn(Dist.CLIENT)
    public override fun getRenderLayer(): BlockRenderLayer {
        return BlockRenderLayer.TRANSLUCENT
    }

    @OnlyIn(Dist.CLIENT)
    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftBlocksTab
    }

    override fun harvestBlock(
        worldIn: World,
        player: EntityPlayer,
        pos: BlockPos,
        state: IBlockState,
        te: TileEntity?,
        tool: ItemStack
    ) {
        player.addStat(StatList.getBlockStats(this))
        player.addExhaustion(0.025f)

        if (this.canSilkHarvest(
                worldIn,
                pos,
                worldIn.getBlockState(pos),
                player
            ) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, tool) > 0
        ) {
            val items: ArrayList<ItemStack> = ArrayList<ItemStack>()
            items.add(this.getSilkTouchDrop(state))

            ForgeEventFactory.fireBlockHarvesting(items, worldIn, pos, state, 0, 1.0f, true, player)
            for (`is`: ItemStack in items) {
                spawnAsEntity(worldIn, pos, `is`)
            }
        } else {
            if (worldIn.provider.getDimension() == -1 || worldIn.provider is IGalacticraftWorldProvider) {
                worldIn.setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState())
                return
            }

            val i1: Int = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, tool)
            harvesters.set(player)
            this.dropBlockAsItem(worldIn, pos, state, i1)
            harvesters.set(null)
            val state1: IBlockState = worldIn.getBlockState(pos.below())
            val material: Material = state1.getMaterial()

            if (material.blocksMovement() || material.isLiquid()) {
                worldIn.setBlockAndUpdate(pos, Blocks.FLOWING_WATER.defaultBlockState())
            }
        }
    }

    override fun quantityDropped(rand: Random): Int {
        return 0
    }

    override fun updateTick(worldIn: World, pos: BlockPos, state: IBlockState, rand: Random) {
        if (worldIn.getLightFor(EnumSkyBlock.BLOCK, pos) > 13 - this.getLightOpacity(state)) {
            if (GCCoreUtil.getDimensionID(worldIn) == -1 || worldIn.provider is IGalacticraftWorldProvider) {
                worldIn.setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState())
                return
            }

            this.dropBlockAsItem(worldIn, pos, worldIn.getBlockState(pos), 0)
            worldIn.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState())
        }
    }

    public override fun getPushReaction(state: IBlockState?): EnumPushReaction {
        return EnumPushReaction.NORMAL
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }

    override fun getBlockFaceShape(
        worldIn: IBlockAccess,
        state: IBlockState,
        pos: BlockPos,
        face: EnumFacing
    ): BlockFaceShape {
        return BlockFaceShape.UNDEFINED
    }
}
