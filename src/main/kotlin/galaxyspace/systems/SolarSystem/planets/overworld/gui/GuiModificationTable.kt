package galaxyspace.systems.SolarSystem.planets.overworld.gui

import java.util.ArrayList
import java.util.Comparator
import java.util.List

import galaxyspace.GalaxySpace
import galaxyspace.api.item.IModificationItem
import galaxyspace.core.network.packet.GSPacketSimple
import galaxyspace.core.network.packet.GSPacketSimple.GSEnumSimplePacket
import galaxyspace.core.prefab.items.modules.ItemModule
import galaxyspace.core.util.GSUtils
import galaxyspace.core.util.GSUtils.Module_Type
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.ContainerModificationTable
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityModificationTable
import micdoodle8.mods.galacticraft.api.item.IHoldableItem
import micdoodle8.mods.galacticraft.api.recipe.SpaceStationRecipe
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.core.client.gui.container.GuiContainerGC
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.item.ItemArmor
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.FMLClientHandler
import org.lwjgl.opengl.GL11
import java.io.IOException

open class GuiModificationTable constructor(
    par1InventoryPlayer: InventoryPlayer?,
    private val tileEntity: TileEntityModificationTable
) : GuiContainerGC(ContainerModificationTable((par1InventoryPlayer)!!, tileEntity)) {
    private var countdown = 0

    //private List<SpaceSuit_Modules> actual_list = new ArrayList<SpaceSuit_Modules>();
    private val actual_list: MutableList<ItemModule?> = ArrayList<ItemModule?>()

    private val xOffsetModule = 10

    init {
        this.xSize = 250
        this.ySize = 204
    }

    override fun initGui() {
        super.initGui()
    }

    override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        this.fontRenderer.drawString(
            EnumColor.WHITE.toString() + this.tileEntity.getName(),
            86 - (this.fontRenderer.getStringWidth(this.tileEntity.getName()) / 2),
            1,
            4210752
        )
        this.fontRenderer.drawString(
            EnumColor.WHITE.toString() + GCCoreUtil.translate("container.inventory"),
            15,
            this.ySize - 88,
            4210752
        )


        //int i = modules_number = 0;
        this.actual_list.clear()

        if (!this.inventorySlots.getSlot(0).getStack().isEmpty()) {
            val stack: ItemStack = this.inventorySlots.getSlot(0).getStack()


            if (stack.getItem() is IModificationItem && (stack.getItem() as IModificationItem).getAvailableModules() != null) {
                for (module in ((stack.getItem() as IModificationItem).getAvailableModules())!!) {
                    if (stack.getItem() is ItemArmor) {
                        val item: ItemArmor = stack.getItem() as ItemArmor
                        if ((module)!!.getType() != Module_Type.ALL && (module)!!.getType() != (stack.getItem() as IModificationItem).getType(
                                stack
                            )
                        ) continue
                        if (!(module)!!.isLoading()) continue

                        if ((module)!!.getEquipmentSlot() == item.armorType || (module)!!.getEquipmentSlot() == null || (module)!!.getType() == Module_Type.ALL) actual_list.add(
                            module
                        )
                    } else {
                        if ((module)!!.getType() == null) continue

                        if ((module)!!.getType() == Module_Type.ALL && stack.getItem() !is IHoldableItem) actual_list.add(
                            module
                        )
                        else {
                            if ((module)!!.getType() != Module_Type.ALL && (module)!!.getType() != (stack.getItem() as IModificationItem).getType(
                                    stack
                                )
                            ) continue

                            actual_list.add(module)
                        }
                    }
                }

                actual_list.sortWith(object : Comparator<ItemModule?> {
                    override fun compare(arg0: ItemModule?, arg1: ItemModule?): Int {
                        return (arg0!!.getName())!!.compareTo((arg1!!.getName())!!)
                    }
                })
            }


            //this.modules_number = actual_list.size();
        }
    }

    override fun drawGuiContainerBackgroundLayer(par1: Float, par2: Int, par3: Int) {
        this.mc.renderEngine.bindTexture(guiTexture)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

        val containerWidth: Int = (this.width - this.xSize) / 2
        val containerHeight: Int = (this.height - this.ySize) / 2
        this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize - 74, this.ySize) // Base Gui


        // Slots
        for (i: Int in this.inventorySlots.inventorySlots.indices) {
            val x: Int = this.inventorySlots.getSlot(i).xPos
            val y: Int = this.inventorySlots.getSlot(i).yPos


            /* if(!(this.inventorySlots.getSlot(i).inventory instanceof InventoryPlayer))
	        {*/
            GL11.glPushMatrix()
            when (i) {
                else -> {
                    this.drawTexturedModalRect(containerWidth + x - 2, containerHeight + y - 2, 192, 26, 20, 21)
                }
            }
            GL11.glPopMatrix()
            //}
        }

        ///////////////////////
        if (!this.inventorySlots.getSlot(0).getStack().isEmpty()) {
            val stack: ItemStack = this.inventorySlots.getSlot(0).getStack()

            /*
        	for(int i = 0; i < actual_list.size(); i++)
 	        {
 	        	//texture button
 		        if(stack.hasTagCompound() && (!stack.getTagCompound().hasKey(this.actual_list.get(i).getName()) || !stack.getTagCompound().getBoolean(this.actual_list.get(i).getName())))
 		        	this.drawTexturedModalRect(containerWidth + 30 + xOffsetModule, containerHeight + 20 + (22 * i), 192, 196, 20, 20);
 		        else
 		        	this.drawTexturedModalRect(containerWidth + 30 + xOffsetModule, containerHeight + 20 + (22 * i), 213, 196, 20, 20);
 	        }
        	 */
            var k: Int = 0
            for (i: Int in actual_list.indices) {
                //ItemStack[] stacks = this.actual_list.get(i).getItemsForModule();//GSUtils.getItemsForModules(this.actual_list.get(i).getName());
                val stacks: ItemStack? = actual_list.get(i)!!.getIcon()

                if (stacks == null) break
                val yPos: Int = if (i > 4 * (i % 4)) i - 4 else i

                this.mc.renderEngine.bindTexture(guiTexture)
                //GL11.glEnable(GL11.GL_ALPHA_TEST);
                //GL11.glEnable(GL11.GL_BLEND);
                if (stack.hasTagCompound() && (!stack.getTagCompound()!!
                        .hasKey(this.actual_list.get(i)!!.getName()) || !stack.getTagCompound()!!
                        .getBoolean(this.actual_list.get(i)!!.getName()))
                ) this.drawTexturedModalRect(
                    containerWidth + 30 + xOffsetModule + (22 * k),
                    containerHeight + 20 + (22 * yPos),
                    192,
                    196,
                    20,
                    20
                )
                else this.drawTexturedModalRect(
                    containerWidth + 30 + xOffsetModule + (22 * k),
                    containerHeight + 20 + (22 * yPos),
                    213,
                    196,
                    20,
                    20
                )

                //GL11.glDisable(GL11.GL_ALPHA_TEST);
                // GL11.glDisable(GL11.GL_BLEND);
                RenderHelper.enableGUIStandardItemLighting()
                this.itemRender.renderItemAndEffectIntoGUI(
                    actual_list.get(i)!!.getIcon(),
                    containerWidth + 32 + xOffsetModule + (22 * k),
                    containerHeight + 22 + (22 * yPos)
                )
                RenderHelper.disableStandardItemLighting()

                if (i >= 3 && i % 3 == 0) {
                    k++
                }
            }

            val mod_count: Int = if (stack.hasTagCompound() && stack.getTagCompound()!!
                    .hasKey("modification_count")
            ) stack.getTagCompound()!!.getInteger("modification_count") else 1
            this.fontRenderer.drawString(
                "Mod. count: " + mod_count,
                containerWidth + 58,
                containerHeight + 105,
                0xFFFFFF
            )
        } else {
            this.fontRenderer.drawSplitString(
                GCCoreUtil.translate("gui.module.empty_slot"),
                containerWidth + 58,
                containerHeight + 60,
                80,
                0xFFFFFF
            )
        }

        if (GalaxySpace.debug) GSUtils.renderDebugGui(this, containerWidth, containerHeight)
    }

    override fun drawScreen(mousePosX: Int, mousePosY: Int, partialTicks: Float) {
        super.drawScreen(mousePosX, mousePosY, partialTicks)
        if (countdown > 0) countdown--

        if (!this.inventorySlots.getSlot(0).getStack().isEmpty()) {
            val containerWidth: Int = (this.width - this.xSize) / 2
            val containerHeight: Int = (this.height - this.ySize) / 2
            val stack: ItemStack = this.inventorySlots.getSlot(0).getStack()

            var k: Int = 0
            for (i: Int in actual_list.indices) {
                val yPos: Int = if (i > 4 * (i % 4)) i - 4 else i

                if (mousePosX >= containerWidth + xOffsetModule + 30 + (22 * k) && mousePosX <= containerWidth + xOffsetModule + 50 + (22 * k) && mousePosY >= containerHeight + 20 + (22 * yPos) && mousePosY <= containerHeight + 40 + (22 * yPos)) {
                    GlStateManager.disableLighting()
                    this.mc.renderEngine.bindTexture(guiTexture)

                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
                    //Left side
                    this.drawTexturedModalRect(
                        containerWidth - 88,
                        containerHeight + 12,
                        0,
                        12,
                        (xSize - 74) / 2,
                        ySize / 2 + 1
                    )
                    //Right side
                    this.drawTexturedModalRect(
                        containerWidth + 173,
                        containerHeight + 12,
                        85,
                        12,
                        (xSize - 74) / 2,
                        ySize / 2 + 1
                    )

                    this.fontRenderer.drawSplitString(
                        GCCoreUtil.translate("gui.message.required_equipment.name"),
                        containerWidth - 65,
                        containerHeight + 15,
                        80,
                        0xFFFF
                    )

                    val encolor: EnumColor = if (stack.hasTagCompound() && stack.getTagCompound()!!
                            .getBoolean(this.actual_list.get(i)!!.getName())
                    ) EnumColor.BRIGHT_GREEN else EnumColor.RED

                    val name: String = GCCoreUtil.translate("gui.module." + actual_list.get(i)!!.getName())
                    this.fontRenderer.drawString(
                        encolor.toString() + name,
                        containerWidth + 205 - name.length,
                        containerHeight + 20,
                        0xFFFFFF
                    )

                    val mode: String = if (actual_list.get(i)!!
                            .isActiveModule()
                    ) EnumColor.PURPLE.toString() + GCCoreUtil.translate("gui.module.active") else EnumColor.GREY.toString() + GCCoreUtil.translate(
                        "gui.module.passive"
                    )
                    this.fontRenderer.drawString(
                        mode,
                        containerWidth + 205 - mode.length,
                        containerHeight + 28,
                        0xFFFFFF
                    )

                    val s: String = GCCoreUtil.translate("gui.module." + actual_list.get(i)!!.getName() + ".desc")
                    this.fontRenderer.drawSplitString(s, containerWidth + 180, containerHeight + 40, 80, 0xFFFFFF)


                    //GlStateManager.enableLighting();
                    val stacks: Array<ItemStack>? = (actual_list.get(i)!!
                        .getItemsForModule() as Array<ItemStack>?) //GSUtils.getItemsForModules(this.actual_list.get(i).getName());

                    if (stacks != null) for (stacklist: ItemStack in stacks) {
                        this.mc.renderEngine.bindTexture(guiTexture)
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
                        val xOffset: Int = -3
                        //this.drawTexturedModalRect(containerWidth - 55, containerHeight + 35, 192, 196, 20, 20);
                        this.drawTexturedModalRect(containerWidth - 55, containerHeight + 35, 192, 26, 20, 21)

                        val amount: Int = this.getAmountInInventory(stacklist)

                        RenderHelper.enableGUIStandardItemLighting()
                        this.itemRender.renderItemAndEffectIntoGUI(
                            stacklist,
                            containerWidth - 50 + xOffset,
                            containerHeight + 37
                        )
                        RenderHelper.disableStandardItemLighting()

                        val valid: Boolean = amount >= stacklist.getCount()
                        val color: Int =
                            if (valid or this.mc.player.capabilities.isCreativeMode) ColorUtil.to32BitColor(
                                255,
                                0,
                                255,
                                0
                            ) else ColorUtil.to32BitColor(255, 255, 0, 0)
                        val str: String = "" + stacklist.getCount()
                        this.fontRenderer.drawString(str, containerWidth - 35 + xOffset, containerHeight + 47, color)

                        this.drawToolTip(
                            containerWidth - 40 + xOffset,
                            containerHeight + 73,
                            stacklist.getDisplayName()
                        )

                        k++
                    }


                    //this.drawToolTip(mousePosX, mousePosY + 70, GCCoreUtil.translateWithFormat("item_desc.shift.name", GameSettings.getKeyDisplayString(FMLClientHandler.instance().getClient().gameSettings.keyBindSneak.getKeyCode()))); 
                    if (this.actual_list.get(i)!!.getForrbidenModules() != null) {
                        this.fontRenderer.drawSplitString(
                            GCCoreUtil.translate("gui.message.forrbiden_modules.name"),
                            containerWidth - 75,
                            containerHeight + 75,
                            80,
                            0xFF0000
                        )

                        for (forbidden in (this.actual_list.get(i)!!.getForrbidenModules())!!) {
                            this.mc.renderEngine.bindTexture(guiTexture)
                            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
                            this.drawTexturedModalRect(containerWidth - 55, containerHeight + 88, 192, 196, 20, 20)

                            RenderHelper.enableGUIStandardItemLighting()
                            this.itemRender.renderItemAndEffectIntoGUI(
                                (forbidden)!!.getIcon(),
                                containerWidth - 53,
                                containerHeight + 90
                            )
                            RenderHelper.disableStandardItemLighting()
                        }
                    }

                    GlStateManager.enableLighting()
                }

                if (i >= 3 && i % 3 == 0) {
                    k++
                }
            }
        }
    }

    protected open fun getAmountInInventory(stack: ItemStack): Int {
        var amountInInv: Int = 0

        for (x: Int in 0..<FMLClientHandler.instance().getClientPlayerEntity().inventory.getSizeInventory()) {
            val slot: ItemStack = FMLClientHandler.instance().getClientPlayerEntity().inventory.getStackInSlot(x)

            if (slot != null) {
                if (SpaceStationRecipe.checkItemEquals(stack, slot)) {
                    amountInInv += slot.getCount()
                }
            }
        }

        return amountInInv
    }

    private fun drawToolTip(
        mousePosX: Int,
        mousePosY: Int,
        tooltip: String,
        width: Int = this.fontRenderer.getStringWidth(tooltip),
        height: Int = 8
    ) {
        GL11.glDepthMask(true)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glPushMatrix()
        GL11.glTranslatef(0f, 0f, 300f)
        val k: Int = width
        var j2: Int = mousePosX - k / 2
        var k2: Int = mousePosY - 12
        val i1: Int = height

        if (j2 + k > this.width) {
            j2 -= (j2 - this.width + k)
        }

        if (k2 + i1 + 6 > this.height) {
            k2 = this.height - i1 - 6
        }

        val j1: Int = ColorUtil.to32BitColor(190, 0, 153, 255)
        this.drawGradientRect(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1)
        this.drawGradientRect(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1)
        this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1)
        this.drawGradientRect(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1)
        this.drawGradientRect(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1)
        val k1: Int = ColorUtil.to32BitColor(170, 0, 153, 255)
        val l1: Int = (k1 and 16711422) shr 1 or (k1 and -16777216)
        this.drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1)
        this.drawGradientRect(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1)
        this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1)
        this.drawGradientRect(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1)

        this.fontRenderer.drawSplitString(tooltip, j2, k2, 150, ColorUtil.to32BitColor(255, 255, 255, 255))

        GL11.glPopMatrix()
        GL11.glDepthMask(false)
    }

    @Throws(IOException::class)
    override fun mouseClicked(mouseX: Int, mouseY: Int, button: Int) {
        super.mouseClicked(mouseX, mouseY, button)

        if (this.inventorySlots.getSlot(0).getStack() != null) {
            val containerWidth: Int = (this.width - this.xSize) / 2
            val containerHeight: Int = (this.height - this.ySize) / 2

            var k: Int = 0
            for (i: Int in actual_list.indices) {
                val yPos: Int = if (i > 4 * (i % 4)) i - 4 else i

                if (mouseX >= containerWidth + xOffsetModule + 30 + (22 * k) && mouseX <= containerWidth + xOffsetModule + 50 + (22 * k) && mouseY >= containerHeight + 20 + (22 * yPos) && mouseY <= containerHeight + 40 + (22 * yPos) && countdown <= 0) {
                    (GalaxySpace.packetPipeline)!!.sendToServer(
                        GSPacketSimple(
                            GSEnumSimplePacket.S_UPDATE_NBT_ITEM_ON_GUI,
                            GCCoreUtil.getDimensionID(this.mc.world),
                            *arrayOf<Any?>(
                                BlockVec3(
                                    this.tileEntity.getPos().getX(),
                                    this.tileEntity.getPos().getY(),
                                    this.tileEntity.getPos().getZ()
                                ), this.actual_list.get(i)!!.getName()
                            )
                        )
                    )
                    countdown = 20 * 3
                }

                if (i >= 3 && i % 3 == 0) {
                    k++
                }
            }
        }
    }

    companion object {
        private val guiTexture = ResourceLocation(GalaxySpace.ASSET_PREFIX, "textures/gui/modification_table.png")
    }
}
