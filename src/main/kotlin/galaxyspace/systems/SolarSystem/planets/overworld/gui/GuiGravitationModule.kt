package galaxyspace.systems.SolarSystem.planets.overworld.gui

import java.util.ArrayList
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementCheckbox.ICheckBoxCallback
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementTextBox.ITextBoxCallback
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket

import galaxyspace.GalaxySpace
import galaxyspace.core.GSItems
import galaxyspace.core.client.gui.tile.GuiTileBase
import galaxyspace.core.network.packet.GSPacketSimple
import galaxyspace.core.network.packet.GSPacketSimple.GSEnumSimplePacket
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.ContainerGravitationModule
import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityGravitationModule
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementCheckbox
import micdoodle8.mods.galacticraft.core.client.gui.element.GuiElementTextBox
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.gui.GuiButton
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import java.io.IOException

@SideOnly(Side.CLIENT)
open class GuiGravitationModule constructor(
    par1InventoryPlayer: InventoryPlayer?,
    private val tileEntity: TileEntityGravitationModule
) : GuiTileBase(ContainerGravitationModule((par1InventoryPlayer)!!, tileEntity), 2, 1), GuiElementTextBox.ITextBoxCallback,
    GuiElementCheckbox.ICheckBoxCallback {
    private var checkboxRenderEffects: GuiElementCheckbox? = null

    @JvmField
    protected var inputFieldList: MutableList<GuiElementTextBox> = ((ArrayList<Any?>() as MutableList<GuiElementTextBox>?))!!
    private var disableButton: GuiButton? = null
    private var radiusField: GuiElementTextBox? = null
    private var strengthField: GuiElementTextBox? = null

    @JvmField
    val BTN_ENABLE: Int = 6

    @JvmField
    val FIELD_RADIUS: Int = 1

    @JvmField
    val FIELD_STRENGTH: Int = 2

    private var radius: Int
    private var strength: Float


    init {
        this.ySize = 204
        moduleInfoX = this.inventorySlots.getSlotFromInventory(tileEntity, 1)!!.xPos
        moduleInfoY = this.inventorySlots.getSlotFromInventory(tileEntity, 1)!!.yPos
        radius = tileEntity.getGravityRadius()
        strength = tileEntity.getGravityStrength()
        moduleList = arrayOf<ItemStack>(
            ItemStack(GSItems.UPGRADES, 1, 0),
            ItemStack(GSItems.UPGRADES, 1, 1),
            ItemStack(GSItems.UPGRADES, 1, 3)
        )
    }

    public override fun initGui() {
        super.initGui()

        this.buttonList.clear()
        val var5: Int = (this.width - this.xSize) / 2
        val var6: Int = (this.height - this.ySize) / 2

        disableButton =
            GuiButton(BTN_ENABLE, var5 + 110, var6 + 78, 50, 20, GCCoreUtil.translate("gui.button.disable.name"))

        this.checkboxRenderEffects = GuiElementCheckbox(
            0,
            this,
            var5 + 10,
            var6 + 80,
            EnumColor.WHITE.toString() + GCCoreUtil.translate("gui.message.effect_visible.name")
        )
        this.buttonList.add(this.checkboxRenderEffects)
        this.buttonList.add(disableButton)

        this.radiusField = GuiElementTextBox(FIELD_RADIUS, this, var5 + 110, var6 + 60, 38, 18, "0", true, 2, true)
        this.strengthField = GuiElementTextBox(FIELD_STRENGTH, this, var5 + 110, var6 + 40, 38, 18, "0", false, 3, true)
        this.addInputField(this.radiusField!!)
        this.addInputField(this.strengthField!!)
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of
     * the items)
     */
    protected override fun drawGuiContainerForegroundLayer(par1: Int, par2: Int) {
        super.drawGuiContainerForegroundLayer(par1, par2)
        val color: TextFormatting = if (getStyle() == Style.MODERN) TextFormatting.WHITE else TextFormatting.DARK_GRAY

        this.checkboxRenderEffects!!.displayString =
            color.toString() + GCCoreUtil.translate("gui.message.effect_visible.name")
        this.fontRenderer.drawString(
            color.toString() + GCCoreUtil.translate("gui.gravity.strength.name"),
            11,
            45,
            4210752
        )
        this.fontRenderer.drawString(
            color.toString() + GCCoreUtil.translate("gui.gravity.radius.name"),
            11,
            65,
            4210752
        )

        var displayText: String = ""

        if (tileEntity.getDisabled(0)) {
            displayText = EnumColor.RED.toString() + GCCoreUtil.translate("gui.status.disabled.name")
            disableButton!!.displayString = GCCoreUtil.translate("gui.button.enable.name")
        } else {
            if (this.tileEntity.hasEnoughEnergyToRun) {
                displayText = EnumColor.BRIGHT_GREEN.toString() + GCCoreUtil.translate("gui.status.running.name")
            } else {
                displayText = EnumColor.ORANGE.toString() + GCCoreUtil.translate("gui.status.idle.name")
            }
            disableButton!!.displayString = GCCoreUtil.translate("gui.button.disable.name")
        }


        this.fontRenderer.drawString(
            color.toString() + GCCoreUtil.translate("gui.message.status.name") + ": " + displayText,
            100,
            104,
            4210752
        )

        for (i: Int in 0..3) if (this.tileEntity.getStackInSlot(i + 1).isItemEqual(ItemStack(GSItems.UPGRADES, 1, 1))) {
            this.fontRenderer.drawSplitString(
                EnumColor.BRIGHT_GREEN.toString() + GCCoreUtil.translate("gui.message.active_pressure_shield.name"),
                10,
                20,
                160,
                4210752
            )
            break
        }

        this.radiusField!!.text = tileEntity.getGravityRadius().toString() + ""

        // this.fontRenderer.drawString(EnumColor.BRIGHT_GREEN + "" + tileEntity.getGravityRadius() , 30, 5, 0xFFFFFF);
    }

    protected override fun drawGuiContainerBackgroundLayer(par1: Float, par2: Int, par3: Int) {
        super.drawGuiContainerBackgroundLayer(par1, par2, par3)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

        val containerWidth: Int = (this.width - this.xSize) / 2
        val containerHeight: Int = (this.height - this.ySize) / 2

        //Energy
        this.renderEnergyBar(
            containerWidth + 4,
            containerHeight + 102,
            this.tileEntity.getScaledElecticalLevel(55),
            this.tileEntity.getEnergyStoredGC(),
            this.tileEntity.getMaxEnergyStoredGC()
        )

        this.checkboxRenderEffects!!.isSelected = this.tileEntity.shouldRenderEffects
    }

    override fun onSelectionChanged(checkbox: GuiElementCheckbox?, newSelected: Boolean) {
        this.tileEntity.shouldRenderEffects = newSelected
        (GalaxySpace.packetPipeline)!!.sendToServer(
            GSPacketSimple(
                GSEnumSimplePacket.S_ON_ADVANCED_GUI_CLICKED_INT,
                GCCoreUtil.getDimensionID(this.mc.world),
                *arrayOf<Any>(
                    6,
                    this.tileEntity.getPos().getX(),
                    this.tileEntity.getPos().getY(),
                    this.tileEntity.getPos().getZ(),
                    if (newSelected) 1 else 0
                )
            )
        )
    }

    protected open fun sendDataToServer() {
        val pos: BlockVec3 = BlockVec3(tileEntity)
        var actualRadius: Int = radius
        var actualStrength: Float = strength

        if (actualRadius > 16) actualRadius = 16
        if (actualStrength > 2.0f) actualStrength = 2.0f
        if (actualStrength < -0.0f) actualStrength = 0.0f

        (GalaxySpace.packetPipeline)!!.sendToServer(
            GSPacketSimple(
                GSEnumSimplePacket.S_GRAVITY_DATA,
                GCCoreUtil.getDimensionID(this.mc.world),
                pos,
                actualRadius,
                actualStrength
            )
        )
        tileEntity.setGravityRadius(actualRadius)
        tileEntity.setGravityStrength(actualStrength)
    }

    override fun actionPerformed(btn: GuiButton) {
        when (btn.id) {
            BTN_ENABLE -> (GalacticraftCore.packetPipeline)!!.sendToServer(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.S_UPDATE_DISABLEABLE_BUTTON,
                    GCCoreUtil.getDimensionID(this.mc.world),
                    arrayOf<Any>(this.tileEntity.getPos(), 0)
                )
            )
        }
    }

    @Throws(IOException::class)
    override fun keyTyped(keyChar: Char, keyID: Int) {
        if (keyID != Keyboard.KEY_ESCAPE /* && keyID != this.mc.gameSettings.keyBindInventory.getKeyCode() */) {
            // do the fields
            for (box: GuiElementTextBox in inputFieldList) {
                if (box.keyTyped(keyChar, keyID)) {
                    return
                }
            }
        }
        super.keyTyped(keyChar, keyID)
    }

    override fun canPlayerEdit(checkbox: GuiElementCheckbox?, player: EntityPlayer?): Boolean {
        return true
    }

    override fun getInitiallySelected(checkbox: GuiElementCheckbox?): Boolean {
        return this.tileEntity.shouldRenderEffects
    }

    override fun onIntruderInteraction() {
    }

    protected open fun addInputField(box: GuiElementTextBox) {
        this.buttonList.add(box)
        this.inputFieldList.add(box)
    }

    override fun canPlayerEdit(textBox: GuiElementTextBox?, player: EntityPlayer?): Boolean {
        return (textBox)!!.id == FIELD_STRENGTH
    }

    override fun onTextChanged(textBox: GuiElementTextBox?, newText: String?) {
        if (newText == null) {
            // don't do anything
            return
        }
        var newValue: Double
        try {
            newValue = newText.toDouble()
            //newValue = Integer.parseInt(newText);
        } catch (wat: NumberFormatException) {
            // this is ridiculous
            return
        }
        if (newValue < 0) {
            return
        }

        when ((textBox)!!.id) {
            FIELD_STRENGTH -> {
                if (newValue > 2.0f) newValue = 2.0
                strength = newValue.toFloat()
            }

            FIELD_RADIUS -> radius = newValue.toInt()
            else -> return
        }
        this.sendDataToServer()
    }

    override fun getInitialText(textBox: GuiElementTextBox?): String? {
        when ((textBox)!!.id) {
            FIELD_RADIUS -> return radius.toString()
            FIELD_STRENGTH -> return strength.toString()
        }

        return (textBox)!!.id.toString()
    }

    override fun getTextColor(textBox: GuiElementTextBox?): Int {
        return ColorUtil.to32BitColor(255, 20, 255, 20)
    }

    override fun onIntruderInteraction(textBox: GuiElementTextBox?) {
    }

    override fun isModuleSupport(): Boolean {
        return true
    }

    override fun getName(): String {
        return tileEntity.getName()
    }

    override fun getBatterySlot(): Slot? {
        return inventorySlots.getSlotFromInventory(tileEntity, 0)
    }
}
