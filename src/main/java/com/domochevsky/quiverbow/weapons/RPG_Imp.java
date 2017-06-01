package com.domochevsky.quiverbow.weapons;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.Main;
import com.domochevsky.quiverbow.ammo.LargeRocket;
import com.domochevsky.quiverbow.projectiles.BigRocket;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RPG_Imp extends _WeaponBase
{
	public RPG_Imp() { super("rocket_launcher_imp", 1); }

	
	public double ExplosionSize;
	private boolean dmgTerrain;		// Can our projectile damage terrain?

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister)
	{
		this.Icon = par1IconRegister.registerIcon("quiverchevsky:weapons/RPG_Improved");
		this.Icon_Empty = par1IconRegister.registerIcon("quiverchevsky:weapons/RPG_Improved_Empty");
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

		Helper.knockUserBack(entity, this.Kickback);			// Kickback

		// Firing
		BigRocket rocket = new BigRocket(world, entity, (float) this.Speed);	// Projectile Speed. Inaccuracy Hor/Vert
		rocket.explosionSize = this.ExplosionSize;
		rocket.dmgTerrain = this.dmgTerrain;

		world.spawnEntityInWorld(rocket); 		// shoom.

		// SFX
		world.playSoundAtEntity(entity, "fireworks.launch", 2.0F, 0.6F);

		this.consumeAmmo(stack, entity, 1);
		this.setCooldown(stack, 60);
	}


	@Override
	public void addProps(FMLPreInitializationEvent event, Configuration config)
	{
		this.Enabled = config.get(this.name, "Am I enabled? (default true)", true).getBoolean(true);
		this.Speed = config.get(this.name, "How fast are my projectiles? (default 2.0 BPT (Blocks Per Tick))", 2.0).getDouble();
		this.Kickback = (byte) config.get(this.name, "How hard do I kick the user back when firing? (default 3)", 3).getInt();
		this.ExplosionSize = config.get(this.name, "How big are my explosions? (default 4.0 blocks, like TNT)", 4.0).getDouble();
		this.dmgTerrain = config.get(this.name, "Can I damage terrain, when in player hands? (default true)", true).getBoolean(true);

		this.isMobUsable = config.get(this.name, "Can I be used by QuiverMobs? (default true)", true).getBoolean(true);
	}


	@Override
	public void addRecipes()
	{
		if (this.Enabled)
		{
			// One Improved Rocket Launcher (empty)
			GameRegistry.addRecipe(new ItemStack(this, 1 , this.getMaxDamage()), "xxx", "yzy", "xxx",
					'x', Blocks.obsidian, 							// Adding an obsidian frame to the RPG
					'y', Items.iron_ingot,
					'z', Helper.getWeaponStackByClass(RPG.class, true)
					);
		}
		else if (Main.noCreative) { this.setCreativeTab(null); }	// Not enabled and not allowed to be in the creative menu

		// Fill the launcher with 1 big rocket
		GameRegistry.addShapelessRecipe(new ItemStack(this), Helper.getAmmoStack(LargeRocket.class, 0), new ItemStack(this, 1, 1));
	}


	@Override
	public String getModelTexPath(ItemStack stack)	// The model texture path
	{
		if (stack.getItemDamage() >= stack.getMaxDamage()) { return "RPG_Imp_empty"; }	// Empty

		return "RPG_Imp";	// Regular
	}
}
