package micdoodle8.mods.galacticraft.planets.asteroids.blocks

import foundation.block.getStateFromMeta

import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.items.ItemBlockDesc
import micdoodle8.mods.galacticraft.core.items.ItemBlockGC
import micdoodle8.mods.galacticraft.planets.asteroids.items.ItemBlockAsteroids
import micdoodle8.mods.galacticraft.planets.asteroids.items.ItemBlockShortRangeTelepad
import micdoodle8.mods.galacticraft.planets.asteroids.items.ItemBlockWalkway
import net.minecraft.world.level.block.Block
import net.minecraft.item.ItemBlock
import net.minecraft.world.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

object AsteroidBlocks {
    @JvmField
    var blockWalkway: Block? = null

    @JvmField
    var blockBasic: Block? = null

    //	public static Block machineFrame;
    @JvmField
    var beamReflector: Block? = null

    @JvmField
    var beamReceiver: Block? = null

    @JvmField
    var shortRangeTelepad: Block? = null

    @JvmField
    var fakeTelepad: Block? = null

    @JvmField
    var blockDenseIce: Block? = null

    @JvmField
    var blockMinerBase: Block? = null

    @JvmField
    var minerBaseFull: Block? = null

    @JvmField
    var spaceWart: Block? = null

    @JvmStatic
    fun initBlocks() {
        blockWalkway = BlockWalkway("walkway")
        blockBasic = BlockBasicAsteroids("asteroids_block")
        beamReflector = BlockBeamReflector("beam_reflector")
        beamReceiver = BlockBeamReceiver("beam_receiver")
        shortRangeTelepad = BlockShortRangeTelepad("telepad_short")
        fakeTelepad = BlockTelepadFake("telepad_fake")
        blockDenseIce = BlockIceAsteroids("dense_ice")
        blockMinerBase = BlockMinerBase("miner_base")
        minerBaseFull = BlockMinerBaseFull("miner_base_full")
        spaceWart = BlockSpaceWart("spacewart")
    }

    @JvmStatic
    fun registerBlock(block: Block?, itemClass: Class<out ItemBlock>?) {
        GCBlocks.registerBlock(block, itemClass)
    }

    @JvmStatic
    fun registerBlocks() {
        registerBlock(blockBasic, ItemBlockAsteroids::class.java)
        registerBlock(blockWalkway, ItemBlockWalkway::class.java)
        registerBlock(beamReflector, ItemBlockDesc::class.java)
        registerBlock(beamReceiver, ItemBlockDesc::class.java)
        registerBlock(shortRangeTelepad, ItemBlockShortRangeTelepad::class.java)
        registerBlock(fakeTelepad, null)
        registerBlock(blockDenseIce, ItemBlockGC::class.java)
        registerBlock(blockMinerBase, ItemBlockDesc::class.java)
        registerBlock(minerBaseFull, null)
        registerBlock(spaceWart, null)
    }

    @JvmStatic
    private fun setHarvestLevel(block: Block?, toolClass: String, level: Int, meta: Int) {
        block!!.setHarvestLevel(toolClass, level, block.getStateFromMeta(meta))
    }

    @JvmStatic
    private fun setHarvestLevel(block: Block, toolClass: String, level: Int) {
        block.setHarvestLevel(toolClass, level)
    }

    @JvmStatic
    fun setHarvestLevels() {
        setHarvestLevel(blockBasic, "pickaxe", 0, 0) //Rock
        setHarvestLevel(blockBasic, "pickaxe", 0, 1) //Rock
        setHarvestLevel(blockBasic, "pickaxe", 0, 2) //Rock
        setHarvestLevel(blockBasic, "pickaxe", 2, 3) //Aluminium
        setHarvestLevel(blockBasic, "pickaxe", 3, 4) //Ilmenite
        setHarvestLevel(blockBasic, "pickaxe", 2, 5) //Iron
    }

    @JvmStatic
    fun oreDictRegistration() {
        OreDictionary.registerOre("oreAluminum", ItemStack(blockBasic, 1, 3))
        OreDictionary.registerOre("oreAluminium", ItemStack(blockBasic, 1, 3))
        OreDictionary.registerOre("oreNaturalAluminum", ItemStack(blockBasic, 1, 3))
        OreDictionary.registerOre("oreIlmenite", ItemStack(blockBasic, 1, 4))
        OreDictionary.registerOre("oreIron", ItemStack(blockBasic, 1, 5))

        OreDictionary.registerOre("blockTitanium", ItemStack(blockBasic, 1, 7))
    }
}
