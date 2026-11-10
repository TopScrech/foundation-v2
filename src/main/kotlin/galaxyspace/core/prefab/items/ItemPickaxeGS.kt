package galaxyspace.core.prefab.items

import galaxyspace.core.util.GSCreativeTabs
import galaxyspace.core.util.GSUtils
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemPickaxe
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

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

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, list: MutableList<String?>, flagIn: ITooltipFlag) {
        if (this.drillMode) if (stack.getTagCompound() != null && stack.getTagCompound()!!
                .getBoolean("drill_mode")
        ) list.add(EnumColor.DARK_GREEN.toString() + GCCoreUtil.translate("gui.pickaxe.drillmodeon"))
        else list.add(EnumColor.DARK_RED.toString() + GCCoreUtil.translate("gui.pickaxe.drillmodeoff"))
    }

    override fun onItemRightClick(world: World, player: EntityPlayer, handIn: EnumHand): ActionResult<ItemStack?> {
        val stack: ItemStack = player.getHeldItem(handIn)

        if (drillMode && !stack.hasTagCompound()) stack.setTagCompound(NBTTagCompound())

        if (!world.isRemote && drillMode) {
            if (!stack.getTagCompound()!!.getBoolean("drill_mode")) {
                stack.getTagCompound()!!.setBoolean("drill_mode", true)
                player.sendMessage(
                    TextComponentString(
                        EnumColor.DARK_GREEN.toString() + GCCoreUtil.translateWithFormat(
                            "gui.pickaxe.drillmodeon"
                        )
                    )
                )
            } else {
                stack.getTagCompound()!!.setBoolean("drill_mode", false)
                player.sendMessage(TextComponentString(EnumColor.DARK_RED.toString() + GCCoreUtil.translateWithFormat("gui.pickaxe.drillmodeoff")))
            }

            return ActionResult<ItemStack?>(EnumActionResult.SUCCESS, player.getHeldItem(handIn))
        }

        return ActionResult<ItemStack?>(EnumActionResult.PASS, player.getHeldItem(handIn))
    }

    override fun onBlockDestroyed(
        stack: ItemStack,
        worldIn: World,
        state: IBlockState,
        pos: BlockPos,
        entityLiving: EntityLivingBase
    ): Boolean {
        val ret: Boolean = super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving)

        if (entityLiving !is EntityPlayer || worldIn.isRemote) {
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
