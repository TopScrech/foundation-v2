package micdoodle8.mods.galacticraft.core.entities.player

import com.mojang.authlib.GameProfile
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.MoverType
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.server.MinecraftServer
import net.minecraft.server.management.PlayerInteractionManager
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.server.level.ServerLevel as WorldServer

/**
 * This class provides various hooks which are missing from Forge or don't quite do what we need.
 *
 * Do not reference this or test 'instance of' this in your code:
 * if PlayerAPI is installed, GCEntityPlayerMP will not be used.
 */
open class GCEntityPlayerMP constructor(
    server: MinecraftServer,
    world: WorldServer,
    profile: GameProfile,
    interactionManager: PlayerInteractionManager
) : EntityPlayerMP(server, world, profile, interactionManager) {
    override fun updateRidden() {
        (GalacticraftCore.proxy)!!.player.updateRiddenPre(this)
        super.updateRidden()
        (GalacticraftCore.proxy)!!.player.updateRiddenPost(this)
    }

    override fun dismountRidingEntity() {
        if (!(GalacticraftCore.proxy)!!.player.dismountEntity(this, this.getRidingEntity())) {
            super.dismountRidingEntity()
        }
    }

    override fun move(type: MoverType, x: Double, y: Double, z: Double) {
        super.move(type, x, y, z)
        (GalacticraftCore.proxy)!!.player.move(this, type, x, y, z)
    }

    override fun wakeUpPlayer(immediately: Boolean, updateWorldFlag: Boolean, setSpawn: Boolean) {
        if ((GalacticraftCore.proxy)!!.player.wakeUpPlayer(this, immediately, updateWorldFlag, setSpawn)) {
            return
        }
        super.wakeUpPlayer(immediately, updateWorldFlag, setSpawn)
    }

    override fun attackEntityFrom(par1DamageSource: DamageSource, par2: Float): Boolean {
        var par2: Float = par2
        par2 = (GalacticraftCore.proxy)!!.player.attackEntityFrom(this, par1DamageSource, par2)
        return par2 != -1f && super.attackEntityFrom(par1DamageSource, par2)
    }

    override fun knockBack(p_70653_1_: Entity, p_70653_2_: Float, impulseX: Double, impulseZ: Double) {
        (GalacticraftCore.proxy)!!.player.knockBack(this, p_70653_1_, p_70653_2_, impulseX, impulseZ)
    }

    override fun isSpectator(): Boolean {
        return (GalacticraftCore.proxy)!!.player.isSpectator(this) || super.isSpectator()
    } /*@Override
    public void setInPortal()
    {
    	if (!(this.world.provider instanceof IGalacticraftWorldProvider))
    	{
    		super.setInPortal();
    	}
    } TODO Fix disable of portal */
}
