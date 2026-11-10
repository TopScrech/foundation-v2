package micdoodle8.mods.galacticraft.core.tile

import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank

open class FluidTankGC : FluidTank {
    constructor(capacity: Int, tile: TileEntity?) : super(capacity) {
        this.tile = tile
    }

    constructor(stack: FluidStack?, capacity: Int, tile: TileEntity?) : super(stack, capacity) {
        this.tile = tile
    }

    open fun getTilePosition(): BlockPos {
        return this.tile.getPos()
    }

    open fun getTile(): TileEntity? {
        return this.tile
    }
}
