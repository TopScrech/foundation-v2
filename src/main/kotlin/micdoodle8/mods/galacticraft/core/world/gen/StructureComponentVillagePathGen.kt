package micdoodle8.mods.galacticraft.core.world.gen

import micdoodle8.mods.galacticraft.core.GCBlocks
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraft.world.gen.structure.StructureBoundingBox
import net.minecraft.world.gen.structure.StructureComponent
import net.minecraft.world.gen.structure.template.TemplateManager
import java.util.Random
import kotlin.math.max

open class StructureComponentVillagePathGen : StructureComponentVillageRoadPiece {
    private var averageGroundLevel = 0

    constructor()

    constructor(
        par1ComponentVillageStartPiece: StructureComponentVillageStartPiece?,
        par2: Int,
        par3Random: Random?,
        par4StructureBoundingBox: StructureBoundingBox,
        par5: EnumFacing?
    ) : super(par1ComponentVillageStartPiece, par2) {
        this.setCoordBaseMode(par5)
        this.boundingBox = par4StructureBoundingBox
        this.averageGroundLevel = max(par4StructureBoundingBox.getXSize(), par4StructureBoundingBox.getZSize())
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
        var var4: Boolean = false
        var var5: Int
        var var6: StructureComponent?

        var5 = par3Random.nextInt(5)
        while (var5 < this.averageGroundLevel - 8) {
            var6 = this.getNextComponentNN(
                par1StructureComponent as StructureComponentVillageStartPiece,
                par2List,
                par3Random,
                0,
                var5
            )

            if (var6 != null) {
                var5 += max(var6.getBoundingBox().getXSize(), var6.getBoundingBox().getZSize())
                var4 = true
            }
            var5 += 2 + par3Random.nextInt(5)
        }

        var5 = par3Random.nextInt(5)
        while (var5 < this.averageGroundLevel - 8) {
            var6 = this.getNextComponentPP(
                par1StructureComponent as StructureComponentVillageStartPiece,
                par2List,
                par3Random,
                0,
                var5
            )

            if (var6 != null) {
                var5 += max(var6.getBoundingBox().getXSize(), var6.getBoundingBox().getZSize())
                var4 = true
            }
            var5 += 2 + par3Random.nextInt(5)
        }

        if (var4 && par3Random.nextInt(3) > 0) {
            when (this.getCoordBaseMode()!!.getHorizontalIndex()) {
                0 -> StructureVillagePiecesMoon.getNextStructureComponentVillagePath(
                    par1StructureComponent as StructureComponentVillageStartPiece,
                    par2List,
                    par3Random,
                    this.boundingBox.minX - 1,
                    this.boundingBox.minY,
                    this.boundingBox.maxZ - 2,
                    EnumFacing.byHorizontalIndex(1),
                    this.getComponentType()
                )

                1 -> StructureVillagePiecesMoon.getNextStructureComponentVillagePath(
                    par1StructureComponent as StructureComponentVillageStartPiece,
                    par2List,
                    par3Random,
                    this.boundingBox.minX,
                    this.boundingBox.minY,
                    this.boundingBox.minZ - 1,
                    EnumFacing.byHorizontalIndex(2),
                    this.getComponentType()
                )

                2 -> StructureVillagePiecesMoon.getNextStructureComponentVillagePath(
                    par1StructureComponent as StructureComponentVillageStartPiece,
                    par2List,
                    par3Random,
                    this.boundingBox.minX - 1,
                    this.boundingBox.minY,
                    this.boundingBox.minZ,
                    EnumFacing.byHorizontalIndex(1),
                    this.getComponentType()
                )

                3 -> StructureVillagePiecesMoon.getNextStructureComponentVillagePath(
                    par1StructureComponent as StructureComponentVillageStartPiece,
                    par2List,
                    par3Random,
                    this.boundingBox.maxX - 2,
                    this.boundingBox.minY,
                    this.boundingBox.minZ - 1,
                    EnumFacing.byHorizontalIndex(2),
                    this.getComponentType()
                )
            }
        }

        if (var4 && par3Random.nextInt(3) > 0) {
            when (this.getCoordBaseMode()!!.getHorizontalIndex()) {
                0 -> StructureVillagePiecesMoon.getNextStructureComponentVillagePath(
                    par1StructureComponent as StructureComponentVillageStartPiece,
                    par2List,
                    par3Random,
                    this.boundingBox.maxX + 1,
                    this.boundingBox.minY,
                    this.boundingBox.maxZ - 2,
                    EnumFacing.byHorizontalIndex(3),
                    this.getComponentType()
                )

                1 -> StructureVillagePiecesMoon.getNextStructureComponentVillagePath(
                    par1StructureComponent as StructureComponentVillageStartPiece,
                    par2List,
                    par3Random,
                    this.boundingBox.minX,
                    this.boundingBox.minY,
                    this.boundingBox.maxZ + 1,
                    EnumFacing.byHorizontalIndex(0),
                    this.getComponentType()
                )

                2 -> StructureVillagePiecesMoon.getNextStructureComponentVillagePath(
                    par1StructureComponent as StructureComponentVillageStartPiece,
                    par2List,
                    par3Random,
                    this.boundingBox.maxX + 1,
                    this.boundingBox.minY,
                    this.boundingBox.minZ,
                    EnumFacing.byHorizontalIndex(3),
                    this.getComponentType()
                )

                3 -> StructureVillagePiecesMoon.getNextStructureComponentVillagePath(
                    par1StructureComponent as StructureComponentVillageStartPiece,
                    par2List,
                    par3Random,
                    this.boundingBox.maxX - 2,
                    this.boundingBox.minY,
                    this.boundingBox.maxZ + 1,
                    EnumFacing.byHorizontalIndex(0),
                    this.getComponentType()
                )
            }
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
        val var4: Block = Blocks.PLANKS

        for (var5: Int in this.boundingBox.minX..this.boundingBox.maxX) {
            for (var6: Int in this.boundingBox.minZ..this.boundingBox.maxZ) {
                val pos: BlockPos =
                    BlockPos(var5, par1World.getTopSolidOrLiquidBlock(BlockPos(var5, 0, var6)).getY() - 1, var6)
                val state: IBlockState = par1World.getBlockState(pos)
                if (par3StructureBoundingBox.isVecInside(
                        BlockPos(
                            var5,
                            64,
                            var6
                        )
                    ) && (state.getBlock() === GCBlocks.blockMoon && state.getBlock()
                        .getMetaFromState(state) == 5 || Blocks.AIR === state.getBlock())
                ) {
                    val var7: Int = par1World.getTopSolidOrLiquidBlock(BlockPos(var5, 0, var6)).getY() - 1
                    par1World.setBlockState(BlockPos(var5, var7, var6), var4.getStateFromMeta(1), 2)
                }
            }
        }

        return true
    }

    companion object {
        @JvmStatic
        fun func_74933_a(
            par0ComponentVillageStartPiece: StructureComponentVillageStartPiece?,
            par1List: MutableList<StructureComponent?>,
            par2Random: Random,
            par3: Int,
            par4: Int,
            par5: Int,
            par6: EnumFacing
        ): StructureBoundingBox? {
            var var7: Int = 7 * MathHelper.getInt(par2Random, 3, 5)
            while (var7 >= 7) {
                val var8: StructureBoundingBox =
                    StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 3, 3, var7, par6)

                if (findIntersecting(par1List, var8) == null) {
                    return var8
                }
                var7 -= 7
            }

            return null
        }
    }
}
