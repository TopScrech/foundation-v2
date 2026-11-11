package foundation.biome

import net.minecraft.network.chat.Component
import net.minecraft.server.packs.PackLocationInfo
import net.minecraft.server.packs.PackSelectionConfig
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.PathPackResources
import net.minecraft.server.packs.repository.Pack
import net.minecraft.server.packs.repository.PackSource
import net.minecraft.server.packs.repository.RepositorySource
import net.minecraftforge.event.AddPackFindersEvent
import net.minecraftforge.fml.ModList
import java.nio.file.Path
import java.util.Optional
import java.util.concurrent.atomic.AtomicBoolean

/** Optional biome classifications retain their pack identity when disabled by the legacy setting */
object FoundationBiomeTags {
    private val installed = AtomicBoolean()

    fun install(enabled: () -> Boolean) {
        if (!installed.compareAndSet(false, true)) return
        AddPackFindersEvent.BUS.addListener { event: AddPackFindersEvent ->
            if (event.getPackType() == PackType.SERVER_DATA) {
                val file = requireNotNull(ModList.getModFileById("asmodeuscore")) { "Embedded Asmodeus Core was not loaded" }.file
                event.addRepositorySource(repositorySource(file.findResource("biome-types"), enabled()))
            }
        }
    }

    fun repositorySource(root: Path, enabled: Boolean): RepositorySource {
        val content = root.resolve(if (enabled) "enabled" else "disabled")
        return RepositorySource { output ->
            val location = PackLocationInfo("foundation/biome-types", Component.literal("Foundation biome classifications"),
                PackSource.BUILT_IN, Optional.empty())
            val pack = requireNotNull(Pack.readMetaAndCreate(location, PathPackResources.PathResourcesSupplier(content),
                PackType.SERVER_DATA, PackSelectionConfig(true, Pack.Position.TOP, true))) {
                "Unable to read the Foundation biome classification pack at $content"
            }
            output.accept(pack)
        }
    }
}
