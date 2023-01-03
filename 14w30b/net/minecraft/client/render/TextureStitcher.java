package net.minecraft.client.render;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import net.minecraft.client.texture.TextureAtlasSprite;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class TextureStitcher {
   private final int maxSpriteAtlasSize;
   private final Set spriteAtlases = Sets.newHashSetWithExpectedSize(256);
   private final List holderList = Lists.newArrayListWithCapacity(256);
   private int sizeX;
   private int sizeY;
   private final int maxSizeX;
   private final int maxSizeY;
   private final boolean doExtraSizeCalulations;
   private final int holderSize;

   public TextureStitcher(int maxSizeX, int maxSizeY, boolean doExtraSizeCalculations, int holderSize, int maxSpriteAtlasSize) {
      this.maxSpriteAtlasSize = maxSpriteAtlasSize;
      this.maxSizeX = maxSizeX;
      this.maxSizeY = maxSizeY;
      this.doExtraSizeCalulations = doExtraSizeCalculations;
      this.holderSize = holderSize;
   }

   public int getTextureAtlasSizeX() {
      return this.sizeX;
   }

   public int getTextureAtlasSizeY() {
      return this.sizeY;
   }

   public void makeNewSpriteAtlas(TextureAtlasSprite sprite) {
      TextureStitcher.Holder var2 = new TextureStitcher.Holder(sprite, this.maxSpriteAtlasSize);
      if (this.holderSize > 0) {
         var2.setSize(this.holderSize);
      }

      this.spriteAtlases.add(var2);
   }

   public void createTextureAtlas() {
      TextureStitcher.Holder[] var1 = this.spriteAtlases.toArray(new TextureStitcher.Holder[this.spriteAtlases.size()]);
      Arrays.sort((Object[])var1);

      for(TextureStitcher.Holder var5 : var1) {
         if (!this.addHoldersToHolderListAndStitch(var5)) {
            String var6 = String.format(
               "Unable to fit: %s - size: %dx%d - Maybe try a lowerresolution resourcepack?",
               var5.getSprite().getName(),
               var5.getSprite().getWidth(),
               var5.getSprite().getHeight()
            );
            throw new TextureStitchException(var5, var6);
         }
      }

      if (this.doExtraSizeCalulations) {
         this.sizeX = MathHelper.smallestEncompassingPowerOfTwo(this.sizeX);
         this.sizeY = MathHelper.smallestEncompassingPowerOfTwo(this.sizeY);
      }
   }

   public List addTextures() {
      ArrayList var1 = Lists.newArrayList();

      for(TextureStitcher.Slot var3 : this.holderList) {
         var3.appendTexturesToList(var1);
      }

      ArrayList var7 = Lists.newArrayList();

      for(TextureStitcher.Slot var4 : var1) {
         TextureStitcher.Holder var5 = var4.getTexture();
         TextureAtlasSprite var6 = var5.getSprite();
         var6.reInitialize(this.sizeX, this.sizeY, var4.getOriginX(), var4.getOriginY(), var5.getTallTexture());
         var7.add(var6);
      }

      return var7;
   }

   private static int calculateSizes(int size, int maxSize) {
      return (size >> maxSize) + ((size & (1 << maxSize) - 1) == 0 ? 0 : 1) << maxSize;
   }

   private boolean addHoldersToHolderListAndStitch(TextureStitcher.Holder holder) {
      for(int var2 = 0; var2 < this.holderList.size(); ++var2) {
         if (((TextureStitcher.Slot)this.holderList.get(var2)).add(holder)) {
            return true;
         }

         holder.swapMainTextureSideSize();
         if (((TextureStitcher.Slot)this.holderList.get(var2)).add(holder)) {
            return true;
         }

         holder.swapMainTextureSideSize();
      }

      return this.stitch(holder);
   }

   private boolean stitch(TextureStitcher.Holder holder) {
      int var2 = Math.min(holder.mainTextureSideSizeCalculatons(), holder.secondaryTextureSideSizeCalculatons());
      boolean var3 = this.sizeX == 0 && this.sizeY == 0;
      boolean var4;
      if (this.doExtraSizeCalulations) {
         int var5 = MathHelper.smallestEncompassingPowerOfTwo(this.sizeX);
         int var6 = MathHelper.smallestEncompassingPowerOfTwo(this.sizeY);
         int var7 = MathHelper.smallestEncompassingPowerOfTwo(this.sizeX + var2);
         int var8 = MathHelper.smallestEncompassingPowerOfTwo(this.sizeY + var2);
         boolean var9 = var7 <= this.maxSizeX;
         boolean var10 = var8 <= this.maxSizeY;
         if (!var9 && !var10) {
            return false;
         }

         boolean var11 = var5 != var7;
         boolean var12 = var6 != var8;
         if (var11 ^ var12) {
            var4 = !var11;
         } else {
            var4 = var9 && var5 <= var6;
         }
      } else {
         boolean var13 = this.sizeX + var2 <= this.maxSizeX;
         boolean var15 = this.sizeY + var2 <= this.maxSizeY;
         if (!var13 && !var15) {
            return false;
         }

         var4 = var13 && (var3 || this.sizeX <= this.sizeY);
      }

      int var14 = Math.max(holder.mainTextureSideSizeCalculatons(), holder.secondaryTextureSideSizeCalculatons());
      if (MathHelper.smallestEncompassingPowerOfTwo((var4 ? this.sizeY : this.sizeX) + var14) > (var4 ? this.maxSizeY : this.maxSizeX)) {
         return false;
      } else {
         TextureStitcher.Slot var16;
         if (var4) {
            if (holder.mainTextureSideSizeCalculatons() > holder.secondaryTextureSideSizeCalculatons()) {
               holder.swapMainTextureSideSize();
            }

            if (this.sizeY == 0) {
               this.sizeY = holder.secondaryTextureSideSizeCalculatons();
            }

            var16 = new TextureStitcher.Slot(this.sizeX, 0, holder.mainTextureSideSizeCalculatons(), this.sizeY);
            this.sizeX += holder.mainTextureSideSizeCalculatons();
         } else {
            var16 = new TextureStitcher.Slot(0, this.sizeY, this.sizeX, holder.secondaryTextureSideSizeCalculatons());
            this.sizeY += holder.secondaryTextureSideSizeCalculatons();
         }

         var16.add(holder);
         this.holderList.add(var16);
         return true;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Holder implements Comparable {
      private final TextureAtlasSprite sprite;
      private final int width;
      private final int height;
      private final int maxSize;
      private boolean tallTexture;
      private float size = 1.0F;

      public Holder(TextureAtlasSprite sprite, int maxSize) {
         this.sprite = sprite;
         this.width = sprite.getWidth();
         this.height = sprite.getHeight();
         this.maxSize = maxSize;
         this.tallTexture = TextureStitcher.calculateSizes(this.height, maxSize) > TextureStitcher.calculateSizes(this.width, maxSize);
      }

      public TextureAtlasSprite getSprite() {
         return this.sprite;
      }

      public int mainTextureSideSizeCalculatons() {
         return this.tallTexture
            ? TextureStitcher.calculateSizes((int)((float)this.height * this.size), this.maxSize)
            : TextureStitcher.calculateSizes((int)((float)this.width * this.size), this.maxSize);
      }

      public int secondaryTextureSideSizeCalculatons() {
         return this.tallTexture
            ? TextureStitcher.calculateSizes((int)((float)this.width * this.size), this.maxSize)
            : TextureStitcher.calculateSizes((int)((float)this.height * this.size), this.maxSize);
      }

      public void swapMainTextureSideSize() {
         this.tallTexture = !this.tallTexture;
      }

      public boolean getTallTexture() {
         return this.tallTexture;
      }

      public void setSize(int size) {
         if (this.width > size && this.height > size) {
            this.size = (float)size / (float)Math.min(this.width, this.height);
         }
      }

      @Override
      public String toString() {
         return "Holder{width=" + this.width + ", height=" + this.height + '}';
      }

      public int compareTo(TextureStitcher.Holder c_54mecevqf) {
         int var2;
         if (this.secondaryTextureSideSizeCalculatons() == c_54mecevqf.secondaryTextureSideSizeCalculatons()) {
            if (this.mainTextureSideSizeCalculatons() == c_54mecevqf.mainTextureSideSizeCalculatons()) {
               if (this.sprite.getName() == null) {
                  return c_54mecevqf.sprite.getName() == null ? 0 : -1;
               }

               return this.sprite.getName().compareTo(c_54mecevqf.sprite.getName());
            }

            var2 = this.mainTextureSideSizeCalculatons() < c_54mecevqf.mainTextureSideSizeCalculatons() ? 1 : -1;
         } else {
            var2 = this.secondaryTextureSideSizeCalculatons() < c_54mecevqf.secondaryTextureSideSizeCalculatons() ? 1 : -1;
         }

         return var2;
      }
   }

   @Environment(EnvType.CLIENT)
   public static class Slot {
      private final int originX;
      private final int originY;
      private final int width;
      private final int height;
      private List textures;
      private TextureStitcher.Holder texture;

      public Slot(int originX, int originY, int width, int height) {
         this.originX = originX;
         this.originY = originY;
         this.width = width;
         this.height = height;
      }

      public TextureStitcher.Holder getTexture() {
         return this.texture;
      }

      public int getOriginX() {
         return this.originX;
      }

      public int getOriginY() {
         return this.originY;
      }

      public boolean add(TextureStitcher.Holder texture) {
         if (this.texture != null) {
            return false;
         } else {
            int var2 = texture.mainTextureSideSizeCalculatons();
            int var3 = texture.secondaryTextureSideSizeCalculatons();
            if (var2 <= this.width && var3 <= this.height) {
               if (var2 == this.width && var3 == this.height) {
                  this.texture = texture;
                  return true;
               } else {
                  if (this.textures == null) {
                     this.textures = Lists.newArrayListWithCapacity(1);
                     this.textures.add(new TextureStitcher.Slot(this.originX, this.originY, var2, var3));
                     int var4 = this.width - var2;
                     int var5 = this.height - var3;
                     if (var5 > 0 && var4 > 0) {
                        int var6 = Math.max(this.height, var4);
                        int var7 = Math.max(this.width, var5);
                        if (var6 >= var7) {
                           this.textures.add(new TextureStitcher.Slot(this.originX, this.originY + var3, var2, var5));
                           this.textures.add(new TextureStitcher.Slot(this.originX + var2, this.originY, var4, this.height));
                        } else {
                           this.textures.add(new TextureStitcher.Slot(this.originX + var2, this.originY, var4, var3));
                           this.textures.add(new TextureStitcher.Slot(this.originX, this.originY + var3, this.width, var5));
                        }
                     } else if (var4 == 0) {
                        this.textures.add(new TextureStitcher.Slot(this.originX, this.originY + var3, var2, var5));
                     } else if (var5 == 0) {
                        this.textures.add(new TextureStitcher.Slot(this.originX + var2, this.originY, var4, var3));
                     }
                  }

                  for(TextureStitcher.Slot var9 : this.textures) {
                     if (var9.add(texture)) {
                        return true;
                     }
                  }

                  return false;
               }
            } else {
               return false;
            }
         }
      }

      public void appendTexturesToList(List list) {
         if (this.texture != null) {
            list.add(this);
         } else if (this.textures != null) {
            for(TextureStitcher.Slot var3 : this.textures) {
               var3.appendTexturesToList(list);
            }
         }
      }

      @Override
      public String toString() {
         return "Slot{originX="
            + this.originX
            + ", originY="
            + this.originY
            + ", width="
            + this.width
            + ", height="
            + this.height
            + ", texture="
            + this.texture
            + ", subSlots="
            + this.textures
            + '}';
      }
   }
}
