package micdoodle8.mods.galacticraft.core.proxy

import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.item.EnumExtendedInventorySlot
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerHandler
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.entities.player.IPlayerServer
import micdoodle8.mods.galacticraft.core.entities.player.PlayerServer
import micdoodle8.mods.galacticraft.core.fluid.FluidNetwork
import micdoodle8.mods.galacticraft.core.tick.TickHandlerServer
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import micdoodle8.mods.galacticraft.core.wrappers.PartialCanister
import micdoodle8.mods.galacticraft.core.wrappers.PlayerGearData
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.network.INetHandler
import net.minecraft.network.NetHandlerPlayServer
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

open class CommonProxyCore {
    @JvmField
    var player: IPlayerServer = PlayerServer()

    open fun preInit(event: FMLPreInitializationEvent?) {
    }

    open fun registerVariants() {
    }

    open fun init(event: FMLInitializationEvent?) {
    }

    open fun postInit(event: FMLPostInitializationEvent?) {
    }

    open fun registerCanister(container: PartialCanister?) {
    }

    open fun registerFluidTexture(fluid: Fluid?, submergedTexture: ResourceLocation?) {
    }

    open fun getClientWorld(): World? {
        return null
    }

    open fun spawnParticle(particleID: String?, position: Vector3?, motion: Vector3?, otherInfo: Array<Any?>?) {
    }

    open fun getWorldForID(dimensionID: Int): World? {
        return WorldUtil.getWorldForDimensionServer(dimensionID)
    }

    open fun getPlayerFromNetHandler(handler: INetHandler?): EntityPlayer? {
        if (handler is NetHandlerPlayServer) {
            return handler.player
        } else {
            return null
        }
    }

    open fun postRegisterItem(item: Item?) {
    }

    open fun unregisterNetwork(fluidNetwork: FluidNetwork?) {
        if (GCCoreUtil.getEffectiveSide().isServer()) {
            TickHandlerServer.removeFluidNetwork(fluidNetwork)
        }
    }

    open fun registerNetwork(fluidNetwork: FluidNetwork?) {
        if (GCCoreUtil.getEffectiveSide().isServer()) {
            TickHandlerServer.addFluidNetwork((fluidNetwork)!!)
        }
    }

    open fun isPaused(): Boolean {
        return false
    }

    open fun getGearData(player: EntityPlayer): PlayerGearData? {
        val stats: GCPlayerStats = (GCPlayerStats.get(player))!!

        val mask: Int =
            if (stats.getMaskInSlot() == null) GCPlayerHandler.GEAR_NOT_PRESENT else GalacticraftRegistry.findMatchingGearID(
                (stats.getMaskInSlot())!!,
                EnumExtendedInventorySlot.MASK
            )
        val gear: Int =
            if (stats.getGearInSlot() == null) GCPlayerHandler.GEAR_NOT_PRESENT else GalacticraftRegistry.findMatchingGearID(
                (stats.getGearInSlot())!!,
                EnumExtendedInventorySlot.GEAR
            )
        val leftTank: Int =
            if (stats.getTankInSlot1() == null) GCPlayerHandler.GEAR_NOT_PRESENT else GalacticraftRegistry.findMatchingGearID(
                (stats.getTankInSlot1())!!,
                EnumExtendedInventorySlot.LEFT_TANK
            )
        val rightTank: Int =
            if (stats.getTankInSlot2() == null) GCPlayerHandler.GEAR_NOT_PRESENT else GalacticraftRegistry.findMatchingGearID(
                (stats.getTankInSlot2())!!,
                EnumExtendedInventorySlot.RIGHT_TANK
            )
        val frequencyModule: Int =
            if (stats.getFrequencyModuleInSlot() == null) GCPlayerHandler.GEAR_NOT_PRESENT else GalacticraftRegistry.findMatchingGearID(
                (stats.getFrequencyModuleInSlot())!!,
                EnumExtendedInventorySlot.FREQUENCY_MODULE
            )
        val thermalPadding: IntArray = IntArray(4)
        thermalPadding[0] =
            if (stats.getThermalHelmetInSlot() == null) GCPlayerHandler.GEAR_NOT_PRESENT else GalacticraftRegistry.findMatchingGearID(
                (stats.getThermalHelmetInSlot())!!,
                EnumExtendedInventorySlot.THERMAL_HELMET
            )
        thermalPadding[1] =
            if (stats.getThermalChestplateInSlot() == null) GCPlayerHandler.GEAR_NOT_PRESENT else GalacticraftRegistry.findMatchingGearID(
                (stats.getThermalChestplateInSlot())!!,
                EnumExtendedInventorySlot.THERMAL_CHESTPLATE
            )
        thermalPadding[2] =
            if (stats.getThermalLeggingsInSlot() == null) GCPlayerHandler.GEAR_NOT_PRESENT else GalacticraftRegistry.findMatchingGearID(
                (stats.getThermalLeggingsInSlot())!!,
                EnumExtendedInventorySlot.THERMAL_LEGGINGS
            )
        thermalPadding[3] =
            if (stats.getThermalBootsInSlot() == null) GCPlayerHandler.GEAR_NOT_PRESENT else GalacticraftRegistry.findMatchingGearID(
                (stats.getThermalBootsInSlot())!!,
                EnumExtendedInventorySlot.THERMAL_BOOTS
            )
        //TODO: Parachute
        return PlayerGearData(player, mask, gear, leftTank, rightTank, frequencyModule, thermalPadding)
    }
}
