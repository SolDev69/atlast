package net.minecraft.client.render.model.block;

import javax.vecmath.Vector3f;
import net.minecraft.util.math.Direction;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class BlockElementRotation {
   public final Vector3f origin;
   public final Direction.Axis axis;
   public final float angle;
   public final boolean rescale;

   public BlockElementRotation(Vector3f vector3f, Direction.Axis c_61vewjfoe, float f, boolean bl) {
      this.origin = vector3f;
      this.axis = c_61vewjfoe;
      this.angle = f;
      this.rescale = bl;
   }
}
