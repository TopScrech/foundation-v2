package asmodeuscore.core.client.gui;

import java.util.Set;

import asmodeuscore.AsmodeusCore;
import asmodeuscore.core.configs.AsmodeusConfig;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;

public class ACConfigGuiFactory implements IModGuiFactory
{
    public static class CoreConfigGUI extends GuiConfig
    {
    	    	
        public CoreConfigGUI(GuiScreen parent)
        {        	
            super(parent, AsmodeusCore.MODID, GCCoreUtil.translate("ac.configgui.title"));
            this.configElements.addAll(AsmodeusConfig.getConfigElements());
        }
    }

    @Override
    public void initialize(Minecraft minecraftInstance)
    {

    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
    {
        return null;
    }

    @Override
	public GuiScreen createConfigGui(GuiScreen arg0)
	{
		return new CoreConfigGUI(arg0);
	}

	@Override
	public boolean hasConfigGui()
	{		
		return true;
	}
}
