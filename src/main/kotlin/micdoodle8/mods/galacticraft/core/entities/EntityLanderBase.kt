package micdoodle8.mods.galacticraft.core.entities

import java.util.UUID
import io.netty.buffer.ByteBuf
import micdoodle8.mods.galacticraft.core.GCFluids
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.inventory.IInventorySettable
import micdoodle8.mods.galacticraft.core.network.PacketDynamic
import micdoodle8.mods.galacticraft.core.network.PacketDynamicInventory
import micdoodle8.mods.galacticraft.core.util.FluidUtil
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.DamageSource
import net.minecraft.util.NonNullList
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.lang.Exception
import java.util.ArrayList

abstract class EntityLanderBase(var1: World?) : EntityAdvancedMotion(var1), IInventorySettable, IScaleableFuelLevel {
    private val FUEL_TANK_CAPACITY = 5000

    @JvmField
    var fuelTank: FluidTank = FluidTank(this.FUEL_TANK_CAPACITY)

    @JvmField
    protected var hasReceivedPacket: Boolean = false
    private var lastShouldMove = false
    private var persistantRiderUUID: UUID? = null
    private var shouldMoveClient: Boolean? = null
    private var shouldMoveServer: Boolean? = null
    private var prevData: ArrayList<*>? = null
    private var networkDataChanged = false
    private var syncAdjustFlag = true

    init {
        this.setSize(3.0f, 3.0f)
    }

    override fun updatePassenger(passenger: Entity) {
        if (this.isPassenger(passenger)) {
            passenger.setPosition(this.posX, this.posY + this.getMountedYOffset() + passenger.getYOffset(), this.posZ)
        }
    }

    override fun shouldSendAdvancedMotionPacket(): Boolean {
        return this.shouldMoveClient != null && this.shouldMoveServer != null
    }

    override fun canSetPositionClient(): Boolean {
        return this.shouldSendAdvancedMotionPacket()
    }

    @SideOnly(Side.CLIENT)
    override fun setPositionAndRotationDirect(
        x: Double,
        y: Double,
        z: Double,
        yaw: Float,
        pitch: Float,
        posRotationIncrements: Int,
        b: Boolean
    ) {
        super.setPositionAndRotationDirect(x, y, z, yaw, pitch, posRotationIncrements, b)
        if (this.syncAdjustFlag && this.world.isBlockLoaded(BlockPos(x, 255.0, z))) {
            val p: EntityPlayer = FMLClientHandler.instance().getClientPlayerEntity()
            val dx: Double = x - p.posX
            val dz: Double = z - p.posZ
            if (dx * dx + dz * dz < 1024) {
                if (!this.world.loadedEntityList.contains(this)) {
                    try {
                        this.world.loadedEntityList.add(this)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                this.posX = x
                this.posY = y
                this.posZ = z

                val cx: Int = MathHelper.floor(x / 16.0)
                val cz: Int = MathHelper.floor(z / 16.0)

                if (!this.addedToChunk || this.chunkCoordX != cx || this.chunkCoordZ != cz) {
                    if (this.addedToChunk && this.world.isBlockLoaded(
                            BlockPos(
                                this.chunkCoordX shl 4,
                                255,
                                this.chunkCoordZ shl 4
                            ), true
                        )
                    ) {
                        this.world.getChunk(this.chunkCoordX, this.chunkCoordZ)
                            .removeEntityAtIndex(this, this.chunkCoordY)
                    }

                    this.addedToChunk = true
                    this.world.getChunk(cx, cz).addEntity(this)
                }

                this.syncAdjustFlag = false
            }
        }
    }

    override fun getScaledFuelLevel(i: Int): Int {
        val fuelLevel: Double =
            (if (this.fuelTank.getFluid() == null) 0 else this.fuelTank.getFluid()!!.amount).toDouble()

        return (fuelLevel * i / this.FUEL_TANK_CAPACITY).toInt()
    }

    constructor(var1: World?, var2: Double, var4: Double, var6: Double, yOffset: Float) : this(var1) {
        this.setPosition(var2, var4, var6)
    }

    constructor(player: EntityPlayerMP, yOffset: Float) : this(
        player.world,
        player.posX,
        player.posY,
        player.posZ,
        yOffset
    ) {
        val stats: GCPlayerStats = (GCPlayerStats.Companion.get(player))!!
        this.stacks = NonNullList.withSize<ItemStack>((stats.getRocketStacks())!!.size + 1, ItemStack.EMPTY)
        this.fuelTank.setFluid(FluidStack(GCFluids.fluidFuel, stats.getFuelLevel()))

        for (i: Int in (stats.getRocketStacks())!!.indices) {
            if (!(stats.getRocketStacks())!!.get(i).isEmpty()) {
                this.stacks.set(i, (stats.getRocketStacks())!!.get(i).copy())
            } else {
                this.stacks.get(i).setCount(0)
            }
        }

        this.setPositionAndRotation(player.posX, player.posY, player.posZ, 0f, 0f)

        player.startRiding(this, true)
    }

    override fun onUpdate() {
        super.onUpdate()

        if (this.ticks < 40 && this.posY > 150) {
            if (this.getPassengers().isEmpty()) {
                val player: EntityPlayer? = this.world.getClosestPlayerToEntity(this, 5.0)

                if (player != null && player.getRidingEntity() == null) {
                    player.startRiding(this)
                }
            }
        }

        if (!this.world.isRemote) {
            this.checkFluidTankTransfer(this.stacks.size - 1, this.fuelTank)
        }

        val box: AxisAlignedBB = this.getEntityBoundingBox().grow(0.2, 0.4, 0.2)

        val var15: MutableList<Entity> = this.world.getEntitiesWithinAABBExcludingEntity(this, box)

        if (var15 != null && !var15.isEmpty()) {
            for (entity: Entity in var15) {
                if (!this.getPassengers().contains(entity)) {
                    this.pushEntityAway(entity)
                }
            }
        }
    }


    private fun checkFluidTankTransfer(slot: Int, tank: FluidTank) {
        FluidUtil.tryFillContainerFuel(tank, this.stacks, slot)
    }

    private fun pushEntityAway(entityToPush: Entity) {
        if (!this.getPassengers().contains(entityToPush) && this.getRidingEntity() !== entityToPush) {
            var d0: Double = this.posX - entityToPush.posX
            var d1: Double = this.posZ - entityToPush.posZ
            var d2: Double = MathHelper.absMax(d0, d1)

            if (d2 >= 0.009999999776482582) {
                d2 = MathHelper.sqrt(d2).toDouble()
                d0 /= d2
                d1 /= d2
                var d3: Double = 1.0 / d2

                if (d3 > 1.0) {
                    d3 = 1.0
                }

                d0 *= d3
                d1 *= d3
                d0 *= 0.05000000074505806
                d1 *= 0.05000000074505806
                d0 *= (1.0f - entityToPush.entityCollisionReduction).toDouble()
                d1 *= (1.0f - entityToPush.entityCollisionReduction).toDouble()
                entityToPush.addVelocity(-d0, 0.0, -d1)
            }
        }
    }

    override fun readEntityFromNBT(nbt: NBTTagCompound) {
        var invSize: Int = nbt.getInteger("rocketStacksLength")
        if (invSize < 3) {
            invSize = 3
        }
        this.stacks = NonNullList.withSize<ItemStack>(invSize, ItemStack.EMPTY)
        ItemStackHelper.loadAllItems(nbt, this.stacks)

        if (nbt.hasKey("fuelTank")) {
            this.fuelTank.readFromNBT(nbt.getCompoundTag("fuelTank"))
        }

        if (nbt.hasKey("RiderUUID_LSB")) {
            this.persistantRiderUUID = UUID(nbt.getLong("RiderUUID_LSB"), nbt.getLong("RiderUUID_MSB"))
        }
    }

    override fun writeEntityToNBT(nbt: NBTTagCompound) {
        if (world.isRemote) return
        nbt.setInteger("rocketStacksLength", this.stacks.size)

        ItemStackHelper.saveAllItems(nbt, this.stacks)

        if (this.fuelTank.getFluid() != null) {
            nbt.setTag("fuelTank", this.fuelTank.writeToNBT(NBTTagCompound()))
        }

        val id: UUID? = this.getOwnerUUID()

        if (id != null) {
            nbt.setLong("RiderUUID_LSB", id.getLeastSignificantBits())
            nbt.setLong("RiderUUID_MSB", id.getMostSignificantBits())
        }
    }

    override fun shouldMove(): Boolean {
        if (this.shouldMoveClient == null || this.shouldMoveServer == null) {
            return false
        }

        if (this.ticks < 40) {
            return false
        }

        return !this.onGround
    }

    abstract fun getInitialMotionY(): Double

    override fun tickInAir() {
        if (this.world.isRemote) {
            if (!this.shouldMove()) {
                this.motionZ = 0.0
                this.motionX = this.motionZ
                this.motionY = this.motionX
            }

            if (this.shouldMove() && !this.lastShouldMove) {
                this.motionY = this.getInitialMotionY()
            }

            this.lastShouldMove = this.shouldMove()
        }
    }

    override fun getNetworkedData(): ArrayList<Any?> {
        val objList: ArrayList<Any?> = ArrayList<Any?>()

        if (!this.world.isRemote) {
            val cargoLength: Int = if (this.stacks != null) this.stacks.size else 0
            objList.add(cargoLength)
            objList.add(if (this.fuelTank.getFluid() == null) 0 else this.fuelTank.getFluid()!!.amount)
        }

        if (this.world.isRemote) {
            this.shouldMoveClient = this.shouldMove()
            objList.add(this.shouldMoveClient)
        } else {
            this.shouldMoveServer = this.shouldMove()
            objList.add(this.shouldMoveServer)
            //Server send rider information for client to check
            objList.add(if (this.getPassengers().isEmpty()) -1 else this.getPassengers().get(0).getEntityId())
        }

        this.networkDataChanged = objList != this.prevData
        this.prevData = objList
        return objList
    }

    override fun networkedDataChanged(): Boolean {
        return this.networkDataChanged || this.shouldMoveClient == null || this.shouldMoveServer == null
    }

    override fun canRiderInteract(): Boolean {
        return true
    }

    override fun getPacketTickSpacing(): Int {
        return 2
    }

    override fun getPacketSendDistance(): Double {
        return 250.0
    }

    override fun readNetworkedData(buffer: ByteBuf?) {
        try {
            if (this.world.isRemote) {
                if (!this.hasReceivedPacket) {
                    (GalacticraftCore.packetPipeline)!!.sendToServer(PacketDynamic(this))
                    this.hasReceivedPacket = true
                }

                val cargoLength: Int = (buffer)!!.readInt()
                if (this.stacks == null || this.stacks.isEmpty()) {
                    this.stacks = NonNullList.withSize<ItemStack>(cargoLength, ItemStack.EMPTY)
                    (GalacticraftCore.packetPipeline)!!.sendToServer(PacketDynamicInventory(this))
                }

                this.fuelTank.setFluid(FluidStack(GCFluids.fluidFuel, (buffer)!!.readInt()))

                this.shouldMoveServer = (buffer)!!.readBoolean()

                //Check has correct rider on client
                val shouldBeMountedId: Int = (buffer)!!.readInt()
                if (this.getPassengers().isEmpty()) {
                    if (shouldBeMountedId > -1) {
                        var e: Entity? = FMLClientHandler.instance().getWorldClient().getEntityByID(shouldBeMountedId)
                        if (e != null) {
                            if (e.dimension != this.dimension) {
                                if (e is EntityPlayer) {
                                    e = WorldUtil.forceRespawnClient(
                                        this.dimension,
                                        e.world.getDifficulty().getId(),
                                        e.world.getWorldInfo().getTerrainType().getName(),
                                        (e as EntityPlayerMP).interactionManager.getGameType().getID()
                                    )
                                    (e)!!.startRiding(this)
                                    this.syncAdjustFlag = true
                                }
                            } else {
                                e.startRiding(this)
                                this.syncAdjustFlag = true
                            }
                        }
                    }
                } else if (this.getPassengers().get(0).getEntityId() != shouldBeMountedId) {
                    if (shouldBeMountedId == -1) {
                        this.removePassengers()
                    } else {
                        var e: Entity? = FMLClientHandler.instance().getWorldClient().getEntityByID(shouldBeMountedId)
                        if (e != null) {
                            if (e.dimension != this.dimension) {
                                if (e is EntityPlayer) {
                                    e = WorldUtil.forceRespawnClient(
                                        this.dimension,
                                        e.world.getDifficulty().getId(),
                                        e.world.getWorldInfo().getTerrainType().getName(),
                                        (e as EntityPlayerMP).interactionManager.getGameType().getID()
                                    )
                                    (e)!!.startRiding(this, true)
                                    this.syncAdjustFlag = true
                                }
                            } else {
                                e.startRiding(this, true)
                                this.syncAdjustFlag = true
                            }
                        }
                    }
                }
            } else {
                this.shouldMoveClient = (buffer)!!.readBoolean()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun allowDamageSource(damageSource: DamageSource?): Boolean {
        return !(damageSource)!!.isExplosion()
    }

    override fun getItemsDropped(): MutableList<ItemStack?>? {
        return this.stacks
    }

    override fun getSizeInventory(): Int {
        return this.stacks.size
    }

    override fun setSizeInventory(size: Int) {
        this.stacks = NonNullList.withSize<ItemStack>(size, ItemStack.EMPTY)
    }

    override fun isItemValidForSlot(var1: Int, var2: ItemStack): Boolean {
        return false
    }

    override fun getPacketRange(): Double {
        return 50.0
    }

    override fun getOwnerUUID(): UUID? {
        if (!this.getPassengers().isEmpty() && this.getPassengers().get(0) !is EntityPlayer) {
            return null
        }

        val id: UUID

        if (!this.getPassengers().isEmpty()) {
            id = this.getPassengers().get(0).getPersistentID()

            this.persistantRiderUUID = id
        } else {
            id = this.persistantRiderUUID!!
        }

        return id
    }

    @SideOnly(Side.CLIENT)
    override fun getBrightnessForRender(): Int {
        var height: Double = this.posY + this.getEyeHeight().toDouble()
        if (height > 255.0) height = 255.0
        val blockpos: BlockPos = BlockPos(this.posX, height, this.posZ)
        return if (this.world.isBlockLoaded(blockpos)) this.world.getCombinedLight(blockpos, 0) else 0
    }
}
