package micdoodle8.mods.galacticraft.core.tile

import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.blocks.BlockAdvanced
import micdoodle8.mods.galacticraft.core.blocks.BlockMulti
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
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
    //            this.world.notifyBlockUpdate(this.getPos());
    //        }
    //    }
    open fun onBlockRemoval() {
        if (this.mainBlockPosition != null) {
            val tileEntity: TileEntity? = this.world.getTileEntity(this.mainBlockPosition)

            if (tileEntity is IMultiBlock) {
                val mainBlock: IMultiBlock = tileEntity as IMultiBlock
                mainBlock.onDestroy(this)
            }
        }
    }

    open fun onBlockActivated(worldIn: World?, pos: BlockPos?, player: EntityPlayer?): Boolean {
        if (this.mainBlockPosition != null) {
            val tileEntity: TileEntity? = this.world.getTileEntity(this.mainBlockPosition)

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
            return this.world.getTileEntity(this.mainBlockPosition)
        }

        return null
    }

    /**
     * Reads a tile entity from NBT.
     */
    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        val tag: NBTTagCompound = nbt.getCompoundTag("mainBlockPosition")
        this.mainBlockPosition = BlockPos(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"))
    }

    /**
     * Writes a tile entity to NBT.
     */
    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)

        if (this.mainBlockPosition != null) {
            val tag: NBTTagCompound = NBTTagCompound()
            tag.setInteger("x", this.mainBlockPosition!!.getX())
            tag.setInteger("y", this.mainBlockPosition!!.getY())
            tag.setInteger("z", this.mainBlockPosition!!.getZ())
            nbt.setTag("mainBlockPosition", tag)
        }

        return nbt
    }

    protected open fun initialiseMultiTiles(pos: BlockPos?, world: World): Boolean {
        val thisTile: IMultiBlock = this as IMultiBlock


        //Client can create its own fake blocks and tiles - no need for networking in 1.8+
        if (world.isRemote) {
            thisTile.onCreate(world, pos)
        }

        val positions: MutableList<BlockPos> = ArrayList<BlockPos>()
        thisTile.getPositions(pos, (positions as MutableList<BlockPos?>?))
        var result: Boolean = true
        for (vecToAdd: BlockPos in positions) {
            val tile: TileEntity? = world.getTileEntity(vecToAdd)
            if (tile is TileEntityMulti) {
                tile.mainBlockPosition = pos
            } else if (tile == null) {
                val b: Block = world.getBlockState(vecToAdd).getBlock()
                if (b !is BlockMulti) {
                    world.setBlockState(
                        vecToAdd,
                        (GCBlocks.fakeBlock)!!.getDefaultState()
                            .withProperty(
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
        if (result == false && !world.isRemote) {
//            //Try again to create all the multiblocks - currently disabled because making new tiles here interferes with server->client tileEntity sync during worldgen (Abandoned Base)
//            thisTile.onCreate(world, pos);
        }

        return result
    }
}
