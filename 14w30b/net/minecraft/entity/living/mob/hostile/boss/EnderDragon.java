package net.minecraft.entity.living.mob.hostile.boss;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.World;

public interface EnderDragon {
   World getWorld();

   boolean damage(EnderDragonPart part, DamageSource source, float amount);
}
