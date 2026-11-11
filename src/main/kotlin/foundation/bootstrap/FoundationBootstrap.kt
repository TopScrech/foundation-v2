package foundation.bootstrap

import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.fml.loading.FMLEnvironment
import net.minecraftforge.fml.loading.FMLPaths
import java.io.File
import java.lang.annotation.ElementType

/** Shared bootstrapping for the four mod containers in the unified jar */
object FoundationBootstrap {
    val configDirectory: File get() = FMLPaths.CONFIGDIR.get().toFile()

    fun sourceFile(type: Class<*>): File = File(type.protectionDomain.codeSource.location.toURI())

    /** Resolve the client implementation by name so a dedicated server never links its classes */
    fun <T : Any> createProxy(commonType: Class<T>, clientName: String): T {
        val name = if (FMLEnvironment.dist == Dist.CLIENT) clientName else commonType.name
        return try {
            commonType.cast(Class.forName(name, true, commonType.classLoader).getDeclaredConstructor().newInstance())
        } catch (cause: ReflectiveOperationException) {
            throw IllegalStateException("Unable to construct Foundation proxy $name", cause)
        }
    }

    /** Load client event installers by name so common code never links renderer classes on a server */
    @Suppress("UNCHECKED_CAST")
    fun installClient(context: FMLJavaModLoadingContext, className: String) {
        if (FMLEnvironment.dist != Dist.CLIENT) return
        val installer = Class.forName(className).getDeclaredConstructor().newInstance() as java.util.function.Consumer<FMLJavaModLoadingContext>
        installer.accept(context)
    }

    fun <T : Any> discover(annotation: Class<out Annotation>, component: Class<T>): List<T> {
        val names = ModList.getAllScanData().asSequence()
            .flatMap { it.annotations.asSequence() }
            .filter { it.targetType() == ElementType.TYPE && it.annotationType().className == annotation.name }
            .map { it.clazz().className }.distinct().sorted().toList()
        return names.map { name ->
            try {
                component.cast(Class.forName(name, true, component.classLoader).getDeclaredConstructor().newInstance())
            } catch (cause: ReflectiveOperationException) {
                throw IllegalStateException("Unable to construct annotated Foundation component $name", cause)
            }
        }
    }

    /** Run shared mutable initialization on the main thread after parallel mod callbacks complete */
    fun attach(
        context: FMLJavaModLoadingContext,
        common: (FMLCommonSetupEvent) -> Unit,
        complete: (FMLLoadCompleteEvent) -> Unit
    ) {
        val group = context.modBusGroup
        FMLCommonSetupEvent.getBus(group).addListener { event: FMLCommonSetupEvent ->
            event.enqueueWork { common(event) }
        }
        FMLLoadCompleteEvent.getBus(group).addListener { event: FMLLoadCompleteEvent ->
            event.enqueueWork { complete(event) }
        }
    }
}
