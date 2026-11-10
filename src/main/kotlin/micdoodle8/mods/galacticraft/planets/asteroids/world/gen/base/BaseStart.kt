package micdoodle8.mods.galacticraft.planets.asteroids.world.gen.base

import com.google.common.collect.Lists
import net.minecraft.util.EnumFacing
import net.minecraft.world.gen.structure.StructureBoundingBox
import net.minecraft.world.gen.structure.StructureComponent
import java.util.Random

open class BaseStart : BaseDeck {
    @JvmField
    var attachedComponents: MutableList<StructureComponent?> = Lists.newArrayList<StructureComponent?>()

    @JvmField
    var componentBounds: MutableList<StructureBoundingBox?> = Lists.newArrayList<StructureBoundingBox?>()

    constructor()

    constructor(
        configuration: BaseConfiguration,
        rand: Random?,
        blockPosX: Int,
        blockPosZ: Int,
        direction: EnumFacing?
    ) : super(configuration, (rand)!!, blockPosX, configuration.getYPosition(), blockPosZ, 1, direction)

    override fun buildComponent(
        componentIn: StructureComponent,
        listIn: MutableList<StructureComponent?>,
        rand: Random
    ) {
        attachedComponents.clear()
        componentBounds.clear()
        componentBounds.add(this.boundingBox)
        listIn.clear()
        listIn.add(this)
        val rooms: MutableList<Piece> = (getRooms(0, this, rand) as MutableList<Piece>)
        for (next: Piece in rooms) {
            listIn.add(next)
            attachedComponents.add(next)
            componentBounds.add(next.getBoundingBox())
        }

        //TODO  applyAsteroidDamage();
        super.buildComponent(componentIn, listIn, rand)
    }
}
