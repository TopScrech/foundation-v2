package asmodeuscore.core.prefab.potions

import asmodeuscore.AsmodeusCore
import asmodeuscore.core.utils.ACDamageSource
import asmodeuscore.core.registers.potions.ACPotions
import asmodeuscore.core.prefab.potions.Hypothermia
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionEffect
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.ArrayList

open class Hypothermia constructor(isBad: Boolean, color: Int) : Potion(isBad, color) {
    private val iconTexture = ResourceLocation(AsmodeusCore.ASSET_PREFIX, "textures/gui/potions/hypothermia.png")

    init {
        this.setPotionName(this, "hypothermia")
    }

    open fun setPotionName(potion: Potion, potionName: String?) {
        potion.setRegistryName(AsmodeusCore.MODID, potionName)
        potion.setPotionName("effect." + potion.getRegistryName().toString())
    }

    override fun hasStatusIcon(): Boolean {
        return true
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
        if (this === ACPotions.hypothermia) {
            val k: Int = 15 shr amplifier
            return if (k > 0) duration % k == 0 else true
        }
        return false
    }

    override fun performEffect(living: EntityLivingBase, amplifier: Int) {
        if (living !is EntityPlayer || living.world.isRemote) return

        living.attackEntityFrom(ACDamageSource.Companion.cold, 0.2f * amplifier)
    }

    override fun getCurativeItems(): MutableList<ItemStack?> {
        val ret: ArrayList<ItemStack?> = ArrayList<ItemStack?>()
        return ret
    }
}
