package net.minecraft.client;

import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class TickTimer {
   float tps;
   private double timeSec;
   public int ticksThisFrame;
   public float partialTick;
   public float tpsScale = 1.0F;
   public float tickDelta;
   private long lastTickTime;
   private long lastCorrectionTime;
   private long cumTickTime;
   private double tickTimeCorrection = 1.0;

   public TickTimer(float tps) {
      this.tps = tps;
      this.lastTickTime = MinecraftClient.getTime();
      this.lastCorrectionTime = System.nanoTime() / 1000000L;
   }

   public void advance() {
      long var1 = MinecraftClient.getTime();
      long var3 = var1 - this.lastTickTime;
      long var5 = System.nanoTime() / 1000000L;
      double var7 = (double)var5 / 1000.0;
      if (var3 <= 1000L && var3 >= 0L) {
         this.cumTickTime += var3;
         if (this.cumTickTime > 1000L) {
            long var9 = var5 - this.lastCorrectionTime;
            double var11 = (double)this.cumTickTime / (double)var9;
            this.tickTimeCorrection += (var11 - this.tickTimeCorrection) * 0.2F;
            this.lastCorrectionTime = var5;
            this.cumTickTime = 0L;
         }

         if (this.cumTickTime < 0L) {
            this.lastCorrectionTime = var5;
         }
      } else {
         this.timeSec = var7;
      }

      this.lastTickTime = var1;
      double var13 = (var7 - this.timeSec) * this.tickTimeCorrection;
      this.timeSec = var7;
      var13 = MathHelper.clamp(var13, 0.0, 1.0);
      this.tickDelta = (float)((double)this.tickDelta + var13 * (double)this.tpsScale * (double)this.tps);
      this.ticksThisFrame = (int)this.tickDelta;
      this.tickDelta -= (float)this.ticksThisFrame;
      if (this.ticksThisFrame > 10) {
         this.ticksThisFrame = 10;
      }

      this.partialTick = this.tickDelta;
   }
}
