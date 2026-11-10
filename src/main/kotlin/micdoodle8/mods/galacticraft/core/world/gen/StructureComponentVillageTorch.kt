package micdoodle8.mods.galacticraft.core.world.gen

import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.blocks.BlockGlowstoneTorch
import net.minecraft.init.Blocks
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import net.minecraft.world.gen.structure.StructureBoundingBox
import net.minecraft.world.gen.structure.StructureComponent
import net.minecraft.world.gen.structure.template.TemplateManager
import java.util.Random

open class StructureComponentVillageTorch : StructureComponentVillage {
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
            0,
            0,
            2,
            3,
            1,
            Blocks.AIR.getDefaultState(),
            Blocks.AIR.getDefaultState(),
            false
        )
        this.setBlockState(par1World, Blocks.DARK_OAK_FENCE.getDefaultState(), 1, 0, 0, par3StructureBoundingBox)
        this.setBlockState(par1World, Blocks.DARK_OAK_FENCE.getDefaultState(), 1, 1, 0, par3StructureBoundingBox)
        this.setBlockState(par1World, Blocks.DARK_OAK_FENCE.getDefaultState(), 1, 2, 0, par3StructureBoundingBox)
        this.setBlockState(par1World, Blocks.WOOL.getStateFromMeta(15), 1, 3, 0, par3StructureBoundingBox)
        val flag: Boolean = this.getCoordBaseMode() == EnumFacing.EAST || this.getCoordBaseMode() == EnumFacing.NORTH
        this.setBlockState(
            par1World,
            (GCBlocks.glowstoneTorch)!!.getDefaultState().withProperty(
                micdoodle8.mods.galacticraft.core.blocks.BlockTorchBase.FACING,
                this.getCoordBaseMode()!!.rotateY()
            ),
            if (flag) 2 else 0,
            3,
            0,
            par3StructureBoundingBox
        )
        this.setBlockState(
            par1World,
            (GCBlocks.glowstoneTorch)!!.getDefaultState()
                .withProperty(micdoodle8.mods.galacticraft.core.blocks.BlockTorchBase.FACING, this.getCoordBaseMode()),
            1,
            3,
            1,
            par3StructureBoundingBox
        )
        this.setBlockState(
            par1World,
            (GCBlocks.glowstoneTorch)!!.getDefaultState().withProperty(
                micdoodle8.mods.galacticraft.core.blocks.BlockTorchBase.FACING,
                this.getCoordBaseMode()!!.rotateYCCW()
            ),
            if (flag) 0 else 2,
            3,
            0,
            par3StructureBoundingBox
        )
        this.setBlockState(
            par1World,
            (GCBlocks.glowstoneTorch)!!.getDefaultState().withProperty(
                micdoodle8.mods.galacticraft.core.blocks.BlockTorchBase.FACING,
                this.getCoordBaseMode()!!.getOpposite()
            ),
            1,
            3,
            -1,
            par3StructureBoundingBox
        )
        return true
    }

    companion object {
        @JvmStatic
        fun func_74904_a(
            par0ComponentVillageStartPiece: StructureComponentVillageStartPiece?,
            par1List: MutableList<StructureComponent?>,
            par2Random: Random?,
            par3: Int,
            par4: Int,
            par5: Int,
            par6: EnumFacing
        ): StructureBoundingBox? {
            val var7: StructureBoundingBox =
                StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 3, 4, 2, par6)
            return if (findIntersecting(par1List, var7) != null) null else var7
        }
    }
}
