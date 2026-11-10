package micdoodle8.mods.galacticraft.core.blocks

import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.tile.TileEntityCoalGenerator
import micdoodle8.mods.galacticraft.core.tile.TileEntityElectricFurnace
import micdoodle8.mods.galacticraft.core.tile.TileEntityEnergyStorageModule
import micdoodle8.mods.galacticraft.core.tile.TileEntityIngotCompressor
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.SoundType
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.IStringSerializable
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import java.util.Random
import kotlin.math.floor

open class BlockMachine constructor(assetName: String?) : BlockTileGC(GCBlocks.machine), IShiftDescription,
    ISortableBlock {
    enum class EnumMachineType(private val meta: Int, private val serializedName: String) : IStringSerializable {
        COAL_GENERATOR(0, "coal_generator"),
        COMPRESSOR(3, "ingot_compressor"); // 3 for backwards compatibility

        fun getMeta(): Int {
            return this.meta
        }

        override fun getName(): String {
            return this.serializedName
        }

        companion object {
            @JvmStatic
            fun byMetadata(meta: Int): EnumMachineType {
                when (meta) {
                    3 -> return COMPRESSOR
                    else -> return COAL_GENERATOR
                }
            }
        }
    }

    init {
        this.setHardness(1.0f)
        this.setSoundType(SoundType.METAL)
        this.setTranslationKey(assetName)
    }

    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftBlocksTab
    }

    override fun randomDisplayTick(stateIn: IBlockState, worldIn: World, pos: BlockPos, rand: Random) {
        val tile: TileEntity? = worldIn.getTileEntity(pos)

        if (tile is TileEntityCoalGenerator) {
            val tileEntity: TileEntityCoalGenerator = tile
            if (tileEntity.heatGJperTick > 0) {
                val particlePosX: Float = pos.getX() + 0.5f
                val particlePosY: Float = pos.getY() + 0.0f + rand.nextFloat() * 6.0f / 16.0f
                val particlePosZ: Float = pos.getZ() + 0.5f
                val particleSize0: Float = 0.52f
                val particleSize1: Float = rand.nextFloat() * 0.6f - 0.3f

                when (stateIn.getValue(FACING)) {
                    EnumFacing.NORTH -> {
                        worldIn.spawnParticle(
                            EnumParticleTypes.SMOKE_NORMAL,
                            (particlePosX + particleSize1).toDouble(),
                            particlePosY.toDouble(),
                            (particlePosZ - particleSize0).toDouble(),
                            0.0,
                            0.0,
                            0.0
                        )
                        worldIn.spawnParticle(
                            EnumParticleTypes.FLAME,
                            (particlePosX + particleSize1).toDouble(),
                            particlePosY.toDouble(),
                            (particlePosZ - particleSize0).toDouble(),
                            0.0,
                            0.0,
                            0.0
                        )
                    }

                    EnumFacing.EAST -> {
                        worldIn.spawnParticle(
                            EnumParticleTypes.SMOKE_NORMAL,
                            (particlePosX + particleSize0).toDouble(),
                            particlePosY.toDouble(),
                            (particlePosZ + particleSize1).toDouble(),
                            0.0,
                            0.0,
                            0.0
                        )
                        worldIn.spawnParticle(
                            EnumParticleTypes.FLAME,
                            (particlePosX + particleSize0).toDouble(),
                            particlePosY.toDouble(),
                            (particlePosZ + particleSize1).toDouble(),
                            0.0,
                            0.0,
                            0.0
                        )
                    }

                    EnumFacing.SOUTH -> {
                        worldIn.spawnParticle(
                            EnumParticleTypes.SMOKE_NORMAL,
                            (particlePosX + particleSize1).toDouble(),
                            particlePosY.toDouble(),
                            (particlePosZ + particleSize0).toDouble(),
                            0.0,
                            0.0,
                            0.0
                        )
                        worldIn.spawnParticle(
                            EnumParticleTypes.FLAME,
                            (particlePosX + particleSize1).toDouble(),
                            particlePosY.toDouble(),
                            (particlePosZ + particleSize0).toDouble(),
                            0.0,
                            0.0,
                            0.0
                        )
                    }

                    EnumFacing.WEST -> {
                        worldIn.spawnParticle(
                            EnumParticleTypes.SMOKE_NORMAL,
                            (particlePosX - particleSize0).toDouble(),
                            particlePosY.toDouble(),
                            (particlePosZ + particleSize1).toDouble(),
                            0.0,
                            0.0,
                            0.0
                        )
                        worldIn.spawnParticle(
                            EnumParticleTypes.FLAME,
                            (particlePosX - particleSize0).toDouble(),
                            particlePosY.toDouble(),
                            (particlePosZ + particleSize1).toDouble(),
                            0.0,
                            0.0,
                            0.0
                        )
                    }
                
else -> {}
}
            }
        }
    }

    /**
     * Called when the block is placed in the world.
     */
    override fun onBlockPlacedBy(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState,
        placer: EntityLivingBase,
        stack: ItemStack
    ) {
        val metadata: Int = getMetaFromState(state)

        val angle: Int = MathHelper.floor(placer.rotationYaw * 4.0f / 360.0f + 0.5) and 3
        val change: Int = EnumFacing.byHorizontalIndex(angle).getOpposite().getHorizontalIndex()

        worldIn.setBlockState(pos, getStateFromMeta((metadata and 12) + change), 3)
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
        val change: Int = (world)!!.getBlockState(pos)
            .getValue(micdoodle8.mods.galacticraft.core.blocks.BlockMachine.Companion.FACING)
            .rotateY().getHorizontalIndex()

        //(Currently neither of these machines is electric, so the following is unnecessary)
//        if (metadata < BlockMachine.COMPRESSOR_METADATA)
//        {
//            TileEntity te = world.getTileEntity(pos);
//            if (te instanceof TileBaseUniversalElectrical)
//            {
//                ((TileBaseUniversalElectrical) te).updateFacing();
//            }
//        }
        (world)!!.setBlockState(pos, getStateFromMeta((metadata and 12) + change), 3)
        return true
    }

    /**
     * Called when the block is right clicked by the player
     */
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
        if (!(world)!!.isRemote) {
            (entityPlayer)!!.openGui(GalacticraftCore.instance, -1, world, (pos)!!.getX(), (pos)!!.getY(), (pos)!!.getZ())
            return true
        }

        return true
    }

    override fun createTileEntity(world: World, state: IBlockState): TileEntity? {
        val metadata: Int = getMetaFromState(state) and 12
        if (metadata == COMPRESSOR_METADATA) {
            return TileEntityIngotCompressor()
        } else if (metadata == 4) {
            return TileEntityEnergyStorageModule()
        } else if (metadata == 8) {
            return TileEntityElectricFurnace()
        } else {
            return TileEntityCoalGenerator()
        }
    }

    open fun getCompressor(): ItemStack {
        return ItemStack(this, 1, COMPRESSOR_METADATA)
    }

    open fun getCoalGenerator(): ItemStack {
        return ItemStack(this, 1, COAL_GENERATOR_METADATA)
    }

    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        list.add(this.getCoalGenerator())
        list.add(this.getCompressor())
    }

    override fun damageDropped(state: IBlockState): Int {
        return getMetaFromState(state) and 12
    }

    override fun getShiftDescription(meta: Int): String? {
        when (meta) {
            COAL_GENERATOR_METADATA -> return GCCoreUtil.translate("tile.coal_generator.description")
            COMPRESSOR_METADATA -> return GCCoreUtil.translate("tile.compressor.description")
        }
        return ""
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        val enumfacing: EnumFacing = EnumFacing.byHorizontalIndex(meta % 4)
        val type: EnumMachineType = EnumMachineType.Companion.byMetadata(floor(meta / 4.0).toInt())
        return this.getDefaultState().withProperty(FACING, enumfacing)
            .withProperty(TYPE, type)
    }

    override fun getMetaFromState(state: IBlockState): Int {
        return (state.getValue(FACING)).getHorizontalIndex() + (state.getValue(TYPE) as EnumMachineType).getMeta() * 4
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, FACING, TYPE)
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.MACHINE
    }

    companion object {
        const val COAL_GENERATOR_METADATA: Int = 0

        const val COMPRESSOR_METADATA: Int = 12

        @JvmField
        val FACING: PropertyDirection = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL)

        @JvmField
        val TYPE: PropertyEnum<EnumMachineType> =
            PropertyEnum.create<EnumMachineType>("type", EnumMachineType::class.java)
    }
}
