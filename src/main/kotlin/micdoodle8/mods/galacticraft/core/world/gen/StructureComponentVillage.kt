package micdoodle8.mods.galacticraft.core.world.gen

import micdoodle8.mods.galacticraft.core.entities.EntityAlienVillager
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.gen.structure.StructureBoundingBox
import net.minecraft.world.gen.structure.StructureComponent
import net.minecraft.world.gen.structure.template.TemplateManager
import java.util.Random

abstract class StructureComponentVillage : StructureComponent {
    private var villagersSpawned = 0

    @JvmField
    protected var startPiece: StructureComponentVillageStartPiece? = null

    constructor()

    protected constructor(
        par1ComponentVillageStartPiece: StructureComponentVillageStartPiece?,
        par2: Int
    ) : super(par2) {
        this.startPiece = par1ComponentVillageStartPiece
    }

    override fun writeStructureToNBT(nbttagcompound: NBTTagCompound) {
        nbttagcompound.setInteger("VCount", this.villagersSpawned)
    }

    override fun readStructureFromNBT(nbt: NBTTagCompound, manager: TemplateManager) {
        this.villagersSpawned = nbt.getInteger("VCount")
    }

    protected fun getNextComponentNN(
        par1ComponentVillageStartPiece: StructureComponentVillageStartPiece?,
        par2List: MutableList<StructureComponent?>?,
        par3Random: Random?,
        par4: Int,
        par5: Int
    ): StructureComponent? {
        when (this.getCoordBaseMode()!!.getHorizontalIndex()) {
            0 -> return StructureVillagePiecesMoon.getNextStructureComponent(
                (par1ComponentVillageStartPiece)!!,
                (par2List)!!,
                (par3Random)!!,
                this.boundingBox.minX - 1,
                this.boundingBox.minY + par4,
                this.boundingBox.minZ + par5,
                EnumFacing.byHorizontalIndex(1),
                this.getComponentType()
            )

            1 -> return StructureVillagePiecesMoon.getNextStructureComponent(
                (par1ComponentVillageStartPiece)!!,
                (par2List)!!,
                (par3Random)!!,
                this.boundingBox.minX + par5,
                this.boundingBox.minY + par4,
                this.boundingBox.minZ - 1,
                EnumFacing.byHorizontalIndex(2),
                this.getComponentType()
            )

            2 -> return StructureVillagePiecesMoon.getNextStructureComponent(
                (par1ComponentVillageStartPiece)!!,
                (par2List)!!,
                (par3Random)!!,
                this.boundingBox.minX - 1,
                this.boundingBox.minY + par4,
                this.boundingBox.minZ + par5,
                EnumFacing.byHorizontalIndex(1),
                this.getComponentType()
            )

            3 -> return StructureVillagePiecesMoon.getNextStructureComponent(
                (par1ComponentVillageStartPiece)!!,
                (par2List)!!,
                (par3Random)!!,
                this.boundingBox.minX + par5,
                this.boundingBox.minY + par4,
                this.boundingBox.minZ - 1,
                EnumFacing.byHorizontalIndex(2),
                this.getComponentType()
            )

            else -> return null
        }
    }

    protected fun getNextComponentPP(
        par1ComponentVillageStartPiece: StructureComponentVillageStartPiece?,
        par2List: MutableList<StructureComponent?>?,
        par3Random: Random?,
        par4: Int,
        par5: Int
    ): StructureComponent? {
        when (this.getCoordBaseMode()!!.getHorizontalIndex()) {
            0 -> return StructureVillagePiecesMoon.getNextStructureComponent(
                (par1ComponentVillageStartPiece)!!,
                (par2List)!!,
                (par3Random)!!,
                this.boundingBox.maxX + 1,
                this.boundingBox.minY + par4,
                this.boundingBox.minZ + par5,
                EnumFacing.byHorizontalIndex(3),
                this.getComponentType()
            )

            1 -> return StructureVillagePiecesMoon.getNextStructureComponent(
                (par1ComponentVillageStartPiece)!!,
                (par2List)!!,
                (par3Random)!!,
                this.boundingBox.minX + par5,
                this.boundingBox.minY + par4,
                this.boundingBox.maxZ + 1,
                EnumFacing.byHorizontalIndex(0),
                this.getComponentType()
            )

            2 -> return StructureVillagePiecesMoon.getNextStructureComponent(
                (par1ComponentVillageStartPiece)!!,
                (par2List)!!,
                (par3Random)!!,
                this.boundingBox.maxX + 1,
                this.boundingBox.minY + par4,
                this.boundingBox.minZ + par5,
                EnumFacing.byHorizontalIndex(3),
                this.getComponentType()
            )

            3 -> return StructureVillagePiecesMoon.getNextStructureComponent(
                (par1ComponentVillageStartPiece)!!,
                (par2List)!!,
                (par3Random)!!,
                this.boundingBox.minX + par5,
                this.boundingBox.minY + par4,
                this.boundingBox.maxZ + 1,
                EnumFacing.byHorizontalIndex(0),
                this.getComponentType()
            )

            else -> return null
        }
    }

    protected fun getAverageGroundLevel(world: World, boundingBox: StructureBoundingBox): Int {
        var i: Int = 0
        var j: Int = 0
        val mutableBlockPos: BlockPos.MutableBlockPos = BlockPos.MutableBlockPos()

        for (k: Int in this.boundingBox.minZ..this.boundingBox.maxZ) {
            for (l: Int in this.boundingBox.minX..this.boundingBox.maxX) {
                mutableBlockPos.setPos(l, 64, k)

                if (boundingBox.isVecInside(mutableBlockPos)) {
                    i += world.getTopSolidOrLiquidBlock(mutableBlockPos).getY()
                    ++j
                }
            }
        }

        if (j == 0) {
            return -1
        } else {
            return i / j
        }
    }

    protected fun spawnVillagers(
        par1World: World,
        par2StructureBoundingBox: StructureBoundingBox,
        par3: Int,
        par4: Int,
        par5: Int,
        par6: Int
    ) {
        if (this.villagersSpawned < par6) {
            for (var7: Int in this.villagersSpawned..<par6) {
                var var8: Int = this.getXWithOffset(par3 + var7, par5)
                val var9: Int = this.getYWithOffset(par4)
                var var10: Int = this.getZWithOffset(par3 + var7, par5)

                var8 += par1World.rand.nextInt(3) - 1
                var10 += par1World.rand.nextInt(3) - 1

                if (!par2StructureBoundingBox.isVecInside(BlockPos(var8, var9, var10))) {
                    break
                }

                ++this.villagersSpawned
                val var11: EntityAlienVillager = EntityAlienVillager(par1World)
                var11.setLocationAndAngles(var8 + 0.5, var9.toDouble(), var10 + 0.5, 0.0f, 0.0f)
                par1World.spawnEntity(var11)
            }
        }
    }

    companion object {
        init {
            try {
                MapGenVillageMoon.Companion.initiateStructures()
            } catch (e: Throwable) {
            }
        }

        @JvmStatic
        protected fun canVillageGoDeeper(par0StructureBoundingBox: StructureBoundingBox?): Boolean {
            return par0StructureBoundingBox != null && par0StructureBoundingBox.minY > 10
        }
    }
}
