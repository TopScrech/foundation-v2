package micdoodle8.mods.galacticraft.core

import foundation.item.legacyItemValue

import foundation.block.getMetaFromState

import micdoodle8.mods.galacticraft.core.blocks.MaterialOleaginous
import micdoodle8.mods.galacticraft.core.entities.EntityMeteorChunk
import micdoodle8.mods.galacticraft.core.event.EventHandlerGC
import micdoodle8.mods.galacticraft.core.items.ItemBucketGC
import micdoodle8.mods.galacticraft.core.items.ItemTier1Rocket
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.util.GCLog
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import micdoodle8.mods.galacticraft.planets.asteroids.items.ItemTier3Rocket
import micdoodle8.mods.galacticraft.planets.mars.items.ItemTier2Rocket
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems
import micdoodle8.mods.galacticraft.planets.venus.VenusItems
import net.minecraft.block.BlockDispenser
import net.minecraft.world.level.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.dispenser.BehaviorDefaultDispenseItem
import net.minecraft.dispenser.BehaviorProjectileDispense
import net.minecraft.dispenser.IBehaviorDispenseItem
import net.minecraft.dispenser.IBlockSource
import net.minecraft.dispenser.IPosition
import net.minecraft.entity.IProjectile
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.item.Items
import net.minecraft.world.item.ItemStack
import net.minecraft.core.Direction as EnumFacing
import net.minecraft.resources.Identifier as ResourceLocation
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry

object GCFluids {
    @JvmField
    var fluidOil: Fluid? = null

    @JvmField
    var fluidFuel: Fluid? = null

    @JvmField
    var fluidOxygenGas: Fluid? = null

    @JvmField
    var fluidHydrogenGas: Fluid? = null

    @JvmField
    var materialOil: Material = MaterialOleaginous(MapColor.BROWN)

    @JvmStatic
    fun registerFluids() {
        fluidOxygenGas = registerFluid("oxygen", 1, 13, 295, true, "oxygen_gas")
        fluidHydrogenGas = registerFluid("hydrogen", 1, 1, 295, true, "hydrogen_gas")
    }

    @JvmStatic
    fun registerOilandFuel() {
        //NOTE: the way this operates will depend on the order in which different mods initialize (normally alphabetical order)
        //Galacticraft can handle things OK if another mod registers oil or fuel first.  The other mod may not be so happy if GC registers oil or fuel first.

        val oilID: String = if (ConfigManagerCore.useOldOilFluidID) "oilgc" else "oil"
        val fuelID: String = if (ConfigManagerCore.useOldFuelFluidID) "fuelgc" else "fuel"

        // Oil:
        if (!FluidRegistry.isFluidRegistered(oilID)) {
            val flowingOil: ResourceLocation = ResourceLocation.parse(Constants.TEXTURE_PREFIX + "blocks/fluids/oil_flow")
            val stillOil: ResourceLocation = ResourceLocation.parse(Constants.TEXTURE_PREFIX + "blocks/fluids/oil_still")
            val gcFluidOil: Fluid = Fluid(oilID, stillOil, flowingOil).setDensity(800).setViscosity(1500)
            FluidRegistry.registerFluid(gcFluidOil)
        } else {
            GCLog.info("Galacticraft oil is not default, issues may occur.")
        }

        fluidOil = FluidRegistry.getFluid(oilID)

        if (fluidOil!!.getBlock() == null) {
            GCBlocks.registerOil()
            fluidOil!!.setBlock(GCBlocks.crudeOil)
        } else {
            GCBlocks.crudeOil = fluidOil!!.getBlock()
        }

        if (GCBlocks.crudeOil != null && !FluidRegistry.getBucketFluids().contains(fluidOil)) {
            FluidRegistry.addBucketForFluid(fluidOil) //Create a Universal Bucket AS WELL AS our type, this is needed to pull oil out of other mods tanks
            GCItems.bucketOil = ItemBucketGC((GCBlocks.crudeOil)!!, (fluidOil)!!)
            ((GCItems.bucketOil)!!.setTranslationKey("bucket_oil"))!!
            GCItems.registerItem((GCItems.bucketOil)!!)
            EventHandlerGC.bucketList.put(GCBlocks.crudeOil, GCItems.bucketOil)
        }

        // Fuel:
        if (!FluidRegistry.isFluidRegistered(fuelID)) {
            val flowingFuel: ResourceLocation = ResourceLocation.parse(Constants.TEXTURE_PREFIX + "blocks/fluids/fuel_flow")
            val stillFuel: ResourceLocation = ResourceLocation.parse(Constants.TEXTURE_PREFIX + "blocks/fluids/fuel_still")
            val gcFluidFuel: Fluid = Fluid(fuelID, stillFuel, flowingFuel).setDensity(400).setViscosity(900)
            FluidRegistry.registerFluid(gcFluidFuel)
        } else {
            GCLog.info("Galacticraft fuel is not default, issues may occur.")
        }

        fluidFuel = FluidRegistry.getFluid(fuelID)

        if (fluidFuel!!.getBlock() == null) {
            GCBlocks.registerFuel()
            fluidFuel!!.setBlock(GCBlocks.fuel)
        } else {
            GCBlocks.fuel = fluidFuel!!.getBlock()
        }

        if (GCBlocks.fuel != null && !FluidRegistry.getBucketFluids().contains(fluidFuel)) {
            FluidRegistry.addBucketForFluid(fluidFuel) //Create a Universal Bucket AS WELL AS our type, this is needed to pull fuel out of other mods tanks
            GCItems.bucketFuel = ItemBucketGC((GCBlocks.fuel)!!, (fluidFuel)!!)
            ((GCItems.bucketFuel)!!.setTranslationKey("bucket_fuel"))!!
            GCItems.registerItem((GCItems.bucketFuel)!!)
            EventHandlerGC.bucketList.put(GCBlocks.fuel, GCItems.bucketFuel)
        }
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
            val texture: ResourceLocation = ResourceLocation.parse(Constants.TEXTURE_PREFIX + "blocks/fluids/" + fluidTexture)
            FluidRegistry.registerFluid(
                Fluid(fluidName, texture, texture).setDensity(density).setViscosity(viscosity)
                    .setTemperature(temperature).setGaseous(gaseous)
            )
            returnFluid = FluidRegistry.getFluid(fluidName)
        } else {
            returnFluid.setGaseous(gaseous)
        }

        return returnFluid
    }

    @JvmStatic
    fun registerLegacyFluids() {
        //If any other mod has registered "fuel" or "oil" and GC has not, then allow GC's appropriate canisters to be fillable with that one as well
//        if (ConfigManagerCore.useOldFuelFluidID && FluidRegistry.isFluidRegistered("fuel"))
//        {
//            FluidContainerRegistry.registerFluidContainer(new FluidContainerRegistry.FluidContainerData(new FluidStack(FluidRegistry.getFluid("fuel"), 1000), new ItemStack(GCItems.fuelCanister, 1, 1), new ItemStack(GCItems.oilCanister, 1, ItemCanisterGeneric.EMPTY)));
//        }
//        if (ConfigManagerCore.useOldOilFluidID && FluidRegistry.isFluidRegistered("oil"))
//        {
//            FluidContainerRegistry.registerFluidContainer(new FluidContainerRegistry.FluidContainerData(new FluidStack(FluidRegistry.getFluid("oil"), 1000), new ItemStack(GCItems.oilCanister, 1, 1), new ItemStack(GCItems.oilCanister, 1, ItemCanisterGeneric.EMPTY)));

//            {
        // TODO Fix BC Oil compat

//            }
//        }

        //Register now any unregistered "oil", "fuel", "oilgc" and "fuelgc" fluids
        //This is for legacy compatibility with any 'in the world' tanks and items filled in different GC versions or with different GC config
        //In those cases, FluidUtil methods (and TileEntityRefinery) will attempt to fresh containers/tanks with the current fuel or oil type

        val flowingOil: ResourceLocation = ResourceLocation.parse(Constants.TEXTURE_PREFIX + "blocks/fluids/oil_flow")
        val flowingFuel: ResourceLocation = ResourceLocation.parse(Constants.TEXTURE_PREFIX + "blocks/fluids/fuel_flow")
        val stillOil: ResourceLocation = ResourceLocation.parse(Constants.TEXTURE_PREFIX + "blocks/fluids/oil_still")
        val stillFuel: ResourceLocation = ResourceLocation.parse(Constants.TEXTURE_PREFIX + "blocks/fluids/fuel_still")
        if (!FluidRegistry.isFluidRegistered("oil")) {
            FluidRegistry.registerFluid(Fluid("oil", stillOil, flowingOil).setDensity(800).setViscosity(1500))
        }
        if (!FluidRegistry.isFluidRegistered("oilgc")) {
            FluidRegistry.registerFluid(Fluid("oilgc", stillOil, flowingOil).setDensity(800).setViscosity(1500))
        }
        if (!FluidRegistry.isFluidRegistered("fuel")) {
            FluidRegistry.registerFluid(Fluid("fuel", stillFuel, flowingFuel).setDensity(400).setViscosity(900))
        }
        if (!FluidRegistry.isFluidRegistered("fuelgc")) {
            FluidRegistry.registerFluid(Fluid("fuelgc", stillFuel, flowingFuel).setDensity(400).setViscosity(900))
        }
    }

    @JvmStatic
    fun registerDispenserBehaviours() {
        val ibehaviordispenseitem: IBehaviorDispenseItem = object : BehaviorDefaultDispenseItem() {
            private val dispenseBehavior = BehaviorDefaultDispenseItem()
            public override fun dispenseStack(source: IBlockSource, stack: ItemStack): ItemStack {
                val itembucket: ItemBucketGC = stack.getItem() as ItemBucketGC
                val blockpos: BlockPos = source.getBlockPos()
                    .offset(source.getBlockState().getValue(BlockDispenser.FACING) as EnumFacing)
                if (itembucket.tryPlaceContainedLiquid(null as EntityPlayer?, source.getWorld(), blockpos)) {
                    return ItemStack(Items.BUCKET)
                } else {
                    return this.dispenseBehavior.dispense(source, stack)
                }
            }
        }
        if (GCItems.bucketFuel != null) {
            BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(GCItems.bucketFuel, ibehaviordispenseitem)
        }
        if (GCItems.bucketOil != null) {
            BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(GCItems.bucketOil, ibehaviordispenseitem)
        }
        if (GalacticraftCore.Companion.isPlanetsLoaded) {
            if (MarsItems.bucketSludge != null) {
                BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(MarsItems.bucketSludge, ibehaviordispenseitem)
            }
            if (VenusItems.bucketSulphuricAcid != null) {
                BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(
                    VenusItems.bucketSulphuricAcid,
                    ibehaviordispenseitem
                )
            }
        }

        // The following code is for other objects, not liquids, but it's convenient to keep it all together
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(GCItems.meteorChunk, object : BehaviorProjectileDispense() {
            override fun getProjectileEntity(worldIn: World, position: IPosition, stack: ItemStack): IProjectile {
                val meteor: EntityMeteorChunk = EntityMeteorChunk(worldIn)
                meteor.setPosition(position.getX(), position.getY(), position.getZ())
                if (stack.legacyItemValue > 0) {
                    meteor.setFire(20)
                    meteor.isHot = true
                }
                meteor.canBePickedUp = 1
                return meteor
            }

            override fun getProjectileVelocity(): Float {
                return 1.0f
            }
        })

        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(
            GCItems.rocketTier1,
            object : BehaviorDefaultDispenseItem() {
                public override fun dispenseStack(source: IBlockSource, stack: ItemStack): ItemStack {
                    val world: World = source.getWorld()
                    val pos: BlockPos = source.getBlockPos()
                        .offset(source.getBlockState().getValue(BlockDispenser.FACING) as EnumFacing, 2)
                    val iblockstate: IBlockState = world.getBlockState(pos)
                    var rocketPlaced: Boolean = false
                    if (iblockstate.getBlock() === GCBlocks.landingPadFull && (GCBlocks.landingPadFull)!!.getMetaFromState(
                            iblockstate
                        ) == 0
                    ) {
                        val centerX: Float = pos.getX() + 0.5f
                        val centerY: Float = pos.getY() + 0.4f
                        val centerZ: Float = pos.getZ() + 0.5f
                        rocketPlaced = ItemTier1Rocket.placeRocketOnPad(
                            stack,
                            world,
                            world.getBlockEntity(pos),
                            centerX,
                            centerY,
                            centerZ
                        )
                    }

                    if (rocketPlaced) {
                        stack.splitStack(1)
                    }
                    return stack
                }
            })

        if (GalacticraftCore.Companion.isPlanetsLoaded) {
            BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(
                MarsItems.rocketMars,
                object : BehaviorDefaultDispenseItem() {
                    public override fun dispenseStack(source: IBlockSource, stack: ItemStack): ItemStack {
                        val world: World = source.getWorld()
                        val pos: BlockPos = source.getBlockPos().offset(
                            source.getBlockState().getValue(BlockDispenser.FACING) as EnumFacing,
                            2
                        )
                        val iblockstate: IBlockState = world.getBlockState(pos)
                        var rocketPlaced: Boolean = false
                        if (iblockstate.getBlock() === GCBlocks.landingPadFull && (GCBlocks.landingPadFull)!!.getMetaFromState(
                                iblockstate
                            ) == 0
                        ) {
                            val centerX: Float = pos.getX() + 0.5f
                            val centerY: Float = pos.getY() + 0.4f
                            val centerZ: Float = pos.getZ() + 0.5f
                            rocketPlaced = ItemTier2Rocket.placeRocketOnPad(
                                stack,
                                world,
                                world.getBlockEntity(pos),
                                centerX,
                                centerY,
                                centerZ
                            )
                        }

                        if (rocketPlaced) {
                            stack.splitStack(1)
                        }
                        return stack
                    }
                })

            BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(
                AsteroidsItems.tier3Rocket,
                object : BehaviorDefaultDispenseItem() {
                    public override fun dispenseStack(source: IBlockSource, stack: ItemStack): ItemStack {
                        val world: World = source.getWorld()
                        val pos: BlockPos = source.getBlockPos().offset(
                            source.getBlockState().getValue(BlockDispenser.FACING) as EnumFacing,
                            2
                        )
                        val iblockstate: IBlockState = world.getBlockState(pos)
                        var rocketPlaced: Boolean = false
                        if (iblockstate.getBlock() === GCBlocks.landingPadFull && (GCBlocks.landingPadFull)!!.getMetaFromState(
                                iblockstate
                            ) == 0
                        ) {
                            val centerX: Float = pos.getX() + 0.5f
                            val centerY: Float = pos.getY() + 0.4f
                            val centerZ: Float = pos.getZ() + 0.5f
                            rocketPlaced = ItemTier3Rocket.placeRocketOnPad(
                                stack,
                                world,
                                world.getBlockEntity(pos),
                                centerX,
                                centerY,
                                centerZ
                            )
                        }

                        if (rocketPlaced) {
                            stack.splitStack(1)
                        }
                        return stack
                    }
                })
        }
    }
}
