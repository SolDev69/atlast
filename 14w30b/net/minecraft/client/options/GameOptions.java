package net.minecraft.client.options;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.C_48kamxasz;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ChatGui;
import net.minecraft.client.render.model.PlayerModelPart;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.SoundCategory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

@Environment(EnvType.CLIENT)
public class GameOptions {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = new Gson();
   private static final ParameterizedType STRING_LIST_TYPE = new ParameterizedType() {
      @Override
      public Type[] getActualTypeArguments() {
         return new Type[]{String.class};
      }

      @Override
      public Type getRawType() {
         return List.class;
      }

      @Override
      public Type getOwnerType() {
         return null;
      }
   };
   private static final String[] GUI_SETTINGS = new String[]{
      "options.guiScale.auto", "options.guiScale.small", "options.guiScale.normal", "options.guiScale.large"
   };
   private static final String[] PARTICLE_SETTINGS = new String[]{"options.particles.all", "options.particles.decreased", "options.particles.minimal"};
   private static final String[] AO_SETTINGS = new String[]{"options.ao.off", "options.ao.min", "options.ao.max"};
   private static final String[] STREAM_COMPRESSION_SETTINGS = new String[]{
      "options.stream.compression.low", "options.stream.compression.medium", "options.stream.compression.high"
   };
   private static final String[] STREAM_CHAT_ENABLED_SETTINGS = new String[]{
      "options.stream.chat.enabled.streaming", "options.stream.chat.enabled.always", "options.stream.chat.enabled.never"
   };
   private static final String[] STREAM_USER_FILTER_SETTINGS = new String[]{
      "options.stream.chat.userFilter.all", "options.stream.chat.userFilter.subs", "options.stream.chat.userFilter.mods"
   };
   private static final String[] STREAM_MIC_TOGGLE_SETTINGS = new String[]{"options.stream.mic_toggle.mute", "options.stream.mic_toggle.talk"};
   public float mouseSensitivity = 0.5F;
   public boolean invertMouseY;
   public int viewDistance = -1;
   public boolean viewBobbing = true;
   public boolean anaglyph;
   public boolean fboEnable = true;
   public int frameLimit = 120;
   public boolean renderClouds = true;
   public boolean fancyGraphics = true;
   public int ambientOcclusion = 2;
   public List resourcePacks = Lists.newArrayList();
   public PlayerEntity.ChatVisibility chatVisibility = PlayerEntity.ChatVisibility.FULL;
   public boolean chatColors = true;
   public boolean chatLinks = true;
   public boolean promptChatLinks = true;
   public float chatOpacity = 1.0F;
   public boolean snooperEnabled = true;
   public boolean fullscreen;
   public boolean vsync = true;
   public boolean useVbo = false;
   public boolean allowBlockAlternatives = true;
   public boolean reducedDebugInfo = false;
   public boolean hideServerAddress;
   public boolean advancedItemTooltips;
   public boolean pauseOnUnfocus = true;
   private final Set playerModelParts = Sets.newHashSet(PlayerModelPart.values());
   public boolean touchscreen;
   public int overrideWidth;
   public int overrideHeight;
   public boolean heldItemTooltips = true;
   public float chatScale = 1.0F;
   public float chatWidth = 1.0F;
   public float unfocusedChatHeight = 0.44366196F;
   public float focusedChatHeight = 1.0F;
   public boolean showInventoryAchievementHint = true;
   public int mipmapLevels = 4;
   private Map soundCategoryVolumes = Maps.newEnumMap(SoundCategory.class);
   public float streamBytesPerPixel = 0.5F;
   public float streamMicVolume = 1.0F;
   public float streamSystemVolume = 1.0F;
   public float streamKbps = 0.5412844F;
   public float streamFps = 0.31690142F;
   public int streamCompression = 1;
   public boolean streamSendMetadata = true;
   public String streamPreferredServer = "";
   public int streamChatEnabled = 0;
   public int streamChatUserFilter = 0;
   public int streamMicToggleBehavior = 0;
   public KeyBinding forwardKey = new KeyBinding("key.forward", 17, "key.categories.movement");
   public KeyBinding leftKey = new KeyBinding("key.left", 30, "key.categories.movement");
   public KeyBinding backKey = new KeyBinding("key.back", 31, "key.categories.movement");
   public KeyBinding rightKey = new KeyBinding("key.right", 32, "key.categories.movement");
   public KeyBinding jumpKey = new KeyBinding("key.jump", 57, "key.categories.movement");
   public KeyBinding sneakKey = new KeyBinding("key.sneak", 42, "key.categories.movement");
   public KeyBinding inventoryKey = new KeyBinding("key.inventory", 18, "key.categories.inventory");
   public KeyBinding usekey = new KeyBinding("key.use", -99, "key.categories.gameplay");
   public KeyBinding dropKey = new KeyBinding("key.drop", 16, "key.categories.gameplay");
   public KeyBinding attackKey = new KeyBinding("key.attack", -100, "key.categories.gameplay");
   public KeyBinding pickItemKey = new KeyBinding("key.pickItem", -98, "key.categories.gameplay");
   public KeyBinding sprintKey = new KeyBinding("key.sprint", 29, "key.categories.gameplay");
   public KeyBinding chatKey = new KeyBinding("key.chat", 20, "key.categories.multiplayer");
   public KeyBinding playerListKey = new KeyBinding("key.playerlist", 15, "key.categories.multiplayer");
   public KeyBinding commandKey = new KeyBinding("key.command", 53, "key.categories.multiplayer");
   public KeyBinding screenshotKey = new KeyBinding("key.screenshot", 60, "key.categories.misc");
   public KeyBinding togglePerspectiveKey = new KeyBinding("key.togglePerspective", 63, "key.categories.misc");
   public KeyBinding smoothCameraKey = new KeyBinding("key.smoothCamera", 0, "key.categories.misc");
   public KeyBinding fullscreenKey = new KeyBinding("key.fullscreen", 87, "key.categories.misc");
   public KeyBinding spectatorOutlinesKey = new KeyBinding("key.spectatorOutlines", 0, "key.categories.misc");
   public KeyBinding streamStartStopKey = new KeyBinding("key.streamStartStop", 64, "key.categories.stream");
   public KeyBinding streamPauseKey = new KeyBinding("key.streamPauseUnpause", 65, "key.categories.stream");
   public KeyBinding streamCommercialKey = new KeyBinding("key.streamCommercial", 0, "key.categories.stream");
   public KeyBinding streamToggleMicKey = new KeyBinding("key.streamToggleMic", 0, "key.categories.stream");
   public KeyBinding[] hotbarKeys = new KeyBinding[]{
      new KeyBinding("key.hotbar.1", 2, "key.categories.inventory"),
      new KeyBinding("key.hotbar.2", 3, "key.categories.inventory"),
      new KeyBinding("key.hotbar.3", 4, "key.categories.inventory"),
      new KeyBinding("key.hotbar.4", 5, "key.categories.inventory"),
      new KeyBinding("key.hotbar.5", 6, "key.categories.inventory"),
      new KeyBinding("key.hotbar.6", 7, "key.categories.inventory"),
      new KeyBinding("key.hotbar.7", 8, "key.categories.inventory"),
      new KeyBinding("key.hotbar.8", 9, "key.categories.inventory"),
      new KeyBinding("key.hotbar.9", 10, "key.categories.inventory")
   };
   public KeyBinding[] ingameKeys = (KeyBinding[])ArrayUtils.addAll(
      new KeyBinding[]{
         this.attackKey,
         this.usekey,
         this.forwardKey,
         this.leftKey,
         this.backKey,
         this.rightKey,
         this.jumpKey,
         this.sneakKey,
         this.dropKey,
         this.inventoryKey,
         this.chatKey,
         this.playerListKey,
         this.pickItemKey,
         this.commandKey,
         this.screenshotKey,
         this.togglePerspectiveKey,
         this.smoothCameraKey,
         this.sprintKey,
         this.streamStartStopKey,
         this.streamPauseKey,
         this.streamCommercialKey,
         this.streamToggleMicKey,
         this.fullscreenKey,
         this.spectatorOutlinesKey
      },
      this.hotbarKeys
   );
   protected MinecraftClient client;
   private File file;
   public Difficulty difficulty = Difficulty.NORMAL;
   public boolean hudEnabled;
   public int perspective;
   public boolean debugEnabled;
   public boolean debugProfilerEnabled;
   public String lastServer = "";
   public boolean smoothCamera;
   public boolean debugCamera;
   public float fov = 70.0F;
   public float gamma;
   public float saturation;
   public int guiScale;
   public int particles;
   public String language = "en_US";
   public boolean forceUnicodeFont = false;

   public GameOptions(MinecraftClient client, File file) {
      this.client = client;
      this.file = new File(file, "options.txt");
      if (client.is64Bit() && Runtime.getRuntime().maxMemory() >= 1000000000L) {
         GameOptions.Option.RENDER_DISTANCE.setMax(32.0F);
      } else {
         GameOptions.Option.RENDER_DISTANCE.setMax(16.0F);
      }

      this.viewDistance = client.is64Bit() ? 12 : 8;
      this.load();
   }

   public GameOptions() {
   }

   public static String getKeyName(int keyCode) {
      return keyCode < 0 ? I18n.translate("key.mouseButton", keyCode + 101) : Keyboard.getKeyName(keyCode);
   }

   public static boolean isPressed(KeyBinding keyBinding) {
      if (keyBinding.getKeyCode() == 0) {
         return false;
      } else {
         return keyBinding.getKeyCode() < 0 ? Mouse.isButtonDown(keyBinding.getKeyCode() + 100) : Keyboard.isKeyDown(keyBinding.getKeyCode());
      }
   }

   public void setKeyCode(KeyBinding keyBinding, int code) {
      keyBinding.setKeyCode(code);
      this.save();
   }

   public void setValue(GameOptions.Option option, float value) {
      if (option == GameOptions.Option.SENSITIVITY) {
         this.mouseSensitivity = value;
      }

      if (option == GameOptions.Option.FOV) {
         this.fov = value;
      }

      if (option == GameOptions.Option.GAMMA) {
         this.gamma = value;
      }

      if (option == GameOptions.Option.FRAMERATE_LIMIT) {
         this.frameLimit = (int)value;
      }

      if (option == GameOptions.Option.CHAT_OPACITY) {
         this.chatOpacity = value;
         this.client.gui.getChat().reset();
      }

      if (option == GameOptions.Option.CHAT_HEIGHT_FOCUSED) {
         this.focusedChatHeight = value;
         this.client.gui.getChat().reset();
      }

      if (option == GameOptions.Option.CHAT_HEIGHT_UNFOCUSED) {
         this.unfocusedChatHeight = value;
         this.client.gui.getChat().reset();
      }

      if (option == GameOptions.Option.CHAT_WIDTH) {
         this.chatWidth = value;
         this.client.gui.getChat().reset();
      }

      if (option == GameOptions.Option.CHAT_SCALE) {
         this.chatScale = value;
         this.client.gui.getChat().reset();
      }

      if (option == GameOptions.Option.MAPMAP_LEVELS) {
         int var3 = this.mipmapLevels;
         this.mipmapLevels = (int)value;
         if ((float)var3 != value) {
            this.client.getSpriteAtlasTexture().setMaxTextureSize(this.mipmapLevels);
            this.client.getTextureManager().bind(SpriteAtlasTexture.BLOCK_ATLAS_BLOCKS);
            this.client.getSpriteAtlasTexture().m_21vhhelxf(false, this.mipmapLevels > 0);
            this.client.onApplyServerResourcePack();
         }
      }

      if (option == GameOptions.Option.BLOCK_ALTERNATIVES) {
         this.allowBlockAlternatives = !this.allowBlockAlternatives;
         this.client.worldRenderer.reload();
      }

      if (option == GameOptions.Option.RENDER_DISTANCE) {
         this.viewDistance = (int)value;
         this.client.worldRenderer.m_06jwhpvvs();
      }

      if (option == GameOptions.Option.STREAM_BYTES_PER_PIXEL) {
         this.streamBytesPerPixel = value;
      }

      if (option == GameOptions.Option.STREAM_VOLUME_MIC) {
         this.streamMicVolume = value;
         this.client.getTwitchStream().m_19ohgzkkn();
      }

      if (option == GameOptions.Option.STREAM_VOLUME_SYSTEM) {
         this.streamSystemVolume = value;
         this.client.getTwitchStream().m_19ohgzkkn();
      }

      if (option == GameOptions.Option.STREAM_KBPS) {
         this.streamKbps = value;
      }

      if (option == GameOptions.Option.STREAM_FPS) {
         this.streamFps = value;
      }
   }

   public void setValue(GameOptions.Option option, int value) {
      if (option == GameOptions.Option.INVERT_MOUSE) {
         this.invertMouseY = !this.invertMouseY;
      }

      if (option == GameOptions.Option.GUI_SCALE) {
         this.guiScale = this.guiScale + value & 3;
      }

      if (option == GameOptions.Option.PARTICLES) {
         this.particles = (this.particles + value) % 3;
      }

      if (option == GameOptions.Option.VIEW_BOBBING) {
         this.viewBobbing = !this.viewBobbing;
      }

      if (option == GameOptions.Option.RENDER_CLOUDS) {
         this.renderClouds = !this.renderClouds;
      }

      if (option == GameOptions.Option.FORCE_UNICODE_FONT) {
         this.forceUnicodeFont = !this.forceUnicodeFont;
         this.client.textRenderer.setUnicode(this.client.getLanguageManager().isUnicode() || this.forceUnicodeFont);
      }

      if (option == GameOptions.Option.FBO_ENABLE) {
         this.fboEnable = !this.fboEnable;
      }

      if (option == GameOptions.Option.ANAGLYPH) {
         this.anaglyph = !this.anaglyph;
         this.client.reloadResources();
      }

      if (option == GameOptions.Option.GRAPHICS) {
         this.fancyGraphics = !this.fancyGraphics;
         this.client.worldRenderer.reload();
      }

      if (option == GameOptions.Option.AMBIENT_OCCLUSION) {
         this.ambientOcclusion = (this.ambientOcclusion + value) % 3;
         this.client.worldRenderer.reload();
      }

      if (option == GameOptions.Option.CHAT_VISIBILITY) {
         this.chatVisibility = PlayerEntity.ChatVisibility.byIndex((this.chatVisibility.getIndex() + value) % 3);
      }

      if (option == GameOptions.Option.STREAM_COMPRESSION) {
         this.streamCompression = (this.streamCompression + value) % 3;
      }

      if (option == GameOptions.Option.STREAM_SEND_METADATA) {
         this.streamSendMetadata = !this.streamSendMetadata;
      }

      if (option == GameOptions.Option.STREAM_CHAT_ENABLED) {
         this.streamChatEnabled = (this.streamChatEnabled + value) % 3;
      }

      if (option == GameOptions.Option.STREAM_CHAT_USER_FILTER) {
         this.streamChatUserFilter = (this.streamChatUserFilter + value) % 3;
      }

      if (option == GameOptions.Option.STREAM_MIC_TOGGLE_BEHAVIOR) {
         this.streamMicToggleBehavior = (this.streamMicToggleBehavior + value) % 2;
      }

      if (option == GameOptions.Option.CHAT_COLOR) {
         this.chatColors = !this.chatColors;
      }

      if (option == GameOptions.Option.CHAT_LINKS) {
         this.chatLinks = !this.chatLinks;
      }

      if (option == GameOptions.Option.CHAT_LINKS_PROMPT) {
         this.promptChatLinks = !this.promptChatLinks;
      }

      if (option == GameOptions.Option.SNOOPER_ENABLED) {
         this.snooperEnabled = !this.snooperEnabled;
      }

      if (option == GameOptions.Option.TOUCHSCREEN) {
         this.touchscreen = !this.touchscreen;
      }

      if (option == GameOptions.Option.USE_FULLSCREEN) {
         this.fullscreen = !this.fullscreen;
         if (this.client.isWindowFocused() != this.fullscreen) {
            this.client.toggleFullscreen();
         }
      }

      if (option == GameOptions.Option.ENABLE_VSYNC) {
         this.vsync = !this.vsync;
         Display.setVSyncEnabled(this.vsync);
      }

      if (option == GameOptions.Option.USE_VBO) {
         this.useVbo = !this.useVbo;
         this.client.worldRenderer.reload();
      }

      if (option == GameOptions.Option.BLOCK_ALTERNATIVES) {
         this.allowBlockAlternatives = !this.allowBlockAlternatives;
         this.client.worldRenderer.reload();
      }

      if (option == GameOptions.Option.REDUCED_DEBUG_INFO) {
         this.reducedDebugInfo = !this.reducedDebugInfo;
      }

      this.save();
   }

   public float getValueFloat(GameOptions.Option option) {
      if (option == GameOptions.Option.FOV) {
         return this.fov;
      } else if (option == GameOptions.Option.GAMMA) {
         return this.gamma;
      } else if (option == GameOptions.Option.SATURATION) {
         return this.saturation;
      } else if (option == GameOptions.Option.SENSITIVITY) {
         return this.mouseSensitivity;
      } else if (option == GameOptions.Option.CHAT_OPACITY) {
         return this.chatOpacity;
      } else if (option == GameOptions.Option.CHAT_HEIGHT_FOCUSED) {
         return this.focusedChatHeight;
      } else if (option == GameOptions.Option.CHAT_HEIGHT_UNFOCUSED) {
         return this.unfocusedChatHeight;
      } else if (option == GameOptions.Option.CHAT_SCALE) {
         return this.chatScale;
      } else if (option == GameOptions.Option.CHAT_WIDTH) {
         return this.chatWidth;
      } else if (option == GameOptions.Option.FRAMERATE_LIMIT) {
         return (float)this.frameLimit;
      } else if (option == GameOptions.Option.MAPMAP_LEVELS) {
         return (float)this.mipmapLevels;
      } else if (option == GameOptions.Option.RENDER_DISTANCE) {
         return (float)this.viewDistance;
      } else if (option == GameOptions.Option.STREAM_BYTES_PER_PIXEL) {
         return this.streamBytesPerPixel;
      } else if (option == GameOptions.Option.STREAM_VOLUME_MIC) {
         return this.streamMicVolume;
      } else if (option == GameOptions.Option.STREAM_VOLUME_SYSTEM) {
         return this.streamSystemVolume;
      } else if (option == GameOptions.Option.STREAM_KBPS) {
         return this.streamKbps;
      } else {
         return option == GameOptions.Option.STREAM_FPS ? this.streamFps : 0.0F;
      }
   }

   public boolean getValueBool(GameOptions.Option option) {
      switch(option) {
         case INVERT_MOUSE:
            return this.invertMouseY;
         case VIEW_BOBBING:
            return this.viewBobbing;
         case ANAGLYPH:
            return this.anaglyph;
         case FBO_ENABLE:
            return this.fboEnable;
         case RENDER_CLOUDS:
            return this.renderClouds;
         case CHAT_COLOR:
            return this.chatColors;
         case CHAT_LINKS:
            return this.chatLinks;
         case CHAT_LINKS_PROMPT:
            return this.promptChatLinks;
         case SNOOPER_ENABLED:
            return this.snooperEnabled;
         case USE_FULLSCREEN:
            return this.fullscreen;
         case ENABLE_VSYNC:
            return this.vsync;
         case USE_VBO:
            return this.useVbo;
         case TOUCHSCREEN:
            return this.touchscreen;
         case STREAM_SEND_METADATA:
            return this.streamSendMetadata;
         case FORCE_UNICODE_FONT:
            return this.forceUnicodeFont;
         case BLOCK_ALTERNATIVES:
            return this.allowBlockAlternatives;
         case REDUCED_DEBUG_INFO:
            return this.reducedDebugInfo;
         default:
            return false;
      }
   }

   private static String getName(String[] settings, int index) {
      if (index < 0 || index >= settings.length) {
         index = 0;
      }

      return I18n.translate(settings[index]);
   }

   public String getValueAsString(GameOptions.Option option) {
      String var2 = I18n.translate(option.getName()) + ": ";
      if (option.isFloatOption()) {
         float var6 = this.getValueFloat(option);
         float var4 = option.normalize(var6);
         if (option == GameOptions.Option.SENSITIVITY) {
            if (var4 == 0.0F) {
               return var2 + I18n.translate("options.sensitivity.min");
            } else {
               return var4 == 1.0F ? var2 + I18n.translate("options.sensitivity.max") : var2 + (int)(var4 * 200.0F) + "%";
            }
         } else if (option == GameOptions.Option.FOV) {
            if (var6 == 70.0F) {
               return var2 + I18n.translate("options.fov.min");
            } else {
               return var6 == 110.0F ? var2 + I18n.translate("options.fov.max") : var2 + (int)var6;
            }
         } else if (option == GameOptions.Option.FRAMERATE_LIMIT) {
            return var6 == option.max ? var2 + I18n.translate("options.framerateLimit.max") : var2 + (int)var6 + " fps";
         } else if (option == GameOptions.Option.RENDER_CLOUDS) {
            return var6 == option.min ? var2 + I18n.translate("options.cloudHeight.min") : var2 + ((int)var6 + 128);
         } else if (option == GameOptions.Option.GAMMA) {
            if (var4 == 0.0F) {
               return var2 + I18n.translate("options.gamma.min");
            } else {
               return var4 == 1.0F ? var2 + I18n.translate("options.gamma.max") : var2 + "+" + (int)(var4 * 100.0F) + "%";
            }
         } else if (option == GameOptions.Option.SATURATION) {
            return var2 + (int)(var4 * 400.0F) + "%";
         } else if (option == GameOptions.Option.CHAT_OPACITY) {
            return var2 + (int)(var4 * 90.0F + 10.0F) + "%";
         } else if (option == GameOptions.Option.CHAT_HEIGHT_UNFOCUSED) {
            return var2 + ChatGui.getHeight(var4) + "px";
         } else if (option == GameOptions.Option.CHAT_HEIGHT_FOCUSED) {
            return var2 + ChatGui.getHeight(var4) + "px";
         } else if (option == GameOptions.Option.CHAT_WIDTH) {
            return var2 + ChatGui.getWidth(var4) + "px";
         } else if (option == GameOptions.Option.RENDER_DISTANCE) {
            return var2 + (int)var6 + " chunks";
         } else if (option == GameOptions.Option.MAPMAP_LEVELS) {
            return var6 == 0.0F ? var2 + I18n.translate("options.off") : var2 + (int)var6;
         } else if (option == GameOptions.Option.STREAM_FPS) {
            return var2 + C_48kamxasz.m_41sqvmjqh(var4) + " fps";
         } else if (option == GameOptions.Option.STREAM_KBPS) {
            return var2 + C_48kamxasz.m_24jzkvtzu(var4) + " Kbps";
         } else if (option == GameOptions.Option.STREAM_BYTES_PER_PIXEL) {
            return var2 + String.format("%.3f bpp", C_48kamxasz.m_67xemlvwq(var4));
         } else {
            return var4 == 0.0F ? var2 + I18n.translate("options.off") : var2 + (int)(var4 * 100.0F) + "%";
         }
      } else if (option.isBooleanOption()) {
         boolean var5 = this.getValueBool(option);
         return var5 ? var2 + I18n.translate("options.on") : var2 + I18n.translate("options.off");
      } else if (option == GameOptions.Option.GUI_SCALE) {
         return var2 + getName(GUI_SETTINGS, this.guiScale);
      } else if (option == GameOptions.Option.CHAT_VISIBILITY) {
         return var2 + I18n.translate(this.chatVisibility.getId());
      } else if (option == GameOptions.Option.PARTICLES) {
         return var2 + getName(PARTICLE_SETTINGS, this.particles);
      } else if (option == GameOptions.Option.AMBIENT_OCCLUSION) {
         return var2 + getName(AO_SETTINGS, this.ambientOcclusion);
      } else if (option == GameOptions.Option.STREAM_COMPRESSION) {
         return var2 + getName(STREAM_COMPRESSION_SETTINGS, this.streamCompression);
      } else if (option == GameOptions.Option.STREAM_CHAT_ENABLED) {
         return var2 + getName(STREAM_CHAT_ENABLED_SETTINGS, this.streamChatEnabled);
      } else if (option == GameOptions.Option.STREAM_CHAT_USER_FILTER) {
         return var2 + getName(STREAM_USER_FILTER_SETTINGS, this.streamChatUserFilter);
      } else if (option == GameOptions.Option.STREAM_MIC_TOGGLE_BEHAVIOR) {
         return var2 + getName(STREAM_MIC_TOGGLE_SETTINGS, this.streamMicToggleBehavior);
      } else if (option == GameOptions.Option.GRAPHICS) {
         if (this.fancyGraphics) {
            return var2 + I18n.translate("options.graphics.fancy");
         } else {
            String var3 = "options.graphics.fast";
            return var2 + I18n.translate("options.graphics.fast");
         }
      } else {
         return var2;
      }
   }

   public void load() {
      try {
         if (!this.file.exists()) {
            return;
         }

         BufferedReader var1 = new BufferedReader(new FileReader(this.file));
         String var2 = "";
         this.soundCategoryVolumes.clear();

         while((var2 = var1.readLine()) != null) {
            try {
               String[] var3 = var2.split(":");
               if (var3[0].equals("mouseSensitivity")) {
                  this.mouseSensitivity = this.parseFloat(var3[1]);
               }

               if (var3[0].equals("fov")) {
                  this.fov = this.parseFloat(var3[1]) * 40.0F + 70.0F;
               }

               if (var3[0].equals("gamma")) {
                  this.gamma = this.parseFloat(var3[1]);
               }

               if (var3[0].equals("saturation")) {
                  this.saturation = this.parseFloat(var3[1]);
               }

               if (var3[0].equals("invertYMouse")) {
                  this.invertMouseY = var3[1].equals("true");
               }

               if (var3[0].equals("renderDistance")) {
                  this.viewDistance = Integer.parseInt(var3[1]);
               }

               if (var3[0].equals("guiScale")) {
                  this.guiScale = Integer.parseInt(var3[1]);
               }

               if (var3[0].equals("particles")) {
                  this.particles = Integer.parseInt(var3[1]);
               }

               if (var3[0].equals("bobView")) {
                  this.viewBobbing = var3[1].equals("true");
               }

               if (var3[0].equals("anaglyph3d")) {
                  this.anaglyph = var3[1].equals("true");
               }

               if (var3[0].equals("maxFps")) {
                  this.frameLimit = Integer.parseInt(var3[1]);
               }

               if (var3[0].equals("fboEnable")) {
                  this.fboEnable = var3[1].equals("true");
               }

               if (var3[0].equals("difficulty")) {
                  this.difficulty = Difficulty.byIndex(Integer.parseInt(var3[1]));
               }

               if (var3[0].equals("fancyGraphics")) {
                  this.fancyGraphics = var3[1].equals("true");
               }

               if (var3[0].equals("ao")) {
                  if (var3[1].equals("true")) {
                     this.ambientOcclusion = 2;
                  } else if (var3[1].equals("false")) {
                     this.ambientOcclusion = 0;
                  } else {
                     this.ambientOcclusion = Integer.parseInt(var3[1]);
                  }
               }

               if (var3[0].equals("renderClouds")) {
                  this.renderClouds = var3[1].equals("true");
               }

               if (var3[0].equals("resourcePacks")) {
                  this.resourcePacks = (List)GSON.fromJson(var2.substring(var2.indexOf(58) + 1), STRING_LIST_TYPE);
                  if (this.resourcePacks == null) {
                     this.resourcePacks = Lists.newArrayList();
                  }
               }

               if (var3[0].equals("lastServer") && var3.length >= 2) {
                  this.lastServer = var2.substring(var2.indexOf(58) + 1);
               }

               if (var3[0].equals("lang") && var3.length >= 2) {
                  this.language = var3[1];
               }

               if (var3[0].equals("chatVisibility")) {
                  this.chatVisibility = PlayerEntity.ChatVisibility.byIndex(Integer.parseInt(var3[1]));
               }

               if (var3[0].equals("chatColors")) {
                  this.chatColors = var3[1].equals("true");
               }

               if (var3[0].equals("chatLinks")) {
                  this.chatLinks = var3[1].equals("true");
               }

               if (var3[0].equals("chatLinksPrompt")) {
                  this.promptChatLinks = var3[1].equals("true");
               }

               if (var3[0].equals("chatOpacity")) {
                  this.chatOpacity = this.parseFloat(var3[1]);
               }

               if (var3[0].equals("snooperEnabled")) {
                  this.snooperEnabled = var3[1].equals("true");
               }

               if (var3[0].equals("fullscreen")) {
                  this.fullscreen = var3[1].equals("true");
               }

               if (var3[0].equals("enableVsync")) {
                  this.vsync = var3[1].equals("true");
               }

               if (var3[0].equals("useVbo")) {
                  this.useVbo = var3[1].equals("true");
               }

               if (var3[0].equals("hideServerAddress")) {
                  this.hideServerAddress = var3[1].equals("true");
               }

               if (var3[0].equals("advancedItemTooltips")) {
                  this.advancedItemTooltips = var3[1].equals("true");
               }

               if (var3[0].equals("pauseOnLostFocus")) {
                  this.pauseOnUnfocus = var3[1].equals("true");
               }

               if (var3[0].equals("touchscreen")) {
                  this.touchscreen = var3[1].equals("true");
               }

               if (var3[0].equals("overrideHeight")) {
                  this.overrideHeight = Integer.parseInt(var3[1]);
               }

               if (var3[0].equals("overrideWidth")) {
                  this.overrideWidth = Integer.parseInt(var3[1]);
               }

               if (var3[0].equals("heldItemTooltips")) {
                  this.heldItemTooltips = var3[1].equals("true");
               }

               if (var3[0].equals("chatHeightFocused")) {
                  this.focusedChatHeight = this.parseFloat(var3[1]);
               }

               if (var3[0].equals("chatHeightUnfocused")) {
                  this.unfocusedChatHeight = this.parseFloat(var3[1]);
               }

               if (var3[0].equals("chatScale")) {
                  this.chatScale = this.parseFloat(var3[1]);
               }

               if (var3[0].equals("chatWidth")) {
                  this.chatWidth = this.parseFloat(var3[1]);
               }

               if (var3[0].equals("showInventoryAchievementHint")) {
                  this.showInventoryAchievementHint = var3[1].equals("true");
               }

               if (var3[0].equals("mipmapLevels")) {
                  this.mipmapLevels = Integer.parseInt(var3[1]);
               }

               if (var3[0].equals("streamBytesPerPixel")) {
                  this.streamBytesPerPixel = this.parseFloat(var3[1]);
               }

               if (var3[0].equals("streamMicVolume")) {
                  this.streamMicVolume = this.parseFloat(var3[1]);
               }

               if (var3[0].equals("streamSystemVolume")) {
                  this.streamSystemVolume = this.parseFloat(var3[1]);
               }

               if (var3[0].equals("streamKbps")) {
                  this.streamKbps = this.parseFloat(var3[1]);
               }

               if (var3[0].equals("streamFps")) {
                  this.streamFps = this.parseFloat(var3[1]);
               }

               if (var3[0].equals("streamCompression")) {
                  this.streamCompression = Integer.parseInt(var3[1]);
               }

               if (var3[0].equals("streamSendMetadata")) {
                  this.streamSendMetadata = var3[1].equals("true");
               }

               if (var3[0].equals("streamPreferredServer") && var3.length >= 2) {
                  this.streamPreferredServer = var2.substring(var2.indexOf(58) + 1);
               }

               if (var3[0].equals("streamChatEnabled")) {
                  this.streamChatEnabled = Integer.parseInt(var3[1]);
               }

               if (var3[0].equals("streamChatUserFilter")) {
                  this.streamChatUserFilter = Integer.parseInt(var3[1]);
               }

               if (var3[0].equals("streamMicToggleBehavior")) {
                  this.streamMicToggleBehavior = Integer.parseInt(var3[1]);
               }

               if (var3[0].equals("forceUnicodeFont")) {
                  this.forceUnicodeFont = var3[1].equals("true");
               }

               if (var3[0].equals("allowBlockAlternatives")) {
                  this.allowBlockAlternatives = var3[1].equals("true");
               }

               if (var3[0].equals("reducedDebugInfo")) {
                  this.reducedDebugInfo = var3[1].equals("true");
               }

               for(KeyBinding var7 : this.ingameKeys) {
                  if (var3[0].equals("key_" + var7.getName())) {
                     var7.setKeyCode(Integer.parseInt(var3[1]));
                  }
               }

               for(SoundCategory var17 : SoundCategory.values()) {
                  if (var3[0].equals("soundCategory_" + var17.getName())) {
                     this.soundCategoryVolumes.put(var17, this.parseFloat(var3[1]));
                  }
               }

               for(PlayerModelPart var18 : PlayerModelPart.values()) {
                  if (var3[0].equals("modelPart_" + var18.getId())) {
                     this.setPlayerModelPart(var18, var3[1].equals("true"));
                  }
               }
            } catch (Exception var8) {
               LOGGER.warn("Skipping bad option: " + var2);
            }
         }

         KeyBinding.updateKeyCodeMap();
         var1.close();
      } catch (Exception var9) {
         LOGGER.error("Failed to load options", var9);
      }
   }

   private float parseFloat(String value) {
      if (value.equals("true")) {
         return 1.0F;
      } else {
         return value.equals("false") ? 0.0F : Float.parseFloat(value);
      }
   }

   public void save() {
      try {
         PrintWriter var1 = new PrintWriter(new FileWriter(this.file));
         var1.println("invertYMouse:" + this.invertMouseY);
         var1.println("mouseSensitivity:" + this.mouseSensitivity);
         var1.println("fov:" + (this.fov - 70.0F) / 40.0F);
         var1.println("gamma:" + this.gamma);
         var1.println("saturation:" + this.saturation);
         var1.println("renderDistance:" + this.viewDistance);
         var1.println("guiScale:" + this.guiScale);
         var1.println("particles:" + this.particles);
         var1.println("bobView:" + this.viewBobbing);
         var1.println("anaglyph3d:" + this.anaglyph);
         var1.println("maxFps:" + this.frameLimit);
         var1.println("fboEnable:" + this.fboEnable);
         var1.println("difficulty:" + this.difficulty.getIndex());
         var1.println("fancyGraphics:" + this.fancyGraphics);
         var1.println("ao:" + this.ambientOcclusion);
         var1.println("renderClouds:" + this.renderClouds);
         var1.println("resourcePacks:" + GSON.toJson(this.resourcePacks));
         var1.println("lastServer:" + this.lastServer);
         var1.println("lang:" + this.language);
         var1.println("chatVisibility:" + this.chatVisibility.getIndex());
         var1.println("chatColors:" + this.chatColors);
         var1.println("chatLinks:" + this.chatLinks);
         var1.println("chatLinksPrompt:" + this.promptChatLinks);
         var1.println("chatOpacity:" + this.chatOpacity);
         var1.println("snooperEnabled:" + this.snooperEnabled);
         var1.println("fullscreen:" + this.fullscreen);
         var1.println("enableVsync:" + this.vsync);
         var1.println("useVbo:" + this.useVbo);
         var1.println("hideServerAddress:" + this.hideServerAddress);
         var1.println("advancedItemTooltips:" + this.advancedItemTooltips);
         var1.println("pauseOnLostFocus:" + this.pauseOnUnfocus);
         var1.println("touchscreen:" + this.touchscreen);
         var1.println("overrideWidth:" + this.overrideWidth);
         var1.println("overrideHeight:" + this.overrideHeight);
         var1.println("heldItemTooltips:" + this.heldItemTooltips);
         var1.println("chatHeightFocused:" + this.focusedChatHeight);
         var1.println("chatHeightUnfocused:" + this.unfocusedChatHeight);
         var1.println("chatScale:" + this.chatScale);
         var1.println("chatWidth:" + this.chatWidth);
         var1.println("showInventoryAchievementHint:" + this.showInventoryAchievementHint);
         var1.println("mipmapLevels:" + this.mipmapLevels);
         var1.println("streamBytesPerPixel:" + this.streamBytesPerPixel);
         var1.println("streamMicVolume:" + this.streamMicVolume);
         var1.println("streamSystemVolume:" + this.streamSystemVolume);
         var1.println("streamKbps:" + this.streamKbps);
         var1.println("streamFps:" + this.streamFps);
         var1.println("streamCompression:" + this.streamCompression);
         var1.println("streamSendMetadata:" + this.streamSendMetadata);
         var1.println("streamPreferredServer:" + this.streamPreferredServer);
         var1.println("streamChatEnabled:" + this.streamChatEnabled);
         var1.println("streamChatUserFilter:" + this.streamChatUserFilter);
         var1.println("streamMicToggleBehavior:" + this.streamMicToggleBehavior);
         var1.println("forceUnicodeFont:" + this.forceUnicodeFont);
         var1.println("allowBlockAlternatives:" + this.allowBlockAlternatives);
         var1.println("reducedDebugInfo:" + this.reducedDebugInfo);

         for(KeyBinding var5 : this.ingameKeys) {
            var1.println("key_" + var5.getName() + ":" + var5.getKeyCode());
         }

         for(SoundCategory var13 : SoundCategory.values()) {
            var1.println("soundCategory_" + var13.getName() + ":" + this.getSoundCategoryVolume(var13));
         }

         for(PlayerModelPart var14 : PlayerModelPart.values()) {
            var1.println("modelPart_" + var14.getId() + ":" + this.playerModelParts.contains(var14));
         }

         var1.close();
      } catch (Exception var6) {
         LOGGER.error("Failed to save options", var6);
      }

      this.syncClientSettings();
   }

   public float getSoundCategoryVolume(SoundCategory category) {
      return this.soundCategoryVolumes.containsKey(category) ? this.soundCategoryVolumes.get(category) : 1.0F;
   }

   public void setSoundCategoryVolume(SoundCategory category, float volume) {
      this.client.getSoundManager().setVolume(category, volume);
      this.soundCategoryVolumes.put(category, volume);
   }

   public void syncClientSettings() {
      if (this.client.player != null) {
         int var1 = 0;

         for(PlayerModelPart var3 : this.playerModelParts) {
            var1 |= var3.getFlag();
         }

         this.client
            .player
            .networkHandler
            .sendPacket(new ClientSettingsC2SPacket(this.language, this.viewDistance, this.chatVisibility, this.chatColors, var1));
      }
   }

   public Set getPlayerModelParts() {
      return ImmutableSet.copyOf(this.playerModelParts);
   }

   public void setPlayerModelPart(PlayerModelPart part, boolean enable) {
      if (enable) {
         this.playerModelParts.add(part);
      } else {
         this.playerModelParts.remove(part);
      }

      this.syncClientSettings();
   }

   public void togglePlayerModelPart(PlayerModelPart part) {
      if (!this.getPlayerModelParts().contains(part)) {
         this.playerModelParts.add(part);
      } else {
         this.playerModelParts.remove(part);
      }

      this.syncClientSettings();
   }

   public boolean renderClouds() {
      return this.viewDistance >= 4 && this.renderClouds;
   }

   @Environment(EnvType.CLIENT)
   public static enum Option {
      INVERT_MOUSE("options.invertMouse", false, true),
      SENSITIVITY("options.sensitivity", true, false),
      FOV("options.fov", true, false, 30.0F, 110.0F, 1.0F),
      GAMMA("options.gamma", true, false),
      SATURATION("options.saturation", true, false),
      RENDER_DISTANCE("options.renderDistance", true, false, 2.0F, 16.0F, 1.0F),
      VIEW_BOBBING("options.viewBobbing", false, true),
      ANAGLYPH("options.anaglyph", false, true),
      FRAMERATE_LIMIT("options.framerateLimit", true, false, 10.0F, 260.0F, 10.0F),
      FBO_ENABLE("options.fboEnable", false, true),
      RENDER_CLOUDS("options.renderClouds", false, true),
      GRAPHICS("options.graphics", false, false),
      AMBIENT_OCCLUSION("options.ao", false, false),
      GUI_SCALE("options.guiScale", false, false),
      PARTICLES("options.particles", false, false),
      CHAT_VISIBILITY("options.chat.visibility", false, false),
      CHAT_COLOR("options.chat.color", false, true),
      CHAT_LINKS("options.chat.links", false, true),
      CHAT_OPACITY("options.chat.opacity", true, false),
      CHAT_LINKS_PROMPT("options.chat.links.prompt", false, true),
      SNOOPER_ENABLED("options.snooper", false, true),
      USE_FULLSCREEN("options.fullscreen", false, true),
      ENABLE_VSYNC("options.vsync", false, true),
      USE_VBO("options.vbo", false, true),
      TOUCHSCREEN("options.touchscreen", false, true),
      CHAT_SCALE("options.chat.scale", true, false),
      CHAT_WIDTH("options.chat.width", true, false),
      CHAT_HEIGHT_FOCUSED("options.chat.height.focused", true, false),
      CHAT_HEIGHT_UNFOCUSED("options.chat.height.unfocused", true, false),
      MAPMAP_LEVELS("options.mipmapLevels", true, false, 0.0F, 4.0F, 1.0F),
      FORCE_UNICODE_FONT("options.forceUnicodeFont", false, true),
      STREAM_BYTES_PER_PIXEL("options.stream.bytesPerPixel", true, false),
      STREAM_VOLUME_MIC("options.stream.micVolumne", true, false),
      STREAM_VOLUME_SYSTEM("options.stream.systemVolume", true, false),
      STREAM_KBPS("options.stream.kbps", true, false),
      STREAM_FPS("options.stream.fps", true, false),
      STREAM_COMPRESSION("options.stream.compression", false, false),
      STREAM_SEND_METADATA("options.stream.sendMetadata", false, true),
      STREAM_CHAT_ENABLED("options.stream.chat.enabled", false, false),
      STREAM_CHAT_USER_FILTER("options.stream.chat.userFilter", false, false),
      STREAM_MIC_TOGGLE_BEHAVIOR("options.stream.micToggleBehavior", false, false),
      BLOCK_ALTERNATIVES("options.blockAlternatives", false, true),
      REDUCED_DEBUG_INFO("options.reducedDebugInfo", false, true);

      private final boolean isFloat;
      private final boolean isBoolean;
      private final String name;
      private final float step;
      private float min;
      private float max;

      public static GameOptions.Option byId(int id) {
         for(GameOptions.Option var4 : values()) {
            if (var4.getId() == id) {
               return var4;
            }
         }

         return null;
      }

      private Option(String name, boolean isFloat, boolean isBoolean) {
         this(name, isFloat, isBoolean, 0.0F, 1.0F, 0.0F);
      }

      private Option(String name, boolean isFloat, boolean isBoolean, float min, float max, float step) {
         this.name = name;
         this.isFloat = isFloat;
         this.isBoolean = isBoolean;
         this.min = min;
         this.max = max;
         this.step = step;
      }

      public boolean isFloatOption() {
         return this.isFloat;
      }

      public boolean isBooleanOption() {
         return this.isBoolean;
      }

      public int getId() {
         return this.ordinal();
      }

      public String getName() {
         return this.name;
      }

      public float getMax() {
         return this.max;
      }

      public void setMax(float value) {
         this.max = value;
      }

      public float normalize(float value) {
         return MathHelper.clamp((this.clampAndRoundToStepMultiple(value) - this.min) / (this.max - this.min), 0.0F, 1.0F);
      }

      public float denormalize(float value) {
         return this.clampAndRoundToStepMultiple(this.min + (this.max - this.min) * MathHelper.clamp(value, 0.0F, 1.0F));
      }

      public float clampAndRoundToStepMultiple(float value) {
         value = this.roundToStepMultiple(value);
         return MathHelper.clamp(value, this.min, this.max);
      }

      protected float roundToStepMultiple(float value) {
         if (this.step > 0.0F) {
            value = this.step * (float)Math.round(value / this.step);
         }

         return value;
      }
   }
}
