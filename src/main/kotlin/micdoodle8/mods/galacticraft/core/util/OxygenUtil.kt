package micdoodle8.mods.galacticraft.core.util

import micdoodle8.mods.galacticraft.api.item.IBreathableArmor.EnumGearType

import micdoodle8.mods.galacticraft.api.block.IPartialSealableBlock
import micdoodle8.mods.galacticraft.api.item.IBreathableArmor
import micdoodle8.mods.galacticraft.api.transmission.NetworkType
import micdoodle8.mods.galacticraft.api.transmission.tile.IConnector
import micdoodle8.mods.galacticraft.api.vector.BlockVec3
import micdoodle8.mods.galacticraft.api.vector.BlockVec3Dim
import micdoodle8.mods.galacticraft.api.world.EnumAtmosphericGas
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats
import micdoodle8.mods.galacticraft.core.fluid.OxygenPressureProtocol
import micdoodle8.mods.galacticraft.core.items.ItemCanisterOxygenInfinite
import micdoodle8.mods.galacticraft.core.items.ItemOxygenGear
import micdoodle8.mods.galacticraft.core.items.ItemOxygenMask
import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank
import micdoodle8.mods.galacticraft.core.tile.TileEntityOxygenDistributor
import net.minecraft.block.Block
import net.minecraft.block.BlockEnchantmentTable
import net.minecraft.block.BlockFarmland
import net.minecraft.block.BlockGlass
import net.minecraft.block.BlockGravel
import net.minecraft.block.BlockLeaves
import net.minecraft.block.BlockLiquid
import net.minecraft.block.BlockPistonBase
import net.minecraft.block.BlockSlab
import net.minecraft.block.BlockSponge
import net.minecraft.block.BlockStainedGlass
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraft.world.WorldProvider
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.ArrayList
import java.util.HashSet
import kotlin.math.min

object OxygenUtil {
    private var checked: HashSet<BlockPos?>? = null

    @JvmStatic
    @SideOnly(Side.CLIENT)
    fun shouldDisplayTankGui(gui: GuiScreen?): Boolean {
        if (FMLClientHandler.instance().getClient().gameSettings.hideGUI) {
            return false
        }

        if (gui == null) {
            return true
        }

        if (gui is GuiInventory) {
            return false
        }

        return gui is GuiChat
    }

    @JvmStatic
    fun isAABBInBreathableAirBlock(entity: EntityLivingBase): Boolean {
        return isAABBInBreathableAirBlock(entity, false)
    }

    @JvmStatic
    fun isAABBInBreathableAirBlock(entity: EntityLivingBase, testThermal: Boolean): Boolean {
        val y: Double = entity.posY + entity.getEyeHeight()
        val x: Double = entity.posX
        val z: Double = entity.posZ

        val sx: Double = entity.getEntityBoundingBox().maxX - entity.getEntityBoundingBox().minX
        val sy: Double = entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY
        val sz: Double = entity.getEntityBoundingBox().maxZ - entity.getEntityBoundingBox().minZ

        //A good first estimate of head size is that it's the smallest of the entity's 3 dimensions (e.g. front to back, for Steve)
        val smin: Double = min(sx, min(sy, sz)) / 2
        var offsetXZ: Double = 0.0

        // If entity is within air lock wall, check adjacent blocks for oxygen
        // The value is equal to the max distance from an adjacent oxygen block to the edge of a sealer wall
        if (entity.world.getBlockState(entity.getPosition()).getBlock() === GCBlocks.airLockSeal) {
            offsetXZ = 0.75
        }

        return isAABBInBreathableAirBlock(
            entity.world,
            AxisAlignedBB(
                x - smin - offsetXZ,
                y - smin,
                z - smin - offsetXZ,
                x + smin + offsetXZ,
                y + smin,
                z + smin + offsetXZ
            ),
            testThermal
        )
    }

    @JvmStatic
    fun isAABBInBreathableAirBlock(world: World, bb: AxisAlignedBB): Boolean {
        return isAABBInBreathableAirBlock(world, bb, false)
    }

    @JvmStatic
    fun isAABBInBreathableAirBlock(world: World, bb: AxisAlignedBB, testThermal: Boolean): Boolean {
        val avgX: Double = (bb.minX + bb.maxX) / 2.0
        val avgY: Double = (bb.minY + bb.maxY) / 2.0
        val avgZ: Double = (bb.minZ + bb.maxZ) / 2.0

        if (testThermal) {
            return isInOxygenAndThermalBlock(world, bb)
        }

        if (inOxygenBubble(world, avgX, avgY, avgZ)) {
            return true
        }

        return isInOxygenBlock(world, bb)
    }

    @JvmStatic
    fun isInOxygenBlock(world: World, bb: AxisAlignedBB): Boolean {
        val xm: Int = MathHelper.floor(bb.minX + 0.001)
        val xx: Int = MathHelper.floor(bb.maxX - 0.001)
        val ym: Int = MathHelper.floor(bb.minY + 0.001)
        val yy: Int = MathHelper.floor(bb.maxY - 0.001)
        val zm: Int = MathHelper.floor(bb.minZ + 0.001)
        val zz: Int = MathHelper.floor(bb.maxZ - 0.001)

        checked = HashSet<BlockPos?>()
        if (world.isAreaLoaded(BlockPos(xm, ym, zm), BlockPos(xx, yy, zz))) {
            for (x: Int in xm..xx) {
                for (z: Int in zm..zz) {
                    for (y: Int in ym..yy) {
                        val pos: BlockPos = BlockPos(x, y, z)
                        val block: Block = world.getBlockState(pos).getBlock()
                        if (testContactWithBreathableAir(world, block, pos, 0) >= 0) {
                            return true
                        }
                    }
                }
            }
        }

        return false
    }

    @JvmStatic
    fun isInOxygenAndThermalBlock(world: World, bb: AxisAlignedBB): Boolean {
        val i: Int = MathHelper.floor(bb.minX + 0.001)
        val j: Int = MathHelper.floor(bb.maxX - 0.001)
        val k: Int = MathHelper.floor(bb.minY + 0.001)
        val l: Int = MathHelper.floor(bb.maxY - 0.001)
        val i1: Int = MathHelper.floor(bb.minZ + 0.001)
        val j1: Int = MathHelper.floor(bb.maxZ - 0.001)

        checked = HashSet<BlockPos?>()
        if (world.isAreaLoaded(BlockPos(i, k, i1), BlockPos(j, l, j1))) {
            for (x: Int in i..j) {
                for (y: Int in k..l) {
                    for (z: Int in i1..j1) {
                        val pos: BlockPos = BlockPos(x, y, z)
                        val block: Block = world.getBlockState(pos).getBlock()
                        if (testContactWithBreathableAir(world, block, pos, 0) == 1)  // Thermal air has metadata 1
                        {
                            return true
                        }
                    }
                }
            }
        }

        return false
    }

    /*
     * A simplified version of the breathable air check which checks
     * all 6 sides of the given block (because a torch can pass air on all sides)
     * Used in BlockUnlitTorch.
     */
    @JvmStatic
    fun checkTorchHasOxygen(world: World, pos: BlockPos): Boolean {
        if (inOxygenBubble(world, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5)) {
            return true
        }
        checked = HashSet<BlockPos?>()
        val vec: BlockVec3 = BlockVec3(pos)
        for (side: Int in 0..5) {
            val sidevec: BlockVec3 = vec.newVecSide(side)
            val state: IBlockState = (sidevec.getBlockState_noChunkLoad(world))!!
            if (testContactWithBreathableAir(world, state.getBlock(), sidevec.toBlockPos(), 1) >= 0) {
                return true
            }
        }
        return false
    }

    /*
     * Test whether the given block at (x,y,z) coordinates is either:
     * - breathable air (returns true)
     * - solid, or air which is not breathable (returns false)
     * - an air-permeable block, for example a torch, in which case test the surrounding
     * air-reachable blocks (up to 5 blocks away) and return true if breathable air is found
     * in one of them, or false if not.
     */
    @JvmStatic
    @Synchronized
    private fun testContactWithBreathableAir(world: World, block: Block?, pos: BlockPos, limitCount: Int): Int {
        checked!!.add(pos)
        if (block === GCBlocks.breatheableAir || block === GCBlocks.brightBreatheableAir) {
            return (block)!!.getMetaFromState(world.getBlockState(pos))
        }

        val state: IBlockState = world.getBlockState(pos)
        if (block == null || block.getMaterial(state) === Material.AIR) {
            return -1
        }

        //Test for non-sided permeable or solid blocks first
        var permeableFlag: Boolean = false
        if (block !is BlockLeaves) {
            if (block.isOpaqueCube(state)) {
                if (block is BlockGravel || block.getMaterial(state) === Material.CLOTH || block is BlockSponge) {
                    permeableFlag = true
                } else {
                    return -1
                }
            } else if (block is BlockGlass || block is BlockStainedGlass) {
                return -1
            } else if (block is BlockLiquid) {
                return -1
            } else if (OxygenPressureProtocol.nonPermeableBlocks.containsKey(block)) {
                val metaList: ArrayList<Int> = OxygenPressureProtocol.nonPermeableBlocks.get(block)!!
                if (metaList.contains(-1) || metaList.contains(state.getBlock().getMetaFromState(state))) {
                    return -1
                }
            }
        } else {
            permeableFlag = true
        }

        //Testing a non-air, permeable block (for example a torch or a ladder)
        if (limitCount < 5) {
            for (side: EnumFacing in EnumFacing.VALUES) {
                if (permeableFlag || canBlockPassAirOnSide(world, block, pos, side)) {
                    val sidevec: BlockPos =
                        pos.add(side.getXOffset(), side.getYOffset(), side.getZOffset())
                    if (!checked!!.contains(sidevec)) {
                        val newblock: Block = world.getBlockState(sidevec).getBlock()
                        val adjResult: Int = testContactWithBreathableAir(world, newblock, sidevec, limitCount + 1)
                        if (adjResult >= 0) {
                            return adjResult
                        }
                    }
                }
            }
        }

        return -1
    }

    //TODO - performance, could add a 'safe' version of this code (inside world borders)
    //TODO - add more performance increase, these sided checks could be done once only
    @JvmStatic
    private fun canBlockPassAirOnSide(world: World, block: Block, vec: BlockPos, side: EnumFacing): Boolean {
        if (block is IPartialSealableBlock) {
            return !(block as IPartialSealableBlock).isSealed(world, vec, side)
        }

        //Half slab seals on the top side or the bottom side according to its metadata
        if (block is BlockSlab) {
            val state: IBlockState = world.getBlockState(vec)
            val meta: Int = state.getBlock().getMetaFromState(state)
            return !(side == EnumFacing.DOWN && (meta and 8) == 8 || side == EnumFacing.UP && (meta and 8) == 0)
        }

        //Farmland etc only seals on the solid underside
        if (block is BlockFarmland || block is BlockEnchantmentTable || block is BlockLiquid) {
            return side != EnumFacing.UP
        }

        if (block is BlockPistonBase) {
            val state: IBlockState = world.getBlockState(vec)
            if (state.getValue(BlockPistonBase.EXTENDED) as Boolean) {
                val meta0: Int = state.getBlock().getMetaFromState(state)
                val facing: EnumFacing? = BlockPistonBase.getFacing(meta0)
                return side != facing
            }
            return false
        }

        return !block.isSideSolid(world.getBlockState(vec), world, vec, EnumFacing.byIndex(side.getIndex() xor 1))
    }

    @JvmStatic
    fun getDrainSpacing(tank: ItemStack, tank2: ItemStack): Int {
        val tank1Valid: Boolean =
            !tank.isEmpty() && tank.getItem() is ItemOxygenTank && tank.getMaxDamage() - tank.getItemDamage() > 0
        val tank2Valid: Boolean =
            !tank2.isEmpty() && tank2.getItem() is ItemOxygenTank && tank2.getMaxDamage() - tank2.getItemDamage() > 0

        if (!tank1Valid && !tank2Valid) {
            return 0
        }

        return 9
    }

    @JvmStatic
    fun hasValidOxygenSetup(player: EntityPlayerMP): Boolean {
        var missingComponent: Boolean = false

        val stats: GCPlayerStats = (GCPlayerStats.get(player))!!

        if (!isItemValidForPlayerTankInv(0, (stats.getExtendedInventory())!!.getStackInSlot(0))) {
            var handled: Boolean = false

            for (armorStack: ItemStack in player.inventory.armorInventory) {
                if (!armorStack.isEmpty() && armorStack.getItem() is IBreathableArmor) {
                    val breathableArmor: IBreathableArmor = armorStack.getItem() as IBreathableArmor

                    if (breathableArmor.handleGearType(IBreathableArmor.EnumGearType.HELMET)) {
                        if (breathableArmor.canBreathe(armorStack, player, IBreathableArmor.EnumGearType.HELMET)) {
                            handled = true
                        }
                    }
                }
            }

            if (!handled) {
                missingComponent = true
            }
        }

        if (!isItemValidForPlayerTankInv(1, (stats.getExtendedInventory())!!.getStackInSlot(1))) {
            var handled: Boolean = false

            for (armorStack: ItemStack in player.inventory.armorInventory) {
                if (!armorStack.isEmpty() && armorStack.getItem() is IBreathableArmor) {
                    val breathableArmor: IBreathableArmor = armorStack.getItem() as IBreathableArmor

                    if (breathableArmor.handleGearType(IBreathableArmor.EnumGearType.GEAR)) {
                        if (breathableArmor.canBreathe(armorStack, player, IBreathableArmor.EnumGearType.GEAR)) {
                            handled = true
                        }
                    }
                }
            }

            if (!handled) {
                missingComponent = true
            }
        }

        if (!isItemValidForPlayerTankInv(
                2,
                (stats.getExtendedInventory())!!.getStackInSlot(2)
            ) && !isItemValidForPlayerTankInv(3, (stats.getExtendedInventory())!!.getStackInSlot(3))
        ) {
            var handled: Boolean = false

            for (armorStack: ItemStack in player.inventory.armorInventory) {
                if (!armorStack.isEmpty() && armorStack.getItem() is IBreathableArmor) {
                    val breathableArmor: IBreathableArmor = armorStack.getItem() as IBreathableArmor

                    if (breathableArmor.handleGearType(IBreathableArmor.EnumGearType.TANK1)) {
                        if (breathableArmor.canBreathe(armorStack, player, IBreathableArmor.EnumGearType.TANK1)) {
                            handled = true
                        }
                    }

                    if (breathableArmor.handleGearType(IBreathableArmor.EnumGearType.TANK2)) {
                        if (breathableArmor.canBreathe(armorStack, player, IBreathableArmor.EnumGearType.TANK2)) {
                            handled = true
                        }
                    }
                }
            }

            if (!handled) {
                missingComponent = true
            }
        }

        return !missingComponent
    }

    @JvmStatic
    fun isItemValidForPlayerTankInv(slotIndex: Int, stack: ItemStack): Boolean {
        when (slotIndex) {
            0 -> return stack.getItem() is ItemOxygenMask
            1 -> return stack.getItem() is ItemOxygenGear
            2, 3 -> return stack.getItem() is ItemOxygenTank || stack.getItem() is ItemCanisterOxygenInfinite
        }

        return false
    }

    @JvmStatic
    fun getAdjacentFluidConnections(tile: TileEntity?): Array<TileEntity?> {
        return getAdjacentFluidConnections(tile, false)
    }

    @JvmStatic
    fun getAdjacentFluidConnections(tile: TileEntity?, ignoreConnect: Boolean): Array<TileEntity?> {
        val adjacentConnections: Array<TileEntity?> = arrayOfNulls<TileEntity>(EnumFacing.VALUES.size)

        if (tile == null) {
            return adjacentConnections
        }

        val thisVec: BlockVec3 = BlockVec3(tile)
        for (direction: EnumFacing in EnumFacing.VALUES) {
            val tileEntity: TileEntity? = thisVec.getTileEntityOnSide(tile.getWorld(), direction)

            var connectable: Boolean = false
            if (tileEntity is IConnector) {
                connectable =
                    ignoreConnect || (tileEntity as IConnector).canConnect(direction.getOpposite(), NetworkType.FLUID)
            } else if (tileEntity != null) {
                connectable = tileEntity.getCapability<IFluidHandler?>(
                    CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,
                    direction.getOpposite()
                ) != null
            }

            if (connectable) {
                adjacentConnections[direction.ordinal] = tileEntity
            }
        }

        return adjacentConnections
    }

    @JvmStatic
    fun noAtmosphericCombustion(provider: WorldProvider?): Boolean {
        if (provider is IGalacticraftWorldProvider) {
            return (!(provider as IGalacticraftWorldProvider).isGasPresent(EnumAtmosphericGas.OXYGEN) && !(provider as IGalacticraftWorldProvider).hasBreathableAtmosphere())
        }

        return false
    }

    @JvmStatic
    fun inOxygenBubble(worldObj: World, avgX: Double, avgY: Double, avgZ: Double): Boolean {
        val dimID: Int = GCCoreUtil.getDimensionID(worldObj)
        for (blockVec: BlockVec3Dim? in TileEntityOxygenDistributor.Companion.loadedTiles) {
            if (blockVec != null && blockVec.dim == dimID) {
                val tile: TileEntity? = blockVec.getTileEntity()
                if (tile is TileEntityOxygenDistributor) {
                    if (tile.inBubble(avgX, avgY, avgZ)) {
                        return true
                    }
                }
            }
        }

        return false
    }
}
