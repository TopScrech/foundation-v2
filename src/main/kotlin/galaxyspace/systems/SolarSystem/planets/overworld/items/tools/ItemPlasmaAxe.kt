package galaxyspace.systems.SolarSystem.planets.overworld.items.tools

import galaxyspace.core.util.GSUtils

import galaxyspace.api.item.IModificationItem
import galaxyspace.core.GSItems
import galaxyspace.core.prefab.items.ItemAxeGS
import galaxyspace.core.prefab.items.modules.ItemModule
import galaxyspace.core.util.GSUtils.Module_Type
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemBasicGS.BasicItems
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.IItemPropertyGetter
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemPlasmaAxe constructor() : ItemAxeGS("plasma_axe", (GSItems.PLASMA_TOOLS)!!), IModificationItem {
    init {
        this.addPropertyOverride(ResourceLocation("heat"), object : IItemPropertyGetter {
            @SideOnly(Side.CLIENT)
            override fun apply(stack: ItemStack, worldIn: World?, entityIn: EntityLivingBase?): Float {
                if (entityIn == null) return 0.0f

                val flag: Boolean = entityIn.getHeldItemMainhand() == stack
                val flag1: Boolean = entityIn.getHeldItemOffhand() == stack

                if (flag || flag1) if (stack.hasTagCompound() && stack.getTagCompound()!!
                        .hasKey(heat)
                ) if (stack.getTagCompound()!!
                        .getFloat(heat) >= max_heat
                ) return if (entityIn is EntityPlayer) 1.0f else 0.0f

                return 0.0f
            }
        })
    }

    override fun onUpdate(stack: ItemStack, world: World, entity: Entity, itemSlot: Int, isSelected: Boolean) {
        super.onUpdate(stack, world, entity, itemSlot, isSelected)
        if (!world.isRemote && entity is EntityPlayer) {
            if (!stack.hasTagCompound()) stack.setTagCompound(NBTTagCompound())

            if (!stack.getTagCompound()!!.hasKey(heat)) stack.getTagCompound()!!.setFloat(heat, 0.0f)

            if (entity.ticksExisted % 20 == 0 && stack.getTagCompound() != null) if (stack.getTagCompound()!!
                    .hasKey(heat)
            ) {
                if (stack.getTagCompound()!!.getFloat(heat) > 0.0f) stack.getTagCompound()!!
                    .setFloat(heat, stack.getTagCompound()!!.getFloat(heat) - 0.2f)


                if (stack.getTagCompound()!!.getFloat(heat) < 0.0f) stack.getTagCompound()!!.setFloat(heat, 0.0f)
            }

            if (stack.getItemDamage() >= stack.getMaxDamage() - 10) tryConsumePlasmaCell(stack, entity)
        }
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, list: MutableList<String?>, flagIn: ITooltipFlag) {
        list.addAll(
            FMLClientHandler.instance()
                .getClient().fontRenderer.listFormattedStringToWidth(GCCoreUtil.translate("gui.plasma_tools.desc"), 250)
        )

        if (stack.getTagCompound() != null && stack.getTagCompound()!!.hasKey(heat)) list.add(
            GCCoreUtil.translate("gui.status.heat.name") + ": " + String.format(
                "%.1f",
                stack.getTagCompound()!!.getFloat(heat)
            )
        )

        list.add("")
        list.add(EnumColor.DARK_RED.toString() + "[WIP] Content")
    }

    override fun hitEntity(stack: ItemStack, target: EntityLivingBase, attacker: EntityLivingBase): Boolean {
        if (stack.getItemDamage() == stack.getMaxDamage()) return false

        stack.damageItem(2, attacker)
        if (stack.hasTagCompound() && stack.getTagCompound()!!.hasKey(heat) && stack.getTagCompound()!!
                .getFloat(heat) < max_heat
        ) stack.getTagCompound()!!.setFloat(heat, stack.getTagCompound()!!.getFloat(heat) + 0.5f)

        return true
    }

    override fun onBlockDestroyed(
        stack: ItemStack,
        worldIn: World,
        state: IBlockState,
        pos: BlockPos,
        entityLiving: EntityLivingBase
    ): Boolean {
        if (!worldIn.isRemote && state.getBlockHardness(worldIn, pos).toDouble() != 0.0) {
            if (stack.hasTagCompound() && stack.getTagCompound()!!.hasKey(heat) && stack.getTagCompound()!!
                    .getFloat(heat) < max_heat
            ) stack.getTagCompound()!!.setFloat(heat, stack.getTagCompound()!!.getFloat(heat) + 0.5f)


            //GSUtils.tryHarvestBlock(stack, worldIn, pos, entityLiving, 1);
            stack.damageItem(1, entityLiving)
        }

        return true
    }

    public override fun getDestroySpeed(stack: ItemStack, state: IBlockState): Float {
        val material: Material = state.getMaterial()

        if (stack.getItemDamage() == stack.getMaxDamage()) return 0.0f

        if (stack.getTagCompound() != null && stack.getTagCompound()!!.hasKey(heat)) if (stack.getTagCompound()!!
                .getFloat(heat) >= max_heat
        ) return 0.0f

        return if (material !== Material.WOOD && material !== Material.PLANTS && material !== Material.VINE)
            super.getDestroySpeed(stack, state)
        else
            30.0f
    }

    private fun tryConsumePlasmaCell(stack: ItemStack, entityLiving: EntityLivingBase) {
        if (entityLiving is EntityPlayer) {
            val player: EntityPlayer = entityLiving
            for (cell: ItemStack in player.inventory.mainInventory) {
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
        return 3
    }

    companion object {
        @JvmField
        var heat: String = "plasma_heat"
        private const val max_heat = 50.0f
    }
}
