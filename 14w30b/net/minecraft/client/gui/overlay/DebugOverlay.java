package net.minecraft.client.gui.overlay;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.property.Property;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.Window;
import net.minecraft.entity.Entity;
import net.minecraft.text.Formatting;
import net.minecraft.util.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.WorldGeneratorType;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public class DebugOverlay extends GuiElement {
   private final MinecraftClient client;
   private final TextRenderer textRenderer;

   public DebugOverlay(MinecraftClient client) {
      this.client = client;
      this.textRenderer = client.textRenderer;
   }

   public void render(Window window) {
      this.client.profiler.push("debug");
      GlStateManager.pushMatrix();
      this.drawGameInfo();
      this.drawSystemInfo(window);
      GlStateManager.popMatrix();
      this.client.profiler.pop();
   }

   private boolean showReducedInfo() {
      return this.client.player.hasReducedDebugInfo() || this.client.options.reducedDebugInfo;
   }

   protected void drawGameInfo() {
      List var1 = this.getGameInfo();

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         String var3 = (String)var1.get(var2);
         if (!Strings.isNullOrEmpty(var3)) {
            int var4 = this.textRenderer.fontHeight;
            int var5 = this.textRenderer.getStringWidth(var3);
            boolean var6 = true;
            int var7 = 2 + var4 * var2;
            fill(1, var7 - 1, 2 + var5 + 1, var7 + var4 - 1, -1873784752);
            this.textRenderer.drawWithoutShadow(var3, 2, var7, 14737632);
         }
      }
   }

   protected void drawSystemInfo(Window window) {
      List var2 = this.getSystemInfo();

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         String var4 = (String)var2.get(var3);
         if (!Strings.isNullOrEmpty(var4)) {
            int var5 = this.textRenderer.fontHeight;
            int var6 = this.textRenderer.getStringWidth(var4);
            int var7 = window.getWidth() - 2 - var6;
            int var8 = 2 + var5 * var3;
            fill(var7 - 1, var8 - 1, var7 + var6 + 1, var8 + var5 - 1, -1873784752);
            this.textRenderer.drawWithoutShadow(var4, var7, var8, 14737632);
         }
      }
   }

   protected List getGameInfo() {
      BlockPos var1 = new BlockPos(this.client.getCamera().x, this.client.getCamera().getBoundingBox().minY, this.client.getCamera().z);
      if (this.showReducedInfo()) {
         return Lists.newArrayList(
            new String[]{
               "Minecraft 14w30c - " + this.client.fpsDebugString,
               this.client.worldRenderer.getChunkDebugInfo(),
               "P: " + this.client.particleManager.getParticlesDebugInfo() + ". T: " + this.client.world.getEntitiesDebugInfo(),
               this.client.world.getChunkSourceDebugInfo(),
               "",
               String.format("Chunk-relative: %d %d %d", var1.getX() & 15, var1.getY() & 15, var1.getZ() & 15)
            }
         );
      } else {
         Entity var2 = this.client.getCamera();
         Direction var3 = var2.getDirection();
         String var4 = "Invalid";
         switch(var3) {
            case NORTH:
               var4 = "Towards negative Z";
               break;
            case SOUTH:
               var4 = "Towards positive Z";
               break;
            case WEST:
               var4 = "Towards negative X";
               break;
            case EAST:
               var4 = "Towards positive X";
         }

         ArrayList var5 = Lists.newArrayList(
            new String[]{
               "Minecraft 14w30c (" + this.client.getGameVersion() + "/" + ClientBrandRetriever.getClientModName() + ")",
               this.client.fpsDebugString,
               this.client.worldRenderer.getChunkDebugInfo(),
               this.client.worldRenderer.getEntityDebugInfo(),
               "P: " + this.client.particleManager.getParticlesDebugInfo() + ". T: " + this.client.world.getEntitiesDebugInfo(),
               this.client.world.getChunkSourceDebugInfo(),
               "",
               String.format("XYZ: %.3f / %.5f / %.3f", this.client.getCamera().x, this.client.getCamera().getBoundingBox().minY, this.client.getCamera().z),
               String.format("Block: %d %d %d", var1.getX(), var1.getY(), var1.getZ()),
               String.format(
                  "Chunk: %d %d %d in %d %d %d", var1.getX() & 15, var1.getY() & 15, var1.getZ() & 15, var1.getX() >> 4, var1.getY() >> 4, var1.getZ() >> 4
               ),
               String.format("Facing: %s (%s) (%.1f / %.1f)", var3, var4, MathHelper.wrapDegrees(var2.yaw), MathHelper.wrapDegrees(var2.pitch))
            }
         );
         if (this.client.world != null && this.client.world.isLoaded(var1)) {
            WorldChunk var6 = this.client.world.getChunk(var1);
            var5.add("Biome: " + var6.getBiome(var1, this.client.world.getBiomeSource()).name);
            var5.add(
               "Light: " + var6.getLight(var1, 0) + " (" + var6.getLight(LightType.SKY, var1) + " sky, " + var6.getLight(LightType.BLOCK, var1) + " block)"
            );
            var5.add(
               String.format("Local Difficulty: %.2f (Day %d)", this.client.world.getLocalDifficulty(var1).get(), this.client.world.getTimeOfDay() / 24000L)
            );
         }

         if (this.client.gameRenderer != null && this.client.gameRenderer.hasShader()) {
            var5.add("Shader: " + this.client.gameRenderer.getShader().getName());
         }

         if (this.client.crosshairTarget != null
            && this.client.crosshairTarget.type == HitResult.Type.BLOCK
            && this.client.crosshairTarget.getBlockPos() != null) {
            BlockPos var7 = this.client.crosshairTarget.getBlockPos();
            var5.add(String.format("Looking at: %d %d %d", var7.getX(), var7.getY(), var7.getZ()));
         }

         return var5;
      }
   }

   protected List getSystemInfo() {
      long var1 = Runtime.getRuntime().maxMemory();
      long var3 = Runtime.getRuntime().totalMemory();
      long var5 = Runtime.getRuntime().freeMemory();
      long var7 = var3 - var5;
      ArrayList var9 = Lists.newArrayList(
         new String[]{
            String.format("Java: %s %dbit", System.getProperty("java.version"), this.client.is64Bit() ? 64 : 32),
            String.format("Mem: % 2d%% %03d/%03dMB", var7 * 100L / var1, convertBytesToMegaBytes(var7), convertBytesToMegaBytes(var1)),
            String.format("Allocated: % 2d%% %03dMB", var3 * 100L / var1, convertBytesToMegaBytes(var3)),
            "",
            String.format("Display: %dx%d (%s)", Display.getWidth(), Display.getHeight(), GL11.glGetString(7936)),
            GL11.glGetString(7937),
            GL11.glGetString(7938)
         }
      );
      if (this.showReducedInfo()) {
         return var9;
      } else {
         if (this.client.crosshairTarget != null
            && this.client.crosshairTarget.type == HitResult.Type.BLOCK
            && this.client.crosshairTarget.getBlockPos() != null) {
            BlockPos var10 = this.client.crosshairTarget.getBlockPos();
            BlockState var11 = this.client.world.getBlockState(var10);
            if (this.client.world.getGeneratorType() != WorldGeneratorType.DEBUG_ALL_BLOCK_STATES) {
               var11 = var11.getBlock().updateShape(var11, this.client.world, var10);
            }

            var9.add("");
            var9.add(String.valueOf(Block.REGISTRY.getKey(var11.getBlock())));

            for(Entry var13 : var11.values().entrySet()) {
               String var14 = ((Comparable)var13.getValue()).toString();
               if (var13.getValue() == Boolean.TRUE) {
                  var14 = Formatting.GREEN + var14;
               } else if (var13.getValue() == Boolean.FALSE) {
                  var14 = Formatting.RED + var14;
               }

               var9.add(((Property)var13.getKey()).getName() + ": " + var14);
            }
         }

         return var9;
      }
   }

   private static long convertBytesToMegaBytes(long bytes) {
      return bytes / 1024L / 1024L;
   }
}
