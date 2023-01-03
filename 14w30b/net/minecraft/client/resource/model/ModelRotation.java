package net.minecraft.client.resource.model;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4d;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public enum ModelRotation {
   X0_Y0(0, 0),
   X0_Y90(0, 90),
   X0_Y180(0, 180),
   X0_Y270(0, 270),
   X90_Y0(90, 0),
   X90_Y90(90, 90),
   X90_Y180(90, 180),
   X90_Y270(90, 270),
   X180_Y0(180, 0),
   X180_Y90(180, 90),
   X180_Y180(180, 180),
   X180_Y270(180, 270),
   X270_Y0(270, 0),
   X270_Y90(270, 90),
   X270_Y180(270, 180),
   X270_Y270(270, 270);

   private static final Map BY_INDEX = Maps.newHashMap();
   private final int index;
   private final Matrix4d rotation;
   private final int stepsX;
   private final int stepsY;

   private static int getIndex(int x, int y) {
      return x * 360 + y;
   }

   private ModelRotation(int x, int y) {
      this.index = getIndex(x, y);
      this.rotation = new Matrix4d();
      Matrix4d var5 = new Matrix4d();
      var5.setIdentity();
      var5.setRotation(new AxisAngle4d(1.0, 0.0, 0.0, (double)((float)(-x) * (float) (Math.PI / 180.0))));
      this.stepsX = MathHelper.abs(x / 90);
      Matrix4d var6 = new Matrix4d();
      var6.setIdentity();
      var6.setRotation(new AxisAngle4d(0.0, 1.0, 0.0, (double)((float)(-y) * (float) (Math.PI / 180.0))));
      this.stepsY = MathHelper.abs(y / 90);
      this.rotation.mul(var6, var5);
   }

   public Matrix4d getRotation() {
      return this.rotation;
   }

   public Direction apply(Direction dir) {
      Direction var2 = dir;

      for(int var3 = 0; var3 < this.stepsX; ++var3) {
         var2 = var2.clockwise(Direction.Axis.X);
      }

      if (var2.getAxis() != Direction.Axis.Y) {
         for(int var4 = 0; var4 < this.stepsY; ++var4) {
            var2 = var2.clockwise(Direction.Axis.Y);
         }
      }

      return var2;
   }

   public int apply(Direction dir, int vertex) {
      int var3 = vertex;
      if (dir.getAxis() == Direction.Axis.X) {
         var3 = (vertex + this.stepsX) % 4;
      }

      Direction var4 = dir;

      for(int var5 = 0; var5 < this.stepsX; ++var5) {
         var4 = var4.clockwise(Direction.Axis.X);
      }

      if (var4.getAxis() == Direction.Axis.Y) {
         var3 = (var3 + this.stepsY) % 4;
      }

      return var3;
   }

   public static ModelRotation by(int x, int y) {
      return (ModelRotation)BY_INDEX.get(getIndex(MathHelper.floorMod(x, 360), MathHelper.floorMod(y, 360)));
   }

   static {
      for(ModelRotation var3 : values()) {
         BY_INDEX.put(var3.index, var3);
      }
   }
}
