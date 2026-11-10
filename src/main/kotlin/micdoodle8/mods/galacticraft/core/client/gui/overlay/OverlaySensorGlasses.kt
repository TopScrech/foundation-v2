package micdoodle8.mods.galacticraft.core.client.gui.overlay

import micdoodle8.mods.galacticraft.api.item.ISensorGlassesArmor
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStatsClient
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.MathHelper
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

@SideOnly(Side.CLIENT)
object OverlaySensorGlasses : Overlay() {
    private val hudTexture = ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/hud.png")
    private val indicatorTexture = ResourceLocation(Constants.ASSET_PREFIX, "textures/gui/indicator.png")

    @JvmField
    val altTexture: ResourceLocation = ResourceLocation(Constants.ASSET_PREFIX, "textures/blocks/sensor_mobs.png")

    private val minecraft: Minecraft = FMLClientHandler.instance().getClient()

    private var zoom = 0

    /**
     * Render the GUI that displays sensor glasses
     */
    @JvmStatic
    fun renderSensorGlassesMain(
        stack: ItemStack?,
        player: EntityPlayer?,
        resolution: ScaledResolution?,
        partialTicks: Float
    ) {
        zoom++

        val f: Float = MathHelper.sin(zoom / 80.0f) * 0.1f + 0.1f

        val scaledresolution: ScaledResolution =
            ClientUtil.getScaledRes(minecraft, minecraft.displayWidth, minecraft.displayHeight)
        val i: Int = scaledresolution.getScaledWidth()
        val k: Int = scaledresolution.getScaledHeight()
        minecraft.entityRenderer.setupOverlayRendering()
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glDepthMask(false)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        GL11.glDisable(GL11.GL_ALPHA_TEST)
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(hudTexture)
        val tessellator: Tessellator = Tessellator.getInstance()
        val worldRenderer: BufferBuilder = tessellator.getBuffer()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos((i / 2 - k - f * 80).toDouble(), (k + f * 40).toDouble(), -90.0).tex(0.0, 1.0).endVertex()
        worldRenderer.pos((i / 2 + k + f * 80).toDouble(), (k + f * 40).toDouble(), -90.0).tex(1.0, 1.0).endVertex()
        worldRenderer.pos((i / 2 + k + f * 80).toDouble(), 0.0 - f * 40, -90.0).tex(1.0, 0.0).endVertex()
        worldRenderer.pos((i / 2 - k - f * 80).toDouble(), 0.0 - f * 40, -90.0).tex(0.0, 0.0).endVertex()
        tessellator.draw()

        GL11.glDepthMask(true)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glEnable(GL11.GL_ALPHA_TEST)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
    }

    @JvmStatic
    fun renderSensorGlassesValueableBlocks(
        stack: ItemStack?,
        player: EntityPlayer?,
        resolution: ScaledResolution?,
        partialTicks: Float
    ) {
        val var51: MutableIterator<BlockVec3> = (ClientProxyCore.valueableBlocks.iterator() as MutableIterator<BlockVec3>)
        var var52: Double
        var var58: Double
        var var59: Double
        var var20: Double
        var var21: Double
        var var60: Float

        while (var51.hasNext()) {
            val coords: BlockVec3 = var51.next()

            var52 = ClientProxyCore.playerPosX - coords.x - 0.5
            var58 = ClientProxyCore.playerPosY - coords.y - 0.5
            var59 = ClientProxyCore.playerPosZ - coords.z - 0.5
            var60 = Math.toDegrees(atan2(var52, var59)).toFloat()
            var20 = sqrt(var52 * var52 + var58 * var58 + var59 * var59) * 0.5
            var21 = sqrt(var52 * var52 + var59 * var59) * 0.5

            val var5: ScaledResolution =
                ClientUtil.getScaledRes(minecraft, minecraft.displayWidth, minecraft.displayHeight)
            val var6: Int = var5.getScaledWidth()
            val var7: Int = var5.getScaledHeight()

            var var2: Boolean = false

            val client: EntityPlayerSP? = PlayerUtil.getPlayerBaseClientFromPlayer(minecraft.player, false)

            if (client != null) {
                val stats: GCPlayerStatsClient = (GCPlayerStatsClient.get(client))!!
                var2 = stats.isUsingAdvancedGoggles()
            }

            minecraft.fontRenderer.drawString(
                GCCoreUtil.translate("gui.sensor.advanced") + ": " + (if (var2) GCCoreUtil.translate(
                    "gui.sensor.advancedon"
                ) else GCCoreUtil.translate("gui.sensor.advancedoff")), var6 / 2 - 50, 4, 0x03b88f
            )

            try {
                GL11.glPushMatrix()

                if (var20 < 4.0) {
                    GL11.glColor4f(0.0f, 255f / 255f, 198f / 255f, min(1.0, max(0.2, (var20 - 1.0) * 0.1)).toFloat())
                    FMLClientHandler.instance().getClient().renderEngine.bindTexture(indicatorTexture)
                    GL11.glRotatef(-var60 - ClientProxyCore.playerRotationYaw + 180.0f, 0.0f, 0.0f, 1.0f)
                    GL11.glTranslated(0.0, if (var2) -var20 * 16 else -var21 * 16, 0.0)
                    GL11.glRotatef(-(-var60 - ClientProxyCore.playerRotationYaw + 180.0f), 0.0f, 0.0f, 1.0f)
                    Overlay.Companion.drawCenteringRectangle(
                        (var6 / 2).toDouble(),
                        (var7 / 2).toDouble(),
                        1.0,
                        8.0,
                        8.0
                    )
                }
            } finally {
                GL11.glPopMatrix()
            }
        }
    }

    @JvmStatic
    fun preRenderMobs() {
        GL11.glEnable(GL11.GL_BLEND)
        //TODO  Enable these to see the entity through solid blocks - but would need a postRenderCallback to switch this off again otherwise everything is changed
//            GL11.glDisable(GL11.GL_DEPTH_TEST);
//            GL11.glDepthMask(false);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        GL11.glDisable(GL11.GL_ALPHA_TEST)
        val i: Int = 15728880
        val j: Int = i % 65536
        val k: Int = i / 65536
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j.toFloat() / 1.0f, k.toFloat() / 1.0f)
        GL11.glTranslatef(0.0f, 0.045f, 0.0f)
        GL11.glScalef(1.07f, 1.035f, 1.07f)
    }

    @JvmStatic
    fun overrideMobTexture(): Boolean {
        val player: EntityPlayer? = Minecraft.getMinecraft().player
        return (player != null && player.inventory.armorItemInSlot(3) != null && player.inventory.armorItemInSlot(3)
            .getItem() is ISensorGlassesArmor)
    }
}
