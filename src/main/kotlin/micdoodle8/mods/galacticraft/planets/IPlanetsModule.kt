package micdoodle8.mods.galacticraft.planets

import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.level.Level as World
import foundation.config.FoundationConfiguration as Configuration
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent as FMLInitializationEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent as FMLPostInitializationEvent
import net.minecraftforge.event.server.ServerStartedEvent as FMLServerStartedEvent
import net.minecraftforge.event.server.ServerStartingEvent as FMLServerStartingEvent
import net.minecraftforge.fml.LogicalSide as Side

interface IPlanetsModule {
    fun preInit()

    fun init(event: FMLInitializationEvent?)

    fun postInit(event: FMLPostInitializationEvent?)

    fun serverInit(event: FMLServerStartedEvent?)

    fun serverStarting(event: FMLServerStartingEvent?)

    fun getGuiIDs(idList: MutableList<Int>?)

    fun getGuiElement(side: Side?, ID: Int, player: EntityPlayer?, world: World?, x: Int, y: Int, z: Int): Any?

    fun getConfiguration(): Configuration?

    fun syncConfig()
}
