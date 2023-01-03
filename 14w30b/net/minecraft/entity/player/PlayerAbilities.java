package net.minecraft.entity.player;

import net.minecraft.nbt.NbtCompound;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class PlayerAbilities {
   public boolean invulnerable;
   public boolean flying;
   public boolean canFly;
   public boolean creativeMode;
   public boolean canModifyWorld = true;
   private float flySpeed = 0.05F;
   private float walkSpeed = 0.1F;

   public void writeNbt(NbtCompound nbt) {
      NbtCompound var2 = new NbtCompound();
      var2.putBoolean("invulnerable", this.invulnerable);
      var2.putBoolean("flying", this.flying);
      var2.putBoolean("mayfly", this.canFly);
      var2.putBoolean("instabuild", this.creativeMode);
      var2.putBoolean("mayBuild", this.canModifyWorld);
      var2.putFloat("flySpeed", this.flySpeed);
      var2.putFloat("walkSpeed", this.walkSpeed);
      nbt.put("abilities", var2);
   }

   public void readNbt(NbtCompound nbt) {
      if (nbt.isType("abilities", 10)) {
         NbtCompound var2 = nbt.getCompound("abilities");
         this.invulnerable = var2.getBoolean("invulnerable");
         this.flying = var2.getBoolean("flying");
         this.canFly = var2.getBoolean("mayfly");
         this.creativeMode = var2.getBoolean("instabuild");
         if (var2.isType("flySpeed", 99)) {
            this.flySpeed = var2.getFloat("flySpeed");
            this.walkSpeed = var2.getFloat("walkSpeed");
         }

         if (var2.isType("mayBuild", 1)) {
            this.canModifyWorld = var2.getBoolean("mayBuild");
         }
      }
   }

   public float getFlySpeed() {
      return this.flySpeed;
   }

   @Environment(EnvType.CLIENT)
   public void setFlySpeed(float flySpeed) {
      this.flySpeed = flySpeed;
   }

   public float getWalkSpeed() {
      return this.walkSpeed;
   }

   @Environment(EnvType.CLIENT)
   public void setWalkSpeed(float walkSpeed) {
      this.walkSpeed = walkSpeed;
   }
}
