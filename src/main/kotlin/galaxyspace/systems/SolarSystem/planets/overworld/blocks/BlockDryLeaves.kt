package galaxyspace.systems.SolarSystem.planets.overworld.blocks

import galaxyspace.core.util.GSCreativeTabs
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.Items
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.util.EnumParticleTypes
import net.minecraft.core.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.Random

open class BlockDryLeaves constructor() : Block(Material.WOOD), ISortableBlock {
    init {
        this.setTranslationKey("dry_leaves")
        this.setTickRandomly(true)
        this.setHardness(0.2f)
        this.setLightOpacity(1)
        this.setSoundType(SoundType.PLANT)
    }

    override fun getCreativeTab(): CreativeTabs? {
        return GSCreativeTabs.GSBlocksTab
    }

    override fun getPickBlock(
        state: IBlockState,
        target: RayTraceResult,
        world: World,
        pos: BlockPos,
        player: EntityPlayer
    ): ItemStack {
        return ItemStack(Item.getItemFromBlock(this), 1, 0)
    }

    @OnlyIn(Dist.CLIENT)
    override fun randomDisplayTick(stateIn: IBlockState, worldIn: World, pos: BlockPos, rand: Random) {
        if (worldIn.isRainingAt(pos.above()) && !worldIn.getBlockState(pos.below()).isTopSolid() && rand.nextInt(15) == 1) {
            val d0: Double = (pos.getX().toFloat() + rand.nextFloat()).toDouble()
            val d1: Double = pos.getY().toDouble() - 0.05
            val d2: Double = (pos.getZ().toFloat() + rand.nextFloat()).toDouble()
            worldIn.spawnParticle(EnumParticleTypes.DRIP_WATER, d0, d1, d2, 0.0, 0.0, 0.0)
        }
    }

    override fun quantityDropped(random: Random): Int {
        return if (random.nextInt(20) == 0) random.nextInt(2) + 1 else 0
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        return Items.STICK
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRenderLayer(): BlockRenderLayer {
        return if (Minecraft.getInstance().gameSettings.fancyGraphics) BlockRenderLayer.CUTOUT_MIPPED else BlockRenderLayer.SOLID
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return false
    }

    @OnlyIn(Dist.CLIENT)
    override fun shouldSideBeRendered(
        blockState: IBlockState,
        blockAccess: IBlockAccess,
        pos: BlockPos,
        side: EnumFacing
    ): Boolean {
        return if (blockAccess.getBlockState(pos.offset(side))
                .getBlock() === this
        ) false else super.shouldSideBeRendered(blockState, blockAccess, pos, side)
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.GENERAL
    }
}
