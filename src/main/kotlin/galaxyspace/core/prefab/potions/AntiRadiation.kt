package galaxyspace.core.prefab.potions

import galaxyspace.GalaxySpace
import galaxyspace.core.prefab.potions.AntiRadiation
import galaxyspace.core.GSPotions
import asmodeuscore.core.utils.ACAttributePlayer
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.ai.attributes.IAttributeInstance
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionEffect
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class AntiRadiation constructor(isBad: Boolean, color: Int) : Potion(isBad, color) {
    private val iconTexture: ResourceLocation

    init {
        setPotionName(this, "antiradiation")
        iconTexture = ResourceLocation(GalaxySpace.ASSET_PREFIX, "textures/gui/potions/radiation.png")
    }

    override fun hasStatusIcon(): Boolean {
        return false
    }

    @SideOnly(Side.CLIENT)
    override fun renderInventoryEffect(x: Int, y: Int, effect: PotionEffect, mc: Minecraft) {
        super.renderInventoryEffect(x, y, effect, mc)
        if (mc.currentScreen != null) {
            mc.getTextureManager().bindTexture(iconTexture)
            Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0f, 0f, 18, 18, 18f, 18f)
        }
    }

    @SideOnly(Side.CLIENT)
    override fun renderHUDEffect(x: Int, y: Int, effect: PotionEffect, mc: Minecraft, alpha: Float) {
        super.renderHUDEffect(x, y, effect, mc, alpha)
        mc.getTextureManager().bindTexture(iconTexture)
        Gui.drawModalRectWithCustomSizedTexture(x + 4, y + 4, 0f, 0f, 18, 18, 18f, 18f)
    }

    override fun isReady(duration: Int, amplifier: Int): Boolean {
        if (this === GSPotions.antiradiation) {
            val k: Int = 20 shr amplifier
            return if (k > 0) duration % k == 0 else true
        }
        return false
    }

    override fun performEffect(living: EntityLivingBase, food: Int) {
        if (this === GSPotions.antiradiation) {
            if (living is EntityPlayer) {
                val player: EntityPlayer = living
                val lvl: IAttributeInstance = player.getEntityAttribute(ACAttributePlayer.RADIATION_LVL)
                if (lvl.getAttributeValue() > 0) lvl.setBaseValue(lvl.getAttributeValue() - 1)
                else if (lvl.getAttributeValue() <= 0) player.removePotionEffect(this)
            }
        }
    }

    companion object {
        @JvmStatic
        fun setPotionName(potion: Potion, potionName: String?) {
            potion.setRegistryName(GalaxySpace.MODID, potionName)
            potion.setPotionName("effect." + potionName)
        }
    }
}
