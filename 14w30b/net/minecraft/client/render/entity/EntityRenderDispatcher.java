package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import java.util.Map;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.entity.living.player.ClientPlayerEntity;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.Culler;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.entity.ChickenModel;
import net.minecraft.client.render.model.entity.CowModel;
import net.minecraft.client.render.model.entity.EnderDragonRenderer;
import net.minecraft.client.render.model.entity.EndermanRenderer;
import net.minecraft.client.render.model.entity.HorseModel;
import net.minecraft.client.render.model.entity.OcelotModel;
import net.minecraft.client.render.model.entity.PigModel;
import net.minecraft.client.render.model.entity.RabbitModel;
import net.minecraft.client.render.model.entity.SheepModel;
import net.minecraft.client.render.model.entity.SlimeModel;
import net.minecraft.client.render.model.entity.SquidModel;
import net.minecraft.client.render.model.entity.WolfModel;
import net.minecraft.client.render.model.entity.ZombieModel;
import net.minecraft.client.render.world.WorldRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.EnderCrystalEntity;
import net.minecraft.entity.EnderEyeEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.FireworksEntity;
import net.minecraft.entity.FishingBobberEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.PrimedTntEntity;
import net.minecraft.entity.XpOrbEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.LeadKnotEntity;
import net.minecraft.entity.decoration.PaintingEntity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.mob.GhastEntity;
import net.minecraft.entity.living.mob.IronGolemEntity;
import net.minecraft.entity.living.mob.MagmaCubeEntity;
import net.minecraft.entity.living.mob.SlimeEntity;
import net.minecraft.entity.living.mob.SnowGolemEntity;
import net.minecraft.entity.living.mob.ambient.BatEntity;
import net.minecraft.entity.living.mob.hostile.BlazeEntity;
import net.minecraft.entity.living.mob.hostile.CaveSpiderEntity;
import net.minecraft.entity.living.mob.hostile.CreeperEntity;
import net.minecraft.entity.living.mob.hostile.EndermanEntity;
import net.minecraft.entity.living.mob.hostile.EndermiteEntity;
import net.minecraft.entity.living.mob.hostile.GiantEntity;
import net.minecraft.entity.living.mob.hostile.GuardianEntity;
import net.minecraft.entity.living.mob.hostile.SkeletonEntity;
import net.minecraft.entity.living.mob.hostile.SliverfishEntity;
import net.minecraft.entity.living.mob.hostile.SpiderEntity;
import net.minecraft.entity.living.mob.hostile.WitchEntity;
import net.minecraft.entity.living.mob.hostile.ZombieEntity;
import net.minecraft.entity.living.mob.hostile.ZombiePigmanEntity;
import net.minecraft.entity.living.mob.hostile.boss.EnderDragonEntity;
import net.minecraft.entity.living.mob.hostile.boss.WitherEntity;
import net.minecraft.entity.living.mob.passive.VillagerEntity;
import net.minecraft.entity.living.mob.passive.animal.ChickenEntity;
import net.minecraft.entity.living.mob.passive.animal.CowEntity;
import net.minecraft.entity.living.mob.passive.animal.HorseBaseEntity;
import net.minecraft.entity.living.mob.passive.animal.MooshroomEntity;
import net.minecraft.entity.living.mob.passive.animal.PigEntity;
import net.minecraft.entity.living.mob.passive.animal.RabbitEntity;
import net.minecraft.entity.living.mob.passive.animal.SheepEntity;
import net.minecraft.entity.living.mob.passive.animal.tamable.OcelotEntity;
import net.minecraft.entity.living.mob.passive.animal.tamable.WolfEntity;
import net.minecraft.entity.living.mob.water.SquidEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.entity.thrown.EggEntity;
import net.minecraft.entity.thrown.EnderPearlEntity;
import net.minecraft.entity.thrown.ExperienceBottleEntity;
import net.minecraft.entity.thrown.PotionEntity;
import net.minecraft.entity.thrown.SnowballEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.entity.vehicle.SpawnerMinecartEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.entity.weather.LightningBoltEntity;
import net.minecraft.item.Items;
import net.minecraft.util.crash.CashReportCategory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class EntityRenderDispatcher {
   private Map renderers = Maps.newHashMap();
   private Map f_45oahpuqr = Maps.newHashMap();
   private PlayerRenderer f_93ozaravr;
   private TextRenderer textRenderer;
   private double _cameraX;
   private double _cameraY;
   private double _cameraZ;
   public TextureManager textureManager;
   public World world;
   public Entity camera;
   public Entity targetEntity;
   public float cameraYaw;
   public float cameraPitch;
   public GameOptions options;
   public double cameraX;
   public double cameraY;
   public double cameraZ;
   private boolean solidRender = false;
   private boolean f_90obmzpfd = true;
   private boolean renderHitboxes = false;

   public EntityRenderDispatcher(TextureManager textureManager, ItemRenderer itemRenderer) {
      this.textureManager = textureManager;
      this.renderers.put(CaveSpiderEntity.class, new CaveSpiderRenderer(this));
      this.renderers.put(SpiderEntity.class, new SpiderRenderer(this));
      this.renderers.put(PigEntity.class, new PigRenderer(this, new PigModel(), 0.7F));
      this.renderers.put(SheepEntity.class, new SheepRenderer(this, new SheepModel(), 0.7F));
      this.renderers.put(CowEntity.class, new CowRenderer(this, new CowModel(), 0.7F));
      this.renderers.put(MooshroomEntity.class, new MooshroomRenderer(this, new CowModel(), 0.7F));
      this.renderers.put(WolfEntity.class, new WolfRenderer(this, new WolfModel(), 0.5F));
      this.renderers.put(ChickenEntity.class, new ChickenRenderer(this, new ChickenModel(), 0.3F));
      this.renderers.put(OcelotEntity.class, new CatRenderer(this, new OcelotModel(), 0.4F));
      this.renderers.put(RabbitEntity.class, new RabbitRenderer(this, new RabbitModel(), 0.3F));
      this.renderers.put(SliverfishEntity.class, new SilverfishRenderer(this));
      this.renderers.put(EndermiteEntity.class, new EndermiteRenderer(this));
      this.renderers.put(CreeperEntity.class, new CreeperRenderer(this));
      this.renderers.put(EndermanEntity.class, new EndermanRenderer(this));
      this.renderers.put(SnowGolemEntity.class, new SnowGolemRenderer(this));
      this.renderers.put(SkeletonEntity.class, new SkeletonRenderer(this));
      this.renderers.put(WitchEntity.class, new WitchRenderer(this));
      this.renderers.put(BlazeEntity.class, new BlazeRenderer(this));
      this.renderers.put(ZombiePigmanEntity.class, new ZombiePigmanRenderer(this));
      this.renderers.put(ZombieEntity.class, new ZombieBaseRenderer(this));
      this.renderers.put(SlimeEntity.class, new SlimeRenderer(this, new SlimeModel(16), 0.25F));
      this.renderers.put(MagmaCubeEntity.class, new MagmaCubeRenderer(this));
      this.renderers.put(GiantEntity.class, new GiantRenderer(this, new ZombieModel(), 0.5F, 6.0F));
      this.renderers.put(GhastEntity.class, new GhastRenderer(this));
      this.renderers.put(SquidEntity.class, new SquidRenderer(this, new SquidModel(), 0.7F));
      this.renderers.put(VillagerEntity.class, new VillagerRenderer(this));
      this.renderers.put(IronGolemEntity.class, new IronGolemRenderer(this));
      this.renderers.put(BatEntity.class, new BatRenderer(this));
      this.renderers.put(GuardianEntity.class, new GuardianRenderer(this));
      this.renderers.put(EnderDragonEntity.class, new EnderDragonRenderer(this));
      this.renderers.put(EnderCrystalEntity.class, new EnderCrystalRenderer(this));
      this.renderers.put(WitherEntity.class, new WitherRenderer(this));
      this.renderers.put(Entity.class, new AreaEffectCloudRenderer(this));
      this.renderers.put(PaintingEntity.class, new PaintingRenderer(this));
      this.renderers.put(ItemFrameEntity.class, new ItemFrameRenderer(this, itemRenderer));
      this.renderers.put(LeadKnotEntity.class, new LeadKnotRenderer(this));
      this.renderers.put(ArrowEntity.class, new ArrowRenderer(this));
      this.renderers.put(SnowballEntity.class, new ProjectileRenderer(this, Items.SNOWBALL, itemRenderer));
      this.renderers.put(EnderPearlEntity.class, new ProjectileRenderer(this, Items.ENDER_PEARL, itemRenderer));
      this.renderers.put(EnderEyeEntity.class, new ProjectileRenderer(this, Items.ENDER_EYE, itemRenderer));
      this.renderers.put(EggEntity.class, new ProjectileRenderer(this, Items.EGG, itemRenderer));
      this.renderers.put(PotionEntity.class, new ProjectileRenderer(this, Items.POTION, 16384, itemRenderer));
      this.renderers.put(ExperienceBottleEntity.class, new ProjectileRenderer(this, Items.EXPERIENCE_BOTTLE, itemRenderer));
      this.renderers.put(FireworksEntity.class, new ProjectileRenderer(this, Items.FIREWORKS, itemRenderer));
      this.renderers.put(FireballEntity.class, new FireballRenderer(this, 2.0F));
      this.renderers.put(SmallFireballEntity.class, new FireballRenderer(this, 0.5F));
      this.renderers.put(WitherSkullEntity.class, new WitherSkullRenderer(this));
      this.renderers.put(ItemEntity.class, new ItemEntityRenderer(this, itemRenderer));
      this.renderers.put(XpOrbEntity.class, new ExperienceOrbRenderer(this));
      this.renderers.put(PrimedTntEntity.class, new TntRenderer(this));
      this.renderers.put(FallingBlockEntity.class, new FallingBlockRenderer(this));
      this.renderers.put(TntMinecartEntity.class, new TntMinecartRenderer(this));
      this.renderers.put(SpawnerMinecartEntity.class, new SpawnerMinecartRenderer(this));
      this.renderers.put(MinecartEntity.class, new MinecartRenderer(this));
      this.renderers.put(BoatEntity.class, new BoatRenderer(this));
      this.renderers.put(FishingBobberEntity.class, new FishingBobberRenderer(this));
      this.renderers.put(HorseBaseEntity.class, new DonkeyRenderer(this, new HorseModel(), 0.75F));
      this.renderers.put(LightningBoltEntity.class, new LightningBoltRenderer(this));
      this.f_93ozaravr = new PlayerRenderer(this);
      this.f_45oahpuqr.put("default", this.f_93ozaravr);
      this.f_45oahpuqr.put("slim", new PlayerRenderer(this, true));
   }

   public void setCameraPos(double x, double y, double z) {
      this._cameraX = x;
      this._cameraY = y;
      this._cameraZ = z;
   }

   public EntityRenderer getRenderer(Class type) {
      EntityRenderer var2 = (EntityRenderer)this.renderers.get(type);
      if (var2 == null && type != Entity.class) {
         var2 = this.getRenderer(type.getSuperclass());
         this.renderers.put(type, var2);
      }

      return var2;
   }

   public EntityRenderer getRenderer(Entity entity) {
      if (entity instanceof ClientPlayerEntity) {
         String var2 = ((ClientPlayerEntity)entity).getModelType();
         PlayerRenderer var3 = (PlayerRenderer)this.f_45oahpuqr.get(var2);
         return var3 != null ? var3 : this.f_93ozaravr;
      } else {
         return this.getRenderer(entity.getClass());
      }
   }

   public void prepare(World world, TextRenderer textureManager, Entity font, Entity camera, GameOptions targetEntity, float options) {
      this.world = world;
      this.options = targetEntity;
      this.camera = font;
      this.targetEntity = camera;
      this.textRenderer = textureManager;
      if (font instanceof LivingEntity && ((LivingEntity)font).isSleeping()) {
         BlockState var7 = world.getBlockState(new BlockPos(font));
         Block var8 = var7.getBlock();
         if (var8 == Blocks.BED) {
            int var9 = ((Direction)var7.get(BedBlock.FACING)).getIdHorizontal();
            this.cameraYaw = (float)(var9 * 90 + 180);
            this.cameraPitch = 0.0F;
         }
      } else {
         this.cameraYaw = font.prevYaw + (font.yaw - font.prevYaw) * options;
         this.cameraPitch = font.prevPitch + (font.pitch - font.prevPitch) * options;
      }

      if (targetEntity.perspective == 2) {
         this.cameraYaw += 180.0F;
      }

      this.cameraX = font.prevTickX + (font.x - font.prevTickX) * (double)options;
      this.cameraY = font.prevTickY + (font.y - font.prevTickY) * (double)options;
      this.cameraZ = font.prevTickZ + (font.z - font.prevTickZ) * (double)options;
   }

   public void m_59erzayop(float f) {
      this.cameraYaw = f;
   }

   public boolean m_42egfdxeq() {
      return this.f_90obmzpfd;
   }

   public void m_01bqsgyjd(boolean bl) {
      this.f_90obmzpfd = bl;
   }

   public void setRenderHitboxes(boolean renderHitboxes) {
      this.renderHitboxes = renderHitboxes;
   }

   public boolean shouldRenderHitboxes() {
      return this.renderHitboxes;
   }

   public boolean render(Entity entity, float tickDelta) {
      return this.render(entity, tickDelta, false);
   }

   public boolean shouldRender(Entity entity, Culler view, double cameraX, double cameraY, double cameraZ) {
      EntityRenderer var9 = this.getRenderer(entity);
      return var9 != null && var9.shouldRender(entity, view, cameraX, cameraY, cameraZ);
   }

   public boolean render(Entity entity, float tickDelta, boolean skipHitbox) {
      if (entity.time == 0) {
         entity.prevTickX = entity.x;
         entity.prevTickY = entity.y;
         entity.prevTickZ = entity.z;
      }

      double var4 = entity.prevTickX + (entity.x - entity.prevTickX) * (double)tickDelta;
      double var6 = entity.prevTickY + (entity.y - entity.prevTickY) * (double)tickDelta;
      double var8 = entity.prevTickZ + (entity.z - entity.prevTickZ) * (double)tickDelta;
      float var10 = entity.prevYaw + (entity.yaw - entity.prevYaw) * tickDelta;
      int var11 = entity.getLightLevel(tickDelta);
      if (entity.isOnFire()) {
         var11 = 15728880;
      }

      int var12 = var11 % 65536;
      int var13 = var11 / 65536;
      GLX.multiTexCoord2f(GLX.GL_TEXTURE1, (float)var12 / 1.0F, (float)var13 / 1.0F);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      return this.render(entity, var4 - this._cameraX, var6 - this._cameraY, var8 - this._cameraZ, var10, tickDelta, skipHitbox);
   }

   public void m_01kmjxpcp(Entity c_47ldwddrb, float f) {
      double var3 = c_47ldwddrb.prevTickX + (c_47ldwddrb.x - c_47ldwddrb.prevTickX) * (double)f;
      double var5 = c_47ldwddrb.prevTickY + (c_47ldwddrb.y - c_47ldwddrb.prevTickY) * (double)f;
      double var7 = c_47ldwddrb.prevTickZ + (c_47ldwddrb.z - c_47ldwddrb.prevTickZ) * (double)f;
      EntityRenderer var9 = this.getRenderer(c_47ldwddrb);
      if (var9 != null && this.textureManager != null) {
         int var10 = c_47ldwddrb.getLightLevel(f);
         int var11 = var10 % 65536;
         int var12 = var10 / 65536;
         GLX.multiTexCoord2f(GLX.GL_TEXTURE1, (float)var11 / 1.0F, (float)var12 / 1.0F);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         var9.renderNameTag(c_47ldwddrb, var3 - this._cameraX, var5 - this._cameraY, var7 - this._cameraZ);
      }
   }

   public boolean render(Entity entity, double dx, double dy, double dz, float yaw, float tickDelta) {
      return this.render(entity, dx, dy, dz, yaw, tickDelta, false);
   }

   public boolean render(Entity entity, double dx, double dy, double dz, float yaw, float tickDelta, boolean skipHitbox) {
      EntityRenderer var11 = null;

      try {
         var11 = this.getRenderer(entity);
         if (var11 != null && this.textureManager != null) {
            try {
               if (var11 instanceof LivingEntityRenderer) {
                  ((LivingEntityRenderer)var11).setSolidRender(this.solidRender);
               }

               var11.render(entity, dx, dy, dz, yaw, tickDelta);
            } catch (Throwable var18) {
               throw new CrashException(CrashReport.of(var18, "Rendering entity in world"));
            }

            try {
               if (!this.solidRender) {
                  var11.postRender(entity, dx, dy, dz, yaw, tickDelta);
               }
            } catch (Throwable var17) {
               throw new CrashException(CrashReport.of(var17, "Post-rendering entity in world"));
            }

            if (this.renderHitboxes && !entity.isInvisible() && !skipHitbox) {
               try {
                  this.renderHitbox(entity, dx, dy, dz, yaw, tickDelta);
               } catch (Throwable var16) {
                  throw new CrashException(CrashReport.of(var16, "Rendering entity hitbox in world"));
               }
            }
         } else if (this.textureManager != null) {
            return false;
         }

         return true;
      } catch (Throwable var19) {
         CrashReport var13 = CrashReport.of(var19, "Rendering entity in world");
         CashReportCategory var14 = var13.addCategory("Entity being rendered");
         entity.populateCrashReport(var14);
         CashReportCategory var15 = var13.addCategory("Renderer details");
         var15.add("Assigned renderer", var11);
         var15.add("Location", CashReportCategory.formatPosition(dx, dy, dz));
         var15.add("Rotation", yaw);
         var15.add("Delta", tickDelta);
         throw new CrashException(var13);
      }
   }

   private void renderHitbox(Entity entity, double dx, double dy, double dz, float g, float h) {
      GlStateManager.depthMask(false);
      GlStateManager.disableTexture();
      GlStateManager.disableLighting();
      GlStateManager.disableCull();
      GlStateManager.enableBlend();
      float var10 = entity.width / 2.0F;
      Box var11 = entity.getBoundingBox();
      Box var12 = new Box(
         var11.minX - entity.x + dx,
         var11.minY - entity.y + dy,
         var11.minZ - entity.z + dz,
         var11.maxX - entity.x + dx,
         var11.maxY - entity.y + dy,
         var11.maxZ - entity.z + dz
      );
      WorldRenderer.renderHitbox(var12, 16777215);
      if (entity instanceof LivingEntity) {
         float var13 = 0.01F;
         WorldRenderer.renderHitbox(
            new Box(
               dx - (double)var10,
               dy + (double)entity.getEyeHeight() - 0.01F,
               dz - (double)var10,
               dx + (double)var10,
               dy + (double)entity.getEyeHeight() + 0.01F,
               dz + (double)var10
            ),
            16711680
         );
      }

      Tessellator var16 = Tessellator.getInstance();
      BufferBuilder var14 = var16.getBufferBuilder();
      Vec3d var15 = entity.m_01qqqsfds(h);
      var14.start(3);
      var14.color(255);
      var14.vertex(dx, dy + (double)entity.getEyeHeight(), dz);
      var14.vertex(dx + var15.x * 2.0, dy + (double)entity.getEyeHeight() + var15.y * 2.0, dz + var15.z * 2.0);
      var16.end();
      GlStateManager.enableTexture();
      GlStateManager.enableLighting();
      GlStateManager.enableCull();
      GlStateManager.enableBlend();
      GlStateManager.depthMask(true);
   }

   public void setWorld(World world) {
      this.world = world;
   }

   public double getSquaredDistanceToCamera(double x, double y, double f) {
      double var7 = x - this.cameraX;
      double var9 = y - this.cameraY;
      double var11 = f - this.cameraZ;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   public TextRenderer getTextRenderer() {
      return this.textRenderer;
   }

   public void setSolidRender(boolean solidRender) {
      this.solidRender = solidRender;
   }
}
