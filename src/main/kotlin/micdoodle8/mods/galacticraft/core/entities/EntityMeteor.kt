package micdoodle8.mods.galacticraft.core.entities

import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import net.minecraft.block.BlockAir
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLiving
import net.minecraft.entity.MoverType
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.datasync.DataParameter
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.network.datasync.EntityDataManager
import net.minecraft.util.DamageSource
import net.minecraft.util.EntityDamageSourceIndirect
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraft.util.text.translation.I18n
import net.minecraft.world.Explosion
import net.minecraft.world.World

open class EntityMeteor constructor(world: World) : Entity(world) {
    @JvmField
    var shootingEntity: EntityLiving? = null

    @JvmField
    var size: Int = 0

    init {
        this.setSize(1.0f, 1.0f)
    }

    constructor(
        world: World,
        x: Double,
        y: Double,
        z: Double,
        motX: Double,
        motY: Double,
        motZ: Double,
        size: Int
    ) : this(world) {
        this.size = size
        this.setSize(1.0f, 1.0f)
        this.setLocationAndAngles(x, y, z, this.rotationYaw, this.rotationPitch)
        this.setPosition(x, y, z)
        this.motionX = motX
        this.motionY = motY
        this.motionZ = motZ
        this.setSize(size)
    }

    override fun onUpdate() {
        this.setRotation(this.rotationYaw + 2f, this.rotationPitch + 2f)
        this.prevPosX = this.posX
        this.prevPosY = this.posY
        this.prevPosZ = this.posZ
        this.motionY -= 0.03999999910593033
        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ)

        if (this.world.isRemote) {
            this.spawnParticles()
        }

        var var15: Vec3d = Vec3d(this.posX, this.posY, this.posZ)
        var var2: Vec3d = Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ)
        var var3: RayTraceResult? = this.world.rayTraceBlocks(var15, var2, true, true, false)
        var15 = Vec3d(this.posX, this.posY, this.posZ)
        var2 = Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ)

        if (var3 != null) {
            var2 = Vec3d(var3.hitVec.x, var3.hitVec.y, var3.hitVec.z)
        }

        var var4: Entity? = null
        val var5: MutableList<*> = this.world.getEntitiesWithinAABBExcludingEntity(
            this,
            this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(2.0, 2.0, 2.0)
        )
        var var6: Double = 0.0
        val var8: MutableIterator<*> = var5.iterator()
        val var10: Double = 0.01

        while (var8.hasNext()) {
            val var9: Entity = var8.next() as Entity

            if (var9.canBeCollidedWith() && !var9.isEntityEqual(this.shootingEntity)) {
                val var11: AxisAlignedBB = var9.getEntityBoundingBox().grow(var10, var10, var10)
                val var12: RayTraceResult? = var11.calculateIntercept(var15, var2)

                if (var12 != null) {
                    val var13: Double = var15.distanceTo(var12.hitVec)

                    if (var13 < var6 || var6 == 0.0) {
                        var4 = var9
                        var6 = var13
                    }
                }
            }
        }

        if (var4 != null) {
            var3 = RayTraceResult(var4)
        }

        if (var3 != null) {
            this.onImpact(var3)
        }

        if (this.posY <= -20 || this.posY >= 400) {
            this.setDead()
        }
    }

    protected open fun spawnParticles() {
        (GalacticraftCore.proxy)!!.spawnParticle(
            "distanceSmoke",
            Vector3(this.posX, this.posY + 1.0 + Math.random(), this.posZ),
            Vector3(0.0, 0.0, 0.0),
            arrayOf<Any?>()
        )
        (GalacticraftCore.proxy)!!.spawnParticle(
            "distanceSmoke",
            Vector3(this.posX + Math.random() / 2, this.posY + 1.0 + Math.random() / 2, this.posZ),
            Vector3(0.0, 0.0, 0.0),
            arrayOf<Any?>()
        )
        (GalacticraftCore.proxy)!!.spawnParticle(
            "distanceSmoke",
            Vector3(this.posX, this.posY + 1.0 + Math.random(), this.posZ + Math.random()),
            Vector3(0.0, 0.0, 0.0),
            arrayOf<Any?>()
        )
        (GalacticraftCore.proxy)!!.spawnParticle(
            "distanceSmoke",
            Vector3(this.posX - Math.random() / 2, this.posY + 1.0 + Math.random() / 2, this.posZ),
            Vector3(0.0, 0.0, 0.0),
            arrayOf<Any?>()
        )
        (GalacticraftCore.proxy)!!.spawnParticle(
            "distanceSmoke",
            Vector3(this.posX, this.posY + 1.0 + Math.random(), this.posZ - Math.random()),
            Vector3(0.0, 0.0, 0.0),
            arrayOf<Any?>()
        )
    }

    protected open fun onImpact(movingObjPos: RayTraceResult?) {
        if (!this.world.isRemote) {
            if (movingObjPos != null) {
                var pos: BlockPos = movingObjPos.getBlockPos()
                if (pos == null) {
                    if (movingObjPos.entityHit != null) {
                        pos = this.world.getTopSolidOrLiquidBlock(movingObjPos.entityHit.getPosition())
                    } else {
                        pos = this.world.getTopSolidOrLiquidBlock(this.getPosition())
                    }
                }
                val above: BlockPos = pos.up()
                if (this.world.getBlockState(above).getBlock() is BlockAir) {
                    this.world.setBlockState(above, (GCBlocks.fallenMeteor)!!.getDefaultState(), 3)
                }

                if (movingObjPos.entityHit != null) {
                    movingObjPos.entityHit.attackEntityFrom(
                        causeMeteorDamage(this, this.shootingEntity),
                        if (ConfigManagerCore.hardMode) 12f else 6f
                    )
                }
            }

            this.world.newExplosion(this, this.posX, this.posY, this.posZ, (this.size / 3 + 2).toFloat(), false, true)
        }

        this.setDead()
    }

    override fun canExplosionDestroyBlock(
        explosionIn: Explosion,
        worldIn: World,
        pos: BlockPos,
        blockStateIn: IBlockState,
        p_174816_5_: Float
    ): Boolean {
        return ConfigManagerCore.meteorBlockDamageEnabled
    }

    override fun entityInit() {
        this.dataManager.register<Int>(SIZE, this.size)
        this.noClip = true
    }

    open fun getSize(): Int {
        return this.dataManager.get<Int>(SIZE)
    }

    /**
     * Sets the state of creeper, -1 to idle and 1 to be 'in fuse'
     */
    open fun setSize(par1: Int) {
        this.dataManager.set<Int>(SIZE, par1)
    }

    override fun readEntityFromNBT(par1NBTTagCompound: NBTTagCompound) {
    }

    override fun writeEntityToNBT(par1NBTTagCompound: NBTTagCompound) {
    }

    companion object {
        private val SIZE: DataParameter<Int> =
            EntityDataManager.createKey<Int>(EntityBuggy::class.java, DataSerializers.VARINT)

        @JvmStatic
        fun causeMeteorDamage(par0EntityMeteor: EntityMeteor, par1Entity: Entity?): DamageSource {
            if (par1Entity != null && par1Entity is EntityPlayer) {
                I18n.translateToLocalFormatted(
                    "death." + "meteor",
                    PlayerUtil.getName(par1Entity) + " was hit by a meteor! That's gotta hurt!"
                )
            }
            return EntityDamageSourceIndirect("explosion", par0EntityMeteor, par1Entity).setProjectile()
        }
    }
}
