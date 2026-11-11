package micdoodle8.mods.galacticraft.planets.venus.world.gen.dungeon

import foundation.block.getStateFromMeta

import micdoodle8.mods.galacticraft.core.world.gen.dungeon.DungeonConfiguration
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.nbt.CompoundTag as NBTTagCompound

open class DungeonConfigurationVenus : DungeonConfiguration {
    private var brickBlockFloor: IBlockState? = null

    constructor()

    constructor(
        brickBlock: IBlockState?,
        brickBlockFloor: IBlockState?,
        yPosition: Int,
        hallwayLengthMin: Int,
        hallwayLengthMax: Int,
        hallwayHeight: Int,
        roomHeight: Int,
        bossRoom: Class<*>?,
        treasureRoom: Class<*>?
    ) : super(
        brickBlock,
        yPosition,
        hallwayLengthMin,
        hallwayLengthMax,
        hallwayHeight,
        roomHeight,
        bossRoom,
        treasureRoom
    ) {
        this.brickBlockFloor = brickBlockFloor
    }

    override fun writeToNBT(tagCompound: NBTTagCompound): NBTTagCompound {
        tagCompound.putString(
            "brickBlock",
            Block.REGISTRY.getNameForObject(this.brickBlockFloor!!.getBlock()).toString()
        )

        return super.writeToNBT(tagCompound)
    }

    override fun readFromNBT(tagCompound: NBTTagCompound) {
        try {
            this.brickBlockFloor = Block.getBlockFromName(tagCompound.getStringOr("brickBlock", ""))!!
                .getStateFromMeta(tagCompound.getIntOr("brickBlockMeta", 0))
        } catch (e: Exception) {
            System.err.println("Failed to read dungeon configuration from NBT")
            e.printStackTrace()
        }

        super.readFromNBT(tagCompound)
    }

    open fun getBrickBlockFloor(): IBlockState? {
        return brickBlockFloor
    }
}
