package net.minecraft.client.resource.skin;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferedImageSkinProvider;
import net.minecraft.client.render.DownloadedSkinParser;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.texture.Texture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.Identifier;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class SkinManager {
   public static final Identifier f_08phnpske = new Identifier("textures/entity/steve.png");
   private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(0, 2, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue<>());
   private final TextureManager textureManager;
   private final File skinsDir;
   private final MinecraftSessionService sessionService;
   private final LoadingCache skinCache;

   public SkinManager(TextureManager textureManager, File skinsDir, MinecraftSessionService sessionService) {
      this.textureManager = textureManager;
      this.skinsDir = skinsDir;
      this.sessionService = sessionService;
      this.skinCache = CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.SECONDS).build(new CacheLoader() {
         public Map load(GameProfile gameProfile) {
            return MinecraftClient.getInstance().createAuthenticationService().getTextures(gameProfile, false);
         }
      });
   }

   public Identifier register(MinecraftProfileTexture texture, Type type) {
      return this.register(texture, type, null);
   }

   public Identifier register(MinecraftProfileTexture texture, Type type, SkinManager.SkinTextureCallback callback) {
      final Identifier var4 = new Identifier("skins/" + texture.getHash());
      Texture var5 = this.textureManager.getTexture(var4);
      if (var5 != null) {
         if (callback != null) {
            callback.textureAvailable(type, var4);
         }
      } else {
         File var6 = new File(this.skinsDir, texture.getHash().substring(0, 2));
         File var7 = new File(var6, texture.getHash());
         final DownloadedSkinParser var8 = type == Type.SKIN ? new DownloadedSkinParser() : null;
         PlayerSkinTexture var9 = new PlayerSkinTexture(var7, texture.getUrl(), f_08phnpske, new BufferedImageSkinProvider() {
            @Override
            public BufferedImage process(BufferedImage image) {
               if (var8 != null) {
                  image = var8.process(image);
               }

               return image;
            }

            @Override
            public void onTextureDownloaded() {
               if (var8 != null) {
                  var8.onTextureDownloaded();
               }

               if (callback != null) {
                  callback.textureAvailable(type, var4);
               }
            }
         });
         this.textureManager.register(var4, var9);
      }

      return var4;
   }

   public void register(GameProfile profile, SkinManager.SkinTextureCallback callback, boolean bl) {
      EXECUTOR.submit(new Runnable() {
         @Override
         public void run() {
            final HashMap var1 = Maps.newHashMap();

            try {
               var1.putAll(SkinManager.this.sessionService.getTextures(profile, bl));
            } catch (InsecureTextureException var3) {
            }

            if (var1.isEmpty() && profile.getId().equals(MinecraftClient.getInstance().getSession().getProfile().getId())) {
               var1.putAll(SkinManager.this.sessionService.getTextures(SkinManager.this.sessionService.fillProfileProperties(profile, false), false));
            }

            MinecraftClient.getInstance().submit(new Runnable() {
               @Override
               public void run() {
                  if (var1.containsKey(Type.SKIN)) {
                     SkinManager.this.register((MinecraftProfileTexture)var1.get(Type.SKIN), Type.SKIN, callback);
                  }

                  if (var1.containsKey(Type.CAPE)) {
                     SkinManager.this.register((MinecraftProfileTexture)var1.get(Type.CAPE), Type.CAPE, callback);
                  }
               }
            });
         }
      });
   }

   public Map getTextures(GameProfile profile) {
      return (Map)this.skinCache.getUnchecked(profile);
   }

   @Environment(EnvType.CLIENT)
   public interface SkinTextureCallback {
      void textureAvailable(Type type, Identifier c_07ipdbewr);
   }
}
