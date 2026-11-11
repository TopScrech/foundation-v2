package micdoodle8.mods.galacticraft.core.client

import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel as WorldClient
import net.minecraftforge.client.IRenderHandler
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class CloudRenderer : IRenderHandler() {
    @OnlyIn(Dist.CLIENT)
    override fun render(partialTicks: Float, world: WorldClient?, mc: Minecraft?) {
        // Do nothing
    }
}
