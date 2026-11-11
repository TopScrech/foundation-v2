package micdoodle8.mods.galacticraft.planets.asteroids.tile

import net.minecraft.network.chat.Component

import net.minecraft.world.ContainerHelper
import foundation.level.offset

import micdoodle8.mods.galacticraft.core.blocks.BlockMulti.EnumBlockMultiType
import micdoodle8.mods.miccore.Annotations.NetworkedField

import io.netty.buffer.ByteBuf
import java.util.EnumSet
import kotlin.math.pow
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.BlockMulti
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlock
import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import micdoodle8.mods.galacticraft.core.tile.IMultiBlock
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets
import micdoodle8.mods.galacticraft.planets.GuiIdsPlanets
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.AsteroidBlocks
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.BlockTelepadFake
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.ShortRangeTelepadHandler
import micdoodle8.mods.galacticraft.planets.asteroids.network.PacketSimpleAsteroids
import micdoodle8.mods.miccore.Annotations
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.WorldlyContainer as ISidedInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.NonNullList
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos

import net.minecraft.world.level.Level as World
import net.minecraftforge.fml.LogicalSide as Side
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.LinkedList
import java.util.Random
import kotlin.math.max
import kotlin.math.min

open class TileEntityShortRangeTelepad constructor() : TileBaseElectricBlock(), IMultiBlock, IInventoryDefaults,
    ISidedInventory {
    override fun getDisplayName(): net.minecraft.network.chat.Component = super<TileBaseElectricBlock>.getDisplayName()

    enum class EnumTelepadSearchResult {
        VALID,
        NOT_FOUND,
        TOO_FAR,
        WRONG_DIM,
        TARGET_DISABLED
    }

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var address: Int = -1

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var addressValid: Boolean = false

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var targetAddress: Int = -1

    @JvmField
    var targetAddressResult: EnumTelepadSearchResult = EnumTelepadSearchResult.NOT_FOUND

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var teleportTime: Int = 0

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var owner: String = ""
    private var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(1, ItemStack.EMPTY)

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var teleporting: Boolean = false
    private var renderAABB: AxisAlignedBB? = null

    init {
        this.storage.setMaxExtract((if (ConfigManagerCore.hardMode) 115 else 50).toFloat())
    }

    open fun canTeleportHere(): Int {
        if (this.world.isRemote) {
            return -1
        }

        this.setAddress(this.address)
        this.setTargetAddress(this.targetAddress)

        if (!this.addressValid) {
            return 1
        }

        if (this.getEnergyStoredGC() < ENERGY_USE_ON_TELEPORT) {
            return 2
        }

        return 0 // GOOD
    }

    override fun update() {
        if (this.ticks % 40 == 0 && !this.world.isRemote) {
            this.setAddress(this.address)
            this.setTargetAddress(this.targetAddress)
        }

        if (!this.world.isRemote) {
            if (!this.getDisabled(0) && this.targetAddressResult == EnumTelepadSearchResult.VALID && (this.ticks % 5 == 0 || teleporting)) {
                val containedEntities: MutableList<*> = this.world.getEntitiesWithinAABB<EntityLivingBase?>(
                    EntityLivingBase::class.java, AxisAlignedBB(
                        this.getPos().getX().toDouble(),
                        this.getPos().getY().toDouble(),
                        this.getPos().getZ().toDouble(),
                        (this.getPos().getX() + 1).toDouble(),
                        (this.getPos().getY() + 2).toDouble(),
                        (this.getPos().getZ() + 1).toDouble()
                    )
                )

                if (containedEntities.size > 0 && this.getEnergyStoredGC() >= ENERGY_USE_ON_TELEPORT) {
                    val entry: ShortRangeTelepadHandler.TelepadEntry? =
                        ShortRangeTelepadHandler.getLocationFromAddress(this.targetAddress)

                    if (entry != null) {
                        teleporting = true
                    }
                } else {
                    teleporting = false
                }
            }

            if (this.teleporting) {
                this.teleportTime++

                if (teleportTime >= MAX_TELEPORT_TIME) {
                    val entry: ShortRangeTelepadHandler.TelepadEntry? =
                        ShortRangeTelepadHandler.getLocationFromAddress(this.targetAddress)

                    val finalPos: BlockVec3? = if (entry == null) null else entry.position

                    if (finalPos != null) {
                        val tileAt: TileEntity? = finalPos.getTileEntity(this.world)
                        val containedEntities: MutableList<EntityLivingBase> =
                            this.world.getEntitiesWithinAABB<EntityLivingBase>(
                                EntityLivingBase::class.java, AxisAlignedBB(
                                    this.getPos().getX().toDouble(),
                                    this.getPos().getY().toDouble(),
                                    this.getPos().getZ().toDouble(),
                                    (this.getPos().getX() + 1).toDouble(),
                                    (this.getPos().getY() + 2).toDouble(),
                                    (this.getPos().getZ() + 1).toDouble()
                                )
                            )

                        if (tileAt is TileEntityShortRangeTelepad) {
                            val destTelepad: TileEntityShortRangeTelepad = tileAt
                            val teleportResult: Int = destTelepad.canTeleportHere()
                            if (teleportResult == 0) {
                                for (e: EntityLivingBase in containedEntities) {
                                    e.setPosition(
                                        (finalPos.x + 0.5f).toDouble(),
                                        (finalPos.y + 0.08f).toDouble(),
                                        (finalPos.z + 0.5f).toDouble()
                                    )
                                    this.world.updateEntityWithOptionalForce(e, true)
                                    if (e is EntityPlayerMP) {
                                        e.connection.setPlayerLocation(
                                            finalPos.x.toDouble(),
                                            finalPos.y.toDouble(),
                                            finalPos.z.toDouble(),
                                            e.yRot,
                                            e.xRot
                                        )
                                    }
                                    (GalacticraftCore.packetPipeline)!!.sendToDimension(
                                        PacketSimpleAsteroids(
                                            PacketSimpleAsteroids.EnumSimplePacketAsteroids.C_TELEPAD_SEND,
                                            GCCoreUtil.getDimensionID(this.world),
                                            arrayOf<Any>(finalPos, e.getId())
                                        ), GCCoreUtil.getDimensionID(this.world)
                                    )
                                }

                                if (containedEntities.size > 0) {
                                    this.storage.setEnergyStored(this.storage.getEnergyStoredGC() - ENERGY_USE_ON_TELEPORT)
                                    destTelepad.storage.setEnergyStored(this.storage.getEnergyStoredGC() - ENERGY_USE_ON_TELEPORT)
                                }
                            } else {
                                when (teleportResult) {
                                    -1 -> for (e: EntityLivingBase? in containedEntities) {
                                        if (e is EntityPlayer) {
                                            e.sendSystemMessage(Component.literal("Cannot Send client-side")) // No need for translation, since this should never happen
                                        }
                                    }

                                    1 -> for (e: EntityLivingBase? in containedEntities) {
                                        if (e is EntityPlayer) {
                                            e.sendSystemMessage(Component.literal("Target address invalid")) // No need for translation, since this should never happen
                                        }
                                    }

                                    2 -> for (e: EntityLivingBase? in containedEntities) {
                                        if (e is EntityPlayer) {
                                            e.sendSystemMessage(Component.literal(GCCoreUtil.translate("gui.message.target_no_energy.name")))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    this.teleportTime = 0
                    this.teleporting = false
                }
            } else {
                this.teleportTime = max(--this.teleportTime, 0)
            }
        }

        super.update()
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)

        this.stacks = NonNullList.withSize<ItemStack>(this.getContainerSize(), ItemStack.EMPTY)
        ItemStackHelper.loadAllItems(nbt, this.stacks)

        if (GCCoreUtil.getEffectiveSide() == Side.SERVER) {
            this.setAddress(nbt.getIntOr("Address", 0))
        }
        this.targetAddress = nbt.getIntOr("TargetAddress", 0)
        this.owner = nbt.getStringOr("Owner", "")
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        ItemStackHelper.saveAllItems(nbt, this.stacks)

        nbt.putInt("TargetAddress", this.targetAddress)
        nbt.putInt("Address", this.address)
        nbt.putString("Owner", this.owner)
        return nbt
    }

    override fun addExtraNetworkedData(networkedList: MutableList<Any?>?) {
        super.addExtraNetworkedData(networkedList)
        (networkedList)!!.add(targetAddressResult.ordinal)
    }

    override fun readExtraNetworkedData(dataStream: ByteBuf?) {
        super.readExtraNetworkedData(dataStream)
        targetAddressResult = EnumTelepadSearchResult.entries[(dataStream)!!.readInt()]
    }

    override fun getPacketRange(): Double {
        return 24.0
    }

    override fun onActivated(entityPlayer: EntityPlayer?): Boolean {
        (entityPlayer)!!.openGui(
            GalacticraftPlanets.instance,
            GuiIdsPlanets.MACHINE_ASTEROIDS,
            this.world,
            this.getPos().getX(),
            this.getPos().getY(),
            this.getPos().getZ()
        )
        return true
    }

    override fun onCreate(world: World?, placedPosition: BlockPos?) {
        val positions: MutableList<BlockPos> = LinkedList<BlockPos>()
        this.getPositions(placedPosition, (positions as MutableList<BlockPos?>?))
        for (vecToAdd: BlockPos in positions) (AsteroidBlocks.fakeTelepad as BlockTelepadFake).makeFakeBlock(
            (world)!!,
            vecToAdd,
            placedPosition,
            ((AsteroidBlocks.fakeTelepad)!!.defaultBlockState()
                .setValue(BlockTelepadFake.TOP, vecToAdd.getY() == (placedPosition)!!.getY() + 2))!!
        )
    }

    override fun getMultiType(): BlockMulti.EnumBlockMultiType {
        //Not actually used - maybe this shouldn't be an IMultiBlock at all?
        return BlockMulti.EnumBlockMultiType.MINER_BASE
    }

    override fun getPositions(placedPosition: BlockPos?, positions: MutableList<BlockPos?>?) {
        val buildHeight: Int = this.world.getHeight() - 1
        var y: Int = 0
        while (y < 3) {
            if ((placedPosition)!!.getY() + y > buildHeight) {
                return
            }
            for (x: Int in -1..1) {
                for (z: Int in -1..1) {
                    if (x == 0 && y == 0 && z == 0) continue
                    (positions)!!.add((placedPosition)!!.offset(x, y, z))
                }
            }
            y += 2
        }
    }

    override fun onDestroy(callingBlock: TileEntity?) {
        val thisBlock: BlockPos = getPos()
        val positions: MutableList<BlockPos> = LinkedList<BlockPos>()
        this.getPositions(thisBlock, (positions as MutableList<BlockPos?>?))

        for (pos: BlockPos in positions) {
            val stateAt: IBlockState = this.world.getBlockState(pos)

            if (stateAt.getBlock() === AsteroidBlocks.fakeTelepad) {
                this.world.destroyBlock(pos, false)
            }
        }
        this.world.destroyBlock(thisBlock, true)
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRenderBoundingBox(): AxisAlignedBB {
        if (this.renderAABB == null) {
            this.renderAABB = AxisAlignedBB(
                (getPos().getX() - 1).toDouble(),
                getPos().getY().toDouble(),
                (getPos().getZ() - 1).toDouble(),
                (getPos().getX() + 2).toDouble(),
                (getPos().getY() + 4).toDouble(),
                (getPos().getZ() + 2).toDouble()
            )
        }
        return this.renderAABB!!
    }

    @OnlyIn(Dist.CLIENT)
    override fun getMaxRenderDistanceSquared(): Double {
        return Constants.RENDERDISTANCE_MEDIUM
    }

    override fun getName(): net.minecraft.network.chat.Component {
        return Component.translatable("container.short_range_telepad.name")
    }

    override fun getSlotsForFace(side: EnumFacing): IntArray {
        return intArrayOf(0)
    }

    override fun hasCustomName(): Boolean {
        return true
    }

    override fun getMaxStackSize(): Int {
        return 64
    }

    override fun stillValid(par1EntityPlayer: EntityPlayer): Boolean {
        return this.world.getBlockEntity(getPos()) === this && par1EntityPlayer.getDistanceSq(
            this.getPos().getX() + 0.5,
            this.getPos().getY() + 0.5,
            this.getPos().getZ() + 0.5
        ) <= 64.0
    }

    override fun canPlaceItem(slotID: Int, itemStack: ItemStack): Boolean {
        return slotID == 0 && ItemElectricBase.isElectricItem(itemStack.getItem())
    }

    override fun canTakeItemThroughFace(index: Int, stack: ItemStack, direction: EnumFacing): Boolean {
        return index == 0
    }

    override fun canPlaceItemThroughFace(index: Int, itemStackIn: ItemStack, direction: EnumFacing?): Boolean {
        return this.canPlaceItem(index, itemStackIn)
    }

    override fun getElectricalOutputDirections(): EnumSet<EnumFacing?> {
        return EnumSet.noneOf<EnumFacing?>(EnumFacing::class.java)
    }

    override fun shouldUseEnergy(): Boolean {
        return !this.getDisabled(0)
    }

    override fun getElectricInputDirection(): EnumFacing? {
        return EnumFacing.from3DDataValue((this.getBlockMetadata() and 3) + 2)
    }

    override fun getBatteryInSlot(): ItemStack {
        return this.getItem(0)
    }

    override fun setDisabled(index: Int, disabled: Boolean) {
        if (this.disableCooldown == 0) {
            when (index) {
                0 -> {
                    this.disabled = disabled
                    this.disableCooldown = 10
                    if (world != null && !world.isRemote) {
                        ShortRangeTelepadHandler.addShortRangeTelepad(this)
                    }
                }

                else -> {}
            }
        }
    }

    override fun getDisabled(index: Int): Boolean {
        when (index) {
            0 -> return this.disabled
            else -> {}
        }

        return true
    }

    open fun setAddress(address: Int) {
        if (this.world != null && address != this.address) {
            ShortRangeTelepadHandler.removeShortRangeTeleporter(this)
        }

        this.address = address

        if (this.address >= 0) {
            val entry: ShortRangeTelepadHandler.TelepadEntry? =
                ShortRangeTelepadHandler.getLocationFromAddress(this.address)
            this.addressValid =
                entry == null || (this.world != null && (entry.dimensionID == GCCoreUtil.getDimensionID(this.world) && entry.position.x == this.getPos()
                    .getX() && entry.position.y == this.getPos().getY() && entry.position.z == this.getPos().getZ()))
        } else {
            this.addressValid = false
        }

        if (this.world != null && !this.world.isRemote) {
            ShortRangeTelepadHandler.addShortRangeTelepad(this)
        }
    }

    open fun updateTarget(): Boolean {
        if (this.targetAddress >= 0 && !this.world.isRemote) {
            this.targetAddressResult = EnumTelepadSearchResult.NOT_FOUND

            val addressResult: ShortRangeTelepadHandler.TelepadEntry? =
                ShortRangeTelepadHandler.getLocationFromAddress(this.targetAddress)

            if (addressResult != null) {
                if (GCCoreUtil.getDimensionID(this.world) == addressResult.dimensionID) {
                    val distance: Double = this.getDistanceSq(
                        (addressResult.position.x + 0.5f).toDouble(),
                        (addressResult.position.y + 0.5f).toDouble(),
                        (addressResult.position.z + 0.5f).toDouble()
                    )

                    if (distance < (TELEPORTER_RANGE * TELEPORTER_RANGE).toDouble().pow(2.0)) {
                        if (!addressResult.enabled) {
                            this.targetAddressResult = EnumTelepadSearchResult.TARGET_DISABLED
                            return false
                        }

                        this.targetAddressResult = EnumTelepadSearchResult.VALID
                        return true
                    } else {
                        this.targetAddressResult = EnumTelepadSearchResult.TOO_FAR
                        return false
                    }
                } else {
                    this.targetAddressResult = EnumTelepadSearchResult.WRONG_DIM
                    return false
                }
            } else {
                this.targetAddressResult = EnumTelepadSearchResult.NOT_FOUND
                return false
            }
        } else {
            this.targetAddressResult = EnumTelepadSearchResult.NOT_FOUND
            return false
        }
    }

    open fun setTargetAddress(address: Int) {
        this.targetAddress = address
        this.updateTarget()
    }

    override fun getContainerSize(): Int {
        return this.stacks.size
    }

    override fun getItem(var1: Int): ItemStack {
        return this.stacks.get(var1)
    }

    override fun removeItem(index: Int, count: Int): ItemStack {
        val itemstack: ItemStack = ContainerHelper.removeItem(this.stacks, index, count)

        if (!itemstack.isEmpty()) {
            this.setChanged()
        }

        return itemstack
    }

    override fun removeItemNoUpdate(index: Int): ItemStack {
        val oldstack: ItemStack = ContainerHelper.takeItem(this.stacks, index)
        if (!oldstack.isEmpty()) {
            this.setChanged()
        }
        return oldstack
    }

    override fun setItem(index: Int, stack: ItemStack) {
        this.stacks.set(index, stack)

        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize())
        }

        this.setChanged()
    }

    override fun isEmpty(): Boolean {
        for (itemstack: ItemStack in this.stacks) {
            if (!itemstack.isEmpty()) {
                return false
            }
        }

        return true
    }

    open fun setOwner(owner: String) {
        this.owner = owner
    }

    open fun getOwner(): String? {
        return this.owner
    }

    @OnlyIn(Dist.CLIENT)
    open fun getReceivingStatus(): String {
        if (!this.addressValid) {
            return EnumColor.RED.toString() + GCCoreUtil.translate("gui.message.invalid_address.name")
        }

        if (this.getEnergyStoredGC() <= 0.0f) {
            return EnumColor.RED.toString() + GCCoreUtil.translate("gui.message.no_energy.name")
        }

        if (this.getEnergyStoredGC() <= ENERGY_USE_ON_TELEPORT) {
            return EnumColor.RED.toString() + GCCoreUtil.translate("gui.message.not_enough_energy.name")
        }

        if (this.getDisabled(0)) {
            return EnumColor.ORANGE.toString() + GCCoreUtil.translate("gui.status.disabled.name")
        }

        return EnumColor.BRIGHT_GREEN.toString() + GCCoreUtil.translate("gui.message.receiving_active.name")
    }

    @OnlyIn(Dist.CLIENT)
    open fun getSendingStatus(): String {
        if (!this.addressValid) {
            return EnumColor.RED.toString() + GCCoreUtil.translate("gui.message.invalid_target_address.name")
        }

        if (this.targetAddressResult == EnumTelepadSearchResult.TOO_FAR) {
            return EnumColor.RED.toString() + GCCoreUtil.translateWithFormat(
                "gui.message.telepad_too_far.name",
                TELEPORTER_RANGE
            )
        }

        if (this.targetAddressResult == EnumTelepadSearchResult.WRONG_DIM) {
            return EnumColor.RED.toString() + GCCoreUtil.translate("gui.message.telepad_wrong_dim.name")
        }

        if (this.targetAddressResult == EnumTelepadSearchResult.NOT_FOUND) {
            return EnumColor.RED.toString() + GCCoreUtil.translate("gui.message.telepad_not_found.name")
        }

        if (this.targetAddressResult == EnumTelepadSearchResult.TARGET_DISABLED) {
            return EnumColor.ORANGE.toString() + GCCoreUtil.translate("gui.message.telepad_target_disabled.name")
        }

        if (this.getEnergyStoredGC() <= 0.0f) {
            return EnumColor.RED.toString() + GCCoreUtil.translate("gui.message.no_energy.name")
        }

        if (this.getEnergyStoredGC() <= ENERGY_USE_ON_TELEPORT) {
            return EnumColor.RED.toString() + GCCoreUtil.translate("gui.message.not_enough_energy.name")
        }

        if (this.getDisabled(0)) {
            return EnumColor.ORANGE.toString() + GCCoreUtil.translate("gui.status.disabled.name")
        }

        return EnumColor.BRIGHT_GREEN.toString() + GCCoreUtil.translate("gui.message.sending_active.name")
    }

    @OnlyIn(Dist.CLIENT)
    open fun getParticleColor(rand: Random, sending: Boolean): Vector3 {
        val teleportTimeScaled: Float = min(1.0f, this.teleportTime / MAX_TELEPORT_TIME.toFloat())
        val f: Float
        f = rand.nextFloat() * 0.6f + 0.4f

        if (sending && this.targetAddressResult != EnumTelepadSearchResult.VALID) {
            return Vector3(f.toDouble(), (f * 0.3f).toDouble(), (f * 0.3f).toDouble())
        }

        if (!sending && !this.addressValid) {
            return Vector3(f.toDouble(), (f * 0.3f).toDouble(), (f * 0.3f).toDouble())
        }

        if (this.getEnergyStoredGC() < ENERGY_USE_ON_TELEPORT) {
            return Vector3(f.toDouble(), (f * 0.6f).toDouble(), (f * 0.3f).toDouble())
        }

        val r: Float = f * 0.3f
        val g: Float = f * (0.3f + (teleportTimeScaled * 0.7f))
        val b: Float = f * (1.0f - (teleportTimeScaled * 0.7f))

        return Vector3(r.toDouble(), g.toDouble(), b.toDouble())
    }

    override fun getFront(): EnumFacing {
        return EnumFacing.NORTH
    }

    companion object {
        const val MAX_TELEPORT_TIME: Int = 150

        const val TELEPORTER_RANGE: Int = 256

        const val ENERGY_USE_ON_TELEPORT: Int = 2500
    }
}
