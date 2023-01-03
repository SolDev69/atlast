package net.minecraft.client.render.block;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockState;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.manager.IResourceManager;
import net.minecraft.client.resource.manager.ResourceReloadListener;
import net.minecraft.client.resource.model.BakedModel;
import net.minecraft.client.resource.model.BasicBakedModel;
import net.minecraft.client.resource.model.WeightedBakedModel;
import net.minecraft.client.texture.TextureAtlasSprite;
import net.minecraft.util.crash.CashReportCategory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.WorldGeneratorType;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class BlockRenderDispatcher implements ResourceReloadListener {
   private BlockModelShaper modelShaper;
   private final GameOptions options;
   private final BlockModelRenderer modelRenderer = new BlockModelRenderer();
   private final BlockEntityRenderer blockEntityRenderer = new BlockEntityRenderer();
   private final LiquidRenderer liquidRenderer = new LiquidRenderer();

   public BlockRenderDispatcher(BlockModelShaper modelShaper, GameOptions options) {
      this.modelShaper = modelShaper;
      this.options = options;
   }

   public BlockModelShaper getModelShaper() {
      return this.modelShaper;
   }

   public void renderMining(BlockState state, BlockPos pos, TextureAtlasSprite miningTexture, IWorld world) {
      Block var5 = state.getBlock();
      int var6 = var5.getRenderType();
      if (var6 == 3) {
         state = var5.updateShape(state, world, pos);
         BakedModel var7 = this.modelShaper.getModel(state);
         BakedModel var8 = new BasicBakedModel.Builder(var7, miningTexture).build();
         this.modelRenderer.render(world, var8, state, pos, Tessellator.getInstance().getBufferBuilder());
      }
   }

   public boolean render(BlockState state, BlockPos pos, IWorld world, BufferBuilder bufferBuilder) {
      try {
         int var5 = state.getBlock().getRenderType();
         if (var5 == -1) {
            return false;
         } else {
            switch(var5) {
               case 1:
                  return this.liquidRenderer.render(world, state, pos, bufferBuilder);
               case 2:
                  return false;
               case 3:
                  BakedModel var9 = this.getModel(state, world, pos);
                  return this.modelRenderer.render(world, var9, state, pos, bufferBuilder);
               default:
                  return false;
            }
         }
      } catch (Throwable var8) {
         CrashReport var6 = CrashReport.of(var8, "Tesselating block in world");
         CashReportCategory var7 = var6.addCategory("Block being tesselated");
         CashReportCategory.addBlockDetails(var7, pos, state.getBlock(), state.getBlock().getMetadataFromState(state));
         throw new CrashException(var6);
      }
   }

   public BlockModelRenderer getModelRenderer() {
      return this.modelRenderer;
   }

   private BakedModel getModel(Block c_68zcrzyxg, int i, BlockPos c_76varpwca) {
      BakedModel var4 = this.modelShaper.getModel(c_68zcrzyxg.getStateFromMetadata(i));
      if (c_76varpwca != null && this.options.allowBlockAlternatives && var4 instanceof WeightedBakedModel) {
         var4 = ((WeightedBakedModel)var4).pick(MathHelper.hashCode(c_76varpwca));
      }

      return var4;
   }

   public BakedModel getModel(BlockState state, IWorld world, BlockPos pos) {
      Block var4 = state.getBlock();
      if (world.getGeneratorType() != WorldGeneratorType.DEBUG_ALL_BLOCK_STATES) {
         try {
            state = var4.updateShape(state, world, pos);
         } catch (Exception var6) {
         }
      }

      BakedModel var5 = this.modelShaper.getModel(state);
      if (pos != null && this.options.allowBlockAlternatives && var5 instanceof WeightedBakedModel) {
         var5 = ((WeightedBakedModel)var5).pick(MathHelper.hashCode(pos));
      }

      return var5;
   }

   public void renderDynamic(Block c_68zcrzyxg, int i, float f) {
      int var4 = c_68zcrzyxg.getRenderType();
      if (var4 != -1) {
         switch(var4) {
            case 1:
            default:
               break;
            case 2:
               this.blockEntityRenderer.render(c_68zcrzyxg, f);
               break;
            case 3:
               BakedModel var5 = this.getModel(c_68zcrzyxg, i, null);
               this.modelRenderer.render(var5, c_68zcrzyxg, i, f, true);
         }
      }
   }

   public boolean m_17eyzvzcz(Block c_68zcrzyxg, int i) {
      if (c_68zcrzyxg == null) {
         return false;
      } else {
         int var3 = c_68zcrzyxg.getRenderType();
         if (var3 == 3) {
            return false;
         } else {
            return var3 == 2;
         }
      }
   }

   @Override
   public void reload(IResourceManager resourceManager) {
      this.liquidRenderer.reload();
   }
}
