package net.minecraft.client.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import net.minecraft.client.resource.manager.IResourceManager;
import net.minecraft.client.resource.manager.ResourceReloadListener;
import net.minecraft.resource.Identifier;
import net.minecraft.util.crash.CashReportCategory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class TextureManager implements Tickable, ResourceReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map textures = Maps.newHashMap();
   private final List tickableTextures = Lists.newArrayList();
   private final Map dynamicIdCounter = Maps.newHashMap();
   private IResourceManager resourceManager;

   public TextureManager(IResourceManager resourceManager) {
      this.resourceManager = resourceManager;
   }

   public void bind(Identifier id) {
      Object var2 = (Texture)this.textures.get(id);
      if (var2 == null) {
         var2 = new ResourceTexture(id);
         this.register(id, (Texture)var2);
      }

      TextureUtil.bind(((Texture)var2).getGlId());
   }

   public boolean register(Identifier id, TickableTexture texture) {
      if (this.register(id, (Texture)texture)) {
         this.tickableTextures.add(texture);
         return true;
      } else {
         return false;
      }
   }

   public boolean register(Identifier id, Texture texture) {
      boolean var3 = true;

      try {
         texture.load(this.resourceManager);
      } catch (IOException var8) {
         LOGGER.warn("Failed to load texture: " + id, var8);
         texture = TextureUtil.MISSING_TEXTURE;
         this.textures.put(id, texture);
         var3 = false;
      } catch (Throwable var9) {
         CrashReport var5 = CrashReport.of(var9, "Registering texture");
         CashReportCategory var6 = var5.addCategory("Resource location being registered");
         var6.add("Resource location", id);
         var6.add("Texture object class", new Callable() {
            public String call() {
               return texture.getClass().getName();
            }
         });
         throw new CrashException(var5);
      }

      this.textures.put(id, texture);
      return var3;
   }

   public Texture getTexture(Identifier id) {
      return (Texture)this.textures.get(id);
   }

   public Identifier register(String name, NativeImageBackedTexture texture) {
      Integer var3 = (Integer)this.dynamicIdCounter.get(name);
      if (var3 == null) {
         var3 = 1;
      } else {
         var3 = var3 + 1;
      }

      this.dynamicIdCounter.put(name, var3);
      Identifier var4 = new Identifier(String.format("dynamic/%s_%d", name, var3));
      this.register(var4, texture);
      return var4;
   }

   @Override
   public void tick() {
      for(Tickable var2 : this.tickableTextures) {
         var2.tick();
      }
   }

   public void close(Identifier id) {
      Texture var2 = this.getTexture(id);
      if (var2 != null) {
         TextureUtil.deleteTexture(var2.getGlId());
      }
   }

   @Override
   public void reload(IResourceManager resourceManager) {
      for(Entry var3 : this.textures.entrySet()) {
         this.register((Identifier)var3.getKey(), (Texture)var3.getValue());
      }
   }
}
