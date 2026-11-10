package micdoodle8.mods.galacticraft.core.tile

import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.blocks.BlockOxygenDetector
import micdoodle8.mods.galacticraft.core.util.OxygenUtil
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos

open class TileEntityOxygenDetector : TileEntity(), ITickable {
    private var ticks = 49
    private var oxygenSearch: AxisAlignedBB? = null

    override fun update() {
        if (!this.world.isRemote && ++this.ticks == 50) {
            this.ticks = 0
            if (this.getBlockType() is BlockOxygenDetector) {
                var oxygenFound: Boolean = false
                if (this.world.provider is IGalacticraftWorldProvider && !(this.world.provider as IGalacticraftWorldProvider).hasBreathableAtmosphere()) {
                    oxygenFound = OxygenUtil.isAABBInBreathableAirBlock(this.world, (this.oxygenSearch)!!, false)
                } else {
                    for (side: EnumFacing in EnumFacing.VALUES) {
                        val offset: BlockPos = this.pos.offset(side, 1)
                        val bs: IBlockState = this.world.getBlockState(offset)
                        if (!bs.getBlock().isSideSolid(bs, world, offset, side.getOpposite())) {
                            oxygenFound = true
                            break
                        }
                    }
                }
                (this.blockType as BlockOxygenDetector).updateOxygenState(this.world, this.getPos(), oxygenFound)
            }
        }
    }

    override fun onLoad() {
        this.oxygenSearch = AxisAlignedBB(
            this.getPos().getX() - 0.6,
            this.getPos().getY() - 0.6,
            this.getPos().getZ() - 0.6,
            this.getPos().getX() + 1.6,
            this.getPos().getY() + 1.6,
            this.getPos().getZ() + 1.6
        )
    }
}
