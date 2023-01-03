package net.minecraft.client;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import javax.imageio.ImageIO;
import net.minecraft.Bootstrap;
import net.minecraft.C_39cmizuwc;
import net.minecraft.C_48kamxasz;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.living.player.LocalClientPlayerEntity;
import net.minecraft.client.entity.particle.ParticleManager;
import net.minecraft.client.gui.GameGui;
import net.minecraft.client.gui.ToastGui;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.ConfirmationListener;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.OutOfMemoryScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SleepingChatScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.inventory.menu.SurvivalInventoryScreen;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.input.MouseInput;
import net.minecraft.client.main.RunArgs;
import net.minecraft.client.network.handler.ClientLoginNetworkHandler;
import net.minecraft.client.network.handler.ClientPlayNetworkHandler;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.options.ServerListEntry;
import net.minecraft.client.player.input.GameInput;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.HeldItemRenderer;
import net.minecraft.client.render.LoadingScreenRenderError;
import net.minecraft.client.render.LoadingScreenRenderer;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.Window;
import net.minecraft.client.render.block.BlockRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.world.ChunkBlockRenderer;
import net.minecraft.client.render.world.WorldRenderer;
import net.minecraft.client.resource.AssetIndex;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.language.LanguageManager;
import net.minecraft.client.resource.manager.IReloadableResourceManager;
import net.minecraft.client.resource.manager.IResourceManager;
import net.minecraft.client.resource.manager.ReloadableResourceManager;
import net.minecraft.client.resource.metadata.AnimationMetadata;
import net.minecraft.client.resource.metadata.FontMetadata;
import net.minecraft.client.resource.metadata.LanguageMetadata;
import net.minecraft.client.resource.metadata.ResourceMetadataSerializerRegistry;
import net.minecraft.client.resource.metadata.ResourcePackMetadata;
import net.minecraft.client.resource.metadata.TextureResourceMetadata;
import net.minecraft.client.resource.metadata.serializer.AnimationMetadataSerializer;
import net.minecraft.client.resource.metadata.serializer.FontMetadataSerializer;
import net.minecraft.client.resource.metadata.serializer.LanguageMetadataSerializer;
import net.minecraft.client.resource.metadata.serializer.ResourcePackMetadataSerializer;
import net.minecraft.client.resource.metadata.serializer.TextureMetadataSerializer;
import net.minecraft.client.resource.model.ModelManager;
import net.minecraft.client.resource.pack.DefaultResourcePack;
import net.minecraft.client.resource.pack.ResourcePackLoader;
import net.minecraft.client.resource.skin.SkinManager;
import net.minecraft.client.sound.music.MusicManager;
import net.minecraft.client.sound.system.SoundManager;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.texture.TickableTexture;
import net.minecraft.client.twitch.ErrorTwitchStream;
import net.minecraft.client.twitch.TwitchStream;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.world.color.FoliageColorReloader;
import net.minecraft.client.world.color.GrassColorReloader;
import net.minecraft.entity.Entities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.LeadKnotEntity;
import net.minecraft.entity.decoration.PaintingEntity;
import net.minecraft.entity.living.mob.hostile.boss.BossBar;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.Connection;
import net.minecraft.network.NetworkProtocol;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.HelloC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.resource.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.stat.StatHandler;
import net.minecraft.stat.achievement.AchievementStatFormatter;
import net.minecraft.stat.achievement.Achievements;
import net.minecraft.text.LiteralText;
import net.minecraft.util.BlockableEventLoop;
import net.minecraft.util.HitResult;
import net.minecraft.util.Utils;
import net.minecraft.util.crash.CashReportCategory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.snooper.Snoopable;
import net.minecraft.util.snooper.Snooper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.WorldData;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.dimension.NetherDimension;
import net.minecraft.world.dimension.TheEndDimension;
import net.minecraft.world.storage.AnvilWorldStorageSource;
import net.minecraft.world.storage.WorldStorage;
import net.minecraft.world.storage.WorldStorageSource;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

@Environment(EnvType.CLIENT)
public class MinecraftClient implements BlockableEventLoop, Snoopable {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Identifier MOJANG_LOGO_TEXTURE = new Identifier("textures/gui/title/mojang.png");
   public static final boolean IS_MAC = Utils.getOS() == Utils.OS.MACOS;
   public static byte[] MEMORY_RESERVED_FOR_CRASH = new byte[10485760];
   private static final List DISPLAY_MODES = Lists.newArrayList(new DisplayMode[]{new DisplayMode(2560, 1600), new DisplayMode(2880, 1800)});
   private final File resourcePacksDir;
   private final PropertyMap userProperties;
   private ServerListEntry currentServerEntry;
   private TextureManager textureManager;
   private static MinecraftClient INSTANCE;
   public ClientPlayerInteractionManager interactionManager;
   private boolean fullscreen;
   private boolean f_98oemlegm;
   private boolean crashed;
   private CrashReport crashReport;
   public int width;
   public int height;
   private TickTimer timer = new TickTimer(20.0F);
   private Snooper snooper = new Snooper("client", this, MinecraftServer.getTimeMillis());
   public ClientWorld world;
   public WorldRenderer worldRenderer;
   private EntityRenderDispatcher entityRenderDispatcher;
   private ItemRenderer itemRenderer;
   private HeldItemRenderer heldItemRenderer;
   public LocalClientPlayerEntity player;
   private Entity camera;
   public Entity targetEntity;
   public ParticleManager particleManager;
   private final Session session;
   private boolean paused;
   public TextRenderer textRenderer;
   public TextRenderer shadowTextRenderer;
   public Screen currentScreen;
   public LoadingScreenRenderer loadingScreenRenderer;
   public GameRenderer gameRenderer;
   private int attackCooldown;
   private int tempWidth;
   private int tempHeight;
   private IntegratedServer server;
   public ToastGui toast;
   public GameGui gui;
   public boolean skipGameRender;
   public HitResult crosshairTarget;
   public GameOptions options;
   public MouseInput mouse;
   public final File runDir;
   private final File assetsDir;
   private final String gameVersion;
   private final Proxy proxy;
   private WorldStorageSource worldStorageSource;
   private static int currentFps;
   private int blockPlaceDelay;
   private boolean hasServerResourcePack;
   private String serverAddress;
   private int serverPort;
   public boolean focused;
   long sysTime = getTime();
   private int joinPlayerCounter;
   private final boolean is64Bit;
   private final boolean demo;
   private Connection clientConnection;
   private boolean isIntegratedServerRunning;
   public final Profiler profiler = new Profiler();
   private long f3CTime = -1L;
   private IReloadableResourceManager resourceManager;
   private final ResourceMetadataSerializerRegistry resourceMetadataSerializerRegistry = new ResourceMetadataSerializerRegistry();
   private final List defaultResourcePacks = Lists.newArrayList();
   private final DefaultResourcePack defaultResourcePack;
   private ResourcePackLoader loader;
   private LanguageManager languageManager;
   private TwitchStream twitchStream;
   private RenderTarget renderTarget;
   private SpriteAtlasTexture blocksSprite;
   private SoundManager soundManager;
   private MusicManager musicTracker;
   private Identifier f_18fgkhzjn;
   private final MinecraftSessionService sessionService;
   private SkinManager skinManager;
   private final Queue f_13gwyakrt = Queues.newArrayDeque();
   private long f_13attzqhm = 0L;
   private final Thread thread = Thread.currentThread();
   private ModelManager modelManager;
   private BlockRenderDispatcher blockRenderer;
   volatile boolean running = true;
   public String fpsDebugString = "";
   public boolean f_67hwexpgs = false;
   public boolean f_39roqfenq = false;
   public boolean f_30gmxuxbl = false;
   public boolean f_40dttiifl = true;
   long timeAtLastSecond = getTime();
   int fpsCounter;
   long timeAfterLastTick = -1L;
   private String openProfilerSection = "root";

   public MinecraftClient(RunArgs session) {
      INSTANCE = this;
      this.runDir = session.location.gameDir;
      this.assetsDir = session.location.assetsDir;
      this.resourcePacksDir = session.location.resourcePacksDir;
      this.gameVersion = session.game.version;
      this.userProperties = session.user.userProperties;
      this.defaultResourcePack = new DefaultResourcePack(new AssetIndex(session.location.assetsDir, session.location.assetIndex).m_06tewhjwt());
      this.proxy = session.user.proxy == null ? Proxy.NO_PROXY : session.user.proxy;
      this.sessionService = new YggdrasilAuthenticationService(session.user.proxy, UUID.randomUUID().toString()).createMinecraftSessionService();
      this.session = session.user.session;
      LOGGER.info("Setting user: " + this.session.getUsername());
      LOGGER.info("(Session ID is " + this.session.getSessionId() + ")");
      this.demo = session.game.demo;
      this.width = session.display.width > 0 ? session.display.width : 1;
      this.height = session.display.height > 0 ? session.display.height : 1;
      this.tempWidth = session.display.width;
      this.tempHeight = session.display.height;
      this.fullscreen = session.display.fullscreen;
      this.is64Bit = checkIs64Bit();
      this.server = new IntegratedServer(this);
      if (session.server.ip != null) {
         this.serverAddress = session.server.ip;
         this.serverPort = session.server.port;
      }

      ImageIO.setUseCache(false);
      Bootstrap.init();
   }

   public void run() {
      this.running = true;

      try {
         this.init();
      } catch (Throwable var11) {
         CrashReport var2 = CrashReport.of(var11, "Initializing game");
         var2.addCategory("Initialization");
         this.printCrashReport(this.populateCrashReport(var2));
         return;
      }

      try {
         try {
            while(this.running) {
               if (this.crashed && this.crashReport != null) {
                  this.printCrashReport(this.crashReport);
                  return;
               } else {
                  try {
                     this.runGame();
                  } catch (OutOfMemoryError var10) {
                     this.cleanHeap();
                     this.openScreen(new OutOfMemoryScreen());
                     System.gc();
                  }
               }
            }

            return;
         } catch (LoadingScreenRenderError var12) {
         } catch (CrashException var13) {
            this.populateCrashReport(var13.getReport());
            this.cleanHeap();
            LOGGER.fatal("Reported exception thrown!", var13);
            this.printCrashReport(var13.getReport());
         } catch (Throwable var14) {
            CrashReport var16 = this.populateCrashReport(new CrashReport("Unexpected error", var14));
            this.cleanHeap();
            LOGGER.fatal("Unreported exception thrown!", var14);
            this.printCrashReport(var16);
         }
      } finally {
         this.stop();
      }
   }

   private void init() {
      this.options = new GameOptions(this, this.runDir);
      this.defaultResourcePacks.add(this.defaultResourcePack);
      this.initTimerHackThread();
      if (this.options.overrideHeight > 0 && this.options.overrideWidth > 0) {
         this.width = this.options.overrideWidth;
         this.height = this.options.overrideHeight;
      }

      LOGGER.info("LWJGL Version: " + Sys.getVersion());
      this.initIcon();
      this.initDisplayMode();
      this.initDisplay();
      GLX.init();
      this.renderTarget = new RenderTarget(this.width, this.height, true);
      this.renderTarget.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      this.m_50hfsxqvn();
      this.loader = new ResourcePackLoader(
         this.resourcePacksDir, new File(this.runDir, "server-resource-packs"), this.defaultResourcePack, this.resourceMetadataSerializerRegistry, this.options
      );
      this.resourceManager = new ReloadableResourceManager(this.resourceMetadataSerializerRegistry);
      this.languageManager = new LanguageManager(this.resourceMetadataSerializerRegistry, this.options.language);
      this.resourceManager.addListener(this.languageManager);
      this.reloadResources();
      this.textureManager = new TextureManager(this.resourceManager);
      this.resourceManager.addListener(this.textureManager);
      this.renderLoadingScreen(this.textureManager);
      this.initTwitchStream();
      this.skinManager = new SkinManager(this.textureManager, new File(this.assetsDir, "skins"), this.sessionService);
      this.worldStorageSource = new AnvilWorldStorageSource(new File(this.runDir, "saves"));
      this.soundManager = new SoundManager(this.resourceManager, this.options);
      this.resourceManager.addListener(this.soundManager);
      this.musicTracker = new MusicManager(this);
      this.textRenderer = new TextRenderer(this.options, new Identifier("textures/font/ascii.png"), this.textureManager, false);
      if (this.options.language != null) {
         this.textRenderer.setUnicode(this.isUnicode());
         this.textRenderer.setRightToLeft(this.languageManager.isRightToLeft());
      }

      this.shadowTextRenderer = new TextRenderer(this.options, new Identifier("textures/font/ascii_sga.png"), this.textureManager, false);
      this.resourceManager.addListener(this.textRenderer);
      this.resourceManager.addListener(this.shadowTextRenderer);
      this.resourceManager.addListener(new GrassColorReloader());
      this.resourceManager.addListener(new FoliageColorReloader());
      Achievements.OPEN_INVENTORY.setFormatter(new AchievementStatFormatter() {
         @Override
         public String format(String value) {
            try {
               return String.format(value, GameOptions.getKeyName(MinecraftClient.this.options.inventoryKey.getKeyCode()));
            } catch (Exception var3) {
               return "Error: " + var3.getLocalizedMessage();
            }
         }
      });
      this.mouse = new MouseInput();
      this.logGlError("Pre startup");
      GlStateManager.enableTexture();
      GlStateManager.shadeModel(7425);
      GlStateManager.clearDepth(1.0);
      GlStateManager.disableDepth();
      GlStateManager.depthFunc(515);
      GlStateManager.enableAlphaTest();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.cullFace(1029);
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      GlStateManager.matrixMode(5888);
      this.logGlError("Startup");
      this.blocksSprite = new SpriteAtlasTexture("textures");
      this.blocksSprite.setMaxTextureSize(this.options.mipmapLevels);
      this.textureManager.register(SpriteAtlasTexture.BLOCK_ATLAS_BLOCKS, (TickableTexture)this.blocksSprite);
      this.textureManager.bind(SpriteAtlasTexture.BLOCK_ATLAS_BLOCKS);
      this.blocksSprite.m_21vhhelxf(false, this.options.mipmapLevels > 0);
      this.modelManager = new ModelManager(this.blocksSprite);
      this.resourceManager.addListener(this.modelManager);
      this.itemRenderer = new ItemRenderer(this.textureManager, this.modelManager);
      this.entityRenderDispatcher = new EntityRenderDispatcher(this.textureManager, this.itemRenderer);
      this.heldItemRenderer = new HeldItemRenderer(this);
      this.resourceManager.addListener(this.itemRenderer);
      this.gameRenderer = new GameRenderer(this, this.resourceManager);
      this.resourceManager.addListener(this.gameRenderer);
      this.blockRenderer = new BlockRenderDispatcher(this.modelManager.getModelShaper(), this.options);
      this.resourceManager.addListener(this.blockRenderer);
      this.worldRenderer = new WorldRenderer(this);
      this.resourceManager.addListener(this.worldRenderer);
      this.toast = new ToastGui(this);
      GlStateManager.viewport(0, 0, this.width, this.height);
      this.particleManager = new ParticleManager(this.world, this.textureManager);
      this.logGlError("Post startup");
      this.gui = new GameGui(this);
      if (this.serverAddress != null) {
         this.openScreen(new ConnectScreen(new TitleScreen(), this, this.serverAddress, this.serverPort));
      } else {
         this.openScreen(new TitleScreen());
      }

      this.textureManager.close(this.f_18fgkhzjn);
      this.f_18fgkhzjn = null;
      this.loadingScreenRenderer = new LoadingScreenRenderer(this);
      if (this.options.fullscreen && !this.fullscreen) {
         this.toggleFullscreen();
      }

      try {
         Display.setVSyncEnabled(this.options.vsync);
      } catch (OpenGLException var2) {
         this.options.vsync = false;
         this.options.save();
      }

      this.worldRenderer.loadPostChain();
   }

   private void m_50hfsxqvn() {
      this.resourceMetadataSerializerRegistry.register(new TextureMetadataSerializer(), TextureResourceMetadata.class);
      this.resourceMetadataSerializerRegistry.register(new FontMetadataSerializer(), FontMetadata.class);
      this.resourceMetadataSerializerRegistry.register(new AnimationMetadataSerializer(), AnimationMetadata.class);
      this.resourceMetadataSerializerRegistry.register(new ResourcePackMetadataSerializer(), ResourcePackMetadata.class);
      this.resourceMetadataSerializerRegistry.register(new LanguageMetadataSerializer(), LanguageMetadata.class);
   }

   private void initTwitchStream() {
      try {
         this.twitchStream = new C_48kamxasz(this, (Property)Iterables.getFirst(this.userProperties.get("twitch_access_token"), null));
      } catch (Throwable var2) {
         this.twitchStream = new ErrorTwitchStream(var2);
         LOGGER.error("Couldn't initialize twitch stream");
      }
   }

   private void initDisplay() {
      Display.setResizable(true);
      Display.setTitle("Minecraft 14w30c");

      try {
         Display.create(new PixelFormat().withDepthBits(24));
      } catch (LWJGLException var4) {
         LOGGER.error("Couldn't set pixel format", var4);

         try {
            Thread.sleep(1000L);
         } catch (InterruptedException var3) {
         }

         if (this.fullscreen) {
            this.updateDisplayMode();
         }

         Display.create();
      }
   }

   private void initDisplayMode() {
      if (this.fullscreen) {
         Display.setFullscreen(true);
         DisplayMode var1 = Display.getDisplayMode();
         this.width = Math.max(1, var1.getWidth());
         this.height = Math.max(1, var1.getHeight());
      } else {
         Display.setDisplayMode(new DisplayMode(this.width, this.height));
      }
   }

   private void initIcon() {
      Utils.OS var1 = Utils.getOS();
      if (var1 != Utils.OS.MACOS) {
         InputStream var2 = null;
         InputStream var3 = null;

         try {
            var2 = this.defaultResourcePack.getCachedResource(new Identifier("icons/icon_16x16.png"));
            var3 = this.defaultResourcePack.getCachedResource(new Identifier("icons/icon_32x32.png"));
            if (var2 != null && var3 != null) {
               Display.setIcon(new ByteBuffer[]{this.readImageBuffer(var2), this.readImageBuffer(var3)});
            }
         } catch (IOException var8) {
            LOGGER.error("Couldn't set icon", var8);
         } finally {
            IOUtils.closeQuietly(var2);
            IOUtils.closeQuietly(var3);
         }
      }
   }

   private static boolean checkIs64Bit() {
      String[] var0 = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};

      for(String var4 : var0) {
         String var5 = System.getProperty(var4);
         if (var5 != null && var5.contains("64")) {
            return true;
         }
      }

      return false;
   }

   public RenderTarget getRenderTarget() {
      return this.renderTarget;
   }

   public String getGameVersion() {
      return this.gameVersion;
   }

   private void initTimerHackThread() {
      Thread var1 = new Thread("Timer hack thread") {
         @Override
         public void run() {
            while(MinecraftClient.this.running) {
               try {
                  Thread.sleep(2147483647L);
               } catch (InterruptedException var2) {
               }
            }
         }
      };
      var1.setDaemon(true);
      var1.start();
   }

   public void crash(CrashReport crashReport) {
      this.crashed = true;
      this.crashReport = crashReport;
   }

   public void printCrashReport(CrashReport crashReport) {
      File var2 = new File(getInstance().runDir, "crash-reports");
      File var3 = new File(var2, "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-client.txt");
      System.out.println(crashReport.buildReport());
      if (crashReport.getFile() != null) {
         System.out.println("#@!@# Game crashed! Crash report saved to: #@!@# " + crashReport.getFile());
         System.exit(-1);
      } else if (crashReport.writeToFile(var3)) {
         System.out.println("#@!@# Game crashed! Crash report saved to: #@!@# " + var3.getAbsolutePath());
         System.exit(-1);
      } else {
         System.out.println("#@?@# Game crashed! Crash report could not be saved. #@?@#");
         System.exit(-2);
      }
   }

   public boolean isUnicode() {
      return this.languageManager.isUnicode() || this.options.forceUnicodeFont;
   }

   public void reloadResources() {
      ArrayList var1 = Lists.newArrayList(this.defaultResourcePacks);

      for(ResourcePackLoader.Entry var3 : this.loader.getAppliedResourcePacks()) {
         var1.add(var3.getResourcePack());
      }

      if (this.loader.getServerResourcePack() != null) {
         var1.add(this.loader.getServerResourcePack());
      }

      try {
         this.resourceManager.reload(var1);
      } catch (RuntimeException var4) {
         LOGGER.info("Caught error stitching, removing all assigned resourcepacks", var4);
         var1.clear();
         var1.addAll(this.defaultResourcePacks);
         this.loader.applyResourcePacks(Collections.emptyList());
         this.resourceManager.reload(var1);
         this.options.resourcePacks.clear();
         this.options.save();
      }

      this.languageManager.reload(var1);
      if (this.worldRenderer != null) {
         this.worldRenderer.reload();
      }
   }

   private ByteBuffer readImageBuffer(InputStream file) {
      BufferedImage var2 = ImageIO.read(file);
      int[] var3 = var2.getRGB(0, 0, var2.getWidth(), var2.getHeight(), null, 0, var2.getWidth());
      ByteBuffer var4 = ByteBuffer.allocate(4 * var3.length);

      for(int var8 : var3) {
         var4.putInt(var8 << 8 | var8 >> 24 & 0xFF);
      }

      ((Buffer)var4).flip();
      return var4;
   }

   private void updateDisplayMode() {
      HashSet var1 = Sets.newHashSet();
      Collections.addAll(var1, Display.getAvailableDisplayModes());
      DisplayMode var2 = Display.getDesktopDisplayMode();
      if (!var1.contains(var2) && Utils.getOS() == Utils.OS.MACOS) {
         for(DisplayMode var4 : DISPLAY_MODES) {
            boolean var5 = true;

            for(DisplayMode var7 : var1) {
               if (var7.getBitsPerPixel() == 32 && var7.getWidth() == var4.getWidth() && var7.getHeight() == var4.getHeight()) {
                  var5 = false;
                  break;
               }
            }

            if (!var5) {
               for(DisplayMode var9 : var1) {
                  if (var9.getBitsPerPixel() == 32 && var9.getWidth() == var4.getWidth() / 2 && var9.getHeight() == var4.getHeight() / 2) {
                     var2 = var9;
                     break;
                  }
               }
            }
         }
      }

      Display.setDisplayMode(var2);
      this.width = var2.getWidth();
      this.height = var2.getHeight();
   }

   private void renderLoadingScreen(TextureManager textureManager) {
      Window var2 = new Window(this, this.width, this.height);
      int var3 = var2.getScale();
      RenderTarget var4 = new RenderTarget(var2.getWidth() * var3, var2.getHeight() * var3, true);
      var4.bindWrite(false);
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      GlStateManager.ortho(0.0, (double)var2.getWidth(), (double)var2.getHeight(), 0.0, 1000.0, 3000.0);
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      GlStateManager.translatef(0.0F, 0.0F, -2000.0F);
      GlStateManager.disableLighting();
      GlStateManager.disableFog();
      GlStateManager.enableDepth();
      GlStateManager.enableTexture();
      InputStream var5 = null;

      try {
         var5 = this.defaultResourcePack.getResource(MOJANG_LOGO_TEXTURE);
         this.f_18fgkhzjn = textureManager.register("logo", new NativeImageBackedTexture(ImageIO.read(var5)));
         textureManager.bind(this.f_18fgkhzjn);
      } catch (IOException var12) {
         LOGGER.error("Unable to load logo: " + MOJANG_LOGO_TEXTURE, var12);
      } finally {
         IOUtils.closeQuietly(var5);
      }

      Tessellator var6 = Tessellator.getInstance();
      BufferBuilder var7 = var6.getBufferBuilder();
      var7.start();
      var7.color(16777215);
      var7.vertex(0.0, (double)this.height, 0.0, 0.0, 0.0);
      var7.vertex((double)this.width, (double)this.height, 0.0, 0.0, 0.0);
      var7.vertex((double)this.width, 0.0, 0.0, 0.0, 0.0);
      var7.vertex(0.0, 0.0, 0.0, 0.0, 0.0);
      var6.end();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      var7.color(16777215);
      short var8 = 256;
      short var9 = 256;
      this.renderMojangLogo((var2.getWidth() - var8) / 2, (var2.getHeight() - var9) / 2, 0, 0, var8, var9);
      GlStateManager.disableLighting();
      GlStateManager.disableFog();
      var4.unbindWrite();
      var4.draw(var2.getWidth() * var3, var2.getHeight() * var3);
      GlStateManager.enableAlphaTest();
      GlStateManager.alphaFunc(516, 0.1F);
      this.updateDisplay();
   }

   public void renderMojangLogo(int x, int y, int u, int v, int width, int height) {
      float var7 = 0.00390625F;
      float var8 = 0.00390625F;
      BufferBuilder var9 = Tessellator.getInstance().getBufferBuilder();
      var9.start();
      var9.vertex((double)(x + 0), (double)(y + height), 0.0, (double)((float)(u + 0) * var7), (double)((float)(v + height) * var8));
      var9.vertex((double)(x + width), (double)(y + height), 0.0, (double)((float)(u + width) * var7), (double)((float)(v + height) * var8));
      var9.vertex((double)(x + width), (double)(y + 0), 0.0, (double)((float)(u + width) * var7), (double)((float)(v + 0) * var8));
      var9.vertex((double)(x + 0), (double)(y + 0), 0.0, (double)((float)(u + 0) * var7), (double)((float)(v + 0) * var8));
      Tessellator.getInstance().end();
   }

   public WorldStorageSource getWorldStorageSource() {
      return this.worldStorageSource;
   }

   public void openScreen(Screen screen) {
      if (this.currentScreen != null) {
         this.currentScreen.removed();
      }

      if (screen == null && this.world == null) {
         screen = new TitleScreen();
      } else if (screen == null && this.player.getHealth() <= 0.0F) {
         screen = new DeathScreen();
      }

      if (screen instanceof TitleScreen) {
         this.options.debugEnabled = false;
         this.gui.getChat().clear();
      }

      this.currentScreen = screen;
      if (screen != null) {
         this.grabMouse();
         Window var2 = new Window(this, this.width, this.height);
         int var3 = var2.getWidth();
         int var4 = var2.getHeight();
         screen.init(this, var3, var4);
         this.skipGameRender = false;
      } else {
         this.soundManager.resume();
         this.closeScreen();
      }
   }

   private void logGlError(String message) {
      if (this.f_98oemlegm) {
         int var2 = GL11.glGetError();
         if (var2 != 0) {
            String var3 = GLU.gluErrorString(var2);
            LOGGER.error("########## GL ERROR ##########");
            LOGGER.error("@ " + message);
            LOGGER.error(var2 + ": " + var3);
         }
      }
   }

   public void stop() {
      try {
         this.twitchStream.m_08tceactq();
         LOGGER.info("Stopping!");

         try {
            this.setWorld(null);
         } catch (Throwable var5) {
         }

         this.soundManager.close();
      } finally {
         Display.destroy();
         if (!this.crashed) {
            System.exit(0);
         }
      }

      System.gc();
   }

   private void runGame() {
      this.profiler.push("root");
      if (Display.isCreated() && Display.isCloseRequested()) {
         this.scheduleStop();
      }

      if (this.paused && this.world != null) {
         float var1 = this.timer.partialTick;
         this.timer.advance();
         this.timer.partialTick = var1;
      } else {
         this.timer.advance();
      }

      if ((this.world == null || this.currentScreen == null) && this.hasServerResourcePack) {
         this.hasServerResourcePack = false;
         this.reloadResources();
      }

      long var5 = System.nanoTime();
      this.profiler.push("tick");

      for(int var3 = 0; var3 < this.timer.ticksThisFrame; ++var3) {
         this.tick();
      }

      this.profiler.swap("preRenderErrors");
      long var6 = System.nanoTime() - var5;
      this.logGlError("Pre render");
      this.profiler.swap("sound");
      this.soundManager.updateListener(this.player, this.timer.partialTick);
      this.profiler.pop();
      this.profiler.push("render");
      GlStateManager.pushMatrix();
      GlStateManager.clear(16640);
      this.renderTarget.bindWrite(true);
      this.profiler.push("display");
      GlStateManager.enableTexture();
      if (this.player != null && this.player.isInWall()) {
         this.options.perspective = 0;
      }

      this.profiler.pop();
      if (!this.skipGameRender) {
         this.profiler.swap("gameRenderer");
         this.gameRenderer.render(this.timer.partialTick);
         this.profiler.pop();
      }

      this.profiler.pop();
      if (!Display.isActive() && this.fullscreen) {
         this.toggleFullscreen();
      }

      if (this.options.debugEnabled && this.options.debugProfilerEnabled) {
         if (!this.profiler.isProfiling) {
            this.profiler.reset();
         }

         this.profiler.isProfiling = true;
         this.renderProfilerChart(var6);
      } else {
         this.profiler.isProfiling = false;
         this.timeAfterLastTick = System.nanoTime();
      }

      this.toast.tick();
      this.renderTarget.unbindWrite();
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      this.renderTarget.draw(this.width, this.height);
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      this.gameRenderer.m_85ssvlslu(this.timer.partialTick);
      GlStateManager.popMatrix();
      this.profiler.push("root");
      this.updateDisplay();
      Thread.yield();
      this.profiler.push("stream");
      this.profiler.push("update");
      this.twitchStream.m_26lsqwmff();
      this.profiler.swap("submit");
      this.twitchStream.m_81hdmzdjh();
      this.profiler.pop();
      this.profiler.pop();
      this.logGlError("Post render");
      ++this.fpsCounter;
      this.paused = this.isInSingleplayer() && this.currentScreen != null && this.currentScreen.shouldPauseGame() && !this.server.isPublished();

      while(getTime() >= this.timeAtLastSecond + 1000L) {
         currentFps = this.fpsCounter;
         this.fpsDebugString = String.format(
            "%d fps (%d chunk update%s) T: %s%s%s%s%s",
            currentFps,
            ChunkBlockRenderer.currentChunkUpdates,
            ChunkBlockRenderer.currentChunkUpdates != 1 ? "s" : "",
            (float)this.options.frameLimit == GameOptions.Option.FRAMERATE_LIMIT.getMax() ? "inf" : this.options.frameLimit,
            this.options.vsync ? " vsync" : "",
            this.options.fancyGraphics ? "" : " fast",
            this.options.renderClouds ? " clouds" : "",
            GLX.useVbo() ? " vbo" : ""
         );
         ChunkBlockRenderer.currentChunkUpdates = 0;
         this.timeAtLastSecond += 1000L;
         this.fpsCounter = 0;
         this.snooper.addCpuInfo();
         if (!this.snooper.isActive()) {
            this.snooper.startSnooping();
         }
      }

      if (this.isFramerateValid()) {
         this.profiler.push("fpslimit_wait");
         Display.sync(this.getMaxFramerate());
         this.profiler.pop();
      }

      this.profiler.pop();
   }

   public void updateDisplay() {
      this.profiler.push("display_update");
      Display.update();
      this.profiler.pop();
      this.updateWindow();
   }

   protected void updateWindow() {
      if (!this.fullscreen && Display.wasResized()) {
         int var1 = this.width;
         int var2 = this.height;
         this.width = Display.getWidth();
         this.height = Display.getHeight();
         if (this.width != var1 || this.height != var2) {
            if (this.width <= 0) {
               this.width = 1;
            }

            if (this.height <= 0) {
               this.height = 1;
            }

            this.onResolutionChanged(this.width, this.height);
         }
      }
   }

   public int getMaxFramerate() {
      return this.world == null && this.currentScreen != null ? 30 : this.options.frameLimit;
   }

   public boolean isFramerateValid() {
      return (float)this.getMaxFramerate() < GameOptions.Option.FRAMERATE_LIMIT.getMax();
   }

   public void cleanHeap() {
      try {
         MEMORY_RESERVED_FOR_CRASH = new byte[0];
         this.worldRenderer.m_18qvuyrzr();
      } catch (Throwable var3) {
      }

      try {
         System.gc();
         this.setWorld(null);
      } catch (Throwable var2) {
      }

      System.gc();
   }

   private void selectProfilerChartSection(int section) {
      List var2 = this.profiler.getResults(this.openProfilerSection);
      if (var2 != null && !var2.isEmpty()) {
         Profiler.Result var3 = (Profiler.Result)var2.remove(0);
         if (section == 0) {
            if (var3.location.length() > 0) {
               int var4 = this.openProfilerSection.lastIndexOf(".");
               if (var4 >= 0) {
                  this.openProfilerSection = this.openProfilerSection.substring(0, var4);
               }
            }
         } else {
            --section;
            if (section < var2.size() && !((Profiler.Result)var2.get(section)).location.equals("unspecified")) {
               if (this.openProfilerSection.length() > 0) {
                  this.openProfilerSection = this.openProfilerSection + ".";
               }

               this.openProfilerSection = this.openProfilerSection + ((Profiler.Result)var2.get(section)).location;
            }
         }
      }
   }

   private void renderProfilerChart(long tickTime) {
      if (this.profiler.isProfiling) {
         List var3 = this.profiler.getResults(this.openProfilerSection);
         Profiler.Result var4 = (Profiler.Result)var3.remove(0);
         GlStateManager.clear(256);
         GlStateManager.matrixMode(5889);
         GlStateManager.enableColorMaterial();
         GlStateManager.loadIdentity();
         GlStateManager.ortho(0.0, (double)this.width, (double)this.height, 0.0, 1000.0, 3000.0);
         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
         GlStateManager.translatef(0.0F, 0.0F, -2000.0F);
         GL11.glLineWidth(1.0F);
         GlStateManager.disableTexture();
         Tessellator var5 = Tessellator.getInstance();
         BufferBuilder var6 = var5.getBufferBuilder();
         short var7 = 160;
         int var8 = this.width - var7 - 10;
         int var9 = this.height - var7 * 2;
         GlStateManager.disableBlend();
         var6.start();
         var6.color(0, 200);
         var6.vertex((double)((float)var8 - (float)var7 * 1.1F), (double)((float)var9 - (float)var7 * 0.6F - 16.0F), 0.0);
         var6.vertex((double)((float)var8 - (float)var7 * 1.1F), (double)(var9 + var7 * 2), 0.0);
         var6.vertex((double)((float)var8 + (float)var7 * 1.1F), (double)(var9 + var7 * 2), 0.0);
         var6.vertex((double)((float)var8 + (float)var7 * 1.1F), (double)((float)var9 - (float)var7 * 0.6F - 16.0F), 0.0);
         var5.end();
         GlStateManager.enableBlend();
         double var10 = 0.0;

         for(int var12 = 0; var12 < var3.size(); ++var12) {
            Profiler.Result var13 = (Profiler.Result)var3.get(var12);
            int var14 = MathHelper.floor(var13.percentageOfParent / 4.0) + 1;
            var6.start(6);
            var6.color(var13.hashCode());
            var6.vertex((double)var8, (double)var9, 0.0);

            for(int var15 = var14; var15 >= 0; --var15) {
               float var16 = (float)((var10 + var13.percentageOfParent * (double)var15 / (double)var14) * (float) Math.PI * 2.0 / 100.0);
               float var17 = MathHelper.sin(var16) * (float)var7;
               float var18 = MathHelper.cos(var16) * (float)var7 * 0.5F;
               var6.vertex((double)((float)var8 + var17), (double)((float)var9 - var18), 0.0);
            }

            var5.end();
            var6.start(5);
            var6.color((var13.hashCode() & 16711422) >> 1);

            for(int var26 = var14; var26 >= 0; --var26) {
               float var32 = (float)((var10 + var13.percentageOfParent * (double)var26 / (double)var14) * (float) Math.PI * 2.0 / 100.0);
               float var33 = MathHelper.sin(var32) * (float)var7;
               float var34 = MathHelper.cos(var32) * (float)var7 * 0.5F;
               var6.vertex((double)((float)var8 + var33), (double)((float)var9 - var34), 0.0);
               var6.vertex((double)((float)var8 + var33), (double)((float)var9 - var34 + 10.0F), 0.0);
            }

            var5.end();
            var10 += var13.percentageOfParent;
         }

         DecimalFormat var19 = new DecimalFormat("##0.00");
         GlStateManager.enableTexture();
         String var20 = "";
         if (!var4.location.equals("unspecified")) {
            var20 = var20 + "[0] ";
         }

         if (var4.location.length() == 0) {
            var20 = var20 + "ROOT ";
         } else {
            var20 = var20 + var4.location + " ";
         }

         int var24 = 16777215;
         this.textRenderer.drawWithShadow(var20, (float)(var8 - var7), (float)(var9 - var7 / 2 - 16), var24);
         this.textRenderer
            .drawWithShadow(
               var20 = var19.format(var4.percentageOfTotal) + "%",
               (float)(var8 + var7 - this.textRenderer.getStringWidth(var20)),
               (float)(var9 - var7 / 2 - 16),
               var24
            );

         for(int var23 = 0; var23 < var3.size(); ++var23) {
            Profiler.Result var25 = (Profiler.Result)var3.get(var23);
            String var27 = "";
            if (var25.location.equals("unspecified")) {
               var27 = var27 + "[?] ";
            } else {
               var27 = var27 + "[" + (var23 + 1) + "] ";
            }

            var27 = var27 + var25.location;
            this.textRenderer.drawWithShadow(var27, (float)(var8 - var7), (float)(var9 + var7 / 2 + var23 * 8 + 20), var25.hashCode());
            this.textRenderer
               .drawWithShadow(
                  var27 = var19.format(var25.percentageOfParent) + "%",
                  (float)(var8 + var7 - 50 - this.textRenderer.getStringWidth(var27)),
                  (float)(var9 + var7 / 2 + var23 * 8 + 20),
                  var25.hashCode()
               );
            this.textRenderer
               .drawWithShadow(
                  var27 = var19.format(var25.percentageOfTotal) + "%",
                  (float)(var8 + var7 - this.textRenderer.getStringWidth(var27)),
                  (float)(var9 + var7 / 2 + var23 * 8 + 20),
                  var25.hashCode()
               );
         }
      }
   }

   public void scheduleStop() {
      this.running = false;
   }

   public void closeScreen() {
      if (Display.isActive()) {
         if (!this.focused) {
            this.focused = true;
            this.mouse.lock();
            this.openScreen(null);
            this.attackCooldown = 10000;
         }
      }
   }

   public void grabMouse() {
      if (this.focused) {
         KeyBinding.resetAll();
         this.focused = false;
         this.mouse.unlock();
      }
   }

   public void openGameMenuScreen() {
      if (this.currentScreen == null) {
         this.openScreen(new GameMenuScreen());
         if (this.isInSingleplayer() && !this.server.isPublished()) {
            this.soundManager.pause();
         }
      }
   }

   private void handleBlockMining(boolean holdingAttack) {
      if (!holdingAttack) {
         this.attackCooldown = 0;
      }

      if (this.attackCooldown <= 0) {
         if (holdingAttack && this.crosshairTarget != null && this.crosshairTarget.type == HitResult.Type.BLOCK) {
            BlockPos var2 = this.crosshairTarget.getBlockPos();
            if (this.world.getBlockState(var2).getBlock().getMaterial() != Material.AIR
               && this.interactionManager.updateBlockMining(var2, this.crosshairTarget.face)) {
               this.particleManager.addBlockMiningParticles(var2, this.crosshairTarget.face);
               this.player.swingHand();
            }
         } else {
            this.interactionManager.stopMiningBlock();
         }
      }
   }

   private void doAttack() {
      if (this.attackCooldown <= 0) {
         this.player.swingHand();
         if (this.crosshairTarget == null) {
            LOGGER.error("Null returned as 'hitResult', this shouldn't happen!");
            if (this.interactionManager.hasAttackCooldown()) {
               this.attackCooldown = 10;
            }
         } else {
            switch(this.crosshairTarget.type) {
               case ENTITY:
                  this.interactionManager.attackEntity(this.player, this.crosshairTarget.entity);
                  break;
               case BLOCK:
                  BlockPos var1 = this.crosshairTarget.getBlockPos();
                  if (this.world.getBlockState(var1).getBlock().getMaterial() != Material.AIR) {
                     this.interactionManager.startMiningBlock(var1, this.crosshairTarget.face);
                     break;
                  }
               case MISS:
               default:
                  if (this.interactionManager.hasAttackCooldown()) {
                     this.attackCooldown = 10;
                  }
            }
         }
      }
   }

   private void doUse() {
      this.blockPlaceDelay = 4;
      boolean var1 = true;
      ItemStack var2 = this.player.inventory.getMainHandStack();
      if (this.crosshairTarget == null) {
         LOGGER.warn("Null returned as 'hitResult', this shouldn't happen!");
      } else {
         switch(this.crosshairTarget.type) {
            case ENTITY:
               if (this.interactionManager.interactEntity(this.player, this.crosshairTarget.entity)) {
                  var1 = false;
               }
               break;
            case BLOCK:
               BlockPos var3 = this.crosshairTarget.getBlockPos();
               if (this.world.getBlockState(var3).getBlock().getMaterial() != Material.AIR) {
                  int var4 = var2 != null ? var2.size : 0;
                  if (this.interactionManager.interactBlock(this.player, this.world, var2, var3, this.crosshairTarget.face, this.crosshairTarget.pos)) {
                     var1 = false;
                     this.player.swingHand();
                  }

                  if (var2 == null) {
                     return;
                  }

                  if (var2.size == 0) {
                     this.player.inventory.inventorySlots[this.player.inventory.selectedSlot] = null;
                  } else if (var2.size != var4 || this.interactionManager.hasCreativeInventory()) {
                     this.gameRenderer.heldItemRenderer.resetSwapAnimation();
                  }
               }
         }
      }

      if (var1) {
         ItemStack var5 = this.player.inventory.getMainHandStack();
         if (var5 != null && this.interactionManager.useItem(this.player, this.world, var5)) {
            this.gameRenderer.heldItemRenderer.resetSwapAnimation2();
         }
      }
   }

   public void toggleFullscreen() {
      try {
         this.fullscreen = !this.fullscreen;
         this.options.fullscreen = this.fullscreen;
         if (this.fullscreen) {
            this.updateDisplayMode();
            this.width = Display.getDisplayMode().getWidth();
            this.height = Display.getDisplayMode().getHeight();
            if (this.width <= 0) {
               this.width = 1;
            }

            if (this.height <= 0) {
               this.height = 1;
            }
         } else {
            Display.setDisplayMode(new DisplayMode(this.tempWidth, this.tempHeight));
            this.width = this.tempWidth;
            this.height = this.tempHeight;
            if (this.width <= 0) {
               this.width = 1;
            }

            if (this.height <= 0) {
               this.height = 1;
            }
         }

         if (this.currentScreen != null) {
            this.onResolutionChanged(this.width, this.height);
         } else {
            this.onResolutionChanged();
         }

         Display.setFullscreen(this.fullscreen);
         Display.setVSyncEnabled(this.options.vsync);
         this.updateDisplay();
      } catch (Exception var2) {
         LOGGER.error("Couldn't toggle fullscreen", var2);
      }
   }

   private void onResolutionChanged(int width, int height) {
      this.width = Math.max(1, width);
      this.height = Math.max(1, height);
      if (this.currentScreen != null) {
         Window var3 = new Window(this, width, height);
         this.currentScreen.resize(this, var3.getWidth(), var3.getHeight());
      }

      this.loadingScreenRenderer = new LoadingScreenRenderer(this);
      this.onResolutionChanged();
   }

   private void onResolutionChanged() {
      this.renderTarget.resize(this.width, this.height);
      if (this.gameRenderer != null) {
         this.gameRenderer.onResolutionChanged(this.width, this.height);
      }
   }

   public void tick() {
      this.profiler.push("scheduledExecutables");
      synchronized(this.f_13gwyakrt) {
         while(!this.f_13gwyakrt.isEmpty()) {
            ((FutureTask)this.f_13gwyakrt.poll()).run();
         }
      }

      this.profiler.pop();
      if (this.blockPlaceDelay > 0) {
         --this.blockPlaceDelay;
      }

      this.profiler.push("gui");
      if (!this.paused) {
         this.gui.tick();
      }

      this.profiler.pop();
      this.gameRenderer.updateTargetEntity(1.0F);
      this.profiler.push("gameMode");
      if (!this.paused && this.world != null) {
         this.interactionManager.tick();
      }

      this.profiler.swap("textures");
      if (!this.paused) {
         this.textureManager.tick();
      }

      if (this.currentScreen == null && this.player != null) {
         if (this.player.getHealth() <= 0.0F) {
            this.openScreen(null);
         } else if (this.player.isSleeping() && this.world != null) {
            this.openScreen(new SleepingChatScreen());
         }
      } else if (this.currentScreen != null && this.currentScreen instanceof SleepingChatScreen && !this.player.isSleeping()) {
         this.openScreen(null);
      }

      if (this.currentScreen != null) {
         this.attackCooldown = 10000;
      }

      if (this.currentScreen != null) {
         try {
            this.currentScreen.handleInputs();
         } catch (Throwable var7) {
            CrashReport var2 = CrashReport.of(var7, "Updating screen events");
            CashReportCategory var3 = var2.addCategory("Affected screen");
            var3.add("Screen name", new Callable() {
               public String call() {
                  return MinecraftClient.this.currentScreen.getClass().getCanonicalName();
               }
            });
            throw new CrashException(var2);
         }

         if (this.currentScreen != null) {
            try {
               this.currentScreen.tick();
            } catch (Throwable var6) {
               CrashReport var15 = CrashReport.of(var6, "Ticking screen");
               CashReportCategory var18 = var15.addCategory("Affected screen");
               var18.add("Screen name", new Callable() {
                  public String call() {
                     return MinecraftClient.this.currentScreen.getClass().getCanonicalName();
                  }
               });
               throw new CrashException(var15);
            }
         }
      }

      if (this.currentScreen == null || this.currentScreen.passEvents) {
         this.profiler.swap("mouse");

         while(Mouse.next()) {
            int var10 = Mouse.getEventButton();
            KeyBinding.setKeyPressed(var10 - 100, Mouse.getEventButtonState());
            if (Mouse.getEventButtonState()) {
               if (this.player.isSpectator() && var10 == 2) {
                  this.gui.getSpectatorGui().mouseMiddleClicked();
               } else {
                  KeyBinding.onKeyPressed(var10 - 100);
               }
            }

            long var16 = getTime() - this.sysTime;
            if (var16 <= 200L) {
               int var4 = Mouse.getEventDWheel();
               if (var4 != 0) {
                  if (this.player.isSpectator()) {
                     var4 = var4 < 0 ? -1 : 1;
                     if (this.gui.getSpectatorGui().isMenuActive()) {
                        this.gui.getSpectatorGui().mouseScrolled(-var4);
                     } else {
                        float var5 = MathHelper.clamp(this.player.abilities.getFlySpeed() + (float)var4 * 0.005F, 0.0F, 0.2F);
                        this.player.abilities.setFlySpeed(var5);
                     }
                  } else {
                     this.player.inventory.scrollInHotbar(var4);
                  }
               }

               if (this.currentScreen == null) {
                  if (!this.focused && Mouse.getEventButtonState()) {
                     this.closeScreen();
                  }
               } else if (this.currentScreen != null) {
                  this.currentScreen.handleMouse();
               }
            }
         }

         if (this.attackCooldown > 0) {
            --this.attackCooldown;
         }

         this.profiler.swap("keyboard");

         while(Keyboard.next()) {
            KeyBinding.setKeyPressed(Keyboard.getEventKey(), Keyboard.getEventKeyState());
            if (Keyboard.getEventKeyState()) {
               KeyBinding.onKeyPressed(Keyboard.getEventKey());
            }

            if (this.f3CTime > 0L) {
               if (getTime() - this.f3CTime >= 6000L) {
                  throw new CrashException(new CrashReport("Manually triggered debug crash", new Throwable()));
               }

               if (!Keyboard.isKeyDown(46) || !Keyboard.isKeyDown(61)) {
                  this.f3CTime = -1L;
               }
            } else if (Keyboard.isKeyDown(46) && Keyboard.isKeyDown(61)) {
               this.f3CTime = getTime();
            }

            this.handleKeyBindings();
            if (Keyboard.getEventKeyState()) {
               if (Keyboard.getEventKey() == 62 && this.gameRenderer != null) {
                  this.gameRenderer.disableShader();
               }

               if (this.currentScreen != null) {
                  this.currentScreen.handleKeyboard();
               } else {
                  if (Keyboard.getEventKey() == 1) {
                     this.openGameMenuScreen();
                  }

                  if (Keyboard.getEventKey() == 32 && Keyboard.isKeyDown(61) && this.gui != null) {
                     this.gui.getChat().clear();
                  }

                  if (Keyboard.getEventKey() == 31 && Keyboard.isKeyDown(61)) {
                     this.reloadResources();
                  }

                  if (Keyboard.getEventKey() == 17 && Keyboard.isKeyDown(61)) {
                  }

                  if (Keyboard.getEventKey() == 18 && Keyboard.isKeyDown(61)) {
                  }

                  if (Keyboard.getEventKey() == 47 && Keyboard.isKeyDown(61)) {
                  }

                  if (Keyboard.getEventKey() == 38 && Keyboard.isKeyDown(61)) {
                  }

                  if (Keyboard.getEventKey() == 22 && Keyboard.isKeyDown(61)) {
                  }

                  if (Keyboard.getEventKey() == 20 && Keyboard.isKeyDown(61)) {
                     this.reloadResources();
                  }

                  if (Keyboard.getEventKey() == 33 && Keyboard.isKeyDown(61)) {
                     boolean var11 = Keyboard.isKeyDown(42) | Keyboard.isKeyDown(54);
                     this.options.setValue(GameOptions.Option.RENDER_DISTANCE, var11 ? -1 : 1);
                  }

                  if (Keyboard.getEventKey() == 30 && Keyboard.isKeyDown(61)) {
                     this.worldRenderer.reload();
                  }

                  if (Keyboard.getEventKey() == 35 && Keyboard.isKeyDown(61)) {
                     this.options.advancedItemTooltips = !this.options.advancedItemTooltips;
                     this.options.save();
                  }

                  if (Keyboard.getEventKey() == 48 && Keyboard.isKeyDown(61)) {
                     this.entityRenderDispatcher.setRenderHitboxes(!this.entityRenderDispatcher.shouldRenderHitboxes());
                  }

                  if (Keyboard.getEventKey() == 25 && Keyboard.isKeyDown(61)) {
                     this.options.pauseOnUnfocus = !this.options.pauseOnUnfocus;
                     this.options.save();
                  }

                  if (Keyboard.getEventKey() == 59) {
                     this.options.hudEnabled = !this.options.hudEnabled;
                  }

                  if (Keyboard.getEventKey() == 61) {
                     this.options.debugEnabled = !this.options.debugEnabled;
                     this.options.debugProfilerEnabled = Screen.isShiftDown();
                  }

                  if (this.options.togglePerspectiveKey.wasPressed()) {
                     ++this.options.perspective;
                     if (this.options.perspective > 2) {
                        this.options.perspective = 0;
                     }

                     if (this.options.perspective == 0) {
                        this.gameRenderer.setCamera(this.getCamera());
                     } else if (this.options.perspective == 1) {
                        this.gameRenderer.setCamera(null);
                     }
                  }

                  if (this.options.smoothCameraKey.wasPressed()) {
                     this.options.smoothCamera = !this.options.smoothCamera;
                  }
               }

               if (this.options.debugEnabled && this.options.debugProfilerEnabled) {
                  if (Keyboard.getEventKey() == 11) {
                     this.selectProfilerChartSection(0);
                  }

                  for(int var12 = 0; var12 < 9; ++var12) {
                     if (Keyboard.getEventKey() == 2 + var12) {
                        this.selectProfilerChartSection(var12 + 1);
                     }
                  }
               }
            }
         }

         for(int var13 = 0; var13 < 9; ++var13) {
            if (this.options.hotbarKeys[var13].wasPressed()) {
               if (this.player.isSpectator()) {
                  this.gui.getSpectatorGui().selectSlot(var13);
               } else {
                  this.player.inventory.selectedSlot = var13;
               }
            }
         }

         boolean var14 = this.options.chatVisibility != PlayerEntity.ChatVisibility.HIDDEN;

         while(this.options.inventoryKey.wasPressed()) {
            if (this.interactionManager.hasRidingInventory()) {
               this.player.openRidingInventory();
            } else {
               this.getNetworkHandler().sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Status.OPEN_INVENTORY_ACHIEVEMENT));
               this.openScreen(new SurvivalInventoryScreen(this.player));
            }
         }

         while(this.options.dropKey.wasPressed()) {
            if (!this.player.isSpectator()) {
               this.player.dropItem(Screen.isControlDown());
            }
         }

         while(this.options.chatKey.wasPressed() && var14) {
            this.openScreen(new ChatScreen());
         }

         if (this.currentScreen == null && this.options.commandKey.wasPressed() && var14) {
            this.openScreen(new ChatScreen("/"));
         }

         if (this.player.isHoldingItem()) {
            if (!this.options.usekey.isPressed()) {
               this.interactionManager.stopUsingHand(this.player);
            }

            while(this.options.attackKey.wasPressed()) {
            }

            while(this.options.usekey.wasPressed()) {
            }

            while(this.options.pickItemKey.wasPressed()) {
            }
         } else {
            while(this.options.attackKey.wasPressed()) {
               this.doAttack();
            }

            while(this.options.usekey.wasPressed()) {
               this.doUse();
            }

            while(this.options.pickItemKey.wasPressed()) {
               this.doPick();
            }
         }

         if (this.options.usekey.isPressed() && this.blockPlaceDelay == 0 && !this.player.isHoldingItem()) {
            this.doUse();
         }

         this.handleBlockMining(this.currentScreen == null && this.options.attackKey.isPressed() && this.focused);
      }

      if (this.world != null) {
         if (this.player != null) {
            ++this.joinPlayerCounter;
            if (this.joinPlayerCounter == 30) {
               this.joinPlayerCounter = 0;
               this.world.addEntityAlways(this.player);
            }
         }

         this.profiler.swap("gameRenderer");
         if (!this.paused) {
            this.gameRenderer.tick();
         }

         this.profiler.swap("levelRenderer");
         if (!this.paused) {
            this.worldRenderer.tick();
         }

         this.profiler.swap("level");
         if (!this.paused) {
            if (this.world.getLightningCooldown() > 0) {
               this.world.setLightningCooldown(this.world.getLightningCooldown() - 1);
            }

            this.world.tickEntities();
         }
      }

      if (!this.paused) {
         this.musicTracker.tick();
         this.soundManager.tick();
      }

      if (this.world != null) {
         if (!this.paused) {
            this.world.setAllowedMobSpawns(this.world.getDifficulty() != Difficulty.PEACEFUL, true);

            try {
               this.world.tick();
            } catch (Throwable var8) {
               CrashReport var17 = CrashReport.of(var8, "Exception in world tick");
               if (this.world == null) {
                  CashReportCategory var19 = var17.addCategory("Affected level");
                  var19.add("Problem", "Level is null!");
               } else {
                  this.world.populateCrashReport(var17);
               }

               throw new CrashException(var17);
            }
         }

         this.profiler.swap("animateTick");
         if (!this.paused && this.world != null) {
            this.world.doRandomDisplayTicks(MathHelper.floor(this.player.x), MathHelper.floor(this.player.y), MathHelper.floor(this.player.z));
         }

         this.profiler.swap("particles");
         if (!this.paused) {
            this.particleManager.tick();
         }
      } else if (this.clientConnection != null) {
         this.profiler.swap("pendingConnection");
         this.clientConnection.tick();
      }

      this.profiler.pop();
      this.sysTime = getTime();
   }

   public void startGame(String worldName, String displayName, WorldSettings worldInfo) {
      this.setWorld(null);
      System.gc();
      WorldStorage var4 = this.worldStorageSource.get(worldName, false);
      WorldData var5 = var4.loadData();
      if (var5 == null && worldInfo != null) {
         var5 = new WorldData(worldInfo, worldName);
         var4.saveData(var5);
      }

      if (worldInfo == null) {
         worldInfo = new WorldSettings(var5);
      }

      try {
         this.server = new IntegratedServer(this, worldName, displayName, worldInfo);
         this.server.start();
         this.isIntegratedServerRunning = true;
      } catch (Throwable var10) {
         CrashReport var7 = CrashReport.of(var10, "Starting integrated server");
         CashReportCategory var8 = var7.addCategory("Starting integrated server");
         var8.add("Level ID", worldName);
         var8.add("Level Name", displayName);
         throw new CrashException(var7);
      }

      this.loadingScreenRenderer.updateProgress(I18n.translate("menu.loadingLevel"));

      while(!this.server.isLoading()) {
         String var6 = this.server.getServerOperation();
         if (var6 != null) {
            this.loadingScreenRenderer.setTask(I18n.translate(var6));
         } else {
            this.loadingScreenRenderer.setTask("");
         }

         try {
            Thread.sleep(200L);
         } catch (InterruptedException var9) {
         }
      }

      this.openScreen(null);
      SocketAddress var11 = this.server.getNetworkIo().bind();
      Connection var12 = Connection.connectLocal(var11);
      var12.setListener(new ClientLoginNetworkHandler(var12, this, null));
      var12.send(new HandshakeC2SPacket(31, var11.toString(), 0, NetworkProtocol.LOGIN));
      var12.send(new HelloC2SPacket(this.getSession().getProfile()));
      this.clientConnection = var12;
   }

   public void setWorld(ClientWorld world) {
      this.setWorld(world, "");
   }

   public void setWorld(ClientWorld world, String title) {
      if (world == null) {
         ClientPlayNetworkHandler var3 = this.getNetworkHandler();
         if (var3 != null) {
            var3.cleanUp();
         }

         if (this.server != null && this.server.hasGameDir()) {
            this.server.stopRunning();
            this.server.setInstance();
         }

         this.server = null;
         this.toast.clear();
         this.gameRenderer.getMapRenderer().clearStateTextures();
      }

      this.camera = null;
      this.clientConnection = null;
      if (this.loadingScreenRenderer != null) {
         this.loadingScreenRenderer.updateTitle(title);
         this.loadingScreenRenderer.setTask("");
      }

      if (world == null && this.world != null) {
         if (this.loader.getServerResourcePack() != null) {
            this.loader.removeServerResourcePack();
            this.reloadResources();
         } else {
            this.loader.removeServerResourcePack();
         }

         this.setCurrentServerEntry(null);
         this.isIntegratedServerRunning = false;
      }

      this.soundManager.stop();
      this.world = world;
      if (world != null) {
         if (this.worldRenderer != null) {
            this.worldRenderer.setWorld(world);
         }

         if (this.particleManager != null) {
            this.particleManager.setWorld(world);
         }

         if (this.player == null) {
            this.player = this.interactionManager.createPlayerEntity(world, new StatHandler());
            this.interactionManager.setFacingSouth(this.player);
         }

         this.player.postSpawn();
         world.addEntity(this.player);
         this.player.input = new GameInput(this.options);
         this.interactionManager.refreshAbilities(this.player);
         this.camera = this.player;
      } else {
         this.worldStorageSource.clearRegionIo();
         this.player = null;
      }

      System.gc();
      this.sysTime = 0L;
   }

   public void teleportToDimension(int dimensionId) {
      this.world.resetSpawnPoint();
      this.world.unloadEntities();
      int var2 = 0;
      String var3 = null;
      if (this.player != null) {
         var2 = this.player.getNetworkId();
         this.world.removeEntity(this.player);
         var3 = this.player.getServerBrand();
      }

      this.camera = null;
      LocalClientPlayerEntity var4 = this.player;
      this.player = this.interactionManager.createPlayerEntity(this.world, this.player == null ? new StatHandler() : this.player.getStatHandler());
      this.player.getDataTracker().update(var4.getDataTracker().collectEntries());
      this.player.dimensionId = dimensionId;
      this.camera = this.player;
      this.player.postSpawn();
      this.player.setServerBrand(var3);
      this.world.addEntity(this.player);
      this.interactionManager.setFacingSouth(this.player);
      this.player.input = new GameInput(this.options);
      this.player.setNetworkId(var2);
      this.interactionManager.refreshAbilities(this.player);
      this.player.setReducedDebugInfo(var4.hasReducedDebugInfo());
      if (this.currentScreen instanceof DeathScreen) {
         this.openScreen(null);
      }
   }

   public final boolean isDemo() {
      return this.demo;
   }

   public ClientPlayNetworkHandler getNetworkHandler() {
      return this.player != null ? this.player.networkHandler : null;
   }

   public static boolean isHudDisabled() {
      return INSTANCE == null || !INSTANCE.options.hudEnabled;
   }

   public static boolean isFancyGraphicsEnabled() {
      return INSTANCE != null && INSTANCE.options.fancyGraphics;
   }

   public static boolean isAmbientOcclusionEnabled() {
      return INSTANCE != null && INSTANCE.options.ambientOcclusion != 0;
   }

   private void doPick() {
      if (this.crosshairTarget != null) {
         boolean var1 = this.player.abilities.creativeMode;
         int var3 = 0;
         boolean var4 = false;
         BlockEntity var5 = null;
         Item var2;
         if (this.crosshairTarget.type == HitResult.Type.BLOCK) {
            BlockPos var6 = this.crosshairTarget.getBlockPos();
            Block var7 = this.world.getBlockState(var6).getBlock();
            if (var7.getMaterial() == Material.AIR) {
               return;
            }

            var2 = var7.getPickItem(this.world, var6);
            if (var2 == null) {
               return;
            }

            if (var1 && (Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157))) {
               var5 = this.world.getBlockEntity(var6);
            }

            Block var8 = var2 instanceof BlockItem && !var7.hasPickItemMetadata() ? Block.byItem(var2) : var7;
            var3 = var8.getPickItemMetadata(this.world, var6);
            var4 = var2.isStackable();
         } else {
            if (this.crosshairTarget.type != HitResult.Type.ENTITY || this.crosshairTarget.entity == null || !var1) {
               return;
            }

            if (this.crosshairTarget.entity instanceof PaintingEntity) {
               var2 = Items.PAINTING;
            } else if (this.crosshairTarget.entity instanceof LeadKnotEntity) {
               var2 = Items.LEAD;
            } else if (this.crosshairTarget.entity instanceof ItemFrameEntity) {
               ItemFrameEntity var11 = (ItemFrameEntity)this.crosshairTarget.entity;
               ItemStack var14 = var11.getItemStackInItemFrame();
               if (var14 == null) {
                  var2 = Items.ITEM_FRAME;
               } else {
                  var2 = var14.getItem();
                  var3 = var14.getMetadata();
                  var4 = true;
               }
            } else if (this.crosshairTarget.entity instanceof MinecartEntity) {
               MinecartEntity var12 = (MinecartEntity)this.crosshairTarget.entity;
               switch(var12.getMinecartType()) {
                  case FURNACE:
                     var2 = Items.FURNACE_MINECART;
                     break;
                  case CHEST:
                     var2 = Items.CHEST_MINECART;
                     break;
                  case TNT:
                     var2 = Items.TNT_MINECART;
                     break;
                  case HOPPER:
                     var2 = Items.HOPPER_MINECART;
                     break;
                  case COMMAND_BLOCK:
                     var2 = Items.COMMAND_BLOCK_MINECART;
                     break;
                  default:
                     var2 = Items.MINECART;
               }
            } else if (this.crosshairTarget.entity instanceof BoatEntity) {
               var2 = Items.BOAT;
            } else {
               var2 = Items.SPAWN_EGG;
               var3 = Entities.getRawId(this.crosshairTarget.entity);
               var4 = true;
               if (var3 <= 0 || !Entities.RAW_ID_TO_SPAWN_EGG_DATA.containsKey(var3)) {
                  return;
               }
            }
         }

         PlayerInventory var13 = this.player.inventory;
         if (var5 == null) {
            var13.pickItem(var2, var3, var4, var1);
         } else {
            NbtCompound var15 = new NbtCompound();
            var5.writeNbt(var15);
            ItemStack var17 = new ItemStack(var2, 1, var3);
            var17.addToNbt("BlockEntityTag", var15);
            NbtCompound var9 = new NbtCompound();
            NbtList var10 = new NbtList();
            var10.add(new NbtString("(+NBT)"));
            var9.put("Lore", var10);
            var17.addToNbt("display", var9);
            var13.setStack(var13.selectedSlot, var17);
         }

         if (var1) {
            int var16 = this.player.playerMenu.slots.size() - 9 + var13.selectedSlot;
            this.interactionManager.addStackToCreativeMenu(var13.getStack(var13.selectedSlot), var16);
         }
      }
   }

   public CrashReport populateCrashReport(CrashReport report) {
      report.getSystemDetails().add("Launched Version", new Callable() {
         public String call() {
            return MinecraftClient.this.gameVersion;
         }
      });
      report.getSystemDetails().add("LWJGL", new Callable() {
         public String call() {
            return Sys.getVersion();
         }
      });
      report.getSystemDetails().add("OpenGL", new Callable() {
         public String call() {
            return GL11.glGetString(7937) + " GL version " + GL11.glGetString(7938) + ", " + GL11.glGetString(7936);
         }
      });
      report.getSystemDetails().add("GL Caps", new Callable() {
         public String call() {
            return GLX.getGlCapsInfo();
         }
      });
      report.getSystemDetails().add("Using VBOs", new Callable() {
         public String call() {
            return MinecraftClient.this.options.useVbo ? "Yes" : "No";
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
                     return MinecraftClient.class.getSigners() == null
                        ? "Very likely; Jar signature invalidated"
                        : "Probably not. Jar signature remains and client brand is untouched.";
                  }
               }
            }
         );
      report.getSystemDetails().add("Type", new Callable() {
         public String call() {
            return "Client (map_client.txt)";
         }
      });
      report.getSystemDetails().add("Resource Packs", new Callable() {
         public String call() {
            return MinecraftClient.this.options.resourcePacks.toString();
         }
      });
      report.getSystemDetails().add("Current Language", new Callable() {
         public String call() {
            return MinecraftClient.this.languageManager.getLanguage().toString();
         }
      });
      report.getSystemDetails().add("Profiler Position", new Callable() {
         public String call() {
            return MinecraftClient.this.profiler.isProfiling ? MinecraftClient.this.profiler.getCurrentLocation() : "N/A (disabled)";
         }
      });
      if (this.world != null) {
         this.world.populateCrashReport(report);
      }

      return report;
   }

   public static MinecraftClient getInstance() {
      return INSTANCE;
   }

   public void onApplyServerResourcePack() {
      this.hasServerResourcePack = true;
   }

   @Override
   public void addSnooperInfo(Snooper snooper) {
      snooper.addToSnoopedData("fps", currentFps);
      snooper.addToSnoopedData("vsync_enabled", this.options.vsync);
      snooper.addToSnoopedData("display_frequency", Display.getDisplayMode().getFrequency());
      snooper.addToSnoopedData("display_type", this.fullscreen ? "fullscreen" : "windowed");
      snooper.addToSnoopedData("run_time", (MinecraftServer.getTimeMillis() - snooper.getSnooperInitTime()) / 60L * 1000L);
      snooper.addToSnoopedData("resource_packs", this.loader.getAppliedResourcePacks().size());
      int var2 = 0;

      for(ResourcePackLoader.Entry var4 : this.loader.getAppliedResourcePacks()) {
         snooper.addToSnoopedData("resource_pack[" + var2++ + "]", var4.getName());
      }

      if (this.server != null && this.server.getSnooper() != null) {
         snooper.addToSnoopedData("snooper_partner", this.server.getSnooper().getSnooperToken());
      }
   }

   @Override
   public void addSnooper(Snooper snooper) {
      snooper.put("opengl_version", GL11.glGetString(7938));
      snooper.put("opengl_vendor", GL11.glGetString(7936));
      snooper.put("client_brand", ClientBrandRetriever.getClientModName());
      snooper.put("launched_version", this.gameVersion);
      ContextCapabilities var2 = GLContext.getCapabilities();
      snooper.put("gl_caps[ARB_arrays_of_arrays]", var2.GL_ARB_arrays_of_arrays);
      snooper.put("gl_caps[ARB_base_instance]", var2.GL_ARB_base_instance);
      snooper.put("gl_caps[ARB_blend_func_extended]", var2.GL_ARB_blend_func_extended);
      snooper.put("gl_caps[ARB_clear_buffer_object]", var2.GL_ARB_clear_buffer_object);
      snooper.put("gl_caps[ARB_color_buffer_float]", var2.GL_ARB_color_buffer_float);
      snooper.put("gl_caps[ARB_compatibility]", var2.GL_ARB_compatibility);
      snooper.put("gl_caps[ARB_compressed_texture_pixel_storage]", var2.GL_ARB_compressed_texture_pixel_storage);
      snooper.put("gl_caps[ARB_compute_shader]", var2.GL_ARB_compute_shader);
      snooper.put("gl_caps[ARB_copy_buffer]", var2.GL_ARB_copy_buffer);
      snooper.put("gl_caps[ARB_copy_image]", var2.GL_ARB_copy_image);
      snooper.put("gl_caps[ARB_depth_buffer_float]", var2.GL_ARB_depth_buffer_float);
      snooper.put("gl_caps[ARB_compute_shader]", var2.GL_ARB_compute_shader);
      snooper.put("gl_caps[ARB_copy_buffer]", var2.GL_ARB_copy_buffer);
      snooper.put("gl_caps[ARB_copy_image]", var2.GL_ARB_copy_image);
      snooper.put("gl_caps[ARB_depth_buffer_float]", var2.GL_ARB_depth_buffer_float);
      snooper.put("gl_caps[ARB_depth_clamp]", var2.GL_ARB_depth_clamp);
      snooper.put("gl_caps[ARB_depth_texture]", var2.GL_ARB_depth_texture);
      snooper.put("gl_caps[ARB_draw_buffers]", var2.GL_ARB_draw_buffers);
      snooper.put("gl_caps[ARB_draw_buffers_blend]", var2.GL_ARB_draw_buffers_blend);
      snooper.put("gl_caps[ARB_draw_elements_base_vertex]", var2.GL_ARB_draw_elements_base_vertex);
      snooper.put("gl_caps[ARB_draw_indirect]", var2.GL_ARB_draw_indirect);
      snooper.put("gl_caps[ARB_draw_instanced]", var2.GL_ARB_draw_instanced);
      snooper.put("gl_caps[ARB_explicit_attrib_location]", var2.GL_ARB_explicit_attrib_location);
      snooper.put("gl_caps[ARB_explicit_uniform_location]", var2.GL_ARB_explicit_uniform_location);
      snooper.put("gl_caps[ARB_fragment_layer_viewport]", var2.GL_ARB_fragment_layer_viewport);
      snooper.put("gl_caps[ARB_fragment_program]", var2.GL_ARB_fragment_program);
      snooper.put("gl_caps[ARB_fragment_shader]", var2.GL_ARB_fragment_shader);
      snooper.put("gl_caps[ARB_fragment_program_shadow]", var2.GL_ARB_fragment_program_shadow);
      snooper.put("gl_caps[ARB_framebuffer_object]", var2.GL_ARB_framebuffer_object);
      snooper.put("gl_caps[ARB_framebuffer_sRGB]", var2.GL_ARB_framebuffer_sRGB);
      snooper.put("gl_caps[ARB_geometry_shader4]", var2.GL_ARB_geometry_shader4);
      snooper.put("gl_caps[ARB_gpu_shader5]", var2.GL_ARB_gpu_shader5);
      snooper.put("gl_caps[ARB_half_float_pixel]", var2.GL_ARB_half_float_pixel);
      snooper.put("gl_caps[ARB_half_float_vertex]", var2.GL_ARB_half_float_vertex);
      snooper.put("gl_caps[ARB_instanced_arrays]", var2.GL_ARB_instanced_arrays);
      snooper.put("gl_caps[ARB_map_buffer_alignment]", var2.GL_ARB_map_buffer_alignment);
      snooper.put("gl_caps[ARB_map_buffer_range]", var2.GL_ARB_map_buffer_range);
      snooper.put("gl_caps[ARB_multisample]", var2.GL_ARB_multisample);
      snooper.put("gl_caps[ARB_multitexture]", var2.GL_ARB_multitexture);
      snooper.put("gl_caps[ARB_occlusion_query2]", var2.GL_ARB_occlusion_query2);
      snooper.put("gl_caps[ARB_pixel_buffer_object]", var2.GL_ARB_pixel_buffer_object);
      snooper.put("gl_caps[ARB_seamless_cube_map]", var2.GL_ARB_seamless_cube_map);
      snooper.put("gl_caps[ARB_shader_objects]", var2.GL_ARB_shader_objects);
      snooper.put("gl_caps[ARB_shader_stencil_export]", var2.GL_ARB_shader_stencil_export);
      snooper.put("gl_caps[ARB_shader_texture_lod]", var2.GL_ARB_shader_texture_lod);
      snooper.put("gl_caps[ARB_shadow]", var2.GL_ARB_shadow);
      snooper.put("gl_caps[ARB_shadow_ambient]", var2.GL_ARB_shadow_ambient);
      snooper.put("gl_caps[ARB_stencil_texturing]", var2.GL_ARB_stencil_texturing);
      snooper.put("gl_caps[ARB_sync]", var2.GL_ARB_sync);
      snooper.put("gl_caps[ARB_tessellation_shader]", var2.GL_ARB_tessellation_shader);
      snooper.put("gl_caps[ARB_texture_border_clamp]", var2.GL_ARB_texture_border_clamp);
      snooper.put("gl_caps[ARB_texture_buffer_object]", var2.GL_ARB_texture_buffer_object);
      snooper.put("gl_caps[ARB_texture_cube_map]", var2.GL_ARB_texture_cube_map);
      snooper.put("gl_caps[ARB_texture_cube_map_array]", var2.GL_ARB_texture_cube_map_array);
      snooper.put("gl_caps[ARB_texture_non_power_of_two]", var2.GL_ARB_texture_non_power_of_two);
      snooper.put("gl_caps[ARB_uniform_buffer_object]", var2.GL_ARB_uniform_buffer_object);
      snooper.put("gl_caps[ARB_vertex_blend]", var2.GL_ARB_vertex_blend);
      snooper.put("gl_caps[ARB_vertex_buffer_object]", var2.GL_ARB_vertex_buffer_object);
      snooper.put("gl_caps[ARB_vertex_program]", var2.GL_ARB_vertex_program);
      snooper.put("gl_caps[ARB_vertex_shader]", var2.GL_ARB_vertex_shader);
      snooper.put("gl_caps[EXT_bindable_uniform]", var2.GL_EXT_bindable_uniform);
      snooper.put("gl_caps[EXT_blend_equation_separate]", var2.GL_EXT_blend_equation_separate);
      snooper.put("gl_caps[EXT_blend_func_separate]", var2.GL_EXT_blend_func_separate);
      snooper.put("gl_caps[EXT_blend_minmax]", var2.GL_EXT_blend_minmax);
      snooper.put("gl_caps[EXT_blend_subtract]", var2.GL_EXT_blend_subtract);
      snooper.put("gl_caps[EXT_draw_instanced]", var2.GL_EXT_draw_instanced);
      snooper.put("gl_caps[EXT_framebuffer_multisample]", var2.GL_EXT_framebuffer_multisample);
      snooper.put("gl_caps[EXT_framebuffer_object]", var2.GL_EXT_framebuffer_object);
      snooper.put("gl_caps[EXT_framebuffer_sRGB]", var2.GL_EXT_framebuffer_sRGB);
      snooper.put("gl_caps[EXT_geometry_shader4]", var2.GL_EXT_geometry_shader4);
      snooper.put("gl_caps[EXT_gpu_program_parameters]", var2.GL_EXT_gpu_program_parameters);
      snooper.put("gl_caps[EXT_gpu_shader4]", var2.GL_EXT_gpu_shader4);
      snooper.put("gl_caps[EXT_multi_draw_arrays]", var2.GL_EXT_multi_draw_arrays);
      snooper.put("gl_caps[EXT_packed_depth_stencil]", var2.GL_EXT_packed_depth_stencil);
      snooper.put("gl_caps[EXT_paletted_texture]", var2.GL_EXT_paletted_texture);
      snooper.put("gl_caps[EXT_rescale_normal]", var2.GL_EXT_rescale_normal);
      snooper.put("gl_caps[EXT_separate_shader_objects]", var2.GL_EXT_separate_shader_objects);
      snooper.put("gl_caps[EXT_shader_image_load_store]", var2.GL_EXT_shader_image_load_store);
      snooper.put("gl_caps[EXT_shadow_funcs]", var2.GL_EXT_shadow_funcs);
      snooper.put("gl_caps[EXT_shared_texture_palette]", var2.GL_EXT_shared_texture_palette);
      snooper.put("gl_caps[EXT_stencil_clear_tag]", var2.GL_EXT_stencil_clear_tag);
      snooper.put("gl_caps[EXT_stencil_two_side]", var2.GL_EXT_stencil_two_side);
      snooper.put("gl_caps[EXT_stencil_wrap]", var2.GL_EXT_stencil_wrap);
      snooper.put("gl_caps[EXT_texture_3d]", var2.GL_EXT_texture_3d);
      snooper.put("gl_caps[EXT_texture_array]", var2.GL_EXT_texture_array);
      snooper.put("gl_caps[EXT_texture_buffer_object]", var2.GL_EXT_texture_buffer_object);
      snooper.put("gl_caps[EXT_texture_integer]", var2.GL_EXT_texture_integer);
      snooper.put("gl_caps[EXT_texture_lod_bias]", var2.GL_EXT_texture_lod_bias);
      snooper.put("gl_caps[EXT_texture_sRGB]", var2.GL_EXT_texture_sRGB);
      snooper.put("gl_caps[EXT_vertex_shader]", var2.GL_EXT_vertex_shader);
      snooper.put("gl_caps[EXT_vertex_weighting]", var2.GL_EXT_vertex_weighting);
      snooper.put("gl_caps[gl_max_vertex_uniforms]", GL11.glGetInteger(35658));
      GL11.glGetError();
      snooper.put("gl_caps[gl_max_fragment_uniforms]", GL11.glGetInteger(35657));
      GL11.glGetError();
      snooper.put("gl_caps[gl_max_vertex_attribs]", GL11.glGetInteger(34921));
      GL11.glGetError();
      snooper.put("gl_caps[gl_max_vertex_texture_image_units]", GL11.glGetInteger(35660));
      GL11.glGetError();
      snooper.put("gl_caps[gl_max_texture_image_units]", GL11.glGetInteger(34930));
      GL11.glGetError();
      snooper.put("gl_caps[gl_max_texture_image_units]", GL11.glGetInteger(35071));
      GL11.glGetError();
      snooper.put("gl_max_texture_size", getMaxTextureSize());
   }

   public static int getMaxTextureSize() {
      for(int var0 = 16384; var0 > 0; var0 >>= 1) {
         GL11.glTexImage2D(32868, 0, 6408, var0, var0, 0, 6408, 5121, (ByteBuffer)null);
         int var1 = GL11.glGetTexLevelParameteri(32868, 0, 4096);
         if (var1 != 0) {
            return var0;
         }
      }

      return -1;
   }

   @Override
   public boolean isSnooperEnabled() {
      return this.options.snooperEnabled;
   }

   public void setCurrentServerEntry(ServerListEntry serverEntry) {
      this.currentServerEntry = serverEntry;
   }

   public ServerListEntry getCurrentServerEntry() {
      return this.currentServerEntry;
   }

   public boolean isIntegratedServerRunning() {
      return this.isIntegratedServerRunning;
   }

   public boolean isInSingleplayer() {
      return this.isIntegratedServerRunning && this.server != null;
   }

   public IntegratedServer getServer() {
      return this.server;
   }

   public static void shutdown() {
      if (INSTANCE != null) {
         IntegratedServer var0 = INSTANCE.getServer();
         if (var0 != null) {
            var0.stop();
         }
      }
   }

   public Snooper getSnooper() {
      return this.snooper;
   }

   public static long getTime() {
      return Sys.getTime() * 1000L / Sys.getTimerResolution();
   }

   public boolean isWindowFocused() {
      return this.fullscreen;
   }

   public Session getSession() {
      return this.session;
   }

   public PropertyMap getUserProperties() {
      return this.userProperties;
   }

   public Proxy getNetworkProxy() {
      return this.proxy;
   }

   public TextureManager getTextureManager() {
      return this.textureManager;
   }

   public IResourceManager getResourceManager() {
      return this.resourceManager;
   }

   public ResourcePackLoader getResourcePackLoader() {
      return this.loader;
   }

   public LanguageManager getLanguageManager() {
      return this.languageManager;
   }

   public SpriteAtlasTexture getSpriteAtlasTexture() {
      return this.blocksSprite;
   }

   public boolean is64Bit() {
      return this.is64Bit;
   }

   public boolean isPaused() {
      return this.paused;
   }

   public SoundManager getSoundManager() {
      return this.soundManager;
   }

   public MusicManager.Environment getMusicEnvironment() {
      if (this.currentScreen instanceof CreditsScreen) {
         return MusicManager.Environment.CREDITS;
      } else if (this.player != null) {
         if (this.player.world.dimension instanceof NetherDimension) {
            return MusicManager.Environment.NETHER;
         } else if (this.player.world.dimension instanceof TheEndDimension) {
            return BossBar.name != null && BossBar.timer > 0 ? MusicManager.Environment.END_BOSS : MusicManager.Environment.END;
         } else {
            return this.player.abilities.creativeMode && this.player.abilities.canFly ? MusicManager.Environment.CREATIVE : MusicManager.Environment.GAME;
         }
      } else {
         return MusicManager.Environment.MENU;
      }
   }

   public TwitchStream getTwitchStream() {
      return this.twitchStream;
   }

   public void handleKeyBindings() {
      int var1 = Keyboard.getEventKey();
      if (var1 != 0 && !Keyboard.isRepeatEvent()) {
         if (!(this.currentScreen instanceof ControlsOptionsScreen) || ((ControlsOptionsScreen)this.currentScreen).f_79xsamikd <= getTime() - 20L) {
            if (Keyboard.getEventKeyState()) {
               if (var1 == this.options.streamStartStopKey.getKeyCode()) {
                  if (this.getTwitchStream().m_99rcqogzt()) {
                     this.getTwitchStream().stopStream();
                  } else if (this.getTwitchStream().m_10yutarck()) {
                     this.openScreen(new ConfirmScreen(new ConfirmationListener() {
                        @Override
                        public void confirmResult(boolean result, int id) {
                           if (result) {
                              MinecraftClient.this.getTwitchStream().m_48nqrofgp();
                           }

                           MinecraftClient.this.openScreen(null);
                        }
                     }, I18n.translate("stream.confirm_start"), "", 0));
                  } else if (!this.getTwitchStream().m_78qjaxsih() || !this.getTwitchStream().m_01cdoylst()) {
                     C_39cmizuwc.m_95brgjnxo(this.currentScreen);
                  } else if (this.world != null) {
                     this.gui.getChat().addMessage(new LiteralText("Not ready to start streaming yet!"));
                  }
               } else if (var1 == this.options.streamPauseKey.getKeyCode()) {
                  if (this.getTwitchStream().m_99rcqogzt()) {
                     if (this.getTwitchStream().m_59ybwvnxm()) {
                        this.getTwitchStream().m_59fglhcmk();
                     } else {
                        this.getTwitchStream().m_62rfzgdrt();
                     }
                  }
               } else if (var1 == this.options.streamCommercialKey.getKeyCode()) {
                  if (this.getTwitchStream().m_99rcqogzt()) {
                     this.getTwitchStream().m_67lgjitba();
                  }
               } else if (var1 == this.options.streamToggleMicKey.getKeyCode()) {
                  this.twitchStream.m_39rjuiusz(true);
               } else if (var1 == this.options.fullscreenKey.getKeyCode()) {
                  this.toggleFullscreen();
               } else if (var1 == this.options.screenshotKey.getKeyCode()) {
                  this.gui.getChat().addMessage(ScreenshotUtils.saveScreenshot(this.runDir, this.width, this.height, this.renderTarget));
               }
            } else if (var1 == this.options.streamToggleMicKey.getKeyCode()) {
               this.twitchStream.m_39rjuiusz(false);
            }
         }
      }
   }

   public MinecraftSessionService createAuthenticationService() {
      return this.sessionService;
   }

   public SkinManager getSkinManager() {
      return this.skinManager;
   }

   public Entity getCamera() {
      return this.camera;
   }

   public void setCamera(Entity camera) {
      this.camera = camera;
      this.gameRenderer.setCamera(camera);
   }

   public ListenableFuture m_58lcfcfpm(Callable callable) {
      Validate.notNull(callable);
      if (!this.isOnSameThread()) {
         ListenableFutureTask var2 = ListenableFutureTask.create(callable);
         synchronized(this.f_13gwyakrt) {
            this.f_13gwyakrt.add(var2);
            return var2;
         }
      } else {
         try {
            return Futures.immediateFuture(callable.call());
         } catch (Exception var6) {
            return Futures.immediateFailedCheckedFuture(var6);
         }
      }
   }

   @Override
   public ListenableFuture submit(Runnable event) {
      Validate.notNull(event);
      return this.m_58lcfcfpm(Executors.callable(event));
   }

   @Override
   public boolean isOnSameThread() {
      return Thread.currentThread() == this.thread;
   }

   public BlockRenderDispatcher getBlockRenderDispatcher() {
      return this.blockRenderer;
   }

   public EntityRenderDispatcher getEntityRenderDispatcher() {
      return this.entityRenderDispatcher;
   }

   public ItemRenderer getItemRenderer() {
      return this.itemRenderer;
   }

   public HeldItemRenderer getHeldItemRenderer() {
      return this.heldItemRenderer;
   }

   public static int getCurrentFps() {
      return currentFps;
   }
}
