package micdoodle8.mods.galacticraft.core.entities

import micdoodle8.mods.miccore.Annotations.NetworkedField
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint

import io.netty.buffer.ByteBuf
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.network.IPacketReceiver
import micdoodle8.mods.galacticraft.core.network.NetworkUtil
import micdoodle8.mods.galacticraft.core.network.PacketDynamic
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.miccore.Annotations
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.level.Level as World
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.LogicalSide as Side
import java.lang.reflect.Field

abstract class EntityAdvanced(world: World) : Entity(world), IPacketReceiver {
    @JvmField
    protected var ticks: Long = 0
    private var fieldCacheClient: LinkedHashSet<Field>? = null
    private var fieldCacheServer: LinkedHashSet<Field>? = null
    private val lastSentData: MutableMap<Field?, Any?> = HashMap<Field?, Any?>()
    private var networkDataChanged = false

    init {
        if (world != null && world.isClientSide) {
            //Empty packet client->server just to kickstart the server into sending this client an initial packet
            this.fieldCacheServer = LinkedHashSet<Field>()
            (GalacticraftCore.packetPipeline)!!.sendToServer(PacketDynamic(this))
        }
    }

    /**
     * Whether or not this entity should be sending packets to/from server
     *
     * @return If the entity needs network capabilities
     */
    abstract fun isNetworkedEntity(): Boolean

    /**
     * Get the amount of ticks between each packet send
     *
     * @param side The target side.
     * @return The amount of ticks to wait before sending another packet to this
     * target
     */
    abstract fun getPacketCooldown(side: Side?): Int

    /**
     * Add any additional data to the stream
     * (only effective if there are both CLIENT and SERVER targeted
     * regular networked fields ... currently nothing in GC uses this)
     *
     * @param networkedList List of additional data
     */
    //    public void addExtraNetworkedData(List<Object> networkedList)
    //    {
    //
    //    }
    /**
     * Read any additional data from the stream
     *
     * @param stream The ByteBuf stream to read data from
     */
    //    public void readExtraNetworkedData(ByteBuf stream)
    //    {
    //
    //    }
    /**
     * Called after a packet is read, only on client side.
     *
     * @param player The player associated with the received packet
     */
    abstract fun onPacketClient(player: EntityPlayer?)

    /**
     * Called after a packet is read, only on server side.
     *
     * @param player The player associated with the received packet
     */
    abstract fun onPacketServer(player: EntityPlayer?)

    /**
     * Packets will be sent to all (client-side) players within this range
     *
     * @return Maximum distance to send packets to client players
     */
    abstract fun getPacketRange(): Double

    override fun onUpdate() {
        super.onUpdate()

        this.ticks++

        if (this.isNetworkedEntity()) {
            if (!this.world.isRemote && this.ticks % this.getPacketCooldown(Side.CLIENT) == 0L) {
                if (this.fieldCacheClient == null) {
                    try {
                        this.initFieldCache()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                val packet: PacketDynamic = PacketDynamic(this)
                if (networkDataChanged) {
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
                }
            }

            if (this.world.isRemote && this.ticks % this.getPacketCooldown(Side.SERVER) == 0L) {
                if (this.fieldCacheClient == null)  //The target server cache may have been initialised to an empty set
                {
                    try {
                        this.initFieldCache()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                val packet: PacketDynamic = PacketDynamic(this)
                if (networkDataChanged) {
                    (GalacticraftCore.packetPipeline)!!.sendToServer(packet)
                }
            }
        }
    }

    @Throws(IllegalArgumentException::class, IllegalAccessException::class)
    private fun initFieldCache() {
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
    }

    override fun getNetworkedData(sendData: ArrayList<Any?>?) {
        var fieldList: MutableSet<Field>? = null
        var changed: Boolean = false

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

        //Currently unused as there is no entity in Galacticraft with extraNetworkedData

//        if (changed)
//        {
//            this.addExtraNetworkedData(sendData);
//        }
//        else
//        {
//            ArrayList<Object> prevSendData = new ArrayList<Object>(sendData);
//
//            this.addExtraNetworkedData(sendData);
//
//            if (!prevSendData.equals(sendData))
//            {
//                changed = true;
//            }
//        }
        networkDataChanged = changed
    }

    override fun decodePacketdata(buffer: ByteBuf?) {
        if (this.fieldCacheClient == null || this.fieldCacheServer == null) {
            try {
                this.initFieldCache()
            } catch (e: Exception) {
                e.printStackTrace()
            }
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

        //        this.readExtraNetworkedData(buffer);
    }
}
