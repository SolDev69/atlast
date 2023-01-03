package net.minecraft.entity.damage;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class DamageTracker {
   private final List damageHistory = Lists.newArrayList();
   private final LivingEntity entity;
   private int ageOnLastDamage;
   private int f_87foxmawe;
   private int f_14yqlkokw;
   private boolean recentlyAttacked;
   private boolean hasDamage;
   private String fallDeathSuffix;

   public DamageTracker(LivingEntity entity) {
      this.entity = entity;
   }

   public void setFallDeathSuffix() {
      this.clearFallDeathSuffix();
      if (this.entity.isClimbing()) {
         Block var1 = this.entity.world.getBlockState(new BlockPos(this.entity.x, this.entity.getBoundingBox().minY, this.entity.z)).getBlock();
         if (var1 == Blocks.LADDER) {
            this.fallDeathSuffix = "ladder";
         } else if (var1 == Blocks.VINE) {
            this.fallDeathSuffix = "vines";
         }
      } else if (this.entity.isInWater()) {
         this.fallDeathSuffix = "water";
      }
   }

   public void onDamage(DamageSource damageSource, float originalHealth, float damage) {
      this.clearDamageHistory();
      this.setFallDeathSuffix();
      DamageRecord var4 = new DamageRecord(damageSource, this.entity.time, originalHealth, damage, this.fallDeathSuffix, this.entity.fallDistance);
      this.damageHistory.add(var4);
      this.ageOnLastDamage = this.entity.time;
      this.hasDamage = true;
      if (var4.isAttackerLiving() && !this.recentlyAttacked && this.entity.isAlive()) {
         this.recentlyAttacked = true;
         this.f_87foxmawe = this.entity.time;
         this.f_14yqlkokw = this.f_87foxmawe;
         this.entity.m_18fvbnxav();
      }
   }

   public Text getDeathMessage() {
      if (this.damageHistory.size() == 0) {
         return new TranslatableText("death.attack.generic", this.entity.getDisplayName());
      } else {
         DamageRecord var1 = this.getBiggestFall();
         DamageRecord var2 = (DamageRecord)this.damageHistory.get(this.damageHistory.size() - 1);
         Text var4 = var2.getAttackerName();
         Entity var5 = var2.getDamageSource().getAttacker();
         Object var3;
         if (var1 != null && var2.getDamageSource() == DamageSource.FALL) {
            Text var6 = var1.getAttackerName();
            if (var1.getDamageSource() == DamageSource.FALL || var1.getDamageSource() == DamageSource.OUT_OF_WORLD) {
               var3 = new TranslatableText("death.fell.accident." + this.getFallDeathSuffix(var1), this.entity.getDisplayName());
            } else if (var6 != null && (var4 == null || !var6.equals(var4))) {
               Entity var9 = var1.getDamageSource().getAttacker();
               ItemStack var8 = var9 instanceof LivingEntity ? ((LivingEntity)var9).getStackInHand() : null;
               if (var8 != null && var8.hasCustomHoverName()) {
                  var3 = new TranslatableText("death.fell.assist.item", this.entity.getDisplayName(), var6, var8.getDisplayName());
               } else {
                  var3 = new TranslatableText("death.fell.assist", this.entity.getDisplayName(), var6);
               }
            } else if (var4 != null) {
               ItemStack var7 = var5 instanceof LivingEntity ? ((LivingEntity)var5).getStackInHand() : null;
               if (var7 != null && var7.hasCustomHoverName()) {
                  var3 = new TranslatableText("death.fell.finish.item", this.entity.getDisplayName(), var4, var7.getDisplayName());
               } else {
                  var3 = new TranslatableText("death.fell.finish", this.entity.getDisplayName(), var4);
               }
            } else {
               var3 = new TranslatableText("death.fell.killer", this.entity.getDisplayName());
            }
         } else {
            var3 = var2.getDamageSource().getDeathMessage(this.entity);
         }

         return (Text)var3;
      }
   }

   public LivingEntity getLastAttacker() {
      LivingEntity var1 = null;
      PlayerEntity var2 = null;
      float var3 = 0.0F;
      float var4 = 0.0F;

      for(DamageRecord var6 : this.damageHistory) {
         if (var6.getDamageSource().getAttacker() instanceof PlayerEntity && (var2 == null || var6.getDamage() > var4)) {
            var4 = var6.getDamage();
            var2 = (PlayerEntity)var6.getDamageSource().getAttacker();
         }

         if (var6.getDamageSource().getAttacker() instanceof LivingEntity && (var1 == null || var6.getDamage() > var3)) {
            var3 = var6.getDamage();
            var1 = (LivingEntity)var6.getDamageSource().getAttacker();
         }
      }

      return (LivingEntity)(var2 != null && var4 >= var3 / 3.0F ? var2 : var1);
   }

   private DamageRecord getBiggestFall() {
      DamageRecord var1 = null;
      DamageRecord var2 = null;
      byte var3 = 0;
      float var4 = 0.0F;

      for(int var5 = 0; var5 < this.damageHistory.size(); ++var5) {
         DamageRecord var6 = (DamageRecord)this.damageHistory.get(var5);
         DamageRecord var7 = var5 > 0 ? (DamageRecord)this.damageHistory.get(var5 - 1) : null;
         if ((var6.getDamageSource() == DamageSource.FALL || var6.getDamageSource() == DamageSource.OUT_OF_WORLD)
            && var6.getFallDistance() > 0.0F
            && (var1 == null || var6.getFallDistance() > var4)) {
            if (var5 > 0) {
               var1 = var7;
            } else {
               var1 = var6;
            }

            var4 = var6.getFallDistance();
         }

         if (var6.getFallDeathSuffix() != null && (var2 == null || var6.getDamage() > (float)var3)) {
            var2 = var6;
         }
      }

      if (var4 > 5.0F && var1 != null) {
         return var1;
      } else {
         return var3 > 5 && var2 != null ? var2 : null;
      }
   }

   private String getFallDeathSuffix(DamageRecord damageRecord) {
      return damageRecord.getFallDeathSuffix() == null ? "generic" : damageRecord.getFallDeathSuffix();
   }

   public int getDuration() {
      return this.recentlyAttacked ? this.entity.time - this.f_87foxmawe : this.f_14yqlkokw - this.f_87foxmawe;
   }

   private void clearFallDeathSuffix() {
      this.fallDeathSuffix = null;
   }

   public void clearDamageHistory() {
      int var1 = this.recentlyAttacked ? 300 : 100;
      if (this.hasDamage && (!this.entity.isAlive() || this.entity.time - this.ageOnLastDamage > var1)) {
         boolean var2 = this.recentlyAttacked;
         this.hasDamage = false;
         this.recentlyAttacked = false;
         this.f_14yqlkokw = this.entity.time;
         if (var2) {
            this.entity.m_10fgolizq();
         }

         this.damageHistory.clear();
      }
   }

   public LivingEntity getPlayer() {
      return this.entity;
   }
}
