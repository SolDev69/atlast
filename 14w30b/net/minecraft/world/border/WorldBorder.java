package net.minecraft.world.border;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class WorldBorder {
   private final List listeners = Lists.newArrayList();
   private double centerX = 0.0;
   private double centerZ = 0.0;
   private double size = 6.0E7;
   private double sizeLerpTarget = this.size;
   private long sizeChangeEnd;
   private long sizeChangeStart;
   private int maxSize = 29999984;
   private double damagePerBlock = 0.2;
   private double safeZone = 5.0;
   private int warningTime = 15;
   private int warningBlocks = 5;

   public boolean contains(BlockPos pos) {
      return (double)(pos.getX() + 1) > this.getMinX()
         && (double)pos.getX() < this.getMaxX()
         && (double)(pos.getZ() + 1) > this.getMinZ()
         && (double)pos.getZ() < this.getMaxZ();
   }

   public boolean contains(ChunkPos pos) {
      return (double)pos.getMaxBlockPosX() > this.getMinX()
         && (double)pos.getMinBlockPosX() < this.getMaxX()
         && (double)pos.getMaxBlockPosZ() > this.getMinZ()
         && (double)pos.getMinBlockPosZ() < this.getMaxZ();
   }

   public boolean contains(Box box) {
      return box.maxX > this.getMinX() && box.minX < this.getMaxX() && box.maxZ > this.getMinZ() && box.minZ < this.getMaxZ();
   }

   public double getDistanceFrom(Entity entity) {
      return this.getDistanceFrom(entity.x, entity.z);
   }

   public double getDistanceFrom(double x, double z) {
      double var5 = z - this.getMinZ();
      double var7 = this.getMaxZ() - z;
      double var9 = x - this.getMinX();
      double var11 = this.getMaxX() - x;
      double var13 = Math.min(var9, var11);
      var13 = Math.min(var13, var5);
      return Math.min(var13, var7);
   }

   public BorderStatus getStatus() {
      if (this.sizeLerpTarget < this.size) {
         return BorderStatus.SHRINKING;
      } else {
         return this.sizeLerpTarget > this.size ? BorderStatus.GROWING : BorderStatus.STATIONARY;
      }
   }

   public double getMinX() {
      double var1 = this.getCenterX() - this.getLerpSize() / 2.0;
      if (var1 < (double)(-this.maxSize)) {
         var1 = (double)(-this.maxSize);
      }

      return var1;
   }

   public double getMinZ() {
      double var1 = this.getCenterZ() - this.getLerpSize() / 2.0;
      if (var1 < (double)(-this.maxSize)) {
         var1 = (double)(-this.maxSize);
      }

      return var1;
   }

   public double getMaxX() {
      double var1 = this.getCenterX() + this.getLerpSize() / 2.0;
      if (var1 > (double)this.maxSize) {
         var1 = (double)this.maxSize;
      }

      return var1;
   }

   public double getMaxZ() {
      double var1 = this.getCenterZ() + this.getLerpSize() / 2.0;
      if (var1 > (double)this.maxSize) {
         var1 = (double)this.maxSize;
      }

      return var1;
   }

   public double getCenterX() {
      return this.centerX;
   }

   public double getCenterZ() {
      return this.centerZ;
   }

   public void setCenter(double x, double z) {
      this.centerX = x;
      this.centerZ = z;

      for(WorldBorderListener var6 : this.getListeners()) {
         var6.onCenterChanged(this, x, z);
      }
   }

   public double getLerpSize() {
      if (this.getStatus() != BorderStatus.STATIONARY) {
         double var1 = (double)((float)(System.currentTimeMillis() - this.sizeChangeStart) / (float)(this.sizeChangeEnd - this.sizeChangeStart));
         if (!(var1 >= 1.0)) {
            return this.size + (this.sizeLerpTarget - this.size) * var1;
         }

         this.setSize(this.sizeLerpTarget);
      }

      return this.size;
   }

   public int getLerpTime() {
      return this.getStatus() != BorderStatus.STATIONARY ? (int)(this.sizeChangeEnd - System.currentTimeMillis()) : 0;
   }

   public double getSizeLerpTarget() {
      return this.sizeLerpTarget;
   }

   public void setSize(double size) {
      this.size = size;
      this.sizeLerpTarget = size;
      this.sizeChangeEnd = System.currentTimeMillis();
      this.sizeChangeStart = this.sizeChangeEnd;

      for(WorldBorderListener var4 : this.getListeners()) {
         var4.onSizeChanged(this, size);
      }
   }

   public void setSize(double d, double e, int i) {
      this.size = d;
      this.sizeLerpTarget = e;
      this.sizeChangeStart = System.currentTimeMillis();
      this.sizeChangeEnd = this.sizeChangeStart + (long)i;

      for(WorldBorderListener var7 : this.getListeners()) {
         var7.onSizeChanged(this, d, e, i);
      }
   }

   protected List getListeners() {
      return Lists.newArrayList(this.listeners);
   }

   public void addListener(WorldBorderListener listener) {
      this.listeners.add(listener);
   }

   public void setMaxSize(int size) {
      this.maxSize = size;
   }

   public int getMaxSize() {
      return this.maxSize;
   }

   public double getSafeZone() {
      return this.safeZone;
   }

   public void setSafeZone(double zone) {
      this.safeZone = zone;

      for(WorldBorderListener var4 : this.getListeners()) {
         var4.onSafeZoneChanged(this, zone);
      }
   }

   public double getDamagePerBlock() {
      return this.damagePerBlock;
   }

   public void setDamagePerBlock(double damage) {
      this.damagePerBlock = damage;

      for(WorldBorderListener var4 : this.getListeners()) {
         var4.onDamagePerBlockChanged(this, damage);
      }
   }

   @Environment(EnvType.CLIENT)
   public double getSizeChangeSpeed() {
      return this.sizeChangeEnd == this.sizeChangeStart ? 0.0 : Math.abs(this.size - this.sizeLerpTarget) / (double)(this.sizeChangeEnd - this.sizeChangeStart);
   }

   public int getWarningTime() {
      return this.warningTime;
   }

   public void setWarningTime(int time) {
      this.warningTime = time;

      for(WorldBorderListener var3 : this.getListeners()) {
         var3.onWarningTimeChanged(this, time);
      }
   }

   public int getWarningBlocks() {
      return this.warningBlocks;
   }

   public void setWarningBlocks(int blocks) {
      this.warningBlocks = blocks;

      for(WorldBorderListener var3 : this.getListeners()) {
         var3.onWarningBlocksChanged(this, blocks);
      }
   }
}
