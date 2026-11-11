package micdoodle8.mods.galacticraft.core.tile

import foundation.block.getMetaFromState

import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.tile.ITileClientUpdates
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.blocks.BlockScreen
import micdoodle8.mods.galacticraft.core.client.screen.DrawGameScreen
import micdoodle8.mods.galacticraft.core.tick.TickHandlerClient
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.ArrayList

open class TileEntityScreen : TileEntityAdvanced(), ITileClientUpdates {
    @JvmField
    var imageType: Int = 0

    @JvmField
    var screen: DrawGameScreen? = null

    @JvmField
    var connectedUp: Boolean = false

    @JvmField
    var connectedDown: Boolean = false

    @JvmField
    var connectedLeft: Boolean = false

    @JvmField
    var connectedRight: Boolean = false

    @JvmField
    var connectionsUp: Int = 0

    @JvmField
    var connectionsDown: Int = 0

    @JvmField
    var connectionsLeft: Int = 0

    @JvmField
    var connectionsRight: Int = 0

    @JvmField
    var isMultiscreen: Boolean = false

    @JvmField
    var screenOffsetx: Int = 0

    @JvmField
    var screenOffsetz: Int = 0

    private val requiresUpdate = 0
    private var doneClientUpdate = false

    //Used on client side only
    @JvmField
    var refreshOnUpdate: Boolean = false
    private var renderAABB: AxisAlignedBB? = null

    open fun setConnectedUp(connectedUpL: Boolean) {
        this.connectedUp = connectedUpL
    }

    open fun setConnectedDown(connectedDownL: Boolean) {
        this.connectedDown = connectedDownL
    }

    open fun setConnectedLeft(connectedLeftL: Boolean) {
        this.connectedLeft = connectedLeftL
    }

    open fun setConnectedRight(connectedRightL: Boolean) {
        this.connectedRight = connectedRightL
    }

    open fun setVerifiedConnectedUp(vec: BlockVec3) {
        val meta: Int = this.getBlockMetadata() and 7
        val tileUp: TileEntity? = vec.getTileEntityOnSide(this.world, 1)
        this.setConnectedUp(tileUp is TileEntityScreen && tileUp.getBlockMetadata() == meta && !tileUp.isInvalid())
    }

    open fun setVerifiedConnectedDown(vec: BlockVec3) {
        val meta: Int = this.getBlockMetadata() and 7
        val tileDown: TileEntity? = vec.getTileEntityOnSide(this.world, 0)
        this.setConnectedDown(tileDown is TileEntityScreen && tileDown.getBlockMetadata() == meta && !tileDown.isInvalid())
    }

    open fun setVerifiedConnectedLeft(vec: BlockVec3) {
        val meta: Int = this.getBlockMetadata() and 7
        val side: Int = this.getLeft(meta)
        val tileLeft: TileEntity? = vec.getTileEntityOnSide(this.world, side)
        this.setConnectedLeft(tileLeft is TileEntityScreen && tileLeft.getBlockMetadata() == meta && !tileLeft.isInvalid())
    }

    open fun setVerifiedConnectedRight(vec: BlockVec3) {
        val meta: Int = this.getBlockMetadata() and 7
        val side: Int = this.getRight(meta)
        val tileRight: TileEntity? = vec.getTileEntityOnSide(this.world, side)
        this.setConnectedRight(tileRight is TileEntityScreen && tileRight.getBlockMetadata() == meta && !tileRight.isInvalid())
    }

    override fun onLoad() {
        if (this.world.isRemote) {
            this.clientOnLoad()
            this.screen = DrawGameScreen(1.0f, 1.0f, this)
        }
    }

    override fun buildDataPacket(data: IntArray?) {
        (data)!![0] = this.imageType
        var connectedFlags: Int = 0
        if (this.connectedUp) {
            connectedFlags += 8
        }
        if (this.connectedDown) {
            connectedFlags += 4
        }
        if (this.connectedLeft) {
            connectedFlags += 2
        }
        if (this.connectedRight) {
            connectedFlags += 1
        }
        (data)!![1] = connectedFlags
    }

    open fun getFront(): EnumFacing {
        return this.getFacing(this.world.getBlockState(getPos()))
    }

    private fun getFacing(state: IBlockState): EnumFacing {
        return state.getValue(BlockScreen.FACING)
    }

    /**
     * Call when a screen (which maybe part of a multiscreen) is either
     * broken or rotated.
     *
     * @param state The state of the screen prior to breaking or rotation
     */
    open fun breakScreen(state: IBlockState) {
        val vec: BlockVec3 = BlockVec3(this)
        val meta: Int = state.getBlock().getMetaFromState(state)
        var tile: TileEntity?
        val facingRight: EnumFacing = getFacing(state).rotateY()

        val left: Int = this.connectionsLeft
        val right: Int = this.connectionsRight
        val up: Int = this.connectionsUp
        val down: Int = this.connectionsDown

        val doUp: Boolean = this.connectedUp
        val doDown: Boolean = this.connectedDown
        val doLeft: Boolean = this.connectedLeft
        val doRight: Boolean = this.connectedRight

        for (x: Int in -left..right) {
            for (z: Int in -up..down) {
                if (x == 0 && z == 0) {
                    this.resetToSingle()
                } else {
                    val newVec: BlockVec3 =
                        vec.clone().modifyPositionFromSide(facingRight, x).modifyPositionFromSide(EnumFacing.DOWN, z)
                    tile = newVec.getTileEntity(this.world)
                    if (tile is TileEntityScreen && tile.getBlockMetadata() == meta) {
                        tile.resetToSingle()
                    }
                }
            }
        }

        //TODO  Try to generate largest screen possible out of remaining blocks
        this.setConnectedDown(false)
        this.setConnectedUp(false)
        this.setConnectedLeft(false)
        this.setConnectedRight(false)

        if (doUp) {
            tile = vec.getTileEntityOnSide(this.world, 1)
            if (tile is TileEntityScreen && tile.getBlockMetadata() == meta && !tile.isInvalid()) {
                if (doLeft) {
                    tile.setConnectedLeft(true)
                }
                if (doRight) {
                    tile.setConnectedRight(true)
                }
                tile.setConnectedUp(true)
                //				if (doLeft) ((TileEntityScreen)tile).connectedLeft = true;
//				if (doRight) ((TileEntityScreen)tile).connectedRight = true;
//				((TileEntityScreen)tile).connectedUp = true;
                tile.refreshConnections(true)
            }
        }
        if (doDown) {
            tile = vec.getTileEntityOnSide(this.world, 0)
            if (tile is TileEntityScreen && tile.getBlockMetadata() == meta && !tile.isInvalid()) {
                if (doLeft) {
                    tile.setConnectedLeft(true)
                }
                if (doRight) {
                    tile.setConnectedRight(true)
                }
                tile.setConnectedDown(true)
                //				if (doLeft) ((TileEntityScreen)tile).connectedLeft = true;
//				if (doRight) ((TileEntityScreen)tile).connectedRight = true;
//				((TileEntityScreen)tile).connectedDown = true;
                tile.refreshConnections(true)
            }
        }
        if (doLeft) {
            tile = vec.getTileEntityOnSide(this.world, this.getLeft(meta))
            if (tile is TileEntityScreen && tile.getBlockMetadata() == meta && !tile.isInvalid()) {
                if (doUp) {
                    tile.setConnectedUp(true)
                }
                if (doDown) {
                    tile.setConnectedDown(true)
                }
                tile.setConnectedLeft(true)
                //				if (doUp) ((TileEntityScreen)tile).connectedUp = true;
//				if (doDown) ((TileEntityScreen)tile).connectedDown = true;
//				((TileEntityScreen)tile).connectedLeft = true;
                tile.refreshConnections(true)
            }
        }
        if (doRight) {
            tile = vec.getTileEntityOnSide(this.world, this.getRight(meta))
            if (tile is TileEntityScreen && tile.getBlockMetadata() == meta && !tile.isInvalid()) {
                if (doUp) {
                    tile.setConnectedUp(true)
                }
                if (doDown) {
                    tile.setConnectedDown(true)
                }
                tile.setConnectedRight(true)
                //				if (doUp) ((TileEntityScreen)tile).connectedUp = true;
//				if (doDown) ((TileEntityScreen)tile).connectedDown = true;
//				((TileEntityScreen)tile).connectedRight = true;
                tile.refreshConnections(true)
            }
        }
    }

    /**
     * Check whether the screen can sustain 'multi-screen' connections on each of its 4 sides
     * (note: this can be called recursively from inside itself)
     *
     * @param doScreen If true, build a new multi-screen if connections are found
     */
    open fun refreshConnections(doScreen: Boolean) {
        this.log("Starting connection check")

        val iblockstate: IBlockState = this.world.getBlockState(this.pos)
        val facing: EnumFacing = iblockstate.getValue(BlockScreen.FACING)

        val meta: Int = this.getBlockMetadata() and 7
        if (meta < 2) {
            //TODO System.out.println("Up/down oriented screens cannot be multiscreen");
            this.resetToSingle()
            return
        }

        var tileUp: TileEntity? = null
        var tileDown: TileEntity? = null
        var tileLeft: TileEntity? = null
        var tileRight: TileEntity? = null
        val vec: BlockVec3 = BlockVec3(this)

        //First, basic check that a neighbour is there and in the same orientation
        if (this.connectedUp) {
            this.setVerifiedConnectedUp(vec)
            tileUp = vec.getTileEntityOnSide(this.world, 1)
        }
        if (this.connectedDown) {
            this.setVerifiedConnectedDown(vec)
            tileDown = vec.getTileEntityOnSide(this.world, 0)
        }
        if (this.connectedLeft) {
            val side: Int = this.getLeft(meta)
            tileLeft = vec.getTileEntityOnSide(this.world, side)
            this.setVerifiedConnectedLeft(vec)
        }
        if (this.connectedRight) {
            val side: Int = this.getRight(meta)
            tileRight = vec.getTileEntityOnSide(this.world, side)
            this.setVerifiedConnectedRight(vec)
        }

        //Now test whether a connection can be sustained with that other tile
        if (this.connectedUp) {
            this.setConnectedUp(this.tryConnectUp(tileUp as TileEntityScreen?))
        }

        if (this.connectedDown) {
            this.setConnectedDown(this.tryConnectDown(tileDown as TileEntityScreen?))
        }

        if (this.connectedLeft) {
            this.setConnectedLeft(this.tryConnectLeft(tileLeft as TileEntityScreen?))
        }

        if (this.connectedRight) {
            this.setConnectedRight(this.tryConnectRight(tileRight as TileEntityScreen?))
        }
        this.log("Ending connection check")
        if (doScreen) {
            this.checkScreenSize()
            this.markDirty()
        }
    }

    //	@Override
    //    public boolean canUpdate()
    //    {
    //        return false;
    //    }
    /**
     * Cycle through different screen contents
     */
    open fun changeChannel() {
        if (!this.world.isRemote) {
            if (++this.imageType >= GalacticraftRegistry.getMaxScreenTypes()) {
                this.imageType = 0
            }

            var flag: Boolean = false

            if (!this.connectedRight && this.canJoinRight()) {
                this.joinRight()
                flag = true
            } else if (!this.connectedLeft && this.canJoinLeft()) {
                this.joinLeft()
                flag = true
            } else if (!this.connectedUp && this.canJoinUp()) {
                this.joinUp()
                flag = true
            } else if (!this.connectedDown && this.canJoinDown()) {
                this.joinDown()
                flag = true
            }

            this.doneClientUpdate = false
            this.refreshConnections(true)
            this.markDirty()

            if (!this.doneClientUpdate) {
                this.updateAllInDimension()
            }
        }
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        this.imageType = nbt.getIntOr("type", 0)
        this.connectionsDown = nbt.getIntOr("connectionsDown", 0)
        this.connectionsUp = nbt.getIntOr("connectionsUp", 0)
        this.connectionsLeft = nbt.getIntOr("connectionsLeft", 0)
        this.connectionsRight = nbt.getIntOr("connectionsRight", 0)
        this.isMultiscreen = nbt.getBooleanOr("multiscreen", false)
        this.setConnectedUp(this.connectionsUp > 0)
        this.setConnectedDown(this.connectionsDown > 0)
        this.setConnectedLeft(this.connectionsLeft > 0)
        this.setConnectedRight(this.connectionsRight > 0)
        //        this.connectedUp = (this.connectionsUp > 0);
//        this.connectedDown = (this.connectionsDown > 0);
//        this.connectedLeft = (this.connectionsLeft > 0);
//        this.connectedRight = (this.connectionsRight > 0);
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        nbt.putInt("type", this.imageType)
        nbt.putInt("connectionsDown", this.connectionsDown)
        nbt.putInt("connectionsUp", this.connectionsUp)
        nbt.putInt("connectionsLeft", this.connectionsLeft)
        nbt.putInt("connectionsRight", this.connectionsRight)
        nbt.putBoolean("multiscreen", this.isMultiscreen)
        return nbt
    }

    override fun getUpdateTag(): NBTTagCompound {
        return this.writeToNBT(NBTTagCompound())
    }

    open fun checkScreenSize() {
        this.log("Checking screen size")
        var up: Int = 0
        var down: Int = 0
        var left: Int = 0
        var right: Int = 0
        val meta: Int = this.getBlockMetadata() and 7

        var vec: BlockVec3 = BlockVec3(this)
        var tile: TileEntityScreen = this
        while (up < 8) {
            if (tile.connectedUp) {
                up++
                val newTile: TileEntity? = vec.getTileEntityOnSide(this.world, 1)
                if (newTile is TileEntityScreen) {
                    tile = newTile
                    vec.translate(0, 1, 0)
                } else {
                    println("Debug - connected up to a non-screen tile")
                    //    				tile.connectedUp = false;
                    this.setConnectedUp(false)
                    tile.markDirty()
                    up--
                    break
                }
            } else {
                break
            }
        }

        vec = BlockVec3(this)
        tile = this
        while (down < 8 - up) {
            if (tile.connectedDown) {
                down++
                val newTile: TileEntity? = vec.getTileEntityOnSide(this.world, 0)
                if (newTile is TileEntityScreen) {
                    tile = newTile
                    vec.translate(0, -1, 0)
                } else {
                    println("Debug - connected down to a non-screen tile")
                    //    				tile.connectedDown = false;
                    this.setConnectedDown(false)
                    tile.markDirty()
                    down--
                    break
                }
            } else {
                break
            }
        }

        vec = BlockVec3(this)
        tile = this
        val leftside: Int = this.getLeft(meta)
        while (left < (if (up + down == 0) 1 else 8)) {
            if (tile.connectedLeft) {
                left++
                val newTile: TileEntity? = vec.getTileEntityOnSide(this.world, leftside)
                if (newTile is TileEntityScreen) {
                    tile = newTile
                    vec = vec.newVecSide(leftside)
                } else {
                    println("Debug - connected left to a non-screen tile")
                    //    				tile.connectedLeft = false;
                    this.setConnectedLeft(false)
                    tile.markDirty()
                    left--
                    break
                }
            } else {
                break
            }
        }

        vec = BlockVec3(this)
        tile = this
        val rightside: Int = this.getRight(meta)
        while (right < (if (up + down == 0) 1 else 8) - left) {
            if (tile.connectedRight) {
                right++
                val newTile: TileEntity? = vec.getTileEntityOnSide(this.world, rightside)
                if (newTile is TileEntityScreen) {
                    tile = newTile
                    vec = vec.newVecSide(rightside)
                } else {
                    println("Debug - connected right to a non-screen tile")
                    //    				tile.connectedRight = false;
                    this.setConnectedRight(false)
                    tile.markDirty()
                    right--
                    break
                }
            } else {
                break
            }
        }

        this.log("Screen size check midpoint " + up + " " + down + " " + left + " " + right + " ")

        vec = BlockVec3(this)
        var newtile: TileEntity? = vec.getTileEntityOnSide(this.world, 1)
        val tileUp: TileEntityScreen? = if (newtile is TileEntityScreen) newtile else null
        newtile = vec.getTileEntityOnSide(this.world, 0)
        val tileDown: TileEntityScreen? = if (newtile is TileEntityScreen) newtile else null
        newtile = vec.getTileEntityOnSide(this.world, leftside)
        val tileLeft: TileEntityScreen? = if (newtile is TileEntityScreen) newtile else null
        newtile = vec.getTileEntityOnSide(this.world, rightside)
        val tileRight: TileEntityScreen? = if (newtile is TileEntityScreen) newtile else null
        //Prevent 3 x 1 and longer
        if (left + right == 0 && up + down >= 1) {
            if (up > 0 && !tileUp!!.connectedUp)  //No need for null check if up > 0
            {
                up = 1
                down = 0
            } else {
                up = 0
                if (tileDown != null && !tileDown.connectedDown) {
                    down = 1
                } else {
                    down = 0
                }
            }
        }
        if (up + down == 0 && left + right >= 1) {
            if (left > 0 && !tileLeft!!.connectedLeft)  //No need for null check if right > 0
            {
                if (right == 0 || tileRight == null || tileRight.connectionsLeft == 0) {
                    left = 1
                    right = 0
                } else {
                    left = 0
                    right = 1
                }
            } else {
                left = 0
                if (tileRight != null && !tileRight.connectedRight) {
                    right = 1
                } else {
                    right = 0
                }
            }
        }

        if (up == 0) {
            this.setConnectedUp(false)
            if (tileUp != null) {
                tileUp.setConnectedDown(false)
            }
            //    		this.connectedUp = false;
//    		if (tileUp != null) tileUp.connectedDown = false;
        }
        if (down == 0) {
            this.setConnectedDown(false)
            if (tileUp != null) {
                tileUp.setConnectedUp(false)
            }
            //    		this.connectedDown = false;
//    		if (tileDown != null) tileDown.connectedUp = false;
        }
        if (left == 0) {
            this.setConnectedLeft(false)
            if (tileUp != null) {
                tileUp.setConnectedRight(false)
            }
            //    		this.connectedLeft = false;
//    		if (tileLeft != null) tileLeft.connectedRight = false;
        }
        if (right == 0) {
            this.setConnectedRight(false)
            if (tileUp != null) {
                tileUp.setConnectedLeft(false)
            }
            //    		this.connectedRight = false;
//    		if (tileRight != null) tileRight.connectedLeft = false;
        }

        this.log("Finished screen size check")

        this.checkWholeScreen(up, down, left, right)
    }

    /**
     * After figuring out the screen edges (overall screen dimensions)
     * check that the screen is a whole A x B rectangle with no tiles missing
     *
     *
     * If it is whole, set all tiles in the screen to match this screen type
     *
     * @param up    Number of blocks the screen edge is away from this in the up direction
     * @param down  Number of blocks the screen edge is away from this in the down direction
     * @param left  Number of blocks the screen edge is away from this in the left direction
     * @param right Number of blocks the screen edge is away from this in the right direction
     * @return True if the screen was whole
     */
    private fun checkWholeScreen(up: Int, down: Int, left: Int, right: Int): Boolean {
        if (up + down + left + right == 0 || up < 0 || down < 0 || left < 0 || right < 0) {
            this.doneClientUpdate = true
            this.resetToSingle()
            return true
        }

        //System.out.println("Checking screen size at "+this.getPos().getX()+","+this.getPos().getZ()+": Up "+up+" Dn "+down+" Lf "+left+" Rg "+right);
        var screenWhole: Boolean = true
        var existingScreen: Boolean = false
        var barrierUp: Int = up
        var barrierDown: Int = down
        var barrierLeft: Int = left
        var barrierRight: Int = right

        val meta: Int = this.getBlockMetadata() and 7
        val vec: BlockVec3 = BlockVec3(this)
        val screenList: ArrayList<TileEntityScreen> = ArrayList<TileEntityScreen>()

        //		int side = this.getRight(meta);
        val side: EnumFacing = getFront().rotateY()

        for (x: Int in -left..right) {
            for (z: Int in -up..down) {
                val newVec: BlockVec3 =
                    vec.clone().modifyPositionFromSide(side, x).modifyPositionFromSide(EnumFacing.DOWN, z)
                val tile: TileEntity? = newVec.getTileEntity(this.world)
                if (tile is TileEntityScreen && tile.getBlockMetadata() == meta && !tile.isInvalid()) {
                    val screenTile: TileEntityScreen = tile
                    screenList.add(screenTile)

                    if (screenTile.isMultiscreen) {
                        if (screenTile.connectionsUp > z + up) {
                            barrierUp = -z - 1
                            existingScreen = true
                        }
                        if (screenTile.connectionsDown > down - z) {
                            barrierDown = z - 1
                            existingScreen = true
                        }
                        if (screenTile.connectionsLeft > x + left) {
                            barrierLeft = -x - 1
                            existingScreen = true
                        }
                        if (screenTile.connectionsRight > right - x) {
                            barrierRight = x - 1
                            existingScreen = true
                        }
                    }
                } else {
                    screenWhole = false
                }
            }
        }

        if (!screenWhole) {
            for (scr: TileEntityScreen in screenList) {
                scr.resetToSingle()
            }

            return false
        }

        if (existingScreen) {
            return this.checkWholeScreen(barrierUp, barrierDown, barrierLeft, barrierRight)
        }

        var newScreen: DrawGameScreen? = null
        var serverside: Boolean = true
        val bottomLeft: TileEntity? =
            vec.clone().modifyPositionFromSide(side, -left).modifyPositionFromSide(EnumFacing.DOWN, down)
                .getTileEntity(this.world)
        if (this.world.isRemote) {
            if (bottomLeft is TileEntityScreen)  //It always will be if reached this far
            {
                newScreen = bottomLeft.screen
                if (!newScreen!!.check(1.0f + left + right, 1.0f + up + down)) {
                    newScreen = DrawGameScreen(1.0f + left + right, 1.0f + up + down, bottomLeft)
                }
            }
            serverside = false
        }

        val it: MutableIterator<TileEntityScreen> = screenList.iterator()
        for (x: Int in -left..right) {
            for (z: Int in -up..down) {
                val screenTile: TileEntityScreen = it.next()
                screenTile.screenOffsetx = x + left
                screenTile.screenOffsetz = z + up
                screenTile.screen = newScreen
                screenTile.connectionsLeft = x + left
                screenTile.connectionsRight = right - x
                screenTile.connectionsUp = z + up
                screenTile.connectionsDown = down - z
                screenTile.isMultiscreen = true
                screenTile.refreshOnUpdate = false
                if (serverside) {
                    screenTile.imageType = this.imageType
                    screenTile.markDirty()
                    screenTile.updateAllInDimension()
                }
                screenTile.refreshConnections(false)
            }
        }

        this.connectionsUp = up
        this.connectionsDown = down
        this.connectionsLeft = left
        this.connectionsRight = right

        return true
    }

    /**
     * Reset the screen to a 1x1 size, not part of a 'multi-screen'
     */
    open fun resetToSingle() {
        this.screenOffsetx = 0
        this.screenOffsetz = 0
        this.connectionsUp = 0
        this.connectionsDown = 0
        this.connectionsLeft = 0
        this.connectionsRight = 0
        this.isMultiscreen = false
        //    	this.connectedDown = this.connectedLeft = this.connectedRight = this.connectedUp = false;
        this.setConnectedLeft(false)
        this.setConnectedRight(false)
        this.setConnectedUp(false)
        this.setConnectedDown(false)
        this.refreshOnUpdate = false
        this.markDirty()
        if (this.world.isRemote) {
            this.screen = DrawGameScreen(1.0f, 1.0f, this)
        } else {
            this.updateAllInDimension()
        }
    }

    /**
     * Get the Minecraft direction which is on the left side
     * for the block orientation given by metadata
     */
    private fun getLeft(meta: Int): Int {
        when (meta) {
            2 -> return 4
            3 -> return 5
            4 -> return 3
            5 -> return 2
        }
        return 4
    }

    /**
     * Get the Minecraft direction which is on the right side
     * for the block orientation given by metadata
     */
    private fun getRight(meta: Int): Int {
        when (meta) {
            2 -> return 5
            3 -> return 4
            4 -> return 2
            5 -> return 3
        }
        return 5
    }

    private fun canJoinRight(): Boolean {
        val meta: Int = this.getBlockMetadata()
        val te: TileEntity? = BlockVec3(this).getTileEntityOnSide(this.world, this.getRight(meta))
        if (te !is TileEntityScreen) {
            return false
        }
        val screenTile: TileEntityScreen = te
        if (screenTile.getBlockMetadata() != meta) {
            return false
        }
        if (screenTile.connectionsUp != this.connectionsUp) {
            return false
        }
        if (screenTile.connectionsDown != this.connectionsDown) {
            return false
        }
        if (this.connectionsUp + this.connectionsDown > 0) {
            return true
        }
        if (this.connectionsLeft > 0) {
            return false
        }
        if (screenTile.connectionsRight > 0) {
            return false
        }
        return true
    }


    private fun canJoinLeft(): Boolean {
        val meta: Int = this.getBlockMetadata()
        val te: TileEntity? = BlockVec3(this).getTileEntityOnSide(this.world, this.getLeft(meta))
        if (te !is TileEntityScreen) {
            return false
        }
        val screenTile: TileEntityScreen = te
        if (screenTile.getBlockMetadata() != meta) {
            return false
        }
        if (screenTile.connectionsUp != this.connectionsUp) {
            return false
        }
        if (screenTile.connectionsDown != this.connectionsDown) {
            return false
        }
        if (this.connectionsUp + this.connectionsDown > 0) {
            return true
        }
        if (this.connectionsRight > 0) {
            return false
        }
        if (screenTile.connectionsLeft > 0) {
            return false
        }
        return true
    }

    private fun canJoinUp(): Boolean {
        val meta: Int = this.getBlockMetadata()
        val te: TileEntity? = BlockVec3(this).getTileEntityOnSide(this.world, 1)
        if (te !is TileEntityScreen) {
            return false
        }
        val screenTile: TileEntityScreen = te
        if (screenTile.getBlockMetadata() != meta) {
            return false
        }
        if (screenTile.connectionsLeft != this.connectionsLeft) {
            return false
        }
        if (screenTile.connectionsRight != this.connectionsRight) {
            return false
        }
        if (this.connectionsLeft + this.connectionsRight > 0) {
            return true
        }
        if (this.connectionsDown > 0) {
            return false
        }
        if (screenTile.connectionsUp > 0) {
            return false
        }
        return true
    }

    private fun canJoinDown(): Boolean {
        val meta: Int = this.getBlockMetadata()
        val te: TileEntity? = BlockVec3(this).getTileEntityOnSide(this.world, 0)
        if (te !is TileEntityScreen) {
            return false
        }
        val screenTile: TileEntityScreen = te
        if (screenTile.getBlockMetadata() != meta) {
            return false
        }
        if (screenTile.connectionsLeft != this.connectionsLeft) {
            return false
        }
        if (screenTile.connectionsRight != this.connectionsRight) {
            return false
        }
        if (this.connectionsLeft + this.connectionsRight > 0) {
            return true
        }
        if (this.connectionsUp > 0) {
            return false
        }
        if (screenTile.connectionsDown > 0) {
            return false
        }
        return true
    }

    private fun joinRight() {
        val meta: Int = this.getBlockMetadata()
        val side: Int = this.getRight(meta)
        val vec: BlockVec3 = BlockVec3(this)
        for (z: Int in -this.connectionsUp..this.connectionsDown) {
            val tile: TileEntity?
            val newVec: BlockVec3 = vec.clone().modifyPositionFromSide(EnumFacing.DOWN, z)
            if (z == 0) {
                tile = this
            } else {
                tile = newVec.getTileEntity(this.world)
            }
            if (tile is TileEntityScreen && tile.getBlockMetadata() == meta && !tile.isInvalid()) {
                val screenTile: TileEntityScreen = tile
                //				screenTile.connectedRight = true;
                screenTile.setConnectedRight(true)
                val te2: TileEntity? = newVec.getTileEntityOnSide(this.world, side)
                if (te2 is TileEntityScreen && te2.getBlockMetadata() == meta && !te2.isInvalid()) {
                    screenTile.tryConnectRight(te2)
                }
            }
        }
    }

    private fun joinLeft() {
        val meta: Int = this.getBlockMetadata()
        val side: Int = this.getLeft(meta)
        val vec: BlockVec3 = BlockVec3(this)
        for (z: Int in -this.connectionsUp..this.connectionsDown) {
            val tile: TileEntity?
            val newVec: BlockVec3 = vec.clone().modifyPositionFromSide(EnumFacing.DOWN, z)
            if (z == 0) {
                tile = this
            } else {
                tile = newVec.getTileEntity(this.world)
            }
            if (tile is TileEntityScreen && tile.getBlockMetadata() == meta && !tile.isInvalid()) {
                val screenTile: TileEntityScreen = tile
                //				screenTile.connectedLeft = true;
                screenTile.setConnectedLeft(true)
                val te2: TileEntity? = newVec.getTileEntityOnSide(this.world, side)
                if (te2 is TileEntityScreen && te2.getBlockMetadata() == meta && !te2.isInvalid()) {
                    screenTile.tryConnectLeft(te2)
                }
            }
        }
    }

    private fun joinUp() {
        val meta: Int = this.getBlockMetadata()
        //    	EnumFacing side = EnumFacing.from3DDataValue(this.getRight(meta));
        val side: EnumFacing = getFront().rotateY()
        val vec: BlockVec3 = BlockVec3(this)
        for (x: Int in -this.connectionsLeft..this.connectionsRight) {
            val tile: TileEntity?
            val newVec: BlockVec3 = vec.clone().modifyPositionFromSide(side, x)
            if (x == 0) {
                tile = this
            } else {
                tile = newVec.getTileEntity(this.world)
            }
            if (tile is TileEntityScreen && tile.getBlockMetadata() == meta && !tile.isInvalid()) {
                val screenTile: TileEntityScreen = tile
                //				screenTile.connectedUp = true;
                screenTile.setConnectedUp(true)
                val te2: TileEntity? = newVec.getTileEntityOnSide(this.world, 1)
                if (te2 is TileEntityScreen && te2.getBlockMetadata() == meta && !te2.isInvalid()) {
                    screenTile.tryConnectUp(te2)
                }
            }
        }
    }

    private fun joinDown() {
        val meta: Int = this.getBlockMetadata()
        //    	EnumFacing side = EnumFacing.from3DDataValue(this.getRight(meta));
        val side: EnumFacing = getFront().rotateY()
        val vec: BlockVec3 = BlockVec3(this)
        for (x: Int in -this.connectionsLeft..this.connectionsRight) {
            val tile: TileEntity?
            val newVec: BlockVec3 = vec.clone().modifyPositionFromSide(side, x)
            if (x == 0) {
                tile = this
            } else {
                tile = newVec.getTileEntity(this.world)
            }
            if (tile is TileEntityScreen && tile.getBlockMetadata() == meta && !tile.isInvalid()) {
                val screenTile: TileEntityScreen = tile
                //				screenTile.connectedDown = true;
                screenTile.setConnectedDown(true)
                val te2: TileEntity? = newVec.getTileEntityOnSide(this.world, 0)
                if (te2 is TileEntityScreen && te2.getBlockMetadata() == meta && !te2.isInvalid()) {
                    screenTile.tryConnectDown(te2)
                }
            }
        }
    }

    private fun tryConnectUp(screenTile: TileEntityScreen?): Boolean {
        if (screenTile!!.connectedDown) {
            return true //No checks?
        }

        //		screenTile.connectedDown = true;
//		if (this.connectedLeft) screenTile.connectedLeft = true;
//		if (this.connectedRight) screenTile.connectedRight = true;
        screenTile.setConnectedDown(true)
        var vec: BlockVec3? = null
        if (this.connectedLeft) {
            vec = BlockVec3(screenTile)
            screenTile.setVerifiedConnectedLeft(vec)
        }
        if (this.connectedRight) {
            if (vec == null) vec = BlockVec3(screenTile)
            screenTile.setVerifiedConnectedRight(vec)
        }
        screenTile.refreshConnections(false)
        //Undo if the neighbour could not maintain the same left-right connections
        if ((this.connectedLeft xor screenTile.connectedLeft) || (this.connectedRight xor screenTile.connectedRight)) {
            screenTile.setConnectedDown(false)
            //			screenTile.connectedDown = false;
            return false
        }

        return true
    }

    private fun tryConnectDown(screenTile: TileEntityScreen?): Boolean {
        if (screenTile!!.connectedUp) {
            return true //No checks?
        }

        //		screenTile.connectedUp = true;
//		if (this.connectedLeft) screenTile.connectedLeft = true;
//		if (this.connectedRight) screenTile.connectedRight = true;
        screenTile.setConnectedUp(true)
        var vec: BlockVec3? = null
        if (this.connectedLeft) {
            vec = BlockVec3(screenTile)
            screenTile.setVerifiedConnectedLeft(vec)
        }
        if (this.connectedRight) {
            if (vec == null) vec = BlockVec3(screenTile)
            screenTile.setVerifiedConnectedRight(vec)
        }
        screenTile.refreshConnections(false)
        //Undo if the neighbour could not maintain the same left-right connections
        if ((this.connectedLeft xor screenTile.connectedLeft) || (this.connectedRight xor screenTile.connectedRight)) {
            screenTile.setConnectedUp(false)
            //			screenTile.connectedUp = false;
            return false
        }

        return true
    }

    private fun tryConnectLeft(screenTile: TileEntityScreen?): Boolean {
        if (screenTile!!.connectedRight) {
            return true //No checks?
        }

        if ((screenTile.connectedUp && !this.connectedUp) || (screenTile.connectedDown && !this.connectedDown)) {
            return false
        }

        //		screenTile.connectedRight = true;
//		if (this.connectedUp) screenTile.connectedUp = true;
//		if (this.connectedDown) screenTile.connectedDown = true;
        screenTile.setConnectedRight(true)
        var vec: BlockVec3? = null
        if (this.connectedUp) {
            vec = BlockVec3(screenTile)
            screenTile.setVerifiedConnectedUp(vec)
        }
        if (this.connectedDown) {
            if (vec == null) vec = BlockVec3(screenTile)
            screenTile.setVerifiedConnectedDown(vec)
        }
        screenTile.refreshConnections(false)
        //Undo if the neighbour could not maintain the same up-down connections
        if ((this.connectedUp xor screenTile.connectedUp) || (this.connectedDown xor screenTile.connectedDown)) {
            screenTile.setConnectedRight(false)
            //			screenTile.connectedRight = false;
            return false
        }

        return true
    }

    private fun tryConnectRight(screenTile: TileEntityScreen?): Boolean {
        if (screenTile!!.connectedLeft) {
            return true //No checks?
        }

        if ((screenTile.connectedUp && !this.connectedUp) || (screenTile.connectedDown && !this.connectedDown)) {
            return false
        }

        //		screenTile.connectedLeft = true;
//		if (this.connectedUp) screenTile.connectedUp = true;
//		if (this.connectedDown) screenTile.connectedDown = true;
        screenTile.setConnectedLeft(true)
        var vec: BlockVec3? = null
        if (this.connectedUp) {
            vec = BlockVec3(screenTile)
            screenTile.setVerifiedConnectedUp(vec)
        }
        if (this.connectedDown) {
            if (vec == null) vec = BlockVec3(screenTile)
            screenTile.setVerifiedConnectedDown(vec)
        }
        screenTile.refreshConnections(false)
        //Undo if the neighbour could not maintain the same up-down connections
        if ((this.connectedUp xor screenTile.connectedUp) || (this.connectedDown xor screenTile.connectedDown)) {
            screenTile.setConnectedLeft(false)
            //			screenTile.connectedLeft = false;
            return false
        }

        return true
    }

    private fun log(msg: String) {
        var connections: String = ""
        var strSide: String = "S"
        if (this.connectedUp) {
            connections = "U"
        }
        if (this.connectedDown) {
            connections += "D"
        }
        if (this.connectedLeft) {
            connections += "L"
        }
        if (this.connectedRight) {
            connections += "R"
        }
        if (this.world.isRemote) {
            strSide = "C"
        }
        //System.out.println(strSide + ":" + msg + " at "+this.getPos().getX()+","+this.getPos().getZ()+" "+connections);
    }

    @OnlyIn(Dist.CLIENT)
    open fun refreshNextTick(b: Boolean) {
        this.refreshOnUpdate = true
        TickHandlerClient.screenConnectionsUpdateList.add(this)
    }

    override fun getPacketRange(): Double {
        return 0.0
    }

    override fun getPacketCooldown(): Int {
        return 0
    }

    override fun isNetworkedTile(): Boolean {
        return false
    }

    @OnlyIn(Dist.CLIENT)
    override fun updateClient(data: MutableList<Any?>?) {
        val screenType: Int = (data)!!.get(1) as Int
        val flags: Int = (data)!!.get(2) as Int
        this.imageType = screenType
        this.connectedUp = (flags and 8) != 0
        this.connectedDown = (flags and 4) != 0
        this.connectedLeft = (flags and 2) != 0
        this.connectedRight = (flags and 1) != 0
        this.refreshNextTick(true)
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRenderBoundingBox(): AxisAlignedBB {
        if (this.renderAABB == null) {
            this.renderAABB = AxisAlignedBB(pos, pos.add(1, 1, 1))
        }
        return this.renderAABB!!
    }

    @OnlyIn(Dist.CLIENT)
    override fun getMaxRenderDistanceSquared(): Double {
        return Constants.RENDERDISTANCE_MEDIUM
    }

    companion object {
        @JvmField
        var FRAMEBORDER: Float = 0.098f //used for rendering
    }
}
