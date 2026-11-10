package micdoodle8.mods.galacticraft.planets.asteroids.items

import com.google.common.collect.Multimap
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.items.ISortableItem
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.EnumRarity
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemSword
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ItemSwordAsteroids constructor(assetName: String?) : ItemSword(AsteroidsItems.TOOL_TITANIUM),
    ISortableItem {
    private val attackDamageD: Double

    init {
        this.setTranslationKey(assetName)
        this.attackDamageD = 9.0
        //this.setTextureName(GalacticraftPlanets.TEXTURE_PREFIX + assetName);
    }

    override fun getAttackDamage(): Float {
        return 6.0f
    }

    override fun getCreativeTab(): CreativeTabs? {
        return GalacticraftCore.galacticraftItemsTab
    }

    @SideOnly(Side.CLIENT)
    override fun getRarity(par1ItemStack: ItemStack): EnumRarity {
        return (ClientProxyCore.galacticraftItem)!!
    }

    override fun getCategory(meta: Int): EnumSortCategoryItem {
        return EnumSortCategoryItem.TOOLS
    }

    override fun getItemAttributeModifiers(equipmentSlot: EntityEquipmentSlot): Multimap<String?, AttributeModifier?> {
        val multimap: Multimap<String?, AttributeModifier?> = super.getItemAttributeModifiers(equipmentSlot)

        if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
            multimap.put(
                SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", this.attackDamageD, 0)
            )
            multimap.put(
                SharedMonsterAttributes.ATTACK_SPEED.getName(),
                AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -2.8, 0)
            )
        }

        return multimap
    }

    override fun onBlockDestroyed(
        stack: ItemStack,
        worldIn: World,
        state: IBlockState,
        pos: BlockPos,
        entityLiving: EntityLivingBase
    ): Boolean {
        val hardness: Float = state.getBlockHardness(worldIn, pos)
        if (hardness > 0f) {
            stack.damageItem(if (hardness > 0.2001f) 2 else 1, entityLiving)
        }

        return true
    }
}
