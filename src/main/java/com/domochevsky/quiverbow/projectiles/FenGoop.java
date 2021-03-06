package com.domochevsky.quiverbow.projectiles;

import com.domochevsky.quiverbow.blocks.BlockRegistry;
import com.domochevsky.quiverbow.blocks.FenLight;
import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;

import net.minecraft.block.SoundType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class FenGoop extends ProjectileBase
{
    public FenGoop(World world)
    {
        super(world);
    }

    public FenGoop(World world, Entity entity, WeaponProperties properties)
    {
        super(world);
        this.doSetup(entity, properties.getProjectileSpeed());
        this.fireDuration = properties.getInt(CommonProperties.FIRE_DUR_ENTITY);
        this.lightTick = properties.getInt(CommonProperties.DESPAWN_TIME);
    }

    private int lightTick;

    @Override
    public void onImpact(RayTraceResult target)
    {
        if (target.entityHit != null) // hit a entity
        {
            target.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getShooter()), 0); // No
            // dmg,
            // but
            // knockback
            target.entityHit.hurtResistantTime = 0;
            target.entityHit.setFire(fireDuration); // Some minor fire, for
            // flavor
        }
        else // hit the terrain
        {
            BlockPos pos = target.getBlockPos().offset(target.sideHit);

            // Is the attached block a valid material?
            boolean canPlace = false;

            if (world.isSideSolid(target.getBlockPos(), target.sideHit, false))
            {
                canPlace = true;
            }
            else if (this.world.getBlockState(target.getBlockPos()).getBlock().isReplaceable(world,
                    target.getBlockPos()))
            {
                canPlace = true;
                pos = target.getBlockPos();
            }

            // Putting light there (if we can), otherwise dropping 1 glowstone
            // dust
            if (canPlace)
            {
                FenLight.placeFenLight(world, pos, target.sideHit);

                if (this.lightTick != 0)
                {
                    this.world.scheduleBlockUpdate(pos, BlockRegistry.FEN_LIGHT, this.lightTick, 1);
                }
            }
            else if (!world.isRemote)
                world.spawnEntity(
                        new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.GLOWSTONE_DUST)));
        }

        // SFX
        for (int i = 0; i < 8; ++i)
        {
            this.world.spawnParticle(EnumParticleTypes.SLIME, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
        }
        this.playSound(SoundType.GLASS.getBreakSound(), 1.0F, 1.0F);

        this.setDead(); // We've hit something, so begone with the projectile
    }
}
