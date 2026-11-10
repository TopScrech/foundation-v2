import micdoodle8.mods.galacticraft.core.dimension.SpinManager
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderSpaceStation
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import sun.misc.Unsafe

private class SpinProbe(provider: WorldProviderSpaceStation) : SpinManager(provider) {
    var refreshedBlock: BlockPos? = null
    var packetUpdates = 0

    override fun refresh(baseBlock: BlockPos?, placingThruster: Boolean): Boolean {
        refreshedBlock = baseBlock
        return false
    }

    override fun sendPackets(player: EntityPlayerMP?) {
        packetUpdates++
    }
}

// Exercise save loading without requiring a running world or packet pipeline
fun main() {
    val field = Unsafe::class.java.getDeclaredField("theUnsafe").apply { isAccessible = true }
    val unsafe = field.get(null) as Unsafe
    val manager = unsafe.allocateInstance(SpinProbe::class.java) as SpinProbe
    manager.readFromNBT(NBTTagCompound())
    check(manager.refreshedBlock == null)
    check(manager.angularVelocityRadians == 0f)
    check(manager.packetUpdates == 1)

    val saved = NBTTagCompound()
    val block = NBTTagCompound()
    block.setInteger("x", 4)
    block.setInteger("y", 64)
    block.setInteger("z", -8)
    saved.setTag("oneBlock", block)
    saved.setFloat("omegaRad", 0.025f)
    manager.readFromNBT(saved)
    check(manager.refreshedBlock == BlockPos(4, 64, -8))
    check(manager.angularVelocityRadians == 0.025f)
    val roundTrip = manager.writeToNBT(NBTTagCompound())
    check(roundTrip.getCompoundTag("oneBlock").getInteger("z") == -8)

    manager.readFromNBT(NBTTagCompound())
    check(manager.refreshedBlock == null)
    check(!manager.writeToNBT(NBTTagCompound()).hasKey("oneBlock"))
    check(manager.packetUpdates == 3)
    println("Passed: empty station data, saved rotation and position, round trip, cleared position")
}
