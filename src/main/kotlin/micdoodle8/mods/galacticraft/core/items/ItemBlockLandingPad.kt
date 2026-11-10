package micdoodle8.mods.galacticraft.core.items

import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import net.minecraft.block.Block
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemBlockLandingPad constructor(block: Block?) : ItemBlockDesc(block) {
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
    }

    public override fun getTranslationKey(par1ItemStack: ItemStack): String {
        var name: String = ""

        when (par1ItemStack.getItemDamage()) {
            0 -> name = "landing_pad"
            1 -> name = "buggy_fueler"
            2 -> name = "cargo_pad"
        }

        return this.getBlock().getTranslationKey() + "." + name
    }

    override fun onCreated(stack: ItemStack, world: World, player: EntityPlayer) {
        if (world.isRemote && stack.getItemDamage() == 0 && player is EntityPlayerSP) {
            ClientProxyCore.playerClientHandler.onBuild(5, player)
        }
    }

    @SideOnly(Side.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    override fun getMetadata(damage: Int): Int {
        return damage
    }
}
