package net.minecraft.client.render.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.MemoryTracker;
import com.mojang.blaze3d.shaders.ProgramManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tessellator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import net.minecraft.C_00ekfeucz;
import net.minecraft.C_00rygukrg;
import net.minecraft.C_09tthcadg;
import net.minecraft.C_34czhuwfp;
import net.minecraft.C_36dgbgejf;
import net.minecraft.C_42nteilvc;
import net.minecraft.C_53fhzsins;
import net.minecraft.C_64kccbmvr;
import net.minecraft.C_87hkwxwgr;
import net.minecraft.C_98pnhiglv;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.SignBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.entity.particle.Particle;
import net.minecraft.client.render.Culler;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.FrustumCuller;
import net.minecraft.client.render.FrustumData;
import net.minecraft.client.render.PostChain;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.client.render.block.BlockRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.resource.manager.IResourceManager;
import net.minecraft.client.resource.manager.ResourceReloadListener;
import net.minecraft.client.sound.event.ISoundEvent;
import net.minecraft.client.sound.event.SimpleSoundEvent;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureAtlasSprite;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.MusicDiscItem;
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
import net.minecraft.world.WorldEventListener;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.WorldChunk;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class WorldRenderer implements WorldEventListener, ResourceReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Identifier MOON_PHASES_TEXTURE = new Identifier("textures/environment/moon_phases.png");
   private static final Identifier SUN_TEXTURE = new Identifier("textures/environment/sun.png");
   private static final Identifier CLOUDS_TEXTURE = new Identifier("textures/environment/clouds.png");
   private static final Identifier END_SKY_TEXTURE = new Identifier("textures/environment/end_sky.png");
   private static final Identifier WORLD_BORDER_TEXTURE = new Identifier("textures/misc/forcefield.png");
   private final MinecraftClient client;
   private final TextureManager textureManager;
   private final Set f_63ozqinqe = Sets.newHashSet();
   private final EntityRenderDispatcher clientWorld;
   private ClientWorld world;
   private Set f_18cxnognh = Sets.newLinkedHashSet();
   private List f_71vlpncur = Lists.newArrayListWithCapacity(69696);
   private C_64kccbmvr f_23hjlfzwm;
   private int f_46yjabdvl = -1;
   private int f_17devwhab = -1;
   private int f_74ifhicpj = -1;
   private VertexFormat f_28elerksp;
   private VertexBuffer f_88diyyyvr;
   private VertexBuffer f_84heolmsu;
   private VertexBuffer f_97wypqipn;
   private int ticks;
   private final Map miningSites = Maps.newHashMap();
   private final Map miningProgress = Maps.newHashMap();
   private final Map playingSongs = Maps.newHashMap();
   private final TextureAtlasSprite[] miningProgressTextures = new TextureAtlasSprite[10];
   private RenderTarget entityOutlinePostChainTempTargetFinal;
   private PostChain entityOutlinePostChain;
   private double chunkSectionCameraX = Double.MIN_VALUE;
   private double chunkSectionCameraY = Double.MIN_VALUE;
   private double chunkSectionCameraZ = Double.MIN_VALUE;
   private int cameraChunkX = Integer.MIN_VALUE;
   private int cameraChunkY = Integer.MIN_VALUE;
   private int cameraChunkZ = Integer.MIN_VALUE;
   private double cameraX = Double.MIN_VALUE;
   private double cameraY = Double.MIN_VALUE;
   private double cameraZ = Double.MIN_VALUE;
   private double cameraPitch = Double.MIN_VALUE;
   private double cameraYaw = Double.MIN_VALUE;
   private final C_34czhuwfp f_81yxjkkqc = new C_34czhuwfp();
   private C_98pnhiglv f_22kecqubs;
   private int renderDistance = -1;
   private int entityRenderCooldown = 2;
   private int entityCount;
   private int globalEntityCount;
   private int f_11yaduyow;
   private boolean f_85qrjoukl = false;
   private FrustumData clipper;
   private final Vector4f[] f_07gmwinho = new Vector4f[8];
   private final Vector3d f_22rmrpijo = new Vector3d();
   private boolean f_60mzvsdpq = false;
   C_42nteilvc f_17jrfoxik;
   private double lastCameraX;
   private double lastCameraY;
   private double lastCameraZ;
   private boolean viewChanged = true;

   public WorldRenderer(MinecraftClient client) {
      this.client = client;
      this.clientWorld = client.getEntityRenderDispatcher();
      this.textureManager = client.getTextureManager();
      this.textureManager.bind(WORLD_BORDER_TEXTURE);
      GL11.glTexParameteri(3553, 10242, 10497);
      GL11.glTexParameteri(3553, 10243, 10497);
      GlStateManager.bindTexture(0);
      this.reloadBlockBreakingStageTextures();
      this.f_60mzvsdpq = GLX.useVbo();
      if (this.f_60mzvsdpq) {
         this.f_22kecqubs = new C_00rygukrg();
         this.f_17jrfoxik = new C_00ekfeucz();
      } else {
         this.f_22kecqubs = new C_87hkwxwgr();
         this.f_17jrfoxik = new C_36dgbgejf();
      }

      this.f_28elerksp = new VertexFormat();
      this.f_28elerksp.add(new VertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.POSITION, 3));
      this.m_12ysiibhi();
      this.m_07rvfosao();
      this.m_02kgkrxcr();
   }

   @Override
   public void reload(IResourceManager resourceManager) {
      this.reloadBlockBreakingStageTextures();
   }

   protected void reloadBlockBreakingStageTextures() {
      SpriteAtlasTexture var1 = this.client.getSpriteAtlasTexture();

      for(int var2 = 0; var2 < this.miningProgressTextures.length; ++var2) {
         this.miningProgressTextures[var2] = var1.getSprite("minecraft:blocks/destroy_stage_" + var2);
      }
   }

   public void loadPostChain() {
      if (GLX.usePostProcess) {
         if (ProgramManager.getInstance() == null) {
            ProgramManager.createInstance();
         }

         Identifier var1 = new Identifier("shaders/post/entity_outline.json");

         try {
            this.entityOutlinePostChain = new PostChain(this.client.getTextureManager(), this.client.getResourceManager(), this.client.getRenderTarget(), var1);
            this.entityOutlinePostChain.resize(this.client.width, this.client.height);
            this.entityOutlinePostChainTempTargetFinal = this.entityOutlinePostChain.getTempTarget("final");
         } catch (IOException var3) {
            LOGGER.warn("Failed to load shader: " + var1, var3);
            this.entityOutlinePostChain = null;
            this.entityOutlinePostChainTempTargetFinal = null;
         } catch (JsonSyntaxException var4) {
            LOGGER.warn("Failed to load shader: " + var1, var4);
            this.entityOutlinePostChain = null;
            this.entityOutlinePostChainTempTargetFinal = null;
         }
      } else {
         this.entityOutlinePostChain = null;
         this.entityOutlinePostChainTempTargetFinal = null;
      }
   }

   public void renderSpectatorOutlines() {
      if (this.shouldRenderSpectatorPlayerOutlines()) {
         GlStateManager.disableBlend();
         GlStateManager.blendFuncSeparate(770, 771, 0, 1);
         this.entityOutlinePostChainTempTargetFinal.draw(this.client.width, this.client.height, false);
         GlStateManager.enableBlend();
      }
   }

   protected boolean shouldRenderSpectatorPlayerOutlines() {
      return this.entityOutlinePostChainTempTargetFinal != null
         && this.entityOutlinePostChain != null
         && this.client.player != null
         && this.client.player.isSpectator()
         && this.client.options.spectatorOutlinesKey.isPressed();
   }

   private void m_02kgkrxcr() {
      Tessellator var1 = Tessellator.getInstance();
      BufferBuilder var2 = var1.getBufferBuilder();
      if (this.f_97wypqipn != null) {
         this.f_97wypqipn.delete();
      }

      if (this.f_74ifhicpj >= 0) {
         MemoryTracker.releaseList(this.f_74ifhicpj);
         this.f_74ifhicpj = -1;
      }

      if (this.f_60mzvsdpq) {
         this.f_97wypqipn = new VertexBuffer(this.f_28elerksp);
         this.m_15joknofk(var2, -16.0F, true);
         var2.end();
         var2.clear();
         this.f_97wypqipn.upload(var2.getBuffer(), var2.getLimit());
      } else {
         this.f_74ifhicpj = MemoryTracker.getLists(1);
         GL11.glNewList(this.f_74ifhicpj, 4864);
         this.m_15joknofk(var2, -16.0F, true);
         var1.end();
         GL11.glEndList();
      }
   }

   private void m_07rvfosao() {
      Tessellator var1 = Tessellator.getInstance();
      BufferBuilder var2 = var1.getBufferBuilder();
      if (this.f_84heolmsu != null) {
         this.f_84heolmsu.delete();
      }

      if (this.f_17devwhab >= 0) {
         MemoryTracker.releaseList(this.f_17devwhab);
         this.f_17devwhab = -1;
      }

      if (this.f_60mzvsdpq) {
         this.f_84heolmsu = new VertexBuffer(this.f_28elerksp);
         this.m_15joknofk(var2, 16.0F, false);
         var2.end();
         var2.clear();
         this.f_84heolmsu.upload(var2.getBuffer(), var2.getLimit());
      } else {
         this.f_17devwhab = MemoryTracker.getLists(1);
         GL11.glNewList(this.f_17devwhab, 4864);
         this.m_15joknofk(var2, 16.0F, false);
         var1.end();
         GL11.glEndList();
      }
   }

   private void m_15joknofk(BufferBuilder bufferBuilder, float y, boolean bl) {
      boolean var4 = true;
      boolean var5 = true;
      bufferBuilder.start();

      for(int var6 = -384; var6 <= 384; var6 += 64) {
         for(int var7 = -384; var7 <= 384; var7 += 64) {
            float var8 = (float)var6;
            float var9 = (float)(var6 + 64);
            if (bl) {
               var9 = (float)var6;
               var8 = (float)(var6 + 64);
            }

            bufferBuilder.vertex((double)var8, (double)y, (double)var7);
            bufferBuilder.vertex((double)var9, (double)y, (double)var7);
            bufferBuilder.vertex((double)var9, (double)y, (double)(var7 + 64));
            bufferBuilder.vertex((double)var8, (double)y, (double)(var7 + 64));
         }
      }
   }

   private void m_12ysiibhi() {
      Tessellator var1 = Tessellator.getInstance();
      BufferBuilder var2 = var1.getBufferBuilder();
      if (this.f_88diyyyvr != null) {
         this.f_88diyyyvr.delete();
      }

      if (this.f_46yjabdvl >= 0) {
         MemoryTracker.releaseList(this.f_46yjabdvl);
         this.f_46yjabdvl = -1;
      }

      if (this.f_60mzvsdpq) {
         this.f_88diyyyvr = new VertexBuffer(this.f_28elerksp);
         this.drawNoise(var2);
         var2.end();
         var2.clear();
         this.f_88diyyyvr.upload(var2.getBuffer(), var2.getLimit());
      } else {
         this.f_46yjabdvl = MemoryTracker.getLists(1);
         GlStateManager.pushMatrix();
         GL11.glNewList(this.f_46yjabdvl, 4864);
         this.drawNoise(var2);
         var1.end();
         GL11.glEndList();
         GlStateManager.popMatrix();
      }
   }

   private void drawNoise(BufferBuilder bufferBuilder) {
      Random var2 = new Random(10842L);
      bufferBuilder.start();

      for(int var3 = 0; var3 < 1500; ++var3) {
         double var4 = (double)(var2.nextFloat() * 2.0F - 1.0F);
         double var6 = (double)(var2.nextFloat() * 2.0F - 1.0F);
         double var8 = (double)(var2.nextFloat() * 2.0F - 1.0F);
         double var10 = (double)(0.15F + var2.nextFloat() * 0.1F);
         double var12 = var4 * var4 + var6 * var6 + var8 * var8;
         if (var12 < 1.0 && var12 > 0.01) {
            var12 = 1.0 / Math.sqrt(var12);
            var4 *= var12;
            var6 *= var12;
            var8 *= var12;
            double var14 = var4 * 100.0;
            double var16 = var6 * 100.0;
            double var18 = var8 * 100.0;
            double var20 = Math.atan2(var4, var8);
            double var22 = Math.sin(var20);
            double var24 = Math.cos(var20);
            double var26 = Math.atan2(Math.sqrt(var4 * var4 + var8 * var8), var6);
            double var28 = Math.sin(var26);
            double var30 = Math.cos(var26);
            double var32 = var2.nextDouble() * Math.PI * 2.0;
            double var34 = Math.sin(var32);
            double var36 = Math.cos(var32);

            for(int var38 = 0; var38 < 4; ++var38) {
               double var39 = 0.0;
               double var41 = (double)((var38 & 2) - 1) * var10;
               double var43 = (double)((var38 + 1 & 2) - 1) * var10;
               double var45 = 0.0;
               double var47 = var41 * var36 - var43 * var34;
               double var49 = var43 * var36 + var41 * var34;
               double var53 = var47 * var28 + 0.0 * var30;
               double var55 = 0.0 * var28 - var47 * var30;
               double var57 = var55 * var22 - var49 * var24;
               double var61 = var49 * var22 + var55 * var24;
               bufferBuilder.vertex(var14 + var57, var16 + var53, var18 + var61);
            }
         }
      }
   }

   public void setWorld(ClientWorld world) {
      if (this.world != null) {
         this.world.removeEventListener(this);
      }

      this.chunkSectionCameraX = Double.MIN_VALUE;
      this.chunkSectionCameraY = Double.MIN_VALUE;
      this.chunkSectionCameraZ = Double.MIN_VALUE;
      this.cameraChunkX = Integer.MIN_VALUE;
      this.cameraChunkY = Integer.MIN_VALUE;
      this.cameraChunkZ = Integer.MIN_VALUE;
      this.clientWorld.setWorld(world);
      this.world = world;
      if (world != null) {
         world.addEventListener(this);
         this.reload();
      }
   }

   public void reload() {
      if (this.world != null) {
         this.viewChanged = true;
         Blocks.LEAVES.setFancyGraphics(this.client.options.fancyGraphics);
         Blocks.LEAVES2.setFancyGraphics(this.client.options.fancyGraphics);
         this.renderDistance = this.client.options.viewDistance;
         boolean var1 = this.f_60mzvsdpq;
         this.f_60mzvsdpq = GLX.useVbo();
         if (var1 && !this.f_60mzvsdpq) {
            this.f_22kecqubs = new C_87hkwxwgr();
            this.f_17jrfoxik = new C_36dgbgejf();
         } else if (!var1 && this.f_60mzvsdpq) {
            this.f_22kecqubs = new C_00rygukrg();
            this.f_17jrfoxik = new C_00ekfeucz();
         }

         if (var1 != this.f_60mzvsdpq) {
            this.m_12ysiibhi();
            this.m_07rvfosao();
            this.m_02kgkrxcr();
         }

         if (this.f_23hjlfzwm != null) {
            this.f_23hjlfzwm.m_89qbnuvdx();
         }

         this.m_98zrrxbcg();
         this.f_63ozqinqe.clear();
         this.f_23hjlfzwm = new C_64kccbmvr(this.world, this.client.options.viewDistance, this, this.f_17jrfoxik);
         if (this.world != null) {
            Entity var2 = this.client.getCamera();
            if (var2 != null) {
               this.f_23hjlfzwm.m_17tqbccfz(var2.x, var2.z);
            }
         }

         this.entityRenderCooldown = 2;
      }
   }

   protected void m_98zrrxbcg() {
      this.f_18cxnognh.clear();
   }

   public void reziseEntityOutlinePostChain(int width, int height) {
      if (GLX.usePostProcess) {
         if (this.entityOutlinePostChain != null) {
            this.entityOutlinePostChain.resize(width, height);
         }
      }
   }

   public void renderEntities(Entity camera, Culler view, float tickDelta) {
      if (this.entityRenderCooldown > 0) {
         --this.entityRenderCooldown;
      } else {
         double var4 = camera.prevX + (camera.x - camera.prevX) * (double)tickDelta;
         double var6 = camera.prevY + (camera.y - camera.prevY) * (double)tickDelta;
         double var8 = camera.prevZ + (camera.z - camera.prevZ) * (double)tickDelta;
         this.world.profiler.push("prepare");
         BlockEntityRenderDispatcher.INSTANCE
            .prepare(this.world, this.client.getTextureManager(), this.client.textRenderer, this.client.getCamera(), tickDelta);
         this.clientWorld.prepare(this.world, this.client.textRenderer, this.client.getCamera(), this.client.targetEntity, this.client.options, tickDelta);
         this.entityCount = 0;
         this.globalEntityCount = 0;
         this.f_11yaduyow = 0;
         Entity var10 = this.client.getCamera();
         double var11 = var10.prevTickX + (var10.x - var10.prevTickX) * (double)tickDelta;
         double var13 = var10.prevTickY + (var10.y - var10.prevTickY) * (double)tickDelta;
         double var15 = var10.prevTickZ + (var10.z - var10.prevTickZ) * (double)tickDelta;
         BlockEntityRenderDispatcher.offsetX = var11;
         BlockEntityRenderDispatcher.offsetY = var13;
         BlockEntityRenderDispatcher.offsetZ = var15;
         this.clientWorld.setCameraPos(var11, var13, var15);
         this.client.gameRenderer.drawLightmap();
         this.world.profiler.swap("global");
         List var17 = this.world.getEntities();
         this.entityCount = var17.size();

         for(int var18 = 0; var18 < this.world.globalEntities.size(); ++var18) {
            Entity var19 = (Entity)this.world.globalEntities.get(var18);
            ++this.globalEntityCount;
            if (var19.isWithinViewDistanceOf(var4, var6, var8)) {
               this.clientWorld.render(var19, tickDelta);
            }
         }

         if (this.shouldRenderSpectatorPlayerOutlines()) {
            GlStateManager.depthFunc(519);
            GlStateManager.disableFog();
            this.entityOutlinePostChainTempTargetFinal.clear();
            this.entityOutlinePostChainTempTargetFinal.bindWrite(false);
            this.world.profiler.swap("entityOutlines");
            Lighting.turnOff();
            this.clientWorld.setSolidRender(true);

            for(int var27 = 0; var27 < var17.size(); ++var27) {
               Entity var31 = (Entity)var17.get(var27);
               boolean var20 = this.client.getCamera() instanceof LivingEntity && ((LivingEntity)this.client.getCamera()).isSleeping();
               boolean var21 = var31.isWithinViewDistanceOf(var4, var6, var8)
                  && (var31.ignoreCameraFrustum || view.isVisible(var31.getBoundingBox()) || var31.rider == this.client.player)
                  && var31 instanceof PlayerEntity;
               if ((var31 != this.client.getCamera() || this.client.options.perspective != 0 || var20) && var21) {
                  this.clientWorld.render(var31, tickDelta);
               }
            }

            this.clientWorld.setSolidRender(false);
            Lighting.turnOn();
            GlStateManager.depthMask(false);
            this.entityOutlinePostChain.process(tickDelta);
            GlStateManager.depthMask(true);
            this.client.getRenderTarget().bindWrite(false);
            GlStateManager.enableFog();
            GlStateManager.depthFunc(515);
            GlStateManager.disableDepth();
            GlStateManager.enableAlphaTest();
         }

         this.world.profiler.swap("entities");

         for(WorldRenderer.C_10cjdgthg var32 : this.f_71vlpncur) {
            WorldChunk var35 = this.world.getChunk(var32.f_27rxgdmlv.m_97olwzrjj());

            for(Entity var22 : var35.getEntitiesBySection()[var32.f_27rxgdmlv.m_97olwzrjj().getY() / 16]) {
               boolean var23 = this.clientWorld.shouldRender(var22, view, var4, var6, var8) || var22.rider == this.client.player;
               if (var23) {
                  boolean var24 = this.client.getCamera() instanceof LivingEntity ? ((LivingEntity)this.client.getCamera()).isSleeping() : false;
                  if (var22 == this.client.getCamera() && this.client.options.perspective == 0 && !var24
                     || var22.y >= 0.0 && var22.y < 256.0 && !this.world.isLoaded(new BlockPos(var22))) {
                     continue;
                  }

                  ++this.globalEntityCount;
                  this.clientWorld.render(var22, tickDelta);
               }

               if (!var23 && var22 instanceof WitherSkullEntity) {
                  this.client.getEntityRenderDispatcher().m_01kmjxpcp(var22, tickDelta);
               }
            }
         }

         this.world.profiler.swap("blockentities");
         Lighting.turnOn();
         synchronized(this.f_63ozqinqe) {
            for(BlockEntity var36 : this.f_63ozqinqe) {
               BlockEntityRenderDispatcher.INSTANCE.render(var36, tickDelta, -1);
            }
         }

         this.m_60xmflqkz();
         Iterator var30 = this.miningProgress.values().iterator();

         while(var30.hasNext()) {
            BlockMiningProgress var34 = (BlockMiningProgress)var30.next();
            BlockPos var37 = var34.getPos();
            BlockEntity var41 = this.world.getBlockEntity(var37);
            if (var41 instanceof ChestBlockEntity) {
               ChestBlockEntity var42 = (ChestBlockEntity)var41;
               if (var42.westNeighbor != null) {
                  var37 = var37.offset(Direction.WEST);
                  var41 = this.world.getBlockEntity(var37);
               } else if (var42.northNeighbor != null) {
                  var37 = var37.offset(Direction.NORTH);
                  var41 = this.world.getBlockEntity(var37);
               }
            }

            if (var41 != null) {
               BlockEntityRenderDispatcher.INSTANCE.render(var41, tickDelta, var34.getProgress());
            } else {
               var30.remove();
            }
         }

         this.m_93kijynnh();
         this.client.gameRenderer.disableTexture2D();
         this.client.profiler.pop();
      }
   }

   public String getChunkDebugInfo() {
      int var1 = this.f_23hjlfzwm.chunkSections.length;
      int var2 = 0;

      for(WorldRenderer.C_10cjdgthg var4 : this.f_71vlpncur) {
         C_53fhzsins var5 = var4.f_27rxgdmlv.f_10rxmxrqu;
         if (var5 != C_53fhzsins.f_89lpuxuhd && !var5.m_29jrzqbxt()) {
            ++var2;
         }
      }

      return String.format("C: %d/%d %sD: %d, %s", var2, var1, this.client.f_40dttiifl ? "(s) " : "", this.renderDistance, this.f_81yxjkkqc.m_43nzrlwev());
   }

   public String getEntityDebugInfo() {
      return "E: "
         + this.globalEntityCount
         + "/"
         + this.entityCount
         + ", B: "
         + this.f_11yaduyow
         + ", I: "
         + (this.entityCount - this.f_11yaduyow - this.globalEntityCount);
   }

   public void m_74gpvqpse(Entity c_47ldwddrb, double d, Culler c_72tlvecqx, int i) {
      if (this.client.options.viewDistance != this.renderDistance) {
         this.reload();
      }

      this.world.profiler.push("camera");
      double var6 = c_47ldwddrb.x - this.chunkSectionCameraX;
      double var8 = c_47ldwddrb.y - this.chunkSectionCameraY;
      double var10 = c_47ldwddrb.z - this.chunkSectionCameraZ;
      if (this.cameraChunkX != c_47ldwddrb.chunkX
         || this.cameraChunkY != c_47ldwddrb.chunkY
         || this.cameraChunkZ != c_47ldwddrb.chunkZ
         || var6 * var6 + var8 * var8 + var10 * var10 > 16.0) {
         this.chunkSectionCameraX = c_47ldwddrb.x;
         this.chunkSectionCameraY = c_47ldwddrb.y;
         this.chunkSectionCameraZ = c_47ldwddrb.z;
         this.cameraChunkX = c_47ldwddrb.chunkX;
         this.cameraChunkY = c_47ldwddrb.chunkY;
         this.cameraChunkZ = c_47ldwddrb.chunkZ;
         this.f_23hjlfzwm.m_17tqbccfz(c_47ldwddrb.x, c_47ldwddrb.z);
      }

      this.world.profiler.swap("renderlistcamera");
      double var12 = c_47ldwddrb.prevTickX + (c_47ldwddrb.x - c_47ldwddrb.prevTickX) * d;
      double var14 = c_47ldwddrb.prevTickY + (c_47ldwddrb.y - c_47ldwddrb.prevTickY) * d;
      double var16 = c_47ldwddrb.prevTickZ + (c_47ldwddrb.z - c_47ldwddrb.prevTickZ) * d;
      this.f_22kecqubs.m_04zpydvcx(var12, var14, var16);
      this.world.profiler.swap("cull");
      if (this.clipper != null) {
         FrustumCuller var18 = new FrustumCuller(this.clipper);
         var18.set(this.f_22rmrpijo.x, this.f_22rmrpijo.y, this.f_22rmrpijo.z);
         c_72tlvecqx = var18;
      }

      this.client.profiler.swap("culling");
      BlockPos var34 = new BlockPos(var12, var14 + (double)c_47ldwddrb.getEyeHeight(), var16);
      ChunkBlockRenderer var19 = this.f_23hjlfzwm.m_73iynvurl(var34);
      this.viewChanged = this.viewChanged
         || !this.f_18cxnognh.isEmpty()
         || c_47ldwddrb.x != this.cameraX
         || c_47ldwddrb.y != this.cameraY
         || c_47ldwddrb.z != this.cameraZ
         || (double)c_47ldwddrb.pitch != this.cameraPitch
         || (double)c_47ldwddrb.yaw != this.cameraYaw;
      this.cameraX = c_47ldwddrb.x;
      this.cameraY = c_47ldwddrb.y;
      this.cameraZ = c_47ldwddrb.z;
      this.cameraPitch = (double)c_47ldwddrb.pitch;
      this.cameraYaw = (double)c_47ldwddrb.yaw;
      boolean var20 = this.clipper != null;
      if (!var20 && this.viewChanged) {
         this.viewChanged = false;
         this.f_71vlpncur = Lists.newArrayList();
         LinkedList var21 = Lists.newLinkedList();
         if (var19 == null) {
            int var36 = var34.getY() > 0 ? 248 : 8;

            for(int var39 = -this.renderDistance; var39 <= this.renderDistance; ++var39) {
               for(int var42 = -this.renderDistance; var42 <= this.renderDistance; ++var42) {
                  ChunkBlockRenderer var45 = this.f_23hjlfzwm.m_73iynvurl(new BlockPos((var39 << 4) + 8, var36, (var42 << 4) + 8));
                  if (var45 != null && c_72tlvecqx.isVisible(var45.f_59rfoaecb)) {
                     var45.m_03zdnqaut(i);
                     var21.add(new WorldRenderer.C_10cjdgthg(var45, null, 0));
                  }
               }
            }
         } else {
            WorldRenderer.C_10cjdgthg var22 = new WorldRenderer.C_10cjdgthg(var19, null, 0);
            C_09tthcadg var23 = new C_09tthcadg();
            BlockPos var24 = new BlockPos(var34.getX() >> 4 << 4, var34.getY() >> 4 << 4, var34.getZ() >> 4 << 4);
            WorldChunk var25 = this.world.getChunk(var24);

            for(BlockPos.Mutable var27 : BlockPos.iterateRegionMutable(var24, var24.add(15, 15, 15))) {
               if (var25.getBlock(var27).isOpaqueCube()) {
                  var23.m_76wlglmxq(var27);
               }
            }

            Set var47 = var23.m_22ngzuorx(var34);
            boolean var49 = var47.isEmpty();
            Vector3f var28 = this.m_84ykppcfz(c_47ldwddrb, d);
            Direction var29 = Direction.getClosest(var28.x, var28.y, var28.z).getOpposite();
            var47.remove(var29);
            if (!var49 && var47.isEmpty()) {
               var49 = true;
            }

            this.f_71vlpncur.add(var22);
            if (!var49) {
               var19.m_03zdnqaut(i);

               for(Direction var31 : var47) {
                  ChunkBlockRenderer var32 = this.m_84yztqgoa(var34, var22.f_27rxgdmlv.m_97olwzrjj(), var31);
                  if (var32 != null) {
                     var32.m_03zdnqaut(i);
                     WorldRenderer.C_10cjdgthg var33 = new WorldRenderer.C_10cjdgthg(var32, var31, 0);
                     var33.f_92ncsbxsg.add(var31);
                     var21.add(var33);
                  }
               }
            }
         }

         while(!var21.isEmpty()) {
            WorldRenderer.C_10cjdgthg var37 = (WorldRenderer.C_10cjdgthg)var21.poll();
            ChunkBlockRenderer var40 = var37.f_27rxgdmlv;
            Direction var43 = var37.f_73glpdxlf;
            BlockPos var46 = var40.m_97olwzrjj();
            this.f_71vlpncur.add(var37);

            for(Direction var52 : Direction.values()) {
               if (!this.client.f_40dttiifl || !var37.f_92ncsbxsg.contains(var52.getOpposite())) {
                  ChunkBlockRenderer var53 = this.m_84yztqgoa(var34, var46, var52);
                  if (var53 != null
                     && (!this.client.f_40dttiifl || var43 == null || var40.m_86rgndiak().m_58vwhpzov(var43.getOpposite(), var52))
                     && var53.m_03zdnqaut(i)
                     && c_72tlvecqx.isVisible(var53.f_59rfoaecb)) {
                     WorldRenderer.C_10cjdgthg var54 = new WorldRenderer.C_10cjdgthg(var53, var52, var37.f_30grnaxtk + 1);
                     var54.f_92ncsbxsg.addAll(var37.f_92ncsbxsg);
                     var54.f_92ncsbxsg.add(var52);
                     var21.add(var54);
                  }
               }
            }
         }
      }

      if (this.f_85qrjoukl) {
         this.m_51yhvevgr(var12, var14, var16);
         this.f_85qrjoukl = false;
      }

      this.f_81yxjkkqc.m_13sztraxa();
      ArrayList var35 = Lists.newArrayList(this.f_18cxnognh);
      this.f_18cxnognh.clear();

      for(WorldRenderer.C_10cjdgthg var41 : this.f_71vlpncur) {
         ChunkBlockRenderer var44 = var41.f_27rxgdmlv;
         if (var44.m_68asckfgm() || var44.m_97tfqykgn()) {
            this.viewChanged = true;
            this.f_18cxnognh.add(var44);
         }
      }

      this.f_18cxnognh.addAll(var35);
      this.client.profiler.pop();
   }

   private ChunkBlockRenderer m_84yztqgoa(BlockPos c_76varpwca, BlockPos c_76varpwca2, Direction c_69garkogr) {
      BlockPos var4 = c_76varpwca2.offset(c_69garkogr, 16);
      if (MathHelper.abs(c_76varpwca.getX() - var4.getX()) > this.renderDistance * 16) {
         return null;
      } else if (var4.getY() < 0 || var4.getY() >= 256) {
         return null;
      } else {
         return MathHelper.abs(c_76varpwca.getZ() - var4.getZ()) > this.renderDistance * 16 ? null : this.f_23hjlfzwm.m_73iynvurl(var4);
      }
   }

   private void m_51yhvevgr(double x, double y, double z) {
      this.clipper = new Frustum();
      ((Frustum)this.clipper).compute();
      Matrix4f var7 = new Matrix4f(this.clipper.modelMatrix);
      var7.transpose();
      Matrix4f var8 = new Matrix4f(this.clipper.projectionMatrix);
      var8.transpose();
      Matrix4f var9 = new Matrix4f();
      var9.mul(var8, var7);
      var9.invert();
      this.f_22rmrpijo.x = x;
      this.f_22rmrpijo.y = y;
      this.f_22rmrpijo.z = z;
      this.f_07gmwinho[0] = new Vector4f(-1.0F, -1.0F, -1.0F, 1.0F);
      this.f_07gmwinho[1] = new Vector4f(1.0F, -1.0F, -1.0F, 1.0F);
      this.f_07gmwinho[2] = new Vector4f(1.0F, 1.0F, -1.0F, 1.0F);
      this.f_07gmwinho[3] = new Vector4f(-1.0F, 1.0F, -1.0F, 1.0F);
      this.f_07gmwinho[4] = new Vector4f(-1.0F, -1.0F, 1.0F, 1.0F);
      this.f_07gmwinho[5] = new Vector4f(1.0F, -1.0F, 1.0F, 1.0F);
      this.f_07gmwinho[6] = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.f_07gmwinho[7] = new Vector4f(-1.0F, 1.0F, 1.0F, 1.0F);

      for(int var10 = 0; var10 < 8; ++var10) {
         var9.transform(this.f_07gmwinho[var10]);
         this.f_07gmwinho[var10].x /= this.f_07gmwinho[var10].w;
         this.f_07gmwinho[var10].y /= this.f_07gmwinho[var10].w;
         this.f_07gmwinho[var10].z /= this.f_07gmwinho[var10].w;
         this.f_07gmwinho[var10].w = 1.0F;
      }
   }

   protected Vector3f m_84ykppcfz(Entity c_47ldwddrb, double d) {
      float var4 = (float)((double)c_47ldwddrb.prevPitch + (double)(c_47ldwddrb.pitch - c_47ldwddrb.prevPitch) * d);
      float var5 = (float)((double)c_47ldwddrb.prevYaw + (double)(c_47ldwddrb.yaw - c_47ldwddrb.prevYaw) * d);
      float var6 = MathHelper.cos(-var5 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float var7 = MathHelper.sin(-var5 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float var8 = -MathHelper.cos(-var4 * (float) (Math.PI / 180.0));
      float var9 = MathHelper.sin(-var4 * (float) (Math.PI / 180.0));
      return new Vector3f(var7 * var8, var9, var6 * var8);
   }

   public int m_86stejbes(BlockLayer c_26szrsafr, double d, int i, Entity c_47ldwddrb) {
      Lighting.turnOff();
      if (c_26szrsafr == BlockLayer.TRANSLUCENT) {
         this.client.profiler.push("translucent_sort");
         double var6 = c_47ldwddrb.x - this.lastCameraX;
         double var8 = c_47ldwddrb.y - this.lastCameraY;
         double var10 = c_47ldwddrb.z - this.lastCameraZ;
         if (var6 * var6 + var8 * var8 + var10 * var10 > 1.0) {
            this.lastCameraX = c_47ldwddrb.x;
            this.lastCameraY = c_47ldwddrb.y;
            this.lastCameraZ = c_47ldwddrb.z;
            int var12 = 0;

            for(WorldRenderer.C_10cjdgthg var14 : this.f_71vlpncur) {
               if (var14.f_27rxgdmlv.f_10rxmxrqu.m_06fysprsm(c_26szrsafr) && var12++ < 15) {
                  this.f_81yxjkkqc.m_25bvfbldo(var14.f_27rxgdmlv);
               }
            }
         }

         this.client.profiler.pop();
      }

      this.client.profiler.push("filterempty");
      int var15 = 0;
      boolean var7 = c_26szrsafr == BlockLayer.TRANSLUCENT;
      int var16 = var7 ? this.f_71vlpncur.size() - 1 : 0;
      int var9 = var7 ? -1 : this.f_71vlpncur.size();
      int var17 = var7 ? -1 : 1;

      for(int var11 = var16; var11 != var9; var11 += var17) {
         ChunkBlockRenderer var18 = ((WorldRenderer.C_10cjdgthg)this.f_71vlpncur.get(var11)).f_27rxgdmlv;
         if (!var18.m_86rgndiak().m_66hqbsyfn(c_26szrsafr)) {
            ++var15;
            this.f_22kecqubs.m_59lctdfif(var18, c_26szrsafr);
         }
      }

      this.client.profiler.swap("render_" + c_26szrsafr);
      this.renderStages(c_26szrsafr);
      this.client.profiler.pop();
      return var15;
   }

   private void renderStages(BlockLayer c_26szrsafr) {
      this.client.gameRenderer.drawLightmap();
      if (GLX.useVbo()) {
         GL11.glEnableClientState(32884);
         GLX.clientActiveTexture(GLX.GL_TEXTURE0);
         GL11.glEnableClientState(32888);
         GLX.clientActiveTexture(GLX.GL_TEXTURE1);
         GL11.glEnableClientState(32888);
         GLX.clientActiveTexture(GLX.GL_TEXTURE0);
         GL11.glEnableClientState(32886);
      }

      this.f_22kecqubs.m_73mzxhtxq(c_26szrsafr);
      if (GLX.useVbo()) {
         for(VertexFormatElement var4 : DefaultVertexFormat.BLOCK.getElements()) {
            VertexFormatElement.Usage var5 = var4.getUsage();
            int var6 = var4.getIndex();
            switch(var5) {
               case POSITION:
                  GL11.glDisableClientState(32884);
                  break;
               case UV:
                  GLX.clientActiveTexture(GLX.GL_TEXTURE0 + var6);
                  GL11.glDisableClientState(32888);
                  GLX.clientActiveTexture(GLX.GL_TEXTURE0);
                  break;
               case COLOR:
                  GL11.glDisableClientState(32886);
                  GlStateManager.clearColor();
            }
         }
      }

      this.client.gameRenderer.disableTexture2D();
   }

   private void m_67pqgvxiz(Iterator iterator) {
      while(iterator.hasNext()) {
         BlockMiningProgress var2 = (BlockMiningProgress)iterator.next();
         int var3 = var2.getLastUpdateTick();
         if (this.ticks - var3 > 400) {
            iterator.remove();
         }
      }
   }

   public void tick() {
      ++this.ticks;
      if (this.ticks % 20 == 0) {
         this.m_67pqgvxiz(this.miningSites.values().iterator());
         this.m_67pqgvxiz(this.miningProgress.values().iterator());
      }
   }

   private void m_29qtnmjpo() {
      GlStateManager.disableFog();
      GlStateManager.disableAlphaTest();
      GlStateManager.disableBlend();
      GlStateManager.blendFuncSeparate(770, 771, 1, 0);
      Lighting.turnOff();
      GlStateManager.depthMask(false);
      this.textureManager.bind(END_SKY_TEXTURE);
      Tessellator var1 = Tessellator.getInstance();
      BufferBuilder var2 = var1.getBufferBuilder();

      for(int var3 = 0; var3 < 6; ++var3) {
         GlStateManager.pushMatrix();
         if (var3 == 1) {
            GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
         }

         if (var3 == 2) {
            GlStateManager.rotatef(-90.0F, 1.0F, 0.0F, 0.0F);
         }

         if (var3 == 3) {
            GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
         }

         if (var3 == 4) {
            GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
         }

         if (var3 == 5) {
            GlStateManager.rotatef(-90.0F, 0.0F, 0.0F, 1.0F);
         }

         var2.start();
         var2.color(2631720);
         var2.vertex(-100.0, -100.0, -100.0, 0.0, 0.0);
         var2.vertex(-100.0, -100.0, 100.0, 0.0, 16.0);
         var2.vertex(100.0, -100.0, 100.0, 16.0, 16.0);
         var2.vertex(100.0, -100.0, -100.0, 16.0, 0.0);
         var1.end();
         GlStateManager.popMatrix();
      }

      GlStateManager.depthMask(true);
      GlStateManager.enableTexture();
      GlStateManager.enableAlphaTest();
   }

   public void renderSky(float tickDelta, int i) {
      if (this.client.world.dimension.getId() == 1) {
         this.m_29qtnmjpo();
      } else if (this.client.world.dimension.isOverworld()) {
         GlStateManager.disableTexture();
         Vec3d var3 = this.world.getSkyColor(this.client.getCamera(), tickDelta);
         float var4 = (float)var3.x;
         float var5 = (float)var3.y;
         float var6 = (float)var3.z;
         if (i != 2) {
            float var7 = (var4 * 30.0F + var5 * 59.0F + var6 * 11.0F) / 100.0F;
            float var8 = (var4 * 30.0F + var5 * 70.0F) / 100.0F;
            float var9 = (var4 * 30.0F + var6 * 70.0F) / 100.0F;
            var4 = var7;
            var5 = var8;
            var6 = var9;
         }

         GlStateManager.color3f(var4, var5, var6);
         Tessellator var23 = Tessellator.getInstance();
         BufferBuilder var24 = var23.getBufferBuilder();
         GlStateManager.depthMask(false);
         GlStateManager.enableFog();
         GlStateManager.color3f(var4, var5, var6);
         if (this.f_60mzvsdpq) {
            this.f_84heolmsu.bind();
            GL11.glEnableClientState(32884);
            GL11.glVertexPointer(3, 5126, 12, 0L);
            this.f_84heolmsu.draw(7);
            this.f_84heolmsu.unbind();
            GL11.glDisableClientState(32884);
         } else {
            GlStateManager.callList(this.f_17devwhab);
         }

         GlStateManager.disableFog();
         GlStateManager.disableAlphaTest();
         GlStateManager.disableBlend();
         GlStateManager.blendFuncSeparate(770, 771, 1, 0);
         Lighting.turnOff();
         float[] var25 = this.world.dimension.getBackgroundColor(this.world.getTimeOfDay(tickDelta), tickDelta);
         if (var25 != null) {
            GlStateManager.disableTexture();
            GlStateManager.shadeModel(7425);
            GlStateManager.pushMatrix();
            GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(MathHelper.sin(this.world.getSunAngle(tickDelta)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotatef(90.0F, 0.0F, 0.0F, 1.0F);
            float var10 = var25[0];
            float var11 = var25[1];
            float var12 = var25[2];
            if (i != 2) {
               float var13 = (var10 * 30.0F + var11 * 59.0F + var12 * 11.0F) / 100.0F;
               float var14 = (var10 * 30.0F + var11 * 70.0F) / 100.0F;
               float var15 = (var10 * 30.0F + var12 * 70.0F) / 100.0F;
               var10 = var13;
               var11 = var14;
               var12 = var15;
            }

            var24.start(6);
            var24.color(var10, var11, var12, var25[3]);
            var24.vertex(0.0, 100.0, 0.0);
            boolean var31 = true;
            var24.color(var25[0], var25[1], var25[2], 0.0F);

            for(int var34 = 0; var34 <= 16; ++var34) {
               float var38 = (float)var34 * (float) Math.PI * 2.0F / 16.0F;
               float var16 = MathHelper.sin(var38);
               float var17 = MathHelper.cos(var38);
               var24.vertex((double)(var16 * 120.0F), (double)(var17 * 120.0F), (double)(-var17 * 40.0F * var25[3]));
            }

            var23.end();
            GlStateManager.popMatrix();
            GlStateManager.shadeModel(7424);
         }

         GlStateManager.enableTexture();
         GlStateManager.blendFuncSeparate(770, 1, 1, 0);
         GlStateManager.pushMatrix();
         float var26 = 1.0F - this.world.getRain(tickDelta);
         float var28 = 0.0F;
         float var29 = 0.0F;
         float var32 = 0.0F;
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, var26);
         GlStateManager.translatef(0.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(-90.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(this.world.getTimeOfDay(tickDelta) * 360.0F, 1.0F, 0.0F, 0.0F);
         float var35 = 30.0F;
         this.textureManager.bind(SUN_TEXTURE);
         var24.start();
         var24.vertex((double)(-var35), 100.0, (double)(-var35), 0.0, 0.0);
         var24.vertex((double)var35, 100.0, (double)(-var35), 1.0, 0.0);
         var24.vertex((double)var35, 100.0, (double)var35, 1.0, 1.0);
         var24.vertex((double)(-var35), 100.0, (double)var35, 0.0, 1.0);
         var23.end();
         var35 = 20.0F;
         this.textureManager.bind(MOON_PHASES_TEXTURE);
         int var39 = this.world.getMoonPhase();
         int var40 = var39 % 4;
         int var41 = var39 / 4 % 2;
         float var18 = (float)(var40 + 0) / 4.0F;
         float var19 = (float)(var41 + 0) / 2.0F;
         float var20 = (float)(var40 + 1) / 4.0F;
         float var21 = (float)(var41 + 1) / 2.0F;
         var24.start();
         var24.vertex((double)(-var35), -100.0, (double)var35, (double)var20, (double)var21);
         var24.vertex((double)var35, -100.0, (double)var35, (double)var18, (double)var21);
         var24.vertex((double)var35, -100.0, (double)(-var35), (double)var18, (double)var19);
         var24.vertex((double)(-var35), -100.0, (double)(-var35), (double)var20, (double)var19);
         var23.end();
         GlStateManager.disableTexture();
         float var22 = this.world.getStarBrightness(tickDelta) * var26;
         if (var22 > 0.0F) {
            GlStateManager.color4f(var22, var22, var22, var22);
            if (this.f_60mzvsdpq) {
               this.f_88diyyyvr.bind();
               GL11.glEnableClientState(32884);
               GL11.glVertexPointer(3, 5126, 12, 0L);
               this.f_88diyyyvr.draw(7);
               this.f_88diyyyvr.unbind();
               GL11.glDisableClientState(32884);
            } else {
               GlStateManager.callList(this.f_46yjabdvl);
            }
         }

         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableBlend();
         GlStateManager.enableAlphaTest();
         GlStateManager.enableFog();
         GlStateManager.popMatrix();
         GlStateManager.disableTexture();
         GlStateManager.color3f(0.0F, 0.0F, 0.0F);
         double var27 = this.client.player.m_24itdohjr(tickDelta).y - this.world.getHorizonHeight();
         if (var27 < 0.0) {
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0F, 12.0F, 0.0F);
            if (this.f_60mzvsdpq) {
               this.f_97wypqipn.bind();
               GL11.glEnableClientState(32884);
               GL11.glVertexPointer(3, 5126, 12, 0L);
               this.f_97wypqipn.draw(7);
               this.f_97wypqipn.unbind();
               GL11.glDisableClientState(32884);
            } else {
               GlStateManager.callList(this.f_74ifhicpj);
            }

            GlStateManager.popMatrix();
            var29 = 1.0F;
            var32 = -((float)(var27 + 65.0));
            var35 = -1.0F;
            var24.start();
            var24.color(0, 255);
            var24.vertex(-1.0, (double)var32, 1.0);
            var24.vertex(1.0, (double)var32, 1.0);
            var24.vertex(1.0, -1.0, 1.0);
            var24.vertex(-1.0, -1.0, 1.0);
            var24.vertex(-1.0, -1.0, -1.0);
            var24.vertex(1.0, -1.0, -1.0);
            var24.vertex(1.0, (double)var32, -1.0);
            var24.vertex(-1.0, (double)var32, -1.0);
            var24.vertex(1.0, -1.0, -1.0);
            var24.vertex(1.0, -1.0, 1.0);
            var24.vertex(1.0, (double)var32, 1.0);
            var24.vertex(1.0, (double)var32, -1.0);
            var24.vertex(-1.0, (double)var32, -1.0);
            var24.vertex(-1.0, (double)var32, 1.0);
            var24.vertex(-1.0, -1.0, 1.0);
            var24.vertex(-1.0, -1.0, -1.0);
            var24.vertex(-1.0, -1.0, -1.0);
            var24.vertex(-1.0, -1.0, 1.0);
            var24.vertex(1.0, -1.0, 1.0);
            var24.vertex(1.0, -1.0, -1.0);
            var23.end();
         }

         if (this.world.dimension.hasGround()) {
            GlStateManager.color3f(var4 * 0.2F + 0.04F, var5 * 0.2F + 0.04F, var6 * 0.6F + 0.1F);
         } else {
            GlStateManager.color3f(var4, var5, var6);
         }

         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.0F, -((float)(var27 - 16.0)), 0.0F);
         GlStateManager.callList(this.f_74ifhicpj);
         GlStateManager.popMatrix();
         GlStateManager.enableTexture();
         GlStateManager.depthMask(true);
      }
   }

   public void renderClouds(float tickDelta, int i) {
      if (this.client.world.dimension.isOverworld()) {
         if (this.client.options.fancyGraphics) {
            this.renderFancyClouds(tickDelta, i);
         } else {
            GlStateManager.disableCull();
            float var3 = (float)(this.client.getCamera().prevTickY + (this.client.getCamera().y - this.client.getCamera().prevTickY) * (double)tickDelta);
            boolean var4 = true;
            boolean var5 = true;
            Tessellator var6 = Tessellator.getInstance();
            BufferBuilder var7 = var6.getBufferBuilder();
            this.textureManager.bind(CLOUDS_TEXTURE);
            GlStateManager.disableBlend();
            GlStateManager.blendFuncSeparate(770, 771, 1, 0);
            Vec3d var8 = this.world.getCloudColor(tickDelta);
            float var9 = (float)var8.x;
            float var10 = (float)var8.y;
            float var11 = (float)var8.z;
            if (i != 2) {
               float var12 = (var9 * 30.0F + var10 * 59.0F + var11 * 11.0F) / 100.0F;
               float var13 = (var9 * 30.0F + var10 * 70.0F) / 100.0F;
               float var14 = (var9 * 30.0F + var11 * 70.0F) / 100.0F;
               var9 = var12;
               var10 = var13;
               var11 = var14;
            }

            float var26 = 4.8828125E-4F;
            double var27 = (double)((float)this.ticks + tickDelta);
            double var15 = this.client.getCamera().prevX + (this.client.getCamera().x - this.client.getCamera().prevX) * (double)tickDelta + var27 * 0.03F;
            double var17 = this.client.getCamera().prevZ + (this.client.getCamera().z - this.client.getCamera().prevZ) * (double)tickDelta;
            int var19 = MathHelper.floor(var15 / 2048.0);
            int var20 = MathHelper.floor(var17 / 2048.0);
            var15 -= (double)(var19 * 2048);
            var17 -= (double)(var20 * 2048);
            float var21 = this.world.dimension.getCloudHeight() - var3 + 0.33F;
            float var22 = (float)(var15 * 4.8828125E-4);
            float var23 = (float)(var17 * 4.8828125E-4);
            var7.start();
            var7.color(var9, var10, var11, 0.8F);

            for(int var24 = -256; var24 < 256; var24 += 32) {
               for(int var25 = -256; var25 < 256; var25 += 32) {
                  var7.vertex(
                     (double)(var24 + 0),
                     (double)var21,
                     (double)(var25 + 32),
                     (double)((float)(var24 + 0) * 4.8828125E-4F + var22),
                     (double)((float)(var25 + 32) * 4.8828125E-4F + var23)
                  );
                  var7.vertex(
                     (double)(var24 + 32),
                     (double)var21,
                     (double)(var25 + 32),
                     (double)((float)(var24 + 32) * 4.8828125E-4F + var22),
                     (double)((float)(var25 + 32) * 4.8828125E-4F + var23)
                  );
                  var7.vertex(
                     (double)(var24 + 32),
                     (double)var21,
                     (double)(var25 + 0),
                     (double)((float)(var24 + 32) * 4.8828125E-4F + var22),
                     (double)((float)(var25 + 0) * 4.8828125E-4F + var23)
                  );
                  var7.vertex(
                     (double)(var24 + 0),
                     (double)var21,
                     (double)(var25 + 0),
                     (double)((float)(var24 + 0) * 4.8828125E-4F + var22),
                     (double)((float)(var25 + 0) * 4.8828125E-4F + var23)
                  );
               }
            }

            var6.end();
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableBlend();
            GlStateManager.enableCull();
         }
      }
   }

   public boolean hasThiccFog(double x, double y, double f, float g) {
      return false;
   }

   private void renderFancyClouds(float tickDelta, int i) {
      GlStateManager.disableCull();
      float var3 = (float)(this.client.getCamera().prevTickY + (this.client.getCamera().y - this.client.getCamera().prevTickY) * (double)tickDelta);
      Tessellator var4 = Tessellator.getInstance();
      BufferBuilder var5 = var4.getBufferBuilder();
      float var6 = 12.0F;
      float var7 = 4.0F;
      double var8 = (double)((float)this.ticks + tickDelta);
      double var10 = (this.client.getCamera().prevX + (this.client.getCamera().x - this.client.getCamera().prevX) * (double)tickDelta + var8 * 0.03F) / 12.0;
      double var12 = (this.client.getCamera().prevZ + (this.client.getCamera().z - this.client.getCamera().prevZ) * (double)tickDelta) / 12.0 + 0.33F;
      float var14 = this.world.dimension.getCloudHeight() - var3 + 0.33F;
      int var15 = MathHelper.floor(var10 / 2048.0);
      int var16 = MathHelper.floor(var12 / 2048.0);
      var10 -= (double)(var15 * 2048);
      var12 -= (double)(var16 * 2048);
      this.textureManager.bind(CLOUDS_TEXTURE);
      GlStateManager.disableBlend();
      GlStateManager.blendFuncSeparate(770, 771, 1, 0);
      Vec3d var17 = this.world.getCloudColor(tickDelta);
      float var18 = (float)var17.x;
      float var19 = (float)var17.y;
      float var20 = (float)var17.z;
      if (i != 2) {
         float var21 = (var18 * 30.0F + var19 * 59.0F + var20 * 11.0F) / 100.0F;
         float var22 = (var18 * 30.0F + var19 * 70.0F) / 100.0F;
         float var23 = (var18 * 30.0F + var20 * 70.0F) / 100.0F;
         var18 = var21;
         var19 = var22;
         var20 = var23;
      }

      float var39 = 0.00390625F;
      float var40 = (float)MathHelper.floor(var10) * 0.00390625F;
      float var41 = (float)MathHelper.floor(var12) * 0.00390625F;
      float var24 = (float)(var10 - (double)MathHelper.floor(var10));
      float var25 = (float)(var12 - (double)MathHelper.floor(var12));
      boolean var26 = true;
      boolean var27 = true;
      float var28 = 9.765625E-4F;
      GlStateManager.scalef(12.0F, 1.0F, 12.0F);

      for(int var29 = 0; var29 < 2; ++var29) {
         if (var29 == 0) {
            GlStateManager.colorMask(false, false, false, false);
         } else {
            switch(i) {
               case 0:
                  GlStateManager.colorMask(false, true, true, true);
                  break;
               case 1:
                  GlStateManager.colorMask(true, false, false, true);
                  break;
               case 2:
                  GlStateManager.colorMask(true, true, true, true);
            }
         }

         for(int var30 = -3; var30 <= 4; ++var30) {
            for(int var31 = -3; var31 <= 4; ++var31) {
               var5.start();
               float var32 = (float)(var30 * 8);
               float var33 = (float)(var31 * 8);
               float var34 = var32 - var24;
               float var35 = var33 - var25;
               if (var14 > -5.0F) {
                  var5.color(var18 * 0.7F, var19 * 0.7F, var20 * 0.7F, 0.8F);
                  var5.normal(0.0F, -1.0F, 0.0F);
                  var5.vertex(
                     (double)(var34 + 0.0F),
                     (double)(var14 + 0.0F),
                     (double)(var35 + 8.0F),
                     (double)((var32 + 0.0F) * 0.00390625F + var40),
                     (double)((var33 + 8.0F) * 0.00390625F + var41)
                  );
                  var5.vertex(
                     (double)(var34 + 8.0F),
                     (double)(var14 + 0.0F),
                     (double)(var35 + 8.0F),
                     (double)((var32 + 8.0F) * 0.00390625F + var40),
                     (double)((var33 + 8.0F) * 0.00390625F + var41)
                  );
                  var5.vertex(
                     (double)(var34 + 8.0F),
                     (double)(var14 + 0.0F),
                     (double)(var35 + 0.0F),
                     (double)((var32 + 8.0F) * 0.00390625F + var40),
                     (double)((var33 + 0.0F) * 0.00390625F + var41)
                  );
                  var5.vertex(
                     (double)(var34 + 0.0F),
                     (double)(var14 + 0.0F),
                     (double)(var35 + 0.0F),
                     (double)((var32 + 0.0F) * 0.00390625F + var40),
                     (double)((var33 + 0.0F) * 0.00390625F + var41)
                  );
               }

               if (var14 <= 5.0F) {
                  var5.color(var18, var19, var20, 0.8F);
                  var5.normal(0.0F, 1.0F, 0.0F);
                  var5.vertex(
                     (double)(var34 + 0.0F),
                     (double)(var14 + 4.0F - 9.765625E-4F),
                     (double)(var35 + 8.0F),
                     (double)((var32 + 0.0F) * 0.00390625F + var40),
                     (double)((var33 + 8.0F) * 0.00390625F + var41)
                  );
                  var5.vertex(
                     (double)(var34 + 8.0F),
                     (double)(var14 + 4.0F - 9.765625E-4F),
                     (double)(var35 + 8.0F),
                     (double)((var32 + 8.0F) * 0.00390625F + var40),
                     (double)((var33 + 8.0F) * 0.00390625F + var41)
                  );
                  var5.vertex(
                     (double)(var34 + 8.0F),
                     (double)(var14 + 4.0F - 9.765625E-4F),
                     (double)(var35 + 0.0F),
                     (double)((var32 + 8.0F) * 0.00390625F + var40),
                     (double)((var33 + 0.0F) * 0.00390625F + var41)
                  );
                  var5.vertex(
                     (double)(var34 + 0.0F),
                     (double)(var14 + 4.0F - 9.765625E-4F),
                     (double)(var35 + 0.0F),
                     (double)((var32 + 0.0F) * 0.00390625F + var40),
                     (double)((var33 + 0.0F) * 0.00390625F + var41)
                  );
               }

               var5.color(var18 * 0.9F, var19 * 0.9F, var20 * 0.9F, 0.8F);
               if (var30 > -1) {
                  var5.normal(-1.0F, 0.0F, 0.0F);

                  for(int var36 = 0; var36 < 8; ++var36) {
                     var5.vertex(
                        (double)(var34 + (float)var36 + 0.0F),
                        (double)(var14 + 0.0F),
                        (double)(var35 + 8.0F),
                        (double)((var32 + (float)var36 + 0.5F) * 0.00390625F + var40),
                        (double)((var33 + 8.0F) * 0.00390625F + var41)
                     );
                     var5.vertex(
                        (double)(var34 + (float)var36 + 0.0F),
                        (double)(var14 + 4.0F),
                        (double)(var35 + 8.0F),
                        (double)((var32 + (float)var36 + 0.5F) * 0.00390625F + var40),
                        (double)((var33 + 8.0F) * 0.00390625F + var41)
                     );
                     var5.vertex(
                        (double)(var34 + (float)var36 + 0.0F),
                        (double)(var14 + 4.0F),
                        (double)(var35 + 0.0F),
                        (double)((var32 + (float)var36 + 0.5F) * 0.00390625F + var40),
                        (double)((var33 + 0.0F) * 0.00390625F + var41)
                     );
                     var5.vertex(
                        (double)(var34 + (float)var36 + 0.0F),
                        (double)(var14 + 0.0F),
                        (double)(var35 + 0.0F),
                        (double)((var32 + (float)var36 + 0.5F) * 0.00390625F + var40),
                        (double)((var33 + 0.0F) * 0.00390625F + var41)
                     );
                  }
               }

               if (var30 <= 1) {
                  var5.normal(1.0F, 0.0F, 0.0F);

                  for(int var42 = 0; var42 < 8; ++var42) {
                     var5.vertex(
                        (double)(var34 + (float)var42 + 1.0F - 9.765625E-4F),
                        (double)(var14 + 0.0F),
                        (double)(var35 + 8.0F),
                        (double)((var32 + (float)var42 + 0.5F) * 0.00390625F + var40),
                        (double)((var33 + 8.0F) * 0.00390625F + var41)
                     );
                     var5.vertex(
                        (double)(var34 + (float)var42 + 1.0F - 9.765625E-4F),
                        (double)(var14 + 4.0F),
                        (double)(var35 + 8.0F),
                        (double)((var32 + (float)var42 + 0.5F) * 0.00390625F + var40),
                        (double)((var33 + 8.0F) * 0.00390625F + var41)
                     );
                     var5.vertex(
                        (double)(var34 + (float)var42 + 1.0F - 9.765625E-4F),
                        (double)(var14 + 4.0F),
                        (double)(var35 + 0.0F),
                        (double)((var32 + (float)var42 + 0.5F) * 0.00390625F + var40),
                        (double)((var33 + 0.0F) * 0.00390625F + var41)
                     );
                     var5.vertex(
                        (double)(var34 + (float)var42 + 1.0F - 9.765625E-4F),
                        (double)(var14 + 0.0F),
                        (double)(var35 + 0.0F),
                        (double)((var32 + (float)var42 + 0.5F) * 0.00390625F + var40),
                        (double)((var33 + 0.0F) * 0.00390625F + var41)
                     );
                  }
               }

               var5.color(var18 * 0.8F, var19 * 0.8F, var20 * 0.8F, 0.8F);
               if (var31 > -1) {
                  var5.normal(0.0F, 0.0F, -1.0F);

                  for(int var43 = 0; var43 < 8; ++var43) {
                     var5.vertex(
                        (double)(var34 + 0.0F),
                        (double)(var14 + 4.0F),
                        (double)(var35 + (float)var43 + 0.0F),
                        (double)((var32 + 0.0F) * 0.00390625F + var40),
                        (double)((var33 + (float)var43 + 0.5F) * 0.00390625F + var41)
                     );
                     var5.vertex(
                        (double)(var34 + 8.0F),
                        (double)(var14 + 4.0F),
                        (double)(var35 + (float)var43 + 0.0F),
                        (double)((var32 + 8.0F) * 0.00390625F + var40),
                        (double)((var33 + (float)var43 + 0.5F) * 0.00390625F + var41)
                     );
                     var5.vertex(
                        (double)(var34 + 8.0F),
                        (double)(var14 + 0.0F),
                        (double)(var35 + (float)var43 + 0.0F),
                        (double)((var32 + 8.0F) * 0.00390625F + var40),
                        (double)((var33 + (float)var43 + 0.5F) * 0.00390625F + var41)
                     );
                     var5.vertex(
                        (double)(var34 + 0.0F),
                        (double)(var14 + 0.0F),
                        (double)(var35 + (float)var43 + 0.0F),
                        (double)((var32 + 0.0F) * 0.00390625F + var40),
                        (double)((var33 + (float)var43 + 0.5F) * 0.00390625F + var41)
                     );
                  }
               }

               if (var31 <= 1) {
                  var5.normal(0.0F, 0.0F, 1.0F);

                  for(int var44 = 0; var44 < 8; ++var44) {
                     var5.vertex(
                        (double)(var34 + 0.0F),
                        (double)(var14 + 4.0F),
                        (double)(var35 + (float)var44 + 1.0F - 9.765625E-4F),
                        (double)((var32 + 0.0F) * 0.00390625F + var40),
                        (double)((var33 + (float)var44 + 0.5F) * 0.00390625F + var41)
                     );
                     var5.vertex(
                        (double)(var34 + 8.0F),
                        (double)(var14 + 4.0F),
                        (double)(var35 + (float)var44 + 1.0F - 9.765625E-4F),
                        (double)((var32 + 8.0F) * 0.00390625F + var40),
                        (double)((var33 + (float)var44 + 0.5F) * 0.00390625F + var41)
                     );
                     var5.vertex(
                        (double)(var34 + 8.0F),
                        (double)(var14 + 0.0F),
                        (double)(var35 + (float)var44 + 1.0F - 9.765625E-4F),
                        (double)((var32 + 8.0F) * 0.00390625F + var40),
                        (double)((var33 + (float)var44 + 0.5F) * 0.00390625F + var41)
                     );
                     var5.vertex(
                        (double)(var34 + 0.0F),
                        (double)(var14 + 0.0F),
                        (double)(var35 + (float)var44 + 1.0F - 9.765625E-4F),
                        (double)((var32 + 0.0F) * 0.00390625F + var40),
                        (double)((var33 + (float)var44 + 0.5F) * 0.00390625F + var41)
                     );
                  }
               }

               var4.end();
            }
         }
      }

      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableBlend();
      GlStateManager.enableCull();
   }

   public void m_06udktacf(long l) {
      this.viewChanged |= this.f_81yxjkkqc.m_77oufjbaz(l);
      Iterator var3 = this.f_18cxnognh.iterator();

      while(var3.hasNext()) {
         ChunkBlockRenderer var4 = (ChunkBlockRenderer)var3.next();
         if (!this.f_81yxjkkqc.m_11mswwlvj(var4)) {
            break;
         }

         var4.m_92dylrpem(false);
         var3.remove();
      }
   }

   public void m_28ljxtzwx(Entity c_47ldwddrb, float f) {
      Tessellator var3 = Tessellator.getInstance();
      BufferBuilder var4 = var3.getBufferBuilder();
      WorldBorder var5 = this.world.getWorldBorder();
      double var6 = (double)(this.client.options.viewDistance * 16);
      if (!(c_47ldwddrb.x < var5.getMaxX() - var6)
         || !(c_47ldwddrb.x > var5.getMinX() + var6)
         || !(c_47ldwddrb.z < var5.getMaxZ() - var6)
         || !(c_47ldwddrb.z > var5.getMinZ() + var6)) {
         double var8 = 1.0 - var5.getDistanceFrom(c_47ldwddrb) / var6;
         var8 = Math.pow(var8, 4.0);
         double var10 = c_47ldwddrb.prevTickX + (c_47ldwddrb.x - c_47ldwddrb.prevTickX) * (double)f;
         double var12 = c_47ldwddrb.prevTickY + (c_47ldwddrb.y - c_47ldwddrb.prevTickY) * (double)f;
         double var14 = c_47ldwddrb.prevTickZ + (c_47ldwddrb.z - c_47ldwddrb.prevTickZ) * (double)f;
         GlStateManager.disableBlend();
         GlStateManager.blendFuncSeparate(770, 1, 1, 0);
         this.textureManager.bind(WORLD_BORDER_TEXTURE);
         GlStateManager.depthMask(false);
         GlStateManager.pushMatrix();
         int var16 = var5.getStatus().getColor();
         float var17 = (float)(var16 >> 16 & 0xFF) / 255.0F;
         float var18 = (float)(var16 >> 8 & 0xFF) / 255.0F;
         float var19 = (float)(var16 & 0xFF) / 255.0F;
         GlStateManager.color4f(var17, var18, var19, (float)var8);
         GlStateManager.polygonOffset(-3.0F, -3.0F);
         GlStateManager.enablePolygonOffset();
         GlStateManager.alphaFunc(516, 0.1F);
         GlStateManager.enableAlphaTest();
         GlStateManager.disableCull();
         float var20 = (float)(MinecraftClient.getTime() % 3000L) / 3000.0F;
         float var21 = 0.0F;
         float var22 = 0.0F;
         float var23 = 128.0F;
         var4.start();
         var4.offset(-var10, -var12, -var14);
         var4.uncolored();
         double var24 = Math.max((double)MathHelper.floor(var14 - var6), var5.getMinZ());
         double var26 = Math.min((double)MathHelper.ceil(var14 + var6), var5.getMaxZ());
         if (var10 > var5.getMaxX() - var6) {
            float var28 = 0.0F;

            for(double var29 = var24; var29 < var26; var28 += 0.5F) {
               double var31 = Math.min(1.0, var26 - var29);
               float var33 = (float)var31 * 0.5F;
               var4.vertex(var5.getMaxX(), 256.0, var29, (double)(var20 + var28), (double)(var20 + 0.0F));
               var4.vertex(var5.getMaxX(), 256.0, var29 + var31, (double)(var20 + var33 + var28), (double)(var20 + 0.0F));
               var4.vertex(var5.getMaxX(), 0.0, var29 + var31, (double)(var20 + var33 + var28), (double)(var20 + 128.0F));
               var4.vertex(var5.getMaxX(), 0.0, var29, (double)(var20 + var28), (double)(var20 + 128.0F));
               ++var29;
            }
         }

         if (var10 < var5.getMinX() + var6) {
            float var37 = 0.0F;

            for(double var40 = var24; var40 < var26; var37 += 0.5F) {
               double var43 = Math.min(1.0, var26 - var40);
               float var46 = (float)var43 * 0.5F;
               var4.vertex(var5.getMinX(), 256.0, var40, (double)(var20 + var37), (double)(var20 + 0.0F));
               var4.vertex(var5.getMinX(), 256.0, var40 + var43, (double)(var20 + var46 + var37), (double)(var20 + 0.0F));
               var4.vertex(var5.getMinX(), 0.0, var40 + var43, (double)(var20 + var46 + var37), (double)(var20 + 128.0F));
               var4.vertex(var5.getMinX(), 0.0, var40, (double)(var20 + var37), (double)(var20 + 128.0F));
               ++var40;
            }
         }

         var24 = Math.max((double)MathHelper.floor(var10 - var6), var5.getMinX());
         var26 = Math.min((double)MathHelper.ceil(var10 + var6), var5.getMaxX());
         if (var14 > var5.getMaxZ() - var6) {
            float var38 = 0.0F;

            for(double var41 = var24; var41 < var26; var38 += 0.5F) {
               double var44 = Math.min(1.0, var26 - var41);
               float var47 = (float)var44 * 0.5F;
               var4.vertex(var41, 256.0, var5.getMaxZ(), (double)(var20 + var38), (double)(var20 + 0.0F));
               var4.vertex(var41 + var44, 256.0, var5.getMaxZ(), (double)(var20 + var47 + var38), (double)(var20 + 0.0F));
               var4.vertex(var41 + var44, 0.0, var5.getMaxZ(), (double)(var20 + var47 + var38), (double)(var20 + 128.0F));
               var4.vertex(var41, 0.0, var5.getMaxZ(), (double)(var20 + var38), (double)(var20 + 128.0F));
               ++var41;
            }
         }

         if (var14 < var5.getMinZ() + var6) {
            float var39 = 0.0F;

            for(double var42 = var24; var42 < var26; var39 += 0.5F) {
               double var45 = Math.min(1.0, var26 - var42);
               float var48 = (float)var45 * 0.5F;
               var4.vertex(var42, 256.0, var5.getMinZ(), (double)(var20 + var39), (double)(var20 + 0.0F));
               var4.vertex(var42 + var45, 256.0, var5.getMinZ(), (double)(var20 + var48 + var39), (double)(var20 + 0.0F));
               var4.vertex(var42 + var45, 0.0, var5.getMinZ(), (double)(var20 + var48 + var39), (double)(var20 + 128.0F));
               var4.vertex(var42, 0.0, var5.getMinZ(), (double)(var20 + var39), (double)(var20 + 128.0F));
               ++var42;
            }
         }

         var3.end();
         var4.offset(0.0, 0.0, 0.0);
         GlStateManager.enableCull();
         GlStateManager.disableAlphaTest();
         GlStateManager.polygonOffset(0.0F, 0.0F);
         GlStateManager.disablePolygonOffset();
         GlStateManager.enableAlphaTest();
         GlStateManager.enableBlend();
         GlStateManager.popMatrix();
         GlStateManager.depthMask(true);
      }
   }

   private void m_60xmflqkz() {
      GlStateManager.blendFuncSeparate(774, 768, 1, 0);
      GlStateManager.disableBlend();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.5F);
      GlStateManager.polygonOffset(-3.0F, -3.0F);
      GlStateManager.enablePolygonOffset();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.enableAlphaTest();
      GlStateManager.pushMatrix();
   }

   private void m_93kijynnh() {
      GlStateManager.disableAlphaTest();
      GlStateManager.polygonOffset(0.0F, 0.0F);
      GlStateManager.disablePolygonOffset();
      GlStateManager.enableAlphaTest();
      GlStateManager.depthMask(true);
      GlStateManager.popMatrix();
   }

   public void renderMiningProgress(Tessellator buffer, BufferBuilder player, Entity coordinateScaling, float f) {
      double var5 = coordinateScaling.prevTickX + (coordinateScaling.x - coordinateScaling.prevTickX) * (double)f;
      double var7 = coordinateScaling.prevTickY + (coordinateScaling.y - coordinateScaling.prevTickY) * (double)f;
      double var9 = coordinateScaling.prevTickZ + (coordinateScaling.z - coordinateScaling.prevTickZ) * (double)f;
      if (!this.miningSites.isEmpty()) {
         this.textureManager.bind(SpriteAtlasTexture.BLOCK_ATLAS_BLOCKS);
         this.m_60xmflqkz();
         player.start();
         player.format(DefaultVertexFormat.BLOCK);
         player.offset(-var5, -var7, -var9);
         player.uncolored();
         Iterator var11 = this.miningSites.values().iterator();

         while(var11.hasNext()) {
            BlockMiningProgress var12 = (BlockMiningProgress)var11.next();
            BlockPos var13 = var12.getPos();
            double var14 = (double)var13.getX() - var5;
            double var16 = (double)var13.getY() - var7;
            double var18 = (double)var13.getZ() - var9;
            if (var14 * var14 + var16 * var16 + var18 * var18 > 1024.0) {
               var11.remove();
            } else {
               BlockState var20 = this.world.getBlockState(var13);
               if (var20.getBlock().getMaterial() != Material.AIR) {
                  int var21 = var12.getProgress();
                  TextureAtlasSprite var22 = this.miningProgressTextures[var21];
                  BlockRenderDispatcher var23 = this.client.getBlockRenderDispatcher();
                  var23.renderMining(var20, var13, var22, this.world);
               }
            }
         }

         buffer.end();
         player.offset(0.0, 0.0, 0.0);
         this.m_93kijynnh();
      }
   }

   public void renderBlockOutline(PlayerEntity player, HitResult targetedBlock, int i, float coordinateScaling) {
      if (i == 0 && targetedBlock.type == HitResult.Type.BLOCK) {
         GlStateManager.disableBlend();
         GlStateManager.blendFuncSeparate(770, 771, 1, 0);
         GlStateManager.color4f(0.0F, 0.0F, 0.0F, 0.4F);
         GL11.glLineWidth(2.0F);
         GlStateManager.disableTexture();
         GlStateManager.depthMask(false);
         float var5 = 0.002F;
         BlockPos var6 = targetedBlock.getBlockPos();
         Block var7 = this.world.getBlockState(var6).getBlock();
         if (var7.getMaterial() != Material.AIR && this.world.getWorldBorder().contains(var6)) {
            var7.updateShape(this.world, var6);
            double var8 = player.prevTickX + (player.x - player.prevTickX) * (double)coordinateScaling;
            double var10 = player.prevTickY + (player.y - player.prevTickY) * (double)coordinateScaling;
            double var12 = player.prevTickZ + (player.z - player.prevTickZ) * (double)coordinateScaling;
            renderHitbox(var7.getOutlineShape(this.world, var6).expand(0.002F, 0.002F, 0.002F).move(-var8, -var10, -var12), -1);
         }

         GlStateManager.depthMask(true);
         GlStateManager.enableTexture();
         GlStateManager.enableBlend();
      }
   }

   public static void renderHitbox(Box box, int color) {
      Tessellator var2 = Tessellator.getInstance();
      BufferBuilder var3 = var2.getBufferBuilder();
      var3.start(3);
      if (color != -1) {
         var3.color(color);
      }

      var3.vertex(box.minX, box.minY, box.minZ);
      var3.vertex(box.maxX, box.minY, box.minZ);
      var3.vertex(box.maxX, box.minY, box.maxZ);
      var3.vertex(box.minX, box.minY, box.maxZ);
      var3.vertex(box.minX, box.minY, box.minZ);
      var2.end();
      var3.start(3);
      if (color != -1) {
         var3.color(color);
      }

      var3.vertex(box.minX, box.maxY, box.minZ);
      var3.vertex(box.maxX, box.maxY, box.minZ);
      var3.vertex(box.maxX, box.maxY, box.maxZ);
      var3.vertex(box.minX, box.maxY, box.maxZ);
      var3.vertex(box.minX, box.maxY, box.minZ);
      var2.end();
      var3.start(1);
      if (color != -1) {
         var3.color(color);
      }

      var3.vertex(box.minX, box.minY, box.minZ);
      var3.vertex(box.minX, box.maxY, box.minZ);
      var3.vertex(box.maxX, box.minY, box.minZ);
      var3.vertex(box.maxX, box.maxY, box.minZ);
      var3.vertex(box.maxX, box.minY, box.maxZ);
      var3.vertex(box.maxX, box.maxY, box.maxZ);
      var3.vertex(box.minX, box.minY, box.maxZ);
      var3.vertex(box.minX, box.maxY, box.maxZ);
      var2.end();
   }

   private void stageChunkSectionRerender(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      this.f_23hjlfzwm.m_23nxycbeg(minX, minY, minZ, maxX, maxY, maxZ);
   }

   @Override
   public void onBlockChanged(BlockPos x) {
      int var2 = x.getX();
      int var3 = x.getY();
      int var4 = x.getZ();
      this.stageChunkSectionRerender(var2 - 1, var3 - 1, var4 - 1, var2 + 1, var3 + 1, var4 + 1);
   }

   @Override
   public void onLightChanged(BlockPos pos) {
      int var2 = pos.getX();
      int var3 = pos.getY();
      int var4 = pos.getZ();
      this.stageChunkSectionRerender(var2 - 1, var3 - 1, var4 - 1, var2 + 1, var3 + 1, var4 + 1);
   }

   @Override
   public void onRegionChanged(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      this.stageChunkSectionRerender(minX - 1, minY - 1, minZ - 1, maxX + 1, maxY + 1, maxZ + 1);
   }

   @Override
   public void onRecordRemoved(String record, BlockPos pos) {
      ISoundEvent var3 = (ISoundEvent)this.playingSongs.get(pos);
      if (var3 != null) {
         this.client.getSoundManager().stop(var3);
         this.playingSongs.remove(pos);
      }

      if (record != null) {
         MusicDiscItem var4 = MusicDiscItem.getByName(record);
         if (var4 != null) {
            this.client.gui.setRecordPlayingOverlay(var4.getDescription());
         }

         SimpleSoundEvent var5 = SimpleSoundEvent.of(new Identifier(record), (float)pos.getX(), (float)pos.getY(), (float)pos.getZ());
         this.playingSongs.put(pos, var5);
         this.client.getSoundManager().play(var5);
      }
   }

   @Override
   public void playSound(String name, double x, double y, double z, float pitch, float volume) {
   }

   @Override
   public void playSound(PlayerEntity source, String name, double x, double y, double z, float pitch, float volume) {
   }

   @Override
   public void addParticle(
      int type, boolean ignoreDistance, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters
   ) {
      try {
         this.tryAddParticle(type, ignoreDistance, x, y, z, velocityX, velocityY, velocityZ, parameters);
      } catch (Throwable var19) {
         CrashReport var17 = CrashReport.of(var19, "Exception while adding particle");
         CashReportCategory var18 = var17.addCategory("Particle being added");
         var18.add("ID", type);
         if (parameters != null) {
            var18.add("Parameters", parameters);
         }

         var18.add("Position", new Callable() {
            public String call() {
               return CashReportCategory.formatPosition(x, y, z);
            }
         });
         throw new CrashException(var17);
      }
   }

   private void addParticle(ParticleType type, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters) {
      this.addParticle(type.getId(), type.ignoreDistance(), x, y, z, velocityX, velocityY, velocityZ, parameters);
   }

   private Particle tryAddParticle(
      int type, boolean ignoreDistance, double x, double y, double z, double velocityX, double velocityY, double velocityZ, int... parameters
   ) {
      if (this.client != null && this.client.getCamera() != null && this.client.particleManager != null) {
         int var16 = this.client.options.particles;
         if (var16 == 1 && this.world.random.nextInt(3) == 0) {
            var16 = 2;
         }

         double var17 = this.client.getCamera().x - x;
         double var19 = this.client.getCamera().y - y;
         double var21 = this.client.getCamera().z - z;
         if (ignoreDistance) {
            return this.client.particleManager.addParticle(type, x, y, z, velocityX, velocityY, velocityZ, parameters);
         } else {
            double var23 = 16.0;
            if (var17 * var17 + var19 * var19 + var21 * var21 > 256.0) {
               return null;
            } else {
               return var16 > 1 ? null : this.client.particleManager.addParticle(type, x, y, z, velocityX, velocityY, velocityZ, parameters);
            }
         }
      } else {
         return null;
      }
   }

   @Override
   public void onEntityAdded(Entity entity) {
   }

   @Override
   public void onEntityRemoved(Entity entity) {
   }

   public void m_18qvuyrzr() {
   }

   @Override
   public void doGlobalEvent(int type, BlockPos pos, int data) {
      switch(type) {
         case 1013:
         case 1018:
            if (this.client.getCamera() != null) {
               double var4 = (double)pos.getX() - this.client.getCamera().x;
               double var6 = (double)pos.getY() - this.client.getCamera().y;
               double var8 = (double)pos.getZ() - this.client.getCamera().z;
               double var10 = Math.sqrt(var4 * var4 + var6 * var6 + var8 * var8);
               double var12 = this.client.getCamera().x;
               double var14 = this.client.getCamera().y;
               double var16 = this.client.getCamera().z;
               if (var10 > 0.0) {
                  var12 += var4 / var10 * 2.0;
                  var14 += var6 / var10 * 2.0;
                  var16 += var8 / var10 * 2.0;
               }

               if (type == 1013) {
                  this.world.playSound(var12, var14, var16, "mob.wither.spawn", 1.0F, 1.0F, false);
               } else {
                  this.world.playSound(var12, var14, var16, "mob.enderdragon.end", 5.0F, 1.0F, false);
               }
            }
      }
   }

   @Override
   public void doEvent(PlayerEntity source, int type, BlockPos pos, int data) {
      Random var5 = this.world.random;
      switch(type) {
         case 1000:
            this.world.playSound(pos, "random.click", 1.0F, 1.0F, false);
            break;
         case 1001:
            this.world.playSound(pos, "random.click", 1.0F, 1.2F, false);
            break;
         case 1002:
            this.world.playSound(pos, "random.bow", 1.0F, 1.2F, false);
            break;
         case 1003:
            if (Math.random() < 0.5) {
               this.world.playSound(pos, "random.door_open", 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
            } else {
               this.world.playSound(pos, "random.door_close", 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
            }
            break;
         case 1004:
            this.world.playSound(pos, "random.fizz", 0.5F, 2.6F + (var5.nextFloat() - var5.nextFloat()) * 0.8F, false);
            break;
         case 1005:
            if (Item.byRawId(data) instanceof MusicDiscItem) {
               this.world.onRecordRemoved(pos, "records." + ((MusicDiscItem)Item.byRawId(data)).recordType);
            } else {
               this.world.onRecordRemoved(pos, null);
            }
            break;
         case 1007:
            this.world.playSound(pos, "mob.ghast.charge", 10.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1008:
            this.world.playSound(pos, "mob.ghast.fireball", 10.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1009:
            this.world.playSound(pos, "mob.ghast.fireball", 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1010:
            this.world.playSound(pos, "mob.zombie.wood", 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1011:
            this.world.playSound(pos, "mob.zombie.metal", 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1012:
            this.world.playSound(pos, "mob.zombie.woodbreak", 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1014:
            this.world.playSound(pos, "mob.wither.shoot", 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1015:
            this.world.playSound(pos, "mob.bat.takeoff", 0.05F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1016:
            this.world.playSound(pos, "mob.zombie.infect", 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1017:
            this.world.playSound(pos, "mob.zombie.unfect", 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1020:
            this.world.playSound(pos, "random.anvil_break", 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1021:
            this.world.playSound(pos, "random.anvil_use", 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1022:
            this.world.playSound(pos, "random.anvil_land", 0.3F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 2000:
            int var32 = data % 3 - 1;
            int var8 = data / 3 % 3 - 1;
            double var34 = (double)pos.getX() + (double)var32 * 0.6 + 0.5;
            double var36 = (double)pos.getY() + 0.5;
            double var40 = (double)pos.getZ() + (double)var8 * 0.6 + 0.5;

            for(int var41 = 0; var41 < 10; ++var41) {
               double var42 = var5.nextDouble() * 0.2 + 0.01;
               double var44 = var34 + (double)var32 * 0.01 + (var5.nextDouble() - 0.5) * (double)var8 * 0.5;
               double var20 = var36 + (var5.nextDouble() - 0.5) * 0.5;
               double var22 = var40 + (double)var8 * 0.01 + (var5.nextDouble() - 0.5) * (double)var32 * 0.5;
               double var24 = (double)var32 * var42 + var5.nextGaussian() * 0.01;
               double var26 = -0.03 + var5.nextGaussian() * 0.01;
               double var28 = (double)var8 * var42 + var5.nextGaussian() * 0.01;
               this.addParticle(ParticleType.SMOKE_NORMAL, var44, var20, var22, var24, var26, var28);
            }
            break;
         case 2001:
            Block var6 = Block.byRawId(data & 4095);
            if (var6.getMaterial() != Material.AIR) {
               this.client
                  .getSoundManager()
                  .play(
                     new SimpleSoundEvent(
                        new Identifier(var6.sound.getDigSound()),
                        (var6.sound.getVolume() + 1.0F) / 2.0F,
                        var6.sound.getPitch() * 0.8F,
                        (float)pos.getX() + 0.5F,
                        (float)pos.getY() + 0.5F,
                        (float)pos.getZ() + 0.5F
                     )
                  );
            }

            this.client.particleManager.addBlockMiningParticles(pos, var6.getStateFromMetadata(data >> 12 & 0xFF));
            break;
         case 2002:
            double var31 = (double)pos.getX();
            double var33 = (double)pos.getY();
            double var35 = (double)pos.getZ();

            for(int var38 = 0; var38 < 8; ++var38) {
               this.addParticle(
                  ParticleType.ITEM_CRACK,
                  var31,
                  var33,
                  var35,
                  var5.nextGaussian() * 0.15,
                  var5.nextDouble() * 0.2,
                  var5.nextGaussian() * 0.15,
                  Item.getRawId(Items.POTION),
                  data
               );
            }

            int var39 = Items.POTION.getPotionColor(data);
            float var14 = (float)(var39 >> 16 & 0xFF) / 255.0F;
            float var15 = (float)(var39 >> 8 & 0xFF) / 255.0F;
            float var16 = (float)(var39 >> 0 & 0xFF) / 255.0F;
            ParticleType var17 = ParticleType.SPELL;
            if (Items.POTION.hasPotionMetadataEfects(data)) {
               var17 = ParticleType.SPELL_INSTANT;
            }

            for(int var43 = 0; var43 < 100; ++var43) {
               double var45 = var5.nextDouble() * 4.0;
               double var46 = var5.nextDouble() * Math.PI * 2.0;
               double var47 = Math.cos(var46) * var45;
               double var25 = 0.01 + var5.nextDouble() * 0.5;
               double var27 = Math.sin(var46) * var45;
               Particle var29 = this.tryAddParticle(
                  var17.getId(), var17.ignoreDistance(), var31 + var47 * 0.1, var33 + 0.3, var35 + var27 * 0.1, var47, var25, var27
               );
               if (var29 != null) {
                  float var30 = 0.75F + var5.nextFloat() * 0.25F;
                  var29.setColor(var14 * var30, var15 * var30, var16 * var30);
                  var29.multiplyVelocity((float)var45);
               }
            }

            this.world.playSound(pos, "game.potion.smash", 1.0F, this.world.random.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 2003:
            double var7 = (double)pos.getX() + 0.5;
            double var9 = (double)pos.getY();
            double var11 = (double)pos.getZ() + 0.5;

            for(int var13 = 0; var13 < 8; ++var13) {
               this.addParticle(
                  ParticleType.ITEM_CRACK,
                  var7,
                  var9,
                  var11,
                  var5.nextGaussian() * 0.15,
                  var5.nextDouble() * 0.2,
                  var5.nextGaussian() * 0.15,
                  Item.getRawId(Items.ENDER_EYE)
               );
            }

            for(double var37 = 0.0; var37 < Math.PI * 2; var37 += Math.PI / 20) {
               this.addParticle(
                  ParticleType.PORTAL,
                  var7 + Math.cos(var37) * 5.0,
                  var9 - 0.4,
                  var11 + Math.sin(var37) * 5.0,
                  Math.cos(var37) * -5.0,
                  0.0,
                  Math.sin(var37) * -5.0
               );
               this.addParticle(
                  ParticleType.PORTAL,
                  var7 + Math.cos(var37) * 5.0,
                  var9 - 0.4,
                  var11 + Math.sin(var37) * 5.0,
                  Math.cos(var37) * -7.0,
                  0.0,
                  Math.sin(var37) * -7.0
               );
            }
            break;
         case 2004:
            for(int var18 = 0; var18 < 20; ++var18) {
               double var19 = (double)pos.getX() + 0.5 + ((double)this.world.random.nextFloat() - 0.5) * 2.0;
               double var21 = (double)pos.getY() + 0.5 + ((double)this.world.random.nextFloat() - 0.5) * 2.0;
               double var23 = (double)pos.getZ() + 0.5 + ((double)this.world.random.nextFloat() - 0.5) * 2.0;
               this.world.addParticle(ParticleType.SMOKE_NORMAL, var19, var21, var23, 0.0, 0.0, 0.0, new int[0]);
               this.world.addParticle(ParticleType.FLAME, var19, var21, var23, 0.0, 0.0, 0.0, new int[0]);
            }
            break;
         case 2005:
            DyeItem.spawnParticles(this.world, pos, data);
      }
   }

   @Override
   public void updateBlockMiningProgress(int id, BlockPos pos, int progress) {
      Map var4 = this.miningSites;
      Block var5 = this.world.getBlockState(pos).getBlock();
      if (var5 instanceof ChestBlock || var5 instanceof EnderChestBlock || var5 instanceof SignBlock || var5 instanceof SkullBlock) {
         var4 = this.miningProgress;
      }

      if (progress >= 0 && progress < 10) {
         BlockMiningProgress var6 = (BlockMiningProgress)var4.get(id);
         if (var6 == null || var6.getPos().getX() != pos.getX() || var6.getPos().getY() != pos.getY() || var6.getPos().getZ() != pos.getZ()) {
            var6 = new BlockMiningProgress(id, pos);
            var4.put(id, var6);
         }

         var6.setProgress(progress);
         var6.setLastUpdateTick(this.ticks);
      } else {
         var4.remove(id);
      }
   }

   public void m_89tdlskcc(Collection collection, Collection collection2) {
      synchronized(this.f_63ozqinqe) {
         this.f_63ozqinqe.removeAll(collection);
         this.f_63ozqinqe.addAll(collection2);
      }
   }

   public void m_06jwhpvvs() {
      this.viewChanged = true;
   }

   @Environment(EnvType.CLIENT)
   class C_10cjdgthg {
      final ChunkBlockRenderer f_27rxgdmlv;
      final Direction f_73glpdxlf;
      final Set f_92ncsbxsg = EnumSet.noneOf(Direction.class);
      final int f_30grnaxtk;

      private C_10cjdgthg(ChunkBlockRenderer c_20vbkqxvz, Direction c_69garkogr, int i) {
         this.f_27rxgdmlv = c_20vbkqxvz;
         this.f_73glpdxlf = c_69garkogr;
         this.f_30grnaxtk = i;
      }
   }
}
