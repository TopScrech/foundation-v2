package asmodeuscore.core.astronomy.dimension.world

import asmodeuscore.core.astronomy.dimension.world.data.MeteoricRainSaveData
import asmodeuscore.core.prefab.entities.EntityAdvMeteor
import micdoodle8.mods.galacticraft.core.GCBlocks
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer

/** Called by the weather tick only for the dimension whose meteor storm is active */
object MeteoricRainSpawner {
    fun spawnMeteor(data: MeteoricRainSaveData, level: ServerLevel) {
        if (!data.isMeteoricStorm()) return
        for (player in level.players()) throwMeteors(level, player, 12, 3)
    }

    private fun throwMeteors(level: ServerLevel, player: ServerPlayer, frequency: Int, blockChance: Int) {
        val random = level.random
        if (random.nextInt(frequency) != 0) return
        val closest = level.getNearestPlayer(player, 100.0)
        if (closest != null && closest.id > player.id) return
        val x = random.nextInt(60) - 30
        val y = random.nextInt(20) + 200
        val z = random.nextInt(60) - 30
        val motionX = random.nextDouble()
        val motionZ = random.nextDouble()
        val block = if (blockChance > 0 && random.nextInt(100) <= blockChance) GCBlocks.fallenMeteor!!.defaultBlockState() else null
        val meteor = EntityAdvMeteor(
            level, player.x + x, player.y + y, player.z + z,
            motionX - 0.5, 0.0, motionZ - 0.5, random.nextInt(3)
        ).setSpawnBlock(block)
        level.addFreshEntity(meteor)
    }
}
