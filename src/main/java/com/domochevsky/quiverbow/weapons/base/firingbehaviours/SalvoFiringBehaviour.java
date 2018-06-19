package com.domochevsky.quiverbow.weapons.base.firingbehaviours;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.weapons.base.MagazineFedWeapon;
import com.domochevsky.quiverbow.weapons.base.WeaponBase;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class SalvoFiringBehaviour<W extends WeaponBase> extends ProjectileFiringBehaviour<W>
{
	public static class SalvoData implements IProjectileData
	{
		public final int shotCount;

		public SalvoData(int shotCount)
		{
			this.shotCount = shotCount;
		}
	}

	private final int shotQuantity;

	public SalvoFiringBehaviour(W weapon, int shotQuantity, IProjectileFactory projectileFactory)
	{
		super(weapon, projectileFactory);
		this.shotQuantity = shotQuantity;
	}

	@Override
	public void fire(ItemStack stack, World world, EntityLivingBase entity, EnumHand hand)
	{
		if (weapon.getCooldown(stack) > 0)
		{
			return;
		} // Hasn't cooled down yet

		Helper.knockUserBack(entity, weapon.getKickback()); // Kickback

		weapon.resetCooldown(stack); // Cooling down now

		for (int shot = 0; shot < shotQuantity; shot++)
		{
			if (!world.isRemote)
				world.spawnEntity(projectileFactory.createProjectile(world, stack, entity, new SalvoData(shot), weapon.getProperties()));

			weapon.doFireFX(world, entity);

			if (weapon.consumeAmmo(stack, entity, 1) && weapon instanceof MagazineFedWeapon)
			{
				((MagazineFedWeapon) weapon).dropMagazine(world, stack, entity);
				return;
			}
			// else, still has ammo left. Continue.
		}
	}

	@Override
	public void update(ItemStack stack, World world, Entity entity, int animTick, boolean holdingItem)
	{}
}
