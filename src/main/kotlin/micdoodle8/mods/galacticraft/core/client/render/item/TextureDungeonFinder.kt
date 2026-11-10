package micdoodle8.mods.galacticraft.core.client.render.item

import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStatsClient
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.texture.TextureUtil
import net.minecraft.item.ItemStack
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.lang.reflect.Field

@SideOnly(Side.CLIENT)
open class TextureDungeonFinder constructor(iconName: String) : TextureAtlasSprite(iconName) {
    /** Current compass heading in radians  */
    @JvmField
    var currentAngle: Double = 0.0

    /** Speed and direction of compass rotation  */
    @JvmField
    var angleDelta: Double = 0.0

    override fun updateAnimation() {
        val minecraft: Minecraft = Minecraft.getMinecraft()

        if (minecraft.world != null && minecraft.player != null) {
            this.updateCompass(minecraft.world, minecraft.player, minecraft.player.rotationYaw.toDouble(), false, false)
        } else {
            this.updateCompass(null as World?, null, 0.0, true, false)
        }
    }

    /**
     * Updates the compass based on the given x,z coords and camera direction
     */
    open fun updateCompass(worldIn: World?, player: EntityPlayerSP?, angle: Double, flag1: Boolean, flag2: Boolean) {
        var angle: Double = angle
        if (!this.framesTextureData.isEmpty()) {
            var d0: Double = 0.0

            if (worldIn != null && player != null && !flag1) {
                if (worldIn.provider is IGalacticraftWorldProvider && player.inventory.hasItemStack(ItemStack(GCItems.dungeonFinder))) {
                    val direction: Double = (GCPlayerStatsClient.get(player))!!.getDungeonDirection().toDouble()
                    angle = (angle - direction) % 360.0
                    d0 = -angle / Constants.RADIANS_TO_DEGREES_D
                } else {
                    d0 = Math.random() * Math.PI * 2.0
                }
            }

            if (flag2) {
                this.currentAngle = d0
            } else {
                var d3: Double

                d3 = d0 - this.currentAngle
                while (d3 < -Math.PI) {
                    d3 += (Math.PI * 2.0)
                }

                while (d3 >= Math.PI) {
                    d3 -= (Math.PI * 2.0)
                }

                d3 = MathHelper.clamp(d3, -1.0, 1.0)
                this.angleDelta += d3 * 0.1
                this.angleDelta *= 0.8
                this.currentAngle += this.angleDelta
            }

            var i: Int

            i =
                ((this.currentAngle / (Math.PI * 2.0) + 1.0) * this.framesTextureData.size.toDouble()).toInt() % this.framesTextureData.size
            while (i < 0) {
                i = (i + this.framesTextureData.size) % this.framesTextureData.size
            }

            if (i != this.frameCounter) {
                this.frameCounter = i
                TextureUtil.uploadTextureMipmap(
                    this.framesTextureData.get(this.frameCounter) as Array<IntArray?>?,
                    this.width,
                    this.height,
                    this.originX,
                    this.originY,
                    false,
                    false
                )
            }
        }
    }

    open fun register(map: TextureMap) {
        try {
            val f: Field =
                map.javaClass.getDeclaredField(if (GCCoreUtil.isDeobfuscated()) "mapRegisteredSprites" else "field_110574_e")
            f.setAccessible(true)
            val textureMap: MutableMap<String?, TextureAtlasSprite?> =
                f.get(map) as MutableMap<String?, TextureAtlasSprite?>
            textureMap.put(this.getIconName(), this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
