package net.minecraft.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class Window {
   private final double scaledWidth;
   private final double scaledHeight;
   private int width;
   private int height;
   private int scale;

   public Window(MinecraftClient client, int width, int height) {
      this.width = width;
      this.height = height;
      this.scale = 1;
      boolean var4 = client.isUnicode();
      int var5 = client.options.guiScale;
      if (var5 == 0) {
         var5 = 1000;
      }

      while(this.scale < var5 && this.width / (this.scale + 1) >= 320 && this.height / (this.scale + 1) >= 240) {
         ++this.scale;
      }

      if (var4 && this.scale % 2 != 0 && this.scale != 1) {
         --this.scale;
      }

      this.scaledWidth = (double)this.width / (double)this.scale;
      this.scaledHeight = (double)this.height / (double)this.scale;
      this.width = MathHelper.ceil(this.scaledWidth);
      this.height = MathHelper.ceil(this.scaledHeight);
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public double getScaledWidth() {
      return this.scaledWidth;
   }

   public double getScaledHeight() {
      return this.scaledHeight;
   }

   public int getScale() {
      return this.scale;
   }
}
