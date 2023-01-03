package net.minecraft.client.render;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.MemoryTracker;
import com.mojang.blaze3d.shaders.ProgramManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.entity.living.player.ClientPlayerEntity;
import net.minecraft.client.entity.particle.ParticleManager;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.client.render.world.WorldRenderer;
import net.minecraft.client.resource.manager.IResourceManager;
import net.minecraft.client.resource.manager.ResourceReloadListener;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SmoothUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.effect.StatusEffect;
import net.minecraft.entity.living.mob.hostile.CreeperEntity;
import net.minecraft.entity.living.mob.hostile.EndermanEntity;
import net.minecraft.entity.living.mob.hostile.SpiderEntity;
import net.minecraft.entity.living.mob.hostile.boss.BossBar;
import net.minecraft.entity.living.mob.passive.animal.AnimalEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.Identifier;
import net.minecraft.util.HitResult;
import net.minecraft.util.crash.CashReportCategory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.Biome;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;

@Environment(EnvType.CLIENT)
public class GameRenderer implements ResourceReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Identifier RAIN_TEXTURE = new Identifier("textures/environment/rain.png");
   private static final Identifier SNOW_TEXTURE = new Identifier("textures/environment/snow.png");
   public static boolean anaglyphEnabled;
   public static int anaglyphFilter;
   private MinecraftClient client;
   private final IResourceManager resourceManager;
   private Random random = new Random();
   private float viewDistance;
   public final HeldItemRenderer heldItemRenderer;
   private final MapRenderer mapRenderer;
   private int ticks;
   private Entity camera;
   private SmoothUtil smoothUtilMouseX = new SmoothUtil();
   private SmoothUtil smoothUtilMouseY = new SmoothUtil();
   private float oldCamPos = 4.0F;
   private float newCamPos = 4.0F;
   private float totalMouseDX;
   private float totalMouseDY;
   private float smoothMouseDX;
   private float smoothMouseDY;
   private float lastTickDelta;
   private float playerMovmentSpeed;
   private float lastMovementFovMultiplier;
   private float skyDarkness;
   private float lastSkyDarkness;
   private boolean thiccFog;
   private boolean newCamPitch = true;
   private boolean f_97uavlmdx = true;
   private long lastWindowFocusedTime = MinecraftClient.getTime();
   private long lastWorldRenderTime;
   private final NativeImageBackedTexture lightmapTexture;
   private final int[] lightmapTexturePixels;
   private final Identifier lightmapTextureId;
   private boolean lightmapDirty;
   private float oldCamPitch;
   private float lightMapRandoms;
   private int weatherSoundAttempts;
   private float[] weatherChunkDistanceNormalizedX = new float[1024];
   private float[] weatherChunkDistanceNormalizedY = new float[1024];
   private FloatBuffer fogColorBuffer = MemoryTracker.createFloatBuffer(16);
   private float fogRed;
   private float fogGreen;
   private float fogBlue;
   private float newFogGrayScale;
   private float oldFogGrayScale;
   private int hasNausea = 0;
   private boolean debugCamera = false;
   private double zoom = 1.0;
   private double zoomX;
   private double zoomY;
   private PostChain shaderEffect;
   private static final Identifier[] SHADERS = new Identifier[]{
      new Identifier("shaders/post/notch.json"),
      new Identifier("shaders/post/fxaa.json"),
      new Identifier("shaders/post/art.json"),
      new Identifier("shaders/post/bumpy.json"),
      new Identifier("shaders/post/blobs2.json"),
      new Identifier("shaders/post/pencil.json"),
      new Identifier("shaders/post/color_convolve.json"),
      new Identifier("shaders/post/deconverge.json"),
      new Identifier("shaders/post/flip.json"),
      new Identifier("shaders/post/invert.json"),
      new Identifier("shaders/post/ntsc.json"),
      new Identifier("shaders/post/outline.json"),
      new Identifier("shaders/post/phosphor.json"),
      new Identifier("shaders/post/scan_pincushion.json"),
      new Identifier("shaders/post/sobel.json"),
      new Identifier("shaders/post/bits.json"),
      new Identifier("shaders/post/desaturate.json"),
      new Identifier("shaders/post/green.json"),
      new Identifier("shaders/post/blur.json"),
      new Identifier("shaders/post/wobble.json"),
      new Identifier("shaders/post/blobs.json"),
      new Identifier("shaders/post/antialias.json"),
      new Identifier("shaders/post/creeper.json"),
      new Identifier("shaders/post/spider.json")
   };
   public static final int SHADER_COUNT = SHADERS.length;
   private int shaderIndex = SHADER_COUNT;
   private boolean shadersEnabled = false;
   private int f_14zqygcrl = 0;

   public GameRenderer(MinecraftClient client, IResourceManager resourceManager) {
      this.client = client;
      this.resourceManager = resourceManager;
      this.heldItemRenderer = client.getHeldItemRenderer();
      this.mapRenderer = new MapRenderer(client.getTextureManager());
      this.lightmapTexture = new NativeImageBackedTexture(16, 16);
      this.lightmapTextureId = client.getTextureManager().register("lightMap", this.lightmapTexture);
      this.lightmapTexturePixels = this.lightmapTexture.getRgbArray();
      this.shaderEffect = null;

      for(int var3 = 0; var3 < 32; ++var3) {
         for(int var4 = 0; var4 < 32; ++var4) {
            float var5 = (float)(var4 - 16);
            float var6 = (float)(var3 - 16);
            float var7 = MathHelper.sqrt(var5 * var5 + var6 * var6);
            this.weatherChunkDistanceNormalizedX[var3 << 5 | var4] = -var6 / var7;
            this.weatherChunkDistanceNormalizedY[var3 << 5 | var4] = var5 / var7;
         }
      }
   }

   public boolean hasShader() {
      return GLX.usePostProcess && this.shaderEffect != null;
   }

   public void disableShader() {
      this.shadersEnabled = !this.shadersEnabled;
   }

   public void setCamera(Entity entity) {
      if (GLX.usePostProcess) {
         if (this.shaderEffect != null) {
            this.shaderEffect.close();
         }

         this.shaderEffect = null;
         if (entity instanceof CreeperEntity) {
            this.loadShader(new Identifier("shaders/post/creeper.json"));
         } else if (entity instanceof SpiderEntity) {
            this.loadShader(new Identifier("shaders/post/spider.json"));
         } else if (entity instanceof EndermanEntity) {
            this.loadShader(new Identifier("shaders/post/invert.json"));
         }
      }
   }

   public void nextShader() {
      if (GLX.usePostProcess) {
         if (this.client.getCamera() instanceof PlayerEntity) {
            if (this.shaderEffect != null) {
               this.shaderEffect.close();
            }

            this.shaderIndex = (this.shaderIndex + 1) % (SHADERS.length + 1);
            if (this.shaderIndex != SHADER_COUNT) {
               this.loadShader(SHADERS[this.shaderIndex]);
            } else {
               this.shaderEffect = null;
            }
         }
      }
   }

   private void loadShader(Identifier identifier) {
      try {
         this.shaderEffect = new PostChain(this.client.getTextureManager(), this.resourceManager, this.client.getRenderTarget(), identifier);
         this.shaderEffect.resize(this.client.width, this.client.height);
         this.shadersEnabled = true;
      } catch (IOException var3) {
         LOGGER.warn("Failed to load shader: " + identifier, var3);
         this.shaderIndex = SHADER_COUNT;
         this.shadersEnabled = false;
      } catch (JsonSyntaxException var4) {
         LOGGER.warn("Failed to load shader: " + identifier, var4);
         this.shaderIndex = SHADER_COUNT;
         this.shadersEnabled = false;
      }
   }

   @Override
   public void reload(IResourceManager resourceManager) {
      if (this.shaderEffect != null) {
         this.shaderEffect.close();
      }

      this.shaderEffect = null;
      if (this.shaderIndex != SHADER_COUNT) {
         this.loadShader(SHADERS[this.shaderIndex]);
      } else {
         this.setCamera(this.client.getCamera());
      }
   }

   public void tick() {
      if (GLX.usePostProcess && ProgramManager.getInstance() == null) {
         ProgramManager.createInstance();
      }

      this.updateMovementFovMultiplier();
      this.updateLightmap();
      this.newFogGrayScale = this.oldFogGrayScale;
      this.newCamPos = this.oldCamPos;
      if (this.client.options.smoothCamera) {
         float var1 = this.client.options.mouseSensitivity * 0.6F + 0.2F;
         float var2 = var1 * var1 * var1 * 8.0F;
         this.smoothMouseDX = this.smoothUtilMouseX.smooth(this.totalMouseDX, 0.05F * var2);
         this.smoothMouseDY = this.smoothUtilMouseY.smooth(this.totalMouseDY, 0.05F * var2);
         this.lastTickDelta = 0.0F;
         this.totalMouseDX = 0.0F;
         this.totalMouseDY = 0.0F;
      }

      if (this.client.getCamera() == null) {
         this.client.setCamera(this.client.player);
      }

      float var4 = this.client.world.getBrightness(new BlockPos(this.client.getCamera()));
      float var5 = (float)this.client.options.viewDistance / 32.0F;
      float var3 = var4 * (1.0F - var5) + var5;
      this.oldFogGrayScale += (var3 - this.oldFogGrayScale) * 0.1F;
      ++this.ticks;
      this.heldItemRenderer.updateHeldItem();
      this.tickWaterSplashing();
      this.lastSkyDarkness = this.skyDarkness;
      if (BossBar.modifiesSkyColor) {
         this.skyDarkness += 0.05F;
         if (this.skyDarkness > 1.0F) {
            this.skyDarkness = 1.0F;
         }

         BossBar.modifiesSkyColor = false;
      } else if (this.skyDarkness > 0.0F) {
         this.skyDarkness -= 0.0125F;
      }
   }

   public PostChain getShader() {
      return this.shaderEffect;
   }

   public void onResolutionChanged(int width, int height) {
      if (GLX.usePostProcess) {
         if (this.shaderEffect != null) {
            this.shaderEffect.resize(width, height);
         }

         this.client.worldRenderer.reziseEntityOutlinePostChain(width, height);
      }
   }

   public void updateTargetEntity(float tickDelta) {
      Entity var2 = this.client.getCamera();
      if (var2 != null) {
         if (this.client.world != null) {
            this.client.profiler.push("pick");
            this.client.targetEntity = null;
            double var3 = (double)this.client.interactionManager.getReach();
            this.client.crosshairTarget = var2.rayTrace(var3, tickDelta);
            double var5 = var3;
            Vec3d var7 = var2.m_24itdohjr(tickDelta);
            if (this.client.interactionManager.hasExtendedReach()) {
               var3 = 6.0;
               var5 = 6.0;
            } else {
               if (var3 > 3.0) {
                  var5 = 3.0;
               }

               var3 = var5;
            }

            if (this.client.crosshairTarget != null) {
               var5 = this.client.crosshairTarget.pos.distanceTo(var7);
            }

            Vec3d var8 = var2.m_01qqqsfds(tickDelta);
            Vec3d var9 = var7.add(var8.x * var3, var8.y * var3, var8.z * var3);
            this.camera = null;
            Vec3d var10 = null;
            float var11 = 1.0F;
            List var12 = this.client
               .world
               .getEntities(var2, var2.getBoundingBox().grow(var8.x * var3, var8.y * var3, var8.z * var3).expand((double)var11, (double)var11, (double)var11));
            double var13 = var5;

            for(int var15 = 0; var15 < var12.size(); ++var15) {
               Entity var16 = (Entity)var12.get(var15);
               if (var16.hasCollision()) {
                  float var17 = var16.getExtraHitboxSize();
                  Box var18 = var16.getBoundingBox().expand((double)var17, (double)var17, (double)var17);
                  HitResult var19 = var18.clip(var7, var9);
                  if (var18.contains(var7)) {
                     if (0.0 < var13 || var13 == 0.0) {
                        this.camera = var16;
                        var10 = var19 == null ? var7 : var19.pos;
                        var13 = 0.0;
                     }
                  } else if (var19 != null) {
                     double var20 = var7.distanceTo(var19.pos);
                     if (var20 < var13 || var13 == 0.0) {
                        if (var16 == var2.vehicle) {
                           if (var13 == 0.0) {
                              this.camera = var16;
                              var10 = var19.pos;
                           }
                        } else {
                           this.camera = var16;
                           var10 = var19.pos;
                           var13 = var20;
                        }
                     }
                  }
               }
            }

            if (this.camera != null && (var13 < var5 || this.client.crosshairTarget == null)) {
               this.client.crosshairTarget = new HitResult(this.camera, var10);
               if (this.camera instanceof LivingEntity || this.camera instanceof ItemFrameEntity) {
                  this.client.targetEntity = this.camera;
               }
            }

            this.client.profiler.pop();
         }
      }
   }

   private void updateMovementFovMultiplier() {
      float var1 = 1.0F;
      if (this.client.getCamera() instanceof ClientPlayerEntity) {
         ClientPlayerEntity var2 = (ClientPlayerEntity)this.client.getCamera();
         var1 = var2.getFovModifier();
      }

      this.lastMovementFovMultiplier = this.playerMovmentSpeed;
      this.playerMovmentSpeed += (var1 - this.playerMovmentSpeed) * 0.5F;
      if (this.playerMovmentSpeed > 1.5F) {
         this.playerMovmentSpeed = 1.5F;
      }

      if (this.playerMovmentSpeed < 0.1F) {
         this.playerMovmentSpeed = 0.1F;
      }
   }

   private float getFov(float tickDelta, boolean fovChanged) {
      if (this.debugCamera) {
         return 90.0F;
      } else {
         Entity var3 = this.client.getCamera();
         float var4 = 70.0F;
         if (fovChanged) {
            var4 = this.client.options.fov;
            var4 *= this.lastMovementFovMultiplier + (this.playerMovmentSpeed - this.lastMovementFovMultiplier) * tickDelta;
         }

         if (var3 instanceof LivingEntity && ((LivingEntity)var3).getHealth() <= 0.0F) {
            float var5 = (float)((LivingEntity)var3).deathTicks + tickDelta;
            var4 /= (1.0F - 500.0F / (var5 + 500.0F)) * 2.0F + 1.0F;
         }

         Block var7 = Camera.getLiquidInside(this.client.world, var3, tickDelta);
         if (var7.getMaterial() == Material.WATER) {
            var4 = var4 * 60.0F / 70.0F;
         }

         return var4;
      }
   }

   private void applyHurtCam(float tickDelta) {
      if (this.client.getCamera() instanceof LivingEntity) {
         LivingEntity var2 = (LivingEntity)this.client.getCamera();
         float var3 = (float)var2.hurtTimer - tickDelta;
         if (var2.getHealth() <= 0.0F) {
            float var4 = (float)var2.deathTicks + tickDelta;
            GlStateManager.rotatef(40.0F - 8000.0F / (var4 + 200.0F), 0.0F, 0.0F, 1.0F);
         }

         if (var3 < 0.0F) {
            return;
         }

         var3 /= (float)var2.hurtAnimationTicks;
         var3 = MathHelper.sin(var3 * var3 * var3 * var3 * (float) Math.PI);
         float var7 = var2.knockbackVelocity;
         GlStateManager.rotatef(-var7, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(-var3 * 14.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotatef(var7, 0.0F, 1.0F, 0.0F);
      }
   }

   private void applyViewBobbing(float tickDelta) {
      if (this.client.getCamera() instanceof PlayerEntity) {
         PlayerEntity var2 = (PlayerEntity)this.client.getCamera();
         float var3 = var2.horizontalVelocity - var2.prevHorizontalSpeed;
         float var4 = -(var2.horizontalVelocity + var3 * tickDelta);
         float var5 = var2.prevStrideDistance + (var2.strideDistance - var2.prevStrideDistance) * tickDelta;
         float var6 = var2.prevCameraPitch + (var2.cameraPitch - var2.prevCameraPitch) * tickDelta;
         GlStateManager.translatef(MathHelper.sin(var4 * (float) Math.PI) * var5 * 0.5F, -Math.abs(MathHelper.cos(var4 * (float) Math.PI) * var5), 0.0F);
         GlStateManager.rotatef(MathHelper.sin(var4 * (float) Math.PI) * var5 * 3.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotatef(Math.abs(MathHelper.cos(var4 * (float) Math.PI - 0.2F) * var5) * 5.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(var6, 1.0F, 0.0F, 0.0F);
      }
   }

   private void transformCamera(float tickDelta) {
      Entity var2 = this.client.getCamera();
      float var3 = var2.getEyeHeight();
      double var4 = var2.prevX + (var2.x - var2.prevX) * (double)tickDelta;
      double var6 = var2.prevY + (var2.y - var2.prevY) * (double)tickDelta + (double)var3;
      double var8 = var2.prevZ + (var2.z - var2.prevZ) * (double)tickDelta;
      if (var2 instanceof LivingEntity && ((LivingEntity)var2).isSleeping()) {
         var3 = (float)((double)var3 + 1.0);
         GlStateManager.translatef(0.0F, 0.3F, 0.0F);
         if (!this.client.options.debugCamera) {
            BlockPos var30 = new BlockPos(var2);
            BlockState var11 = this.client.world.getBlockState(var30);
            Block var32 = var11.getBlock();
            if (var32 == Blocks.BED) {
               int var33 = ((Direction)var11.get(BedBlock.FACING)).getIdHorizontal();
               GlStateManager.rotatef((float)(var33 * 90), 0.0F, 1.0F, 0.0F);
            }

            GlStateManager.rotatef(var2.prevYaw + (var2.yaw - var2.prevYaw) * tickDelta + 180.0F, 0.0F, -1.0F, 0.0F);
            GlStateManager.rotatef(var2.prevPitch + (var2.pitch - var2.prevPitch) * tickDelta, -1.0F, 0.0F, 0.0F);
         }
      } else if (this.client.options.perspective > 0) {
         double var10 = (double)(this.newCamPos + (this.oldCamPos - this.newCamPos) * tickDelta);
         if (this.client.options.debugCamera) {
            GlStateManager.translatef(0.0F, 0.0F, (float)(-var10));
         } else {
            float var12 = var2.yaw;
            float var13 = var2.pitch;
            if (this.client.options.perspective == 2) {
               var13 += 180.0F;
            }

            double var14 = (double)(-MathHelper.sin(var12 / 180.0F * (float) Math.PI) * MathHelper.cos(var13 / 180.0F * (float) Math.PI)) * var10;
            double var16 = (double)(MathHelper.cos(var12 / 180.0F * (float) Math.PI) * MathHelper.cos(var13 / 180.0F * (float) Math.PI)) * var10;
            double var18 = (double)(-MathHelper.sin(var13 / 180.0F * (float) Math.PI)) * var10;

            for(int var20 = 0; var20 < 8; ++var20) {
               float var21 = (float)((var20 & 1) * 2 - 1);
               float var22 = (float)((var20 >> 1 & 1) * 2 - 1);
               float var23 = (float)((var20 >> 2 & 1) * 2 - 1);
               var21 *= 0.1F;
               var22 *= 0.1F;
               var23 *= 0.1F;
               HitResult var24 = this.client
                  .world
                  .rayTrace(
                     new Vec3d(var4 + (double)var21, var6 + (double)var22, var8 + (double)var23),
                     new Vec3d(var4 - var14 + (double)var21 + (double)var23, var6 - var18 + (double)var22, var8 - var16 + (double)var23)
                  );
               if (var24 != null) {
                  double var25 = var24.pos.distanceTo(new Vec3d(var4, var6, var8));
                  if (var25 < var10) {
                     var10 = var25;
                  }
               }
            }

            if (this.client.options.perspective == 2) {
               GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
            }

            GlStateManager.rotatef(var2.pitch - var13, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(var2.yaw - var12, 0.0F, 1.0F, 0.0F);
            GlStateManager.translatef(0.0F, 0.0F, (float)(-var10));
            GlStateManager.rotatef(var12 - var2.yaw, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(var13 - var2.pitch, 1.0F, 0.0F, 0.0F);
         }
      } else {
         GlStateManager.translatef(0.0F, 0.0F, -0.1F);
      }

      if (!this.client.options.debugCamera) {
         GlStateManager.rotatef(var2.prevPitch + (var2.pitch - var2.prevPitch) * tickDelta, 1.0F, 0.0F, 0.0F);
         if (var2 instanceof AnimalEntity) {
            AnimalEntity var31 = (AnimalEntity)var2;
            GlStateManager.rotatef(var31.prevHeadYaw + (var31.headYaw - var31.prevHeadYaw) * tickDelta + 180.0F, 0.0F, 1.0F, 0.0F);
         } else {
            GlStateManager.rotatef(var2.prevYaw + (var2.yaw - var2.prevYaw) * tickDelta + 180.0F, 0.0F, 1.0F, 0.0F);
         }
      }

      GlStateManager.translatef(0.0F, -var3, 0.0F);
      var4 = var2.prevX + (var2.x - var2.prevX) * (double)tickDelta;
      var6 = var2.prevY + (var2.y - var2.prevY) * (double)tickDelta + (double)var3;
      var8 = var2.prevZ + (var2.z - var2.prevZ) * (double)tickDelta;
      this.thiccFog = this.client.worldRenderer.hasThiccFog(var4, var6, var8, tickDelta);
   }

   private void updateCamera(float tickDelta, int anaglyphRenderPass) {
      this.viewDistance = (float)(this.client.options.viewDistance * 16);
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      float var3 = 0.07F;
      if (this.client.options.anaglyph) {
         GlStateManager.translatef((float)(-(anaglyphRenderPass * 2 - 1)) * var3, 0.0F, 0.0F);
      }

      if (this.zoom != 1.0) {
         GlStateManager.translatef((float)this.zoomX, (float)(-this.zoomY), 0.0F);
         GlStateManager.scaled(this.zoom, this.zoom, 1.0);
      }

      Project.gluPerspective(this.getFov(tickDelta, true), (float)this.client.width / (float)this.client.height, 0.05F, this.viewDistance * MathHelper.SQRT_TWO);
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      if (this.client.options.anaglyph) {
         GlStateManager.translatef((float)(anaglyphRenderPass * 2 - 1) * 0.1F, 0.0F, 0.0F);
      }

      this.applyHurtCam(tickDelta);
      if (this.client.options.viewBobbing) {
         this.applyViewBobbing(tickDelta);
      }

      float var4 = this.client.player.oldNetherPortalDuration
         + (this.client.player.netherPortalDuration - this.client.player.oldNetherPortalDuration) * tickDelta;
      if (var4 > 0.0F) {
         byte var5 = 20;
         if (this.client.player.hasStatusEffect(StatusEffect.NAUSEA)) {
            var5 = 7;
         }

         float var6 = 5.0F / (var4 * var4 + 5.0F) - var4 * 0.04F;
         var6 *= var6;
         GlStateManager.rotatef(((float)this.ticks + tickDelta) * (float)var5, 0.0F, 1.0F, 1.0F);
         GlStateManager.scalef(1.0F / var6, 1.0F, 1.0F);
         GlStateManager.rotatef(-((float)this.ticks + tickDelta) * (float)var5, 0.0F, 1.0F, 1.0F);
      }

      this.transformCamera(tickDelta);
      if (this.debugCamera) {
         switch(this.hasNausea) {
            case 0:
               GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
               break;
            case 1:
               GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
               break;
            case 2:
               GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
               break;
            case 3:
               GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
               break;
            case 4:
               GlStateManager.rotatef(-90.0F, 1.0F, 0.0F, 0.0F);
         }
      }
   }

   private void renderHand(float tickDelta, int anaglyphFilter) {
      if (!this.debugCamera) {
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         float var3 = 0.07F;
         if (this.client.options.anaglyph) {
            GlStateManager.translatef((float)(-(anaglyphFilter * 2 - 1)) * var3, 0.0F, 0.0F);
         }

         Project.gluPerspective(this.getFov(tickDelta, false), (float)this.client.width / (float)this.client.height, 0.05F, this.viewDistance * 2.0F);
         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
         if (this.client.options.anaglyph) {
            GlStateManager.translatef((float)(anaglyphFilter * 2 - 1) * 0.1F, 0.0F, 0.0F);
         }

         GlStateManager.pushMatrix();
         this.applyHurtCam(tickDelta);
         if (this.client.options.viewBobbing) {
            this.applyViewBobbing(tickDelta);
         }

         boolean var4 = this.client.getCamera() instanceof LivingEntity && ((LivingEntity)this.client.getCamera()).isSleeping();
         if (this.client.options.perspective == 0 && !var4 && !this.client.options.hudEnabled && !this.client.interactionManager.isInSpectatorMode()) {
            this.drawLightmap();
            this.heldItemRenderer.renderInFirstPerson(tickDelta);
            this.disableTexture2D();
         }

         GlStateManager.popMatrix();
         if (this.client.options.perspective == 0 && !var4) {
            this.heldItemRenderer.renderScreenSpaceEffects(tickDelta);
            this.applyHurtCam(tickDelta);
         }

         if (this.client.options.viewBobbing) {
            this.applyViewBobbing(tickDelta);
         }
      }
   }

   public void disableTexture2D() {
      GlStateManager.activeTexture(GLX.GL_TEXTURE1);
      GlStateManager.disableTexture();
      GlStateManager.activeTexture(GLX.GL_TEXTURE0);
   }

   public void drawLightmap() {
      GlStateManager.activeTexture(GLX.GL_TEXTURE1);
      GlStateManager.matrixMode(5890);
      GlStateManager.loadIdentity();
      float var1 = 0.00390625F;
      GlStateManager.scalef(var1, var1, var1);
      GlStateManager.translatef(8.0F, 8.0F, 8.0F);
      GlStateManager.matrixMode(5888);
      this.client.getTextureManager().bind(this.lightmapTextureId);
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      GL11.glTexParameteri(3553, 10242, 10496);
      GL11.glTexParameteri(3553, 10243, 10496);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableTexture();
      GlStateManager.activeTexture(GLX.GL_TEXTURE0);
   }

   private void updateLightmap() {
      this.lightMapRandoms = (float)((double)this.lightMapRandoms + (Math.random() - Math.random()) * Math.random() * Math.random());
      this.lightMapRandoms = (float)((double)this.lightMapRandoms * 0.9);
      this.oldCamPitch += (this.lightMapRandoms - this.oldCamPitch) * 1.0F;
      this.lightmapDirty = true;
   }

   private void cleanLightmap(float tickDelta) {
      if (this.lightmapDirty) {
         this.client.profiler.push("lightTex");
         ClientWorld var2 = this.client.world;
         if (var2 != null) {
            for(int var3 = 0; var3 < 256; ++var3) {
               float var4 = var2.calculateAmbientLight(1.0F) * 0.95F + 0.05F;
               float var5 = var2.dimension.getBrightnessTable()[var3 / 16] * var4;
               float var6 = var2.dimension.getBrightnessTable()[var3 % 16] * (this.oldCamPitch * 0.1F + 1.5F);
               if (var2.getLightningCooldown() > 0) {
                  var5 = var2.dimension.getBrightnessTable()[var3 / 16];
               }

               float var7 = var5 * (var2.calculateAmbientLight(1.0F) * 0.65F + 0.35F);
               float var8 = var5 * (var2.calculateAmbientLight(1.0F) * 0.65F + 0.35F);
               float var11 = var6 * ((var6 * 0.6F + 0.4F) * 0.6F + 0.4F);
               float var12 = var6 * (var6 * var6 * 0.6F + 0.4F);
               float var13 = var7 + var6;
               float var14 = var8 + var11;
               float var15 = var5 + var12;
               var13 = var13 * 0.96F + 0.03F;
               var14 = var14 * 0.96F + 0.03F;
               var15 = var15 * 0.96F + 0.03F;
               if (this.skyDarkness > 0.0F) {
                  float var16 = this.lastSkyDarkness + (this.skyDarkness - this.lastSkyDarkness) * tickDelta;
                  var13 = var13 * (1.0F - var16) + var13 * 0.7F * var16;
                  var14 = var14 * (1.0F - var16) + var14 * 0.6F * var16;
                  var15 = var15 * (1.0F - var16) + var15 * 0.6F * var16;
               }

               if (var2.dimension.getId() == 1) {
                  var13 = 0.22F + var6 * 0.75F;
                  var14 = 0.28F + var11 * 0.75F;
                  var15 = 0.25F + var12 * 0.75F;
               }

               if (this.client.player.hasStatusEffect(StatusEffect.NIGHTVISION)) {
                  float var33 = this.getNightVisionLightOffset(this.client.player, tickDelta);
                  float var17 = 1.0F / var13;
                  if (var17 > 1.0F / var14) {
                     var17 = 1.0F / var14;
                  }

                  if (var17 > 1.0F / var15) {
                     var17 = 1.0F / var15;
                  }

                  var13 = var13 * (1.0F - var33) + var13 * var17 * var33;
                  var14 = var14 * (1.0F - var33) + var14 * var17 * var33;
                  var15 = var15 * (1.0F - var33) + var15 * var17 * var33;
               }

               if (var13 > 1.0F) {
                  var13 = 1.0F;
               }

               if (var14 > 1.0F) {
                  var14 = 1.0F;
               }

               if (var15 > 1.0F) {
                  var15 = 1.0F;
               }

               float var34 = this.client.options.gamma;
               float var35 = 1.0F - var13;
               float var18 = 1.0F - var14;
               float var19 = 1.0F - var15;
               var35 = 1.0F - var35 * var35 * var35 * var35;
               var18 = 1.0F - var18 * var18 * var18 * var18;
               var19 = 1.0F - var19 * var19 * var19 * var19;
               var13 = var13 * (1.0F - var34) + var35 * var34;
               var14 = var14 * (1.0F - var34) + var18 * var34;
               var15 = var15 * (1.0F - var34) + var19 * var34;
               var13 = var13 * 0.96F + 0.03F;
               var14 = var14 * 0.96F + 0.03F;
               var15 = var15 * 0.96F + 0.03F;
               if (var13 > 1.0F) {
                  var13 = 1.0F;
               }

               if (var14 > 1.0F) {
                  var14 = 1.0F;
               }

               if (var15 > 1.0F) {
                  var15 = 1.0F;
               }

               if (var13 < 0.0F) {
                  var13 = 0.0F;
               }

               if (var14 < 0.0F) {
                  var14 = 0.0F;
               }

               if (var15 < 0.0F) {
                  var15 = 0.0F;
               }

               short var20 = 255;
               int var21 = (int)(var13 * 255.0F);
               int var22 = (int)(var14 * 255.0F);
               int var23 = (int)(var15 * 255.0F);
               this.lightmapTexturePixels[var3] = var20 << 24 | var21 << 16 | var22 << 8 | var23;
            }

            this.lightmapTexture.upload();
            this.lightmapDirty = false;
            this.client.profiler.pop();
         }
      }
   }

   private float getNightVisionLightOffset(LivingEntity player, float tickDelta) {
      int var3 = player.getEffectInstance(StatusEffect.NIGHTVISION).getDuration();
      return var3 > 200 ? 1.0F : 0.7F + MathHelper.sin(((float)var3 - tickDelta) * (float) Math.PI * 0.2F) * 0.3F;
   }

   public void render(float tickDelta) {
      boolean var2 = Display.isActive();
      if (!var2 && this.client.options.pauseOnUnfocus && (!this.client.options.touchscreen || !Mouse.isButtonDown(1))) {
         if (MinecraftClient.getTime() - this.lastWindowFocusedTime > 500L) {
            this.client.openGameMenuScreen();
         }
      } else {
         this.lastWindowFocusedTime = MinecraftClient.getTime();
      }

      this.client.profiler.push("mouse");
      if (this.client.focused && var2) {
         this.client.mouse.tick();
         float var3 = this.client.options.mouseSensitivity * 0.6F + 0.2F;
         float var4 = var3 * var3 * var3 * 8.0F;
         float var5 = (float)this.client.mouse.dx * var4;
         float var6 = (float)this.client.mouse.dy * var4;
         byte var7 = 1;
         if (this.client.options.invertMouseY) {
            var7 = -1;
         }

         if (this.client.options.smoothCamera) {
            this.totalMouseDX += var5;
            this.totalMouseDY += var6;
            float var8 = tickDelta - this.lastTickDelta;
            this.lastTickDelta = tickDelta;
            var5 = this.smoothMouseDX * var8;
            var6 = this.smoothMouseDY * var8;
            this.client.player.updateSmoothCamera(var5, var6 * (float)var7);
         } else {
            this.client.player.updateSmoothCamera(var5, var6 * (float)var7);
         }
      }

      this.client.profiler.pop();
      if (!this.client.skipGameRender) {
         anaglyphEnabled = this.client.options.anaglyph;
         final Window var13 = new Window(this.client, this.client.width, this.client.height);
         int var14 = var13.getWidth();
         int var16 = var13.getHeight();
         final int var18 = Mouse.getX() * var14 / this.client.width;
         final int var19 = var16 - Mouse.getY() * var16 / this.client.height - 1;
         int var20 = this.client.options.frameLimit;
         if (this.client.world != null) {
            this.client.profiler.push("level");
            int var9 = Math.max(MinecraftClient.getCurrentFps(), 30);
            this.renderWorld(tickDelta, this.lastWorldRenderTime + (long)(1000000000 / var9));
            if (GLX.usePostProcess) {
               this.client.worldRenderer.renderSpectatorOutlines();
               if (this.shaderEffect != null && this.shadersEnabled) {
                  GlStateManager.matrixMode(5890);
                  GlStateManager.pushMatrix();
                  GlStateManager.loadIdentity();
                  this.shaderEffect.process(tickDelta);
                  GlStateManager.popMatrix();
               }

               this.client.getRenderTarget().bindWrite(true);
            }

            this.lastWorldRenderTime = System.nanoTime();
            this.client.profiler.swap("gui");
            if (!this.client.options.hudEnabled || this.client.currentScreen != null) {
               GlStateManager.alphaFunc(516, 0.1F);
               this.client.gui.render(tickDelta);
            }

            this.client.profiler.pop();
         } else {
            GlStateManager.viewport(0, 0, this.client.width, this.client.height);
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            this.setupHudMatrixMode();
            this.lastWorldRenderTime = System.nanoTime();
         }

         if (this.client.currentScreen != null) {
            GlStateManager.clear(256);

            try {
               this.client.currentScreen.render(var18, var19, tickDelta);
            } catch (Throwable var12) {
               CrashReport var10 = CrashReport.of(var12, "Rendering screen");
               CashReportCategory var11 = var10.addCategory("Screen render details");
               var11.add("Screen name", new Callable() {
                  public String call() {
                     return GameRenderer.this.client.currentScreen.getClass().getCanonicalName();
                  }
               });
               var11.add("Mouse location", new Callable() {
                  public String call() {
                     return String.format("Scaled: (%d, %d). Absolute: (%d, %d)", var18, var19, Mouse.getX(), Mouse.getY());
                  }
               });
               var11.add(
                  "Screen size",
                  new Callable() {
                     public String call() {
                        return String.format(
                           "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %d",
                           var13.getWidth(),
                           var13.getHeight(),
                           GameRenderer.this.client.width,
                           GameRenderer.this.client.height,
                           var13.getScale()
                        );
                     }
                  }
               );
               throw new CrashException(var10);
            }
         }
      }
   }

   public void m_85ssvlslu(float f) {
      this.setupHudMatrixMode();
      this.client.gui.renderStreamOverlay(new Window(this.client, this.client.width, this.client.height));
   }

   private boolean m_54tdgtjcv() {
      if (!this.f_97uavlmdx) {
         return false;
      } else {
         Entity var1 = this.client.getCamera();
         boolean var2 = var1 instanceof PlayerEntity && !this.client.options.hudEnabled;
         if (var2 && !((PlayerEntity)var1).abilities.canModifyWorld) {
            ItemStack var3 = ((PlayerEntity)var1).getMainHandStack();
            if (this.client.crosshairTarget != null && this.client.crosshairTarget.type == HitResult.Type.BLOCK) {
               BlockPos var4 = this.client.crosshairTarget.getBlockPos();
               Block var5 = this.client.world.getBlockState(var4).getBlock();
               if (this.client.interactionManager.getGameMode() == WorldSettings.GameMode.SPECTATOR) {
                  var2 = var5.hasBlockEntity() && this.client.world.getBlockEntity(var4) instanceof Inventory;
               } else {
                  var2 = var3 != null && (var3.hasMineBlockOverride(var5) || var3.hasPlaceOnBlockOverride(var5));
               }
            }
         }

         return var2;
      }
   }

   private void m_77ehnszge(float f) {
      if (this.client.options.debugEnabled
         && !this.client.options.hudEnabled
         && !this.client.player.hasReducedDebugInfo()
         && !this.client.options.reducedDebugInfo) {
         Entity var2 = this.client.getCamera();
         GlStateManager.disableBlend();
         GlStateManager.blendFuncSeparate(770, 771, 1, 0);
         GL11.glLineWidth(1.0F);
         GlStateManager.disableTexture();
         GlStateManager.depthMask(false);
         GlStateManager.pushMatrix();
         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
         this.transformCamera(f);
         GlStateManager.translatef(0.0F, var2.getEyeHeight(), 0.0F);
         WorldRenderer.renderHitbox(new Box(0.0, 0.0, 0.0, 0.005, 1.0E-4, 1.0E-4), -65536);
         WorldRenderer.renderHitbox(new Box(0.0, 0.0, 0.0, 1.0E-4, 1.0E-4, 0.005), -16776961);
         WorldRenderer.renderHitbox(new Box(0.0, 0.0, 0.0, 1.0E-4, 0.0033, 1.0E-4), -16711936);
         GlStateManager.popMatrix();
         GlStateManager.depthMask(true);
         GlStateManager.enableTexture();
         GlStateManager.enableBlend();
      }
   }

   public void renderWorld(float tickDelta, long renderTimeLimit) {
      this.cleanLightmap(tickDelta);
      if (this.client.getCamera() == null) {
         this.client.setCamera(this.client.player);
      }

      this.updateTargetEntity(tickDelta);
      GlStateManager.disableDepth();
      GlStateManager.enableAlphaTest();
      GlStateManager.alphaFunc(516, 0.5F);
      this.client.profiler.push("center");
      if (this.client.options.anaglyph) {
         anaglyphFilter = 0;
         GlStateManager.colorMask(false, true, true, false);
         this.m_57wgxspgp(0, tickDelta, renderTimeLimit);
         anaglyphFilter = 1;
         GlStateManager.colorMask(true, false, false, false);
         this.m_57wgxspgp(1, tickDelta, renderTimeLimit);
         GlStateManager.colorMask(true, true, true, false);
      } else {
         this.m_57wgxspgp(2, tickDelta, renderTimeLimit);
      }

      this.client.profiler.pop();
   }

   private void m_57wgxspgp(int anaglyphRenderPass, float tickDelta, long renderTimeLimit) {
      WorldRenderer var5 = this.client.worldRenderer;
      ParticleManager var6 = this.client.particleManager;
      boolean var7 = this.m_54tdgtjcv();
      GlStateManager.enableCull();
      this.client.profiler.swap("clear");
      GlStateManager.viewport(0, 0, this.client.width, this.client.height);
      this.renderBackground(tickDelta);
      GlStateManager.clear(16640);
      this.client.profiler.swap("camera");
      this.updateCamera(tickDelta, anaglyphRenderPass);
      Camera.setup(this.client.player, this.client.options.perspective == 2);
      this.client.profiler.swap("frustum");
      Frustum.getInstance();
      this.client.profiler.swap("culling");
      FrustumCuller var8 = new FrustumCuller();
      Entity var9 = this.client.getCamera();
      double var10 = var9.prevTickX + (var9.x - var9.prevTickX) * (double)tickDelta;
      double var12 = var9.prevTickY + (var9.y - var9.prevTickY) * (double)tickDelta;
      double var14 = var9.prevTickZ + (var9.z - var9.prevTickZ) * (double)tickDelta;
      var8.set(var10, var12, var14);
      if (this.client.options.viewDistance >= 4) {
         this.renderFog(-1, tickDelta);
         this.client.profiler.swap("sky");
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         Project.gluPerspective(this.getFov(tickDelta, true), (float)this.client.width / (float)this.client.height, 0.05F, this.viewDistance * 2.0F);
         GlStateManager.matrixMode(5888);
         var5.renderSky(tickDelta, anaglyphRenderPass);
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         Project.gluPerspective(
            this.getFov(tickDelta, true), (float)this.client.width / (float)this.client.height, 0.05F, this.viewDistance * MathHelper.SQRT_TWO
         );
         GlStateManager.matrixMode(5888);
      }

      this.renderFog(0, tickDelta);
      GlStateManager.shadeModel(7425);
      if (var9.y < 128.0) {
         this.renderAboveClouds(var5, tickDelta, anaglyphRenderPass);
      }

      this.client.profiler.swap("prepareterrain");
      this.renderFog(0, tickDelta);
      this.client.getTextureManager().bind(SpriteAtlasTexture.BLOCK_ATLAS_BLOCKS);
      Lighting.turnOff();
      this.client.profiler.swap("terrain_setup");
      var5.m_74gpvqpse(var9, (double)tickDelta, var8, this.f_14zqygcrl++);
      if (anaglyphRenderPass == 0 || anaglyphRenderPass == 2) {
         this.client.profiler.swap("updatechunks");
         this.client.worldRenderer.m_06udktacf(renderTimeLimit);
      }

      this.client.profiler.swap("terrain");
      GlStateManager.matrixMode(5888);
      GlStateManager.pushMatrix();
      GlStateManager.disableAlphaTest();
      var5.m_86stejbes(BlockLayer.SOLID, (double)tickDelta, anaglyphRenderPass, var9);
      GlStateManager.enableAlphaTest();
      var5.m_86stejbes(BlockLayer.CUTOUT_MIPPED, (double)tickDelta, anaglyphRenderPass, var9);
      this.client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_BLOCKS).m_60hztdglb(false, false);
      var5.m_86stejbes(BlockLayer.CUTOUT, (double)tickDelta, anaglyphRenderPass, var9);
      this.client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_BLOCKS).m_42jngdvts();
      GlStateManager.shadeModel(7424);
      GlStateManager.alphaFunc(516, 0.1F);
      if (!this.debugCamera) {
         GlStateManager.matrixMode(5888);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         Lighting.turnOn();
         this.client.profiler.swap("entities");
         var5.renderEntities(var9, var8, tickDelta);
         Lighting.turnOff();
         this.disableTexture2D();
         GlStateManager.matrixMode(5888);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         if (this.client.crosshairTarget != null && var9.isSubmergedIn(Material.WATER) && var7) {
            PlayerEntity var16 = (PlayerEntity)var9;
            GlStateManager.disableAlphaTest();
            this.client.profiler.swap("outline");
            var5.renderBlockOutline(var16, this.client.crosshairTarget, 0, tickDelta);
            GlStateManager.enableAlphaTest();
         }
      }

      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      if (var7 && this.client.crosshairTarget != null && !var9.isSubmergedIn(Material.WATER)) {
         PlayerEntity var17 = (PlayerEntity)var9;
         GlStateManager.disableAlphaTest();
         this.client.profiler.swap("outline");
         var5.renderBlockOutline(var17, this.client.crosshairTarget, 0, tickDelta);
         GlStateManager.enableAlphaTest();
      }

      this.client.profiler.swap("destroyProgress");
      GlStateManager.disableBlend();
      GlStateManager.blendFuncSeparate(770, 1, 1, 0);
      var5.renderMiningProgress(Tessellator.getInstance(), Tessellator.getInstance().getBufferBuilder(), var9, tickDelta);
      GlStateManager.enableBlend();
      if (!this.debugCamera) {
         this.drawLightmap();
         this.client.profiler.swap("litParticles");
         var6.renderLitParticles(var9, tickDelta);
         Lighting.turnOff();
         this.renderFog(0, tickDelta);
         this.client.profiler.swap("particles");
         var6.renderParticles(var9, tickDelta);
         this.disableTexture2D();
      }

      GlStateManager.depthMask(false);
      GlStateManager.enableCull();
      this.client.profiler.swap("weather");
      this.renderWeather(tickDelta);
      GlStateManager.depthMask(true);
      var5.m_28ljxtzwx(var9, tickDelta);
      GlStateManager.enableBlend();
      GlStateManager.enableCull();
      GlStateManager.blendFuncSeparate(770, 771, 1, 0);
      GlStateManager.alphaFunc(516, 0.1F);
      this.renderFog(0, tickDelta);
      GlStateManager.disableBlend();
      GlStateManager.depthMask(false);
      this.client.getTextureManager().bind(SpriteAtlasTexture.BLOCK_ATLAS_BLOCKS);
      GlStateManager.shadeModel(7425);
      if (this.client.options.fancyGraphics) {
         this.client.profiler.swap("translucent");
         GlStateManager.disableBlend();
         GlStateManager.blendFuncSeparate(770, 771, 1, 0);
         var5.m_86stejbes(BlockLayer.TRANSLUCENT, (double)tickDelta, anaglyphRenderPass, var9);
         GlStateManager.enableBlend();
      } else {
         this.client.profiler.swap("translucent");
         var5.m_86stejbes(BlockLayer.TRANSLUCENT, (double)tickDelta, anaglyphRenderPass, var9);
      }

      GlStateManager.shadeModel(7424);
      GlStateManager.depthMask(true);
      GlStateManager.enableCull();
      GlStateManager.enableBlend();
      GlStateManager.disableFog();
      if (var9.y >= 128.0) {
         this.client.profiler.swap("aboveClouds");
         this.renderAboveClouds(var5, tickDelta, anaglyphRenderPass);
      }

      this.client.profiler.swap("hand");
      if (this.newCamPitch) {
         GlStateManager.clear(256);
         this.renderHand(tickDelta, anaglyphRenderPass);
         this.m_77ehnszge(tickDelta);
      }
   }

   private void renderAboveClouds(WorldRenderer worldRenderer, float tickDelta, int i) {
      if (this.client.options.renderClouds()) {
         this.client.profiler.swap("clouds");
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         Project.gluPerspective(this.getFov(tickDelta, true), (float)this.client.width / (float)this.client.height, 0.05F, this.viewDistance * 4.0F);
         GlStateManager.matrixMode(5888);
         GlStateManager.pushMatrix();
         this.renderFog(0, tickDelta);
         worldRenderer.renderClouds(tickDelta, i);
         GlStateManager.disableFog();
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         Project.gluPerspective(
            this.getFov(tickDelta, true), (float)this.client.width / (float)this.client.height, 0.05F, this.viewDistance * MathHelper.SQRT_TWO
         );
         GlStateManager.matrixMode(5888);
      }
   }

   private void tickWaterSplashing() {
      float var1 = this.client.world.getRain(1.0F);
      if (!this.client.options.fancyGraphics) {
         var1 /= 2.0F;
      }

      if (var1 != 0.0F) {
         this.random.setSeed((long)this.ticks * 312987231L);
         Entity var2 = this.client.getCamera();
         ClientWorld var3 = this.client.world;
         BlockPos var4 = new BlockPos(var2);
         byte var5 = 10;
         double var6 = 0.0;
         double var8 = 0.0;
         double var10 = 0.0;
         int var12 = 0;
         int var13 = (int)(100.0F * var1 * var1);
         if (this.client.options.particles == 1) {
            var13 >>= 1;
         } else if (this.client.options.particles == 2) {
            var13 = 0;
         }

         for(int var14 = 0; var14 < var13; ++var14) {
            BlockPos var15 = var3.getPrecipitationHeight(
               var4.add(this.random.nextInt(var5) - this.random.nextInt(var5), 0, this.random.nextInt(var5) - this.random.nextInt(var5))
            );
            Biome var16 = var3.getBiome(var15);
            BlockPos var17 = var15.down();
            Block var18 = var3.getBlockState(var17).getBlock();
            if (var15.getY() <= var4.getY() + var5 && var15.getY() >= var4.getY() - var5 && var16.canRain() && var16.getTemperature(var15) >= 0.15F) {
               float var19 = this.random.nextFloat();
               float var20 = this.random.nextFloat();
               if (var18.getMaterial() == Material.LAVA) {
                  this.client
                     .world
                     .addParticle(
                        ParticleType.SMOKE_NORMAL,
                        (double)((float)var15.getX() + var19),
                        (double)((float)var15.getY() + 0.1F) - var18.getMinY(),
                        (double)((float)var15.getZ() + var20),
                        0.0,
                        0.0,
                        0.0,
                        new int[0]
                     );
               } else if (var18.getMaterial() != Material.AIR) {
                  var18.updateShape(var3, var17);
                  if (this.random.nextInt(++var12) == 0) {
                     var6 = (double)((float)var17.getX() + var19);
                     var8 = (double)((float)var17.getY() + 0.1F) + var18.getMaxY() - 1.0;
                     var10 = (double)((float)var17.getZ() + var20);
                  }

                  this.client
                     .world
                     .addParticle(
                        ParticleType.WATER_DROP,
                        (double)((float)var17.getX() + var19),
                        (double)((float)var17.getY() + 0.1F) + var18.getMaxY(),
                        (double)((float)var17.getZ() + var20),
                        0.0,
                        0.0,
                        0.0,
                        new int[0]
                     );
               }
            }
         }

         if (var12 > 0 && this.random.nextInt(3) < this.weatherSoundAttempts++) {
            this.weatherSoundAttempts = 0;
            if (var8 > (double)(var4.getY() + 1) && var3.getPrecipitationHeight(var4).getY() > MathHelper.floor((float)var4.getY())) {
               this.client.world.playSound(var6, var8, var10, "ambient.weather.rain", 0.1F, 0.5F, false);
            } else {
               this.client.world.playSound(var6, var8, var10, "ambient.weather.rain", 0.2F, 1.0F, false);
            }
         }
      }
   }

   protected void renderWeather(float tickDelta) {
      float var2 = this.client.world.getRain(tickDelta);
      if (!(var2 <= 0.0F)) {
         this.drawLightmap();
         Entity var3 = this.client.getCamera();
         ClientWorld var4 = this.client.world;
         int var5 = MathHelper.floor(var3.x);
         int var6 = MathHelper.floor(var3.y);
         int var7 = MathHelper.floor(var3.z);
         Tessellator var8 = Tessellator.getInstance();
         BufferBuilder var9 = var8.getBufferBuilder();
         GlStateManager.disableCull();
         GL11.glNormal3f(0.0F, 1.0F, 0.0F);
         GlStateManager.disableBlend();
         GlStateManager.blendFuncSeparate(770, 771, 1, 0);
         GlStateManager.alphaFunc(516, 0.1F);
         double var10 = var3.prevTickX + (var3.x - var3.prevTickX) * (double)tickDelta;
         double var12 = var3.prevTickY + (var3.y - var3.prevTickY) * (double)tickDelta;
         double var14 = var3.prevTickZ + (var3.z - var3.prevTickZ) * (double)tickDelta;
         int var16 = MathHelper.floor(var12);
         byte var17 = 5;
         if (this.client.options.fancyGraphics) {
            var17 = 10;
         }

         byte var18 = -1;
         float var19 = (float)this.ticks + tickDelta;
         if (this.client.options.fancyGraphics) {
            var17 = 10;
         }

         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

         for(int var20 = var7 - var17; var20 <= var7 + var17; ++var20) {
            for(int var21 = var5 - var17; var21 <= var5 + var17; ++var21) {
               int var22 = (var20 - var7 + 16) * 32 + var21 - var5 + 16;
               float var23 = this.weatherChunkDistanceNormalizedX[var22] * 0.5F;
               float var24 = this.weatherChunkDistanceNormalizedY[var22] * 0.5F;
               BlockPos var25 = new BlockPos(var21, 0, var20);
               Biome var26 = var4.getBiome(var25);
               if (var26.canRain() || var26.canSnow()) {
                  int var27 = var4.getPrecipitationHeight(var25).getY();
                  int var28 = var6 - var17;
                  int var29 = var6 + var17;
                  if (var28 < var27) {
                     var28 = var27;
                  }

                  if (var29 < var27) {
                     var29 = var27;
                  }

                  float var30 = 1.0F;
                  int var31 = var27;
                  if (var27 < var16) {
                     var31 = var16;
                  }

                  if (var28 != var29) {
                     this.random.setSeed((long)(var21 * var21 * 3121 + var21 * 45238971 ^ var20 * var20 * 418711 + var20 * 13761));
                     float var32 = var26.getTemperature(new BlockPos(var21, var28, var20));
                     if (var4.getBiomeSource().getTemperature(var32, var27) >= 0.15F) {
                        if (var18 != 0) {
                           if (var18 >= 0) {
                              var8.end();
                           }

                           var18 = 0;
                           this.client.getTextureManager().bind(RAIN_TEXTURE);
                           var9.start();
                        }

                        float var33 = ((float)(this.ticks + var21 * var21 * 3121 + var21 * 45238971 + var20 * var20 * 418711 + var20 * 13761 & 31) + tickDelta)
                           / 32.0F
                           * (3.0F + this.random.nextFloat());
                        double var34 = (double)((float)var21 + 0.5F) - var3.x;
                        double var36 = (double)((float)var20 + 0.5F) - var3.z;
                        float var38 = MathHelper.sqrt(var34 * var34 + var36 * var36) / (float)var17;
                        float var39 = 1.0F;
                        var9.brightness(var4.getLightColor(new BlockPos(var21, var31, var20), 0));
                        var9.color(var39, var39, var39, ((1.0F - var38 * var38) * 0.5F + 0.5F) * var2);
                        var9.offset(-var10 * 1.0, -var12 * 1.0, -var14 * 1.0);
                        var9.vertex(
                           (double)((float)var21 - var23) + 0.5,
                           (double)var28,
                           (double)((float)var20 - var24) + 0.5,
                           (double)(0.0F * var30),
                           (double)((float)var28 * var30 / 4.0F + var33 * var30)
                        );
                        var9.vertex(
                           (double)((float)var21 + var23) + 0.5,
                           (double)var28,
                           (double)((float)var20 + var24) + 0.5,
                           (double)(1.0F * var30),
                           (double)((float)var28 * var30 / 4.0F + var33 * var30)
                        );
                        var9.vertex(
                           (double)((float)var21 + var23) + 0.5,
                           (double)var29,
                           (double)((float)var20 + var24) + 0.5,
                           (double)(1.0F * var30),
                           (double)((float)var29 * var30 / 4.0F + var33 * var30)
                        );
                        var9.vertex(
                           (double)((float)var21 - var23) + 0.5,
                           (double)var29,
                           (double)((float)var20 - var24) + 0.5,
                           (double)(0.0F * var30),
                           (double)((float)var29 * var30 / 4.0F + var33 * var30)
                        );
                        var9.offset(0.0, 0.0, 0.0);
                     } else {
                        if (var18 != 1) {
                           if (var18 >= 0) {
                              var8.end();
                           }

                           var18 = 1;
                           this.client.getTextureManager().bind(SNOW_TEXTURE);
                           var9.start();
                        }

                        float var42 = ((float)(this.ticks & 511) + tickDelta) / 512.0F;
                        float var43 = this.random.nextFloat() + var19 * 0.01F * (float)this.random.nextGaussian();
                        float var35 = this.random.nextFloat() + var19 * (float)this.random.nextGaussian() * 0.001F;
                        double var44 = (double)((float)var21 + 0.5F) - var3.x;
                        double var45 = (double)((float)var20 + 0.5F) - var3.z;
                        float var40 = MathHelper.sqrt(var44 * var44 + var45 * var45) / (float)var17;
                        float var41 = 1.0F;
                        var9.brightness((var4.getLightColor(new BlockPos(var21, var31, var20), 0) * 3 + 15728880) / 4);
                        var9.color(var41, var41, var41, ((1.0F - var40 * var40) * 0.3F + 0.5F) * var2);
                        var9.offset(-var10 * 1.0, -var12 * 1.0, -var14 * 1.0);
                        var9.vertex(
                           (double)((float)var21 - var23) + 0.5,
                           (double)var28,
                           (double)((float)var20 - var24) + 0.5,
                           (double)(0.0F * var30 + var43),
                           (double)((float)var28 * var30 / 4.0F + var42 * var30 + var35)
                        );
                        var9.vertex(
                           (double)((float)var21 + var23) + 0.5,
                           (double)var28,
                           (double)((float)var20 + var24) + 0.5,
                           (double)(1.0F * var30 + var43),
                           (double)((float)var28 * var30 / 4.0F + var42 * var30 + var35)
                        );
                        var9.vertex(
                           (double)((float)var21 + var23) + 0.5,
                           (double)var29,
                           (double)((float)var20 + var24) + 0.5,
                           (double)(1.0F * var30 + var43),
                           (double)((float)var29 * var30 / 4.0F + var42 * var30 + var35)
                        );
                        var9.vertex(
                           (double)((float)var21 - var23) + 0.5,
                           (double)var29,
                           (double)((float)var20 - var24) + 0.5,
                           (double)(0.0F * var30 + var43),
                           (double)((float)var29 * var30 / 4.0F + var42 * var30 + var35)
                        );
                        var9.offset(0.0, 0.0, 0.0);
                     }
                  }
               }
            }
         }

         if (var18 >= 0) {
            var8.end();
         }

         GlStateManager.enableCull();
         GlStateManager.enableBlend();
         GlStateManager.alphaFunc(516, 0.1F);
         this.disableTexture2D();
      }
   }

   public void setupHudMatrixMode() {
      Window var1 = new Window(this.client, this.client.width, this.client.height);
      GlStateManager.clear(256);
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      GlStateManager.ortho(0.0, var1.getScaledWidth(), var1.getScaledHeight(), 0.0, 1000.0, 3000.0);
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      GlStateManager.translatef(0.0F, 0.0F, -2000.0F);
   }

   private void renderBackground(float tickDelta) {
      ClientWorld var2 = this.client.world;
      Entity var3 = this.client.getCamera();
      float var4 = 0.25F + 0.75F * (float)this.client.options.viewDistance / 32.0F;
      var4 = 1.0F - (float)Math.pow((double)var4, 0.25);
      Vec3d var5 = var2.getSkyColor(this.client.getCamera(), tickDelta);
      float var6 = (float)var5.x;
      float var7 = (float)var5.y;
      float var8 = (float)var5.z;
      Vec3d var9 = var2.getFogColor(tickDelta);
      this.fogRed = (float)var9.x;
      this.fogGreen = (float)var9.y;
      this.fogBlue = (float)var9.z;
      if (this.client.options.viewDistance >= 4) {
         double var10 = -1.0;
         Vec3d var12 = MathHelper.sin(var2.getSunAngle(tickDelta)) > 0.0F ? new Vec3d(var10, 0.0, 0.0) : new Vec3d(1.0, 0.0, 0.0);
         float var13 = (float)var3.m_01qqqsfds(tickDelta).dot(var12);
         if (var13 < 0.0F) {
            var13 = 0.0F;
         }

         if (var13 > 0.0F) {
            float[] var14 = var2.dimension.getBackgroundColor(var2.getTimeOfDay(tickDelta), tickDelta);
            if (var14 != null) {
               var13 *= var14[3];
               this.fogRed = this.fogRed * (1.0F - var13) + var14[0] * var13;
               this.fogGreen = this.fogGreen * (1.0F - var13) + var14[1] * var13;
               this.fogBlue = this.fogBlue * (1.0F - var13) + var14[2] * var13;
            }
         }
      }

      this.fogRed += (var6 - this.fogRed) * var4;
      this.fogGreen += (var7 - this.fogGreen) * var4;
      this.fogBlue += (var8 - this.fogBlue) * var4;
      float var20 = var2.getRain(tickDelta);
      if (var20 > 0.0F) {
         float var11 = 1.0F - var20 * 0.5F;
         float var22 = 1.0F - var20 * 0.4F;
         this.fogRed *= var11;
         this.fogGreen *= var11;
         this.fogBlue *= var22;
      }

      float var21 = var2.getThunder(tickDelta);
      if (var21 > 0.0F) {
         float var23 = 1.0F - var21 * 0.5F;
         this.fogRed *= var23;
         this.fogGreen *= var23;
         this.fogBlue *= var23;
      }

      Block var24 = Camera.getLiquidInside(this.client.world, var3, tickDelta);
      if (this.thiccFog) {
         Vec3d var26 = var2.getCloudColor(tickDelta);
         this.fogRed = (float)var26.x;
         this.fogGreen = (float)var26.y;
         this.fogBlue = (float)var26.z;
      } else if (var24.getMaterial() == Material.WATER) {
         float var27 = (float)EnchantmentHelper.getRespirationLevel(var3) * 0.2F;
         if (var3 instanceof LivingEntity && ((LivingEntity)var3).hasStatusEffect(StatusEffect.WATER_BREATHING)) {
            var27 = var27 * 0.3F + 0.6F;
         }

         this.fogRed = 0.02F + var27;
         this.fogGreen = 0.02F + var27;
         this.fogBlue = 0.2F + var27;
      } else if (var24.getMaterial() == Material.LAVA) {
         this.fogRed = 0.6F;
         this.fogGreen = 0.1F;
         this.fogBlue = 0.0F;
      }

      float var28 = this.newFogGrayScale + (this.oldFogGrayScale - this.newFogGrayScale) * tickDelta;
      this.fogRed *= var28;
      this.fogGreen *= var28;
      this.fogBlue *= var28;
      double var29 = (var3.prevTickY + (var3.y - var3.prevTickY) * (double)tickDelta) * var2.dimension.getFogSize();
      if (var3 instanceof LivingEntity && ((LivingEntity)var3).hasStatusEffect(StatusEffect.BLINDNESS)) {
         int var16 = ((LivingEntity)var3).getEffectInstance(StatusEffect.BLINDNESS).getDuration();
         if (var16 < 20) {
            var29 *= (double)(1.0F - (float)var16 / 20.0F);
         } else {
            var29 = 0.0;
         }
      }

      if (var29 < 1.0) {
         if (var29 < 0.0) {
            var29 = 0.0;
         }

         var29 *= var29;
         this.fogRed = (float)((double)this.fogRed * var29);
         this.fogGreen = (float)((double)this.fogGreen * var29);
         this.fogBlue = (float)((double)this.fogBlue * var29);
      }

      if (this.skyDarkness > 0.0F) {
         float var31 = this.lastSkyDarkness + (this.skyDarkness - this.lastSkyDarkness) * tickDelta;
         this.fogRed = this.fogRed * (1.0F - var31) + this.fogRed * 0.7F * var31;
         this.fogGreen = this.fogGreen * (1.0F - var31) + this.fogGreen * 0.6F * var31;
         this.fogBlue = this.fogBlue * (1.0F - var31) + this.fogBlue * 0.6F * var31;
      }

      if (var3 instanceof LivingEntity && ((LivingEntity)var3).hasStatusEffect(StatusEffect.NIGHTVISION)) {
         float var32 = this.getNightVisionLightOffset((LivingEntity)var3, tickDelta);
         float var17 = 1.0F / this.fogRed;
         if (var17 > 1.0F / this.fogGreen) {
            var17 = 1.0F / this.fogGreen;
         }

         if (var17 > 1.0F / this.fogBlue) {
            var17 = 1.0F / this.fogBlue;
         }

         this.fogRed = this.fogRed * (1.0F - var32) + this.fogRed * var17 * var32;
         this.fogGreen = this.fogGreen * (1.0F - var32) + this.fogGreen * var17 * var32;
         this.fogBlue = this.fogBlue * (1.0F - var32) + this.fogBlue * var17 * var32;
      }

      if (this.client.options.anaglyph) {
         float var33 = (this.fogRed * 30.0F + this.fogGreen * 59.0F + this.fogBlue * 11.0F) / 100.0F;
         float var34 = (this.fogRed * 30.0F + this.fogGreen * 70.0F) / 100.0F;
         float var18 = (this.fogRed * 30.0F + this.fogBlue * 70.0F) / 100.0F;
         this.fogRed = var33;
         this.fogGreen = var34;
         this.fogBlue = var18;
      }

      GlStateManager.clearColor(this.fogRed, this.fogGreen, this.fogBlue, 0.0F);
   }

   private void renderFog(int mode, float tickDelta) {
      Entity var3 = this.client.getCamera();
      boolean var4 = false;
      if (var3 instanceof PlayerEntity) {
         var4 = ((PlayerEntity)var3).abilities.creativeMode;
      }

      GL11.glFog(2918, this.setFogColor(this.fogRed, this.fogGreen, this.fogBlue, 1.0F));
      GL11.glNormal3f(0.0F, -1.0F, 0.0F);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      Block var5 = Camera.getLiquidInside(this.client.world, var3, tickDelta);
      if (var3 instanceof LivingEntity && ((LivingEntity)var3).hasStatusEffect(StatusEffect.BLINDNESS)) {
         float var10 = 5.0F;
         int var12 = ((LivingEntity)var3).getEffectInstance(StatusEffect.BLINDNESS).getDuration();
         if (var12 < 20) {
            var10 = 5.0F + (this.viewDistance - 5.0F) * (1.0F - (float)var12 / 20.0F);
         }

         GlStateManager.fogMode(9729);
         if (mode == -1) {
            GlStateManager.fogStart(0.0F);
            GlStateManager.fogEnd(var10 * 0.8F);
         } else {
            GlStateManager.fogStart(var10 * 0.25F);
            GlStateManager.fogEnd(var10);
         }

         if (GLContext.getCapabilities().GL_NV_fog_distance) {
            GL11.glFogi(34138, 34139);
         }
      } else if (this.thiccFog) {
         GlStateManager.fogMode(2048);
         GlStateManager.fogDensity(0.1F);
      } else if (var5.getMaterial() == Material.WATER) {
         GlStateManager.fogMode(2048);
         if (var3 instanceof LivingEntity && ((LivingEntity)var3).hasStatusEffect(StatusEffect.WATER_BREATHING)) {
            GlStateManager.fogDensity(0.01F);
         } else {
            GlStateManager.fogDensity(0.1F - (float)EnchantmentHelper.getRespirationLevel(var3) * 0.03F);
         }
      } else if (var5.getMaterial() == Material.LAVA) {
         GlStateManager.fogMode(2048);
         GlStateManager.fogDensity(2.0F);
      } else {
         float var6 = this.viewDistance;
         if (this.client.world.dimension.doesWaterVaporize() && !var4) {
            double var7 = (double)((var3.getLightLevel(tickDelta) & 15728640) >> 20) / 16.0
               + (var3.prevTickY + (var3.y - var3.prevTickY) * (double)tickDelta + 4.0) / 32.0;
            if (var7 < 1.0) {
               if (var7 < 0.0) {
                  var7 = 0.0;
               }

               var7 *= var7;
               float var9 = 100.0F * (float)var7;
               if (var9 < 5.0F) {
                  var9 = 5.0F;
               }

               if (var6 > var9) {
                  var6 = var9;
               }
            }
         }

         GlStateManager.fogMode(9729);
         if (mode == -1) {
            GlStateManager.fogStart(0.0F);
            GlStateManager.fogEnd(var6);
         } else {
            GlStateManager.fogStart(var6 * 0.75F);
            GlStateManager.fogEnd(var6);
         }

         if (GLContext.getCapabilities().GL_NV_fog_distance) {
            GL11.glFogi(34138, 34139);
         }

         if (this.client.world.dimension.isFogThick((int)var3.x, (int)var3.z)) {
            GlStateManager.fogStart(var6 * 0.05F);
            GlStateManager.fogEnd(Math.min(var6, 192.0F) * 0.5F);
         }
      }

      GlStateManager.enableColorMaterial();
      GlStateManager.enableFog();
      GlStateManager.colorMaterial(1028, 4608);
   }

   private FloatBuffer setFogColor(float r, float g, float b, float a) {
      ((Buffer)this.fogColorBuffer).clear();
      this.fogColorBuffer.put(r).put(g).put(b).put(a);
      ((Buffer)this.fogColorBuffer).flip();
      return this.fogColorBuffer;
   }

   public MapRenderer getMapRenderer() {
      return this.mapRenderer;
   }
}
