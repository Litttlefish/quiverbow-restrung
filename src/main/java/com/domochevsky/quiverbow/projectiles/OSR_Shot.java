package com.domochevsky.quiverbow.projectiles;

import net.minecraft.entity.Entity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.net.NetHelper;

public class OSR_Shot extends ProjectilePotionEffect
{
	public int entitiesHit;		
	
	public OSR_Shot(World world) { super(world); }

	public OSR_Shot(World world, Entity entity, float speed, PotionEffect... effects)
	{
	    super(world, effects);
	    this.doSetup(entity, speed);
	}
	
	
	@Override
	public void doFlightSFX()
	{				
		NetHelper.sendParticleMessageToAllPlayers(this.worldObj, this.getEntityId(), (byte) 3, (byte) 1);
	}
	
	
	@Override
	public void onImpact(MovingObjectPosition movPos)
	{
	    if (movPos.entityHit != null) 		// We hit a living thing!
	    {		
		super.onImpact(movPos);

		this.setDead();	// Hit an entity, so begone.
	    }
	    else 	// Hit the terrain
	    {
		// Glass breaking
		if (Helper.tryBlockBreak(this.worldObj, this, movPos, 2) && this.entitiesHit < 2) { this.entitiesHit += 1; }
		else { this.setDead(); }	// Punching through glass, 2 thick
	    }

	    // SFX
	    NetHelper.sendParticleMessageToAllPlayers(this.worldObj, this.getEntityId(), (byte) 3, (byte) 1);
	    this.worldObj.playSoundAtEntity(this, "random.bowhit", 1.0F, 0.5F);
	}
	
	
	@Override
	public byte[] getRenderType()
	{
		byte[] type = new byte[3];
		
		type[0] = 2;	// Type 2, generic projectile
		type[1] = 16;	// Length
		type[2] = 2;	// Width
		
		return type;
	}
	
	
	@Override
	public String getEntityTexturePath() { return "textures/entity/obsidian.png"; }	// Our projectile texture
}
