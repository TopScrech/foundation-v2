package micdoodle8.mods.galacticraft.core.tile

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
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.lang.Exception
import java.util.ArrayList

open class TileEntityPanelLight constructor() : TileEntity(), IPacketReceiver {
    @JvmField
    var meta: Int = 0
    private var superState: IBlockState? = null

    @JvmField
    var color: Int = 0xf0f0e0

    @SideOnly(Side.CLIENT)
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

    @SideOnly(Side.CLIENT)
    override fun getRenderBoundingBox(): AxisAlignedBB {
        if (this.renderAABB == null) {
            this.renderAABB = AxisAlignedBB(pos, pos.add(1, 1, 1))
        }
        return this.renderAABB!!
    }

    @SideOnly(Side.CLIENT)
    override fun getMaxRenderDistanceSquared(): Double {
        return Constants.RENDERDISTANCE_LONG
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)

        this.meta = nbt.getInteger("meta")
        if (nbt.hasKey("col")) {
            this.color = nbt.getInteger("col")
        }
        val tag: NBTTagCompound = nbt.getCompoundTag("sust")
        if (!tag.isEmpty()) {
            this.superState = readBlockState(tag)
        }
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        nbt.setInteger("meta", this.meta)
        nbt.setInteger("col", this.color)
        if (this.superState != null) {
            val tag: NBTTagCompound = NBTTagCompound()
            Companion.writeBlockState(tag, this.superState!!)
            nbt.setTag("sust", tag)
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
            if (!tag.hasKey("Name", 8)) {
                return Blocks.AIR.getDefaultState()
            } else {
                val block: Block = Block.REGISTRY.getObject(ResourceLocation(tag.getString("Name")))

                if (tag.hasKey("Meta")) {
                    val meta: Int = tag.getInteger("Meta")
                    if (meta >= 0 && meta < 16) {
                        return block.getStateFromMeta(meta)
                    }
                }

                return block.getDefaultState()
            }
        }

        /**
         * Writes the given blockstate to the given tag.  In MC1.10+ use NBTUtil instead!
         */
        @JvmStatic
        fun writeBlockState(tag: NBTTagCompound, state: IBlockState): NBTTagCompound {
            tag.setString("Name", net.minecraft.block.Block.REGISTRY.getNameForObject(state.getBlock()).toString())
            tag.setInteger("Meta", state.getBlock().getMetaFromState(state))
            return tag
        }

        @JvmStatic
        fun readBlockState(name: String, meta: Int): IBlockState {
            val block: Block = Block.REGISTRY.getObject(ResourceLocation(name))
            if (block == null) {
                return Blocks.AIR.getDefaultState()
            }
            return block.getStateFromMeta(meta)
        }
    }
}
