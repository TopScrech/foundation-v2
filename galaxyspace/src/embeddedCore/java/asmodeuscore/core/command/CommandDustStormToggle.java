package asmodeuscore.core.command;

import asmodeuscore.api.dimension.IProviderWeather;
import asmodeuscore.core.astronomy.dimension.world.data.DustStormSaveData;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class CommandDustStormToggle extends CommandBase {

	@Override
	public String getName() {
		return "toggleduststorm";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/" + this.getName();
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		this.toggleRainfall(server, sender.getEntityWorld());
		notifyCommandListener(sender, this, "commands.downfall.success", new Object[0]);
	}

	protected void toggleRainfall(MinecraftServer server, World world) {
		if (world.provider instanceof IProviderWeather && world.provider instanceof IGalacticraftWorldProvider) {
			IGalacticraftWorldProvider gcprovider = (IGalacticraftWorldProvider) world.provider;
			String name = gcprovider.getCelestialBody().getName();
			DustStormSaveData msd = DustStormSaveData.get(world, name);

			if (msd.isDustStorm())
				msd.tickDustStorm = 1;
			else
				msd.tickClearWeather = 1;

		}
	}

}
