package galaxyspace.core.mixins

import asmodeuscore.api.dimension.IAdvancedSpace
import org.spongepowered.asm.mixin.Overwrite
import micdoodle8.mods.galacticraft.api.item.IItemThermal
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerHandler
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.util.CompatibilityManager
import micdoodle8.mods.galacticraft.core.util.DamageSourceGC
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.OxygenUtil
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.item.ItemStack
import net.minecraft.world.effect.MobEffectInstance as PotionEffect
import net.minecraft.util.Mth as MathHelper
import org.spongepowered.asm.mixin.Mixin
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Mixin(value = [GCPlayerHandler::class], remap = false)
open class MixinGCPlayerHandler {
    /**
     * @author ViTold
     * @reason
     */
    @Overwrite
    protected open fun checkThermalStatus(player: EntityPlayerMP, playerStats: GCPlayerStats) {
        val gh: GCPlayerHandler = this as Any as GCPlayerHandler

        if (player.level().provider is IGalacticraftWorldProvider && !player.capabilities.isCreativeMode && !CompatibilityManager.isAndroid(
                player
            )
        ) {
            val thermalPaddingHelm: ItemStack = (playerStats.getExtendedInventory())!!.getStackInSlot(6)
            val thermalPaddingChestplate: ItemStack = (playerStats.getExtendedInventory())!!.getStackInSlot(7)
            val thermalPaddingLeggings: ItemStack = (playerStats.getExtendedInventory())!!.getStackInSlot(8)
            val thermalPaddingBoots: ItemStack = (playerStats.getExtendedInventory())!!.getStackInSlot(9)
            var lowestThermalStrength: Float = 0.0f
            if (!thermalPaddingHelm.isEmpty() && !thermalPaddingChestplate.isEmpty() && !thermalPaddingLeggings.isEmpty() && !thermalPaddingBoots.isEmpty()) {
                if (thermalPaddingHelm.getItem() is IItemThermal) {
                    lowestThermalStrength += (thermalPaddingHelm.getItem() as IItemThermal).getThermalStrength()
                        .toFloat()
                }
                if (thermalPaddingChestplate.getItem() is IItemThermal) {
                    lowestThermalStrength += (thermalPaddingChestplate.getItem() as IItemThermal).getThermalStrength()
                        .toFloat()
                }
                if (thermalPaddingLeggings.getItem() is IItemThermal) {
                    lowestThermalStrength += (thermalPaddingLeggings.getItem() as IItemThermal).getThermalStrength()
                        .toFloat()
                }
                if (thermalPaddingBoots.getItem() is IItemThermal) {
                    lowestThermalStrength += (thermalPaddingBoots.getItem() as IItemThermal).getThermalStrength()
                        .toFloat()
                }
                lowestThermalStrength /= 4.0f
                lowestThermalStrength = abs(lowestThermalStrength) //It shouldn't be negative, but just in case!
            }
            var provider: IGalacticraftWorldProvider = player.level().provider as IGalacticraftWorldProvider
            var thermalLevelMod: Float = provider.getThermalLevelModifier()

            if (player.level().provider is IAdvancedSpace) {
                provider = player.level().provider as IAdvancedSpace
                thermalLevelMod = provider.getThermalLevelModifier(player)
            }

            var absThermalLevelMod: Float = abs(thermalLevelMod)

            if (absThermalLevelMod > 0.0) {
                val thermalLevelCooldownBase: Int = abs(MathHelper.floor(200 / thermalLevelMod))
                var normaliseCooldown: Int = MathHelper.floor(150 / lowestThermalStrength)
                var thermalLevelTickCooldown: Int = thermalLevelCooldownBase
                if (thermalLevelTickCooldown < 1) {
                    thermalLevelTickCooldown = 1 //Prevent divide by zero errors
                }

                if (!thermalPaddingHelm.isEmpty() && !thermalPaddingChestplate.isEmpty() && !thermalPaddingLeggings.isEmpty() && !thermalPaddingBoots.isEmpty()) {
                    //If the thermal strength exceeds the dimension's thermal level mod, it can't improve the normalise
                    //This factor of 1.5F is chosen so that a combination of Tier 1 and Tier 2 thermal isn't enough to normalise on Venus (three Tier 2, one Tier 1 stays roughly constant)
                    val relativeFactor: Float = max(1.0f, absThermalLevelMod / lowestThermalStrength) / 1.5f
                    normaliseCooldown = MathHelper.floor(normaliseCooldown / absThermalLevelMod * relativeFactor)
                    if (normaliseCooldown < 1) {
                        normaliseCooldown = 1 //Prevent divide by zero errors
                    }
                    // Player is wearing all required thermal padding items
                    if ((player.tickCount - 1) % normaliseCooldown == 0) {
                        gh.normaliseThermalLevel(player, playerStats, 1)
                    }
                    thermalLevelMod /= max(1.0f, lowestThermalStrength / 2.0f)
                    absThermalLevelMod = abs(thermalLevelMod)
                }

                if (OxygenUtil.isAABBInBreathableAirBlock(player, true)) {
                    playerStats.setThermalLevelNormalising(true)
                    gh.normaliseThermalLevel(player, playerStats, 1)
                    // If player is in ambient thermal area, slowly reset to normal
                    return
                }

                // For each piece of thermal equipment being used, slow down the the harmful thermal change slightly
                if (!thermalPaddingHelm.isEmpty()) {
                    thermalLevelTickCooldown += thermalLevelCooldownBase
                }
                if (!thermalPaddingChestplate.isEmpty()) {
                    thermalLevelTickCooldown += thermalLevelCooldownBase
                }
                if (!thermalPaddingLeggings.isEmpty()) {
                    thermalLevelTickCooldown += thermalLevelCooldownBase
                }
                if (!thermalPaddingBoots.isEmpty()) {
                    thermalLevelTickCooldown += thermalLevelCooldownBase
                }

                // Instead of increasing/decreasing the thermal level by a large amount every ~200 ticks, increase/decrease
                //      by a small amount each time (still the same average increase/decrease)
                var thermalLevelTickCooldownSingle: Int =
                    MathHelper.floor(thermalLevelTickCooldown / absThermalLevelMod)
                if (thermalLevelTickCooldownSingle < 1) {
                    thermalLevelTickCooldownSingle = 1 //Prevent divide by zero errors
                }

                if ((player.tickCount - 1) % thermalLevelTickCooldownSingle == 0) {
                    val last: Int = playerStats.getThermalLevel()
                    playerStats.setThermalLevel(min(max(last + (if (thermalLevelMod < 0) -1 else 1), -22), 22))

                    if (playerStats.getThermalLevel() != last) {
                        //handler.sendThermalLevelPacket(player, playerStats);
                        (GalacticraftCore.packetPipeline)!!.sendTo(
                            PacketSimple(
                                PacketSimple.EnumSimplePacket.C_UPDATE_THERMAL_LEVEL,
                                GCCoreUtil.getDimensionID(player.level()),
                                arrayOf<Any>(playerStats.getThermalLevel(), playerStats.isThermalLevelNormalising())
                            ), player
                        )
                    }
                }

                // If the normalisation is outpacing the freeze/overheat
                playerStats.setThermalLevelNormalising(thermalLevelTickCooldownSingle > normaliseCooldown && !thermalPaddingHelm.isEmpty() && !thermalPaddingChestplate.isEmpty() && !thermalPaddingLeggings.isEmpty() && !thermalPaddingBoots.isEmpty())

                if (!playerStats.isThermalLevelNormalising()) {
                    if ((player.tickCount - 1) % thermalLevelTickCooldown == 0) {
                        if (abs(playerStats.getThermalLevel()) >= 22) {
                            foundation.damage.FoundationDamageTypes.hurt(player, DamageSourceGC.thermal, 1.5f)
                        }
                    }

                    if (playerStats.getThermalLevel() < -15) {
                        player.addEffect(PotionEffect(MobEffects.SLOWNESS, 5, 2, true, true))
                    }

                    if (playerStats.getThermalLevel() > 15) {
                        player.addEffect(PotionEffect(MobEffects.NAUSEA, 5, 2, true, true))
                    }
                } else  //Normalise thermal level if on Space Station or non-modifier planet
                {
                    playerStats.setThermalLevelNormalising(true)
                    gh.normaliseThermalLevel(player, playerStats, 2)
                }
            } else  //Normalise thermal level if on Overworld or any non-GC dimension
            {
                playerStats.setThermalLevelNormalising(true)
                gh.normaliseThermalLevel(player, playerStats, 3)
            }
        }
    }
}
