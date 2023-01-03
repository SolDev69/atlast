package net.minecraft.client.resource.model;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.block.BlockModelShaper;
import net.minecraft.client.render.model.block.BlockElement;
import net.minecraft.client.render.model.block.BlockElementFace;
import net.minecraft.client.render.model.block.BlockModel;
import net.minecraft.client.render.model.block.BlockModelDefinition;
import net.minecraft.client.render.model.block.FaceBakery;
import net.minecraft.client.render.model.block.ItemModelGenerator;
import net.minecraft.client.render.model.block.ModelTransformations;
import net.minecraft.client.resource.IResource;
import net.minecraft.client.resource.ModelIdentifier;
import net.minecraft.client.resource.manager.IResourceManager;
import net.minecraft.client.texture.LoadTextureCallback;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.resource.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.MappedRegistry;
import net.minecraft.util.registry.Registry;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class ModelBakery {
   private static final Set UNREFERENCED_TEXTURES = Sets.newHashSet(
      new Identifier[]{
         new Identifier("blocks/water_flow"),
         new Identifier("blocks/water_still"),
         new Identifier("blocks/lava_flow"),
         new Identifier("blocks/lava_still"),
         new Identifier("blocks/destroy_stage_0"),
         new Identifier("blocks/destroy_stage_1"),
         new Identifier("blocks/destroy_stage_2"),
         new Identifier("blocks/destroy_stage_3"),
         new Identifier("blocks/destroy_stage_4"),
         new Identifier("blocks/destroy_stage_5"),
         new Identifier("blocks/destroy_stage_6"),
         new Identifier("blocks/destroy_stage_7"),
         new Identifier("blocks/destroy_stage_8"),
         new Identifier("blocks/destroy_stage_9"),
         new Identifier("items/empty_armor_slot_helmet"),
         new Identifier("items/empty_armor_slot_chestplate"),
         new Identifier("items/empty_armor_slot_leggings"),
         new Identifier("items/empty_armor_slot_boots")
      }
   );
   private static final Logger LOGGER = LogManager.getLogger();
   protected static final ModelIdentifier MISSING = new ModelIdentifier("builtin/missing", "missing");
   private static final Map BUILTIN = Maps.newHashMap();
   private static final Joiner PARENT_CHAIN_JOINER = Joiner.on(" -> ");
   private final IResourceManager resourceManager;
   private final Map textures = Maps.newHashMap();
   private final Map blockModels = Maps.newLinkedHashMap();
   private final Map variants = Maps.newLinkedHashMap();
   private final SpriteAtlasTexture blockAtlas;
   private final BlockModelShaper modelShaper;
   private final FaceBakery faceBakery = new FaceBakery();
   private final ItemModelGenerator itemModelGenerator = new ItemModelGenerator();
   private MappedRegistry bakedRegistry = new MappedRegistry();
   private static final BlockModel GENERATION_MARKER = BlockModel.fromJson(
      "{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}"
   );
   private static final BlockModel COMPASS_GENERATION_MARKER = BlockModel.fromJson(
      "{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}"
   );
   private static final BlockModel CLASS_GENERATION_MARKER = BlockModel.fromJson(
      "{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}"
   );
   private static final BlockModel BLOCK_ENTITY_MARKER = BlockModel.fromJson(
      "{\"elements\":[{  \"from\": [0, 0, 0],   \"to\": [16, 16, 16],   \"faces\": {       \"down\": {\"uv\": [0, 0, 16, 16], \"texture\":\"\"}   }}]}"
   );
   private Map itemModels = Maps.newLinkedHashMap();
   private final Map modelDefinitions = Maps.newHashMap();
   private Map itemVariants = Maps.newIdentityHashMap();

   public ModelBakery(IResourceManager resourceManager, SpriteAtlasTexture blockAtlas, BlockModelShaper modelShaper) {
      this.resourceManager = resourceManager;
      this.blockAtlas = blockAtlas;
      this.modelShaper = modelShaper;
   }

   public Registry getBakedModels() {
      this.loadBuiltIn();
      this.finalizeBlockModels();
      this.loadTextures();
      this.generateItemModels();
      this.bakeModels();
      return this.bakedRegistry;
   }

   private void loadBuiltIn() {
      this.registerVariants(this.modelShaper.getModels().provide().values());
      this.variants
         .put(
            MISSING,
            new BlockModelDefinition.MultiVariant(
               MISSING.getVariant(),
               Lists.newArrayList(
                  new BlockModelDefinition.Variant[]{new BlockModelDefinition.Variant(new Identifier(MISSING.getPath()), ModelRotation.X0_Y0, false, 1)}
               )
            )
         );
      Identifier var1 = new Identifier("item_frame");
      BlockModelDefinition var2 = this.loadDefinition(var1);
      this.registerVariant(var2, new ModelIdentifier(var1, "normal"));
      this.registerVariant(var2, new ModelIdentifier(var1, "map"));
      this.loadBlockModels();
      this.loadItemModels();
   }

   private void registerVariants(Collection ids) {
      for(ModelIdentifier var3 : ids) {
         try {
            BlockModelDefinition var4 = this.loadDefinition(var3);

            try {
               this.registerVariant(var4, var3);
            } catch (Exception var6) {
               LOGGER.warn("Unable to load variant: " + var3.getVariant() + " from " + var3);
            }
         } catch (Exception var7) {
            LOGGER.warn("Unable to load definition " + var3, var7);
         }
      }
   }

   private void registerVariant(BlockModelDefinition modelDefinition, ModelIdentifier id) {
      this.variants.put(id, modelDefinition.getVariant(id.getVariant()));
   }

   private BlockModelDefinition loadDefinition(Identifier id) {
      Identifier var2 = this.getBlockStatesJsonId(id);
      BlockModelDefinition var3 = (BlockModelDefinition)this.modelDefinitions.get(var2);
      if (var3 == null) {
         ArrayList var4 = Lists.newArrayList();

         try {
            for(IResource var6 : this.resourceManager.getResources(var2)) {
               InputStream var7 = null;

               try {
                  var7 = var6.asStream();
                  BlockModelDefinition var8 = BlockModelDefinition.fromJson(new InputStreamReader(var7, Charsets.UTF_8));
                  var4.add(var8);
               } catch (Exception var13) {
                  throw new RuntimeException(
                     "Encountered an exception when loading model definition of '"
                        + id
                        + "' from: '"
                        + var6.getId()
                        + "' in resourcepack: '"
                        + var6.getSourceName()
                        + "'",
                     var13
                  );
               } finally {
                  IOUtils.closeQuietly(var7);
               }
            }
         } catch (IOException var15) {
            throw new RuntimeException("Encountered an exception when loading model definition of model " + var2.toString(), var15);
         }

         var3 = new BlockModelDefinition((List)var4);
         this.modelDefinitions.put(var2, var3);
      }

      return var3;
   }

   private Identifier getBlockStatesJsonId(Identifier id) {
      return new Identifier(id.getNamespace(), "blockstates/" + id.getPath() + ".json");
   }

   private void loadBlockModels() {
      for(ModelIdentifier var2 : this.variants.keySet()) {
         for(BlockModelDefinition.Variant var4 : ((BlockModelDefinition.MultiVariant)this.variants.get(var2)).getVariants()) {
            Identifier var5 = var4.getId();
            if (this.blockModels.get(var5) == null) {
               try {
                  BlockModel var6 = this.loadBlockModel(var5);
                  this.blockModels.put(var5, var6);
               } catch (Exception var7) {
                  LOGGER.warn("Unable to load block model: '" + var5 + "' for variant: '" + var2 + "'", var7);
               }
            }
         }
      }
   }

   private BlockModel loadBlockModel(Identifier id) {
      String var3 = id.getPath();
      if ("builtin/generated".equals(var3)) {
         return GENERATION_MARKER;
      } else if ("builtin/compass".equals(var3)) {
         return COMPASS_GENERATION_MARKER;
      } else if ("builtin/clock".equals(var3)) {
         return CLASS_GENERATION_MARKER;
      } else if ("builtin/entity".equals(var3)) {
         return BLOCK_ENTITY_MARKER;
      } else {
         Object var2;
         if (var3.startsWith("builtin/")) {
            String var4 = var3.substring("builtin/".length());
            String var5 = (String)BUILTIN.get(var4);
            if (var5 == null) {
               throw new FileNotFoundException(id.toString());
            }

            var2 = new StringReader(var5);
         } else {
            IResource var9 = this.resourceManager.getResource(this.getModelsJsonId(id));
            var2 = new InputStreamReader(var9.asStream(), Charsets.UTF_8);
         }

         BlockModel var11;
         try {
            BlockModel var10 = BlockModel.fromJson((Reader)var2);
            var10.name = id.toString();
            var11 = var10;
         } finally {
            var2.close();
         }

         return var11;
      }
   }

   private Identifier getModelsJsonId(Identifier id) {
      return new Identifier(id.getNamespace(), "models/" + id.getPath() + ".json");
   }

   private void loadItemModels() {
      this.registerItemVariants();

      for(Item var2 : Item.REGISTRY) {
         for(String var5 : this.getItemVariants(var2)) {
            Identifier var6 = this.getItemModelId(var5);
            this.itemModels.put(var5, var6);
            if (this.blockModels.get(var6) == null) {
               try {
                  BlockModel var7 = this.loadBlockModel(var6);
                  this.blockModels.put(var6, var7);
               } catch (Exception var8) {
                  LOGGER.warn("Unable to load item model: '" + var6 + "' for item: '" + Item.REGISTRY.getKey(var2) + "'", var8);
               }
            }
         }
      }
   }

   private void registerItemVariants() {
      this.itemVariants
         .put(
            Item.byBlock(Blocks.STONE),
            Lists.newArrayList(new String[]{"stone", "granite", "granite_smooth", "diorite", "diorite_smooth", "andesite", "andesite_smooth"})
         );
      this.itemVariants.put(Item.byBlock(Blocks.DIRT), Lists.newArrayList(new String[]{"dirt", "coarse_dirt", "podzol"}));
      this.itemVariants
         .put(
            Item.byBlock(Blocks.PLANKS),
            Lists.newArrayList(new String[]{"oak_planks", "spruce_planks", "birch_planks", "jungle_planks", "acacia_planks", "dark_oak_planks"})
         );
      this.itemVariants
         .put(
            Item.byBlock(Blocks.SAPLING),
            Lists.newArrayList(new String[]{"oak_sapling", "spruce_sapling", "birch_sapling", "jungle_sapling", "acacia_sapling", "dark_oak_sapling"})
         );
      this.itemVariants.put(Item.byBlock(Blocks.SAND), Lists.newArrayList(new String[]{"sand", "red_sand"}));
      this.itemVariants.put(Item.byBlock(Blocks.LOG), Lists.newArrayList(new String[]{"oak_log", "spruce_log", "birch_log", "jungle_log"}));
      this.itemVariants.put(Item.byBlock(Blocks.LEAVES), Lists.newArrayList(new String[]{"oak_leaves", "spruce_leaves", "birch_leaves", "jungle_leaves"}));
      this.itemVariants.put(Item.byBlock(Blocks.SPONGE), Lists.newArrayList(new String[]{"sponge", "sponge_wet"}));
      this.itemVariants.put(Item.byBlock(Blocks.SANDSTONE), Lists.newArrayList(new String[]{"sandstone", "chiseled_sandstone", "smooth_sandstone"}));
      this.itemVariants.put(Item.byBlock(Blocks.TALLGRASS), Lists.newArrayList(new String[]{"dead_bush", "tall_grass", "fern"}));
      this.itemVariants.put(Item.byBlock(Blocks.DEADBUSH), Lists.newArrayList(new String[]{"dead_bush"}));
      this.itemVariants
         .put(
            Item.byBlock(Blocks.WOOL),
            Lists.newArrayList(
               new String[]{
                  "black_wool",
                  "red_wool",
                  "green_wool",
                  "brown_wool",
                  "blue_wool",
                  "purple_wool",
                  "cyan_wool",
                  "silver_wool",
                  "gray_wool",
                  "pink_wool",
                  "lime_wool",
                  "yellow_wool",
                  "light_blue_wool",
                  "magenta_wool",
                  "orange_wool",
                  "white_wool"
               }
            )
         );
      this.itemVariants.put(Item.byBlock(Blocks.YELLOW_FLOWER), Lists.newArrayList(new String[]{"dandelion"}));
      this.itemVariants
         .put(
            Item.byBlock(Blocks.RED_FLOWER),
            Lists.newArrayList(
               new String[]{"poppy", "blue_orchid", "allium", "houstonia", "red_tulip", "orange_tulip", "white_tulip", "pink_tulip", "oxeye_daisy"}
            )
         );
      this.itemVariants
         .put(
            Item.byBlock(Blocks.STONE_SLAB),
            Lists.newArrayList(
               new String[]{"stone_slab", "sandstone_slab", "cobblestone_slab", "brick_slab", "stone_brick_slab", "nether_brick_slab", "quartz_slab"}
            )
         );
      this.itemVariants
         .put(
            Item.byBlock(Blocks.STAINED_GLASS),
            Lists.newArrayList(
               new String[]{
                  "black_stained_glass",
                  "red_stained_glass",
                  "green_stained_glass",
                  "brown_stained_glass",
                  "blue_stained_glass",
                  "purple_stained_glass",
                  "cyan_stained_glass",
                  "silver_stained_glass",
                  "gray_stained_glass",
                  "pink_stained_glass",
                  "lime_stained_glass",
                  "yellow_stained_glass",
                  "light_blue_stained_glass",
                  "magenta_stained_glass",
                  "orange_stained_glass",
                  "white_stained_glass"
               }
            )
         );
      this.itemVariants
         .put(
            Item.byBlock(Blocks.MONSTER_EGG),
            Lists.newArrayList(
               new String[]{
                  "stone_monster_egg",
                  "cobblestone_monster_egg",
                  "stone_brick_monster_egg",
                  "mossy_brick_monster_egg",
                  "cracked_brick_monster_egg",
                  "chiseled_brick_monster_egg"
               }
            )
         );
      this.itemVariants
         .put(
            Item.byBlock(Blocks.STONE_BRICKS), Lists.newArrayList(new String[]{"stonebrick", "mossy_stonebrick", "cracked_stonebrick", "chiseled_stonebrick"})
         );
      this.itemVariants
         .put(
            Item.byBlock(Blocks.WOODEN_SLAB),
            Lists.newArrayList(new String[]{"oak_slab", "spruce_slab", "birch_slab", "jungle_slab", "acacia_slab", "dark_oak_slab"})
         );
      this.itemVariants.put(Item.byBlock(Blocks.COBBLESTONE_WALL), Lists.newArrayList(new String[]{"cobblestone_wall", "mossy_cobblestone_wall"}));
      this.itemVariants.put(Item.byBlock(Blocks.ANVIL), Lists.newArrayList(new String[]{"anvil_intact", "anvil_slightly_damaged", "anvil_very_damaged"}));
      this.itemVariants.put(Item.byBlock(Blocks.QUARTZ_BLOCK), Lists.newArrayList(new String[]{"quartz_block", "chiseled_quartz_block", "quartz_column"}));
      this.itemVariants
         .put(
            Item.byBlock(Blocks.STAINED_HARDENED_CLAY),
            Lists.newArrayList(
               new String[]{
                  "black_stained_hardened_clay",
                  "red_stained_hardened_clay",
                  "green_stained_hardened_clay",
                  "brown_stained_hardened_clay",
                  "blue_stained_hardened_clay",
                  "purple_stained_hardened_clay",
                  "cyan_stained_hardened_clay",
                  "silver_stained_hardened_clay",
                  "gray_stained_hardened_clay",
                  "pink_stained_hardened_clay",
                  "lime_stained_hardened_clay",
                  "yellow_stained_hardened_clay",
                  "light_blue_stained_hardened_clay",
                  "magenta_stained_hardened_clay",
                  "orange_stained_hardened_clay",
                  "white_stained_hardened_clay"
               }
            )
         );
      this.itemVariants
         .put(
            Item.byBlock(Blocks.STAINED_GLASS_PANE),
            Lists.newArrayList(
               new String[]{
                  "black_stained_glass_pane",
                  "red_stained_glass_pane",
                  "green_stained_glass_pane",
                  "brown_stained_glass_pane",
                  "blue_stained_glass_pane",
                  "purple_stained_glass_pane",
                  "cyan_stained_glass_pane",
                  "silver_stained_glass_pane",
                  "gray_stained_glass_pane",
                  "pink_stained_glass_pane",
                  "lime_stained_glass_pane",
                  "yellow_stained_glass_pane",
                  "light_blue_stained_glass_pane",
                  "magenta_stained_glass_pane",
                  "orange_stained_glass_pane",
                  "white_stained_glass_pane"
               }
            )
         );
      this.itemVariants.put(Item.byBlock(Blocks.LEAVES2), Lists.newArrayList(new String[]{"acacia_leaves", "dark_oak_leaves"}));
      this.itemVariants.put(Item.byBlock(Blocks.LOG2), Lists.newArrayList(new String[]{"acacia_log", "dark_oak_log"}));
      this.itemVariants.put(Item.byBlock(Blocks.PRISMARINE), Lists.newArrayList(new String[]{"prismarine", "prismarine_bricks", "dark_prismarine"}));
      this.itemVariants
         .put(
            Item.byBlock(Blocks.CARPET),
            Lists.newArrayList(
               new String[]{
                  "black_carpet",
                  "red_carpet",
                  "green_carpet",
                  "brown_carpet",
                  "blue_carpet",
                  "purple_carpet",
                  "cyan_carpet",
                  "silver_carpet",
                  "gray_carpet",
                  "pink_carpet",
                  "lime_carpet",
                  "yellow_carpet",
                  "light_blue_carpet",
                  "magenta_carpet",
                  "orange_carpet",
                  "white_carpet"
               }
            )
         );
      this.itemVariants
         .put(
            Item.byBlock(Blocks.DOUBLE_PLANT),
            Lists.newArrayList(new String[]{"sunflower", "syringa", "double_grass", "double_fern", "double_rose", "paeonia"})
         );
      this.itemVariants.put(Items.BOW, Lists.newArrayList(new String[]{"bow", "bow_pulling_0", "bow_pulling_1", "bow_pulling_2"}));
      this.itemVariants.put(Items.COAL, Lists.newArrayList(new String[]{"coal", "charcoal"}));
      this.itemVariants.put(Items.FISHING_ROD, Lists.newArrayList(new String[]{"fishing_rod", "fishing_rod_cast"}));
      this.itemVariants.put(Items.FISH, Lists.newArrayList(new String[]{"cod", "salmon", "clownfish", "pufferfish"}));
      this.itemVariants.put(Items.COOKED_FISH, Lists.newArrayList(new String[]{"cooked_cod", "cooked_salmon"}));
      this.itemVariants
         .put(
            Items.DYE,
            Lists.newArrayList(
               new String[]{
                  "dye_black",
                  "dye_red",
                  "dye_green",
                  "dye_brown",
                  "dye_blue",
                  "dye_purple",
                  "dye_cyan",
                  "dye_silver",
                  "dye_gray",
                  "dye_pink",
                  "dye_lime",
                  "dye_yellow",
                  "dye_light_blue",
                  "dye_magenta",
                  "dye_orange",
                  "dye_white"
               }
            )
         );
      this.itemVariants.put(Items.POTION, Lists.newArrayList(new String[]{"bottle_drinkable", "bottle_splash"}));
      this.itemVariants.put(Items.SKULL, Lists.newArrayList(new String[]{"skull_skeleton", "skull_wither", "skull_zombie", "skull_char", "skull_creeper"}));
   }

   private List getItemVariants(Item item) {
      List var2 = (List)this.itemVariants.get(item);
      if (var2 == null) {
         var2 = Collections.singletonList(((Identifier)Item.REGISTRY.getKey(item)).toString());
      }

      return var2;
   }

   private Identifier getItemModelId(String path) {
      Identifier var2 = new Identifier(path);
      return new Identifier(var2.getNamespace(), "item/" + var2.getPath());
   }

   private void bakeModels() {
      for(ModelIdentifier var2 : this.variants.keySet()) {
         WeightedBakedModel.Builder var3 = new WeightedBakedModel.Builder();
         int var4 = 0;

         for(BlockModelDefinition.Variant var6 : ((BlockModelDefinition.MultiVariant)this.variants.get(var2)).getVariants()) {
            BlockModel var7 = (BlockModel)this.blockModels.get(var6.getId());
            if (var7 != null && var7.isComplete()) {
               ++var4;
               var3.add(this.bake(var7, var6.gerRotation(), var6.isUvLocked()), var6.getWeight());
            } else {
               LOGGER.warn("Missing model for: " + var2);
            }
         }

         if (var4 == 0) {
            LOGGER.warn("No weighted models for: " + var2);
         } else if (var4 == 1) {
            this.bakedRegistry.put(var2, var3.first());
         } else {
            this.bakedRegistry.put(var2, var3.build());
         }
      }

      for(Entry var9 : this.itemModels.entrySet()) {
         Identifier var10 = (Identifier)var9.getValue();
         ModelIdentifier var11 = new ModelIdentifier((String)var9.getKey(), "inventory");
         BlockModel var12 = (BlockModel)this.blockModels.get(var10);
         if (var12 == null || !var12.isComplete()) {
            LOGGER.warn("Missing model for: " + var10);
         } else if (this.isBlockEntity(var12)) {
            this.bakedRegistry
               .put(var11, new BuiltInModel(new ModelTransformations(var12.m_81pqtuasw(), var12.m_10lvezxir(), var12.m_09toxmbvv(), var12.m_12mcmbtqy())));
         } else {
            this.bakedRegistry.put(var11, this.bake(var12, ModelRotation.X0_Y0, false));
         }
      }
   }

   private Set getBlockTextures() {
      HashSet var1 = Sets.newHashSet();
      ArrayList var2 = Lists.newArrayList(this.variants.keySet());
      Collections.sort(var2, new Comparator() {
         public int compare(ModelIdentifier c_54kqlxagv, ModelIdentifier c_54kqlxagv2) {
            return c_54kqlxagv.toString().compareTo(c_54kqlxagv2.toString());
         }
      });

      for(ModelIdentifier var4 : var2) {
         BlockModelDefinition.MultiVariant var5 = (BlockModelDefinition.MultiVariant)this.variants.get(var4);

         for(BlockModelDefinition.Variant var7 : var5.getVariants()) {
            BlockModel var8 = (BlockModel)this.blockModels.get(var7.getId());
            if (var8 == null) {
               LOGGER.warn("Missing model for: " + var4);
            } else {
               var1.addAll(this.getTextures(var8));
            }
         }
      }

      var1.addAll(UNREFERENCED_TEXTURES);
      return var1;
   }

   private BakedModel bake(BlockModel model, ModelRotation rotation, boolean uvLock) {
      TextureAtlasSprite var4 = (TextureAtlasSprite)this.textures.get(new Identifier(model.getTexture("particle")));
      BasicBakedModel.Builder var5 = new BasicBakedModel.Builder(model).particleIcon(var4);

      for(BlockElement var7 : model.getElements()) {
         for(Direction var9 : var7.faces.keySet()) {
            BlockElementFace var10 = (BlockElementFace)var7.faces.get(var9);
            TextureAtlasSprite var11 = (TextureAtlasSprite)this.textures.get(new Identifier(model.getTexture(var10.texture)));
            if (var10.cullFace == null) {
               var5.unculledFace(this.bakeFace(var7, var10, var11, var9, rotation, uvLock));
            } else {
               var5.culledFace(rotation.apply(var10.cullFace), this.bakeFace(var7, var10, var11, var9, rotation, uvLock));
            }
         }
      }

      return var5.build();
   }

   private BakedQuad bakeFace(
      BlockElement element, BlockElementFace elementFace, TextureAtlasSprite blockSprite, Direction face, ModelRotation rotation, boolean uvLock
   ) {
      return this.faceBakery.bake(element.from, element.to, elementFace, blockSprite, face, rotation, element.rotation, uvLock, element.shade);
   }

   private void finalizeBlockModels() {
      this.loadBlockModelDependencies();

      for(BlockModel var2 : this.blockModels.values()) {
         var2.findParent(this.blockModels);
      }

      BlockModel.checkHierarchy(this.blockModels);
   }

   private void loadBlockModelDependencies() {
      ArrayDeque var1 = Queues.newArrayDeque();
      HashSet var2 = Sets.newHashSet();

      for(Identifier var4 : this.blockModels.keySet()) {
         var2.add(var4);
         Identifier var5 = ((BlockModel)this.blockModels.get(var4)).getParentId();
         if (var5 != null) {
            var1.add(var5);
         }
      }

      while(!var1.isEmpty()) {
         Identifier var7 = (Identifier)var1.pop();

         try {
            if (this.blockModels.get(var7) != null) {
               continue;
            }

            BlockModel var8 = this.loadBlockModel(var7);
            this.blockModels.put(var7, var8);
            Identifier var9 = var8.getParentId();
            if (var9 != null && !var2.contains(var9)) {
               var1.add(var9);
            }
         } catch (Exception var6) {
            LOGGER.warn("In parent chain: " + PARENT_CHAIN_JOINER.join(this.getBlockModelDependencies(var7)) + "; unable to load model: '" + var7 + "'", var6);
         }

         var2.add(var7);
      }
   }

   private List getBlockModelDependencies(Identifier id) {
      ArrayList var2 = Lists.newArrayList(new Identifier[]{id});
      Identifier var3 = id;

      while((var3 = this.findParentBlockModel(var3)) != null) {
         var2.add(0, var3);
      }

      return var2;
   }

   private Identifier findParentBlockModel(Identifier id) {
      for(Entry var3 : this.blockModels.entrySet()) {
         BlockModel var4 = (BlockModel)var3.getValue();
         if (var4 != null && id.equals(var4.getParentId())) {
            return (Identifier)var3.getKey();
         }
      }

      return null;
   }

   private Set getTextures(BlockModel model) {
      HashSet var2 = Sets.newHashSet();

      for(BlockElement var4 : model.getElements()) {
         for(BlockElementFace var6 : var4.faces.values()) {
            Identifier var7 = new Identifier(model.getTexture(var6.texture));
            var2.add(var7);
         }
      }

      var2.add(new Identifier(model.getTexture("particle")));
      return var2;
   }

   private void loadTextures() {
      final Set var1 = this.getBlockTextures();
      var1.addAll(this.getItemTextures());
      var1.remove(SpriteAtlasTexture.f_42ttyyeqx);
      LoadTextureCallback var2 = new LoadTextureCallback() {
         @Override
         public void onTextureLoaded(SpriteAtlasTexture atlas) {
            for(Identifier var3 : var1) {
               TextureAtlasSprite var4 = atlas.m_91bydfggl(var3);
               ModelBakery.this.textures.put(var3, var4);
            }
         }
      };
      this.blockAtlas.m_16toxtqos(this.resourceManager, var2);
      this.textures.put(new Identifier("missingno"), this.blockAtlas.m_92lyecmxz());
   }

   private Set getItemTextures() {
      HashSet var1 = Sets.newHashSet();

      for(Identifier var3 : this.itemModels.values()) {
         BlockModel var4 = (BlockModel)this.blockModels.get(var3);
         if (var4 != null) {
            var1.add(new Identifier(var4.getTexture("particle")));
            if (this.isGeneration(var4)) {
               for(String var11 : ItemModelGenerator.LAYERS) {
                  Identifier var12 = new Identifier(var4.getTexture(var11));
                  if (var4.getRoot() == COMPASS_GENERATION_MARKER && !SpriteAtlasTexture.f_42ttyyeqx.equals(var12)) {
                     TextureAtlasSprite.m_41puehtak(var12.toString());
                  } else if (var4.getRoot() == CLASS_GENERATION_MARKER && !SpriteAtlasTexture.f_42ttyyeqx.equals(var12)) {
                     TextureAtlasSprite.m_41txmgmed(var12.toString());
                  }

                  var1.add(var12);
               }
            } else if (!this.isBlockEntity(var4)) {
               for(BlockElement var6 : var4.getElements()) {
                  for(BlockElementFace var8 : var6.faces.values()) {
                     Identifier var9 = new Identifier(var4.getTexture(var8.texture));
                     var1.add(var9);
                  }
               }
            }
         }
      }

      return var1;
   }

   private boolean isGeneration(BlockModel model) {
      if (model == null) {
         return false;
      } else {
         BlockModel var2 = model.getRoot();
         return var2 == GENERATION_MARKER || var2 == COMPASS_GENERATION_MARKER || var2 == CLASS_GENERATION_MARKER;
      }
   }

   private boolean isBlockEntity(BlockModel model) {
      if (model == null) {
         return false;
      } else {
         BlockModel var2 = model.getRoot();
         return var2 == BLOCK_ENTITY_MARKER;
      }
   }

   private void generateItemModels() {
      for(Identifier var2 : this.itemModels.values()) {
         BlockModel var3 = (BlockModel)this.blockModels.get(var2);
         if (this.isGeneration(var3)) {
            BlockModel var4 = this.generateItemModels(var3);
            if (var4 != null) {
               var4.name = var2.toString();
            }

            this.blockModels.put(var2, var4);
         } else if (this.isBlockEntity(var3)) {
            this.blockModels.put(var2, var3);
         }
      }

      for(TextureAtlasSprite var6 : this.textures.values()) {
         if (!var6.hasMeta()) {
            var6.clearFrames();
         }
      }
   }

   private BlockModel generateItemModels(BlockModel model) {
      return this.itemModelGenerator.generate(this.blockAtlas, model);
   }

   static {
      BUILTIN.put(
         "missing",
         "{ \"textures\": {   \"particle\": \"missingno\",   \"missingno\": \"missingno\"}, \"elements\": [ {     \"from\": [ 0, 0, 0 ],     \"to\": [ 16, 16, 16 ],     \"faces\": {         \"down\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"down\", \"texture\": \"#missingno\" },         \"up\":    { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"up\", \"texture\": \"#missingno\" },         \"north\": { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"north\", \"texture\": \"#missingno\" },         \"south\": { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"south\", \"texture\": \"#missingno\" },         \"west\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"west\", \"texture\": \"#missingno\" },         \"east\":  { \"uv\": [ 0, 0, 16, 16 ], \"cullface\": \"east\", \"texture\": \"#missingno\" }    }}]}"
      );
      GENERATION_MARKER.name = "generation marker";
      COMPASS_GENERATION_MARKER.name = "compass generation marker";
      CLASS_GENERATION_MARKER.name = "class generation marker";
      BLOCK_ENTITY_MARKER.name = "block entity marker";
   }
}
