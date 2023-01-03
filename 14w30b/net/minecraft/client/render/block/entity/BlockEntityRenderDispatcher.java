package net.minecraft.client.render.block.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.MovingBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.crash.CashReportCategory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class BlockEntityRenderDispatcher {
   private Map renderers = Maps.newHashMap();
   public static BlockEntityRenderDispatcher INSTANCE = new BlockEntityRenderDispatcher();
   private TextRenderer textRenderer;
   public static double offsetX;
   public static double offsetY;
   public static double offsetZ;
   public TextureManager textureManager;
   public World world;
   public Entity camera;
   public float cameraYaw;
   public float cameraPitch;
   public double cameraX;
   public double cameraY;
   public double cameraZ;

   private BlockEntityRenderDispatcher() {
      this.renderers.put(SignBlockEntity.class, new SignRenderer());
      this.renderers.put(MobSpawnerBlockEntity.class, new MobSpawnerRenderer());
      this.renderers.put(MovingBlockEntity.class, new MovingBlockRenderer());
      this.renderers.put(ChestBlockEntity.class, new ChestRenderer());
      this.renderers.put(EnderChestBlockEntity.class, new EnderChestRenderer());
      this.renderers.put(EnchantingTableBlockEntity.class, new EnchantingTableRenderer());
      this.renderers.put(EndPortalBlockEntity.class, new EndPortalRenderer());
      this.renderers.put(BeaconBlockEntity.class, new BeaconRenderer());
      this.renderers.put(SkullBlockEntity.class, new SkullRenderer());
      this.renderers.put(BannerBlockEntity.class, new BannerRenderer());

      for(BlockEntityRenderer var2 : this.renderers.values()) {
         var2.init(this);
      }
   }

   public BlockEntityRenderer getRenderer(Class type) {
      BlockEntityRenderer var2 = (BlockEntityRenderer)this.renderers.get(type);
      if (var2 == null && type != BlockEntity.class) {
         var2 = this.getRenderer(type.getSuperclass());
         this.renderers.put(type, var2);
      }

      return var2;
   }

   public boolean hasRenderer(BlockEntity blockEntity) {
      return this.getRenderer(blockEntity) != null;
   }

   public BlockEntityRenderer getRenderer(BlockEntity blockEntity) {
      return blockEntity == null ? null : this.getRenderer(blockEntity.getClass());
   }

   public void prepare(World world, TextureManager textureManager, TextRenderer textRenderer, Entity camera, float tickDelta) {
      if (this.world != world) {
         this.setWorld(world);
      }

      this.textureManager = textureManager;
      this.camera = camera;
      this.textRenderer = textRenderer;
      this.cameraYaw = camera.prevYaw + (camera.yaw - camera.prevYaw) * tickDelta;
      this.cameraPitch = camera.prevPitch + (camera.pitch - camera.prevPitch) * tickDelta;
      this.cameraX = camera.prevTickX + (camera.x - camera.prevTickX) * (double)tickDelta;
      this.cameraY = camera.prevTickY + (camera.y - camera.prevTickY) * (double)tickDelta;
      this.cameraZ = camera.prevTickZ + (camera.z - camera.prevTickZ) * (double)tickDelta;
   }

   public void render(BlockEntity blockEntity, float tickDelta, int blockMiningProgress) {
      if (blockEntity.squaredDistanceTo(this.cameraX, this.cameraY, this.cameraZ) < blockEntity.getSquaredViewDistance()) {
         int var4 = this.world.getLightColor(blockEntity.getPos(), 0);
         int var5 = var4 % 65536;
         int var6 = var4 / 65536;
         GLX.multiTexCoord2f(GLX.GL_TEXTURE1, (float)var5 / 1.0F, (float)var6 / 1.0F);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         BlockPos var7 = blockEntity.getPos();
         this.render(blockEntity, (double)var7.getX() - offsetX, (double)var7.getY() - offsetY, (double)var7.getZ() - offsetZ, tickDelta, blockMiningProgress);
      }
   }

   public void render(BlockEntity blockEntity, double x, double y, double z, float tickDelta) {
      this.render(blockEntity, x, y, z, tickDelta, -1);
   }

   public void render(BlockEntity blockEntity, double x, double y, double z, float tickDelta, int blockMiningProgress) {
      BlockEntityRenderer var10 = this.getRenderer(blockEntity);
      if (var10 != null) {
         try {
            var10.render(blockEntity, x, y, z, tickDelta, blockMiningProgress);
         } catch (Throwable var14) {
            CrashReport var12 = CrashReport.of(var14, "Rendering Block Entity");
            CashReportCategory var13 = var12.addCategory("Block Entity Details");
            blockEntity.populateCrashReport(var13);
            throw new CrashException(var12);
         }
      }
   }

   public void setWorld(World world) {
      this.world = world;

      for(BlockEntityRenderer var3 : this.renderers.values()) {
         if (var3 != null) {
            var3.setWorld(world);
         }
      }
   }

   public TextRenderer getTextRenderer() {
      return this.textRenderer;
   }
}
