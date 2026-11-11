package galaxyspace.core.events

import galaxyspace.core.events.UpdateBlockEvent
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
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
