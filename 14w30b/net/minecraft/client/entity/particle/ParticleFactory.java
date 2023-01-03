package net.minecraft.client.entity.particle;

import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public interface ParticleFactory {
   Particle create(int type, World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters);
}
