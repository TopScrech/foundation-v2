package micdoodle8.mods.galacticraft.core.world.gen

import foundation.block.getStateFromMeta

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.tileentity.TileEntityMobSpawner
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.core.BlockPos
import net.minecraft.world.EnumSkyBlock
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.gen.structure.StructureComponent
import java.util.Random

abstract class StructureComponentMoon : StructureComponent {
    constructor()

    constructor(var1: Int) : super(var1)

    protected fun placeSpawnerAtCurrentPosition(
        var1: World,
        var2: Random?,
        var3: Int,
        var4: Int,
        var5: Int,
        var6: ResourceLocation?,
        var7: BoundingBox
    ): TileEntityMobSpawner? {
        var var8: TileEntityMobSpawner? = null
        val var9: Int = this.getXWithOffset(var3, var5)
        val var10: Int = this.getYWithOffset(var4)
        val var11: Int = this.getZWithOffset(var3, var5)

        if (var7.isInside(BlockPos(var9, var10, var11)) && var1.getBlockState(BlockPos(var9, var10, var11))
                .getBlock() !== Blocks.MOB_SPAWNER
        ) {
            var1.setBlock(BlockPos(var9, var10, var11), Blocks.MOB_SPAWNER.defaultBlockState(), 2)
            var8 = var1.getBlockEntity(BlockPos(var9, var10, var11)) as TileEntityMobSpawner?

            if (var8 != null) {
                var8.getSpawnerBaseLogic().setEntityId(var6)
            }
        }

        return var8
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

    //    public int[] getOffsetAsIfRotated(int[] var1, int var2)
    //    {
    //        final int var3 = this.getCoordBaseMode();
    //        final int[] var4 = new int[3];
    //        this.coordBaseMode = (var2);
    //        var4[0] = this.getXWithOffset(var1[0], var1[2]);
    //        var4[1] = this.getYWithOffset(var1[1]);
    //        var4[2] = this.getZWithOffset(var1[0], var1[2]);
    //        this.setCoordBaseMode(var3);
    //        return var4;
    //    }
    override fun getXWithOffset(var1: Int, var2: Int): Int {
        when (this.getCoordBaseMode()!!.getHorizontalIndex()) {
            0 -> return this.boundingBox.minX() + var1

            1 -> return this.boundingBox.maxX() - var2

            2 -> return this.boundingBox.maxX() - var1

            3 -> return this.boundingBox.minX() + var2

            else -> return var1
        }
    }

    override fun getYWithOffset(var1: Int): Int {
        return super.getYWithOffset(var1)
    }

    override fun getZWithOffset(var1: Int, var2: Int): Int {
        when (this.getCoordBaseMode()!!.getHorizontalIndex()) {
            0 -> return this.boundingBox.minZ() + var2

            1 -> return this.boundingBox.minZ() + var1

            2 -> return this.boundingBox.maxZ() - var2

            3 -> return this.boundingBox.maxZ() - var1

            else -> return var2
        }
    }

    protected fun getXWithOffsetAsIfRotated(var1: Int, var2: Int, var3: Int): Int {
        if (this.getCoordBaseMode()!!.getHorizontalIndex() < 0) {
            return var1
        } else {
            when ((this.getCoordBaseMode()!!.getHorizontalIndex() + var3) % 4) {
                0 -> return this.boundingBox.minX() + var1

                1 -> return this.boundingBox.maxX() - var2

                2 -> return this.boundingBox.maxX() - var1

                3 -> return this.boundingBox.minX() + var2

                else -> return var1
            }
        }
    }

    protected fun getZWithOffsetAsIfRotated(var1: Int, var2: Int, var3: Int): Int {
        if (this.getCoordBaseMode()!!.getHorizontalIndex() < 0) {
            return var1
        } else {
            when ((this.getCoordBaseMode()!!.getHorizontalIndex() + var3) % 4) {
                0 -> return this.boundingBox.minZ() + var2

                1 -> return this.boundingBox.minZ() + var1

                2 -> return this.boundingBox.maxZ() - var2

                3 -> return this.boundingBox.maxZ() - var1

                else -> return var2
            }
        }
    }

    //    @Override
    //    protected Block getBlockAtCurrentPosition(World var1, int var2, int var3, int var4, BoundingBox var5)
    //    {
    //        return super.getBlockAtCurrentPosition(var1, var2, var3, var4, var5);
    //    }
    protected fun placeBlockRotated(
        var1: World,
        var2: Block,
        var3: Int,
        var4: Int,
        var5: Int,
        var6: Int,
        var7: Int,
        var8: BoundingBox
    ) {
        val var9: Int = this.getXWithOffsetAsIfRotated(var4, var6, var7)
        val var10: Int = this.getYWithOffset(var5)
        val var11: Int = this.getZWithOffsetAsIfRotated(var4, var6, var7)

        if (var8.isInside(BlockPos(var9, var10, var11))) {
            var1.setBlock(BlockPos(var9, var10, var11), var2.getStateFromMeta(var3), 2)
        }
    }

    protected fun fillBlocksRotated(
        var1: World,
        var2: BoundingBox,
        var3: Int,
        var4: Int,
        var5: Int,
        var6: Int,
        var7: Int,
        var8: Int,
        var9: Block,
        var10: Int,
        var11: Int
    ) {
        for (var12: Int in var4..var7) {
            for (var13: Int in var3..var6) {
                for (var14: Int in var5..var8) {
                    this.placeBlockRotated(var1, var9, var10, var13, var12, var14, var11, var2)
                }
            }
        }
    }

    protected fun fillAirRotated(
        var1: World,
        var2: BoundingBox,
        var3: Int,
        var4: Int,
        var5: Int,
        var6: Int,
        var7: Int,
        var8: Int,
        var9: Int
    ) {
        this.fillBlocksRotated(var1, var2, var3, var4, var5, var6, var7, var8, Blocks.AIR, 0, var9)
    }

    protected fun getStairMeta(var1: Int): Int {
        when ((this.getCoordBaseMode()!!.getHorizontalIndex() + var1) % 4) {
            0 -> return 0

            1 -> return 2

            2 -> return 1

            3 -> return 3

            else -> return -1
        }
    }

    protected fun getLadderMeta(var1: Int): Int {
        when ((this.getCoordBaseMode()!!.getHorizontalIndex() + var1) % 4) {
            0 -> return 4

            1 -> return 2

            2 -> return 5

            3 -> return 3

            else -> return -1
        }
    }

    protected fun getLadderMeta(var1: Int, var2: Int): Int {
        return this.getLadderMeta(var1 + var2)
    }

    fun nullifySkyLightForBoundingBox(var1: World) {
        this.nullifySkyLight(
            var1,
            this.boundingBox.minX() - 1,
            this.boundingBox.minY() - 1,
            this.boundingBox.minZ() - 1,
            this.boundingBox.maxX() + 1,
            this.boundingBox.maxY() + 1,
            this.boundingBox.maxZ() + 1
        )
    }

    fun nullifySkyLightAtCurrentPosition(
        var1: World,
        var2: Int,
        var3: Int,
        var4: Int,
        var5: Int,
        var6: Int,
        var7: Int
    ) {
        this.nullifySkyLight(
            var1,
            this.getXWithOffset(var2, var4),
            this.getYWithOffset(var3),
            this.getZWithOffset(var2, var4),
            this.getXWithOffset(var5, var7),
            this.getYWithOffset(var6),
            this.getZWithOffset(var5, var7)
        )
    }

    fun nullifySkyLight(var1: World, var2: Int, var3: Int, var4: Int, var5: Int, var6: Int, var7: Int) {
        for (var8: Int in var2..var5) {
            for (var9: Int in var4..var7) {
                for (var10: Int in var3..var6) {
                    var1.setLightFor(EnumSkyBlock.SKY, BlockPos(var8, var10, var9), 0)
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun getComponentToAddBoundingBox(
            var0: Int,
            var1: Int,
            var2: Int,
            var3: Int,
            var4: Int,
            var5: Int,
            var6: Int,
            var7: Int,
            var8: Int,
            var9: Int
        ): BoundingBox {
            when (var9) {
                0 -> return BoundingBox(
                    var0 + var3,
                    var1 + var4,
                    var2 + var5,
                    var0 + var6 + var3,
                    var1 + var7 + var4,
                    var2 + var8 + var5
                )

                1 -> return BoundingBox(
                    var0 - var8 + var5,
                    var1 + var4,
                    var2 + var3,
                    var0 + var5,
                    var1 + var7 + var4,
                    var2 + var6 + var3
                )

                2 -> return BoundingBox(
                    var0 - var6 - var3,
                    var1 + var4,
                    var2 - var8 - var5,
                    var0 - var3,
                    var1 + var7 + var4,
                    var2 - var5
                )

                3 -> return BoundingBox(
                    var0 + var5,
                    var1 + var4,
                    var2 - var6,
                    var0 + var8 + var5,
                    var1 + var7 + var4,
                    var2 + var3
                )

                else -> return BoundingBox(
                    var0 + var3,
                    var1 + var4,
                    var2 + var5,
                    var0 + var6 + var3,
                    var1 + var7 + var4,
                    var2 + var8 + var5
                )
            }
        }
    }
}
