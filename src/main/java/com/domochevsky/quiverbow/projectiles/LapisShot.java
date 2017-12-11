package com.domochevsky.quiverbow.projectiles;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.net.NetHelper;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class LapisShot extends ProjectilePotionEffect
{
    public LapisShot(World world)
    {
	super(world);
    }

    public LapisShot(World world, Entity entity, float speed, PotionEffect... effects)
    {
	super(world, effects);
	this.doSetup(entity, speed);
    }

    @Override
    public void onImpact(RayTraceResult movPos) // Server-side
    {
	if (movPos.entityHit != null) // We hit a living thing!
	{
	    super.onImpact(movPos);

	    this.setDead(); // We've hit something, so begone with the
			    // projectile
	}

	else // Hit the terrain
	{
	    // Helper.tryBlockBreak(this.world, this, movPos, 1);

	    if (Helper.tryBlockBreak(this.world, this, movPos.getBlockPos(), 1))
	    {
		this.setDead();
	    } // Going straight through a thing
	    else // Didn't manage to break that block, so we're stuck now for a
		 // short while
	    {

		this.stuckBlockX = movPos.getBlockPos().getX();
		this.stuckBlockY = movPos.getBlockPos().getY();
		this.stuckBlockZ = movPos.getBlockPos().getZ();

		IBlockState stuckState = world.getBlockState(movPos.getBlockPos());
		this.stuckBlock = stuckState.getBlock();

		this.motionX = (double) ((float) (movPos.hitVec.xCoord - this.posX));
		this.motionY = (double) ((float) (movPos.hitVec.yCoord - this.posY));
		this.motionZ = (double) ((float) (movPos.hitVec.zCoord - this.posZ));

		float distance = MathHelper
			.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);

		this.posX -= this.motionX / (double) distance * 0.05000000074505806D;
		this.posY -= this.motionY / (double) distance * 0.05000000074505806D;
		this.posZ -= this.motionZ / (double) distance * 0.05000000074505806D;

		this.inGround = true;

		this.arrowShake = 7;

		if (stuckState.getMaterial() != Material.AIR)
		{
		    this.stuckBlock.onEntityCollidedWithBlock(this.world, movPos.getBlockPos(), stuckState, this);
		}
	    }
	}

	// SFX
	this.playSound(SoundEvents.BLOCK_WOOD_BUTTON_CLICK_ON, 1.0F, 0.5F);
	NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), EnumParticleTypes.SMOKE_NORMAL,
		(byte) 4);
    }

    @Override
    public void doFlightSFX()
    {
	NetHelper.sendParticleMessageToAllPlayers(this.world, this.getEntityId(), EnumParticleTypes.SPELL, (byte) 2);
    }

    @Override
    public byte[] getRenderType()
    {
	byte[] type = new byte[3];

	type[0] = 2; // Type 2, generic projectile
	type[1] = 8; // Length
	type[2] = 2; // Width

	return type; // Fallback, 0 0 0
    }

    @Override
    public String getEntityTexturePath()
    {
	return "textures/entity/lapis.png";
    } // Our projectile texture
}
