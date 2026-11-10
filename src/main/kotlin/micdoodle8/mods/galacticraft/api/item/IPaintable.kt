package micdoodle8.mods.galacticraft.api.item

import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.fml.relauncher.Side

interface IPaintable {
    fun setColor(color: Int, player: EntityPlayer?, side: Side?): Int
}
