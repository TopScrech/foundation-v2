package micdoodle8.mods.galacticraft.core.tile

import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.blocks.BlockAdvanced
import micdoodle8.mods.galacticraft.core.blocks.BlockMulti
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import java.util.ArrayList

open class TileEntityMulti : TileEntity {
    //NOTE: No need for networking in 1.8+: see comment in initialiseMultiTiles()
    // The the position of the main block
    @JvmField
    var mainBlockPosition: BlockPos? = null

    constructor()

    constructor(mainBlockPosition: BlockPos?) {
        this.mainBlockPosition = mainBlockPosition
    }

    //    public void setMainBlock(BlockPos mainBlock)
    //    {
    //        this.mainBlockPosition = mainBlock;
    //
    //        if (!this.world.isRemote)
    //        {
    //            this.world.sendBlockUpdated(this.getPos());
    //        }
    //    }
    open fun onBlockRemoval() {
        if (this.mainBlockPosition != null) {
            val tileEntity: TileEntity? = this.world.getBlockEntity(this.mainBlockPosition)

            if (tileEntity is IMultiBlock) {
                val mainBlock: IMultiBlock = tileEntity as IMultiBlock
                mainBlock.onDestroy(this)
            }
        }
    }

    open fun onBlockActivated(worldIn: World?, pos: BlockPos?, player: EntityPlayer?): Boolean {
        if (this.mainBlockPosition != null) {
            val tileEntity: TileEntity? = this.world.getBlockEntity(this.mainBlockPosition)

            if (tileEntity is IMultiBlock) {
                return (tileEntity as IMultiBlock).onActivated(player)
            }
        }

        return false
    }

    open fun onBlockWrenched(
        world: World,
        pos: BlockPos?,
        entityPlayer: EntityPlayer,
        hand: EnumHand,
        side: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        if (this.mainBlockPosition != null) {
            val state: IBlockState = this.world.getBlockState(this.mainBlockPosition)

            if (state.getBlock() is BlockAdvanced) {
                return (state.getBlock() as BlockAdvanced).onBlockActivated(
                    world,
                    (this.mainBlockPosition)!!,
                    state,
                    entityPlayer,
                    hand,
                    side,
                    hitX,
                    hitY,
                    hitZ
                )
            }
        }

        return false
    }

    open fun getMainBlockTile(): TileEntity? {
        if (this.mainBlockPosition != null) {
            return this.world.getBlockEntity(this.mainBlockPosition)
        }

        return null
    }

    /**
     * Reads a tile entity from NBT.
     */
    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        val tag: NBTTagCompound = nbt.getCompoundOrEmpty("mainBlockPosition")
        this.mainBlockPosition = BlockPos(tag.getIntOr("x", 0), tag.getIntOr("y", 0), tag.getIntOr("z", 0))
    }

    /**
     * Writes a tile entity to NBT.
     */
    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)

        if (this.mainBlockPosition != null) {
            val tag: NBTTagCompound = NBTTagCompound()
            tag.putInt("x", this.mainBlockPosition!!.getX())
            tag.putInt("y", this.mainBlockPosition!!.getY())
            tag.putInt("z", this.mainBlockPosition!!.getZ())
            nbt.put("mainBlockPosition", tag)
        }

        return nbt
    }

    protected open fun initialiseMultiTiles(pos: BlockPos?, world: World): Boolean {
        val thisTile: IMultiBlock = this as IMultiBlock


        //Client can create its own fake blocks and tiles - no need for networking in 1.8+
        if (world.isClientSide) {
            thisTile.onCreate(world, pos)
        }

        val positions: MutableList<BlockPos> = ArrayList<BlockPos>()
        thisTile.getPositions(pos, (positions as MutableList<BlockPos?>?))
        var result: Boolean = true
        for (vecToAdd: BlockPos in positions) {
            val tile: TileEntity? = world.getBlockEntity(vecToAdd)
            if (tile is TileEntityMulti) {
                tile.mainBlockPosition = pos
            } else if (tile == null) {
                val b: Block = world.getBlockState(vecToAdd).getBlock()
                if (b !is BlockMulti) {
                    world.setBlock(
                        vecToAdd,
                        (GCBlocks.fakeBlock)!!.defaultBlockState()
                            .setValue(
                                BlockMulti.MULTI_TYPE,
                                (thisTile.getMultiType())!!
                            ),
                        2
                    )
                }
                world.setTileEntity(vecToAdd, TileEntityMulti(pos))
            } else {
                result = false
            }
        }
        if (result == false && !world.isClientSide) {
//            //Try again to create all the multiblocks - currently disabled because making new tiles here interferes with server->client tileEntity sync during worldgen (Abandoned Base)
//            thisTile.onCreate(world, pos);
        }

        return result
    }
}
