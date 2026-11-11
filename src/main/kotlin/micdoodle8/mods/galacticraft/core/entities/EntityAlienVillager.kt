package micdoodle8.mods.galacticraft.core.entities

import net.minecraft.network.chat.Component

import foundation.item.legacyItemValue

import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank

import foundation.item.FoundationItemVariants

import foundation.persistence.containsTag
import foundation.persistence.listOfType

import java.util.Random

import micdoodle8.mods.galacticraft.api.entity.IEntityBreathable
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerHandler
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.items.ItemBasic
import micdoodle8.mods.galacticraft.core.items.ItemCanisterGeneric
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.wrappers.PlayerGearData
import net.minecraft.entity.*
import net.minecraft.entity.ai.*
import net.minecraft.entity.effect.EntityLightningBolt
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.item.EntityXPOrb
import net.minecraft.entity.monster.EntityWitch
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.entity.monster.IMob
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.server.level.ServerPlayer as EntityPlayerMP
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.item.Items
import net.minecraft.world.effect.MobEffects
import net.minecraft.sounds.SoundEvents
import net.minecraft.inventory.InventoryBasic
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.nbt.ListTag as NBTTagList
import net.minecraft.pathfinding.PathNavigateGround
import net.minecraft.world.effect.MobEffectInstance as PotionEffect
import net.minecraft.stats.StatList
import net.minecraft.util.*
import net.minecraft.core.BlockPos

import net.minecraft.village.MerchantRecipe
import net.minecraft.village.MerchantRecipeList
import net.minecraft.village.Village
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import java.util.*

open class EntityAlienVillager constructor(worldIn: World) : EntityAgeable(worldIn), IMerchant, INpc,
    IEntityBreathable {
    private var randomTickDivider = 0
    private var isMating = false
    private var isPlaying = false
    private var villageObj: Village? = null
    private var buyingPlayer: EntityPlayer? = null
    private var buyingList: MerchantRecipeList? = null
    private var timeUntilReset = 0
    private var needsInitilization = false
    private var wealth = 0
    private var lastBuyingPlayer: String? = null
    private var isLookingForHome = false
    private val villagerInventory: InventoryBasic

    init {
        this.villagerInventory = InventoryBasic("Items", false, 8)
        this.setSize(0.6f, 1.8f)
        (this.getNavigator() as PathNavigateGround).setBreakDoors(true)
        this.tasks.addTask(0, EntityAISwimming(this))
        this.tasks.addTask(1, EntityAIAvoidEntity<EntityZombie>(this, EntityZombie::class.java, 8.0f, 0.6, 0.6))
        this.tasks.addTask(1, EntityAITradePlayerGC(this))
        this.tasks.addTask(1, EntityAILookAtTradePlayerGC(this))
        this.tasks.addTask(2, EntityAIMoveIndoors(this))
        this.tasks.addTask(3, EntityAIRestrictOpenDoor(this))
        this.tasks.addTask(4, EntityAIOpenDoor(this, true))
        this.tasks.addTask(5, EntityAIMoveTowardsRestriction(this, 0.6))
        this.tasks.addTask(9, EntityAIWatchClosest2(this, EntityPlayer::class.java, 3.0f, 1.0f))
        this.tasks.addTask(9, EntityAIWander(this, 0.6))
        this.tasks.addTask(10, EntityAIWatchClosest(this, EntityLiving::class.java, 8.0f))
        this.setCanPickUpLoot(true)
    }

    override fun onGrowingAdult() {
        super.onGrowingAdult()
    }

    override fun applyEntityAttributes() {
        super.applyEntityAttributes()
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5)
    }

    override fun updateAITasks() {
        if (--this.randomTickDivider <= 0) {
            val blockpos: BlockPos = BlockPos(this)
            this.world.getVillageCollection().addToVillagerPositionList(blockpos)
            this.randomTickDivider = 70 + this.rand.nextInt(50)
            this.villageObj = this.world.getVillageCollection().getNearestVillage(blockpos, 32)

            if (this.villageObj == null) {
                this.detachHome()
            } else {
                val blockpos1: BlockPos = this.villageObj!!.getCenter()
                this.setHomePosAndDistance(blockpos1, (this.villageObj!!.getVillageRadius().toFloat() * 1.0f).toInt())

                if (this.isLookingForHome) {
                    this.isLookingForHome = false
                    this.villageObj!!.setDefaultPlayerReputation(5)
                }
            }
        }

        if (!this.isTrading() && this.timeUntilReset > 0) {
            --this.timeUntilReset

            if (this.timeUntilReset <= 0) {
                if (this.needsInitilization) {
                    for (merchantrecipe: MerchantRecipe in this.buyingList!!) {
                        if (merchantrecipe.isRecipeDisabled()) {
                            merchantrecipe.increaseMaxTradeUses(this.rand.nextInt(6) + this.rand.nextInt(6) + 2)
                        }
                    }

                    this.populateBuyingList()
                    this.needsInitilization = false

                    if (this.villageObj != null && this.lastBuyingPlayer != null) {
                        this.world.setEntityState(this, 14.toByte())
                        this.villageObj!!.modifyPlayerReputation(this.lastBuyingPlayer, 1)
                    }
                }

                this.addEffect(PotionEffect(MobEffects.REGENERATION, 200, 0))
            }
        }

        super.updateAITasks()
    }

    override fun processInteract(player: EntityPlayer, hand: EnumHand): Boolean {
        val itemstack: ItemStack = player.inventory.getSelectedItem()
        val flag: Boolean = itemstack != null && itemstack.getItem() === Items.SPAWN_EGG

        if (!flag && this.isEntityAlive() && !this.isTrading() && !this.isChild() && !player.isShiftKeyDown()) {
            val gearData: PlayerGearData? = (GalacticraftCore.proxy)!!.getGearData(player)

            if (!this.world.isRemote && (this.buyingList == null || this.buyingList!!.size > 0)) {
                if (gearData != null && gearData.getFrequencyModule() != GCPlayerHandler.GEAR_NOT_PRESENT) {
                    this.setCustomer(player)
                    player.displayVillagerTradeGui(this)
                } else {
                    if (player is EntityPlayerMP) {
                        val playerMP: EntityPlayerMP = player
                        val stats: GCPlayerStats = (GCPlayerStats.get(playerMP))!!
                        if (stats.getChatCooldown() == 0) {
                            player.sendSystemMessage(Component.literal(GCCoreUtil.translate("gui.village.warning.no_freq_mod")))
                            stats.setChatCooldown(20)
                        }
                    }
                }
            }

            player.addStat(StatList.TALKED_TO_VILLAGER)
            return true
        } else {
            return super.processInteract(player, hand)
        }
    }

    override fun writeEntityToNBT(tagCompound: NBTTagCompound) {
        super.writeEntityToNBT(tagCompound)
        tagCompound.putInt("Riches", this.wealth)

        if (this.buyingList != null) {
            tagCompound.put("Offers", this.buyingList!!.getRecipiesAsTags())
        }

        val nbttaglist: NBTTagList = NBTTagList()

        for (i: Int in 0..<this.villagerInventory.getSizeInventory()) {
            val itemstack: ItemStack = this.villagerInventory.getStackInSlot(i)

            if (!itemstack.isEmpty()) {
                nbttaglist.add(itemstack.writeToNBT(NBTTagCompound()))
            }
        }

        tagCompound.put("Inventory", nbttaglist)
    }

    override fun readEntityFromNBT(tagCompund: NBTTagCompound) {
        super.readEntityFromNBT(tagCompund)
        this.wealth = tagCompund.getIntOr("Riches", 0)

        if (tagCompund.containsTag("Offers", 10)) {
            val nbttagcompound: NBTTagCompound = tagCompund.getCompoundOrEmpty("Offers")
            this.buyingList = MerchantRecipeList(nbttagcompound)
        }

        val nbttaglist: NBTTagList = tagCompund.listOfType("Inventory", 10)

        for (i: Int in 0..<nbttaglist.size) {
            val itemstack: ItemStack = ItemStack(nbttaglist.getCompoundOrEmpty(i))

            if (!itemstack.isEmpty()) {
                this.villagerInventory.addItem(itemstack)
            }
        }

        this.setCanPickUpLoot(true)
    }

    override fun canDespawn(): Boolean {
        return false
    }

    override fun getSoundPitch(): Float {
        return (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 0.5f
    }

    override fun getAmbientSound(): SoundEvent? {
        return if (this.isTrading()) SoundEvents.ENTITY_VILLAGER_TRADING else SoundEvents.ENTITY_VILLAGER_AMBIENT
    }

    override fun getHurtSound(damageSourceIn: DamageSource): SoundEvent? {
        return SoundEvents.ENTITY_VILLAGER_HURT
    }

    override fun getDeathSound(): SoundEvent? {
        return SoundEvents.ENTITY_VILLAGER_DEATH
    }

    open fun isMating(): Boolean {
        return this.isMating
    }

    open fun setMating(mating: Boolean) {
        this.isMating = mating
    }

    open fun setPlaying(playing: Boolean) {
        this.isPlaying = playing
    }

    open fun isPlaying(): Boolean {
        return this.isPlaying
    }

    override fun setRevengeTarget(livingBase: EntityLivingBase?) {
        super.setRevengeTarget(livingBase)

        if (this.villageObj != null && livingBase != null) {
            this.villageObj!!.addOrRenewAgressor(livingBase)

            if (livingBase is EntityPlayer) {
                var i: Int = -1

                if (this.isChild()) {
                    i = -3
                }

                this.villageObj!!.modifyPlayerReputation(livingBase.getName(), i)

                if (this.isEntityAlive()) {
                    this.world.setEntityState(this, 13.toByte())
                }
            }
        }
    }

    override fun onDeath(cause: DamageSource) {
        if (this.villageObj != null) {
            val entity: Entity? = cause.getTrueSource()

            if (entity != null) {
                if (entity is EntityPlayer) {
                    this.villageObj!!.modifyPlayerReputation(entity.getName(), -2)
                } else if (entity is IMob) {
                    this.villageObj!!.endMatingSeason()
                }
            } else {
                val entityplayer: EntityPlayer? = this.world.getClosestPlayerToEntity(this, 16.0)

                if (entityplayer != null) {
                    this.villageObj!!.endMatingSeason()
                }
            }
        }

        super.onDeath(cause)
    }

    override fun setCustomer(p_70932_1_: EntityPlayer?) {
        this.buyingPlayer = p_70932_1_
    }

    override fun getCustomer(): EntityPlayer? {
        return this.buyingPlayer
    }

    open fun isTrading(): Boolean {
        return this.buyingPlayer != null
    }

    override fun useRecipe(recipe: MerchantRecipe) {
        recipe.incrementToolUses()
        this.livingSoundTime = -this.getTalkInterval()
        this.playSound(SoundEvents.ENTITY_VILLAGER_YES, this.getSoundVolume(), this.getSoundPitch())
        var i: Int = 3 + this.rand.nextInt(4)

        if (recipe.getToolUses() == 1 || this.rand.nextInt(5) == 0) {
            this.timeUntilReset = 40
            this.needsInitilization = true

            if (this.buyingPlayer != null) {
                this.lastBuyingPlayer = this.buyingPlayer!!.getName()
            } else {
                this.lastBuyingPlayer = null
            }

            i += 5
        }

        if (recipe.getItemToBuy().getItem() === Items.EMERALD) {
            this.wealth += recipe.getItemToBuy().getCount()
        }

        if (recipe.getRewardsExp()) {
            this.world.spawnEntity(EntityXPOrb(this.world, this.posX, this.posY + 0.5, this.posZ, i))
        }
    }

    override fun verifySellingItem(stack: ItemStack) {
        if (!this.world.isRemote && this.livingSoundTime > -this.getTalkInterval() + 20) {
            this.livingSoundTime = -this.getTalkInterval()

            if (!stack.isEmpty()) {
                this.playSound(SoundEvents.ENTITY_VILLAGER_YES, this.getSoundVolume(), this.getSoundPitch())
            } else {
                this.playSound(SoundEvents.ENTITY_VILLAGER_NO, this.getSoundVolume(), this.getSoundPitch())
            }
        }
    }

    override fun getRecipes(p_70934_1_: EntityPlayer): MerchantRecipeList? {
        if (this.buyingList == null) {
            this.populateBuyingList()
        }

        return this.buyingList
    }

    private fun populateBuyingList() {
        if (this.buyingList == null) {
            this.buyingList = MerchantRecipeList()
        }

        for (tradeList: ITradeList? in DEFAULT_TRADE_LIST_MAP) {
            tradeList!!.modifyMerchantRecipeList(this.buyingList, this.rand)
        }
    }

    @OnlyIn(Dist.CLIENT)
    override fun setRecipes(recipeList: MerchantRecipeList?) {
    }

    override fun getEyeHeight(): Float {
        var f: Float = 1.62f

        if (this.isChild()) {
            f = (f.toDouble() - 0.81).toFloat()
        }

        return f
    }

    @OnlyIn(Dist.CLIENT)
    override fun handleStatusUpdate(id: Byte) {
        if (id.toInt() == 12) {
            this.spawnParticles(EnumParticleTypes.HEART)
        } else if (id.toInt() == 13) {
            this.spawnParticles(EnumParticleTypes.VILLAGER_ANGRY)
        } else if (id.toInt() == 14) {
            this.spawnParticles(EnumParticleTypes.VILLAGER_HAPPY)
        } else {
            super.handleStatusUpdate(id)
        }
    }

    @OnlyIn(Dist.CLIENT)
    private fun spawnParticles(particleType: EnumParticleTypes) {
        for (i: Int in 0..4) {
            val d0: Double = this.rand.nextGaussian() * 0.02
            val d1: Double = this.rand.nextGaussian() * 0.02
            val d2: Double = this.rand.nextGaussian() * 0.02
            this.world.spawnParticle(
                particleType,
                this.posX + (this.rand.nextFloat() * this.width * 2.0f).toDouble() - this.width.toDouble(),
                this.posY + 1.0 + (this.rand.nextFloat() * this.height).toDouble(),
                this.posZ + (this.rand.nextFloat() * this.width * 2.0f).toDouble() - this.width.toDouble(),
                d0,
                d1,
                d2,
                *IntArray(0)
            )
        }
    }

    override fun onInitialSpawn(difficulty: DifficultyInstance, livingdata: IEntityLivingData?): IEntityLivingData? {
        var livingdata: IEntityLivingData? = livingdata
        livingdata = super.onInitialSpawn(difficulty, livingdata)
        return livingdata
    }

    override fun createChild(ageable: EntityAgeable): EntityAlienVillager? {
        val entityvillager: EntityAlienVillager = EntityAlienVillager(this.world)
        entityvillager.onInitialSpawn(
            this.world.getDifficultyForLocation(BlockPos(entityvillager)),
            null as IEntityLivingData?
        )
        return entityvillager
    }

    override fun getWorld(): World {
        return this.world
    }

    override fun getPos(): BlockPos {
        return BlockPos(this)
    }

    override fun canBeLeashedTo(player: EntityPlayer): Boolean {
        return false
    }

    override fun onStruckByLightning(lightningBolt: EntityLightningBolt) {
        if (!this.world.isRemote && !this.isDead) {
            val entitywitch: EntityWitch = EntityEvolvedWitch(this.world)
            entitywitch.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch)
            entitywitch.onInitialSpawn(
                this.world.getDifficultyForLocation(BlockPos(entitywitch)),
                null as IEntityLivingData?
            )
            entitywitch.setNoAI(this.isAIDisabled())

            if (this.hasCustomName()) {
                entitywitch.setCustomNameTag(this.getCustomNameTag())
                entitywitch.setAlwaysRenderNameTag(this.getAlwaysRenderNameTag())
            }

            this.world.spawnEntity(entitywitch)
            this.setDead()
        }
    }

    open fun getVillagerInventory(): InventoryBasic {
        return this.villagerInventory
    }

    override fun updateEquipmentIfNeeded(itemEntity: EntityItem) {
        val itemstack: ItemStack = itemEntity.getItem()
        val item: Item = itemstack.getItem()

        if (this.canVillagerPickupItem(item)) {
            val itemstack1: ItemStack = this.villagerInventory.addItem(itemstack)

            if (itemstack1.isEmpty()) {
                itemEntity.setDead()
            } else {
                itemstack.setCount(itemstack1.getCount())
            }
        }
    }

    private fun canVillagerPickupItem(itemIn: Item): Boolean {
        return itemIn === Items.BREAD || itemIn === Items.POTATO || itemIn === Items.CARROT || itemIn === Items.WHEAT || itemIn === Items.WHEAT_SEEDS
    }

    open fun func_175553_cp(): Boolean {
        return this.hasEnoughItems(1)
    }

    open fun canAbondonItems(): Boolean {
        return this.hasEnoughItems(2)
    }

    open fun func_175557_cr(): Boolean {
        return !this.hasEnoughItems(5)
    }

    private fun hasEnoughItems(multiplier: Int): Boolean {
        for (i: Int in 0..<this.villagerInventory.getSizeInventory()) {
            val itemstack: ItemStack = this.villagerInventory.getStackInSlot(i)

            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() === Items.BREAD && itemstack.getCount() >= 3 * multiplier || itemstack.getItem() === Items.POTATO && itemstack.getCount() >= 12 * multiplier || itemstack.getItem() === Items.CARROT && itemstack.getCount() >= 12 * multiplier) {
                    return true
                }

                if (itemstack.getItem() === Items.WHEAT && itemstack.getCount() >= 9 * multiplier) {
                    return true
                }
            }
        }

        return false
    }

    override fun canBreath(): Boolean {
        return true
    }

    override fun replaceItemInInventory(inventorySlot: Int, itemStackIn: ItemStack): Boolean {
        if (super.replaceItemInInventory(inventorySlot, itemStackIn)) {
            return true
        } else {
            val i: Int = inventorySlot - 300

            if (i >= 0 && i < this.villagerInventory.getSizeInventory()) {
                this.villagerInventory.setInventorySlotContents(i, itemStackIn)
                return true
            } else {
                return false
            }
        }
    }

    open class EmeraldForItems constructor(@JvmField var sellItem: ItemStack, @JvmField var price: PriceInfo?) :
        ITradeList {
        override fun modifyMerchantRecipeList(recipeList: MerchantRecipeList?, random: Random) {
            var i: Int = 1

            if (this.price != null) {
                i = this.price!!.getPrice(random)
            }

            val tradeStack: ItemStack = this.sellItem.copy()
            tradeStack.setCount(i)

            recipeList!!.add(MerchantRecipe(tradeStack, FoundationItemVariants.stack(requireNotNull(GCItems.itemBasicMoon), 1, 2)))
        }
    }

    interface ITradeList {
        fun modifyMerchantRecipeList(recipeList: MerchantRecipeList?, random: Random)
    }

    open class ItemAndEmeraldToItem : ITradeList {
        @JvmField
        var field_179411_a: ItemStack

        @JvmField
        var field_179409_b: PriceInfo?

        @JvmField
        var field_179410_c: ItemStack

        constructor(p_i45813_1_: Item, p_i45813_2_: PriceInfo?, p_i45813_3_: Item) {
            this.field_179411_a = ItemStack(p_i45813_1_)
            this.field_179409_b = p_i45813_2_
            this.field_179410_c = ItemStack(p_i45813_3_)
        }

        constructor(p_i45813_1_: ItemStack, p_i45813_2_: PriceInfo?, p_i45813_3_: ItemStack) {
            this.field_179411_a = p_i45813_1_
            this.field_179409_b = p_i45813_2_
            this.field_179410_c = p_i45813_3_
        }


        override fun modifyMerchantRecipeList(recipeList: MerchantRecipeList?, random: Random) {
            var i: Int = 1

            if (this.field_179409_b != null) {
                i = this.field_179409_b!!.getPrice(random)
            }

            recipeList!!.add(
                MerchantRecipe(
                    this.field_179411_a.copy(),
                    FoundationItemVariants.stack(requireNotNull(GCItems.itemBasicMoon), i, 2),
                    this.field_179410_c.copy()
                )
            )
        }
    }

    open class ListItemForEmeralds constructor(
        @JvmField var itemToBuy: ItemStack,
        @JvmField var priceInfo: PriceInfo?
    ) : ITradeList {
        override fun modifyMerchantRecipeList(recipeList: MerchantRecipeList?, random: Random) {
            var i: Int = 1

            if (this.priceInfo != null) {
                i = this.priceInfo!!.getPrice(random)
            }

            val itemstack: ItemStack
            val itemstack1: ItemStack

            if (i < 0) {
                itemstack = FoundationItemVariants.stack(requireNotNull(GCItems.itemBasicMoon), 1, 2)
                itemstack1 = ItemStack(this.itemToBuy.getItem(), -i, this.itemToBuy.legacyItemValue)
            } else {
                itemstack = FoundationItemVariants.stack(requireNotNull(GCItems.itemBasicMoon), i, 2)
                itemstack1 = ItemStack(this.itemToBuy.getItem(), 1, this.itemToBuy.legacyItemValue)
            }

            recipeList!!.add(MerchantRecipe(itemstack, itemstack1))
        }
    }

    open class PriceInfo constructor(p_i45810_1_: Int, p_i45810_2_: Int) :
        Tuple<Int,Int>(p_i45810_1_, p_i45810_2_) {
        open fun getPrice(rand: Random): Int {
            return if ((this.getFirst() as Int) >= (this.getSecond() as Int)) (this.getFirst() as Int) else (this.getFirst() as Int) + rand.nextInt(
                (this.getSecond() as Int) - (this.getFirst() as Int) + 1
            )
        }
    }

    companion object {
        private val DEFAULT_TRADE_LIST_MAP: Array<ITradeList?> = ((arrayOf<ITradeList>(
            ListItemForEmeralds(ItemStack(GCItems.oxMask, 1, 0), PriceInfo(1, 2)),
            ListItemForEmeralds(ItemOxygenTank.stack(requireNotNull(GCItems.oxTankLight), 1, 235), PriceInfo(3, 4)),
            ListItemForEmeralds(ItemStack(GCItems.oxygenGear, 1, 0), PriceInfo(3, 4)),
            ListItemForEmeralds(ItemStack(GCItems.fuelCanister, 1, 317), PriceInfo(3, 4)),
            ListItemForEmeralds(FoundationItemVariants.stack(requireNotNull(GCItems.parachute), 1, 0), PriceInfo(1, 2)),
            ListItemForEmeralds(ItemStack(GCItems.battery, 1, 58), PriceInfo(2, 4)),
            ItemAndEmeraldToItem(
                ItemStack(GCItems.oilCanister, 1, ItemCanisterGeneric.EMPTY),
                PriceInfo(1, 1),
                foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.foodItem), 1, 1)
            ),  //carrots = also yields a tin!
            ListItemForEmeralds(FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, ItemBasic.WAFER_BASIC), PriceInfo(3, 4)),
            ItemAndEmeraldToItem(
                foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.schematic), 1, 0),
                PriceInfo(3, 5),
                foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.schematic), 1, 1)
            ),  //Exchange buggy and rocket schematics
            ItemAndEmeraldToItem(
                foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.schematic), 1, 1),
                PriceInfo(3, 5),
                foundation.item.FoundationItemVariants.stack(requireNotNull(GCItems.schematic), 1, 0)
            ),  //Exchange buggy and rocket schematics
            ItemAndEmeraldToItem(
                FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 2, 3),
                PriceInfo(1, 1),
                FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 6)
            ),  //Compressed Tin - needed to craft a Fuel Loader
            ItemAndEmeraldToItem(
                FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 2, 4),
                PriceInfo(1, 1),
                FoundationItemVariants.stack(requireNotNull(GCItems.basicItem), 1, 7)
            ),  //Compressed Copper - needed to craft a Fuel Loader
            EmeraldForItems(
                ItemStack(Blocks.SAPLING, 1, 3),
                PriceInfo(11, 39)
            ) //The one thing Alien Villagers don't have and can't get is jungle trees...
        ) as Array<EntityAlienVillager.ITradeList?>?))!!
    }
}
