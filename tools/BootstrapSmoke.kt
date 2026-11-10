import java.io.File
import java.net.URLClassLoader
import net.minecraft.launchwrapper.Launch
import net.minecraft.launchwrapper.LaunchClassLoader

fun main(args: Array<String>) {
    val parent = Launch::class.java.classLoader as URLClassLoader
    Launch.classLoader = LaunchClassLoader((parent.urLs.toList() + File(args[0]).toURI().toURL()).toTypedArray())
    Launch.blackboard = hashMapOf<String, Any>(
        "TweakClasses" to arrayListOf("net.minecraftforge.fml.common.launcher.FMLTweaker"),
        "Tweaks" to arrayListOf<Any>()
    )
    Launch.minecraftHome = File(args[1])
    Thread.currentThread().contextClassLoader = Launch.classLoader
    for (prefix in listOf("net.minecraftforge.fml.relauncher.", "net.minecraftforge.classloading.", "org.apache.commons.", "org.apache.http.", "org.apache.maven.", "com.google.common.", "org.objectweb.asm.", "LZMA.")) {
        Launch.classLoader.addClassLoaderExclusion(prefix)
    }
    Launch.classLoader.addTransformerExclusion("foundation.")
    val plugin = Class.forName("foundation.FoundationLoadingPlugin", true, Launch.classLoader).getDeclaredConstructor().newInstance()
    check(plugin.javaClass.getMethod("getModContainerClass").invoke(plugin) == "micdoodle8.mods.miccore.MicdoodleModContainer")
    check(Class.forName("org.spongepowered.asm.service.MixinService", false, Launch.classLoader).classLoader === parent)
    println("BOOTSTRAP_SMOKE_PASSED")
}
