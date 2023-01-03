package net.minecraft.crafting;

import net.minecraft.block.Blocks;
import net.minecraft.block.DirtBlock;
import net.minecraft.block.PrismarineBlock;
import net.minecraft.block.QuartzBlock;
import net.minecraft.block.SandBlock;
import net.minecraft.block.SandstoneBlock;
import net.minecraft.block.StoneBlock;
import net.minecraft.block.StoneSlabBlock;
import net.minecraft.block.StonebrickBlock;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class BlockRecipes {
   public void register(CraftingManager manager) {
      manager.registerShaped(new ItemStack(Blocks.CHEST), "###", "# #", "###", '#', Blocks.PLANKS);
      manager.registerShaped(new ItemStack(Blocks.TRAPPED_CHEST), "#-", '#', Blocks.CHEST, '-', Blocks.TRIPWIRE_HOOK);
      manager.registerShaped(new ItemStack(Blocks.ENDER_CHEST), "###", "#E#", "###", '#', Blocks.OBSIDIAN, 'E', Items.ENDER_EYE);
      manager.registerShaped(new ItemStack(Blocks.FURNACE), "###", "# #", "###", '#', Blocks.COBBLESTONE);
      manager.registerShaped(new ItemStack(Blocks.CRAFTING_TABLE), "##", "##", '#', Blocks.PLANKS);
      manager.registerShaped(new ItemStack(Blocks.SANDSTONE), "##", "##", '#', new ItemStack(Blocks.SAND, 1, SandBlock.Variant.SAND.getIndex()));
      manager.registerShaped(
         new ItemStack(Blocks.SANDSTONE, 4, SandstoneBlock.Type.SMOOTH.getIndex()),
         "##",
         "##",
         '#',
         new ItemStack(Blocks.SANDSTONE, 1, SandstoneBlock.Type.DEFAULT.getIndex())
      );
      manager.registerShaped(
         new ItemStack(Blocks.SANDSTONE, 1, SandstoneBlock.Type.CHISELED.getIndex()),
         "#",
         "#",
         '#',
         new ItemStack(Blocks.STONE_SLAB, 1, StoneSlabBlock.Variant.SAND.getIndex())
      );
      manager.registerShaped(
         new ItemStack(Blocks.QUARTZ_BLOCK, 1, QuartzBlock.Variant.CHISELED.getIndex()),
         "#",
         "#",
         '#',
         new ItemStack(Blocks.STONE_SLAB, 1, StoneSlabBlock.Variant.QUARTZ.getIndex())
      );
      manager.registerShaped(
         new ItemStack(Blocks.QUARTZ_BLOCK, 2, QuartzBlock.Variant.LINES_Y.getIndex()),
         "#",
         "#",
         '#',
         new ItemStack(Blocks.QUARTZ_BLOCK, 1, QuartzBlock.Variant.DEFAULT.getIndex())
      );
      manager.registerShaped(new ItemStack(Blocks.STONE_BRICKS, 4), "##", "##", '#', new ItemStack(Blocks.STONE, 1, StoneBlock.Variant.STONE.getIndex()));
      manager.registerShaped(
         new ItemStack(Blocks.STONE_BRICKS, 1, StonebrickBlock.CHISELED_INDEX),
         "#",
         "#",
         '#',
         new ItemStack(Blocks.STONE_SLAB, 1, StoneSlabBlock.Variant.SMOOTHBRICK.getIndex())
      );
      manager.registerShapeless(new ItemStack(Blocks.STONE_BRICKS, 1, StonebrickBlock.MOSSY_INDEX), Blocks.STONE_BRICKS, Blocks.VINE);
      manager.registerShapeless(new ItemStack(Blocks.MOSSY_COBBLESTONE, 1), Blocks.COBBLESTONE, Blocks.VINE);
      manager.registerShaped(new ItemStack(Blocks.IRON_BARS, 16), "###", "###", '#', Items.IRON_INGOT);
      manager.registerShaped(new ItemStack(Blocks.GLASS_PANE, 16), "###", "###", '#', Blocks.GLASS);
      manager.registerShaped(new ItemStack(Blocks.REDSTONE_LAMP, 1), " R ", "RGR", " R ", 'R', Items.REDSTONE, 'G', Blocks.GLOWSTONE);
      manager.registerShaped(new ItemStack(Blocks.BEACON, 1), "GGG", "GSG", "OOO", 'G', Blocks.GLASS, 'S', Items.NETHER_STAR, 'O', Blocks.OBSIDIAN);
      manager.registerShaped(new ItemStack(Blocks.NETHER_BRICKS, 1), "NN", "NN", 'N', Items.NETHERBRICK);
      manager.registerShaped(new ItemStack(Blocks.STONE, 2, StoneBlock.Variant.DIORITE.getIndex()), "CQ", "QC", 'C', Blocks.COBBLESTONE, 'Q', Items.QUARTZ);
      manager.registerShapeless(
         new ItemStack(Blocks.STONE, 1, StoneBlock.Variant.GRANITE.getIndex()),
         new ItemStack(Blocks.STONE, 1, StoneBlock.Variant.DIORITE.getIndex()),
         Items.QUARTZ
      );
      manager.registerShapeless(
         new ItemStack(Blocks.STONE, 2, StoneBlock.Variant.ANDESITE.getIndex()),
         new ItemStack(Blocks.STONE, 1, StoneBlock.Variant.DIORITE.getIndex()),
         Blocks.COBBLESTONE
      );
      manager.registerShaped(
         new ItemStack(Blocks.DIRT, 4, DirtBlock.Variant.DOARSE_DIRT.getIndex()),
         "DG",
         "GD",
         'D',
         new ItemStack(Blocks.DIRT, 1, DirtBlock.Variant.DIRT.getIndex()),
         'G',
         Blocks.GRAVEL
      );
      manager.registerShaped(
         new ItemStack(Blocks.STONE, 4, StoneBlock.Variant.DIORITE_SMOOTH.getIndex()),
         "SS",
         "SS",
         'S',
         new ItemStack(Blocks.STONE, 1, StoneBlock.Variant.DIORITE.getIndex())
      );
      manager.registerShaped(
         new ItemStack(Blocks.STONE, 4, StoneBlock.Variant.GRANITE_SMOOTH.getIndex()),
         "SS",
         "SS",
         'S',
         new ItemStack(Blocks.STONE, 1, StoneBlock.Variant.GRANITE.getIndex())
      );
      manager.registerShaped(
         new ItemStack(Blocks.STONE, 4, StoneBlock.Variant.ANDESITE_SMOOTH.getIndex()),
         "SS",
         "SS",
         'S',
         new ItemStack(Blocks.STONE, 1, StoneBlock.Variant.ANDESITE.getIndex())
      );
      manager.registerShaped(new ItemStack(Blocks.PRISMARINE, 1, PrismarineBlock.ROUGH_VARIANT), "SS", "SS", 'S', Items.PRISMARINE_SHARD);
      manager.registerShaped(new ItemStack(Blocks.PRISMARINE, 1, PrismarineBlock.BRICKS_VARIANT), "SSS", "SSS", "SSS", 'S', Items.PRISMARINE_SHARD);
      manager.registerShaped(
         new ItemStack(Blocks.PRISMARINE, 1, PrismarineBlock.DARK_VARIANT),
         "SSS",
         "SIS",
         "SSS",
         'S',
         Items.PRISMARINE_SHARD,
         'I',
         new ItemStack(Items.DYE, 1, DyeColor.BLACK.getMetadata())
      );
      manager.registerShaped(new ItemStack(Blocks.SEA_LANTERN, 1, 0), "SCS", "CCC", "SCS", 'S', Items.PRISMARINE_SHARD, 'C', Items.PRISMARINE_CRYSTALS);
   }
}
