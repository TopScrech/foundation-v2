package micdoodle8.mods.galacticraft.core.tile

import foundation.block.getStateFromMeta
import foundation.block.getMetaFromState

import foundation.persistence.containsTag

import micdoodle8.mods.galacticraft.core.blocks.BlockPanelLighting.PanelType

import io.netty.buffer.ByteBuf
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.BlockPanelLighting
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.network.IPacketReceiver
import micdoodle8.mods.galacticraft.core.network.PacketDynamic
import micdoodle8.mods.galacticraft.core.util.RedstoneUtil
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.AsteroidBlocks
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.level.block.Blocks
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.lang.Exception
import java.util.ArrayList

open class TileEntityPanelLight constructor() : TileEntity(), IPacketReceiver {
    @JvmField
    var meta: Int = 0
    private var superState: IBlockState? = null

    @JvmField
    var color: Int = 0xf0f0e0

    @OnlyIn(Dist.CLIENT)
    private var renderAABB: AxisAlignedBB? = null

    open fun initialise(
        type: Int,
        facing: EnumFacing,
        player: EntityPlayer,
        isRemote: Boolean,
        superStateClient: IBlockState?
    ) {
        this.meta = facing.ordinal
        if (isRemote) {
            this.superState = superStateClient
            this.color = BlockPanelLighting.color
        } else {
            val stats: GCPlayerStats = (GCPlayerStats.get(player))!!
            this.superState = ((stats)!!.getPanelLightingBases())!![type]
            this.color = stats.getPanelLightingColor()
        }
    }


    open fun getBaseBlock(): IBlockState {
        if (this.superState != null && this.superState!!.getBlock() === Blocks.AIR) {
            this.superState = null
        }
        return (if (this.superState == null) micdoodle8.mods.galacticraft.core.tile.TileEntityPanelLight.Companion.defaultLook else this.superState)!!
    }

    open fun getType(): BlockPanelLighting.PanelType {
        if (this.world != null) {
            val b: IBlockState = this.world.getBlockState(this.pos)
            if (b.getBlock() is BlockPanelLighting) {
                return b.getValue(BlockPanelLighting.TYPE) as BlockPanelLighting.PanelType
            }
        }
        return BlockPanelLighting.PanelType.SQUARE
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRenderBoundingBox(): AxisAlignedBB {
        if (this.renderAABB == null) {
            this.renderAABB = AxisAlignedBB(pos, pos.add(1, 1, 1))
        }
        return this.renderAABB!!
    }

    @OnlyIn(Dist.CLIENT)
    override fun getMaxRenderDistanceSquared(): Double {
        return Constants.RENDERDISTANCE_LONG
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)

        this.meta = nbt.getIntOr("meta", 0)
        if (nbt.containsTag("col")) {
            this.color = nbt.getIntOr("col", 0)
        }
        val tag: NBTTagCompound = nbt.getCompoundOrEmpty("sust")
        if (!tag.isEmpty()) {
            this.superState = readBlockState(tag)
        }
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        nbt.putInt("meta", this.meta)
        nbt.putInt("col", this.color)
        if (this.superState != null) {
            val tag: NBTTagCompound = NBTTagCompound()
            Companion.writeBlockState(tag, this.superState!!)
            nbt.put("sust", tag)
        }
        return nbt
    }

    override fun getUpdateTag(): NBTTagCompound {
        return this.writeToNBT(NBTTagCompound())
    }

    override fun onLoad() {
        if (this.world.isRemote) {
            //Request any networked information from server on first client update
            (GalacticraftCore.packetPipeline)!!.sendToServer(PacketDynamic(this))
        }
    }

    override fun getNetworkedData(sendData: ArrayList<Any?>?) {
        if (this.world.isRemote) {
            return
        }

        (sendData)!!.add(this.meta.toByte())
        (sendData)!!.add(this.color)
        if (this.superState != null) {
            val block: Block = this.superState!!.getBlock()
            if (block === Blocks.AIR) {
                this.superState = null
                return
            }

            (sendData)!!.add(net.minecraft.block.Block.REGISTRY.getNameForObject(block).toString())
            (sendData)!!.add(block.getMetaFromState(this.superState).toByte())
        }
    }

    override fun decodePacketdata(buffer: ByteBuf?) {
        if (this.world.isRemote) {
            try {
                this.meta = (buffer)!!.readByte().toInt()
                this.color = (buffer)!!.readInt()
                if ((buffer)!!.readableBytes() > 0) {
                    val name: String = ByteBufUtils.readUTF8String(buffer)
                    val otherMeta: Int = (buffer)!!.readByte().toInt()
                    this.superState = readBlockState(name, otherMeta)
                    this.world.markBlockRangeForRenderUpdate(this.pos, this.pos)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    open fun getEnabled(): Boolean {
        return !RedstoneUtil.isBlockReceivingRedstone(this.world, this.getPos())
    }

    companion object {
        private val defaultLook: IBlockState =
            if (GalacticraftCore.isPlanetsLoaded) (AsteroidBlocks.blockBasic)!!.getStateFromMeta(6) else (GCBlocks.basicBlock)!!.getStateFromMeta(
                4
            )

        /**
         * Reads a blockstate from the given tag.  In MC1.10+ use NBTUtil instead!
         */
        @JvmStatic
        fun readBlockState(tag: NBTTagCompound): IBlockState {
            if (!tag.containsTag("Name", 8)) {
                return Blocks.AIR.defaultBlockState()
            } else {
                val block: Block = Block.REGISTRY.getObject(ResourceLocation(tag.getStringOr("Name", "")))

                if (tag.containsTag("Meta")) {
                    val meta: Int = tag.getIntOr("Meta", 0)
                    if (meta >= 0 && meta < 16) {
                        return block.getStateFromMeta(meta)
                    }
                }

                return block.defaultBlockState()
            }
        }

        /**
         * Writes the given blockstate to the given tag.  In MC1.10+ use NBTUtil instead!
         */
        @JvmStatic
        fun writeBlockState(tag: NBTTagCompound, state: IBlockState): NBTTagCompound {
            tag.putString("Name", net.minecraft.block.Block.REGISTRY.getNameForObject(state.getBlock()).toString())
            tag.putInt("Meta", state.getBlock().getMetaFromState(state))
            return tag
        }

        @JvmStatic
        fun readBlockState(name: String, meta: Int): IBlockState {
            val block: Block = Block.REGISTRY.getObject(ResourceLocation.parse(name))
            if (block == null) {
                return Blocks.AIR.defaultBlockState()
            }
            return block.getStateFromMeta(meta)
        }
    }
}
