package micdoodle8.mods.galacticraft.api.prefab.entity

import foundation.persistence.containsTag
import foundation.persistence.listOfType

import com.google.common.base.Predicate
import io.netty.buffer.ByteBuf
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.entity.IIgnoreShift
import micdoodle8.mods.galacticraft.api.entity.ITelemetry
import micdoodle8.mods.galacticraft.api.vector.BlockVec3Dim
import micdoodle8.mods.galacticraft.api.world.IExitHeight
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.screen.GameScreenText
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.network.IPacketReceiver
import micdoodle8.mods.galacticraft.core.network.PacketDynamic
import micdoodle8.mods.galacticraft.core.tile.TileEntityTelemetry
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.DamageSourceGC
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.MoverType
import net.minecraft.entity.item.EntityItem
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.nbt.ListTag as NBTTagList
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.Level as World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.EntityEvent
import net.minecraftforge.fluids.FluidTank
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.opengl.GL11
import java.lang.Exception
import java.util.ArrayList
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Do not include this prefab class in your released mod download.
 */
abstract class EntitySpaceshipBase(par1World: World?) : Entity(par1World), IPacketReceiver, IIgnoreShift, ITelemetry {
    enum class EnumLaunchPhase {
        UNIGNITED,
        IGNITED,
        LAUNCHED,
        LANDING
    }

    @JvmField
    var launchPhase: Int

    @JvmField
    protected var ticks: Long = 0

    @JvmField
    protected var dragAir: Double = 0.0

    @JvmField
    var timeUntilLaunch: Int = 0

    @JvmField
    var timeSinceLaunch: Float = 0f

    @JvmField
    var rollAmplitude: Float = 0f

    @JvmField
    var shipDamage: Float = 0f
    private val telemetryList = ArrayList<BlockVec3Dim?>()
    private var addToTelemetry = false

    @JvmField
    var fuelTank: FluidTank = FluidTank(this.getFuelTankCapacity() * ConfigManagerCore.rocketFuelFactor)
    private var syncAdjustX = 0.0
    private var syncAdjustY = 0.0
    private var syncAdjustZ = 0.0
    private var syncAdjustFlag = false

    override fun isInRangeToRenderDist(distance: Double): Boolean {
        var d0: Double = this.getEntityBoundingBox().getAverageEdgeLength()

        if (java.lang.Double.isNaN(d0)) {
            d0 = 1.0
        }

        d0 = d0 * 64.0 * 5.0
        return distance < d0 * d0
    }

    abstract fun getFuelTankCapacity(): Int

    abstract fun getMaxFuel(): Int

    abstract fun getScaledFuelLevel(i: Int): Int

    abstract fun getPreLaunchWait(): Int

    override fun canTriggerWalking(): Boolean {
        return false
    }

    override fun entityInit() {
    }

    override fun getCollisionBox(par1Entity: Entity): AxisAlignedBB? {
        return null
    }

    override fun getCollisionBoundingBox(): AxisAlignedBB? {
        return null
    }

    override fun canBePushed(): Boolean {
        return false
    }

    override fun attackEntityFrom(par1DamageSource: DamageSource, par2: Float): Boolean {
        if (!this.world.isRemote && !this.isDead) {
            val e: Entity? = par1DamageSource.getTrueSource()
            val flag: Boolean = e is EntityPlayer && e.capabilities.isCreativeMode
            if (this.isEntityInvulnerable(par1DamageSource) || this.posY > 300 || (e is EntityLivingBase && e !is EntityPlayer)) {
                return false
            } else {
                this.rollAmplitude = 10f
                this.markVelocityChanged()
                this.shipDamage += par2 * 10

                if (e is EntityPlayer && e.capabilities.isCreativeMode) {
                    this.shipDamage = 100f
                }

                if (flag || this.shipDamage > 90 && !this.world.isRemote) {
                    this.removePassengers()

                    if (flag) {
                        this.setDead()
                    } else {
                        this.setDead()
                        this.dropShipAsItem()
                    }
                    return true
                }

                return true
            }
        } else {
            return true
        }
    }

    fun dropShipAsItem() {
        if (this.world.isRemote) {
            return
        }

        for (item: ItemStack in this.getItemsDropped(ArrayList<ItemStack?>())!!) {
            val entityItem: EntityItem? = this.entityDropItem(item, 0f)

            if (item.hasTagCompound()) {
                entityItem!!.getItem().setTagCompound(item.getTagCompound()!!.copy() as NBTTagCompound)
            }
        }
    }

    abstract fun getItemsDropped(droppedItemList: MutableList<ItemStack?>?): MutableList<ItemStack>?

    override fun performHurtAnimation() {
        this.rollAmplitude = 5f
        this.shipDamage += this.shipDamage * 10
    }

    override fun canBeCollidedWith(): Boolean {
        return !this.isDead
    }

    override fun shouldRiderSit(): Boolean {
        return false
    }

    override fun onUpdate() {
        this.ticks++

        super.onUpdate()

        if (this.addToTelemetry) {
            this.addToTelemetry = false
            for (vec: BlockVec3Dim in ArrayList<BlockVec3Dim>(this.telemetryList)) {
                val t1: TileEntity? = vec.getTileEntityNoLoad()
                if (t1 is TileEntityTelemetry && !t1.isInvalid()) {
                    if (t1.linkedEntity === this) t1.addTrackedEntity(this)
                }
            }
        }

        for (e: Entity in this.getPassengers()) {
            e.fallDistance = 0.0f
        }

        if (this.posY > (if (this.world.provider is IExitHeight) (this.world.provider as IExitHeight).getYCoordinateToTeleport() else 1200.0) && this.launchPhase != EnumLaunchPhase.LANDING.ordinal) {
            this.onReachAtmosphere()
            //            if (this.world.isRemote)
//            	this.posY = 1 + (this.world.provider instanceof IExitHeight ? ((IExitHeight) this.world.provider).getYCoordinateToTeleport() : 1200);
        }

        if (this.rollAmplitude > 0) {
            this.rollAmplitude--
        }

        if (this.shipDamage > 0) {
            this.shipDamage--
        }

        if (!this.world.isRemote) {
            if (this.posY < 0.0) {
                this.setDead()
            } else if (this.posY > (if (this.world.provider is IExitHeight) (this.world.provider as IExitHeight).getYCoordinateToTeleport() else 1200.0) + (if (this.launchPhase == EnumLaunchPhase.LANDING.ordinal) 355 else 100)) {
                for (e: Entity? in this.getPassengers()) {
                    if (e is EntityPlayerMP) {
                        val stats: GCPlayerStats = (GCPlayerStats.get(e))!!
                        if (stats.isUsingPlanetSelectionGui()) {
                            this.setDead()
                        }
                    } else this.setDead()
                }
            }

            if (this.timeSinceLaunch > 50 && this.onGround) {
                this.failRocket()
            }
        }

        if (this.launchPhase == EnumLaunchPhase.UNIGNITED.ordinal) {
            this.timeUntilLaunch = this.getPreLaunchWait()
        }

        if (this.launchPhase >= EnumLaunchPhase.LAUNCHED.ordinal) {
            this.timeSinceLaunch++
        } else {
            this.timeSinceLaunch = 0f
        }

        if (this.timeUntilLaunch > 0 && this.launchPhase == EnumLaunchPhase.IGNITED.ordinal) {
            this.timeUntilLaunch--
        }

        val box: AxisAlignedBB = this.getEntityBoundingBox().grow(0.2)

        val var15: MutableList<*> = this.world.getEntitiesWithinAABBExcludingEntity(this, box)

        if (var15 != null && !var15.isEmpty()) {
            for (var52: Int in var15.indices) {
                val var17: Entity = var15.get(var52) as Entity

                if (this.getPassengers().contains(var17)) {
                    var17.applyEntityCollision(this)
                }
            }
        }

        if (this.timeUntilLaunch == 0 && this.launchPhase == EnumLaunchPhase.IGNITED.ordinal) {
            this.setLaunchPhase(EnumLaunchPhase.LAUNCHED)
            this.onLaunch()
        }

        if (this.rotationPitch > 90) {
            this.rotationPitch = 90f
        }

        if (this.rotationPitch < -90) {
            this.rotationPitch = -90f
        }

        this.motionX =
            -(50 * cos(this.rotationYaw / Constants.RADIANS_TO_DEGREES_D) * sin(this.rotationPitch * 0.01 / Constants.RADIANS_TO_DEGREES_D))
        this.motionZ =
            -(50 * sin(this.rotationYaw / Constants.RADIANS_TO_DEGREES_D) * sin(this.rotationPitch * 0.01 / Constants.RADIANS_TO_DEGREES_D))

        if (this.launchPhase < EnumLaunchPhase.LAUNCHED.ordinal) {
            this.motionZ = 0.0
            this.motionY = this.motionZ
            this.motionX = this.motionY
        }

        if (this.world.isRemote) {
            this.setPosition(this.posX, this.posY, this.posZ)

            if (this.shouldMoveClientSide()) {
                this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ)
            }
        } else {
            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ)
        }

        this.setRotation(this.rotationYaw, this.rotationPitch)

        if (this.world.isRemote) {
            this.setPosition(this.posX, this.posY, this.posZ)
        }

        this.prevPosX = this.posX
        this.prevPosY = this.posY
        this.prevPosZ = this.posZ

        if (!this.world.isRemote && this.ticks % 3 == 0L) {
            (GalacticraftCore.packetPipeline)!!.sendToDimension(PacketDynamic(this), this.world.provider.getDimension())
            // PacketDispatcher.sendPacketToAllInDimension(GCCorePacketManager.getPacket(GalacticraftCore.CHANNELENTITIES,
            // this, this.getNetworkedData(new ArrayList())),
            // this.world.provider.getDimension());
        }
    }

    protected open fun shouldMoveClientSide(): Boolean {
        return true
    }

    abstract fun hasValidFuel(): Boolean

    override fun decodePacketdata(buffer: ByteBuf?) {
        if (!this.world.isRemote) {
            Exception().printStackTrace()
        }
        val newLaunchPhase: Int = (buffer)!!.readInt()
        if (this.launchPhase != newLaunchPhase) {
            this.setLaunchPhase(EnumLaunchPhase.entries[newLaunchPhase])
            if (newLaunchPhase == EnumLaunchPhase.LANDING.ordinal) {
                this.syncAdjustFlag = true
            }
        } else if (this.hasValidFuel()) {
            if (abs(this.syncAdjustX - this.posX) > 0.2) this.motionX += (this.syncAdjustX - this.posX) / 40.0
            if (abs(this.syncAdjustY - this.posY) > 0.2) this.motionY += (this.syncAdjustY - this.posY) / 40.0
            if (abs(this.syncAdjustZ - this.posZ) > 0.2) this.motionZ += (this.syncAdjustZ - this.posZ) / 40.0
        }
        this.timeSinceLaunch = (buffer)!!.readFloat()
        this.timeUntilLaunch = (buffer)!!.readInt()
    }

    override fun getNetworkedData(list: ArrayList<Any?>?) {
        if (this.world.isRemote) {
            return
        }
        (list)!!.add(this.launchPhase)
        (list)!!.add(this.timeSinceLaunch)
        (list)!!.add(this.timeUntilLaunch)
    }

    fun turnYaw(f: Float) {
        this.rotationYaw += f
    }

    fun turnPitch(f: Float) {
        this.rotationPitch += f
    }

    protected open fun failRocket() {
        for (passenger: Entity in this.getPassengers()) {
            foundation.damage.FoundationDamageTypes.hurt(passenger, DamageSourceGC.spaceshipCrash, (4.0 * 20 + 1.0).toInt().toFloat())
        }

        if (!ConfigManagerCore.disableSpaceshipGrief) {
            this.world.createExplosion(this, this.posX, this.posY, this.posZ, 5f, true)
        }

        this.setDead()
    }

    @OnlyIn(Dist.CLIENT)
    override fun setPositionAndRotationDirect(
        x: kotlin.Double,
        y: kotlin.Double,
        z: kotlin.Double,
        yaw: Float,
        pitch: Float,
        posRotationIncrements: Int,
        b: Boolean
    ) {
        this.setRotation(yaw, pitch)
        if (this.syncAdjustFlag && this.world.isBlockLoaded(BlockPos(x, 255.0, z)) && this.hasValidFuel()) {
            val p: EntityPlayer = FMLClientHandler.instance().getClientPlayerEntity()
            val dx: kotlin.Double = x - p.x
            val dz: kotlin.Double = z - p.z
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

                this.syncAdjustX = 0.0
                this.syncAdjustY = 0.0
                this.syncAdjustZ = 0.0
                this.syncAdjustFlag = false
            }
        } else {
            this.syncAdjustX = x
            this.syncAdjustY = y
            this.syncAdjustZ = z
        }
    }

    override fun writeEntityToNBT(nbt: NBTTagCompound) {
        nbt.putInt("launchPhase", this.launchPhase + 1)
        nbt.putInt("timeUntilLaunch", this.timeUntilLaunch)
        if (telemetryList.size > 0) {
            val teleNBTList: NBTTagList = NBTTagList()
            for (vec: BlockVec3Dim in ArrayList<BlockVec3Dim>(this.telemetryList)) {
                val tag: NBTTagCompound = NBTTagCompound()
                vec.writeToNBT(tag)
                teleNBTList.add(tag)
            }
            nbt.put("telemetryList", teleNBTList)
        }
    }

    override fun readEntityFromNBT(nbt: NBTTagCompound) {
        this.timeUntilLaunch = nbt.getIntOr("timeUntilLaunch", 0)

        var hasOldTags: Boolean = false

        // Backwards compatibility:
        if (nbt.getKeySet().contains("launched")) {
            hasOldTags = true

            val launched: Boolean = nbt.getBooleanOr("launched", false)

            if (launched) {
                this.setLaunchPhase(EnumLaunchPhase.LAUNCHED)
            }
        }

        // Backwards compatibility:
        if (nbt.getKeySet().contains("ignite")) {
            hasOldTags = true

            val ignite: Int = nbt.getIntOr("ignite", 0)

            if (ignite == 1) {
                this.setLaunchPhase(EnumLaunchPhase.IGNITED)
            }
        }

        // Backwards compatibility:
        if (hasOldTags) {
            if (this.launchPhase < EnumLaunchPhase.LAUNCHED.ordinal && this.launchPhase != EnumLaunchPhase.IGNITED.ordinal) {
                this.setLaunchPhase(EnumLaunchPhase.UNIGNITED)
            }
        } else {
            this.setLaunchPhase(EnumLaunchPhase.entries[nbt.getIntOr("launchPhase", 0) - 1])
        }

        //Update all Telemetry Units which are still tracking this rocket
        this.telemetryList.clear()
        if (nbt.containsTag("telemetryList")) {
            val teleNBT: NBTTagList = nbt.listOfType("telemetryList", 10)
            if (teleNBT.size > 0) {
                for (j: Int in teleNBT.size - 1 downTo 0) {
                    val tag1: NBTTagCompound = teleNBT.getCompoundOrEmpty(j)
                    if (tag1 != null) {
                        this.telemetryList.add(BlockVec3Dim.readFromNBT(tag1))
                    }
                }
                this.addToTelemetry = true
            }
        }
    }

    fun getLaunched(): Boolean {
        return this.launchPhase >= EnumLaunchPhase.LAUNCHED.ordinal
    }

    fun canBeRidden(): Boolean {
        return false
    }

    open fun ignite() {
        this.setLaunchPhase(EnumLaunchPhase.IGNITED)
    }

    override fun getMountedYOffset(): kotlin.Double {
        return -0.9
    }

    open fun getOnPadYOffset(): kotlin.Double {
        return 0.0
    }

    open fun onLaunch() {
        MinecraftForge.EVENT_BUS.post(RocketLaunchEvent(this))
    }

    open fun onReachAtmosphere() {
    }

    @OnlyIn(Dist.CLIENT)
    fun spawnParticle(
        var1: String?,
        var2: kotlin.Double,
        var4: kotlin.Double,
        var6: kotlin.Double,
        var8: kotlin.Double,
        var10: kotlin.Double,
        var12: kotlin.Double
    ) {
    }

    override fun canRiderInteract(): Boolean {
        return true
    }

    fun getSpaceshipGui(): ResourceLocation? {
        return GalacticraftRegistry.getResouceLocationForDimension(this.world.provider.javaClass)
    }

    fun setLaunchPhase(phase: EnumLaunchPhase) {
        this.launchPhase = phase.ordinal
    }

    override fun shouldIgnoreShiftExit(): Boolean {
        return this.launchPhase >= EnumLaunchPhase.LAUNCHED.ordinal
    }

    open class RocketLaunchEvent constructor(@JvmField val rocket: EntitySpaceshipBase?) : EntityEvent(rocket)

    fun addTelemetry(tile: TileEntityTelemetry) {
        this.telemetryList.add(BlockVec3Dim(tile))
    }

    fun getTelemetry(): ArrayList<TileEntityTelemetry?> {
        val returnList: ArrayList<TileEntityTelemetry?> = ArrayList<TileEntityTelemetry?>()
        for (vec: BlockVec3Dim in ArrayList<BlockVec3Dim>(this.telemetryList)) {
            val t1: TileEntity? = vec.getTileEntity()
            if (t1 is TileEntityTelemetry && !t1.isInvalid()) {
                if (t1.linkedEntity === this) returnList.add(t1)
            }
        }
        return returnList
    }

    override fun transmitData(data: IntArray?) {
        (data)!![0] = this.timeUntilLaunch
        (data)!![1] = this.posY.toInt()
        //data[2] is entity speed already set as default by TileEntityTelemetry
        (data)!![3] = this.getScaledFuelLevel(100)
        (data)!![4] = this.rotationPitch.toInt()
    }

    override fun receiveData(data: IntArray?, str: Array<String?>?) {
        //Spaceships:
        //  data0 = launch countdown
        //  data1 = height
        //  data2 = speed
        //  data3 = fuel remaining
        //  data4 = pitch angle
        val countdown: Int = (data)!![0]
        (str)!![0] = ""
        (str)!![1] =
            if (countdown == 400) GCCoreUtil.translate("gui.rocket.on_launchpad") else (if (countdown > 0) GCCoreUtil.translate(
                "gui.rocket.countdown"
            ) + ": " + countdown / 20 else GCCoreUtil.translate("gui.rocket.launched"))
        (str)!![2] = GCCoreUtil.translate("gui.rocket.height") + ": " + (data)!![1]
        (str)!![3] = (GameScreenText)!!.makeSpeedString((data)!![2])
        (str)!![4] = GCCoreUtil.translate("gui.message.fuel.name") + ": " + (data)!![3] + "%"
    }

    override fun adjustDisplay(data: IntArray?) {
        GL11.glRotatef(((data)!![4])!!.toFloat(), -1f, 0f, 0f)
        GL11.glTranslatef(0f, this.height / 4, 0f)
    }

    /**
     * Used in RenderTier1Rocket for standard rendering. Ignore if using a custom renderer.
     *
     * @return Y-axis offset for rendering at correct position
     */
    open fun getRenderOffsetY(): Float {
        return 1.34f
    }

    init {
        this.launchPhase = EnumLaunchPhase.UNIGNITED.ordinal
        this.preventEntitySpawning = true
        this.ignoreFrustumCheck = true
    }

    @OnlyIn(Dist.CLIENT)
    override fun getBrightnessForRender(): Int {
        var height: kotlin.Double = this.posY + this.getEyeHeight().toDouble()
        if (height > 255.0) height = 255.0
        val blockpos: BlockPos = BlockPos(this.posX, height, this.posZ)
        return if (this.world.isBlockLoaded(blockpos)) this.world.getCombinedLight(blockpos, 0) else 0
    }

    companion object {
        @JvmField
        val rocketSelector: Predicate<Entity> = object : Predicate<Entity> {
            override fun apply(e: Entity?): Boolean {
                return e is EntitySpaceshipBase && e.isEntityAlive()
            }
        }
    }
}
