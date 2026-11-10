package galaxyspace.core

import galaxyspace.core.prefab.potions.AntiRadiation
import galaxyspace.core.util.GSUtils
import net.minecraftforge.fml.common.registry.ForgeRegistries

object GSPotions {
    @JvmField
    var antiradiation: AntiRadiation? = null

    @JvmStatic
    fun initialize() {
        initPotions()
    }

    @JvmStatic
    private fun initPotions() {
        antiradiation = AntiRadiation(true, GSUtils.getColor(255, 10, 100, 10))

        ForgeRegistries.POTIONS.register(antiradiation)
    }
}
