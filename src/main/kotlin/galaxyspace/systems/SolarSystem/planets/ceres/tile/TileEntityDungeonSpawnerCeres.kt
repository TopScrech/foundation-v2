package galaxyspace.systems.SolarSystem.planets.ceres.tile

import galaxyspace.systems.SolarSystem.planets.ceres.tile.TileEntityDungeonSpawnerCeres
import galaxyspace.systems.SolarSystem.planets.ceres.entities.EntityBossBlaze
import micdoodle8.mods.galacticraft.core.client.sounds.GCSounds
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSkeleton
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSpider
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedZombie
import micdoodle8.mods.galacticraft.core.tile.TileEntityDungeonSpawner
import net.minecraft.world.entity.Entity
import net.minecraft.entity.EntityLiving
import net.minecraft.sounds.SoundSource as SoundCategory
import java.util.ArrayList

open class TileEntityDungeonSpawnerCeres constructor() :
    TileEntityDungeonSpawner<EntityBossBlaze>(EntityBossBlaze::class.java) {
    override fun getDisabledCreatures(): MutableList<Class<out EntityLiving>?> {
        val list: MutableList<Class<out EntityLiving>?> = ArrayList<Class<out EntityLiving>?>()
        list.add(EntityEvolvedSkeleton::class.java)
        list.add(EntityEvolvedZombie::class.java)
        list.add(EntityEvolvedSpider::class.java)
        return list
    }

    override fun playSpawnSound(entity: Entity?) {
        this.world.playSound(
            null,
            (entity)!!.x,
            (entity)!!.y,
            (entity)!!.z,
            GCSounds.scaryScape,
            SoundCategory.AMBIENT,
            9.0f,
            1.4f
        )
    }
}
