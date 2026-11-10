package asmodeuscore.core.handler.capabilities;

import java.lang.ref.WeakReference;

import asmodeuscore.core.astronomy.SpaceData.Engine_Type;
import micdoodle8.mods.galacticraft.core.util.GCLog;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

public class ACStatsCapability implements IStatsCapability {
	public WeakReference<EntityPlayerMP> player;
	public int buildFlags = 0;
	
	public Engine_Type rocket_engine = Engine_Type.FUEL_ENGINE;
	private boolean isSecondPassenger;

	public static IStatsCapability get(Entity entity)
    {
        return entity.getCapability(ACCapabilityStatsHandler.AC_STATS_CAPABILITY, null);
    }
	
	@Override
	public void saveNBTData(NBTTagCompound nbt) {
		nbt.setInteger("rocket_engine", getEngineType().getID());		
		nbt.setBoolean("SecondPassenger", isSecondPassenger);
	}

	@Override
	public void loadNBTData(NBTTagCompound nbt) {
		try {
			
			this.rocket_engine = Engine_Type.byID(nbt.getInteger("rocket_engine"));
			this.isSecondPassenger = nbt.getBoolean("SecondPassenger");
			
		} catch (Exception e) {
			GCLog.severe("Found error in saved AsmodeusCore player data for " + player.get().getGameProfile().getName() + " - this should fix itself next relog.");
			e.printStackTrace();
		}

		GCLog.debug("Finished loading AsmodeusCore player data for " + player.get().getGameProfile().getName() + " : " + this.buildFlags);
	}

	@Override
	public void copyFrom(IStatsCapability oldData, boolean keepInv) {
		//this.radiationLevel = oldData.getRadiationLevel();
		this.rocket_engine = oldData.getEngineType();
		this.isSecondPassenger = oldData.isSecondPassenger();
	}

	@Override
	public WeakReference<EntityPlayerMP> getPlayer() {
		return player;
	}

	@Override
	public void setPlayer(WeakReference<EntityPlayerMP> player) {
		this.player = player;
	}

	@Override
	public void setEngineType(Engine_Type type) {
		this.rocket_engine = type;
	}

	@Override
	public Engine_Type getEngineType() {
		return this.rocket_engine;
	}

	@Override
	public boolean isSecondPassenger() {
		return isSecondPassenger;
	}

	@Override
	public void setSecondPassenger(boolean b) {
		this.isSecondPassenger = b;
	}
}
