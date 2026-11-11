package micdoodle8.mods.galacticraft.core.client.model

import java.util.HashMap
import java.util.HashSet

import com.google.common.collect.ImmutableMap
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.IReloadableResourceManager
import net.minecraft.client.resources.IResource
import net.minecraft.client.resources.IResourceManager
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.client.model.ICustomModelLoader
import net.minecraftforge.client.model.IModel
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.client.model.obj.OBJModel
import java.io.IOException
import java.util.*

/*
 * Loader for OBJ models.
 * To enable your mod call instance.addDomain(modid).
 * If you need more control over accepted resources - extend the class, and register a new instance with ModelLoaderRegistry.
 */
open class OBJLoaderGC : ICustomModelLoader {
    private var manager: IResourceManager? = null
    private val enabledDomains: MutableSet<String?> = HashSet<String?>()
    private val cache: MutableMap<ResourceLocation?, IModel?> = HashMap<ResourceLocation?, IModel?>()

    open fun addDomain(domain: String) {
        enabledDomains.add(domain.lowercase(Locale.getDefault()))
    }

    override fun onResourceManagerReload(resourceManager: IResourceManager) {
        this.manager = resourceManager
        cache.clear()
    }

    override fun accepts(modelLocation: ResourceLocation): Boolean {
        return enabledDomains.contains(modelLocation.getNamespace()) && modelLocation.getPath()
            .endsWith(".obj")
    }

    @Throws(IOException::class)
    override fun loadModel(modelLocation: ResourceLocation): IModel {
        var model: IModel? = null
        if (cache.containsKey(modelLocation)) {
            model = cache.get(modelLocation)
        } else {
            try {
                val prefix: String = if (modelLocation.getPath().contains("models/")) "" else "models/"
                val file: ResourceLocation =
                    ResourceLocation(modelLocation.getNamespace(), prefix + modelLocation.getPath())
                val resource: IResource = manager!!.getResource(file)
                if (resource != null) {
                    val parser: OBJModel.Parser = OBJModel.Parser(resource, manager)
                    try {
                        model = parser.parse().process(ImmutableMap.of<String?, String?>("flip-v", "true"))
                    } finally {
                        resource.getInputStream().close()
                        cache.put(modelLocation, model)
                    }
                }
            } catch (e: IOException) {
                throw e
            }
        }
        if (model == null) return ModelLoaderRegistry.getMissingModel()
        return model
    }

    companion object {
        @JvmField
        val instance: OBJLoaderGC = OBJLoaderGC()

        init {
            (Minecraft.getInstance().getResourceManager() as IReloadableResourceManager).registerReloadListener(
                instance
            )
        }
    }
}
