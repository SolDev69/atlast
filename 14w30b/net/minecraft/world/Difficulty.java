package net.minecraft.world;

public enum Difficulty {
   PEACEFUL(0, "options.difficulty.peaceful"),
   EASY(1, "options.difficulty.easy"),
   NORMAL(2, "options.difficulty.normal"),
   HARD(3, "options.difficulty.hard");

   private static final Difficulty[] ALL = new Difficulty[values().length];
   private final int index;
   private final String id;

   private Difficulty(int index, String id) {
      this.index = index;
      this.id = id;
   }

   public int getIndex() {
      return this.index;
   }

   public static Difficulty byIndex(int index) {
      return ALL[index % ALL.length];
   }

   public String getName() {
      return this.id;
   }

   static {
      for(Difficulty var3 : values()) {
         ALL[var3.index] = var3;
      }
   }
}
