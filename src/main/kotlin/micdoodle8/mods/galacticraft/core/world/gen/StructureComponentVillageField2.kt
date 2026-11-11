package micdoodle8.mods.galacticraft.core.world.gen

import foundation.block.getStateFromMeta

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.gen.structure.StructureComponent
import net.minecraft.world.gen.structure.template.TemplateManager
import java.util.Random

open class StructureComponentVillageField2 : StructureComponentVillage {
    private var averageGroundLevel = -1

    private var cropTypeA: Block? = null
    private var cropTypeB: Block? = null
    private var cropTypeC: Block? = null
    private var cropTypeD: Block? = null

    constructor()

    constructor(
        par1ComponentVillageStartPiece: StructureComponentVillageStartPiece?,
        par2: Int,
        par3Random: Random,
        par4StructureBoundingBox: BoundingBox?,
        par5: EnumFacing?
    ) : super(par1ComponentVillageStartPiece, par2) {
        this.setCoordBaseMode(par5)
        this.boundingBox = par4StructureBoundingBox
        this.cropTypeA = this.getRandomCrop(par3Random)
        this.cropTypeB = this.getRandomCrop(par3Random)
        this.cropTypeC = this.getRandomCrop(par3Random)
        this.cropTypeD = this.getRandomCrop(par3Random)
    }

    override fun writeStructureToNBT(nbt: NBTTagCompound) {
        super.writeStructureToNBT(nbt)

        nbt.putInt("AvgGroundLevel", this.averageGroundLevel)
        nbt.putInt("CropTypeA", Block.getIdFromBlock(this.cropTypeA))
        nbt.putInt("CropTypeB", Block.getIdFromBlock(this.cropTypeB))
        nbt.putInt("CropTypeC", Block.getIdFromBlock(this.cropTypeC))
        nbt.putInt("CropTypeD", Block.getIdFromBlock(this.cropTypeD))
    }

    override fun readStructureFromNBT(nbt: NBTTagCompound, manager: TemplateManager) {
        super.readStructureFromNBT(nbt, manager)

        this.averageGroundLevel = nbt.getIntOr("AvgGroundLevel", 0)
        this.cropTypeA = Block.getBlockById(nbt.getIntOr("CropTypeA", 0))
        this.cropTypeB = Block.getBlockById(nbt.getIntOr("CropTypeB", 0))
        this.cropTypeC = Block.getBlockById(nbt.getIntOr("CropTypeC", 0))
        this.cropTypeD = Block.getBlockById(nbt.getIntOr("CropTypeD", 0))
    }

    private fun getRandomCrop(par1Random: Random): Block? {
        when (par1Random.nextInt(5)) {
            0 -> return Blocks.CARROTS
            1 -> return Blocks.POTATOES
            else -> return Blocks.WHEAT
        }
    }

    /**
     * second Part of Structure generating, this for example places Spiderwebs,
     * Mob Spawners, it closes Mineshafts at the end, it adds Fences...
     */
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

            this.boundingBox.move(0, this.averageGroundLevel - this.boundingBox.maxY() + 4 - 1, 0)
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
            Blocks.FARMLAND.defaultBlockState(),
            Blocks.FARMLAND.defaultBlockState(),
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
            Blocks.FARMLAND.defaultBlockState(),
            Blocks.FARMLAND.defaultBlockState(),
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
            Blocks.FARMLAND.defaultBlockState(),
            Blocks.FARMLAND.defaultBlockState(),
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
            Blocks.FARMLAND.defaultBlockState(),
            Blocks.FARMLAND.defaultBlockState(),
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
        var i: Int

        i = 1
        while (i <= 7) {
            this.setBlockState(
                par1World,
                this.cropTypeA!!.getStateFromMeta(MathHelper.getInt(par2Random, 2, 7)),
                1,
                1,
                i,
                par3StructureBoundingBox
            )
            this.setBlockState(
                par1World,
                this.cropTypeA!!.getStateFromMeta(MathHelper.getInt(par2Random, 2, 7)),
                2,
                1,
                i,
                par3StructureBoundingBox
            )
            this.setBlockState(
                par1World,
                this.cropTypeB!!.getStateFromMeta(MathHelper.getInt(par2Random, 2, 7)),
                4,
                1,
                i,
                par3StructureBoundingBox
            )
            this.setBlockState(
                par1World,
                this.cropTypeB!!.getStateFromMeta(MathHelper.getInt(par2Random, 2, 7)),
                5,
                1,
                i,
                par3StructureBoundingBox
            )
            this.setBlockState(
                par1World,
                this.cropTypeC!!.getStateFromMeta(MathHelper.getInt(par2Random, 2, 7)),
                7,
                1,
                i,
                par3StructureBoundingBox
            )
            this.setBlockState(
                par1World,
                this.cropTypeC!!.getStateFromMeta(MathHelper.getInt(par2Random, 2, 7)),
                8,
                1,
                i,
                par3StructureBoundingBox
            )
            this.setBlockState(
                par1World,
                this.cropTypeD!!.getStateFromMeta(MathHelper.getInt(par2Random, 2, 7)),
                10,
                1,
                i,
                par3StructureBoundingBox
            )
            this.setBlockState(
                par1World,
                this.cropTypeD!!.getStateFromMeta(MathHelper.getInt(par2Random, 2, 7)),
                11,
                1,
                i,
                par3StructureBoundingBox
            )
            ++i
        }

        i = 0
        while (i < 9) {
            for (j: Int in 0..12) {
                this.clearCurrentPositionBlocksUpwards(par1World, j, 4, i, par3StructureBoundingBox)
                this.replaceAirAndLiquidDownwards(
                    par1World,
                    Blocks.DIRT.defaultBlockState(),
                    j,
                    -1,
                    i,
                    par3StructureBoundingBox
                )
            }
            ++i
        }

        return true
    }

    companion object {
        @JvmStatic
        fun func_74900_a(
            par0ComponentVillageStartPiece: StructureComponentVillageStartPiece?,
            par1List: MutableList<StructureComponent?>,
            par2Random: Random,
            par3: Int,
            par4: Int,
            par5: Int,
            par6: EnumFacing,
            par7: Int
        ): StructureComponentVillageField2? {
            val structureboundingbox: BoundingBox =
                BoundingBox.orientBox(par3, par4, par5, 0, 0, 0, 13, 4, 9, par6)
            return if (StructureComponentVillage.Companion.canVillageGoDeeper(structureboundingbox) && findIntersecting(
                    par1List,
                    structureboundingbox
                ) == null
            ) StructureComponentVillageField2(
                par0ComponentVillageStartPiece,
                par7,
                par2Random,
                structureboundingbox,
                par6
            ) else null
        }
    }
}
