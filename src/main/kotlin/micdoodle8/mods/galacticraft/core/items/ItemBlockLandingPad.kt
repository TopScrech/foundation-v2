package micdoodle8.mods.galacticraft.core.items

import foundation.item.legacyItemValue

import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import net.minecraft.world.level.block.Block
import net.minecraft.client.player.LocalPlayer as EntityPlayerSP
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.item.EnumRarity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class ItemBlockLandingPad constructor(block: Block?) : ItemBlockDesc(block) {
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
    }

    public override fun getTranslationKey(par1ItemStack: ItemStack): String {
        var name: String = ""

        when (par1ItemStack.legacyItemValue) {
            0 -> name = "landing_pad"
            1 -> name = "buggy_fueler"
            2 -> name = "cargo_pad"
        }

        return this.getBlock().getTranslationKey() + "." + name
    }

    override fun onCreated(stack: ItemStack, world: World, player: EntityPlayer) {
        if (world.isClientSide && stack.legacyItemValue == 0 && player is EntityPlayerSP) {
            ClientProxyCore.playerClientHandler.onBuild(5, player)
        }
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    override fun getMetadata(damage: Int): Int {
        return damage
    }
}
