package net.minecraft.block.material;

public class Material {
   public static final Material AIR = new AirMaterial(MaterialColor.AIR);
   public static final Material GRASS = new Material(MaterialColor.GRASS);
   public static final Material DIRT = new Material(MaterialColor.DIRT);
   public static final Material WOOD = new Material(MaterialColor.WOOD).setFlammable();
   public static final Material STONE = new Material(MaterialColor.STONE).setRequiresTool();
   public static final Material IRON = new Material(MaterialColor.IRON).setRequiresTool();
   public static final Material ANVIL = new Material(MaterialColor.IRON).setRequiresTool().setBlocksPistonMove();
   public static final Material WATER = new LiquidMaterial(MaterialColor.WATER).setDestroyOnPistonMove();
   public static final Material LAVA = new LiquidMaterial(MaterialColor.LAVA).setDestroyOnPistonMove();
   public static final Material LEAVES = new Material(MaterialColor.FOLIAGE).setFlammable().setTranslucent().setDestroyOnPistonMove();
   public static final Material PLANT = new PlantMaterial(MaterialColor.FOLIAGE).setDestroyOnPistonMove();
   public static final Material REPLACEABLE_PLANT = new PlantMaterial(MaterialColor.FOLIAGE).setFlammable().setDestroyOnPistonMove().setReplaceable();
   public static final Material SPONGE = new Material(MaterialColor.WEB);
   public static final Material WOOL = new Material(MaterialColor.WEB).setFlammable();
   public static final Material FIRE = new AirMaterial(MaterialColor.AIR).setDestroyOnPistonMove();
   public static final Material SAND = new Material(MaterialColor.SAND);
   public static final Material DECORATION = new PlantMaterial(MaterialColor.AIR).setDestroyOnPistonMove();
   public static final Material CARPET = new PlantMaterial(MaterialColor.WEB).setFlammable();
   public static final Material GLASS = new Material(MaterialColor.AIR).setTranslucent().setCanBeBrokenInAdventureMode();
   public static final Material REDSTONE_LAMP = new Material(MaterialColor.AIR).setCanBeBrokenInAdventureMode();
   public static final Material TNT = new Material(MaterialColor.LAVA).setFlammable().setTranslucent();
   public static final Material CORAL = new Material(MaterialColor.FOLIAGE).setDestroyOnPistonMove();
   public static final Material ICE = new Material(MaterialColor.ICE).setTranslucent().setCanBeBrokenInAdventureMode();
   public static final Material PACKED_ICE = new Material(MaterialColor.ICE).setCanBeBrokenInAdventureMode();
   public static final Material SNOW_LAYER = new PlantMaterial(MaterialColor.WHITE)
      .setReplaceable()
      .setTranslucent()
      .setRequiresTool()
      .setDestroyOnPistonMove();
   public static final Material SNOW = new Material(MaterialColor.WHITE).setRequiresTool();
   public static final Material CACTUS = new Material(MaterialColor.FOLIAGE).setTranslucent().setDestroyOnPistonMove();
   public static final Material CLAY = new Material(MaterialColor.CLAY);
   public static final Material PUMPKIN = new Material(MaterialColor.FOLIAGE).setDestroyOnPistonMove();
   public static final Material EGG = new Material(MaterialColor.FOLIAGE).setDestroyOnPistonMove();
   public static final Material PORTAL = new PortalMaterial(MaterialColor.AIR).setBlocksPistonMove();
   public static final Material CAKE = new Material(MaterialColor.AIR).setDestroyOnPistonMove();
   public static final Material COBWEB = (new Material(MaterialColor.WEB) {
      @Override
      public boolean blocksMovement() {
         return false;
      }
   }).setRequiresTool().setDestroyOnPistonMove();
   public static final Material PISTON = new Material(MaterialColor.STONE).setBlocksPistonMove();
   public static final Material BARRIER = new Material(MaterialColor.AIR).setRequiresTool().setBlocksPistonMove();
   private boolean flammable;
   private boolean replaceable;
   private boolean translucent;
   private final MaterialColor color;
   private boolean toolNotRequired = true;
   private int pistonMoveBehavior;
   private boolean canBeBrokenInAdventureMode;

   public Material(MaterialColor color) {
      this.color = color;
   }

   public boolean isLiquid() {
      return false;
   }

   public boolean isSolid() {
      return true;
   }

   public boolean isOpaque() {
      return true;
   }

   public boolean blocksMovement() {
      return true;
   }

   private Material setTranslucent() {
      this.translucent = true;
      return this;
   }

   protected Material setRequiresTool() {
      this.toolNotRequired = false;
      return this;
   }

   protected Material setFlammable() {
      this.flammable = true;
      return this;
   }

   public boolean isFlammable() {
      return this.flammable;
   }

   public Material setReplaceable() {
      this.replaceable = true;
      return this;
   }

   public boolean isReplaceable() {
      return this.replaceable;
   }

   public boolean isSolidBlocking() {
      return this.translucent ? false : this.blocksMovement();
   }

   public boolean isToolNotRequired() {
      return this.toolNotRequired;
   }

   public int getPistonMoveBehavior() {
      return this.pistonMoveBehavior;
   }

   protected Material setDestroyOnPistonMove() {
      this.pistonMoveBehavior = 1;
      return this;
   }

   protected Material setBlocksPistonMove() {
      this.pistonMoveBehavior = 2;
      return this;
   }

   protected Material setCanBeBrokenInAdventureMode() {
      this.canBeBrokenInAdventureMode = true;
      return this;
   }

   public MaterialColor getColor() {
      return this.color;
   }
}
