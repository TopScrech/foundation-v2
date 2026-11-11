package micdoodle8.mods.galacticraft.core.items

import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level as World

interface IClickableItem {
    /**
     * Use to replicate ItemStack version of onItemRightClick as in MC1.10
     */
    fun onItemRightClick(itemStack: ItemStack?, worldIn: World?, player: EntityPlayer?): ItemStack?
}
