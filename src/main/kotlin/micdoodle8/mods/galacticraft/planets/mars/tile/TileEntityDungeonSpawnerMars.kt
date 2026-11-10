package micdoodle8.mods.galacticraft.planets.mars.tile

import micdoodle8.mods.galacticraft.core.client.sounds.GCSounds
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSkeleton
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSpider
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedZombie
import micdoodle8.mods.galacticraft.core.tile.TileEntityDungeonSpawner
import micdoodle8.mods.galacticraft.planets.mars.entities.EntityCreeperBoss
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLiving
import net.minecraft.util.SoundCategory
import java.util.ArrayList

open class TileEntityDungeonSpawnerMars constructor() :
    TileEntityDungeonSpawner<EntityCreeperBoss>(EntityCreeperBoss::class.java) {
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
            (entity)!!.posX,
            (entity)!!.posY,
            (entity)!!.posZ,
            GCSounds.scaryScape,
            SoundCategory.AMBIENT,
            9.0f,
            1.4f
        )
    }
}
