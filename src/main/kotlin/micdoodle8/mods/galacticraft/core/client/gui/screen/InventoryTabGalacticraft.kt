package micdoodle8.mods.galacticraft.core.client.gui.screen

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket

import micdoodle8.mods.galacticraft.api.client.tabs.AbstractTab
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.client.FMLClientHandler

open class InventoryTabGalacticraft constructor() : AbstractTab(0, 0, 0, ItemStack(GCItems.oxMask)) {
    override fun onTabClicked() {
        (GalacticraftCore.packetPipeline)!!.sendToServer(
            PacketSimple(
                PacketSimple.EnumSimplePacket.S_OPEN_EXTENDED_INVENTORY,
                GCCoreUtil.getDimensionID(FMLClientHandler.instance().getClient().world),
                arrayOf<Any?>()
            )
        )
        ClientProxyCore.playerClientHandler.onBuild(0, FMLClientHandler.instance().getClientPlayerEntity())
    }

    override fun shouldAddToList(): Boolean {
        return true
    }
}
