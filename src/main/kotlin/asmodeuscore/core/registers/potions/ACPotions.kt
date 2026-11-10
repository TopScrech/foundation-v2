package asmodeuscore.core.registers.potions

import asmodeuscore.core.registers.potions.ACPotions
import asmodeuscore.core.prefab.potions.Overheat
import asmodeuscore.core.prefab.potions.Radiation
import asmodeuscore.core.prefab.potions.Hypothermia
import asmodeuscore.core.utils.Utils
import net.minecraftforge.fml.common.registry.ForgeRegistries

object ACPotions {
    @JvmField
    var radiation: Radiation? = null

    @JvmField
    var overheat: Overheat? = null

    @JvmField
    var hypothermia: Hypothermia? = null

    @JvmStatic
    fun initialize() {
        initPotions()
    }

    @JvmStatic
    private fun initPotions() {
        radiation = Radiation(true, -4502242)
        overheat = Overheat(true, Utils.getIntColor(150, 80, 0))
        hypothermia = Hypothermia(true, Utils.getIntColor(30, 144, 255))

        ForgeRegistries.POTIONS.register(radiation)
        ForgeRegistries.POTIONS.register(overheat)
        ForgeRegistries.POTIONS.register(hypothermia)
    }
}
