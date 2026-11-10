package galaxyspace.systems.SolarSystem.satellites.jupiter.dimension

import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.ITeleportType
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import java.util.Random

open class TeleportTypeJupiterSS : ITeleportType {
    override fun useParachute(): Boolean {
        return false
    }

    override fun getEntitySpawnLocation(world: WorldServer?, player: Entity?): Vector3 {
        return Vector3(0.5, 65.0, 0.5)
    }

    override fun getPlayerSpawnLocation(world: WorldServer?, player: EntityPlayerMP?): Vector3 {
        return this.getEntitySpawnLocation(world, player)
    }

    override fun getParaChestSpawnLocation(world: WorldServer?, player: EntityPlayerMP?, rand: Random?): Vector3 {
        return Vector3(-8.5, 90.0, -1.5)
    }

    override fun onSpaceDimensionChanged(newWorld: World?, player: EntityPlayerMP?, ridingAutoRocket: Boolean) {
        if (ConfigManagerCore.spaceStationsRequirePermission && !(newWorld)!!.isRemote) {
            (player)!!.sendMessage(
                TextComponentString(
                    EnumColor.YELLOW.toString() + GCCoreUtil.translate("gui.spacestation.type_command") + " " + EnumColor.AQUA + "/ssinvite " + GCCoreUtil.translate(
                        "gui.spacestation.playername"
                    ) + " " + EnumColor.YELLOW + GCCoreUtil.translate("gui.spacestation.to_allow_entry")
                )
            )
        }
    }

    override fun setupAdventureSpawn(player: EntityPlayerMP?) {
    }
}
