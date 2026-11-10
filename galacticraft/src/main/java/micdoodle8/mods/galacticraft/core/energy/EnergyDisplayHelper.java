package micdoodle8.mods.galacticraft.core.energy;

import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class EnergyDisplayHelper
{
    public static void getEnergyDisplayTooltip(float energyVal, float maxEnergy, List<String> strList)
    {
        strList.add(TextFormatting.GREEN + GCCoreUtil.translate("gui.message.energy") + ": " + getEnergyDisplayS(energyVal));
        strList.add(TextFormatting.RED + GCCoreUtil.translate("gui.message.max_energy") + ": " + getEnergyDisplayS(maxEnergy));
    }

    public static String getEnergyDisplayS(float energyVal)
    {
        if (EnergyConfigHandler.displayEnergyUnitsRF)
        {
            return getEnergyDisplayRF(energyVal * EnergyConfigHandler.TO_RF_RATIOdisp);
        }
        String val = String.valueOf(getEnergyDisplayI(energyVal));
        String newVal = "";

        for (int i = val.length() - 1; i >= 0; i--)
        {
            newVal += val.charAt(val.length() - 1 - i);
            if (i % 3 == 0 && i != 0)
            {
                newVal += ',';
            }
        }

        return newVal + " gJ";
    }

    public static String getEnergyDisplayRF(float energyVal)
    {
        String val = String.valueOf(getEnergyDisplayI(energyVal));

        return val + " RF";
    }

    public static int getEnergyDisplayI(float energyVal)
    {
        return MathHelper.floor(energyVal);
    }

    public static String getEnergyDisplay1DP(float energyVal)
    {
        return "" + MathHelper.floor(energyVal) + "." + (MathHelper.floor(energyVal * 10) % 10) + (MathHelper.floor(energyVal * 100) % 10);
    }
}
