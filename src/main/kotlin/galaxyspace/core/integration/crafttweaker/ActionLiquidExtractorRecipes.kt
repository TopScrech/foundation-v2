package galaxyspace.core.integration.crafttweaker

import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityLiquidExtractor
import crafttweaker.IAction
import crafttweaker.api.minecraft.CraftTweakerMC
import crafttweaker.api.liquid.ILiquidStack
import crafttweaker.api.block.IBlock

open class ActionLiquidExtractorRecipes {
    open class Add constructor(private val block: IBlock?, private val liquid: ILiquidStack?) : IAction {
        override fun apply() {
            TileEntityLiquidExtractor.addBlockAndFluid(
                CraftTweakerMC.getBlock(block),
                CraftTweakerMC.getLiquidStack(liquid)
            )
        }

        override fun describe(): String {
            return "Added Liquid Extractor recipe for " + this.block
        }
    } /*
	static class Remove implements IAction
	{
		private final ILiquidStack liquid;
		public Remove(ILiquidStack fluid)
		{
			this.liquid = fluid;
		}
		
		@Override
		public void apply() {
		
		}

		@Override
		public String describe() {
			return "Removed Fuel Generator recipe fluid for " + this.liquid;
		}
		
	}*/
}
