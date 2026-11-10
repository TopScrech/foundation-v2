package micdoodle8.mods.galacticraft.core.world.gen

import micdoodle8.mods.galacticraft.core.GCBlocks
import net.minecraft.block.BlockPlanks
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import net.minecraft.world.gen.structure.StructureBoundingBox
import net.minecraft.world.gen.structure.StructureComponent
import net.minecraft.world.gen.structure.template.TemplateManager
import java.util.Random

open class StructureComponentVillageWell : StructureComponentVillage {
    private var averageGroundLevel = -1

    constructor()

    constructor(
        par1ComponentVillageStartPiece: StructureComponentVillageStartPiece?,
        par2: Int,
        par3Random: Random,
        par4: Int,
        par5: Int
    ) : super(par1ComponentVillageStartPiece, par2) {
        this.setCoordBaseMode(EnumFacing.byIndex(par3Random.nextInt(4)))

        when (this.getCoordBaseMode()!!.getHorizontalIndex()) {
            0, 2 -> this.boundingBox = StructureBoundingBox(par4, 64, par5, par4 + 6 - 1, 78, par5 + 6 - 1)
            else -> this.boundingBox = StructureBoundingBox(par4, 64, par5, par4 + 6 - 1, 78, par5 + 6 - 1)
        }
    }

    override fun writeStructureToNBT(nbt: NBTTagCompound) {
        super.writeStructureToNBT(nbt)

        nbt.setInteger("AvgGroundLevel", this.averageGroundLevel)
    }

    override fun readStructureFromNBT(nbt: NBTTagCompound, manager: TemplateManager) {
        super.readStructureFromNBT(nbt, manager)

        this.averageGroundLevel = nbt.getInteger("AvgGroundLevel")
    }

    override fun buildComponent(
        par1StructureComponent: StructureComponent,
        par2List: MutableList<StructureComponent?>,
        par3Random: Random
    ) {
        StructureVillagePiecesMoon.getNextStructureComponentVillagePath(
            par1StructureComponent as StructureComponentVillageStartPiece,
            par2List,
            par3Random,
            this.boundingBox.minX - 1,
            this.boundingBox.maxY - 4,
            this.boundingBox.minZ + 1,
            EnumFacing.byHorizontalIndex(1),
            this.getComponentType()
        )
        StructureVillagePiecesMoon.getNextStructureComponentVillagePath(
            par1StructureComponent,
            par2List,
            par3Random,
            this.boundingBox.maxX + 1,
            this.boundingBox.maxY - 4,
            this.boundingBox.minZ + 1,
            EnumFacing.byHorizontalIndex(3),
            this.getComponentType()
        )
        StructureVillagePiecesMoon.getNextStructureComponentVillagePath(
            par1StructureComponent,
            par2List,
            par3Random,
            this.boundingBox.minX + 1,
            this.boundingBox.maxY - 4,
            this.boundingBox.minZ - 1,
            EnumFacing.byHorizontalIndex(2),
            this.getComponentType()
        )
        StructureVillagePiecesMoon.getNextStructureComponentVillagePath(
            par1StructureComponent,
            par2List,
            par3Random,
            this.boundingBox.minX + 1,
            this.boundingBox.maxY - 4,
            this.boundingBox.maxZ + 1,
            EnumFacing.byHorizontalIndex(0),
            this.getComponentType()
        )
    }

    override fun addComponentParts(
        par1World: World,
        par2Random: Random,
        par3StructureBoundingBox: StructureBoundingBox
    ): Boolean {
        if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = this.getAverageGroundLevel(par1World, par3StructureBoundingBox)

            if (this.averageGroundLevel < 0) {
                return true
            }

            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 3, 0)
        }

        this.fillWithBlocks(
            par1World,
            par3StructureBoundingBox,
            1,
            0,
            1,
            4,
            12,
            4,
            (GCBlocks.basicBlock)!!.getStateFromMeta(4),
            Blocks.FLOWING_WATER.getDefaultState(),
            false
        )
        this.setBlockState(par1World, Blocks.AIR.getDefaultState(), 2, 12, 2, par3StructureBoundingBox)
        this.setBlockState(par1World, Blocks.AIR.getDefaultState(), 3, 12, 2, par3StructureBoundingBox)
        this.setBlockState(par1World, Blocks.AIR.getDefaultState(), 2, 12, 3, par3StructureBoundingBox)
        this.setBlockState(par1World, Blocks.AIR.getDefaultState(), 3, 12, 3, par3StructureBoundingBox)
        this.setBlockState(par1World, Blocks.DARK_OAK_FENCE.getDefaultState(), 1, 13, 1, par3StructureBoundingBox)
        this.setBlockState(par1World, Blocks.DARK_OAK_FENCE.getDefaultState(), 1, 14, 1, par3StructureBoundingBox)
        this.setBlockState(par1World, Blocks.DARK_OAK_FENCE.getDefaultState(), 4, 13, 1, par3StructureBoundingBox)
        this.setBlockState(par1World, Blocks.DARK_OAK_FENCE.getDefaultState(), 4, 14, 1, par3StructureBoundingBox)
        this.setBlockState(par1World, Blocks.DARK_OAK_FENCE.getDefaultState(), 1, 13, 4, par3StructureBoundingBox)
        this.setBlockState(par1World, Blocks.DARK_OAK_FENCE.getDefaultState(), 1, 14, 4, par3StructureBoundingBox)
        this.setBlockState(par1World, Blocks.DARK_OAK_FENCE.getDefaultState(), 4, 13, 4, par3StructureBoundingBox)
        this.setBlockState(par1World, Blocks.DARK_OAK_FENCE.getDefaultState(), 4, 14, 4, par3StructureBoundingBox)
        this.fillWithBlocks(
            par1World,
            par3StructureBoundingBox,
            1,
            15,
            1,
            4,
            15,
            4,
            (GCBlocks.basicBlock)!!.getStateFromMeta(4),
            (GCBlocks.basicBlock)!!.getStateFromMeta(4),
            false
        )

        for (var4: Int in 0..5) {
            for (var5: Int in 0..5) {
                if (var5 == 0 || var5 == 5 || var4 == 0 || var4 == 5) {
                    this.setBlockState(
                        par1World,
                        Blocks.PLANKS.getDefaultState().withProperty(
                            BlockPlanks.VARIANT,
                            BlockPlanks.EnumType.SPRUCE
                        ),
                        var5,
                        11,
                        var4,
                        par3StructureBoundingBox
                    )
                    this.clearCurrentPositionBlocksUpwards(par1World, var5, 12, var4, par3StructureBoundingBox)
                }
            }
        }
        return true
    }
}
