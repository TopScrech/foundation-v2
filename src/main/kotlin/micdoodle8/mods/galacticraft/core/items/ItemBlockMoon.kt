package micdoodle8.mods.galacticraft.core.items

import foundation.item.legacyItemValue

import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import net.minecraft.world.level.block.Block
import net.minecraft.item.EnumRarity
import net.minecraft.world.item.ItemStack
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class ItemBlockMoon constructor(block: Block?) : ItemBlockDesc(block) {
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
    }

    override fun getMetadata(meta: Int): Int {
        return meta
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    public override fun getTranslationKey(itemstack: ItemStack): String {
        val name: String

        when (itemstack.legacyItemValue) {
            0 -> {
                name = "coppermoon"
            }

            1 -> {
                name = "tinmoon"
            }

            2 -> {
                name = "cheesestone"
            }

            3 -> {
                name = "moondirt"
            }

            4 -> {
                name = "moonstone"
            }

            5 -> {
                name = "moongrass"
            }

            6 -> {
                name = "sapphiremoon"
            }

            14 -> {
                name = "bricks"
            }

            else -> name = "null"
        }

        return this.getBlock().getTranslationKey() + "." + name
    }

    public override fun getTranslationKey(): String {
        return this.getBlock().getTranslationKey() + ".0"
    }
}
