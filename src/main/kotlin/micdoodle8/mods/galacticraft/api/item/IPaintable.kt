package micdoodle8.mods.galacticraft.api.item

import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraftforge.fml.LogicalSide as Side

interface IPaintable {
    fun setColor(color: Int, player: EntityPlayer?, side: Side?): Int
}
