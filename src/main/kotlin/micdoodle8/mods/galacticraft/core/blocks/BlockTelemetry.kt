package micdoodle8.mods.galacticraft.core.blocks

import java.util.UUID
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.tile.TileEntityTelemetry
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.World

open class BlockTelemetry constructor(assetName: String?) : BlockAdvancedTile(Material.IRON), IShiftDescription,
    ISortableBlock {
    //Metadata: 0-3 = orientation;  bits 2,3 = reserved for future use
    init {
        this.setHardness(1.0f)
        this.setSoundType(SoundType.METAL)
        this.setTranslationKey(assetName)
    }

    override fun onBlockPlacedBy(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState,
        placer: EntityLivingBase,
        stack: ItemStack
    ) {
        val angle: Int = MathHelper.floor(placer.rotationYaw * 4.0f / 360.0f + 0.5) and 3
        var change: Int = 0

        when (angle) {
            0 -> change = 3
            1 -> change = 4
            2 -> change = 2
            3 -> change = 5
        }

        worldIn.setBlockState(pos, getStateFromMeta(change), 3)
    }

    override fun onUseWrench(
        world: World?,
        pos: BlockPos?,
        entityPlayer: EntityPlayer?,
        hand: EnumHand?,
        heldItem: ItemStack?,
        side: EnumFacing?,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        val metadata: Int = getMetaFromState((world)!!.getBlockState(pos))
        val facing: Int = metadata and 3
        var change: Int = 0

        when (facing) {
            0 -> change = 1
            1 -> change = 3
            2 -> change = 5
            3 -> change = 4
            4 -> change = 2
            5 -> change = 0
        }
        change += (12 and metadata)
        (world)!!.setBlockState(pos, getStateFromMeta(change), 2)

        return true
    }

    override fun createNewTileEntity(world: World, meta: Int): TileEntity? {
        return TileEntityTelemetry()
    }

    public override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftBlocksTab
    }

    override fun onMachineActivated(
        world: World?,
        pos: BlockPos?,
        state: IBlockState?,
        entityPlayer: EntityPlayer?,
        hand: EnumHand?,
        heldItem: ItemStack?,
        side: EnumFacing?,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        if (!(world)!!.isRemote) {
            val tile: TileEntity? = (world)!!.getTileEntity(pos)
            if (tile is TileEntityTelemetry) {
                val held: ItemStack = (entityPlayer)!!.inventory.getCurrentItem()
                //Look for Frequency Module
                if (!held.isEmpty() && held.getItem() === GCItems.basicItem && held.getItemDamage() == 19) {
                    var fmData: NBTTagCompound? = held.getTagCompound()
                    if (fmData != null && fmData.hasKey("linkedUUIDMost") && fmData.hasKey("linkedUUIDLeast")) {
                        val uuid: UUID = UUID(fmData.getLong("linkedUUIDMost"), fmData.getLong("linkedUUIDLeast"))
                        tile.addTrackedEntity(uuid)
                        (entityPlayer)!!.sendMessage(TextComponentString(GCCoreUtil.translate("gui.telemetry_succeed.message")))
                    } else {
                        (entityPlayer)!!.sendMessage(TextComponentString(GCCoreUtil.translate("gui.telemetry_fail.message")))

                        if (fmData == null) {
                            fmData = NBTTagCompound()
                            held.setTagCompound(fmData)
                        }
                    }
                    fmData.setInteger("teCoordX", (pos)!!.getX())
                    fmData.setInteger("teCoordY", (pos)!!.getY())
                    fmData.setInteger("teCoordZ", (pos)!!.getZ())
                    fmData.setInteger("teDim", GCCoreUtil.getDimensionID(world))
                    return true
                }

                val wearing: ItemStack? = (GCPlayerStats.get((entityPlayer)!!))!!.getFrequencyModuleInSlot()
                if (wearing != null) {
                    if (wearing.hasTagCompound() && wearing.getTagCompound()!!.hasKey("teDim")) {
                        return false
                    }
                    (entityPlayer)!!.sendMessage(TextComponentString(GCCoreUtil.translate("gui.telemetry_fail_wearing_it.message")))
                } else {
                    (entityPlayer)!!.sendMessage(TextComponentString(GCCoreUtil.translate("gui.telemetry_fail_no_frequency_module.message")))
                }
            }
        }
        return false
    }

    override fun getShiftDescription(meta: Int): String? {
        return GCCoreUtil.translate(this.getTranslationKey() + ".description")
    }

    override fun showDescription(meta: Int): Boolean {
        return true
    }

    override fun getCategory(meta: Int): EnumSortCategoryBlock {
        return EnumSortCategoryBlock.MACHINE
    }
}
