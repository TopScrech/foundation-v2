package micdoodle8.mods.galacticraft.planets

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLServerStartedEvent
import net.minecraftforge.fml.common.event.FMLServerStartingEvent
import net.minecraftforge.fml.relauncher.Side

interface IPlanetsModule {
    fun preInit(event: FMLPreInitializationEvent?)

    fun init(event: FMLInitializationEvent?)

    fun postInit(event: FMLPostInitializationEvent?)

    fun serverInit(event: FMLServerStartedEvent?)

    fun serverStarting(event: FMLServerStartingEvent?)

    fun getGuiIDs(idList: MutableList<Int>?)

    fun getGuiElement(side: Side?, ID: Int, player: EntityPlayer?, world: World?, x: Int, y: Int, z: Int): Any?

    fun getConfiguration(): Configuration?

    fun syncConfig()
}
