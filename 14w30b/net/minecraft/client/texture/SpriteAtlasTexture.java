package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.TextureUtil;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.TextureStitchException;
import net.minecraft.client.render.TextureStitcher;
import net.minecraft.client.resource.IResource;
import net.minecraft.client.resource.manager.IResourceManager;
import net.minecraft.client.resource.metadata.AnimationMetadata;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.resource.Identifier;
import net.minecraft.util.crash.CashReportCategory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class SpriteAtlasTexture extends AbstractTexture implements TickableTexture {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final Identifier f_42ttyyeqx = new Identifier("missingno");
   public static final Identifier BLOCK_ATLAS_BLOCKS = new Identifier("textures/atlas/blocks.png");
   private final List animatedSprites = Lists.newArrayList();
   private final Map spritesToLoad = Maps.newHashMap();
   private final Map loadedSprites = Maps.newHashMap();
   private final String name;
   private final LoadTextureCallback f_56zvexwtc;
   private int maxTextureSize;
   private final TextureAtlasSprite texture = new TextureAtlasSprite("missingno");

   public SpriteAtlasTexture(String string) {
      this(string, null);
   }

   public SpriteAtlasTexture(String id, LoadTextureCallback name) {
      this.name = id;
      this.f_56zvexwtc = name;
   }

   private void init() {
      int[] var1 = TextureUtil.MISSING_DATA;
      this.texture.setWidth(16);
      this.texture.setHeight(16);
      int[][] var2 = new int[this.maxTextureSize + 1][];
      var2[0] = var1;
      this.texture.setFrames(Lists.newArrayList(new int[][][]{var2}));
   }

   @Override
   public void load(IResourceManager resourceManager) {
      if (this.f_56zvexwtc != null) {
         this.m_16toxtqos(resourceManager, this.f_56zvexwtc);
      }
   }

   public void m_16toxtqos(IResourceManager c_09fopjucn, LoadTextureCallback c_00rmakpgg) {
      this.spritesToLoad.clear();
      c_00rmakpgg.onTextureLoaded(this);
      this.init();
      this.clearGlId();
      this.processTexture(c_09fopjucn);
   }

   public void processTexture(IResourceManager resourceManager) {
      int var2 = MinecraftClient.getMaxTextureSize();
      TextureStitcher var3 = new TextureStitcher(var2, var2, true, 0, this.maxTextureSize);
      this.loadedSprites.clear();
      this.animatedSprites.clear();
      int var4 = Integer.MAX_VALUE;

      for(Entry var6 : this.spritesToLoad.entrySet()) {
         TextureAtlasSprite var7 = (TextureAtlasSprite)var6.getValue();
         Identifier var8 = new Identifier(var7.getName());
         Identifier var9 = this.getNewIdentifier(var8, 0);

         try {
            IResource var10 = resourceManager.getResource(var9);
            BufferedImage[] var11 = new BufferedImage[1 + this.maxTextureSize];
            var11[0] = TextureUtil.readImage(var10.asStream());
            TextureResourceMetadata var12 = (TextureResourceMetadata)var10.getMetadata("texture");
            if (var12 != null) {
               List var13 = var12.getMipmaps();
               if (!var13.isEmpty()) {
                  int var14 = var11[0].getWidth();
                  int var15 = var11[0].getHeight();
                  if (MathHelper.smallestEncompassingPowerOfTwo(var14) != var14 || MathHelper.smallestEncompassingPowerOfTwo(var15) != var15) {
                     throw new RuntimeException("Unable to load extra miplevels, source-texture is not power of two");
                  }
               }

               for(int var39 : var13) {
                  if (var39 > 0 && var39 < var11.length - 1 && var11[var39] == null) {
                     Identifier var16 = this.getNewIdentifier(var8, var39);

                     try {
                        var11[var39] = TextureUtil.readImage(resourceManager.getResource(var16).asStream());
                     } catch (IOException var21) {
                        LOGGER.error("Unable to load miplevel {} from: {}", new Object[]{var39, var16, var21});
                     }
                  }
               }
            }

            AnimationMetadata var37 = (AnimationMetadata)var10.getMetadata("animation");
            var7.loadTextues(var11, var37);
         } catch (RuntimeException var22) {
            LOGGER.error("Unable to parse metadata from " + var9, var22);
            continue;
         } catch (IOException var23) {
            LOGGER.error("Using missing texture, unable to load " + var9, var23);
            continue;
         }

         var4 = Math.min(var4, Math.min(var7.getWidth(), var7.getHeight()));
         var3.makeNewSpriteAtlas(var7);
      }

      int var24 = MathHelper.log2(var4);
      if (var24 < this.maxTextureSize) {
         LOGGER.debug("{}: dropping miplevel from {} to {}, because of minTexel: {}", new Object[]{this.name, this.maxTextureSize, var24, var4});
         this.maxTextureSize = var24;
      }

      for(final TextureAtlasSprite var27 : this.spritesToLoad.values()) {
         try {
            var27.applyMipmaps(this.maxTextureSize);
         } catch (Throwable var20) {
            CrashReport var32 = CrashReport.of(var20, "Applying mipmap");
            CashReportCategory var34 = var32.addCategory("Sprite being mipmapped");
            var34.add("Sprite name", new Callable() {
               public String call() {
                  return var27.getName();
               }
            });
            var34.add("Sprite size", new Callable() {
               public String call() {
                  return var27.getWidth() + " x " + var27.getHeight();
               }
            });
            var34.add("Sprite frames", new Callable() {
               public String call() {
                  return var27.getFrameSize() + " frames";
               }
            });
            var34.add("Mipmap levels", this.maxTextureSize);
            throw new CrashException(var32);
         }
      }

      this.texture.applyMipmaps(this.maxTextureSize);
      var3.makeNewSpriteAtlas(this.texture);

      try {
         var3.createTextureAtlas();
      } catch (TextureStitchException var19) {
         throw var19;
      }

      LOGGER.info("Created: {}x{} {}-atlas", new Object[]{var3.getTextureAtlasSizeX(), var3.getTextureAtlasSizeY(), this.name});
      TextureUtil.prepareImage(this.getGlId(), this.maxTextureSize, var3.getTextureAtlasSizeX(), var3.getTextureAtlasSizeY());
      HashMap var26 = Maps.newHashMap(this.spritesToLoad);

      for(TextureAtlasSprite var30 : var3.addTextures()) {
         String var33 = var30.getName();
         var26.remove(var33);
         this.loadedSprites.put(var33, var30);

         try {
            TextureUtil.upload(var30.getFrame(0), var30.getWidth(), var30.getHeight(), var30.getX(), var30.getY(), false, false);
         } catch (Throwable var18) {
            CrashReport var35 = CrashReport.of(var18, "Stitching texture atlas");
            CashReportCategory var36 = var35.addCategory("Texture being stitched together");
            var36.add("Atlas path", this.name);
            var36.add("Sprite", var30);
            throw new CrashException(var35);
         }

         if (var30.hasMeta()) {
            this.animatedSprites.add(var30);
         }
      }

      for(TextureAtlasSprite var31 : var26.values()) {
         var31.copyData(this.texture);
      }

      TextureUtil.writeAsPNG(this.name.replaceAll("/", "_"), this.getGlId(), this.maxTextureSize, var3.getTextureAtlasSizeX(), var3.getTextureAtlasSizeY());
   }

   private Identifier getNewIdentifier(Identifier identifier, int id) {
      return id == 0
         ? new Identifier(identifier.getNamespace(), String.format("%s/%s%s", this.name, identifier.getPath(), ".png"))
         : new Identifier(identifier.getNamespace(), String.format("%s/mipmaps/%s.%d%s", this.name, identifier.getPath(), id, ".png"));
   }

   public TextureAtlasSprite getSprite(String identifier) {
      TextureAtlasSprite var2 = (TextureAtlasSprite)this.loadedSprites.get(identifier);
      if (var2 == null) {
         var2 = this.texture;
      }

      return var2;
   }

   public void update() {
      TextureUtil.bind(this.getGlId());

      for(TextureAtlasSprite var2 : this.animatedSprites) {
         var2.update();
      }
   }

   public TextureAtlasSprite m_91bydfggl(Identifier c_07ipdbewr) {
      if (c_07ipdbewr == null) {
         throw new IllegalArgumentException("Location cannot be null!");
      } else {
         TextureAtlasSprite var2 = (TextureAtlasSprite)this.spritesToLoad.get(c_07ipdbewr);
         if (var2 == null) {
            var2 = TextureAtlasSprite.m_53dhrcjlm(c_07ipdbewr);
            this.spritesToLoad.put(c_07ipdbewr.toString(), var2);
         }

         return var2;
      }
   }

   @Override
   public void tick() {
      this.update();
   }

   public void setMaxTextureSize(int size) {
      this.maxTextureSize = size;
   }

   public TextureAtlasSprite m_92lyecmxz() {
      return this.texture;
   }
}
