package micdoodle8.mods.galacticraft.core.blocks

import foundation.item.FoundationFrequencyModules
import net.minecraft.network.chat.Component
import foundation.persistence.containsTag

import java.util.UUID
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.items.IShiftDescription
import micdoodle8.mods.galacticraft.core.tile.TileEntityTelemetry
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryBlock
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.world.level.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.world.entity.LivingEntity as EntityLivingBase
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.world.InteractionHand as EnumHand
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth as MathHelper

import net.minecraft.world.level.Level as World

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
        val angle: Int = MathHelper.floor(placer.yRot * 4.0f / 360.0f + 0.5) and 3
        var change: Int = 0

        when (angle) {
            0 -> change = 3
            1 -> change = 4
            2 -> change = 2
            3 -> change = 5
        }

        worldIn.setBlock(pos, getStateFromMeta(change), 3)
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
        (world)!!.setBlock(pos, getStateFromMeta(change), 2)

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
        if (!(world)!!.isClientSide) {
            val tile: TileEntity? = (world)!!.getBlockEntity(pos)
            if (tile is TileEntityTelemetry) {
                val held: ItemStack = (entityPlayer)!!.inventory.getSelectedItem()
                //Look for Frequency Module
                if (FoundationFrequencyModules.isModule(held)) {
                    val uuid = FoundationFrequencyModules.linkedEntity(held)
                    if (uuid != null) {
                        tile.addTrackedEntity(uuid)
                        entityPlayer.sendSystemMessage(Component.translatable("gui.telemetry_succeed.message"))
                    } else {
                        entityPlayer.sendSystemMessage(Component.translatable("gui.telemetry_fail.message"))
                    }
                    FoundationFrequencyModules.bindTelemetry(held, world.dimension(), requireNotNull(pos))
                    return true
                }

                val wearing: ItemStack? = (GCPlayerStats.get((entityPlayer)!!))!!.getFrequencyModuleInSlot()
                if (wearing != null) {
                    if (FoundationFrequencyModules.hasTelemetry(wearing)) {
                        return false
                    }
                    (entityPlayer)!!.sendSystemMessage(Component.literal(GCCoreUtil.translate("gui.telemetry_fail_wearing_it.message")))
                } else {
                    (entityPlayer)!!.sendSystemMessage(Component.literal(GCCoreUtil.translate("gui.telemetry_fail_no_frequency_module.message")))
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
