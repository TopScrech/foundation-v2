package galaxyspace.core

import galaxyspace.GalaxySpace
import galaxyspace.core.prefab.blocks.BlockFluidGS
import galaxyspace.core.prefab.blocks.BlockFluidNatureGas
import galaxyspace.systems.SolarSystem.moons.titan.blocks.BlockLiquidEthaneMethane
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import net.minecraft.world.level.block.Block
import net.minecraft.block.material.EnumPushReaction
import net.minecraft.world.level.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.block.material.MaterialLiquid
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry

object GSFluids {
    @JvmField
    var LiquidEthaneMethane: Fluid? = null

    @JvmField
    var Helium3: Fluid? = null

    @JvmField
    var Ethane: Fluid? = null

    @JvmField
    var HeliumHydrogen: Fluid? = null

    @JvmField
    var NatureGas: Fluid? = null

    @JvmField
    var BLOCK_LEMETHANE: Block? = null

    @JvmField
    var BLOCK_HELIUM3: Block? = null

    @JvmField
    var BLOCK_ETHANE: Block? = null

    @JvmField
    var BLOCK_HELIUM_HYDROGEN: Block? = null

    @JvmField
    var BLOCK_NATURE_GAS: Block? = null

    @JvmField
    val LEMethane: Material = object : MaterialLiquid(MapColor.BROWN) {
        override fun getPushReaction(): EnumPushReaction {
            return EnumPushReaction.DESTROY
        }
    }

    @JvmField
    val HELIUM: Material = object : MaterialLiquid(MapColor.SNOW) {
        override fun getPushReaction(): EnumPushReaction {
            return EnumPushReaction.DESTROY
        }
    }

    @JvmField
    val HH: Material = object : MaterialLiquid(MapColor.PINK) {
        override fun getPushReaction(): EnumPushReaction {
            return EnumPushReaction.DESTROY
        }
    }

    @JvmStatic
    fun initialize() {
        LiquidEthaneMethane = registerFluid("liquidethanemethane", 1200, 140, 90, false, "liquid_ethanemethane")
        Helium3 = registerFluid("helium3", -1000, 1000, 0, true, "liquid_helium3")
        Ethane = registerFluid("ethane", -1000, 1000, 0, true, "liquid_ethane")
        HeliumHydrogen = registerFluid("heliumhydrogen", 1200, 140, 90, false, "liquid_heliumhydrogen")

        NatureGas = registerFluid("naturegas", -800, 1000, 10, true, "liquid_naturegas")

        (GalacticraftCore.proxy)!!.registerFluidTexture(
            LiquidEthaneMethane,
            ResourceLocation.fromNamespaceAndPath(GalaxySpace.Companion.ASSET_PREFIX, "textures/fluids/liquid_ethanemethane_still.png")
        )

        BlockFluidRegistration()
    }

    @JvmStatic
    fun BlockFluidRegistration() {
        BLOCK_LEMETHANE = BlockLiquidEthaneMethane()
        BLOCK_HELIUM3 = BlockFluidGS((Helium3)!!, HELIUM, false)
        BLOCK_ETHANE = BlockFluidGS((Ethane)!!, HH, false)
        BLOCK_HELIUM_HYDROGEN = BlockFluidGS((HeliumHydrogen)!!, HH, false)
        BLOCK_NATURE_GAS = BlockFluidNatureGas((NatureGas)!!, HELIUM)

        GSBlocks.registerBlock((BLOCK_LEMETHANE)!!, null)
        GSBlocks.registerBlock((BLOCK_HELIUM3)!!, null)
        GSBlocks.registerBlock((BLOCK_ETHANE)!!, null)
        GSBlocks.registerBlock((BLOCK_HELIUM_HYDROGEN)!!, null)
        GSBlocks.registerBlock((BLOCK_NATURE_GAS)!!, null)
    }

    @JvmStatic
    private fun registerFluid(
        fluidName: String,
        density: Int,
        viscosity: Int,
        temperature: Int,
        gaseous: Boolean,
        fluidTexture: String
    ): Fluid? {
        var returnFluid: Fluid? = FluidRegistry.getFluid(fluidName)

        if (returnFluid == null) {
            val textureStill: ResourceLocation =
                ResourceLocation.parse(GalaxySpace.Companion.TEXTURE_PREFIX + "fluids/" + fluidTexture + "_still")
            val textureFlowing: ResourceLocation =
                ResourceLocation.parse(GalaxySpace.Companion.TEXTURE_PREFIX + "fluids/" + fluidTexture + "_flowing")
            FluidRegistry.registerFluid(
                Fluid(fluidName, textureStill, textureFlowing).setDensity(density).setViscosity(viscosity)
                    .setTemperature(temperature).setGaseous(gaseous)
            )
            returnFluid = FluidRegistry.getFluid(fluidName)
            //if(!returnFluid.isGaseous())
            FluidRegistry.addBucketForFluid(returnFluid)
        } else {
            returnFluid.setGaseous(gaseous)
        }


        //GalaxySpace.debug(returnFluid.getName() + "");
        return returnFluid
    }
}
