package micdoodle8.mods.galacticraft.planets.mars.tile

import micdoodle8.mods.galacticraft.core.blocks.BlockMulti.EnumBlockMultiType
import micdoodle8.mods.galacticraft.planets.mars.network.PacketSimpleMars.EnumSimplePacketMars

import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.BlockMulti
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.tile.IMultiBlock
import micdoodle8.mods.galacticraft.core.tile.TileEntityMulti
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.mars.network.PacketSimpleMars
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.Biomes
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.World
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.LinkedList

open class TileEntityCryogenicChamber constructor() : TileEntityMulti(null), IMultiBlock {
    @JvmField
    var isOccupied: Boolean = false
    private var initialised = false

    @SideOnly(Side.CLIENT)
    override fun getRenderBoundingBox(): AxisAlignedBB {
        return AxisAlignedBB(
            (getPos().getX() - 1).toDouble(),
            getPos().getY().toDouble(),
            (getPos().getZ() - 1).toDouble(),
            (getPos().getX() + 2).toDouble(),
            (getPos().getY() + 3).toDouble(),
            (getPos().getZ() + 2).toDouble()
        )
    }

    override fun onActivated(entityPlayer: EntityPlayer?): Boolean {
        if (this.world.isRemote) {
            return false
        }

        val enumstatus: EntityPlayer.SleepResult =
            this.sleepInBedAt((entityPlayer)!!, this.getPos().getX(), this.getPos().getY(), this.getPos().getZ())

        when (enumstatus) {
            EntityPlayer.SleepResult.OK -> {
                (entityPlayer as EntityPlayerMP).connection.setPlayerLocation(
                    entityPlayer.posX,
                    entityPlayer.posY,
                    entityPlayer.posZ,
                    entityPlayer.rotationYaw,
                    entityPlayer.rotationPitch
                )
                (GalacticraftCore.packetPipeline)!!.sendTo(
                    PacketSimpleMars(
                        PacketSimpleMars.EnumSimplePacketMars.C_BEGIN_CRYOGENIC_SLEEP,
                        GCCoreUtil.getDimensionID(entityPlayer.world),
                        arrayOf<Any>(this.getPos())
                    ), entityPlayer
                )
                return true
            }

            EntityPlayer.SleepResult.NOT_POSSIBLE_NOW -> {
                val stats: GCPlayerStats = (GCPlayerStats.get((entityPlayer)!!))!!
                (entityPlayer)!!.sendMessage(
                    TextComponentString(
                        GCCoreUtil.translateWithFormat(
                            "gui.cryogenic.chat.cant_use",
                            stats.getCryogenicChamberCooldown() / 20
                        )
                    )
                )
                return false
            }

            else -> return false
        }
    }

    open fun sleepInBedAt(entityPlayer: EntityPlayer, par1: Int, par2: Int, par3: Int): EntityPlayer.SleepResult {
        if (!this.world.isRemote) {
            if (entityPlayer.isPlayerSleeping() || !entityPlayer.isEntityAlive()) {
                return EntityPlayer.SleepResult.OTHER_PROBLEM
            }

            if (this.world.getBiome(BlockPos(par1, par2, par3)) === Biomes.HELL) {
                return EntityPlayer.SleepResult.NOT_POSSIBLE_HERE
            }

            val stats: GCPlayerStats = (GCPlayerStats.get(entityPlayer))!!
            if (stats.getCryogenicChamberCooldown() > 0) {
                return EntityPlayer.SleepResult.NOT_POSSIBLE_NOW
            }
        }

        if (entityPlayer.isRiding()) {
            entityPlayer.dismountRidingEntity()
        }

        entityPlayer.setPosition(
            (this.getPos().getX() + 0.5f).toDouble(),
            (this.getPos().getY() + 1.9f).toDouble(),
            (this.getPos().getZ() + 0.5f).toDouble()
        )

        entityPlayer.sleeping = true
        entityPlayer.sleepTimer = 0
        entityPlayer.bedLocation = BlockPos(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ())
        entityPlayer.motionY = 0.0
        entityPlayer.motionZ = entityPlayer.motionY
        entityPlayer.motionX = entityPlayer.motionZ

        if (!this.world.isRemote) {
            this.world.updateAllPlayersSleepingFlag()
        }

        return EntityPlayer.SleepResult.OK
    }

    //    @Override
    //    public boolean canUpdate()
    //    {
    //        return true;
    //    }
    override fun update() {
        if (!this.initialised) {
            this.initialised = this.initialiseMultiTiles(this.getPos(), this.world)
        }
    }

    override fun onCreate(world: World?, placedPosition: BlockPos?) {
        this.mainBlockPosition = placedPosition
        this.markDirty()

        val positions: MutableList<BlockPos> = LinkedList<BlockPos>()
        this.getPositions(placedPosition, (positions as MutableList<BlockPos?>?))
        (GCBlocks.fakeBlock as BlockMulti).makeFakeBlock((world)!!, positions, placedPosition, this.getMultiType())
    }

    override fun getMultiType(): BlockMulti.EnumBlockMultiType {
        return BlockMulti.EnumBlockMultiType.CRYO_CHAMBER
    }

    override fun getPositions(placedPosition: BlockPos?, positions: MutableList<BlockPos?>?) {
        val buildHeight: Int = this.world.getHeight() - 1

        for (y: Int in 1..2) {
            if ((placedPosition)!!.getY() + y > buildHeight) {
                return
            }
            (positions)!!.add(BlockPos((placedPosition)!!.getX(), (placedPosition)!!.getY() + y, (placedPosition)!!.getZ()))
        }
    }

    override fun onDestroy(callingBlock: TileEntity?) {
        val thisBlock: BlockPos = getPos()
        val positions: MutableList<BlockPos> = LinkedList<BlockPos>()
        this.getPositions(thisBlock, (positions as MutableList<BlockPos?>?))

        for (pos: BlockPos in positions) {
            val stateAt: IBlockState = this.world.getBlockState(pos)

            if (stateAt.getBlock() === GCBlocks.fakeBlock && stateAt.getValue(BlockMulti.MULTI_TYPE) as BlockMulti.EnumBlockMultiType == BlockMulti.EnumBlockMultiType.CRYO_CHAMBER) {
                if (this.world.isRemote && this.world.rand.nextDouble() < 0.1) {
                    FMLClientHandler.instance().getClient().effectRenderer.addBlockDestroyEffects(
                        pos,
                        this.world.getBlockState(pos)
                    )
                }
                this.world.destroyBlock(pos, false)
            }
        }
        this.world.destroyBlock(thisBlock, true)
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        this.isOccupied = nbt.getBoolean("IsChamberOccupied")
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        super.writeToNBT(nbt)
        nbt.setBoolean("IsChamberOccupied", this.isOccupied)
        return nbt
    }

    override fun getUpdateTag(): NBTTagCompound {
        return this.writeToNBT(NBTTagCompound())
    }
}
