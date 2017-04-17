package com.domochevsky.quiverbow.ammo;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class RocketBundle extends _AmmoBase
{	
	@Override
	public String getIconPath() { return "Bundle_Rockets"; }
	
	@Override
	public void addRecipes() 
	{ 
        // A bundle of rockets (8)
        GameRegistry.addRecipe(new ItemStack(this), "xxx", "xyx", "xxx",						
                'x', Items.fireworks, 
                'y', Items.string
        );  
       
        // Bundle of rockets back to 8 rockets
        GameRegistry.addShapelessRecipe(new ItemStack(Items.fireworks, 8), new ItemStack(this));
	}
}
