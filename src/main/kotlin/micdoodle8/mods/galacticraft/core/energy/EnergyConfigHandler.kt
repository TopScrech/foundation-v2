package micdoodle8.mods.galacticraft.core.energy

import java.util.ArrayList

import micdoodle8.mods.galacticraft.core.util.GCLog
import net.minecraftforge.common.config.Configuration
import java.io.File

/**
 * The universal energy compatibility module allows Galacticraft to be
 * compatible with most other major power systems in Minecraft as well.
 *
 * @author Calclavia, Micdoodle, radfast
 */
object EnergyConfigHandler {
    private var config: Configuration? = null

    /**
     * Ratio of RF energy to Galacticraft energy(gJ).
     * Multiply RF energy by this to convert to gJ.
     */
    @JvmField
    var RF_RATIO: Float = 1.6f

    private var conversionLossFactor = 100

    /**
     * Convert gJ back to RF
     */
    @JvmField
    var TO_RF_RATIO: Float = 1 / RF_RATIO

    @JvmField
    var TO_RF_RATIOdisp: Float = 1 / RF_RATIO

    @JvmField
    var displayEnergyUnitsRF: Boolean = false

    private var cachedRFLoaded = false
    private var cachedRFLoadedValue = false
    private var cachedRF1LoadedValue = false
    private var cachedRF2LoadedValue = false

    @JvmField
    var disableRFInput: Boolean = false

    @JvmField
    var disableRFOutput: Boolean = false

    @JvmField
    var disableFEInput: Boolean = false

    @JvmField
    var disableFEOutput: Boolean = false

    /**
     * You must call this function to enable the Universal Network module.
     */
    @JvmStatic
    fun setDefaultValues(file: File) {
        if (config == null) {
            config = Configuration(file)
        }

        config!!.load()

        RF_RATIO =
            config!!.get("Compatibility", "RF Conversion Ratio", RF_RATIO.toDouble()).getDouble(RF_RATIO.toDouble())
                .toFloat()

        conversionLossFactor = config!!.get(
            "Compatibility",
            "Loss factor when converting energy as a percentage (100 = no loss, 90 = 10% loss ...)",
            100
        ).getInt(100)
        if (conversionLossFactor > 100) {
            conversionLossFactor = 100
        }
        if (conversionLossFactor < 5) {
            conversionLossFactor = 5
        }

        updateRatios()

        displayEnergyUnitsRF =
            config!!.get("Display", "Show Galacticraft machines energy in RF instead of gJ?", false).getBoolean(false)

        disableRFInput = config!!.get("Compatibility", "Disable INPUT of RF energy", false).getBoolean(false)
        disableRFOutput = config!!.get("Compatibility", "Disable OUTPUT of RF energy", false).getBoolean(false)
        disableFEInput =
            config!!.get("Compatibility", "Disable INPUT of Forge Energy to GC machines", false).getBoolean(false)
        disableFEOutput =
            config!!.get("Compatibility", "Disable OUTPUT of Forge Energy from GC machines", false).getBoolean(false)

        config!!.save()
    }

    @JvmStatic
    fun isRFAPILoaded(): Boolean {
        if (!cachedRFLoaded) {
            initialiseRF()
        }

        return cachedRFLoadedValue
    }

    @JvmStatic
    fun isRFAPIv1Loaded(): Boolean {
        if (!cachedRFLoaded) {
            initialiseRF()
        }

        return cachedRF1LoadedValue
    }

    @JvmStatic
    fun isRFAPIv2Loaded(): Boolean {
        if (!cachedRFLoaded) {
            initialiseRF()
        }

        return cachedRF2LoadedValue
    }

    @JvmStatic
    private fun initialiseRF() {
        cachedRFLoaded = true
        cachedRFLoadedValue = false
        cachedRF2LoadedValue = false
        var count: Int = 0
        var count2: Int = 0
        try {
            if (Class.forName("cofh.redstoneflux.api.IEnergyConnection") != null) {
                count++
            }
            if (Class.forName("cofh.redstoneflux.api.IEnergyHandler") != null) {
                count += 2
            }
        } catch (e: Exception) {
        }
        try {
            if (Class.forName("cofh.redstoneflux.api.IEnergyProvider") != null) {
                count2++
            }
        } catch (e: Exception) {
        }
        try {
            if (Class.forName("cofh.redstoneflux.api.IEnergyReceiver") != null) {
                count2++
            }
        } catch (e: Exception) {
        }

        if ((count + count2 == 3 && count2 != 1) || count + count2 == 5) {
            cachedRFLoadedValue = true
            cachedRF1LoadedValue = (count == 3)
            cachedRF2LoadedValue = (count2 == 2)
        } else if (count > 0 || count2 > 0) {
            GCLog.severe("Incomplete Redstone Flux API detected: Galacticraft will not support RF energy connections until this is fixed.")
        }
    }

    @JvmStatic
    private fun updateRatios() {
        //Sense checks to avoid crazy large inverse ratios or ratios

        if (RF_RATIO < 0.001f) {
            RF_RATIO = 0.001f
        }
        if (RF_RATIO > 100f) {
            RF_RATIO = 100f
        }

        val factor: Float = conversionLossFactor / 100f

        TO_RF_RATIO = factor / RF_RATIO

        TO_RF_RATIOdisp = 1 / RF_RATIO

        RF_RATIO *= factor
    }

    @JvmStatic
    fun serverConfigOverride(returnList: ArrayList<Any?>) {
        returnList.add(0f) // Reserved legacy network slot
        returnList.add(RF_RATIO)
        returnList.add(0f) // Reserved legacy network slot
        returnList.add(0f) // Reserved legacy network slot
        returnList.add(conversionLossFactor)
    }

    @JvmStatic
    fun setConfigOverride(reserved1: Float, sRF: Float, reserved2: Float, reserved3: Float, sLossRatio: Int) {
        RF_RATIO = sRF

        conversionLossFactor = sLossRatio
        updateRatios()
    }
}
