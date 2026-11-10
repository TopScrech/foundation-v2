package micdoodle8.mods.galacticraft.planets

import micdoodle8.mods.galacticraft.api.vector.Vector3
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.relauncher.Side

interface IPlanetsModuleClient {
    fun preInit(event: FMLPreInitializationEvent?)

    fun registerVariants()

    fun init(event: FMLInitializationEvent?)

    fun postInit(event: FMLPostInitializationEvent?)

    fun getGuiIDs(idList: MutableList<Int>?)

    fun getGuiElement(side: Side?, ID: Int, player: EntityPlayer?, world: World?, x: Int, y: Int, z: Int): Any?

    fun spawnParticle(particleID: String?, position: Vector3?, motion: Vector3?, vararg extraData: Any?)
}
