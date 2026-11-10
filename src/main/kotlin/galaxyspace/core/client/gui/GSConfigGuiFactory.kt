package galaxyspace.core.client.gui

import galaxyspace.GalaxySpace
import galaxyspace.core.configs.GSConfigCore
import galaxyspace.core.configs.GSConfigDimensions
import galaxyspace.core.configs.GSConfigEnergy
import galaxyspace.core.configs.GSConfigSchematics
import galaxyspace.systems.ACentauriSystem.core.configs.ACConfigCore
import galaxyspace.systems.ACentauriSystem.core.configs.ACConfigDimensions
import galaxyspace.systems.BarnardsSystem.core.configs.BRConfigCore
import galaxyspace.systems.BarnardsSystem.core.configs.BRConfigDimensions
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.client.IModGuiFactory
import net.minecraftforge.fml.client.config.GuiConfig

open class GSConfigGuiFactory : IModGuiFactory {
    open class CoreConfigGUI constructor(parent: GuiScreen?) :
        GuiConfig(parent, GalaxySpace.Companion.MODID, GCCoreUtil.translate("gs.configgui.title")) {
        init {
            this.configElements.addAll(GSConfigCore.getConfigElements())
            this.configElements.addAll(GSConfigDimensions.getConfigElements())
            this.configElements.addAll(GSConfigSchematics.getConfigElements())
            this.configElements.addAll(ACConfigDimensions.getConfigElements())
            this.configElements.addAll(BRConfigDimensions.getConfigElements())
            this.configElements.addAll(GSConfigEnergy.getConfigElements())
            this.configElements.addAll(ACConfigCore.getConfigElements())
            this.configElements.addAll(BRConfigCore.getConfigElements())
        }
    }

    override fun initialize(minecraftInstance: Minecraft?) {
    }

    override fun runtimeGuiCategories(): MutableSet<IModGuiFactory.RuntimeOptionCategoryElement?>? {
        return null
    }

    override fun createConfigGui(arg0: GuiScreen?): GuiScreen {
        return CoreConfigGUI(arg0)
    }

    override fun hasConfigGui(): Boolean {
        return true
    }
}
