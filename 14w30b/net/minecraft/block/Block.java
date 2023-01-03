package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.client.render.block.BlockLayer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.XpOrbEntity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.locale.I18n;
import net.minecraft.resource.Identifier;
import net.minecraft.stat.Stats;
import net.minecraft.util.HitResult;
import net.minecraft.util.Id2ObjectBiMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.DefaultedIdRegistry;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class Block {
   private static final Identifier AIR_ID = new Identifier("air");
   public static final DefaultedIdRegistry REGISTRY = new DefaultedIdRegistry(AIR_ID);
   public static final Id2ObjectBiMap STATE_REGISTRY = new Id2ObjectBiMap();
   private ItemGroup itemGroup;
   public static final Block.Sound ORE_SOUND = new Block.Sound("stone", 1.0F, 1.0F);
   public static final Block.Sound WOOD_SOUND = new Block.Sound("wood", 1.0F, 1.0F);
   public static final Block.Sound GRAVEL_SOUND = new Block.Sound("gravel", 1.0F, 1.0F);
   public static final Block.Sound GRASS_SOUND = new Block.Sound("grass", 1.0F, 1.0F);
   public static final Block.Sound STONE_SOUND = new Block.Sound("stone", 1.0F, 1.0F);
   public static final Block.Sound RAIL_SOUND = new Block.Sound("stone", 1.0F, 1.5F);
   public static final Block.Sound GLASS_SOUND = new Block.Sound("stone", 1.0F, 1.0F) {
      @Override
      public String getDigSound() {
         return "dig.glass";
      }

      @Override
      public String getSound() {
         return "step.stone";
      }
   };
   public static final Block.Sound CLOTH_SOUND = new Block.Sound("cloth", 1.0F, 1.0F);
   public static final Block.Sound SAND_SOUND = new Block.Sound("sand", 1.0F, 1.0F);
   public static final Block.Sound SNOW_SOUND = new Block.Sound("snow", 1.0F, 1.0F);
   public static final Block.Sound LADDER_SOUND = new Block.Sound("ladder", 1.0F, 1.0F) {
      @Override
      public String getDigSound() {
         return "dig.wood";
      }
   };
   public static final Block.Sound ANVIL_SOUND = new Block.Sound("anvil", 0.3F, 1.0F) {
      @Override
      public String getDigSound() {
         return "dig.stone";
      }

      @Override
      public String getSound() {
         return "random.anvil_land";
      }
   };
   protected boolean opaqueCube;
   protected int opacity;
   protected boolean isTranslucent;
   protected int lightLevel;
   protected boolean useNeighborLight;
   protected float miningSpeed;
   protected float resistance;
   protected boolean stats = true;
   protected boolean ticksRandomly;
   protected boolean hasBlockEntity;
   protected double minX;
   protected double minY;
   protected double minZ;
   protected double maxX;
   protected double maxY;
   protected double maxZ;
   public Block.Sound sound = ORE_SOUND;
   public float gravity = 1.0F;
   protected final Material material;
   public float slipperiness = 0.6F;
   protected final StateDefinition stateDefinition;
   private BlockState defaultState;
   private String id;

   public static int getRawId(Block block) {
      return REGISTRY.getId(block);
   }

   public static int serialize(BlockState state) {
      return getRawId(state.getBlock()) + (state.getBlock().getMetadataFromState(state) << 12);
   }

   public static Block byRawId(int id) {
      return (Block)REGISTRY.get(id);
   }

   public static BlockState deserialize(int blockdata) {
      int var1 = blockdata & 4095;
      int var2 = blockdata >> 12 & 15;
      return byRawId(var1).getStateFromMetadata(var2);
   }

   public static Block byItem(Item item) {
      return item instanceof BlockItem ? ((BlockItem)item).getBlock() : null;
   }

   public static Block byId(String id) {
      Identifier var1 = new Identifier(id);
      if (REGISTRY.containsKey(var1)) {
         return (Block)REGISTRY.get(var1);
      } else {
         try {
            return (Block)REGISTRY.get(Integer.parseInt(id));
         } catch (NumberFormatException var3) {
            return null;
         }
      }
   }

   public boolean isOpaque() {
      return this.opaqueCube;
   }

   public int getOpacity() {
      return this.opacity;
   }

   @Environment(EnvType.CLIENT)
   public boolean isTranslucent() {
      return this.isTranslucent;
   }

   public int getLightLevel() {
      return this.lightLevel;
   }

   public boolean usesNeighborLight() {
      return this.useNeighborLight;
   }

   public Material getMaterial() {
      return this.material;
   }

   public MaterialColor getMaterialColor(BlockState state) {
      return this.getMaterial().getColor();
   }

   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState();
   }

   public int getMetadataFromState(BlockState state) {
      if (state != null && !state.properties().isEmpty()) {
         throw new IllegalArgumentException("Don't know how to convert " + state + " back into data...");
      } else {
         return 0;
      }
   }

   public BlockState updateShape(BlockState state, IWorld world, BlockPos pos) {
      return state;
   }

   protected Block(Material material) {
      this.material = material;
      this.setShape(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      this.opaqueCube = this.isOpaqueCube();
      this.opacity = this.isOpaqueCube() ? 255 : 0;
      this.isTranslucent = !material.isOpaque();
      this.stateDefinition = this.createStateDefinition();
      this.setDefaultState(this.stateDefinition.any());
   }

   protected Block setSound(Block.Sound sound) {
      this.sound = sound;
      return this;
   }

   protected Block setOpacity(int opacity) {
      this.opacity = opacity;
      return this;
   }

   protected Block setLightLevel(float lightLevel) {
      this.lightLevel = (int)(15.0F * lightLevel);
      return this;
   }

   protected Block setResistance(float resistance) {
      this.resistance = resistance * 3.0F;
      return this;
   }

   public boolean blocksAmbientLight() {
      return this.material.blocksMovement() && this.isFullCube();
   }

   public boolean isConductor() {
      return this.material.isSolidBlocking() && this.isFullCube() && !this.isPowerSource();
   }

   public boolean isViewBlocking() {
      return this.material.blocksMovement() && this.isFullCube();
   }

   public boolean isFullCube() {
      return true;
   }

   public boolean canWalkThrough(IWorld world, BlockPos pos) {
      return !this.material.blocksMovement();
   }

   public int getRenderType() {
      return 3;
   }

   public boolean canBeReplaced(World world, BlockPos pos) {
      return false;
   }

   protected Block setStrength(float strength) {
      this.miningSpeed = strength;
      if (this.resistance < strength * 5.0F) {
         this.resistance = strength * 5.0F;
      }

      return this;
   }

   protected Block setUnbreakable() {
      this.setStrength(-1.0F);
      return this;
   }

   public float getMiningSpeed(World world, BlockPos pos) {
      return this.miningSpeed;
   }

   protected Block setTicksRandomly(boolean ticksRandomly) {
      this.ticksRandomly = ticksRandomly;
      return this;
   }

   public boolean ticksRandomly() {
      return this.ticksRandomly;
   }

   public boolean hasBlockEntity() {
      return this.hasBlockEntity;
   }

   protected final void setShape(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
      this.minX = (double)minX;
      this.minY = (double)minY;
      this.minZ = (double)minZ;
      this.maxX = (double)maxX;
      this.maxY = (double)maxY;
      this.maxZ = (double)maxZ;
   }

   @Environment(EnvType.CLIENT)
   public int getLightColor(IWorld world, BlockPos pos) {
      Block var3 = world.getBlockState(pos).getBlock();
      int var4 = world.getLightColor(pos, var3.getLightLevel());
      if (var4 == 0 && var3 instanceof SlabBlock) {
         pos = pos.down();
         var3 = world.getBlockState(pos).getBlock();
         return world.getLightColor(pos, var3.getLightLevel());
      } else {
         return var4;
      }
   }

   @Environment(EnvType.CLIENT)
   public boolean shouldRenderFace(IWorld world, BlockPos pos, Direction face) {
      if (face == Direction.DOWN && this.minY > 0.0) {
         return true;
      } else if (face == Direction.UP && this.maxY < 1.0) {
         return true;
      } else if (face == Direction.NORTH && this.minZ > 0.0) {
         return true;
      } else if (face == Direction.SOUTH && this.maxZ < 1.0) {
         return true;
      } else if (face == Direction.WEST && this.minX > 0.0) {
         return true;
      } else if (face == Direction.EAST && this.maxX < 1.0) {
         return true;
      } else {
         return !world.getBlockState(pos).getBlock().isOpaqueCube();
      }
   }

   public boolean isFaceSolid(IWorld world, BlockPos pos, Direction face) {
      return world.getBlockState(pos).getBlock().getMaterial().isSolid();
   }

   @Environment(EnvType.CLIENT)
   public Box getOutlineShape(World world, BlockPos pos) {
      return new Box(
         (double)pos.getX() + this.minX,
         (double)pos.getY() + this.minY,
         (double)pos.getZ() + this.minZ,
         (double)pos.getX() + this.maxX,
         (double)pos.getY() + this.maxY,
         (double)pos.getZ() + this.maxZ
      );
   }

   public void getCollisionBoxes(World world, BlockPos pos, BlockState state, Box entityBox, List boxes, Entity entity) {
      Box var7 = this.getCollisionShape(world, pos, state);
      if (var7 != null && entityBox.intersects(var7)) {
         boxes.add(var7);
      }
   }

   public Box getCollisionShape(World world, BlockPos pos, BlockState state) {
      return new Box(
         (double)pos.getX() + this.minX,
         (double)pos.getY() + this.minY,
         (double)pos.getZ() + this.minZ,
         (double)pos.getX() + this.maxX,
         (double)pos.getY() + this.maxY,
         (double)pos.getZ() + this.maxZ
      );
   }

   public boolean isOpaqueCube() {
      return true;
   }

   public boolean hasCollision(BlockState state, boolean allowFluids) {
      return this.hasCollision();
   }

   public boolean hasCollision() {
      return true;
   }

   public void randomTick(World world, BlockPos pos, BlockState state, Random random) {
      this.tick(world, pos, state, random);
   }

   public void tick(World world, BlockPos pos, BlockState state, Random random) {
   }

   @Environment(EnvType.CLIENT)
   public void randomDisplayTick(World world, BlockPos pos, BlockState state, Random random) {
   }

   public void onBroken(World world, BlockPos pos, BlockState state) {
   }

   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
   }

   public int getTickRate(World world) {
      return 10;
   }

   public void onAdded(World world, BlockPos pos, BlockState state) {
   }

   public void onRemoved(World world, BlockPos pos, BlockState state) {
   }

   public int getBaseDropCount(Random random) {
      return 1;
   }

   public Item getDropItem(BlockState state, Random random, int fortuneLevel) {
      return Item.byBlock(this);
   }

   public float getMiningSpeed(PlayerEntity player, World world, BlockPos pos) {
      float var4 = this.getMiningSpeed(world, pos);
      if (var4 < 0.0F) {
         return 0.0F;
      } else {
         return !player.canBreakBlock(this) ? player.getMiningSpeed(this, false) / var4 / 100.0F : player.getMiningSpeed(this, true) / var4 / 30.0F;
      }
   }

   public final void dropItems(World world, BlockPos pos, BlockState state, int fortuneLevel) {
      this.dropItems(world, pos, state, 1.0F, fortuneLevel);
   }

   public void dropItems(World world, BlockPos pos, BlockState state, float luck, int fortuneLevel) {
      if (!world.isClient) {
         int var6 = this.getDropCount(fortuneLevel, world.random);

         for(int var7 = 0; var7 < var6; ++var7) {
            if (!(world.random.nextFloat() > luck)) {
               Item var8 = this.getDropItem(state, world.random, fortuneLevel);
               if (var8 != null) {
                  this.dropItems(world, pos, new ItemStack(var8, 1, this.getDropItemMetadata(state)));
               }
            }
         }
      }
   }

   protected void dropItems(World world, BlockPos pos, ItemStack stack) {
      if (!world.isClient && world.getGameRules().getBoolean("doTileDrops")) {
         float var4 = 0.5F;
         double var5 = (double)(world.random.nextFloat() * var4) + (double)(1.0F - var4) * 0.5;
         double var7 = (double)(world.random.nextFloat() * var4) + (double)(1.0F - var4) * 0.5;
         double var9 = (double)(world.random.nextFloat() * var4) + (double)(1.0F - var4) * 0.5;
         ItemEntity var11 = new ItemEntity(world, (double)pos.getX() + var5, (double)pos.getY() + var7, (double)pos.getZ() + var9, stack);
         var11.resetPickupCooldown();
         world.addEntity(var11);
      }
   }

   protected void dropXp(World world, BlockPos pos, int size) {
      if (!world.isClient) {
         while(size > 0) {
            int var4 = XpOrbEntity.roundSize(size);
            size -= var4;
            world.addEntity(new XpOrbEntity(world, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, var4));
         }
      }
   }

   public int getDropItemMetadata(BlockState state) {
      return 0;
   }

   public float getBlastResistance(Entity entity) {
      return this.resistance / 5.0F;
   }

   public HitResult rayTrace(World world, BlockPos pos, Vec3d start, Vec3d end) {
      this.updateShape(world, pos);
      start = start.add((double)(-pos.getX()), (double)(-pos.getY()), (double)(-pos.getZ()));
      end = end.add((double)(-pos.getX()), (double)(-pos.getY()), (double)(-pos.getZ()));
      Vec3d var5 = start.intermediateWithX(end, this.minX);
      Vec3d var6 = start.intermediateWithX(end, this.maxX);
      Vec3d var7 = start.intermediateWithY(end, this.minY);
      Vec3d var8 = start.intermediateWithY(end, this.maxY);
      Vec3d var9 = start.intermediateWithZ(end, this.minZ);
      Vec3d var10 = start.intermediateWithZ(end, this.maxZ);
      if (!this.isWithinPlaneYZ(var5)) {
         var5 = null;
      }

      if (!this.isWithinPlaneYZ(var6)) {
         var6 = null;
      }

      if (!this.isWithinPlaneXZ(var7)) {
         var7 = null;
      }

      if (!this.isWithinPlaneXZ(var8)) {
         var8 = null;
      }

      if (!this.isWithinPlaneXY(var9)) {
         var9 = null;
      }

      if (!this.isWithinPlaneXY(var10)) {
         var10 = null;
      }

      Vec3d var11 = null;
      if (var5 != null && (var11 == null || start.squaredDistanceTo(var5) < start.squaredDistanceTo(var11))) {
         var11 = var5;
      }

      if (var6 != null && (var11 == null || start.squaredDistanceTo(var6) < start.squaredDistanceTo(var11))) {
         var11 = var6;
      }

      if (var7 != null && (var11 == null || start.squaredDistanceTo(var7) < start.squaredDistanceTo(var11))) {
         var11 = var7;
      }

      if (var8 != null && (var11 == null || start.squaredDistanceTo(var8) < start.squaredDistanceTo(var11))) {
         var11 = var8;
      }

      if (var9 != null && (var11 == null || start.squaredDistanceTo(var9) < start.squaredDistanceTo(var11))) {
         var11 = var9;
      }

      if (var10 != null && (var11 == null || start.squaredDistanceTo(var10) < start.squaredDistanceTo(var11))) {
         var11 = var10;
      }

      if (var11 == null) {
         return null;
      } else {
         Direction var12 = null;
         if (var11 == var5) {
            var12 = Direction.WEST;
         }

         if (var11 == var6) {
            var12 = Direction.EAST;
         }

         if (var11 == var7) {
            var12 = Direction.DOWN;
         }

         if (var11 == var8) {
            var12 = Direction.UP;
         }

         if (var11 == var9) {
            var12 = Direction.NORTH;
         }

         if (var11 == var10) {
            var12 = Direction.SOUTH;
         }

         return new HitResult(var11.add((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()), var12, pos);
      }
   }

   private boolean isWithinPlaneYZ(Vec3d vec) {
      if (vec == null) {
         return false;
      } else {
         return vec.y >= this.minY && vec.y <= this.maxY && vec.z >= this.minZ && vec.z <= this.maxZ;
      }
   }

   private boolean isWithinPlaneXZ(Vec3d vec) {
      if (vec == null) {
         return false;
      } else {
         return vec.x >= this.minX && vec.x <= this.maxX && vec.z >= this.minZ && vec.z <= this.maxZ;
      }
   }

   private boolean isWithinPlaneXY(Vec3d vec) {
      if (vec == null) {
         return false;
      } else {
         return vec.x >= this.minX && vec.x <= this.maxX && vec.y >= this.minY && vec.y <= this.maxY;
      }
   }

   public void onExploded(World world, BlockPos pos, Explosion explosion) {
   }

   @Environment(EnvType.CLIENT)
   public BlockLayer getRenderLayer() {
      return BlockLayer.SOLID;
   }

   public boolean canPlace(World world, BlockPos pos, Direction dir, ItemStack stack) {
      return this.canPlace(world, pos, dir);
   }

   public boolean canPlace(World world, BlockPos pos, Direction dir) {
      return this.canSurvive(world, pos);
   }

   public boolean canSurvive(World world, BlockPos pos) {
      return world.getBlockState(pos).getBlock().material.isReplaceable();
   }

   public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction face, float dx, float dy, float dz) {
      return false;
   }

   public void onSteppedOn(World world, BlockPos pos, Entity entity) {
   }

   public BlockState getPlacementState(World world, BlockPos pos, Direction dir, float dx, float dy, float dz, int metadata, LivingEntity entity) {
      return this.getStateFromMetadata(metadata);
   }

   public void startMining(World world, BlockPos pos, PlayerEntity player) {
   }

   public Vec3d applyMaterialDrag(World world, BlockPos pos, Entity entity, Vec3d velocity) {
      return velocity;
   }

   public void updateShape(IWorld world, BlockPos pos) {
   }

   public final double getMinX() {
      return this.minX;
   }

   public final double getMaxX() {
      return this.maxX;
   }

   public final double getMinY() {
      return this.minY;
   }

   public final double getMaxY() {
      return this.maxY;
   }

   public final double getMinZ() {
      return this.minZ;
   }

   public final double getMaxZ() {
      return this.maxZ;
   }

   @Environment(EnvType.CLIENT)
   public int getColor() {
      return 16777215;
   }

   @Environment(EnvType.CLIENT)
   public int getColor(int tint) {
      return 16777215;
   }

   @Environment(EnvType.CLIENT)
   public int getColor(IWorld world, BlockPos pos, int tint) {
      return 16777215;
   }

   @Environment(EnvType.CLIENT)
   public final int getColor(IWorld world, BlockPos pos) {
      return this.getColor(world, pos, 0);
   }

   public int getEmittedWeakPower(IWorld world, BlockPos pos, BlockState state, Direction dir) {
      return 0;
   }

   public boolean isPowerSource() {
      return false;
   }

   public void onEntityCollision(World world, BlockPos pos, BlockState state, Entity entity) {
   }

   public int getEmittedStrongPower(IWorld world, BlockPos pos, BlockState state, Direction dir) {
      return 0;
   }

   public void setBlockItemBounds() {
   }

   public void afterMinedByPlayer(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
      player.incrementStat(Stats.BLOCKS_MINED[getRawId(this)]);
      player.addFatigue(0.025F);
      if (this.hasSilkTouchDrops() && EnchantmentHelper.hasSilkTouch(player)) {
         ItemStack var7 = this.getSilkTouchDrop(state);
         if (var7 != null) {
            this.dropItems(world, pos, var7);
         }
      } else {
         int var6 = EnchantmentHelper.getFortuneLevel(player);
         this.dropItems(world, pos, state, var6);
      }
   }

   protected boolean hasSilkTouchDrops() {
      return this.isFullCube() && !this.hasBlockEntity;
   }

   protected ItemStack getSilkTouchDrop(BlockState state) {
      int var2 = 0;
      Item var3 = Item.byBlock(this);
      if (var3 != null && var3.isStackable()) {
         var2 = this.getMetadataFromState(state);
      }

      return new ItemStack(var3, 1, var2);
   }

   public int getDropCount(int fortuneLevel, Random random) {
      return this.getBaseDropCount(random);
   }

   public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
   }

   public Block setId(String id) {
      this.id = id;
      return this;
   }

   public String getName() {
      return I18n.translate(this.getTranslationKey() + ".name");
   }

   public String getTranslationKey() {
      return "tile." + this.id;
   }

   public boolean doEvent(World world, BlockPos pos, BlockState state, int type, int data) {
      return false;
   }

   public boolean hasStats() {
      return this.stats;
   }

   protected Block disableStats() {
      this.stats = false;
      return this;
   }

   public int getPistonMoveBehavior() {
      return this.material.getPistonMoveBehavior();
   }

   @Environment(EnvType.CLIENT)
   public float getAmbientOcclusionLight() {
      return this.blocksAmbientLight() ? 0.2F : 1.0F;
   }

   public void onFallenOn(World world, BlockPos pos, Entity entity, float fallDistance) {
      entity.applyFallDamage(fallDistance, 1.0F);
   }

   public void beforeCollision(World world, Entity pos) {
      pos.velocityY = 0.0;
   }

   @Environment(EnvType.CLIENT)
   public Item getPickItem(World world, BlockPos pos) {
      return Item.byBlock(this);
   }

   public int getPickItemMetadata(World world, BlockPos pos) {
      return this.getDropItemMetadata(world.getBlockState(pos));
   }

   @Environment(EnvType.CLIENT)
   public void addToCreativeMenu(Item item, ItemGroup group, List stacks) {
      stacks.add(new ItemStack(item, 1, 0));
   }

   @Environment(EnvType.CLIENT)
   public ItemGroup getItemGroup() {
      return this.itemGroup;
   }

   public Block setItemGroup(ItemGroup group) {
      this.itemGroup = group;
      return this;
   }

   public void beforeMinedByPlayer(World world, BlockPos pos, BlockState state, PlayerEntity player) {
   }

   public void randomPrecipitationTick(World world, BlockPos pos) {
   }

   @Environment(EnvType.CLIENT)
   public boolean hasPickItemMetadata() {
      return false;
   }

   public boolean acceptsImmediateTicks() {
      return true;
   }

   public boolean shouldDropItemsOnExplosion(Explosion explosion) {
      return true;
   }

   public boolean is(Block block) {
      return this == block;
   }

   public static boolean areEqual(Block block1, Block block2) {
      if (block1 == null || block2 == null) {
         return false;
      } else {
         return block1 == block2 ? true : block1.is(block2);
      }
   }

   public boolean hasAnalogOutput() {
      return false;
   }

   public int getAnalogOutput(World world, BlockPos pos) {
      return 0;
   }

   @Environment(EnvType.CLIENT)
   public int m_43rfjsapl(int i) {
      return -1;
   }

   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this);
   }

   public StateDefinition stateDefinition() {
      return this.stateDefinition;
   }

   protected final void setDefaultState(BlockState state) {
      this.defaultState = state;
   }

   public final BlockState defaultState() {
      return this.defaultState;
   }

   @Environment(EnvType.CLIENT)
   public Block.OffsetType getOffsetType() {
      return Block.OffsetType.NONE;
   }

   public static void init() {
      register(0, AIR_ID, new AirBlock().setId("air"));
      register(1, "stone", new StoneBlock().setStrength(1.5F).setResistance(10.0F).setSound(STONE_SOUND).setId("stone"));
      register(2, "grass", new GrassBlock().setStrength(0.6F).setSound(GRASS_SOUND).setId("grass"));
      register(3, "dirt", new DirtBlock().setStrength(0.5F).setSound(GRAVEL_SOUND).setId("dirt"));
      Block var0 = new Block(Material.STONE)
         .setStrength(2.0F)
         .setResistance(10.0F)
         .setSound(STONE_SOUND)
         .setId("stonebrick")
         .setItemGroup(ItemGroup.BUILDING_BLOCKS);
      register(4, "cobblestone", var0);
      Block var1 = new PlanksBlock().setStrength(2.0F).setResistance(5.0F).setSound(WOOD_SOUND).setId("wood");
      register(5, "planks", var1);
      register(6, "sapling", new SaplingBlock().setStrength(0.0F).setSound(GRASS_SOUND).setId("sapling"));
      register(
         7,
         "bedrock",
         new Block(Material.STONE)
            .setUnbreakable()
            .setResistance(6000000.0F)
            .setSound(STONE_SOUND)
            .setId("bedrock")
            .disableStats()
            .setItemGroup(ItemGroup.BUILDING_BLOCKS)
      );
      register(8, "flowing_water", new FlowingLiquidBlock(Material.WATER).setStrength(100.0F).setOpacity(3).setId("water").disableStats());
      register(9, "water", new LiquidSourceBlock(Material.WATER).setStrength(100.0F).setOpacity(3).setId("water").disableStats());
      register(10, "flowing_lava", new FlowingLiquidBlock(Material.LAVA).setStrength(100.0F).setLightLevel(1.0F).setId("lava").disableStats());
      register(11, "lava", new LiquidSourceBlock(Material.LAVA).setStrength(100.0F).setLightLevel(1.0F).setId("lava").disableStats());
      register(12, "sand", new SandBlock().setStrength(0.5F).setSound(SAND_SOUND).setId("sand"));
      register(13, "gravel", new GravelBlock().setStrength(0.6F).setSound(GRAVEL_SOUND).setId("gravel"));
      register(14, "gold_ore", new OreBlock().setStrength(3.0F).setResistance(5.0F).setSound(STONE_SOUND).setId("oreGold"));
      register(15, "iron_ore", new OreBlock().setStrength(3.0F).setResistance(5.0F).setSound(STONE_SOUND).setId("oreIron"));
      register(16, "coal_ore", new OreBlock().setStrength(3.0F).setResistance(5.0F).setSound(STONE_SOUND).setId("oreCoal"));
      register(17, "log", new LogBlock().setId("log"));
      register(18, "leaves", new LeavesBlock().setId("leaves"));
      register(19, "sponge", new SpongeBlock().setStrength(0.6F).setSound(GRASS_SOUND).setId("sponge"));
      register(20, "glass", new GlassBlock(Material.GLASS, false).setStrength(0.3F).setSound(GLASS_SOUND).setId("glass"));
      register(21, "lapis_ore", new OreBlock().setStrength(3.0F).setResistance(5.0F).setSound(STONE_SOUND).setId("oreLapis"));
      register(
         22,
         "lapis_block",
         new MineralBlock(MaterialColor.LAPIS)
            .setStrength(3.0F)
            .setResistance(5.0F)
            .setSound(STONE_SOUND)
            .setId("blockLapis")
            .setItemGroup(ItemGroup.BUILDING_BLOCKS)
      );
      register(23, "dispenser", new DispenserBlock().setStrength(3.5F).setSound(STONE_SOUND).setId("dispenser"));
      Block var2 = new SandstoneBlock().setSound(STONE_SOUND).setStrength(0.8F).setId("sandStone");
      register(24, "sandstone", var2);
      register(25, "noteblock", new NoteBlock().setStrength(0.8F).setId("musicBlock"));
      register(26, "bed", new BedBlock().setStrength(0.2F).setId("bed").disableStats());
      register(27, "golden_rail", new PoweredRailBlock().setStrength(0.7F).setSound(RAIL_SOUND).setId("goldenRail"));
      register(28, "detector_rail", new DetectorRailBlock().setStrength(0.7F).setSound(RAIL_SOUND).setId("detectorRail"));
      register(29, "sticky_piston", new PistonBaseBlock(true).setId("pistonStickyBase"));
      register(30, "web", new CobwebBlock().setOpacity(1).setStrength(4.0F).setId("web"));
      register(31, "tallgrass", new TallPlantBlock().setStrength(0.0F).setSound(GRASS_SOUND).setId("tallgrass"));
      register(32, "deadbush", new DeadBushBlock().setStrength(0.0F).setSound(GRASS_SOUND).setId("deadbush"));
      register(33, "piston", new PistonBaseBlock(false).setId("pistonBase"));
      register(34, "piston_head", new PistonHeadBlock());
      register(35, "wool", new ColoredBlock(Material.WOOL).setStrength(0.8F).setSound(CLOTH_SOUND).setId("cloth"));
      register(36, "piston_extension", new MovingBlock());
      register(37, "yellow_flower", new YellowFlowerBlock().setStrength(0.0F).setSound(GRASS_SOUND).setId("flower1"));
      register(38, "red_flower", new RedFlowerBlock().setStrength(0.0F).setSound(GRASS_SOUND).setId("flower2"));
      register(39, "brown_mushroom", new MushroomPlantBlock().setStrength(0.0F).setSound(GRASS_SOUND).setLightLevel(0.125F).setId("mushroom"));
      register(40, "red_mushroom", new MushroomPlantBlock().setStrength(0.0F).setSound(GRASS_SOUND).setId("mushroom"));
      register(41, "gold_block", new MineralBlock(MaterialColor.GOLD).setStrength(3.0F).setResistance(10.0F).setSound(RAIL_SOUND).setId("blockGold"));
      register(42, "iron_block", new MineralBlock(MaterialColor.IRON).setStrength(5.0F).setResistance(10.0F).setSound(RAIL_SOUND).setId("blockIron"));
      register(43, "double_stone_slab", new DoubleStoneSlabBlock().setStrength(2.0F).setResistance(10.0F).setSound(STONE_SOUND).setId("stoneSlab"));
      register(44, "stone_slab", new SingleStoneSlabBlock().setStrength(2.0F).setResistance(10.0F).setSound(STONE_SOUND).setId("stoneSlab"));
      Block var3 = new Block(Material.STONE)
         .setStrength(2.0F)
         .setResistance(10.0F)
         .setSound(STONE_SOUND)
         .setId("brick")
         .setItemGroup(ItemGroup.BUILDING_BLOCKS);
      register(45, "brick_block", var3);
      register(46, "tnt", new TntBlock().setStrength(0.0F).setSound(GRASS_SOUND).setId("tnt"));
      register(47, "bookshelf", new BookshelfBlock().setStrength(1.5F).setSound(WOOD_SOUND).setId("bookshelf"));
      register(
         48,
         "mossy_cobblestone",
         new Block(Material.STONE).setStrength(2.0F).setResistance(10.0F).setSound(STONE_SOUND).setId("stoneMoss").setItemGroup(ItemGroup.BUILDING_BLOCKS)
      );
      register(49, "obsidian", new ObsidianBlock().setStrength(50.0F).setResistance(2000.0F).setSound(STONE_SOUND).setId("obsidian"));
      register(50, "torch", new TorchBlock().setStrength(0.0F).setLightLevel(0.9375F).setSound(WOOD_SOUND).setId("torch"));
      register(51, "fire", new FireBlock().setStrength(0.0F).setLightLevel(1.0F).setSound(WOOD_SOUND).setId("fire").disableStats());
      register(52, "mob_spawner", new MobSpawnerBlock().setStrength(5.0F).setSound(RAIL_SOUND).setId("mobSpawner").disableStats());
      register(53, "oak_stairs", new StairsBlock(var1.defaultState().set(PlanksBlock.VARIANT, PlanksBlock.Variant.OAK)).setId("stairsWood"));
      register(54, "chest", new ChestBlock(0).setStrength(2.5F).setSound(WOOD_SOUND).setId("chest"));
      register(55, "redstone_wire", new RedstoneWireBlock().setStrength(0.0F).setSound(ORE_SOUND).setId("redstoneDust").disableStats());
      register(56, "diamond_ore", new OreBlock().setStrength(3.0F).setResistance(5.0F).setSound(STONE_SOUND).setId("oreDiamond"));
      register(57, "diamond_block", new MineralBlock(MaterialColor.DIAMOND).setStrength(5.0F).setResistance(10.0F).setSound(RAIL_SOUND).setId("blockDiamond"));
      register(58, "crafting_table", new CraftingTableBlock().setStrength(2.5F).setSound(WOOD_SOUND).setId("workbench"));
      register(59, "wheat", new WheatBlock().setId("crops"));
      Block var4 = new FarmlandBlock().setStrength(0.6F).setSound(GRAVEL_SOUND).setId("farmland");
      register(60, "farmland", var4);
      register(61, "furnace", new FurnaceBlock(false).setStrength(3.5F).setSound(STONE_SOUND).setId("furnace").setItemGroup(ItemGroup.DECORATIONS));
      register(62, "lit_furnace", new FurnaceBlock(true).setStrength(3.5F).setSound(STONE_SOUND).setLightLevel(0.875F).setId("furnace"));
      register(63, "standing_sign", new StandingSignBlock().setStrength(1.0F).setSound(WOOD_SOUND).setId("sign").disableStats());
      register(64, "wooden_door", new DoorBlock(Material.WOOD).setStrength(3.0F).setSound(WOOD_SOUND).setId("doorWood").disableStats());
      register(65, "ladder", new LadderBlock().setStrength(0.4F).setSound(LADDER_SOUND).setId("ladder"));
      register(66, "rail", new RailBlock().setStrength(0.7F).setSound(RAIL_SOUND).setId("rail"));
      register(67, "stone_stairs", new StairsBlock(var0.defaultState()).setId("stairsStone"));
      register(68, "wall_sign", new WallSignBlock().setStrength(1.0F).setSound(WOOD_SOUND).setId("sign").disableStats());
      register(69, "lever", new LeverBlock().setStrength(0.5F).setSound(WOOD_SOUND).setId("lever"));
      register(
         70,
         "stone_pressure_plate",
         new PressurePlateBlock(Material.STONE, PressurePlateBlock.ActivationRule.MOBS).setStrength(0.5F).setSound(STONE_SOUND).setId("pressurePlate")
      );
      register(71, "iron_door", new DoorBlock(Material.IRON).setStrength(5.0F).setSound(RAIL_SOUND).setId("doorIron").disableStats());
      register(
         72,
         "wooden_pressure_plate",
         new PressurePlateBlock(Material.WOOD, PressurePlateBlock.ActivationRule.EVERYTHING).setStrength(0.5F).setSound(WOOD_SOUND).setId("pressurePlate")
      );
      register(
         73,
         "redstone_ore",
         new RedstoneOreBlock(false).setStrength(3.0F).setResistance(5.0F).setSound(STONE_SOUND).setId("oreRedstone").setItemGroup(ItemGroup.BUILDING_BLOCKS)
      );
      register(
         74,
         "lit_redstone_ore",
         new RedstoneOreBlock(true).setLightLevel(0.625F).setStrength(3.0F).setResistance(5.0F).setSound(STONE_SOUND).setId("oreRedstone")
      );
      register(75, "unlit_redstone_torch", new RedstoneTorchBlock(false).setStrength(0.0F).setSound(WOOD_SOUND).setId("notGate"));
      register(
         76,
         "redstone_torch",
         new RedstoneTorchBlock(true).setStrength(0.0F).setLightLevel(0.5F).setSound(WOOD_SOUND).setId("notGate").setItemGroup(ItemGroup.REDSTONE)
      );
      register(77, "stone_button", new StoneButtonBlock().setStrength(0.5F).setSound(STONE_SOUND).setId("button"));
      register(78, "snow_layer", new SnowLayerBlock().setStrength(0.1F).setSound(SNOW_SOUND).setId("snow").setOpacity(0));
      register(79, "ice", new IceBlock().setStrength(0.5F).setOpacity(3).setSound(GLASS_SOUND).setId("ice"));
      register(80, "snow", new SnowBlock().setStrength(0.2F).setSound(SNOW_SOUND).setId("snow"));
      register(81, "cactus", new CactusBlock().setStrength(0.4F).setSound(CLOTH_SOUND).setId("cactus"));
      register(82, "clay", new ClayBlock().setStrength(0.6F).setSound(GRAVEL_SOUND).setId("clay"));
      register(83, "reeds", new SugarCaneBlock().setStrength(0.0F).setSound(GRASS_SOUND).setId("reeds").disableStats());
      register(84, "jukebox", new JukeboxBlock().setStrength(2.0F).setResistance(10.0F).setSound(STONE_SOUND).setId("jukebox"));
      register(85, "fence", new FenceBlock(Material.WOOD).setStrength(2.0F).setResistance(5.0F).setSound(WOOD_SOUND).setId("fence"));
      Block var5 = new PumpkinBlock().setStrength(1.0F).setSound(WOOD_SOUND).setId("pumpkin");
      register(86, "pumpkin", var5);
      register(87, "netherrack", new NetherrackBlock().setStrength(0.4F).setSound(STONE_SOUND).setId("hellrock"));
      register(88, "soul_sand", new SoulSandBlock().setStrength(0.5F).setSound(SAND_SOUND).setId("hellsand"));
      register(89, "glowstone", new GlowstoneBlock(Material.GLASS).setStrength(0.3F).setSound(GLASS_SOUND).setLightLevel(1.0F).setId("lightgem"));
      register(90, "portal", new PortalBlock().setStrength(-1.0F).setSound(GLASS_SOUND).setLightLevel(0.75F).setId("portal"));
      register(91, "lit_pumpkin", new PumpkinBlock().setStrength(1.0F).setSound(WOOD_SOUND).setLightLevel(1.0F).setId("litpumpkin"));
      register(92, "cake", new CakeBlock().setStrength(0.5F).setSound(CLOTH_SOUND).setId("cake").disableStats());
      register(93, "unpowered_repeater", new RepeaterBlock(false).setStrength(0.0F).setSound(WOOD_SOUND).setId("diode").disableStats());
      register(94, "powered_repeater", new RepeaterBlock(true).setStrength(0.0F).setSound(WOOD_SOUND).setId("diode").disableStats());
      register(95, "stained_glass", new StainedGlassBlock(Material.GLASS).setStrength(0.3F).setSound(GLASS_SOUND).setId("stainedGlass"));
      register(96, "trapdoor", new TrapdoorBlock(Material.WOOD).setStrength(3.0F).setSound(WOOD_SOUND).setId("trapdoor").disableStats());
      register(97, "monster_egg", new InfestedBlock().setStrength(0.75F).setId("monsterStoneEgg"));
      Block var6 = new StonebrickBlock().setStrength(1.5F).setResistance(10.0F).setSound(STONE_SOUND).setId("stonebricksmooth");
      register(98, "stonebrick", var6);
      register(99, "brown_mushroom_block", new MushroomBlock(Material.WOOD, 0).setStrength(0.2F).setSound(WOOD_SOUND).setId("mushroom"));
      register(100, "red_mushroom_block", new MushroomBlock(Material.WOOD, 1).setStrength(0.2F).setSound(WOOD_SOUND).setId("mushroom"));
      register(101, "iron_bars", new PaneBlock(Material.IRON, true).setStrength(5.0F).setResistance(10.0F).setSound(RAIL_SOUND).setId("fenceIron"));
      register(102, "glass_pane", new PaneBlock(Material.GLASS, false).setStrength(0.3F).setSound(GLASS_SOUND).setId("thinGlass"));
      Block var7 = new MelonBlock().setStrength(1.0F).setSound(WOOD_SOUND).setId("melon");
      register(103, "melon_block", var7);
      register(104, "pumpkin_stem", new StemBlock(var5).setStrength(0.0F).setSound(WOOD_SOUND).setId("pumpkinStem"));
      register(105, "melon_stem", new StemBlock(var7).setStrength(0.0F).setSound(WOOD_SOUND).setId("pumpkinStem"));
      register(106, "vine", new VineBlock().setStrength(0.2F).setSound(GRASS_SOUND).setId("vine"));
      register(107, "fence_gate", new FenceGateBlock().setStrength(2.0F).setResistance(5.0F).setSound(WOOD_SOUND).setId("fenceGate"));
      register(108, "brick_stairs", new StairsBlock(var3.defaultState()).setId("stairsBrick"));
      register(
         109,
         "stone_brick_stairs",
         new StairsBlock(var6.defaultState().set(StonebrickBlock.VARIANT, StonebrickBlock.Variant.DEFAULT)).setId("stairsStoneBrickSmooth")
      );
      register(110, "mycelium", new MyceliumBlock().setStrength(0.6F).setSound(GRASS_SOUND).setId("mycel"));
      register(111, "waterlily", new LilyPadBlock().setStrength(0.0F).setSound(GRASS_SOUND).setId("waterlily"));
      Block var8 = new NetherBrickBlock()
         .setStrength(2.0F)
         .setResistance(10.0F)
         .setSound(STONE_SOUND)
         .setId("netherBrick")
         .setItemGroup(ItemGroup.BUILDING_BLOCKS);
      register(112, "nether_brick", var8);
      register(113, "nether_brick_fence", new FenceBlock(Material.STONE).setStrength(2.0F).setResistance(10.0F).setSound(STONE_SOUND).setId("netherFence"));
      register(114, "nether_brick_stairs", new StairsBlock(var8.defaultState()).setId("stairsNetherBrick"));
      register(115, "nether_wart", new NetherWartBlock().setId("netherStalk"));
      register(116, "enchanting_table", new EnchantingTableBlock().setStrength(5.0F).setResistance(2000.0F).setId("enchantmentTable"));
      register(117, "brewing_stand", new BrewingStandBlock().setStrength(0.5F).setLightLevel(0.125F).setId("brewingStand"));
      register(118, "cauldron", new CauldronBlock().setStrength(2.0F).setId("cauldron"));
      register(119, "end_portal", new EndPortalBlock(Material.PORTAL).setStrength(-1.0F).setResistance(6000000.0F));
      register(
         120,
         "end_portal_frame",
         new EndPortalFrameBlock()
            .setSound(GLASS_SOUND)
            .setLightLevel(0.125F)
            .setStrength(-1.0F)
            .setId("endPortalFrame")
            .setResistance(6000000.0F)
            .setItemGroup(ItemGroup.DECORATIONS)
      );
      register(
         121,
         "end_stone",
         new Block(Material.STONE).setStrength(3.0F).setResistance(15.0F).setSound(STONE_SOUND).setId("whiteStone").setItemGroup(ItemGroup.BUILDING_BLOCKS)
      );
      register(122, "dragon_egg", new DragonEggBlock().setStrength(3.0F).setResistance(15.0F).setSound(STONE_SOUND).setLightLevel(0.125F).setId("dragonEgg"));
      register(
         123, "redstone_lamp", new RedstoneLampBlock(false).setStrength(0.3F).setSound(GLASS_SOUND).setId("redstoneLight").setItemGroup(ItemGroup.REDSTONE)
      );
      register(124, "lit_redstone_lamp", new RedstoneLampBlock(true).setStrength(0.3F).setSound(GLASS_SOUND).setId("redstoneLight"));
      register(125, "double_wooden_slab", new DoubleWoodenSlabBlock().setStrength(2.0F).setResistance(5.0F).setSound(WOOD_SOUND).setId("woodSlab"));
      register(126, "wooden_slab", new SingleWoodenSlabBlock().setStrength(2.0F).setResistance(5.0F).setSound(WOOD_SOUND).setId("woodSlab"));
      register(127, "cocoa", new CocoaBlock().setStrength(0.2F).setResistance(5.0F).setSound(WOOD_SOUND).setId("cocoa"));
      register(128, "sandstone_stairs", new StairsBlock(var2.defaultState().set(SandstoneBlock.TYPE, SandstoneBlock.Type.SMOOTH)).setId("stairsSandStone"));
      register(129, "emerald_ore", new OreBlock().setStrength(3.0F).setResistance(5.0F).setSound(STONE_SOUND).setId("oreEmerald"));
      register(
         130, "ender_chest", new EnderChestBlock().setStrength(22.5F).setResistance(1000.0F).setSound(STONE_SOUND).setId("enderChest").setLightLevel(0.5F)
      );
      register(131, "tripwire_hook", new TripwireHookBlock().setId("tripWireSource"));
      register(132, "tripwire", new TripwireBlock().setId("tripWire"));
      register(133, "emerald_block", new MineralBlock(MaterialColor.EMERALD).setStrength(5.0F).setResistance(10.0F).setSound(RAIL_SOUND).setId("blockEmerald"));
      register(134, "spruce_stairs", new StairsBlock(var1.defaultState().set(PlanksBlock.VARIANT, PlanksBlock.Variant.SPRUCE)).setId("stairsWoodSpruce"));
      register(135, "birch_stairs", new StairsBlock(var1.defaultState().set(PlanksBlock.VARIANT, PlanksBlock.Variant.BIRCH)).setId("stairsWoodBirch"));
      register(136, "jungle_stairs", new StairsBlock(var1.defaultState().set(PlanksBlock.VARIANT, PlanksBlock.Variant.JUNGLE)).setId("stairsWoodJungle"));
      register(137, "command_block", new CommandBlock().setUnbreakable().setResistance(6000000.0F).setId("commandBlock"));
      register(138, "beacon", new BeaconBlock().setId("beacon").setLightLevel(1.0F));
      register(139, "cobblestone_wall", new WallBlock(var0).setId("cobbleWall"));
      register(140, "flower_pot", new FlowerPotBlock().setStrength(0.0F).setSound(ORE_SOUND).setId("flowerPot"));
      register(141, "carrots", new CarrotsBlock().setId("carrots"));
      register(142, "potatoes", new PotatoesBlock().setId("potatoes"));
      register(143, "wooden_button", new WoodenButtonBlock().setStrength(0.5F).setSound(WOOD_SOUND).setId("button"));
      register(144, "skull", new SkullBlock().setStrength(1.0F).setSound(STONE_SOUND).setId("skull"));
      register(145, "anvil", new AnvilBlock().setStrength(5.0F).setSound(ANVIL_SOUND).setResistance(2000.0F).setId("anvil"));
      register(146, "trapped_chest", new ChestBlock(1).setStrength(2.5F).setSound(WOOD_SOUND).setId("chestTrap"));
      register(
         147,
         "light_weighted_pressure_plate",
         new WeightedPressurePlateBlock("gold_block", Material.IRON, 15).setStrength(0.5F).setSound(WOOD_SOUND).setId("weightedPlate_light")
      );
      register(
         148,
         "heavy_weighted_pressure_plate",
         new WeightedPressurePlateBlock("iron_block", Material.IRON, 150).setStrength(0.5F).setSound(WOOD_SOUND).setId("weightedPlate_heavy")
      );
      register(149, "unpowered_comparator", new ComparatorBlock(false).setStrength(0.0F).setSound(WOOD_SOUND).setId("comparator").disableStats());
      register(
         150, "powered_comparator", new ComparatorBlock(true).setStrength(0.0F).setLightLevel(0.625F).setSound(WOOD_SOUND).setId("comparator").disableStats()
      );
      register(151, "daylight_detector", new DaylightDetectorBlock().setStrength(0.2F).setSound(WOOD_SOUND).setId("daylightDetector"));
      register(152, "redstone_block", new RedstoneBlock(MaterialColor.LAVA).setStrength(5.0F).setResistance(10.0F).setSound(RAIL_SOUND).setId("blockRedstone"));
      register(153, "quartz_ore", new OreBlock().setStrength(3.0F).setResistance(5.0F).setSound(STONE_SOUND).setId("netherquartz"));
      register(154, "hopper", new HopperBlock().setStrength(3.0F).setResistance(8.0F).setSound(WOOD_SOUND).setId("hopper"));
      Block var9 = new QuartzBlock().setSound(STONE_SOUND).setStrength(0.8F).setId("quartzBlock");
      register(155, "quartz_block", var9);
      register(156, "quartz_stairs", new StairsBlock(var9.defaultState().set(QuartzBlock.VARIANT, QuartzBlock.Variant.DEFAULT)).setId("stairsQuartz"));
      register(157, "activator_rail", new PoweredRailBlock().setStrength(0.7F).setSound(RAIL_SOUND).setId("activatorRail"));
      register(158, "dropper", new DropperBlock().setStrength(3.5F).setSound(STONE_SOUND).setId("dropper"));
      register(
         159,
         "stained_hardened_clay",
         new ColoredBlock(Material.STONE).setStrength(1.25F).setResistance(7.0F).setSound(STONE_SOUND).setId("clayHardenedStained")
      );
      register(160, "stained_glass_pane", new StainedGlassPaneBlock().setStrength(0.3F).setSound(GLASS_SOUND).setId("thinStainedGlass"));
      register(161, "leaves2", new Leaves2Block().setId("leaves"));
      register(162, "log2", new Log2Block().setId("log"));
      register(163, "acacia_stairs", new StairsBlock(var1.defaultState().set(PlanksBlock.VARIANT, PlanksBlock.Variant.ACACIA)).setId("stairsWoodAcacia"));
      register(164, "dark_oak_stairs", new StairsBlock(var1.defaultState().set(PlanksBlock.VARIANT, PlanksBlock.Variant.DARK_OAK)).setId("stairsWoodDarkOak"));
      register(165, "slime", new SlimeBlock().setId("slime"));
      register(166, "barrier", new BarrierBlock().setId("barrier"));
      register(167, "iron_trapdoor", new TrapdoorBlock(Material.IRON).setStrength(5.0F).setSound(RAIL_SOUND).setId("ironTrapdoor").disableStats());
      register(168, "prismarine", new PrismarineBlock().setStrength(1.5F).setResistance(10.0F).setSound(STONE_SOUND).setId("prismarine"));
      register(169, "sea_lantern", new SeaLanternBlock(Material.GLASS).setStrength(0.3F).setSound(GLASS_SOUND).setLightLevel(1.0F).setId("seaLantern"));
      register(170, "hay_block", new HayBlock().setStrength(0.5F).setSound(GRASS_SOUND).setId("hayBlock").setItemGroup(ItemGroup.BUILDING_BLOCKS));
      register(171, "carpet", new CarpetBlock().setStrength(0.1F).setSound(CLOTH_SOUND).setId("woolCarpet").setOpacity(0));
      register(172, "hardened_clay", new HardenedClayBlock().setStrength(1.25F).setResistance(7.0F).setSound(STONE_SOUND).setId("clayHardened"));
      register(
         173,
         "coal_block",
         new Block(Material.STONE).setStrength(5.0F).setResistance(10.0F).setSound(STONE_SOUND).setId("blockCoal").setItemGroup(ItemGroup.BUILDING_BLOCKS)
      );
      register(174, "packed_ice", new PackedIceBlock().setStrength(0.5F).setSound(GLASS_SOUND).setId("icePacked"));
      register(175, "double_plant", new DoublePlantBlock());
      register(176, "standing_banner", new BannerBlock.Standing().setStrength(1.0F).setSound(WOOD_SOUND).setId("banner").disableStats());
      register(177, "wall_banner", new BannerBlock.Wall().setStrength(1.0F).setSound(WOOD_SOUND).setId("banner").disableStats());
      REGISTRY.validate();

      for(Block var11 : REGISTRY) {
         if (var11.material == Material.AIR) {
            var11.useNeighborLight = false;
         } else {
            boolean var12 = false;
            boolean var13 = var11 instanceof StairsBlock;
            boolean var14 = var11 instanceof SlabBlock;
            boolean var15 = var11 == var4;
            boolean var16 = var11.isTranslucent;
            boolean var17 = var11.opacity == 0;
            if (var13 || var14 || var15 || var16 || var17) {
               var12 = true;
            }

            var11.useNeighborLight = var12;
         }
      }

      for(Block var19 : REGISTRY) {
         for(BlockState var21 : var19.stateDefinition().all()) {
            int var22 = REGISTRY.getId(var19) << 4 | var19.getMetadataFromState(var21);
            STATE_REGISTRY.put(var21, var22);
         }
      }
   }

   private static void register(int rawId, Identifier id, Block block) {
      REGISTRY.register(rawId, id, block);
   }

   private static void register(int rawId, String name, Block block) {
      register(rawId, new Identifier(name), block);
   }

   @Environment(EnvType.CLIENT)
   public static enum OffsetType {
      NONE,
      XZ,
      XYZ;
   }

   public static class Sound {
      public final String id;
      public final float volume;
      public final float pitch;

      public Sound(String id, float volume, float pitch) {
         this.id = id;
         this.volume = volume;
         this.pitch = pitch;
      }

      public float getVolume() {
         return this.volume;
      }

      public float getPitch() {
         return this.pitch;
      }

      public String getDigSound() {
         return "dig." + this.id;
      }

      public String getStepSound() {
         return "step." + this.id;
      }

      public String getSound() {
         return this.getDigSound();
      }
   }
}
