package galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks

import net.minecraft.block.BlockPlanks.EnumType

import galaxyspace.systems.BarnardsSystem.core.BRBlocks
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Leaves.EnumBlockLeaves
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Leaves
import galaxyspace.core.util.GSCreativeTabs
import asmodeuscore.api.blocks.ICustomLeaves
import net.minecraft.block.Block
import net.minecraft.block.BlockLeaves
import net.minecraft.block.BlockPlanks
import net.minecraft.block.SoundType
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.IStringSerializable
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.Random

open class Barnarda_C_Leaves constructor() : BlockLeaves(), ICustomLeaves {
    @JvmField
    var leafSurroundings: IntArray? = null

    init {
        this.setTranslationKey("barnarda_c_leaves")
        this.setHardness(0.5f)
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

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        return Item.getItemFromBlock(BRBlocks.BARNARDA_C_DANDELIONS)
    }

    override fun damageDropped(state: IBlockState): Int {
        return 12
    }

    override fun updateTick(worldIn: World, pos: BlockPos, state: IBlockState, rand: Random) {
        if (!worldIn.isRemote) {
            if ((state.getValue(CHECK_DECAY) as Boolean) && (state.getValue(DECAYABLE) as Boolean)) {
                val i: Int = 4
                val j: Int = 5
                val k: Int = pos.getX()
                val l: Int = pos.getY()
                val i1: Int = pos.getZ()
                val j1: Int = 32
                val k1: Int = 1024
                val l1: Int = 16

                val range: Int = 6

                if (this.leafSurroundings == null) {
                    this.leafSurroundings = IntArray(32768)
                }

                if (!worldIn.isAreaLoaded(
                        pos,
                        1
                    )
                ) return  // Forge: prevent decaying leaves from updating neighbors and loading unloaded chunks

                if (worldIn.isAreaLoaded(
                        pos,
                        6
                    )
                )  // Forge: extend range from 5 to 6 to account for neighbor checks in world.markAndNotifyBlock -> world.updateObservingBlocksAt
                {
                    val `blockpos$mutableblockpos`: BlockPos.MutableBlockPos = BlockPos.MutableBlockPos()

                    for (i2: Int in -range..range) {
                        for (j2: Int in -range..range) {
                            for (k2: Int in -range..range) {
                                val iblockstate: IBlockState =
                                    worldIn.getBlockState(`blockpos$mutableblockpos`.setPos(k + i2, l + j2, i1 + k2))
                                val block: Block = iblockstate.getBlock()

                                if (!block.canSustainLeaves(
                                        iblockstate,
                                        worldIn,
                                        `blockpos$mutableblockpos`.setPos(k + i2, l + j2, i1 + k2)
                                    )
                                ) {
                                    if (block.isLeaves(
                                            iblockstate,
                                            worldIn,
                                            `blockpos$mutableblockpos`.setPos(k + i2, l + j2, i1 + k2)
                                        )
                                    ) {
                                        this.leafSurroundings!![(i2 + 16) * 1024 + (j2 + 16) * 32 + k2 + 16] = -2
                                    } else {
                                        this.leafSurroundings!![(i2 + 16) * 1024 + (j2 + 16) * 32 + k2 + 16] = -1
                                    }
                                } else {
                                    this.leafSurroundings!![(i2 + 16) * 1024 + (j2 + 16) * 32 + k2 + 16] = 0
                                }
                            }
                        }
                    }

                    for (i3: Int in 1..4) {
                        for (j3: Int in -range..range) {
                            for (k3: Int in -range..range) {
                                for (l3: Int in -range..range) {
                                    if (this.leafSurroundings!![(j3 + 16) * 1024 + (k3 + 16) * 32 + l3 + 16] == i3 - 1) {
                                        if (this.leafSurroundings!![(j3 + 16 - 1) * 1024 + (k3 + 16) * 32 + l3 + 16] == -2) {
                                            this.leafSurroundings!![(j3 + 16 - 1) * 1024 + (k3 + 16) * 32 + l3 + 16] = i3
                                        }

                                        if (this.leafSurroundings!![(j3 + 16 + 1) * 1024 + (k3 + 16) * 32 + l3 + 16] == -2) {
                                            this.leafSurroundings!![(j3 + 16 + 1) * 1024 + (k3 + 16) * 32 + l3 + 16] = i3
                                        }

                                        if (this.leafSurroundings!![(j3 + 16) * 1024 + (k3 + 16 - 1) * 32 + l3 + 16] == -2) {
                                            this.leafSurroundings!![(j3 + 16) * 1024 + (k3 + 16 - 1) * 32 + l3 + 16] = i3
                                        }

                                        if (this.leafSurroundings!![(j3 + 16) * 1024 + (k3 + 16 + 1) * 32 + l3 + 16] == -2) {
                                            this.leafSurroundings!![(j3 + 16) * 1024 + (k3 + 16 + 1) * 32 + l3 + 16] = i3
                                        }

                                        if (this.leafSurroundings!![(j3 + 16) * 1024 + (k3 + 16) * 32 + (l3 + 16 - 1)] == -2) {
                                            this.leafSurroundings!![(j3 + 16) * 1024 + (k3 + 16) * 32 + (l3 + 16 - 1)] = i3
                                        }

                                        if (this.leafSurroundings!![(j3 + 16) * 1024 + (k3 + 16) * 32 + l3 + 16 + 1] == -2) {
                                            this.leafSurroundings!![(j3 + 16) * 1024 + (k3 + 16) * 32 + l3 + 16 + 1] = i3
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                val l2: Int = this.leafSurroundings!![16912]

                if (l2 >= 0) {
                    worldIn.setBlockState(pos, state.withProperty(CHECK_DECAY, false), 4)
                } else {
                    this.destroy(worldIn, pos)
                }
            }
        }
    }

    private fun destroy(worldIn: World, pos: BlockPos) {
        this.dropBlockAsItem(worldIn, pos, worldIn.getBlockState(pos), 0)
        worldIn.setBlockToAir(pos)
    }

    @SideOnly(Side.CLIENT)
    override fun getSubBlocks(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        for (blockBasic: EnumBlockLeaves in EnumBlockLeaves.entries) {
            list.add(ItemStack(this, 1, blockBasic.getMeta()))
        }
    }

    override fun isOpaqueCube(state: IBlockState): Boolean {
        return Blocks.LEAVES.isOpaqueCube(state)
    }

    @SideOnly(Side.CLIENT)
    override fun getRenderLayer(): BlockRenderLayer {
        return if (Minecraft.getMinecraft().gameSettings.fancyGraphics) BlockRenderLayer.CUTOUT_MIPPED else BlockRenderLayer.SOLID
    }


    @SideOnly(Side.CLIENT)
    override fun shouldSideBeRendered(
        state: IBlockState,
        blockAccess: IBlockAccess,
        pos: BlockPos,
        side: EnumFacing
    ): Boolean {
        val neighborPos: BlockPos = pos.offset(side)
        if (isOpaqueCube(state) && blockAccess.getBlockState(neighborPos).getBlock() === this) {
            return false
        }
        return !blockAccess.getBlockState(neighborPos)
            .doesSideBlockRendering(blockAccess, neighborPos, side.getOpposite())
    }

    override fun onSheared(
        item: ItemStack,
        world: IBlockAccess,
        pos: BlockPos,
        fortune: Int
    ): MutableList<ItemStack?> {
        return NonNullList.withSize<ItemStack>(
            1,
            ItemStack(this, 1, world.getBlockState(pos).getValue(BASIC_TYPE).ordinal)
        )
    }

    override fun getWoodType(meta: Int): BlockPlanks.EnumType {
        return BlockPlanks.EnumType.OAK
    }

    override fun getFlammability(world: IBlockAccess, pos: BlockPos, face: EnumFacing): Int {
        return 60
    }

    override fun getFireSpreadSpeed(world: IBlockAccess, pos: BlockPos, face: EnumFacing): Int {
        return 30
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    enum class EnumBlockLeaves(private val meta: Int, private val serializedName: String) : IStringSerializable {
        GRASS(0, "barnarda_c_leaves_1");

        fun getMeta(): Int {
            return this.meta
        }

        override fun getName(): String {
            return this.serializedName
        }

        companion object {
            @JvmStatic
            fun byMetadata(meta: Int): EnumBlockLeaves? {
                return entries[meta]
            }
        }
    }

    /*
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(BASIC_TYPE, EnumBlockLeaves.byMetadata(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumBlockLeaves) state.getValue(BASIC_TYPE)).getMeta();
	}
*/
    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.getDefaultState()
            .withProperty(BASIC_TYPE, (EnumBlockLeaves.byMetadata((meta and 3) % 4))!!)
            .withProperty(DECAYABLE, (meta and 4) == 0)
            .withProperty(CHECK_DECAY, (meta and 8) > 0)
        //return this.getDefaultState().withProperty(BASIC_TYPE,  EnumBlockLeaves.byMetadata(meta)).withProperty(DECAYABLE, (meta & 4) == 0).withProperty(CHECK_DECAY, (meta & 8) > 0);
    }

    override fun getMetaFromState(state: IBlockState): Int {
        var i: Int = state.getValue(BASIC_TYPE).ordinal

        if (!state.getValue(DECAYABLE)) {
            i = i or 4
        }

        if (state.getValue(CHECK_DECAY)) {
            i = i or 8
        }

        return i
    }

    public override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, *arrayOf<IProperty<*>>(BASIC_TYPE, CHECK_DECAY, DECAYABLE))
    }

    override fun getDefaultLeavesColor(): Int {
        return 0x985cff
    }


    companion object {
        @JvmField
        val BASIC_TYPE: PropertyEnum<EnumBlockLeaves> =
            PropertyEnum.create<EnumBlockLeaves>("type", EnumBlockLeaves::class.java)
    }
}
