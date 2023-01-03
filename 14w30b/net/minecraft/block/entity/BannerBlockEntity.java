package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class BannerBlockEntity extends BlockEntity {
   private int baseColor;
   private NbtList patternsNbt;
   private boolean hasPatternData;
   private List patterns;
   private List colors;
   private String texture;

   public void set(ItemStack stack) {
      this.patternsNbt = null;
      if (stack.hasNbt() && stack.getNbt().isType("BlockEntityTag", 10)) {
         NbtCompound var2 = stack.getNbt().getCompound("BlockEntityTag");
         if (var2.contains("Patterns")) {
            this.patternsNbt = (NbtList)var2.getList("Patterns", 10).copy();
         }

         if (var2.isType("Base", 99)) {
            this.baseColor = var2.getInt("Base");
         } else {
            this.baseColor = stack.getMetadata() & 15;
         }
      } else {
         this.baseColor = stack.getMetadata() & 15;
      }

      this.patterns = null;
      this.colors = null;
      this.texture = "";
      this.hasPatternData = true;
   }

   @Override
   public void writeNbt(NbtCompound nbt) {
      super.writeNbt(nbt);
      nbt.putInt("Base", this.baseColor);
      if (this.patternsNbt != null) {
         nbt.put("Patterns", this.patternsNbt);
      }
   }

   @Override
   public void readNbt(NbtCompound nbt) {
      super.readNbt(nbt);
      this.baseColor = nbt.getInt("Base");
      this.patternsNbt = nbt.getList("Patterns", 10);
      this.patterns = null;
      this.colors = null;
      this.texture = null;
      this.hasPatternData = true;
   }

   @Override
   public Packet createUpdatePacket() {
      NbtCompound var1 = new NbtCompound();
      this.writeNbt(var1);
      return new BlockEntityUpdateS2CPacket(this.pos, 6, var1);
   }

   public int getBase() {
      return this.baseColor;
   }

   public static int getBaseColor(ItemStack stack) {
      NbtCompound var1 = stack.getNbt("BlockEntityTag", false);
      return var1 != null && var1.contains("Base") ? var1.getInt("Base") : stack.getMetadata();
   }

   public static int getPatternCount(ItemStack stack) {
      NbtCompound var1 = stack.getNbt("BlockEntityTag", false);
      return var1 != null && var1.contains("Patterns") ? var1.getList("Patterns", 10).size() : 0;
   }

   @Environment(EnvType.CLIENT)
   public List getPatterns() {
      this.setPatterns();
      return this.patterns;
   }

   @Environment(EnvType.CLIENT)
   public List getColors() {
      this.setPatterns();
      return this.colors;
   }

   @Environment(EnvType.CLIENT)
   public String getTexture() {
      this.setPatterns();
      return this.texture;
   }

   @Environment(EnvType.CLIENT)
   private void setPatterns() {
      if (this.patterns == null || this.colors == null || this.texture == null) {
         if (!this.hasPatternData) {
            this.texture = "";
         } else {
            this.patterns = Lists.newArrayList();
            this.colors = Lists.newArrayList();
            this.patterns.add(BannerBlockEntity.Pattern.BASE);
            this.colors.add(DyeColor.byMetadata(this.baseColor));
            this.texture = "b" + this.baseColor;
            if (this.patternsNbt != null) {
               for(int var1 = 0; var1 < this.patternsNbt.size(); ++var1) {
                  NbtCompound var2 = this.patternsNbt.getCompound(var1);
                  BannerBlockEntity.Pattern var3 = BannerBlockEntity.Pattern.byId(var2.getString("Pattern"));
                  if (var3 != null) {
                     this.patterns.add(var3);
                     int var4 = var2.getInt("Color");
                     this.colors.add(DyeColor.byMetadata(var4));
                     this.texture = this.texture + var3.getId() + var4;
                  }
               }
            }
         }
      }
   }

   public static void removeLastPattern(ItemStack stack) {
      NbtCompound var1 = stack.getNbt("BlockEntityTag", false);
      if (var1 != null && var1.isType("Patterns", 9)) {
         NbtList var2 = var1.getList("Patterns", 10);
         if (var2.size() > 0) {
            var2.remove(var2.size() - 1);
            if (var2.isEmpty()) {
               stack.getNbt().remove("BlockEntityTag");
               if (stack.getNbt().isEmpty()) {
                  stack.setNbt(null);
               }
            }
         }
      }
   }

   public static enum Pattern {
      BASE("base", "b"),
      SQUARE_BOTTOM_LEFT("square_bottom_left", "bl", "   ", "   ", "#  "),
      SQUARE_BOTTOM_RIGHT("square_bottom_right", "br", "   ", "   ", "  #"),
      SQUARE_TOP_LEFT("square_top_left", "tl", "#  ", "   ", "   "),
      SQUARE_TOP_RIGHT("square_top_right", "tr", "  #", "   ", "   "),
      STRIPE_BOTTOM("stripe_bottom", "bs", "   ", "   ", "###"),
      STRIPE_TOP("stripe_top", "ts", "###", "   ", "   "),
      STRIPE_LEFT("stripe_left", "ls", "#  ", "#  ", "#  "),
      STRIPE_RIGHT("stripe_right", "rs", "  #", "  #", "  #"),
      STRIPE_CENTER("stripe_center", "cs", " # ", " # ", " # "),
      STRIPE_MIDDLE("stripe_middle", "ms", "   ", "###", "   "),
      STRIPE_DOWNRIGHT("stripe_downright", "drs", "#  ", " # ", "  #"),
      STRIPE_DOWNLEFT("stripe_downleft", "dls", "  #", " # ", "#  "),
      STRIPE_SMALL("small_stripes", "ss", "# #", "# #", "   "),
      CROSS("cross", "cr", "# #", " # ", "# #"),
      STRAIGHT_CROSS("straight_cross", "sc", " # ", "###", " # "),
      TRIANGLE_BOTTOM("triangle_bottom", "bt", "   ", " # ", "# #"),
      TRIANGLE_TOP("triangle_top", "tt", "# #", " # ", "   "),
      TRIANGLES_BOTTOM("triangles_bottom", "bts", "   ", "# #", " # "),
      TRIANGLES_TOP("triangles_top", "tts", " # ", "# #", "   "),
      DIAGONAL_LEFT("diagonal_left", "ld", "## ", "#  ", "   "),
      DIAGONAL_RIGHT("diagonal_right", "rd", " ##", "  #", "   "),
      CIRCLE_MIDDLE("circle", "mc", "   ", " # ", "   "),
      RHOMBUS_MIDDLE("rhombus", "mr", " # ", "# #", " # "),
      HALF_VERTICAL("half_vertical", "vh", "## ", "## ", "## "),
      HALF_HORIZONTAL("half_horizontal", "hh", "###", "###", "   "),
      BORDER("border", "bo", "###", "# #", "###"),
      CURLY_BORDER("curly_border", "cbo", new ItemStack(Blocks.VINE)),
      CREEPER("creeper", "cre", new ItemStack(Items.SKULL, 1, 4)),
      GRADIENT("gradient", "gra", "# #", " # ", " # "),
      BRICKS("bricks", "bri", new ItemStack(Blocks.BRICKS)),
      SKULL("skull", "sku", new ItemStack(Items.SKULL, 1, 1)),
      FLOWER("flower", "flo", new ItemStack(Blocks.RED_FLOWER, 1, FlowerBlock.Type.OXEY_DAISY.getIndex())),
      MOJANG("mojang", "moj", new ItemStack(Items.GOLDEN_APPLE, 1, 1));

      private String name;
      private String id;
      private String[] patterns = new String[3];
      private ItemStack item;

      private Pattern(String name, String id) {
         this.name = name;
         this.id = id;
      }

      private Pattern(String name, String id, ItemStack item) {
         this(name, id);
         this.item = item;
      }

      private Pattern(String name, String id, String topPattern, String middlePattern, String bottomPattern) {
         this(name, id);
         this.patterns[0] = topPattern;
         this.patterns[1] = middlePattern;
         this.patterns[2] = bottomPattern;
      }

      @Environment(EnvType.CLIENT)
      public String getName() {
         return this.name;
      }

      public String getId() {
         return this.id;
      }

      public String[] getPatterns() {
         return this.patterns;
      }

      public boolean hasPattern() {
         return this.item != null || this.patterns[0] != null;
      }

      public boolean hasItem() {
         return this.item != null;
      }

      public ItemStack getItem() {
         return this.item;
      }

      @Environment(EnvType.CLIENT)
      public static BannerBlockEntity.Pattern byId(String id) {
         for(BannerBlockEntity.Pattern var4 : values()) {
            if (var4.id.equals(id)) {
               return var4;
            }
         }

         return null;
      }
   }
}
