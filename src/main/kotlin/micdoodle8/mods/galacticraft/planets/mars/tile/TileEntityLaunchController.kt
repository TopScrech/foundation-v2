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
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.inventory.ISidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.WorldServer
import net.minecraftforge.common.ForgeChunkManager
import net.minecraftforge.fml.relauncher.Side
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
            nbt.setInteger("ChunkLoaderTileX", this.getPos().getX())
            nbt.setInteger("ChunkLoaderTileY", this.getPos().getY())
            nbt.setInteger("ChunkLoaderTileZ", this.getPos().getZ())

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

        this.ownerName = nbt.getString("OwnerName")
        this.launchDropdownSelection = nbt.getInteger("LaunchSelection")
        this.frequency = nbt.getInteger("ControllerFrequency")
        this.destFrequency = nbt.getInteger("TargetFrequency")
        this.frequencyCheckNeeded = true
        this.launchPadRemovalDisabled = nbt.getBoolean("LaunchPadRemovalDisabled")
        this.launchSchedulingEnabled = nbt.getBoolean("LaunchPadSchedulingEnabled")
        this.hideTargetDestination = nbt.getBoolean("HideTargetDestination")
        this.requiresClientUpdate = GCCoreUtil.getEffectiveSide() == Side.SERVER
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        this.writeStandardItemsToNBT(nbt, (this.stacks)!!)
        nbt.setString("OwnerName", this.ownerName)
        nbt.setInteger("LaunchSelection", this.launchDropdownSelection)
        nbt.setInteger("ControllerFrequency", this.frequency)
        nbt.setInteger("TargetFrequency", this.destFrequency)
        nbt.setBoolean("LaunchPadRemovalDisabled", this.launchPadRemovalDisabled)
        nbt.setBoolean("LaunchPadSchedulingEnabled", this.launchSchedulingEnabled)
        nbt.setBoolean("HideTargetDestination", this.hideTargetDestination)
        return nbt
    }

    public override fun getContainingItems(): NonNullList<ItemStack>? {
        return this.stacks
    }

    override fun getName(): String {
        return GCCoreUtil.translate("container.launchcontroller.name")
    }

    override fun hasCustomName(): Boolean {
        return true
    }

    override fun isItemValidForSlot(slotID: Int, itemStack: ItemStack): Boolean {
        return slotID == 0 && ItemElectricBase.isElectricItem(itemStack.getItem())
    }

    override fun getSlotsForFace(side: EnumFacing): IntArray {
        return intArrayOf(0)
    }

    override fun canInsertItem(slotID: Int, par2ItemStack: ItemStack, par3: EnumFacing): Boolean {
        return this.isItemValidForSlot(slotID, par2ItemStack)
    }

    override fun canExtractItem(slotID: Int, par2ItemStack: ItemStack, par3: EnumFacing): Boolean {
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
                        tile2 = world.getTileEntity(tile2!!.getPos())
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
                            tile2 = world.getTileEntity(tile2!!.getPos())
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
