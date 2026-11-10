package asmodeuscore.core.astronomy.dimension.world.gen.dungeons.standart

import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.nbt.NBTTagCompound
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
        tagCompound.setString("brickBlock", Block.REGISTRY.getNameForObject(this.brickBlock!!.getBlock()).toString())
        tagCompound.setInteger("brickBlockMeta", this.brickBlock!!.getBlock().getMetaFromState(this.brickBlock))

        if (topBlock != null) {
            tagCompound.setString("topBlock", Block.REGISTRY.getNameForObject(this.topBlock!!.getBlock()).toString())
            tagCompound.setInteger("topBlockMeta", this.topBlock!!.getBlock().getMetaFromState(this.topBlock))
        }
        if (floorBlock != null) {
            tagCompound.setString(
                "floorBlock",
                Block.REGISTRY.getNameForObject(this.floorBlock!!.getBlock()).toString()
            )
            tagCompound.setInteger("floorBlockMeta", this.floorBlock!!.getBlock().getMetaFromState(this.floorBlock))
        }

        tagCompound.setInteger("yPosition", this.yPosition)
        tagCompound.setInteger("hallwayLengthMin", this.hallwayLengthMin)
        tagCompound.setInteger("hallwayLengthMax", this.hallwayLengthMax)
        tagCompound.setInteger("hallwayHeight", this.hallwayHeight)
        tagCompound.setInteger("roomHeight", this.roomHeight)

        if (this.corridor != null) tagCompound.setString("corridor", this.corridor!!.getName())
        if (this.emptyRoom != null) tagCompound.setString("emptyRoom", this.emptyRoom!!.getName())
        if (this.spawnerRoom != null) tagCompound.setString("spawnerRoom", this.spawnerRoom!!.getName())
        if (this.chestRoom != null) tagCompound.setString("chestRoom", this.chestRoom!!.getName())

        tagCompound.setString("bossRoom", this.bossRoom!!.getName())
        tagCompound.setString("treasureRoom", this.treasureRoom!!.getName())
        return tagCompound
    }

    open fun readFromNBT(tagCompound: NBTTagCompound) {
        try {
            this.brickBlock = Block.getBlockFromName(tagCompound.getString("brickBlock"))!!
                .getStateFromMeta(tagCompound.getInteger("brickBlockMeta"))
            if (topBlock != null) this.topBlock = Block.getBlockFromName(tagCompound.getString("topBlock"))!!
                .getStateFromMeta(tagCompound.getInteger("topBlockMeta"))
            if (floorBlock != null) this.floorBlock = Block.getBlockFromName(tagCompound.getString("floorBlock"))!!
                .getStateFromMeta(tagCompound.getInteger("floorBlockMeta"))
            this.yPosition = tagCompound.getInteger("yPosition")
            this.hallwayLengthMin = tagCompound.getInteger("hallwayLengthMin")
            this.hallwayLengthMax = tagCompound.getInteger("hallwayLengthMax")
            this.hallwayHeight = tagCompound.getInteger("hallwayHeight")
            this.roomHeight = tagCompound.getInteger("roomHeight")
            if (this.corridor != null) this.corridor = Class.forName(tagCompound.getString("corridor"))
            if (this.emptyRoom != null) this.emptyRoom = Class.forName(tagCompound.getString("emptyRoom"))
            if (this.spawnerRoom != null) this.spawnerRoom = Class.forName(tagCompound.getString("spawnerRoom"))
            if (this.chestRoom != null) this.chestRoom = Class.forName(tagCompound.getString("chestRoom"))
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
