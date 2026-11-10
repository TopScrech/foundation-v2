package micdoodle8.mods.galacticraft.core.world.gen

import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraft.world.gen.structure.StructureBoundingBox
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
        par4StructureBoundingBox: StructureBoundingBox?,
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

        nbt.setInteger("AvgGroundLevel", this.averageGroundLevel)
        nbt.setInteger("CropTypeA", Block.getIdFromBlock(this.cropTypeA))
        nbt.setInteger("CropTypeB", Block.getIdFromBlock(this.cropTypeB))
        nbt.setInteger("CropTypeC", Block.getIdFromBlock(this.cropTypeC))
        nbt.setInteger("CropTypeD", Block.getIdFromBlock(this.cropTypeD))
    }

    override fun readStructureFromNBT(nbt: NBTTagCompound, manager: TemplateManager) {
        super.readStructureFromNBT(nbt, manager)

        this.averageGroundLevel = nbt.getInteger("AvgGroundLevel")
        this.cropTypeA = Block.getBlockById(nbt.getInteger("CropTypeA"))
        this.cropTypeB = Block.getBlockById(nbt.getInteger("CropTypeB"))
        this.cropTypeC = Block.getBlockById(nbt.getInteger("CropTypeC"))
        this.cropTypeD = Block.getBlockById(nbt.getInteger("CropTypeD"))
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
        par3StructureBoundingBox: StructureBoundingBox
    ): Boolean {
        if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = this.getAverageGroundLevel(par1World, par3StructureBoundingBox)

            if (this.averageGroundLevel < 0) {
                return true
            }

            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 4 - 1, 0)
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
            Blocks.FARMLAND.getDefaultState(),
            Blocks.FARMLAND.getDefaultState(),
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
            Blocks.FARMLAND.getDefaultState(),
            Blocks.FARMLAND.getDefaultState(),
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
            Blocks.FARMLAND.getDefaultState(),
            Blocks.FARMLAND.getDefaultState(),
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
            Blocks.FARMLAND.getDefaultState(),
            Blocks.FARMLAND.getDefaultState(),
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
                    Blocks.DIRT.getDefaultState(),
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
            val structureboundingbox: StructureBoundingBox =
                StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 13, 4, 9, par6)
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
