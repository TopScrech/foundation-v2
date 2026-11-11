package micdoodle8.mods.galacticraft.core.tile

import micdoodle8.mods.miccore.Annotations.NetworkedField

import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.blocks.BlockMachineTiered
import micdoodle8.mods.galacticraft.core.energy.item.ItemElectricBase
import micdoodle8.mods.galacticraft.core.energy.tile.TileBaseElectricBlockWithInventory
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.miccore.Annotations
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.Items
import net.minecraft.world.WorldlyContainer as ISidedInventory
import net.minecraft.world.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.core.NonNullList
import net.minecraftforge.fml.LogicalSide as Side
import java.util.HashSet

open class TileEntityElectricFurnace @JvmOverloads constructor(tier: Int = 1) :
    TileBaseElectricBlockWithInventory(), ISidedInventory, IMachineSides {
    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var processTimeRequired: Int = PROCESS_TIME_REQUIRED

    @JvmField
    @Annotations.NetworkedField(targetSide = Side.CLIENT)
    var processTicks: Int = 0

    private var stacks: NonNullList<ItemStack> = NonNullList.withSize<ItemStack>(4, ItemStack.EMPTY)

    @JvmField
    val playersUsing: MutableSet<EntityPlayer> = HashSet<EntityPlayer>()

    private var initialised = false

    private fun setTier2() {
        this.storage.setCapacity(25000f)
        this.storage.setMaxExtract((if (ConfigManagerCore.hardMode) 90 else 60).toFloat())
        this.processTimeRequired = 100
        this.setTierGC(2)
    }

    override fun update() {
        if (!this.initialised) {
            val metadata: Int = this.getBlockMetadata()
            //for version update compatibility
            val b: Block = this.world.getBlockState(this.getPos()).getBlock()
            if (b === GCBlocks.machineBase) {
                this.world.setBlock(this.getPos(), (GCBlocks.machineTiered)!!.defaultBlockState(),  /*,s 4*/2)
            } else if (metadata >= 8) {
                this.setTier2()
            }
            this.initialised = true
        }

        super.update()

        if (!this.world.isRemote) {
            if (this.canProcess()) {
                if (this.hasEnoughEnergyToRun) {
                    //100% extra speed boost for Tier 2 machine if powered by Tier 2 power
                    if (this.tierGC >= 2) {
                        this.processTimeRequired = PROCESS_TIME_REQUIRED / 2 / this.poweredByTierGC
                    }

                    if (this.processTicks == 0) {
                        this.processTicks = this.processTimeRequired
                    } else {
                        if (--this.processTicks <= 0) {
                            this.smeltItem()
                            this.processTicks = if (this.canProcess()) this.processTimeRequired else 0
                        }
                    }
                } else if (this.processTicks > 0 && this.processTicks < this.processTimeRequired) {
                    //Apply a "cooling down" process if the electric furnace runs out of energy while smelting
                    if (this.world.rand.nextInt(4) == 0) {
                        this.processTicks++
                    }
                }
            } else {
                this.processTicks = 0
            }
        } else {
            //Smoother client side animation before the networked fields get updated
            if (this.processTicks > 0 && this.processTicks < this.processTimeRequired) {
                this.processTicks--
            }
        }
    }

    /**
     * @return Is this machine able to process its specific task?
     */
    open fun canProcess(): Boolean {
        if (this.stacks.get(1).isEmpty()) {
            return false
        }
        val result: ItemStack = FurnaceRecipes.instance().getSmeltingResult(this.stacks.get(1))
        if (result.isEmpty()) {
            return false
        }


        if (this.tierGC == 1) {
            if (!this.stacks.get(2).isEmpty()) {
                return (this.stacks.get(2).isItemEqual(result) && this.stacks.get(2).getCount() < 64)
            }
        }


        //Electric Arc Furnace
        if (this.stacks.get(2).isEmpty() || this.stacks.get(3).isEmpty()) {
            return true
        }
        var space: Int = 0
        if (this.stacks.get(2).isItemEqual(result)) {
            space = 64 - this.stacks.get(2).getCount()
        }
        if (this.stacks.get(3).isItemEqual(result)) {
            space += 64 - this.stacks.get(3).getCount()
        }
        return space >= 2
    }

    /**
     * Turn one item from the furnace source stack into the appropriate smelted
     * item in the furnace result stack
     */
    open fun smeltItem() {
        if (this.canProcess()) {
            val resultItemStack: ItemStack = FurnaceRecipes.instance().getSmeltingResult(this.stacks.get(1))
            var doubleResult: Boolean = false
            if (this.tierGC > 1) {
                val nameSmelted: String = this.stacks.get(1).getTranslationKey().toLowerCase()
                if ((resultItemStack.getTranslationKey().toLowerCase()
                        .contains("ingot") || resultItemStack.getItem() === Items.QUARTZ) && (nameSmelted.contains("ore") || nameSmelted.contains(
                        "raw"
                    ) || nameSmelted.contains("moon") || nameSmelted.contains("mars") || nameSmelted.contains("shard"))
                ) {
                    doubleResult = true
                }
            }

            if (doubleResult) {
                var space2: Int = 0
                var space3: Int = 0
                if (this.stacks.get(2).isEmpty()) {
                    this.stacks.set(2, resultItemStack.copy())
                    this.stacks.get(2).grow(resultItemStack.getCount())
                    space2 = 2
                } else if (this.stacks.get(2).isItemEqual(resultItemStack)) {
                    space2 = (64 - this.stacks.get(2).getCount()) / resultItemStack.getCount()
                    if (space2 > 2) space2 = 2
                    this.stacks.get(2).grow(resultItemStack.getCount() * space2)
                }
                if (space2 < 2) {
                    if (this.stacks.get(3).isEmpty()) {
                        this.stacks.set(3, resultItemStack.copy())
                        if (space2 == 0) {
                            this.stacks.get(3).grow(resultItemStack.getCount())
                        }
                    } else if (this.stacks.get(3).isItemEqual(resultItemStack)) {
                        space3 = (64 - this.stacks.get(3).getCount()) / resultItemStack.getCount()
                        if (space3 > 2 - space2) space3 = 2 - space2
                        this.stacks.get(3).grow(resultItemStack.getCount() * space3)
                    }
                }
            } else if (this.stacks.get(2).isEmpty()) {
                this.stacks.set(2, resultItemStack.copy())
            } else if (this.stacks.get(2).isItemEqual(resultItemStack)) {
                this.stacks.get(2).grow(resultItemStack.getCount())
            }

            this.stacks.get(1).shrink(1)
        }
    }

    override fun readFromNBT(nbt: NBTTagCompound) {
        super.readFromNBT(nbt)
        if (this.storage.getEnergyStoredGC() > STANDARD_CAPACITY) {
            this.setTier2()
            this.initialised = true
        } else {
            this.initialised = false
        }
        this.processTicks = nbt.getIntOr("smeltingTicks", 0)
        this.stacks = this.readStandardItemsFromNBT(nbt)

        this.readMachineSidesFromNBT(nbt) //Needed by IMachineSides
    }

    override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
        if (this.tierGC == 1 && this.storage.getEnergyStoredGC() > STANDARD_CAPACITY) {
            this.storage.setEnergyStored(STANDARD_CAPACITY)
        }
        super.writeToNBT(nbt)
        nbt.putInt("smeltingTicks", this.processTicks)
        this.writeStandardItemsToNBT(nbt, this.stacks)

        this.addMachineSidesToNBT(nbt) //Needed by IMachineSides

        return nbt
    }

    override fun getContainingItems(): NonNullList<ItemStack>? {
        return this.stacks
    }

    override fun getContainerSize(): Int {
        return if (this.tierGC == 1 && this.initialised) 3 else 4
    }

    override fun getName(): net.minecraft.network.chat.Component {
        return net.minecraft.network.chat.Component.translatable(if (this.tierGC == 1) "tile.machine.2.name" else "tile.machine.7.name")
    }

    //    @Override
    //    public boolean hasCustomName()
    //    {
    //        return true;
    //    }
    /**
     * Returns true if automation is allowed to insert the given stack (ignoring
     * stack size) into the given slot.
     */
    override fun canPlaceItem(slotID: Int, itemStack: ItemStack): Boolean {
        if (itemStack.isEmpty()) {
            return false
        }
        return if (slotID == 1) !FurnaceRecipes.instance().getSmeltingResult(itemStack)
            .isEmpty() else slotID == 0 && ItemElectricBase.isElectricItem(itemStack.getItem())
    }

    override fun getSlotsForFace(side: EnumFacing): IntArray {
        if (this.tierGC == 2) {
            return intArrayOf(0, 1, 2, 3)
        }
        return intArrayOf(0, 1, 2)
    }

    override fun canPlaceItemThroughFace(slotID: Int, par2ItemStack: ItemStack, par3: EnumFacing?): Boolean {
        return this.canPlaceItem(slotID, par2ItemStack)
    }

    override fun canTakeItemThroughFace(slotID: Int, par2ItemStack: ItemStack, par3: EnumFacing): Boolean {
        return slotID == 2 || this.tierGC == 2 && slotID == 3
    }

    override fun shouldUseEnergy(): Boolean {
        return this.canProcess()
    }

    override fun hasCustomName(): Boolean {
        return false
    }

    override fun getFront(): EnumFacing {
        val state: IBlockState = this.world.getBlockState(getPos())
        if (state.getBlock() is BlockMachineTiered) {
            return state.getValue(BlockMachineTiered.FACING)
        }
        return EnumFacing.NORTH
    }

    override fun getElectricInputDirection(): EnumFacing {
        when (this.getSide(IMachineSides.MachineSide.ELECTRIC_IN)) {
            IMachineSides.Face.RIGHT -> return getFront().rotateYCCW()
            IMachineSides.Face.REAR -> return getFront().getOpposite()
            IMachineSides.Face.TOP -> return EnumFacing.UP
            IMachineSides.Face.BOTTOM -> return EnumFacing.DOWN
            IMachineSides.Face.LEFT -> return getFront().rotateY()
            else -> return getFront().rotateY()
        }
    }

    //------------------
    //Added these methods and field to implement IMachineSides properly 
    //------------------
    override fun listConfigurableSides(): Array<IMachineSides.MachineSide> {
        return arrayOf<IMachineSides.MachineSide>(IMachineSides.MachineSide.ELECTRIC_IN)
    }

    override fun listDefaultFaces(): Array<IMachineSides.Face> {
        return arrayOf<IMachineSides.Face>(IMachineSides.Face.LEFT)
    }

    private var machineSides: Array<IMachineSides.MachineSidePack?>? = null

    /*
     * @param tier: 1 = Electric Furnace  2 = Electric Arc Furnace
     */
    init {
run initialization@ {

        this.initialised = true
        if (tier == 1) {
            this.storage.setMaxExtract((if (ConfigManagerCore.hardMode) 60 else 45).toFloat())
            return@initialization
        }

        this.setTier2()
    
}
}

    override fun getAllMachineSides(): Array<IMachineSides.MachineSidePack> {
        if (this.machineSides == null) {
            this.initialiseSides()
        }

        return ((this.machineSides as Array<IMachineSides.MachineSidePack>?))!!
    }

    override fun setupMachineSides(length: Int) {
        this.machineSides = arrayOfNulls<IMachineSides.MachineSidePack>(length)
    }

    override fun onLoad() {
        this.clientOnLoad()
    }

    override fun getConfigurationType(): IMachineSidesProperties? {
        return BlockMachineTiered.MACHINESIDES_RENDERTYPE
    } //------------------END OF IMachineSides implementation

    companion object {
        //The electric furnace is 50% faster than a vanilla Furnace
        //but at a cost of some inefficiency:
        //It uses 46800 gJ to smelt 8 ingots quickly
        //compared with the energy generated by 1 coal which is 38400 gJ
        //
        //The efficiency can be increased using a Tier 2 furnace
        @JvmField
        var PROCESS_TIME_REQUIRED: Int = 130
    }
}
