package net.minecraft.client.sound.system;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import io.netty.util.internal.ThreadLocalRandom;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.client.sound.SoundPool;
import net.minecraft.client.sound.event.ISoundEvent;
import net.minecraft.client.sound.event.ITickableSoundEvent;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.MathHelper;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.Source;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

@Environment(EnvType.CLIENT)
public class SoundSystem {
   private static final Marker MARKER = MarkerManager.getMarker("SOUNDS");
   private static final Logger LOGGER = LogManager.getLogger();
   private final SoundManager manager;
   private final GameOptions options;
   private SoundSystem.System soundSystem;
   private boolean started;
   private int ticks = 0;
   private final Map events = HashBiMap.create();
   private final Map channels = ((BiMap)this.events).inverse();
   private Map sources = Maps.newHashMap();
   private final Multimap channelsByCategory = HashMultimap.create();
   private final List tickableEvents = Lists.newArrayList();
   private final Map soundQueue = Maps.newHashMap();
   private final Map soundBuffer = Maps.newHashMap();

   public SoundSystem(SoundManager manager, GameOptions options) {
      this.manager = manager;
      this.options = options;

      try {
         SoundSystemConfig.addLibrary(LibraryLWJGLOpenAL.class);
         SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
      } catch (SoundSystemException var4) {
         LOGGER.error(MARKER, "Error linking with the LibraryJavaSound plug-in", var4);
      }
   }

   public void reload() {
      this.close();
      this.start();
   }

   private synchronized void start() {
      if (!this.started) {
         try {
            new Thread(new Runnable() {
               @Override
               public void run() {
                  SoundSystem.this.soundSystem = SoundSystem.this.new System();
                  SoundSystem.this.started = true;
                  SoundSystem.this.soundSystem.setMasterVolume(SoundSystem.this.options.getSoundCategoryVolume(SoundCategory.MASTER));
                  SoundSystem.LOGGER.info(SoundSystem.MARKER, "Sound engine started");
               }
            }, "Sound Library Loader").start();
         } catch (RuntimeException var2) {
            LOGGER.error(MARKER, "Error starting SoundSystem. Turning off sounds & music", var2);
            this.options.setSoundCategoryVolume(SoundCategory.MASTER, 0.0F);
            this.options.save();
         }
      }
   }

   private float getVolume(SoundCategory category) {
      return category != null && category != SoundCategory.MASTER ? this.options.getSoundCategoryVolume(category) : 1.0F;
   }

   public void setVolume(SoundCategory category, float volume) {
      if (this.started) {
         if (category == SoundCategory.MASTER) {
            this.soundSystem.setMasterVolume(volume);
         } else {
            for(String var4 : this.channelsByCategory.get(category)) {
               ISoundEvent var5 = (ISoundEvent)this.events.get(var4);
               float var6 = this.getVolume(var5, (Sound)this.sources.get(var5), category);
               if (var6 <= 0.0F) {
                  this.stop(var5);
               } else {
                  this.soundSystem.setVolume(var4, var6);
               }
            }
         }
      }
   }

   public void close() {
      if (this.started) {
         this.stop();
         this.soundSystem.cleanup();
         this.started = false;
      }
   }

   public void stop() {
      if (this.started) {
         for(String var2 : this.events.keySet()) {
            this.soundSystem.stop(var2);
         }

         this.events.clear();
         this.soundQueue.clear();
         this.tickableEvents.clear();
         this.channelsByCategory.clear();
         this.sources.clear();
         this.soundBuffer.clear();
      }
   }

   public void tick() {
      ++this.ticks;

      for(ITickableSoundEvent var2 : this.tickableEvents) {
         var2.tick();
         if (var2.isDone()) {
            this.stop(var2);
         } else {
            String var3 = (String)this.channels.get(var2);
            this.soundSystem.setVolume(var3, this.getVolume(var2, (Sound)this.sources.get(var2), this.manager.get(var2.getId()).getCategory()));
            this.soundSystem.setPitch(var3, this.getPitch(var2, (Sound)this.sources.get(var2)));
            this.soundSystem.setPosition(var3, var2.getX(), var2.getY(), var2.getZ());
         }
      }

      Iterator var9 = this.events.entrySet().iterator();

      while(var9.hasNext()) {
         Entry var10 = (Entry)var9.next();
         String var12 = (String)var10.getKey();
         ISoundEvent var4 = (ISoundEvent)var10.getValue();
         if (!this.soundSystem.playing(var12)) {
            int var5 = this.soundBuffer.get(var12);
            if (var5 <= this.ticks) {
               int var6 = var4.getPeriod();
               if (var4.isRepeatable() && var6 > 0) {
                  this.soundQueue.put(var4, this.ticks + var6);
               }

               var9.remove();
               LOGGER.debug(MARKER, "Removed channel {} because it's not playing anymore", new Object[]{var12});
               this.soundSystem.removeSource(var12);
               this.soundBuffer.remove(var12);
               this.sources.remove(var4);

               try {
                  this.channelsByCategory.remove(this.manager.get(var4.getId()).getCategory(), var12);
               } catch (RuntimeException var8) {
               }

               if (var4 instanceof ITickableSoundEvent) {
                  this.tickableEvents.remove(var4);
               }
            }
         }
      }

      Iterator var11 = this.soundQueue.entrySet().iterator();

      while(var11.hasNext()) {
         Entry var13 = (Entry)var11.next();
         if (this.ticks >= var13.getValue()) {
            ISoundEvent var14 = (ISoundEvent)var13.getKey();
            if (var14 instanceof ITickableSoundEvent) {
               ((ITickableSoundEvent)var14).tick();
            }

            this.play(var14);
            var11.remove();
         }
      }
   }

   public boolean isPlaying(ISoundEvent event) {
      if (!this.started) {
         return false;
      } else {
         String var2 = (String)this.channels.get(event);
         if (var2 == null) {
            return false;
         } else {
            return this.soundSystem.playing(var2) || this.soundBuffer.containsKey(var2) && this.soundBuffer.get(var2) <= this.ticks;
         }
      }
   }

   public void stop(ISoundEvent event) {
      if (this.started) {
         String var2 = (String)this.channels.get(event);
         if (var2 != null) {
            this.soundSystem.stop(var2);
         }
      }
   }

   public void play(ISoundEvent event) {
      if (this.started) {
         if (this.soundSystem.getMasterVolume() <= 0.0F) {
            LOGGER.debug(MARKER, "Skipped playing soundEvent: {}, master volume was zero", new Object[]{event.getId()});
         } else {
            SoundPool var2 = this.manager.get(event.getId());
            if (var2 == null) {
               LOGGER.warn(MARKER, "Unable to play unknown soundEvent: {}", new Object[]{event.getId()});
            } else {
               Sound var3 = var2.get();
               if (var3 == SoundManager.MISSING_SOUND) {
                  LOGGER.warn(MARKER, "Unable to play empty soundEvent: {}", new Object[]{var2.getId()});
               } else {
                  float var4 = event.getVolume();
                  float var5 = 16.0F;
                  if (var4 > 1.0F) {
                     var5 *= var4;
                  }

                  SoundCategory var6 = var2.getCategory();
                  float var7 = this.getVolume(event, var3, var6);
                  double var8 = (double)this.getPitch(event, var3);
                  Identifier var10 = var3.getIdentifier();
                  if (var7 == 0.0F) {
                     LOGGER.debug(MARKER, "Skipped playing sound {}, volume was zero.", new Object[]{var10});
                  } else {
                     boolean var11 = event.isRepeatable() && event.getPeriod() == 0;
                     String var12 = MathHelper.nextUuid(ThreadLocalRandom.current()).toString();
                     if (var3.isStream()) {
                        this.soundSystem
                           .newStreamingSource(
                              false,
                              var12,
                              getSoundUrl(var10),
                              var10.toString(),
                              var11,
                              event.getX(),
                              event.getY(),
                              event.getZ(),
                              event.getAttenuationType().get(),
                              var5
                           );
                     } else {
                        this.soundSystem
                           .newSource(
                              false,
                              var12,
                              getSoundUrl(var10),
                              var10.toString(),
                              var11,
                              event.getX(),
                              event.getY(),
                              event.getZ(),
                              event.getAttenuationType().get(),
                              var5
                           );
                     }

                     LOGGER.debug(MARKER, "Playing sound {} for event {} as channel {}", new Object[]{var3.getIdentifier(), var2.getId(), var12});
                     this.soundSystem.setPitch(var12, (float)var8);
                     this.soundSystem.setVolume(var12, var7);
                     this.soundSystem.play(var12);
                     this.soundBuffer.put(var12, this.ticks + 20);
                     this.events.put(var12, event);
                     this.sources.put(event, var3);
                     if (var6 != SoundCategory.MASTER) {
                        this.channelsByCategory.put(var6, var12);
                     }

                     if (event instanceof ITickableSoundEvent) {
                        this.tickableEvents.add((ITickableSoundEvent)event);
                     }
                  }
               }
            }
         }
      }
   }

   private float getPitch(ISoundEvent event, Sound sound) {
      return (float)MathHelper.clamp((double)event.getPitch() * sound.getVolume(), 0.5, 2.0);
   }

   private float getVolume(ISoundEvent event, Sound sound, SoundCategory category) {
      return (float)MathHelper.clamp((double)event.getVolume() * sound.getPitch(), 0.0, 1.0) * this.getVolume(category);
   }

   public void pause() {
      for(String var2 : this.events.keySet()) {
         LOGGER.debug(MARKER, "Pausing channel {}", new Object[]{var2});
         this.soundSystem.pause(var2);
      }
   }

   public void resume() {
      for(String var2 : this.events.keySet()) {
         LOGGER.debug(MARKER, "Resuming channel {}", new Object[]{var2});
         this.soundSystem.play(var2);
      }
   }

   public void play(ISoundEvent event, int delay) {
      this.soundQueue.put(event, this.ticks + delay);
   }

   private static URL getSoundUrl(Identifier id) {
      String var1 = String.format("%s:%s:%s", "mcsounddomain", id.getNamespace(), id.getPath());
      URLStreamHandler var2 = new URLStreamHandler() {
         @Override
         protected URLConnection openConnection(URL url) {
            return new URLConnection(url) {
               @Override
               public void connect() {
               }

               @Override
               public InputStream getInputStream() {
                  return MinecraftClient.getInstance().getResourceManager().getResource(id).asStream();
               }
            };
         }
      };

      try {
         return new URL(null, var1, var2);
      } catch (MalformedURLException var4) {
         throw new Error("TODO: Sanely handle url exception! :D");
      }
   }

   public void updateListener(PlayerEntity player, float tickDelta) {
      if (this.started && player != null) {
         float var3 = player.prevPitch + (player.pitch - player.prevPitch) * tickDelta;
         float var4 = player.prevYaw + (player.yaw - player.prevYaw) * tickDelta;
         double var5 = player.prevX + (player.x - player.prevX) * (double)tickDelta;
         double var7 = player.prevY + (player.y - player.prevY) * (double)tickDelta + (double)player.getEyeHeight();
         double var9 = player.prevZ + (player.z - player.prevZ) * (double)tickDelta;
         float var11 = MathHelper.cos((var4 + 90.0F) * ((float) (Math.PI / 180.0)));
         float var12 = MathHelper.sin((var4 + 90.0F) * (float) (Math.PI / 180.0));
         float var13 = MathHelper.cos(-var3 * (float) (Math.PI / 180.0));
         float var14 = MathHelper.sin(-var3 * (float) (Math.PI / 180.0));
         float var15 = MathHelper.cos((-var3 + 90.0F) * (float) (Math.PI / 180.0));
         float var16 = MathHelper.sin((-var3 + 90.0F) * (float) (Math.PI / 180.0));
         float var17 = var11 * var13;
         float var19 = var12 * var13;
         float var20 = var11 * var15;
         float var22 = var12 * var15;
         this.soundSystem.setListenerPosition((float)var5, (float)var7, (float)var9);
         this.soundSystem.setListenerOrientation(var17, var14, var19, var20, var16, var22);
      }
   }

   @Environment(EnvType.CLIENT)
   class System extends paulscode.sound.SoundSystem {
      private System() {
      }

      public boolean playing(String channel) {
         synchronized(SoundSystemConfig.THREAD_SYNC) {
            if (this.soundLibrary == null) {
               return false;
            } else {
               Source var3 = (Source)this.soundLibrary.getSources().get(channel);
               if (var3 == null) {
                  return false;
               } else {
                  return var3.playing() || var3.paused() || var3.preLoad;
               }
            }
         }
      }
   }
}
