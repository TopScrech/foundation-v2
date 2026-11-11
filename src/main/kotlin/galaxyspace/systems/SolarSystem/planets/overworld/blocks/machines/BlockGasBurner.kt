package galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines

import foundation.block.LegacyBlockMetadata
import foundation.block.getStateFromMeta

import foundation.level.offset

import galaxyspace.GalaxySpace
import asmodeuscore.api.item.IShiftDescription
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityGasBurner
import galaxyspace.systems.SolarSystem.planets.overworld.blocks.machines.BlockGasBurner
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.blocks.BlockAdvancedTile
import micdoodle8.mods.galacticraft.core.blocks.ISortableBlock
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseUniversalElectrical
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.OxygenUtil
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.core.Direction
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.util.EnumParticleTypes
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.Random

open class BlockGasBurner constructor() : BlockAdvancedTile(GCBlocks.machine), IShiftDescription, ISortableBlock, LegacyBlockMetadata {
    init {
        this.setTranslationKey("gas_burner")
        this.setHardness(1.0f)
        this.setSoundType(SoundType.METAL)
    }

    override fun onMachineActivated(
        world: World?,
        pos: BlockPos?,
        state: IBlockState?,
        playerIn: EntityPlayer?,
        hand: EnumHand?,
        heldItem: ItemStack?,
        side: EnumFacing?,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        //playerIn.openGui(GalaxySpace.instance, -1, world, pos.getX(), pos.getY(), pos.getZ());
        return true
    }

    @OnlyIn(Dist.CLIENT)
    override fun randomDisplayTick(stateIn: IBlockState, world: World, pos: BlockPos, rand: Random) {
        val tile: TileEntity? = world.getBlockEntity(pos)
        if (tile is TileEntityGasBurner) {
            val tileGas: TileEntityGasBurner = tile

            if (tileGas.processTicks > 0) {
                if (!OxygenUtil.isAABBInBreathableAirBlock(world, AxisAlignedBB(pos.offset(0, 1, 0)))) {
                    val var7: Float = pos.getX() + 0.5f
                    val var8: Float = pos.getY() + 1.1f
                    val var9: Float = pos.getZ() + 0.5f
                    val var10: Float = 0.0f
                    val var11: Float = 0.0f

                    for (i: Int in -1..1) {
                        for (j: Int in -1..1) {
                            world.spawnParticle(
                                EnumParticleTypes.SMOKE_NORMAL,
                                var7 + var11 + i * 0.2,
                                var8.toDouble(),
                                var9 + var10 + j * 0.2,
                                0.0,
                                0.01,
                                0.0
                            )
                            world.spawnParticle(
                                EnumParticleTypes.FLAME,
                                var7 + var11 + i * 0.1,
                                var8 - 0.2,
                                var9 + var10 + j * 0.1,
                                0.0,
                                0.0001,
                                0.0
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onBlockPlacedBy(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState,
        placer: EntityLivingBase,
        stack: ItemStack
    ) {
        val angle: Int = MathHelper.floor(placer.yRot * 4.0f / 360.0f + 0.5) and 3
        val change: Int = EnumFacing.byHorizontalIndex(angle).getOpposite().getHorizontalIndex()
        worldIn.setBlock(pos, getStateFromMeta(change), 3)
    }

    override fun onUseWrench(
        world: World?,
        pos: BlockPos?,
        entityPlayer: EntityPlayer?,
        hand: EnumHand?,
        heldItem: ItemStack?,
        side: EnumFacing?,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        val metadata: Int = getMetaFromState((world)!!.getBlockState(pos))
        val change: Int = (world)!!.getBlockState(pos).getValue(FACING).rotateY().getHorizontalIndex()
        (world)!!.setBlock(pos, this.getStateFromMeta(metadata - (metadata % 4) + change), 3)

        val te: TileEntity? = (world)!!.getBlockEntity(pos)
        if (te is TileBaseUniversalElectrical) {
            te.updateFacing()
        }

        return true
    }

    override fun createNewTileEntity(world: World, meta: Int): TileEntity? {
        return TileEntityGasBurner()
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate(this.getTranslationKey() + ".desc")
    }

    override fun getDescription(meta: Int): String? {
        return null
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.MACHINE
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        val enumfacing: EnumFacing = EnumFacing.byHorizontalIndex(meta % 4)
        return this.defaultBlockState().setValue(FACING, enumfacing)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return state.getValue(FACING).getHorizontalIndex()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, FACING)
    }

    companion object {
        @JvmField
        val FACING: EnumProperty<Direction> = BlockStateProperties.HORIZONTAL_FACING
    }
}
