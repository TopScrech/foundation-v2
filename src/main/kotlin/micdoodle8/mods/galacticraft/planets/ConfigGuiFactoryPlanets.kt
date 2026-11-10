package micdoodle8.mods.galacticraft.planets

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.client.IModGuiFactory
import net.minecraftforge.fml.client.config.GuiConfig

open class ConfigGuiFactoryPlanets : IModGuiFactory {
    open class PlanetsConfigGUI constructor(parent: GuiScreen?) : GuiConfig(
        parent,
        GalacticraftPlanets.Companion.getConfigElements(),
        Constants.MOD_ID_PLANETS,
        false,
        false,
        GCCoreUtil.translate("gc.configgui.planets.title")
    )


    override fun initialize(minecraftInstance: Minecraft?) {
    }

    override fun runtimeGuiCategories(): MutableSet<IModGuiFactory.RuntimeOptionCategoryElement?>? {
        return null
    }

    override fun createConfigGui(arg0: GuiScreen?): GuiScreen {
        // TODO  Forge 2282 addition!
        return PlanetsConfigGUI(arg0)
    }

    override fun hasConfigGui(): Boolean {
        return true
    }
}
