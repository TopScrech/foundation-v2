package micdoodle8.mods.galacticraft.core.entities

import micdoodle8.mods.galacticraft.core.network.PacketEntityUpdate.IEntityFullSync
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint
import java.util.ArrayList
import java.util.Map
import java.util.Random

import io.netty.buffer.ByteBuf
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.network.PacketEntityUpdate
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.particle.Particle
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.MoverType
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.DamageSource
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

abstract class EntityAdvancedMotion(world: World?) : InventoryEntity(world), IControllableEntity,
    PacketEntityUpdate.IEntityFullSync {
    @JvmField
    protected var ticks: Long = 0

    @JvmField
    var currentDamage: Float = 0f

    @JvmField
    var timeSinceHit: Int = 0

    @JvmField
    var rockDirection: Int = 0

    @JvmField
    var advancedPositionX: Double = 0.0

    @JvmField
    var advancedPositionY: Double = 0.0

    @JvmField
    var advancedPositionZ: Double = 0.0

    @JvmField
    var advancedYaw: Double = 0.0

    @JvmField
    var advancedPitch: Double = 0.0

    @JvmField
    var posRotIncrements: Int = 0

    @JvmField
    protected var lastOnGround: Boolean = false

    init {
        this.preventEntitySpawning = true
        this.ignoreFrustumCheck = true
        this.isImmuneToFire = true
    }

    constructor(world: World?, var2: Double, var4: Double, var6: Double) : this(world) {
        this.setPosition(var2, var4, var6)
    }

    override fun entityInit() {
    }

    override fun canTriggerWalking(): Boolean {
        return false
    }

    override fun canBePushed(): Boolean {
        return false
    }

    override fun getMountedYOffset(): Double {
        return this.height - 1.0
    }

    override fun canBeCollidedWith(): Boolean {
        return !this.isDead
    }

    override fun updatePassenger(passenger: Entity) {
        if (this.isPassenger(passenger)) {
            val offsetx: Double = cos(this.rotationYaw / Constants.RADIANS_TO_DEGREES_D + 114.8) * -0.5
            val offsetz: Double = sin(this.rotationYaw / Constants.RADIANS_TO_DEGREES_D + 114.8) * -0.5
            passenger.setPosition(
                this.posX + offsetx,
                this.posY + this.getMountedYOffset() + passenger.getYOffset(),
                this.posZ + offsetz
            )
        }
    }

    override fun setPositionRotationAndMotion(
        x: Double,
        y: Double,
        z: Double,
        yaw: Float,
        pitch: Float,
        motX: Double,
        motY: Double,
        motZ: Double,
        onGround: Boolean
    ) {
        if (this.world.isRemote) {
            this.advancedPositionX = x
            this.advancedPositionY = y
            this.advancedPositionZ = z
            this.advancedYaw = yaw.toDouble()
            this.advancedPitch = pitch.toDouble()
            this.motionX = motX
            this.motionY = motY
            this.motionZ = motZ
            this.posRotIncrements = 5
        } else {
            this.setPosition(x, y, z)
            this.setRotation(yaw, pitch)
            this.motionX = motX
            this.motionY = motY
            this.motionZ = motZ
            if (onGround || this.forceGroundUpdate()) {
                this.onGround = onGround
            }
        }
    }

    protected fun forceGroundUpdate(): Boolean {
        return true
    }

    override fun performHurtAnimation() {
        this.rockDirection = -this.rockDirection
        this.timeSinceHit = 10
        this.currentDamage *= 5f
    }

    override fun attackEntityFrom(var1: DamageSource, var2: Float): Boolean {
        if (this.isDead || var1 == DamageSource.CACTUS || !this.allowDamageSource(var1)) {
            return true
        } else {
            val e: Entity? = var1.getTrueSource()
            if (this.isEntityInvulnerable(var1) || this.posY > 300 || (e is EntityLivingBase && e !is EntityPlayer)) {
                return false
            } else {
                this.rockDirection = -this.rockDirection
                this.timeSinceHit = 10
                this.currentDamage = this.currentDamage + var2 * 10
                this.markVelocityChanged()

                if (e is EntityPlayer && e.capabilities.isCreativeMode) {
                    this.currentDamage = 100f
                }

                if (this.currentDamage > 70) {
                    if (!this.getPassengers().isEmpty()) {
                        this.removePassengers()

                        return false
                    }

                    if (!this.world.isRemote) {
                        this.dropItems()

                        this.setDead()
                    }
                }

                return true
            }
        }
    }

    abstract fun getItemsDropped(): MutableList<ItemStack?>?

    abstract fun shouldMove(): Boolean

    abstract fun shouldSpawnParticles(): Boolean

    /**
     * @return map of the particle vectors. Map key is the position and map
     * value is the motion of the particles. Each entry will be spawned
     * as a separate particle
     */
    abstract fun getParticleMap(): MutableMap<Vector3?, Vector3?>?

    @SideOnly(Side.CLIENT)
    abstract fun getParticle(
        rand: Random?,
        x: Double,
        y: Double,
        z: Double,
        motX: Double,
        motY: Double,
        motZ: Double
    ): Particle?

    abstract fun tickInAir()

    abstract fun tickOnGround()

    abstract fun onGroundHit()

    abstract fun getMotionVec(): Vector3

    /**
     * Can be called in the superclass init method
     * before the subclass fields have been initialised!
     * Therefore include null checks!!!
     */
    abstract fun getNetworkedData(): ArrayList<Any?>?

    /**
     * @return ticks between packets being sent to client
     */
    abstract fun getPacketTickSpacing(): Int

    /**
     * @return players within this distance will recieve packets from this
     * entity
     */
    abstract fun getPacketSendDistance(): Double

    abstract fun readNetworkedData(buffer: ByteBuf?)

    abstract fun allowDamageSource(damageSource: DamageSource?): Boolean

    fun dropItems() {
        if (this.getItemsDropped() == null) {
            return
        }

        for (item: ItemStack? in this.getItemsDropped()!!) {
            if (item != null && !item.isEmpty()) {
                this.entityDropItem(item, 0f)
            }
        }
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
        if (!this.getPassengers().isEmpty()) {
            if (this.getPassengers().contains(FMLClientHandler.instance().getClient().player)) {
            } else {
                this.posRotIncrements = posRotationIncrements + 5
                this.advancedPositionX = x
                this.advancedPositionY = y
                this.advancedPositionZ = z
                this.advancedYaw = yaw.toDouble()
                this.advancedPitch = pitch.toDouble()
            }
        }
    }

    override fun move(type: MoverType, x: Double, y: Double, z: Double) {
        if (this.shouldMove()) {
            super.move(type, x, y, z)
        }
    }

    abstract fun canSetPositionClient(): Boolean

    abstract fun shouldSendAdvancedMotionPacket(): Boolean

    override fun onUpdate() {
        this.ticks++

        super.onUpdate()

        if (this.canSetPositionClient() && this.world.isRemote && (this.getPassengers()
                .isEmpty() || !this.getPassengers().contains(FMLClientHandler.instance().getClient().player))
        ) {
            val x: Double
            val y: Double
            val var12: Double
            val z: Double
            if (this.posRotIncrements > 0) {
                x = this.posX + (this.advancedPositionX - this.posX) / this.posRotIncrements
                y = this.posY + (this.advancedPositionY - this.posY) / this.posRotIncrements
                z = this.posZ + (this.advancedPositionZ - this.posZ) / this.posRotIncrements
                var12 = MathHelper.wrapDegrees(this.advancedYaw - this.rotationYaw)
                this.rotationYaw = (this.rotationYaw + var12 / this.posRotIncrements).toFloat()
                this.rotationPitch =
                    (this.rotationPitch + (this.advancedPitch - this.rotationPitch) / this.posRotIncrements).toFloat()
                --this.posRotIncrements
                this.setPosition(x, y, z)
                this.setRotation(this.rotationYaw, this.rotationPitch)
            } else {
//                x = this.posX + this.motionX;
//                y = this.posY + this.motionY;
//                z = this.posZ + this.motionZ;
//                this.setPosition(x, y, z);
            }
        }

        if (this.timeSinceHit > 0) {
            this.timeSinceHit--
        }

        if (this.currentDamage > 0) {
            this.currentDamage--
        }

        if (this.shouldSpawnParticles() && this.world.isRemote) {
            this.spawnParticles(this.getParticleMap()!!)
        }

        if (this.onGround) {
            this.tickOnGround()
        } else {
            this.tickInAir()
        }

        if (this.world.isRemote) {
            val mot: Vector3 = this.getMotionVec()
            this.motionX = mot.x
            this.motionY = mot.y
            this.motionZ = mot.z
        }
        //Necessary on both server and client to achieve a correct this.onGround setting
        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ)

        if (this.onGround && !this.lastOnGround) {
            this.onGroundHit()
        }

        if (shouldSendAdvancedMotionPacket()) {
            if (this.world.isRemote) {
                (GalacticraftCore.packetPipeline)!!.sendToServer(PacketEntityUpdate(this))
            }

            if (!this.world.isRemote && this.ticks % 5 == 0L) {
                (GalacticraftCore.packetPipeline)!!.sendToAllAround(
                    PacketEntityUpdate(this),
                    NetworkRegistry.TargetPoint(
                        GCCoreUtil.getDimensionID(this.world),
                        this.posX,
                        this.posY,
                        this.posZ,
                        50.0
                    )
                )
            }
        }

        this.prevPosX = this.posX
        this.prevPosY = this.posY
        this.prevPosZ = this.posZ
        this.lastOnGround = this.onGround
    }

    override fun getNetworkedData(sendData: ArrayList<Any?>?) {
        (sendData)!!.addAll(this.getNetworkedData()!!)
    }

    override fun decodePacketdata(buffer: ByteBuf?) {
        this.readNetworkedData(buffer)
    }

    @SideOnly(Side.CLIENT)
    fun spawnParticles(points: MutableMap<Vector3?, Vector3?>) {
        for (vec in points.entries) {
            val posVec: Vector3 = (vec.key)!!
            val motionVec: Vector3 = (vec.value)!!

            this.spawnParticle(
                this.getParticle(
                    this.rand,
                    posVec.x,
                    posVec.y,
                    posVec.z,
                    motionVec.x,
                    motionVec.y,
                    motionVec.z
                )
            )
        }
    }

    @SideOnly(Side.CLIENT)
    fun spawnParticle(fx: Particle?) {
        val mc: Minecraft? = FMLClientHandler.instance().getClient()

        if (mc != null && mc.getRenderViewEntity() != null && mc.effectRenderer != null) {
            if (fx != null) {
                mc.effectRenderer.addEffect(fx)
            }
        }
    }
}
