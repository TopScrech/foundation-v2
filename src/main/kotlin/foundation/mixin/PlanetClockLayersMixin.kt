package foundation.mixin

import foundation.level.FoundationPlanetClocks
import net.minecraft.world.attribute.EnvironmentAttributeSystem
import net.minecraft.world.level.Level
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(value = [EnvironmentAttributeSystem.Builder::class], remap = false)
abstract class PlanetClockLayersMixin {
    @Inject(method = ["addDynamicLayers"], at = [At("HEAD")], cancellable = true, remap = false)
    private fun foundationPlanetTime(level: Level, callback: CallbackInfoReturnable<EnvironmentAttributeSystem.Builder>) {
        val builder = this as Any as EnvironmentAttributeSystem.Builder
        if (FoundationPlanetClocks.install(builder, level)) callback.returnValue = builder
    }
}
