package galaxyspace.systems.SolarSystem.planets.overworld.items

import net.minecraft.init.Items
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.RayTraceResult.Type
import net.minecraftforge.fml.common.registry.ForgeRegistries

import galaxyspace.core.GSItems
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemBasicGS.BasicItems
import galaxyspace.core.util.GSUtils
import galaxyspace.core.util.GSCreativeTabs
import galaxyspace.core.configs.GSConfigSchematics
import galaxyspace.systems.SolarSystem.planets.overworld.items.ItemBasicGS
import galaxyspace.GalaxySpace
import galaxyspace.core.configs.GSConfigCore
import asmodeuscore.core.utils.ACAttributePlayer
import galaxyspace.core.GSPotions
import galaxyspace.core.network.packet.GSPacketSimple
import galaxyspace.core.network.packet.GSPacketSimple.GSEnumSimplePacket
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.items.IClickableItem
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.util.EnumColor
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import micdoodle8.mods.galacticraft.planets.asteroids.blocks.AsteroidBlocks
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems
import micdoodle8.mods.galacticraft.planets.asteroids.tile.TileEntityShortRangeTelepad
import micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks
import net.minecraft.block.Block
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityList
import net.minecraft.entity.ai.attributes.IAttributeInstance
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.Item
import net.minecraft.item.ItemDye
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.potion.PotionEffect
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.NonNullList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.ItemHandlerHelper
import java.util.Locale

open class ItemBasicGS constructor() : Item(), ISortableItem {
    enum class BasicItems @JvmOverloads constructor(private val meta: Int, private val stacksize: Int = 64) {
        MODULE_SMALLCANISTER(0),
        PART_SOLARFLARES(1),
        SOLARFLARES(2),
        DOLOMITE_CRYSTAL(3),
        DOLOMITE_MEAL(4),
        WAFER_MODERN(5),
        HEMATITE(6),
        SULFUR(7),
        UNKNOW_CRYSTAL(8),
        ANTIRADIATION_TABLETS(9),
        SCHEMATIC_BOX(10),
        MODULE_BASE(11),
        VOLCANIC_STONE(12),
        METEORIC_IRON_FRAGMENTS(13),
        BLANK_SCHEMATIC(14),
        URANIUM_FRAGMENTS(15),
        TEMP_SHIELD_CONTROL(16, 1),
        ICE_BUCKET(17, 1),
        EMERGENCY_PORTABLE_TELEPORT(18, 1),
        GUIDE_BOOK(19, 1),
        ADVANCED_EMERGENCY_KIT(20),
        THERMAL_CLOTH_T3(21),
        THERMAL_CLOTH_T4(22),
        OXYGEN_ICE_CRYSTAL(23),
        NITROGEN_ICE_CRYSTAL(24),
        METHANE_ICE_CRYSTAL(25),
        HYDROGEN_ICE_CRYSTAL(26),
        DRY_ICE_CRYSTAL(27),
        COLONIST_KIT(28),
        EMPTY_PLASMA_CELL(29),
        FILLED_PLASMA_CELL(30),
        WOLF_THERMAL_SUIT(31, 1),
        ANIMAL_CAGE(32, 1),
        IRON_FAN(33, 1),
        STEEL_FAN(34, 1),
        PLASTIC_FAN(35, 1),
        CARBON_FAN(36, 1),
        RAW_PLASTIC(37),
        PLASTIC(38),
        GAS_EXTRACTOR(39, 1); //EMPTY SLOT

        fun getName(): String {
            return this.name.lowercase(Locale.getDefault())
        }

        fun getItemStack(): ItemStack {
            return getItemStack(1)
        }

        fun getItemStack(count: Int): ItemStack {
            return ItemStack(GSItems.BASIC, count, meta)
        }

        fun getMeta(): Int {
            return this.meta
        }

        fun getMaxStackSize(): Int {
            return this.stacksize
        }
    }

    init {
        this.setMaxDamage(0)
        this.setHasSubtypes(true)
        this.setMaxStackSize(64)
        this.setTranslationKey("gs_basic")
        this.setCreativeTab(GSCreativeTabs.GSItemsTab)
    }

    override fun getSubItems(tab: CreativeTabs, list: NonNullList<ItemStack>) {
        if (tab === GSCreativeTabs.GSItemsTab || tab === CreativeTabs.SEARCH) {
            for (i: Int in BasicItems.entries.toTypedArray().indices) {
                if (BasicItems.entries[i].getMeta() == BasicItems.GAS_EXTRACTOR.getMeta()) continue

                if (BasicItems.entries[i].getName() != "null") {
                    list.add(ItemStack(this, 1, i))
                }
            }
        }
    }

    override fun onUpdate(stack: ItemStack, worldIn: World, entityIn: Entity, itemSlot: Int, isSelected: Boolean) {
        super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected)
        val n: Int = stack.getItemDamage()

        if (n == BasicItems.GAS_EXTRACTOR.getMeta()) {
            stack.shrink(1)
            worldIn.spawnEntity(
                EntityItem(
                    worldIn,
                    entityIn.posX,
                    entityIn.posY,
                    entityIn.posZ,
                    ItemStack(GSItems.GAS_EXTRACTOR, 1, GSItems.GAS_EXTRACTOR.getMaxDamage())
                )
            )
        }
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, world: World?, list: MutableList<String?>, flagIn: ITooltipFlag) {
        val n: Int = stack.getItemDamage()

        if (n == BasicItems.DOLOMITE_MEAL.getMeta()) list.add(GCCoreUtil.translate("gui.bonemeal.desc"))
        else if (n == BasicItems.SCHEMATIC_BOX.getMeta()) list.add(GCCoreUtil.translate("gui.schematic_box.desc"))
        else if (n == BasicItems.BLANK_SCHEMATIC.getMeta() && !GSConfigSchematics.enableDuplicateSchematic) {
            list.add(EnumColor.DARK_RED.toString() + "Disabled in config.")
        } else if (n == BasicItems.TEMP_SHIELD_CONTROL.getMeta()) {
            var time: Int = SHIELD_TIME
            if (stack.hasTagCompound()) {
                val shieldtime: Int = stack.getTagCompound()!!.getInteger("shieldtime")
                time = shieldtime
            } else {
                time = SHIELD_TIME
            }
            list.add(GCCoreUtil.translate("gui.temp_shield_control.desc"))
            list.add(GCCoreUtil.translate("gui.message.can_find_in_dungeon"))
            list.add(GCCoreUtil.translate("gui.charge") + " " + time + " " + GCCoreUtil.translate("gui.seconds"))
        } else if (n == BasicItems.EMERGENCY_PORTABLE_TELEPORT.getMeta()) {
            list.add(GCCoreUtil.translate("gui.emergency_portable_teleport.desc"))
            list.add(GCCoreUtil.translate("gui.message.can_find_in_dungeon"))
            list.add("")
            if (stack.hasTagCompound()) {
                if (stack.getTagCompound()!!
                        .getIntArray("position").size <= 0
                ) list.add(GCCoreUtil.translate("gui.not_attached.desc"))
                else {
                    val pos: IntArray = stack.getTagCompound()!!.getIntArray("position")
                    list.add(GCCoreUtil.translate("gui.attached.desc") + " DIM:" + pos[3] + " X:" + pos[0] + ", Y:" + pos[1] + ", Z:" + pos[2])
                }

                if (stack.getTagCompound()!!.getBoolean("turnonoff")) {
                    list.add(GCCoreUtil.translate("gui.enabled.desc"))
                }
            }
        } else if (n == BasicItems.EMPTY_PLASMA_CELL.getMeta() || n == BasicItems.FILLED_PLASMA_CELL.getMeta()) {
            list.add(EnumColor.DARK_RED.toString() + "[WIP] Content")
        } else if (n == BasicItems.ANIMAL_CAGE.getMeta()) {
            list.add(GCCoreUtil.translate("gui.animal_cage.desc"))

            if (stack.hasTagCompound()) {
                if (stack.getTagCompound()!!.hasKey("entityData")) {
                    val entity: Entity? =
                        EntityList.createEntityFromNBT(stack.getTagCompound()!!.getCompoundTag("entityData"), world)
                    list.add(
                        GCCoreUtil.translate("gui.animal_cage_stored.desc") + " " + EnumColor.BRIGHT_GREEN + entity!!.getDisplayName()
                            .getFormattedText()
                    )
                }

                /*if(stack.getTagCompound().hasKey("destroyedLvl"))
				{
					list.add(stack.getTagCompound().getInteger("destroyedLvl") + "");
				}*/
            }
        } else if (n >= 33 && n <= 36) {
            if (stack.hasTagCompound()) {
                if (stack.getTagCompound()!!.hasKey("destroyedLvl")) {
                    list.add(
                        "Durability: " + (FANS_DURABILITY[n - 33] - stack.getTagCompound()!!
                            .getInteger("destroyedLvl")) + " / " + FANS_DURABILITY[n - 33]
                    )
                }
            }
        }
    }

    override fun getTranslationKey(stack: ItemStack): String {
        if (stack.getItemDamage() == BasicItems.THERMAL_CLOTH_T3.getMeta() || stack.getItemDamage() == BasicItems.THERMAL_CLOTH_T4.getMeta()) return "item.thermal_cloth"

        val meta: Int = if (stack.getItemDamage() > BasicItems.entries.size - 1) 0 else stack.getItemDamage()
        return "item." + BasicItems.entries[meta].getName()
    }

    override fun getMetadata(par1: Int): Int {
        return par1
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.GENERAL
    }

    override fun getItemStackLimit(stack: ItemStack): Int {
        val n: Int = stack.getItemDamage()

        for (item: BasicItems in BasicItems.entries) if (item.getMeta() == n) return item.getMaxStackSize()


        return this.getItemStackLimit()
    }

    override fun onItemRightClick(world: World, player: EntityPlayer, hand: EnumHand): ActionResult<ItemStack?> {
        val stack: ItemStack = player.getHeldItem(hand)

        if (world.isRemote) return ActionResult<ItemStack?>(EnumActionResult.PASS, player.getHeldItem(hand))

        /*
		if(stack.getItemDamage() == 28)
		{
			RayTraceResult ray = this.getRay(world, player, true);
			Map<IBlockState, Integer> blocks = new HashMap<IBlockState, Integer>();
			
			if(ray != null) {		
				for(BlockPos scan : ray.getBlockPos().getAllInBox(ray.getBlockPos().add(-2, -2, -2), ray.getBlockPos().add(2, 2, 2))) {
					IBlockState block = world.getBlockState(scan);				
					if(world.isAirBlock(scan)) continue;				
					
					if(blocks.containsKey(block)) {
						blocks.put(block, blocks.get(block) + 1);
						continue;
					}
					
					blocks.put(block, 1);
				}
				
				for(Entry<IBlockState, Integer> block : blocks.entrySet()) {
					ItemStack item = new ItemStack(Item.getItemFromBlock(block.getKey().getBlock()), 1, block.getKey().getBlock().getMetaFromState(block.getKey()));				
						
					player.sendMessage(new TextComponentString(TextFormatting.GREEN + item.getDisplayName() + " x" + block.getValue()));
				}//GalaxySpace.debug("" + blocks);
				player.sendMessage(new TextComponentString(TextFormatting.GREEN + "#################################"));
				
			}
			
		}*/
        if (stack.getItemDamage() == BasicItems.ICE_BUCKET.getMeta()) {
            /*	RayTraceResult ray = this.rayTrace(world, player, false);
			if(ray != null && !world.isRemote) {
				if(stack.hasTagCompound() && stack.getTagCompound().hasKey("current_block")) {

					Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(stack.getTagCompound().getString("current_block")));

					if(block != null && world.isAirBlock(ray.getBlockPos().up())) {
						world.setBlockState(ray.getBlockPos().up(), block.getDefaultState());

						return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, new ItemStack(Items.BUCKET));
					}
				}

			}*/

            return ActionResult<ItemStack?>(EnumActionResult.PASS, player.getHeldItem(hand))
        }

        if (stack.getItemDamage() == BasicItems.DOLOMITE_MEAL.getMeta()) {
            val ray: RayTraceResult? = GSUtils.getRay(world, player, true)
            if (ray != null && ItemDye.applyBonemeal(stack, world, ray.getBlockPos(), player, hand)) {
                if (!world.isRemote) {
                    world.playEvent(2005, ray.getBlockPos(), 0)
                }

                return ActionResult<ItemStack?>(EnumActionResult.SUCCESS, player.getHeldItem(hand))
            }
            return ActionResult<ItemStack?>(EnumActionResult.PASS, player.getHeldItem(hand))
        } else if (stack.getItemDamage() == 6) {
            /*StatsCapability gs_stats = galaxyspace.core.handler.capabilities.StatsCapability.get(player);
			gs_stats.setKnowledgeResearch(1, 6);
			GalaxySpace.packetPipeline.sendTo(new GSPacketSimple(GSEnumSimplePacket.C_UPDATE_RESEARCH, player.world, new Object[] {1, 6}), (EntityPlayerMP) player);
			*/

            return ActionResult<ItemStack?>(EnumActionResult.SUCCESS, player.getHeldItem(hand))
        } else if (stack.getItemDamage() == BasicItems.GUIDE_BOOK.getMeta()) {
            player.openGui(GalaxySpace.MODID, GSConfigCore.guiIDGuideBook, world, 0, 0, 0)

            return ActionResult<ItemStack?>(EnumActionResult.SUCCESS, player.getHeldItem(hand))
        } else if (stack.getItemDamage() == BasicItems.ANTIRADIATION_TABLETS.getMeta()) {
            val lvl: IAttributeInstance = player.getEntityAttribute(ACAttributePlayer.RADIATION_LVL)
            if (lvl.getAttributeValue() > 0) {
                player.addPotionEffect(PotionEffect(GSPotions.antiradiation, 20 * 10))
                stack.shrink(1)
                return ActionResult<ItemStack?>(EnumActionResult.SUCCESS, player.getHeldItem(hand))
            } else {
                player.sendMessage(TextComponentString(TextFormatting.DARK_GREEN.toString() + GCCoreUtil.translate(("gui.message.noradiation"))))
                return ActionResult<ItemStack?>(EnumActionResult.PASS, player.getHeldItem(hand))
            }
        } else if (stack.getItemDamage() == BasicItems.SCHEMATIC_BOX.getMeta()) {
            for (i: Int in 0..4) if (player.inventory.getFirstEmptyStack() != -1) player.inventory.addItemStackToInventory(
                ItemStack(GSItems.SCHEMATICS, 1, i)
            )
            else world.spawnEntity(
                EntityItem(
                    world, player.posX, player.posY, player.posZ,
                    ItemStack(GSItems.SCHEMATICS, 1, i)
                )
            )

            stack.shrink(1)
            return ActionResult<ItemStack?>(EnumActionResult.SUCCESS, player.getHeldItem(hand))
        } else if (stack.getItemDamage() == BasicItems.EMERGENCY_PORTABLE_TELEPORT.getMeta()) {
            if (!player.isSneaking()) return ActionResult<ItemStack?>(EnumActionResult.PASS, player.getHeldItem(hand))

            if (!stack.hasTagCompound()) stack.setTagCompound(NBTTagCompound())

            val ray: RayTraceResult? = player.rayTrace(5.0, player.ticksExisted.toFloat())
            val block: Block = world.getBlockState(ray!!.getBlockPos()).getBlock()

            if (block === AsteroidBlocks.shortRangeTelepad) {
                val tile: TileEntityShortRangeTelepad? =
                    world.getTileEntity(ray.getBlockPos()) as TileEntityShortRangeTelepad?

                if (tile!!.hasEnoughEnergyToRun && tile.addressValid) {
                    stack.getTagCompound()!!.setIntArray(
                        "position",
                        intArrayOf(
                            ray.getBlockPos().getX(),
                            ray.getBlockPos().getY(),
                            ray.getBlockPos().getZ(),
                            world.provider.getDimension()
                        )
                    )
                } else {
                    player.sendMessage(TextComponentString(TextFormatting.DARK_RED.toString() + GCCoreUtil.translate(("gui.message.invalid_teleport_conf"))))
                }
            } else {
                stack.getTagCompound()!!.setBoolean("turnonoff", !stack.getTagCompound()!!.getBoolean("turnonoff"))
            }
            return ActionResult<ItemStack?>(EnumActionResult.SUCCESS, player.getHeldItem(hand))
        } else if (stack.getItemDamage() == BasicItems.ADVANCED_EMERGENCY_KIT.getMeta()) {
            if (player is EntityPlayerMP) {
                for (i: Int in 0..<SIZE) {
                    var newGear: ItemStack? = getContents(i)
                    if (newGear!!.getItem() is IClickableItem) {
                        newGear = (newGear.getItem() as IClickableItem).onItemRightClick(newGear, world, player)
                    }
                    if (newGear!!.getCount() >= 1) {
                        ItemHandlerHelper.giveItemToPlayer(player, newGear, 0)
                    }
                }

                stack.shrink(1)
                return ActionResult<ItemStack?>(EnumActionResult.SUCCESS, stack)
            }
        } else if (stack.getItemDamage() == BasicItems.COLONIST_KIT.getMeta()) {
            if (player is EntityPlayerMP) {
                val stacks: Array<ItemStack?> = ((arrayOf<ItemStack>(
                    ItemStack(GCBlocks.oxygenDistributor, 1),
                    ItemStack(GCBlocks.oxygenCollector, 1),
                    ItemStack(GCBlocks.oxygenCompressor, 1),
                    ItemStack(GCBlocks.solarPanel, 1, 4),
                    ItemStack(GSItems.BASIC, 1, 20),
                    ItemStack(GCBlocks.solarPanel, 1, 4),
                    ItemStack(GCBlocks.machineTiered, 1, 0),
                    ItemStack(MarsBlocks.machine, 1, 0),
                    ItemStack(GCBlocks.machineBase2, 1, 8)
                ) as Array<ItemStack?>?))!!

                for (items: ItemStack? in stacks) ItemHandlerHelper.giveItemToPlayer(player, items!!, 0)

                stack.shrink(1)
                return ActionResult<ItemStack?>(EnumActionResult.SUCCESS, stack)
            }
        } else if (stack.getItemDamage() == BasicItems.ANIMAL_CAGE.getMeta()) {
            if (!stack.hasTagCompound()) stack.setTagCompound(NBTTagCompound())

            if (!stack.getTagCompound()!!.hasKey("hasMob")) stack.getTagCompound()!!.setBoolean("hasMob", false)
            if (!stack.getTagCompound()!!.hasKey("destroyedLvl")) stack.getTagCompound()!!.setInteger("destroyedLvl", 0)

            if (!stack.getTagCompound()!!.getBoolean("hasMob")) {
                //RayTraceResult ray = player.rayTrace(5, player.ticksExisted);//this.getRay(world, player, false);

                if (player is EntityPlayerMP) {
                    (GalaxySpace.packetPipeline)!!.sendTo(
                        GSPacketSimple(
                            GSEnumSimplePacket.C_GET_CAGE_ENTITY,
                            GCCoreUtil.getDimensionID(player.world)
                        ), player
                    )
                }
                return ActionResult<ItemStack?>(EnumActionResult.SUCCESS, player.getHeldItem(hand))
            } else {
                val result: RayTraceResult? = GSUtils.getRay(world, player, false)
                if (result != null && result.typeOfHit != null) {
                    if (result.typeOfHit == RayTraceResult.Type.BLOCK && stack.getTagCompound()!!
                            .hasKey("entityData")
                    ) {
                        var entity: Entity? = EntityList.createEntityFromNBT(
                            stack.getTagCompound()!!.getCompoundTag("entityData"),
                            player.world
                        )

                        if (entity is EntityAnimal) {
                            val pos: BlockPos = result.getBlockPos()
                            entity.dimension = player.dimension
                            entity.world = player.world
                            entity.setPosition(pos.getX().toDouble(), pos.getY() + 1.0, pos.getZ().toDouble())
                            if (!world.isRemote) {
                                world.spawnEntity(entity)
                            }
                            entity = null

                            stack.getTagCompound()!!.setBoolean("hasMob", false)
                            stack.getTagCompound()!!.removeTag("entityData")
                            if (stack.getTagCompound()!!.getInteger("destroyedLvl") < 2) stack.getTagCompound()!!
                                .setInteger("destroyedLvl", stack.getTagCompound()!!.getInteger("destroyedLvl") + 1)
                            else stack.splitStack(1)

                            return ActionResult<ItemStack?>(EnumActionResult.SUCCESS, player.getHeldItem(hand))
                        }
                    }
                }
            }
        }

        return ActionResult<ItemStack?>(EnumActionResult.PASS, player.getHeldItem(hand))
    }


    override fun showDurabilityBar(stack: ItemStack): Boolean {
        if (stack.hasTagCompound() && stack.getTagCompound()!!.hasKey("destroyedLvl")) {
            return stack.getTagCompound()!!.getInteger("destroyedLvl") >= 0
        }

        return stack.isItemDamaged()
    }

    override fun getDurabilityForDisplay(stack: ItemStack): Double {
        if (stack.getItemDamage() == BasicItems.ANIMAL_CAGE.getMeta()) {
            if (stack.hasTagCompound()) {
                return (stack.getTagCompound()!!.getInteger("destroyedLvl") / 10.0) / 0.31
            }
        }
        if (stack.getItemDamage() >= 33 && stack.getItemDamage() < 37) {
            if (stack.hasTagCompound()) {
                return stack.getTagCompound()!!.getInteger("destroyedLvl")
                    .toDouble() / FANS_DURABILITY[stack.getItemDamage() - 33].toDouble()
            }
        }

        return stack.getItemDamage().toDouble() / stack.getMaxDamage().toDouble()
    }

    //END
    override fun getItemBurnTime(itemStack: ItemStack): Int {
        if (itemStack.getItemDamage() == BasicItems.VOLCANIC_STONE.getMeta()) return 128000
        return -1
    }


    companion object {
        @JvmStatic
        fun getEnumNames(): Array<String?> {
            val s: Array<String?> = arrayOfNulls<String>(BasicItems.entries.size)
            for (i: Int in BasicItems.entries.toTypedArray().indices) {
                s[i] = BasicItems.entries[i].getName()
            }

            return s
        }

        @JvmField
        val SHIELD_TIME: Int = 10 * 60
        private const val SIZE = 9

        @JvmField
        val FANS_DURABILITY: IntArray = intArrayOf(
            600 * 3,
            1800 * 3,
            7200 * 3,
            21600 * 3
        )

        @JvmStatic
        fun getColonistKitRecipe(): Array<Any?> {
            val stacks: Array<ItemStack?> = ((arrayOf<ItemStack>(
                ItemStack(GCBlocks.oxygenDistributor, 1),
                ItemStack(GCBlocks.oxygenCollector, 1),
                ItemStack(GCBlocks.oxygenCompressor, 1),
                ItemStack(GCBlocks.solarPanel, 1, 4),
                ItemStack(GSItems.BASIC, 1, 20),
                ItemStack(GCBlocks.solarPanel, 1, 4),
                ItemStack(GCBlocks.machineTiered, 1, 0),
                ItemStack(MarsBlocks.machine, 1, 0),
                ItemStack(GCBlocks.machineBase2, 1, 8)
            ) as Array<ItemStack?>?))!!

            val result: Array<Any?> = arrayOf<Any?>(
                "ABC",
                "DEF",
                "GHI",
                'A',
                null,
                'B',
                null,
                'C',
                null,
                'D',
                null,
                'E',
                null,
                'F',
                null,
                'G',
                null,
                'H',
                null,
                'I',
                null
            )
            for (i: Int in stacks.indices) {
                result[i * 2 + 4] = stacks[i]
            }

            return result
        }

        //EMERGENCY KIT
        @JvmStatic
        fun getContents(slot: Int): ItemStack? {
            when (slot) {
                0 -> return ItemStack(GSItems.SPACE_SUIT_HELMET, 1, GSItems.SPACE_SUIT_HELMET.getMaxDamage())
                1 -> return ItemStack(GSItems.SPACE_SUIT_BODY, 1, GSItems.SPACE_SUIT_BODY.getMaxDamage())
                2 -> return ItemStack(GSItems.SPACE_SUIT_LEGGINS, 1, GSItems.SPACE_SUIT_LEGGINS.getMaxDamage())
                3 -> return ItemStack(GSItems.SPACE_SUIT_BOOTS, 1, GSItems.SPACE_SUIT_BOOTS.getMaxDamage())
                4 -> return ItemStack(AsteroidsItems.thermalPadding, 1, 0)
                5 -> return ItemStack(AsteroidsItems.thermalPadding, 1, 1)
                6 -> return ItemStack(AsteroidsItems.thermalPadding, 1, 2)
                7 -> return ItemStack(AsteroidsItems.thermalPadding, 1, 3)
                8 -> return ItemStack(GCItems.emergencyKit, 1, 0)
                else -> return null
            }
        }

        @JvmStatic
        fun getRecipe(): Array<Any?> {
            val result: Array<Any?> = arrayOf<Any?>(
                "EAB",
                "CID",
                "FGH",
                'A',
                null,
                'B',
                null,
                'C',
                null,
                'D',
                null,
                'E',
                null,
                'F',
                null,
                'G',
                null,
                'H',
                null,
                'I',
                null
            )
            for (i: Int in 0..<SIZE) {
                result[i * 2 + 4] = getContents(i)
            }
            return result
        }
    }
}
