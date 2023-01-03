package net.minecraft.entity.decoration;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class PaintingEntity extends DecorationEntity {
   public PaintingEntity.Motive motive;

   public PaintingEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
   }

   public PaintingEntity(World c_54ruxjwzt, BlockPos c_76varpwca, Direction c_69garkogr) {
      super(c_54ruxjwzt, c_76varpwca);
      ArrayList var4 = Lists.newArrayList();

      for(PaintingEntity.Motive var8 : PaintingEntity.Motive.values()) {
         this.motive = var8;
         this.setDirection(c_69garkogr);
         if (this.isPosValid()) {
            var4.add(var8);
         }
      }

      if (!var4.isEmpty()) {
         this.motive = (PaintingEntity.Motive)var4.get(this.random.nextInt(var4.size()));
      }

      this.setDirection(c_69garkogr);
   }

   @Environment(EnvType.CLIENT)
   public PaintingEntity(World world, BlockPos x, Direction y, String z) {
      this(world, x, y);

      for(PaintingEntity.Motive var8 : PaintingEntity.Motive.values()) {
         if (var8.name.equals(z)) {
            this.motive = var8;
            break;
         }
      }

      this.setDirection(y);
   }

   @Override
   public void writeCustomNbt(NbtCompound nbt) {
      nbt.putString("Motive", this.motive.name);
      super.writeCustomNbt(nbt);
   }

   @Override
   public void readCustomNbt(NbtCompound nbt) {
      String var2 = nbt.getString("Motive");

      for(PaintingEntity.Motive var6 : PaintingEntity.Motive.values()) {
         if (var6.name.equals(var2)) {
            this.motive = var6;
         }
      }

      if (this.motive == null) {
         this.motive = PaintingEntity.Motive.KEBAB;
      }

      super.readCustomNbt(nbt);
   }

   @Override
   public int getWidth() {
      return this.motive.width;
   }

   @Override
   public int getHeight() {
      return this.motive.height;
   }

   @Override
   public void onAttack(Entity entity) {
      if (entity instanceof PlayerEntity) {
         PlayerEntity var2 = (PlayerEntity)entity;
         if (var2.abilities.creativeMode) {
            return;
         }
      }

      this.dropItem(new ItemStack(Items.PAINTING), 0.0F);
   }

   @Override
   public void refreshPositionAndAngles(double x, double y, double z, float yaw, float pitch) {
      BlockPos var9 = new BlockPos(x - this.x, y - this.y, z - this.z);
      BlockPos var10 = this.pos.add(var9);
      this.setPosition((double)var10.getX(), (double)var10.getY(), (double)var10.getZ());
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void updatePositionAndAngles(double x, double y, double z, float yaw, float pitch, int steps) {
      BlockPos var10 = new BlockPos(x - this.x, y - this.y, z - this.z);
      BlockPos var11 = this.pos.add(var10);
      this.setPosition((double)var11.getX(), (double)var11.getY(), (double)var11.getZ());
   }

   public static enum Motive {
      KEBAB("Kebab", 16, 16, 0, 0),
      AZTEC("Aztec", 16, 16, 16, 0),
      ALBAN("Alban", 16, 16, 32, 0),
      AZTEC2("Aztec2", 16, 16, 48, 0),
      BOMB("Bomb", 16, 16, 64, 0),
      PLANT("Plant", 16, 16, 80, 0),
      WASTELAND("Wasteland", 16, 16, 96, 0),
      POOL("Pool", 32, 16, 0, 32),
      COURBET("Courbet", 32, 16, 32, 32),
      SEA("Sea", 32, 16, 64, 32),
      SUNSET("Sunset", 32, 16, 96, 32),
      CREEBET("Creebet", 32, 16, 128, 32),
      WANDERER("Wanderer", 16, 32, 0, 64),
      GRAHAM("Graham", 16, 32, 16, 64),
      MATCH("Match", 32, 32, 0, 128),
      BUST("Bust", 32, 32, 32, 128),
      STAGE("Stage", 32, 32, 64, 128),
      VOID("Void", 32, 32, 96, 128),
      SKULL_AND_ROSES("SkullAndRoses", 32, 32, 128, 128),
      WTIHER("Wither", 32, 32, 160, 128),
      FIGHTERS("Fighters", 64, 32, 0, 96),
      POINTER("Pointer", 64, 64, 0, 192),
      PIGSCENE("Pigscene", 64, 64, 64, 192),
      BURNING_SKULL("BurningSkull", 64, 64, 128, 192),
      SKELETON("Skeleton", 64, 48, 192, 64),
      DONKEY_KONG("DonkeyKong", 64, 48, 192, 112);

      public static final int SKULL_AND_ROSES_LENGTH = "SkullAndRoses".length();
      public final String name;
      public final int width;
      public final int height;
      public final int widthOffset;
      public final int heightOffset;

      private Motive(String name, int width, int height, int l, int m) {
         this.name = name;
         this.width = width;
         this.height = height;
         this.widthOffset = l;
         this.heightOffset = m;
      }
   }
}
