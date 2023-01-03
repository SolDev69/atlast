package net.minecraft.server;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.Proxy;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import javax.imageio.ImageIO;
import net.minecraft.Bootstrap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.WorldTimeS2CPacket;
import net.minecraft.server.command.Command;
import net.minecraft.server.command.handler.CommandHandler;
import net.minecraft.server.command.handler.CommandManager;
import net.minecraft.server.command.source.CommandResults;
import net.minecraft.server.command.source.CommandSource;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.entity.living.player.ServerPlayerEntity;
import net.minecraft.server.network.ServerNetworkIo;
import net.minecraft.server.world.DemoServerWorld;
import net.minecraft.server.world.ReadOnlyServerWorld;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.server.world.ServerWorldEventListener;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.BlockableEventLoop;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.Tickable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.snooper.Snoopable;
import net.minecraft.util.snooper.Snooper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldData;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.gen.WorldGeneratorType;
import net.minecraft.world.storage.AnvilWorldStorageSource;
import net.minecraft.world.storage.WorldStorage;
import net.minecraft.world.storage.WorldStorageSource;
import net.minecraft.world.storage.exception.WorldStorageException;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MinecraftServer implements CommandSource, Runnable, BlockableEventLoop, Snoopable {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final File USER_CACHE = new File("usercache.json");
   private static MinecraftServer instance;
   private final WorldStorageSource worldStorageSource;
   private final Snooper snooper = new Snooper("server", this, getTimeMillis());
   private final File gameDir;
   private final List tickables = Lists.newArrayList();
   private final CommandHandler commandHandler;
   public final Profiler profiler = new Profiler();
   private final ServerNetworkIo networkIo;
   private final ServerStatus status = new ServerStatus();
   private final Random random = new Random();
   @Environment(EnvType.SERVER)
   private String serverIp;
   private int serverPort = -1;
   public ServerWorld[] worlds;
   private PlayerManager playerManager;
   private boolean running = true;
   private boolean stopped;
   private int ticks;
   protected final Proxy proxy;
   public String progressType;
   public int progress;
   private boolean onlineMode;
   private boolean spawnAnimals;
   private boolean spawnNpcs;
   private boolean pvpEnabled;
   private boolean flightEnabled;
   private String motd;
   private int worldHeight;
   private int playerIdleTimeout = 0;
   public final long[] averageTickTimes = new long[100];
   public long[][] worldTickTimes;
   private KeyPair keyPair;
   private String userName;
   private String worldDirName;
   @Environment(EnvType.CLIENT)
   private String worldName;
   private boolean demo;
   private boolean bonusChestEnabled;
   private boolean stopping;
   private String resourcePackUrl = "";
   private boolean loading;
   private long lastWarnTime;
   private String serverOperation;
   private boolean profiling;
   private boolean forceGameMode;
   private final YggdrasilAuthenticationService authService;
   private final MinecraftSessionService sessionService;
   private long lastPlayerSampleUpdate = 0L;
   private final GameProfileRepository gameProfileRepository;
   private final PlayerCache playerCache;
   private final Queue pendingEvents = Queues.newArrayDeque();
   private Thread thread;
   private long nextTickTime = getTimeMillis();

   @Environment(EnvType.CLIENT)
   public MinecraftServer(Proxy proxy, File userCacheFile) {
      this.proxy = proxy;
      instance = this;
      this.gameDir = null;
      this.networkIo = null;
      this.playerCache = new PlayerCache(this, userCacheFile);
      this.commandHandler = null;
      this.worldStorageSource = null;
      this.authService = new YggdrasilAuthenticationService(proxy, UUID.randomUUID().toString());
      this.sessionService = this.authService.createMinecraftSessionService();
      this.gameProfileRepository = this.authService.createProfileRepository();
   }

   public MinecraftServer(File gameDir, Proxy proxy, File userCacheFile) {
      this.proxy = proxy;
      instance = this;
      this.gameDir = gameDir;
      this.networkIo = new ServerNetworkIo(this);
      this.playerCache = new PlayerCache(this, userCacheFile);
      this.commandHandler = this.createCommandHandler();
      this.worldStorageSource = new AnvilWorldStorageSource(gameDir);
      this.authService = new YggdrasilAuthenticationService(proxy, UUID.randomUUID().toString());
      this.sessionService = this.authService.createMinecraftSessionService();
      this.gameProfileRepository = this.authService.createProfileRepository();
   }

   protected CommandManager createCommandHandler() {
      return new CommandManager();
   }

   protected abstract boolean init();

   protected void convertWorld(String name) {
      if (this.getWorldStorageSource().needsConversion(name)) {
         LOGGER.info("Converting map!");
         this.setServerOperation("menu.convertingLevel");
         this.getWorldStorageSource().convert(name, new ProgressListener() {
            private long lastUpdateTime = MinecraftServer.getTimeMillis();

            @Override
            public void updateProgress(String title) {
            }

            @Environment(EnvType.CLIENT)
            @Override
            public void updateTitle(String title) {
            }

            @Override
            public void progressStagePercentage(int percentage) {
               if (MinecraftServer.getTimeMillis() - this.lastUpdateTime >= 1000L) {
                  this.lastUpdateTime = MinecraftServer.getTimeMillis();
                  MinecraftServer.LOGGER.info("Converting... " + percentage + "%");
               }
            }

            @Environment(EnvType.CLIENT)
            @Override
            public void setDone() {
            }

            @Override
            public void setTask(String task) {
            }
         });
      }
   }

   protected synchronized void setServerOperation(String operation) {
      this.serverOperation = operation;
   }

   @Environment(EnvType.CLIENT)
   public synchronized String getServerOperation() {
      return this.serverOperation;
   }

   protected void loadWorld(String name, String serverName, long seed, WorldGeneratorType generatorType, String generatorOptions) {
      this.convertWorld(name);
      this.setServerOperation("menu.loadingLevel");
      this.worlds = new ServerWorld[3];
      this.worldTickTimes = new long[this.worlds.length][100];
      WorldStorage var7 = this.worldStorageSource.get(name, true);
      this.findResourcePack(this.getWorldDirName(), var7);
      WorldData var9 = var7.loadData();
      WorldSettings var8;
      if (var9 == null) {
         if (this.isDemo()) {
            var8 = DemoServerWorld.SETTINGS;
         } else {
            var8 = new WorldSettings(seed, this.getDefaultGameMode(), this.shouldGenerateStructures(), this.isHardcore(), generatorType);
            var8.setGeneratorOptions(generatorOptions);
            if (this.bonusChestEnabled) {
               var8.enableBonusChest();
            }
         }

         var9 = new WorldData(var8, serverName);
      } else {
         var9.setName(serverName);
         var8 = new WorldSettings(var9);
      }

      for(int var10 = 0; var10 < this.worlds.length; ++var10) {
         byte var11 = 0;
         if (var10 == 1) {
            var11 = -1;
         }

         if (var10 == 2) {
            var11 = 1;
         }

         if (var10 == 0) {
            if (this.isDemo()) {
               this.worlds[var10] = (ServerWorld)new DemoServerWorld(this, var7, var9, var11, this.profiler).init();
            } else {
               this.worlds[var10] = (ServerWorld)new ServerWorld(this, var7, var9, var11, this.profiler).init();
            }

            this.worlds[var10].init(var8);
         } else {
            this.worlds[var10] = (ServerWorld)new ReadOnlyServerWorld(this, var7, var11, this.worlds[0], this.profiler).init();
         }

         this.worlds[var10].addEventListener(new ServerWorldEventListener(this, this.worlds[var10]));
         if (!this.isSinglePlayer()) {
            this.worlds[var10].getData().setDefaultGamemode(this.getDefaultGameMode());
         }
      }

      this.playerManager.onWorldsLoaded(this.worlds);
      this.setDifficulty(this.getDefaultDifficulty());
      this.prepareWorlds();
   }

   protected void prepareWorlds() {
      boolean var1 = true;
      boolean var2 = true;
      boolean var3 = true;
      boolean var4 = true;
      int var5 = 0;
      this.setServerOperation("menu.generatingTerrain");
      byte var6 = 0;
      LOGGER.info("Preparing start region for level " + var6);
      ServerWorld var7 = this.worlds[var6];
      BlockPos var8 = var7.getSpawnPoint();
      long var9 = getTimeMillis();

      for(int var11 = -192; var11 <= 192 && this.isRunning(); var11 += 16) {
         for(int var12 = -192; var12 <= 192 && this.isRunning(); var12 += 16) {
            long var13 = getTimeMillis();
            if (var13 - var9 > 1000L) {
               this.logProgress("Preparing spawn area", var5 * 100 / 625);
               var9 = var13;
            }

            ++var5;
            var7.chunkCache.loadChunk(var8.getX() + var11 >> 4, var8.getZ() + var12 >> 4);
         }
      }

      this.clearProgress();
   }

   protected void findResourcePack(String worldName, WorldStorage saveHandler) {
      File var3 = new File(saveHandler.getDir(), "resources.zip");
      if (var3.isFile()) {
         this.setResourcePack("level://" + worldName + "/" + var3.getName());
      }
   }

   public abstract boolean shouldGenerateStructures();

   public abstract WorldSettings.GameMode getDefaultGameMode();

   public abstract Difficulty getDefaultDifficulty();

   public abstract boolean isHardcore();

   public abstract int getOpPermissionLevel();

   protected void logProgress(String progressType, int progress) {
      this.progressType = progressType;
      this.progress = progress;
      LOGGER.info(progressType + ": " + progress + "%");
   }

   protected void clearProgress() {
      this.progressType = null;
      this.progress = 0;
   }

   protected void saveWorlds(boolean silent) {
      if (!this.stopping) {
         for(ServerWorld var5 : this.worlds) {
            if (var5 != null) {
               if (!silent) {
                  LOGGER.info("Saving chunks for level '" + var5.getData().getName() + "'/" + var5.dimension.getName());
               }

               try {
                  var5.save(true, null);
               } catch (WorldStorageException var7) {
                  LOGGER.warn(var7.getMessage());
               }
            }
         }
      }
   }

   public void stop() {
      if (!this.stopping) {
         LOGGER.info("Stopping server");
         if (this.getNetworkIo() != null) {
            this.getNetworkIo().close();
         }

         if (this.playerManager != null) {
            LOGGER.info("Saving players");
            this.playerManager.saveData();
            this.playerManager.disconnectAll();
         }

         if (this.worlds != null) {
            LOGGER.info("Saving worlds");
            this.saveWorlds(false);

            for(int var1 = 0; var1 < this.worlds.length; ++var1) {
               ServerWorld var2 = this.worlds[var1];
               var2.waitIfSaving();
            }
         }

         if (this.snooper.isActive()) {
            this.snooper.stopSnooping();
         }
      }
   }

   @Environment(EnvType.SERVER)
   public String getServerIp() {
      return this.serverIp;
   }

   @Environment(EnvType.SERVER)
   public void setServerIp(String serverIp) {
      this.serverIp = serverIp;
   }

   public boolean isRunning() {
      return this.running;
   }

   public void stopRunning() {
      this.running = false;
   }

   @Environment(EnvType.CLIENT)
   protected void setInstance() {
      instance = null;
   }

   @Override
   public void run() {
      try {
         if (this.init()) {
            this.nextTickTime = getTimeMillis();
            long var1 = 0L;
            this.status.setDescription(new LiteralText(this.motd));
            this.status.setVersion(new ServerStatus.Version("14w30c", 31));
            this.setStatus(this.status);

            while(this.running) {
               long var49 = getTimeMillis();
               long var5 = var49 - this.nextTickTime;
               if (var5 > 2000L && this.nextTickTime - this.lastWarnTime >= 15000L) {
                  LOGGER.warn(
                     "Can't keep up! Did the system time change, or is the server overloaded? Running {}ms behind, skipping {} tick(s)",
                     new Object[]{var5, var5 / 50L}
                  );
                  var5 = 2000L;
                  this.lastWarnTime = this.nextTickTime;
               }

               if (var5 < 0L) {
                  LOGGER.warn("Time ran backwards! Did the system time change?");
                  var5 = 0L;
               }

               var1 += var5;
               this.nextTickTime = var49;
               if (this.worlds[0].canSkipNight()) {
                  this.tick();
                  var1 = 0L;
               } else {
                  while(var1 > 50L) {
                     var1 -= 50L;
                     this.tick();
                  }
               }

               Thread.sleep(Math.max(1L, 50L - var1));
               this.loading = true;
            }
         } else {
            this.onServerCrashed(null);
         }
      } catch (Throwable var46) {
         LOGGER.error("Encountered an unexpected exception", var46);
         Object var2 = null;
         CrashReport var48;
         if (var46 instanceof CrashException) {
            var48 = this.populateCrashReport(((CrashException)var46).getReport());
         } else {
            var48 = this.populateCrashReport(new CrashReport("Exception in server tick loop", var46));
         }

         File var3 = new File(
            new File(this.getRunDir(), "crash-reports"), "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-server.txt"
         );
         if (var48.writeToFile(var3)) {
            LOGGER.error("This crash report has been saved to: " + var3.getAbsolutePath());
         } else {
            LOGGER.error("We were unable to save this crash report to disk.");
         }

         this.onServerCrashed(var48);
      } finally {
         try {
            this.stop();
            this.stopped = true;
         } catch (Throwable var44) {
            LOGGER.error("Exception stopping the server", var44);
         } finally {
            this.exit();
         }
      }
   }

   private void setStatus(ServerStatus status) {
      File var2 = this.getFile("server-icon.png");
      if (var2.isFile()) {
         ByteBuf var3 = Unpooled.buffer();

         try {
            BufferedImage var4 = ImageIO.read(var2);
            Validate.validState(var4.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
            Validate.validState(var4.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
            ImageIO.write(var4, "PNG", new ByteBufOutputStream(var3));
            ByteBuf var5 = Base64.encode(var3);
            status.setFavicon("data:image/png;base64," + var5.toString(Charsets.UTF_8));
         } catch (Exception var9) {
            LOGGER.error("Couldn't load server icon", var9);
         } finally {
            var3.release();
         }
      }
   }

   public File getRunDir() {
      return new File(".");
   }

   protected void onServerCrashed(CrashReport report) {
   }

   protected void exit() {
   }

   public void tick() {
      long var1 = System.nanoTime();
      ++this.ticks;
      if (this.profiling) {
         this.profiling = false;
         this.profiler.isProfiling = true;
         this.profiler.reset();
      }

      this.profiler.push("root");
      this.tickWorlds();
      if (var1 - this.lastPlayerSampleUpdate >= 5000000000L) {
         this.lastPlayerSampleUpdate = var1;
         this.status.setPlayers(new ServerStatus.Players(this.getMaxPlayerCount(), this.getPlayerCount()));
         GameProfile[] var3 = new GameProfile[Math.min(this.getPlayerCount(), 12)];
         int var4 = MathHelper.nextInt(this.random, 0, this.getPlayerCount() - var3.length);

         for(int var5 = 0; var5 < var3.length; ++var5) {
            var3[var5] = ((ServerPlayerEntity)this.playerManager.players.get(var4 + var5)).getGameProfile();
         }

         Collections.shuffle(Arrays.asList(var3));
         this.status.getPlayers().set(var3);
      }

      if (this.ticks % 900 == 0) {
         this.profiler.push("save");
         this.playerManager.saveData();
         this.saveWorlds(true);
         this.profiler.pop();
      }

      this.profiler.push("tallying");
      this.averageTickTimes[this.ticks % 100] = System.nanoTime() - var1;
      this.profiler.pop();
      this.profiler.push("snooper");
      if (!this.snooper.isActive() && this.ticks > 100) {
         this.snooper.startSnooping();
      }

      if (this.ticks % 6000 == 0) {
         this.snooper.addCpuInfo();
      }

      this.profiler.pop();
      this.profiler.pop();
   }

   public void tickWorlds() {
      this.profiler.push("jobs");
      synchronized(this.pendingEvents) {
         while(!this.pendingEvents.isEmpty()) {
            try {
               ((FutureTask)this.pendingEvents.poll()).run();
            } catch (Throwable var9) {
               LOGGER.fatal(var9);
            }
         }
      }

      this.profiler.swap("levels");

      for(int var11 = 0; var11 < this.worlds.length; ++var11) {
         long var2 = System.nanoTime();
         if (var11 == 0 || this.isNetherAllowed()) {
            ServerWorld var4 = this.worlds[var11];
            this.profiler.push(var4.getData().getName());
            if (this.ticks % 20 == 0) {
               this.profiler.push("timeSync");
               this.playerManager
                  .sendToDimension(
                     new WorldTimeS2CPacket(var4.getTime(), var4.getTimeOfDay(), var4.getGameRules().getBoolean("doDaylightCycle")), var4.dimension.getId()
                  );
               this.profiler.pop();
            }

            this.profiler.push("tick");

            try {
               var4.tick();
            } catch (Throwable var8) {
               CrashReport var6 = CrashReport.of(var8, "Exception ticking world");
               var4.populateCrashReport(var6);
               throw new CrashException(var6);
            }

            try {
               var4.tickEntities();
            } catch (Throwable var7) {
               CrashReport var13 = CrashReport.of(var7, "Exception ticking world entities");
               var4.populateCrashReport(var13);
               throw new CrashException(var13);
            }

            this.profiler.pop();
            this.profiler.push("tracker");
            var4.getEntityTracker().tick();
            this.profiler.pop();
            this.profiler.pop();
         }

         this.worldTickTimes[var11][this.ticks % 100] = System.nanoTime() - var2;
      }

      this.profiler.swap("connection");
      this.getNetworkIo().tick();
      this.profiler.swap("players");
      this.playerManager.tick();
      this.profiler.swap("tickables");

      for(int var12 = 0; var12 < this.tickables.size(); ++var12) {
         ((Tickable)this.tickables.get(var12)).tick();
      }

      this.profiler.pop();
   }

   public boolean isNetherAllowed() {
      return true;
   }

   @Environment(EnvType.SERVER)
   public void addTickable(Tickable tickable) {
      this.tickables.add(tickable);
   }

   @Environment(EnvType.SERVER)
   public static void main(String[] args) {
      Bootstrap.init();

      try {
         boolean var1 = true;
         String var2 = null;
         String var3 = ".";
         String var4 = null;
         boolean var5 = false;
         boolean var6 = false;
         int var7 = -1;

         for(int var8 = 0; var8 < args.length; ++var8) {
            String var9 = args[var8];
            String var10 = var8 == args.length - 1 ? null : args[var8 + 1];
            boolean var11 = false;
            if (var9.equals("nogui") || var9.equals("--nogui")) {
               var1 = false;
            } else if (var9.equals("--port") && var10 != null) {
               var11 = true;

               try {
                  var7 = Integer.parseInt(var10);
               } catch (NumberFormatException var13) {
               }
            } else if (var9.equals("--singleplayer") && var10 != null) {
               var11 = true;
               var2 = var10;
            } else if (var9.equals("--universe") && var10 != null) {
               var11 = true;
               var3 = var10;
            } else if (var9.equals("--world") && var10 != null) {
               var11 = true;
               var4 = var10;
            } else if (var9.equals("--demo")) {
               var5 = true;
            } else if (var9.equals("--bonusChest")) {
               var6 = true;
            }

            if (var11) {
               ++var8;
            }
         }

         final DedicatedServer var15 = new DedicatedServer(new File(var3));
         if (var2 != null) {
            var15.setUserName(var2);
         }

         if (var4 != null) {
            var15.setWorldDirName(var4);
         }

         if (var7 >= 0) {
            var15.setServerPort(var7);
         }

         if (var5) {
            var15.setDemo(true);
         }

         if (var6) {
            var15.enableBonusChest(true);
         }

         if (var1 && !GraphicsEnvironment.isHeadless()) {
            var15.createGui();
         }

         var15.start();
         Runtime.getRuntime().addShutdownHook(new Thread("Server Shutdown Thread") {
            @Override
            public void run() {
               var15.stop();
            }
         });
      } catch (Exception var14) {
         LOGGER.fatal("Failed to start the minecraft server", var14);
      }
   }

   public void start() {
      this.thread = new Thread(this, "Server thread");
      this.thread.start();
   }

   public File getFile(String path) {
      return new File(this.getRunDir(), path);
   }

   @Environment(EnvType.SERVER)
   public void info(String message) {
      LOGGER.info(message);
   }

   public void warn(String message) {
      LOGGER.warn(message);
   }

   public ServerWorld getWorld(int id) {
      if (id == -1) {
         return this.worlds[1];
      } else {
         return id == 1 ? this.worlds[2] : this.worlds[0];
      }
   }

   @Environment(EnvType.SERVER)
   public String getIp() {
      return this.serverIp;
   }

   @Environment(EnvType.SERVER)
   public int getPort() {
      return this.serverPort;
   }

   @Environment(EnvType.SERVER)
   public String getMotd() {
      return this.motd;
   }

   public String getGameVersion() {
      return "14w30c";
   }

   public int getPlayerCount() {
      return this.playerManager.getCount();
   }

   public int getMaxPlayerCount() {
      return this.playerManager.getMaxCount();
   }

   public String[] getPlayerNames() {
      return this.playerManager.getNames();
   }

   public GameProfile[] getGameProfiles() {
      return this.playerManager.getProfiles();
   }

   @Environment(EnvType.SERVER)
   public String getPlugins() {
      return "";
   }

   @Environment(EnvType.SERVER)
   public String executeRconCommand(String command) {
      Console.getInstance().destroy();
      this.commandHandler.run(Console.getInstance(), command);
      return Console.getInstance().getTextAsString();
   }

   @Environment(EnvType.SERVER)
   public boolean isDebuggingEnabled() {
      return false;
   }

   @Environment(EnvType.SERVER)
   public void error(String message) {
      LOGGER.error(message);
   }

   @Environment(EnvType.SERVER)
   public void log(String message) {
      if (this.isDebuggingEnabled()) {
         LOGGER.info(message);
      }
   }

   public String getServerModName() {
      return "vanilla";
   }

   public CrashReport populateCrashReport(CrashReport report) {
      report.getSystemDetails().add("Profiler Position", new Callable() {
         public String call() {
            return MinecraftServer.this.profiler.isProfiling ? MinecraftServer.this.profiler.getCurrentLocation() : "N/A (disabled)";
         }
      });
      if (this.playerManager != null) {
         report.getSystemDetails()
            .add(
               "Player Count",
               new Callable() {
                  public String call() {
                     return MinecraftServer.this.playerManager.getCount()
                        + " / "
                        + MinecraftServer.this.playerManager.getMaxCount()
                        + "; "
                        + MinecraftServer.this.playerManager.players;
                  }
               }
            );
      }

      return report;
   }

   public List getCommandSuggestions(CommandSource source, String command) {
      ArrayList var3 = Lists.newArrayList();
      if (command.startsWith("/")) {
         command = command.substring(1);
         boolean var11 = !command.contains(" ");
         List var12 = this.commandHandler.getSuggestions(source, command);
         if (var12 != null) {
            for(String var14 : var12) {
               if (var11) {
                  var3.add("/" + var14);
               } else {
                  var3.add(var14);
               }
            }
         }

         return var3;
      } else {
         String[] var4 = command.split(" ", -1);
         String var5 = var4[var4.length - 1];

         for(String var9 : this.playerManager.getNames()) {
            if (Command.doesStringStartWith(var5, var9)) {
               var3.add(var9);
            }
         }

         return var3;
      }
   }

   public static MinecraftServer getInstance() {
      return instance;
   }

   public boolean hasGameDir() {
      return this.gameDir != null;
   }

   @Override
   public String getName() {
      return "Server";
   }

   @Override
   public void sendMessage(Text message) {
      LOGGER.info(message.buildString());
   }

   @Override
   public boolean canUseCommand(int permissionLevel, String command) {
      return true;
   }

   public CommandHandler getCommandHandler() {
      return this.commandHandler;
   }

   public KeyPair getKeyPair() {
      return this.keyPair;
   }

   @Environment(EnvType.SERVER)
   public int getServerPort() {
      return this.serverPort;
   }

   @Environment(EnvType.SERVER)
   public void setServerPort(int serverPort) {
      this.serverPort = serverPort;
   }

   public String getUserName() {
      return this.userName;
   }

   public void setUserName(String userName) {
      this.userName = userName;
   }

   public boolean isSinglePlayer() {
      return this.userName != null;
   }

   public String getWorldDirName() {
      return this.worldDirName;
   }

   public void setWorldDirName(String worldDirName) {
      this.worldDirName = worldDirName;
   }

   @Environment(EnvType.CLIENT)
   public void setWorldName(String worldName) {
      this.worldName = worldName;
   }

   @Environment(EnvType.CLIENT)
   public String getWorldName() {
      return this.worldName;
   }

   public void setKeyPair(KeyPair keyPair) {
      this.keyPair = keyPair;
   }

   public void setDifficulty(Difficulty difficulty) {
      for(int var2 = 0; var2 < this.worlds.length; ++var2) {
         ServerWorld var3 = this.worlds[var2];
         if (var3 != null) {
            if (var3.getData().isHardcore()) {
               var3.getData().setDifficulty(Difficulty.HARD);
               var3.setAllowedMobSpawns(true, true);
            } else if (this.isSinglePlayer()) {
               var3.getData().setDifficulty(difficulty);
               var3.setAllowedMobSpawns(var3.getDifficulty() != Difficulty.PEACEFUL, true);
            } else {
               var3.getData().setDifficulty(difficulty);
               var3.setAllowedMobSpawns(this.isMonsterSpawningEnabled(), this.spawnAnimals);
            }
         }
      }
   }

   protected boolean isMonsterSpawningEnabled() {
      return true;
   }

   public boolean isDemo() {
      return this.demo;
   }

   public void setDemo(boolean demo) {
      this.demo = demo;
   }

   public void enableBonusChest(boolean bonusChestEnabled) {
      this.bonusChestEnabled = bonusChestEnabled;
   }

   public WorldStorageSource getWorldStorageSource() {
      return this.worldStorageSource;
   }

   public void deleteWorldAndStop() {
      this.stopping = true;
      this.getWorldStorageSource().clearRegionIo();

      for(int var1 = 0; var1 < this.worlds.length; ++var1) {
         ServerWorld var2 = this.worlds[var1];
         if (var2 != null) {
            var2.waitIfSaving();
         }
      }

      this.getWorldStorageSource().delete(this.worlds[0].getStorage().getName());
      this.stopRunning();
   }

   public String getResourcePackUrl() {
      return this.resourcePackUrl;
   }

   public void setResourcePack(String url) {
      this.resourcePackUrl = url;
   }

   @Override
   public void addSnooperInfo(Snooper snooper) {
      snooper.addToSnoopedData("whitelist_enabled", false);
      snooper.addToSnoopedData("whitelist_count", 0);
      snooper.addToSnoopedData("players_current", this.getPlayerCount());
      snooper.addToSnoopedData("players_max", this.getMaxPlayerCount());
      snooper.addToSnoopedData("players_seen", this.playerManager.getSavedIds().length);
      snooper.addToSnoopedData("uses_auth", this.onlineMode);
      snooper.addToSnoopedData("gui_state", this.hasGui() ? "enabled" : "disabled");
      snooper.addToSnoopedData("run_time", (getTimeMillis() - snooper.getSnooperInitTime()) / 60L * 1000L);
      snooper.addToSnoopedData("avg_tick_ms", (int)(MathHelper.average(this.averageTickTimes) * 1.0E-6));
      int var2 = 0;

      for(int var3 = 0; var3 < this.worlds.length; ++var3) {
         if (this.worlds[var3] != null) {
            ServerWorld var4 = this.worlds[var3];
            WorldData var5 = var4.getData();
            snooper.addToSnoopedData("world[" + var2 + "][dimension]", var4.dimension.getId());
            snooper.addToSnoopedData("world[" + var2 + "][mode]", var5.getDefaultGamemode());
            snooper.addToSnoopedData("world[" + var2 + "][difficulty]", var4.getDifficulty());
            snooper.addToSnoopedData("world[" + var2 + "][hardcore]", var5.isHardcore());
            snooper.addToSnoopedData("world[" + var2 + "][generator_name]", var5.getGeneratorType().getId());
            snooper.addToSnoopedData("world[" + var2 + "][generator_version]", var5.getGeneratorType().getVersion());
            snooper.addToSnoopedData("world[" + var2 + "][height]", this.worldHeight);
            snooper.addToSnoopedData("world[" + var2 + "][chunks_loaded]", var4.getChunkSource().getLoadedCount());
            ++var2;
         }
      }

      snooper.addToSnoopedData("worlds", var2);
   }

   @Override
   public void addSnooper(Snooper snooper) {
      snooper.put("singleplayer", this.isSinglePlayer());
      snooper.put("server_brand", this.getServerModName());
      snooper.put("gui_supported", GraphicsEnvironment.isHeadless() ? "headless" : "supported");
      snooper.put("dedicated", this.isDedicated());
   }

   @Override
   public boolean isSnooperEnabled() {
      return true;
   }

   public abstract boolean isDedicated();

   public boolean isOnlineMode() {
      return this.onlineMode;
   }

   public void setOnlineMode(boolean onlineMode) {
      this.onlineMode = onlineMode;
   }

   public boolean shouldSpawnAnimals() {
      return this.spawnAnimals;
   }

   public void setSpawnAnimals(boolean spawnAnimals) {
      this.spawnAnimals = spawnAnimals;
   }

   public boolean shouldSpawnNpcs() {
      return this.spawnNpcs;
   }

   public void setSpawnNpcs(boolean spawnNpcs) {
      this.spawnNpcs = spawnNpcs;
   }

   public boolean isPvpEnabled() {
      return this.pvpEnabled;
   }

   public void setPvpEnabled(boolean pvpEnabled) {
      this.pvpEnabled = pvpEnabled;
   }

   public boolean isFlightEnabled() {
      return this.flightEnabled;
   }

   public void setFlightEnabled(boolean flightEnabled) {
      this.flightEnabled = flightEnabled;
   }

   public abstract boolean areCommandBlocksEnabled();

   public String getServerMotd() {
      return this.motd;
   }

   public void setMotd(String motd) {
      this.motd = motd;
   }

   public int getWorldHeight() {
      return this.worldHeight;
   }

   public void setWorldHeight(int worldHeight) {
      this.worldHeight = worldHeight;
   }

   @Environment(EnvType.SERVER)
   public boolean hasStopped() {
      return this.stopped;
   }

   public PlayerManager getPlayerManager() {
      return this.playerManager;
   }

   public void setPlayerManager(PlayerManager playerManager) {
      this.playerManager = playerManager;
   }

   public void setDefaultGameMode(WorldSettings.GameMode gamemode) {
      for(int var2 = 0; var2 < this.worlds.length; ++var2) {
         getInstance().worlds[var2].getData().setDefaultGamemode(gamemode);
      }
   }

   public ServerNetworkIo getNetworkIo() {
      return this.networkIo;
   }

   @Environment(EnvType.CLIENT)
   public boolean isLoading() {
      return this.loading;
   }

   public boolean hasGui() {
      return false;
   }

   public abstract String publish(WorldSettings.GameMode defaultGameMode, boolean allowCommands);

   public int getTicks() {
      return this.ticks;
   }

   public void enableProfiling() {
      this.profiling = true;
   }

   @Environment(EnvType.CLIENT)
   public Snooper getSnooper() {
      return this.snooper;
   }

   @Override
   public BlockPos getSourceBlockPos() {
      return BlockPos.ORIGIN;
   }

   @Override
   public World getSourceWorld() {
      return this.worlds[0];
   }

   @Override
   public Entity asEntity() {
      return null;
   }

   public int getSpawnProtectionRadius() {
      return 16;
   }

   public boolean isSpawnProtected(World world, BlockPos pos, PlayerEntity player) {
      return false;
   }

   @Environment(EnvType.SERVER)
   public void setForceGameMode(boolean forceGameMode) {
      this.forceGameMode = forceGameMode;
   }

   public boolean shouldForceGameMode() {
      return this.forceGameMode;
   }

   public Proxy getProxy() {
      return this.proxy;
   }

   public static long getTimeMillis() {
      return System.currentTimeMillis();
   }

   public int getPlayerIdleTimeout() {
      return this.playerIdleTimeout;
   }

   public void setPlayerIdleTimeout(int playerIdleTimeout) {
      this.playerIdleTimeout = playerIdleTimeout;
   }

   @Override
   public Text getDisplayName() {
      return new LiteralText(this.getName());
   }

   public boolean shouldAnnouncePlayerAchievements() {
      return true;
   }

   public MinecraftSessionService getSessionService() {
      return this.sessionService;
   }

   public GameProfileRepository getGameProfileRepository() {
      return this.gameProfileRepository;
   }

   public PlayerCache getPlayerCache() {
      return this.playerCache;
   }

   public ServerStatus getStatus() {
      return this.status;
   }

   public void forcePlayerSampleUpdate() {
      this.lastPlayerSampleUpdate = 0L;
   }

   public Entity getEntity(UUID uuid) {
      for(ServerWorld var5 : this.worlds) {
         if (var5 != null) {
            Entity var6 = var5.getEntity(uuid);
            if (var6 != null) {
               return var6;
            }
         }
      }

      return null;
   }

   @Override
   public boolean sendCommandFeedback() {
      return getInstance().worlds[0].getGameRules().getBoolean("sendCommandFeedback");
   }

   @Override
   public void addResult(CommandResults.Type type, int result) {
   }

   public int getMaxWorldSize() {
      return 29999984;
   }

   public ListenableFuture submit(Callable event) {
      Validate.notNull(event);
      if (!this.isOnSameThread()) {
         ListenableFutureTask var2 = ListenableFutureTask.create(event);
         synchronized(this.pendingEvents) {
            this.pendingEvents.add(var2);
            return var2;
         }
      } else {
         try {
            return Futures.immediateFuture(event.call());
         } catch (Exception var6) {
            return Futures.immediateFailedCheckedFuture(var6);
         }
      }
   }

   @Override
   public ListenableFuture submit(Runnable event) {
      Validate.notNull(event);
      return this.submit(Executors.callable(event));
   }

   @Override
   public boolean isOnSameThread() {
      return Thread.currentThread() == this.thread;
   }

   public int getNetworkCompressionThreshold() {
      return 256;
   }

   @Environment(EnvType.SERVER)
   public long getNextTickTime() {
      return this.nextTickTime;
   }

   @Environment(EnvType.SERVER)
   public Thread getThread() {
      return this.thread;
   }
}
