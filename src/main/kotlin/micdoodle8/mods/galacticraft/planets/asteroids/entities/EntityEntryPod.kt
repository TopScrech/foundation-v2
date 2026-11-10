package micdoodle8.mods.galacticraft.planets.asteroids.entities

import micdoodle8.mods.galacticraft.api.entity.ICameraZoomEntity
import micdoodle8.mods.galacticraft.api.entity.IIgnoreShift
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.entities.EntityLanderBase
import micdoodle8.mods.galacticraft.core.entities.IScaleableFuelLevel
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.particle.Particle
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.Random

open class EntityEntryPod : EntityLanderBase, IScaleableFuelLevel, ICameraZoomEntity, IIgnoreShift {
    constructor(var1: World?) : super(var1) {
        this.setSize(1.5f, 3.0f)
    }

    constructor(player: EntityPlayerMP) : super(player, 0.0f) {
        this.setSize(1.5f, 3.0f)
    }

    override fun getInitialMotionY(): Double {
        return -0.5
    }

    override fun getMountedYOffset(): Double {
        return this.height - 2.0
    }

    override fun getRotateOffset(): Float {
        //Signal no rotate
        return -20.0f
    }

    override fun shouldSpawnParticles(): Boolean {
        return false
    }

    override fun getParticleMap(): MutableMap<Vector3?, Vector3?>? {
        return null
    }

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

    override fun tickOnGround() {
    }

    override fun tickInAir() {
        super.tickInAir()

        if (this.world.isRemote) {
            if (!this.onGround) {
                this.motionY -= 0.002
            }
        }
    }

    override fun onGroundHit() {
        val pos: BlockPos = BlockPos(this).up(2)
        this.world.setBlockState(pos, (GCBlocks.brightAir)!!.getDefaultState(), 2)
    }

    override fun getMotionVec(): Vector3 {
        if (this.onGround) {
            return Vector3(0.0, 0.0, 0.0)
        }

        if (this.ticks >= 40 && this.ticks < 45) {
            this.motionY = this.getInitialMotionY()
        }

        return Vector3(this.motionX, this.motionY, this.motionZ)
    }

    override fun getCameraZoom(): Float {
        return 15.0f
    }

    override fun defaultThirdPerson(): Boolean {
        return true
    }

    override fun pressKey(key: Int): Boolean {
        return false
    }

    override fun getName(): String {
        return GCCoreUtil.translate("container.entry_pod.name")
    }

    override fun hasCustomName(): Boolean {
        return true
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

    override fun shouldIgnoreShiftExit(): Boolean {
        return !this.onGround
    }
}
