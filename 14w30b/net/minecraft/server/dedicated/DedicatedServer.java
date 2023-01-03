package net.minecraft.server.dedicated;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Proxy;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.network.encryption.EncryptionUtils;
import net.minecraft.server.Eula;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.dedicated.gui.DedicatedServerGui;
import net.minecraft.server.rcon.QueryResponseHandler;
import net.minecraft.server.rcon.RconServer;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.snooper.Snooper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.gen.WorldGeneratorType;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.SERVER)
public class DedicatedServer extends MinecraftServer implements IDedicatedServer {
   private static final Logger LOGGER = LogManager.getLogger();
   private final List pendingCommands = Collections.synchronizedList(Lists.newArrayList());
   private QueryResponseHandler queryResponseHandler;
   private RconServer rconServer;
   private ServerProperties properties;
   private Eula eula;
   private boolean shouldGenerateStructures;
   private WorldSettings.GameMode defaultGameMode;
   private boolean hasGui;

   public DedicatedServer(File gameDir) {
      super(gameDir, Proxy.NO_PROXY);
      new Thread("Server Infinisleeper") {
         {
            this.setDaemon(true);
            this.start();
         }

         @Override
         public void run() {
            while(true) {
               try {
                  Thread.sleep(2147483647L);
               } catch (InterruptedException var2) {
               }
            }
         }
      };
   }

   @Override
   protected boolean init() {
      Thread var1 = new Thread("Server console handler") {
         @Override
         public void run() {
            BufferedReader var1 = new BufferedReader(new InputStreamReader(System.in));

            String var2;
            try {
               while(!DedicatedServer.this.hasStopped() && DedicatedServer.this.isRunning() && (var2 = var1.readLine()) != null) {
                  DedicatedServer.this.queueCommand(var2, DedicatedServer.this);
               }
            } catch (IOException var4) {
               DedicatedServer.LOGGER.error("Exception handling console input", var4);
            }
         }
      };
      var1.setDaemon(true);
      var1.start();
      LOGGER.info("Starting minecraft server version 14w30b");
      if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
         LOGGER.warn("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
      }

      LOGGER.info("Loading properties");
      this.properties = new ServerProperties(new File("server.properties"));
      this.eula = new Eula(new File("eula.txt"));
      if (!this.eula.isAccepted()) {
         LOGGER.info("You need to agree to the EULA in order to run the server. Go to eula.txt for more info.");
         this.eula.write();
         return false;
      } else {
         if (this.isSinglePlayer()) {
            this.setServerIp("127.0.0.1");
         } else {
            this.setOnlineMode(this.properties.getOrDefault("online-mode", true));
            this.setServerIp(this.properties.getOrDefault("server-ip", ""));
         }

         this.setSpawnAnimals(this.properties.getOrDefault("spawn-animals", true));
         this.setSpawnNpcs(this.properties.getOrDefault("spawn-npcs", true));
         this.setPvpEnabled(this.properties.getOrDefault("pvp", true));
         this.setFlightEnabled(this.properties.getOrDefault("allow-flight", false));
         this.setResourcePack(this.properties.getOrDefault("resource-pack", ""));
         this.setMotd(this.properties.getOrDefault("motd", "A Minecraft Server"));
         this.setForceGameMode(this.properties.getOrDefault("force-gamemode", false));
         this.setPlayerIdleTimeout(this.properties.getOrDefault("player-idle-timeout", 0));
         if (this.properties.getOrDefault("difficulty", 1) < 0) {
            this.properties.set("difficulty", 0);
         } else if (this.properties.getOrDefault("difficulty", 1) > 3) {
            this.properties.set("difficulty", 3);
         }

         this.shouldGenerateStructures = this.properties.getOrDefault("generate-structures", true);
         int var2 = this.properties.getOrDefault("gamemode", WorldSettings.GameMode.SURVIVAL.getIndex());
         this.defaultGameMode = WorldSettings.getGameModeById(var2);
         LOGGER.info("Default game type: " + this.defaultGameMode);
         InetAddress var3 = null;
         if (this.getServerIp().length() > 0) {
            var3 = InetAddress.getByName(this.getServerIp());
         }

         if (this.getServerPort() < 0) {
            this.setServerPort(this.properties.getOrDefault("server-port", 25565));
         }

         LOGGER.info("Generating keypair");
         this.setKeyPair(EncryptionUtils.generateKeyPair());
         LOGGER.info("Starting Minecraft server on " + (this.getServerIp().length() == 0 ? "*" : this.getServerIp()) + ":" + this.getServerPort());

         try {
            this.getNetworkIo().bind(var3, this.getServerPort());
         } catch (IOException var16) {
            LOGGER.warn("**** FAILED TO BIND TO PORT!");
            LOGGER.warn("The exception was: {}", new Object[]{var16.toString()});
            LOGGER.warn("Perhaps a server is already running on that port?");
            return false;
         }

         if (!this.isOnlineMode()) {
            LOGGER.warn("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
            LOGGER.warn("The server will make no attempt to authenticate usernames. Beware.");
            LOGGER.warn(
               "While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose."
            );
            LOGGER.warn("To change this, set \"online-mode\" to \"true\" in the server.properties file.");
         }

         if (this.convertUsers()) {
            this.getPlayerCache().save();
         }

         if (!UserConverter.areUsersConverted(this.properties)) {
            return false;
         } else {
            this.setPlayerManager(new DedicatedPlayerManager(this));
            long var4 = System.nanoTime();
            if (this.getWorldDirName() == null) {
               this.setWorldDirName(this.properties.getOrDefault("level-name", "world"));
            }

            String var6 = this.properties.getOrDefault("level-seed", "");
            String var7 = this.properties.getOrDefault("level-type", "DEFAULT");
            String var8 = this.properties.getOrDefault("generator-settings", "");
            long var9 = new Random().nextLong();
            if (var6.length() > 0) {
               try {
                  long var11 = Long.parseLong(var6);
                  if (var11 != 0L) {
                     var9 = var11;
                  }
               } catch (NumberFormatException var15) {
                  var9 = (long)var6.hashCode();
               }
            }

            WorldGeneratorType var17 = WorldGeneratorType.byId(var7);
            if (var17 == null) {
               var17 = WorldGeneratorType.DEFAULT;
            }

            this.shouldAnnouncePlayerAchievements();
            this.areCommandBlocksEnabled();
            this.getOpPermissionLevel();
            this.isSnooperEnabled();
            this.getNetworkCompressionThreshold();
            this.setWorldHeight(this.properties.getOrDefault("max-build-height", 256));
            this.setWorldHeight((this.getWorldHeight() + 8) / 16 * 16);
            this.setWorldHeight(MathHelper.clamp(this.getWorldHeight(), 64, 256));
            this.properties.set("max-build-height", this.getWorldHeight());
            LOGGER.info("Preparing level \"" + this.getWorldDirName() + "\"");
            this.loadWorld(this.getWorldDirName(), this.getWorldDirName(), var9, var17, var8);
            long var12 = System.nanoTime() - var4;
            String var14 = String.format("%.3fs", (double)var12 / 1.0E9);
            LOGGER.info("Done (" + var14 + ")! For help, type \"help\" or \"?\"");
            if (this.properties.getOrDefault("enable-query", false)) {
               LOGGER.info("Starting GS4 status listener");
               this.queryResponseHandler = new QueryResponseHandler(this);
               this.queryResponseHandler.start();
            }

            if (this.properties.getOrDefault("enable-rcon", false)) {
               LOGGER.info("Starting remote control listener");
               this.rconServer = new RconServer(this);
               this.rconServer.start();
            }

            return true;
         }
      }
   }

   @Override
   public void setDefaultGameMode(WorldSettings.GameMode gamemode) {
      super.setDefaultGameMode(gamemode);
      this.defaultGameMode = gamemode;
   }

   @Override
   public boolean shouldGenerateStructures() {
      return this.shouldGenerateStructures;
   }

   @Override
   public WorldSettings.GameMode getDefaultGameMode() {
      return this.defaultGameMode;
   }

   @Override
   public Difficulty getDefaultDifficulty() {
      return Difficulty.byIndex(this.properties.getOrDefault("difficulty", 1));
   }

   @Override
   public boolean isHardcore() {
      return this.properties.getOrDefault("hardcore", false);
   }

   @Override
   protected void onServerCrashed(CrashReport report) {
      while(this.isRunning()) {
         this.runPendingCommands();

         try {
            Thread.sleep(10L);
         } catch (InterruptedException var3) {
         }
      }
   }

   @Override
   public CrashReport populateCrashReport(CrashReport report) {
      report = super.populateCrashReport(report);
      report.getSystemDetails().add("Is Modded", new Callable() {
         public String call() {
            String var1 = DedicatedServer.this.getServerModName();
            return !var1.equals("vanilla") ? "Definitely; Server brand changed to '" + var1 + "'" : "Unknown (can't tell)";
         }
      });
      report.getSystemDetails().add("Type", new Callable() {
         public String call() {
            return "Dedicated Server (map_server.txt)";
         }
      });
      return report;
   }

   @Override
   protected void exit() {
      System.exit(0);
   }

   @Override
   protected void tickWorlds() {
      super.tickWorlds();
      this.runPendingCommands();
   }

   @Override
   public boolean isNetherAllowed() {
      return this.properties.getOrDefault("allow-nether", true);
   }

   @Override
   public boolean isMonsterSpawningEnabled() {
      return this.properties.getOrDefault("spawn-monsters", true);
   }

   @Override
   public void addSnooperInfo(Snooper snooper) {
      snooper.addToSnoopedData("whitelist_enabled", this.getPlayerManager().isWhitelistEnabled());
      snooper.addToSnoopedData("whitelist_count", this.getPlayerManager().getWhitelistNames().length);
      super.addSnooperInfo(snooper);
   }

   @Override
   public boolean isSnooperEnabled() {
      return this.properties.getOrDefault("snooper-enabled", true);
   }

   public void queueCommand(String command, CommandSource source) {
      this.pendingCommands.add(new PendingCommand(command, source));
   }

   public void runPendingCommands() {
      while(!this.pendingCommands.isEmpty()) {
         PendingCommand var1 = (PendingCommand)this.pendingCommands.remove(0);
         this.getCommandHandler().run(var1.source, var1.command);
      }
   }

   @Override
   public boolean isDedicated() {
      return true;
   }

   public DedicatedPlayerManager getPlayerManager() {
      return (DedicatedPlayerManager)super.getPlayerManager();
   }

   @Override
   public int getPropertyOrDefault(String key, int defaultValue) {
      return this.properties.getOrDefault(key, defaultValue);
   }

   @Override
   public String getPropertyOrDefault(String key, String defaultValue) {
      return this.properties.getOrDefault(key, defaultValue);
   }

   public boolean getPropertyOrDefault(String name, boolean defaultValue) {
      return this.properties.getOrDefault(name, defaultValue);
   }

   @Override
   public void setProperty(String key, Object value) {
      this.properties.set(key, value);
   }

   @Override
   public void saveProperties() {
      this.properties.save();
   }

   @Override
   public String getPropertiesFilePath() {
      File var1 = this.properties.getFile();
      return var1 != null ? var1.getAbsolutePath() : "No settings file";
   }

   public void createGui() {
      DedicatedServerGui.create(this);
      this.hasGui = true;
   }

   @Override
   public boolean hasGui() {
      return this.hasGui;
   }

   @Override
   public String publish(WorldSettings.GameMode defaultGameMode, boolean allowCommands) {
      return "";
   }

   @Override
   public boolean areCommandBlocksEnabled() {
      return this.properties.getOrDefault("enable-command-block", false);
   }

   @Override
   public int getSpawnProtectionRadius() {
      return this.properties.getOrDefault("spawn-protection", super.getSpawnProtectionRadius());
   }

   @Override
   public boolean isSpawnProtected(World world, BlockPos pos, PlayerEntity player) {
      if (world.dimension.getId() != 0) {
         return false;
      } else if (this.getPlayerManager().getOps().isEmpty()) {
         return false;
      } else if (this.getPlayerManager().isOp(player.getGameProfile())) {
         return false;
      } else if (this.getSpawnProtectionRadius() <= 0) {
         return false;
      } else {
         BlockPos var4 = world.getSpawnPoint();
         int var5 = MathHelper.abs(pos.getX() - var4.getX());
         int var6 = MathHelper.abs(pos.getZ() - var4.getZ());
         int var7 = Math.max(var5, var6);
         return var7 <= this.getSpawnProtectionRadius();
      }
   }

   @Override
   public int getOpPermissionLevel() {
      return this.properties.getOrDefault("op-permission-level", 4);
   }

   @Override
   public void setPlayerIdleTimeout(int playerIdleTimeout) {
      super.setPlayerIdleTimeout(playerIdleTimeout);
      this.properties.set("player-idle-timeout", playerIdleTimeout);
      this.saveProperties();
   }

   @Override
   public boolean shouldAnnouncePlayerAchievements() {
      return this.properties.getOrDefault("announce-player-achievements", true);
   }

   @Override
   public int getMaxWorldSize() {
      int var1 = this.properties.getOrDefault("max-world-size", super.getMaxWorldSize());
      if (var1 < 1) {
         var1 = 1;
      } else if (var1 > super.getMaxWorldSize()) {
         var1 = super.getMaxWorldSize();
      }

      return var1;
   }

   @Override
   public int getNetworkCompressionThreshold() {
      return this.properties.getOrDefault("network-compression-threshold", super.getNetworkCompressionThreshold());
   }

   protected boolean convertUsers() {
      boolean var2 = false;

      for(int var1 = 0; !var2 && var1 <= 2; ++var1) {
         if (var1 > 0) {
            LOGGER.warn("Encountered a problem while converting the user banlist, retrying in a few seconds");
            this.sleep();
         }

         var2 = UserConverter.convertPlayerBans(this);
      }

      boolean var3 = false;

      for(int var7 = 0; !var3 && var7 <= 2; ++var7) {
         if (var7 > 0) {
            LOGGER.warn("Encountered a problem while converting the ip banlist, retrying in a few seconds");
            this.sleep();
         }

         var3 = UserConverter.convertIpBans(this);
      }

      boolean var4 = false;

      for(int var8 = 0; !var4 && var8 <= 2; ++var8) {
         if (var8 > 0) {
            LOGGER.warn("Encountered a problem while converting the op list, retrying in a few seconds");
            this.sleep();
         }

         var4 = UserConverter.convertOps(this);
      }

      boolean var5 = false;

      for(int var9 = 0; !var5 && var9 <= 2; ++var9) {
         if (var9 > 0) {
            LOGGER.warn("Encountered a problem while converting the whitelist, retrying in a few seconds");
            this.sleep();
         }

         var5 = UserConverter.convertWhitelist(this);
      }

      boolean var6 = false;

      for(int var10 = 0; !var6 && var10 <= 2; ++var10) {
         if (var10 > 0) {
            LOGGER.warn("Encountered a problem while converting the player save files, retrying in a few seconds");
            this.sleep();
         }

         var6 = UserConverter.convertPlayers(this, this.properties);
      }

      return var2 || var3 || var4 || var5 || var6;
   }

   private void sleep() {
      try {
         Thread.sleep(5000L);
      } catch (InterruptedException var2) {
      }
   }
}
