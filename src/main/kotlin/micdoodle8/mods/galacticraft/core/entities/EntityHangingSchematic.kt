package micdoodle8.mods.galacticraft.core.entities

import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket

import micdoodle8.mods.galacticraft.api.recipe.SchematicRegistry
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityHanging
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class EntityHangingSchematic : EntityHanging {
    @JvmField
    var schematic: Int = 0
    private var sendToClient = false

    constructor(worldIn: World) : super(worldIn)

    constructor(worldIn: World, pos: BlockPos, facing: EnumFacing, meta: Int) : super(worldIn, pos) {
        this.schematic = meta
        this.updateFacingWithBoundingBox(facing)
    }

    private var tickCounter1 = 0
    override fun onUpdate() {
        this.prevPosX = this.posX
        this.prevPosY = this.posY
        this.prevPosZ = this.posZ

        if (this.sendToClient) {
            this.sendToClient = false
            this.sendToClient(this.world, this.hangingPosition)
        }

        if (this.tickCounter1++ == 10) {
            this.tickCounter1 = 0

            if (!this.world.isRemote && !this.isDead && !this.onValidSurface()) {
                this.setDead()
                this.onBroken(null as Entity?)
            }
        }
    }

    override fun setDead() {
        super.setDead()
    }

    override fun writeEntityToNBT(tagCompound: NBTTagCompound) {
        tagCompound.setInteger("schem", this.schematic)
        super.writeEntityToNBT(tagCompound)
    }

    override fun readEntityFromNBT(tag: NBTTagCompound) {
        this.schematic = tag.getInteger("schem")
        super.readEntityFromNBT(tag)
        this.setSendToClient()
    }

    override fun getWidthPixels(): Int {
        return 32
    }

    override fun getHeightPixels(): Int {
        return 32
    }

    override fun onBroken(brokenEntity: Entity?) {
        if (this.world.getGameRules().getBoolean("doEntityDrops")) {
            if (brokenEntity is EntityPlayer) {
                val entityplayer: EntityPlayer = brokenEntity

                if (entityplayer.capabilities.isCreativeMode) {
                    return
                }
            }

            this.entityDropItem(SchematicRegistry.getSchematicItem(this.schematic), 0.0f)
        }
    }

    override fun getPickedResult(target: RayTraceResult): ItemStack {
        return SchematicRegistry.getSchematicItem(this.schematic)
    }

    override fun setLocationAndAngles(x: Double, y: Double, z: Double, yaw: Float, pitch: Float) {
        val blockpos: BlockPos = this.hangingPosition.add(x - this.posX, y - this.posY, z - this.posZ)
        this.setPosition(blockpos.getX().toDouble(), blockpos.getY().toDouble(), blockpos.getZ().toDouble())
    }

    @SideOnly(Side.CLIENT)
    override fun setPositionAndRotationDirect(
        x: Double,
        y: Double,
        z: Double,
        yaw: Float,
        pitch: Float,
        posRotationIncrements: Int,
        teleport: Boolean
    ) {
    }

    open fun sendToClient(worldIn: World, blockpos: BlockPos) {
        val dimID: Int = GCCoreUtil.getDimensionID(worldIn)
        GCCoreUtil.sendToAllAround(
            PacketSimple(
                PacketSimple.EnumSimplePacket.C_SPAWN_HANGING_SCHEMATIC,
                dimID,
                arrayOf<Any?>(blockpos, this.getEntityId(), this.facingDirection!!.ordinal, this.schematic)
            ), worldIn, dimID, blockpos, 150.0
        )
    }

    open fun setSendToClient() {
        this.sendToClient = true
    }

    override fun playPlaceSound() {
        this.playSound(SoundEvents.ENTITY_PAINTING_PLACE, 1.0f, 1.0f)
    }
}
