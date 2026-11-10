package micdoodle8.mods.galacticraft.core.energy;

import micdoodle8.mods.galacticraft.core.util.GCLog;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.ArrayList;

/**
 * The universal energy compatibility module allows Galacticraft to be
 * compatible with most other major power systems in Minecraft as well.
 *
 * @author Calclavia, Micdoodle, radfast
 */
public class EnergyConfigHandler
{

    private static Configuration config;

    /**
     * Ratio of RF energy to Galacticraft energy(gJ).
     * Multiply RF energy by this to convert to gJ.
     */
    public static float RF_RATIO = 1.6F;

    private static int conversionLossFactor = 100;

    /**
     * Convert gJ back to RF
     */
    public static float TO_RF_RATIO = 1 / EnergyConfigHandler.RF_RATIO;

    public static float TO_RF_RATIOdisp = 1 / EnergyConfigHandler.RF_RATIO;

    public static boolean displayEnergyUnitsRF = false;

    private static boolean cachedRFLoaded = false;
    private static boolean cachedRFLoadedValue = false;
    private static boolean cachedRF1LoadedValue = false;
    private static boolean cachedRF2LoadedValue = false;

    public static boolean disableRFInput = false;
    public static boolean disableRFOutput = false;
    public static boolean disableFEInput = false;
    public static boolean disableFEOutput = false;

    /**
     * You must call this function to enable the Universal Network module.
     */
    public static void setDefaultValues(File file)
    {
        if (EnergyConfigHandler.config == null)
        {
            EnergyConfigHandler.config = new Configuration(file);
        }

        EnergyConfigHandler.config.load();

        EnergyConfigHandler.RF_RATIO = (float) EnergyConfigHandler.config.get("Compatibility", "RF Conversion Ratio", EnergyConfigHandler.RF_RATIO).getDouble(EnergyConfigHandler.RF_RATIO);

        EnergyConfigHandler.conversionLossFactor = EnergyConfigHandler.config.get("Compatibility", "Loss factor when converting energy as a percentage (100 = no loss, 90 = 10% loss ...)", 100).getInt(100);
        if (EnergyConfigHandler.conversionLossFactor > 100)
        {
            EnergyConfigHandler.conversionLossFactor = 100;
        }
        if (EnergyConfigHandler.conversionLossFactor < 5)
        {
            EnergyConfigHandler.conversionLossFactor = 5;
        }

        updateRatios();

        EnergyConfigHandler.displayEnergyUnitsRF = EnergyConfigHandler.config.get("Display", "Show Galacticraft machines energy in RF instead of gJ?", false).getBoolean(false);

        EnergyConfigHandler.disableRFInput = EnergyConfigHandler.config.get("Compatibility", "Disable INPUT of RF energy", false).getBoolean(false);
        EnergyConfigHandler.disableRFOutput = EnergyConfigHandler.config.get("Compatibility", "Disable OUTPUT of RF energy", false).getBoolean(false);
        EnergyConfigHandler.disableFEInput = EnergyConfigHandler.config.get("Compatibility", "Disable INPUT of Forge Energy to GC machines", false).getBoolean(false);
        EnergyConfigHandler.disableFEOutput = EnergyConfigHandler.config.get("Compatibility", "Disable OUTPUT of Forge Energy from GC machines", false).getBoolean(false);

        EnergyConfigHandler.config.save();
    }

    public static boolean isRFAPILoaded()
    {
        if (!cachedRFLoaded)
        {
            initialiseRF();
        }

        return cachedRFLoadedValue;
    }

    public static boolean isRFAPIv1Loaded()
    {
        if (!cachedRFLoaded)
        {
            initialiseRF();
        }

        return cachedRF1LoadedValue;
    }

    public static boolean isRFAPIv2Loaded()
    {
        if (!cachedRFLoaded)
        {
            initialiseRF();
        }

        return cachedRF2LoadedValue;
    }

    private static void initialiseRF()
    {
        cachedRFLoaded = true;
        cachedRFLoadedValue = false;
        cachedRF2LoadedValue = false;
        int count = 0;
        int count2 = 0;
        try
        {
            if (Class.forName("cofh.redstoneflux.api.IEnergyConnection") != null)
            {
                count++;
            }
            if (Class.forName("cofh.redstoneflux.api.IEnergyHandler") != null)
            {
                count += 2;
            }
        }
        catch (Exception e)
        {
        }
        try
        {
            if (Class.forName("cofh.redstoneflux.api.IEnergyProvider") != null)
            {
                count2++;
            }
        }
        catch (Exception e)
        {
        }
        try
        {
            if (Class.forName("cofh.redstoneflux.api.IEnergyReceiver") != null)
            {
                count2++;
            }
        }
        catch (Exception e)
        {
        }

        if ((count + count2 == 3 && count2 != 1) || count + count2 == 5)
        {
            cachedRFLoadedValue = true;
            cachedRF1LoadedValue = (count == 3);
            cachedRF2LoadedValue = (count2 == 2);
        }
        else if (count > 0 || count2 > 0)
        {
            GCLog.severe("Incomplete Redstone Flux API detected: Galacticraft will not support RF energy connections until this is fixed.");
        }
    }

    private static void updateRatios()
    {
        //Sense checks to avoid crazy large inverse ratios or ratios

        if (EnergyConfigHandler.RF_RATIO < 0.001F)
        {
            EnergyConfigHandler.RF_RATIO = 0.001F;
        }
        if (EnergyConfigHandler.RF_RATIO > 100F)
        {
            EnergyConfigHandler.RF_RATIO = 100F;
        }

        float factor = conversionLossFactor / 100F;

        TO_RF_RATIO = factor / EnergyConfigHandler.RF_RATIO;

        TO_RF_RATIOdisp = 1 / EnergyConfigHandler.RF_RATIO;

        EnergyConfigHandler.RF_RATIO *= factor;

    }

    public static void serverConfigOverride(ArrayList<Object> returnList)
    {
        returnList.add(0F); // Reserved legacy network slot
        returnList.add(EnergyConfigHandler.RF_RATIO);
        returnList.add(0F); // Reserved legacy network slot
        returnList.add(0F); // Reserved legacy network slot
        returnList.add(EnergyConfigHandler.conversionLossFactor);
    }

    public static void setConfigOverride(float reserved1, float sRF, float reserved2, float reserved3, int sLossRatio)
    {

        EnergyConfigHandler.RF_RATIO = sRF;

        EnergyConfigHandler.conversionLossFactor = sLossRatio;
        updateRatios();
    }
}
