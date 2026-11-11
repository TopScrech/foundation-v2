package galaxyspace.core.client.gui.overlay

import foundation.item.legacyItemValue

import foundation.inventory.armorItems
import foundation.persistence.containsTag

import asmodeuscore.AsmodeusCore
import asmodeuscore.core.astronomy.gui.overlay.OverlayDetectors
import galaxyspace.api.item.IModificationItem
import galaxyspace.core.client.GSKeyHandlerClient
import galaxyspace.core.configs.GSConfigCore
import galaxyspace.core.prefab.items.modules.ItemModule
import galaxyspace.systems.SolarSystem.planets.overworld.items.armor.ItemSpaceSuit
import micdoodle8.mods.galacticraft.core.client.gui.overlay.Overlay
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStatsClient
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer as EntityPlayerSP
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.EquipmentSlot as EntityEquipmentSlot
import net.minecraft.world.item.ItemStack
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.opengl.GL11

@OnlyIn(Dist.CLIENT)
object OverlaySpaceSuit : Overlay() {
    @JvmStatic
    fun renderSpaceSuitOverlay(player: EntityPlayer?) {
        if (player == null) return

        val mc: Minecraft = Minecraft.getInstance()
        val scaled: ScaledResolution = ScaledResolution(mc)
        val playerBaseClient: EntityPlayerSP = (PlayerUtil.getPlayerBaseClientFromPlayer(player, false))!!
        val stats: GCPlayerStatsClient? = GCPlayerStatsClient.get(playerBaseClient)


        var i: Int = 0

        /*
		if(player.world.provider instanceof IGalacticraftWorldProvider)
		{
			IGalacticraftWorldProvider prov = (IGalacticraftWorldProvider) player.world.provider;
			float temp = prov.getCelestialBody().atmosphere.thermalLevel() == 0.0F ? (prov.getThermalLevelModifier() * BodiesHelper.getDefaultDergrees) + BodiesHelper.getDefaultDergrees : prov.getThermalLevelModifier() * BodiesHelper.getDefaultDergrees;
			String str = GalaxySpace.debug ? "Thermal Level: " + String.format("%.4f", prov.getThermalLevelModifier()) + " | " + String.format("%.2f", temp) + " C" : "T: " + String.format("%.1f", temp) + " C";
			mc.fontRenderer.drawString(str, scaled.getScaledWidth() - (str.length() * 5) - 12, 75, 0xFFFFFF);
		}
		*/
        var hasSuit: Boolean = false
        for (stack: ItemStack in player.inventory.armorItems) {
            if (!stack.isEmpty() && stack.getItem() is ItemSpaceSuit) {
                var hasModule: Boolean = false
                var module: ItemStack = ItemStack.EMPTY

                if ((stack.getItem() as IModificationItem).getAvailableModules() != null) for (modules in ((stack.getItem() as IModificationItem).getAvailableModules())!!) {
                    if (stack.getTagCompound()!!.containsTag((modules)!!.getName()) && (modules)!!.isActiveModule()) {
                        hasModule = stack.getTagCompound()!!.getBoolean((modules)!!.getName())
                        module = ((modules)!!.getIcon())!!
                    }
                }

                if (module.isEmpty()) continue

                hasSuit = true
                break
            }
        }

        val keys: Array<String> = arrayOf<String>(
            GSKeyHandlerClient.toggleHelmet.getDisplayName(),
            GSKeyHandlerClient.toggleChest.getDisplayName(),
            GSKeyHandlerClient.toggleLegs.getDisplayName(),
            GSKeyHandlerClient.toggleBoots.getDisplayName()
        )

        var max: Int = 0
        for (key: String in keys) if (key.length > max) max = key.length

        val xOffset: Int = -35
        var yOffset: Int = scaled.getScaledHeight() - 140
        val guiTexture: ResourceLocation = ResourceLocation.fromNamespaceAndPath(AsmodeusCore.ASSET_PREFIX, "textures/gui/gui.png")

        if (GSConfigCore.spacesuit_pos == "up" || GSConfigCore.spacesuit_pos == "top") yOffset = 70

        if (GSConfigCore.spacesuit_pos == "center") yOffset = scaled.getScaledHeight() / 2 + 20

        if (GSConfigCore.spacesuit_pos == "down" || GSConfigCore.spacesuit_pos == "bottom") yOffset =
            scaled.getScaledHeight() - 140

        if (hasSuit) {
            //mc.entityRenderer.setupOverlayRendering();
            GlStateManager.enableBlend()
            GlStateManager.depthMask(false)
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)

            GlStateManager.disableLighting()
            GlStateManager.enableDepth()
            GlStateManager.enableAlpha()
            Minecraft.getInstance().renderEngine.bindTexture(guiTexture)
            OverlayDetectors.drawTexturedModalRect(
                xOffset.toDouble() + 35,
                yOffset.toDouble() - 78,
                (50 + (max * mc.fontRenderer.FONT_HEIGHT)).toFloat(),
                86f,
                0f,
                43f,
                30f,
                65f,
                false,
                false,
                256f,
                256f
            )
            GlStateManager.color(1f, 1f, 1f)
            GlStateManager.disableAlpha()
            GlStateManager.disableBlend()
        }


        for (stack: ItemStack in player.inventory.armorItems) {
            if (stack.isEmpty() || stack.getItem() !is ItemSpaceSuit) continue

            if (!stack.isEmpty() && stack.getItem() is ItemSpaceSuit) {
                var hasModule: Boolean = false
                var module: ItemStack = ItemStack.EMPTY

                if ((stack.getItem() as IModificationItem).getAvailableModules() != null) for (modules in ((stack.getItem() as IModificationItem).getAvailableModules())!!) {
                    if (stack.getTagCompound()!!.containsTag((modules)!!.getName()) && (modules)!!.isActiveModule()) {
                        hasModule = stack.getTagCompound()!!.getBoolean((modules)!!.getName())
                        module = ((modules)!!.getIcon())!!
                    }
                }

                if (module.isEmpty()) continue


                GL11.glPushMatrix()

                var enable: Boolean = false
                if (player.getItemStackFromSlot(EntityEquipmentSlot.HEAD) == stack) {
                    if (stack.getTagCompound()!!.getBoolean(ItemSpaceSuit.suit_buttons[0])) {
                        enable = true
                    }
                }



                if (player.getItemStackFromSlot(EntityEquipmentSlot.CHEST) == stack) {
                    if (stack.getTagCompound()!!.getBoolean(ItemSpaceSuit.suit_buttons[1])) {
                        enable = true
                    }
                }



                if (player.getItemStackFromSlot(EntityEquipmentSlot.LEGS) == stack) {
                    if (stack.getTagCompound()!!.getBoolean(ItemSpaceSuit.suit_buttons[2])) {
                        enable = true
                    }
                }



                if (player.getItemStackFromSlot(EntityEquipmentSlot.FEET) == stack) {
                    if (stack.getTagCompound()!!.getBoolean(ItemSpaceSuit.suit_buttons[3])) {
                        enable = true
                    }
                }


                val key: String = keys[3 - player.inventory.armorItems.indexOf(stack)]


                val horizontal: Boolean = false
                if (!module.isEmpty()) {
                    var status: EnumColor = if (enable) EnumColor.BRIGHT_GREEN else EnumColor.RED
                    if (stack.legacyItemValue >= stack.getMaxDamage()) status = EnumColor.ORANGE


                    RenderHelper.enableStandardItemLighting()
                    if (horizontal) {
                        mc.getRenderItem().renderItemAndEffectIntoGUI(module, xOffset - (i * 35) + 160, yOffset - 6)
                        mc.getRenderItem()
                            .renderItemOverlays(mc.fontRenderer, stack, xOffset - (i * 35) + 160, yOffset - 6)
                        RenderHelper.disableStandardItemLighting()
                        mc.fontRenderer.drawSplitString(
                            status.toString() + "[" + key + "] ",
                            xOffset - (i * 35) + 162 - ((key.length / 2) * 6),
                            yOffset + 10,
                            key.length * 20,
                            0xFFFFFF
                        )
                    } else {
                        mc.getRenderItem().renderItemIntoGUI(module, xOffset + 40, yOffset - (i * 20) - 15)
                        if (status == EnumColor.ORANGE) {
                            Minecraft.getInstance().renderEngine.bindTexture(guiTexture)
                            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
                            OverlayDetectors.drawTexturedModalRect(
                                (xOffset.toFloat() + 50).toDouble(),
                                (yOffset.toFloat() - 5 - (i * 20)).toDouble(),
                                15f,
                                15f,
                                81f,
                                14f,
                                10f,
                                10f,
                                false,
                                false,
                                256f,
                                256f
                            )
                        } else mc.getRenderItem()
                            .renderItemOverlays(mc.fontRenderer, stack, xOffset + 40, yOffset - (i * 20) - 15)

                        RenderHelper.disableStandardItemLighting()
                        mc.fontRenderer.drawString(
                            status.toString() + "[" + key + "] ",
                            xOffset + 60,
                            yOffset - (i * 20) - 10,
                            0xFFFFFF
                        )
                    }
                } else {
                    if (horizontal) mc.getRenderItem()
                        .renderItemAndEffectIntoGUI(stack, xOffset - (i * 20) + 60, yOffset - 10)
                    else mc.getRenderItem().renderItemAndEffectIntoGUI(stack, xOffset, yOffset - (i * 20))
                    //mc.fontRenderer.drawString(stack.getMaxDamage() - stack.getItemDamage() + "/" + stack.getMaxDamage(), xOffset + 20, scaled.getScaledHeight() - (yOffset - 5) - (i * 20), 0xFFFFFF);
                }


                GL11.glPopMatrix()
            }

            i++
        }
    }
}
