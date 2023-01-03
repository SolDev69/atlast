package net.minecraft.client.render.item;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tessellator;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirtBlock;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.InfestedBlock;
import net.minecraft.block.MushroomBlock;
import net.minecraft.block.PlanksBlock;
import net.minecraft.block.PrismarineBlock;
import net.minecraft.block.QuartzBlock;
import net.minecraft.block.SandBlock;
import net.minecraft.block.SandstoneBlock;
import net.minecraft.block.StoneBlock;
import net.minecraft.block.StoneSlabBlock;
import net.minecraft.block.StonebrickBlock;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.model.block.ModelTransformation;
import net.minecraft.client.render.model.block.ModelTransformations;
import net.minecraft.client.resource.ModelIdentifier;
import net.minecraft.client.resource.manager.IResourceManager;
import net.minecraft.client.resource.manager.ResourceReloadListener;
import net.minecraft.client.resource.model.BakedModel;
import net.minecraft.client.resource.model.BakedQuad;
import net.minecraft.client.resource.model.ItemModelProvider;
import net.minecraft.client.resource.model.ModelManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.FishItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.resource.Identifier;
import net.minecraft.util.crash.CashReportCategory;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

@Environment(EnvType.CLIENT)
public class ItemRenderer implements ResourceReloadListener {
   private static final Identifier ENCHANTMENT_GLINT_TEXTURE = new Identifier("textures/misc/enchanted_item_glint.png");
   private boolean useCustomDisplayColor = true;
   public float zOffset;
   private final ItemModelShaper modelShaper;
   private final TextureManager textureManager;
   public static float f_37fbuxbuw = 0.0F;
   public static float f_95amfbdix = 0.0F;
   public static float f_56lzlaoql = 0.0F;
   public static float f_08cbpgwxu = 0.0F;
   public static float f_57ilxsdcw = 0.0F;
   public static float f_03cueakzi = 0.0F;
   public static float f_44anpvset = 0.0F;
   public static float f_83odglsxg = 0.0F;
   public static float f_53wwgasdt = 0.0F;

   public ItemRenderer(TextureManager textureManager, ModelManager manager) {
      this.textureManager = textureManager;
      this.modelShaper = new ItemModelShaper(manager);
      this.registerGuiModels();
   }

   public void setUseCustomDisplayColor(boolean useCustomDisplayColor) {
      this.useCustomDisplayColor = useCustomDisplayColor;
   }

   public ItemModelShaper getModelShaper() {
      return this.modelShaper;
   }

   protected void registerModel(Item item, int metadata, String id) {
      this.modelShaper.register(item, metadata, new ModelIdentifier(id, "inventory"));
   }

   protected void registerModel(Block block, int metadata, String id) {
      this.registerModel(Item.byBlock(block), metadata, id);
   }

   private void registerModel(Block block, String id) {
      this.registerModel(block, 0, id);
   }

   private void registerModel(Item item, String id) {
      this.registerModel(item, 0, id);
   }

   private void m_55uhitevd(BakedModel c_51yvnkdmo, ItemStack c_72owraavl, float f, float g, float h) {
      for(Direction var9 : Direction.values()) {
         this.m_28xwugdkx(c_51yvnkdmo.getQuads(var9), c_72owraavl, f, g, h);
      }

      this.m_28xwugdkx(c_51yvnkdmo.getQuads(), c_72owraavl, f, g, h);
   }

   public void renderHeldItem(ItemStack stack, BakedModel model) {
      GlStateManager.pushMatrix();
      GlStateManager.scalef(0.5F, 0.5F, 0.5F);
      if (model.isCustomRenderer()) {
         GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableRescaleNormal();
         BlockEntityItemRenderer.INSTANCE.render(stack);
      } else {
         GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
         this.m_55uhitevd(model, stack, 1.0F, 1.0F, 1.0F);
         if (stack.hasEnchantmentGlint()) {
            this.renderEnchantmentGlint(stack, model);
         }
      }

      GlStateManager.popMatrix();
   }

   private void renderEnchantmentGlint(ItemStack c_72owraavl, BakedModel c_51yvnkdmo) {
      GlStateManager.depthMask(false);
      GlStateManager.depthFunc(514);
      GlStateManager.disableLighting();
      GlStateManager.blendFunc(768, 1);
      this.textureManager.bind(ENCHANTMENT_GLINT_TEXTURE);
      float var3 = 1.2F;
      float var4 = 0.6F;
      float var5 = 0.3F;
      float var6 = 0.96000004F;
      GlStateManager.matrixMode(5890);
      GlStateManager.pushMatrix();
      float var7 = 8.0F;
      GlStateManager.scalef(var7, var7, var7);
      float var8 = (float)(MinecraftClient.getTime() % 3000L) / 3000.0F / 8.0F;
      GlStateManager.translatef(var8, 0.0F, 0.0F);
      GlStateManager.rotatef(-50.0F, 0.0F, 0.0F, 1.0F);
      this.m_55uhitevd(c_51yvnkdmo, c_72owraavl, 0.6F, 0.3F, 0.96000004F);
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      GlStateManager.scalef(var7, var7, var7);
      var8 = (float)(MinecraftClient.getTime() % 4873L) / 4873.0F / 8.0F;
      GlStateManager.translatef(-var8, 0.0F, 0.0F);
      GlStateManager.rotatef(10.0F, 0.0F, 0.0F, 1.0F);
      this.m_55uhitevd(c_51yvnkdmo, c_72owraavl, 0.6F, 0.3F, 0.96000004F);
      GlStateManager.popMatrix();
      GlStateManager.matrixMode(5888);
      GlStateManager.blendFunc(770, 771);
      GlStateManager.enableLighting();
      GlStateManager.depthFunc(515);
      GlStateManager.depthMask(true);
      this.textureManager.bind(SpriteAtlasTexture.BLOCK_ATLAS_BLOCKS);
   }

   private void m_28xwugdkx(List list, ItemStack c_72owraavl, float f, float g, float h) {
      Tessellator var6 = Tessellator.getInstance();
      BufferBuilder var7 = var6.getBufferBuilder();
      if (this.useCustomDisplayColor) {
         GlStateManager.color4f(f, g, h, 1.0F);
      }

      for(BakedQuad var9 : list) {
         var7.start();
         var7.format(DefaultVertexFormat.BLOCK_NORMALS);
         var7.vertices(var9.getVertices());
         float var10 = 1.0F;
         float var11 = 1.0F;
         float var12 = 1.0F;
         if (var9.hasTint()) {
            int var13 = c_72owraavl.getItem().getDisplayColor(c_72owraavl, var9.getTintIndex());
            if (GameRenderer.anaglyphEnabled) {
               var13 = TextureUtil.getAnaglyphColor(var13);
            }

            var10 = (float)(var13 >> 16 & 0xFF) / 255.0F;
            var11 = (float)(var13 >> 8 & 0xFF) / 255.0F;
            var12 = (float)(var13 & 0xFF) / 255.0F;
         }

         var10 *= f;
         var11 *= g;
         var12 *= h;
         var7.setColor(var10, var11, var12, 1.0F, 4);
         var7.setColor(var10, var11, var12, 1.0F, 3);
         var7.setColor(var10, var11, var12, 1.0F, 2);
         var7.setColor(var10, var11, var12, 1.0F, 1);
         Vec3i var17 = var9.getFace().getNormal();
         var7.postNormal((float)var17.getX(), (float)var17.getY(), (float)var17.getZ());
         var6.end();
      }

      if (this.useCustomDisplayColor) {
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      }
   }

   public boolean isGui3d(ItemStack stack) {
      BakedModel var2 = this.modelShaper.getModel(stack);
      return var2 == null ? false : var2.isGui3d();
   }

   private void prepareHeldItemRender(ItemStack stack) {
      BakedModel var2 = this.modelShaper.getModel(stack);
      Item var3 = stack.getItem();
      if (var3 != null) {
         boolean var4 = var2.isGui3d();
         if (!var4) {
            GlStateManager.scalef(2.0F, 2.0F, 2.0F);
         }

         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      }
   }

   public void renderHeldItem(ItemStack c_72owraavl) {
      BakedModel var2 = this.modelShaper.getModel(c_72owraavl);
      this.renderHeldItem(c_72owraavl, var2, ModelTransformations.Type.NONE);
   }

   public void renderHeldItem(ItemStack stack, LivingEntity entity, ModelTransformations.Type transformationType) {
      BakedModel var4 = this.modelShaper.getModel(stack);
      if (entity instanceof PlayerEntity) {
         PlayerEntity var5 = (PlayerEntity)entity;
         Item var6 = stack.getItem();
         ModelIdentifier var7 = null;
         if (var6 == Items.FISHING_ROD && var5.fishingBobber != null) {
            var7 = new ModelIdentifier("fishing_rod_cast", "inventory");
         } else if (var6 == Items.BOW && var5.getItemInHand() != null) {
            int var8 = stack.getUseDuration() - var5.getItemUseTimer();
            if (var8 >= 18) {
               var7 = new ModelIdentifier("bow_pulling_2", "inventory");
            } else if (var8 > 13) {
               var7 = new ModelIdentifier("bow_pulling_1", "inventory");
            } else if (var8 > 0) {
               var7 = new ModelIdentifier("bow_pulling_0", "inventory");
            }
         }

         if (var7 != null) {
            var4 = this.modelShaper.getManager().getModel(var7);
         }
      }

      this.renderHeldItem(stack, var4, transformationType);
   }

   protected void m_30lwnnbxd(ModelTransformation c_85djrxdvl) {
      if (c_85djrxdvl != ModelTransformation.NONE) {
         GlStateManager.translatef(c_85djrxdvl.translation.x + f_37fbuxbuw, c_85djrxdvl.translation.y + f_95amfbdix, c_85djrxdvl.translation.z + f_56lzlaoql);
         GlStateManager.rotatef(c_85djrxdvl.rotation.y + f_57ilxsdcw, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(c_85djrxdvl.rotation.x + f_08cbpgwxu, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(c_85djrxdvl.rotation.z + f_03cueakzi, 0.0F, 0.0F, 1.0F);
         GlStateManager.scalef(c_85djrxdvl.scale.x + f_44anpvset, c_85djrxdvl.scale.y + f_83odglsxg, c_85djrxdvl.scale.z + f_53wwgasdt);
      }
   }

   protected void renderHeldItem(ItemStack stack, BakedModel model, ModelTransformations.Type transformationType) {
      this.textureManager.bind(SpriteAtlasTexture.BLOCK_ATLAS_BLOCKS);
      this.textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_BLOCKS).m_60hztdglb(false, false);
      this.prepareHeldItemRender(stack);
      GlStateManager.enableRescaleNormal();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.disableBlend();
      GlStateManager.blendFuncSeparate(770, 771, 1, 0);
      GlStateManager.pushMatrix();
      switch(transformationType) {
         case NONE:
         default:
            break;
         case THIRD_PERSON:
            this.m_30lwnnbxd(model.getTransformations().thirdPerson);
            break;
         case FIRST_PERSON:
            this.m_30lwnnbxd(model.getTransformations().firstPerson);
            break;
         case HEAD:
            this.m_30lwnnbxd(model.getTransformations().head);
            break;
         case GUI:
            this.m_30lwnnbxd(model.getTransformations().gui);
      }

      this.renderHeldItem(stack, model);
      GlStateManager.popMatrix();
      GlStateManager.disableRescaleNormal();
      GlStateManager.enableBlend();
      this.textureManager.bind(SpriteAtlasTexture.BLOCK_ATLAS_BLOCKS);
      this.textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_BLOCKS).m_42jngdvts();
   }

   public void renderGuiItemModel(ItemStack stack, int x, int y) {
      BakedModel var4 = this.modelShaper.getModel(stack);
      GlStateManager.pushMatrix();
      this.textureManager.bind(SpriteAtlasTexture.BLOCK_ATLAS_BLOCKS);
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableAlphaTest();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.disableBlend();
      GlStateManager.blendFunc(770, 771);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.prepareGuiItemRender(x, y, var4.isGui3d());
      this.m_30lwnnbxd(var4.getTransformations().gui);
      this.renderHeldItem(stack, var4);
      GlStateManager.disableAlphaTest();
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableLighting();
      GlStateManager.popMatrix();
   }

   private void prepareGuiItemRender(int x, int y, boolean gui3d) {
      GlStateManager.translatef((float)x, (float)y, 100.0F + this.zOffset);
      GlStateManager.translatef(8.0F, 8.0F, 0.0F);
      GlStateManager.scalef(1.0F, 1.0F, -1.0F);
      GlStateManager.scalef(0.5F, 0.5F, 0.5F);
      if (gui3d) {
         GlStateManager.scalef(40.0F, 40.0F, 40.0F);
         GlStateManager.rotatef(210.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(-135.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.enableLighting();
      } else {
         GlStateManager.scalef(64.0F, 64.0F, 64.0F);
         GlStateManager.rotatef(180.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.disableLighting();
      }
   }

   public void renderGuiItem(ItemStack stack, int x, int y) {
      if (stack != null) {
         this.zOffset += 50.0F;

         try {
            this.renderGuiItemModel(stack, x, y);
         } catch (Throwable var7) {
            CrashReport var5 = CrashReport.of(var7, "Rendering item");
            CashReportCategory var6 = var5.addCategory("Item being rendered");
            var6.add("Item Type", new Callable() {
               public String call() {
                  return String.valueOf(stack.getItem());
               }
            });
            var6.add("Item Aux", new Callable() {
               public String call() {
                  return String.valueOf(stack.getMetadata());
               }
            });
            var6.add("Item NBT", new Callable() {
               public String call() {
                  return String.valueOf(stack.getNbt());
               }
            });
            var6.add("Item Foil", new Callable() {
               public String call() {
                  return String.valueOf(stack.hasEnchantmentGlint());
               }
            });
            throw new CrashException(var5);
         }

         this.zOffset -= 50.0F;
      }
   }

   public void renderGuiItemDecorations(TextRenderer textRenderer, ItemStack stack, int x, int y) {
      this.renderGuiItemDecorations(textRenderer, stack, x, y, null);
   }

   public void renderGuiItemDecorations(TextRenderer textRenderer, ItemStack stack, int x, int y, String stackSizeText) {
      if (stack != null) {
         if (stack.size > 1 || stackSizeText != null) {
            String var6 = stackSizeText == null ? String.valueOf(stack.size) : stackSizeText;
            GlStateManager.disableLighting();
            GlStateManager.enableDepth();
            GlStateManager.enableBlend();
            textRenderer.drawWithShadow(var6, (float)(x + 19 - 2 - textRenderer.getStringWidth(var6)), (float)(y + 6 + 3), 16777215);
            GlStateManager.enableLighting();
            GlStateManager.disableDepth();
         }

         if (stack.isDamaged()) {
            int var12 = (int)Math.round(13.0 - (double)stack.getDamage() * 13.0 / (double)stack.getMaxDamage());
            int var7 = (int)Math.round(255.0 - (double)stack.getDamage() * 255.0 / (double)stack.getMaxDamage());
            GlStateManager.disableLighting();
            GlStateManager.enableDepth();
            GlStateManager.disableTexture();
            GlStateManager.disableAlphaTest();
            GlStateManager.enableBlend();
            Tessellator var8 = Tessellator.getInstance();
            BufferBuilder var9 = var8.getBufferBuilder();
            int var10 = 255 - var7 << 16 | var7 << 8;
            int var11 = (255 - var7) / 4 << 16 | 16128;
            this.renderBufferVerticies(var9, x + 2, y + 13, 13, 2, 0);
            this.renderBufferVerticies(var9, x + 2, y + 13, 12, 1, var11);
            this.renderBufferVerticies(var9, x + 2, y + 13, var12, 1, var10);
            GlStateManager.disableBlend();
            GlStateManager.enableAlphaTest();
            GlStateManager.enableTexture();
            GlStateManager.enableLighting();
            GlStateManager.disableDepth();
         }
      }
   }

   private void renderBufferVerticies(BufferBuilder buffer, int x, int y, int dX, int z, int color) {
      buffer.start();
      buffer.color(color);
      buffer.vertex((double)(x + 0), (double)(y + 0), 0.0);
      buffer.vertex((double)(x + 0), (double)(y + z), 0.0);
      buffer.vertex((double)(x + dX), (double)(y + z), 0.0);
      buffer.vertex((double)(x + dX), (double)(y + 0), 0.0);
      Tessellator.getInstance().end();
   }

   private void registerGuiModels() {
      this.registerModel(Blocks.ANVIL, "anvil_intact");
      this.registerModel(Blocks.ANVIL, 1, "anvil_slightly_damaged");
      this.registerModel(Blocks.ANVIL, 2, "anvil_very_damaged");
      this.registerModel(Blocks.CARPET, DyeColor.BLACK.getIndex(), "black_carpet");
      this.registerModel(Blocks.CARPET, DyeColor.BLUE.getIndex(), "blue_carpet");
      this.registerModel(Blocks.CARPET, DyeColor.BROWN.getIndex(), "brown_carpet");
      this.registerModel(Blocks.CARPET, DyeColor.CYAN.getIndex(), "cyan_carpet");
      this.registerModel(Blocks.CARPET, DyeColor.GRAY.getIndex(), "gray_carpet");
      this.registerModel(Blocks.CARPET, DyeColor.GREEN.getIndex(), "green_carpet");
      this.registerModel(Blocks.CARPET, DyeColor.LIGHT_BLUE.getIndex(), "light_blue_carpet");
      this.registerModel(Blocks.CARPET, DyeColor.LIME.getIndex(), "lime_carpet");
      this.registerModel(Blocks.CARPET, DyeColor.MAGENTA.getIndex(), "magenta_carpet");
      this.registerModel(Blocks.CARPET, DyeColor.ORANGE.getIndex(), "orange_carpet");
      this.registerModel(Blocks.CARPET, DyeColor.PINK.getIndex(), "pink_carpet");
      this.registerModel(Blocks.CARPET, DyeColor.PURPLE.getIndex(), "purple_carpet");
      this.registerModel(Blocks.CARPET, DyeColor.RED.getIndex(), "red_carpet");
      this.registerModel(Blocks.CARPET, DyeColor.SILVER.getIndex(), "silver_carpet");
      this.registerModel(Blocks.CARPET, DyeColor.WHITE.getIndex(), "white_carpet");
      this.registerModel(Blocks.CARPET, DyeColor.YELLOW.getIndex(), "yellow_carpet");
      this.registerModel(Blocks.COBBLESTONE_WALL, WallBlock.Variant.MOSSY.getIndex(), "mossy_cobblestone_wall");
      this.registerModel(Blocks.COBBLESTONE_WALL, WallBlock.Variant.NORMAL.getIndex(), "cobblestone_wall");
      this.registerModel(Blocks.DIRT, DirtBlock.Variant.DOARSE_DIRT.getIndex(), "coarse_dirt");
      this.registerModel(Blocks.DIRT, DirtBlock.Variant.DIRT.getIndex(), "dirt");
      this.registerModel(Blocks.DIRT, DirtBlock.Variant.PODZOL.getIndex(), "podzol");
      this.registerModel(Blocks.DOUBLE_PLANT, DoublePlantBlock.Variant.FERN.getIndex(), "double_fern");
      this.registerModel(Blocks.DOUBLE_PLANT, DoublePlantBlock.Variant.GRASS.getIndex(), "double_grass");
      this.registerModel(Blocks.DOUBLE_PLANT, DoublePlantBlock.Variant.PAEONIA.getIndex(), "paeonia");
      this.registerModel(Blocks.DOUBLE_PLANT, DoublePlantBlock.Variant.ROSE.getIndex(), "double_rose");
      this.registerModel(Blocks.DOUBLE_PLANT, DoublePlantBlock.Variant.SUNFLOWER.getIndex(), "sunflower");
      this.registerModel(Blocks.DOUBLE_PLANT, DoublePlantBlock.Variant.SYRINGA.getIndex(), "syringa");
      this.registerModel(Blocks.LEAVES, PlanksBlock.Variant.BIRCH.getIndex(), "birch_leaves");
      this.registerModel(Blocks.LEAVES, PlanksBlock.Variant.JUNGLE.getIndex(), "jungle_leaves");
      this.registerModel(Blocks.LEAVES, PlanksBlock.Variant.OAK.getIndex(), "oak_leaves");
      this.registerModel(Blocks.LEAVES, PlanksBlock.Variant.SPRUCE.getIndex(), "spruce_leaves");
      this.registerModel(Blocks.LEAVES2, PlanksBlock.Variant.ACACIA.getIndex() - 4, "acacia_leaves");
      this.registerModel(Blocks.LEAVES2, PlanksBlock.Variant.DARK_OAK.getIndex() - 4, "dark_oak_leaves");
      this.registerModel(Blocks.LOG, PlanksBlock.Variant.BIRCH.getIndex(), "birch_log");
      this.registerModel(Blocks.LOG, PlanksBlock.Variant.JUNGLE.getIndex(), "jungle_log");
      this.registerModel(Blocks.LOG, PlanksBlock.Variant.OAK.getIndex(), "oak_log");
      this.registerModel(Blocks.LOG, PlanksBlock.Variant.SPRUCE.getIndex(), "spruce_log");
      this.registerModel(Blocks.LOG2, PlanksBlock.Variant.ACACIA.getIndex() - 4, "acacia_log");
      this.registerModel(Blocks.LOG2, PlanksBlock.Variant.DARK_OAK.getIndex() - 4, "dark_oak_log");
      this.registerModel(Blocks.MONSTER_EGG, InfestedBlock.Variant.CHISELED_STONEBRICK.getIndex(), "chiseled_brick_monster_egg");
      this.registerModel(Blocks.MONSTER_EGG, InfestedBlock.Variant.COBBLESTONE.getIndex(), "cobblestone_monster_egg");
      this.registerModel(Blocks.MONSTER_EGG, InfestedBlock.Variant.CRACKED_STONEBRICK.getIndex(), "cracked_brick_monster_egg");
      this.registerModel(Blocks.MONSTER_EGG, InfestedBlock.Variant.MOSSY_STONEBRICK.getIndex(), "mossy_brick_monster_egg");
      this.registerModel(Blocks.MONSTER_EGG, InfestedBlock.Variant.STONE.getIndex(), "stone_monster_egg");
      this.registerModel(Blocks.MONSTER_EGG, InfestedBlock.Variant.STONEBRICK.getIndex(), "stone_brick_monster_egg");
      this.registerModel(Blocks.PLANKS, PlanksBlock.Variant.ACACIA.getIndex(), "acacia_planks");
      this.registerModel(Blocks.PLANKS, PlanksBlock.Variant.BIRCH.getIndex(), "birch_planks");
      this.registerModel(Blocks.PLANKS, PlanksBlock.Variant.DARK_OAK.getIndex(), "dark_oak_planks");
      this.registerModel(Blocks.PLANKS, PlanksBlock.Variant.JUNGLE.getIndex(), "jungle_planks");
      this.registerModel(Blocks.PLANKS, PlanksBlock.Variant.OAK.getIndex(), "oak_planks");
      this.registerModel(Blocks.PLANKS, PlanksBlock.Variant.SPRUCE.getIndex(), "spruce_planks");
      this.registerModel(Blocks.PRISMARINE, PrismarineBlock.Variant.BRICKS.getIndex(), "prismarine_bricks");
      this.registerModel(Blocks.PRISMARINE, PrismarineBlock.Variant.DARK.getIndex(), "dark_prismarine");
      this.registerModel(Blocks.PRISMARINE, PrismarineBlock.Variant.ROUGH.getIndex(), "prismarine");
      this.registerModel(Blocks.QUARTZ_BLOCK, QuartzBlock.Variant.CHISELED.getIndex(), "chiseled_quartz_block");
      this.registerModel(Blocks.QUARTZ_BLOCK, QuartzBlock.Variant.DEFAULT.getIndex(), "quartz_block");
      this.registerModel(Blocks.QUARTZ_BLOCK, QuartzBlock.Variant.LINES_Y.getIndex(), "quartz_column");
      this.registerModel(Blocks.RED_FLOWER, FlowerBlock.Type.ALLIUM.getIndex(), "allium");
      this.registerModel(Blocks.RED_FLOWER, FlowerBlock.Type.BLUE_ORCHID.getIndex(), "blue_orchid");
      this.registerModel(Blocks.RED_FLOWER, FlowerBlock.Type.HOUSTONIA.getIndex(), "houstonia");
      this.registerModel(Blocks.RED_FLOWER, FlowerBlock.Type.ORANGE_TULIP.getIndex(), "orange_tulip");
      this.registerModel(Blocks.RED_FLOWER, FlowerBlock.Type.OXEY_DAISY.getIndex(), "oxeye_daisy");
      this.registerModel(Blocks.RED_FLOWER, FlowerBlock.Type.PINK_TULIP.getIndex(), "pink_tulip");
      this.registerModel(Blocks.RED_FLOWER, FlowerBlock.Type.POPPY.getIndex(), "poppy");
      this.registerModel(Blocks.RED_FLOWER, FlowerBlock.Type.RED_TULIP.getIndex(), "red_tulip");
      this.registerModel(Blocks.RED_FLOWER, FlowerBlock.Type.WHITE_TULIP.getIndex(), "white_tulip");
      this.registerModel(Blocks.SAND, SandBlock.Variant.RED_SAND.getIndex(), "red_sand");
      this.registerModel(Blocks.SAND, SandBlock.Variant.SAND.getIndex(), "sand");
      this.registerModel(Blocks.SANDSTONE, SandstoneBlock.Type.CHISELED.getIndex(), "chiseled_sandstone");
      this.registerModel(Blocks.SANDSTONE, SandstoneBlock.Type.DEFAULT.getIndex(), "sandstone");
      this.registerModel(Blocks.SANDSTONE, SandstoneBlock.Type.SMOOTH.getIndex(), "smooth_sandstone");
      this.registerModel(Blocks.SAPLING, PlanksBlock.Variant.ACACIA.getIndex(), "acacia_sapling");
      this.registerModel(Blocks.SAPLING, PlanksBlock.Variant.BIRCH.getIndex(), "birch_sapling");
      this.registerModel(Blocks.SAPLING, PlanksBlock.Variant.DARK_OAK.getIndex(), "dark_oak_sapling");
      this.registerModel(Blocks.SAPLING, PlanksBlock.Variant.JUNGLE.getIndex(), "jungle_sapling");
      this.registerModel(Blocks.SAPLING, PlanksBlock.Variant.OAK.getIndex(), "oak_sapling");
      this.registerModel(Blocks.SAPLING, PlanksBlock.Variant.SPRUCE.getIndex(), "spruce_sapling");
      this.registerModel(Blocks.SPONGE, 0, "sponge");
      this.registerModel(Blocks.SPONGE, 1, "sponge_wet");
      this.registerModel(Blocks.STAINED_GLASS, DyeColor.BLACK.getIndex(), "black_stained_glass");
      this.registerModel(Blocks.STAINED_GLASS, DyeColor.BLUE.getIndex(), "blue_stained_glass");
      this.registerModel(Blocks.STAINED_GLASS, DyeColor.BROWN.getIndex(), "brown_stained_glass");
      this.registerModel(Blocks.STAINED_GLASS, DyeColor.CYAN.getIndex(), "cyan_stained_glass");
      this.registerModel(Blocks.STAINED_GLASS, DyeColor.GRAY.getIndex(), "gray_stained_glass");
      this.registerModel(Blocks.STAINED_GLASS, DyeColor.GREEN.getIndex(), "green_stained_glass");
      this.registerModel(Blocks.STAINED_GLASS, DyeColor.LIGHT_BLUE.getIndex(), "light_blue_stained_glass");
      this.registerModel(Blocks.STAINED_GLASS, DyeColor.LIME.getIndex(), "lime_stained_glass");
      this.registerModel(Blocks.STAINED_GLASS, DyeColor.MAGENTA.getIndex(), "magenta_stained_glass");
      this.registerModel(Blocks.STAINED_GLASS, DyeColor.ORANGE.getIndex(), "orange_stained_glass");
      this.registerModel(Blocks.STAINED_GLASS, DyeColor.PINK.getIndex(), "pink_stained_glass");
      this.registerModel(Blocks.STAINED_GLASS, DyeColor.PURPLE.getIndex(), "purple_stained_glass");
      this.registerModel(Blocks.STAINED_GLASS, DyeColor.RED.getIndex(), "red_stained_glass");
      this.registerModel(Blocks.STAINED_GLASS, DyeColor.SILVER.getIndex(), "silver_stained_glass");
      this.registerModel(Blocks.STAINED_GLASS, DyeColor.WHITE.getIndex(), "white_stained_glass");
      this.registerModel(Blocks.STAINED_GLASS, DyeColor.YELLOW.getIndex(), "yellow_stained_glass");
      this.registerModel(Blocks.STAINED_GLASS_PANE, DyeColor.BLACK.getIndex(), "black_stained_glass_pane");
      this.registerModel(Blocks.STAINED_GLASS_PANE, DyeColor.BLUE.getIndex(), "blue_stained_glass_pane");
      this.registerModel(Blocks.STAINED_GLASS_PANE, DyeColor.BROWN.getIndex(), "brown_stained_glass_pane");
      this.registerModel(Blocks.STAINED_GLASS_PANE, DyeColor.CYAN.getIndex(), "cyan_stained_glass_pane");
      this.registerModel(Blocks.STAINED_GLASS_PANE, DyeColor.GRAY.getIndex(), "gray_stained_glass_pane");
      this.registerModel(Blocks.STAINED_GLASS_PANE, DyeColor.GREEN.getIndex(), "green_stained_glass_pane");
      this.registerModel(Blocks.STAINED_GLASS_PANE, DyeColor.LIGHT_BLUE.getIndex(), "light_blue_stained_glass_pane");
      this.registerModel(Blocks.STAINED_GLASS_PANE, DyeColor.LIME.getIndex(), "lime_stained_glass_pane");
      this.registerModel(Blocks.STAINED_GLASS_PANE, DyeColor.MAGENTA.getIndex(), "magenta_stained_glass_pane");
      this.registerModel(Blocks.STAINED_GLASS_PANE, DyeColor.ORANGE.getIndex(), "orange_stained_glass_pane");
      this.registerModel(Blocks.STAINED_GLASS_PANE, DyeColor.PINK.getIndex(), "pink_stained_glass_pane");
      this.registerModel(Blocks.STAINED_GLASS_PANE, DyeColor.PURPLE.getIndex(), "purple_stained_glass_pane");
      this.registerModel(Blocks.STAINED_GLASS_PANE, DyeColor.RED.getIndex(), "red_stained_glass_pane");
      this.registerModel(Blocks.STAINED_GLASS_PANE, DyeColor.SILVER.getIndex(), "silver_stained_glass_pane");
      this.registerModel(Blocks.STAINED_GLASS_PANE, DyeColor.WHITE.getIndex(), "white_stained_glass_pane");
      this.registerModel(Blocks.STAINED_GLASS_PANE, DyeColor.YELLOW.getIndex(), "yellow_stained_glass_pane");
      this.registerModel(Blocks.STAINED_HARDENED_CLAY, DyeColor.BLACK.getIndex(), "black_stained_hardened_clay");
      this.registerModel(Blocks.STAINED_HARDENED_CLAY, DyeColor.BLUE.getIndex(), "blue_stained_hardened_clay");
      this.registerModel(Blocks.STAINED_HARDENED_CLAY, DyeColor.BROWN.getIndex(), "brown_stained_hardened_clay");
      this.registerModel(Blocks.STAINED_HARDENED_CLAY, DyeColor.CYAN.getIndex(), "cyan_stained_hardened_clay");
      this.registerModel(Blocks.STAINED_HARDENED_CLAY, DyeColor.GRAY.getIndex(), "gray_stained_hardened_clay");
      this.registerModel(Blocks.STAINED_HARDENED_CLAY, DyeColor.GREEN.getIndex(), "green_stained_hardened_clay");
      this.registerModel(Blocks.STAINED_HARDENED_CLAY, DyeColor.LIGHT_BLUE.getIndex(), "light_blue_stained_hardened_clay");
      this.registerModel(Blocks.STAINED_HARDENED_CLAY, DyeColor.LIME.getIndex(), "lime_stained_hardened_clay");
      this.registerModel(Blocks.STAINED_HARDENED_CLAY, DyeColor.MAGENTA.getIndex(), "magenta_stained_hardened_clay");
      this.registerModel(Blocks.STAINED_HARDENED_CLAY, DyeColor.ORANGE.getIndex(), "orange_stained_hardened_clay");
      this.registerModel(Blocks.STAINED_HARDENED_CLAY, DyeColor.PINK.getIndex(), "pink_stained_hardened_clay");
      this.registerModel(Blocks.STAINED_HARDENED_CLAY, DyeColor.PURPLE.getIndex(), "purple_stained_hardened_clay");
      this.registerModel(Blocks.STAINED_HARDENED_CLAY, DyeColor.RED.getIndex(), "red_stained_hardened_clay");
      this.registerModel(Blocks.STAINED_HARDENED_CLAY, DyeColor.SILVER.getIndex(), "silver_stained_hardened_clay");
      this.registerModel(Blocks.STAINED_HARDENED_CLAY, DyeColor.WHITE.getIndex(), "white_stained_hardened_clay");
      this.registerModel(Blocks.STAINED_HARDENED_CLAY, DyeColor.YELLOW.getIndex(), "yellow_stained_hardened_clay");
      this.registerModel(Blocks.STONE, StoneBlock.Variant.ANDESITE.getIndex(), "andesite");
      this.registerModel(Blocks.STONE, StoneBlock.Variant.ANDESITE_SMOOTH.getIndex(), "andesite_smooth");
      this.registerModel(Blocks.STONE, StoneBlock.Variant.DIORITE.getIndex(), "diorite");
      this.registerModel(Blocks.STONE, StoneBlock.Variant.DIORITE_SMOOTH.getIndex(), "diorite_smooth");
      this.registerModel(Blocks.STONE, StoneBlock.Variant.GRANITE.getIndex(), "granite");
      this.registerModel(Blocks.STONE, StoneBlock.Variant.GRANITE_SMOOTH.getIndex(), "granite_smooth");
      this.registerModel(Blocks.STONE, StoneBlock.Variant.STONE.getIndex(), "stone");
      this.registerModel(Blocks.STONE_BRICKS, StonebrickBlock.Variant.CRACKED.getIndex(), "cracked_stonebrick");
      this.registerModel(Blocks.STONE_BRICKS, StonebrickBlock.Variant.DEFAULT.getIndex(), "stonebrick");
      this.registerModel(Blocks.STONE_BRICKS, StonebrickBlock.Variant.CHISELED.getIndex(), "chiseled_stonebrick");
      this.registerModel(Blocks.STONE_BRICKS, StonebrickBlock.Variant.MOSSY.getIndex(), "mossy_stonebrick");
      this.registerModel(Blocks.STONE_SLAB, StoneSlabBlock.Variant.BRICK.getIndex(), "brick_slab");
      this.registerModel(Blocks.STONE_SLAB, StoneSlabBlock.Variant.COBBLESTONE.getIndex(), "cobblestone_slab");
      this.registerModel(Blocks.STONE_SLAB, StoneSlabBlock.Variant.NETHERBRICK.getIndex(), "nether_brick_slab");
      this.registerModel(Blocks.STONE_SLAB, StoneSlabBlock.Variant.QUARTZ.getIndex(), "quartz_slab");
      this.registerModel(Blocks.STONE_SLAB, StoneSlabBlock.Variant.SAND.getIndex(), "sandstone_slab");
      this.registerModel(Blocks.STONE_SLAB, StoneSlabBlock.Variant.SMOOTHBRICK.getIndex(), "stone_brick_slab");
      this.registerModel(Blocks.STONE_SLAB, StoneSlabBlock.Variant.STONE.getIndex(), "stone_slab");
      this.registerModel(Blocks.TALLGRASS, TallPlantBlock.Type.DEAD_BUSH.getIndex(), "dead_bush");
      this.registerModel(Blocks.TALLGRASS, TallPlantBlock.Type.FERN.getIndex(), "fern");
      this.registerModel(Blocks.TALLGRASS, TallPlantBlock.Type.GRASS.getIndex(), "tall_grass");
      this.registerModel(Blocks.WOODEN_SLAB, PlanksBlock.Variant.ACACIA.getIndex(), "acacia_slab");
      this.registerModel(Blocks.WOODEN_SLAB, PlanksBlock.Variant.BIRCH.getIndex(), "birch_slab");
      this.registerModel(Blocks.WOODEN_SLAB, PlanksBlock.Variant.DARK_OAK.getIndex(), "dark_oak_slab");
      this.registerModel(Blocks.WOODEN_SLAB, PlanksBlock.Variant.JUNGLE.getIndex(), "jungle_slab");
      this.registerModel(Blocks.WOODEN_SLAB, PlanksBlock.Variant.OAK.getIndex(), "oak_slab");
      this.registerModel(Blocks.WOODEN_SLAB, PlanksBlock.Variant.SPRUCE.getIndex(), "spruce_slab");
      this.registerModel(Blocks.WOOL, DyeColor.BLACK.getIndex(), "black_wool");
      this.registerModel(Blocks.WOOL, DyeColor.BLUE.getIndex(), "blue_wool");
      this.registerModel(Blocks.WOOL, DyeColor.BROWN.getIndex(), "brown_wool");
      this.registerModel(Blocks.WOOL, DyeColor.CYAN.getIndex(), "cyan_wool");
      this.registerModel(Blocks.WOOL, DyeColor.GRAY.getIndex(), "gray_wool");
      this.registerModel(Blocks.WOOL, DyeColor.GREEN.getIndex(), "green_wool");
      this.registerModel(Blocks.WOOL, DyeColor.LIGHT_BLUE.getIndex(), "light_blue_wool");
      this.registerModel(Blocks.WOOL, DyeColor.LIME.getIndex(), "lime_wool");
      this.registerModel(Blocks.WOOL, DyeColor.MAGENTA.getIndex(), "magenta_wool");
      this.registerModel(Blocks.WOOL, DyeColor.ORANGE.getIndex(), "orange_wool");
      this.registerModel(Blocks.WOOL, DyeColor.PINK.getIndex(), "pink_wool");
      this.registerModel(Blocks.WOOL, DyeColor.PURPLE.getIndex(), "purple_wool");
      this.registerModel(Blocks.WOOL, DyeColor.RED.getIndex(), "red_wool");
      this.registerModel(Blocks.WOOL, DyeColor.SILVER.getIndex(), "silver_wool");
      this.registerModel(Blocks.WOOL, DyeColor.WHITE.getIndex(), "white_wool");
      this.registerModel(Blocks.WOOL, DyeColor.YELLOW.getIndex(), "yellow_wool");
      this.registerModel(Blocks.ACACIA_STAIRS, "acacia_stairs");
      this.registerModel(Blocks.ACTIVATOR_RAIL, "activator_rail");
      this.registerModel(Blocks.BEACON, "beacon");
      this.registerModel(Blocks.BEDROCK, "bedrock");
      this.registerModel(Blocks.BIRCH_STAIRS, "birch_stairs");
      this.registerModel(Blocks.BOOKSHELF, "bookshelf");
      this.registerModel(Blocks.BRICKS, "brick_block");
      this.registerModel(Blocks.BRICKS, "brick_block");
      this.registerModel(Blocks.BRICK_STAIRS, "brick_stairs");
      this.registerModel(Blocks.BROWN_MUSHROOM, "brown_mushroom");
      this.registerModel(Blocks.CACTUS, "cactus");
      this.registerModel(Blocks.CLAY, "clay");
      this.registerModel(Blocks.COAL_BLOCK, "coal_block");
      this.registerModel(Blocks.COAL_ORE, "coal_ore");
      this.registerModel(Blocks.COBBLESTONE, "cobblestone");
      this.registerModel(Blocks.CRAFTING_TABLE, "crafting_table");
      this.registerModel(Blocks.DARK_OAK_STAIRS, "dark_oak_stairs");
      this.registerModel(Blocks.DAYLIGHT_DETECTOR, "daylight_detector");
      this.registerModel(Blocks.DEADBUSH, "dead_bush");
      this.registerModel(Blocks.DETECTOR_RAIL, "detector_rail");
      this.registerModel(Blocks.DIAMOND_BLOCK, "diamond_block");
      this.registerModel(Blocks.DIAMOND_ORE, "diamond_ore");
      this.registerModel(Blocks.DISPENSER, "dispenser");
      this.registerModel(Blocks.DROPPER, "dropper");
      this.registerModel(Blocks.EMERALD_BLOCK, "emerald_block");
      this.registerModel(Blocks.EMERALD_ORE, "emerald_ore");
      this.registerModel(Blocks.ENCHANTING_TABLE, "enchanting_table");
      this.registerModel(Blocks.END_PORTAL_FRAME, "end_portal_frame");
      this.registerModel(Blocks.END_STONE, "end_stone");
      this.registerModel(Blocks.FENCE, "fence");
      this.registerModel(Blocks.FENCE_GATE, "fence_gate");
      this.registerModel(Blocks.FURNACE, "furnace");
      this.registerModel(Blocks.GLASS, "glass");
      this.registerModel(Blocks.GLASS_PANE, "glass_pane");
      this.registerModel(Blocks.GLOWSTONE, "glowstone");
      this.registerModel(Blocks.POWERED_RAIL, "golden_rail");
      this.registerModel(Blocks.GOLD_BLOCK, "gold_block");
      this.registerModel(Blocks.GOLD_ORE, "gold_ore");
      this.registerModel(Blocks.GRASS, "grass");
      this.registerModel(Blocks.GRAVEL, "gravel");
      this.registerModel(Blocks.HARDENED_CLAY, "hardened_clay");
      this.registerModel(Blocks.HAY, "hay_block");
      this.registerModel(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, "heavy_weighted_pressure_plate");
      this.registerModel(Blocks.HOPPER, "hopper");
      this.registerModel(Blocks.ICE, "ice");
      this.registerModel(Blocks.IRON_BARS, "iron_bars");
      this.registerModel(Blocks.IRON_BLOCK, "iron_block");
      this.registerModel(Blocks.IRON_ORE, "iron_ore");
      this.registerModel(Blocks.IRON_TRAPDOOR, "iron_trapdoor");
      this.registerModel(Blocks.JUKEBOX, "jukebox");
      this.registerModel(Blocks.JUNGLE_STAIRS, "jungle_stairs");
      this.registerModel(Blocks.LADDER, "ladder");
      this.registerModel(Blocks.LAPIS_BLOCK, "lapis_block");
      this.registerModel(Blocks.LAPIS_ORE, "lapis_ore");
      this.registerModel(Blocks.LEVER, "lever");
      this.registerModel(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, "light_weighted_pressure_plate");
      this.registerModel(Blocks.LIT_PUMPKIN, "lit_pumpkin");
      this.registerModel(Blocks.MELON_BLOCK, "melon_block");
      this.registerModel(Blocks.MOSSY_COBBLESTONE, "mossy_cobblestone");
      this.registerModel(Blocks.MYCELIUM, "mycelium");
      this.registerModel(Blocks.NETHERRACK, "netherrack");
      this.registerModel(Blocks.NETHER_BRICKS, "nether_brick");
      this.registerModel(Blocks.NETHER_BRICK_FENCE, "nether_brick_fence");
      this.registerModel(Blocks.NETHER_BRICK_STAIRS, "nether_brick_stairs");
      this.registerModel(Blocks.NOTEBLOCK, "noteblock");
      this.registerModel(Blocks.OAK_STAIRS, "oak_stairs");
      this.registerModel(Blocks.OBSIDIAN, "obsidian");
      this.registerModel(Blocks.PACKED_ICE, "packed_ice");
      this.registerModel(Blocks.PISTON, "piston");
      this.registerModel(Blocks.PUMPKIN, "pumpkin");
      this.registerModel(Blocks.QUARTZ_ORE, "quartz_ore");
      this.registerModel(Blocks.QUARTZ_STAIRS, "quartz_stairs");
      this.registerModel(Blocks.RAIL, "rail");
      this.registerModel(Blocks.REDSTONE_BLOCK, "redstone_block");
      this.registerModel(Blocks.REDSTONE_LAMP, "redstone_lamp");
      this.registerModel(Blocks.REDSTONE_ORE, "redstone_ore");
      this.registerModel(Blocks.REDSTONE_TORCH, "redstone_torch");
      this.registerModel(Blocks.RED_MUSHROOM, "red_mushroom");
      this.registerModel(Blocks.SANDSTONE_STAIRS, "sandstone_stairs");
      this.registerModel(Blocks.SEA_LANTERN, "sea_lantern");
      this.registerModel(Blocks.SLIME, "slime");
      this.registerModel(Blocks.SNOW, "snow");
      this.registerModel(Blocks.SNOW_LAYER, "snow_layer");
      this.registerModel(Blocks.SOUL_SAND, "soul_sand");
      this.registerModel(Blocks.SPRUCE_STAIRS, "spruce_stairs");
      this.registerModel(Blocks.STICKY_PISTON, "sticky_piston");
      this.registerModel(Blocks.STONE_BRICK_STAIRS, "stone_brick_stairs");
      this.registerModel(Blocks.STONE_BUTTON, "stone_button");
      this.registerModel(Blocks.STONE_PRESSURE_PLATE, "stone_pressure_plate");
      this.registerModel(Blocks.STONE_STAIRS, "stone_stairs");
      this.registerModel(Blocks.TNT, "tnt");
      this.registerModel(Blocks.TORCH, "torch");
      this.registerModel(Blocks.TRAPDOOR, "trapdoor");
      this.registerModel(Blocks.TRIPWIRE_HOOK, "tripwire_hook");
      this.registerModel(Blocks.VINE, "vine");
      this.registerModel(Blocks.LILY_PAD, "waterlily");
      this.registerModel(Blocks.WEB, "web");
      this.registerModel(Blocks.WOODEN_BUTTON, "wooden_button");
      this.registerModel(Blocks.WOODEN_PRESSURE_PLATE, "wooden_pressure_plate");
      this.registerModel(Blocks.YELLOW_FLOWER, FlowerBlock.Type.DANDELION.getIndex(), "dandelion");
      this.registerModel(Blocks.CHEST, "chest");
      this.registerModel(Blocks.TRAPPED_CHEST, "trapped_chest");
      this.registerModel(Blocks.ENDER_CHEST, "ender_chest");
      this.registerModel(Items.IRON_SHOVEL, "iron_shovel");
      this.registerModel(Items.IRON_PICKAXE, "iron_pickaxe");
      this.registerModel(Items.IRON_AXE, "iron_axe");
      this.registerModel(Items.FLINT_AND_STEEL, "flint_and_steel");
      this.registerModel(Items.APPLE, "apple");
      this.registerModel(Items.BOW, 0, "bow");
      this.registerModel(Items.BOW, 1, "bow_pulling_0");
      this.registerModel(Items.BOW, 2, "bow_pulling_1");
      this.registerModel(Items.BOW, 3, "bow_pulling_2");
      this.registerModel(Items.ARROW, "arrow");
      this.registerModel(Items.COAL, 0, "coal");
      this.registerModel(Items.COAL, 1, "charcoal");
      this.registerModel(Items.DIAMOND, "diamond");
      this.registerModel(Items.IRON_INGOT, "iron_ingot");
      this.registerModel(Items.GOLD_INGOT, "gold_ingot");
      this.registerModel(Items.IRON_SWORD, "iron_sword");
      this.registerModel(Items.WOODEN_SWORD, "wooden_sword");
      this.registerModel(Items.WOODEN_SHOVEL, "wooden_shovel");
      this.registerModel(Items.WOODEN_PICKAXE, "wooden_pickaxe");
      this.registerModel(Items.WOODEN_AXE, "wooden_axe");
      this.registerModel(Items.STONE_SWORD, "stone_sword");
      this.registerModel(Items.STONE_SHOVEL, "stone_shovel");
      this.registerModel(Items.STONE_PICKAXE, "stone_pickaxe");
      this.registerModel(Items.STONE_AXE, "stone_axe");
      this.registerModel(Items.DIAMOND_SWORD, "diamond_sword");
      this.registerModel(Items.DIAMOND_SHOVEL, "diamond_shovel");
      this.registerModel(Items.DIAMOND_PICKAXE, "diamond_pickaxe");
      this.registerModel(Items.DIAMOND_AXE, "diamond_axe");
      this.registerModel(Items.STICK, "stick");
      this.registerModel(Items.BOWL, "bowl");
      this.registerModel(Items.MUSHROOM_STEW, "mushroom_stew");
      this.registerModel(Items.GOLDEN_SWORD, "golden_sword");
      this.registerModel(Items.GOLDEN_SHOVEL, "golden_shovel");
      this.registerModel(Items.GOLDEN_PICKAXE, "golden_pickaxe");
      this.registerModel(Items.GOLDEN_AXE, "golden_axe");
      this.registerModel(Items.STRING, "string");
      this.registerModel(Items.FEATHER, "feather");
      this.registerModel(Items.GUNPOWDER, "gunpowder");
      this.registerModel(Items.WOODEN_HOE, "wooden_hoe");
      this.registerModel(Items.STONE_HOE, "stone_hoe");
      this.registerModel(Items.IRON_HOE, "iron_hoe");
      this.registerModel(Items.DIAMOND_HOE, "diamond_hoe");
      this.registerModel(Items.GOLDEN_HOE, "golden_hoe");
      this.registerModel(Items.WHEAT_SEEDS, "wheat_seeds");
      this.registerModel(Items.WHEAT, "wheat");
      this.registerModel(Items.BREAD, "bread");
      this.registerModel(Items.LEATHER_HELMET, "leather_helmet");
      this.registerModel(Items.LEATHER_CHESTPLATE, "leather_chestplate");
      this.registerModel(Items.LEATHER_LEGGINGS, "leather_leggings");
      this.registerModel(Items.LEATHER_BOOTS, "leather_boots");
      this.registerModel(Items.CHAINMAIL_HELMET, "chainmail_helmet");
      this.registerModel(Items.CHAINMAIL_CHESTPLATE, "chainmail_chestplate");
      this.registerModel(Items.CHAINMAIL_LEGGINGS, "chainmail_leggings");
      this.registerModel(Items.CHAINMAIL_BOOTS, "chainmail_boots");
      this.registerModel(Items.IRON_HELMET, "iron_helmet");
      this.registerModel(Items.IRON_CHESTPLATE, "iron_chestplate");
      this.registerModel(Items.IRON_LEGGINGS, "iron_leggings");
      this.registerModel(Items.IRON_BOOTS, "iron_boots");
      this.registerModel(Items.DIAMOND_HELMET, "diamond_helmet");
      this.registerModel(Items.DIAMOND_CHESTPLATE, "diamond_chestplate");
      this.registerModel(Items.DIAMOND_LEGGINGS, "diamond_leggings");
      this.registerModel(Items.DIAMOND_BOOTS, "diamond_boots");
      this.registerModel(Items.GOLDEN_HELMET, "golden_helmet");
      this.registerModel(Items.GOLDEN_CHESTPLATE, "golden_chestplate");
      this.registerModel(Items.GOLDEN_LEGGINGS, "golden_leggings");
      this.registerModel(Items.GOLDEN_BOOTS, "golden_boots");
      this.registerModel(Items.FLINT, "flint");
      this.registerModel(Items.PORKCHOP, "porkchop");
      this.registerModel(Items.COOKED_PORKCHOP, "cooked_porkchop");
      this.registerModel(Items.PAINTING, "painting");
      this.registerModel(Items.GOLDEN_APPLE, "golden_apple");
      this.registerModel(Items.GOLDEN_APPLE, 1, "golden_apple");
      this.registerModel(Items.SIGN, "sign");
      this.registerModel(Items.WOODEN_DOOR, "wooden_door");
      this.registerModel(Items.BUCKET, "bucket");
      this.registerModel(Items.WATER_BUCKET, "water_bucket");
      this.registerModel(Items.LAVA_BUCKET, "lava_bucket");
      this.registerModel(Items.MINECART, "minecart");
      this.registerModel(Items.SADDLE, "saddle");
      this.registerModel(Items.IRON_DOOR, "iron_door");
      this.registerModel(Items.REDSTONE, "redstone");
      this.registerModel(Items.SNOWBALL, "snowball");
      this.registerModel(Items.BOAT, "boat");
      this.registerModel(Items.LEATHER, "leather");
      this.registerModel(Items.MILK_BUCKET, "milk_bucket");
      this.registerModel(Items.BRICK, "brick");
      this.registerModel(Items.CLAY_BALL, "clay_ball");
      this.registerModel(Items.REEDS, "reeds");
      this.registerModel(Items.PAPER, "paper");
      this.registerModel(Items.BOOK, "book");
      this.registerModel(Items.SLIME_BALL, "slime_ball");
      this.registerModel(Items.CHEST_MINECART, "chest_minecart");
      this.registerModel(Items.FURNACE_MINECART, "furnace_minecart");
      this.registerModel(Items.EGG, "egg");
      this.registerModel(Items.COMPASS, "compass");
      this.registerModel(Items.FISHING_ROD, "fishing_rod");
      this.registerModel(Items.FISHING_ROD, 1, "fishing_rod_cast");
      this.registerModel(Items.CLOCK, "clock");
      this.registerModel(Items.GLOWSTONE_DUST, "glowstone_dust");
      this.registerModel(Items.FISH, FishItem.Type.COD.getId(), "cod");
      this.registerModel(Items.FISH, FishItem.Type.SALMON.getId(), "salmon");
      this.registerModel(Items.FISH, FishItem.Type.CLOWNFISH.getId(), "clownfish");
      this.registerModel(Items.FISH, FishItem.Type.PUFFERFISH.getId(), "pufferfish");
      this.registerModel(Items.COOKED_FISH, FishItem.Type.COD.getId(), "cooked_cod");
      this.registerModel(Items.COOKED_FISH, FishItem.Type.SALMON.getId(), "cooked_salmon");
      this.registerModel(Items.DYE, DyeColor.BLACK.getMetadata(), "dye_black");
      this.registerModel(Items.DYE, DyeColor.RED.getMetadata(), "dye_red");
      this.registerModel(Items.DYE, DyeColor.GREEN.getMetadata(), "dye_green");
      this.registerModel(Items.DYE, DyeColor.BROWN.getMetadata(), "dye_brown");
      this.registerModel(Items.DYE, DyeColor.BLUE.getMetadata(), "dye_blue");
      this.registerModel(Items.DYE, DyeColor.PURPLE.getMetadata(), "dye_purple");
      this.registerModel(Items.DYE, DyeColor.CYAN.getMetadata(), "dye_cyan");
      this.registerModel(Items.DYE, DyeColor.SILVER.getMetadata(), "dye_silver");
      this.registerModel(Items.DYE, DyeColor.GRAY.getMetadata(), "dye_gray");
      this.registerModel(Items.DYE, DyeColor.PINK.getMetadata(), "dye_pink");
      this.registerModel(Items.DYE, DyeColor.LIME.getMetadata(), "dye_lime");
      this.registerModel(Items.DYE, DyeColor.YELLOW.getMetadata(), "dye_yellow");
      this.registerModel(Items.DYE, DyeColor.LIGHT_BLUE.getMetadata(), "dye_light_blue");
      this.registerModel(Items.DYE, DyeColor.MAGENTA.getMetadata(), "dye_magenta");
      this.registerModel(Items.DYE, DyeColor.ORANGE.getMetadata(), "dye_orange");
      this.registerModel(Items.DYE, DyeColor.WHITE.getMetadata(), "dye_white");
      this.registerModel(Items.BONE, "bone");
      this.registerModel(Items.SUGAR, "sugar");
      this.registerModel(Items.CAKE, "cake");
      this.registerModel(Items.BED, "bed");
      this.registerModel(Items.REPEATER, "repeater");
      this.registerModel(Items.COOKIE, "cookie");
      this.registerModel(Items.SHEARS, "shears");
      this.registerModel(Items.MELON, "melon");
      this.registerModel(Items.PUMPKIN_SEEDS, "pumpkin_seeds");
      this.registerModel(Items.MELON_SEEDS, "melon_seeds");
      this.registerModel(Items.BEEF, "beef");
      this.registerModel(Items.COOKED_BEEF, "cooked_beef");
      this.registerModel(Items.CHICKEN, "chicken");
      this.registerModel(Items.COOKED_CHICKEN, "cooked_chicken");
      this.registerModel(Items.RABBIT, "rabbit");
      this.registerModel(Items.COOKED_RABBIT, "cooked_rabbit");
      this.registerModel(Items.MUTTON, "mutton");
      this.registerModel(Items.COOKED_MUTTON, "cooked_mutton");
      this.registerModel(Items.RABBIT_FOOT, "rabbit_foot");
      this.registerModel(Items.RABBIT_HIDE, "rabbit_hide");
      this.registerModel(Items.RABBIT_STEW, "rabbit_stew");
      this.registerModel(Items.ROTTEN_FLESH, "rotten_flesh");
      this.registerModel(Items.ENDER_PEARL, "ender_pearl");
      this.registerModel(Items.BLAZE_ROD, "blaze_rod");
      this.registerModel(Items.GHAST_TEAR, "ghast_tear");
      this.registerModel(Items.GOLD_NUGGET, "gold_nugget");
      this.registerModel(Items.NETHER_WART, "nether_wart");
      this.modelShaper
         .register(
            Items.POTION,
            new ItemModelProvider() {
               @Override
               public ModelIdentifier provide(ItemStack stack) {
                  return PotionItem.isSplashPotion(stack.getMetadata())
                     ? new ModelIdentifier("bottle_splash", "inventory")
                     : new ModelIdentifier("bottle_drinkable", "inventory");
               }
            }
         );
      this.registerModel(Items.GLASS_BOTTLE, "glass_bottle");
      this.registerModel(Items.SPIDER_EYE, "spider_eye");
      this.registerModel(Items.FERMENTED_SPIDER_EYE, "fermented_spider_eye");
      this.registerModel(Items.BLAZE_POWDER, "blaze_powder");
      this.registerModel(Items.MAGMA_CREAM, "magma_cream");
      this.registerModel(Items.BREWING_STAND, "brewing_stand");
      this.registerModel(Items.CAULDRON, "cauldron");
      this.registerModel(Items.ENDER_EYE, "ender_eye");
      this.registerModel(Items.SPECKLED_MELON, "speckled_melon");
      this.modelShaper.register(Items.SPAWN_EGG, new ItemModelProvider() {
         @Override
         public ModelIdentifier provide(ItemStack stack) {
            return new ModelIdentifier("spawn_egg", "inventory");
         }
      });
      this.registerModel(Items.EXPERIENCE_BOTTLE, "experience_bottle");
      this.registerModel(Items.FIRE_CHARGE, "fire_charge");
      this.registerModel(Items.WRITABLE_BOOK, "writable_book");
      this.registerModel(Items.EMERALD, "emerald");
      this.registerModel(Items.ITEM_FRAME, "item_frame");
      this.registerModel(Items.FLOWER_POT, "flower_pot");
      this.registerModel(Items.CARROT, "carrot");
      this.registerModel(Items.POTATO, "potato");
      this.registerModel(Items.BAKED_POTATO, "baked_potato");
      this.registerModel(Items.POISONOUS_POTATO, "poisonous_potato");
      this.registerModel(Items.MAP, "map");
      this.registerModel(Items.GOLDEN_CARROT, "golden_carrot");
      this.registerModel(Items.SKULL, "skull_skeleton");
      this.registerModel(Items.SKULL, 1, "skull_wither");
      this.registerModel(Items.SKULL, 2, "skull_zombie");
      this.registerModel(Items.SKULL, 3, "skull_char");
      this.registerModel(Items.SKULL, 4, "skull_creeper");
      this.registerModel(Items.CARROT_ON_A_STICK, "carrot_on_a_stick");
      this.registerModel(Items.NETHER_STAR, "nether_star");
      this.registerModel(Items.PUMPKIN_PIE, "pumpkin_pie");
      this.registerModel(Items.FIREWORKS_CHARGE, "firework_charge");
      this.registerModel(Items.COMPARATOR, "comparator");
      this.registerModel(Items.NETHERBRICK, "netherbrick");
      this.registerModel(Items.QUARTZ, "quartz");
      this.registerModel(Items.TNT_MINECART, "tnt_minecart");
      this.registerModel(Items.HOPPER_MINECART, "hopper_minecart");
      this.registerModel(Items.IRON_HORSE_ARMOR, "iron_horse_armor");
      this.registerModel(Items.GOLDEN_HORSE_ARMOR, "golden_horse_armor");
      this.registerModel(Items.DIAMOND_HORSE_ARMOR, "diamond_horse_armor");
      this.registerModel(Items.LEAD, "lead");
      this.registerModel(Items.NAME_TAG, "name_tag");
      this.modelShaper.register(Items.BANNER, new ItemModelProvider() {
         @Override
         public ModelIdentifier provide(ItemStack stack) {
            return new ModelIdentifier("banner", "inventory");
         }
      });
      this.registerModel(Items.RECORD_13, "record_13");
      this.registerModel(Items.RECORD_CAT, "record_cat");
      this.registerModel(Items.RECORD_BLOCKS, "record_blocks");
      this.registerModel(Items.RECORD_CHIRP, "record_chirp");
      this.registerModel(Items.RECORD_FAR, "record_far");
      this.registerModel(Items.RECORD_MALL, "record_mall");
      this.registerModel(Items.RECORD_MELLOHI, "record_mellohi");
      this.registerModel(Items.RECORD_STAL, "record_stal");
      this.registerModel(Items.RECORD_STRAD, "record_strad");
      this.registerModel(Items.RECORD_WARD, "record_ward");
      this.registerModel(Items.RECORD_11, "record_11");
      this.registerModel(Items.RECORD_WAIT, "record_wait");
      this.registerModel(Items.PRISMARINE_SHARD, "prismarine_shard");
      this.registerModel(Items.PRISMARINE_CRYSTALS, "prismarine_crystals");
      this.modelShaper.register(Items.ENCHANTED_BOOK, new ItemModelProvider() {
         @Override
         public ModelIdentifier provide(ItemStack stack) {
            return new ModelIdentifier("enchanted_book", "inventory");
         }
      });
      this.modelShaper.register(Items.FILLED_MAP, new ItemModelProvider() {
         @Override
         public ModelIdentifier provide(ItemStack stack) {
            return new ModelIdentifier("filled_map", "inventory");
         }
      });
      this.registerModel(Blocks.COMMAND_BLOCK, "command_block");
      this.registerModel(Items.FIREWORKS, "fireworks");
      this.registerModel(Items.COMMAND_BLOCK_MINECART, "command_block_minecart");
      this.registerModel(Blocks.BARRIER, "barrier");
      this.registerModel(Blocks.MOB_SPAWNER, "mob_spawner");
      this.registerModel(Items.WRITTEN_BOOK, "written_book");
      this.registerModel(Blocks.BROWN_MUSHROOM_BLOCK, MushroomBlock.Variant.ALL_INSIDE.getIndex(), "brown_mushroom_block");
      this.registerModel(Blocks.RED_MUSHROOM_BLOCK, MushroomBlock.Variant.ALL_INSIDE.getIndex(), "red_mushroom_block");
      this.registerModel(Blocks.DRAGON_EGG, "dragon_egg");
   }

   @Override
   public void reload(IResourceManager resourceManager) {
      this.modelShaper.rebuildCache();
   }
}
