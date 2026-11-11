package micdoodle8.mods.galacticraft.planets.asteroids.entities

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.MoverType
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.network.datasync.DataParameter
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.network.datasync.EntityDataManager
import net.minecraft.world.level.Level as World
import kotlin.math.abs

open class EntitySmallAsteroid constructor(world: World) : Entity(world) {
    @JvmField
    var spinPitch: Float = 0f

    @JvmField
    var spinYaw: Float = 0f

    @JvmField
    var type: Int = 0
    private var asteroidFirstUpdate = true

    init {
        this.setSize(1.0f, 1.0f)
        this.isImmuneToFire = true
    }

    override fun onEntityUpdate() {
        if (!this.asteroidFirstUpdate) {
            // Kill non-moving entities
            if (abs(this.posX - this.prevPosX) + abs(this.posZ - this.prevPosZ) <= 0) {
                this.setDead()
            } else if (this.posY > 288.0 || this.posY < -32.0 || this.ticksExisted > 3000) {
                this.setDead()
            }
        }

        super.onEntityUpdate()

        if (!this.world.isRemote) {
            this.setSpinPitch(this.spinPitch)
            this.setSpinYaw(this.spinYaw)
            this.setAsteroidType(this.type)
            this.rotationPitch += this.spinPitch
            this.rotationYaw += this.spinYaw
        } else {
            this.rotationPitch += this.getSpinPitch()
            this.rotationYaw += this.getSpinYaw()
        }

        val sqrdMotion: Double = this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ

        if (sqrdMotion < 0.05) {
            // If the motion is too low (for some odd reason), speed it back up slowly.
            this.motionX *= 1.001
            this.motionY *= 1.001
            this.motionZ *= 1.001
        }

        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ)
        this.asteroidFirstUpdate = false
    }

    override fun entityInit() {
        this.dataManager.register<Float>(SPIN_PITCH, 0.0f)
        this.dataManager.register<Float>(SPIN_YAW, 0.0f)
        this.dataManager.register<Int>(ASTEROID_TYPE, 0)
    }

    override fun readEntityFromNBT(nbt: NBTTagCompound) {
        this.spinPitch = nbt.getFloatOr("spinPitch", 0f)
        this.spinYaw = nbt.getFloatOr("spinYaw", 0f)
        this.ticksExisted = nbt.getIntOr("ageTicks", 0)
    }

    override fun writeEntityToNBT(nbt: NBTTagCompound) {
        nbt.putFloat("spinPitch", this.spinPitch)
        nbt.putFloat("spinYaw", this.spinYaw)
        nbt.putInt("ageTicks", this.ticksExisted)
    }

    open fun getSpinPitch(): Float {
        return this.dataManager.get<Float>(SPIN_PITCH)
    }

    open fun getSpinYaw(): Float {
        return this.dataManager.get<Float>(SPIN_YAW)
    }

    open fun setSpinPitch(pitch: Float) {
        this.dataManager.set<Float>(SPIN_PITCH, pitch)
    }

    open fun setSpinYaw(yaw: Float) {
        this.dataManager.set<Float>(SPIN_YAW, yaw)
    }

    open fun getAsteroidType(): Int {
        return this.dataManager.get<Int>(ASTEROID_TYPE)
    }

    open fun setAsteroidType(type: Int) {
        this.dataManager.set<Int>(ASTEROID_TYPE, type)
    }

    companion object {
        private val SPIN_PITCH: DataParameter<Float> =
            EntityDataManager.createKey<Float>(EntitySmallAsteroid::class.java, DataSerializers.FLOAT)
        private val SPIN_YAW: DataParameter<Float> =
            EntityDataManager.createKey<Float>(EntitySmallAsteroid::class.java, DataSerializers.FLOAT)
        private val ASTEROID_TYPE: DataParameter<Int> =
            EntityDataManager.createKey<Int>(EntitySmallAsteroid::class.java, DataSerializers.VARINT)
    }
}
