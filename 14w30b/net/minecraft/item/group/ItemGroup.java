package net.minecraft.item.group;

import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentEntry;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public abstract class ItemGroup {
   public static final ItemGroup[] BY_ID = new ItemGroup[12];
   public static final ItemGroup BUILDING_BLOCKS = new ItemGroup(0, "buildingBlocks") {
      @Environment(EnvType.CLIENT)
      @Override
      public Item getIconItem() {
         return Item.byBlock(Blocks.BRICKS);
      }
   };
   public static final ItemGroup DECORATIONS = new ItemGroup(1, "decorations") {
      @Environment(EnvType.CLIENT)
      @Override
      public Item getIconItem() {
         return Item.byBlock(Blocks.DOUBLE_PLANT);
      }

      @Environment(EnvType.CLIENT)
      @Override
      public int getIconMetadata() {
         return DoublePlantBlock.Variant.PAEONIA.getIndex();
      }
   };
   public static final ItemGroup REDSTONE = new ItemGroup(2, "redstone") {
      @Environment(EnvType.CLIENT)
      @Override
      public Item getIconItem() {
         return Items.REDSTONE;
      }
   };
   public static final ItemGroup TRANSPORTATION = new ItemGroup(3, "transportation") {
      @Environment(EnvType.CLIENT)
      @Override
      public Item getIconItem() {
         return Item.byBlock(Blocks.POWERED_RAIL);
      }
   };
   public static final ItemGroup MISC = (new ItemGroup(4, "misc") {
      @Environment(EnvType.CLIENT)
      @Override
      public Item getIconItem() {
         return Items.LAVA_BUCKET;
      }
   }).setEnchantmentTargets(new EnchantmentTarget[]{EnchantmentTarget.ALL});
   public static final ItemGroup SEARCH = (new ItemGroup(5, "search") {
      @Environment(EnvType.CLIENT)
      @Override
      public Item getIconItem() {
         return Items.COMPASS;
      }
   }).setTexture("item_search.png");
   public static final ItemGroup FOOD = new ItemGroup(6, "food") {
      @Environment(EnvType.CLIENT)
      @Override
      public Item getIconItem() {
         return Items.APPLE;
      }
   };
   public static final ItemGroup TOOLS = (new ItemGroup(7, "tools") {
      @Environment(EnvType.CLIENT)
      @Override
      public Item getIconItem() {
         return Items.IRON_AXE;
      }
   }).setEnchantmentTargets(new EnchantmentTarget[]{EnchantmentTarget.DIGGER, EnchantmentTarget.FISHING_ROD, EnchantmentTarget.BREAKABLE});
   public static final ItemGroup COMBAT = (new ItemGroup(8, "combat") {
         @Environment(EnvType.CLIENT)
         @Override
         public Item getIconItem() {
            return Items.GOLDEN_SWORD;
         }
      })
      .setEnchantmentTargets(
         new EnchantmentTarget[]{
            EnchantmentTarget.ARMOR,
            EnchantmentTarget.ARMOR_FEET,
            EnchantmentTarget.ARMOR_HEAD,
            EnchantmentTarget.ARMOR_LEGS,
            EnchantmentTarget.ARMOR_TORSO,
            EnchantmentTarget.BOW,
            EnchantmentTarget.WEAPON
         }
      );
   public static final ItemGroup BREWING = new ItemGroup(9, "brewing") {
      @Environment(EnvType.CLIENT)
      @Override
      public Item getIconItem() {
         return Items.POTION;
      }
   };
   public static final ItemGroup MATERIALS = new ItemGroup(10, "materials") {
      @Environment(EnvType.CLIENT)
      @Override
      public Item getIconItem() {
         return Items.STICK;
      }
   };
   public static final ItemGroup INVENTORY = (new ItemGroup(11, "inventory") {
      @Environment(EnvType.CLIENT)
      @Override
      public Item getIconItem() {
         return Item.byBlock(Blocks.CHEST);
      }
   }).setTexture("inventory.png").removeScrollbar().removeTooltip();
   private final int id;
   private final String name;
   private String texture = "items.png";
   private boolean scrollbar = true;
   private boolean tooltip = true;
   private EnchantmentTarget[] targets;
   @Environment(EnvType.CLIENT)
   private ItemStack item;

   public ItemGroup(int id, String name) {
      this.id = id;
      this.name = name;
      BY_ID[id] = this;
   }

   @Environment(EnvType.CLIENT)
   public int getId() {
      return this.id;
   }

   @Environment(EnvType.CLIENT)
   public String getName() {
      return this.name;
   }

   @Environment(EnvType.CLIENT)
   public String getDisplayName() {
      return "itemGroup." + this.getName();
   }

   @Environment(EnvType.CLIENT)
   public ItemStack getIcon() {
      if (this.item == null) {
         this.item = new ItemStack(this.getIconItem(), 1, this.getIconMetadata());
      }

      return this.item;
   }

   @Environment(EnvType.CLIENT)
   public abstract Item getIconItem();

   @Environment(EnvType.CLIENT)
   public int getIconMetadata() {
      return 0;
   }

   @Environment(EnvType.CLIENT)
   public String getTexture() {
      return this.texture;
   }

   public ItemGroup setTexture(String texture) {
      this.texture = texture;
      return this;
   }

   @Environment(EnvType.CLIENT)
   public boolean hasTooltip() {
      return this.tooltip;
   }

   public ItemGroup removeTooltip() {
      this.tooltip = false;
      return this;
   }

   @Environment(EnvType.CLIENT)
   public boolean hasScrollbar() {
      return this.scrollbar;
   }

   public ItemGroup removeScrollbar() {
      this.scrollbar = false;
      return this;
   }

   @Environment(EnvType.CLIENT)
   public int getColumn() {
      return this.id % 6;
   }

   @Environment(EnvType.CLIENT)
   public boolean isTopRow() {
      return this.id < 6;
   }

   @Environment(EnvType.CLIENT)
   public EnchantmentTarget[] getEnchantmentTargets() {
      return this.targets;
   }

   public ItemGroup setEnchantmentTargets(EnchantmentTarget... targets) {
      this.targets = targets;
      return this;
   }

   @Environment(EnvType.CLIENT)
   public boolean containsEnchantmentTarget(EnchantmentTarget target) {
      if (this.targets == null) {
         return false;
      } else {
         for(EnchantmentTarget var5 : this.targets) {
            if (var5 == target) {
               return true;
            }
         }

         return false;
      }
   }

   @Environment(EnvType.CLIENT)
   public void showItems(List stacks) {
      for(Item var3 : Item.REGISTRY) {
         if (var3 != null && var3.getItemGroup() == this) {
            var3.addToCreativeMenu(var3, this, stacks);
         }
      }

      if (this.getEnchantmentTargets() != null) {
         this.showBooks(stacks, this.getEnchantmentTargets());
      }
   }

   @Environment(EnvType.CLIENT)
   public void showBooks(List stacks, EnchantmentTarget... targets) {
      for(Enchantment var6 : Enchantment.ALL) {
         if (var6 != null && var6.target != null) {
            boolean var7 = false;

            for(int var8 = 0; var8 < targets.length && !var7; ++var8) {
               if (var6.target == targets[var8]) {
                  var7 = true;
               }
            }

            if (var7) {
               stacks.add(Items.ENCHANTED_BOOK.getStackWithEnchantment(new EnchantmentEntry(var6, var6.getMaxLevel())));
            }
         }
      }
   }
}
