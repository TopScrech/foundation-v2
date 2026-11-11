package micdoodle8.mods.galacticraft.core.energy

import net.minecraft.network.chat.Component
import net.minecraft.util.Mth as MathHelper
import net.minecraft.ChatFormatting as TextFormatting

object EnergyDisplayHelper {
    @JvmStatic
    fun getEnergyDisplayTooltip(energyVal: Float, maxEnergy: Float, strList: MutableList<String?>) {
        strList.add(
            TextFormatting.GREEN.toString() + Component.translatable("gui.message.energy").string + ": " + getEnergyDisplayS(
                energyVal
            )
        )
        strList.add(
            TextFormatting.RED.toString() + Component.translatable("gui.message.max_energy").string + ": " + getEnergyDisplayS(
                maxEnergy
            )
        )
    }

    @JvmStatic
    fun getEnergyDisplayS(energyVal: Float): String {
        if (EnergyConfigHandler.displayEnergyUnitsRF) {
            return getEnergyDisplayRF(energyVal * EnergyConfigHandler.TO_RF_RATIOdisp)
        }
        val `val`: String = getEnergyDisplayI(energyVal).toString()
        var newVal: String = ""

        for (i: Int in `val`.length - 1 downTo 0) {
            newVal += `val`.get(`val`.length - 1 - i)
            if (i % 3 == 0 && i != 0) {
                newVal += ','
            }
        }

        return newVal + " gJ"
    }

    @JvmStatic
    fun getEnergyDisplayRF(energyVal: Float): String {
        val `val`: String = getEnergyDisplayI(energyVal).toString()

        return `val` + " RF"
    }

    @JvmStatic
    fun getEnergyDisplayI(energyVal: Float): Int {
        return MathHelper.floor(energyVal)
    }

    @JvmStatic
    fun getEnergyDisplay1DP(energyVal: Float): String {
        return "" + MathHelper.floor(energyVal) + "." + (MathHelper.floor(energyVal * 10) % 10) + (MathHelper.floor(
            energyVal * 100
        ) % 10)
    }
}
