package net.minecraft;

import com.mojang.blaze3d.platform.TextureUtil;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.resource.manager.IResourceManager;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.item.DyeColor;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class C_46wfxponx extends AbstractTexture {
   private static final Logger f_10dalibhm = LogManager.getLogger();
   private final Identifier f_75kuqygde;
   private final List f_32upkktbw;
   private final List f_11woeubar;

   public C_46wfxponx(Identifier c_07ipdbewr, List list, List list2) {
      this.f_75kuqygde = c_07ipdbewr;
      this.f_32upkktbw = list;
      this.f_11woeubar = list2;
   }

   @Override
   public void load(IResourceManager resourceManager) {
      this.clearGlId();

      BufferedImage var2;
      try {
         BufferedImage var3 = TextureUtil.readImage(resourceManager.getResource(this.f_75kuqygde).asStream());
         var2 = new BufferedImage(var3.getWidth(), var3.getHeight(), var3.getType());
         Graphics var4 = var2.getGraphics();
         var4.drawImage(var3, 0, 0, null);

         for(int var5 = 0; var5 < this.f_32upkktbw.size() && var5 < this.f_11woeubar.size(); ++var5) {
            String var6 = (String)this.f_32upkktbw.get(var5);
            MaterialColor var7 = ((DyeColor)this.f_11woeubar.get(var5)).getMaterialColor();
            if (var6 != null) {
               InputStream var8 = resourceManager.getResource(new Identifier(var6)).asStream();
               BufferedImage var9 = TextureUtil.readImage(var8);
               if (var9.getWidth() == var2.getWidth() && var9.getHeight() == var2.getHeight() && var9.getType() == 6) {
                  for(int var10 = 0; var10 < var9.getHeight(); ++var10) {
                     for(int var11 = 0; var11 < var9.getWidth(); ++var11) {
                        int var12 = var9.getRGB(var11, var10);
                        if ((var12 & 0xFF000000) != 0) {
                           int var13 = (var12 & 0xFF0000) << 8 & 0xFF000000;
                           int var14 = var3.getRGB(var11, var10);
                           int var15 = MathHelper.mulARGB(var14, var7.color) & 16777215;
                           var9.setRGB(var11, var10, var13 | var15);
                        }
                     }
                  }

                  var2.getGraphics().drawImage(var9, 0, 0, null);
               }
            }
         }
      } catch (IOException var16) {
         f_10dalibhm.error("Couldn't load layered image", var16);
         return;
      }

      TextureUtil.uploadTexture(this.getGlId(), var2);
   }
}
