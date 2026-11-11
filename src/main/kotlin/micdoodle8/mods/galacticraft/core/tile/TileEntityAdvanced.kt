package micdoodle8.mods.galacticraft.core.tile

import micdoodle8.mods.miccore.Annotations.NetworkedField
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint

import io.netty.buffer.ByteBuf
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.network.IPacketReceiver
import micdoodle8.mods.galacticraft.core.network.NetworkUtil
import micdoodle8.mods.galacticraft.core.network.PacketDynamic
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.miccore.Annotations
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.util.ITickable
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.LogicalSide as Side
import java.lang.Exception
import java.lang.reflect.Field
import java.util.ArrayList
import java.util.HashMap
import java.util.LinkedHashSet

abstract class TileEntityAdvanced : TileEntity(), IPacketReceiver, ITickable {
    @JvmField
    var ticks: Int = 0
    private var fieldCacheClient: LinkedHashSet<Field>? = null
    private var fieldCacheServer: LinkedHashSet<Field>? = null
    private val lastSentData: MutableMap<Field?, Any?> = HashMap<Field?, Any?>(4, 1f)
    private var networkDataChanged = false

    override fun update() {
        if (this.ticks == 0) {
            this.initiate()

            if (this.isNetworkedTile()) {
                if (this.fieldCacheClient == null || this.fieldCacheServer == null) {
                    this.initFieldCache()
                }

                if (this.world != null && this.world.isRemote && this.fieldCacheClient!!.size > 0) {
                    //Request any networked information from server on first client update (maybe client just logged on, but server networkdata didn't change recently)
                    (GalacticraftCore.packetPipeline)!!.sendToServer(PacketDynamic(this))
                }
            }
        }

        this.ticks++

        if (this.isNetworkedTile() && this.ticks % this.getPacketCooldown() == 0) {
            if (this.world.isRemote && this.fieldCacheServer!!.size > 0) {
                val packet: PacketDynamic = PacketDynamic(this)
                if (networkDataChanged) {
                    (GalacticraftCore.packetPipeline)!!.sendToServer(packet)
                }
            } else if (!this.world.isRemote && this.fieldCacheClient!!.size > 0) {
                val packet: PacketDynamic = PacketDynamic(this)
                if (networkDataChanged) {
                    (GalacticraftCore.packetPipeline)!!.sendToAllAround(
                        packet,
                        NetworkRegistry.TargetPoint(
                            GCCoreUtil.getDimensionID(this.world),
                            getPos().getX().toDouble(),
                            getPos().getY().toDouble(),
                            getPos().getZ().toDouble(),
                            this.getPacketRange()
                        )
                    )
                }
            }
        }
    }

    private fun initFieldCache() {
        try {
            this.fieldCacheClient = LinkedHashSet<Field>()
            this.fieldCacheServer = LinkedHashSet<Field>()

            for (field: Field in this.javaClass.getFields()) {
                if (field.isAnnotationPresent(Annotations.NetworkedField::class.java)) {
                    val f: Annotations.NetworkedField =
                        field.getAnnotation<Annotations.NetworkedField>(Annotations.NetworkedField::class.java)

                    if (f.targetSide == Side.CLIENT) {
                        this.fieldCacheClient!!.add(field)
                    } else {
                        this.fieldCacheServer!!.add(field)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    abstract fun getPacketRange(): Double

    abstract fun getPacketCooldown(): Int

    abstract fun isNetworkedTile(): Boolean

    open fun addExtraNetworkedData(networkedList: MutableList<Any?>?) {
    }

    open fun readExtraNetworkedData(dataStream: ByteBuf?) {
    }

    fun initiate() {
    }

    override fun getNetworkedData(sendData: ArrayList<Any?>?) {
        var fieldList: MutableSet<Field>? = null
        var changed: Boolean = false

        if (this.fieldCacheClient == null || this.fieldCacheServer == null) {
            this.initFieldCache()
        }

        if (this.world.isRemote) {
            fieldList = this.fieldCacheServer
        } else {
            fieldList = this.fieldCacheClient
        }

        for (f: Field in fieldList!!) {
            var fieldChanged: Boolean = false
            try {
                val data: Any? = f.get(this)
                val lastData: Any? = lastSentData.get(f)

                if (!NetworkUtil.fuzzyEquals(lastData, data)) {
                    fieldChanged = true
                }

                (sendData)!!.add(data)

                if (fieldChanged) {
                    lastSentData.put(f, NetworkUtil.cloneNetworkedObject(data))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            changed = changed or fieldChanged
        }

        if (changed) {
            this.addExtraNetworkedData(sendData)
        } else {
            val prevSendData: ArrayList<Any?> = ArrayList<Any?>(sendData)

            this.addExtraNetworkedData(sendData)

            if (prevSendData != sendData) {
                changed = true
            }
        }

        networkDataChanged = changed
    }

    override fun decodePacketdata(buffer: ByteBuf?) {
        if (this.world == null) {
            GCLog.severe("World is NULL! Connot decode packet data!")
            return
        }

        if (this.fieldCacheClient == null || this.fieldCacheServer == null) {
            this.initFieldCache()
        }

        //        if (this.world.isRemote && this.fieldCacheClient.size() == 0)
//        {
//            return;
//        }
//        else if (!this.world.isRemote && this.fieldCacheServer.size() == 0)
//        {
//            return;
//        }
        var fieldSet: MutableSet<Field>? = null

        if (this.world.isRemote) {
            fieldSet = this.fieldCacheClient
        } else {
            fieldSet = this.fieldCacheServer
        }

        for (field: Field in fieldSet!!) {
            try {
                val obj: Any? = NetworkUtil.getFieldValueFromStream(field, (buffer)!!, this.world)
                field.set(this, obj)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        this.readExtraNetworkedData(buffer)
    }

    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState): Boolean {
        return oldState.getBlock() !== newSate.getBlock()
    }
}
