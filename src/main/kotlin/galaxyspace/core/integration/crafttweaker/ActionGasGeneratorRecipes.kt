package galaxyspace.core.integration.crafttweaker

import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityGasGenerator
import crafttweaker.IAction
import crafttweaker.api.minecraft.CraftTweakerMC
import crafttweaker.api.liquid.ILiquidStack

open class ActionGasGeneratorRecipes {
    open class Add constructor(
        private val liquid: ILiquidStack?,
        private val burn_time: Int,
        private val mod_energy: Float,
        private val drain_amount: Int
    ) : IAction {
        override fun apply() {
            TileEntityGasGenerator.registerNewFuel(
                CraftTweakerMC.getLiquidStack(liquid).getFluid(),
                burn_time,
                mod_energy,
                drain_amount
            )
        }

        override fun describe(): String {
            return "Added Gas Generator recipe fluid for " + this.liquid
        }
    }

    open class Remove constructor(private val liquid: ILiquidStack?) : IAction {
        override fun apply() {
            TileEntityGasGenerator.removeFuel(CraftTweakerMC.getLiquidStack(liquid).getFluid())
        }

        override fun describe(): String {
            return "Removed Gas Generator recipe fluid for " + this.liquid
        }
    }
}
