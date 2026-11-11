package micdoodle8.mods.galacticraft.core.tile

import micdoodle8.mods.miccore.Annotations.NetworkedField

import io.netty.buffer.ByteBuf
import micdoodle8.mods.miccore.Annotations
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraftforge.fml.LogicalSide as Side

open class TileEntityFallenMeteor : TileEntityAdvanced() {
    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var heatLevel: Int = MAX_HEAT_LEVEL
    private var sentOnePacket = false

    override fun update() {
        super.update()

        if (!this.world.isRemote && this.heatLevel > 0) {
            this.heatLevel--
        }
    }

    open fun getHeatLevel(): Int {
        return this.heatLevel
    }

    open fun setHeatLevel(heatLevel: Int) {
        this.heatLevel = heatLevel
    }

    open fun getScaledHeatLevel(): Float {
        return this.heatLevel.toFloat() / MAX_HEAT_LEVEL
    }

    override fun readExtraNetworkedData(dataStream: ByteBuf?) {
        if (this.world.isRemote) {
            this.world.notifyLightSet(this.getPos())
        }
    }

    override fun addExtraNetworkedData(networkedList: MutableList<Any?>?) {
        this.sentOnePacket = true
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        this.heatLevel = nbt.getIntOr("MeteorHeatLevel", 0)
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        nbt.putInt("MeteorHeatLevel", this.heatLevel)
        return nbt
    }

    override fun getUpdateTag(): NBTTagCompound {
        return this.writeToNBT(NBTTagCompound())
    }

    override fun getPacketRange(): Double {
        return 50.0
    }

    override fun getPacketCooldown(): Int {
        return if (this.sentOnePacket) 100 else 1
    }

    override fun isNetworkedTile(): Boolean {
        return true
    }

    companion object {
        const val MAX_HEAT_LEVEL: Int = 5000
    }
}
