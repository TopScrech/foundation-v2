package galaxyspace.core.events

import galaxyspace.core.events.UpdateBlockEvent
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.eventhandler.Cancelable
import net.minecraftforge.fml.common.eventhandler.Event
import java.util.Random

@Cancelable
open class UpdateBlockEvent constructor(
    @JvmField val world: World?,
    @JvmField val pos: BlockPos?,
    @JvmField val block: IBlockState?,
    @JvmField val rand: Random?
) : Event()
