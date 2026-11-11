package micdoodle8.mods.galacticraft.planets.venus.items

import foundation.item.legacyItemValue

import micdoodle8.mods.galacticraft.core.items.ItemBlockDesc
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.venus.blocks.BlockTorchWeb
import net.minecraft.world.level.block.Block
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.EnumRarity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class ItemBlockTorchWeb constructor(block: Block?) : ItemBlockDesc(block) {
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
            micdoodle8.mods.galacticraft.planets.venus.blocks.BlockTorchWeb.EnumWebType.entries.toTypedArray()[itemstack.legacyItemValue].getName()
        return this.getBlock().getTranslationKey() + "." + name
    }

    override fun addInformation(
        stack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<String?>,
        flagIn: ITooltipFlag
    ) {
        if (stack.legacyItemValue == 1) {
            tooltip.add(GCCoreUtil.translate("tile.web_torch.web_torch_1.description"))
        }
    }

    public override fun getTranslationKey(): String {
        return this.getBlock().getTranslationKey() + ".0"
    }
}
