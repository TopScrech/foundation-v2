package micdoodle8.mods.galacticraft.planets

import com.google.common.collect.Lists
import micdoodle8.mods.galacticraft.core.client.model.OBJLoaderGC
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.planets.asteroids.AsteroidsModuleClient
import micdoodle8.mods.galacticraft.planets.mars.MarsModuleClient
import micdoodle8.mods.galacticraft.planets.venus.VenusModuleClient
import net.minecraft.item.Item
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLServerStartingEvent

open class PlanetsProxyClient : PlanetsProxy() {
    private val itemsToRegisterJson: MutableList<Item?> = Lists.newArrayList<Item?>()

    override fun preInit(event: FMLPreInitializationEvent?) {
        OBJLoaderGC.instance.addDomain(GalacticraftPlanets.Companion.ASSET_PREFIX)
        GalacticraftPlanets.Companion.clientModules.add(MarsModuleClient())
        GalacticraftPlanets.Companion.clientModules.add(AsteroidsModuleClient())
        GalacticraftPlanets.Companion.clientModules.add(VenusModuleClient())

        super.preInit(event)

        for (module: IPlanetsModuleClient in GalacticraftPlanets.Companion.clientModules) {
            module.preInit(event)
        }
    }

    override fun registerVariants() {
        for (module: IPlanetsModuleClient in GalacticraftPlanets.Companion.clientModules) {
            module.registerVariants()
        }
    }

    override fun init(event: FMLInitializationEvent?) {
        super.init(event)

        for (module: IPlanetsModuleClient in GalacticraftPlanets.Companion.clientModules) {
            module.init(event)
        }

        for (toReg: Item? in itemsToRegisterJson) {
            ClientUtil.registerItemJson(GalacticraftPlanets.Companion.TEXTURE_PREFIX, (toReg)!!)
        }
    }

    override fun postInit(event: FMLPostInitializationEvent?) {
        super.postInit(event)

        for (module: IPlanetsModuleClient in GalacticraftPlanets.Companion.clientModules) {
            module.postInit(event)
        }
    }

    override fun serverStarting(event: FMLServerStartingEvent?) {
        super.serverStarting(event)
    }

    override fun postRegisterItem(item: Item?) {
        if (!(item)!!.getHasSubtypes()) {
            itemsToRegisterJson.add(item)
        }
    }
}
