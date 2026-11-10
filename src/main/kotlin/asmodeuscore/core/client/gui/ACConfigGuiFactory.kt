package asmodeuscore.core.client.gui

import asmodeuscore.core.configs.AsmodeusConfig
import asmodeuscore.AsmodeusCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.client.IModGuiFactory
import net.minecraftforge.fml.client.config.GuiConfig

open class ACConfigGuiFactory : IModGuiFactory {
    open class CoreConfigGUI constructor(parent: GuiScreen?) :
        GuiConfig(parent, AsmodeusCore.MODID, GCCoreUtil.translate("ac.configgui.title")) {
        init {
            this.configElements.addAll(AsmodeusConfig.getConfigElements())
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
