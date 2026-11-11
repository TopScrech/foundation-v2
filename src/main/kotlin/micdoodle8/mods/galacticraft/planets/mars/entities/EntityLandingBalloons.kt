package micdoodle8.mods.galacticraft.planets.mars.entities

import java.util.ArrayList
import java.util.Random

import io.netty.buffer.ByteBuf
import micdoodle8.mods.galacticraft.api.entity.ICameraZoomEntity
import micdoodle8.mods.galacticraft.api.entity.IIgnoreShift
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.entities.EntityLanderBase
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.mars.util.MarsUtil
import net.minecraft.client.particle.Particle
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.*

open class EntityLandingBalloons : EntityLanderBase, IIgnoreShift, ICameraZoomEntity {
    private var groundHitCount = 0
    private var rotationPitchSpeed = 0f
    private var rotationYawSpeed = 0f

    constructor(world: World?) : super(world) {
        this.setSize(2.0f, 2.0f)
        this.rotationPitchSpeed = this.rand.nextFloat()
        this.rotationYawSpeed = this.rand.nextFloat()
    }

    constructor(player: EntityPlayerMP) : super(player, 0f) {
        this.setSize(2.0f, 2.0f)
    }

    override fun getMountedYOffset(): Double {
        return super.getMountedYOffset() - 0.9
    }

    override fun getRotateOffset(): Float {
        //Signal no rotate
        return -20.0f
    }

    override fun onUpdate() {
        if (!this.getPassengers().isEmpty()) {
            for (entity: Entity in this.getPassengers()) {
                entity.onGround = false
            }
        }

        super.onUpdate()

        if (!this.getPassengers().isEmpty()) {
            for (entity: Entity in this.getPassengers()) {
                entity.onGround = false
            }
        }

        if (!this.onGround) {
            this.rotationPitch += this.rotationPitchSpeed
            this.rotationYaw += this.rotationYawSpeed
        }
    }

    override fun readEntityFromNBT(nbt: NBTTagCompound) {
        super.readEntityFromNBT(nbt)
        this.groundHitCount = nbt.getIntOr("GroundHitCount", 0)
    }

    override fun writeEntityToNBT(nbt: NBTTagCompound) {
        super.writeEntityToNBT(nbt)
        nbt.putInt("GroundHitCount", this.groundHitCount)
    }

    override fun getName(): net.minecraft.network.chat.Component {
        return net.minecraft.network.chat.Component.translatable("container.mars_lander.name")
    }

    override fun hasCustomName(): Boolean {
        return true
    }

    override fun processInitialInteract(player: EntityPlayer, hand: EnumHand): Boolean {
        if (this.world.isRemote) {
            if (!this.onGround) {
                return false
            }

            this.removePassengers()

            return true
        } else if (this.getPassengers().isEmpty() && this.groundHitCount >= 14 && player is EntityPlayerMP) {
            MarsUtil.openParachestInventory(player, this)
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

        //        switch (key)
//        {
//            case 0: // Accelerate
//            {
//                this.rotationPitchSpeed -= 0.5F * TURN_FACTOR;
//                return true;
//            }
//            case 1: // Deccelerate
//            {
//                this.rotationPitchSpeed += 0.5F * TURN_FACTOR;
//                return true;
//            }
//            case 2: // Left
//                this.rotationYawSpeed -= 0.5F * TURN_FACTOR;
//                return true;
//            case 3: // Right
//                this.rotationYawSpeed += 0.5F * TURN_FACTOR;
//                return true;
//        }
        return false
    }

    override fun shouldMove(): Boolean {
        if (this.ticks < 40 || !this.hasReceivedPacket) {
            return false
        }

        return ((!this.getPassengers().isEmpty() && this.groundHitCount < 14) || !this.onGround)
    }

    override fun shouldSpawnParticles(): Boolean {
        return false
    }

    override fun getParticleMap(): MutableMap<Vector3?, Vector3?>? {
        return null
    }

    @OnlyIn(Dist.CLIENT)
    override fun getParticle(
        rand: Random?,
        x: Double,
        y: Double,
        z: Double,
        motX: Double,
        motY: Double,
        motZ: Double
    ): Particle? {
        return null
    }

    override fun tickInAir() {
        if (this.world.isRemote) {
            if (this.groundHitCount == 0) {
                this.motionY = -this.posY / 50.0
            } else if (this.groundHitCount < 14 || this.shouldMove()) {
                this.motionY *= 0.95
                this.motionY -= 0.08
            } else {
                if (!this.shouldMove()) {
                    this.rotationYawSpeed = 0.0f
                    this.rotationPitchSpeed = this.rotationYawSpeed
                    this.motionZ = this.rotationPitchSpeed.toDouble()
                    this.motionX = this.motionZ
                    this.motionY = this.motionX
                }
            }
        }
    }

    override fun tickOnGround() {
    }

    override fun onGroundHit() {
    }

    override fun getMotionVec(): Vector3 {
        if (this.onGround) {
            if (this.groundHitCount < 14) {
                this.groundHitCount++
                val mag: Double = (1.0 / this.groundHitCount) * 4.0
                var mX: Double = this.rand.nextDouble() - 0.5
                var mY: Double = 1.0
                var mZ: Double = this.rand.nextDouble() - 0.5
                mX *= mag / 3.0
                mY *= mag
                mZ *= mag / 3.0
                return Vector3(mX, mY, mZ)
            }
        }

        if (this.ticks >= 40 && this.ticks < 45) {
            this.motionY = this.getInitialMotionY()
        }

        if (!this.shouldMove()) {
            return Vector3(0.0, 0.0, 0.0)
        }

        return Vector3(this.motionX, if (this.ticks < 40) 0.0 else this.motionY, this.motionZ)
    }

    override fun getNetworkedData(): ArrayList<Any?> {
        val objList: ArrayList<Any?> = ArrayList<Any?>()
        objList.addAll(super.getNetworkedData())
        if ((this.world.isRemote && this.hasReceivedPacket && this.groundHitCount <= 14) || (!this.world.isRemote && this.groundHitCount == 14)) {
            objList.add(this.groundHitCount)
        }
        return objList
    }

    override fun getPacketTickSpacing(): Int {
        return 5
    }

    override fun getPacketSendDistance(): Double {
        return 50.0
    }

    override fun readNetworkedData(buffer: ByteBuf?) {
        try {
            super.readNetworkedData(buffer)

            if ((buffer)!!.readableBytes() > 0) {
                this.groundHitCount = (buffer)!!.readInt()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun allowDamageSource(damageSource: DamageSource?): Boolean {
        return this.groundHitCount > 0 && super.allowDamageSource(damageSource)
    }

    override fun getInitialMotionY(): Double {
        return 0.0
    }

    override fun getCameraZoom(): Float {
        return 15.0f
    }

    override fun defaultThirdPerson(): Boolean {
        return true
    }

    override fun shouldIgnoreShiftExit(): Boolean {
        return this.groundHitCount < 14 || !this.onGround
    }
}
