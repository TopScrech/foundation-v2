package micdoodle8.mods.galacticraft.core.tile

import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.blocks.BlockConcealedDetector
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.util.ITickable
import net.minecraft.world.phys.AABB as AxisAlignedBB

open class TileEntityPlayerDetector : TileEntity(), ITickable {
    private var ticks = 24
    private var playerSearch: AxisAlignedBB? = null
    private var result = false

    override fun update() {
        if (!this.world.isRemote && ++this.ticks >= 25) {
            this.ticks = 0
            var facing: Int = 0
            val state: IBlockState = this.world.getBlockState(this.pos)
            if (state.getBlock() === GCBlocks.concealedDetector) {
                facing = state.getValue(BlockConcealedDetector.FACING)
            }
            val x: Int = this.getPos().getX()
            val y: Double = this.getPos().getY().toDouble()
            val z: Int = this.getPos().getZ()
            val range: Double = 14.0
            val hysteresis: Double = if (result) 3.0 else 0.0
            when (facing) {
                0 -> this.playerSearch = AxisAlignedBB(
                    x - range / 2 + 0.5 - hysteresis,
                    y - 6 - hysteresis,
                    z - range - hysteresis,
                    x + range / 2 + 0.5 + hysteresis,
                    y + 2 + hysteresis,
                    z + hysteresis
                )

                1 -> this.playerSearch = AxisAlignedBB(
                    x + 1 - hysteresis,
                    y - 6 - hysteresis,
                    z - range / 2 + 0.5 - hysteresis,
                    x + range + 1 + hysteresis,
                    y + 2 + hysteresis,
                    z + range / 2 + 0.5 + hysteresis
                )

                2 ->                 //South
                    this.playerSearch = AxisAlignedBB(
                        x - range / 2 + 0.5 - hysteresis,
                        y - 6 - hysteresis,
                        z + 1 - hysteresis,
                        x + range / 2 + 0.5 + hysteresis,
                        y + 2 + hysteresis,
                        z + range + 1.0 + hysteresis
                    )

                3 -> this.playerSearch = AxisAlignedBB(
                    x - range - hysteresis,
                    y - 6 - hysteresis,
                    z - range / 2 + 0.5 - hysteresis,
                    x + hysteresis,
                    y + 2 + hysteresis,
                    z + range / 2 + 0.5 + hysteresis
                )
            }
            result = !this.world.getEntitiesWithinAABB<EntityPlayer>(EntityPlayer::class.java, playerSearch).isEmpty()
            if (this.getBlockType() is BlockConcealedDetector) {
                (this.blockType as BlockConcealedDetector).updateState(this.world, this.getPos(), result)
            }
        }
    }

    open fun detectingPlayer(): Boolean {
        return result
    }
}
