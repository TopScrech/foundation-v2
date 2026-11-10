package galaxyspace.core.integration.crafttweaker

import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityHydroponicBase
import crafttweaker.api.item.IItemStack
import crafttweaker.IAction
import crafttweaker.api.minecraft.CraftTweakerMC
import crafttweaker.api.block.IBlock
import crafttweaker.mc1120.item.MCItemStack

open class ActionHydroponicRecipes {
    open class Add constructor(
        private val seed: IItemStack?,
        private val product: IItemStack?,
        private val secproduct: IItemStack?,
        private val secchance: Int,
        private val stages: Int,
        private val block: IBlock?,
        product_rand: Boolean,
        secproduct_rand: Boolean
    ) : IAction {
        private val rand_counts = BooleanArray(2)

        constructor(
            seed: IItemStack?,
            product: IItemStack?,
            stages: Int,
            block: IBlock?,
            product_rand: Boolean
        ) : this(seed, product, MCItemStack.EMPTY, 0, stages, block, product_rand, false)

        init {
            this.rand_counts[0] = product_rand
            this.rand_counts[1] = secproduct_rand
        }

        override fun apply() {
            TileEntityHydroponicBase.addPlant(
                CraftTweakerMC.getItemStack(seed),
                CraftTweakerMC.getItemStack(product),
                CraftTweakerMC.getItemStack(secproduct),
                secchance,
                CraftTweakerMC.getBlock(block),
                stages,
                rand_counts
            )
        }

        override fun describe(): String {
            return "Added Hydroponic Farm plant recipe for " + this.seed
        }
    }

    open class Remove constructor(private val item: IItemStack?) : IAction {
        override fun apply() {
            TileEntityHydroponicBase.removePlant(CraftTweakerMC.getItemStack(item))
        }

        override fun describe(): String {
            return "Removed Hydroponic Farm plant recipe for " + this.item
        }
    }
}
