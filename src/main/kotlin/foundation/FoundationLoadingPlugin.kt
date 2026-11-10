package foundation

import micdoodle8.mods.miccore.MicdoodlePlugin
import net.minecraft.launchwrapper.Launch
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin
import java.net.URL
import java.net.URLClassLoader

/** Forge skips FMLCorePlugin when the same manifest also declares TweakClass */
@IFMLLoadingPlugin.Name("Foundation")
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.TransformerExclusions("foundation.", "micdoodle8.mods.miccore.", "org.spongepowered.asm.", "kotlin.")
class FoundationLoadingPlugin : IFMLLoadingPlugin {
    private val micdoodlePlugin: MicdoodlePlugin

    init {
        initializeMixins()
        micdoodlePlugin = MicdoodlePlugin()
    }

    private fun initializeMixins() {
        // Mixin's LaunchWrapper service delegates its own packages to the parent loader
        // Add this jar there before loading any Mixin class to avoid splitting nested classes across loaders
        val parent = Launch::class.java.classLoader as URLClassLoader
        val addUrl = URLClassLoader::class.java.getDeclaredMethod("addURL", URL::class.java)
        addUrl.isAccessible = true
        addUrl.invoke(parent, javaClass.protectionDomain.codeSource.location)
        Launch.classLoader.addClassLoaderExclusion("org.spongepowered.asm.")

        // Forge is already constructing this plugin, so Mixin's platform scanner must not inject it again
        val corePlugins = System.getProperty("fml.coreMods.load", "").split(',').filter { it.isNotEmpty() }
        if (javaClass.name !in corePlugins) {
            System.setProperty("fml.coreMods.load", (corePlugins + javaClass.name).joinToString(","))
        }
        Class.forName("org.spongepowered.asm.launch.MixinBootstrap", true, parent).getMethod("init").invoke(null)
        val environmentClass = Class.forName("org.spongepowered.asm.mixin.MixinEnvironment", true, parent)
        val environment = environmentClass.getMethod("getDefaultEnvironment").invoke(null)
        environmentClass.getMethod("setObfuscationContext", String::class.java).invoke(environment, "searge")
        val mixins = Class.forName("org.spongepowered.asm.mixin.Mixins", true, parent)
        val addConfiguration = mixins.getMethod("addConfiguration", String::class.java)
        addConfiguration.invoke(null, "mixins.asmodeuscore.json")
        addConfiguration.invoke(null, "mixins.galaxyspace.json")
    }

    override fun getASMTransformerClass(): Array<String>? = micdoodlePlugin.getASMTransformerClass()
    override fun getModContainerClass(): String? = micdoodlePlugin.getModContainerClass()
    override fun getSetupClass(): String? = micdoodlePlugin.getSetupClass()
    // The manifest's foundation_at.cfg already contains the combined access rules
    override fun getAccessTransformerClass(): String? = null
    override fun injectData(data: MutableMap<String, Any>?) = micdoodlePlugin.injectData(data)
}
