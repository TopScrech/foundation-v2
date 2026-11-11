package asmodeuscore.core.handler

import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors

import asmodeuscore.core.event.AsmodeusClientEvent
import asmodeuscore.core.handler.ProviderFogHandler
import asmodeuscore.api.dimension.IProviderFog
import asmodeuscore.core.utils.Utils
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.ActiveRenderInfo
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.settings.GameSettings
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EquipmentSlot as EntityEquipmentSlot
import net.minecraft.world.item.ItemStack
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper
import net.minecraft.world.phys.Vec3 as Vec3d
import net.minecraft.world.EnumSkyBlock
import net.minecraft.world.level.Level as World
import net.minecraft.world.WorldProvider
import net.minecraft.world.level.biome.Biome
import net.minecraftforge.client.event.EntityViewRenderEvent
import net.minecraftforge.common.ForgeModContainer
import net.minecraftforge.eventbus.api.listener.SubscribeEvent
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import kotlin.math.min

open class ProviderFogHandler {
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    open fun onGetFogColour(event: EntityViewRenderEvent.FogColors) {
        if (event.getEntity() is EntityPlayer) {
            val player: EntityPlayer = event.getEntity() as EntityPlayer
            val world: World = player.level()
            val x: Int = MathHelper.floor(player.x)
            val y: Int = MathHelper.floor(player.y)
            val z: Int = MathHelper.floor(player.z)
            val blockAtEyes: IBlockState = ActiveRenderInfo.getBlockStateAtEntityViewpoint(
                world,
                event.getEntity(),
                event.getRenderPartialTicks().toFloat()
            )

            if (blockAtEyes.getMaterial() === Material.LAVA) {
                return
            }

            val mixedColor: Vec3d

            if (blockAtEyes.getMaterial() === Material.WATER) {
                mixedColor = getFogBlendColorWater(world, player, x, y, z, event.getRenderPartialTicks())
            } else {
                mixedColor = getFogBlendColour(
                    world,
                    player,
                    x,
                    y,
                    z,
                    event.getRed(),
                    event.getGreen(),
                    event.getBlue(),
                    event.getRenderPartialTicks()
                )
            }
            val temp: Float = (player.y / 700).toFloat() % 255

            val r: Float = (if (mixedColor.x - temp < 0) 0.0 else mixedColor.x - temp).toFloat()
            val g: Float = (if (mixedColor.y - temp < 0) 0.0 else mixedColor.y - temp).toFloat()
            val b: Float = (if (mixedColor.z - temp < 0) 0.0 else mixedColor.z - temp).toFloat()
            event.setRed(r)
            event.setGreen(g)
            event.setBlue(b)
        }
    }

    @SubscribeEvent
    open fun renderFog(event: EntityViewRenderEvent.RenderFogEvent) {
        val world: World = Minecraft.getInstance().player.level()
        val provider: WorldProvider? = world.provider

        if (provider is IProviderFog) {
            val sandstormFog: Float = 1f
            val providerAtum: IProviderFog = provider as IProviderFog


            val player: EntityPlayer = event.getEntity() as EntityPlayer
            val playerX: Int = MathHelper.floor(player.x)
            val playerY: Int = MathHelper.floor(player.y)
            val playerZ: Int = MathHelper.floor(player.z)

            var fogDensity: Float = 1 - providerAtum.getFogDensity(playerX, playerY, playerZ) //0.015F default;

            if (event.getEntity() is EntityPlayer) {
                if (fogDensity == 0.0f) GlStateManager.setFog(GlStateManager.FogMode.LINEAR)
                else GlStateManager.setFog(GlStateManager.FogMode.EXP)

                val helmet: ItemStack = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD)
                /*if (player.getPosition().getY() <= 60) {
                    fogDensity += (float) (62 - player.getPosition().getY()) * 0.025F;
                }*/
                /* if (helmet.getItem() instanceof ItemEyesOfAtum) {
                    fogDensity = fogDensity / 3;
                }
                if (helmet.getItem() == AtumItems.WANDERER_HELMET || helmet.getItem() == AtumItems.DESERT_HELMET_IRON || helmet.getItem() == AtumItems.DESERT_HELMET_GOLD || helmet.getItem() == AtumItems.DESERT_HELMET_DIAMOND) {
                    fogDensity = fogDensity / 1.5F;
                }*/
                //if (player.posY >= player.world.getSeaLevel() - 8) {
                fogDensity *= sandstormFog - (sandstormFog - sandstormFog * 1.0f)

                val light: Int =
                    player.level().getLightFor(EnumSkyBlock.SKY, BlockPos(player.x, player.y + 1, player.z))
                if (light < 12) {
                    if (light <= 0) fogDensity = 0.001f
                    else fogDensity = fogDensity / ((12 - light) * 2)
                }


                //if(player.posY > 150)
                fogDensity = (fogDensity / (player.y / 30)).toFloat()

                //}
                if (AsmodeusClientEvent.Companion.inOxygenBlock) fogDensity = 0.05f

                GlStateManager.setFogDensity(fogDensity)
            }
        }
    }

    companion object {
        private const val fogX = 0.0
        private const val fogZ = 0.0
        private const val fogInit = false
        private const val fogFarPlaneDistance = 0f
        private const val newFogDensity = 0f
        private const val fogDensity = 1.0f

        /*
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void onRenderFog(EntityViewRenderEvent.RenderFogEvent event)
	{
		
		Entity entity = event.getEntity();
		World world = entity.world;
		int playerX = MathHelper.floor(entity.posX);
		int playerY = MathHelper.floor(entity.posY);
		int playerZ = MathHelper.floor(entity.posZ);

		if(world == null) return;
		if(!(entity instanceof EntityPlayerSP)) return;
		
		if (playerX == fogX && playerZ == fogZ && fogInit)
		{
			renderFog(event.getFogMode(), fogFarPlaneDistance, 0.75f);
			return;
		}
		
		
		fogInit = true;

		int distance = 20;
		float fpDistanceBiomeFog = 1F;
		float weightBiomeFog = 1;

		for (int x = -distance; x <= distance; ++x)
		{
			for (int z = -distance; z <= distance; ++z)
			{
				WorldProvider provider = world.provider;
				
				if (provider instanceof IProviderFog)
				{	
					float distancePart = ((IProviderFog)provider).getFogDensity(playerX + x, playerY, playerZ + z);
					
					
					
					int light = Minecraft.getInstance().level.getLightFor(EnumSkyBlock.SKY, Minecraft.getInstance().player.getPosition());
					//System.out.println(light);
					if(light < 6)
					{
						distancePart = 1.0F - ((15 - light) / 100) * light;
						
					}			
					float weightPart = 1;

					if (x == -distance)
					{
						double xDiff = 1 - (entity.posX - playerX);
						distancePart *= xDiff;
						weightPart *= xDiff;
					}
					else if (x == distance)
					{
						double xDiff = entity.posX - playerX;
						distancePart *= xDiff;
						weightPart *= xDiff;
					}

					if (z == -distance)
					{
						double zDiff = 1 - (entity.posZ - playerZ);
						distancePart *= zDiff;
						weightPart *= zDiff;
					}
					else if (z == distance)
					{
						double zDiff = entity.posZ - playerZ;
						distancePart *= zDiff;
						weightPart *= zDiff;
					}
					fpDistanceBiomeFog += distancePart;
					weightBiomeFog += weightPart;
				}
			}
		}

		float weightMixed = distance * 2 * distance * 2;
		float weightDefault = weightMixed - weightBiomeFog;
		float fpDistanceBiomeFogAvg = weightBiomeFog == 0 ? 0 : fpDistanceBiomeFog / weightBiomeFog;
		float farPlaneDistance = (fpDistanceBiomeFog * 240 + event.getFarPlaneDistance() * weightDefault) / weightMixed;
		float farPlaneDistanceScaleBiome = 0.1f * (1 - fpDistanceBiomeFogAvg) + 0.75f * fpDistanceBiomeFogAvg;
		float farPlaneDistanceScale = (farPlaneDistanceScaleBiome * weightBiomeFog + 0.75f * weightDefault) / weightMixed;
		fogX = entity.posX;
		fogZ = entity.posZ;
		fogFarPlaneDistance = Math.min(farPlaneDistance, event.getFarPlaneDistance());
		renderFog(event.getFogMode(), fogFarPlaneDistance, 0.75F);
	
	}*/
        @JvmStatic
        private fun renderFog(fogMode: Int, farPlaneDistance: Float, farPlaneDistanceScale: Float) {
            if (fogMode < 0) {
                GlStateManager.setFogStart(0f)
                GlStateManager.setFogEnd(farPlaneDistance)
            } else {
                GlStateManager.setFogStart(farPlaneDistance * farPlaneDistanceScale)
                GlStateManager.setFogEnd(farPlaneDistance)
            }
        }

        @JvmStatic
        private fun postProcessColor(
            world: World,
            player: EntityLivingBase,
            r: Float,
            g: Float,
            b: Float,
            renderPartialTicks: Double
        ): Vec3d {
            var r: Float = r
            var g: Float = g
            var b: Float = b
            var darkScale: Double =
                (player.lastTickPosY + (player.y - player.lastTickPosY) * renderPartialTicks) * world.provider.getVoidFogYFactor()

            if (player.hasEffect(MobEffects.BLINDNESS)) {
                val duration: Int = player.getEffect(MobEffects.BLINDNESS)!!.getDuration()
                darkScale *= (if (duration < 20) 1 - duration / 20f else 0f).toDouble()
            }
            if (darkScale < 1) {
                darkScale = if (darkScale < 0) 0.0 else darkScale * darkScale
                r *= darkScale.toFloat()
                g *= darkScale.toFloat()
                b *= darkScale.toFloat()
            }

            if (player.hasEffect(MobEffects.NIGHT_VISION)) {
                val duration: Int = player.getEffect(MobEffects.NIGHT_VISION)!!.getDuration()
                val brightness: Float =
                    if (duration > 200) 1f else 0.7f + MathHelper.sin(((duration - renderPartialTicks) * Math.PI * 0.2f).toFloat()) * 0.3f
                var scale: Float = 1 / r
                scale = min(scale, 1 / g)
                scale = min(scale, 1 / b)
                r = r * (1 - brightness) + r * scale * brightness
                g = g * (1 - brightness) + g * scale * brightness
                b = b * (1 - brightness) + b * scale * brightness
            }
            if (Minecraft.getInstance().gameSettings.anaglyph) {
                val aR: Float = (r * 30 + g * 59 + b * 11) / 100
                val aG: Float = (r * 30 + g * 70) / 100
                val aB: Float = (r * 30 + b * 70) / 100
                r = aR
                g = aG
                b = aB
            }
            return Vec3d(r.toDouble(), g.toDouble(), b.toDouble())
        }

        @JvmStatic
        private fun getFogBlendColorWater(
            world: World,
            playerEntity: EntityLivingBase,
            playerX: Int,
            playerY: Int,
            playerZ: Int,
            renderPartialTicks: Double
        ): Vec3d {
            val distance: Int = 2
            var rBiomeFog: Float = 0f
            var gBiomeFog: Float = 0f
            var bBiomeFog: Float = 0f

            for (x: Int in -distance..distance) {
                for (z: Int in -distance..distance) {
                    val biome: Biome = world.getBiomeForCoordsBody(
                        BlockPos(
                            (playerX + x).toDouble(),
                            playerEntity.y,
                            (playerZ + z).toDouble()
                        )
                    )
                    val waterColorMult: Int = biome.getWaterColorMultiplier()
                    var rPart: Float = ((waterColorMult and 0xFF0000) shr 16).toFloat()
                    var gPart: Float = ((waterColorMult and 0x00FF00) shr 8).toFloat()
                    var bPart: Float = (waterColorMult and 0x0000FF).toFloat()

                    if (x == -distance) {
                        val xDiff: Double = 1 - (playerEntity.x - playerX)
                        rPart *= xDiff.toFloat()
                        gPart *= xDiff.toFloat()
                        bPart *= xDiff.toFloat()
                    } else if (x == distance) {
                        val xDiff: Double = playerEntity.x - playerX
                        rPart *= xDiff.toFloat()
                        gPart *= xDiff.toFloat()
                        bPart *= xDiff.toFloat()
                    }

                    if (z == -distance) {
                        val zDiff: Double = 1 - (playerEntity.z - playerZ)
                        rPart *= zDiff.toFloat()
                        gPart *= zDiff.toFloat()
                        bPart *= zDiff.toFloat()
                    } else if (z == distance) {
                        val zDiff: Double = playerEntity.z - playerZ
                        rPart *= zDiff.toFloat()
                        gPart *= zDiff.toFloat()
                        bPart *= zDiff.toFloat()
                    }

                    rBiomeFog += rPart
                    gBiomeFog += gPart
                    bBiomeFog += bPart
                }
            }

            rBiomeFog /= 255f
            gBiomeFog /= 255f
            bBiomeFog /= 255f

            val weight: Float = (distance * 2 * distance * 2).toFloat()
            val respirationLevel: Float = EnchantmentHelper.getRespirationModifier(playerEntity) * 0.2f
            val rMixed: Float = (rBiomeFog * 0.02f + respirationLevel) / weight
            val gMixed: Float = (gBiomeFog * 0.02f + respirationLevel) / weight
            val bMixed: Float = (bBiomeFog * 0.2f + respirationLevel) / weight
            return postProcessColor(world, playerEntity, rMixed, gMixed, bMixed, renderPartialTicks)
        }

        @JvmStatic
        private fun getFogBlendColour(
            world: World,
            playerEntity: EntityLivingBase,
            playerX: Int,
            playerY: Int,
            playerZ: Int,
            defR: Float,
            defG: Float,
            defB: Float,
            renderPartialTicks: Double
        ): Vec3d {
            val settings: GameSettings = Minecraft.getInstance().gameSettings
            val ranges: IntArray = ForgeModContainer.blendRanges
            var distance: Int = 6

            if (settings.fancyGraphics && settings.renderDistanceChunks >= 0 && settings.renderDistanceChunks < ranges.size) {
                distance = ranges[settings.renderDistanceChunks]
            }

            var rBiomeFog: Float = 0f
            var gBiomeFog: Float = 0f
            var bBiomeFog: Float = 0f
            var weightBiomeFog: Float = 0f

            for (x: Int in -distance..distance) {
                for (z: Int in -distance..distance) {
                    val provider: WorldProvider? = world.provider

                    //BiomeGenBase biome = world.getBiomeGenForCoords(playerX + x, playerZ + z);
                    if (provider is IProviderFog) {
                        val biomeFog: IProviderFog = provider as IProviderFog
                        val fogColor: Vec3d = provider.getFogColor(renderPartialTicks.toFloat(), 0.0f)

                        val fogColour: Int = Utils.getIntColor(
                            Math.round(fogColor.x.toFloat() * 255),
                            Math.round(fogColor.y.toFloat() * 255),
                            Math.round(fogColor.z.toFloat() * 255)
                        ) //biomeFog.getFogColor(playerX + x, playerY, playerZ + z);
                        var rPart: Float = ((fogColour and 0xFF0000) shr 16).toFloat()
                        var gPart: Float = ((fogColour and 0x00FF00) shr 8).toFloat()
                        var bPart: Float = (fogColour and 0x0000FF).toFloat()

                        var weightPart: Float = 1f

                        if (x == -distance) {
                            val xDiff: Double = 1 - (playerEntity.x - playerX)
                            rPart *= xDiff.toFloat()
                            gPart *= xDiff.toFloat()
                            bPart *= xDiff.toFloat()
                            weightPart *= xDiff.toFloat()
                        } else if (x == distance) {
                            val xDiff: Double = playerEntity.x - playerX
                            rPart *= xDiff.toFloat()
                            gPart *= xDiff.toFloat()
                            bPart *= xDiff.toFloat()
                            weightPart *= xDiff.toFloat()
                        }

                        if (z == -distance) {
                            val zDiff: Double = 1 - (playerEntity.z - playerZ)
                            rPart *= zDiff.toFloat()
                            gPart *= zDiff.toFloat()
                            bPart *= zDiff.toFloat()
                            weightPart *= zDiff.toFloat()
                        } else if (z == distance) {
                            val zDiff: Double = playerEntity.z - playerZ
                            rPart *= zDiff.toFloat()
                            gPart *= zDiff.toFloat()
                            bPart *= zDiff.toFloat()
                            weightPart *= zDiff.toFloat()
                        }

                        val temp: Float = (playerEntity.y / 1000).toFloat()

                        rBiomeFog += rPart
                        gBiomeFog += gPart
                        bBiomeFog += bPart
                        weightBiomeFog += weightPart
                    }
                }
            }

            if (weightBiomeFog == 0f || distance == 0) {
                return Vec3d(defR.toDouble(), defG.toDouble(), defB.toDouble())
            }

            rBiomeFog /= 255f
            gBiomeFog /= 255f
            bBiomeFog /= 255f

            // Calculate day / night / weather scale for BiomeFog component
            val celestialAngle: Float = world.getCelestialAngle(renderPartialTicks.toFloat())
            val baseScale: Float =
                MathHelper.clamp(MathHelper.cos(celestialAngle * Math.PI.toFloat() * 2.0f) * 2.0f + 0.5f, 0f, 1f)
            var rScale: Float = baseScale * 0.94f + 0.06f
            var gScale: Float = baseScale * 0.94f + 0.06f
            var bScale: Float = baseScale * 0.91f + 0.09f
            val rainStrength: Float = world.getRainStrength(renderPartialTicks.toFloat())

            if (rainStrength > 0) {
                rScale *= 1 - rainStrength * 0.5f
                gScale *= 1 - rainStrength * 0.5f
                bScale *= 1 - rainStrength * 0.4f
            }

            val thunderStrength: Float = world.getThunderStrength(renderPartialTicks.toFloat())

            if (thunderStrength > 0) {
                rScale *= 1 - thunderStrength * 0.5f
                gScale *= 1 - thunderStrength * 0.5f
                bScale *= 1 - thunderStrength * 0.5f
            }


            // Apply post-processing to BiomeFog component.  Default color was already processed by Vanilla.
            rBiomeFog *= rScale / weightBiomeFog
            gBiomeFog *= gScale / weightBiomeFog
            bBiomeFog *= bScale / weightBiomeFog

            val processedColor: Vec3d =
                postProcessColor(world, playerEntity, rBiomeFog, gBiomeFog, bBiomeFog, renderPartialTicks)
            rBiomeFog = processedColor.x.toFloat()
            gBiomeFog = processedColor.y.toFloat()
            bBiomeFog = processedColor.z.toFloat()

            // Mix default fog component with BiomeFog component
            val weightMixed: Float = (distance * 2 * distance * 2).toFloat()
            val weightDefault: Float = weightMixed - weightBiomeFog

            val x: Double = ((rBiomeFog * weightBiomeFog + defR * weightDefault) / weightMixed).toDouble()
            val y: Double = ((gBiomeFog * weightBiomeFog + defG * weightDefault) / weightMixed).toDouble()
            val z: Double = ((bBiomeFog * weightBiomeFog + defB * weightDefault) / weightMixed).toDouble()
            return Vec3d(x, y, z)
        }
    }
}
