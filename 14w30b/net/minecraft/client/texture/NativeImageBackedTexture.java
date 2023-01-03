package net.minecraft.client.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import java.awt.image.BufferedImage;
import net.minecraft.client.resource.manager.IResourceManager;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class NativeImageBackedTexture extends AbstractTexture {
   private final int[] rgbArray;
   private final int width;
   private final int height;

   public NativeImageBackedTexture(BufferedImage image) {
      this(image.getWidth(), image.getHeight());
      image.getRGB(0, 0, image.getWidth(), image.getHeight(), this.rgbArray, 0, image.getWidth());
      this.upload();
   }

   public NativeImageBackedTexture(int width, int height) {
      this.width = width;
      this.height = height;
      this.rgbArray = new int[width * height];
      TextureUtil.prepareImage(this.getGlId(), width, height);
   }

   @Override
   public void load(IResourceManager resourceManager) {
   }

   public void upload() {
      TextureUtil.uploadTexture(this.getGlId(), this.rgbArray, this.width, this.height);
   }

   public int[] getRgbArray() {
      return this.rgbArray;
   }
}
