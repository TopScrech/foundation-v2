package micdoodle8.mods.galacticraft.planets.asteroids.world.gen.base

import micdoodle8.mods.galacticraft.planets.asteroids.world.gen.base.BaseDeck.EnumBaseType
import micdoodle8.mods.galacticraft.planets.asteroids.world.gen.base.BaseRoom.EnumRoomType

import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import java.lang.Exception
import java.util.Random

open class BaseConfiguration {
    private var yPosition = 0
    private var baseType = 0
    private var hangar = false
    private var roomHeight = 0
    private var roomDepth = 0
    private var wallBlock: IBlockState? = null
    private var roomsNo = 0
    private var randomRoomTypes: IntArray? = null
    private val roomTypes: Array<BaseRoom.EnumRoomType> = BaseRoom.EnumRoomType.entries.toTypedArray()

    constructor()

    constructor(yPosition: Int, rand: Random) {
        val types: Array<BaseDeck.EnumBaseType> = BaseDeck.EnumBaseType.entries.toTypedArray()
        this.yPosition = yPosition - 2 + rand.nextInt(5)
        this.baseType = rand.nextInt(types.size)
        this.hangar = rand.nextInt(3) == 0
        this.roomHeight = types[this.baseType]!!.height
        this.roomDepth = if (this.hangar) 7 else rand.nextInt(3) + 5
        this.wallBlock = types[this.baseType]!!.wall
        this.roomsNo = rand.nextInt(2) + 2
        this.createRandomRoomList(rand)
    }

    private fun createRandomRoomList(rand: Random) {
        val range: Int = this.roomTypes.size
        val size: Int = if (this.hangar) 8 else this.roomsNo * 6
        this.randomRoomTypes = IntArray(size)
        for (i: Int in 0..<size) {
            this.randomRoomTypes!![i] = i % range
        }
        var index: Int
        var temp: Int
        for (i: Int in size - 1 downTo 1) {
            index = rand.nextInt(i + 1)
            if (i == index) continue
            temp = this.randomRoomTypes!![index]
            this.randomRoomTypes!![index] = this.randomRoomTypes!![i]
            this.randomRoomTypes!![i] = temp
        }


        //Make sure there's a Cargo Loader on lower tier (50/50 chance this causes one other room to be missed completely, that's OK!)
        if (this.hangar) {
            var storeFound: Boolean = false
            for (i: Int in 0..3) {
                if (this.randomRoomTypes!![i] == BaseRoom.EnumRoomType.STORE.ordinal) {
                    storeFound = true
                    break
                }
            }
            if (!storeFound) {
                this.randomRoomTypes!![rand.nextInt(4)] = BaseRoom.EnumRoomType.STORE.ordinal
            }
        }
    }

    open fun writeToNBT(tagCompound: NBTTagCompound) {
        tagCompound.putInt("yPos", this.yPosition)
        tagCompound.putInt("dT", this.baseType + (if (this.hangar) 16 else 0))
        tagCompound.putInt("rmD", this.roomDepth)
        tagCompound.putInt("rmN", this.roomsNo)
    }

    open fun readFromNBT(tagCompound: NBTTagCompound) {
        try {
            this.yPosition = tagCompound.getIntOr("yPos", 0)
            this.baseType = tagCompound.getIntOr("dT", 0)
            this.hangar = false
            if (this.baseType >= 16) {
                this.hangar = true
                this.baseType -= 16
            }
            this.roomDepth = tagCompound.getIntOr("rmD", 0)
            this.roomsNo = tagCompound.getIntOr("rmN", 0)
            this.roomHeight = BaseDeck.EnumBaseType.entries[this.baseType].height
            this.wallBlock = BaseDeck.EnumBaseType.entries[this.baseType].wall
        } catch (e: Exception) {
            System.err.println("Failed to read Abandoned Base configuration from NBT")
            System.err.println(tagCompound.toString())
        }
    }

    open fun getYPosition(): Int {
        return this.yPosition
    }

    open fun getWallBlock(): IBlockState? {
        return this.wallBlock
    }

    open fun getRoomHeight(): Int {
        return if (hangar) HANGAR_AIRLOCK_HEIGHT else this.roomHeight
    }

    open fun getRoomDepth(): Int {
        return this.roomDepth
    }

    open fun getDeckType(): BaseDeck.EnumBaseType? {
        return BaseDeck.EnumBaseType.entries[this.baseType]
    }

    open fun isHangarDeck(): Boolean {
        return hangar
    }

    open fun getCorridorWidth(): Int {
        return if (hangar) HANGAR_AIRLOCK_WIDTH else this.getDeckType()!!.width
    }

    open fun getRoomsNo(): Int {
        return if (hangar) 2 else roomsNo
    }

    open fun getCorridorLength(): Int {
        if (getRoomsNo() == 1) return BaseDeck.Companion.ROOMLARGE

        if (getRoomsNo() == 2) return BaseDeck.Companion.ROOMLARGE + BaseDeck.Companion.ROOMLARGE

        return getRoomsNo() * BaseDeck.Companion.ROOMSMALL + 2 * (BaseDeck.Companion.ROOMLARGE - BaseDeck.Companion.ROOMSMALL)
    }

    open fun getRandomRoom(i: Int): BaseRoom.EnumRoomType? {
        return roomTypes[this.randomRoomTypes!![i % this.randomRoomTypes!!.size]]
    }

    companion object {
        private const val HANGAR_AIRLOCK_HEIGHT = 6
        private const val HANGAR_AIRLOCK_WIDTH = 7
    }
}
