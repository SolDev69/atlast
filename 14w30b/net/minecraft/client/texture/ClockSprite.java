package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClockSprite extends TextureAtlasSprite {
   private double f_50ugrfbis;
   private double f_24imefcxw;

   public ClockSprite(String string) {
      super(string);
   }

   @Override
   public void update() {
      if (!this.frames.isEmpty()) {
         MinecraftClient var1 = MinecraftClient.getInstance();
         double var2 = 0.0;
         if (var1.world != null && var1.player != null) {
            float var4 = var1.world.getTimeOfDay(1.0F);
            var2 = (double)var4;
            if (!var1.world.dimension.isOverworld()) {
               var2 = Math.random();
            }
         }

         double var7 = var2 - this.f_50ugrfbis;

         while(var7 < -0.5) {
            ++var7;
         }

         while(var7 >= 0.5) {
            --var7;
         }

         var7 = MathHelper.clamp(var7, -1.0, 1.0);
         this.f_24imefcxw += var7 * 0.1;
         this.f_24imefcxw *= 0.8;
         this.f_50ugrfbis += this.f_24imefcxw;
         int var6 = (int)((this.f_50ugrfbis + 1.0) * (double)this.frames.size()) % this.frames.size();

         while(var6 < 0) {
            var6 = (var6 + this.frames.size()) % this.frames.size();
         }

         if (var6 != this.frameIndex) {
            this.frameIndex = var6;
            TextureUtil.upload((int[][])this.frames.get(this.frameIndex), this.width, this.height, this.x, this.y, false, false);
         }
      }
   }
}
