package net.minecraft.stat;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.crafting.CraftingManager;
import net.minecraft.crafting.recipe.CraftingRecipe;
import net.minecraft.entity.Entities;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.Identifier;
import net.minecraft.smelting.SmeltingManager;
import net.minecraft.stat.achievement.Achievements;
import net.minecraft.text.TranslatableText;

public class Stats {
   protected static Map BY_ID = Maps.newHashMap();
   public static List ALL = Lists.newArrayList();
   public static List GENERAL = Lists.newArrayList();
   public static List USED = Lists.newArrayList();
   public static List MINED = Lists.newArrayList();
   public static Stat GAMES_LEFT = new GeneralStat("stat.leaveGame", new TranslatableText("stat.leaveGame")).setLocal().register();
   public static Stat MINUTES_PLAYED = new GeneralStat("stat.playOneMinute", new TranslatableText("stat.playOneMinute"), Stat.TIME_FORMATTER)
      .setLocal()
      .register();
   public static Stat TIME_SINCE_DEATH = new GeneralStat("stat.timeSinceDeath", new TranslatableText("stat.timeSinceDeath"), Stat.TIME_FORMATTER)
      .setLocal()
      .register();
   public static Stat CM_WALKED = new GeneralStat("stat.walkOneCm", new TranslatableText("stat.walkOneCm"), Stat.DISTANCE_FORMATTER).setLocal().register();
   public static Stat CROUCH_ONE_CM = new GeneralStat("stat.crouchOneCm", new TranslatableText("stat.crouchOneCm"), Stat.DISTANCE_FORMATTER)
      .setLocal()
      .register();
   public static Stat SPRINT_ONE_CM = new GeneralStat("stat.sprintOneCm", new TranslatableText("stat.sprintOneCm"), Stat.DISTANCE_FORMATTER)
      .setLocal()
      .register();
   public static Stat CM_SWUM = new GeneralStat("stat.swimOneCm", new TranslatableText("stat.swimOneCm"), Stat.DISTANCE_FORMATTER).setLocal().register();
   public static Stat CM_FALLEN = new GeneralStat("stat.fallOneCm", new TranslatableText("stat.fallOneCm"), Stat.DISTANCE_FORMATTER).setLocal().register();
   public static Stat CM_CLIMB = new GeneralStat("stat.climbOneCm", new TranslatableText("stat.climbOneCm"), Stat.DISTANCE_FORMATTER).setLocal().register();
   public static Stat CM_FLOWN = new GeneralStat("stat.flyOneCm", new TranslatableText("stat.flyOneCm"), Stat.DISTANCE_FORMATTER).setLocal().register();
   public static Stat CM_DIVEN = new GeneralStat("stat.diveOneCm", new TranslatableText("stat.diveOneCm"), Stat.DISTANCE_FORMATTER).setLocal().register();
   public static Stat CM_MINECART = new GeneralStat("stat.minecartOneCm", new TranslatableText("stat.minecartOneCm"), Stat.DISTANCE_FORMATTER)
      .setLocal()
      .register();
   public static Stat CM_SAILED = new GeneralStat("stat.boatOneCm", new TranslatableText("stat.boatOneCm"), Stat.DISTANCE_FORMATTER).setLocal().register();
   public static Stat CM_PIG = new GeneralStat("stat.pigOneCm", new TranslatableText("stat.pigOneCm"), Stat.DISTANCE_FORMATTER).setLocal().register();
   public static Stat CM_HORSE = new GeneralStat("stat.horseOneCm", new TranslatableText("stat.horseOneCm"), Stat.DISTANCE_FORMATTER).setLocal().register();
   public static Stat JUMPS = new GeneralStat("stat.jump", new TranslatableText("stat.jump")).setLocal().register();
   public static Stat DROPS = new GeneralStat("stat.drop", new TranslatableText("stat.drop")).setLocal().register();
   public static Stat DAMAGE_DEALT = new GeneralStat("stat.damageDealt", new TranslatableText("stat.damageDealt"), Stat.DIVIDE_BY_TEN_FORMATTER).register();
   public static Stat DAMAGE_TAKEN = new GeneralStat("stat.damageTaken", new TranslatableText("stat.damageTaken"), Stat.DIVIDE_BY_TEN_FORMATTER).register();
   public static Stat DEATHS = new GeneralStat("stat.deaths", new TranslatableText("stat.deaths")).register();
   public static Stat MOBS_KILLED = new GeneralStat("stat.mobKills", new TranslatableText("stat.mobKills")).register();
   public static Stat ANIMALS_BRED = new GeneralStat("stat.animalsBred", new TranslatableText("stat.animalsBred")).register();
   public static Stat PLAYERS_KILLED = new GeneralStat("stat.playerKills", new TranslatableText("stat.playerKills")).register();
   public static Stat FISH_CAUGHT = new GeneralStat("stat.fishCaught", new TranslatableText("stat.fishCaught")).register();
   public static Stat JUNK_FISHED = new GeneralStat("stat.junkFished", new TranslatableText("stat.junkFished")).register();
   public static Stat TREASURE_FISHED = new GeneralStat("stat.treasureFished", new TranslatableText("stat.treasureFished")).register();
   public static Stat TALKED_TO_VILLAGER = new GeneralStat("stat.talkedToVillager", new TranslatableText("stat.talkedToVillager")).register();
   public static Stat TRADED_WITH_VILLAGER = new GeneralStat("stat.tradedWithVillager", new TranslatableText("stat.tradedWithVillager")).register();
   public static final Stat[] BLOCKS_MINED = new Stat[4096];
   public static final Stat[] ITEMS_CRAFTED = new Stat[32000];
   public static final Stat[] ITEMS_USED = new Stat[32000];
   public static final Stat[] ITEMS_BROKEN = new Stat[32000];

   public static void init() {
      initBlocksMinedStats();
      initItemsUsedStats();
      initItemsBrokenStats();
      initItemsCraftedStats();
      Achievements.init();
      Entities.load();
   }

   private static void initItemsCraftedStats() {
      HashSet var0 = Sets.newHashSet();

      for(CraftingRecipe var2 : CraftingManager.getInstance().getRecipes()) {
         if (var2.getOutput() != null) {
            var0.add(var2.getOutput().getItem());
         }
      }

      for(ItemStack var7 : SmeltingManager.getInstance().getRecipes().values()) {
         var0.add(var7.getItem());
      }

      for(Item var8 : var0) {
         if (var8 != null) {
            int var3 = Item.getRawId(var8);
            String var4 = translationKey(var8);
            if (var4 != null) {
               ITEMS_CRAFTED[var3] = new ItemStat("stat.craftItem.", var4, new TranslatableText("stat.craftItem", new ItemStack(var8).getDisplayName()), var8)
                  .register();
            }
         }
      }

      mergeBlockStats(ITEMS_CRAFTED);
   }

   private static void initBlocksMinedStats() {
      for(Block var1 : Block.REGISTRY) {
         Item var2 = Item.byBlock(var1);
         if (var2 != null) {
            int var3 = Block.getRawId(var1);
            String var4 = translationKey(var2);
            if (var4 != null && var1.hasStats()) {
               BLOCKS_MINED[var3] = new ItemStat("stat.mineBlock.", var4, new TranslatableText("stat.mineBlock", new ItemStack(var1).getDisplayName()), var2)
                  .register();
               MINED.add((ItemStat)BLOCKS_MINED[var3]);
            }
         }
      }

      mergeBlockStats(BLOCKS_MINED);
   }

   private static void initItemsUsedStats() {
      for(Item var1 : Item.REGISTRY) {
         if (var1 != null) {
            int var2 = Item.getRawId(var1);
            String var3 = translationKey(var1);
            if (var3 != null) {
               ITEMS_USED[var2] = new ItemStat("stat.useItem.", var3, new TranslatableText("stat.useItem", new ItemStack(var1).getDisplayName()), var1)
                  .register();
               if (!(var1 instanceof BlockItem)) {
                  USED.add((ItemStat)ITEMS_USED[var2]);
               }
            }
         }
      }

      mergeBlockStats(ITEMS_USED);
   }

   private static void initItemsBrokenStats() {
      for(Item var1 : Item.REGISTRY) {
         if (var1 != null) {
            int var2 = Item.getRawId(var1);
            String var3 = translationKey(var1);
            if (var3 != null && var1.isDamageable()) {
               ITEMS_BROKEN[var2] = new ItemStat("stat.breakItem.", var3, new TranslatableText("stat.breakItem", new ItemStack(var1).getDisplayName()), var1)
                  .register();
            }
         }
      }

      mergeBlockStats(ITEMS_BROKEN);
   }

   private static String translationKey(Item item) {
      Identifier var1 = (Identifier)Item.REGISTRY.getKey(item);
      return var1 != null ? var1.toString().replace(':', '.') : null;
   }

   private static void mergeBlockStats(Stat[] stats) {
      mergeBlockStats(stats, Blocks.WATER, Blocks.FLOWING_WATER);
      mergeBlockStats(stats, Blocks.LAVA, Blocks.FLOWING_LAVA);
      mergeBlockStats(stats, Blocks.LIT_PUMPKIN, Blocks.PUMPKIN);
      mergeBlockStats(stats, Blocks.LIT_FURNACE, Blocks.FURNACE);
      mergeBlockStats(stats, Blocks.LIT_REDSTONE_ORE, Blocks.REDSTONE_ORE);
      mergeBlockStats(stats, Blocks.POWERED_REPEATER, Blocks.REPEATER);
      mergeBlockStats(stats, Blocks.POWERED_COMPARATOR, Blocks.COMPARATOR);
      mergeBlockStats(stats, Blocks.REDSTONE_TORCH, Blocks.UNLIT_REDSTONE_TORCH);
      mergeBlockStats(stats, Blocks.LIT_REDSTONE_LAMP, Blocks.REDSTONE_LAMP);
      mergeBlockStats(stats, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM);
      mergeBlockStats(stats, Blocks.DOUBLE_STONE_SLAB, Blocks.STONE_SLAB);
      mergeBlockStats(stats, Blocks.DOUBLE_WOODEN_SLAB, Blocks.WOODEN_SLAB);
      mergeBlockStats(stats, Blocks.GRASS, Blocks.DIRT);
      mergeBlockStats(stats, Blocks.FARMLAND, Blocks.DIRT);
   }

   private static void mergeBlockStats(Stat[] stats, Block block1, Block block2) {
      int var3 = Block.getRawId(block1);
      int var4 = Block.getRawId(block2);
      if (stats[var3] != null && stats[var4] == null) {
         stats[var4] = stats[var3];
      } else {
         ALL.remove(stats[var3]);
         MINED.remove(stats[var3]);
         GENERAL.remove(stats[var3]);
         stats[var3] = stats[var4];
      }
   }

   public static Stat createEntityKillStat(Entities.SpawnEggData spawnEggData) {
      String var1 = Entities.getId(spawnEggData.id);
      return var1 == null
         ? null
         : new Stat("stat.killEntity." + var1, new TranslatableText("stat.entityKill", new TranslatableText("entity." + var1 + ".name"))).register();
   }

   public static Stat createKilledByEntityStat(Entities.SpawnEggData spawnEggData) {
      String var1 = Entities.getId(spawnEggData.id);
      return var1 == null
         ? null
         : new Stat("stat.entityKilledBy." + var1, new TranslatableText("stat.entityKilledBy", new TranslatableText("entity." + var1 + ".name"))).register();
   }

   public static Stat get(String id) {
      return (Stat)BY_ID.get(id);
   }
}
