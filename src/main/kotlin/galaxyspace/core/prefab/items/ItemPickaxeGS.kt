package galaxyspace.core.prefab.items

import net.minecraft.network.chat.Component

import galaxyspace.core.util.GSCreativeTabs
import galaxyspace.core.util.GSUtils
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.item.ItemPickaxe
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.util.ActionResult
import net.minecraft.world.InteractionResult as EnumActionResult
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.core.BlockPos

import net.minecraft.world.level.Level as World
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.api.distmarker.Dist

open class ItemPickaxeGS constructor(assetName: String, material: ToolMaterial, drillMode: Boolean) :
    ItemPickaxe(material) {
    private val drillMode: Boolean

    //private boolean isAncient;
    @JvmField
    var breakRadius: Int

    @JvmField
    var breakDepth: Int = 0

    init {
        this.setTranslationKey(assetName)
        this.setNoRepair()

        this.drillMode = drillMode
        //this.isAncient = isAncient;
        this.breakRadius = 1
    }

    override fun getCreativeTab(): CreativeTabs? {
        return GSCreativeTabs.GSArmorTab
    }

    @OnlyIn(Dist.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, list: MutableList<String?>, flagIn: ITooltipFlag) {
        if (this.drillMode) if (stack.getTagCompound() != null && stack.getTagCompound()!!
                .getBoolean("drill_mode")
        ) list.add(EnumColor.DARK_GREEN.toString() + GCCoreUtil.translate("gui.pickaxe.drillmodeon"))
        else list.add(EnumColor.DARK_RED.toString() + GCCoreUtil.translate("gui.pickaxe.drillmodeoff"))
    }

    override fun onItemRightClick(world: World, player: EntityPlayer, handIn: EnumHand): ActionResult<ItemStack?> {
        val stack: ItemStack = player.getItemInHand(handIn)

        if (drillMode && !stack.hasTagCompound()) stack.setTagCompound(NBTTagCompound())

        if (!world.isClientSide && drillMode) {
            if (!stack.getTagCompound()!!.getBoolean("drill_mode")) {
                stack.getTagCompound()!!.putBoolean("drill_mode", true)
                player.sendSystemMessage(
                    Component.literal(
                        EnumColor.DARK_GREEN.toString() + GCCoreUtil.translateWithFormat(
                            "gui.pickaxe.drillmodeon"
                        )
                    )
                )
            } else {
                stack.getTagCompound()!!.putBoolean("drill_mode", false)
                player.sendSystemMessage(Component.literal(EnumColor.DARK_RED.toString() + GCCoreUtil.translateWithFormat("gui.pickaxe.drillmodeoff")))
            }

            return ActionResult<ItemStack?>(EnumActionResult.SUCCESS, player.getItemInHand(handIn))
        }

        return ActionResult<ItemStack?>(EnumActionResult.PASS, player.getItemInHand(handIn))
    }

    override fun onBlockDestroyed(
        stack: ItemStack,
        worldIn: World,
        state: IBlockState,
        pos: BlockPos,
        entityLiving: EntityLivingBase
    ): Boolean {
        val ret: Boolean = super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving)

        if (entityLiving !is EntityPlayer || worldIn.isClientSide) {
            return ret
        }


        if (!stack.hasTagCompound() || stack.hasTagCompound() && !stack.getTagCompound()!!
                .getBoolean("drill_mode")
        ) return ret

        GSUtils.tryHarvestBlock(stack, worldIn, pos, entityLiving, breakRadius)

        return ret
    }

    companion object {
        @JvmField
        var materials: Array<Material?> = arrayOf<Material?>(
            Material.ROCK,
            Material.IRON,
            Material.ICE,
            Material.GLASS,
            Material.PISTON,
            Material.ANVIL
        )
    }
}
