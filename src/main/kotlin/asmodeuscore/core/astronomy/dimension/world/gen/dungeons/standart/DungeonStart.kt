package asmodeuscore.core.astronomy.dimension.world.gen.dungeons.standart

import foundation.level.horizontalStructureBounds

import com.google.common.collect.Lists
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.gen.structure.StructureComponent
import foundation.logging.FoundationLog as FMLLog
import java.util.Random

open class DungeonStart : RoomEntrance {
    @JvmField
    var attachedComponents: MutableList<StructureComponent?> = Lists.newArrayList<StructureComponent?>()

    @JvmField
    var componentBounds: MutableList<BoundingBox> = Lists.newArrayList<BoundingBox>()

    constructor()

    constructor(
        world: World?,
        configuration: DungeonConfiguration?,
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
            var next: Piece? = getNextPiece(this, rand)
            while (next != null) {
                listIn.add(next)
                attachedComponents.add(next)
                componentBounds.add(next.getBoundingBox())

                next = next.getNextPiece(this, rand)
            }
            if (attachedComponents.size >= 3 && attachedComponents.get(attachedComponents.size - 1) is RoomTreasure &&
                attachedComponents.get(attachedComponents.size - 3) is RoomBoss
            ) {
                validAttempt = true
            }
            attempts++
        }

        FMLLog.warning("GS Dungeon generation took " + attempts + " attempt(s)")

        if (!validAttempt) {
            val xPos: Int = this.boundingBox.minX() + (this.boundingBox.maxX() - this.boundingBox.minX()) / 2
            val zPos: Int = this.boundingBox.minZ() + (this.boundingBox.maxZ() - this.boundingBox.minZ()) / 2
            System.err.println("Could not find valid dungeon layout! This is a bug, please report it, including your world seed (/seed) and dungeon location (" + xPos + ", " + zPos + ")")
        }

        super.buildComponent(componentIn, listIn, rand)
    }

    open fun checkIntersection(blockX: Int, blockZ: Int, sizeX: Int, sizeZ: Int): Boolean {
        return this.checkIntersection(horizontalStructureBounds(blockX, blockZ, blockX + sizeX, blockZ + sizeZ))
    }

    open fun checkIntersection(bounds: BoundingBox): Boolean {
        for (i: Int in 0..<componentBounds.size - 1) {
            val boundingBox: BoundingBox = componentBounds.get(i)
            if (boundingBox.intersects(bounds)) {
                return true
            }
        }

        return false
    }
}
