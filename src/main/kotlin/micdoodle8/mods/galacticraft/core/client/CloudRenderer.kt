package micdoodle8.mods.galacticraft.core.client

import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.WorldClient
import net.minecraftforge.client.IRenderHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class CloudRenderer : IRenderHandler() {
    @SideOnly(Side.CLIENT)
    override fun render(partialTicks: Float, world: WorldClient?, mc: Minecraft?) {
        // Do nothing
    }
}
