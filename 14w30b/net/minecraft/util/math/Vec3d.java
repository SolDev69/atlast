package net.minecraft.util.math;

import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class Vec3d {
   public final double x;
   public final double y;
   public final double z;

   public Vec3d(double x, double y, double z) {
      if (x == -0.0) {
         x = 0.0;
      }

      if (y == -0.0) {
         y = 0.0;
      }

      if (z == -0.0) {
         z = 0.0;
      }

      this.x = x;
      this.y = y;
      this.z = z;
   }

   @Environment(EnvType.CLIENT)
   public Vec3d subtractFrom(Vec3d vec) {
      return new Vec3d(vec.x - this.x, vec.y - this.y, vec.z - this.z);
   }

   public Vec3d normalize() {
      double var1 = (double)MathHelper.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
      return var1 < 1.0E-4 ? new Vec3d(0.0, 0.0, 0.0) : new Vec3d(this.x / var1, this.y / var1, this.z / var1);
   }

   public double dot(Vec3d vec) {
      return this.x * vec.x + this.y * vec.y + this.z * vec.z;
   }

   @Environment(EnvType.CLIENT)
   public Vec3d cross(Vec3d vec) {
      return new Vec3d(this.y * vec.z - this.z * vec.y, this.z * vec.x - this.x * vec.z, this.x * vec.y - this.y * vec.x);
   }

   public Vec3d subtract(Vec3d vec) {
      return this.subtract(vec.x, vec.y, vec.z);
   }

   public Vec3d subtract(double x, double y, double z) {
      return this.add(-x, -y, -z);
   }

   public Vec3d add(Vec3d vec) {
      return this.add(vec.x, vec.y, vec.z);
   }

   public Vec3d add(double x, double y, double z) {
      return new Vec3d(this.x + x, this.y + y, this.z + z);
   }

   public double distanceTo(Vec3d vec) {
      double var2 = vec.x - this.x;
      double var4 = vec.y - this.y;
      double var6 = vec.z - this.z;
      return (double)MathHelper.sqrt(var2 * var2 + var4 * var4 + var6 * var6);
   }

   public double squaredDistanceTo(Vec3d vec) {
      double var2 = vec.x - this.x;
      double var4 = vec.y - this.y;
      double var6 = vec.z - this.z;
      return var2 * var2 + var4 * var4 + var6 * var6;
   }

   public double length() {
      return (double)MathHelper.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
   }

   public Vec3d intermediateWithX(Vec3d vec, double x) {
      double var4 = vec.x - this.x;
      double var6 = vec.y - this.y;
      double var8 = vec.z - this.z;
      if (var4 * var4 < 1.0E-7F) {
         return null;
      } else {
         double var10 = (x - this.x) / var4;
         return !(var10 < 0.0) && !(var10 > 1.0) ? new Vec3d(this.x + var4 * var10, this.y + var6 * var10, this.z + var8 * var10) : null;
      }
   }

   public Vec3d intermediateWithY(Vec3d vec, double y) {
      double var4 = vec.x - this.x;
      double var6 = vec.y - this.y;
      double var8 = vec.z - this.z;
      if (var6 * var6 < 1.0E-7F) {
         return null;
      } else {
         double var10 = (y - this.y) / var6;
         return !(var10 < 0.0) && !(var10 > 1.0) ? new Vec3d(this.x + var4 * var10, this.y + var6 * var10, this.z + var8 * var10) : null;
      }
   }

   public Vec3d intermediateWithZ(Vec3d vec, double z) {
      double var4 = vec.x - this.x;
      double var6 = vec.y - this.y;
      double var8 = vec.z - this.z;
      if (var8 * var8 < 1.0E-7F) {
         return null;
      } else {
         double var10 = (z - this.z) / var8;
         return !(var10 < 0.0) && !(var10 > 1.0) ? new Vec3d(this.x + var4 * var10, this.y + var6 * var10, this.z + var8 * var10) : null;
      }
   }

   @Override
   public String toString() {
      return "(" + this.x + ", " + this.y + ", " + this.z + ")";
   }

   public Vec3d rotateX(float angle) {
      float var2 = MathHelper.cos(angle);
      float var3 = MathHelper.sin(angle);
      double var4 = this.x;
      double var6 = this.y * (double)var2 + this.z * (double)var3;
      double var8 = this.z * (double)var2 - this.y * (double)var3;
      return new Vec3d(var4, var6, var8);
   }

   public Vec3d rotateY(float angle) {
      float var2 = MathHelper.cos(angle);
      float var3 = MathHelper.sin(angle);
      double var4 = this.x * (double)var2 + this.z * (double)var3;
      double var6 = this.y;
      double var8 = this.z * (double)var2 - this.x * (double)var3;
      return new Vec3d(var4, var6, var8);
   }
}
