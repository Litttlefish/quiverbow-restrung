package com.domochevsky.quiverbow.weapons;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.projectiles.WebShot;
import com.domochevsky.quiverbow.recipes.RecipeLoadAmmo;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SilkenSpinner extends _WeaponBase
{
	public SilkenSpinner()
	{
		super("silken_spinner", 8);
		this.setCreativeTab(CreativeTabs.tabTools);		// This is a tool
	}

	


	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/WebGun");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/WebGun_Empty");
	}


	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if (world.isRemote) { return stack; }								// Not doing this on client side
		if (this.getDamage(stack) >= this.getMaxDamage()) { return stack; }	// Is empty

		this.doSingleFire(stack, world, player);	// Handing it over to the neutral firing function
		return stack;
	}


	@Override
	public void doSingleFire(ItemStack stack, World world, Entity entity)		// Server side
	{
		if (this.getCooldown(stack) > 0) { return; }	// Hasn't cooled down yet

		// SFX
		world.playSoundAtEntity(entity, "tile.piston.out", 1.0F, 2.0F);

		// Firing
		WebShot projectile = new WebShot(world, entity, (float) this.Speed);
		world.spawnEntityInWorld(projectile); 			// Firing!

		this.consumeAmmo(stack, entity, 1);
		this.setCooldown(stack, this.Cooldown);
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);

		this.Speed = config.get(this.name, "How fast are my projectiles? (default 1.5 BPT (Blocks Per Tick))", 1.5).getDouble();

		this.Cooldown = config.get(this.name, "How long until I can fire again? (default 20 ticks)", 20).getInt();

		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default false. Potentially abusable for free cobwebs.)", false).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "ihi", "gpg", "tsi",
					'p', Blocks.piston,
					's', Blocks.sticky_piston,
					't', Blocks.tripwire_hook,
					'i', Items.iron_ingot,
					'h', Blocks.hopper,
					'g', Blocks.glass_pane
					);
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		// Making web out of string
		GameRegistry.addRecipe(new ItemStack(Blocks.web), "s s", " s ", "s s",
				's', Items.string
				);
		
		GameRegistry.addRecipe(new RecipeLoadAmmo(this).addComponent(Blocks.web, 1));
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		if (stack.getItemDamage() >= stack.getMaxDamage()) { return "WebGun_empty"; }	// empty

		return "WebGun";	// Regular
	}
}
