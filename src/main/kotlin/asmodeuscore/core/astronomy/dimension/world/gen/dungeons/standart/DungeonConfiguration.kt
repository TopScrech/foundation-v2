package asmodeuscore.core.astronomy.dimension.world.gen.dungeons.standart

import foundation.block.getStateFromMeta
import foundation.block.getMetaFromState

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import java.lang.Exception

open class DungeonConfiguration {
    private var brickBlock: IBlockState? = null
    private var topBlock: IBlockState? = null
    private var floorBlock: IBlockState? = null
    private var yPosition = 0
    private var hallwayLengthMin = 0
    private var hallwayLengthMax = 0
    private var hallwayHeight = 0
    private var roomHeight = 0
    private var corridor: Class<*>? = null
    private var emptyRoom: Class<*>? = null
    private var spawnerRoom: Class<*>? = null
    private var chestRoom: Class<*>? = null
    private var bossRoom: Class<*>? = null
    private var treasureRoom: Class<*>? = null

    constructor()

    constructor(
        brickBlock: IBlockState?,
        topBlock: IBlockState?,
        floorBlock: IBlockState?,
        yPosition: Int,
        hallwayLengthMin: Int,
        hallwayLengthMax: Int,
        hallwayHeight: Int,
        roomHeight: Int,
        corridor: Class<*>?,
        emptyRoom: Class<*>?,
        spawnerRoom: Class<*>?,
        chestRoom: Class<*>?,
        bossRoom: Class<*>?,
        treasureRoom: Class<*>?
    ) {
        this.brickBlock = brickBlock
        this.topBlock = topBlock
        this.floorBlock = floorBlock

        this.yPosition = yPosition
        this.hallwayLengthMin = hallwayLengthMin
        this.hallwayLengthMax = hallwayLengthMax
        this.hallwayHeight = hallwayHeight
        this.roomHeight = roomHeight
        this.corridor = corridor
        this.emptyRoom = emptyRoom
        this.spawnerRoom = spawnerRoom
        this.chestRoom = chestRoom
        this.bossRoom = bossRoom
        this.treasureRoom = treasureRoom
    }

    open fun writeToNBT(tagCompound: NBTTagCompound): NBTTagCompound {
        tagCompound.putString("brickBlock", Block.REGISTRY.getNameForObject(this.brickBlock!!.getBlock()).toString())
        tagCompound.putInt("brickBlockMeta", this.brickBlock!!.getBlock().getMetaFromState(this.brickBlock))

        if (topBlock != null) {
            tagCompound.putString("topBlock", Block.REGISTRY.getNameForObject(this.topBlock!!.getBlock()).toString())
            tagCompound.putInt("topBlockMeta", this.topBlock!!.getBlock().getMetaFromState(this.topBlock))
        }
        if (floorBlock != null) {
            tagCompound.putString(
                "floorBlock",
                Block.REGISTRY.getNameForObject(this.floorBlock!!.getBlock()).toString()
            )
            tagCompound.putInt("floorBlockMeta", this.floorBlock!!.getBlock().getMetaFromState(this.floorBlock))
        }

        tagCompound.putInt("yPosition", this.yPosition)
        tagCompound.putInt("hallwayLengthMin", this.hallwayLengthMin)
        tagCompound.putInt("hallwayLengthMax", this.hallwayLengthMax)
        tagCompound.putInt("hallwayHeight", this.hallwayHeight)
        tagCompound.putInt("roomHeight", this.roomHeight)

        if (this.corridor != null) tagCompound.putString("corridor", this.corridor!!.getName())
        if (this.emptyRoom != null) tagCompound.putString("emptyRoom", this.emptyRoom!!.getName())
        if (this.spawnerRoom != null) tagCompound.putString("spawnerRoom", this.spawnerRoom!!.getName())
        if (this.chestRoom != null) tagCompound.putString("chestRoom", this.chestRoom!!.getName())

        tagCompound.putString("bossRoom", this.bossRoom!!.getName())
        tagCompound.putString("treasureRoom", this.treasureRoom!!.getName())
        return tagCompound
    }

    open fun readFromNBT(tagCompound: NBTTagCompound) {
        try {
            this.brickBlock = Block.getBlockFromName(tagCompound.getStringOr("brickBlock", ""))!!
                .getStateFromMeta(tagCompound.getIntOr("brickBlockMeta", 0))
            if (topBlock != null) this.topBlock = Block.getBlockFromName(tagCompound.getStringOr("topBlock", ""))!!
                .getStateFromMeta(tagCompound.getIntOr("topBlockMeta", 0))
            if (floorBlock != null) this.floorBlock = Block.getBlockFromName(tagCompound.getStringOr("floorBlock", ""))!!
                .getStateFromMeta(tagCompound.getIntOr("floorBlockMeta", 0))
            this.yPosition = tagCompound.getIntOr("yPosition", 0)
            this.hallwayLengthMin = tagCompound.getIntOr("hallwayLengthMin", 0)
            this.hallwayLengthMax = tagCompound.getIntOr("hallwayLengthMax", 0)
            this.hallwayHeight = tagCompound.getIntOr("hallwayHeight", 0)
            this.roomHeight = tagCompound.getIntOr("roomHeight", 0)
            if (this.corridor != null) this.corridor = Class.forName(tagCompound.getStringOr("corridor", ""))
            if (this.emptyRoom != null) this.emptyRoom = Class.forName(tagCompound.getStringOr("emptyRoom", ""))
            if (this.spawnerRoom != null) this.spawnerRoom = Class.forName(tagCompound.getStringOr("spawnerRoom", ""))
            if (this.chestRoom != null) this.chestRoom = Class.forName(tagCompound.getStringOr("chestRoom", ""))
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

    open fun getOtherBlock(floor: Boolean): IBlockState? {
        if (floor) return floorBlock
        return topBlock
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

    open fun getCorridor(): Class<*>? {
        return corridor
    }

    open fun getEmptyRoom(): Class<*>? {
        return emptyRoom
    }

    open fun getSpawnerRoom(): Class<*>? {
        return spawnerRoom
    }

    open fun getChestRoom(): Class<*>? {
        return chestRoom
    }

    open fun getBossRoom(): Class<*>? {
        return bossRoom
    }

    open fun getTreasureRoom(): Class<*>? {
        return treasureRoom
    }
}
