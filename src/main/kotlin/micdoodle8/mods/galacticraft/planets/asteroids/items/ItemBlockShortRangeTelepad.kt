package micdoodle8.mods.galacticraft.planets.asteroids.items

import micdoodle8.mods.galacticraft.core.items.ItemBlockDesc
import net.minecraft.world.level.block.Block
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class ItemBlockShortRangeTelepad constructor(block: Block?) : ItemBlockDesc(block) {
    @OnlyIn(Dist.CLIENT)
    override fun addInformation(
        par1ItemStack: ItemStack,
        worldIn: World?,
        tooltip: MutableList<String?>,
        flagIn: ITooltipFlag
    ) {
        super.addInformation(par1ItemStack, worldIn, tooltip, flagIn)
    }
}
