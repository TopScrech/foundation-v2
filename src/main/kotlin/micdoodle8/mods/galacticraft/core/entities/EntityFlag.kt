package micdoodle8.mods.galacticraft.core.entities

import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.util.ClientUtil
import micdoodle8.mods.galacticraft.core.wrappers.FlagData
import net.minecraft.world.level.block.Block
import net.minecraft.block.BlockFence
import net.minecraft.world.level.block.SoundType
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.MoverType
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.network.datasync.DataParameter
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.network.datasync.EntityDataManager
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.sounds.SoundSource as SoundCategory
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.level.Level as World

open class EntityFlag constructor(world: World) : Entity(world) {
    @JvmField
    var xPosition: Double = 0.0

    @JvmField
    var yPosition: Double = 0.0

    @JvmField
    var zPosition: Double = 0.0

    @JvmField
    var indestructable: Boolean = false

    @JvmField
    var flagData: FlagData? = null

    init {
        this.setSize(0.4f, 3f)
        this.ignoreFrustumCheck = true
    }

    constructor(par1World: World, x: Double, y: Double, z: Double, dir: Int) : this(par1World) {
        this.setFacingAngle(dir)
        this.setPosition(x, y, z)
        this.xPosition = x
        this.yPosition = y
        this.zPosition = z
    }

    override fun attackEntityFrom(par1DamageSource: DamageSource, par2: Float): Boolean {
        val e: Entity? = par1DamageSource.getTrueSource()
        val flag: Boolean = e is EntityPlayer && e.capabilities.isCreativeMode

        if (!this.world.isRemote && !this.isDead && !this.indestructable) {
            if (this.isEntityInvulnerable(par1DamageSource)) {
                return false
            } else {
                this.markVelocityChanged()
                this.setDamage(this.getDamage() + par2 * 10)
                this.world.playSound(
                    null,
                    this.posX,
                    this.posY,
                    this.posZ,
                    SoundType.METAL.getBreakSound(),
                    SoundCategory.BLOCKS,
                    SoundType.METAL.getVolume(),
                    SoundType.METAL.getPitch() + 1.0f
                )

                if (e is EntityPlayer && e.capabilities.isCreativeMode) {
                    this.setDamage(100.0f)
                }

                if (flag || this.getDamage() > 40) {
                    if (!this.getPassengers().isEmpty()) {
                        this.removePassengers()
                    }

                    if (flag) {
                        this.setDead()
                    } else {
                        this.setDead()
                        this.dropItemStack()
                    }
                }

                return true
            }
        } else {
            return true
        }
    }

    override fun getPickedResult(target: RayTraceResult): ItemStack {
        return ItemStack(GCItems.flag, 1, this.getType())
    }

    open fun getWidth(): Int {
        return 25
    }

    open fun getHeight(): Int {
        return 40
    }

    override fun canBeCollidedWith(): Boolean {
        return true
    }

    override fun canTriggerWalking(): Boolean {
        return false
    }

    override fun getCollisionBox(par1Entity: Entity): AxisAlignedBB? {
        return par1Entity.getCollisionBoundingBox()
    }

    override fun canBePushed(): Boolean {
        return false
    }

    override fun entityInit() {
        this.dataManager.register<String?>(OWNER, "")
        this.dataManager.register<Float>(DAMAGE, 0.0f)
        this.dataManager.register<Int>(TYPE, -1)
        this.dataManager.register<Int>(FACING_ANGLE, -1)
    }

    public override fun readEntityFromNBT(par1NBTTagCompound: NBTTagCompound) {
        this.setOwner(par1NBTTagCompound.getStringOr("Owner", ""))
        this.setType(par1NBTTagCompound.getIntOr("Type", 0))
        this.indestructable = par1NBTTagCompound.getBooleanOr("Indestructable", false)

        this.xPosition = par1NBTTagCompound.getDoubleOr("TileX", 0.0)
        this.yPosition = par1NBTTagCompound.getDoubleOr("TileY", 0.0)
        this.zPosition = par1NBTTagCompound.getDoubleOr("TileZ", 0.0)
        this.setFacingAngle(par1NBTTagCompound.getIntOr("AngleI", 0))
    }

    override fun writeEntityToNBT(par1NBTTagCompound: NBTTagCompound) {
        par1NBTTagCompound.putString("Owner", this.getOwner().toString())
        par1NBTTagCompound.putInt("Type", this.getType())
        par1NBTTagCompound.putBoolean("Indestructable", this.indestructable)
        par1NBTTagCompound.putInt("AngleI", this.getFacingAngle())
        par1NBTTagCompound.putDouble("TileX", this.xPosition)
        par1NBTTagCompound.putDouble("TileY", this.yPosition)
        par1NBTTagCompound.putDouble("TileZ", this.zPosition)
    }

    open fun dropItemStack() {
        this.entityDropItem(ItemStack(GCItems.flag, 1, this.getType()), 0.0f)
    }

    override fun onUpdate() {
        super.onUpdate()

        if ((this.ticksExisted - 1) % 20 == 0 && this.world.isRemote) {
            this.flagData =
                ClientUtil.updateFlagData(this.getOwner(), Minecraft.getInstance().player.getDistance(this) < 50.0)
        }

        var vec: Vector3 = Vector3(this.posX, this.posY, this.posZ)
        vec = vec.translate(Vector3(0.0, -1.0, 0.0))
        val blockAt: Block? = vec.getBlock(this.world)

        if (blockAt != null) {
            val pos: BlockPos = BlockPos(vec.intX(), vec.intY(), vec.intZ())
            if (blockAt is BlockFence) {
            } else if (blockAt.isAir(this.world.getBlockState(pos), this.world, pos)) {
                this.motionY -= 0.02
            }
        }

        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ)
    }

    override fun processInitialInteract(player: EntityPlayer, hand: EnumHand): Boolean {
        if (!this.world.isRemote) {
            this.setFacingAngle(this.getFacingAngle() + 3)
        }

        return true
    }

    open fun setOwner(par1: String?) {
        this.dataManager.set<String?>(OWNER, par1.toString())
    }

    open fun getOwner(): String {
        return this.dataManager.get<String>(OWNER)
    }

    open fun setDamage(par1: Float) {
        this.dataManager.set<Float>(DAMAGE, par1)
    }

    open fun getDamage(): Float {
        return this.dataManager.get<Float>(DAMAGE)
    }

    open fun setType(par1: Int) {
        this.dataManager.set<Int>(TYPE, par1)
    }

    open fun getType(): Int {
        return this.dataManager.get<Int>(TYPE)
    }

    open fun setFacingAngle(par1: Int) {
        this.dataManager.set<Int>(FACING_ANGLE, par1)
    }

    open fun getFacingAngle(): Int {
        return this.dataManager.get<Int>(FACING_ANGLE)
    }

    companion object {
        private val OWNER: DataParameter<String?> =
            EntityDataManager.createKey<String?>(EntityFlag::class.java, DataSerializers.STRING)
        private val DAMAGE: DataParameter<Float> =
            EntityDataManager.createKey<Float>(EntityFlag::class.java, DataSerializers.FLOAT)
        private val TYPE: DataParameter<Int> =
            EntityDataManager.createKey<Int>(EntityFlag::class.java, DataSerializers.VARINT)
        private val FACING_ANGLE: DataParameter<Int> =
            EntityDataManager.createKey<Int>(EntityFlag::class.java, DataSerializers.VARINT)
    }
}
