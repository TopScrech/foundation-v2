package micdoodle8.mods.galacticraft.planets.mars.client.gui

import micdoodle8.mods.galacticraft.planets.mars.network.PacketSimpleMars.EnumSimplePacketMars

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.mars.entities.EntitySlimeling
import micdoodle8.mods.galacticraft.planets.mars.network.PacketSimpleMars
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.screens.Screen as GuiScreen
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.fml.client.FMLClientHandler
import java.io.IOException
import kotlin.math.floor

open class GuiSlimelingFeed constructor(private val slimeling: EntitySlimeling) : GuiScreen() {
    private val xSize = 138
    private val ySize = 51

    //    public static RenderItem drawItems = new RenderItem();
    @JvmField
    var timeBackspacePressed: Long = 0

    @JvmField
    var cursorPulse: Int = 0

    @JvmField
    var backspacePressed: Int = 0

    @JvmField
    var isTextFocused: Boolean = false

    @JvmField
    var buttonGrowSlimeling: GuiButton? = null

    @JvmField
    var buttonBreedSlimeling: GuiButton? = null

    @JvmField
    var buttonStrengthenSlimeling: GuiButton? = null

    @JvmField
    var buttonHealSlimeling: GuiButton? = null

    override fun initGui() {
        super.initGui()
        this.buttonList.clear()
        val var6: Int = (this.height - this.ySize) / 2
        this.buttonGrowSlimeling =
            GuiButton(0, this.width / 2 - 65, var6 - 15, 64, 20, GCCoreUtil.translate("gui.message.grow.name"))
        this.buttonList.add(this.buttonGrowSlimeling)
        this.buttonBreedSlimeling =
            GuiButton(1, this.width / 2 + 1, var6 - 15, 64, 20, GCCoreUtil.translate("gui.message.breed.name"))
        this.buttonList.add(this.buttonBreedSlimeling)
        this.buttonStrengthenSlimeling =
            GuiButton(2, this.width / 2 - 65, var6 + 7, 64, 20, GCCoreUtil.translate("gui.message.strengthen.name"))
        this.buttonList.add(this.buttonStrengthenSlimeling)
        this.buttonHealSlimeling =
            GuiButton(3, this.width / 2 + 1, var6 + 7, 64, 20, GCCoreUtil.translate("gui.message.heal.name"))
        this.buttonList.add(this.buttonHealSlimeling)
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }

    @Throws(IOException::class)
    override fun keyTyped(keyChar: Char, keyID: Int) {
        if (!this.buttonGrowSlimeling!!.enabled && !this.buttonBreedSlimeling!!.enabled && !this.buttonStrengthenSlimeling!!.enabled && !this.buttonHealSlimeling!!.enabled) {
            super.keyTyped(keyChar, keyID)
        }
        return
    }

    override fun actionPerformed(par1GuiButton: GuiButton) {
        if (par1GuiButton.enabled) {
            when (par1GuiButton.id) {
                0 -> (GalacticraftCore.packetPipeline)!!.sendToServer(
                    PacketSimpleMars(
                        PacketSimpleMars.EnumSimplePacketMars.S_UPDATE_SLIMELING_DATA,
                        GCCoreUtil.getDimensionID(mc.world),
                        arrayOf<Any>(this.slimeling.getEntityId(), 2, "")
                    )
                )

                1 -> {
                    if (!this.slimeling.isInLove() && this.slimeling.isOwner(this.mc.player) && this.slimeling.world.isRemote) {
                        this.slimeling.setInLove(this.mc.player)
                    }

                    (GalacticraftCore.packetPipeline)!!.sendToServer(
                        PacketSimpleMars(
                            PacketSimpleMars.EnumSimplePacketMars.S_UPDATE_SLIMELING_DATA,
                            GCCoreUtil.getDimensionID(mc.world),
                            arrayOf<Any>(this.slimeling.getEntityId(), 3, "")
                        )
                    )
                }

                2 -> (GalacticraftCore.packetPipeline)!!.sendToServer(
                    PacketSimpleMars(
                        PacketSimpleMars.EnumSimplePacketMars.S_UPDATE_SLIMELING_DATA,
                        GCCoreUtil.getDimensionID(mc.world),
                        arrayOf<Any>(this.slimeling.getEntityId(), 4, "")
                    )
                )

                3 -> (GalacticraftCore.packetPipeline)!!.sendToServer(
                    PacketSimpleMars(
                        PacketSimpleMars.EnumSimplePacketMars.S_UPDATE_SLIMELING_DATA,
                        GCCoreUtil.getDimensionID(mc.world),
                        arrayOf<Any>(this.slimeling.getEntityId(), 5, "")
                    )
                )
            }

            FMLClientHandler.instance().getClient().displayGuiScreen(null)
        }
    }

    override fun drawScreen(par1: Int, par2: Int, par3: Float) {
        this.mc.renderEngine.bindTexture(slimelingPanelGui)
        val var5: Int = (this.width - this.xSize) / 2
        val var6: Int = (this.height - this.ySize) / 2
        this.drawTexturedModalRect(var5, var6 - 20, 0, 0, this.xSize, this.ySize)

        super.drawScreen(par1, par2, par3)

        this.buttonHealSlimeling!!.enabled =
            this.slimeling.getHealth() < floor(this.slimeling.getMaxHealth().toDouble())
        this.buttonGrowSlimeling!!.enabled = this.slimeling.getScale() < 1.0f
        this.buttonStrengthenSlimeling!!.enabled = this.slimeling.getAttackDamage() < 1.0
        this.buttonBreedSlimeling!!.enabled = !this.slimeling.isInLove() && !this.slimeling.isChild()
    }

    companion object {
        private val slimelingPanelGui =
            ResourceLocation.fromNamespaceAndPath(GalacticraftPlanets.ASSET_PREFIX, "textures/gui/slimeling_panel1.png")
    }
}
