package micdoodle8.mods.galacticraft.core.items

import foundation.item.legacyItemValue

import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import net.minecraft.world.level.block.Block
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemBlock
import net.minecraft.world.item.ItemStack
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class ItemBlockDummy constructor(block: Block) : ItemBlock(block) {
    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
    }

    //    @Override
    //    @OnlyIn(Dist.CLIENT)
    //    public IIcon getIconFromDamage(int par1)
    //    {
    //        return this.getBlock().getIcon(0, par1);
    //    }
    override fun getMetadata(damage: Int): Int {
        return damage
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    public override fun getTranslationKey(itemstack: ItemStack): String {
        val metadata: Int = itemstack.legacyItemValue
        var blockName: String? = ""

        when (metadata) {
            1 -> blockName = "spaceStationBase"
            2 -> blockName = "launchPad"
            3 -> blockName = "nasaWorkbench"
            4 -> blockName = "solar"
            5 -> blockName = "cryogenicChamber"
            else -> blockName = null
        }

        return this.getBlock().getTranslationKey() + "." + blockName
    }

    public override fun getTranslationKey(): String {
        return this.getBlock().getTranslationKey() + ".0"
    }
}
