package micdoodle8.mods.galacticraft.planets.venus.items

import foundation.item.legacyItemValue

import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.planets.venus.blocks.BlockBasicVenus
import net.minecraft.world.level.block.Block
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemBlock
import net.minecraft.world.item.ItemStack
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class ItemBlockBasicVenus constructor(block: Block) : ItemBlock(block) {
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
            else -> name =
                micdoodle8.mods.galacticraft.planets.venus.blocks.BlockBasicVenus.EnumBlockBasicVenus.entries.toTypedArray()[itemstack.legacyItemValue].getName()
        }

        return this.getBlock().getTranslationKey() + "." + name
    }

    public override fun getTranslationKey(): String {
        return this.getBlock().getTranslationKey() + ".0"
    }
}
