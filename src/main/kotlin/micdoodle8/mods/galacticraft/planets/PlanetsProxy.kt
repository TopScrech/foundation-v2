package micdoodle8.mods.galacticraft.planets

import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.venus.tick.VenusTickHandlerServer
import micdoodle8.mods.galacticraft.planets.venus.tile.SolarModuleNetwork
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level as World
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent as FMLInitializationEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent as FMLPostInitializationEvent
import net.minecraftforge.event.server.ServerStartedEvent as FMLServerStartedEvent
import net.minecraftforge.event.server.ServerStartingEvent as FMLServerStartingEvent
import net.minecraftforge.fml.common.network.IGuiHandler
import net.minecraftforge.fml.LogicalSide as Side
import java.util.ArrayList

open class PlanetsProxy : IGuiHandler {
    open fun preInit() {
        for (module: IPlanetsModule in GalacticraftPlanets.Companion.commonModules) {
            module.preInit()
        }
    }

    open fun registerVariants() {
    }

    open fun init(event: FMLInitializationEvent?) {
        for (module: IPlanetsModule in GalacticraftPlanets.Companion.commonModules) {
            module.init(event)
        }
    }

    open fun postInit(event: FMLPostInitializationEvent?) {
        for (module: IPlanetsModule in GalacticraftPlanets.Companion.commonModules) {
            module.postInit(event)
        }
    }

    open fun serverStarting(event: FMLServerStartingEvent?) {
        for (module: IPlanetsModule in GalacticraftPlanets.Companion.commonModules) {
            module.serverStarting(event)
        }
    }

    open fun serverInit(event: FMLServerStartedEvent?) {
        for (module: IPlanetsModule in GalacticraftPlanets.Companion.commonModules) {
            module.serverInit(event)
        }
    }

    override fun getServerGuiElement(ID: Int, player: EntityPlayer?, world: World?, x: Int, y: Int, z: Int): Any? {
        for (module: IPlanetsModule in GalacticraftPlanets.Companion.commonModules) {
            val guiIDs: MutableList<Int> = ArrayList<Int>()
            module.getGuiIDs(guiIDs)
            if (guiIDs.contains(ID)) {
                return module.getGuiElement(Side.SERVER, ID, player, world, x, y, z)
            }
        }

        return null
    }

    override fun getClientGuiElement(ID: Int, player: EntityPlayer?, world: World?, x: Int, y: Int, z: Int): Any? {
        for (module: IPlanetsModuleClient in GalacticraftPlanets.Companion.clientModules) {
            val guiIDs: MutableList<Int> = ArrayList<Int>()
            module.getGuiIDs(guiIDs)
            if (guiIDs.contains(ID)) {
                return module.getGuiElement(Side.CLIENT, ID, player, world, x, y, z)
            }
        }

        return null
    }

    open fun postRegisterItem(item: Item?) {
    }

    open fun unregisterNetwork(solarNetwork: SolarModuleNetwork?) {
        if (GCCoreUtil.getEffectiveSide().isServer()) {
            VenusTickHandlerServer.removeSolarNetwork(solarNetwork)
        }
    }

    open fun registerNetwork(solarNetwork: SolarModuleNetwork?) {
        if (GCCoreUtil.getEffectiveSide().isServer()) {
            VenusTickHandlerServer.addSolarNetwork(solarNetwork)
        }
    }
}
