package net.minecraft.client.util;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.Buffer;
import java.nio.IntBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class ScreenshotUtils {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
   private static IntBuffer intBuffer;
   private static int[] buffer;

   public static Text saveScreenshot(File parent, int textureWidth, int textureHeight, RenderTarget buffer) {
      return saveScreenshot(parent, null, textureWidth, textureHeight, buffer);
   }

   public static Text saveScreenshot(File parent, String name, int textureWidth, int textureHeight, RenderTarget buffer) {
      try {
         File var5 = new File(parent, "screenshots");
         var5.mkdir();
         if (GLX.useFbo()) {
            textureWidth = buffer.width;
            textureHeight = buffer.height;
         }

         int var6 = textureWidth * textureHeight;
         if (intBuffer == null || intBuffer.capacity() < var6) {
            intBuffer = BufferUtils.createIntBuffer(var6);
            ScreenshotUtils.buffer = new int[var6];
         }

         GL11.glPixelStorei(3333, 1);
         GL11.glPixelStorei(3317, 1);
         ((Buffer)intBuffer).clear();
         if (GLX.useFbo()) {
            GlStateManager.bindTexture(buffer.colorTextureId);
            GL11.glGetTexImage(3553, 0, 32993, 33639, intBuffer);
         } else {
            GL11.glReadPixels(0, 0, textureWidth, textureHeight, 32993, 33639, intBuffer);
         }

         intBuffer.get(ScreenshotUtils.buffer);
         TextureUtil.copyTextureValues(ScreenshotUtils.buffer, textureWidth, textureHeight);
         BufferedImage var7 = null;
         if (GLX.useFbo()) {
            var7 = new BufferedImage(buffer.viewWidth, buffer.viewHeight, 1);
            int var8 = buffer.height - buffer.viewHeight;

            for(int var9 = var8; var9 < buffer.height; ++var9) {
               for(int var10 = 0; var10 < buffer.viewWidth; ++var10) {
                  var7.setRGB(var10, var9 - var8, ScreenshotUtils.buffer[var9 * buffer.width + var10]);
               }
            }
         } else {
            var7 = new BufferedImage(textureWidth, textureHeight, 1);
            var7.setRGB(0, 0, textureWidth, textureHeight, ScreenshotUtils.buffer, 0, textureWidth);
         }

         File var13;
         if (name == null) {
            var13 = getScreenshotFile(var5);
         } else {
            var13 = new File(var5, name);
         }

         ImageIO.write(var7, "png", var13);
         LiteralText var14 = new LiteralText(var13.getName());
         var14.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, var13.getAbsolutePath()));
         var14.getStyle().setUnderlined(true);
         return new TranslatableText("screenshot.success", var14);
      } catch (Exception var11) {
         LOGGER.warn("Couldn't save screenshot", var11);
         return new TranslatableText("screenshot.failure", var11.getMessage());
      }
   }

   private static File getScreenshotFile(File screenshotsDirectory) {
      String var2 = DATE_FORMAT.format(new Date()).toString();
      int var3 = 1;

      while(true) {
         File var1 = new File(screenshotsDirectory, var2 + (var3 == 1 ? "" : "_" + var3) + ".png");
         if (!var1.exists()) {
            return var1;
         }

         ++var3;
      }
   }
}
