package micdoodle8.mods.galacticraft.planets

import micdoodle8.mods.galacticraft.api.vector.Vector3
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.level.Level as World
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent as FMLInitializationEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent as FMLPostInitializationEvent
import net.minecraftforge.fml.LogicalSide as Side

interface IPlanetsModuleClient {
    fun preInit()

    fun registerVariants()

    fun init(event: FMLInitializationEvent?)

    fun postInit(event: FMLPostInitializationEvent?)

    fun getGuiIDs(idList: MutableList<Int>?)

    fun getGuiElement(side: Side?, ID: Int, player: EntityPlayer?, world: World?, x: Int, y: Int, z: Int): Any?

    fun spawnParticle(particleID: String?, position: Vector3?, motion: Vector3?, vararg extraData: Any?)
}
