package micdoodle8.mods.galacticraft.core.world.gen

import net.minecraft.core.Direction as EnumFacing
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.gen.structure.StructureComponent
import java.util.ArrayList
import java.util.Random
import kotlin.math.abs

object StructureVillagePiecesMoon {
    @JvmStatic
    fun getStructureVillageWeightedPieceList(
        par0Random: Random,
        par1: Int
    ): ArrayList<StructureVillagePieceWeightMoon?> {
        val var2: ArrayList<StructureVillagePieceWeightMoon?> = ArrayList<StructureVillagePieceWeightMoon?>()
        var2.add(
            StructureVillagePieceWeightMoon(
                StructureComponentVillageWoodHut::class.java,
                5,
                MathHelper.getInt(par0Random, 2 + par1, 5 + par1 * 3)
            )
        )
        var2.add(
            StructureVillagePieceWeightMoon(
                StructureComponentVillageField::class.java,
                5,
                MathHelper.getInt(par0Random, 3 + par1, 5 + par1)
            )
        )
        var2.add(
            StructureVillagePieceWeightMoon(
                StructureComponentVillageField2::class.java,
                5,
                MathHelper.getInt(par0Random, 3 + par1, 5 + par1)
            )
        )
        var2.add(
            StructureVillagePieceWeightMoon(
                StructureComponentVillageHouse::class.java,
                5,
                MathHelper.getInt(par0Random, 3 + par1, 4 + par1 * 2)
            )
        )

        val var3: MutableIterator<StructureVillagePieceWeightMoon?> = var2.iterator()

        while (var3.hasNext()) {
            if (var3.next()!!.villagePiecesLimit == 0) {
                var3.remove()
            }
        }

        return var2
    }

    @JvmStatic
    private fun func_75079_a(par0List: MutableList<StructureVillagePieceWeightMoon?>): Int {
        var var1: Boolean = false
        var var2: Int = 0
        var var4: StructureVillagePieceWeightMoon

        val var3: MutableIterator<StructureVillagePieceWeightMoon> = (par0List.iterator() as MutableIterator<StructureVillagePieceWeightMoon>)
        while (var3.hasNext()) {
            var4 = var3.next()

            if (var4.villagePiecesLimit > 0 && var4.villagePiecesSpawned < var4.villagePiecesLimit) {
                var1 = true
            }
            var2 += var4.villagePieceWeight
        }

        return if (var1) var2 else -1
    }

    @JvmStatic
    private fun func_75083_a(
        par0ComponentVillageStartPiece: StructureComponentVillageStartPiece,
        par1StructureVillagePieceWeight: StructureVillagePieceWeightMoon,
        par2List: MutableList<StructureComponent?>?,
        par3Random: Random,
        par4: Int,
        par5: Int,
        par6: Int,
        par7: EnumFacing?,
        par8: Int
    ): StructureComponentVillage? {
        val var9: Class<*>? = par1StructureVillagePieceWeight.villagePieceClass
        var var10: Any? = null

        if (var9 == StructureComponentVillageWoodHut::class.java) {
            var10 = StructureComponentVillageWoodHut.Companion.func_74908_a(
                par0ComponentVillageStartPiece,
                (par2List)!!,
                par3Random,
                par4,
                par5,
                par6,
                (par7)!!,
                par8
            )
        } else if (var9 == StructureComponentVillageField::class.java) {
            var10 = StructureComponentVillageField.Companion.func_74900_a(
                par0ComponentVillageStartPiece,
                (par2List)!!,
                par3Random,
                par4,
                par5,
                par6,
                (par7)!!,
                par8
            )
        } else if (var9 == StructureComponentVillageField2::class.java) {
            var10 = StructureComponentVillageField2.Companion.func_74900_a(
                par0ComponentVillageStartPiece,
                (par2List)!!,
                par3Random,
                par4,
                par5,
                par6,
                (par7)!!,
                par8
            )
        } else if (var9 == StructureComponentVillageHouse::class.java) {
            var10 = StructureComponentVillageHouse.Companion.func_74921_a(
                par0ComponentVillageStartPiece,
                (par2List)!!,
                par3Random,
                par4,
                par5,
                par6,
                (par7)!!,
                par8
            )
        }

        return var10 as StructureComponentVillage?
    }

    @JvmStatic
    private fun getNextVillageComponent(
        par0ComponentVillageStartPiece: StructureComponentVillageStartPiece,
        par1List: MutableList<StructureComponent?>?,
        par2Random: Random,
        par3: Int,
        par4: Int,
        par5: Int,
        par6: EnumFacing?,
        par7: Int
    ): StructureComponentVillage? {
        val var8: Int = func_75079_a((par0ComponentVillageStartPiece.structureVillageWeightedPieceList)!!)

        if (var8 <= 0) {
            return null
        } else {
            var var9: Int = 0

            while (var9 < 5) {
                ++var9
                var var10: Int = par2Random.nextInt(var8)
                val var11: MutableIterator<StructureVillagePieceWeightMoon> =
                    ((par0ComponentVillageStartPiece.structureVillageWeightedPieceList)!!.iterator() as MutableIterator<StructureVillagePieceWeightMoon>)

                while (var11.hasNext()) {
                    val var12: StructureVillagePieceWeightMoon = var11.next()
                    var10 -= var12.villagePieceWeight

                    if (var10 < 0) {
                        if (!var12.canSpawnMoreVillagePiecesOfType(par7) || var12 === par0ComponentVillageStartPiece.structVillagePieceWeight && (par0ComponentVillageStartPiece.structureVillageWeightedPieceList)!!.size > 1) {
                            break
                        }

                        val var13: StructureComponentVillage? = func_75083_a(
                            par0ComponentVillageStartPiece,
                            var12,
                            par1List,
                            par2Random,
                            par3,
                            par4,
                            par5,
                            par6,
                            par7
                        )

                        if (var13 != null) {
                            ++var12.villagePiecesSpawned
                            par0ComponentVillageStartPiece.structVillagePieceWeight = var12

                            if (!var12.canSpawnMoreVillagePieces()) {
                                (par0ComponentVillageStartPiece.structureVillageWeightedPieceList)!!.remove(var12)
                            }

                            return var13
                        }
                    }
                }
            }

            val var14: BoundingBox? = StructureComponentVillageTorch.Companion.func_74904_a(
                par0ComponentVillageStartPiece,
                (par1List)!!,
                par2Random,
                par3,
                par4,
                par5,
                (par6)!!
            )

            if (var14 != null) {
                return StructureComponentVillageTorch(par0ComponentVillageStartPiece, par7, par2Random, var14, par6)
            } else {
                return null
            }
        }
    }

    /**
     * attempts to find a next Structure Component to be spawned, private
     * Village function
     */
    @JvmStatic
    private fun getNextVillageStructureComponent(
        par0ComponentVillageStartPiece: StructureComponentVillageStartPiece,
        par1List: MutableList<StructureComponent?>,
        par2Random: Random,
        par3: Int,
        par4: Int,
        par5: Int,
        par6: EnumFacing?,
        par7: Int
    ): StructureComponent? {
        if (par7 > 50) {
            return null
        } else if (abs(par3 - par0ComponentVillageStartPiece.getBoundingBox().minX()) <= 112 && abs(par5 - par0ComponentVillageStartPiece.getBoundingBox().minZ()) <= 112) {
            val var8: StructureComponentVillage? = getNextVillageComponent(
                par0ComponentVillageStartPiece,
                par1List,
                par2Random,
                par3,
                par4,
                par5,
                par6,
                par7 + 1
            )

            if (var8 != null) {
                par1List.add(var8)
                par0ComponentVillageStartPiece.field_74932_i.add(var8)
                return var8
            }

            return null
        } else {
            return null
        }
    }

    @JvmStatic
    private fun getNextComponentVillagePath(
        par0ComponentVillageStartPiece: StructureComponentVillageStartPiece,
        par1List: MutableList<StructureComponent?>,
        par2Random: Random,
        par3: Int,
        par4: Int,
        par5: Int,
        par6: EnumFacing?,
        par7: Int
    ): StructureComponent? {
        if (par7 > 3 + par0ComponentVillageStartPiece.terrainType) {
            return null
        } else if (abs(par3 - par0ComponentVillageStartPiece.getBoundingBox().minX()) <= 112 && abs(par5 - par0ComponentVillageStartPiece.getBoundingBox().minZ()) <= 112) {
            val var8: BoundingBox? = StructureComponentVillagePathGen.Companion.func_74933_a(
                par0ComponentVillageStartPiece,
                par1List,
                par2Random,
                par3,
                par4,
                par5,
                (par6)!!
            )

            if (var8 != null && var8.minY() > 10) {
                val var9: StructureComponentVillagePathGen =
                    StructureComponentVillagePathGen(par0ComponentVillageStartPiece, par7, par2Random, var8, par6)

                par1List.add(var9)
                par0ComponentVillageStartPiece.field_74930_j.add(var9)
                return var9
            }

            return null
        } else {
            return null
        }
    }

    /**
     * attempts to find a next Structure Component to be spawned
     */
    @JvmStatic
    fun getNextStructureComponent(
        par0ComponentVillageStartPiece: StructureComponentVillageStartPiece,
        par1List: MutableList<StructureComponent?>,
        par2Random: Random,
        par3: Int,
        par4: Int,
        par5: Int,
        par6: EnumFacing?,
        par7: Int
    ): StructureComponent? {
        return getNextVillageStructureComponent(
            par0ComponentVillageStartPiece,
            par1List,
            par2Random,
            par3,
            par4,
            par5,
            par6,
            par7
        )
    }

    @JvmStatic
    fun getNextStructureComponentVillagePath(
        par0ComponentVillageStartPiece: StructureComponentVillageStartPiece,
        par1List: MutableList<StructureComponent?>,
        par2Random: Random,
        par3: Int,
        par4: Int,
        par5: Int,
        par6: EnumFacing?,
        par7: Int
    ): StructureComponent? {
        return getNextComponentVillagePath(
            par0ComponentVillageStartPiece,
            par1List,
            par2Random,
            par3,
            par4,
            par5,
            par6,
            par7
        )
    }
}
