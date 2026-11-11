package micdoodle8.mods.galacticraft.core.entities.player

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.MoverType
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.damagesource.DamageSource

interface IPlayerServer {
    fun updateRiddenPre(player: EntityPlayerMP?)

    fun updateRiddenPost(player: EntityPlayerMP?)

    fun dismountEntity(player: EntityPlayerMP?, par1Entity: Entity?): Boolean

    fun move(player: EntityPlayerMP?, type: MoverType?, x: Double, y: Double, z: Double)

    fun wakeUpPlayer(
        player: EntityPlayerMP?,
        immediately: Boolean,
        updateWorldFlag: Boolean,
        setSpawn: Boolean
    ): Boolean

    fun attackEntityFrom(player: EntityPlayerMP?, par1DamageSource: DamageSource?, par2: Float): Float

    fun knockBack(player: EntityPlayerMP?, p_70653_1_: Entity?, p_70653_2_: Float, impulseX: Double, impulseZ: Double)

    fun isSpectator(player: EntityPlayerMP?): Boolean

    fun setNoClip(player: EntityPlayerMP?, noClip: Boolean)
}
