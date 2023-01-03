package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class CompassSprite extends TextureAtlasSprite {
   public double f_19uughxjv;
   public double f_29hcqvgoc;
   public static String f_11nxdzvjg;

   public CompassSprite(String string) {
      super(string);
      f_11nxdzvjg = string;
   }

   @Override
   public void update() {
      MinecraftClient var1 = MinecraftClient.getInstance();
      if (var1.world != null && var1.player != null) {
         this.m_12wsbzays(var1.world, var1.player.x, var1.player.z, (double)var1.player.yaw, false, false);
      } else {
         this.m_12wsbzays(null, 0.0, 0.0, 0.0, true, false);
      }
   }

   public void m_12wsbzays(World c_54ruxjwzt, double d, double e, double f, boolean bl, boolean bl2) {
      if (!this.frames.isEmpty()) {
         double var10 = 0.0;
         if (c_54ruxjwzt != null && !bl) {
            BlockPos var12 = c_54ruxjwzt.getSpawnPoint();
            double var13 = (double)var12.getX() - d;
            double var15 = (double)var12.getZ() - e;
            f %= 360.0;
            var10 = -((f - 90.0) * Math.PI / 180.0 - Math.atan2(var15, var13));
            if (!c_54ruxjwzt.dimension.isOverworld()) {
               var10 = Math.random() * (float) Math.PI * 2.0;
            }
         }

         if (bl2) {
            this.f_19uughxjv = var10;
         } else {
            double var18 = var10 - this.f_19uughxjv;

            while(var18 < -Math.PI) {
               var18 += Math.PI * 2;
            }

            while(var18 >= Math.PI) {
               var18 -= Math.PI * 2;
            }

            var18 = MathHelper.clamp(var18, -1.0, 1.0);
            this.f_29hcqvgoc += var18 * 0.1;
            this.f_29hcqvgoc *= 0.8;
            this.f_19uughxjv += this.f_29hcqvgoc;
         }

         int var20 = (int)((this.f_19uughxjv / (Math.PI * 2) + 1.0) * (double)this.frames.size()) % this.frames.size();

         while(var20 < 0) {
            var20 = (var20 + this.frames.size()) % this.frames.size();
         }

         if (var20 != this.frameIndex) {
            this.frameIndex = var20;
            TextureUtil.upload((int[][])this.frames.get(this.frameIndex), this.width, this.height, this.x, this.y, false, false);
         }
      }
   }
}
