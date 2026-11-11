package galaxyspace.systems.SolarSystem.planets.overworld.items.armor

import net.minecraft.network.chat.Component

import foundation.item.legacyItemValue

import foundation.inventory.armorItems
import foundation.persistence.containsTag

import java.util.HashMap

import asmodeuscore.core.client.render.ModelOBJArmor
import galaxyspace.api.item.IJetpackArmor
import galaxyspace.api.item.IModificationItem
import galaxyspace.core.prefab.items.ItemElectricArmor
import galaxyspace.core.prefab.items.modules.ItemModule
import galaxyspace.core.util.GSCreativeTabs
import galaxyspace.core.util.GSUtils.Module_Type
import galaxyspace.systems.SolarSystem.planets.overworld.render.item.ItemSpaceSuitModel
import micdoodle8.mods.galacticraft.api.item.IArmorGravity
import micdoodle8.mods.galacticraft.api.item.IItemElectric
import micdoodle8.mods.galacticraft.api.item.ISensorGlassesArmor
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.client.gui.overlay.OverlaySensorGlasses
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.model.ModelBiped
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.settings.GameSettings
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.entity.Entity
import net.minecraft.entity.EntityLiving
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.EquipmentSlot as EntityEquipmentSlot
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.core.NonNullList

import net.minecraft.ChatFormatting as TextFormatting
import net.minecraft.world.level.Level as World
import net.minecraftforge.common.ISpecialArmor
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist
import org.lwjgl.input.Keyboard
import java.util.*
import kotlin.math.min

abstract class ItemSpaceSuit(name: String?, materialIn: ArmorMaterial?, armorIndex: EntityEquipmentSlot) :
    ItemElectricArmor((materialIn)!!, armorIndex), ISensorGlassesArmor, IModificationItem, IJetpackArmor, ISpecialArmor,
    IArmorGravity {
    private var jumpCharge = 0f

    init {
        this.setTranslationKey(name + "_" + armorIndex.toString().lowercase(Locale.getDefault()))
        this.setMaxDamage(200)
        this.setNoRepair()
    }

    override fun onUpdate(stack: ItemStack, worldIn: World, entityIn: Entity, itemSlot: Int, isSelected: Boolean) {
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected)

        if (!stack.hasTagCompound()) stack.setTagCompound(NBTTagCompound())

        if (!stack.getTagCompound()!!.containsTag(mod_count)) {
            stack.getTagCompound()!!.putInt(mod_count, getModificationCount(stack))
        }

        if (!stack.getTagCompound()!!.containsTag("Unbreakable")) stack.getTagCompound()!!.putBoolean("Unbreakable", true)

        when (getArmorType(stack)) {
            EntityEquipmentSlot.HEAD -> if (!stack.getTagCompound()!!.containsTag(suit_buttons[0])) stack.getTagCompound()!!
                .putBoolean(suit_buttons[0], false)

            EntityEquipmentSlot.CHEST -> if (!stack.getTagCompound()!!.containsTag(suit_buttons[1])) stack.getTagCompound()!!
                .putBoolean(suit_buttons[1], false)

            EntityEquipmentSlot.LEGS -> if (!stack.getTagCompound()!!.containsTag(suit_buttons[2])) stack.getTagCompound()!!
                .putBoolean(suit_buttons[2], false)

            EntityEquipmentSlot.FEET -> if (!stack.getTagCompound()!!.containsTag(suit_buttons[3])) stack.getTagCompound()!!
                .putBoolean(suit_buttons[3], false)

            else -> {}
        }
    }

    private fun pressed() {
        val mc: Minecraft = Minecraft.getInstance()

        if (mc.gameSettings.keyBindJump.isKeyDown()) pressedKey[0] = true
        else pressedKey[0] = false

        if (mc.gameSettings.keyBindForward.isKeyDown()) pressedKey[1] = true
        else pressedKey[1] = false

        if (mc.gameSettings.keyBindSneak.isKeyDown()) pressedKey[2] = true
        else pressedKey[2] = false
    }

    @OnlyIn(Dist.CLIENT)
    override fun renderHelmetOverlay(
        stack: ItemStack,
        player: EntityPlayer,
        resolution: ScaledResolution,
        partialTicks: Float
    ) {
        if (getArmorType(stack) == EntityEquipmentSlot.HEAD && stack.getTagCompound()!!
                .getBoolean("sensor") && getElectricityStored(stack) > 2
        ) {
            if (stack.getTagCompound()!!.getBoolean(suit_buttons[0])) {
                GlStateManager.pushMatrix()
                OverlaySensorGlasses.renderSensorGlassesMain(stack, player, resolution, partialTicks)
                OverlaySensorGlasses.renderSensorGlassesValueableBlocks(stack, player, resolution, partialTicks)
                GlStateManager.popMatrix()
            }
        }
    }

    override fun onArmorTick(world: World, player: EntityPlayer, itemStack: ItemStack) {
        if (!world.isClientSide && !player.capabilities.isCreativeMode) {
            if (player.level().provider is IGalacticraftWorldProvider) {
                if ((player.level().provider as IGalacticraftWorldProvider).shouldCorrodeArmor()) {
                    val stats: GCPlayerStats = (GCPlayerStats.get(player))!!
                    if ((stats.getShieldControllerInSlot())!!.isEmpty()) this.discharge(itemStack, 50f, true)
                }
            }
        }

        if (world.isClientSide) {
            pressed()
        }

        if (itemStack.hasTagCompound()) {
            for (modules in (this.getAvailableModules())!!) {
                if (getArmorType(itemStack) == (modules)!!.getEquipmentSlot()) {
                    if (itemStack.getTagCompound()!!
                            .getBoolean((modules)!!.getName()) && getElectricityStored(itemStack) >= (modules)!!.getDischargeCount()
                    ) {
                        (modules)!!.onUpdate(
                            world,
                            player,
                            itemStack,
                            itemStack.getTagCompound()!!
                                .getBoolean(suit_buttons[3 - getArmorType(itemStack).getIndex()])
                        )
                        if (itemStack.getTagCompound()!!
                                .getBoolean(suit_buttons[3 - getArmorType(itemStack).getIndex()])
                        ) if (player.tickCount % 20 == 0) this.discharge(
                            itemStack,
                            (modules)!!.getDischargeCount().toFloat(),
                            true
                        )
                    }
                }
            }

            if (getArmorType(itemStack) == EntityEquipmentSlot.FEET) {
                if (itemStack.getTagCompound()!!.getBoolean(suit_buttons[3])) {
                    if (pressedKey[0]) {
                        if (itemStack.getTagCompound()!!.getBoolean("jump") && getElectricityStored(itemStack) >= 5) {
                            if (player.onGround) this.jumpCharge = 1.0f

                            if (player.deltaMovement.y >= 0.0 && this.jumpCharge > 0.0f && !player.isInWater()) {
                                /*if (this.jumpCharge == 1.0f) {
	                                player.motionX *= 3.5;
	                                player.motionZ *= 3.5;
	                            }*/

                                player.fallDistance = 0.3f
                                this.discharge(itemStack, 5f, true)

                                player.motionY += (this.jumpCharge * 0.25f).toDouble()
                                this.jumpCharge = (this.jumpCharge.toDouble() * 0.75).toFloat()
                            }
                        } else if (this.jumpCharge < 1.0f) {
                            this.jumpCharge = 0.0f
                        }
                    }
                }
            }
        }
    }


    public override fun discharge(itemStack: ItemStack?, energy: Float, doTransfer: Boolean): Float {
        val thisEnergy: Float = this.getElectricityStored(itemStack)
        val energyToTransfer: Float = min(min(thisEnergy, energy), this.transferMax)

        if (doTransfer) this.setElectricity(itemStack, thisEnergy - energyToTransfer)


        return energyToTransfer
    }

    @OnlyIn(Dist.CLIENT)
    override fun addInformation(stack: ItemStack, world: World?, list: MutableList<String?>, flagIn: ITooltipFlag) {
        addInfo(stack, world, list, flagIn)

        var color: String = ""
        val joules: Float = this.getElectricityStored(stack)

        if (joules <= this.getMaxElectricityStored(stack) / 3) {
            color = "\u00a74"
        } else if (joules > this.getMaxElectricityStored(stack) * 2 / 3) {
            color = "\u00a72"
        } else {
            color = "\u00a76"
        }


        //list.add(EnumColor.DARK_GREEN + GCCoreUtil.translate("gui.module.caninstall"));
        list.add(
            EnumColor.GREY.toString() + GCCoreUtil.translate("gui.module.available_modules") + " " + stack.getTagCompound()!!
                .getIntOr(mod_count, 0)
        )
        list.add("")
        list.add(EnumColor.AQUA.toString() + GCCoreUtil.translate("gui.module.list"))
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            for (s in (this.getAvailableModules())!!) {
                if ((s)!!.getEquipmentSlot() == getArmorType(stack) && stack.getTagCompound()!!
                        .containsTag((s)!!.getName())
                ) list.addAll(
                    Minecraft.getInstance().fontRenderer.listFormattedStringToWidth(
                        this.check(
                            stack,
                            ((s)!!.getName())!!
                        ) + GCCoreUtil.translate("gui.module." + (s)!!.getName()), 150
                    )
                )
            }
        } else {
            list.add(
                EnumColor.DARK_AQUA.toString() + GCCoreUtil.translateWithFormat(
                    "item_desc.shift.name",
                    GameSettings.getKeyDisplayString(
                        Minecraft.getInstance().gameSettings.keyBindSneak.getKeyCode()
                    )
                )
            )
        }
        list.add("")
        list.add(
            (color + EnergyDisplayHelper.getEnergyDisplayS(joules) + "/"
                    + EnergyDisplayHelper.getEnergyDisplayS(this.getMaxElectricityStored(stack)))
        )
    }

    private fun check(stack: ItemStack, nbt: String): String {
        if (stack.hasTagCompound() && stack.getTagCompound()!!
                .getBoolean(nbt)
        ) return TextFormatting.DARK_GREEN.toString() + "- "
        return TextFormatting.DARK_RED.toString() + "- "
    }

    protected fun getArmorType(stack: ItemStack): EntityEquipmentSlot {
        val i: EntityEquipmentSlot = EntityLiving.getSlotForItemStack(stack)
        return i
    }

    fun needText(player: EntityPlayer, text: String?) {
        player.sendSystemMessage(Component.literal(TextFormatting.DARK_RED.toString() + GCCoreUtil.translate("gui.message." + text)))
    }

    fun installText(player: EntityPlayer, text: String?) {
        player.sendSystemMessage(Component.literal(TextFormatting.DARK_GREEN.toString() + GCCoreUtil.translate("gui.message.install." + text)))
    }

    @OnlyIn(Dist.CLIENT)
    override fun getArmorModel(
        entityLiving: EntityLivingBase,
        itemStack: ItemStack,
        armorSlot: EntityEquipmentSlot,
        model: ModelBiped
    ): ModelBiped? {
        if (models.get(armorSlot) == null) models.put(armorSlot, ItemSpaceSuitModel(armorSlot.getIndex()))

        var armorModel: ModelBiped? = models.get(armorSlot)
        if (armorSlot == EntityEquipmentSlot.CHEST && itemStack.hasTagCompound() && itemStack.getTagCompound()!!
                .getBoolean("jetpack")
        ) {
            if (jetpack_model == null) jetpack_model = ItemSpaceSuitModel(5)

            armorModel = jetpack_model
        }

        if (itemStack.getItem() is ItemSpaceSuit) {
            armorModel = fillingArmorModel(armorModel, entityLiving)
            if (hasColor(itemStack) && armorModel is ModelOBJArmor) armorModel.color = getColor(itemStack)
        }
        return armorModel
    }

    public override fun getMaxElectricityStored(stack: ItemStack?): Float {
        val energy: Boolean = (stack)!!.hasTagCompound() && (stack)!!.getTagCompound()!!.containsTag("energy")
        when (getArmorType((stack)!!).getIndex()) {
            0 -> return if (energy) 100000.0f else 25000.0f
            1 -> return if (energy) 150000.0f else 50000.0f
            2 -> return if (energy) 250000.0f else 75000.0f
            3 -> return if (energy) 50000.0f else 20000.0f
            else -> return 10000.0f
        }
    }

    override fun gravityOverrideIfLow(player: EntityPlayer?): Int {
        if (!(player)!!.inventory.armorItems.get(0).isEmpty() && !(player)!!.inventory.armorItems.get(0)
                .hasTagCompound()
        ) (player)!!.inventory.armorItems.get(0).setTagCompound(NBTTagCompound())

        return if (!(player)!!.inventory.armorItems.get(0).isEmpty() && (player)!!.inventory.armorItems.get(0)
                .getTagCompound()!!.getBoolean(suit_buttons[3])
            && (player)!!.inventory.armorItems.get(0).getTagCompound()!!.getBoolean("gravity")
            && getElectricityStored((player)!!.inventory.armorItems.get(0)) > 0
        ) 100 else 0
    }

    override fun gravityOverrideIfHigh(p: EntityPlayer?): Int {
        return 0
    }

    override fun getProperties(
        player: EntityLivingBase?,
        armor: ItemStack,
        source: DamageSource?,
        damage: Double,
        slot: Int
    ): ISpecialArmor.ArmorProperties {
        if ((source)!!.isUnblockable()) return ISpecialArmor.ArmorProperties(0, 0.0, 0)


        return ISpecialArmor.ArmorProperties(0, 0.16, Int.MAX_VALUE)
    }

    override fun getArmorDisplay(player: EntityPlayer?, armor: ItemStack, slot: Int): Int {
        return 0
    }

    override fun damageArmor(
        entity: EntityLivingBase?,
        stack: ItemStack,
        source: DamageSource?,
        damage: Int,
        slot: Int
    ) {
        this.discharge(stack, 5f, true)
    }

    override fun getSubItems(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        if (tab === GSCreativeTabs.GSArmorTab || tab === CreativeTabs.SEARCH) {
            list.add(ItemStack(this, 1, this.getMaxDamage()))
        }
    }

    @OnlyIn(Dist.CLIENT)
    override fun hasEffect(stack: ItemStack): Boolean {
        return false
    }

    override fun isBookEnchantable(stack: ItemStack, book: ItemStack): Boolean {
        return false
    }

    override fun getRGBDurabilityForDisplay(stack: ItemStack): Int {
        return 0x2ed8db
    }

    override fun showDurabilityBar(stack: ItemStack): Boolean {
        return true
    }

    override fun consumeFuel(stack: ItemStack?, fuel: Int) {
        this.discharge(stack, 5f, true)
    }

    override fun decrementFuel(stack: ItemStack?) {
        if ((stack)!!.getItem() is IItemElectric) ((stack)!!.getItem() as IItemElectric).discharge(stack, 1f, true)
    }

    override fun getFuel(stack: ItemStack?): Int {
        return (stack)!!.getTagCompound()!!.getIntOr(IJetpackArmor.TAG_FUEL, 0)
    }

    override fun canFly(stack: ItemStack?, player: EntityPlayer?): Boolean {
        if ((stack)!!.hasTagCompound() && (stack)!!.getTagCompound()!!.getBoolean("jetpack") && (stack)!!.getTagCompound()!!
                .getBoolean(suit_buttons[1])
        ) if ((stack)!!.legacyItemValue < (stack)!!.getMaxDamage()) {
            return true
        }
        return false
    }

    override fun isActivated(stack: ItemStack?): Boolean {
        return (stack)!!.getTagCompound()!!.getBoolean(IJetpackArmor.TAG_ACT)
    }

    override fun switchState(stack: ItemStack?, state: Boolean) {
        if (!(stack)!!.isEmpty() && (stack)!!.hasTagCompound()) (stack)!!.getTagCompound()!!
            .putBoolean(IJetpackArmor.TAG_ACT, state)
        else {
            (stack)!!.setTagCompound(NBTTagCompound())
        }
    }

    override fun getFireStreams(stack: ItemStack?): Int {
        return 2
    }

    override fun getType(stack: ItemStack?): Module_Type {
        return Module_Type.SPACESUIT
    }

    /*
	@Override
	public ItemModule[] getAvailableModules() {
		return new ItemModule[] { new SensorLens(), new Nightvision(), new Jetpack(), new Speed(), new Gravity(), new Stepassist(), new Jump(), new Energy(), new Protection() };
	}
*/
    override fun getModificationCount(stack: ItemStack?): Int {
        return 2
    }

    override fun getItemEnchantability(): Int {
        return 0
    }

    override fun canApplyAtEnchantingTable(stack: ItemStack, enchantment: Enchantment): Boolean {
        return false
    }

    abstract fun addInfo(stack: ItemStack?, world: World?, list: MutableList<String?>?, flagIn: ITooltipFlag?)

    companion object {
        @JvmField
        var mod_count: String = "modification_count"

        @JvmField
        var suit_buttons: Array<String?> =
            ((arrayOf<String>("helmet_button", "chest_button", "legs_button", "boots_button") as Array<String?>?))!!

        @JvmField
        var pressedKey: BooleanArray = BooleanArray(3)
        private val models = HashMap<EntityEquipmentSlot?, ItemSpaceSuitModel?>()
        private var jetpack_model: ItemSpaceSuitModel? = null

        @JvmStatic
        fun fillingArmorModel(model: ModelBiped?, entityLiving: EntityLivingBase): ModelBiped? {
            if (model == null) return model
            model.bipedLeftLeg.showModel = false
            model.bipedRightLeg.showModel = model.bipedLeftLeg.showModel
            model.bipedLeftArm.showModel = model.bipedRightLeg.showModel
            model.bipedRightArm.showModel = model.bipedLeftArm.showModel
            model.bipedBody.showModel = model.bipedRightArm.showModel
            model.bipedHeadwear.showModel = model.bipedBody.showModel
            model.bipedHead.showModel = model.bipedHeadwear.showModel
            model.isSneak = entityLiving.isShiftKeyDown()
            model.isRiding = entityLiving.isPassenger()
            model.isChild = entityLiving.isChild()

            return model
        }
    }
}
