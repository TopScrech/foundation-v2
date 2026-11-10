package galaxyspace.core.events

import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.eventhandler.Cancelable
import net.minecraftforge.fml.common.eventhandler.Event

@Cancelable
open class SetBlockEvent constructor(
    @JvmField val world: World?,
    @JvmField val pos: BlockPos?,
    @JvmField val block: IBlockState?,
    @JvmField val flags: Int
) : Event()
