package asmodeuscore.core.command

import asmodeuscore.api.dimension.IProviderWeather
import asmodeuscore.core.astronomy.dimension.world.data.FrozenStormSaveData
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import net.minecraft.command.CommandBase
import net.minecraft.command.CommandException
import net.minecraft.command.ICommandSender
import net.minecraft.server.MinecraftServer
import net.minecraft.world.World

open class CommandFrozenStormToggle : CommandBase() {
    override fun getName(): String {
        return "togglefrozenstorm"
    }

    override fun getUsage(sender: ICommandSender): String {
        return "/" + this.getName()
    }

    override fun getRequiredPermissionLevel(): Int {
        return 2
    }

    @Throws(CommandException::class)
    override fun execute(server: MinecraftServer, sender: ICommandSender, args: Array<String?>) {
        this.toggleRainfall(server, sender.getEntityWorld())
        notifyCommandListener(sender, this, "commands.downfall.success", *arrayOfNulls<Any>(0))
    }

    protected open fun toggleRainfall(server: MinecraftServer?, world: World) {
        if (world.provider is IProviderWeather && world.provider is IGalacticraftWorldProvider) {
            val gcprovider: IGalacticraftWorldProvider = world.provider as IGalacticraftWorldProvider
            val name: String? = (gcprovider.getCelestialBody())!!.getName()
            val msd: FrozenStormSaveData = FrozenStormSaveData.get(world, name)

            if (msd.isFrozenStorm()) msd.tickStorm = 1
            else msd.tickClearWeather = 1
        }
    }
}
