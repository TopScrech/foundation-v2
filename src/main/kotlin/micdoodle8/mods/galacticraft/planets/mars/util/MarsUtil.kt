package micdoodle8.mods.galacticraft.planets.mars.util

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket
import micdoodle8.mods.galacticraft.planets.mars.network.PacketSimpleMars.EnumSimplePacketMars

import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.inventory.ContainerParaChest
import micdoodle8.mods.galacticraft.core.inventory.ContainerRocketInventory
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.recipe.NasaWorkbenchRecipe
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.mars.entities.EntityCargoRocket
import micdoodle8.mods.galacticraft.planets.mars.entities.EntityLandingBalloons
import micdoodle8.mods.galacticraft.planets.mars.entities.EntitySlimeling
import micdoodle8.mods.galacticraft.planets.mars.inventory.ContainerLaunchControllerAdvanced
import micdoodle8.mods.galacticraft.planets.mars.inventory.ContainerSlimeling
import micdoodle8.mods.galacticraft.planets.mars.network.PacketSimpleMars
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityLaunchController
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.item.ItemStack
import java.util.HashMap

object MarsUtil {
    @JvmStatic
    fun addRocketBenchT2Recipe(result: ItemStack?, input: HashMap<Int, ItemStack?>?) {
        GalacticraftRegistry.addT2RocketRecipe(NasaWorkbenchRecipe((result)!!, (input)!!))
    }

    @JvmStatic
    fun adCargoRocketRecipe(result: ItemStack?, input: HashMap<Int, ItemStack?>?) {
        GalacticraftRegistry.addCargoRocketRecipe(NasaWorkbenchRecipe((result)!!, (input)!!))
    }

    @JvmStatic
    fun openParachestInventory(player: EntityPlayerMP, landerInv: EntityLandingBalloons) {
        player.getNextWindowId()
        player.closeContainer()
        val windowId: Int = player.currentWindowId
        (GalacticraftCore.packetPipeline)!!.sendTo(
            PacketSimple(
                PacketSimple.EnumSimplePacket.C_OPEN_PARACHEST_GUI,
                GCCoreUtil.getDimensionID(player.level()),
                arrayOf<Any>(windowId, 1, landerInv.getEntityId())
            ), player
        )
        player.openContainer = ContainerParaChest(player.inventory, landerInv, player)
        player.openContainer.windowId = windowId
        player.openContainer.addListener(player)
    }

    @JvmStatic
    fun openSlimelingInventory(player: EntityPlayerMP, slimeling: EntitySlimeling) {
        player.getNextWindowId()
        player.closeContainer()
        val windowId: Int = player.currentWindowId
        (GalacticraftCore.packetPipeline)!!.sendTo(
            PacketSimpleMars(
                PacketSimpleMars.EnumSimplePacketMars.C_OPEN_CUSTOM_GUI,
                GCCoreUtil.getDimensionID(player.level()),
                arrayOf<Any>(windowId, 0, slimeling.getEntityId())
            ), player
        )
        player.openContainer = ContainerSlimeling(player.inventory, slimeling, player)
        player.openContainer.windowId = windowId
        player.openContainer.addListener(player)
    }

    @JvmStatic
    fun openCargoRocketInventory(player: EntityPlayerMP, rocket: EntityCargoRocket) {
        player.getNextWindowId()
        player.closeContainer()
        val windowId: Int = player.currentWindowId
        (GalacticraftCore.packetPipeline)!!.sendTo(
            PacketSimpleMars(
                PacketSimpleMars.EnumSimplePacketMars.C_OPEN_CUSTOM_GUI,
                GCCoreUtil.getDimensionID(player.level()),
                arrayOf<Any>(windowId, 1, rocket.getEntityId())
            ), player
        )
        player.openContainer = ContainerRocketInventory(player.inventory, rocket, (rocket.rocketType)!!, player)
        player.openContainer.windowId = windowId
        player.openContainer.addListener(player)
    }

    @JvmStatic
    fun openAdvancedLaunchController(player: EntityPlayerMP, launchController: TileEntityLaunchController) {
        player.getNextWindowId()
        player.closeContainer()
        val windowId: Int = player.currentWindowId
        (GalacticraftCore.packetPipeline)!!.sendTo(
            PacketSimpleMars(
                PacketSimpleMars.EnumSimplePacketMars.C_OPEN_CUSTOM_GUI_TILE,
                GCCoreUtil.getDimensionID(player.level()),
                arrayOf<Any>(windowId, 0, launchController.getPos())
            ), player
        )
        player.openContainer = ContainerLaunchControllerAdvanced(player.inventory, launchController, player)
        player.openContainer.windowId = windowId
        player.openContainer.addListener(player)
    }
}
