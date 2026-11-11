package galaxyspace.core.mixins

import org.spongepowered.asm.mixin.Overwrite
import micdoodle8.mods.galacticraft.api.world.EnumAtmosphericGas
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityMethaneSynthesizer
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.world.WorldProvider
import org.spongepowered.asm.mixin.Mixin
import java.util.ArrayList

@Mixin(TileEntityMethaneSynthesizer::class)
open class MixinTileEntityMethaneSynthesizer : TileEntity() {
    /**
     * @author ViTold
     * @reason
     */
    @Overwrite(remap = false)
    open fun getAirProducts(): Int {
        val WP: WorldProvider? = this.getWorld().provider
        if (WP is IGalacticraftWorldProvider) {
            val atmos: ArrayList<EnumAtmosphericGas> =
                ((WP as IGalacticraftWorldProvider).getCelestialBody())!!.atmosphere.composition

            if (atmos.contains(EnumAtmosphericGas.CO2)) return 1
        }
        return 0
    }
}
