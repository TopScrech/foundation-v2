package micdoodle8.mods.galacticraft.core

import org.lwjgl.opengl.GL11
import micdoodle8.mods.galacticraft.api.entity.IAntiGrav
import micdoodle8.mods.galacticraft.api.entity.ICameraZoomEntity
import micdoodle8.mods.galacticraft.api.item.IArmorGravity
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.api.world.IOrbitDimension
import micdoodle8.mods.galacticraft.api.world.IWeatherProvider
import micdoodle8.mods.galacticraft.api.world.IZeroGDimension
import micdoodle8.mods.galacticraft.core.blocks.BlockGrating
import micdoodle8.mods.galacticraft.core.client.BubbleRenderer
import micdoodle8.mods.galacticraft.core.client.FootprintRenderer
import micdoodle8.mods.galacticraft.core.client.SkyProviderOverworld
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderMoon
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderSpaceStation
import micdoodle8.mods.galacticraft.core.entities.player.EnumGravity
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStatsClient
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.FluidUtil
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.core.util.OxygenUtil
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import micdoodle8.mods.galacticraft.planets.venus.VenusItems
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.passive.EntityChicken
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.EntityArrow
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.client.ForgeHooksClient
import net.minecraftforge.client.MinecraftForgeClient
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.common.IWorldGenerator
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.lang.Exception
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.LinkedList
import java.util.Random
import kotlin.math.max
import kotlin.math.min

/**
 * These methods are called from vanilla minecraft through bytecode injection done in MicdoodleCore
 *
 * See https://github.com/micdoodle8/MicdoodleCore
 */
object TransformerHooks {
    private val otherModGeneratorsWhitelist: MutableList<IWorldGenerator> = LinkedList<IWorldGenerator>()
    private var generatorTCAuraNodes: IWorldGenerator? = null
    private var generateTCAuraNodes: Method? = null
    private var generatorsInitialised = false

    @JvmField
    var spawnListAE2_GC: MutableList<Block?> = LinkedList<Block?>()

    @JvmField
    var renderBuilder: ThreadLocal<BufferBuilder?> = ThreadLocal<BufferBuilder?>()
    private var rainSoundCounter = 0
    private val random = Random()

    @JvmStatic
    fun getGravityForEntity(entity: Entity): Double {
        if (entity.world.provider is IGalacticraftWorldProvider) {
            if (entity is EntityChicken && !OxygenUtil.isAABBInBreathableAirBlock(
                    entity.world,
                    entity.getEntityBoundingBox()
                )
            ) {
                return 0.08
            }

            val customProvider: IGalacticraftWorldProvider = entity.world.provider as IGalacticraftWorldProvider
            if (entity is EntityPlayer) {
                val player: EntityPlayer = entity
                if (player.inventory != null) {
                    var armorModLowGrav: Int = 100
                    var armorModHighGrav: Int = 100
                    for (armorPiece: ItemStack? in player.getArmorInventoryList()) {
                        if (armorPiece != null && armorPiece.getItem() is IArmorGravity) {
                            armorModLowGrav -= (armorPiece.getItem() as IArmorGravity).gravityOverrideIfLow(player)
                            armorModHighGrav -= (armorPiece.getItem() as IArmorGravity).gravityOverrideIfHigh(player)
                        }
                    }
                    if (armorModLowGrav > 100) {
                        armorModLowGrav = 100
                    }
                    if (armorModHighGrav > 100) {
                        armorModHighGrav = 100
                    }
                    if (armorModLowGrav < 0) {
                        armorModLowGrav = 0
                    }
                    if (armorModHighGrav < 0) {
                        armorModHighGrav = 0
                    }
                    if (customProvider.getGravity() > 0) {
                        return 0.08 - (customProvider.getGravity() * armorModLowGrav) / 100
                    }
                    return 0.08 - (customProvider.getGravity() * armorModHighGrav) / 100
                }
            }
            return 0.08 - customProvider.getGravity()
        } else if (entity is IAntiGrav) {
            return 0.0
        } else {
            return 0.08
        }
    }

    @JvmStatic
    fun getItemGravity(e: EntityItem): Double {
        if (e.world.provider is IGalacticraftWorldProvider) {
            val customProvider: IGalacticraftWorldProvider = e.world.provider as IGalacticraftWorldProvider
            return max(
                0.002,
                0.03999999910593033 - (if (customProvider is IOrbitDimension) 0.05999999910593033 else customProvider.getGravity()
                    .toDouble()) / 1.75
            )
        } else {
            return 0.03999999910593033
        }
    }

    @JvmStatic
    fun getArrowGravity(e: EntityArrow): Float {
        if (e.world.provider is IGalacticraftWorldProvider) {
            return (e.world.provider as IGalacticraftWorldProvider).getArrowGravity()
        } else {
            return 0.05f
        }
    }

    @JvmStatic
    fun getRainStrength(world: World, partialTicks: Float): Float {
        if (world.isRemote) {
            if (world.provider.getSkyRenderer() is SkyProviderOverworld) {
                return 0.0f
            }
        }

        return world.prevRainingStrength + (world.rainingStrength - world.prevRainingStrength) * partialTicks
    }

    @JvmStatic
    fun otherModGenerate(
        chunkX: Int,
        chunkZ: Int,
        world: World,
        chunkGenerator: IChunkGenerator?,
        chunkProvider: IChunkProvider?
    ) {
        if (world.provider is WorldProviderSpaceStation) {
            return
        }

        if (world.provider !is IGalacticraftWorldProvider || ConfigManagerCore.enableOtherModsFeatures) {
            try {
                GameRegistry.generateWorld(chunkX, chunkZ, world, chunkGenerator, chunkProvider)
            } catch (e: Exception) {
                GCLog.severe("Error in another mod's worldgen.  This is *NOT* a Galacticraft bug, report it to the other mod please.")
                GCLog.severe("Details:- Dimension:" + GCCoreUtil.getDimensionID(world) + "  Chunk cx,cz:" + chunkX + "," + chunkZ + "  Seed:" + world.getSeed())
                e.printStackTrace()
            }
            return
        }

        if (!generatorsInitialised) {
            generatorsInitialised = true

            if (ConfigManagerCore.whitelistCoFHCoreGen) {
                addWorldGenForName("CoFHCore custom oregen", "cofh.cofhworld.init.WorldHandler")
            }
            addWorldGenForName("GalacticGreg oregen", "bloodasp.galacticgreg.GT_Worldgenerator_Space")
            addWorldGenForName("Dense Ores oregen", "com.rwtema.denseores.WorldGenOres")
            addWorldGenForName("AE2 meteorites worldgen", "appeng.worldgen.MeteoriteWorldGen")

            try {
                val genThaumCraft: Class<*>? = Class.forName("thaumcraft.common.lib.world.ThaumcraftWorldGenerator")
                if (genThaumCraft != null && ConfigManagerCore.enableThaumCraftNodes) {
                    val regField: Field = GameRegistry::class.java.getDeclaredField("worldGenerators")
                    regField.setAccessible(true)
                    val registeredGenerators: MutableSet<IWorldGenerator?> =
                        regField.get(null) as MutableSet<IWorldGenerator?>
                    for (gen: IWorldGenerator? in registeredGenerators) {
                        if (genThaumCraft.isInstance(gen)) {
                            generatorTCAuraNodes = gen
                            break
                        }
                    }
                    if (generatorTCAuraNodes != null) {
                        generateTCAuraNodes = genThaumCraft.getDeclaredMethod(
                            "generateWildNodes",
                            World::class.java,
                            Random::class.java,
                            Int::class.javaPrimitiveType,
                            Int::class.javaPrimitiveType,
                            Boolean::class.javaPrimitiveType,
                            Boolean::class.javaPrimitiveType
                        )
                        generateTCAuraNodes!!.setAccessible(true)
                        GCLog.info("Whitelisting ThaumCraft aura node generation on planets.")
                    }
                }
            } catch (e: Exception) {
            }
        }

        if (otherModGeneratorsWhitelist.size > 0 || generateTCAuraNodes != null) {
            try {
                val worldSeed: Long = world.getSeed()
                val fmlRandom: Random = Random(worldSeed)
                val xSeed: Long = fmlRandom.nextLong() shr (2 + 1L).toInt()
                val zSeed: Long = fmlRandom.nextLong() shr (2 + 1L).toInt()
                val chunkSeed: Long = (xSeed * chunkX + zSeed * chunkZ) xor worldSeed
                fmlRandom.setSeed(chunkSeed)

                for (gen: IWorldGenerator in otherModGeneratorsWhitelist) {
                    gen.generate(fmlRandom, chunkX, chunkZ, world, chunkGenerator, chunkProvider)
                }
                if (generateTCAuraNodes != null) {
                    generateTCAuraNodes!!.invoke(generatorTCAuraNodes, world, fmlRandom, chunkX, chunkZ, false, true)
                }
            } catch (e: Exception) {
                GCLog.severe("Error in another mod's worldgen.  This is *NOT* a Galacticraft bug, report it to the other mod please.")
                e.printStackTrace()
            }
        }
    }

    @JvmStatic
    private fun addWorldGenForName(logString: String, name: String) {
        try {
            val target: Class<*>? = Class.forName(name)
            if (target != null) {
                val regField: Field = GameRegistry::class.java.getDeclaredField("worldGenerators")
                regField.setAccessible(true)
                val registeredGenerators: MutableSet<IWorldGenerator?> =
                    regField.get(null) as MutableSet<IWorldGenerator?>
                for (gen: IWorldGenerator? in registeredGenerators) {
                    if (target.isInstance(gen)) {
                        otherModGeneratorsWhitelist.add(gen!!)
                        GCLog.info("Whitelisting " + logString + " on planets.")
                        return
                    }
                }
            }
        } catch (e: Exception) {
        }
    }

    /*
     * Used to supplement the hard-coded blocklist in AE2's MeteoritePlacer class
     */
    @JvmStatic
    fun addAE2MeteorSpawn(o: Any?, b: Block?): Boolean {
        if (o is MutableCollection<*>) {
            (o as MutableCollection<Block?>).add(b)
            o.addAll(spawnListAE2_GC)
            return true
        }
        return false
    }

    @JvmStatic
    @SideOnly(Side.CLIENT)
    fun getWorldBrightness(world: World): Float {
        if (world.provider is WorldProviderMoon) {
            val f1: Float = world.getCelestialAngle(1.0f)
            var f2: Float = 1.0f - (MathHelper.cos(f1 * Constants.twoPI) * 2.0f + 0.2f)

            if (f2 < 0.0f) {
                f2 = 0.0f
            }

            if (f2 > 1.0f) {
                f2 = 1.0f
            }

            f2 = 1.0f - f2
            return f2 * 0.8f
        }

        return world.getSunBrightness(1.0f)
    }

    @JvmStatic
    @SideOnly(Side.CLIENT)
    fun getColorRed(world: World?): Float {
        return WorldUtil.getWorldColor((world)!!).x.toFloat()
    }

    @JvmStatic
    @SideOnly(Side.CLIENT)
    fun getColorGreen(world: World?): Float {
        return WorldUtil.getWorldColor((world)!!).y.toFloat()
    }

    @JvmStatic
    @SideOnly(Side.CLIENT)
    fun getColorBlue(world: World?): Float {
        return WorldUtil.getWorldColor((world)!!).z.toFloat()
    }

    @JvmStatic
    @SideOnly(Side.CLIENT)
    fun getFogColorHook(world: World): Vec3d {
        val player: EntityPlayerSP = FMLClientHandler.instance().getClient().player
        if (world.provider.getSkyRenderer() is SkyProviderOverworld) {
            var var20: Float = ((player.posY).toFloat() - Constants.OVERWORLD_SKYPROVIDER_STARTHEIGHT) / 1000.0f
            var20 = MathHelper.sqrt(var20)
            val var21: Float = max(1.0f - var20 * 40.0f, 0.0f)

            val vec: Vec3d = world.getFogColor(1.0f)

            return Vec3d(
                vec.x * max(1.0f - var20 * 1.29f, 0.0f),
                vec.y * max(1.0f - var20 * 1.29f, 0.0f),
                vec.z * max(1.0f - var20 * 1.29f, 0.0f)
            )
        }

        return world.getFogColor(1.0f)
    }

    @JvmStatic
    @SideOnly(Side.CLIENT)
    fun getSkyColorHook(world: World): Vec3d {
        val player: EntityPlayerSP? = FMLClientHandler.instance().getClient().player
        if (world.provider.getSkyRenderer() is SkyProviderOverworld || (player != null && player.posY > Constants.OVERWORLD_CLOUD_HEIGHT && player.getRidingEntity() is EntitySpaceshipBase)) {
            val f1: Float = world.getCelestialAngle(1.0f)
            var f2: Float = MathHelper.cos(f1 * Constants.twoPI) * 2.0f + 0.5f

            if (f2 < 0.0f) {
                f2 = 0.0f
            }

            if (f2 > 1.0f) {
                f2 = 1.0f
            }

            val i: Int = MathHelper.floor(player!!.posX)
            val j: Int = MathHelper.floor(player.posY)
            val k: Int = MathHelper.floor(player.posZ)
            val pos: BlockPos = BlockPos(i, j, k)
            val l: Int = ForgeHooksClient.getSkyBlendColour(world, pos)
            var f4: Float = (l shr 16 and 255).toFloat() / 255.0f
            var f5: Float = (l shr 8 and 255).toFloat() / 255.0f
            var f6: Float = (l and 255).toFloat() / 255.0f
            f4 *= f2
            f5 *= f2
            f6 *= f2

            if (player.posY <= Constants.OVERWORLD_SKYPROVIDER_STARTHEIGHT) {
                val vec: Vec3d = world.getSkyColor(FMLClientHandler.instance().getClient().getRenderViewEntity(), 1.0f)
                val blend: Double =
                    (player.posY - Constants.OVERWORLD_CLOUD_HEIGHT) / (Constants.OVERWORLD_SKYPROVIDER_STARTHEIGHT - Constants.OVERWORLD_CLOUD_HEIGHT)
                val ablend: Double = 1 - blend
                return Vec3d(f4 * blend + vec.x * ablend, f5 * blend + vec.y * ablend, f6 * blend + vec.z * ablend)
            } else {
                var blend: Double = min(1.0, (player.posY - Constants.OVERWORLD_SKYPROVIDER_STARTHEIGHT) / 300.0)
                val ablend: Double = 1.0 - blend
                blend /= 255.0
                return Vec3d(f4 * ablend + blend * 31.0, f5 * ablend + blend * 8.0, f6 * ablend + blend * 99.0)
            }
        }

        return world.getSkyColor(FMLClientHandler.instance().getClient().getRenderViewEntity(), 1.0f)
    }

    @JvmStatic
    @SideOnly(Side.CLIENT)
    fun getRenderPosY(viewEntity: Entity, regular: Double): Double {
        if (viewEntity.posY >= 256) {
            return 255.0
        } else {
            return regular + viewEntity.getEyeHeight()
        }
    }

    @JvmStatic
    @SideOnly(Side.CLIENT)
    fun shouldRenderFire(entity: Entity): Boolean {
        if (entity.world == null || entity.world.provider !is IGalacticraftWorldProvider) {
            return entity.isBurning()
        }

        if (entity !is EntityLivingBase && entity !is EntityArrow) {
            return entity.isBurning()
        }

        if (entity.isBurning()) {
            if (OxygenUtil.noAtmosphericCombustion(entity.world.provider)) {
                return OxygenUtil.isAABBInBreathableAirBlock(entity.world, entity.getEntityBoundingBox())
            } else {
                return true
            }
            //Disable fire on Galacticraft worlds with no oxygen
        }

        return false
    }

    @JvmStatic
    @SideOnly(Side.CLIENT)
    fun orientCamera(partialTicks: Float) {
        val player: EntityPlayerSP = ClientProxyCore.mc.player
        val stats: GCPlayerStatsClient = (GCPlayerStatsClient.get(player))!!

        val viewEntity: Entity? = ClientProxyCore.mc.getRenderViewEntity()

        if (player.getRidingEntity() is ICameraZoomEntity && ClientProxyCore.mc.gameSettings.thirdPersonView == 0) {
            val entity: Entity? = player.getRidingEntity()
            var offset: Float = (entity as ICameraZoomEntity).getRotateOffset()
            if (offset > -10f) {
                offset += ClientProxyCore.PLAYER_Y_OFFSET
                GL11.glTranslatef(0f, -offset, 0f)
                val anglePitch: Float =
                    entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks
                val angleYaw: Float =
                    entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks
                GL11.glRotatef(-anglePitch, 0.0f, 0.0f, 1.0f)
                GL11.glRotatef(angleYaw, 0.0f, 1.0f, 0.0f)

                GL11.glTranslatef(0f, offset, 0f)
            }
        }

        if (viewEntity is EntityLivingBase && viewEntity.world.provider is IZeroGDimension && !viewEntity.isPlayerSleeping()) {
            val pitch: Float =
                viewEntity.prevRotationPitch + (viewEntity.rotationPitch - viewEntity.prevRotationPitch) * partialTicks
            val yaw: Float =
                viewEntity.prevRotationYaw + (viewEntity.rotationYaw - viewEntity.prevRotationYaw) * partialTicks + 180.0f
            val eyeHeightChange: Float = viewEntity.width / 2.0f

            //            GL11.glTranslatef(0.0F, -f1, 0.0F);
            GL11.glRotatef(-yaw, 0.0f, 1.0f, 0.0f)
            GL11.glRotatef(-pitch, 1.0f, 0.0f, 0.0f)
            GL11.glTranslatef(0.0f, 0.0f, 0.1f)

            val gDir: EnumGravity = (stats.getGdir())!!
            GL11.glRotatef(180.0f * gDir.getThetaX(), 1.0f, 0.0f, 0.0f)
            GL11.glRotatef(180.0f * gDir.getThetaZ(), 0.0f, 0.0f, 1.0f)
            GL11.glRotatef(pitch * gDir.getPitchGravityX(), 1.0f, 0.0f, 0.0f)
            GL11.glRotatef(pitch * gDir.getPitchGravityY(), 0.0f, 1.0f, 0.0f)
            GL11.glRotatef(yaw * gDir.getYawGravityX(), 1.0f, 0.0f, 0.0f)
            GL11.glRotatef(yaw * gDir.getYawGravityY(), 0.0f, 1.0f, 0.0f)
            GL11.glRotatef(yaw * gDir.getYawGravityZ(), 0.0f, 0.0f, 1.0f)

            //        	GL11.glTranslatef(sneakY * gDir.getSneakVecX(), sneakY * gDir.getSneakVecY(), sneakY * gDir.getSneakVecZ());
            GL11.glTranslatef(
                eyeHeightChange * gDir.getEyeVecX(),
                eyeHeightChange * gDir.getEyeVecY(),
                eyeHeightChange * gDir.getEyeVecZ()
            )

            if (stats.getGravityTurnRate() < 1.0f) {
                GL11.glRotatef(
                    90.0f * (stats.getGravityTurnRatePrev() + (stats.getGravityTurnRate() - stats.getGravityTurnRatePrev()) * partialTicks),
                    stats.getGravityTurnVecX(),
                    stats.getGravityTurnVecY(),
                    stats.getGravityTurnVecZ()
                )
            }
        }

        //omit this for interesting 3P views
//        GL11.glTranslatef(0.0F, 0.0F, -0.1F);
//        GL11.glRotatef(pitch, 1.0F, 0.0F, 0.0F);
//        GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
//        GL11.glTranslatef(0.0F, f1, 0.0F);
    }

    @JvmStatic
    @SideOnly(Side.CLIENT)
    fun renderLiquidOverlays(partialTicks: Float) {
        var within: Boolean = false
        for (entry: MutableMap.MutableEntry<Fluid?, ResourceLocation?> in ClientProxyCore.submergedTextures.entries) {
            if (FluidUtil.isInsideOfFluid(ClientProxyCore.mc.player, (entry.key)!!)) {
                within = true
                ClientProxyCore.mc.getTextureManager().bindTexture(entry.value)
                break
            }
        }

        if (!within) {
            return
        }

        val tessellator: Tessellator = Tessellator.getInstance()
        val f1: Float = ClientProxyCore.mc.player.getBrightness() / 3.0f
        GL11.glColor4f(f1, f1, f1, 1.0f)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glPushMatrix()
        val f2: Float = 4.0f
        val f3: Float = -1.1f
        val f4: Float = 1.1f
        val f5: Float = -1.1f
        val f6: Float = 1.1f
        val f7: Float = -0.25f
        val f8: Float = -ClientProxyCore.mc.player.rotationYaw / 64.0f
        val f9: Float = ClientProxyCore.mc.player.rotationPitch / 64.0f
        val worldRenderer: BufferBuilder = tessellator.getBuffer()
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
        worldRenderer.pos(f3.toDouble(), f5.toDouble(), f7.toDouble()).tex((f2 + f8).toDouble(), (f2 + f9).toDouble())
            .endVertex()
        worldRenderer.pos(f4.toDouble(), f5.toDouble(), f7.toDouble()).tex((0.0f + f8).toDouble(), (f2 + f9).toDouble())
            .endVertex()
        worldRenderer.pos(f4.toDouble(), f6.toDouble(), f7.toDouble())
            .tex((0.0f + f8).toDouble(), (0.0f + f9).toDouble()).endVertex()
        worldRenderer.pos(f3.toDouble(), f6.toDouble(), f7.toDouble()).tex((f2 + f8).toDouble(), (0.0f + f9).toDouble())
            .endVertex()
        tessellator.draw()
        GL11.glPopMatrix()
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        GL11.glDisable(GL11.GL_BLEND)
    }

    @JvmStatic
    @SideOnly(Side.CLIENT)
    fun renderFootprints(partialTicks: Float) {
        FootprintRenderer.renderFootprints(ClientProxyCore.mc.player, partialTicks)
        MinecraftForge.EVENT_BUS.post(ClientProxyCore.EventSpecialRender(partialTicks))
        BubbleRenderer.renderBubbles(ClientProxyCore.mc.player, partialTicks)
    }

    @JvmStatic
    @SideOnly(Side.CLIENT)
    fun getCameraZoom(previous: Double): Double {
        if (ConfigManagerCore.disableVehicleCameraChanges) {
            return previous
        }

        val player: EntityPlayer = Minecraft.getMinecraft().player
        if (player.getRidingEntity() != null && player.getRidingEntity() is ICameraZoomEntity) {
            return (player.getRidingEntity() as ICameraZoomEntity).getCameraZoom().toDouble()
        }

        return previous
    }

    @JvmStatic
    fun armorDamageHook(entity: EntityLivingBase?): Double {
        if (entity is EntityPlayer && GalacticraftCore.Companion.isPlanetsLoaded) {
            val stats: GCPlayerStats? = GCPlayerStats.get(entity)
            if (stats != null) {
                val shield: ItemStack? = stats.getShieldControllerInSlot()
                if (shield != null && !shield.isEmpty() && shield.getItem() === VenusItems.basicItem && shield.getItemDamage() == 0) {
                    return 0.0
                }
            }
        }
        return 1.0
    }

    @JvmStatic
    fun setCurrentBuffer(buffer: BufferBuilder?) {
        renderBuilder.set(buffer)
    }

    @JvmStatic
    fun isGrating(orig: Boolean, b: Block?): Boolean {
        return orig || (b is BlockGrating && b !== GCBlocks.grating && MinecraftForgeClient.getRenderLayer() == BlockRenderLayer.TRANSLUCENT)
    }

    @JvmStatic
    fun armorDamageHookF(entity: EntityLivingBase?): Float {
        return armorDamageHook(entity).toFloat()
    }

    @JvmStatic
    @SideOnly(Side.CLIENT)
    fun addRainParticles(result: Int, rendererUpdateCount: Int, f: Float): Int {
        val mc: Minecraft = Minecraft.getMinecraft()
        val world: World = mc.world
        if (result == 0 || world.provider !is IWeatherProvider) {
            // Either no rain or it's a vanilla dimension
            return result
        }
        val moddedProvider: IWeatherProvider = (world.provider as IWeatherProvider)

        random.setSeed(rendererUpdateCount.toLong() * 312987231L)
        val entity: Entity? = mc.getRenderViewEntity()
        val blockpos: BlockPos = BlockPos(entity)
        val i: Int = 10
        var x: Double = 0.0
        var y: Double = 0.0
        var z: Double = 0.0
        var xx: Double = 0.0
        var yy: Double = 0.0
        var zz: Double = 0.0
        var j: Int = 0
        var k: Int = (100.0f * f * f).toInt()

        if (mc.gameSettings.particleSetting == 1) {
            k = k shr 1
        } else if (mc.gameSettings.particleSetting == 2) {
            k = 0
        }

        for (l: Int in 0..<k) {
            val blockpos1: BlockPos = world.getPrecipitationHeight(
                blockpos.add(
                    random.nextInt(i) - random.nextInt(i),
                    0,
                    random.nextInt(i) - random.nextInt(i)
                )
            )
            val biome: Biome = world.getBiome(blockpos1)

            if (blockpos1.getY() <= blockpos.getY() + i && blockpos1.getY() >= blockpos.getY() - i) {
                val xd: Double = random.nextDouble()
                val zd: Double = random.nextDouble()
                val blockpos2: BlockPos = blockpos1.down()
                val iblockstate: IBlockState = world.getBlockState(blockpos2)
                val axisalignedbb: AxisAlignedBB = iblockstate.getBoundingBox(world, blockpos2)

                if (iblockstate.getMaterial() !== Material.LAVA && iblockstate.getBlock() !== Blocks.MAGMA) {
                    if (iblockstate.getMaterial() !== Material.AIR) {
                        ++j

                        x = blockpos2.getX() + xd
                        y = blockpos2.getY() + 0.1 + axisalignedbb.maxY
                        z = blockpos2.getZ() + zd
                        if (random.nextInt(j) == 0) {
                            xx = x
                            yy = y - 1.0
                            zz = z
                        }

                        mc.effectRenderer.addEffect(moddedProvider.getParticle(mc.world, x, y, z))
                    }
                } else {
                    mc.world.spawnParticle(
                        EnumParticleTypes.SMOKE_NORMAL,
                        blockpos1.getX() + xd,
                        blockpos1.getY() + 0.1 - axisalignedbb.minY,
                        blockpos1.getZ() + zd,
                        0.0,
                        0.0,
                        0.0,
                        *IntArray(0)
                    )
                }
            }
        }

        if (j > 0 && random.nextInt(moddedProvider.getSoundInterval(f)) < rainSoundCounter++) {
            rainSoundCounter = 0

            moddedProvider.weatherSounds(j, mc, world, blockpos, xx, yy, zz, random)
        }


        // Bypass vanilla code after returning from this
        return 0
    }
}
