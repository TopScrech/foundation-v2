package micdoodle8.mods.galacticraft.core.items

import micdoodle8.mods.galacticraft.core.blocks.BlockSolar
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import net.minecraft.block.Block
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import kotlin.math.max
import kotlin.math.min

open class ItemBlockSolar constructor(block: Block?) : ItemBlockDesc(block) {
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
    }

    public override fun getTranslationKey(par1ItemStack: ItemStack): String {
        val index: Int = min(
            max(par1ItemStack.getItemDamage() / 4, 0),
            micdoodle8.mods.galacticraft.core.blocks.BlockSolar.EnumSolarType.entries.toTypedArray().size
        )

        val name: String =
            micdoodle8.mods.galacticraft.core.blocks.BlockSolar.EnumSolarType.entries.toTypedArray()[index].getName()

        return this.getBlock().getTranslationKey() + "." + name
    }

    @SideOnly(Side.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    override fun getMetadata(damage: Int): Int {
        return damage
    }
}
