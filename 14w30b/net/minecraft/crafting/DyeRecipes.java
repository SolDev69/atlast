package net.minecraft.crafting;

import net.minecraft.block.Blocks;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.FlowerBlock;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class DyeRecipes {
   public void register(CraftingManager manager) {
      for(int var2 = 0; var2 < 16; ++var2) {
         manager.registerShapeless(new ItemStack(Blocks.WOOL, 1, var2), new ItemStack(Items.DYE, 1, 15 - var2), new ItemStack(Item.byBlock(Blocks.WOOL), 1, 0));
         manager.registerShaped(
            new ItemStack(Blocks.STAINED_HARDENED_CLAY, 8, 15 - var2),
            "###",
            "#X#",
            "###",
            '#',
            new ItemStack(Blocks.HARDENED_CLAY),
            'X',
            new ItemStack(Items.DYE, 1, var2)
         );
         manager.registerShaped(
            new ItemStack(Blocks.STAINED_GLASS, 8, 15 - var2), "###", "#X#", "###", '#', new ItemStack(Blocks.GLASS), 'X', new ItemStack(Items.DYE, 1, var2)
         );
         manager.registerShaped(new ItemStack(Blocks.STAINED_GLASS_PANE, 16, var2), "###", "###", '#', new ItemStack(Blocks.STAINED_GLASS, 1, var2));
      }

      manager.registerShapeless(
         new ItemStack(Items.DYE, 1, DyeColor.YELLOW.getMetadata()), new ItemStack(Blocks.YELLOW_FLOWER, 1, FlowerBlock.Type.DANDELION.getIndex())
      );
      manager.registerShapeless(new ItemStack(Items.DYE, 1, DyeColor.RED.getMetadata()), new ItemStack(Blocks.RED_FLOWER, 1, FlowerBlock.Type.POPPY.getIndex()));
      manager.registerShapeless(new ItemStack(Items.DYE, 3, DyeColor.WHITE.getMetadata()), Items.BONE);
      manager.registerShapeless(
         new ItemStack(Items.DYE, 2, DyeColor.PINK.getMetadata()),
         new ItemStack(Items.DYE, 1, DyeColor.RED.getMetadata()),
         new ItemStack(Items.DYE, 1, DyeColor.WHITE.getMetadata())
      );
      manager.registerShapeless(
         new ItemStack(Items.DYE, 2, DyeColor.ORANGE.getMetadata()),
         new ItemStack(Items.DYE, 1, DyeColor.RED.getMetadata()),
         new ItemStack(Items.DYE, 1, DyeColor.YELLOW.getMetadata())
      );
      manager.registerShapeless(
         new ItemStack(Items.DYE, 2, DyeColor.LIME.getMetadata()),
         new ItemStack(Items.DYE, 1, DyeColor.GREEN.getMetadata()),
         new ItemStack(Items.DYE, 1, DyeColor.WHITE.getMetadata())
      );
      manager.registerShapeless(
         new ItemStack(Items.DYE, 2, DyeColor.GRAY.getMetadata()),
         new ItemStack(Items.DYE, 1, DyeColor.BLACK.getMetadata()),
         new ItemStack(Items.DYE, 1, DyeColor.WHITE.getMetadata())
      );
      manager.registerShapeless(
         new ItemStack(Items.DYE, 2, DyeColor.SILVER.getMetadata()),
         new ItemStack(Items.DYE, 1, DyeColor.GRAY.getMetadata()),
         new ItemStack(Items.DYE, 1, DyeColor.WHITE.getMetadata())
      );
      manager.registerShapeless(
         new ItemStack(Items.DYE, 3, DyeColor.SILVER.getMetadata()),
         new ItemStack(Items.DYE, 1, DyeColor.BLACK.getMetadata()),
         new ItemStack(Items.DYE, 1, DyeColor.WHITE.getMetadata()),
         new ItemStack(Items.DYE, 1, DyeColor.WHITE.getMetadata())
      );
      manager.registerShapeless(
         new ItemStack(Items.DYE, 2, DyeColor.LIGHT_BLUE.getMetadata()),
         new ItemStack(Items.DYE, 1, DyeColor.BLUE.getMetadata()),
         new ItemStack(Items.DYE, 1, DyeColor.WHITE.getMetadata())
      );
      manager.registerShapeless(
         new ItemStack(Items.DYE, 2, DyeColor.CYAN.getMetadata()),
         new ItemStack(Items.DYE, 1, DyeColor.BLUE.getMetadata()),
         new ItemStack(Items.DYE, 1, DyeColor.GREEN.getMetadata())
      );
      manager.registerShapeless(
         new ItemStack(Items.DYE, 2, DyeColor.PURPLE.getMetadata()),
         new ItemStack(Items.DYE, 1, DyeColor.BLUE.getMetadata()),
         new ItemStack(Items.DYE, 1, DyeColor.RED.getMetadata())
      );
      manager.registerShapeless(
         new ItemStack(Items.DYE, 2, DyeColor.MAGENTA.getMetadata()),
         new ItemStack(Items.DYE, 1, DyeColor.PURPLE.getMetadata()),
         new ItemStack(Items.DYE, 1, DyeColor.PINK.getMetadata())
      );
      manager.registerShapeless(
         new ItemStack(Items.DYE, 3, DyeColor.MAGENTA.getMetadata()),
         new ItemStack(Items.DYE, 1, DyeColor.BLUE.getMetadata()),
         new ItemStack(Items.DYE, 1, DyeColor.RED.getMetadata()),
         new ItemStack(Items.DYE, 1, DyeColor.PINK.getMetadata())
      );
      manager.registerShapeless(
         new ItemStack(Items.DYE, 4, DyeColor.MAGENTA.getMetadata()),
         new ItemStack(Items.DYE, 1, DyeColor.BLUE.getMetadata()),
         new ItemStack(Items.DYE, 1, DyeColor.RED.getMetadata()),
         new ItemStack(Items.DYE, 1, DyeColor.RED.getMetadata()),
         new ItemStack(Items.DYE, 1, DyeColor.WHITE.getMetadata())
      );
      manager.registerShapeless(
         new ItemStack(Items.DYE, 1, DyeColor.LIGHT_BLUE.getMetadata()), new ItemStack(Blocks.RED_FLOWER, 1, FlowerBlock.Type.BLUE_ORCHID.getIndex())
      );
      manager.registerShapeless(
         new ItemStack(Items.DYE, 1, DyeColor.MAGENTA.getMetadata()), new ItemStack(Blocks.RED_FLOWER, 1, FlowerBlock.Type.ALLIUM.getIndex())
      );
      manager.registerShapeless(
         new ItemStack(Items.DYE, 1, DyeColor.SILVER.getMetadata()), new ItemStack(Blocks.RED_FLOWER, 1, FlowerBlock.Type.HOUSTONIA.getIndex())
      );
      manager.registerShapeless(
         new ItemStack(Items.DYE, 1, DyeColor.RED.getMetadata()), new ItemStack(Blocks.RED_FLOWER, 1, FlowerBlock.Type.RED_TULIP.getIndex())
      );
      manager.registerShapeless(
         new ItemStack(Items.DYE, 1, DyeColor.ORANGE.getMetadata()), new ItemStack(Blocks.RED_FLOWER, 1, FlowerBlock.Type.ORANGE_TULIP.getIndex())
      );
      manager.registerShapeless(
         new ItemStack(Items.DYE, 1, DyeColor.SILVER.getMetadata()), new ItemStack(Blocks.RED_FLOWER, 1, FlowerBlock.Type.WHITE_TULIP.getIndex())
      );
      manager.registerShapeless(
         new ItemStack(Items.DYE, 1, DyeColor.PINK.getMetadata()), new ItemStack(Blocks.RED_FLOWER, 1, FlowerBlock.Type.PINK_TULIP.getIndex())
      );
      manager.registerShapeless(
         new ItemStack(Items.DYE, 1, DyeColor.SILVER.getMetadata()), new ItemStack(Blocks.RED_FLOWER, 1, FlowerBlock.Type.OXEY_DAISY.getIndex())
      );
      manager.registerShapeless(
         new ItemStack(Items.DYE, 2, DyeColor.YELLOW.getMetadata()), new ItemStack(Blocks.DOUBLE_PLANT, 1, DoublePlantBlock.Variant.SUNFLOWER.getIndex())
      );
      manager.registerShapeless(
         new ItemStack(Items.DYE, 2, DyeColor.MAGENTA.getMetadata()), new ItemStack(Blocks.DOUBLE_PLANT, 1, DoublePlantBlock.Variant.SYRINGA.getIndex())
      );
      manager.registerShapeless(
         new ItemStack(Items.DYE, 2, DyeColor.RED.getMetadata()), new ItemStack(Blocks.DOUBLE_PLANT, 1, DoublePlantBlock.Variant.ROSE.getIndex())
      );
      manager.registerShapeless(
         new ItemStack(Items.DYE, 2, DyeColor.PINK.getMetadata()), new ItemStack(Blocks.DOUBLE_PLANT, 1, DoublePlantBlock.Variant.PAEONIA.getIndex())
      );

      for(int var3 = 0; var3 < 16; ++var3) {
         manager.registerShaped(new ItemStack(Blocks.CARPET, 3, var3), "##", '#', new ItemStack(Blocks.WOOL, 1, var3));
      }
   }
}
