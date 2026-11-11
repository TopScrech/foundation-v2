package galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks

import foundation.level.offset

import galaxyspace.systems.BarnardsSystem.core.BRBlocks
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Blocks.EnumBlockBarnardaC
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Blocks
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.blocks.Barnarda_C_Farmland
import net.minecraft.world.level.block.Block
import net.minecraft.block.BlockFarmland
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.Item
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.phys.AABB as AxisAlignedBB
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter as IBlockAccess
import net.minecraft.world.level.Level as World
import net.minecraftforge.common.EnumPlantType
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.common.IPlantable
import java.util.Random
import kotlin.math.min

// These static methods intentionally hide methods inherited from Java classes
@Suppress("ACCIDENTAL_OVERRIDE")
open class Barnarda_C_Farmland constructor() : BlockFarmland() {
    init {
        this.setTranslationKey("barnarda_c_farmland")
        this.setSoundType(SoundType.GROUND)
        this.setHarvestLevel("spade", 1)
        this.setHardness(0.6f)
    }

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item {
        return BRBlocks.BARNARDA_C_BLOCKS.getItemDropped(
            BRBlocks.BARNARDA_C_BLOCKS.defaultBlockState().setValue(
                Barnarda_C_Blocks.Companion.BASIC_TYPE,
                EnumBlockBarnardaC.DIRT
            ), rand, fortune
        )
    }

    override fun updateTick(worldIn: World, pos: BlockPos, state: IBlockState, rand: Random) {
        val i: Int = (state.getValue(MOISTURE) as Int)

        if (!this.hasWater(worldIn, pos) && !worldIn.isRainingAt(pos.above())) {
            if (i > 0) {
                worldIn.setBlock(pos, state.setValue(MOISTURE, i - 1), 2)
            } else if (!this.hasCrops(worldIn, pos)) {
                turnToDirt(worldIn, pos)
            }
        } else if (i < 7) {
            worldIn.setBlock(pos, state.setValue(MOISTURE, 7), 2)
        }
    }

    override fun onFallenUpon(worldIn: World, pos: BlockPos, entityIn: Entity, fallDistance: Float) {
        if (ForgeHooks.onFarmlandTrample(
                worldIn,
                pos,
                BRBlocks.BARNARDA_C_BLOCKS.defaultBlockState().setValue(
                    Barnarda_C_Blocks.Companion.BASIC_TYPE,
                    EnumBlockBarnardaC.DIRT
                ),
                fallDistance,
                entityIn
            )
        )  // Forge: Move logic to Entity#canTrample
        {
            turnToDirt(worldIn, pos)
        }

        //super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
    }

    override fun neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block, fromPos: BlockPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos)

        if (worldIn.getBlockState(pos.above()).getMaterial().isSolid()) {
            turnToDirt(worldIn, pos)
        }
    }

    override fun onBlockAdded(worldIn: World, pos: BlockPos, state: IBlockState) {
        super.onBlockAdded(worldIn, pos, state)

        if (worldIn.getBlockState(pos.above()).getMaterial().isSolid()) {
            turnToDirt(worldIn, pos)
        }
    }

    override fun canSustainPlant(
        state: IBlockState,
        world: IBlockAccess,
        pos: BlockPos,
        direction: EnumFacing,
        plantable: IPlantable
    ): Boolean {
        val plant: IBlockState? = plantable.getPlant(world, pos.offset(direction))
        val plantType: EnumPlantType? = plantable.getPlantType(world, pos.offset(direction))

        /*
        switch (plantType)
        {
            case Crop:   return this == Blocks.FARMLAND;
            default: return false;
        }*/
        return plantType == EnumPlantType.Crop
    }

    private fun hasCrops(worldIn: World, pos: BlockPos): Boolean {
        val block: Block = worldIn.getBlockState(pos.above()).getBlock()
        return block is IPlantable && canSustainPlant(
            worldIn.getBlockState(pos),
            worldIn,
            pos,
            EnumFacing.UP,
            block as IPlantable
        )
    }

    private fun hasWater(worldIn: World, pos: BlockPos): Boolean {
        for (`blockpos$mutableblockpos`: BlockPos.MutableBlockPos in BlockPos.getAllInBoxMutable(
            pos.offset(-4, 0, -4),
            pos.offset(4, 1, 4)
        )) {
            if (worldIn.getBlockState(`blockpos$mutableblockpos`).getMaterial() === Material.WATER) {
                return true
            }
        }

        return false
    }

    companion object {
        @JvmStatic
        protected fun turnToDirt(p_190970_0_: World, worldIn: BlockPos) {
            p_190970_0_.setBlockAndUpdate(
                worldIn,
                BRBlocks.BARNARDA_C_BLOCKS.defaultBlockState().setValue(
                    Barnarda_C_Blocks.Companion.BASIC_TYPE,
                    EnumBlockBarnardaC.DIRT
                )
            )
            val axisalignedbb: AxisAlignedBB = field_194405_c.offset(worldIn)

            for (entity: Entity in p_190970_0_.getEntitiesWithinAABBExcludingEntity(null as Entity?, axisalignedbb)) {
                val d0: Double = min(
                    axisalignedbb.maxY - axisalignedbb.minY,
                    axisalignedbb.maxY - entity.getBoundingBox().minY
                )
                entity.setPositionAndUpdate(entity.x, entity.y + d0 + 0.001, entity.z)
            }
        }
    }
}
