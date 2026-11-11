package micdoodle8.mods.galacticraft.core.blocks

import foundation.block.LegacyBlockMetadata

import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseUniversalElectrical
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenCollector
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.core.Direction
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.Random

open class BlockOxygenCollector constructor(assetName: String?) : BlockAdvancedTile(Material.ROCK),
    IShiftDescription, ISortableBlock, LegacyBlockMetadata {
    init {
        this.setHardness(1.0f)
        this.setSoundType(SoundType.METAL)
        this.setTranslationKey(assetName)
    }

    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftBlocksTab
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
        val state: IBlockState = (world)!!.getBlockState(pos)
        TileBaseUniversalElectrical.onUseWrenchBlock(state, (world)!!, (pos)!!, state.getValue(FACING))
        return true
    }

    override fun onMachineActivated(
        world: World?,
        pos: BlockPos?,
        state: IBlockState?,
        entityPlayer: EntityPlayer?,
        hand: EnumHand?,
        heldItem: ItemStack?,
        side: EnumFacing?,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        (entityPlayer)!!.openGui(GalacticraftCore.instance, -1, world, (pos)!!.getX(), (pos)!!.getY(), (pos)!!.getZ())
        return true
    }

    override fun createTileEntity(world: World, state: IBlockState): TileEntity? {
        return TileEntityOxygenCollector()
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

    @OnlyIn(Dist.CLIENT)
    override fun randomDisplayTick(stateIn: IBlockState, worldIn: World, pos: BlockPos, rand: Random) {
        val tile: TileEntity? = worldIn.getBlockEntity(pos)
        if (tile is TileEntityOxygenCollector) {
            if (tile.lastOxygenCollected > 1) {
                for (particleCount: Int in 0..9) {
                    var x2: Double = (pos.getX() + rand.nextFloat()).toDouble()
                    val y2: Double = (pos.getY() + rand.nextFloat()).toDouble()
                    var z2: Double = (pos.getZ() + rand.nextFloat()).toDouble()
                    var mX: Double = 0.0
                    var mY: Double = 0.0
                    var mZ: Double = 0.0
                    val dir: Int = rand.nextInt(2) * 2 - 1
                    mX = (rand.nextFloat() - 0.5) * 0.5
                    mY = (rand.nextFloat() - 0.5) * 0.5
                    mZ = (rand.nextFloat() - 0.5) * 0.5

                    val meta: Int = getMetaFromState(stateIn)

                    if (meta == 3 || meta == 2) {
                        x2 = pos.getX() + 0.5 + 0.25 * dir
                        mX = (rand.nextFloat() * 2.0f * dir).toDouble()
                    } else {
                        z2 = pos.getZ() + 0.5 + 0.25 * dir
                        mZ = (rand.nextFloat() * 2.0f * dir).toDouble()
                    }

                    (GalacticraftCore.proxy)!!.spawnParticle(
                        "oxygen",
                        Vector3(x2, y2, z2),
                        Vector3(mX, mY, mZ),
                        (arrayOf<Any>(Vector3(0.7, 0.7, 1.0)) as Array<Any?>?)
                    )
                }
            }
        }
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate(this.getTranslationKey() + ".description")
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        val enumfacing: EnumFacing = EnumFacing.byHorizontalIndex(meta)
        return this.defaultBlockState().setValue(FACING, enumfacing)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(FACING) as EnumFacing).getHorizontalIndex()
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, FACING)
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.MACHINE
    }

    companion object {
        @JvmField
        val FACING: EnumProperty<Direction> = BlockStateProperties.HORIZONTAL_FACING
    }
}
