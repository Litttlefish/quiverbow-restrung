package com.domochevsky.quiverbow.weapons.base.fireshape;

import java.util.ArrayList;

import com.domochevsky.quiverbow.Helper;
import com.domochevsky.quiverbow.config.WeaponProperties;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class HitscanFireShape implements FireShape
{
    private final HitEffect hitEffect;
    private final int piercing;

    public HitscanFireShape(HitEffect hitEffect)
    {
        this(hitEffect, 0);
    }

    public HitscanFireShape(HitEffect hitEffect, int piercing)
    {
        this.hitEffect = hitEffect;
        this.piercing = piercing;
    }

    @Override
    public boolean fire(World world, EntityLivingBase shooter, ItemStack stack, WeaponProperties properties)
    {
        Vec3d eyeVec = shooter.getPositionVector().addVector(0.0D, shooter.getEyeHeight(), 0.0D);
        Vec3d endVec = eyeVec.add(shooter.getLookVec().scale(shooter.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue()));
        if (piercing > 0)
        {
            for (RayTraceResult result : Helper.raytraceAll(new ArrayList<>(), world, shooter, eyeVec, endVec))
            {
                if (!processRay(world, shooter, properties, result))
                    return false;
            }
            return true;
        }
        else
            return processRay(world, shooter, properties, Helper.raytraceClosestObject(world, shooter, eyeVec, endVec));
    }

    private boolean processRay(World world, EntityLivingBase shooter, WeaponProperties properties, RayTraceResult result)
    {
        if (result == null)
            return false;
        switch (result.typeOfHit)
        {
        case BLOCK:
            BlockPos pos = result.getBlockPos();
            hitEffect.apply(world, shooter, properties, pos.getX(), pos.getY(), pos.getZ());
            break;
        case ENTITY:
            hitEffect.apply(world, shooter, properties, result.entityHit.posX, result.entityHit.posY, result.entityHit.posZ);
            break;
        default:
            return false;
        }
        return true;
    }

    public interface HitEffect
    {
        public void apply(World world, EntityLivingBase user, WeaponProperties properties, double x, double y, double z);
    }
}