package micdoodle8.mods.galacticraft.api.client.tabs

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.inventory.ContainerPlayer
import net.minecraft.network.play.client.CPacketCloseWindow
import net.minecraft.potion.PotionEffect
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.lang.Exception
import java.util.ArrayList

open class TabRegistry {
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    open fun guiPostInit(event: GuiScreenEvent.InitGuiEvent.Post) {
        if (event.getGui() is GuiInventory) {
            var guiLeft: Int = (event.getGui().width - 176) / 2
            val guiTop: Int = (event.getGui().height - 166) / 2
            recipeBookOffset =
                Companion.getRecipeBookOffset((event.getGui() as net.minecraft.client.gui.inventory.GuiInventory?)!!)
            guiLeft += getPotionOffset() + recipeBookOffset

            updateTabValues(guiLeft, guiTop, InventoryTabVanilla::class.java)
            addTabsToList(event.getButtonList())
        }
    }

    companion object {
        private val tabList = ArrayList<AbstractTab>()
        private var clazzJEIConfig: Class<*>? = null

        @JvmField
        var clazzNEIConfig: Class<*>? = null

        init {
            try {
                //Checks for JEI by looking for this class instead of a Loader.isModLoaded() check
                clazzJEIConfig = Class.forName("mezz.jei.config.Config")
            } catch (ignore: Exception) {
                //no log spam
            }
            //Only activate NEI feature if NEI is standalone
            if (clazzJEIConfig == null) {
                try {
                    clazzNEIConfig = Class.forName("codechicken.nei.NEIClientConfig")
                } catch (ignore: Throwable) {
                    //no log spam
                }
            }
        }

        @JvmStatic
        fun registerTab(tab: AbstractTab) {
            tabList.add(tab)
        }

        @JvmStatic
        fun getTabList(): ArrayList<AbstractTab> {
            return tabList
        }

        //Retained for backwards compatibility with TC pre version 1.6.0d40
        @JvmStatic
        fun addTabsToInventory(gui: GuiContainer?) {
        }

        private val mc: Minecraft = FMLClientHandler.instance().getClient()
        private var initWithPotion = false

        @JvmField
        var recipeBookOffset: Int = 0

        @JvmStatic
        fun openInventoryGui() {
            mc.player.connection.sendPacket(CPacketCloseWindow(mc.player.openContainer.windowId))
            val inventory: GuiInventory = GuiInventory(mc.player)
            mc.displayGuiScreen(inventory)
        }

        @JvmStatic
        fun updateTabValues(cornerX: Int, cornerY: Int, selectedButton: Class<*>?) {
            var count: Int = 2
            for (i: Int in tabList.indices) {
                val t: AbstractTab = tabList.get(i)

                if (t.shouldAddToList()) {
                    t.id = count
                    t.x = cornerX + (count - 2) * 28
                    t.y = cornerY - 28
                    t.enabled = t.javaClass != selectedButton
                    t.potionOffsetLast = getPotionOffsetNEI()
                    count++
                }
            }
        }

        @JvmStatic
        fun addTabsToList(buttonList: MutableList<GuiButton?>) {
            for (tab: AbstractTab in tabList) {
                if (tab.shouldAddToList()) {
                    buttonList.add(tab)
                }
            }
        }

        @JvmStatic
        fun getPotionOffset(): Int {
            /*Disabled in 1.12.2 because a vanilla bug means potion offsets are currently not a thing
 *The vanilla bug is that GuiInventory.initGui() resets GuiLeft to the recipe book version of GuiLeft,
 *and in GuiRecipeBook.updateScreenPosition() it takes no account of potion offset even if the recipe book is inactive.

		// If at least one potion is active...
		if (doPotionOffsetVanilla())
		{
			initWithPotion = true;
			return 60 + getPotionOffsetJEI() + getPotionOffsetNEI();
		}
 */

            // No potions, no offset needed

            initWithPotion = false
            return 0
        }

        @JvmStatic
        fun doPotionOffsetVanilla(): Boolean {
            for (potioneffect: PotionEffect in mc.player.getActivePotionEffects()) {
                if (potioneffect.getPotion().shouldRender(potioneffect)) {
                    return true
                }
            }
            return false
        }

        @JvmStatic
        fun getPotionOffsetJEI(): Int {
            if (clazzJEIConfig != null) {
                try {
                    val enabled: Any? = clazzJEIConfig!!.getMethod("isOverlayEnabled").invoke(null)
                    if (enabled is Boolean) {
                        if (!enabled) {
                            // If JEI is disabled, no special change to getPotionOffset()
                            return 0
                        }
                        //Active JEI undoes the standard potion offset (they listen for GuiScreenEvent.PotionShiftEvent)
                        return -60
                    }
                } catch (ignore: Exception) {
                    //no log spam
                }
            }
            return 0
        }

        @JvmStatic
        fun getPotionOffsetNEI(): Int {
            if (initWithPotion && clazzNEIConfig != null) {
                try {
                    // Check whether NEI is hidden and enabled
                    val hidden: Any? = clazzNEIConfig!!.getMethod("isHidden").invoke(null)
                    val enabled: Any? = clazzNEIConfig!!.getMethod("isEnabled").invoke(null)
                    if (hidden is Boolean && enabled is Boolean) {
                        if (hidden || !enabled) {
                            // If NEI is disabled or hidden, it does not affect the tabs offset with potions 
                            return 0
                        }
                        //But active NEI undoes the standard potion offset
                        return -60
                    }
                } catch (ignore: Exception) {
                    //no log spam
                }
            }
            //No NEI, no change
            return 0
        }

        @JvmStatic
        fun getRecipeBookOffset(gui: GuiInventory): Int {
            val widthTooNarrow: Boolean = gui.width < 379
            gui.func_194310_f().func_194303_a(
                gui.width,
                gui.height,
                mc,
                widthTooNarrow,
                (gui.inventorySlots as ContainerPlayer).craftMatrix
            )
            return gui.func_194310_f()
                .updateScreenPosition(widthTooNarrow, gui.width, gui.xSize) - (gui.width - 176) / 2
        }
    }
}
