package micdoodle8.mods.galacticraft.core.tile

import micdoodle8.mods.galacticraft.core.blocks.BlockMulti.EnumBlockMultiType

import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.BlockMulti
import micdoodle8.mods.galacticraft.core.client.gui.GuiIdsCore
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.ArrayList
import kotlin.math.abs

open class TileEntityNasaWorkbench constructor() : TileEntityMulti(null), IMultiBlock {
    private var initialised = false

    override fun onActivated(entityPlayer: EntityPlayer?): Boolean {
        (entityPlayer)!!.openGui(
            GalacticraftCore.instance,
            GuiIdsCore.NASA_WORKBENCH_ROCKET,
            this.world,
            this.getPos().getX(),
            this.getPos().getY(),
            this.getPos().getZ()
        )
        return true
    }

    override fun update() {
        if (!this.initialised) {
            this.initialised = this.initialiseMultiTiles(this.getPos(), this.world)
        }
    }

    override fun onCreate(world: World?, placedPosition: BlockPos?) {
        this.mainBlockPosition = placedPosition
        this.markDirty()
        val positions: MutableList<BlockPos> = ArrayList<BlockPos>()
        this.getPositions(placedPosition, (positions as MutableList<BlockPos?>?))
        (GCBlocks.fakeBlock as BlockMulti).makeFakeBlock((world)!!, positions, placedPosition, this.getMultiType())
    }

    override fun getMultiType(): BlockMulti.EnumBlockMultiType {
        return BlockMulti.EnumBlockMultiType.NASA_WORKBENCH
    }

    override fun getPositions(placedPosition: BlockPos?, positions: MutableList<BlockPos?>?) {
        val buildHeight: Int = this.world.getHeight() - 1

        for (y: Int in 1..2) {
            if ((placedPosition)!!.getY() + y > buildHeight) {
                return
            }

            for (x: Int in -1..1) {
                for (z: Int in -1..1) {
                    if (abs(x) != 1 || abs(z) != 1) {
                        (positions)!!.add(
                            BlockPos(
                                (placedPosition)!!.getX() + x,
                                (placedPosition)!!.getY() + y,
                                (placedPosition)!!.getZ() + z
                            )
                        )
                    }
                }
            }
        }

        if ((placedPosition)!!.getY() + 3 <= buildHeight) {
            (positions)!!.add(BlockPos((placedPosition)!!.getX(), (placedPosition)!!.getY() + 3, (placedPosition)!!.getZ()))
        }
    }

    override fun onDestroy(callingBlock: TileEntity?) {
        val thisBlock: BlockPos = getPos()
        val positions: MutableList<BlockPos> = ArrayList<BlockPos>()
        this.getPositions(thisBlock, (positions as MutableList<BlockPos?>?))

        for (pos: BlockPos in positions) {
            val stateAt: IBlockState = this.world.getBlockState(pos)

            if (stateAt.getBlock() === GCBlocks.fakeBlock && stateAt.getValue(BlockMulti.MULTI_TYPE) == BlockMulti.EnumBlockMultiType.NASA_WORKBENCH) {
                if (this.world.isRemote && this.world.rand.nextDouble() < 0.05) {
                    FMLClientHandler.instance().getClient().effectRenderer.addBlockDestroyEffects(
                        pos,
                        this.world.getBlockState(thisBlock)
                    )
                }
                this.world.setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState())
            }
        }
        this.world.destroyBlock(thisBlock, true)
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRenderBoundingBox(): AxisAlignedBB {
        return AxisAlignedBB(
            (getPos().getX() - 1).toDouble(),
            (getPos().getY() - 1).toDouble(),
            (getPos().getZ() - 1).toDouble(),
            (getPos().getX() + 2).toDouble(),
            (getPos().getY() + 5).toDouble(),
            (getPos().getZ() + 2).toDouble()
        )
    }
}
