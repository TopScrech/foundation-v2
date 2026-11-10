package asmodeuscore.core.astronomy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import asmodeuscore.api.dimension.IAdvancedSpace.ClassBody;
import asmodeuscore.api.dimension.IAdvancedSpace.TempBody;
import asmodeuscore.api.dimension.IAdvancedSpace.TypeBody;
import asmodeuscore.api.space.IExBody;
import asmodeuscore.core.astronomy.dimension.world.gen.ACBiome;
import asmodeuscore.core.astronomy.gui.screen.GuiCustomTeleporting;
import asmodeuscore.core.astronomy.gui.screen.GuiCustomTeleporting.TeleportData;
import asmodeuscore.core.prefab.celestialbody.ExMoon;
import asmodeuscore.core.prefab.celestialbody.ExPlanet;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.IChildBody;
import micdoodle8.mods.galacticraft.api.galaxies.Moon;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.Satellite;
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem;
import micdoodle8.mods.galacticraft.api.galaxies.Star;
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.AtmosphereInfo;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedCreeper;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedEnderman;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSkeleton;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSpider;
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedZombie;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.util.EnumHelper;

public class BodiesRegistry {
	
	public enum Galaxies
	{
		/** Our Galaxy. Milky Way  **/
		MILKYWAY("milky_way", null),
		/** Andromeda  **/
		ANDROMEDA("andromeda", null);
		///** Small Magellanic Cloud  **/
		//SMC("smc"),
		///** Large Magellanic Cloud  **/
		//LMC("lmc");
		
		private final String name;
		private final ResourceLocation image;
		Galaxies(String name, ResourceLocation image)
		{			
			this.name = name;
			this.image = image;
		}
		
		public String getName()
		{
			return this.name;
		}
		
		public ResourceLocation getImage()
		{
			return this.image;
		}
	}
	
	public static Galaxies getGalaxy(String name)
	{
		
		for(Galaxies gal : Galaxies.values())
			if(gal.getName().equals(name))
				return gal;
		
		return null;
	}
	
	public static Galaxies getGalaxyFromBody(CelestialBody body)
	{
		if(body instanceof Planet) {
			return getGalaxy(((Planet) body).getParentSolarSystem().getUnlocalizedParentGalaxyName().replace("galaxy.", ""));
		}
		
		if(body instanceof IChildBody) {
			return getGalaxy(((IChildBody) body).getParentPlanet().getParentSolarSystem().getUnlocalizedParentGalaxyName().replace("galaxy.", ""));
		}
		
		return null;
	}

	//---------------------
	
	private static Map<CelestialBody, BodiesData> data = new HashMap();
	private static int max_tier = 3;
	public static int getDefaultDergrees = 30;
	private static List<Map<String, ? extends CelestialBody>> bodiesToRegister = new ArrayList();
	
	public static void setMaxTier(int tier)	{
		if(getMaxTier() < tier)
			max_tier = tier;
	}
	
	public static int getMaxTier()	{
		return max_tier;
	}

	
	/**
	 * 
	 * @param celestialbody
	 * @return if there is data - BodiesData, else - default preset: 
	 * new BodiesData(TypeBody)
	 */
	public static BodiesData getData(CelestialBody body)	{
		if(body instanceof Planet)
			return data.get(body) != null ? data.get(body) : new BodiesData(TypeBody.PLANET);
	
		if(body instanceof Moon)
			return data.get(body) != null ? data.get(body) : new BodiesData(TypeBody.MOON);
		
		return data.get(body);
	}
	
	public static List<Map<String, ? extends CelestialBody>> getBodies(){
		return bodiesToRegister;
	}
	
	public static void registerCelestialBody(Map<String, ? extends CelestialBody> body) {
		bodiesToRegister.add(body);
	}
	
	public static Galaxies registerGalaxy(String galaxy, ResourceLocation image)
	{
		return EnumHelper.addEnum(Galaxies.class, galaxy.toUpperCase(), new Class<?>[]{String.class, ResourceLocation.class}, galaxy.toLowerCase(), image);
	}
	
	public static ClassBody registerClass(String classname)
	{
		return EnumHelper.addEnum(ClassBody.class, classname.toUpperCase(), new Class<?>[] {});
	}
	
	public static TypeBody registerType(String typename)
	{
		return EnumHelper.addEnum(TypeBody.class, typename.toUpperCase(), new Class<?>[] {});
	}
	
	public static void addTeleportData(int dimID, ResourceLocation image)
	{
		String[] possibleStrings = new String[] { "Taking one small step", "Taking one giant leap", "Prepare for entry!" };
		addTeleportData(dimID, image, possibleStrings);
	}
	
	public static void addTeleportData(int dimID, ResourceLocation image, String[] text)
	{	
		GuiCustomTeleporting.getList().add(new TeleportData(image, text, dimID));
	}
	
	public static SolarSystem registerSolarSystem(String prefix, String name, Galaxies galaxy, Vector3 pos, String starname, float size) {
		return registerSolarSystem(prefix, name, galaxy, pos, starname, size, null);
	}
	
	public static SolarSystem registerSolarSystem(String prefix, String name, Galaxies galaxy, Vector3 pos, String starname, float size, @Nullable ResourceLocation icon) {
		SolarSystem body = new SolarSystem(name, galaxy.getName());
		body.setMapPosition(pos);
		Star main = new Star(starname);
		main.setParentSolarSystem(body);
		main.setTierRequired(-1);
		main.setRelativeSize(size);
		main.setBodyIcon(icon != null ? icon : new ResourceLocation(prefix, "textures/gui/celestialbodies/" + name + "/" + starname + ".png"));
		
		body.setMainStar(main);
		
		return body;
	}
	
	public static ExPlanet registerExPlanet(SolarSystem system, String name, String prefix, float distancefromcenter) {
		ExPlanet body = new ExPlanet(name).setParentSolarSystem(system);
		String path = system.getUnlocalizedName().replace("solarsystem.", "");
		body.setRingColorRGB(0.0F, 0.4F, 0.9F);
		body.setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(distancefromcenter, distancefromcenter));
		body.setBodyIcon(new ResourceLocation(prefix, "textures/gui/celestialbodies/" + path + "/" + name + ".png"));
		
		return body;
	}
	
	public static ExMoon registerExMoon(Planet parent, String name, String prefix, float distancefromcenter) {
		ExMoon body = new ExMoon(name).setParentPlanet(parent);
		String path = parent.getParentSolarSystem().getUnlocalizedName().replace("solarsystem.", "");
		body.setRingColorRGB(0.0F, 0.4F, 0.9F);
		body.setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(distancefromcenter, distancefromcenter));
		body.setBodyIcon(new ResourceLocation(prefix, "textures/gui/celestialbodies/" + path + "/moons/" + name + ".png"));
		
		return body;
	}
	
	public static void setOrbitData(CelestialBody body, float phase, float size, float relativetime) {
		setOrbitData(body, phase, size, relativetime, 0F, 0F, 0F, 0F);		
    }
	
	public static void setOrbitData(CelestialBody body, float phase, float size, float relativetime, float eccentricityX, float eccentricityY, float orbitOffsetX, float orbitOffsetY) {
			body.setPhaseShift(phase);		
			body.setRelativeSize(size);
			body.setRelativeOrbitTime(relativetime);
		if(body instanceof ExPlanet) {
			((ExPlanet)body).setOrbitEccentricity(eccentricityX, eccentricityY);
			((ExPlanet)body).setOrbitOffset(orbitOffsetX, orbitOffsetY);
		}		
		if(body instanceof ExMoon) {
			((ExMoon)body).setOrbitEccentricity(eccentricityX, eccentricityY);
			((ExMoon)body).setOrbitOffset(orbitOffsetX, orbitOffsetY);
		}
    }
	
	@Deprecated
	public static void setPlanetData(CelestialBody body, int atm_pressure, long day_length, float gravity, boolean isSolarRadiation)
	{
		setPlanetData(body, (float)atm_pressure, day_length, gravity, isSolarRadiation);
	}
	
	public static void setPlanetData(CelestialBody body, float water_pressure, float atm_pressure, long day_length, float gravity, boolean isSolarRadiation)
	{
		if(body instanceof ExPlanet) 
			((ExPlanet)body).setWaterPressure(water_pressure);
		if(body instanceof ExMoon)
			((ExMoon)body).setWaterPressure(water_pressure);
		
		setPlanetData(body, atm_pressure, day_length, gravity, isSolarRadiation);
	}

	public static void setPlanetData(CelestialBody body, float atm_pressure, long day_length, float gravity, boolean isSolarRadiation)
	{
		if(body instanceof ExPlanet) {
			((ExPlanet)body).setGravity(gravity);
			((ExPlanet)body).setAtmosphericPressure(atm_pressure);
			((ExPlanet)body).setDayLength(day_length);
			((ExPlanet)body).setSolarRadiation(isSolarRadiation ? 1.0F : 0.0F);
		}
		
		if(body instanceof ExMoon) {
			((ExMoon)body).setGravity(gravity);
			((ExMoon)body).setAtmosphericPressure(atm_pressure);
			((ExMoon)body).setDayLength(day_length);
			((ExMoon)body).setSolarRadiation(isSolarRadiation ? 1.0F : 0.0F);
		}
	}
	
	
	
	public static void setAtmosphere(CelestialBody body, boolean breathable, boolean precipitation, boolean corrosive, float temperature, float windLevel, float density) {
		body.setAtmosphere(new AtmosphereInfo(breathable, precipitation, corrosive, temperature, windLevel, density));
	}
	
	public static void setProviderData(CelestialBody body, Class<? extends WorldProvider> provider, int dimID, int tier, Biome... biome) {
		body.setTierRequired(tier);
		body.setDimensionInfo(dimID, provider);
		if(biome != null) body.setBiomeInfo(biome);
		body.addMobInfo(new Biome.SpawnListEntry(EntityEvolvedZombie.class, 8, 2, 3));
		body.addMobInfo(new Biome.SpawnListEntry(EntityEvolvedSpider.class, 8, 2, 3));
		body.addMobInfo(new Biome.SpawnListEntry(EntityEvolvedSkeleton.class, 8, 2, 3));
		body.addMobInfo(new Biome.SpawnListEntry(EntityEvolvedCreeper.class, 8, 2, 3));
		body.addMobInfo(new Biome.SpawnListEntry(EntityEvolvedEnderman.class, 10, 1, 4));
	}

	public static void setClassBody(CelestialBody body, ClassBody classbody)
	{
		if(body instanceof ExPlanet)
			((ExPlanet)body).setClassPlanet(classbody);
		if(body instanceof ExMoon)
			((ExMoon)body).setClassPlanet(classbody);
	}
	
	public static Planet registerPlanet(String name, String prefix, Class<? extends WorldProvider> provider, int dimID, int tier, float phase, float size, float distancefromcenter, float relativetime, Biome... biome) {
		return registerPlanet(GalacticraftCore.solarSystemSol, name, prefix, provider, dimID, tier, phase, size, distancefromcenter, relativetime, biome);
	}
	
	public static Planet registerPlanet(SolarSystem system, String name, String prefix, Class<? extends WorldProvider> provider, int dimID, int tier, float phase, float size, float distancefromcenter, float relativetime, Biome... biome) {
		Planet body = new Planet(name).setParentSolarSystem(system);
		String path = system.getUnlocalizedName().replace("solarsystem.", "");
		body.setRingColorRGB(0.0F, 0.4F, 0.9F);
		body.setPhaseShift(phase);		
		body.setRelativeSize(size);    	
		body.setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(distancefromcenter, distancefromcenter));
		body.setRelativeOrbitTime(relativetime);
		body.setBodyIcon(new ResourceLocation(prefix, "textures/gui/celestialbodies/" + path + "/" + name + ".png"));
		//body.setAtmosphere(new AtmosphereInfo(breathable, precipitation, corrosive, relativeTemperature, windLevel, density))
		
		if(provider != null) 
		{		
			body.setTierRequired(tier);
			body.setDimensionInfo(dimID, provider);
			if(biome != null) body.setBiomeInfo(biome);
			body.addMobInfo(new Biome.SpawnListEntry(EntityEvolvedZombie.class, 8, 2, 3));
			body.addMobInfo(new Biome.SpawnListEntry(EntityEvolvedSpider.class, 8, 2, 3));
			body.addMobInfo(new Biome.SpawnListEntry(EntityEvolvedSkeleton.class, 8, 2, 3));
			body.addMobInfo(new Biome.SpawnListEntry(EntityEvolvedCreeper.class, 8, 2, 3));
			body.addMobInfo(new Biome.SpawnListEntry(EntityEvolvedEnderman.class, 10, 1, 4));
		}
		
		return body;
	}
	
	public static Moon registerMoon(Planet parent, String name, String prefix, Class<? extends WorldProvider> provider, int dimID, int tier, float phase, float size, float distancefromcenter, float relativetime, Biome... biome) {
		Moon body = new Moon(name).setParentPlanet(parent);
		String path = parent.getParentSolarSystem().getUnlocalizedName().replace("solarsystem.", "");
		
		body.setRingColorRGB(0.0F, 0.4F, 0.9F);
		body.setPhaseShift(phase);		
		body.setRelativeSize(size);    	
		body.setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(distancefromcenter, distancefromcenter));
		body.setRelativeOrbitTime(relativetime);
		body.setBodyIcon(new ResourceLocation(prefix, "textures/gui/celestialbodies/" + path + "/moons/" + name + ".png"));

		if(provider != null) 
		{	
			body.setTierRequired(tier);
			body.setDimensionInfo(dimID, provider);
			if(biome != null) body.setBiomeInfo(biome);
			body.addMobInfo(new Biome.SpawnListEntry(EntityEvolvedZombie.class, 8, 2, 3));
			body.addMobInfo(new Biome.SpawnListEntry(EntityEvolvedSpider.class, 8, 2, 3));
			body.addMobInfo(new Biome.SpawnListEntry(EntityEvolvedSkeleton.class, 8, 2, 3));
			body.addMobInfo(new Biome.SpawnListEntry(EntityEvolvedCreeper.class, 8, 2, 3));
			body.addMobInfo(new Biome.SpawnListEntry(EntityEvolvedEnderman.class, 10, 1, 4));
		}
		
		return body;
	}
	
	public static Satellite registerSatellite(Planet parent, Class<? extends WorldProvider> provider, int dimID, int dimIDStatic) {
		Satellite body = new Satellite("spacestation." + parent.getUnlocalizedName().replace("planet.", ""));
		body.setParentBody(parent);
		body.setRelativeDistanceFromCenter(new CelestialBody.ScalableDistance(5.5F, 5.5F));
		body.setRingColorRGB(0.0F, 0.4F, 0.9F);
		body.setBodyIcon(new ResourceLocation("galacticraftcore:textures/gui/celestialbodies/space_station.png"));
		
		if(provider != null) 
		{
			if(parent.getReachable())
				body.setTierRequired(parent.getTierRequirement());
			else
				body.setTierRequired(max_tier);
			
			body.setDimensionInfo(dimID, dimIDStatic, provider);
			body.setBiomeInfo(ACBiome.ACSpace);
		}
		return body;
	}
	
	/**
	 * 
	 * @param body - Celestial body
	 * @param bodydata - data with additions info.
	 */
	public static void registerBodyData(CelestialBody body, BodiesData bodydata) {	
		data.put(body, bodydata);
		//System.out.println("Successful register data: " + body.getName() + " | " + bodydata.toString());
	}
	
	/**
	 * 
	 * @param Si - real gravity value.
	 * @return Transformed value for Galacticraft gravity system.
	 */
	public static float calculateGravity(float Si) {
		float i = (9.81F - Si) * (0.085F / 9.81F);
		float k = Math.round(i * 1000);
		return k / 1000;
	}
	
	public static String getLocale(Object temp)	{
		return GCCoreUtil.translate("gui.info." + temp.toString().toLowerCase() + ".name");		
	}
	
	public static String classPlanet(CelestialBody body, WorldProvider dim)	{
		BodiesData data = BodiesRegistry.getData(body);
		String type = "";
		
		if(body instanceof Star || data.getType() == TypeBody.STAR) {

			if(data != null && data.getStarType() != null) {
				if(data.getStarColor() != null)
					return BodiesRegistry.getLocale(data.getStarColor()) + " " + BodiesRegistry.getLocale(data.getStarType().name().toLowerCase());
				
				return BodiesRegistry.getLocale(data.getStarType().name().toLowerCase());
			}
		} else if(body.getReachable()) {
			
			if (body == GalacticraftCore.planetOverworld)
				return BodiesRegistry.getLocale(TempBody.COMFORT) + " " + BodiesRegistry.getLocale(ClassBody.TERRA);
			
			float temp = body.atmosphere.thermalLevel();
			boolean breath = body.atmosphere.isBreathable();
			TempBody tempbody = TempBody.COMFORT;
			
			// 2.0 --- +oo
			if (temp >= 2.0F)
				tempbody = TempBody.HOT;
			// 1.0 --- 1.9
			if (temp >= 1.0F && temp < 2.0)
				tempbody = TempBody.WARM;
			// -0.9 --- 0.9 T
			if (temp > -1.0F && temp < 1.0F)
				tempbody = TempBody.COMFORT;
			// -1.9 --- -1.0F
			if (temp > -2.0 && temp <= -1.0F)
				tempbody = TempBody.COOL;
			// -oo --- -2.0F
			if (temp <= -2.0F)
				tempbody = TempBody.COLD;
			
			type = getLocale(tempbody);

			if(body instanceof IExBody)
			{
				IExBody planet = (IExBody) body;
				if(planet.getClassPlanet() != null)
					return type + " " + BodiesRegistry.getLocale(planet.getClassPlanet());
			}
			if (dim instanceof WorldProviderSpace && ((WorldProviderSpace) dim).getDayLength() == 0.0)
				return BodiesRegistry.getLocale(ClassBody.ASTEROID);
			
			if (breath)
				return type + " " + BodiesRegistry.getLocale(ClassBody.TERRA);

			if (!breath) {
				if(body.atmosphere.hasNoGases())
					return type + " " + BodiesRegistry.getLocale(ClassBody.SELENA);			
		
			/*	if (dim instanceof IGalacticraftWorldProvider && ((IGalacticraftWorldProvider) dim).getCelestialBody() instanceof IExBody && ((IExBody) ((IGalacticraftWorldProvider) dim).getCelestialBody()).getClassPlanet() == ClassBody.TITAN) {
					if (temp > 2.5F)
						type = BodiesRegistry.getLocale(TempBody.HOT);
					else if (temp < -2.5F)
						type = BodiesRegistry.getLocale(TempBody.ICY);
					else
						type = BodiesRegistry.getLocale(TempBody.COMFORT);
					return type + " " + BodiesRegistry.getLocale(ClassBody.TITAN);
				}*/
				return type + " " + BodiesRegistry.getLocale(ClassBody.DESERT);
			}
			
			if (temp < -3.0F)
				return BodiesRegistry.getLocale(ClassBody.ICEWORLD);
			
		}
		
		if (data != null && data.getPlanetClass() != null) 
			return BodiesRegistry.getLocale(data.getPlanetClass().name().toLowerCase());
		

		return GCCoreUtil.translate("gui.message.unknown");
	}
	
	public static float getDistanceFromCenter(World world) {
		if(world.provider instanceof IGalacticraftWorldProvider)
		{
			CelestialBody current_body = ((IGalacticraftWorldProvider)world.provider).getCelestialBody();
			float distance = 0.0F;
			if(current_body instanceof Planet) {
				distance = current_body.getRelativeDistanceFromCenter().unScaledDistance;
			}
			if(current_body instanceof IChildBody) {
				distance = ((IChildBody)current_body).getParentPlanet().getRelativeDistanceFromCenter().unScaledDistance;
			}
			return distance;
		}
		return 0F;
	}
}
