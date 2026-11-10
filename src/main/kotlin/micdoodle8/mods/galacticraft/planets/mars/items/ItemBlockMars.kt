package micdoodle8.mods.galacticraft.planets.mars.items

import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import net.minecraft.block.Block
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemBlockMars constructor(block: Block) : ItemBlock(block) {
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }

    @SideOnly(Side.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    public override fun getTranslationKey(itemstack: ItemStack): String {
        var name: String = ""

        when (itemstack.getItemDamage()) {
            0 -> {
                name = "coppermars"
            }

            1 -> {
                name = "tinmars"
            }

            3 -> {
                name = "ironmars"
            }

            2 -> {
                name = "deshmars"
            }

            4 -> {
                name = "marscobblestone"
            }

            5 -> {
                name = "marsgrass"
            }

            6 -> {
                name = "marsdirt"
            }

            7 -> {
                name = "marsdungeon"
            }

            8 -> {
                name = "marsdeco"
            }

            9 -> {
                name = "marsstone"
            }

            else -> name = "null"
        }

        return this.getBlock().getTranslationKey() + "." + name
    }

    public override fun getTranslationKey(): String {
        return this.getBlock().getTranslationKey() + ".0"
    }
}
