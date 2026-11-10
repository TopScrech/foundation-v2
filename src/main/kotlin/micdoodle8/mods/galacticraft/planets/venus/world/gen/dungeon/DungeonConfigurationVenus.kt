package micdoodle8.mods.galacticraft.planets.venus.world.gen.dungeon

import micdoodle8.mods.galacticraft.core.world.gen.dungeon.DungeonConfiguration
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.nbt.NBTTagCompound

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
        tagCompound.setString(
            "brickBlock",
            Block.REGISTRY.getNameForObject(this.brickBlockFloor!!.getBlock()).toString()
        )

        return super.writeToNBT(tagCompound)
    }

    override fun readFromNBT(tagCompound: NBTTagCompound) {
        try {
            this.brickBlockFloor = Block.getBlockFromName(tagCompound.getString("brickBlock"))!!
                .getStateFromMeta(tagCompound.getInteger("brickBlockMeta"))
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
