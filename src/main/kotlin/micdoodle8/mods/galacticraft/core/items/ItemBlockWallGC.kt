package micdoodle8.mods.galacticraft.core.items

import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import net.minecraft.block.Block
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemBlockWallGC constructor(block: Block) : ItemBlock(block) {
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
        var meta: Int = itemstack.getItemDamage()

        if (meta < 0 || meta >= types.size) {
            meta = 0
        }
        return super.getTranslationKey() + "." + types[meta]
    }

    companion object {
        private val types: Array<String?> = ((arrayOf<String>(
            "tin",
            "tin",
            "moon",
            "moon_bricks",
            "mars",
            "mars_bricks"
        ) as Array<String?>?))!!
    }
}
