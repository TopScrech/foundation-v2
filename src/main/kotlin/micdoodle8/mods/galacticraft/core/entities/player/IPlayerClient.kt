package micdoodle8.mods.galacticraft.core.entities.player

import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.MoverType

interface IPlayerClient {
    fun move(player: EntityPlayerSP?, type: MoverType?, x: Double, y: Double, z: Double)

    fun onUpdate(player: EntityPlayerSP?)

    fun onLivingUpdatePre(player: EntityPlayerSP?)

    fun onLivingUpdatePost(player: EntityPlayerSP?)

    fun getBedOrientationInDegrees(player: EntityPlayerSP?, vanillaDegrees: Float): Float

    fun isEntityInsideOpaqueBlock(player: EntityPlayerSP?, vanillaInside: Boolean): Boolean

    fun wakeUpPlayer(
        player: EntityPlayerSP?,
        immediately: Boolean,
        updateWorldFlag: Boolean,
        setSpawn: Boolean
    ): Boolean

    fun onBuild(i: Int, player: EntityPlayerSP?)
}
