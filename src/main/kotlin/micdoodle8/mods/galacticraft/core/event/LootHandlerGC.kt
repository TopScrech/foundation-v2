package micdoodle8.mods.galacticraft.core.event

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import net.minecraft.util.ResourceLocation
import net.minecraft.world.storage.loot.LootTableList

object LootHandlerGC {
    @JvmField
    var TABLE_CRASHED_PROBE: ResourceLocation? = null

    @JvmStatic
    fun registerAll() {
        if (GalacticraftCore.isPlanetsLoaded) TABLE_CRASHED_PROBE = register("crashed_probe")
    }

    @JvmStatic
    private fun register(table: String): ResourceLocation {
        return LootTableList.register(ResourceLocation(Constants.MOD_ID_CORE, table))
    }
}
