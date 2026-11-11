package galaxyspace.systems.SolarSystem.planets.overworld.items.tools

import foundation.item.legacyItemValue

import foundation.inventory.mainItems
import foundation.persistence.containsTag

import galaxyspace.api.item.IModificationItem
import galaxyspace.core.GSItems
import galaxyspace.core.prefab.items.ItemSwordGS
import galaxyspace.core.prefab.items.modules.ItemModule
import galaxyspace.core.util.GSUtils.Module_Type
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemBasicGS.BasicItems
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.item.IItemPropertyGetter
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class ItemPlasmaSword constructor() : ItemSwordGS("plasma_sword", (GSItems.PLASMA_TOOLS)!!), IModificationItem {
    init {
        this.addPropertyOverride(ResourceLocation.parse("heat"), object : IItemPropertyGetter {
            @OnlyIn(Dist.CLIENT)
            override fun apply(stack: ItemStack, worldIn: World?, entityIn: EntityLivingBase?): Float {
                if (entityIn == null) return 0.0f

                val flag: Boolean = entityIn.getMainHandItem() == stack
                val flag1: Boolean = entityIn.getOffhandItem() == stack

                if (flag || flag1) if (stack.hasTagCompound() && stack.getTagCompound()!!
                        .containsTag(heat)
                ) if (stack.getTagCompound()!!
                        .getFloat(heat) >= max_heat
                ) return if (entityIn is EntityPlayer) 1.0f else 0.0f

                return 0.0f
            }
        })
    }

    override fun onUpdate(stack: ItemStack, world: World, entity: Entity, itemSlot: Int, isSelected: Boolean) {
        super.onUpdate(stack, world, entity, itemSlot, isSelected)
        if (!world.isClientSide && entity is EntityPlayer) {
            if (!stack.hasTagCompound()) stack.setTagCompound(NBTTagCompound())
            if (!stack.getTagCompound()!!.containsTag(heat)) stack.getTagCompound()!!.putFloat(heat, 0.0f)

            if (entity.tickCount % 20 == 0 && stack.getTagCompound() != null) if (stack.getTagCompound()!!
                    .containsTag(heat)
            ) {
                if (stack.getTagCompound()!!.getFloat(heat) > 0.0f) stack.getTagCompound()!!
                    .putFloat(heat, stack.getTagCompound()!!.getFloat(heat) - 0.2f)


                if (stack.getTagCompound()!!.getFloat(heat) < 0.0f) stack.getTagCompound()!!.putFloat(heat, 0.0f)
            }

            if (stack.legacyItemValue >= stack.getMaxDamage() - 10) tryConsumePlasmaCell(stack, entity)
        }
    }

    override fun onCreated(stack: ItemStack, worldIn: World, playerIn: EntityPlayer) {
    }

    @OnlyIn(Dist.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, list: MutableList<String?>, flagIn: ITooltipFlag) {
        list.addAll(
            FMLClientHandler.instance()
                .getClient().fontRenderer.listFormattedStringToWidth(GCCoreUtil.translate("gui.plasma_tools.desc"), 250)
        )

        if (stack.getTagCompound() != null && stack.getTagCompound()!!.containsTag(heat)) list.add(
            GCCoreUtil.translate("gui.status.heat.name") + ": " + String.format(
                "%.1f",
                stack.getTagCompound()!!.getFloat(heat)
            )
        )

        list.add("")
        list.add(EnumColor.DARK_RED.toString() + "[WIP] Content")
    }

    override fun hitEntity(stack: ItemStack, target: EntityLivingBase, attacker: EntityLivingBase): Boolean {
        if (stack.legacyItemValue == stack.getMaxDamage()) return false


        stack.damageItem(1, attacker)
        if (stack.hasTagCompound() && stack.getTagCompound()!!.containsTag(heat) && stack.getTagCompound()!!
                .getFloat(heat) < max_heat
        ) stack.getTagCompound()!!.putFloat(heat, stack.getTagCompound()!!.getFloat(heat) + 0.5f)

        return true
    }


    override fun onBlockDestroyed(
        stack: ItemStack,
        worldIn: World,
        state: IBlockState,
        pos: BlockPos,
        entityLiving: EntityLivingBase
    ): Boolean {
        if (stack.legacyItemValue == stack.getMaxDamage() - 1) return false

        if (state.getBlockHardness(worldIn, pos).toDouble() != 0.0) {
            if (stack.hasTagCompound() && stack.getTagCompound()!!.containsTag(heat) && stack.getTagCompound()!!
                    .getFloat(heat) < max_heat
            ) stack.getTagCompound()!!.putFloat(heat, stack.getTagCompound()!!.getFloat(heat) + 1.5f)

            stack.damageItem(2, entityLiving)
        }

        return true
    }

    private fun tryConsumePlasmaCell(stack: ItemStack, entityLiving: EntityLivingBase) {
        if (entityLiving is EntityPlayer) {
            val player: EntityPlayer = entityLiving
            for (cell: ItemStack in player.inventory.mainItems) {
                if (cell.isItemEqual(BasicItems.FILLED_PLASMA_CELL.getItemStack())) {
                    if (cell.getCount() > 1) {
                        player.addItemStackToInventory(BasicItems.EMPTY_PLASMA_CELL.getItemStack())
                        cell.shrink(1)
                    } else cell.setItemDamage(BasicItems.EMPTY_PLASMA_CELL.getMeta())
                    stack.setItemDamage(0)
                    break
                }
            }
        }
    }

    override fun getType(stack: ItemStack?): Module_Type {
        return Module_Type.TOOLS
    }

    override fun getAvailableModules(): Array<ItemModule?>? {
        return null
    }

    override fun getModificationCount(stack: ItemStack?): Int {
        return 2
    }

    companion object {
        @JvmField
        var heat: String = "plasma_heat"
        private const val max_heat = 20.0f
    }
}
