package com.domochevsky.quiverbow.projectiles;

import com.domochevsky.quiverbow.net.NetHelper;

import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class RedSpray extends ProjectilePotionEffect
{
	public RedSpray(World world)
	{
		super(world);
	}

	public RedSpray(World world, Entity entity, float speed, float accHor, float accVert, PotionEffect... effects)
	{
		super(world, effects);
		this.damage = 0;
		this.doSetup(entity, speed, accHor, accVert);
	}

	@Override
	public void doFlightSFX()
	{
		NetHelper.sendParticleMessageToAllPlayers(this.world, this, EnumParticleTypes.REDSTONE, (byte) 4);
	}

	@Override
	public void onImpact(RayTraceResult movPos)
	{
		if (movPos.entityHit != null) // We hit a living thing!
		{
			super.onImpact(movPos);
		}
		// else, hit the terrain

		// SFX
		this.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 0.7F, 1.5F);
		this.world.spawnParticle(EnumParticleTypes.REDSTONE, this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);

		this.setDead(); // We've hit something, so begone with the projectile
	}

	@Override
	public byte[] getRenderType()
	{
		byte[] type = new byte[3];

		type[0] = 2; // Type 2, generic projectile
		type[1] = 2; // Length
		type[2] = 2; // Width

		return type; // Fallback, 0 0 0
	}

	@Override
	public String getEntityTexturePath()
	{
		return "textures/entity/redspray.png";
	} // Our projectile texture
}
