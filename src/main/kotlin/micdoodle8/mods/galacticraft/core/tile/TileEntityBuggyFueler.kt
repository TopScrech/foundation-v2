package micdoodle8.mods.galacticraft.core.tile

import micdoodle8.mods.galacticraft.core.blocks.BlockMulti.EnumBlockMultiType

import com.google.common.base.Predicate
import micdoodle8.mods.galacticraft.api.entity.ICargoEntity
import micdoodle8.mods.galacticraft.api.entity.IDockable
import micdoodle8.mods.galacticraft.api.entity.IFuelable
import micdoodle8.mods.galacticraft.api.tile.IFuelDock
import micdoodle8.mods.galacticraft.api.tile.ILandingPadAttachable
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.blocks.BlockMulti
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.client.FMLClientHandler
import java.util.ArrayList
import java.util.HashSet
import kotlin.math.abs

open class TileEntityBuggyFueler constructor() : TileEntityMulti(null), IMultiBlock, IFuelable, IFuelDock,
    ICargoEntity {
    private var dockedEntity: IDockable? = null
    private var initialised = false

    override fun update() {
        if (!this.initialised) {
            if (!this.world.isRemote) this.onCreate(this.world, this.getPos())
            this.initialiseMultiTiles(this.getPos(), this.world)
            this.initialised = true
        }

        if (!this.world.isRemote) {
            val list: MutableList<Entity?> = this.world.getEntitiesWithinAABB<Entity?>(
                Entity::class.java, AxisAlignedBB(
                    this.getPos().getX() - 1.5, this.getPos().getY() - 2.0, this.getPos().getZ() - 1.5,
                    this.getPos().getX() + 1.5, this.getPos().getY() + 4.0, this.getPos().getZ() + 1.5
                ), Predicate { input: Entity? -> input is IFuelable })

            var changed: Boolean = false

            for (o: Any? in list) {
                if (o != null && o is IDockable && !this.world.isRemote) {
                    val fuelable: IDockable = o

                    if (fuelable.isDockValid(this)) {
                        this.dockedEntity = fuelable

                        this.dockedEntity!!.setPad(this)

                        changed = true
                    }
                }
            }

            if (!changed) {
                if (this.dockedEntity != null) {
                    this.dockedEntity!!.setPad(null)
                }

                this.dockedEntity = null
            }
        }
    }

    override fun onActivated(entityPlayer: EntityPlayer?): Boolean {
        return false
    }

    override fun onCreate(world: World?, placedPosition: BlockPos?) {
        this.mainBlockPosition = placedPosition
        this.markDirty()

        val positions: MutableList<BlockPos> = ArrayList<BlockPos>()
        this.getPositions(placedPosition, (positions as MutableList<BlockPos?>?))
        (GCBlocks.fakeBlock as BlockMulti).makeFakeBlock((world)!!, positions, placedPosition, this.getMultiType())
    }

    override fun getMultiType(): BlockMulti.EnumBlockMultiType {
        return BlockMulti.EnumBlockMultiType.BUGGY_FUEL_PAD
    }

    override fun getPositions(placedPosition: BlockPos?, positions: MutableList<BlockPos?>?) {
        val y: Int = (placedPosition)!!.getY()
        for (x: Int in -1..1) {
            for (z: Int in -1..1) {
                if (x == 0 && z == 0) continue
                (positions)!!.add(BlockPos((placedPosition)!!.getX() + x, y, (placedPosition)!!.getZ() + z))
            }
        }
    }

    override fun onDestroy(callingBlock: TileEntity?) {
        val thisBlock: BlockPos = getPos()
        val positions: MutableList<BlockPos> = ArrayList<BlockPos>()
        this.getPositions(thisBlock, (positions as MutableList<BlockPos?>?))

        for (pos: BlockPos in positions) {
            val stateAt: IBlockState = this.world.getBlockState(pos)

            if (stateAt.getBlock() === GCBlocks.fakeBlock && stateAt.getValue(BlockMulti.MULTI_TYPE) as BlockMulti.EnumBlockMultiType == BlockMulti.EnumBlockMultiType.BUGGY_FUEL_PAD) {
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

        if (this.dockedEntity != null) {
            this.dockedEntity!!.onPadDestroyed()
            this.dockedEntity = null
        }
    }

    override fun addFuel(liquid: FluidStack?, doFill: Boolean): Int {
        if (this.dockedEntity != null) {
            return this.dockedEntity!!.addFuel(liquid, doFill)
        }

        return 0
    }

    override fun removeFuel(amount: Int): FluidStack? {
        if (this.dockedEntity != null) {
            return this.dockedEntity!!.removeFuel(amount)
        }

        return null
    }

    override fun addCargo(stack: ItemStack?, doAdd: Boolean): ICargoEntity.EnumCargoLoadingState? {
        if (this.dockedEntity != null) {
            return this.dockedEntity!!.addCargo(stack, doAdd)
        }

        return ICargoEntity.EnumCargoLoadingState.NOTARGET
    }

    override fun removeCargo(doRemove: Boolean): ICargoEntity.RemovalResult? {
        if (this.dockedEntity != null) {
            return this.dockedEntity!!.removeCargo(doRemove)
        }

        return ICargoEntity.RemovalResult(ICargoEntity.EnumCargoLoadingState.NOTARGET, ItemStack.EMPTY)
    }

    override fun getConnectedTiles(): HashSet<ILandingPadAttachable?> {
        val connectedTiles: HashSet<ILandingPadAttachable?> = HashSet<ILandingPadAttachable?>()

        for (x: Int in -2..2) {
            for (z: Int in -2..2) {
                if (x == -2 || x == 2 || z == -2 || z == 2) {
                    if (abs(x) != abs(z)) {
                        val tile: TileEntity? = this.world.getBlockEntity(
                            BlockPos(
                                this.getPos().getX() + x,
                                this.getPos().getY(),
                                this.getPos().getZ() + z
                            )
                        )

                        if (tile != null && tile is ILandingPadAttachable && (tile as ILandingPadAttachable).canAttachToLandingPad(
                                this.world,
                                this.getPos()
                            )
                        ) {
                            connectedTiles.add(tile as ILandingPadAttachable)
                        }
                    }
                }
            }
        }

        return connectedTiles
    }

    override fun isBlockAttachable(world: IBlockAccess?, pos: BlockPos?): Boolean {
        val tile: TileEntity? = (world)!!.getTileEntity(pos)

        if (tile != null && tile is ILandingPadAttachable) {
            return (tile as ILandingPadAttachable).canAttachToLandingPad(world, this.getPos())
        }

        return false
    }

    override fun getDockedEntity(): IDockable? {
        return this.dockedEntity
    }

    override fun dockEntity(entity: IDockable?) {
        this.dockedEntity = entity
    }
}
