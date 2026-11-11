package galaxyspace.core.prefab.entities

import galaxyspace.core.prefab.inventory.InventoryAstroWolf
import galaxyspace.GalaxySpace
import galaxyspace.core.network.packet.GSPacketSimple
import galaxyspace.core.network.packet.GSPacketSimple.GSEnumSimplePacket
import galaxyspace.core.prefab.entities.EntityAstroWolf
import micdoodle8.mods.galacticraft.api.entity.IEntityBreathable
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank
import micdoodle8.mods.galacticraft.core.util.DamageSourceGC
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.OxygenUtil
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.passive.EntityWolf
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.network.datasync.DataParameter
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.network.datasync.EntityDataManager
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.world.level.Level as World
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.LogicalSide as Side
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import kotlin.math.abs

open class EntityAstroWolf constructor(worldIn: World) : EntityWolf(worldIn), IEntityBreathable {
    @JvmField
    var wolfInventory: InventoryAstroWolf = InventoryAstroWolf(this)
    private val isHelmet = false

    @JvmField
    var needSync: Boolean = true

    override fun applyEntityAttributes() {
        super.applyEntityAttributes()
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896)

        if (this.isTamed()) {
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(35.0)
        } else {
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0)
        }
    }

    override fun onEntityUpdate() {
        super.onEntityUpdate()

        if (!world.isRemote) {
            if (this.world.provider is IGalacticraftWorldProvider) {
                if (abs((this.world.provider as IGalacticraftWorldProvider).getThermalLevelModifier()) >= 1.0f) if (this.wolfInventory.getItem(
                        1
                    ).isEmpty() && this.ticksExisted % 20 == 0
                ) foundation.damage.FoundationDamageTypes.hurt(this, DamageSourceGC.thermal, 0.2f)


                if (!OxygenUtil.isAABBInBreathableAirBlock(this) && this.ticksExisted % 40 == 0) {
                    if (this.wolfInventory.getItem(0)
                            .getItem() === GCItems.oxMask
                    ) if (this.wolfInventory.getItem(2)
                            .getItem() is ItemOxygenTank && ItemOxygenTank.used(this.wolfInventory.getItem(2)) < ItemOxygenTank.capacityOf(this.wolfInventory.getItem(2))
                    ) ItemOxygenTank.setUsed(this.wolfInventory.getItem(2), ItemOxygenTank.used(this.wolfInventory.getItem(2)) + 1)
                }
            }
        }

        if (needSync) {
            if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
                (GalaxySpace.packetPipeline)!!.sendToServer(
                    GSPacketSimple(
                        GSEnumSimplePacket.S_UPDATE_WOLF_INV,
                        GCCoreUtil.getDimensionID(world),
                        *arrayOf<Any>(getEntityId())
                    )
                )
            }
        }
    }

    override fun processInteract(player: EntityPlayer, hand: EnumHand): Boolean {
        val itemstack: ItemStack = player.getItemInHand(hand)

        if (this.isTamed()) {
            if (player.isShiftKeyDown()) {
                if (this.isOwner(player)) {
                    player.openGui(GalaxySpace.MODID, 1005, this.world, this.getEntityId(), 0, 0)
                }
                return true
            }

            if (!itemstack.isEmpty()) {
                val nutrition = foundation.item.FoundationFoods.wolfHealingNutrition(itemstack)
                if (nutrition != null && this.getHealth() < 35.0f) {
                    if (!player.hasInfiniteMaterials()) itemstack.shrink(1)
                    this.heal(nutrition.toFloat())
                    return true
                }
            }
        }
        return super.processInteract(player, hand)
    }

    @OnlyIn(Dist.CLIENT)
    open fun setWolfInventory(inv: InventoryAstroWolf) {
        this.wolfInventory = inv
    }

    override fun canBreath(): Boolean {
        return !this.wolfInventory.getItem(0).isEmpty() && !this.wolfInventory.getItem(2)
            .isEmpty() && ItemOxygenTank.used(this.wolfInventory.getItem(2)) < ItemOxygenTank.capacityOf(this.wolfInventory.getItem(2))
    }

    override fun writeEntityToNBT(nbt: NBTTagCompound) {
        super.writeEntityToNBT(nbt)
        ItemStackHelper.saveAllItems(nbt, this.wolfInventory.getInventory())
    }

    override fun readEntityFromNBT(nbt: NBTTagCompound) {
        super.readEntityFromNBT(nbt)
        ItemStackHelper.loadAllItems(nbt, this.wolfInventory.getInventory())
    }

    override fun onDeath(p_70645_1_: DamageSource) {
        super.onDeath(p_70645_1_)

        if (!this.world.isRemote) {
            this.entityDropItem(this.wolfInventory.getItem(0), 0.5f)
            this.entityDropItem(this.wolfInventory.getItem(1), 0.5f)
            this.entityDropItem(this.wolfInventory.getItem(2), 0.5f)
        }
    }

    override fun setTamed(tamed: Boolean) {
        super.setTamed(tamed)

        if (tamed) {
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(35.0)
        } else {
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0)
        }

        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0)
    }

    companion object {
        private val DATA_HEALTH_ID: DataParameter<Float> =
            EntityDataManager.createKey<Float>(EntityWolf::class.java, DataSerializers.FLOAT)
    }
}
