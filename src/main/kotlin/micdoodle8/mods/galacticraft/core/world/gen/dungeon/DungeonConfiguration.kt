package micdoodle8.mods.galacticraft.core.world.gen.dungeon

import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.nbt.NBTTagCompound
import java.lang.Exception

open class DungeonConfiguration {
    private var brickBlock: IBlockState? = null
    private var yPosition = 0
    private var hallwayLengthMin = 0
    private var hallwayLengthMax = 0
    private var hallwayHeight = 0
    private var roomHeight = 0
    private var bossRoom: Class<*>? = null
    private var treasureRoom: Class<*>? = null

    constructor()

    constructor(
        brickBlock: IBlockState?,
        yPosition: Int,
        hallwayLengthMin: Int,
        hallwayLengthMax: Int,
        hallwayHeight: Int,
        roomHeight: Int,
        bossRoom: Class<*>?,
        treasureRoom: Class<*>?
    ) {
        this.brickBlock = brickBlock
        this.yPosition = yPosition
        this.hallwayLengthMin = hallwayLengthMin
        this.hallwayLengthMax = hallwayLengthMax
        this.hallwayHeight = hallwayHeight
        this.roomHeight = roomHeight
        this.bossRoom = bossRoom
        this.treasureRoom = treasureRoom
    }

    open fun writeToNBT(tagCompound: NBTTagCompound): NBTTagCompound {
        tagCompound.setString("brickBlock", Block.REGISTRY.getNameForObject(this.brickBlock!!.getBlock()).toString())
        tagCompound.setInteger("brickBlockMeta", this.brickBlock!!.getBlock().getMetaFromState(this.brickBlock))
        tagCompound.setInteger("yPosition", this.yPosition)
        tagCompound.setInteger("hallwayLengthMin", this.hallwayLengthMin)
        tagCompound.setInteger("hallwayLengthMax", this.hallwayLengthMax)
        tagCompound.setInteger("hallwayHeight", this.hallwayHeight)
        tagCompound.setInteger("roomHeight", this.roomHeight)
        tagCompound.setString("bossRoom", this.bossRoom!!.getName())
        tagCompound.setString("treasureRoom", this.treasureRoom!!.getName())
        return tagCompound
    }

    open fun readFromNBT(tagCompound: NBTTagCompound) {
        try {
            this.brickBlock = Block.getBlockFromName(tagCompound.getString("brickBlock"))!!
                .getStateFromMeta(tagCompound.getInteger("brickBlockMeta"))
            this.yPosition = tagCompound.getInteger("yPosition")
            this.hallwayLengthMin = tagCompound.getInteger("hallwayLengthMin")
            this.hallwayLengthMax = tagCompound.getInteger("hallwayLengthMax")
            this.hallwayHeight = tagCompound.getInteger("hallwayHeight")
            this.roomHeight = tagCompound.getInteger("roomHeight")
            this.bossRoom = Class.forName(tagCompound.getString("bossRoom"))
            this.treasureRoom = Class.forName(tagCompound.getString("treasureRoom"))
        } catch (e: Exception) {
            System.err.println("Failed to read dungeon configuration from NBT")
            e.printStackTrace()
        }
    }

    open fun getBrickBlock(): IBlockState? {
        return brickBlock
    }

    open fun getYPosition(): Int {
        return yPosition
    }

    open fun getHallwayLengthMin(): Int {
        return hallwayLengthMin
    }

    open fun getHallwayLengthMax(): Int {
        return hallwayLengthMax
    }

    open fun getHallwayHeight(): Int {
        return hallwayHeight
    }

    open fun getRoomHeight(): Int {
        return roomHeight
    }

    open fun getBossRoom(): Class<*>? {
        return bossRoom
    }

    open fun getTreasureRoom(): Class<*>? {
        return treasureRoom
    }
}
