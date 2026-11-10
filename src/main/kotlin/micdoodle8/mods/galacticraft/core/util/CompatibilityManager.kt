package micdoodle8.mods.galacticraft.core.util

import micdoodle8.mods.galacticraft.core.GCBlocks
import net.minecraft.block.Block
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.WorldServer
import net.minecraft.world.gen.ChunkProviderServer
import net.minecraftforge.fml.common.Loader
import java.lang.Exception
import java.lang.reflect.Method

//import cpw.mods.fml.common.Loader;
//import cpw.mods.fml.common.registry.GameRegistry;
object CompatibilityManager {
    @JvmField
    var PlayerAPILoaded: Boolean = Loader.isModLoaded("PlayerAPI")

    @JvmField
    var RenderPlayerAPILoaded: Boolean = Loader.isModLoaded("RenderPlayerAPI")

    @JvmField
    var modJEILoaded: Boolean = Loader.isModLoaded("jei")

    private var modGTLoaded = false
    private val modTELoaded = Loader.isModLoaded("thermalexpansion")

    private var modAetherIILoaded = false
    private var modBasicComponentsLoaded = false
    private var modAppEngLoaded = false
    private var modPneumaticCraftLoaded = false

    private val modEIOLoaded = Loader.isModLoaded("enderio")

    @JvmField
    var modAALoaded: Boolean = Loader.isModLoaded("actuallyadditions")
    private var spongeLoaded = false
    private var modMatterOverdriveLoaded = false
    private var wailaLoaded = false

    @JvmField
    var isMFRLoaded: Boolean = Loader.isModLoaded("minefactoryreloaded")

    @JvmField
    var isSmartMovingLoaded: Boolean = Loader.isModLoaded("smartmoving")

    @JvmField
    var isTConstructLoaded: Boolean = Loader.isModLoaded("tconstruct")

    @JvmField
    var isWitcheryLoaded: Boolean = Loader.isModLoaded("witchery")

    @JvmField
    var isCubicChunksLoaded: Boolean = false

    //    public static int isBG2Loaded = 0;
    @JvmField
    var classGTOre: Class<*>? = null
    private var spongeOverride: Method? = null

    private var androidPlayerGet: Method? = null
    private var androidPlayerIsAndroid: Method? = null

    @JvmStatic
    fun checkForCompatibleMods() {
        if (Loader.isModLoaded("gregtech") || Loader.isModLoaded("gregtech_addon")) {
            modGTLoaded = true
        }

        if (isMFRLoaded) {
            GCLog.info("Galacticraft: activating MFR compatibility feature.")
        }

        if (modTELoaded) {
            GCLog.info("Galacticraft: activating ThermalExpansion compatibility features.")
        }

        if (isTConstructLoaded) {
            GCLog.info("Galacticraft: activating Tinker's Construct compatibility features.")
        }

        if (Loader.isModLoaded("aetherii")) {
            modAetherIILoaded = true
            GCLog.info("Galacticraft: activating AetherII compatibility feature.")
        }

        if (Loader.isModLoaded("basiccomponents")) {
            modBasicComponentsLoaded = true
        }

        if (Loader.isModLoaded("appliedenergistics2")) {
            modAppEngLoaded = true
            GCLog.info("Galacticraft: activating AppliedEnergistics2 compatibility features.")
        }

        if (Loader.isModLoaded("pneumaticcraft")) {
            modPneumaticCraftLoaded = true
            GCLog.info("Galacticraft: activating PneumaticCraft compatibility features.")
        }

        if (Loader.isModLoaded("waila")) {
            wailaLoaded = true
            GCLog.info("Galacticraft: activating WAILA compatibility features.")
        }

        if (Loader.isModLoaded("sponge")) {
            try {
                spongeOverride =
                    Class.forName("org.spongepowered.common.interfaces.world.gen.IMixinChunkProviderServer")
                        .getMethod("setForceChunkRequests", Boolean::class.javaPrimitiveType)
                spongeLoaded = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (Loader.isModLoaded("cubicchunks")) {
            isCubicChunksLoaded = true
        }

        //TODO      
//        //Compatibility with BattleGear2 - was used by RenderEvolvedSkeleton in 1.7.10
//        try
//        {
//            Class<?> clazz = Class.forName("mods.battlegear2.MobHookContainerClass");
//
//            //accessing this: public static final int Skell_Arrow_Datawatcher = 25;
//            CompatibilityManager.isBG2Loaded = clazz.getField("Skell_Arrow_Datawatcher").getInt(null);
//        }
//        catch (Exception e)
//        {
//        }
        if (Loader.isModLoaded("matteroverdrive")) {
            try {
                val androidPlayer: Class<*> = Class.forName("matteroverdrive.entity.android_player.AndroidPlayer")
                val androidCapability: Class<*> =
                    Class.forName("matteroverdrive.entity.player.MOPlayerCapabilityProvider")
                androidPlayerGet = androidCapability.getMethod("GetAndroidCapability", Entity::class.java)
                androidPlayerIsAndroid = androidPlayer.getMethod("isAndroid")
                modMatterOverdriveLoaded = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @JvmStatic
    fun isTELoaded(): Boolean {
        return modTELoaded
    }

    @JvmStatic
    fun isGTLoaded(): Boolean {
        return modGTLoaded
    }

    @JvmStatic
    fun isAIILoaded(): Boolean {
        return modAetherIILoaded
    }

    @JvmStatic
    fun isAppEngLoaded(): Boolean {
        return modAppEngLoaded
    }

    @JvmStatic
    fun isPneumaticCraftLoaded(): Boolean {
        return modPneumaticCraftLoaded
    }

    @JvmStatic
    fun isWailaLoaded(): Boolean {
        return wailaLoaded
    }

    @JvmStatic
    fun spongeOverrideStart(w: WorldServer?) {
    }

    @JvmStatic
    fun forceLoadChunks(w: WorldServer) {
        if (spongeLoaded) {
            val cps: ChunkProviderServer = w.getChunkProvider()
            try {
                spongeOverride!!.invoke(cps, true)
            } catch (ignore: Exception) {
            }
        }
    }

    @JvmStatic
    fun forceLoadChunksEnd(w: WorldServer) {
        if (spongeLoaded) {
            val cps: ChunkProviderServer = w.getChunkProvider()
            try {
                spongeOverride!!.invoke(cps, false)
            } catch (ignore: Exception) {
            }
        }
    }

    @JvmStatic
    fun registerMicroBlocks() {
        try {
            val clazz: Class<*>? = Class.forName("codechicken.microblock.MicroMaterialRegistry")
            if (clazz != null) {
                var registerMethod: Method? = null
                val methodz: Array<Method> = clazz.getMethods()
                for (m: Method in methodz) {
                    if (m.getName() == "registerMaterial") {
                        registerMethod = m
                        break
                    }
                }
                val clazzbm: Class<*> = Class.forName("codechicken.microblock.BlockMicroMaterial")
                registerMethod!!.invoke(
                    null,
                    clazzbm.getConstructor(Block::class.java, Int::class.javaPrimitiveType)
                        .newInstance(GCBlocks.basicBlock, 3),
                    "tile.gcBlockCore.decoblock1"
                )
                registerMethod.invoke(
                    null,
                    clazzbm.getConstructor(Block::class.java, Int::class.javaPrimitiveType)
                        .newInstance(GCBlocks.basicBlock, 4),
                    "tile.gcBlockCore.decoblock2"
                )
                registerMethod.invoke(
                    null,
                    clazzbm.getConstructor(Block::class.java, Int::class.javaPrimitiveType)
                        .newInstance(GCBlocks.basicBlock, 9),
                    "tile.gcBlockCore.copperBlock"
                )
                registerMethod.invoke(
                    null,
                    clazzbm.getConstructor(Block::class.java, Int::class.javaPrimitiveType)
                        .newInstance(GCBlocks.basicBlock, 10),
                    "tile.gcBlockCore.tinBlock"
                )
                registerMethod.invoke(
                    null,
                    clazzbm.getConstructor(Block::class.java, Int::class.javaPrimitiveType)
                        .newInstance(GCBlocks.basicBlock, 11),
                    "tile.gcBlockCore.aluminumBlock"
                )
                registerMethod.invoke(
                    null,
                    clazzbm.getConstructor(Block::class.java, Int::class.javaPrimitiveType)
                        .newInstance(GCBlocks.basicBlock, 12),
                    "tile.gcBlockCore.meteorironBlock"
                )
                registerMethod.invoke(
                    null,
                    clazzbm.getConstructor(Block::class.java, Int::class.javaPrimitiveType)
                        .newInstance(GCBlocks.blockMoon, 3),
                    "tile.moonBlock.moondirt"
                )
                registerMethod.invoke(
                    null,
                    clazzbm.getConstructor(Block::class.java, Int::class.javaPrimitiveType)
                        .newInstance(GCBlocks.blockMoon, 4),
                    "tile.moonBlock.moonstone"
                )
                registerMethod.invoke(
                    null,
                    clazzbm.getConstructor(Block::class.java, Int::class.javaPrimitiveType)
                        .newInstance(GCBlocks.blockMoon, 5),
                    "tile.moonBlock.moongrass"
                )
                registerMethod.invoke(
                    null,
                    clazzbm.getConstructor(Block::class.java, Int::class.javaPrimitiveType)
                        .newInstance(GCBlocks.blockMoon, 14),
                    "tile.moonBlock.bricks"
                )
                GCLog.info("Galacticraft: activating CodeChicken Microblocks compatibility.")
            }
        } catch (e: Exception) {
        }
    }

    @JvmStatic
    fun isAndroid(player: EntityPlayer?): Boolean {
        if (modMatterOverdriveLoaded) {
//          Equivalent to:
//            AndroidPlayer androidPlayer = AndroidPlayer.get(player);
//            return (androidPlayer != null && androidPlayer.isAndroid());
            try {
                val androidPlayer: Any? = androidPlayerGet!!.invoke(null, player)
                if (androidPlayer != null) {
                    return ((micdoodle8.mods.galacticraft.core.util.CompatibilityManager.androidPlayerIsAndroid)!!.invoke(
                        androidPlayer
                    ) as kotlin.Boolean?)!!
                }
            } catch (ignore: Exception) {
            }
        }
        return false
    }

    @JvmStatic
    fun useAluDust(): Boolean {
        return modAppEngLoaded || modTELoaded || modEIOLoaded || modAALoaded
    }
}
