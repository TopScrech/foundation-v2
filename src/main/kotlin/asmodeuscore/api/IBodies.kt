package asmodeuscore.api

import asmodeuscore.api.IBodies
import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

interface IBodies {
    fun canRegister(): Boolean
    fun preInit(event: FMLPreInitializationEvent?)
    fun init(event: FMLInitializationEvent?)
    fun postInit(event: FMLPostInitializationEvent?)
    fun registerRecipes()
    fun registerRender()
    fun registerVariant()

    fun preInitialization(event: FMLPreInitializationEvent?) {
    }

    fun registerItems(event: RegistryEvent.Register<Item?>?) {
    }

    fun getPriority(): Int {
        return 0
    }
}
