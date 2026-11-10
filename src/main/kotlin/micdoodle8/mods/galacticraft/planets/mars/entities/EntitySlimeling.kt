package micdoodle8.mods.galacticraft.planets.mars.entities

import java.util.UUID

import com.google.common.base.Predicate
import micdoodle8.mods.galacticraft.api.entity.IEntityBreathable
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.core.Constants
import micdoodle8.mods.galacticraft.core.client.sounds.GCSounds
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.util.ColorUtil
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.core.util.WorldUtil
import micdoodle8.mods.galacticraft.planets.mars.MarsModuleClient
import micdoodle8.mods.galacticraft.planets.mars.inventory.InventorySlimeling
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityAgeable
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.*
import net.minecraft.entity.monster.EntityCreeper
import net.minecraft.entity.monster.EntityGhast
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.entity.passive.EntityHorse
import net.minecraft.entity.passive.EntityTameable
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.Items
import net.minecraft.init.MobEffects
import net.minecraft.init.SoundEvents
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.network.datasync.DataParameter
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.network.datasync.EntityDataManager
import net.minecraft.util.DamageSource
import net.minecraft.util.EntityDamageSource
import net.minecraft.util.EnumHand
import net.minecraft.util.SoundEvent
import net.minecraft.util.math.MathHelper
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.World
import net.minecraftforge.common.ForgeHooks
import java.util.*
import kotlin.math.max
import kotlin.math.min

open class EntitySlimeling constructor(par1World: World) : EntityTameable(par1World), IEntityBreathable {
    @JvmField
    var slimelingInventory: InventorySlimeling = InventorySlimeling(this)

    @JvmField
    var colorRed: Float = 0f

    @JvmField
    var colorGreen: Float = 0f

    @JvmField
    var colorBlue: Float = 0f

    @JvmField
    var ticksAlive: Long = 0

    @JvmField
    var age: Int = 0

    @JvmField
    val MAX_AGE: Int = 100000

    @JvmField
    var slimelingName: String = GCCoreUtil.translate("gui.message.unnamed.name")

    @JvmField
    var favFoodID: Int = 1

    @JvmField
    var attackDamage: Float = 0.05f

    @JvmField
    var kills: Int = 0

    init {
        this.setSize(0.45f, 0.7f)
        this.tasks.addTask(1, EntityAISwimming(this))
        this.aiSit = EntityAISitGC(this)
        this.tasks.addTask(2, this.aiSit)
        this.tasks.addTask(3, EntityAILeapAtTarget(this, 0.4f))
        this.tasks.addTask(4, EntityAIAttackMelee(this, 1.0, true))
        this.tasks.addTask(5, EntityAIFollowOwner(this, 1.0, 10.0f, 2.0f))
        this.tasks.addTask(6, EntityAIMate(this, 1.0))
        this.tasks.addTask(7, EntityAIWander(this, 1.0))
        this.tasks.addTask(9, EntityAIWatchClosest(this, EntityPlayer::class.java, 8.0f))
        this.tasks.addTask(9, EntityAILookIdle(this))
        this.targetTasks.addTask(1, EntityAIOwnerHurtByTarget(this))
        this.targetTasks.addTask(2, EntityAIOwnerHurtTarget(this))
        this.targetTasks.addTask(3, EntityAIHurtByTarget(this, true))
        this.targetTasks.addTask(
            4,
            EntityAITargetNonTamed(
                this,
                EntitySludgeling::class.java,
                false,
                Predicate { p_apply_1_: Any? -> p_apply_1_ is EntitySludgeling })
        )
        this.setTamed(false)

        when (this.rand.nextInt(3)) {
            0 -> this.colorRed = 1.0f
            1 -> this.colorBlue = 1.0f
            2 -> {
                this.colorRed = 1.0f
                this.colorGreen = 1.0f
            }
        }

        this.setRandomFavFood()
    }

    override fun getOwner(): EntityLivingBase? {
        val owner: EntityLivingBase? = super.getOwner()
        if (owner == null) {
            val ownerName: String? = getOwnerUsername()
            if (ownerName != null) {
                return this.world.getPlayerEntityByName(ownerName)
            }
        }
        return owner
    }

    override fun isOwner(entityLivingBase: EntityLivingBase): Boolean {
        return entityLivingBase === this.getOwner()
    }

    override fun canBreatheUnderwater(): Boolean {
        return true
    }

    open fun getSlimelingSize(): Float {
        return this.getScale() * 2.0f
    }

    override fun setScaleForAge(par1: Boolean) {
        this.setScale(this.getSlimelingSize())
    }

    override fun isChild(): Boolean {
        return this.getAge() / this.MAX_AGE.toFloat() < 0.33f
    }

    private fun setRandomFavFood() {
        when (this.rand.nextInt(10)) {
            0 -> this.favFoodID = Item.getIdFromItem(Items.GOLD_INGOT)
            1 -> this.favFoodID = Item.getIdFromItem(Items.FLINT_AND_STEEL)
            2 -> this.favFoodID = Item.getIdFromItem(Items.BAKED_POTATO)
            3 -> this.favFoodID = Item.getIdFromItem(Items.STONE_SWORD)
            4 -> this.favFoodID = Item.getIdFromItem(Items.GUNPOWDER)
            5 -> this.favFoodID = Item.getIdFromItem(Items.WOODEN_HOE)
            6 -> this.favFoodID = Item.getIdFromItem(Items.EMERALD)
            7 -> this.favFoodID = Item.getIdFromItem(Items.FISH)
            8 -> this.favFoodID = Item.getIdFromItem(Items.REPEATER)
            9 -> this.favFoodID = Item.getIdFromItem(Items.BOAT)
        }
    }

    constructor(par1World: World, red: Float, green: Float, blue: Float) : this(par1World) {
        this.colorRed = red
        this.colorGreen = green
        this.colorBlue = blue
    }

    override fun applyEntityAttributes() {
        super.applyEntityAttributes()
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896)
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(this.getMaxHealthSlimeling())
    }


    //    @Override
    //    public boolean isAIEnabled()
    //    {
    //        return true;
    //    }
    override fun updateAITasks() {
        this.dataManager.set<Float>(HEALTH, this.getHealth())
    }

    override fun entityInit() {
        super.entityInit()
        this.dataManager.register<Float>(HEALTH, this.getHealth())
        this.dataManager.register<Float>(COLOR_RED, this.colorRed)
        this.dataManager.register<Float>(COLOR_GREEN, this.colorGreen)
        this.dataManager.register<Float>(COLOR_BLUE, this.colorBlue)
        this.dataManager.register<Int>(AGE, this.age)
        this.dataManager.register<String?>(NAME, "")
        this.dataManager.register<Int>(FAV_FOOD_ID, this.favFoodID)
        this.dataManager.register<Float>(ATTACK_DAMAGE, this.attackDamage)
        this.dataManager.register<Int>(KILLS, this.kills)
        this.dataManager.register<String?>(OWNER_USERNAME, "")
        this.setName(GCCoreUtil.translate("gui.message.unnamed.name"))
    }

    override fun writeEntityToNBT(nbt: NBTTagCompound) {
        super.writeEntityToNBT(nbt)
        nbt.setTag("SlimelingInventory", this.slimelingInventory.writeToNBT(NBTTagList()))
        nbt.setFloat("SlimeRed", this.colorRed)
        nbt.setFloat("SlimeGreen", this.colorGreen)
        nbt.setFloat("SlimeBlue", this.colorBlue)
        nbt.setInteger("SlimelingAge", this.age)
        nbt.setString("SlimelingName", this.slimelingName)
        nbt.setInteger("FavFoodID", this.favFoodID)
        nbt.setFloat("SlimelingDamage", this.attackDamage)
        nbt.setInteger("SlimelingKills", this.kills)
        nbt.setString("OwnerUsername", this.getOwnerUsername())
    }

    override fun readEntityFromNBT(nbt: NBTTagCompound) {
        super.readEntityFromNBT(nbt)
        this.slimelingInventory.readFromNBT(nbt.getTagList("SlimelingInventory", 10))
        this.colorRed = nbt.getFloat("SlimeRed")
        this.colorGreen = nbt.getFloat("SlimeGreen")
        this.colorBlue = nbt.getFloat("SlimeBlue")
        this.age = nbt.getInteger("SlimelingAge")
        this.slimelingName = nbt.getString("SlimelingName")
        this.favFoodID = nbt.getInteger("FavFoodID")
        this.attackDamage = nbt.getFloat("SlimelingDamage")
        this.kills = nbt.getInteger("SlimelingKills")
        this.setOwnerUsername(nbt.getString("OwnerUsername"))
        this.setColorRed(this.colorRed)
        this.setColorGreen(this.colorGreen)
        this.setColorBlue(this.colorBlue)
        this.setAge(this.age)
        this.setName(this.slimelingName)
        this.setKillCount(this.kills)
    }

    override fun getHurtSound(damageSourceIn: DamageSource): SoundEvent? {
        this.playSound(SoundEvents.BLOCK_SLIME_STEP, this.getSoundVolume(), 1.1f)
        return null
    }

    override fun getDeathSound(): SoundEvent? {
        this.playSound(GCSounds.slimeDeath, this.getSoundVolume(), 0.8f)
        return null
    }

    override fun getDropItem(): Item? {
        return Items.SLIME_BALL
    }

    override fun onLivingUpdate() {
        super.onLivingUpdate()

        if (!this.world.isRemote) {
            if (this.ticksAlive <= 0) {
                this.setColorRed(this.colorRed)
                this.setColorGreen(this.colorGreen)
                this.setColorBlue(this.colorBlue)
            }

            this.ticksAlive++

            if (this.ticksAlive % 2 == 0L) {
                if (this.age < this.MAX_AGE) {
                    this.age++
                }

                this.setAge(min(this.age, this.MAX_AGE))
            }

            this.setFavoriteFood(this.favFoodID)
            this.setAttackDamage(this.attackDamage)
            this.setKillCount(this.kills)
            //            this.setCargoSlot(this.slimelingInventory.getStackInSlot(1));
        }

        if (!this.world.isRemote) {
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(this.getMaxHealthSlimeling())

            if (this.getOwnerUsername().isEmpty()) {
                val owner: Entity? = this.getOwner()

                if (owner != null) {
                    this.setOwnerUsername(owner.getName())
                }
            }
        }
    }

    private fun getMaxHealthSlimeling(): Double {
        if (this.isTamed()) {
            return 20.001 + 30.0 * (this.age.toDouble() / this.MAX_AGE.toDouble())
        } else {
            return 8.0
        }
    }

    override fun getEyeHeight(): Float {
        return this.height * 0.8f
    }

    override fun attackEntityFrom(par1DamageSource: DamageSource, par2: Float): Boolean {
        var par2: Float = par2
        if (this.isEntityInvulnerable(par1DamageSource)) {
            return false
        } else {
            val entity: Entity? = par1DamageSource.getTrueSource()
            this.setSittingAI(false)

            if (entity != null && entity !is EntityPlayer) {
                par2 = (par2 + 1.0f) / 2.0f
            }

            return super.attackEntityFrom(par1DamageSource, par2)
        }
    }

    override fun attackEntityAsMob(par1Entity: Entity): Boolean {
        return par1Entity.attackEntityFrom(EntityDamageSource("slimeling", this), this.getDamage())
    }

    open fun getDamage(): Float {
        val i: Int = if (this.isTamed()) 5 else 2
        return i * this.getAttackDamage()
    }

    override fun setTamed(par1: Boolean) {
        super.setTamed(par1)
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(this.getMaxHealthSlimeling())
    }

    override fun processInteract(player: EntityPlayer, hand: EnumHand): Boolean {
        val itemstack: ItemStack = player.inventory.getCurrentItem()

        if (this.isTamed()) {
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() === this.getFavoriteFood()) {
                    if (this.isOwner(player)) {
                        itemstack.shrink(1)

                        if (itemstack.isEmpty()) {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY)
                        }

                        if (this.world.isRemote) {
                            MarsModuleClient.openSlimelingGui(this, 1)
                        }

                        if (this.rand.nextInt(3) == 0) {
                            this.setRandomFavFood()
                        }
                    } else {
                        if (player is EntityPlayerMP) {
                            val stats: GCPlayerStats = (GCPlayerStats.get(player))!!
                            if (stats.getChatCooldown() == 0) {
                                player.sendMessage(TextComponentString(GCCoreUtil.translate("gui.slimeling.chat.wrong_player")))
                                stats.setChatCooldown(100)
                            }
                        }
                    }
                } else {
                    if (this.world.isRemote) {
                        MarsModuleClient.openSlimelingGui(this, 0)
                    }
                }
            } else {
                if (this.world.isRemote) {
                    MarsModuleClient.openSlimelingGui(this, 0)
                }
            }

            return true
        } else if (!itemstack.isEmpty() && itemstack.getItem() === Items.SLIME_BALL) {
            if (!player.capabilities.isCreativeMode) {
                itemstack.shrink(1)
            }

            if (itemstack.isEmpty()) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY)
            }

            if (!this.world.isRemote) {
                if (this.rand.nextInt(3) == 0) {
                    this.setTamed(true)
                    this.getNavigator().clearPath()
                    this.setAttackTarget(null)
                    this.setSittingAI(true)
                    this.setHealth(20.0f)
                    this.setOwnerId(player.getUniqueID())
                    this.setOwnerUsername(player.getName())
                    this.playTameEffect(true)
                    this.world.setEntityState(this, 7.toByte())
                } else {
                    this.playTameEffect(false)
                    this.world.setEntityState(this, 6.toByte())
                }
            }

            return true
        }

        return super.processInteract(player, hand)
    }

    open fun setSittingAI(sitting: Boolean) {
        this.aiSit.setSitting(sitting)
    }

    open fun getOwnerUsername(): String {
        val s: String = this.dataManager.get<String>(OWNER_USERNAME)
        return if (s == null || s.length == 0) "" else s
    }

    open fun setOwnerUsername(username: String) {
        this.dataManager.set<String?>(OWNER_USERNAME, username)
    }

    override fun isBreedingItem(par1ItemStack: ItemStack): Boolean {
        return false
    }

    open fun spawnBabyAnimal(par1EntityAgeable: EntityAgeable?): EntitySlimeling? {
        if (par1EntityAgeable is EntitySlimeling) {
            val otherSlimeling: EntitySlimeling = par1EntityAgeable

            val colorParentA: Vector3 =
                Vector3(this.getColorRed().toDouble(), this.getColorGreen().toDouble(), this.getColorBlue().toDouble())
            val colorParentB: Vector3 = Vector3(
                otherSlimeling.getColorRed().toDouble(),
                otherSlimeling.getColorGreen().toDouble(),
                otherSlimeling.getColorBlue().toDouble()
            )
            val newColor: Vector3 = ColorUtil.addColorsRealistically(colorParentA, colorParentB)
            newColor.x = max(min(newColor.x, 1.0), 0.0)
            newColor.y = max(min(newColor.y, 1.0), 0.0)
            newColor.z = max(min(newColor.z, 1.0), 0.0)
            val newSlimeling: EntitySlimeling =
                EntitySlimeling(this.world, newColor.x.toFloat(), newColor.y.toFloat(), newColor.z.toFloat())

            val s: UUID? = this.getOwnerId()

            if (s != null) {
                newSlimeling.setOwnerId(s)
                newSlimeling.setTamed(true)
            }

            return newSlimeling
        }

        return null
    }

    override fun canMateWith(par1EntityAnimal: EntityAnimal): Boolean {
        if (par1EntityAnimal === this) {
            return false
        } else if (!this.isTamed()) {
            return false
        } else if (par1EntityAnimal !is EntitySlimeling) {
            return false
        } else {
            val slimeling: EntitySlimeling = par1EntityAnimal
            return slimeling.isTamed() && !slimeling.isSitting() && this.isInLove() && slimeling.isInLove()
        }
    }

    override fun shouldAttackEntity(toAttack: EntityLivingBase, owner: EntityLivingBase): Boolean {
        if (toAttack !is EntityCreeper && toAttack !is EntityGhast) {
            if (toAttack is EntitySlimeling) {
                val slimeling: EntitySlimeling = toAttack

                if (slimeling.isTamed() && slimeling.getOwner() === owner) {
                    return false
                }
            }

            return !(toAttack is EntityPlayer && owner is EntityPlayer && !owner.canAttackPlayer(toAttack)) && (toAttack !is EntityHorse || !toAttack.isTame())
        } else {
            return false
        }
    }

    override fun createChild(par1EntityAgeable: EntityAgeable): EntityAgeable? {
        return this.spawnBabyAnimal(par1EntityAgeable)
    }

    open fun getColorRed(): Float {
        return this.dataManager.get<Float>(COLOR_RED)
    }

    open fun setColorRed(color: Float) {
        this.dataManager.set<Float>(COLOR_RED, color)
    }

    open fun getColorGreen(): Float {
        return this.dataManager.get<Float>(COLOR_GREEN)
    }

    open fun setColorGreen(color: Float) {
        this.dataManager.set<Float>(COLOR_GREEN, color)
    }

    open fun getColorBlue(): Float {
        return this.dataManager.get<Float>(COLOR_BLUE)
    }

    open fun setColorBlue(color: Float) {
        this.dataManager.set<Float>(COLOR_BLUE, color)
    }

    open fun getAge(): Int {
        return this.dataManager.get<Int>(AGE)
    }

    open fun setAge(age: Int) {
        this.dataManager.set<Int>(AGE, age)
    }

    override fun getName(): String {
        return this.dataManager.get<String>(NAME)
    }

    open fun setName(name: String) {
        this.dataManager.set<String?>(NAME, name)
    }

    open fun getFavoriteFood(): Item {
        return Item.getItemById(this.dataManager.get<Int>(FAV_FOOD_ID))
    }

    open fun setFavoriteFood(foodID: Int) {
        this.dataManager.set<Int>(FAV_FOOD_ID, foodID)
    }

    open fun getAttackDamage(): Float {
        return this.dataManager.get<Float>(ATTACK_DAMAGE)
    }

    open fun setAttackDamage(damage: Float) {
        this.dataManager.set<Float>(ATTACK_DAMAGE, damage)
    }

    open fun getKillCount(): Int {
        return this.dataManager.get<Int>(KILLS)
    }

    open fun setKillCount(damage: Int) {
        this.dataManager.set<Int>(KILLS, damage)
    }

    override fun canBreath(): Boolean {
        return true
    }

    open fun getScale(): Float {
        return this.getAge() / this.MAX_AGE.toFloat() * 0.5f + 0.5f
    }

    open fun getAiSit(): EntityAISit? {
        return this.aiSit
    }

    override fun onDeath(p_70645_1_: DamageSource) {
        super.onDeath(p_70645_1_)

        if (!this.world.isRemote) {
            val bag: ItemStack = this.slimelingInventory.getStackInSlot(1)
            if (bag != null && bag.getItem() === MarsItems.marsItemBasic && bag.getItemDamage() == 4) {
                this.slimelingInventory.decrStackSize(1, 64)
                this.entityDropItem(bag, 0.5f)
            }
        }
    }

    open class EntityAISitGC constructor(private val theEntity: EntityTameable) : EntityAISit(theEntity) {
        private var isSitting = false

        init {
            this.setMutexBits(5)
        }

        override fun shouldExecute(): Boolean {
            if (!this.theEntity.isTamed()) {
                return false
            } else if (this.theEntity.isInWater()) {
                return false
            } else {
                val e: Entity? = this.theEntity.getOwner()
                if (e is EntityLivingBase) {
                    val living: EntityLivingBase = e
                    return if (living == null) true else (if (this.theEntity.getDistanceSq(living) < 144.0 && living.getRevengeTarget() != null) false else this.isSitting)
                }
                return false
            }
        }

        override fun startExecuting() {
            this.theEntity.getNavigator().clearPath()
            this.theEntity.setSitting(true)
        }

        override fun resetTask() {
            this.theEntity.setSitting(false)
        }

        override fun setSitting(isSitting: Boolean) {
            this.isSitting = isSitting
        }
    }

    override fun jump() {
        this.motionY = 0.48 / WorldUtil.getGravityFactor(this)
        if (this.motionY < 0.28) {
            this.motionY = 0.28
        }

        if (this.isPotionActive(MobEffects.JUMP_BOOST)) {
            this.motionY += ((this.getActivePotionEffect(MobEffects.JUMP_BOOST)!!.getAmplifier() + 1) * 0.1f).toDouble()
        }

        if (this.isSprinting()) {
            val f: Float = this.rotationYaw / Constants.RADIANS_TO_DEGREES
            this.motionX -= (MathHelper.sin(f) * 0.2f).toDouble()
            this.motionZ += (MathHelper.cos(f) * 0.2f).toDouble()
        }

        this.isAirBorne = true
        ForgeHooks.onLivingJump(this)
    }

    companion object {
        private val HEALTH: DataParameter<Float> =
            EntityDataManager.createKey<Float>(EntitySlimeling::class.java, DataSerializers.FLOAT)
        private val COLOR_RED: DataParameter<Float> =
            EntityDataManager.createKey<Float>(EntitySlimeling::class.java, DataSerializers.FLOAT)
        private val COLOR_GREEN: DataParameter<Float> =
            EntityDataManager.createKey<Float>(EntitySlimeling::class.java, DataSerializers.FLOAT)
        private val COLOR_BLUE: DataParameter<Float> =
            EntityDataManager.createKey<Float>(EntitySlimeling::class.java, DataSerializers.FLOAT)
        private val AGE: DataParameter<Int> =
            EntityDataManager.createKey<Int>(EntitySlimeling::class.java, DataSerializers.VARINT)
        private val NAME: DataParameter<String?> =
            EntityDataManager.createKey<String?>(EntitySlimeling::class.java, DataSerializers.STRING)
        private val FAV_FOOD_ID: DataParameter<Int> =
            EntityDataManager.createKey<Int>(EntitySlimeling::class.java, DataSerializers.VARINT)
        private val ATTACK_DAMAGE: DataParameter<Float> =
            EntityDataManager.createKey<Float>(EntitySlimeling::class.java, DataSerializers.FLOAT)
        private val KILLS: DataParameter<Int> =
            EntityDataManager.createKey<Int>(EntitySlimeling::class.java, DataSerializers.VARINT)
        private val OWNER_USERNAME: DataParameter<String?> =
            EntityDataManager.createKey<String?>(EntitySlimeling::class.java, DataSerializers.STRING)
    }
}
