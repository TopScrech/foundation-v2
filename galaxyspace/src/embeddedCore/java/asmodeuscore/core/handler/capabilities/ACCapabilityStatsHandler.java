package asmodeuscore.core.handler.capabilities;

import asmodeuscore.AsmodeusCore;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class ACCapabilityStatsHandler {
	@CapabilityInject(IStatsCapability.class)
	public static Capability<IStatsCapability> AC_STATS_CAPABILITY = null;

	public static final ResourceLocation AC_PLAYER_PROPERTIES = new ResourceLocation(AsmodeusCore.ASSET_PREFIX, "player_stats");
	public static final ResourceLocation AC_PLAYER_PROPERTIES_CLIENT = new ResourceLocation(AsmodeusCore.ASSET_PREFIX, "player_stats_client");

	public static void register() {
		CapabilityManager.INSTANCE.register(IStatsCapability.class, new Capability.IStorage<IStatsCapability>() {
			@Override
			public NBTBase writeNBT(Capability<IStatsCapability> capability, IStatsCapability instance, EnumFacing side) {
				return null;
			}

			@Override
			public void readNBT(Capability<IStatsCapability> capability, IStatsCapability instance, EnumFacing side, NBTBase nbt) {
			}
		}, ACStatsCapability::new);
	}
}