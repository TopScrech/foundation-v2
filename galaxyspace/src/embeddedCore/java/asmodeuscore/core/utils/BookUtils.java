package asmodeuscore.core.utils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import asmodeuscore.api.space.IBookPage;
import net.minecraft.util.ResourceLocation;

public class BookUtils {
	
	public static Map<String, ResourceLocation> GUIDE_BOOK_CATEGORIES = new HashMap<String, ResourceLocation>();
	public static Set<IBookPage> GUIDE_BOOK_PAGES = new TreeSet<IBookPage>(new Comparator<IBookPage>()
		{
			@Override
			public int compare(IBookPage arg0, IBookPage arg1) {
				return arg0.titlePage().compareTo(arg1.titlePage());
			}
		});
	
	public enum Book_Cateroies {
		GENERAL,
		BODIES,
		BLOCKS,
		ITEMS,
		MECHANICS;
		
		public String getName()
		{
			return this.name().toLowerCase();
		}
	}
		
	public static void addGuideBookCategory(String name, ResourceLocation icon)
	{
		GUIDE_BOOK_CATEGORIES.put(name, icon);
	}
		
	public static void addGuideBookPage(IBookPage page)
	{
		GUIDE_BOOK_PAGES.add(page);
	}
}
