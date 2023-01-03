package net.minecraft.client.render;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class DownloadedSkinParser implements BufferedImageSkinProvider {
   private int[] data;
   private int width;
   private int height;

   @Override
   public BufferedImage process(BufferedImage image) {
      if (image == null) {
         return null;
      } else {
         this.width = 64;
         this.height = 64;
         BufferedImage var2 = new BufferedImage(this.width, this.height, 2);
         Graphics var3 = var2.getGraphics();
         var3.drawImage(image, 0, 0, null);
         if (image.getHeight() == 32) {
            var3.drawImage(var2, 24, 48, 20, 52, 4, 16, 8, 20, null);
            var3.drawImage(var2, 28, 48, 24, 52, 8, 16, 12, 20, null);
            var3.drawImage(var2, 20, 52, 16, 64, 8, 20, 12, 32, null);
            var3.drawImage(var2, 24, 52, 20, 64, 4, 20, 8, 32, null);
            var3.drawImage(var2, 28, 52, 24, 64, 0, 20, 4, 32, null);
            var3.drawImage(var2, 32, 52, 28, 64, 12, 20, 16, 32, null);
            var3.drawImage(var2, 40, 48, 36, 52, 44, 16, 48, 20, null);
            var3.drawImage(var2, 44, 48, 40, 52, 48, 16, 52, 20, null);
            var3.drawImage(var2, 36, 52, 32, 64, 48, 20, 52, 32, null);
            var3.drawImage(var2, 40, 52, 36, 64, 44, 20, 48, 32, null);
            var3.drawImage(var2, 44, 52, 40, 64, 40, 20, 44, 32, null);
            var3.drawImage(var2, 48, 52, 44, 64, 52, 20, 56, 32, null);
         }

         var3.dispose();
         this.data = ((DataBufferInt)var2.getRaster().getDataBuffer()).getData();
         this.setOpaque(0, 0, 32, 16);
         this.setTransperant(32, 0, 64, 32);
         this.setOpaque(0, 16, 64, 32);
         this.setTransperant(0, 32, 16, 48);
         this.setTransperant(16, 32, 40, 48);
         this.setTransperant(40, 32, 56, 48);
         this.setTransperant(0, 48, 16, 64);
         this.setOpaque(16, 48, 48, 64);
         this.setTransperant(48, 48, 64, 64);
         return var2;
      }
   }

   @Override
   public void onTextureDownloaded() {
   }

   private void setTransperant(int uMin, int vMin, int uMax, int vMax) {
      if (!this.hasTransperancy(uMin, vMin, uMax, vMax)) {
         for(int var5 = uMin; var5 < uMax; ++var5) {
            for(int var6 = vMin; var6 < vMax; ++var6) {
               this.data[var5 + var6 * this.width] &= 16777215;
            }
         }
      }
   }

   private void setOpaque(int uMin, int vMin, int uMax, int vMax) {
      for(int var5 = uMin; var5 < uMax; ++var5) {
         for(int var6 = vMin; var6 < vMax; ++var6) {
            this.data[var5 + var6 * this.width] |= -16777216;
         }
      }
   }

   private boolean hasTransperancy(int uMin, int vMin, int uMax, int vMax) {
      for(int var5 = uMin; var5 < uMax; ++var5) {
         for(int var6 = vMin; var6 < vMax; ++var6) {
            int var7 = this.data[var5 + var6 * this.width];
            if ((var7 >> 24 & 0xFF) < 128) {
               return true;
            }
         }
      }

      return false;
   }
}
