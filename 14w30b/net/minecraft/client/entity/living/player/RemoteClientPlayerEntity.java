package net.minecraft.client.entity.living.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class RemoteClientPlayerEntity extends ClientPlayerEntity {
   private boolean f_61jhcvien;
   private int f_91bqouepw;
   private double f_25hmwzfqa;
   private double f_84socdtub;
   private double f_62wwhawcd;
   private double f_94lzxsmlz;
   private double f_39cfboftm;

   public RemoteClientPlayerEntity(World c_54ruxjwzt, GameProfile gameProfile) {
      super(c_54ruxjwzt, gameProfile);
      this.stepHeight = 0.0F;
      this.noClip = true;
      this.sleepOffsetY = 0.25F;
      this.viewDistanceScaling = 10.0;
   }

   @Override
   public boolean damage(DamageSource source, float amount) {
      return true;
   }

   @Override
   public void updatePositionAndAngles(double x, double y, double z, float yaw, float pitch, int i, boolean bl) {
      this.f_25hmwzfqa = x;
      this.f_84socdtub = y;
      this.f_62wwhawcd = z;
      this.f_94lzxsmlz = (double)yaw;
      this.f_39cfboftm = (double)pitch;
      this.f_91bqouepw = i;
   }

   @Override
   public void tick() {
      this.sleepOffsetY = 0.0F;
      super.tick();
      this.prevHandSwingAmount = this.handSwingAmount;
      double var1 = this.x - this.prevX;
      double var3 = this.z - this.prevZ;
      float var5 = MathHelper.sqrt(var1 * var1 + var3 * var3) * 4.0F;
      if (var5 > 1.0F) {
         var5 = 1.0F;
      }

      this.handSwingAmount += (var5 - this.handSwingAmount) * 0.4F;
      this.handSwing += this.handSwingAmount;
      if (!this.f_61jhcvien && this.isSwimming() && this.inventory.inventorySlots[this.inventory.selectedSlot] != null) {
         ItemStack var6 = this.inventory.inventorySlots[this.inventory.selectedSlot];
         this.setUseItem(this.inventory.inventorySlots[this.inventory.selectedSlot], var6.getItem().getUseDuration(var6));
         this.f_61jhcvien = true;
      } else if (this.f_61jhcvien && !this.isSwimming()) {
         this.emptyHand();
         this.f_61jhcvien = false;
      }
   }

   @Override
   public void tickAI() {
      if (this.f_91bqouepw > 0) {
         double var1 = this.x + (this.f_25hmwzfqa - this.x) / (double)this.f_91bqouepw;
         double var3 = this.y + (this.f_84socdtub - this.y) / (double)this.f_91bqouepw;
         double var5 = this.z + (this.f_62wwhawcd - this.z) / (double)this.f_91bqouepw;
         double var7 = this.f_94lzxsmlz - (double)this.yaw;

         while(var7 < -180.0) {
            var7 += 360.0;
         }

         while(var7 >= 180.0) {
            var7 -= 360.0;
         }

         this.yaw = (float)((double)this.yaw + var7 / (double)this.f_91bqouepw);
         this.pitch = (float)((double)this.pitch + (this.f_39cfboftm - (double)this.pitch) / (double)this.f_91bqouepw);
         --this.f_91bqouepw;
         this.setPosition(var1, var3, var5);
         this.setRotation(this.yaw, this.pitch);
      }

      this.prevStrideDistance = this.strideDistance;
      this.updateHandSwing();
      float var9 = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
      float var2 = (float)Math.atan(-this.velocityY * 0.2F) * 15.0F;
      if (var9 > 0.1F) {
         var9 = 0.1F;
      }

      if (!this.onGround || this.getHealth() <= 0.0F) {
         var9 = 0.0F;
      }

      if (this.onGround || this.getHealth() <= 0.0F) {
         var2 = 0.0F;
      }

      this.strideDistance += (var9 - this.strideDistance) * 0.4F;
      this.cameraPitch += (var2 - this.cameraPitch) * 0.8F;
   }

   @Override
   public void setEquipmentStack(int slot, ItemStack stack) {
      if (slot == 0) {
         this.inventory.inventorySlots[this.inventory.selectedSlot] = stack;
      } else {
         this.inventory.armorSlots[slot - 1] = stack;
      }
   }

   @Override
   public void sendMessage(Text message) {
      MinecraftClient.getInstance().gui.getChat().addMessage(message);
   }

   @Override
   public boolean canUseCommand(int permissionLevel, String command) {
      return false;
   }

   @Override
   public BlockPos getSourceBlockPos() {
      return new BlockPos(this.x + 0.5, this.y + 0.5, this.z + 0.5);
   }
}
