package galaxyspace.core.mixins

import galaxyspace.core.events.SetBlockEvent
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.profiler.Profiler
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.WorldProvider
import net.minecraft.world.storage.ISaveHandler
import net.minecraft.world.storage.WorldInfo
import net.minecraftforge.common.MinecraftForge
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.Mixin

@Mixin(World::class)
abstract class MixinWorld protected constructor(
    saveHandlerIn: ISaveHandler?,
    info: WorldInfo?,
    providerIn: WorldProvider?,
    profilerIn: Profiler?,
    client: Boolean
) {
    @JvmField
    @Shadow
    @Final
    var provider: WorldProvider? = null

    @Inject(
        at = [At("RETURN")],
        method = ["setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z"],
        cancellable = true
    )
    fun `galaxyspace$setBlockState`(
        pos: BlockPos?,
        newState: IBlockState?,
        flags: Int,
        cir: CallbackInfoReturnable<Boolean>
    ) {
        val event: SetBlockEvent = SetBlockEvent(this.provider!!.world, pos, newState, flags)
        MinecraftForge.EVENT_BUS.post(event)

        cir.setReturnValue(!event.isCanceled())
    }
}
