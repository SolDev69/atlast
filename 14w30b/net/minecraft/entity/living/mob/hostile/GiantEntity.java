package net.minecraft.entity.living.mob.hostile;

import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GiantEntity extends HostileEntity {
   public GiantEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.setDimensions(this.width * 6.0F, this.height * 6.0F);
   }

   @Override
   public float getEyeHeight() {
      return 10.440001F;
   }

   @Override
   protected void initAttributes() {
      super.initAttributes();
      this.initializeAttribute(EntityAttributes.MAX_HEALTH).setBase(100.0);
      this.initializeAttribute(EntityAttributes.MOVEMENT_SPEED).setBase(0.5);
      this.initializeAttribute(EntityAttributes.ATTACK_DAMAGE).setBase(50.0);
   }

   @Override
   public float getPathfindingFavor(BlockPos x) {
      return this.world.getBrightness(x) - 0.5F;
   }
}
