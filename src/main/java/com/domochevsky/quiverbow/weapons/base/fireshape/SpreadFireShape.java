package com.domochevsky.quiverbow.weapons.base.fireshape;

import org.apache.commons.lang3.tuple.Pair;

import com.domochevsky.quiverbow.config.WeaponProperties;
import com.domochevsky.quiverbow.weapons.base.CommonProperties;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class SpreadFireShape implements FireShape
{
    public static final Pair<String, String> PROJECTILES =
        Pair.of("projectiles", "How many projectiles are spawned when fired");
    private final SpreadProjectileFactory projectileFactory;

    public SpreadFireShape(SpreadProjectileFactory projectileFactory)
    {
        this.projectileFactory = projectileFactory;
    }

    @Override
    public boolean fire(World world, EntityLivingBase shooter, ItemStack stack, WeaponProperties properties)
    {
        if (!world.isRemote)
        {
            float spread = properties.getFloat(CommonProperties.SPREAD);
            for (int i = 0; i < properties.getInt(PROJECTILES); i++)
            {
                // http://www.anderswallin.net/2009/05/uniform-random-points-in-a-circle-using-polar-coordinates/
                float theta = world.rand.nextFloat() * 2 * (float) Math.PI;
                float r = spread * MathHelper.sqrt(world.rand.nextFloat());
                float spreadHor = r * MathHelper.cos(theta);
                float spreadVert = r * MathHelper.sin(theta);
                world.spawnEntity(projectileFactory.createProjectile(world, shooter, properties, spreadHor, spreadVert));
            }
        }
        return true;
    }

    public interface SpreadProjectileFactory
    {
        public Entity createProjectile(World world, EntityLivingBase shooter, WeaponProperties properties, float spreadHor, float spreadVert);
    }
}
