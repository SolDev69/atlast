package net.minecraft.item;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirtBlock;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.InfestedBlock;
import net.minecraft.block.PlanksBlock;
import net.minecraft.block.PrismarineBlock;
import net.minecraft.block.SandBlock;
import net.minecraft.block.SandstoneBlock;
import net.minecraft.block.StoneBlock;
import net.minecraft.block.StonebrickBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.PaintingEntity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.effect.PotionHelper;
import net.minecraft.entity.living.effect.StatusEffect;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.locale.I18n;
import net.minecraft.resource.Identifier;
import net.minecraft.util.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.IdRegistry;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class Item {
   public static final IdRegistry REGISTRY = new IdRegistry();
   private static final Map BLOCK_ITEMS = Maps.newHashMap();
   protected static final UUID ATTACK_DAMAGE_MODIFIER_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
   private ItemGroup group;
   protected static Random random = new Random();
   protected int maxStackSize = 64;
   private int maxDamage;
   protected boolean handheld;
   protected boolean stackable;
   private Item recipeRemainder;
   private String brewingRecipe;
   private String id;

   public static int getRawId(Item item) {
      return item == null ? 0 : REGISTRY.getId(item);
   }

   public static Item byRawId(int id) {
      return (Item)REGISTRY.get(id);
   }

   public static Item byBlock(Block block) {
      return (Item)BLOCK_ITEMS.get(block);
   }

   public static Item byId(String id) {
      Item var1 = (Item)REGISTRY.get(new Identifier(id));
      if (var1 == null) {
         try {
            return byRawId(Integer.parseInt(id));
         } catch (NumberFormatException var3) {
         }
      }

      return var1;
   }

   public Item setMaxStackSize(int size) {
      this.maxStackSize = size;
      return this;
   }

   public boolean use(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction face, float dx, float dy, float dz) {
      return false;
   }

   public float getMiningSpeed(ItemStack stack, Block block) {
      return 1.0F;
   }

   public ItemStack startUsing(ItemStack stack, World world, PlayerEntity player) {
      return stack;
   }

   public ItemStack finishUsing(ItemStack stack, World world, PlayerEntity player) {
      return stack;
   }

   public int getMaxStackSize() {
      return this.maxStackSize;
   }

   public int getBlockMetadata(int metadata) {
      return 0;
   }

   public boolean isStackable() {
      return this.stackable;
   }

   protected Item setStackable(boolean stackable) {
      this.stackable = stackable;
      return this;
   }

   public int getMaxDamage() {
      return this.maxDamage;
   }

   protected Item setMaxDamage(int damage) {
      this.maxDamage = damage;
      return this;
   }

   public boolean isDamageable() {
      return this.maxDamage > 0 && !this.stackable;
   }

   public boolean attackEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      return false;
   }

   public boolean mineBlock(ItemStack stack, World world, Block block, BlockPos pos, LivingEntity entity) {
      return false;
   }

   public boolean canEffectivelyMine(Block block) {
      return false;
   }

   public boolean canInteract(ItemStack stack, PlayerEntity player, LivingEntity entity) {
      return false;
   }

   public Item setHandheld() {
      this.handheld = true;
      return this;
   }

   @Environment(EnvType.CLIENT)
   public boolean isHandheld() {
      return this.handheld;
   }

   @Environment(EnvType.CLIENT)
   public boolean shouldRotate() {
      return false;
   }

   public Item setId(String id) {
      this.id = id;
      return this;
   }

   public String getDescription(ItemStack stack) {
      String var2 = this.getTranslationKey(stack);
      return var2 == null ? "" : I18n.translate(var2);
   }

   public String getTranslationKey() {
      return "item." + this.id;
   }

   public String getTranslationKey(ItemStack stack) {
      return "item." + this.id;
   }

   public Item setRecipeRemainder(Item item) {
      this.recipeRemainder = item;
      return this;
   }

   public boolean shouldSyncNbt() {
      return true;
   }

   public Item getRecipeRemainder() {
      return this.recipeRemainder;
   }

   public boolean hasRecipeRemainder() {
      return this.recipeRemainder != null;
   }

   @Environment(EnvType.CLIENT)
   public int getDisplayColor(ItemStack stack, int color) {
      return 16777215;
   }

   public void tick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
   }

   public void onResult(ItemStack stack, World world, PlayerEntity player) {
   }

   public boolean isNetworkSynced() {
      return false;
   }

   public UseAction getUseAction(ItemStack stack) {
      return UseAction.NONE;
   }

   public int getUseDuration(ItemStack stack) {
      return 0;
   }

   public void stopUsing(ItemStack stack, World world, PlayerEntity player, int remainingUseTime) {
   }

   protected Item setBrewingRecipe(String recipe) {
      this.brewingRecipe = recipe;
      return this;
   }

   public String getBrewingRecipe(ItemStack stack) {
      return this.brewingRecipe;
   }

   public boolean hasBrewingRecipe(ItemStack stack) {
      return this.getBrewingRecipe(stack) != null;
   }

   @Environment(EnvType.CLIENT)
   public void addHoverText(ItemStack stack, PlayerEntity player, List tooltip, boolean advanced) {
   }

   public String getName(ItemStack stack) {
      return ("" + I18n.translate(this.getDescription(stack) + ".name")).trim();
   }

   @Environment(EnvType.CLIENT)
   public boolean hasEnchantmentGlint(ItemStack stack) {
      return stack.hasEnchantments();
   }

   public Rarity getRarity(ItemStack stack) {
      return stack.hasEnchantments() ? Rarity.RARE : Rarity.COMMON;
   }

   public boolean isEnchantable(ItemStack stack) {
      return this.getMaxStackSize() == 1 && this.isDamageable();
   }

   protected HitResult getUseTarget(World world, PlayerEntity player, boolean allowLiquids) {
      float var4 = player.prevPitch + (player.pitch - player.prevPitch);
      float var5 = player.prevYaw + (player.yaw - player.prevYaw);
      double var6 = player.prevX + (player.x - player.prevX);
      double var8 = player.prevY + (player.y - player.prevY) + (double)player.getEyeHeight();
      double var10 = player.prevZ + (player.z - player.prevZ);
      Vec3d var12 = new Vec3d(var6, var8, var10);
      float var13 = MathHelper.cos(-var5 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float var14 = MathHelper.sin(-var5 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float var15 = -MathHelper.cos(-var4 * (float) (Math.PI / 180.0));
      float var16 = MathHelper.sin(-var4 * (float) (Math.PI / 180.0));
      float var17 = var14 * var15;
      float var19 = var13 * var15;
      double var20 = 5.0;
      Vec3d var22 = var12.add((double)var17 * var20, (double)var16 * var20, (double)var19 * var20);
      return world.rayTrace(var12, var22, allowLiquids, !allowLiquids, false);
   }

   public int getEnchantability() {
      return 0;
   }

   @Environment(EnvType.CLIENT)
   public void addToCreativeMenu(Item item, ItemGroup group, List list) {
      list.add(new ItemStack(item, 1, 0));
   }

   @Environment(EnvType.CLIENT)
   public ItemGroup getItemGroup() {
      return this.group;
   }

   public Item setItemGroup(ItemGroup group) {
      this.group = group;
      return this;
   }

   public boolean canAlwaysUse() {
      return false;
   }

   public boolean isReparable(ItemStack stack, ItemStack ingredient) {
      return false;
   }

   public Multimap getDefaultAttributeModifiers() {
      return HashMultimap.create();
   }

   public static void init() {
      register(Blocks.STONE, new VariantBlockItem(Blocks.STONE, Blocks.STONE, new Function() {
         public String apply(ItemStack c_72owraavl) {
            return StoneBlock.Variant.byIndex(c_72owraavl.getMetadata()).getName();
         }
      }).setId("stone"));
      register(Blocks.GRASS, new GrassBlockItem(Blocks.GRASS, true));
      register(Blocks.DIRT, new VariantBlockItem(Blocks.DIRT, Blocks.DIRT, new Function() {
         public String apply(ItemStack c_72owraavl) {
            return DirtBlock.Variant.byIndex(c_72owraavl.getMetadata()).getName();
         }
      }).setId("dirt"));
      register(Blocks.COBBLESTONE);
      register(Blocks.PLANKS, new VariantBlockItem(Blocks.PLANKS, Blocks.PLANKS, new Function() {
         public String apply(ItemStack c_72owraavl) {
            return PlanksBlock.Variant.byIndex(c_72owraavl.getMetadata()).getName();
         }
      }).setId("wood"));
      register(Blocks.SAPLING, new VariantBlockItem(Blocks.SAPLING, Blocks.SAPLING, new Function() {
         public String apply(ItemStack c_72owraavl) {
            return PlanksBlock.Variant.byIndex(c_72owraavl.getMetadata()).getName();
         }
      }).setId("sapling"));
      register(Blocks.BEDROCK);
      register(Blocks.SAND, new VariantBlockItem(Blocks.SAND, Blocks.SAND, new Function() {
         public String apply(ItemStack c_72owraavl) {
            return SandBlock.Variant.byIndex(c_72owraavl.getMetadata()).getName();
         }
      }).setId("sand"));
      register(Blocks.GRAVEL);
      register(Blocks.GOLD_ORE);
      register(Blocks.IRON_ORE);
      register(Blocks.COAL_ORE);
      register(Blocks.LOG, new VariantBlockItem(Blocks.LOG, Blocks.LOG, new Function() {
         public String apply(ItemStack c_72owraavl) {
            return PlanksBlock.Variant.byIndex(c_72owraavl.getMetadata()).getName();
         }
      }).setId("log"));
      register(Blocks.LOG2, new VariantBlockItem(Blocks.LOG2, Blocks.LOG2, new Function() {
         public String apply(ItemStack c_72owraavl) {
            return PlanksBlock.Variant.byIndex(c_72owraavl.getMetadata() + 4).getName();
         }
      }).setId("log"));
      register(Blocks.LEAVES, new LeavesItem(Blocks.LEAVES).setId("leaves"));
      register(Blocks.LEAVES2, new LeavesItem(Blocks.LEAVES2).setId("leaves"));
      register(Blocks.SPONGE, new VariantBlockItem(Blocks.SPONGE, Blocks.SPONGE, new Function() {
         public String apply(ItemStack c_72owraavl) {
            return (c_72owraavl.getMetadata() & 1) == 1 ? "wet" : "dry";
         }
      }).setId("sponge"));
      register(Blocks.GLASS);
      register(Blocks.LAPIS_ORE);
      register(Blocks.LAPIS_BLOCK);
      register(Blocks.DISPENSER);
      register(Blocks.SANDSTONE, new VariantBlockItem(Blocks.SANDSTONE, Blocks.SANDSTONE, new Function() {
         public String apply(ItemStack c_72owraavl) {
            return SandstoneBlock.Type.byIndex(c_72owraavl.getMetadata()).getName();
         }
      }).setId("sandStone"));
      register(Blocks.NOTEBLOCK);
      register(Blocks.POWERED_RAIL);
      register(Blocks.DETECTOR_RAIL);
      register(Blocks.STICKY_PISTON, new StickyPistonBlockItem(Blocks.STICKY_PISTON));
      register(Blocks.WEB);
      register(Blocks.TALLGRASS, new GrassBlockItem(Blocks.TALLGRASS, true).setNames(new String[]{"shrub", "grass", "fern"}));
      register(Blocks.DEADBUSH);
      register(Blocks.PISTON, new StickyPistonBlockItem(Blocks.PISTON));
      register(Blocks.WOOL, new WoolItem(Blocks.WOOL).setId("cloth"));
      register(Blocks.YELLOW_FLOWER, new VariantBlockItem(Blocks.YELLOW_FLOWER, Blocks.YELLOW_FLOWER, new Function() {
         public String apply(ItemStack c_72owraavl) {
            return FlowerBlock.Type.byIndex(FlowerBlock.Group.YELLOW, c_72owraavl.getMetadata()).getName();
         }
      }).setId("flower"));
      register(Blocks.RED_FLOWER, new VariantBlockItem(Blocks.RED_FLOWER, Blocks.RED_FLOWER, new Function() {
         public String apply(ItemStack c_72owraavl) {
            return FlowerBlock.Type.byIndex(FlowerBlock.Group.RED, c_72owraavl.getMetadata()).getName();
         }
      }).setId("rose"));
      register(Blocks.BROWN_MUSHROOM);
      register(Blocks.RED_MUSHROOM);
      register(Blocks.GOLD_BLOCK);
      register(Blocks.IRON_BLOCK);
      register(Blocks.STONE_SLAB, new StoneSlabItem(Blocks.STONE_SLAB, Blocks.STONE_SLAB, Blocks.DOUBLE_STONE_SLAB).setId("stoneSlab"));
      register(Blocks.BRICKS);
      register(Blocks.TNT);
      register(Blocks.BOOKSHELF);
      register(Blocks.MOSSY_COBBLESTONE);
      register(Blocks.OBSIDIAN);
      register(Blocks.TORCH);
      register(Blocks.MOB_SPAWNER);
      register(Blocks.OAK_STAIRS);
      register(Blocks.CHEST);
      register(Blocks.DIAMOND_ORE);
      register(Blocks.DIAMOND_BLOCK);
      register(Blocks.CRAFTING_TABLE);
      register(Blocks.FARMLAND);
      register(Blocks.FURNACE);
      register(Blocks.LIT_FURNACE);
      register(Blocks.LADDER);
      register(Blocks.RAIL);
      register(Blocks.STONE_STAIRS);
      register(Blocks.LEVER);
      register(Blocks.STONE_PRESSURE_PLATE);
      register(Blocks.WOODEN_PRESSURE_PLATE);
      register(Blocks.REDSTONE_ORE);
      register(Blocks.REDSTONE_TORCH);
      register(Blocks.STONE_BUTTON);
      register(Blocks.SNOW_LAYER, new SnowLayerItem(Blocks.SNOW_LAYER));
      register(Blocks.ICE);
      register(Blocks.SNOW);
      register(Blocks.CACTUS);
      register(Blocks.CLAY);
      register(Blocks.JUKEBOX);
      register(Blocks.FENCE);
      register(Blocks.PUMPKIN);
      register(Blocks.NETHERRACK);
      register(Blocks.SOUL_SAND);
      register(Blocks.GLOWSTONE);
      register(Blocks.LIT_PUMPKIN);
      register(Blocks.TRAPDOOR);
      register(Blocks.MONSTER_EGG, new VariantBlockItem(Blocks.MONSTER_EGG, Blocks.MONSTER_EGG, new Function() {
         public String apply(ItemStack c_72owraavl) {
            return InfestedBlock.Variant.byIndex(c_72owraavl.getMetadata()).getName();
         }
      }).setId("monsterStoneEgg"));
      register(Blocks.STONE_BRICKS, new VariantBlockItem(Blocks.STONE_BRICKS, Blocks.STONE_BRICKS, new Function() {
         public String apply(ItemStack c_72owraavl) {
            return StonebrickBlock.Variant.byIndex(c_72owraavl.getMetadata()).getName();
         }
      }).setId("stonebricksmooth"));
      register(Blocks.BROWN_MUSHROOM_BLOCK);
      register(Blocks.RED_MUSHROOM_BLOCK);
      register(Blocks.IRON_BARS);
      register(Blocks.GLASS_PANE);
      register(Blocks.MELON_BLOCK);
      register(Blocks.VINE, new GrassBlockItem(Blocks.VINE, false));
      register(Blocks.FENCE_GATE);
      register(Blocks.BRICK_STAIRS);
      register(Blocks.STONE_BRICK_STAIRS);
      register(Blocks.MYCELIUM);
      register(Blocks.LILY_PAD, new LilyPadItem(Blocks.LILY_PAD));
      register(Blocks.NETHER_BRICKS);
      register(Blocks.NETHER_BRICK_FENCE);
      register(Blocks.NETHER_BRICK_STAIRS);
      register(Blocks.ENCHANTING_TABLE);
      register(Blocks.END_PORTAL_FRAME);
      register(Blocks.END_STONE);
      register(Blocks.DRAGON_EGG);
      register(Blocks.REDSTONE_LAMP);
      register(Blocks.WOODEN_SLAB, new StoneSlabItem(Blocks.WOODEN_SLAB, Blocks.WOODEN_SLAB, Blocks.DOUBLE_WOODEN_SLAB).setId("woodSlab"));
      register(Blocks.SANDSTONE_STAIRS);
      register(Blocks.EMERALD_ORE);
      register(Blocks.ENDER_CHEST);
      register(Blocks.TRIPWIRE_HOOK);
      register(Blocks.EMERALD_BLOCK);
      register(Blocks.SPRUCE_STAIRS);
      register(Blocks.BIRCH_STAIRS);
      register(Blocks.JUNGLE_STAIRS);
      register(Blocks.COMMAND_BLOCK);
      register(Blocks.BEACON);
      register(Blocks.COBBLESTONE_WALL, new VariantBlockItem(Blocks.COBBLESTONE_WALL, Blocks.COBBLESTONE_WALL, new Function() {
         public String apply(ItemStack c_72owraavl) {
            return WallBlock.Variant.byIndex(c_72owraavl.getMetadata()).getName();
         }
      }).setId("cobbleWall"));
      register(Blocks.WOODEN_BUTTON);
      register(Blocks.ANVIL, new AnvilItem(Blocks.ANVIL).setId("anvil"));
      register(Blocks.TRAPPED_CHEST);
      register(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);
      register(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
      register(Blocks.DAYLIGHT_DETECTOR);
      register(Blocks.REDSTONE_BLOCK);
      register(Blocks.QUARTZ_ORE);
      register(Blocks.HOPPER);
      register(
         Blocks.QUARTZ_BLOCK, new VariantBlockItem(Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_BLOCK, new String[]{"default", "chiseled", "lines"}).setId("quartzBlock")
      );
      register(Blocks.QUARTZ_STAIRS);
      register(Blocks.ACTIVATOR_RAIL);
      register(Blocks.DROPPER);
      register(Blocks.STAINED_HARDENED_CLAY, new WoolItem(Blocks.STAINED_HARDENED_CLAY).setId("clayHardenedStained"));
      register(Blocks.BARRIER);
      register(Blocks.IRON_TRAPDOOR);
      register(Blocks.HAY);
      register(Blocks.CARPET, new WoolItem(Blocks.CARPET).setId("woolCarpet"));
      register(Blocks.HARDENED_CLAY);
      register(Blocks.COAL_BLOCK);
      register(Blocks.PACKED_ICE);
      register(Blocks.ACACIA_STAIRS);
      register(Blocks.DARK_OAK_STAIRS);
      register(Blocks.SLIME);
      register(Blocks.DOUBLE_PLANT, new BushItem(Blocks.DOUBLE_PLANT, Blocks.DOUBLE_PLANT, new Function() {
         public String apply(ItemStack c_72owraavl) {
            return DoublePlantBlock.Variant.byIndex(c_72owraavl.getMetadata()).getName();
         }
      }).setId("doublePlant"));
      register(Blocks.STAINED_GLASS, new WoolItem(Blocks.STAINED_GLASS).setId("stainedGlass"));
      register(Blocks.STAINED_GLASS_PANE, new WoolItem(Blocks.STAINED_GLASS_PANE).setId("stainedGlassPane"));
      register(Blocks.PRISMARINE, new VariantBlockItem(Blocks.PRISMARINE, Blocks.PRISMARINE, new Function() {
         public String apply(ItemStack c_72owraavl) {
            return PrismarineBlock.Variant.byIndex(c_72owraavl.getMetadata()).getName();
         }
      }).setId("prismarine"));
      register(Blocks.SEA_LANTERN);
      register(256, "iron_shovel", new ShovelItem(Item.ToolMaterial.IRON).setId("shovelIron"));
      register(257, "iron_pickaxe", new PickaxeItem(Item.ToolMaterial.IRON).setId("pickaxeIron"));
      register(258, "iron_axe", new AxeItem(Item.ToolMaterial.IRON).setId("hatchetIron"));
      register(259, "flint_and_steel", new FlintAndSteelItem().setId("flintAndSteel"));
      register(260, "apple", new FoodItem(4, 0.3F, false).setId("apple"));
      register(261, "bow", new BowItem().setId("bow"));
      register(262, "arrow", new Item().setId("arrow").setItemGroup(ItemGroup.COMBAT));
      register(263, "coal", new CoalItem().setId("coal"));
      register(264, "diamond", new Item().setId("diamond").setItemGroup(ItemGroup.MATERIALS));
      register(265, "iron_ingot", new Item().setId("ingotIron").setItemGroup(ItemGroup.MATERIALS));
      register(266, "gold_ingot", new Item().setId("ingotGold").setItemGroup(ItemGroup.MATERIALS));
      register(267, "iron_sword", new SwordItem(Item.ToolMaterial.IRON).setId("swordIron"));
      register(268, "wooden_sword", new SwordItem(Item.ToolMaterial.WOOD).setId("swordWood"));
      register(269, "wooden_shovel", new ShovelItem(Item.ToolMaterial.WOOD).setId("shovelWood"));
      register(270, "wooden_pickaxe", new PickaxeItem(Item.ToolMaterial.WOOD).setId("pickaxeWood"));
      register(271, "wooden_axe", new AxeItem(Item.ToolMaterial.WOOD).setId("hatchetWood"));
      register(272, "stone_sword", new SwordItem(Item.ToolMaterial.STONE).setId("swordStone"));
      register(273, "stone_shovel", new ShovelItem(Item.ToolMaterial.STONE).setId("shovelStone"));
      register(274, "stone_pickaxe", new PickaxeItem(Item.ToolMaterial.STONE).setId("pickaxeStone"));
      register(275, "stone_axe", new AxeItem(Item.ToolMaterial.STONE).setId("hatchetStone"));
      register(276, "diamond_sword", new SwordItem(Item.ToolMaterial.DIAMOND).setId("swordDiamond"));
      register(277, "diamond_shovel", new ShovelItem(Item.ToolMaterial.DIAMOND).setId("shovelDiamond"));
      register(278, "diamond_pickaxe", new PickaxeItem(Item.ToolMaterial.DIAMOND).setId("pickaxeDiamond"));
      register(279, "diamond_axe", new AxeItem(Item.ToolMaterial.DIAMOND).setId("hatchetDiamond"));
      register(280, "stick", new Item().setHandheld().setId("stick").setItemGroup(ItemGroup.MATERIALS));
      register(281, "bowl", new Item().setId("bowl").setItemGroup(ItemGroup.MATERIALS));
      register(282, "mushroom_stew", new StewItem(6).setId("mushroomStew"));
      register(283, "golden_sword", new SwordItem(Item.ToolMaterial.GOLD).setId("swordGold"));
      register(284, "golden_shovel", new ShovelItem(Item.ToolMaterial.GOLD).setId("shovelGold"));
      register(285, "golden_pickaxe", new PickaxeItem(Item.ToolMaterial.GOLD).setId("pickaxeGold"));
      register(286, "golden_axe", new AxeItem(Item.ToolMaterial.GOLD).setId("hatchetGold"));
      register(287, "string", new PlaceableItem(Blocks.TRIPWIRE).setId("string").setItemGroup(ItemGroup.MATERIALS));
      register(288, "feather", new Item().setId("feather").setItemGroup(ItemGroup.MATERIALS));
      register(289, "gunpowder", new Item().setId("sulphur").setBrewingRecipe(PotionHelper.GUNPOWDER).setItemGroup(ItemGroup.MATERIALS));
      register(290, "wooden_hoe", new HoeItem(Item.ToolMaterial.WOOD).setId("hoeWood"));
      register(291, "stone_hoe", new HoeItem(Item.ToolMaterial.STONE).setId("hoeStone"));
      register(292, "iron_hoe", new HoeItem(Item.ToolMaterial.IRON).setId("hoeIron"));
      register(293, "diamond_hoe", new HoeItem(Item.ToolMaterial.DIAMOND).setId("hoeDiamond"));
      register(294, "golden_hoe", new HoeItem(Item.ToolMaterial.GOLD).setId("hoeGold"));
      register(295, "wheat_seeds", new WheatSeedsItem(Blocks.WHEAT, Blocks.FARMLAND).setId("seeds"));
      register(296, "wheat", new Item().setId("wheat").setItemGroup(ItemGroup.MATERIALS));
      register(297, "bread", new FoodItem(5, 0.6F, false).setId("bread"));
      register(298, "leather_helmet", new ArmorItem(ArmorItem.Material.CLOTH, 0, 0).setId("helmetCloth"));
      register(299, "leather_chestplate", new ArmorItem(ArmorItem.Material.CLOTH, 0, 1).setId("chestplateCloth"));
      register(300, "leather_leggings", new ArmorItem(ArmorItem.Material.CLOTH, 0, 2).setId("leggingsCloth"));
      register(301, "leather_boots", new ArmorItem(ArmorItem.Material.CLOTH, 0, 3).setId("bootsCloth"));
      register(302, "chainmail_helmet", new ArmorItem(ArmorItem.Material.CHAIN, 1, 0).setId("helmetChain"));
      register(303, "chainmail_chestplate", new ArmorItem(ArmorItem.Material.CHAIN, 1, 1).setId("chestplateChain"));
      register(304, "chainmail_leggings", new ArmorItem(ArmorItem.Material.CHAIN, 1, 2).setId("leggingsChain"));
      register(305, "chainmail_boots", new ArmorItem(ArmorItem.Material.CHAIN, 1, 3).setId("bootsChain"));
      register(306, "iron_helmet", new ArmorItem(ArmorItem.Material.IRON, 2, 0).setId("helmetIron"));
      register(307, "iron_chestplate", new ArmorItem(ArmorItem.Material.IRON, 2, 1).setId("chestplateIron"));
      register(308, "iron_leggings", new ArmorItem(ArmorItem.Material.IRON, 2, 2).setId("leggingsIron"));
      register(309, "iron_boots", new ArmorItem(ArmorItem.Material.IRON, 2, 3).setId("bootsIron"));
      register(310, "diamond_helmet", new ArmorItem(ArmorItem.Material.DIAMOND, 3, 0).setId("helmetDiamond"));
      register(311, "diamond_chestplate", new ArmorItem(ArmorItem.Material.DIAMOND, 3, 1).setId("chestplateDiamond"));
      register(312, "diamond_leggings", new ArmorItem(ArmorItem.Material.DIAMOND, 3, 2).setId("leggingsDiamond"));
      register(313, "diamond_boots", new ArmorItem(ArmorItem.Material.DIAMOND, 3, 3).setId("bootsDiamond"));
      register(314, "golden_helmet", new ArmorItem(ArmorItem.Material.GOLD, 4, 0).setId("helmetGold"));
      register(315, "golden_chestplate", new ArmorItem(ArmorItem.Material.GOLD, 4, 1).setId("chestplateGold"));
      register(316, "golden_leggings", new ArmorItem(ArmorItem.Material.GOLD, 4, 2).setId("leggingsGold"));
      register(317, "golden_boots", new ArmorItem(ArmorItem.Material.GOLD, 4, 3).setId("bootsGold"));
      register(318, "flint", new Item().setId("flint").setItemGroup(ItemGroup.MATERIALS));
      register(319, "porkchop", new FoodItem(3, 0.3F, true).setId("porkchopRaw"));
      register(320, "cooked_porkchop", new FoodItem(8, 0.8F, true).setId("porkchopCooked"));
      register(321, "painting", new WallHangableItem(PaintingEntity.class).setId("painting"));
      register(322, "golden_apple", new AppleItem(4, 1.2F, false).alwaysEdible().setStatusEffect(StatusEffect.REGENERATION.id, 5, 1, 1.0F).setId("appleGold"));
      register(323, "sign", new SignItem().setId("sign"));
      register(324, "wooden_door", new WoodenDoorItem(Material.WOOD).setId("doorWood"));
      Item var0 = new BucketItem(Blocks.AIR).setId("bucket").setMaxStackSize(16);
      register(325, "bucket", var0);
      register(326, "water_bucket", new BucketItem(Blocks.FLOWING_WATER).setId("bucketWater").setRecipeRemainder(var0));
      register(327, "lava_bucket", new BucketItem(Blocks.FLOWING_LAVA).setId("bucketLava").setRecipeRemainder(var0));
      register(328, "minecart", new MinecartItem(MinecartEntity.Type.RIDEABLE).setId("minecart"));
      register(329, "saddle", new SaddleItem().setId("saddle"));
      register(330, "iron_door", new WoodenDoorItem(Material.IRON).setId("doorIron"));
      register(331, "redstone", new RedstoneItem().setId("redstone").setBrewingRecipe(PotionHelper.REDSTONE));
      register(332, "snowball", new SnowballItem().setId("snowball"));
      register(333, "boat", new BoatItem().setId("boat"));
      register(334, "leather", new Item().setId("leather").setItemGroup(ItemGroup.MATERIALS));
      register(335, "milk_bucket", new MilkBucketItem().setId("milk").setRecipeRemainder(var0));
      register(336, "brick", new Item().setId("brick").setItemGroup(ItemGroup.MATERIALS));
      register(337, "clay_ball", new Item().setId("clay").setItemGroup(ItemGroup.MATERIALS));
      register(338, "reeds", new PlaceableItem(Blocks.REEDS).setId("reeds").setItemGroup(ItemGroup.MATERIALS));
      register(339, "paper", new Item().setId("paper").setItemGroup(ItemGroup.MISC));
      register(340, "book", new BookItem().setId("book").setItemGroup(ItemGroup.MISC));
      register(341, "slime_ball", new Item().setId("slimeball").setItemGroup(ItemGroup.MISC));
      register(342, "chest_minecart", new MinecartItem(MinecartEntity.Type.CHEST).setId("minecartChest"));
      register(343, "furnace_minecart", new MinecartItem(MinecartEntity.Type.FURNACE).setId("minecartFurnace"));
      register(344, "egg", new EggItem().setId("egg"));
      register(345, "compass", new Item().setId("compass").setItemGroup(ItemGroup.TOOLS));
      register(346, "fishing_rod", new FishingRodItem().setId("fishingRod"));
      register(347, "clock", new Item().setId("clock").setItemGroup(ItemGroup.TOOLS));
      register(348, "glowstone_dust", new Item().setId("yellowDust").setBrewingRecipe(PotionHelper.GLOWSTONE).setItemGroup(ItemGroup.MATERIALS));
      register(349, "fish", new FishItem(false).setId("fish").setStackable(true));
      register(350, "cooked_fish", new FishItem(true).setId("fish").setStackable(true));
      register(351, "dye", new DyeItem().setId("dyePowder"));
      register(352, "bone", new Item().setId("bone").setHandheld().setItemGroup(ItemGroup.MISC));
      register(353, "sugar", new Item().setId("sugar").setBrewingRecipe(PotionHelper.SUGAR).setItemGroup(ItemGroup.MATERIALS));
      register(354, "cake", new PlaceableItem(Blocks.CAKE).setMaxStackSize(1).setId("cake").setItemGroup(ItemGroup.FOOD));
      register(355, "bed", new BedItem().setMaxStackSize(1).setId("bed"));
      register(356, "repeater", new PlaceableItem(Blocks.REPEATER).setId("diode").setItemGroup(ItemGroup.REDSTONE));
      register(357, "cookie", new FoodItem(2, 0.1F, false).setId("cookie"));
      register(358, "filled_map", new FilledMapItem().setId("map"));
      register(359, "shears", new ShearsItem().setId("shears"));
      register(360, "melon", new FoodItem(2, 0.3F, false).setId("melon"));
      register(361, "pumpkin_seeds", new WheatSeedsItem(Blocks.PUMPKIN_STEM, Blocks.FARMLAND).setId("seeds_pumpkin"));
      register(362, "melon_seeds", new WheatSeedsItem(Blocks.MELON_STEM, Blocks.FARMLAND).setId("seeds_melon"));
      register(363, "beef", new FoodItem(3, 0.3F, true).setId("beefRaw"));
      register(364, "cooked_beef", new FoodItem(8, 0.8F, true).setId("beefCooked"));
      register(365, "chicken", new FoodItem(2, 0.3F, true).setStatusEffect(StatusEffect.HUNGER.id, 30, 0, 0.3F).setId("chickenRaw"));
      register(366, "cooked_chicken", new FoodItem(6, 0.6F, true).setId("chickenCooked"));
      register(367, "rotten_flesh", new FoodItem(4, 0.1F, true).setStatusEffect(StatusEffect.HUNGER.id, 30, 0, 0.8F).setId("rottenFlesh"));
      register(368, "ender_pearl", new EnderPearlItem().setId("enderPearl"));
      register(369, "blaze_rod", new Item().setId("blazeRod").setItemGroup(ItemGroup.MATERIALS).setHandheld());
      register(370, "ghast_tear", new Item().setId("ghastTear").setBrewingRecipe(PotionHelper.GHAST_TEAR).setItemGroup(ItemGroup.BREWING));
      register(371, "gold_nugget", new Item().setId("goldNugget").setItemGroup(ItemGroup.MATERIALS));
      register(372, "nether_wart", new WheatSeedsItem(Blocks.NETHER_WART, Blocks.SOUL_SAND).setId("netherStalkSeeds").setBrewingRecipe("+4"));
      register(373, "potion", new PotionItem().setId("potion"));
      register(374, "glass_bottle", new GlassBottleItem().setId("glassBottle"));
      register(
         375,
         "spider_eye",
         new FoodItem(2, 0.8F, false).setStatusEffect(StatusEffect.POISON.id, 5, 0, 1.0F).setId("spiderEye").setBrewingRecipe(PotionHelper.POISON)
      );
      register(
         376,
         "fermented_spider_eye",
         new Item().setId("fermentedSpiderEye").setBrewingRecipe(PotionHelper.FERMENTED_SPIDER_EYE).setItemGroup(ItemGroup.BREWING)
      );
      register(377, "blaze_powder", new Item().setId("blazePowder").setBrewingRecipe(PotionHelper.BLAZE_POWDER).setItemGroup(ItemGroup.BREWING));
      register(378, "magma_cream", new Item().setId("magmaCream").setBrewingRecipe(PotionHelper.MAGMA_CREAM).setItemGroup(ItemGroup.BREWING));
      register(379, "brewing_stand", new PlaceableItem(Blocks.BREWING_STAND).setId("brewingStand").setItemGroup(ItemGroup.BREWING));
      register(380, "cauldron", new PlaceableItem(Blocks.CAULDRON).setId("cauldron").setItemGroup(ItemGroup.BREWING));
      register(381, "ender_eye", new EnderEyeItem().setId("eyeOfEnder"));
      register(382, "speckled_melon", new Item().setId("speckledMelon").setBrewingRecipe(PotionHelper.GLISTERING_MELON).setItemGroup(ItemGroup.BREWING));
      register(383, "spawn_egg", new SpawnEggItem().setId("monsterPlacer"));
      register(384, "experience_bottle", new ExperienceBottleItem().setId("expBottle"));
      register(385, "fire_charge", new FireChargeItem().setId("fireball"));
      register(386, "writable_book", new BookAndQuillItem().setId("writingBook").setItemGroup(ItemGroup.MISC));
      register(387, "written_book", new WrittenBookItem().setId("writtenBook").setMaxStackSize(16));
      register(388, "emerald", new Item().setId("emerald").setItemGroup(ItemGroup.MATERIALS));
      register(389, "item_frame", new WallHangableItem(ItemFrameEntity.class).setId("frame"));
      register(390, "flower_pot", new PlaceableItem(Blocks.FLOWER_POT).setId("flowerPot").setItemGroup(ItemGroup.DECORATIONS));
      register(391, "carrot", new CropItem(3, 0.6F, Blocks.CARROTS, Blocks.FARMLAND).setId("carrots"));
      register(392, "potato", new CropItem(1, 0.3F, Blocks.POTATOES, Blocks.FARMLAND).setId("potato"));
      register(393, "baked_potato", new FoodItem(5, 0.6F, false).setId("potatoBaked"));
      register(394, "poisonous_potato", new FoodItem(2, 0.3F, false).setStatusEffect(StatusEffect.POISON.id, 5, 0, 0.6F).setId("potatoPoisonous"));
      register(395, "map", new EmptyMapItem().setId("emptyMap"));
      register(396, "golden_carrot", new FoodItem(6, 1.2F, false).setId("carrotGolden").setBrewingRecipe(PotionHelper.GOLDEN_CARROT));
      register(397, "skull", new SkullItem().setId("skull"));
      register(398, "carrot_on_a_stick", new CarrotOnAStickItem().setId("carrotOnAStick"));
      register(399, "nether_star", new NetherStarItem().setId("netherStar").setItemGroup(ItemGroup.MATERIALS));
      register(400, "pumpkin_pie", new FoodItem(8, 0.3F, false).setId("pumpkinPie").setItemGroup(ItemGroup.FOOD));
      register(401, "fireworks", new FireworksItem().setId("fireworks"));
      register(402, "firework_charge", new FireworksChargeItem().setId("fireworksCharge").setItemGroup(ItemGroup.MISC));
      register(403, "enchanted_book", new EnchantedBookItem().setMaxStackSize(1).setId("enchantedBook"));
      register(404, "comparator", new PlaceableItem(Blocks.COMPARATOR).setId("comparator").setItemGroup(ItemGroup.REDSTONE));
      register(405, "netherbrick", new Item().setId("netherbrick").setItemGroup(ItemGroup.MATERIALS));
      register(406, "quartz", new Item().setId("netherquartz").setItemGroup(ItemGroup.MATERIALS));
      register(407, "tnt_minecart", new MinecartItem(MinecartEntity.Type.TNT).setId("minecartTnt"));
      register(408, "hopper_minecart", new MinecartItem(MinecartEntity.Type.HOPPER).setId("minecartHopper"));
      register(409, "prismarine_shard", new Item().setId("prismarineShard").setItemGroup(ItemGroup.MATERIALS));
      register(410, "prismarine_crystals", new Item().setId("prismarineCrystals").setItemGroup(ItemGroup.MATERIALS));
      register(411, "rabbit", new FoodItem(3, 0.3F, true).setId("rabbitRaw"));
      register(412, "cooked_rabbit", new FoodItem(5, 0.6F, true).setId("rabbitCooked"));
      register(413, "rabbit_stew", new StewItem(10).setId("rabbitStew"));
      register(414, "rabbit_foot", new Item().setId("rabbitFoot").setBrewingRecipe(PotionHelper.f_78qmeymir).setItemGroup(ItemGroup.MATERIALS));
      register(415, "rabbit_hide", new Item().setId("rabbitHide").setItemGroup(ItemGroup.MATERIALS));
      register(417, "iron_horse_armor", new Item().setId("horsearmormetal").setMaxStackSize(1).setItemGroup(ItemGroup.MISC));
      register(418, "golden_horse_armor", new Item().setId("horsearmorgold").setMaxStackSize(1).setItemGroup(ItemGroup.MISC));
      register(419, "diamond_horse_armor", new Item().setId("horsearmordiamond").setMaxStackSize(1).setItemGroup(ItemGroup.MISC));
      register(420, "lead", new LeadItem().setId("leash"));
      register(421, "name_tag", new NameTagItem().setId("nameTag"));
      register(422, "command_block_minecart", new MinecartItem(MinecartEntity.Type.COMMAND_BLOCK).setId("minecartCommandBlock").setItemGroup(null));
      register(423, "mutton", new FoodItem(2, 0.3F, true).setId("muttonRaw"));
      register(424, "cooked_mutton", new FoodItem(6, 0.8F, true).setId("muttonCooked"));
      register(425, "banner", new BannerItem().setId("banner"));
      register(2256, "record_13", new MusicDiscItem("13").setId("record"));
      register(2257, "record_cat", new MusicDiscItem("cat").setId("record"));
      register(2258, "record_blocks", new MusicDiscItem("blocks").setId("record"));
      register(2259, "record_chirp", new MusicDiscItem("chirp").setId("record"));
      register(2260, "record_far", new MusicDiscItem("far").setId("record"));
      register(2261, "record_mall", new MusicDiscItem("mall").setId("record"));
      register(2262, "record_mellohi", new MusicDiscItem("mellohi").setId("record"));
      register(2263, "record_stal", new MusicDiscItem("stal").setId("record"));
      register(2264, "record_strad", new MusicDiscItem("strad").setId("record"));
      register(2265, "record_ward", new MusicDiscItem("ward").setId("record"));
      register(2266, "record_11", new MusicDiscItem("11").setId("record"));
      register(2267, "record_wait", new MusicDiscItem("wait").setId("record"));
   }

   private static void register(Block block) {
      register(block, new BlockItem(block));
   }

   protected static void register(Block block, Item item) {
      register(Block.getRawId(block), (Identifier)Block.REGISTRY.getKey(block), item);
      BLOCK_ITEMS.put(block, item);
   }

   private static void register(int rawId, String id, Item item) {
      register(rawId, new Identifier(id), item);
   }

   private static void register(int rawId, Identifier id, Item item) {
      REGISTRY.register(rawId, id, item);
   }

   public static enum ToolMaterial {
      WOOD(0, 59, 2.0F, 0.0F, 15),
      STONE(1, 131, 4.0F, 1.0F, 5),
      IRON(2, 250, 6.0F, 2.0F, 14),
      DIAMOND(3, 1561, 8.0F, 3.0F, 10),
      GOLD(0, 32, 12.0F, 0.0F, 22);

      private final int strength;
      private final int maxDurability;
      private final float miningSpeed;
      private final float attackDamage;
      private final int enchantability;

      private ToolMaterial(int strength, int maxDurability, float miningSpeed, float attackDamage, int enchantability) {
         this.strength = strength;
         this.maxDurability = maxDurability;
         this.miningSpeed = miningSpeed;
         this.attackDamage = attackDamage;
         this.enchantability = enchantability;
      }

      public int getMaxDurability() {
         return this.maxDurability;
      }

      public float getMiningSpeed() {
         return this.miningSpeed;
      }

      public float getAttackDamage() {
         return this.attackDamage;
      }

      public int getStrength() {
         return this.strength;
      }

      public int getEnchantability() {
         return this.enchantability;
      }

      public Item getRepairIngredient() {
         if (this == WOOD) {
            return Item.byBlock(Blocks.PLANKS);
         } else if (this == STONE) {
            return Item.byBlock(Blocks.COBBLESTONE);
         } else if (this == GOLD) {
            return Items.GOLD_INGOT;
         } else if (this == IRON) {
            return Items.IRON_INGOT;
         } else {
            return this == DIAMOND ? Items.DIAMOND : null;
         }
      }
   }
}
