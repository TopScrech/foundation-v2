package micdoodle8.mods.galacticraft.core.entities

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket

import micdoodle8.mods.galacticraft.api.entity.ICameraZoomEntity
import micdoodle8.mods.galacticraft.api.entity.IIgnoreShift
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.fx.ParticleLanderFlame
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.particle.Particle
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.HashMap
import java.util.Random
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

open class EntityLander : EntityLanderBase, IIgnoreShift, ICameraZoomEntity {
    private var lastMotionY = 0.0

    constructor(world: World?) : super(world) {
        this.setSize(3.0f, 4.25f)
    }

    constructor(player: EntityPlayerMP) : super(player, 0.0f)

    override fun getMountedYOffset(): Double {
        return 2.25
    }

    override fun getRotateOffset(): Float {
        return +0.0f
    }

    override fun onUpdate() {
        super.onUpdate()

        this.lastMotionY = this.motionY
    }

    override fun readEntityFromNBT(nbt: NBTTagCompound) {
        super.readEntityFromNBT(nbt)

        this.lastMotionY = this.motionY
    }

    override fun writeEntityToNBT(nbt: NBTTagCompound) {
        super.writeEntityToNBT(nbt)
    }

    override fun getName(): String {
        return GCCoreUtil.translate("container.lander.name")
    }

    override fun hasCustomName(): Boolean {
        return true
    }

    override fun processInitialInteract(player: EntityPlayer, hand: EnumHand): Boolean {
        if (this.world.isRemote) {
            if (!this.onGround) {
                return false
            }

            if (!this.getPassengers().isEmpty()) {
                this.removePassengers()
            }

            return true
        }

        if (this.getPassengers().isEmpty() && player is EntityPlayerMP) {
            GCCoreUtil.openParachestInv(player, this)
            return true
        } else if (player is EntityPlayerMP) {
            if (!this.onGround) {
                return false
            }

            this.removePassengers()
            return true
        } else {
            return true
        }
    }

    override fun pressKey(key: Int): Boolean {
        if (this.onGround) {
            return false
        }

        val turnFactor: Float = 2.0f
        val angle: Float = 45f

        when (key) {
            0 -> {
                this.rotationPitch = min(max(this.rotationPitch - 0.5f * turnFactor, -angle), angle)
                return true
            }

            1 -> {
                this.rotationPitch = min(max(this.rotationPitch + 0.5f * turnFactor, -angle), angle)
                return true
            }

            2 -> {
                this.rotationYaw -= 0.5f * turnFactor
                return true
            }

            3 -> {
                this.rotationYaw += 0.5f * turnFactor
                return true
            }

            4 -> {
                this.motionY = min(this.motionY + 0.03f, if (this.posY < 90) -0.15 else -1.0)
                return true
            }

            5 -> {
                this.motionY = min(this.motionY - 0.022f, -1.0)
                return true
            }
        }

        return false
    }

    override fun shouldSpawnParticles(): Boolean {
        return this.ticks > 40 && this.rotationPitch != 0.0000001f
    }

    override fun getParticleMap(): MutableMap<Vector3?, Vector3?> {
        val sinPitch: Double = sin(this.rotationPitch / Constants.RADIANS_TO_DEGREES_D)
        val x1: Double = 4 * cos(this.rotationYaw / Constants.RADIANS_TO_DEGREES_D) * sinPitch
        val z1: Double = 4 * sin(this.rotationYaw / Constants.RADIANS_TO_DEGREES_D) * sinPitch
        val y1: Double = -4 * abs(cos(this.rotationPitch / Constants.RADIANS_TO_DEGREES_D))

        Vector3(this)

        val particleMap: MutableMap<Vector3?, Vector3?> = HashMap<Vector3?, Vector3?>()
        particleMap.put(
            Vector3(this.posX, this.posY + 1.0 + this.motionY / 2, this.posZ),
            Vector3(x1, y1 + this.motionY / 2, z1)
        )
        return particleMap
    }

    @SideOnly(Side.CLIENT)
    override fun getParticle(
        rand: Random?,
        x: Double,
        y: Double,
        z: Double,
        motX: Double,
        motY: Double,
        motZ: Double
    ): Particle {
        val passenger: EntityLivingBase? = if (this.getPassengers().isEmpty() || this.getPassengers()
                .get(0) !is EntityLivingBase
        ) null else this.getPassengers().get(0) as EntityLivingBase?
        return ParticleLanderFlame(this.world, x, y, z, motX, motY, motZ, passenger)
    }

    override fun tickInAir() {
        super.tickInAir()

        if (this.world.isRemote) {
            if (!this.onGround) {
                this.motionY -= 0.008
            }

            val motY: Double = -1 * sin(this.rotationPitch / Constants.RADIANS_TO_DEGREES_D)
            val motX: Double = cos(this.rotationYaw / Constants.RADIANS_TO_DEGREES_D) * motY
            val motZ: Double = sin(this.rotationYaw / Constants.RADIANS_TO_DEGREES_D) * motY
            this.motionX = motX / 2.0f
            this.motionZ = motZ / 2.0f
        }
    }

    override fun tickOnGround() {
        //Signal switch off flames
        this.rotationPitch = 0.0000001f
    }

    override fun onGroundHit() {
        if (!this.world.isRemote) {
            if (abs(this.lastMotionY) > 2.0) {
                for (entity: Entity in this.getPassengers()) {
                    entity.dismountRidingEntity()
                    if (entity is EntityPlayerMP) {
                        (GalacticraftCore.packetPipeline)!!.sendTo(
                            PacketSimple(
                                PacketSimple.EnumSimplePacket.C_RESET_THIRD_PERSON,
                                GCCoreUtil.getDimensionID(this.world),
                                arrayOf<Any?>()
                            ), entity
                        )
                    }
                    entity.motionX = 0.0
                    entity.motionY = 0.0
                    entity.motionZ = 0.0
                    entity.setPosition(entity.posX, this.posY + this.getMountedYOffset(), entity.posZ)
                    this.world.updateEntityWithOptionalForce(entity, false)
                }
                this.world.createExplosion(this, this.posX, this.posY, this.posZ, 12f, true)

                this.setDead()
            }
        }
    }

    override fun getMotionVec(): Vector3 {
        if (this.onGround) {
            return Vector3(0.0, 0.0, 0.0)
        }

        if (this.ticks >= 40 && this.ticks < 45) {
            this.motionY = this.getInitialMotionY()
        }

        return Vector3(this.motionX, if (this.ticks < 40) 0.0 else this.motionY, this.motionZ)
    }

    override fun getCameraZoom(): Float {
        return 15f
    }

    override fun defaultThirdPerson(): Boolean {
        return true
    }

    override fun shouldIgnoreShiftExit(): Boolean {
        return !this.onGround
    }

    override fun getInitialMotionY(): Double {
        return -2.5
    }

    override fun canTriggerWalking(): Boolean {
        return false
    }

    override fun getCollisionBoundingBox(): AxisAlignedBB? {
        return null
    }

    override fun getCollisionBox(par1Entity: Entity): AxisAlignedBB? {
        return null
    }

    override fun canBePushed(): Boolean {
        return false
    }

    override fun canBeCollidedWith(): Boolean {
        return !this.isDead
    }
}
