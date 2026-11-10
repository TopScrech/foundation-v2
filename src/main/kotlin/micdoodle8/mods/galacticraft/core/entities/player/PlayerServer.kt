package micdoodle8.mods.galacticraft.core.entities.player

import java.util.LinkedList

import micdoodle8.mods.galacticraft.api.entity.IIgnoreShift
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.dimension.WorldProviderMoon
import micdoodle8.mods.galacticraft.core.entities.EntityCelestialFake
import micdoodle8.mods.galacticraft.core.event.EventWakePlayer
import micdoodle8.mods.galacticraft.core.util.DamageSourceGC
import micdoodle8.mods.galacticraft.planets.asteroids.dimension.WorldProviderAsteroids
import micdoodle8.mods.galacticraft.planets.asteroids.items.ItemArmorAsteroids
import micdoodle8.mods.galacticraft.planets.mars.items.ItemArmorMars
import net.minecraft.entity.Entity
import net.minecraft.entity.MoverType
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.util.DamageSource
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraftforge.common.MinecraftForge
import java.util.*

open class PlayerServer : IPlayerServer {
    private var updatingRidden = false
    override fun updateRiddenPre(player: EntityPlayerMP?) {
        this.updatingRidden = true
    }

    override fun updateRiddenPost(player: EntityPlayerMP?) {
        this.updatingRidden = false
    }

    override fun dismountEntity(player: EntityPlayerMP?, par1Entity: Entity?): Boolean {
        return updatingRidden && (player)!!.getRidingEntity() is IIgnoreShift && ((player)!!.getRidingEntity() as IIgnoreShift).shouldIgnoreShiftExit()
    }

    override fun move(player: EntityPlayerMP?, type: MoverType?, x: Double, y: Double, z: Double) {
        // If the player is on the moon, not airbourne and not riding anything
        if ((player)!!.world.provider is WorldProviderMoon && !(player)!!.world.isRemote && (player)!!.getRidingEntity() == null) {
            GCPlayerHandler.updateFeet((player)!!, x, z)
        }
    }

    override fun wakeUpPlayer(
        player: EntityPlayerMP?,
        immediately: Boolean,
        updateWorldFlag: Boolean,
        setSpawn: Boolean
    ): Boolean {
        val c: BlockPos? = (player)!!.bedLocation

        if (c != null) {
            val event: EventWakePlayer = EventWakePlayer(player, c, immediately, updateWorldFlag, setSpawn, false)
            MinecraftForge.EVENT_BUS.post(event)

            if (event.result == null || event.result == EntityPlayer.SleepResult.OK) {
                return false
            }
        }

        return true
    }

    override fun attackEntityFrom(player: EntityPlayerMP?, par1DamageSource: DamageSource?, par2: Float): Float {
        //No damage while in Celestial Selection screen
        var par2: Float = par2
        if ((player)!!.getRidingEntity() is EntityCelestialFake) {
            return -1f
        }

        if (GalacticraftCore.isPlanetsLoaded) {
            if (par1DamageSource === DamageSource.OUT_OF_WORLD) {
                if ((player)!!.world.provider is WorldProviderAsteroids) {
                    if ((player)!!.posY > -120.0) {
                        return -1f
                    }
                    if ((player)!!.posY > -180.0) {
                        par2 /= 2f
                    }
                }
            } else if (par1DamageSource === DamageSource.FALL || par1DamageSource === DamageSourceGC.spaceshipCrash) {
                var titaniumCount: Int = 0
                if ((player)!!.inventory != null) {
                    for (armorPiece: ItemStack? in (player)!!.getArmorInventoryList()) {
                        if (armorPiece != null && armorPiece.getItem() is ItemArmorAsteroids) {
                            titaniumCount++
                        }
                    }
                }
                if (titaniumCount == 4) {
                    titaniumCount = 5
                }
                par2 *= (1 - 0.15 * titaniumCount).toFloat()
            }
        }

        return if (par2 == -1f) -0.9999f else par2
    }

    override fun knockBack(
        player: EntityPlayerMP?,
        p_70653_1_: Entity?,
        p_70653_2_: Float,
        impulseX: Double,
        impulseZ: Double
    ) {
        var deshCount: Int = 0
        if ((player)!!.inventory != null && GalacticraftCore.isPlanetsLoaded) {
            for (i: Int in 0..3) {
                for (armorPiece: ItemStack? in (player)!!.getArmorInventoryList()) {
                    if (armorPiece != null && armorPiece.getItem() is ItemArmorMars) {
                        deshCount++
                    }
                }
            }
        }

        if ((player)!!.getRNG().nextDouble() >= (player)!!.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE)
                .getAttributeValue()
        ) {
            (player)!!.isAirBorne = deshCount < 2
            val f1: Float = MathHelper.sqrt(impulseX * impulseX + impulseZ * impulseZ)
            val f2: Float = 0.4f - deshCount * 0.05f
            val d1: Double = 2.0 - deshCount * 0.15
            (player)!!.motionX /= d1
            (player)!!.motionY /= d1
            (player)!!.motionZ /= d1
            (player)!!.motionX -= f2 * impulseX / f1
            (player)!!.motionY += f2.toDouble()
            (player)!!.motionZ -= f2 * impulseZ / f1

            if ((player)!!.motionY > 0.4) {
                (player)!!.motionY = 0.4
            }
        }
    }

    override fun isSpectator(player: EntityPlayerMP?): Boolean {
        return (noClipList as Collection<Any?>).contains(player)
    }

    override fun setNoClip(player: EntityPlayerMP?, noClip: Boolean) {
        if (noClip) {
            if (!(noClipList as Collection<Any?>).contains(player)) noClipList.add((player)!!)
        } else {
            (noClipList as MutableCollection<Any?>).remove(player)
        }
    }

    companion object {
        @JvmField
        var noClipList: MutableList<EntityPlayer> = LinkedList<EntityPlayer>()
    }
}
