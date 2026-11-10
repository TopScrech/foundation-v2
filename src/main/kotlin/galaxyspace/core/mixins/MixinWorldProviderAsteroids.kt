package galaxyspace.core.mixins

import galaxyspace.core.configs.GSConfigCore
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.WorldProviderAsteroids
import org.spongepowered.asm.mixin.Mixin

@Mixin(value = [WorldProviderAsteroids::class], remap = false)
open class MixinWorldProviderAsteroids {
    /**
     * @author
     * @reason
     */
    @Inject(at = [At("HEAD")], method = ["getGravity"], cancellable = true)
    open fun getGravity(cir: CallbackInfoReturnable<Float>) {
        cir.setReturnValue(if (GSConfigCore.enableZeroGravityOnAsteroids) 0.08f else 0.072f)
    }
}
