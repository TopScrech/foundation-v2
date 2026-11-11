package micdoodle8.mods.galacticraft.core.util

import micdoodle8.mods.galacticraft.core.util.ThreadRequirementMissing
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiMissingCore
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.LogicalSide as Side
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class ThreadRequirementMissing constructor(threadSide: Side?) :
    Thread("Galacticraft Requirement Check Thread") {
    init {
        this.setDaemon(true)
        Companion.threadSide = threadSide
    }

    override fun run() {
        if (!Loader.isModLoaded("micdoodlecore")) {
            if (threadSide!!.isServer()) {
                FMLCommonHandler.instance().getMinecraftServerInstance()
                    .logSevere("===================================================================")
                FMLCommonHandler.instance().getMinecraftServerInstance()
                    .logSevere("MicdoodleCore not found in mods folder. Galacticraft will not load.")
                FMLCommonHandler.instance().getMinecraftServerInstance()
                    .logSevere("===================================================================")
            } else {
                openGuiClient()
            }
        }
    }

    companion object {
        private var threadSide: Side? = null

        @JvmField
        var INSTANCE: ThreadRequirementMissing? = null

        @JvmStatic
        fun beginCheck(threadSide: Side?) {
            INSTANCE = ThreadRequirementMissing(threadSide)
            INSTANCE!!.start()
        }

        @JvmStatic
        @OnlyIn(Dist.CLIENT)
        private fun openGuiClient() {
            FMLClientHandler.instance().getClient().displayGuiScreen(GuiMissingCore())
        }
    }
}
