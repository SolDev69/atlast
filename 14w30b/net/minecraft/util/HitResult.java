package net.minecraft.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class HitResult {
   private BlockPos blockPos;
   public HitResult.Type type;
   public Direction face;
   public Vec3d pos;
   public Entity entity;

   public HitResult(Vec3d pos, Direction face, BlockPos blockPos) {
      this(HitResult.Type.BLOCK, pos, face, blockPos);
   }

   public HitResult(Vec3d pos, Direction face) {
      this(HitResult.Type.BLOCK, pos, face, BlockPos.ORIGIN);
   }

   public HitResult(Entity entity) {
      this(entity, new Vec3d(entity.x, entity.y, entity.z));
   }

   public HitResult(HitResult.Type type, Vec3d pos, Direction face, BlockPos blockPos) {
      this.type = type;
      this.blockPos = blockPos;
      this.face = face;
      this.pos = new Vec3d(pos.x, pos.y, pos.z);
   }

   public HitResult(Entity entity, Vec3d pos) {
      this.type = HitResult.Type.ENTITY;
      this.entity = entity;
      this.pos = pos;
   }

   public BlockPos getBlockPos() {
      return this.blockPos;
   }

   @Override
   public String toString() {
      return "HitResult{type=" + this.type + ", blockpos=" + this.blockPos + ", f=" + this.face + ", pos=" + this.pos + ", entity=" + this.entity + '}';
   }

   public static enum Type {
      MISS,
      BLOCK,
      ENTITY;
   }
}
