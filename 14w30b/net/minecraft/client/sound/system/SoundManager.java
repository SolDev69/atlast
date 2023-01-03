package net.minecraft.client.sound.system;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.IResource;
import net.minecraft.client.resource.manager.IResourceManager;
import net.minecraft.client.resource.manager.ResourceReloadListener;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.client.sound.SoundContainer;
import net.minecraft.client.sound.SoundList;
import net.minecraft.client.sound.SoundListDeserializer;
import net.minecraft.client.sound.SoundPool;
import net.minecraft.client.sound.event.ISoundEvent;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.resource.Identifier;
import net.minecraft.util.Tickable;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class SoundManager implements ResourceReloadListener, Tickable {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = new GsonBuilder().registerTypeAdapter(SoundList.class, new SoundListDeserializer()).create();
   private static final ParameterizedType TYPE = new ParameterizedType() {
      @Override
      public Type[] getActualTypeArguments() {
         return new Type[]{String.class, SoundList.class};
      }

      @Override
      public Type getRawType() {
         return Map.class;
      }

      @Override
      public Type getOwnerType() {
         return null;
      }
   };
   public static final Sound MISSING_SOUND = new Sound(new Identifier("meta:missing_sound"), 0.0, 0.0, false);
   private final SoundRegistry registry = new SoundRegistry();
   private final SoundSystem soundSystem;
   private final IResourceManager resourceManager;

   public SoundManager(IResourceManager resourceManager, GameOptions options) {
      this.resourceManager = resourceManager;
      this.soundSystem = new SoundSystem(this, options);
   }

   @Override
   public void reload(IResourceManager resourceManager) {
      this.soundSystem.reload();
      this.registry.clear();

      for(String var3 : resourceManager.getNamespaces()) {
         try {
            for(IResource var6 : resourceManager.getResources(new Identifier(var3, "sounds.json"))) {
               try {
                  Map var7 = this.loadContainers(var6.asStream());

                  for(Entry var9 : var7.entrySet()) {
                     this.register(new Identifier(var3, (String)var9.getKey()), (SoundList)var9.getValue());
                  }
               } catch (RuntimeException var10) {
                  LOGGER.warn("Invalid sounds.json", var10);
               }
            }
         } catch (IOException var11) {
         }
      }
   }

   protected Map loadContainers(InputStream is) {
      Map var2;
      try {
         var2 = (Map)GSON.fromJson(new InputStreamReader(is), TYPE);
      } finally {
         IOUtils.closeQuietly(is);
      }

      return var2;
   }

   private void register(Identifier id, SoundList list) {
      boolean var4 = !this.registry.containsKey(id);
      SoundPool var3;
      if (!var4 && !list.isReplacable()) {
         var3 = (SoundPool)this.registry.get(id);
      } else {
         if (!var4) {
            LOGGER.debug("Replaced sound event location {}", new Object[]{id});
         }

         var3 = new SoundPool(id, 1.0, 1.0, list.getCategory());
         this.registry.add(var3);
      }

      for(final SoundList.Entry var6 : list.getSounds()) {
         String var7 = var6.getName();
         Identifier var8 = new Identifier(var7);
         final String var9 = var7.contains(":") ? var8.getNamespace() : id.getNamespace();
         Object var10;
         switch(var6.getType()) {
            case FILE:
               Identifier var11 = new Identifier(var9, "sounds/" + var8.getPath() + ".ogg");
               InputStream var12 = null;

               try {
                  var12 = this.resourceManager.getResource(var11).asStream();
               } catch (FileNotFoundException var18) {
                  LOGGER.warn("File {} does not exist, cannot add it to event {}", new Object[]{var11, id});
                  continue;
               } catch (IOException var19) {
                  LOGGER.warn("Could not load sound file " + var11 + ", cannot add it to event " + id, var19);
                  continue;
               } finally {
                  IOUtils.closeQuietly(var12);
               }

               var10 = new FileSoundContainer(new Sound(var11, (double)var6.getPitch(), (double)var6.getVolume(), var6.isStream()), var6.getWeight());
               break;
            case EVENT:
               var10 = new SoundContainer() {
                  final Identifier id = new Identifier(var9, var6.getName());

                  @Override
                  public int getWeight() {
                     SoundPool var1 = (SoundPool)SoundManager.this.registry.get(this.id);
                     return var1 == null ? 0 : var1.getWeight();
                  }

                  public Sound get() {
                     SoundPool var1 = (SoundPool)SoundManager.this.registry.get(this.id);
                     return var1 == null ? SoundManager.MISSING_SOUND : var1.get();
                  }
               };
               break;
            default:
               throw new IllegalStateException("IN YOU FACE");
         }

         var3.add((SoundContainer)var10);
      }
   }

   public SoundPool get(Identifier id) {
      return (SoundPool)this.registry.get(id);
   }

   public void play(ISoundEvent event) {
      this.soundSystem.play(event);
   }

   public void play(ISoundEvent event, int delay) {
      this.soundSystem.play(event, delay);
   }

   public void updateListener(PlayerEntity player, float tickDelta) {
      this.soundSystem.updateListener(player, tickDelta);
   }

   public void pause() {
      this.soundSystem.pause();
   }

   public void stop() {
      this.soundSystem.stop();
   }

   public void close() {
      this.soundSystem.close();
   }

   @Override
   public void tick() {
      this.soundSystem.tick();
   }

   public void resume() {
      this.soundSystem.resume();
   }

   public void setVolume(SoundCategory category, float volume) {
      if (category == SoundCategory.MASTER && volume <= 0.0F) {
         this.stop();
      }

      this.soundSystem.setVolume(category, volume);
   }

   public void stop(ISoundEvent event) {
      this.soundSystem.stop(event);
   }

   public SoundPool getRandom(SoundCategory... categories) {
      ArrayList var2 = Lists.newArrayList();

      for(Identifier var4 : this.registry.keySet()) {
         SoundPool var5 = (SoundPool)this.registry.get(var4);
         if (ArrayUtils.contains(categories, var5.getCategory())) {
            var2.add(var5);
         }
      }

      return var2.isEmpty() ? null : (SoundPool)var2.get(new Random().nextInt(var2.size()));
   }

   public boolean isPlaying(ISoundEvent instance) {
      return this.soundSystem.isPlaying(instance);
   }
}
