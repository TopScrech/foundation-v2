import net.minecraft.world.World
import net.minecraft.world.WorldServer
import sun.misc.Unsafe

// Run against the development runtime classpath without starting Minecraft
fun main(decoratorClasses: Array<String>) {
    val field = Unsafe::class.java.getDeclaredField("theUnsafe")
    field.isAccessible = true
    val unsafe = field.get(null) as Unsafe
    val world = unsafe.allocateInstance(WorldServer::class.java)
    check(decoratorClasses.size == 12)
    for (name in decoratorClasses) {
        val type = Class.forName(name)
        val decorator = unsafe.allocateInstance(type)
        val get = type.getDeclaredMethod("getCurrentWorld").apply { isAccessible = true }
        val set = type.getDeclaredMethod("setCurrentWorld", World::class.java).apply { isAccessible = true }
        check(get.invoke(decorator) == null) { "$name rejected the idle state" }
        set.invoke(decorator, world)
        check(get.invoke(decorator) === world) { "$name lost the active world" }
        set.invoke(decorator, null)
        check(get.invoke(decorator) == null) { "$name failed to clear the world" }
    }
    println("Passed: all 12 decorators accepted idle, active and cleared world states")
}
