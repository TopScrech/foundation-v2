package micdoodle8.mods.galacticraft.planets.mars.tile

import micdoodle8.mods.galacticraft.planets.mars.network.PacketSimpleMars.EnumSimplePacketMars
import micdoodle8.mods.miccore.Annotations.NetworkedField
import net.minecraftforge.common.ForgeChunkManager.Ticket

import micdoodle8.mods.galacticraft.api.entity.IDockable
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityAutoRocket
import micdoodle8.mods.galacticraft.api.tile.IFuelDock
import micdoodle8.mods.galacticraft.api.tile.ILandingPadAttachable
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.BlockLandingPadFull
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlockWithInventory
import micdoodle8.mods.galacticraft.core.tile.TileEntityLandingPad
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.world.ChunkLoadingCallback
import micdoodle8.mods.galacticraft.core.world.IChunkLoader
import micdoodle8.mods.galacticraft.planets.mars.ConfigManagerMars
import micdoodle8.mods.galacticraft.planets.mars.blocks.BlockMachineMars
import micdoodle8.mods.galacticraft.planets.mars.network.PacketSimpleMars
import micdoodle8.mods.miccore.Annotations
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.WorldlyContainer as ISidedInventory
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.NonNullList
import net.minecraft.core.BlockPos
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.server.level.ServerLevel as WorldServer
import net.minecraftforge.common.ForgeChunkManager
import net.minecraftforge.fml.LogicalSide as Side
import java.util.ArrayList

// import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
// import micdoodle8.mods.galacticraft.core.util.GCLog;
// import java.util.HashMap;
// import java.util.Map;
open class TileEntityLaunchController constructor() : TileBaseElectricBlockWithInventory(), IChunkLoader,
    ISidedInventory, ILandingPadAttachable {
    private var stacks: NonNullList<ItemStack>? = NonNullList.withSize<ItemStack>(1, ItemStack.EMPTY)

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var launchPadRemovalDisabled: Boolean = true
    private var chunkLoadTicket: ForgeChunkManager.Ticket? = null
    private val connectedPads: MutableList<BlockPos> = ArrayList<BlockPos>()

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var frequency: Int = -1

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var destFrequency: Int = -1

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var ownerName: String = ""

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var frequencyValid: Boolean = false

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var destFrequencyValid: Boolean = false

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var launchDropdownSelection: Int = 0

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var launchSchedulingEnabled: Boolean = false

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var controlEnabled: Boolean = false

    @JvmField
    var hideTargetDestination: Boolean = true

    @JvmField
    var requiresClientUpdate: Boolean = false

    @JvmField
    var attachedDock: Any? = null
    private var frequencyCheckNeeded = false

    //    private static Map<Integer, Long> tickCounts = new HashMap<>();
    //    private static Map<Integer, Integer> instanceCounts = new HashMap<>();
    init {
        this.storage.setMaxExtract(6f)
        this.noRedstoneControl = true
    }

    override fun update() {
        super.update()

        if (!this.world.isRemote) {
            this.controlEnabled = this.launchSchedulingEnabled && this.hasEnoughEnergyToRun && !this.getDisabled(0)

            if (this.frequencyCheckNeeded) {
                this.checkDestFrequencyValid()
                this.frequencyCheckNeeded = false
            }

            if (this.requiresClientUpdate) {
                // PacketDispatcher.sendPacketToAllPlayers(this.getPacket());
                // TODO
                this.requiresClientUpdate = false
            }

            if (this.ticks % 40 == 0) {
                this.setFrequency(this.frequency)
                this.setDestinationFrequency(this.destFrequency)
            }

            if (this.ticks % 20 == 0) {
                if (this.chunkLoadTicket != null) {
                    for (i: Int in this.connectedPads.indices) {
                        val coords: BlockPos = this.connectedPads.get(i)
                        val block: Block = this.world.getBlockState(coords).getBlock()

                        if (block !== GCBlocks.landingPadFull) {
                            this.connectedPads.removeAt(i)
                            ForgeChunkManager.unforceChunk(
                                this.chunkLoadTicket,
                                ChunkPos(coords.getX() shr 4, coords.getZ() shr 4)
                            )
                        }
                    }
                }
            }
        } else {
            if (this.frequency == -1 && this.destFrequency == -1) {
                (GalacticraftCore.packetPipeline)!!.sendToServer(
                    PacketSimpleMars(
                        PacketSimpleMars.EnumSimplePacketMars.S_UPDATE_ADVANCED_GUI,
                        GCCoreUtil.getDimensionID(this.world),
                        arrayOf<Any>(5, this.getPos(), 0)
                    )
                )
            }
        }
    }

    override fun getOwnerName(): String? {
        return this.ownerName
    }

    override fun setOwnerName(ownerName: String?) {
        this.ownerName = (ownerName)!!
    }

    override fun invalidate() {
        super.invalidate()

        if (this.chunkLoadTicket != null) {
            ForgeChunkManager.releaseTicket(this.chunkLoadTicket)
        }
    }

    override fun onTicketLoaded(ticket: ForgeChunkManager.Ticket?, placed: Boolean) {
        if (!this.world.isRemote && ConfigManagerMars.launchControllerChunkLoad) {
            if (ticket == null) {
                return
            }

            if (this.chunkLoadTicket == null) {
                this.chunkLoadTicket = ticket
            }

            val nbt: NBTTagCompound = this.chunkLoadTicket!!.getModData()
            nbt.putInt("ChunkLoaderTileX", this.getPos().getX())
            nbt.putInt("ChunkLoaderTileY", this.getPos().getY())
            nbt.putInt("ChunkLoaderTileZ", this.getPos().getZ())

            for (x: Int in -2..2) {
                for (z: Int in -2..2) {
                    val blockID: Block = this.world.getBlockState(this.getPos().add(x, 0, z)).getBlock()

                    if (blockID is BlockLandingPadFull) {
                        if (this.getPos().getX() + x shr 4 != this.getPos().getX() shr 4 || this.getPos()
                                .getZ() + z shr 4 != this.getPos().getZ() shr 4
                        ) {
                            this.connectedPads.add(
                                BlockPos(
                                    this.getPos().getX() + x,
                                    this.getPos().getY(),
                                    this.getPos().getZ() + z
                                )
                            )

                            if (placed) {
                                ChunkLoadingCallback.forceChunk(
                                    this.chunkLoadTicket,
                                    this.world,
                                    this.getPos().getX() + x,
                                    this.getPos().getY(),
                                    this.getPos().getZ() + z,
                                    this.getOwnerName()
                                )
                            } else {
                                ChunkLoadingCallback.addToList(
                                    this.world,
                                    this.getPos().getX(),
                                    this.getPos().getY(),
                                    this.getPos().getZ(),
                                    this.getOwnerName()
                                )
                            }
                        }
                    }
                }
            }

            ChunkLoadingCallback.forceChunk(
                this.chunkLoadTicket,
                this.world,
                this.getPos().getX(),
                this.getPos().getY(),
                this.getPos().getZ(),
                this.getOwnerName()
            )
        }
    }

    override fun getTicket(): ForgeChunkManager.Ticket? {
        return this.chunkLoadTicket
    }

    override fun getCoords(): BlockPos {
        return BlockPos(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ())
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        this.stacks = this.readStandardItemsFromNBT(nbt)

        this.ownerName = nbt.getStringOr("OwnerName", "")
        this.launchDropdownSelection = nbt.getIntOr("LaunchSelection", 0)
        this.frequency = nbt.getIntOr("ControllerFrequency", 0)
        this.destFrequency = nbt.getIntOr("TargetFrequency", 0)
        this.frequencyCheckNeeded = true
        this.launchPadRemovalDisabled = nbt.getBooleanOr("LaunchPadRemovalDisabled", false)
        this.launchSchedulingEnabled = nbt.getBooleanOr("LaunchPadSchedulingEnabled", false)
        this.hideTargetDestination = nbt.getBooleanOr("HideTargetDestination", false)
        this.requiresClientUpdate = GCCoreUtil.getEffectiveSide() == Side.SERVER
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        this.writeStandardItemsToNBT(nbt, (this.stacks)!!)
        nbt.putString("OwnerName", this.ownerName)
        nbt.putInt("LaunchSelection", this.launchDropdownSelection)
        nbt.putInt("ControllerFrequency", this.frequency)
        nbt.putInt("TargetFrequency", this.destFrequency)
        nbt.putBoolean("LaunchPadRemovalDisabled", this.launchPadRemovalDisabled)
        nbt.putBoolean("LaunchPadSchedulingEnabled", this.launchSchedulingEnabled)
        nbt.putBoolean("HideTargetDestination", this.hideTargetDestination)
        return nbt
    }

    public override fun getContainingItems(): NonNullList<ItemStack>? {
        return this.stacks
    }

    override fun getName(): net.minecraft.network.chat.Component {
        return net.minecraft.network.chat.Component.translatable("container.launchcontroller.name")
    }

    override fun hasCustomName(): Boolean {
        return true
    }

    override fun canPlaceItem(slotID: Int, itemStack: ItemStack): Boolean {
        return slotID == 0 && ItemElectricBase.isElectricItem(itemStack.getItem())
    }

    override fun getSlotsForFace(side: EnumFacing): IntArray {
        return intArrayOf(0)
    }

    override fun canPlaceItemThroughFace(slotID: Int, par2ItemStack: ItemStack, par3: EnumFacing?): Boolean {
        return this.canPlaceItem(slotID, par2ItemStack)
    }

    override fun canTakeItemThroughFace(slotID: Int, par2ItemStack: ItemStack, par3: EnumFacing): Boolean {
        return slotID == 0
    }

    override fun shouldUseEnergy(): Boolean {
        return !this.getDisabled(0)
    }

    override fun setDisabled(index: Int, disabled: Boolean) {
        if (this.disableCooldown == 0) {
            when (index) {
                0 -> {
                    this.disabled = disabled
                    this.disableCooldown = 10
                }

                1 -> this.launchSchedulingEnabled = disabled
                2 -> {
                    this.hideTargetDestination = disabled
                    this.disableCooldown = 10
                }
            }
        }
    }

    override fun getDisabled(index: Int): Boolean {
        when (index) {
            0 -> return this.disabled
            1 -> return this.launchSchedulingEnabled
            2 -> return this.hideTargetDestination
        }

        return true
    }

    override fun canAttachToLandingPad(world: IBlockAccess?, pos: BlockPos?): Boolean {
        val tile: TileEntity? = (world)!!.getTileEntity(pos)

        return tile is TileEntityLandingPad
    }

    open fun setFrequency(frequency: Int) {
        this.frequency = frequency

        if (this.frequency >= 0) {
            this.frequencyValid = true
            val servers: Array<WorldServer> = (GCCoreUtil.getWorldServerList(this.world))!!

            worldLoop@ for (i: Int in servers.indices) {
                val world: WorldServer = servers[i]

                for (tile2: TileEntity? in ArrayList<TileEntity?>(world.loadedTileEntityList)) {
                    var tile2: TileEntity? = tile2
                    if (this !== tile2) {
                        tile2 = world.getBlockEntity(tile2!!.getBlockPos())
                        if (tile2 == null) {
                            continue
                        }

                        if (tile2 is TileEntityLaunchController) {
                            val launchController2: TileEntityLaunchController = tile2

                            if (launchController2.frequency == this.frequency) {
                                GCLog.debug(
                                    "Launch Controller frequency conflict at " + tile2.getPos() + " on dim: " + GCCoreUtil.getDimensionID(
                                        tile2
                                    )
                                )
                                this.frequencyValid = false
                                break@worldLoop
                            }
                        }
                    }
                }
            }
        } else {
            this.frequencyValid = false
        }
    }

    open fun setDestinationFrequency(frequency: Int) {
        if (frequency != this.destFrequency) {
            this.destFrequency = frequency
            this.checkDestFrequencyValid()
            this.updateRocketOnDockSettings()
        }
    }

    open fun checkDestFrequencyValid() {
        if (!this.world.isRemote) {
            this.destFrequencyValid = false
            if (this.destFrequency >= 0) {
                val servers: Array<WorldServer> = (GCCoreUtil.getWorldServerList(this.world))!!
                for (i: Int in servers.indices) {
                    val world: WorldServer = servers[i]

                    for (tile2: TileEntity? in ArrayList<TileEntity?>(world.loadedTileEntityList)) {
                        var tile2: TileEntity? = tile2
                        if (this !== tile2) {
                            tile2 = world.getBlockEntity(tile2!!.getBlockPos())
                            if (tile2 == null) {
                                continue
                            }

                            if (tile2 is TileEntityLaunchController) {
                                val launchController2: TileEntityLaunchController = tile2

                                if (launchController2.frequency == this.destFrequency) {
                                    this.destFrequencyValid = true
                                    return
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    open fun validFrequency(): Boolean {
        this.checkDestFrequencyValid()
        return !this.getDisabled(0) && this.hasEnoughEnergyToRun && this.frequencyValid && this.destFrequencyValid
    }

    open fun setLaunchDropdownSelection(newvalue: Int) {
        if (newvalue != this.launchDropdownSelection) {
            this.launchDropdownSelection = newvalue
            this.checkDestFrequencyValid()
            this.updateRocketOnDockSettings()
        }
    }

    open fun setLaunchSchedulingEnabled(newvalue: Boolean) {
        if (newvalue != this.launchSchedulingEnabled) {
            this.launchSchedulingEnabled = newvalue
            this.checkDestFrequencyValid()
            this.updateRocketOnDockSettings()
        }
    }

    open fun updateRocketOnDockSettings() {
        if (this.attachedDock is TileEntityLandingPad) {
            val pad: TileEntityLandingPad = (this.attachedDock as TileEntityLandingPad)
            val rocket: IDockable? = pad.getDockedEntity()
            if (rocket is EntityAutoRocket) {
                rocket.updateControllerSettings(pad)
            }
        }
    }

    open fun setAttachedPad(pad: IFuelDock?) {
        this.attachedDock = pad
    }

    override fun getFront(): EnumFacing {
        val state: IBlockState = this.world.getBlockState(getPos())
        if (state.getBlock() is BlockMachineMars) {
            return state.getValue(BlockMachineMars.FACING)
        }
        return EnumFacing.NORTH
    }

    override fun getElectricInputDirection(): EnumFacing {
        return getFront().rotateY()
    }

    companion object {
        const val WATTS_PER_TICK: Int = 1
    }
}
