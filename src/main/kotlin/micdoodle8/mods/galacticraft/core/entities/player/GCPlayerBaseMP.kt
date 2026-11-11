package micdoodle8.mods.galacticraft.core.entities.player

import api.player.server.ServerPlayerAPI
import api.player.server.ServerPlayerBase
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.MoverType
import net.minecraft.world.damagesource.DamageSource

open class GCPlayerBaseMP constructor(playerAPI: ServerPlayerAPI) : ServerPlayerBase(playerAPI) {
    private fun getClientHandler(): IPlayerServer? {
        return (GalacticraftCore.proxy)!!.player
    }


    //    @Override
    //    public void updateRidden()
    //    {
    //        this.getClientHandler().updateRiddenPre(this);
    //        super.updateRidden();
    //        this.getClientHandler().updateRiddenPost(this);
    //    }
    //
    //    @Override
    //    public void mountEntity(Entity par1Entity)
    //    {
    //        if (!this.getClientHandler().mountEntity(this, par1Entity))
    //        {
    //            super.mountEntity(par1Entity);
    //        }
    //    }
    override fun moveEntity(moverType: MoverType?, x: Double, y: Double, z: Double) {
        super.moveEntity(moverType, x, y, z)
        this.getClientHandler()!!.move(this.player, moverType, x, y, z)
    }

    override fun wakeUpPlayer(par1: Boolean, par2: Boolean, par3: Boolean) {
        if (!this.getClientHandler()!!.wakeUpPlayer(this.player, par1, par2, par3)) {
            super.wakeUpPlayer(par1, par2, par3)
        }
    }

    override fun attackEntityFrom(par1DamageSource: DamageSource?, par2: Float): Boolean {
        var par2: Float = par2
        par2 = this.getClientHandler()!!.attackEntityFrom(this.player, par1DamageSource, par2)

        if (par2 == -1f) {
            return false
        }

        return super.attackEntityFrom(par1DamageSource, par2)
    }

    override fun knockBack(p_70653_1_: Entity?, p_70653_2_: Float, impulseX: Double, impulseZ: Double) {
        this.getClientHandler()!!.knockBack(this.player, p_70653_1_, p_70653_2_, impulseX, impulseZ)
    }
}
