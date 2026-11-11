package micdoodle8.mods.galacticraft.core.items

import foundation.item.legacyItemValue

import micdoodle8.mods.galacticraft.core.blocks.BlockSolar
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import net.minecraft.world.level.block.Block
import net.minecraft.item.EnumRarity
import net.minecraft.world.item.ItemStack
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import kotlin.math.max
import kotlin.math.min

open class ItemBlockSolar constructor(block: Block?) : ItemBlockDesc(block) {
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
    }

    public override fun getTranslationKey(par1ItemStack: ItemStack): String {
        val index: Int = min(
            max(par1ItemStack.legacyItemValue / 4, 0),
            micdoodle8.mods.galacticraft.core.blocks.BlockSolar.EnumSolarType.entries.toTypedArray().size
        )

        val name: String =
            micdoodle8.mods.galacticraft.core.blocks.BlockSolar.EnumSolarType.entries.toTypedArray()[index].getName()

        return this.getBlock().getTranslationKey() + "." + name
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    override fun getMetadata(damage: Int): Int {
        return damage
    }
}
