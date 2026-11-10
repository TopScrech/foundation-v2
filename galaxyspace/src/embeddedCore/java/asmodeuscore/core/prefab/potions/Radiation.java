package asmodeuscore.core.prefab.potions;

import java.util.ArrayList;
import java.util.List;

import asmodeuscore.AsmodeusCore;
import asmodeuscore.core.configs.AsmodeusConfig;
import asmodeuscore.core.registers.potions.ACPotions;
import asmodeuscore.core.utils.ACDamageSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Radiation extends Potion
{
	private final ResourceLocation iconTexture = new ResourceLocation(AsmodeusCore.ASSET_PREFIX, "textures/gui/potions/radiation.png");;
	
	public Radiation(boolean isBad, int color)
	{
		super(isBad, color);
		this.setPotionName(this, "radiation");
	}
	
	public static void setPotionName(Potion potion, final String potionName) {
		potion.setRegistryName(AsmodeusCore.MODID, potionName);
		potion.setPotionName("effect." + potion.getRegistryName().toString());
	}

	@Override
	public boolean hasStatusIcon() {
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc)
	{
		super.renderInventoryEffect(x, y, effect, mc);
		if (mc.currentScreen != null)
		{
			mc.getTextureManager().bindTexture(iconTexture);
			Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0, 0, 18, 18, 18, 18);
		}
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha)
	{
		super.renderHUDEffect(x, y, effect, mc, alpha);
		mc.getTextureManager().bindTexture(iconTexture);
		Gui.drawModalRectWithCustomSizedTexture(x + 4, y + 4, 0, 0, 18, 18, 18, 18);
	}	

	@Override
	public boolean isReady(int duration, int amplifier)
	{
		if (this == ACPotions.radiation)
		{
			int k = 20 >> amplifier;
			return k > 0 ? duration % k == 0 : true;
		}
		return false;
	}
	
	@Override
	public void performEffect(EntityLivingBase living, int food)
	{
		if (!(living instanceof EntityPlayer) || living.world.isRemote)
			return;

		living.attackEntityFrom(ACDamageSource.solar, 3.0F * AsmodeusConfig.solarRadiationDamageRate);
	}
	
	@Override
	public List<ItemStack> getCurativeItems()
    {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();        
        return ret;
    }
}