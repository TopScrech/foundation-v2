package micdoodle8.mods.galacticraft.core.client.gui.screen

import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen as GuiScreen
import net.minecraftforge.fml.client.IModGuiFactory
import net.minecraftforge.fml.client.config.GuiConfig

open class ConfigGuiFactoryCore : IModGuiFactory {
    open class CoreConfigGUI constructor(parent: GuiScreen?) : GuiConfig(
        parent,
        ConfigManagerCore.getConfigElements(),
        Constants.MOD_ID_CORE,
        false,
        false,
        GCCoreUtil.translate("gc.configgui.title")
    )


    override fun initialize(minecraftInstance: Minecraft?) {
    }

    override fun runtimeGuiCategories(): MutableSet<IModGuiFactory.RuntimeOptionCategoryElement?>? {
        return null
    }

    override fun createConfigGui(arg0: GuiScreen?): GuiScreen {
        // TODO  Forge 2282 addition!
        return CoreConfigGUI(arg0)
    }

    override fun hasConfigGui(): Boolean {
        return true
    }
}
