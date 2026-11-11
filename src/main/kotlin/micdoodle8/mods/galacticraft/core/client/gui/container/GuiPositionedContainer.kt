package micdoodle8.mods.galacticraft.core.client.gui.container

import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.core.BlockPos

abstract class GuiPositionedContainer(container: Container?, pos: BlockPos) : GuiContainerGC((container)!!) {
    private val x: Int
    private val y: Int
    private val z: Int

    init {
        this.x = pos.getX()
        this.y = pos.getY()
        this.z = pos.getZ()
    }

    fun getX(): Int {
        return this.x
    }

    fun getY(): Int {
        return this.y
    }

    fun getZ(): Int {
        return this.z
    }
}
