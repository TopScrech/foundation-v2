package micdoodle8.mods.galacticraft.planets.venus.world.gen.dungeon

import java.util.Random

import com.google.common.collect.Lists
import micdoodle8.mods.galacticraft.core.util.GCLog
import net.minecraft.world.World
import net.minecraft.world.gen.structure.StructureBoundingBox
import net.minecraft.world.gen.structure.StructureComponent
import java.util.*

open class DungeonStartVenus : RoomEntranceVenus {
    @JvmField
    var attachedComponents: MutableList<StructureComponent?> = Lists.newArrayList<StructureComponent?>()

    @JvmField
    var componentBounds: MutableList<StructureBoundingBox> = Lists.newArrayList<StructureBoundingBox>()

    constructor()

    constructor(
        world: World?,
        configuration: DungeonConfigurationVenus?,
        rand: Random,
        blockPosX: Int,
        blockPosZ: Int
    ) : super(world, (configuration)!!, rand, blockPosX, blockPosZ)

    override fun buildComponent(
        componentIn: StructureComponent,
        listIn: MutableList<StructureComponent?>,
        rand: Random
    ) {
        var validAttempt: Boolean = false
        val maxAttempts: Int = 10
        var attempts: Int = 0
        while (!validAttempt && attempts < maxAttempts) {
            attachedComponents.clear()
            componentBounds.clear()
            componentBounds.add(this.boundingBox)
            listIn.clear()
            listIn.add(this)
            var next: PieceVenus? = getNextPiece(this, rand)
            while (next != null) {
                listIn.add(next)
                attachedComponents.add(next)
                componentBounds.add(next.getBoundingBox())

                next = next.getNextPiece(this, rand)
            }
            if (attachedComponents.size >= 3 && attachedComponents.get(attachedComponents.size - 1) is RoomTreasureVenus &&
                attachedComponents.get(attachedComponents.size - 3) is RoomBossVenus
            ) {
                validAttempt = true
            }
            attempts++
        }

        GCLog.debug("Dungeon generation took " + attempts + " attempt(s)")

        if (!validAttempt) {
            val xPos: Int = this.boundingBox.minX + (this.boundingBox.maxX - this.boundingBox.minX) / 2
            val zPos: Int = this.boundingBox.minZ + (this.boundingBox.maxZ - this.boundingBox.minZ) / 2
            System.err.println("Could not find valid venus dungeon layout! This is a bug, please report it, including your world seed (/seed) and dungeon location (" + xPos + ", " + zPos + ")")
        }
    }

    open fun checkIntersection(blockX: Int, blockZ: Int, sizeX: Int, sizeZ: Int): Boolean {
        return this.checkIntersection(StructureBoundingBox(blockX, blockZ, blockX + sizeX, blockZ + sizeZ))
    }

    open fun checkIntersection(bounds: StructureBoundingBox): Boolean {
        for (i: Int in 0..<componentBounds.size - 1) {
            val boundingBox: StructureBoundingBox = componentBounds.get(i)
            if (boundingBox.intersectsWith(bounds)) {
                return true
            }
        }

        return false
    }
}
