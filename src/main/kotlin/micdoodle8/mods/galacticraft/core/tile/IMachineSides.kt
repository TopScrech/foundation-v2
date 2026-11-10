package micdoodle8.mods.galacticraft.core.tile

import micdoodle8.mods.galacticraft.core.tile.IMachineSidesProperties.MachineSidesModel

import micdoodle8.mods.galacticraft.api.tile.ITileClientUpdates
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.IBlockState
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.Arrays

/**
 * Used as a common interface for any TileEntity with configurable power, pipe, etc sides
 *
 */
interface IMachineSides : ITileClientUpdates {
    /**
     * The different sides which a MachineSide (e.g. electric input)
     * can be set to.  These are relative to the front of the machine
     * (looking at the machine from the normal front).
     */
    enum class Face
        (private val serializedName: String, private val next: Int, private val prior: Int) {
        LEFT("l", 2, 1),
        RIGHT("r", 0, 2),
        REAR("b", 1, 0),
        TOP("u", 4, 4),
        BOTTOM("d", 3, 3),
        NOT_SET("n", 5, 5),
        NOT_CONFIGURABLE("fail", 6, 6);

        fun getName(): String {
            return this.serializedName
        }

        fun next(): Face? {
            return entries[this.next]
        }

        fun prior(): Face? {
            return entries[this.prior]
        }

        companion object {
            @JvmField
            var Horizontals: Array<Face> = arrayOf<Face>(LEFT, REAR, RIGHT)

            @JvmField
            var AllAvailable: Array<Face> = arrayOf<Face>(LEFT, REAR, RIGHT, TOP, BOTTOM)
        }
    }

    /*
     * All the different possible types of configurable side.
     * 
     * Each of these can correspond to a texture and a function
     * according to the block type.  Many may be unused.
     * 
     * this.listConfigurableSides() provides a list of which ones
     * the tile actually uses.
     */
    enum class MachineSide
        (private val serializedName: String) {
        FRONT("front"),
        REARDECO("reardeco"),
        PLAIN("plain"),
        TOP("top"),
        BASE("bottom"),
        ELECTRIC_IN("elec_in"),
        ELECTRIC_OUT("elec_out"),
        PIPE_IN("pipe_in"),
        PIPE_OUT("pipe_out"),
        CUSTOM("custom")
    }


    //--------------METHODS--------------
    /**
     * The front of this machine, with the graphic.
     * This CANNOT be set: use a wrench to change the block's facing to change it
     * (If player wants to change the front then rotate the block's facing and all other sides)
     */
    fun getFront(): EnumFacing?

    /**
     * Used internally by tileEntity logic
     */
    fun getElectricInputDirection(): EnumFacing? {
        return null
    }

    /**
     * Used internally by tileEntity logic
     */
    fun getElectricOutputDirection(): EnumFacing? {
        return null
    }

    /**
     * Used internally by tileEntity logic
     */
    fun getPipeInputDirection(): EnumFacing? {
        return null
    }

    /**
     * Used internally by tileEntity logic
     */
    fun getPipeOutputDirection(): EnumFacing? {
        return null
    }

    /**
     * Used internally by tileEntity logic
     */
    fun getCustomDirection(): EnumFacing? {
        return null
    }

    /*
     * Returns true if the setting succeeded
     * (Also returns true if it is an attempt to set it
     * to the same value as it is currently.)
     * 
     * Returns false if the MachineSide is not configurable.
     * 
     * Use isValidForSide() first to test whether the setting
     * is valid for the side here.
     */
    fun setSide(sideToSet: MachineSide?, newSide: Face?): Boolean {
        if (this.getConfigurationType() === IMachineSidesProperties.Companion.NOT_CONFIGURABLE) {
            return false
        }

        for (msp: MachineSidePack in this.getAllMachineSides()!!) {
            if (msp.test(sideToSet)) {
                msp.set(newSide)
                return true
            }
        }

        return false
    }

    /**
     * Like setSide() but using index for performance
     * - for internal use
     *
     * index matches the index in getConfigurableSides()
     */
    fun setSide(index: Int, newSide: Face?): Boolean {
        if (this.getConfigurationType() === IMachineSidesProperties.Companion.NOT_CONFIGURABLE) {
            return false
        }

        val msps: Array<MachineSidePack> = this.getAllMachineSides()!!
        if (index >= 0 && index < msps.size) {
            msps[index].set(newSide)
            return true
        }

        return false
    }

    /**
     * Like getSide(MachineSide) but using index for performance
     * - for internal use
     *
     * index matches the index in getConfigurableSides()
     */
    fun getSide(index: Int): Face {
        val msps: Array<MachineSidePack> = this.getAllMachineSides()!!
        if (index >= 0 && index < msps.size) return msps[index].get()

        return Face.NOT_CONFIGURABLE
    }

    fun getSide(sideToGet: MachineSide?): Face? {
        for (msp: MachineSidePack in this.getAllMachineSides()!!) {
            if (msp.test(sideToGet)) {
                return msp.get()
            }
        }
        return Face.NOT_CONFIGURABLE
    }

    /*
     * Like getSide() but results limited to sides
     * which the blockstate is allowed to render
     * 
     * index matches the index in getConfigurableSides()
     */
    fun getAllowedSide(index: Int): Face? {
        val test: Face? = this.getSide(index)
        for (face: Face? in this.allowableFaces()!!) {
            if (face == test) return test
        }
        return this.listDefaultFaces()!![index]
    }

    /**
     * Returns true if the machine side can be set to this face
     *
     * By default, returns all allowable configurable faces
     *
     * Overide this for special tile logic for specific sides
     */
    fun isValidForSide(sideToSet: MachineSide?, newSide: Face?): Boolean {
        for (msp: MachineSidePack in this.getAllMachineSides()!!) {
            if (msp.test(sideToSet)) {
                return Arrays.asList<Face>(*(this.allowableFaces()!!)).contains(newSide)
            }
        }


        //TODO: could maybe return true for default settings for this.getConfigurationType()
        //e.g. front == front, top == top, base == bottom
        return false
    }

    /**
     * Can be used for rendering this machine in a GUI
     */
    fun textureTypeLeft(): MachineSide? {
        for (msp: MachineSidePack in this.getAllMachineSides()!!) {
            if (msp.get() == Face.LEFT) {
                return msp.getRenderType()
            }
        }

        return MachineSide.PLAIN
    }

    /**
     * Can be used for rendering this machine in a GUI
     */
    fun textureTypeRight(): MachineSide? {
        for (msp: MachineSidePack in this.getAllMachineSides()!!) {
            if (msp.get() == Face.RIGHT) {
                return msp.getRenderType()
            }
        }

        return MachineSide.PLAIN
    }

    /**
     * Can be used for rendering this machine in a GUI
     */
    fun textureTypeRear(): MachineSide? {
        for (msp: MachineSidePack in this.getAllMachineSides()!!) {
            if (msp.get() == Face.REAR) {
                return msp.getRenderType()
            }
        }
        return MachineSide.PLAIN
    }

    /**
     * Can be used for rendering this machine in a GUI
     */
    fun textureTypeTop(): MachineSide? {
        for (msp: MachineSidePack in this.getAllMachineSides()!!) {
            if (msp.get() == Face.TOP) {
                return msp.getRenderType()
            }
        }
        return MachineSide.TOP
    }

    /**
     * Can be used for rendering this machine in a GUI
     */
    fun textureTypeBase(): MachineSide? {
        for (msp: MachineSidePack in this.getAllMachineSides()!!) {
            if (msp.get() == Face.BOTTOM) {
                return msp.getRenderType()
            }
        }

        return MachineSide.BASE
    }

    /**
     * Essential for block rendering - use in getActualState()
     * Automatically returns a valid property as long as getConfigurationType() matches the property
     * (see BlockMachineTiered for an example)
     *
     * Override this if you want more options.
     */
    fun buildBlockStateProperty(): IMachineSidesProperties.MachineSidesModel? {
        val length: Int = (listConfigurableSides())!!.size
        when (length) {
            1 -> return IMachineSidesProperties.Companion.getModelForOneFace((this.getAllowedSide(0))!!)
                .validFor((getConfigurationType())!!)

            2 -> return IMachineSidesProperties.Companion.getModelForTwoFaces(
                (this.getAllowedSide(0))!!,
                (this.getAllowedSide(1))!!
            ).validFor((getConfigurationType())!!)

            else -> return IMachineSidesProperties.Companion.getModelForTwoFaces(
                (this.getAllowedSide(0))!!,
                (this.getAllowedSide(1))!!
            ).validFor((getConfigurationType())!!)
        }
    }

    /**
     * For testing purposes - Sneak Wrench to activate this
     */
    fun nextSideConfiguration(te: TileEntity) {
        if (this.getConfigurationType() === IMachineSidesProperties.Companion.NOT_CONFIGURABLE) {
            return
        }

        val length: Int = (listConfigurableSides())!!.size


        //TODO: adapt result of Face.next() and Face.prior() according to Horiz or All models
        when (length) {
            1 -> this.setSide(0, this.getSide(0).next())
            2 -> {
                val leadingSide: Face = this.getSide(0)
                if (this.getSide(1) != leadingSide.next()) {
                    this.setSide(1, leadingSide.prior())
                    this.setSide(0, leadingSide.next())
                } else {
                    this.setSide(1, leadingSide.prior())
                }
            }

            else -> {}
        }

        if (te.getWorld().isRemote) {
            te.getWorld().markBlockRangeForRenderUpdate(te.getPos(), te.getPos())
        } else {
            this.updateAllInDimension()
        }
    }

    /**
     * Array with all the configurable sides for this machine
     * (not including the front)
     *
     * Any sides not included in this list are defaults:
     * PLAIN for left, right and rear sides
     * TOP for top
     * BASE for bottom
     */
    fun listConfigurableSides(): Array<MachineSide>?

    /**
     * Array with the default faces to match entries in listConfigurableSides()
     */
    fun listDefaultFaces(): Array<Face>?


    /**
     * All the different allowable faces which the configurable sides can be configured to
     * (for example it might be horizontal faces only, for this machine)
     */
    fun allowableFaces(): Array<Face>? {
        return this.getConfigurationType()!!.allowableFaces()
    }

    /**
     * The type of configuration (one or two sides to configure,
     * only horizontal settings or all allowed) - this needs to match
     * the blockstate model implementation, take it from the Block.
     * (Look at TileEnergyStorageModule for an example.)
     */
    fun getConfigurationType(): IMachineSidesProperties?

    /**
     * Call this in getAllMachineSides() or else in the class constructor
     */
    fun initialiseSides() {
        val length: Int = (listConfigurableSides())!!.size
        this.setupMachineSides(length)
        for (i: Int in 0..<length) {
            this.getAllMachineSides()!![i] =
                IMachineSides.MachineSidePack(listConfigurableSides()!![i]!!, listDefaultFaces()!![i])
        }
    }

    /**
     * The array storing all configurable machine sides
     * and their current settings in this tile.
     *
     * IMPORTANT: Run initialiseSides() in the first call to this
     * if it would otherwise be null - see examples in GC code
     */
    fun getAllMachineSides(): Array<MachineSidePack>?

    /**
     * Create an array of MachineSidePack[length]
     * which can later be get using getAllMachineSides()
     */
    fun setupMachineSides(length: Int)

    fun addMachineSidesToNBT(par1nbtTagCompound: NBTTagCompound) {
        val tagList: NBTTagList = NBTTagList()
        for (msp: MachineSidePack in this.getAllMachineSides()!!) {
            val tag: NBTTagCompound = NBTTagCompound()
            msp.writeToNBT(tag)
            tagList.appendTag(tag)
        }
        par1nbtTagCompound.setTag("macsides", tagList)
    }


    fun readMachineSidesFromNBT(par1nbtTagCompound: NBTTagCompound) {
        val tagList: NBTTagList = par1nbtTagCompound.getTagList("macsides", 10)
        val msps: Array<MachineSidePack> = this.getAllMachineSides()!!
        if (tagList.tagCount() == msps.size) {
            for (i: Int in 0..<tagList.tagCount()) {
                msps[i].readFromNBT(tagList.getCompoundTagAt(i))
            }
        }
    }

    override fun buildDataPacket(data: IntArray?) {
        val msps: Array<MachineSidePack> = this.getAllMachineSides()!!
        for (i: Int in msps.indices) {
            (data)!![i] = msps[i].get().ordinal
        }
    }

    @SideOnly(Side.CLIENT)
    override fun updateClient(data: MutableList<Any?>?) {
        val msps: Array<MachineSidePack> = this.getAllMachineSides()!!
        for (i: Int in msps.indices) {
            msps[i].set(((data)!!.get(i + 1) as kotlin.Int?)!!)
        }
        val pos: BlockPos = (this as TileEntity).getPos()
        (this as TileEntity).getWorld().markBlockRangeForRenderUpdate(pos, pos)
    }

    /**
     * A container object for Machine Sides settings
     * This should be implemented in an array by each TileEntity
     *
     */
    class MachineSidePack
    constructor(type: MachineSide, defaultFace: Face?) {
        private val theSide: MachineSide
        private var currentFace: Face? = null

        init {
            checkNotNull(type)
            this.theSide = type
            this.set(defaultFace)
        }

        open fun writeToNBT(tag: NBTTagCompound) {
            tag.setInteger("ts", this.theSide.ordinal)
            tag.setInteger("cf", this.currentFace!!.ordinal)
        }

        open fun readFromNBT(tag: NBTTagCompound) {
            var ts: Int = -1
            var cf: Int = -1
            if (tag.hasKey("ts")) {
                ts = tag.getInteger("ts")
            }
            if (tag.hasKey("cf")) {
                cf = tag.getInteger("cf")
            }
            if (ts == theSide.ordinal && cf >= 0 && cf < Face.entries.size) {
                this.currentFace = Face.entries[cf]
            }
        }

        open fun set(face: Face?) {
            this.currentFace = if (face == null) Face.NOT_SET else face
        }

        open fun set(index: Int) {
            if (index >= 0 && index < Face.entries.size) this.currentFace = Face.entries[index]
        }

        open fun get(): Face {
            return this.currentFace!!
        }

        open fun getRenderType(): MachineSide? {
            return this.theSide
        }

        open fun test(type: MachineSide?): Boolean {
            return this.theSide == type
        }
    }

    companion object {
        /**
         * Call this from a Block's getActualState() method
         *
         * @param state   The blockstate prior to addition of this property
         * @param tile    The tile entity matching the block position (null will return a default value)
         * @param renderType  The calling block's MACHINESIDES_RENDERTYPE
         * @param key     The name given to the machine sides property in the calling block e.g. SIDES
         *
         * @return   A blockstate with the property added
         */
        @JvmStatic
        fun addPropertyForTile(
            state: IBlockState,
            tile: TileEntity?,
            renderType: IMachineSidesProperties,
            key: PropertyEnum<IMachineSidesProperties.MachineSidesModel>
        ): IBlockState {
            if (tile is IMachineSides) {
                val tileSides: IMachineSides = tile as IMachineSides
                return state.withProperty(
                    key,
                    (tileSides.buildBlockStateProperty())!!
                )
            } else return state.withProperty(
                key,
                renderType.getDefault()
            )
        }
    }
}
