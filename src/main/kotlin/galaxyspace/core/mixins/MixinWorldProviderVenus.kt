package galaxyspace.core.mixins

import org.spongepowered.asm.mixin.Overwrite
import micdoodle8.mods.galacticraft.planets.venus.dimension.WorldProviderVenus
import org.spongepowered.asm.mixin.Mixin

@Mixin(value = [WorldProviderVenus::class], remap = false)
open class MixinWorldProviderVenus {
    /**
     * @author ViTold
     * @reason
     */
    @Overwrite
    open fun getSolarEnergyMultiplier(): Double {
        return 0.0
    }
}
