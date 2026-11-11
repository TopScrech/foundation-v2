package micdoodle8.mods.galacticraft.core.items

import foundation.item.legacyItemValue

import micdoodle8.mods.galacticraft.core.blocks.BlockBasic
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import net.minecraft.world.level.block.Block
import net.minecraft.item.EnumRarity
import net.minecraft.world.item.ItemStack
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import kotlin.math.max
import kotlin.math.min

open class ItemBlockBase constructor(block: Block?) : ItemBlockDesc(block) {
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
        val name: String =
            micdoodle8.mods.galacticraft.core.blocks.BlockBasic.EnumBlockBasic.entries.toTypedArray()[min(
                max(itemstack.legacyItemValue - 3, 0),
                10
            )].getName()

        return this.getBlock().getTranslationKey() + "." + name
    }

    public override fun getTranslationKey(): String {
        return this.getBlock().getTranslationKey() + ".0"
    }
}
