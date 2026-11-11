package micdoodle8.mods.galacticraft.core.world.gen

import foundation.block.LegacyVanillaBlockStates
import foundation.block.LegacyVanillaBlockIds

import foundation.block.getStateFromMeta

import net.minecraft.world.level.block.Blocks
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.levelgen.structure.BoundingBox
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
        par4StructureBoundingBox: BoundingBox?,
        par5: EnumFacing?
    ) : super(par1ComponentVillageStartPiece, par2) {
        this.setCoordBaseMode(par5)
        this.boundingBox = par4StructureBoundingBox
    }

    override fun writeStructureToNBT(nbt: NBTTagCompound) {
        super.writeStructureToNBT(nbt)

        nbt.putInt("AvgGroundLevel", this.averageGroundLevel)
    }

    override fun readStructureFromNBT(nbt: NBTTagCompound, manager: TemplateManager) {
        super.readStructureFromNBT(nbt, manager)

        this.averageGroundLevel = nbt.getIntOr("AvgGroundLevel", 0)
    }

    override fun addComponentParts(
        par1World: World,
        par2Random: Random,
        par3StructureBoundingBox: BoundingBox
    ): Boolean {
        if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = this.getAverageGroundLevel(par1World, par3StructureBoundingBox)

            if (this.averageGroundLevel < 0) {
                return true
            }

            this.boundingBox.move(0, this.averageGroundLevel - this.boundingBox.maxY() + 7 - 1, 0)
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
            Blocks.AIR.defaultBlockState(),
            Blocks.AIR.defaultBlockState(),
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
            Blocks.DIRT.defaultBlockState(),
            Blocks.DIRT.defaultBlockState(),
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
            Blocks.DIRT.defaultBlockState(),
            Blocks.DIRT.defaultBlockState(),
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
            Blocks.DIRT.defaultBlockState(),
            Blocks.DIRT.defaultBlockState(),
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
            Blocks.DIRT.defaultBlockState(),
            Blocks.DIRT.defaultBlockState(),
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
            Blocks.LOG.defaultBlockState(),
            Blocks.LOG.defaultBlockState(),
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
            Blocks.LOG.defaultBlockState(),
            Blocks.LOG.defaultBlockState(),
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
            Blocks.LOG.defaultBlockState(),
            Blocks.LOG.defaultBlockState(),
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
            Blocks.LOG.defaultBlockState(),
            Blocks.LOG.defaultBlockState(),
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
            Blocks.LOG.defaultBlockState(),
            Blocks.LOG.defaultBlockState(),
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
            Blocks.FLOWING_WATER.defaultBlockState(),
            Blocks.FLOWING_WATER.defaultBlockState(),
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
            Blocks.FLOWING_WATER.defaultBlockState(),
            Blocks.FLOWING_WATER.defaultBlockState(),
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
                            LegacyVanillaBlockStates.state(LegacyVanillaBlockIds.SAPLING, MathHelper.getInt(par2Random, 0, 2)),
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
                    Blocks.DIRT.defaultBlockState(),
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
            val var8: BoundingBox =
                BoundingBox.orientBox(par3, par4, par5, 0, 0, 0, 13, 4, 9, par6)
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
