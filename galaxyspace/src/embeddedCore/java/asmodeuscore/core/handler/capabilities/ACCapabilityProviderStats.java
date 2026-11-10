package asmodeuscore.core.handler.capabilities;

import java.lang.ref.WeakReference;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class ACCapabilityProviderStats implements ICapabilitySerializable<NBTTagCompound> {
	private EntityPlayerMP owner;
	private IStatsCapability statsCapability;

	public ACCapabilityProviderStats(EntityPlayerMP owner) {
		this.owner = owner;
		this.statsCapability = ACCapabilityStatsHandler.AC_STATS_CAPABILITY.getDefaultInstance();
		this.statsCapability.setPlayer(new WeakReference<>(this.owner));
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == ACCapabilityStatsHandler.AC_STATS_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (ACCapabilityStatsHandler.AC_STATS_CAPABILITY != null && capability == ACCapabilityStatsHandler.AC_STATS_CAPABILITY) {
			return ACCapabilityStatsHandler.AC_STATS_CAPABILITY.cast(statsCapability);
		}

		return null;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		statsCapability.saveNBTData(nbt);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		statsCapability.loadNBTData(nbt);
	}
}
