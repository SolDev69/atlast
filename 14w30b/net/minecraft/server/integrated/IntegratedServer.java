package net.minecraft.server.integrated;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.encryption.EncryptionUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.handler.CommandManager;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.world.DemoServerWorld;
import net.minecraft.server.world.ReadOnlyServerWorld;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ServerWorldEventListener;
import net.minecraft.util.NetworkUtils;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.snooper.Snooper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.WorldData;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.gen.WorldGeneratorType;
import net.minecraft.world.storage.WorldStorage;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class IntegratedServer extends MinecraftServer {
   private static final Logger LOGGER = LogManager.getLogger();
   private final MinecraftClient client;
   private final WorldSettings info;
   private boolean paused;
   private boolean published;
   private LanServerPinger pinger;

   public IntegratedServer(MinecraftClient client) {
      super(client.getNetworkProxy(), new File(client.runDir, USER_CACHE.getName()));
      this.client = client;
      this.info = null;
   }

   public IntegratedServer(MinecraftClient client, String name, String serverName, WorldSettings info) {
      super(new File(client.runDir, "saves"), client.getNetworkProxy(), new File(client.runDir, USER_CACHE.getName()));
      this.setUserName(client.getSession().getUsername());
      this.setWorldDirName(name);
      this.setWorldName(serverName);
      this.setDemo(client.isDemo());
      this.enableBonusChest(info.hasBonusChest());
      this.setWorldHeight(256);
      this.setPlayerManager(new IntegratedPlayerManager(this));
      this.client = client;
      this.info = this.isDemo() ? DemoServerWorld.SETTINGS : info;
   }

   @Override
   protected CommandManager createCommandHandler() {
      return new IntegratedCommandManager();
   }

   @Override
   protected void loadWorld(String name, String serverName, long seed, WorldGeneratorType generatorType, String generatorOptions) {
      this.convertWorld(name);
      this.worlds = new ServerWorld[3];
      this.worldTickTimes = new long[this.worlds.length][100];
      WorldStorage var7 = this.getWorldStorageSource().get(name, true);
      this.findResourcePack(this.getWorldDirName(), var7);
      WorldData var8 = var7.loadData();
      if (var8 == null) {
         var8 = new WorldData(this.info, serverName);
      } else {
         var8.setName(serverName);
      }

      for(int var9 = 0; var9 < this.worlds.length; ++var9) {
         byte var10 = 0;
         if (var9 == 1) {
            var10 = -1;
         }

         if (var9 == 2) {
            var10 = 1;
         }

         if (var9 == 0) {
            if (this.isDemo()) {
               this.worlds[var9] = (ServerWorld)new DemoServerWorld(this, var7, var8, var10, this.profiler).init();
            } else {
               this.worlds[var9] = (ServerWorld)new ServerWorld(this, var7, var8, var10, this.profiler).init();
            }

            this.worlds[var9].init(this.info);
         } else {
            this.worlds[var9] = (ServerWorld)new ReadOnlyServerWorld(this, var7, var10, this.worlds[0], this.profiler).init();
         }

         this.worlds[var9].addEventListener(new ServerWorldEventListener(this, this.worlds[var9]));
      }

      this.getPlayerManager().onWorldsLoaded(this.worlds);
      if (this.worlds[0].getData().getDifficulty() == null) {
         this.setDifficulty(this.client.options.difficulty);
      }

      this.prepareWorlds();
   }

   @Override
   protected boolean init() {
      LOGGER.info("Starting integrated minecraft server version 14w30c");
      this.setOnlineMode(true);
      this.setSpawnAnimals(true);
      this.setSpawnNpcs(true);
      this.setPvpEnabled(true);
      this.setFlightEnabled(true);
      LOGGER.info("Generating keypair");
      this.setKeyPair(EncryptionUtils.generateKeyPair());
      this.loadWorld(this.getWorldDirName(), this.getWorldName(), this.info.getSeed(), this.info.getGeneratorType(), this.info.getGeneratorOptions());
      this.setMotd(this.getUserName() + " - " + this.worlds[0].getData().getName());
      return true;
   }

   @Override
   protected void tick() {
      boolean var1 = this.paused;
      this.paused = MinecraftClient.getInstance().getNetworkHandler() != null && MinecraftClient.getInstance().isPaused();
      if (!var1 && this.paused) {
         LOGGER.info("Saving and pausing game...");
         this.getPlayerManager().saveData();
         this.saveWorlds(false);
      }

      if (!this.paused) {
         super.tick();
         if (this.client.options.viewDistance != this.getPlayerManager().getChunkViewDistance()) {
            LOGGER.info("Changing view distance to {}, from {}", new Object[]{this.client.options.viewDistance, this.getPlayerManager().getChunkViewDistance()});
            this.getPlayerManager().updateViewDistance(this.client.options.viewDistance);
         }

         if (this.client.world != null) {
            WorldData var2 = this.worlds[0].getData();
            WorldData var3 = this.client.world.getData();
            if (!var2.isDifficultyLocked() && var3.getDifficulty() != var2.getDifficulty()) {
               LOGGER.info("Changing difficulty to {}, from {}", new Object[]{var3.getDifficulty(), var2.getDifficulty()});
               this.setDifficulty(var3.getDifficulty());
            } else if (var3.isDifficultyLocked() && !var2.isDifficultyLocked()) {
               LOGGER.info("Locking difficulty to {}", new Object[]{var3.getDifficulty()});

               for(ServerWorld var7 : this.worlds) {
                  if (var7 != null) {
                     var7.getData().setDifficultyLocked(true);
                  }
               }
            }
         }
      }
   }

   @Override
   public boolean shouldGenerateStructures() {
      return false;
   }

   @Override
   public WorldSettings.GameMode getDefaultGameMode() {
      return this.info.getGameMode();
   }

   @Override
   public Difficulty getDefaultDifficulty() {
      return this.client.world.getData().getDifficulty();
   }

   @Override
   public boolean isHardcore() {
      return this.info.isHardcore();
   }

   @Override
   public File getRunDir() {
      return this.client.runDir;
   }

   @Override
   public boolean isDedicated() {
      return false;
   }

   @Override
   protected void onServerCrashed(CrashReport report) {
      this.client.crash(report);
   }

   @Override
   public CrashReport populateCrashReport(CrashReport report) {
      report = super.populateCrashReport(report);
      report.getSystemDetails().add("Type", new Callable() {
         public String call() {
            return "Integrated Server (map_client.txt)";
         }
      });
      report.getSystemDetails()
         .add(
            "Is Modded",
            new Callable() {
               public String call() {
                  String var1 = ClientBrandRetriever.getClientModName();
                  if (!var1.equals("vanilla")) {
                     return "Definitely; Client brand changed to '" + var1 + "'";
                  } else {
                     var1 = IntegratedServer.this.getServerModName();
                     if (!var1.equals("vanilla")) {
                        return "Definitely; Server brand changed to '" + var1 + "'";
                     } else {
                        return MinecraftClient.class.getSigners() == null
                           ? "Very likely; Jar signature invalidated"
                           : "Probably not. Jar signature remains and both client + server brands are untouched.";
                     }
                  }
               }
            }
         );
      return report;
   }

   @Override
   public void setDifficulty(Difficulty difficulty) {
      super.setDifficulty(difficulty);
      if (this.client.world != null) {
         this.client.world.getData().setDifficulty(difficulty);
      }
   }

   @Override
   public void addSnooperInfo(Snooper snooper) {
      super.addSnooperInfo(snooper);
      snooper.addToSnoopedData("snooper_partner", this.client.getSnooper().getSnooperToken());
   }

   @Override
   public boolean isSnooperEnabled() {
      return MinecraftClient.getInstance().isSnooperEnabled();
   }

   @Override
   public String publish(WorldSettings.GameMode defaultGameMode, boolean allowCommands) {
      try {
         int var3 = -1;

         try {
            var3 = NetworkUtils.getLocalPort();
         } catch (IOException var5) {
         }

         if (var3 <= 0) {
            var3 = 25564;
         }

         this.getNetworkIo().bind(null, var3);
         LOGGER.info("Started on " + var3);
         this.published = true;
         this.pinger = new LanServerPinger(this.getServerMotd(), var3 + "");
         this.pinger.start();
         this.getPlayerManager().setDefaultGamemode(defaultGameMode);
         this.getPlayerManager().setAllowCommands(allowCommands);
         return var3 + "";
      } catch (IOException var6) {
         return null;
      }
   }

   @Override
   public void stop() {
      super.stop();
      if (this.pinger != null) {
         this.pinger.interrupt();
         this.pinger = null;
      }
   }

   @Override
   public void stopRunning() {
      super.stopRunning();
      if (this.pinger != null) {
         this.pinger.interrupt();
         this.pinger = null;
      }

      for(ServerPlayerEntity var3 : Lists.newArrayList(this.getPlayerManager().players)) {
         this.getPlayerManager().remove(var3);
      }
   }

   @Override
   public void setInstance() {
      this.setInstance();
   }

   public boolean isPublished() {
      return this.published;
   }

   @Override
   public void setDefaultGameMode(WorldSettings.GameMode gamemode) {
      this.getPlayerManager().setDefaultGamemode(gamemode);
   }

   @Override
   public boolean areCommandBlocksEnabled() {
      return true;
   }

   @Override
   public int getOpPermissionLevel() {
      return 4;
   }
}
