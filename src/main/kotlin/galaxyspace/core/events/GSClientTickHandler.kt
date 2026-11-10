package galaxyspace.core.events

import java.util.HashMap
import java.util.Random
import org.lwjgl.opengl.GL11
import net.minecraft.client.model.ModelBiped.ArmPose
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent

import asmodeuscore.core.astronomy.gui.book.ACGuiGuideBook
import asmodeuscore.core.utils.BookUtils.Book_Cateroies
import asmodeuscore.core.utils.Utils
import galaxyspace.GalaxySpace
import galaxyspace.api.item.IJetpackArmor
import galaxyspace.api.item.IModificationItem
import galaxyspace.core.client.gui.GSGuiMainMenu
import galaxyspace.core.client.gui.book.pages.general.Page_ActualUpdate
import galaxyspace.core.client.gui.overlay.OverlaySpaceSuit
import galaxyspace.core.configs.GSConfigCore
import galaxyspace.core.network.packet.GSPacketSimple
import galaxyspace.core.network.packet.GSPacketSimple.GSEnumSimplePacket
import galaxyspace.core.util.GSThreadVersionCheck
import galaxyspace.core.util.GSUtils
import galaxyspace.systems.SolarSystem.planets.overworld.items.tools.ItemGeologicalScanner
import galaxyspace.systems.SolarSystem.planets.overworld.items.tools.ItemMatterManipulator
import micdoodle8.mods.galacticraft.api.event.ZeroGravityEvent
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiMainMenu
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelBiped
import net.minecraft.client.model.ModelPlayer
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderGlobal
import net.minecraft.client.renderer.Tessellator
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumHandSide
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.client.event.RenderLivingEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.event.entity.player.ItemTooltipEvent
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*
import kotlin.math.min

open class GSClientTickHandler {
    @JvmField
    var mc: Minecraft = FMLClientHandler.instance().getClient()

    @JvmField
    var rand: Random? = null

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    open fun onGuiOpenEvent(event: GuiOpenEvent) {
        if ((event.getGui() is GuiMainMenu) && GSConfigCore.Companion.enableNewMenu) event.setGui(GSGuiMainMenu())

        /*if (event.gui instanceof GuiRocketInventory && mc.thePlayer.ridingEntity instanceof EntityTier4Rocket)
		{
			event.gui = new GSGuiRocketInventory(mc.thePlayer.inventory, (EntityTier4Rocket) mc.thePlayer.ridingEntity, ((EntityTier4Rocket) mc.thePlayer.ridingEntity).getType());
		}*/
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    open fun onToolTip(e: ItemTooltipEvent) {
        if (e.getItemStack().getItem() is IModificationItem) {
            val mod: IModificationItem = e.getItemStack().getItem() as IModificationItem
            if (mod.getType(e.getItemStack()) != null && mod.getAvailableModules() != null) {
                //e.getToolTip().add("");
                e.getToolTip().add(1, EnumColor.AQUA.toString() + GCCoreUtil.translate("gui.module.caninstall"))
            }
        }

        if (e.getItemStack().isItemEqual(ItemStack(MarsItems.schematic, 1, 0))) {
            if (GSConfigCore.Companion.enableAdvancedRocketCraft) {
                //e.getToolTip().add("");
                e.getToolTip().add(1, EnumColor.RED.toString() + "Disabled. See new recipe in JEI/NEI!")
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    open fun onClientTick(event: TickEvent.ClientTickEvent) {
        val player: EntityPlayerSP? = Minecraft.getMinecraft().player
        val world: WorldClient? = mc.world

        if (event.phase == TickEvent.Phase.END) {
            if (Side.CLIENT != null) {
                if (player != null && world != null) {
                    if (GSThreadVersionCheck.newversion && mc.inGameHasFocus && GSConfigCore.Companion.enableCheckVersion) {
                        GSThreadVersionCheck.newversion = false


                        //Minecraft.getMinecraft().addScheduledTask(() -> FMLClientHandler.instance().showGuiScreen(new GSGuiUpdate()));
                        Minecraft.getMinecraft().addScheduledTask(Runnable {
                            FMLClientHandler.instance().showGuiScreen(
                                ACGuiGuideBook(
                                    ACGuiGuideBook.Mode.TEXT,
                                    Book_Cateroies.GENERAL.getName(),
                                    Page_ActualUpdate(true)
                                )
                            )
                        })
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = false)
    open fun onLivingRender(event: RenderLivingEvent.Post<*>) {
        if (event.getEntity() is EntityPlayer) {
            val player: EntityPlayer = event.getEntity() as EntityPlayer

            val mainhand: ItemStack = player.getHeldItemMainhand()
            val offhand: ItemStack = player.getHeldItemOffhand()

            if (!mainhand.isEmpty() && mainhand.getItem() is ItemMatterManipulator || !offhand.isEmpty() && offhand.getItem() is ItemMatterManipulator) {
                val mdl: ModelBase = event.getRenderer().getMainModel()

                if (mdl is ModelPlayer) {
                    val model: ModelPlayer = mdl
                    if (player.getPrimaryHand() == EnumHandSide.RIGHT) {
                        model.bipedRightArm.rotateAngleY = -0.1f + model.bipedHead.rotateAngleY
                        //model.rightArmPose = ArmPose.BOW_AND_ARROW;
                    } else {
                        model.leftArmPose = ModelBiped.ArmPose.BOW_AND_ARROW
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    @SideOnly(Side.CLIENT)
    open fun onRenderTick(event: TickEvent.RenderTickEvent) {
        val minecraft: Minecraft = FMLClientHandler.instance().getClient()
        val player: EntityPlayerSP? = minecraft.player
        val playerBaseClient: EntityPlayerSP? = PlayerUtil.getPlayerBaseClientFromPlayer(player, false)

        if (event.phase == TickEvent.Phase.END) {
            if (player != null) {
                /*
        		if(player.ridingEntity instanceof EntityTieredRocket)
        		{
        			OverlayRocketHelp.renderSpaceshipOverlay();
        		}*/

                GlStateManager.disableLighting()

                GlStateManager.pushMatrix()
                if (minecraft.inGameHasFocus && !minecraft.gameSettings.hideGUI && GSConfigCore.Companion.enableSpaceSuitHUD) OverlaySpaceSuit.renderSpaceSuitOverlay(
                    playerBaseClient
                )
                GlStateManager.popMatrix()
                //System.out.println(ticks);
                val scaled: ScaledResolution = ScaledResolution(mc)
                if (minecraft.inGameHasFocus && !minecraft.gameSettings.hideGUI && ticks > 0) {
                    GlStateManager.pushMatrix()
                    val xPos: Int = 10
                    val yPos: Int = scaled.getScaledHeight() / 2 - 50
                    val color: Int = Utils.getIntColorWHC(0, 0, 0, 150)
                    var offsetY: Int = 0
                    mc.fontRenderer.drawStringWithShadow(
                        "Geological Scanner Data:",
                        xPos.toFloat(),
                        (yPos - 10).toFloat(),
                        0xFFFFFF
                    )
                    for (block: MutableMap.MutableEntry<IBlockState?, String?> in blocks.entries) {
                        val item: ItemStack = ItemStack(
                            Item.getItemFromBlock(block.key!!.getBlock()),
                            1,
                            block.key!!.getBlock().getMetaFromState(block.key)
                        )

                        Gui.drawRect(xPos, yPos + (24 * offsetY), xPos + 180, yPos + 22 + (24 * offsetY), color)
                        mc.getRenderItem().zLevel = -100f
                        mc.getRenderItem().renderItemIntoGUI(item, xPos + 3, yPos + (24 * offsetY) + 3)
                        mc.fontRenderer.drawStringWithShadow(
                            block.value,
                            (xPos + 25).toFloat(),
                            (yPos + (24 * offsetY) + 6).toFloat(),
                            0xFFFFFF
                        )
                        offsetY++
                    }


                    GlStateManager.popMatrix()
                }
                if (ticks > 0 && player.ticksExisted % 10 == 0) ticks--
            }
        }
    }

    /*
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onDrawBlockSelectionBox(DrawBlockHighlightEvent e) {
		
	}
	*/
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    open fun onZeroGravity(event: ZeroGravityEvent.InFreefall) {
        val entity: EntityLivingBase = event.getEntityLiving()
        if (GSEventHandler.Companion.inGravityZone(entity.getEntityWorld(), entity, false)) event.setCanceled(true)
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    open fun renderBlocks(e: RenderWorldLastEvent?) {
        val player: EntityPlayerSP = Minecraft.getMinecraft().player
        val stack: ItemStack = player.getHeldItemMainhand()

        if (player != null && stack != null && stack.getItem() is ItemGeologicalScanner && stack.getItemDamage() < stack.getMaxDamage() && stack.getTagCompound()!!
                .getInteger("mode") == 0
        ) {
            val ray: RayTraceResult? = GSUtils.getRay(player.getEntityWorld(), player, false)

            if (ray != null && ray.hitVec.distanceTo(player.getPositionVector()) < 5.0f) {
                val tess: Tessellator = Tessellator.getInstance()
                val worldRenderer: BufferBuilder = tess.getBuffer()

                //GalaxySpace.debug(ray.hitVec.distanceTo(player.getPositionVector()) + "");
                GlStateManager.enableBlend()
                GlStateManager.tryBlendFuncSeparate(
                    GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                    GlStateManager.SourceFactor.ONE,
                    GlStateManager.DestFactor.ZERO
                )
                GlStateManager.glLineWidth(2.0f)
                GlStateManager.disableTexture2D()
                GlStateManager.depthMask(false)
                val blockpos: BlockPos = ray.getBlockPos()
                val iblockstate: IBlockState = player.world.getBlockState(blockpos)

                if (iblockstate.getMaterial() !== Material.AIR && player.world.getWorldBorder().contains(blockpos)) {
                    val d3: Double =
                        player.lastTickPosX + (player.posX - player.lastTickPosX) * player.ticksExisted.toDouble()
                    val d4: Double =
                        player.lastTickPosY + (player.posY - player.lastTickPosY) * player.ticksExisted.toDouble()
                    val d5: Double =
                        player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * player.ticksExisted.toDouble()
                    RenderGlobal.drawSelectionBoundingBox(
                        iblockstate.getSelectedBoundingBox(player.world, blockpos)
                            .grow(2.0).offset(-d3, -d4, -d5), 1.0f, 0.0f, 0.0f, 0.4f
                    )


                    //FMLClientHandler.instance().getClient().renderEngine.bindTexture(new ResourceLocation(GalacticraftPlanets.ASSET_PREFIX, "textures/misc/gradient.png"));
                }

                GlStateManager.depthMask(true)
                GlStateManager.enableTexture2D()
                GlStateManager.disableBlend()
                /*GlStateManager.pushMatrix();
	    		FMLClientHandler.instance().getClient().renderEngine.bindTexture(new ResourceLocation(GalacticraftPlanets.ASSET_PREFIX, "textures/misc/gradient.png"));
	    		GlStateManager.disableDepth();
	            GlStateManager.enableTexture2D();
	            net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
	            Minecraft.getMinecraft().entityRenderer.disableLightmap();
	            GlStateManager.disableTexture2D();
	    		GlStateManager.translate(ray.getBlockPos().getX(), ray.getBlockPos().getY() + 1, ray.getBlockPos().getZ());
	    		final Tessellator tess = Tessellator.getInstance();
	    		BufferBuilder worldRenderer = tess.getBuffer();
	    		GlStateManager.color(1.0F, 0.7F, 0.7F, 1.0F);
	    		float cA = -0.01F;
	    		float cB = 1.01F;

	    		worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        		worldRenderer.pos(cA, cB, cA).tex(0D, 1D).endVertex();
        		worldRenderer.pos(cB, cB, cA).tex(1D, 1D).endVertex();
        		worldRenderer.pos(cB, cB, cB).tex(1D, 0D).endVertex();
        		worldRenderer.pos(cA, cB, cB).tex(0D, 0D).endVertex();
        		tess.draw();
        		worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        		worldRenderer.pos(cA, cA, cA).tex(0D, 0D).endVertex();
        		worldRenderer.pos(cA, cA, cB).tex(0D, 1D).endVertex();
        		worldRenderer.pos(cB, cA, cB).tex(1D, 1D).endVertex();
        		worldRenderer.pos(cB, cA, cA).tex(1D, 0D).endVertex();
        		tess.draw();
        		worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        		worldRenderer.pos(cA, cA, cA).tex(1D, 0D).endVertex();
        		worldRenderer.pos(cA, cB, cA).tex(0D, 0D).endVertex();
        		worldRenderer.pos(cA, cB, cB).tex(0D, 1D).endVertex();
        		worldRenderer.pos(cA, cA, cB).tex(1D, 1D).endVertex();
        		tess.draw();
        		worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        		worldRenderer.pos(cB, cA, cA).tex(1D, 1D).endVertex();
        		worldRenderer.pos(cB, cA, cB).tex(1D, 0D).endVertex();
        		worldRenderer.pos(cB, cB, cB).tex(0D, 0D).endVertex();
        		worldRenderer.pos(cB, cB, cA).tex(0D, 1D).endVertex();
        		tess.draw();
        		worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        		worldRenderer.pos(cA, cA, cA).tex(1D, 0D).endVertex();
        		worldRenderer.pos(1F, cA, cA).tex(0D, 0D).endVertex();
        		worldRenderer.pos(1F, 1F, cA).tex(0D, 1D).endVertex();
        		worldRenderer.pos(cA, 1F, cA).tex(1D, 1D).endVertex();
        		tess.draw();
        		worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        		worldRenderer.pos(1F, cA, 1F).tex(1D, 1D).endVertex();
        		worldRenderer.pos(cA, cA, 1F).tex(1D, 0D).endVertex();
        		worldRenderer.pos(cA, 1F, 1F).tex(0D, 0D).endVertex();
        		worldRenderer.pos(1F, 1F, 1F).tex(0D, 1D).endVertex();
        		tess.draw();
	    		GlStateManager.popMatrix();*/
            }
        }

        if (stack.getItem() is ItemMatterManipulator) {
            val ray: RayTraceResult? = GSUtils.getRay(player.getEntityWorld(), player, false)
            if (ray != null && ray.hitVec.distanceTo(player.getPositionVector()) < 15.0f) {
                ItemMatterManipulator.drawLine(
                    player.getPosition().add(0.0, player.getEyeHeight().toDouble(), 0.0),
                    ray.getBlockPos(),
                    player.posX,
                    player.posY,
                    player.posZ
                )
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    open fun onPlayerTick(e: TickEvent.PlayerTickEvent) {
        if (e.phase == TickEvent.Phase.START && e.side == Side.CLIENT) {
            val chest: ItemStack = e.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST)

            if (chest != null && chest.getItem() is IJetpackArmor) if ((chest.getItem() as IJetpackArmor).canFly(
                    chest,
                    e.player
                )
            ) parseKeybindings(e.player)
        }
    }

    private fun parseKeybindings(player: EntityPlayer) {
        var power: Float = 1.0f
        val worldHeight1: Int = player.getEntityWorld().getHeight()
        val maxFlightHeight1: Int = (worldHeight1.toFloat() / 1.28f).toInt()
        var y: Double = player.posY

        if (y > (maxFlightHeight1 - 25).toDouble()) {
            if (y > maxFlightHeight1.toDouble()) {
                y = maxFlightHeight1.toDouble()
            }

            power = (power.toDouble() * ((maxFlightHeight1.toDouble() - y) / 25.0)).toFloat()
        }


        if (Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown()) {
            //player.motionY += urmot;
            player.motionY = min(player.motionY + (power * 0.15f).toDouble(), 0.6000000238418579)

            (GalaxySpace.packetPipeline)!!.sendToServer(
                GSPacketSimple(
                    GSEnumSimplePacket.S_CHANGE_FLIGHT_STATE,
                    player.world,
                    arrayOf<Any>(true)
                )
            )
        } else (GalaxySpace.packetPipeline)!!.sendToServer(
            GSPacketSimple(
                GSEnumSimplePacket.S_CHANGE_FLIGHT_STATE,
                player.world,
                arrayOf<Any>(false)
            )
        )
    }

    companion object {
        @JvmField
        var blocks: MutableMap<IBlockState?, String?> = HashMap<IBlockState?, String?>()

        @JvmField
        var ticks: Int = 0
    }
}
