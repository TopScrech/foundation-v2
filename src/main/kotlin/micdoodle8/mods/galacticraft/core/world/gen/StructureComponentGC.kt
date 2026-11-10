package micdoodle8.mods.galacticraft.core.world.gen

import net.minecraft.init.Blocks
import net.minecraft.tileentity.TileEntityMobSpawner
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.gen.structure.StructureBoundingBox
import net.minecraft.world.gen.structure.StructureComponent
import java.util.Random

abstract class StructureComponentGC(var1: Int) : StructureComponent(var1) {
    protected fun placeSpawnerAtCurrentPosition(
        var1: World,
        var2: Random?,
        var3: Int,
        var4: Int,
        var5: Int,
        var6: ResourceLocation?,
        var7: StructureBoundingBox
    ) {
        val var8: Int = this.getXWithOffset(var3, var5)
        val var9: Int = this.getYWithOffset(var4)
        val var10: Int = this.getZWithOffset(var3, var5)

        val pos: BlockPos = BlockPos(var8, var9, var10)
        if (var7.isVecInside(pos) && var1.getBlockState(pos).getBlock() !== Blocks.MOB_SPAWNER) {
            var1.setBlockState(pos, Blocks.MOB_SPAWNER.getDefaultState(), 2)
            val var11: TileEntityMobSpawner? = var1.getTileEntity(pos) as TileEntityMobSpawner?

            if (var11 != null) {
                var11.getSpawnerBaseLogic().setEntityId(var6)
            }
        }
    }

    protected fun offsetTowerCoords(var1: Int, var2: Int, var3: Int, var4: Int, var5: Int): IntArray {
        val var6: Int = this.getXWithOffset(var1, var3)
        val var7: Int = this.getYWithOffset(var2)
        val var8: Int = this.getZWithOffset(var1, var3)
        return if (var5 == 0) intArrayOf(
            var6 + 1,
            var7 - 1,
            var8 - var4 / 2
        ) else if (var5 == 1) intArrayOf(var6 + var4 / 2, var7 - 1, var8 + 1) else if (var5 == 2) intArrayOf(
            var6 - 1,
            var7 - 1,
            var8 + var4 / 2
        ) else if (var5 == 3) intArrayOf(var6 - var4 / 2, var7 - 1, var8 - 1) else intArrayOf(var1, var2, var3)
    }

    fun getOffsetAsIfRotated(var1: IntArray, var2: EnumFacing?): IntArray {
        val var3: EnumFacing? = getCoordBaseMode()
        val var4: IntArray = IntArray(3)
        this.setCoordBaseMode(var2)
        var4[0] = this.getXWithOffset(var1[0], var1[2])
        var4[1] = this.getYWithOffset(var1[1])
        var4[2] = this.getZWithOffset(var1[0], var1[2])
        this.setCoordBaseMode(var3)
        return var4
    }

    override fun getXWithOffset(var1: Int, var2: Int): Int {
        when (getCoordBaseMode()!!.getHorizontalIndex()) {
            0 -> return this.boundingBox.minX + var1

            1 -> return this.boundingBox.maxX - var2

            2 -> return this.boundingBox.maxX - var1

            3 -> return this.boundingBox.minX + var2

            else -> return var1
        }
    }

    override fun getZWithOffset(var1: Int, var2: Int): Int {
        when (getCoordBaseMode()!!.getHorizontalIndex()) {
            0 -> return this.boundingBox.minZ + var2

            1 -> return this.boundingBox.minZ + var1

            2 -> return this.boundingBox.maxZ - var2

            3 -> return this.boundingBox.maxZ - var1

            else -> return var2
        }
    }

    override fun getYWithOffset(var1: Int): Int {
        return super.getYWithOffset(var1)
    }

    protected object SwitchEnumFacing {
        @JvmField
        var field_176064_a: IntArray = IntArray(EnumFacing.VALUES.size)

        init {
            try {
                field_176064_a[EnumFacing.NORTH.ordinal] = 1
            } catch (var4: NoSuchFieldError) {
            }

            try {
                field_176064_a[EnumFacing.SOUTH.ordinal] = 2
            } catch (var3: NoSuchFieldError) {
            }

            try {
                field_176064_a[EnumFacing.WEST.ordinal] = 3
            } catch (var2: NoSuchFieldError) {
            }

            try {
                field_176064_a[EnumFacing.EAST.ordinal] = 4
            } catch (var1: NoSuchFieldError) {
            }
        }
    }

    companion object {
        @JvmStatic
        fun getComponentToAddBoundingBox(
            x: Int,
            y: Int,
            z: Int,
            lengthOffset: Int,
            heightOffset: Int,
            widthOffset: Int,
            length: Int,
            height: Int,
            width: Int,
            coordBaseMode: EnumFacing?
        ): StructureBoundingBox {
            if (coordBaseMode != null) {
                when (SwitchEnumFacing.field_176064_a[coordBaseMode.ordinal]) {
                    0 -> return StructureBoundingBox(
                        x + lengthOffset,
                        y + heightOffset,
                        z + widthOffset,
                        x + length + lengthOffset,
                        y + height + heightOffset,
                        z + width + widthOffset
                    )

                    1 -> return StructureBoundingBox(
                        x - width + widthOffset,
                        y + heightOffset,
                        z + lengthOffset,
                        x + widthOffset,
                        y + height + heightOffset,
                        z + length + lengthOffset
                    )

                    2 -> return StructureBoundingBox(
                        x - length - lengthOffset,
                        y + heightOffset,
                        z - width - widthOffset,
                        x - lengthOffset,
                        y + height + heightOffset,
                        z - widthOffset
                    )

                    3 -> return StructureBoundingBox(
                        x + widthOffset,
                        y + heightOffset,
                        z - length,
                        x + width + widthOffset,
                        y + height + heightOffset,
                        z + lengthOffset
                    )
                }
            }
            return StructureBoundingBox(
                x + lengthOffset,
                y + heightOffset,
                z + widthOffset,
                x + length + lengthOffset,
                y + height + heightOffset,
                z + width + widthOffset
            )
        }
    }
}
