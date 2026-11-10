package micdoodle8.mods.galacticraft.core.world.gen

import net.minecraft.world.World
import net.minecraft.world.gen.structure.StructureComponent
import net.minecraft.world.gen.structure.StructureStart
import java.util.ArrayList
import java.util.Random

open class StructureVillageStartMoon : StructureStart {
    constructor()

    constructor(par1World: World, par2Random: Random, par3: Int, par4: Int, par5: Int) : super(par3, par4) {
        val var6: ArrayList<StructureVillagePieceWeightMoon?> =
            StructureVillagePiecesMoon.getStructureVillageWeightedPieceList(par2Random, par5)
        val var7: StructureComponentVillageStartPiece = StructureComponentVillageStartPiece(
            par1World.getBiomeProvider(),
            0,
            par2Random,
            (par3 shl 4) + 2,
            (par4 shl 4) + 2,
            var6,
            par5
        )
        this.components.add(var7)
        var7.buildComponent(var7, this.components, par2Random)
        val var8: ArrayList<Any?> = var7.field_74930_j
        val var9: ArrayList<Any?> = var7.field_74932_i
        var var10: Int

        while (!var8.isEmpty() || !var9.isEmpty()) {
            val var11: StructureComponent

            if (var8.isEmpty()) {
                var10 = par2Random.nextInt(var9.size)
                var11 = var9.removeAt(var10) as StructureComponent
                var11.buildComponent(var7, this.components, par2Random)
            } else {
                var10 = par2Random.nextInt(var8.size)
                var11 = var8.removeAt(var10) as StructureComponent
                var11.buildComponent(var7, this.components, par2Random)
            }
        }

        this.updateBoundingBox()
        var10 = 0
        val var13: MutableIterator<StructureComponent?> = this.components.iterator()

        while (var13.hasNext()) {
            val var12: StructureComponent? = var13.next()

            if (var12 !is StructureComponentVillageRoadPiece) {
                ++var10
            }
        }
    }

    /**
     * currently only defined for Villages, returns true if Village has more
     * than 2 non-road components
     */
    override fun isSizeableStructure(): Boolean {
        return true
    }
}
