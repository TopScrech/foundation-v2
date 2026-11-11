package micdoodle8.mods.galacticraft.core.client.render.entities

import micdoodle8.mods.galacticraft.api.entity.ICameraZoomEntity
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.blocks.BlockMulti
import micdoodle8.mods.galacticraft.core.client.model.ModelPlayerGC
import micdoodle8.mods.galacticraft.core.client.render.entities.layer.*
import micdoodle8.mods.galacticraft.core.tile.TileEntityMulti
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.mars.blocks.BlockMachineMars
import micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.client.model.ModelPlayer
import net.minecraft.client.renderer.entity.RenderLivingBase
import net.minecraft.client.renderer.entity.RenderPlayer
import net.minecraft.client.renderer.entity.layers.LayerArmorBase
import net.minecraft.client.renderer.entity.layers.LayerCustomHead
import net.minecraft.client.renderer.entity.layers.LayerHeldItem
import net.minecraft.client.renderer.entity.layers.LayerRenderer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.player.PlayerEvent
import org.lwjgl.opengl.GL11
import java.lang.reflect.Field

/**
 * This renders the thermal armor (unless RenderPlayerAPI is installed).
 * The thermal armor render is done after the corresponding body part of the player is drawn.
 * This ALSO patches RenderPlayer so that it uses ModelPlayerGC in place of ModelPlayer to draw the player.
 *
 *
 * Finally, this also adds a hook into applyRotations so as to fire a RotatePlayerEvent - used by the Cryogenic Chamber
 *
 * @author User
 */
open class RenderPlayerGC : RenderPlayer {
    @JvmOverloads
    constructor(smallArms: Boolean = false) : super(
        Minecraft.getInstance().getRenderManager(),
        smallArms
    ) {
        this.mainModel = ModelPlayerGC(0.0f, smallArms)
        this.addGCLayers()
    }

    private fun addGCLayers() {
        var f1: Field? = null
        val f2: Field? = null
        val f3: Field? = null
        try {
            f1 =
                LayerArmorBase::class.java.getDeclaredField(if (GCCoreUtil.isDeobfuscated()) "renderer" else "field_177190_a")
            f1.setAccessible(true)
        } catch (ignore: Exception) {
        }
        // The following code removes the vanilla skull and item layer renderers and replaces them with the Galacticraft ones
        // Also updates all armor layers (including layers added by other mods) to reflect GC model limb positioning
        var itemLayerIndex: Int = -1
        var skullLayerIndex: Int = -1
        for (i: Int in this.layerRenderers.indices) {
            val layer: LayerRenderer<*>? = this.layerRenderers.get(i)
            if (layer is LayerHeldItem) {
                itemLayerIndex = i
            } else if (layer is LayerArmorBase<*>) {
                if (f1 != null) {
                    try {
                        f1.set(layer, this)
                    } catch (ignore: Exception) {
                    }
                }
            } else if (layer is LayerCustomHead) {
                skullLayerIndex = i
            }
        }
        if (skullLayerIndex >= 0) {
            this.setLayer<EntityLivingBase?, LayerCustomHead?>(
                skullLayerIndex,
                LayerCustomHead(this.getMainModel().bipedHead)
            )
        }
        if (itemLayerIndex >= 0 && !ConfigManagerCore.disableVehicleCameraChanges) {
            this.setLayer<AbstractClientPlayer, LayerHeldItemGC>(itemLayerIndex, LayerHeldItemGC(this))
        }

        this.addLayer<AbstractClientPlayer?, LayerOxygenTanks?>(LayerOxygenTanks(this))
        this.addLayer<AbstractClientPlayer?, LayerOxygenGear?>(LayerOxygenGear(this))
        this.addLayer<AbstractClientPlayer?, LayerOxygenMask?>(LayerOxygenMask(this))
        this.addLayer<AbstractClientPlayer?, LayerOxygenParachute?>(LayerOxygenParachute(this))
        this.addLayer<AbstractClientPlayer?, LayerFrequencyModule?>(LayerFrequencyModule(this))

        if (GalacticraftCore.isPlanetsLoaded) {
            this.addLayer<EntityLivingBase?, LayerThermalPadding?>(LayerThermalPadding(this))

            thermalPaddingTexture0 = ResourceLocation.fromNamespaceAndPath("galacticraftplanets", "textures/misc/thermal_padding_0.png")
            thermalPaddingTexture1 = ResourceLocation.fromNamespaceAndPath("galacticraftplanets", "textures/misc/thermal_padding_1.png")
            thermalPaddingTexture1_T2 =
                ResourceLocation.fromNamespaceAndPath("galacticraftplanets", "textures/misc/thermal_padding_t2_1.png")
            heatShieldTexture = ResourceLocation.fromNamespaceAndPath("galacticraftplanets", "textures/misc/shield.png")

            this.addLayer<EntityLivingBase?, LayerShield?>(LayerShield(this))
        }
    }

    private fun <V : EntityLivingBase?, U : LayerRenderer<V>?> setLayer(index: Int, layer: U?) {
        this.layerRenderers.set(index, layer as LayerRenderer<AbstractClientPlayer>?)
    }

    constructor(old: RenderPlayer, smallArms: Boolean) : super(
        Minecraft.getInstance().getRenderManager(), smallArms
    ) {
        this.mainModel = ModelPlayerGC(0.0f, smallArms)


        //Preserve any layers added by other mods, for example WearableBackpacks
        try {
            val f: Field = old.javaClass.getSuperclass()
                .getDeclaredField(if (GCCoreUtil.isDeobfuscated()) "layerRenderers" else "field_177097_h")
            f.setAccessible(true)
            val layers: MutableList<LayerRenderer<*>> = f.get(old) as MutableList<LayerRenderer<*>>
            if (layers.size > 0) {
                for (oldLayer: LayerRenderer<*> in layers) {
                    var oldLayer: LayerRenderer<*> = oldLayer
                    if (this.hasNoLayer(oldLayer.javaClass)) {
                        var newInstance: LayerRenderer<*>? = null
                        try {
                            newInstance =
                                oldLayer.javaClass.getConstructor(RenderLivingBase::class.java).newInstance(this)
                        } catch (ignore: Exception) {
                        }
                        if (newInstance == null) {
                            try {
                                newInstance =
                                    oldLayer.javaClass.getConstructor(RenderPlayer::class.java).newInstance(this)
                            } catch (ignore: Exception) {
                            }
                        }
                        if (newInstance == null) {
                            try {
                                newInstance = oldLayer.javaClass.getConstructor(
                                    Boolean::class.javaPrimitiveType,
                                    ModelPlayer::class.java
                                ).newInstance(smallArms, this.mainModel)
                            } catch (ignore: Exception) {
                            }
                        }
                        if (newInstance != null) {
                            oldLayer = newInstance
                        }
                        this.addLayer(oldLayer as LayerRenderer<net.minecraft.client.entity.AbstractClientPlayer>)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        this.addGCLayers()
    }

    private fun hasNoLayer(test: Class<out LayerRenderer<*>?>): Boolean {
        for (oldLayer: LayerRenderer<*> in this.layerRenderers) {
            if (oldLayer.javaClass == test) return false
        }
        return true
    }

    override fun preRenderCallback(entitylivingbaseIn: AbstractClientPlayer, partialTickTime: Float) {
        super.preRenderCallback(entitylivingbaseIn, partialTickTime)

        if (entitylivingbaseIn.isAlive() && entitylivingbaseIn.isPlayerSleeping()) {
            val event: RotatePlayerEvent = RotatePlayerEvent(entitylivingbaseIn)
            MinecraftForge.EVENT_BUS.post(event)

            if (!event.vanillaOverride) {
                super.preRenderCallback(entitylivingbaseIn, partialTickTime)
            } else if (event.shouldRotate == null || (event.shouldRotate)!!) {
                entitylivingbaseIn.yHeadRot = 0f
                entitylivingbaseIn.yHeadRotO = 0f
                GL11.glTranslatef(0.0f, 0.3f, 0.0f)
            }
        }
    }

    override fun applyRotations(abstractClientPlayer: AbstractClientPlayer, par2: Float, par3: Float, par4: Float) {
        if (abstractClientPlayer.isAlive() && abstractClientPlayer.isPlayerSleeping()) {
            val event: RotatePlayerEvent = RotatePlayerEvent(abstractClientPlayer)
            MinecraftForge.EVENT_BUS.post(event)

            if (!event.vanillaOverride) {
                super.applyRotations(abstractClientPlayer, par2, par3, par4)
            } else if (event.shouldRotate == null) {
                GL11.glRotatef(abstractClientPlayer.getBedOrientationInDegrees(), 0.0f, 1.0f, 0.0f)
            } else if ((event.shouldRotate)!!) {
                var rotation: Float = 0.0f

                if (abstractClientPlayer.bedLocation != null) {
                    var bed: IBlockState = abstractClientPlayer.level().getBlockState(abstractClientPlayer.bedLocation)

                    if (bed.getBlock().isBed(
                            bed,
                            abstractClientPlayer.level(),
                            abstractClientPlayer.bedLocation,
                            abstractClientPlayer
                        )
                    ) {
                        if (bed.getBlock() === GCBlocks.fakeBlock && bed.getValue(
                                BlockMulti.MULTI_TYPE
                            ) == BlockMulti.EnumBlockMultiType.CRYO_CHAMBER
                        ) {
                            val tile: TileEntity? =
                                event.getEntityPlayer().world.getBlockEntity(abstractClientPlayer.bedLocation)
                            if (tile is TileEntityMulti) {
                                bed = event.getEntityPlayer().world.getBlockState(tile.mainBlockPosition)
                            }
                        }

                        if (bed.getBlock() === MarsBlocks.machine && bed.getValue(
                                BlockMachineMars.TYPE
                            ) == BlockMachineMars.EnumMachineType.CRYOGENIC_CHAMBER
                        ) {
                            when (bed.getValue(BlockMachineMars.FACING)) {
                                EnumFacing.NORTH -> rotation = 0.0f
                                EnumFacing.EAST -> rotation = 270.0f
                                EnumFacing.SOUTH -> rotation = 180.0f
                                EnumFacing.WEST -> rotation = 90.0f
                            
else -> {}
}
                        }
                    }
                }

                GL11.glRotatef(rotation, 0.0f, 1.0f, 0.0f)
            }
        } else {
            if (Minecraft.getInstance().gameSettings.thirdPersonView != 0) {
                val player: EntityPlayer = abstractClientPlayer as EntityPlayer

                if (player.getVehicle() is ICameraZoomEntity) {
                    val rocket: Entity? = player.getVehicle()
                    val rotateOffset: Float = (rocket as ICameraZoomEntity).getRotateOffset()
                    if (rotateOffset > -10f) {
                        GL11.glTranslatef(0f, -rotateOffset, 0f)
                        val anglePitch: Float = rocket.prevRotationPitch
                        val angleYaw: Float = rocket.prevRotationYaw
                        GL11.glRotatef(-angleYaw, 0.0f, 1.0f, 0.0f)
                        GL11.glRotatef(anglePitch, 0.0f, 0.0f, 1.0f)
                        GL11.glTranslatef(0f, rotateOffset, 0f)
                    }
                }
            }
            super.applyRotations(abstractClientPlayer, par2, par3, par4)
        }
    }

    open class RotatePlayerEvent constructor(player: AbstractClientPlayer?) : PlayerEvent(player) {
        @JvmField
        var shouldRotate: Boolean? = null

        @JvmField
        var vanillaOverride: Boolean = false
    }

    companion object {
        @JvmField
        var thermalPaddingTexture0: ResourceLocation? = null

        @JvmField
        var thermalPaddingTexture1: ResourceLocation? = null

        @JvmField
        var thermalPaddingTexture1_T2: ResourceLocation? = null

        @JvmField
        var heatShieldTexture: ResourceLocation? = null

        @JvmField
        var flagThermalOverride: Boolean = false
        private val isSmartRenderLoaded: Boolean? = null
    }
}
