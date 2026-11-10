package micdoodle8.mods.galacticraft.core.tile

import micdoodle8.mods.miccore.Annotations.NetworkedField

import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.client.sounds.GCSounds
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import micdoodle8.mods.miccore.Annotations
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.relauncher.Side

open class TileEntityAirLockController constructor() : TileEntityAirLock() {
    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var redstoneActivation: Boolean = false

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var playerDistanceActivation: Boolean = true

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var playerDistanceSelection: Int = 0

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var playerNameMatches: Boolean = false

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var playerToOpenFor: String = ""

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var invertSelection: Boolean = false

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var horizontalModeEnabled: Boolean = false

    @JvmField
    var lastHorizontalModeEnabled: Boolean = false

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var ownerName: String = ""

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var active: Boolean = false

    @JvmField
    var lastActive: Boolean = false
    private var otherAirLocks = 0
    private var lastOtherAirLocks = 0
    private var protocol: AirLockProtocol? = null
    private var lastProtocol: AirLockProtocol?

    init {
        this.lastProtocol = this.protocol
    }

    override fun update() {
        super.update()

        if (!this.world.isRemote) {
            this.active = false

            if (this.redstoneActivation) {
                this.active = this.world.getRedstonePowerFromNeighbors(this.getPos()) > 0
            }

            if ((this.active || !this.redstoneActivation) && this.playerDistanceActivation) {
                var distance: Double = 0.0

                when (this.playerDistanceSelection) {
                    0 -> distance = 1.0
                    1 -> distance = 2.0
                    2 -> distance = 5.0
                    3 -> distance = 10.0
                }

                val minPos: Vector3 = Vector3(this).translate(0.5 - distance)
                val maxPos: Vector3 = Vector3(this).translate(0.5 + distance)
                val matchingRegion: AxisAlignedBB =
                    AxisAlignedBB(minPos.x, minPos.y, minPos.z, maxPos.x, maxPos.y, maxPos.z)
                val playersWithin: MutableList<EntityPlayer> =
                    this.world.getEntitiesWithinAABB<EntityPlayer>(EntityPlayer::class.java, matchingRegion)

                if (this.playerNameMatches) {
                    var foundPlayer: Boolean = false
                    for (p: EntityPlayer? in playersWithin) {
                        if (PlayerUtil.getName(p).equals(this.playerToOpenFor, ignoreCase = true)) {
                            foundPlayer = true
                            break
                        }
                    }
                    this.active = foundPlayer
                } else {
                    this.active = !playersWithin.isEmpty()
                }
            }

            if (!this.invertSelection) {
                this.active = !this.active
            }

            if (this.protocol == null) {
                this.lastProtocol = AirLockProtocol(this)
                this.protocol = this.lastProtocol
            }

            if (this.ticks % 5 == 0) {
                if (this.horizontalModeEnabled != this.lastHorizontalModeEnabled) {
                    this.unsealAirLock()
                } else if (this.active || this.lastActive) {
                    this.lastOtherAirLocks = this.otherAirLocks
                    this.otherAirLocks = this.protocol!!.calculate(this.horizontalModeEnabled)

                    if (this.active) {
                        if (this.otherAirLocks != this.lastOtherAirLocks || !this.lastActive) {
                            this.unsealAirLock()
                            if (this.otherAirLocks >= 0) {
                                this.sealAirLock()
                            }
                        }
                    } else {
                        if (this.lastActive) {
                            this.unsealAirLock()
                        }
                    }
                }

                if (this.active != this.lastActive) {
                    val state: IBlockState = this.world.getBlockState(this.getPos())
                    this.world.notifyBlockUpdate(this.getPos(), state, state, 3)
                }

                this.lastActive = this.active
                this.lastProtocol = this.protocol
                this.lastHorizontalModeEnabled = this.horizontalModeEnabled
            }
        }
    }

    private fun sealAirLock() {
        var x: Int = (this.lastProtocol!!.maxX + this.lastProtocol!!.minX) / 2
        var y: Int = (this.lastProtocol!!.maxY + this.lastProtocol!!.minY) / 2
        var z: Int = (this.lastProtocol!!.maxZ + this.lastProtocol!!.minZ) / 2

        if (this.world.getBlockState(BlockPos(x, y, z)).getBlock() !== GCBlocks.airLockSeal) {
            this.world.playSound(
                null,
                x.toDouble(),
                y.toDouble(),
                z.toDouble(),
                GCSounds.openAirLock,
                SoundCategory.BLOCKS,
                1.0f,
                1.0f
            )
        }

        if (this.horizontalModeEnabled) {
            if (this.protocol!!.minY == this.protocol!!.maxY && this.protocol!!.minX != this.protocol!!.maxX && this.protocol!!.minZ != this.protocol!!.maxZ) {
                x = this.protocol!!.minX + 1
                while (x <= this.protocol!!.maxX - 1) {
                    z = this.protocol!!.minZ + 1
                    while (z <= this.protocol!!.maxZ - 1) {
                        val pos: BlockPos = BlockPos(x, y, z)
                        if (this.world.getBlockState(pos).getBlock()
                                .isAir(this.world.getBlockState(pos), this.world, pos)
                        ) {
                            this.world.setBlockState(pos, (GCBlocks.airLockSeal)!!.getDefaultState(), 3)
                        }
                        z++
                    }
                    x++
                }
            }
        } else {
            if (this.protocol!!.minX != this.protocol!!.maxX) {
                x = this.protocol!!.minX + 1
                while (x <= this.protocol!!.maxX - 1) {
                    y = this.protocol!!.minY + 1
                    while (y <= this.protocol!!.maxY - 1) {
                        val pos: BlockPos = BlockPos(x, y, z)
                        if (this.world.getBlockState(pos).getBlock()
                                .isAir(this.world.getBlockState(pos), this.world, pos)
                        ) {
                            this.world.setBlockState(pos, (GCBlocks.airLockSeal)!!.getDefaultState(), 3)
                        }
                        y++
                    }
                    x++
                }
            } else if (this.protocol!!.minZ != this.protocol!!.maxZ) {
                z = this.protocol!!.minZ + 1
                while (z <= this.protocol!!.maxZ - 1) {
                    y = this.protocol!!.minY + 1
                    while (y <= this.protocol!!.maxY - 1) {
                        val pos: BlockPos = BlockPos(x, y, z)
                        if (this.world.getBlockState(pos).getBlock()
                                .isAir(this.world.getBlockState(pos), this.world, pos)
                        ) {
                            this.world.setBlockState(pos, (GCBlocks.airLockSeal)!!.getDefaultState(), 3)
                        }
                        y++
                    }
                    z++
                }
            }
        }
    }

    open fun unsealAirLock() {
        if (this.lastProtocol == null) {
            return
        }

        var x: Int = this.lastProtocol!!.minX + (this.lastProtocol!!.maxX - this.lastProtocol!!.minX) / 2
        var y: Int = this.lastProtocol!!.minY + (this.lastProtocol!!.maxY - this.lastProtocol!!.minY) / 2
        var z: Int = this.lastProtocol!!.minZ + (this.lastProtocol!!.maxZ - this.lastProtocol!!.minZ) / 2

        var pos: BlockPos = BlockPos(x, y, z)
        val state: IBlockState = this.world.getBlockState(pos)

        if (state.getMaterial() !== Material.AIR) {
            this.world.playSound(
                null,
                x.toDouble(),
                y.toDouble(),
                z.toDouble(),
                GCSounds.closeAirLock,
                SoundCategory.BLOCKS,
                1.0f,
                1.0f
            )
        }

        if (this.lastHorizontalModeEnabled) {
            if (this.protocol!!.minY == this.protocol!!.maxY && this.protocol!!.minX != this.protocol!!.maxX && this.protocol!!.minZ != this.protocol!!.maxZ) {
                x = this.protocol!!.minX + 1
                while (x <= this.protocol!!.maxX - 1) {
                    z = this.protocol!!.minZ + 1
                    while (z <= this.protocol!!.maxZ - 1) {
                        pos = BlockPos(x, y, z)
                        if (this.world.getBlockState(pos).getBlock() === GCBlocks.airLockSeal) {
                            this.world.setBlockToAir(pos)
                        }
                        z++
                    }
                    x++
                }
            }
        } else {
            if (this.lastProtocol!!.minX != this.lastProtocol!!.maxX) {
                x = this.lastProtocol!!.minX + 1
                while (x <= this.lastProtocol!!.maxX - 1) {
                    y = this.lastProtocol!!.minY + 1
                    while (y <= this.lastProtocol!!.maxY - 1) {
                        pos = BlockPos(x, y, z)
                        if (this.world.getBlockState(pos).getBlock() === GCBlocks.airLockSeal) {
                            this.world.setBlockToAir(pos)
                        }
                        y++
                    }
                    x++
                }
            } else if (this.lastProtocol!!.minZ != this.lastProtocol!!.maxZ) {
                z = this.lastProtocol!!.minZ + 1
                while (z <= this.lastProtocol!!.maxZ - 1) {
                    y = this.lastProtocol!!.minY + 1
                    while (y <= this.lastProtocol!!.maxY - 1) {
                        pos = BlockPos(x, y, z)
                        if (this.world.getBlockState(pos).getBlock() === GCBlocks.airLockSeal) {
                            this.world.setBlockToAir(pos)
                        }
                        y++
                    }
                    z++
                }
            }
        }
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        this.ownerName = nbt.getString("OwnerName")
        this.redstoneActivation = nbt.getBoolean("RedstoneActivation")
        this.playerDistanceActivation = nbt.getBoolean("PlayerDistanceActivation")
        this.playerDistanceSelection = nbt.getInteger("PlayerDistanceSelection")
        this.playerNameMatches = nbt.getBoolean("PlayerNameMatches")
        this.playerToOpenFor = nbt.getString("PlayerToOpenFor")
        this.invertSelection = nbt.getBoolean("InvertSelection")
        this.active = nbt.getBoolean("active")
        this.lastActive = nbt.getBoolean("lastActive")
        this.horizontalModeEnabled = nbt.getBoolean("HorizontalModeEnabled")
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        nbt.setString("OwnerName", this.ownerName)
        nbt.setBoolean("RedstoneActivation", this.redstoneActivation)
        nbt.setBoolean("PlayerDistanceActivation", this.playerDistanceActivation)
        nbt.setInteger("PlayerDistanceSelection", this.playerDistanceSelection)
        nbt.setBoolean("PlayerNameMatches", this.playerNameMatches)
        nbt.setString("PlayerToOpenFor", this.playerToOpenFor)
        nbt.setBoolean("InvertSelection", this.invertSelection)
        nbt.setBoolean("active", this.active)
        nbt.setBoolean("lastActive", this.lastActive)
        nbt.setBoolean("HorizontalModeEnabled", this.horizontalModeEnabled)
        return nbt
    }

    override fun getUpdateTag(): NBTTagCompound {
        return this.writeToNBT(NBTTagCompound())
    }

    override fun getPacketRange(): Double {
        return 20.0
    }

    override fun getPacketCooldown(): Int {
        return 3
    }

    override fun isNetworkedTile(): Boolean {
        return true
    }
}
