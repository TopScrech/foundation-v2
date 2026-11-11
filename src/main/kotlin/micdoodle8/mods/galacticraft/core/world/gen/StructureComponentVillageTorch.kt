package micdoodle8.mods.galacticraft.core.world.gen

import foundation.block.LegacyVanillaBlockStates
import foundation.block.LegacyVanillaBlockIds

import foundation.block.getStateFromMeta

import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.blocks.BlockGlowstoneTorch
import net.minecraft.world.level.block.Blocks
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.levelgen.structure.BoundingBox
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
            0,
            0,
            2,
            3,
            1,
            Blocks.AIR.defaultBlockState(),
            Blocks.AIR.defaultBlockState(),
            false
        )
        this.setBlockState(par1World, Blocks.DARK_OAK_FENCE.defaultBlockState(), 1, 0, 0, par3StructureBoundingBox)
        this.setBlockState(par1World, Blocks.DARK_OAK_FENCE.defaultBlockState(), 1, 1, 0, par3StructureBoundingBox)
        this.setBlockState(par1World, Blocks.DARK_OAK_FENCE.defaultBlockState(), 1, 2, 0, par3StructureBoundingBox)
        this.setBlockState(par1World, LegacyVanillaBlockStates.state(LegacyVanillaBlockIds.WOOL, 15), 1, 3, 0, par3StructureBoundingBox)
        val flag: Boolean = this.getCoordBaseMode() == EnumFacing.EAST || this.getCoordBaseMode() == EnumFacing.NORTH
        this.setBlockState(
            par1World,
            (GCBlocks.glowstoneTorch)!!.defaultBlockState().setValue(
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
            (GCBlocks.glowstoneTorch)!!.defaultBlockState()
                .setValue(micdoodle8.mods.galacticraft.core.blocks.BlockTorchBase.FACING, this.getCoordBaseMode()),
            1,
            3,
            1,
            par3StructureBoundingBox
        )
        this.setBlockState(
            par1World,
            (GCBlocks.glowstoneTorch)!!.defaultBlockState().setValue(
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
            (GCBlocks.glowstoneTorch)!!.defaultBlockState().setValue(
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
        ): BoundingBox? {
            val var7: BoundingBox =
                BoundingBox.orientBox(par3, par4, par5, 0, 0, 0, 3, 4, 2, par6)
            return if (findIntersecting(par1List, var7) != null) null else var7
        }
    }
}
