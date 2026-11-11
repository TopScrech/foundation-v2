package micdoodle8.mods.galacticraft.core.entities

import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint

import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.network.IPacketReceiver
import micdoodle8.mods.galacticraft.core.network.PacketDynamic
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level as World
import net.minecraftforge.fml.common.network.NetworkRegistry

abstract class NetworkedEntity(par1World: World) : Entity(par1World), IPacketReceiver {
    init {
        if (par1World != null && par1World.isClientSide) {
            (GalacticraftCore.packetPipeline)!!.sendToServer(PacketDynamic(this))
        }
    }

    override fun onUpdate() {
        super.onUpdate()

        val packet: PacketDynamic = PacketDynamic(this)
        if (this.networkedDataChanged()) {
            if (!this.world.isRemote) {
                (GalacticraftCore.packetPipeline)!!.sendToAllAround(
                    packet,
                    NetworkRegistry.TargetPoint(
                        GCCoreUtil.getDimensionID(this.world),
                        this.posX,
                        this.posY,
                        this.posZ,
                        this.getPacketRange()
                    )
                )
            } else {
                (GalacticraftCore.packetPipeline)!!.sendToServer(packet)
            }
        }
    }

    abstract fun networkedDataChanged(): Boolean

    abstract fun getPacketRange(): Double
}
