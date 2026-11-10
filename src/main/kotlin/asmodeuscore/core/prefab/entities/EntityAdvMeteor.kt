package asmodeuscore.core.prefab.entities

import asmodeuscore.core.prefab.entities.EntityAdvMeteor
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.entities.EntityMeteor
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.World

open class EntityAdvMeteor : EntityMeteor {
    private var blockSpawn: IBlockState? = (GCBlocks.fallenMeteor)!!.getDefaultState()

    constructor(world: World?) : super((world)!!)

    constructor(
        world: World?,
        x: Double,
        y: Double,
        z: Double,
        motX: Double,
        motY: Double,
        motZ: Double,
        size: Int
    ) : super((world)!!, x, y, z, motX, motY, motZ, size)

    open fun setSpawnBlock(block: IBlockState?): EntityAdvMeteor {
        this.blockSpawn = block
        return this
    }

    override fun onImpact(movingObjPos: RayTraceResult?) {
        if (!this.world.isRemote) {
            this.world.newExplosion(this, this.posX, this.posY, this.posZ, (this.size / 2 + 2).toFloat(), false, true)

            if (movingObjPos != null) {
                var pos: BlockPos = movingObjPos.getBlockPos()
                if (pos == null) {
                    if (movingObjPos.entityHit != null) {
                        pos = this.world.getTopSolidOrLiquidBlock(movingObjPos.entityHit.getPosition())
                    } else {
                        pos = this.world.getTopSolidOrLiquidBlock(this.getPosition())
                    }
                }
                val above: BlockPos = pos.up()
                if (blockSpawn != null && this.world.isAirBlock(above)) {
                    this.world.setBlockState(above, blockSpawn, 3)
                }

                if (movingObjPos.entityHit != null) {
                    movingObjPos.entityHit.attackEntityFrom(
                        causeMeteorDamage(this, this.shootingEntity),
                        if (ConfigManagerCore.hardMode) 12f else 6f
                    )
                }
            }
        }

        this.setDead()
    }
}
