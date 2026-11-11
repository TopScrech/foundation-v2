package foundation.verification

import cpw.mods.modlauncher.serviceapi.ILaunchPluginService
import net.minecraftforge.fml.common.asm.CapabilityTokenSubclass
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import java.lang.instrument.ClassFileTransformer
import java.lang.instrument.Instrumentation
import java.security.ProtectionDomain
import java.util.concurrent.atomic.AtomicInteger

/** Apply Forge's actual token transformer to SDK classes in the isolated checks, without emulating capabilities */
class ForgeCapabilityTestAgent {
    companion object {
        private val transformed = AtomicInteger()

        @JvmStatic
        fun premain(arguments: String?, instrumentation: Instrumentation) {
            val forge = CapabilityTokenSubclass()
            instrumentation.addTransformer(object : ClassFileTransformer {
                override fun transform(loader: ClassLoader?, className: String?, type: Class<*>?, domain: ProtectionDomain?, bytes: ByteArray): ByteArray? {
                    if (className != "net/minecraftforge/common/capabilities/CapabilityToken" &&
                        className?.startsWith("net/minecraftforge/common/capabilities/ForgeCapabilities$") != true) return null
                    val reader = ClassReader(bytes)
                    val node = ClassNode()
                    reader.accept(node, 0)
                    val flags = forge.processClassWithFlags(ILaunchPluginService.Phase.AFTER, node, Type.getObjectType(className), "foundation-sdk-checks")
                    if (flags == 0) return null
                    val writer = ClassWriter(reader, ClassWriter.COMPUTE_MAXS)
                    node.accept(writer)
                    transformed.incrementAndGet()
                    System.setProperty("foundation.checks.capabilityTransformations", transformed.get().toString())
                    return writer.toByteArray()
                }
            })
        }
    }
}
