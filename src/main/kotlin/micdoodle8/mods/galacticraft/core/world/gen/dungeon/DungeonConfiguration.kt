package micdoodle8.mods.galacticraft.core.world.gen.dungeon

import foundation.block.getStateFromMeta
import foundation.block.getMetaFromState

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.nbt.CompoundTag as NBTTagCompound
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
        tagCompound.putString("brickBlock", Block.REGISTRY.getNameForObject(this.brickBlock!!.getBlock()).toString())
        tagCompound.putInt("brickBlockMeta", this.brickBlock!!.getBlock().getMetaFromState(this.brickBlock))
        tagCompound.putInt("yPosition", this.yPosition)
        tagCompound.putInt("hallwayLengthMin", this.hallwayLengthMin)
        tagCompound.putInt("hallwayLengthMax", this.hallwayLengthMax)
        tagCompound.putInt("hallwayHeight", this.hallwayHeight)
        tagCompound.putInt("roomHeight", this.roomHeight)
        tagCompound.putString("bossRoom", this.bossRoom!!.getName())
        tagCompound.putString("treasureRoom", this.treasureRoom!!.getName())
        return tagCompound
    }

    open fun readFromNBT(tagCompound: NBTTagCompound) {
        try {
            this.brickBlock = Block.getBlockFromName(tagCompound.getStringOr("brickBlock", ""))!!
                .getStateFromMeta(tagCompound.getIntOr("brickBlockMeta", 0))
            this.yPosition = tagCompound.getIntOr("yPosition", 0)
            this.hallwayLengthMin = tagCompound.getIntOr("hallwayLengthMin", 0)
            this.hallwayLengthMax = tagCompound.getIntOr("hallwayLengthMax", 0)
            this.hallwayHeight = tagCompound.getIntOr("hallwayHeight", 0)
            this.roomHeight = tagCompound.getIntOr("roomHeight", 0)
            this.bossRoom = Class.forName(tagCompound.getStringOr("bossRoom", ""))
            this.treasureRoom = Class.forName(tagCompound.getStringOr("treasureRoom", ""))
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
