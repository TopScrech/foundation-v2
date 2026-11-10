package micdoodle8.mods.galacticraft.core.world.gen

import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraft.world.gen.structure.StructureBoundingBox
import net.minecraft.world.gen.structure.StructureComponent
import net.minecraft.world.gen.structure.template.TemplateManager
import java.util.Random

open class StructureComponentVillageField : StructureComponentVillage {
    private var averageGroundLevel = -1

    constructor()

    constructor(
        par1ComponentVillageStartPiece: StructureComponentVillageStartPiece?,
        par2: Int,
        par3Random: Random?,
        par4StructureBoundingBox: StructureBoundingBox?,
        par5: EnumFacing?
    ) : super(par1ComponentVillageStartPiece, par2) {
        this.setCoordBaseMode(par5)
        this.boundingBox = par4StructureBoundingBox
    }

    override fun writeStructureToNBT(nbt: NBTTagCompound) {
        super.writeStructureToNBT(nbt)

        nbt.setInteger("AvgGroundLevel", this.averageGroundLevel)
    }

    override fun readStructureFromNBT(nbt: NBTTagCompound, manager: TemplateManager) {
        super.readStructureFromNBT(nbt, manager)

        this.averageGroundLevel = nbt.getInteger("AvgGroundLevel")
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

            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 7 - 1, 0)
        }

        this.fillWithBlocks(
            par1World,
            par3StructureBoundingBox,
            0,
            1,
            0,
            12,
            4,
            8,
            Blocks.AIR.getDefaultState(),
            Blocks.AIR.getDefaultState(),
            false
        )
        this.fillWithBlocks(
            par1World,
            par3StructureBoundingBox,
            1,
            0,
            1,
            2,
            0,
            7,
            Blocks.DIRT.getDefaultState(),
            Blocks.DIRT.getDefaultState(),
            false
        )
        this.fillWithBlocks(
            par1World,
            par3StructureBoundingBox,
            4,
            0,
            1,
            5,
            0,
            7,
            Blocks.DIRT.getDefaultState(),
            Blocks.DIRT.getDefaultState(),
            false
        )
        this.fillWithBlocks(
            par1World,
            par3StructureBoundingBox,
            7,
            0,
            1,
            8,
            0,
            7,
            Blocks.DIRT.getDefaultState(),
            Blocks.DIRT.getDefaultState(),
            false
        )
        this.fillWithBlocks(
            par1World,
            par3StructureBoundingBox,
            10,
            0,
            1,
            11,
            0,
            7,
            Blocks.DIRT.getDefaultState(),
            Blocks.DIRT.getDefaultState(),
            false
        )
        this.fillWithBlocks(
            par1World,
            par3StructureBoundingBox,
            0,
            0,
            0,
            0,
            0,
            8,
            Blocks.LOG.getDefaultState(),
            Blocks.LOG.getDefaultState(),
            false
        )
        this.fillWithBlocks(
            par1World,
            par3StructureBoundingBox,
            6,
            0,
            0,
            6,
            0,
            8,
            Blocks.LOG.getDefaultState(),
            Blocks.LOG.getDefaultState(),
            false
        )
        this.fillWithBlocks(
            par1World,
            par3StructureBoundingBox,
            12,
            0,
            0,
            12,
            0,
            8,
            Blocks.LOG.getDefaultState(),
            Blocks.LOG.getDefaultState(),
            false
        )
        this.fillWithBlocks(
            par1World,
            par3StructureBoundingBox,
            1,
            0,
            0,
            11,
            0,
            0,
            Blocks.LOG.getDefaultState(),
            Blocks.LOG.getDefaultState(),
            false
        )
        this.fillWithBlocks(
            par1World,
            par3StructureBoundingBox,
            1,
            0,
            8,
            11,
            0,
            8,
            Blocks.LOG.getDefaultState(),
            Blocks.LOG.getDefaultState(),
            false
        )
        this.fillWithBlocks(
            par1World,
            par3StructureBoundingBox,
            3,
            0,
            1,
            3,
            0,
            7,
            Blocks.FLOWING_WATER.getDefaultState(),
            Blocks.FLOWING_WATER.getDefaultState(),
            false
        )
        this.fillWithBlocks(
            par1World,
            par3StructureBoundingBox,
            9,
            0,
            1,
            9,
            0,
            7,
            Blocks.FLOWING_WATER.getDefaultState(),
            Blocks.FLOWING_WATER.getDefaultState(),
            false
        )
        var var4: Int

        var4 = 1
        while (var4 <= 7) {
            for (i: Int in 1..11) {
                if (i % 3 != 0) {
                    if (par2Random.nextInt(3) == 0) {
                        this.setBlockState(
                            par1World,
                            Blocks.SAPLING.getStateFromMeta(MathHelper.getInt(par2Random, 0, 2)),
                            i,
                            1,
                            var4,
                            par3StructureBoundingBox
                        )
                    }
                }
            }
            ++var4
        }

        var4 = 0
        while (var4 < 9) {
            for (var5: Int in 0..12) {
                this.clearCurrentPositionBlocksUpwards(par1World, var5, 4, var4, par3StructureBoundingBox)
                this.replaceAirAndLiquidDownwards(
                    par1World,
                    Blocks.DIRT.getDefaultState(),
                    var5,
                    -1,
                    var4,
                    par3StructureBoundingBox
                )
            }
            ++var4
        }

        return true
    }

    companion object {
        @JvmStatic
        fun func_74900_a(
            par0ComponentVillageStartPiece: StructureComponentVillageStartPiece?,
            par1List: MutableList<StructureComponent?>,
            par2Random: Random?,
            par3: Int,
            par4: Int,
            par5: Int,
            par6: EnumFacing,
            par7: Int
        ): StructureComponentVillageField? {
            val var8: StructureBoundingBox =
                StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 13, 4, 9, par6)
            return if (findIntersecting(par1List, var8) == null) StructureComponentVillageField(
                par0ComponentVillageStartPiece,
                par7,
                par2Random,
                var8,
                par6
            ) else null
        }
    }
}
