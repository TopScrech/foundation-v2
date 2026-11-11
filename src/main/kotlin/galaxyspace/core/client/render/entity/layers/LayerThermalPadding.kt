package galaxyspace.core.client.render.entity.layers

import galaxyspace.GalaxySpace
import galaxyspace.core.GSItems
import galaxyspace.core.util.GSConstants
import galaxyspace.systems.SolarSystem.planets.overworld.items.armor.ItemThermalPaddingBase
import micdoodle8.mods.galacticraft.api.item.IItemThermal
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.client.model.ModelPlayerGC
import micdoodle8.mods.galacticraft.core.client.render.entities.RenderPlayerGC
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerHandler
import micdoodle8.mods.galacticraft.core.wrappers.PlayerGearData
import net.minecraft.client.Minecraft
import net.minecraft.client.model.ModelBiped
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderPlayer
import net.minecraft.client.renderer.entity.layers.LayerArmorBase
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.EquipmentSlot as EntityEquipmentSlot
import net.minecraft.world.item.ItemStack
import net.minecraft.resources.Identifier as ResourceLocation
import org.lwjgl.opengl.GL11
import kotlin.math.sin

open class LayerThermalPadding constructor(private val renderer: RenderPlayer) :
    LayerArmorBase<ModelBiped>(renderer) {
    private val texture_t3 = ResourceLocation.fromNamespaceAndPath(GalaxySpace.MODID, "textures/model/armor/thermal_padding_t3_1.png")
    private val texture_t4 = ResourceLocation.fromNamespaceAndPath(GalaxySpace.MODID, "textures/model/armor/thermal_padding_t4_1.png")

    override fun shouldCombineTextures(): Boolean {
        return false
    }

    override fun setModelSlotVisible(model: ModelBiped, slotIn: EntityEquipmentSlot) {
        model.setVisible(false)

        when (slotIn) {
            EntityEquipmentSlot.HEAD -> {
                model.bipedRightLeg.showModel = true
                model.bipedLeftLeg.showModel = true
            }

            EntityEquipmentSlot.CHEST -> {
                model.bipedRightLeg.showModel = true
                model.bipedLeftLeg.showModel = true
            }

            EntityEquipmentSlot.LEGS -> {
                model.bipedBody.showModel = true
                model.bipedRightArm.showModel = true
                model.bipedLeftArm.showModel = true
            }

            EntityEquipmentSlot.FEET -> {
                model.bipedHead.showModel = true
                model.bipedHeadwear.showModel = true
            }

            else -> {}
        }
    }

    open fun getItemStackFromSlot(living: EntityLivingBase?, slotIn: EntityEquipmentSlot): ItemStack {
        val gearData: PlayerGearData? = (GalacticraftCore.proxy)!!.getGearData((living as EntityPlayer?)!!)

        if (gearData != null) {
            val padding: Int = gearData.getThermalPadding(slotIn.getSlotIndex() - 1)
            if (padding != GCPlayerHandler.GEAR_NOT_PRESENT) {
                when (padding) {
                    GSConstants.GEAR_ID_THERMAL_PADDING_T3_HELMET, GSConstants.GEAR_ID_THERMAL_PADDING_T3_CHESTPLATE, GSConstants.GEAR_ID_THERMAL_PADDING_T3_LEGGINGS, GSConstants.GEAR_ID_THERMAL_PADDING_T3_BOOTS -> return ItemStack(
                        GSItems.THERMAL_PADDING_3,
                        1,
                        slotIn.getSlotIndex() - 1
                    )

                    GSConstants.GEAR_ID_THERMAL_PADDING_T4_HELMET, GSConstants.GEAR_ID_THERMAL_PADDING_T4_CHESTPLATE, GSConstants.GEAR_ID_THERMAL_PADDING_T4_LEGGINGS, GSConstants.GEAR_ID_THERMAL_PADDING_T4_BOOTS -> return ItemStack(
                        GSItems.THERMAL_PADDING_4,
                        1,
                        slotIn.getSlotIndex() - 1
                    )

                    else -> {}
                }
            }
        }

        return ItemStack.EMPTY //This null is OK, it's used only as flag by calling code in this same class
    }

    override fun doRenderLayer(
        entitylivingbaseIn: EntityLivingBase,
        limbSwing: Float,
        limbSwingAmount: Float,
        partialTicks: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float,
        scale: Float
    ) {
        this.renderArmorLayer(
            entitylivingbaseIn,
            limbSwing,
            limbSwingAmount,
            partialTicks,
            ageInTicks,
            netHeadYaw,
            headPitch,
            scale,
            EntityEquipmentSlot.CHEST
        )
        this.renderArmorLayer(
            entitylivingbaseIn,
            limbSwing,
            limbSwingAmount,
            partialTicks,
            ageInTicks,
            netHeadYaw,
            headPitch,
            scale,
            EntityEquipmentSlot.LEGS
        )
        this.renderArmorLayer(
            entitylivingbaseIn,
            limbSwing,
            limbSwingAmount,
            partialTicks,
            ageInTicks,
            netHeadYaw,
            headPitch,
            scale,
            EntityEquipmentSlot.FEET
        )
        this.renderArmorLayer(
            entitylivingbaseIn,
            limbSwing,
            limbSwingAmount,
            partialTicks,
            ageInTicks,
            netHeadYaw,
            headPitch,
            scale,
            EntityEquipmentSlot.HEAD
        )
    }

    private fun renderArmorLayer(
        entityLivingBaseIn: EntityLivingBase,
        limbSwing: Float,
        limbSwingAmount: Float,
        partialTicks: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float,
        scale: Float,
        slotIn: EntityEquipmentSlot
    ) {
        val itemstack: ItemStack = this.getItemStackFromSlot(entityLivingBaseIn, slotIn)


        if (!itemstack.isEmpty()) {
            val model: ModelBiped = this.getModelFromSlot(slotIn)
            model.setModelAttributes(this.renderer.getMainModel())
            model.setLivingAnimations(entityLivingBaseIn, limbSwing, limbSwingAmount, partialTicks)
            this.setModelSlotVisible(model, slotIn)
            if (itemstack.getItem() is ItemThermalPaddingBase) {
                val item: IItemThermal = itemstack.getItem() as IItemThermal
                when (item.getThermalStrength()) {
                    3 -> this.renderer.bindTexture(this.texture_t3)
                    4 -> this.renderer.bindTexture(this.texture_t4)
                }
                //GalaxySpace.debug(itemstack.getUnlocalizedName() + "");
            }

            model.render(entityLivingBaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale)

            // Start alpha render
            GlStateManager.disableLighting()
            Minecraft.getInstance().renderEngine.bindTexture(RenderPlayerGC.thermalPaddingTexture0)
            GlStateManager.enableAlpha()
            GlStateManager.enableBlend()
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            val time: Float = entityLivingBaseIn.tickCount / 10.0f
            val sTime: Float = sin(time.toDouble()).toFloat() * 0.5f + 0.5f

            var r: Float = 0.2f * sTime
            var g: Float = 1.0f * sTime
            var b: Float = 0.2f * sTime

            if (entityLivingBaseIn.level().provider is IGalacticraftWorldProvider) {
                val modifier: Float =
                    (entityLivingBaseIn.level().provider as IGalacticraftWorldProvider).getThermalLevelModifier()

                if (modifier > 0) {
                    b = g
                    g = r
                } else if (modifier < 0) {
                    r = g
                    g = b
                }
            }

            GlStateManager.color(r, g, b, 0.4f * sTime)
            model.render(entityLivingBaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale)
            GlStateManager.color(1f, 1f, 1f, 1f)
            GlStateManager.disableBlend()
            GlStateManager.enableAlpha()
            GlStateManager.enableLighting()
        }
    }

    override fun initArmor() {
        this.modelLeggings = ModelPlayerGC(0.55f, false) //Head inside Oxygen Mask
        this.modelArmor = ModelPlayerGC(0.05f, false) //Chest and limbs close to skin
    }

    override fun getModelFromSlot(slotIn: EntityEquipmentSlot): ModelBiped {
        return if (slotIn == EntityEquipmentSlot.FEET) this.modelLeggings else this.modelArmor //FEET is intended here, actually picks up the helmet (yes really)
    }
}
