package net.minecraft.item;

import net.minecraft.block.material.MaterialColor;
import net.minecraft.text.Formatting;
import net.minecraft.util.StringRepresentable;

public enum DyeColor implements StringRepresentable {
   WHITE(0, 15, "white", "white", MaterialColor.WHITE, Formatting.WHITE),
   ORANGE(1, 14, "orange", "orange", MaterialColor.ORANGE, Formatting.GOLD),
   MAGENTA(2, 13, "magenta", "magenta", MaterialColor.MAGENTA, Formatting.AQUA),
   LIGHT_BLUE(3, 12, "light_blue", "lightBlue", MaterialColor.LIGHT_BLUE, Formatting.BLUE),
   YELLOW(4, 11, "yellow", "yellow", MaterialColor.YELLOW, Formatting.YELLOW),
   LIME(5, 10, "lime", "lime", MaterialColor.LIME, Formatting.GREEN),
   PINK(6, 9, "pink", "pink", MaterialColor.PINK, Formatting.LIGHT_PURPLE),
   GRAY(7, 8, "gray", "gray", MaterialColor.GRAY, Formatting.DARK_GRAY),
   SILVER(8, 7, "silver", "silver", MaterialColor.LIGHT_GRAY, Formatting.GRAY),
   CYAN(9, 6, "cyan", "cyan", MaterialColor.CYAN, Formatting.DARK_AQUA),
   PURPLE(10, 5, "purple", "purple", MaterialColor.PURPLE, Formatting.DARK_PURPLE),
   BLUE(11, 4, "blue", "blue", MaterialColor.BLUE, Formatting.DARK_BLUE),
   BROWN(12, 3, "brown", "brown", MaterialColor.BROWN, Formatting.GOLD),
   GREEN(13, 2, "green", "green", MaterialColor.GREEN, Formatting.DARK_GREEN),
   RED(14, 1, "red", "red", MaterialColor.RED, Formatting.DARK_RED),
   BLACK(15, 0, "black", "black", MaterialColor.BLACK, Formatting.BLACK);

   private static final DyeColor[] ALL = new DyeColor[values().length];
   private static final DyeColor[] BY_METADATA = new DyeColor[values().length];
   private final int index;
   private final int metadata;
   private final String id;
   private final String name;
   private final MaterialColor materialColor;
   private final Formatting formatting;

   private DyeColor(int index, int metadata, String id, String name, MaterialColor materialColor, Formatting formatting) {
      this.index = index;
      this.metadata = metadata;
      this.id = id;
      this.name = name;
      this.materialColor = materialColor;
      this.formatting = formatting;
   }

   public int getIndex() {
      return this.index;
   }

   public int getMetadata() {
      return this.metadata;
   }

   public String getName() {
      return this.name;
   }

   public MaterialColor getMaterialColor() {
      return this.materialColor;
   }

   public static DyeColor byMetadata(int metadata) {
      if (metadata < 0 || metadata >= BY_METADATA.length) {
         metadata = 0;
      }

      return BY_METADATA[metadata];
   }

   public static DyeColor byIndex(int index) {
      if (index < 0 || index >= ALL.length) {
         index = 0;
      }

      return ALL[index];
   }

   @Override
   public String toString() {
      return this.name;
   }

   @Override
   public String getStringRepresentation() {
      return this.id;
   }

   static {
      for(DyeColor var3 : values()) {
         ALL[var3.getIndex()] = var3;
         BY_METADATA[var3.getMetadata()] = var3;
      }
   }
}
