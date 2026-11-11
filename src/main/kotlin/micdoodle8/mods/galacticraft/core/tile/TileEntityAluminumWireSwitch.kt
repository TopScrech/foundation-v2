package micdoodle8.mods.galacticraft.core.tile
import micdoodle8.mods.galacticraft.api.transmission.grid.mergeUntyped

import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.transmission.grid.IElectricityNetwork
import micdoodle8.mods.galacticraft.api.transmission.grid.IGridNetwork
import micdoodle8.mods.galacticraft.api.transmission.tile.IConnector
import micdoodle8.mods.galacticraft.api.transmission.tile.INetworkProvider
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.core.energy.grid.EnergyNetwork
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseConductor
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseUniversalConductor
import micdoodle8.mods.galacticraft.core.util.RedstoneUtil
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing

open class TileEntityAluminumWireSwitch @JvmOverloads constructor(@JvmField var tier: Int = 1) :
    TileBaseUniversalConductor() {
    private var disableConnections = false

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        this.tier = nbt.getIntOr("tier", 0)
        //For legacy worlds (e.g. converted from 1.6.4)
        if (this.tier == 0) {
            this.tier = 1
        }
        this.disableConnections = this.disableConnections()
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        nbt.putInt("tier", this.tier)
        return nbt
    }

    override fun getUpdateTag(): NBTTagCompound {
        return this.writeToNBT(NBTTagCompound())
    }

    override fun getTierGC(): Int {
        return this.tier
    }

    override fun refresh() {
        val newDisableConnections: Boolean = this.disableConnections()
        if (newDisableConnections && !this.disableConnections) {
            this.disableConnections = newDisableConnections
            if (!this.world.isRemote) {
                this.disConnect()
            }
        } else if (!newDisableConnections && this.disableConnections) {
            this.disableConnections = newDisableConnections
            if (!this.world.isRemote) {
                this.setNetwork(null) //Force a full network refresh of this and conductors either side
            }
        }

        if (!this.world.isRemote) {
            this.adjacentConnections = null
            if (!this.disableConnections) {
                this.getNetwork()!!.refresh()

                val thisVec: BlockVec3 = BlockVec3(this)
                for (side: EnumFacing in EnumFacing.values()) {
                    if (this.canConnect(side, NetworkType.POWER)) {
                        val tileEntity: TileEntity? = thisVec.getTileEntityOnSide(this.world, side)

                        if (tileEntity is TileBaseConductor && tileEntity.canConnect(
                                side.getOpposite(),
                                NetworkType.POWER
                            )
                        ) {
                            val otherNet: IGridNetwork<*, *, *>? = (tileEntity as INetworkProvider).getNetwork()
                            if (this.getNetwork() != otherNet) {
                                if (!(otherNet!!.getTransmitters())!!.isEmpty()) {
                                    otherNet.mergeUntyped(this.getNetwork())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun disConnect() {
        val net: EnergyNetwork? = this.getNetwork() as EnergyNetwork?
        if (net != null) {
            net.split(this)
        }
    }

    private fun disableConnections(): Boolean {
        return RedstoneUtil.isBlockReceivingRedstone(this.world, this.pos)
    }

    override fun canConnect(direction: EnumFacing?, type: NetworkType?): Boolean {
        return type == NetworkType.POWER && !this.disableConnections()
    }

    override fun getNetwork(): IElectricityNetwork? {
        if (this.network == null) {
            val network: EnergyNetwork = EnergyNetwork()
            if (!this.disableConnections) network.getTransmitters().add(this)
            this.setNetwork(network)
        }

        return this.network as IElectricityNetwork?
    }

    override fun getAdjacentConnections(): Array<TileEntity?> {
        if (this.adjacentConnections == null) {
            this.adjacentConnections = arrayOfNulls<TileEntity>(6)

            if (!this.disableConnections) {
                val thisVec: BlockVec3 = BlockVec3(this)
                for (i: Int in 0..5) {
                    val side: EnumFacing = EnumFacing.from3DDataValue(i)
                    if (this.canConnect(side, NetworkType.POWER)) {
                        val tileEntity: TileEntity? = thisVec.getTileEntityOnSide(this.world, side)

                        if (tileEntity is IConnector) {
                            if ((tileEntity as IConnector).canConnect(side.getOpposite(), NetworkType.POWER)) {
                                (this.adjacentConnections)!![i] = tileEntity
                            }
                        }
                    }
                }
            }
        }

        return (this.adjacentConnections)!!
    }

    //RF
    override fun receiveEnergy(from: EnumFacing?, maxReceive: Int, simulate: Boolean): Int {
        return if (this.disableConnections) 0 else super.receiveEnergy(from, maxReceive, simulate)
    }

    //RF
    override fun canConnectEnergy(from: EnumFacing?): Boolean {
        return if (this.disableConnections()) false else super.canConnectEnergy(from)
    }
}
