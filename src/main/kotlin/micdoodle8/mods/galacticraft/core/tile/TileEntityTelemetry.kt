package micdoodle8.mods.galacticraft.core.tile

import foundation.item.FoundationFrequencyModules
import foundation.persistence.containsTag

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint

import com.mojang.authlib.GameProfile
import java.util.UUID
import micdoodle8.mods.galacticraft.api.entity.ITelemetry
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.api.vector.BlockVec3Dim
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.entity.monster.EntitySkeleton
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.entity.passive.EntityHorse
import net.minecraft.entity.passive.EntityOcelot
import net.minecraft.entity.passive.EntitySheep
import net.minecraft.entity.passive.EntityVillager
import net.minecraft.entity.passive.EntityWolf
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.util.ITickable
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.Level as World
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.registry.EntityEntry
import net.minecraftforge.fml.common.registry.EntityRegistry
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.HashSet
import kotlin.math.max

open class TileEntityTelemetry : TileEntity(), ITickable {
    @JvmField
    var clientClass: Class<*>? = null

    @JvmField
    var clientData: IntArray = intArrayOf(-1)

    @JvmField
    var clientName: String? = null

    @JvmField
    var clientGameProfile: GameProfile? = null

    @JvmField
    var linkedEntity: Entity? = null
    private var toUpdate: UUID? = null
    private var pulseRate = 400
    private var lastHurttime = 0
    private var ticks = 0

    override fun onLoad() {
        if (this.world.isRemote) {
            loadedList.add(BlockVec3Dim(this))
        }
    }

    override fun invalidate() {
        super.invalidate()
        if (this.world.isRemote) {
            loadedList.remove(BlockVec3Dim(this))
        }
    }

    override fun update() {
        if (!this.world.isRemote && ++this.ticks % 2 == 0) {
            if (this.toUpdate != null) {
                this.addTrackedEntity(this.toUpdate)
                this.toUpdate = null
            }

            var name: String? = null
            val data: IntArray = intArrayOf(-1, -1, -1, -1, -1)
            var strUUID: String = ""

            if (linkedEntity != null) {
                //Help the Garbage Collector
                if (linkedEntity!!.isRemoved()) {
                    linkedEntity = null
                    name = ""
                    //TODO: track players after death and respawn? or not?
                } else {
                    if (linkedEntity is EntityPlayerMP) {
                        name = "$" + linkedEntity!!.getName()
                    } else {
                        val entityEntry: EntityEntry? = EntityRegistry.getEntry(((linkedEntity)!!.javaClass)!!)
                        if (entityEntry != null && entityEntry.getRegistryName() != null) {
                            name = entityEntry.getRegistryName().toString()
                        }
                    }

                    if (name == null) {
                        GCLog.info("Telemetry Unit: Error finding name for " + ((linkedEntity)!!.javaClass.getSimpleName())!!)
                        name = ""
                    }

                    val xmotion: Double = linkedEntity!!.deltaMovement.x
                    val ymotion: Double =
                        if (linkedEntity is EntityLivingBase) linkedEntity!!.deltaMovement.y + 0.078 else linkedEntity!!.deltaMovement.y
                    val zmotion: Double = linkedEntity!!.deltaMovement.z
                    data[2] =
                        (MathHelper.sqrt(xmotion * xmotion + ymotion * ymotion + zmotion * zmotion) * 2000.0).toInt()

                    if (linkedEntity is ITelemetry) {
                        (linkedEntity as ITelemetry).transmitData(data)
                    } else if (linkedEntity is EntityLivingBase) {
                        val eLiving: EntityLivingBase = linkedEntity as EntityLivingBase
                        data[0] = eLiving.hurtTime

                        //Calculate a "pulse rate" based on motion and taking damage
                        this.pulseRate--
                        if (eLiving.hurtTime > this.lastHurttime) {
                            this.pulseRate += 100
                        }
                        this.lastHurttime = eLiving.hurtTime
                        if (eLiving.getVehicle() != null) {
                            data[2] /= 4 //reduced pulse effect if riding a vehicle
                        } else if (data[2] > 1) {
                            this.pulseRate += 2
                        }
                        this.pulseRate += max(data[2] - pulseRate, 0) / 4
                        if (this.pulseRate > 2000) {
                            this.pulseRate = 2000
                        }
                        if (this.pulseRate < 400) {
                            this.pulseRate = 400
                        }
                        data[2] = this.pulseRate / 10

                        data[1] = (eLiving.getHealth() * 100 / eLiving.getMaxHealth()).toInt()
                        if (eLiving is EntityPlayerMP) {
                            data[3] = eLiving.getFoodStats().getFoodLevel() * 5
                            val stats: GCPlayerStats = (GCPlayerStats.get(eLiving))!!
                            data[4] = stats.getAirRemaining() * 4096 + stats.getAirRemaining2()
                            val uuid: UUID = eLiving.getUUID()
                            if (uuid != null) {
                                strUUID = uuid.toString()
                            }
                        } else if (eLiving is EntityHorse) {
//                            data[3] = ((EntityHorse) eLiving).getType().ordinal();
                            data[4] = eLiving.getHorseVariant()
                        } else if (eLiving is EntityVillager) {
                            data[3] = eLiving.getProfession()
                            data[4] = eLiving.getGrowingAge()
                        } else if (eLiving is EntityWolf) {
                            data[3] = eLiving.getCollarColor().getDyeDamage()
                            data[4] = if (eLiving.isBegging()) 1 else 0
                        } else if (eLiving is EntitySheep) {
                            data[3] = eLiving.getFleeceColor().getDyeDamage()
                            data[4] = if (eLiving.getSheared()) 1 else 0
                        } else if (eLiving is EntityOcelot) {
                            data[3] = eLiving.getTameSkin()
                        } else if (eLiving is EntitySkeleton) {
//                            data[3] = ((EntitySkeleton) eLiving).getSkeletonType().ordinal();
                        } else if (eLiving is EntityZombie) {
//                            data[3] = ((EntityZombie) eLiving).isVillager() ? 1 : 0; TODO Fix for MC 1.10
                            data[4] = if (eLiving.isChild()) 1 else 0
                        }
                    }
                }
            } else {
                name = ""
            }
            (GalacticraftCore.packetPipeline)!!.sendToAllAround(
                PacketSimple(
                    PacketSimple.EnumSimplePacket.C_UPDATE_TELEMETRY,
                    this.world.provider.getDimension(),
                    arrayOf<Any>(this.getPos(), name, data[0], data[1], data[2], data[3], data[4], strUUID)
                ),
                NetworkRegistry.TargetPoint(
                    this.world.provider.getDimension(),
                    this.getPos().getX().toDouble(),
                    this.getPos().getY().toDouble(),
                    this.getPos().getZ().toDouble(),
                    320.0
                )
            )
        }
    }

    @OnlyIn(Dist.CLIENT)
    open fun receiveUpdate(data: MutableList<Any?>, dimID: Int) {
        val name: String = data.get(1) as String
        if (name.startsWith("$")) {
            //It's a player name
            this.clientClass = EntityPlayerMP::class.java
            val strName: String = name.substring(1)
            this.clientName = strName
            this.clientGameProfile = PlayerUtil.getSkinForName(strName, (data.get(7) as String?)!!, dimID)
        } else {
            val entityEntry: EntityEntry? = ForgeRegistries.ENTITIES.getValue(ResourceLocation.parse(name))
            this.clientClass = if (entityEntry == null) null else entityEntry.getEntityClass()
        }
        this.clientData = IntArray(5)
        for (i: Int in 2..6) {
            this.clientData[i - 2] = (data.get(i) as kotlin.Int?)!!
        }
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        val msb: Long = nbt.getLongOr("entityUUIDMost", 0L)
        val lsb: Long = nbt.getLongOr("entityUUIDLeast", 0L)
        this.toUpdate = UUID(msb, lsb)
    }


    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        if (this.linkedEntity != null && !this.linkedEntity!!.isRemoved()) {
            nbt.putLong("entityUUIDMost", this.linkedEntity!!.getUUID().getMostSignificantBits())
            nbt.putLong("entityUUIDLeast", this.linkedEntity!!.getUUID().getLeastSignificantBits())
        }
        return nbt
    }

    open fun addTrackedEntity(uuid: UUID?) {
        this.pulseRate = 400
        this.lastHurttime = 0
        val eList: MutableList<Entity> = this.world.loadedEntityList
        for (e: Entity in eList) {
            if (e.getUUID() == uuid) {
                this.linkedEntity = e
                if (e is EntitySpaceshipBase) {
                    e.addTelemetry(this)
                }
                return
            }
        }
        //TODO Add some kind of watcher to add the entity when next loaded
        this.linkedEntity = null
    }

    open fun addTrackedEntity(e: Entity?) {
        this.pulseRate = 400
        this.lastHurttime = 0
        this.linkedEntity = e
        if (e is EntitySpaceshipBase) {
            e.addTelemetry(this)
        }
    }

    open fun removeTrackedEntity() {
        this.pulseRate = 400
        this.linkedEntity = null
    }

    override fun shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newSate: IBlockState): Boolean {
        return oldState.getBlock() !== newSate.getBlock()
    }

    companion object {
        @JvmField
        var loadedList: HashSet<BlockVec3Dim> = HashSet<BlockVec3Dim>()

        @JvmStatic
        fun getNearest(te: TileEntity?): TileEntityTelemetry? {
            if (te == null) {
                return null
            }
            val target: BlockVec3 = BlockVec3(te)

            var distSq: Int = 1025
            var nearest: BlockVec3Dim? = null
            val dim: Int = GCCoreUtil.getDimensionID(te.getLevel())
            for (telemeter: BlockVec3Dim in loadedList) {
                if (telemeter.dim != dim) {
                    continue
                }
                val dist: Int = telemeter.distanceSquared(target)
                if (dist < distSq) {
                    distSq = dist
                    nearest = telemeter
                }
            }

            if (nearest == null) {
                return null
            }
            val result: TileEntity? = te.getLevel().getBlockEntity(BlockPos(nearest.x, nearest.y, nearest.z))
            if (result is TileEntityTelemetry) {
                return result
            }
            return null
        }

        /**
         * Call this when a player wears a frequency module to check
         * whether it has been linked with a Telemetry Unit.
         *
         * @param held   The frequency module
         * @param player
         */
        @JvmStatic
        fun frequencyModulePlayer(held: ItemStack?, player: EntityPlayerMP, remove: Boolean) {
            if (held == null) return
            val target = try {
                FoundationFrequencyModules.telemetry(held)
            } catch (cause: IllegalArgumentException) {
                GCLog.debug("Frequency module dimension is unavailable: ${cause.message}")
                return
            } ?: return
            val level = player.level().getServer().getLevel(target.dimension) ?: return
            val tile = level.getBlockEntity(target.position)
            if (tile is TileEntityTelemetry) {
                if (remove) {
                    if (tile.linkedEntity === player) tile.removeTrackedEntity()
                } else tile.addTrackedEntity(player.uuid)
            }
        }

        @JvmStatic
        fun updateLinkedPlayer(playerOld: EntityPlayerMP?, playerNew: EntityPlayerMP?) {
            for (telemeter: BlockVec3Dim in loadedList) {
                val te: TileEntity? = telemeter.getTileEntityNoLoad()
                if (te is TileEntityTelemetry) {
                    if (te.linkedEntity === playerOld) {
                        te.linkedEntity = playerNew
                    }
                }
            }
        }
    }
}
