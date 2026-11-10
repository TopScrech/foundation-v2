package asmodeuscore.core.utils

import asmodeuscore.core.utils.UtilsGC
import com.mjr.planetprogression.api.research.ResearchHooksSP
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.api.world.IOrbitDimension
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.dimension.SpaceStationWorldData
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.ResourceLocation
import net.minecraft.world.WorldProvider
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.common.Optional
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.apache.commons.lang3.ArrayUtils
import java.util.ArrayList
import java.util.HashMap
import kotlin.math.cos

object UtilsGC {
    @JvmField
    var missingIcon: TextureAtlasSprite? = null
    private var texMap: TextureMap? = null

    @JvmStatic
    fun initFluidTextures(map: TextureMap) {
        missingIcon = map.getMissingSprite()

        texMap = map
    }

    @JvmStatic
    fun getBlockTexture(block: Block?): TextureAtlasSprite? {
        return getBlockTexture(block, "")
    }

    @JvmStatic
    fun getBlockTexture(block: Block?, addpath: String?): TextureAtlasSprite? {
        if (block == null) {
            return missingIcon
        }

        val spriteLocation: ResourceLocation?
        spriteLocation = block.getRegistryName()

        val sprite: TextureAtlasSprite =
            texMap!!.getAtlasSprite(spriteLocation!!.getNamespace() + ":blocks/" + spriteLocation.getPath() + addpath)

        return if (sprite != null) sprite else missingIcon
    }

    @JvmStatic
    fun getFluidTexture(fluid: Fluid?, type: FluidType?): TextureAtlasSprite? {
        if (fluid == null || type == null) {
            return missingIcon
        }

        val spriteLocation: ResourceLocation
        if (type == FluidType.STILL) {
            spriteLocation = fluid.getStill()
        } else {
            spriteLocation = fluid.getFlowing()
        }

        val sprite: TextureAtlasSprite? = texMap!!.getTextureExtry(spriteLocation.toString())

        return if (sprite != null) sprite else missingIcon
    }

    @JvmStatic
    fun drawFluid(xPos: Int, yPos: Int, scale: Int, fluid: FluidStack?) {
        var scale: Int = scale
        if (fluid == null) {
            return
        }

        val guiWidth: Int = 0 //(width - xSize) / 2;
        val guiHeight: Int = 0 //(height - ySize) / 2;

        var start: Int = 0

        while (true) {
            val renderRemaining: Int

            if (scale > 16) {
                renderRemaining = 16
                scale -= 16
            } else {
                renderRemaining = scale
                scale = 0
            }

            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
            drawTexturedModalRect(
                guiWidth + xPos,
                guiHeight + yPos + 58 - renderRemaining - start,
                getFluidTexture(fluid.getFluid(), FluidType.STILL),
                16,
                16 - (16 - renderRemaining)
            )
            start += 16

            if (renderRemaining == 0 || scale == 0) {
                break
            }
        }
    }

    @JvmStatic
    fun drawTexturedModalRect(
        xCoord: Int,
        yCoord: Int,
        textureSprite: TextureAtlasSprite?,
        widthIn: Int,
        heightIn: Int
    ) {
        val zLevel: Double = 0.0
        val tessellator: Tessellator = Tessellator.getInstance()
        val bufferbuilder: BufferBuilder = tessellator.getBuffer()
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX)
        bufferbuilder.pos((xCoord + 0).toDouble(), (yCoord + heightIn).toDouble(), zLevel)
            .tex(textureSprite!!.getMinU().toDouble(), textureSprite.getMaxV().toDouble()).endVertex()
        bufferbuilder.pos((xCoord + widthIn).toDouble(), (yCoord + heightIn).toDouble(), zLevel)
            .tex(textureSprite.getMaxU().toDouble(), textureSprite.getMaxV().toDouble()).endVertex()
        bufferbuilder.pos((xCoord + widthIn).toDouble(), (yCoord + 0).toDouble(), zLevel)
            .tex(textureSprite.getMaxU().toDouble(), textureSprite.getMinV().toDouble()).endVertex()
        bufferbuilder.pos((xCoord + 0).toDouble(), (yCoord + 0).toDouble(), zLevel)
            .tex(textureSprite.getMinU().toDouble(), textureSprite.getMinV().toDouble()).endVertex()
        tessellator.draw()
    }

    @JvmStatic
    fun getIntColor(r: Int, g: Int, b: Int, a: Int): Int {
        val A: Int = a * 256 * 256 * 256
        val R: Int = r * 256 * 256
        val G: Int = g * 256
        val B: Int = b
        val color: Int = A + R + G + B

        return color - 16777216
    }

    @JvmStatic
    fun calculateCelestialAngle(worldtime: Long, ticks: Float, daylenght: Float): Float {
        val j: Int = (worldtime % daylenght).toInt()
        var f1: Float = (j.toFloat() + ticks) / daylenght - 0.25f

        if (f1 < 0.0f) {
            ++f1
        }

        if (f1 > 1.0f) {
            --f1
        }

        val f2: Float = f1
        f1 = 1.0f - ((cos(f1.toDouble() * Math.PI) + 1.0) / 2.0).toFloat()
        f1 = f2 + (f1 - f2) / 3.0f
        return f1
    }

    @JvmStatic
    fun getPossibleDimensionsForSpaceshipTier(tier: Int, playerBase: EntityPlayerMP?): MutableList<Int> {
        val temp: MutableList<Int> = ArrayList<Int>()

        if (!ConfigManagerCore.disableRocketsToOverworld) {
            temp.add(ConfigManagerCore.idDimensionOverworld)
        }

        for (element: Int in (WorldUtil.registeredPlanets)!!) {
            if (element == ConfigManagerCore.idDimensionOverworld) {
                continue
            }
            val provider: WorldProvider? = WorldUtil.getProviderForDimensionServer(element)

            if (provider != null) {
                if (provider is IGalacticraftWorldProvider) {
                    if ((provider as IGalacticraftWorldProvider).canSpaceshipTierPass(tier)) {
                        temp.add(element)
                    }
                } else {
                    temp.add(element)
                }
            }
        }

        for (element: Int in (WorldUtil.registeredSpaceStations)!!.keys) {
            val data: SpaceStationWorldData = (SpaceStationWorldData.getStationData(playerBase!!.world, element, null))!!

            if (!ConfigManagerCore.spaceStationsRequirePermission || data.getAllowedAll() || data.getAllowedPlayers()
                    .contains(PlayerUtil.getName(playerBase)) || ArrayUtils.contains(
                    playerBase.server.getPlayerList().getOppedPlayerNames(), playerBase.getName()
                )
            ) {
                //Satellites always reachable from their own homeworld or from its other satellites
                if (playerBase != null) {
                    val currentWorld: Int = playerBase.dimension
                    //Player is on homeworld
                    if (currentWorld == data.getHomePlanet()) {
                        temp.add(element)
                        continue
                    }
                    if (playerBase.world.provider is IOrbitDimension) {
                        //Player is currently on another space station around the same planet
                        val dataCurrent: SpaceStationWorldData =
                            (SpaceStationWorldData.getStationData(playerBase.world, playerBase.dimension, null))!!
                        if (dataCurrent.getHomePlanet() == data.getHomePlanet()) {
                            temp.add(element)
                            continue
                        }
                    }
                }

                //Testing dimension is a satellite, but with a different homeworld - test its tier
                val homeWorld: WorldProvider? = WorldUtil.getProviderForDimensionServer(data.getHomePlanet())

                if (homeWorld != null) {
                    if (homeWorld is IGalacticraftWorldProvider) {
                        if ((homeWorld as IGalacticraftWorldProvider).canSpaceshipTierPass(tier)) {
                            temp.add(element)
                        }
                    } else {
                        temp.add(element)
                    }
                }
            }
        }

        return temp
    }

    @JvmStatic
    fun getArrayOfPossibleDimensions(tier: Int, playerBase: EntityPlayerMP?): HashMap<String?,Int> {
        val ids: MutableList<Int> = getPossibleDimensionsForSpaceshipTier(tier, playerBase)
        val map: HashMap<String?,Int> = HashMap<String?,Int>(ids.size, 1f)

        for (id: Int in ids) {
            var celestialBody: CelestialBody? = WorldUtil.getReachableCelestialBodiesForDimensionID(id)

            //It's a space station
            if (id > 0 && celestialBody == null) {
                celestialBody = GalacticraftCore.satelliteSpaceStation
                //This no longer checks whether a WorldProvider can be created, for performance reasons (that causes the dimension to load unnecessarily at map building stage)
                if (playerBase != null) {
                    val data: SpaceStationWorldData = (SpaceStationWorldData.getStationData(playerBase.world, id, null))!!
                    map.put(
                        (celestialBody)!!.getName() + "$" + data.getOwner() + "$" + data.getSpaceStationName() + "$" + id + "$" + data.getHomePlanet(),
                        id
                    )
                }
            } else  //It's a planet or moon
            {
                if (celestialBody === GalacticraftCore.planetOverworld) {
                    map.put((celestialBody)!!.getName(), id)
                } else {
                    val provider: WorldProvider? = WorldUtil.getProviderForDimensionServer(id)
                    if (celestialBody != null && provider != null) {
                        if (provider is IGalacticraftWorldProvider && provider !is IOrbitDimension || GCCoreUtil.getDimensionID(
                                provider
                            ) == 0
                        ) {
                            map.put(celestialBody.getName(), GCCoreUtil.getDimensionID(provider))
                        }
                    }
                }
            }
        }

        val cBodyList: ArrayList<CelestialBody> = ArrayList<CelestialBody>()
        cBodyList.addAll((GalaxyRegistry.getRegisteredPlanets())!!.values)
        cBodyList.addAll((GalaxyRegistry.getRegisteredMoons())!!.values)

        for (body: CelestialBody in cBodyList) {
            if (!body.getReachable()) {
                map.put(body.getLocalizedName() + "*", body.getDimensionID())
            }
        }

        WorldUtil.celestialMapCache.put(playerBase, map)
        return map
    }

    /**
     * Get the cached version of getArrayOfPossibleDimensions() to reduce server load + unwanted dimension loading
     * The cache will be updated every time the 'proper' version of getArrayOfPossibleDimensions is called.
     *
     * @param tier       - the rocket tier to test
     * @param playerBase - the player who will be riding the rocket (needed for checking space station permissions)
     * @return a Map of the names of the dimension vs. the dimension IDs
     */
    @JvmStatic
    fun getArrayOfPossibleDimensionsAgain(tier: Int, playerBase: EntityPlayerMP?): HashMap<String?,Int> {
        val map: HashMap<String?,Int>? = WorldUtil.celestialMapCache.get(playerBase)
        if (map != null) {
            return map
        }
        return getArrayOfPossibleDimensions(tier, playerBase)
    }

    @JvmStatic
    @SideOnly(Side.CLIENT)
    @Optional.Method(modid = "planetprogression")
    fun hasUnlocks(player: EntityPlayerSP?, body: SolarSystem?): Boolean {
        return ResearchHooksSP::class.java.getMethod("hasUnlockedBodiesInSystem", EntityPlayerSP::class.java, SolarSystem::class.java).invoke(null, player, body) as Boolean
    }

    @JvmStatic
    @SideOnly(Side.CLIENT)
    @Optional.Method(modid = "planetprogression")
    fun isReasearched(player: EntityPlayerSP?, body: CelestialBody?): Boolean {
        return ResearchHooksSP::class.java.getMethod("hasUnlockedCelestialBody", EntityPlayerSP::class.java, CelestialBody::class.java).invoke(null, player, body) as Boolean
    }

    enum class FluidType {
        STILL,
        FLOWING
    }
}
