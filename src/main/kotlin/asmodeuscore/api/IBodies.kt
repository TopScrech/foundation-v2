package asmodeuscore.api

import asmodeuscore.api.IBodies
import net.minecraft.world.item.Item
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent as FMLInitializationEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent as FMLPostInitializationEvent

interface IBodies {
    fun canRegister(): Boolean
    fun preInit()
    fun init(event: FMLInitializationEvent?)
    fun postInit(event: FMLPostInitializationEvent?)
    fun registerRecipes()
    fun registerRender()
    fun registerVariant()

    fun preInitialization() {
    }

    fun registerItems() {
    }

    fun getPriority(): Int {
        return 0
    }
}
