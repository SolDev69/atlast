package net.minecraft.block.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.crash.CashReportCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BlockEntity {
   private static final Logger LOGGER = LogManager.getLogger();
   private static Map ID_TO_TYPE = Maps.newHashMap();
   private static Map TYPE_TO_ID = Maps.newHashMap();
   protected World world;
   protected BlockPos pos = BlockPos.ORIGIN;
   protected boolean removed;
   private int cachedMetadata = -1;
   protected Block cachedBlock;

   private static void register(Class type, String id) {
      if (ID_TO_TYPE.containsKey(id)) {
         throw new IllegalArgumentException("Duplicate id: " + id);
      } else {
         ID_TO_TYPE.put(id, type);
         TYPE_TO_ID.put(type, id);
      }
   }

   public World getWorld() {
      return this.world;
   }

   public void setWorld(World world) {
      this.world = world;
   }

   public boolean hasWorld() {
      return this.world != null;
   }

   public void readNbt(NbtCompound nbt) {
      this.pos = new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
   }

   public void writeNbt(NbtCompound nbt) {
      String var2 = (String)TYPE_TO_ID.get(this.getClass());
      if (var2 == null) {
         throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
      } else {
         nbt.putString("id", var2);
         nbt.putInt("x", this.pos.getX());
         nbt.putInt("y", this.pos.getY());
         nbt.putInt("z", this.pos.getZ());
      }
   }

   public static BlockEntity fromNbt(NbtCompound nbt) {
      BlockEntity var1 = null;

      try {
         Class var2 = (Class)ID_TO_TYPE.get(nbt.getString("id"));
         if (var2 != null) {
            var1 = (BlockEntity)var2.newInstance();
         }
      } catch (Exception var3) {
         var3.printStackTrace();
      }

      if (var1 != null) {
         var1.readNbt(nbt);
      } else {
         LOGGER.warn("Skipping BlockEntity with id " + nbt.getString("id"));
      }

      return var1;
   }

   public int getCachedMetadata() {
      if (this.cachedMetadata == -1) {
         BlockState var1 = this.world.getBlockState(this.pos);
         this.cachedMetadata = var1.getBlock().getMetadataFromState(var1);
      }

      return this.cachedMetadata;
   }

   public void markDirty() {
      if (this.world != null) {
         BlockState var1 = this.world.getBlockState(this.pos);
         this.cachedMetadata = var1.getBlock().getMetadataFromState(var1);
         this.world.onBlockEntityChanged(this.pos, this);
         if (this.getCachedBlock() != Blocks.AIR) {
            this.world.updateComparators(this.pos, this.getCachedBlock());
         }
      }
   }

   @Environment(EnvType.CLIENT)
   public double squaredDistanceTo(double x, double y, double z) {
      double var7 = (double)this.pos.getX() + 0.5 - x;
      double var9 = (double)this.pos.getY() + 0.5 - y;
      double var11 = (double)this.pos.getZ() + 0.5 - z;
      return var7 * var7 + var9 * var9 + var11 * var11;
   }

   @Environment(EnvType.CLIENT)
   public double getSquaredViewDistance() {
      return 4096.0;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public Block getCachedBlock() {
      if (this.cachedBlock == null) {
         this.cachedBlock = this.world.getBlockState(this.pos).getBlock();
      }

      return this.cachedBlock;
   }

   public Packet createUpdatePacket() {
      return null;
   }

   public boolean isRemoved() {
      return this.removed;
   }

   public void markRemoved() {
      this.removed = true;
   }

   public void cancelRemoval() {
      this.removed = false;
   }

   public boolean doEvent(int type, int data) {
      return false;
   }

   public void clearBlockCache() {
      this.cachedBlock = null;
      this.cachedMetadata = -1;
   }

   public void populateCrashReport(CashReportCategory category) {
      category.add("Name", new Callable() {
         public String call() {
            return (String)BlockEntity.TYPE_TO_ID.get(BlockEntity.this.getClass()) + " // " + BlockEntity.this.getClass().getCanonicalName();
         }
      });
      if (this.world != null) {
         CashReportCategory.addBlockDetails(category, this.pos, this.getCachedBlock(), this.getCachedMetadata());
         category.add("Actual block type", new Callable() {
            public String call() {
               int var1 = Block.getRawId(BlockEntity.this.world.getBlockState(BlockEntity.this.pos).getBlock());

               try {
                  return String.format("ID #%d (%s // %s)", var1, Block.byRawId(var1).getTranslationKey(), Block.byRawId(var1).getClass().getCanonicalName());
               } catch (Throwable var3) {
                  return "ID #" + var1;
               }
            }
         });
         category.add("Actual block data value", new Callable() {
            public String call() {
               BlockState var1 = BlockEntity.this.world.getBlockState(BlockEntity.this.pos);
               int var2 = var1.getBlock().getMetadataFromState(var1);
               if (var2 < 0) {
                  return "Unknown? (Got " + var2 + ")";
               } else {
                  String var3 = String.format("%4s", Integer.toBinaryString(var2)).replace(" ", "0");
                  return String.format("%1$d / 0x%1$X / 0b%2$s", var2, var3);
               }
            }
         });
      }
   }

   public void setPos(BlockPos pos) {
      this.pos = pos;
   }

   static {
      register(FurnaceBlockEntity.class, "Furnace");
      register(ChestBlockEntity.class, "Chest");
      register(EnderChestBlockEntity.class, "EnderChest");
      register(JukeboxBlock.JukeboxBlockEntity.class, "RecordPlayer");
      register(DispenserBlockEntity.class, "Trap");
      register(DropperBlockEntity.class, "Dropper");
      register(SignBlockEntity.class, "Sign");
      register(MobSpawnerBlockEntity.class, "MobSpawner");
      register(NoteBlockBlockEntity.class, "Music");
      register(MovingBlockEntity.class, "Piston");
      register(BrewingStandBlockEntity.class, "Cauldron");
      register(EnchantingTableBlockEntity.class, "EnchantTable");
      register(EndPortalBlockEntity.class, "Airportal");
      register(CommandBlockBlockEntity.class, "Control");
      register(BeaconBlockEntity.class, "Beacon");
      register(SkullBlockEntity.class, "Skull");
      register(DaylightDetectorBlockEntity.class, "DLDetector");
      register(HopperBlockEntity.class, "Hopper");
      register(ComparatorBlockEntity.class, "Comparator");
      register(FlowerPotBlockEntity.class, "FlowerPot");
      register(BannerBlockEntity.class, "Banner");
   }
}
