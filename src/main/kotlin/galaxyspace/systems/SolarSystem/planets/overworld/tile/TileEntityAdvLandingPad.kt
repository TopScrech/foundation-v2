package galaxyspace.systems.SolarSystem.planets.overworld.tile

import galaxyspace.core.GSBlocks
import galaxyspace.core.prefab.blocks.GSBlockMulti
import micdoodle8.mods.galacticraft.api.entity.ICargoEntity
import micdoodle8.mods.galacticraft.api.entity.IDockable
import micdoodle8.mods.galacticraft.api.entity.IFuelable
import micdoodle8.mods.galacticraft.api.tile.IFuelDock
import micdoodle8.mods.galacticraft.api.tile.ILandingPadAttachable
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.BlockMulti
import micdoodle8.mods.galacticraft.core.tile.IMultiBlock
import micdoodle8.mods.galacticraft.core.tile.TileEntityMulti
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityLaunchController
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
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.ArrayList
import java.util.HashSet

open class TileEntityAdvLandingPad constructor() : TileEntityMulti(null), IMultiBlock, IFuelable, IFuelDock,
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
                Entity::class.java,
                AxisAlignedBB(
                    this.getPos().getX() - 0.5,
                    this.getPos().getY().toDouble(),
                    this.getPos().getZ() - 0.5,
                    this.getPos().getX() + 0.5,
                    this.getPos().getY() + 1.0,
                    this.getPos().getZ() + 0.5
                )
            )

            var docked: Boolean = false
            /*
			for (final Object o : list) {
				if (o instanceof IDockable && !((Entity) o).isDead) {
					final IDockable fuelable = (IDockable) o;

					if (!fuelable.inFlight()) {
						docked = true;

						if (fuelable != this.dockedEntity && fuelable.isDockValid(this)) {
							if (fuelable instanceof ILandable) {
								((ILandable) fuelable).landEntity(this.getPos());
							} else {
								fuelable.setPad(this);
								this.dockedEntity.setPad(this);
							}
						}

						break;
					}
				}
			}
*/
            for (o: Any? in list) {
                if (o != null && o is IDockable && !this.world.isRemote) {
                    val fuelable: IDockable = o

                    if (fuelable.isDockValid(this)) {
                        this.dockedEntity = fuelable

                        this.dockedEntity!!.setPad(this)

                        docked = true
                    }
                }
            }

            if (!docked) {
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

        for (pos: BlockPos in positions) {
            if (pos.getX() == (placedPosition)!!.getX() + 2 || pos.getX() == (placedPosition)!!.getX() - 2 || pos.getZ() == (placedPosition)!!.getZ() + 2 || pos.getZ() == (placedPosition)!!.getZ() - 2) (world)!!.setBlock(
                pos,
                GSBlocks.FAKE_BLOCK.defaultBlockState()
                    .setValue(
                        GSBlockMulti.MULTI_TYPE,
                        GSBlockMulti.EnumBlockMultiType.ADVANCED_ROCKET_PAD_1
                    ),
                0
            )
            else (world)!!.setBlock(
                pos,
                GSBlocks.FAKE_BLOCK.defaultBlockState()
                    .setValue(
                        GSBlockMulti.MULTI_TYPE,
                        this.getGSMultiType()
                    ),
                0
            )

            (world)!!.setTileEntity(pos, TileEntityMulti(placedPosition))
        }
        //((GSBlockMulti) GSBlocks.FAKE_BLOCK).makeFakeBlock(world, positions, placedPosition, this.getGSMultiType());
    }

    override fun getMultiType(): BlockMulti.EnumBlockMultiType? {
        return null
    }

    open fun getGSMultiType(): GSBlockMulti.EnumBlockMultiType {
        return GSBlockMulti.EnumBlockMultiType.ADVANCED_ROCKET_PAD
    }

    override fun getPositions(placedPosition: BlockPos?, positions: MutableList<BlockPos?>?) {
        val y: Int = (placedPosition)!!.getY()
        for (x: Int in -2..2) {
            for (z: Int in -2..2) {
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

            if (stateAt.getBlock() === GSBlocks.FAKE_BLOCK) {
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

    override fun getConnectedTiles(): HashSet<ILandingPadAttachable?> {
        val connectedTiles: HashSet<ILandingPadAttachable?> = HashSet<ILandingPadAttachable?>()

        for (x: Int in this.getPos().getX() - 3..<this.getPos().getX() + 3) {
            this.testConnectedTile(x, this.getPos().getZ() - 3, connectedTiles)
            this.testConnectedTile(x, this.getPos().getZ() + 3, connectedTiles)
        }

        for (z: Int in this.getPos().getZ() - 3..<this.getPos().getZ() + 3) {
            this.testConnectedTile(this.getPos().getX() - 3, z, connectedTiles)
            this.testConnectedTile(this.getPos().getX() + 3, z, connectedTiles)
        }

        return connectedTiles
    }

    private fun testConnectedTile(x: Int, z: Int, connectedTiles: HashSet<ILandingPadAttachable?>) {
        val testPos: BlockPos = BlockPos(x, this.getPos().getY(), z)

        if (!this.world.isBlockLoaded(testPos, false)) return

        val tile: TileEntity? = this.world.getBlockEntity(testPos)

        if (tile is ILandingPadAttachable
            && (tile as ILandingPadAttachable).canAttachToLandingPad(this.world, this.getPos())
        ) {
            connectedTiles.add(tile as ILandingPadAttachable)


            if (GalacticraftCore.isPlanetsLoaded && tile is TileEntityLaunchController) {
                tile.setAttachedPad(this)
            }
        }
    }

    override fun addCargo(itemStack: ItemStack?, doAdd: Boolean): ICargoEntity.EnumCargoLoadingState? {
        if (this.dockedEntity != null) {
            return this.dockedEntity!!.addCargo(itemStack, doAdd)
        }

        return ICargoEntity.EnumCargoLoadingState.NOTARGET
    }

    override fun removeCargo(doRemove: Boolean): ICargoEntity.RemovalResult? {
        if (this.dockedEntity != null) {
            return this.dockedEntity!!.removeCargo(doRemove)
        }

        return ICargoEntity.RemovalResult(ICargoEntity.EnumCargoLoadingState.NOTARGET, ItemStack.EMPTY)
    }

    @OnlyIn(Dist.CLIENT)
    override fun getRenderBoundingBox(): AxisAlignedBB {
        return AxisAlignedBB(
            (getPos().getX() - 2).toDouble(),
            getPos().getY().toDouble(),
            (getPos().getZ() - 2).toDouble(),
            (getPos().getX() + 3).toDouble(),
            getPos().getY() + 0.4,
            (getPos().getZ() + 3).toDouble()
        )
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
