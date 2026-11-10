package galaxyspace.core.mixins

import galaxyspace.GalaxySpace
import org.spongepowered.asm.mixin.Overwrite
import micdoodle8.mods.galacticraft.api.world.EnumAtmosphericGas
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityGasLiquefier
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.WorldProvider
import org.spongepowered.asm.mixin.Mixin
import java.util.ArrayList
import java.util.Locale

@Mixin(value = [TileEntityGasLiquefier::class], remap = false)
open class MixinTileEntityGasLiquefier : TileEntity() {
    /**
     * @author ViTold
     * @reason
     */
    @Overwrite
    open fun getAirProducts(): Int {
        GalaxySpace.info("MIXIN TEST AIR PRODUCTS IN GL")
        val gl: TileEntityGasLiquefier = this as Any as TileEntityGasLiquefier

        val WP: WorldProvider? = this.getWorld().provider
        if (WP is IGalacticraftWorldProvider) {
            var result: Int = 0
            val atmos: ArrayList<EnumAtmosphericGas> =
                ((WP as IGalacticraftWorldProvider).getCelestialBody())!!.atmosphere.composition
            if (atmos.size > 0) {
                result = gl.getIdFromName((atmos.get(0) as EnumAtmosphericGas).name.lowercase(Locale.getDefault())) + 1
            }

            if (atmos.size > 1) {
                result += 16 * (gl.getIdFromName((atmos.get(1) as EnumAtmosphericGas).name.lowercase(Locale.getDefault())) + 1)
            }

            if (atmos.size > 2) {
                result += 256 * (gl.getIdFromName((atmos.get(2) as EnumAtmosphericGas).name.lowercase(Locale.getDefault())) + 1)
            }

            return result
        }
        return 35
    }
}
