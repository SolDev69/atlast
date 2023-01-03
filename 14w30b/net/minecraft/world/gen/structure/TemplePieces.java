package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.PlanksBlock;
import net.minecraft.block.SandstoneBlock;
import net.minecraft.block.StoneSlabBlock;
import net.minecraft.block.StonebrickBlock;
import net.minecraft.block.TripwireBlock;
import net.minecraft.block.TripwireHookBlock;
import net.minecraft.entity.living.mob.hostile.WitchEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class TemplePieces {
   public static void register() {
      StructureManager.registerPiece(TemplePieces.DesertPyramid.class, "TeDP");
      StructureManager.registerPiece(TemplePieces.JungleTemple.class, "TeJP");
      StructureManager.registerPiece(TemplePieces.WitchHut.class, "TeSH");
   }

   public static class DesertPyramid extends TemplePieces.ScatteredStructurePiece {
      private boolean[] hasChest = new boolean[4];
      private static final List LOOT_ENTRIES = Lists.newArrayList(
         new LootEntry[]{
            new LootEntry(Items.DIAMOND, 0, 1, 3, 3),
            new LootEntry(Items.IRON_INGOT, 0, 1, 5, 10),
            new LootEntry(Items.GOLD_INGOT, 0, 2, 7, 15),
            new LootEntry(Items.EMERALD, 0, 1, 3, 2),
            new LootEntry(Items.BONE, 0, 4, 6, 20),
            new LootEntry(Items.ROTTEN_FLESH, 0, 3, 7, 16),
            new LootEntry(Items.SADDLE, 0, 1, 1, 3),
            new LootEntry(Items.IRON_HORSE_ARMOR, 0, 1, 1, 1),
            new LootEntry(Items.GOLDEN_HORSE_ARMOR, 0, 1, 1, 1),
            new LootEntry(Items.DIAMOND_HORSE_ARMOR, 0, 1, 1, 1)
         }
      );

      public DesertPyramid() {
      }

      public DesertPyramid(Random random, int x, int z) {
         super(random, x, 64, z, 21, 15, 21);
      }

      @Override
      protected void writeNbt(NbtCompound nbt) {
         super.writeNbt(nbt);
         nbt.putBoolean("hasPlacedChest0", this.hasChest[0]);
         nbt.putBoolean("hasPlacedChest1", this.hasChest[1]);
         nbt.putBoolean("hasPlacedChest2", this.hasChest[2]);
         nbt.putBoolean("hasPlacedChest3", this.hasChest[3]);
      }

      @Override
      protected void readNbt(NbtCompound nbt) {
         super.readNbt(nbt);
         this.hasChest[0] = nbt.getBoolean("hasPlacedChest0");
         this.hasChest[1] = nbt.getBoolean("hasPlacedChest1");
         this.hasChest[2] = nbt.getBoolean("hasPlacedChest2");
         this.hasChest[3] = nbt.getBoolean("hasPlacedChest3");
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         this.fillWithOutline(world, box, 0, -4, 0, this.width - 1, 0, this.depth - 1, Blocks.SANDSTONE.defaultState(), Blocks.SANDSTONE.defaultState(), false);

         for(int var4 = 1; var4 <= 9; ++var4) {
            this.fillWithOutline(
               world,
               box,
               var4,
               var4,
               var4,
               this.width - 1 - var4,
               var4,
               this.depth - 1 - var4,
               Blocks.SANDSTONE.defaultState(),
               Blocks.SANDSTONE.defaultState(),
               false
            );
            this.fillWithOutline(
               world,
               box,
               var4 + 1,
               var4,
               var4 + 1,
               this.width - 2 - var4,
               var4,
               this.depth - 2 - var4,
               Blocks.AIR.defaultState(),
               Blocks.AIR.defaultState(),
               false
            );
         }

         for(int var14 = 0; var14 < this.width; ++var14) {
            for(int var5 = 0; var5 < this.depth; ++var5) {
               byte var6 = -5;
               this.fillColumnDown(world, Blocks.SANDSTONE.defaultState(), var14, var6, var5, box);
            }
         }

         int var15 = this.postProcessBlockMetadata(Blocks.SANDSTONE_STAIRS, 3);
         int var16 = this.postProcessBlockMetadata(Blocks.SANDSTONE_STAIRS, 2);
         int var17 = this.postProcessBlockMetadata(Blocks.SANDSTONE_STAIRS, 0);
         int var7 = this.postProcessBlockMetadata(Blocks.SANDSTONE_STAIRS, 1);
         int var8 = ~DyeColor.ORANGE.getMetadata() & 15;
         int var9 = ~DyeColor.BLUE.getMetadata() & 15;
         this.fillWithOutline(world, box, 0, 0, 0, 4, 9, 4, Blocks.SANDSTONE.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, 1, 10, 1, 3, 10, 3, Blocks.SANDSTONE.defaultState(), Blocks.SANDSTONE.defaultState(), false);
         this.setBlockState(world, Blocks.SANDSTONE_STAIRS.getStateFromMetadata(var15), 2, 10, 0, box);
         this.setBlockState(world, Blocks.SANDSTONE_STAIRS.getStateFromMetadata(var16), 2, 10, 4, box);
         this.setBlockState(world, Blocks.SANDSTONE_STAIRS.getStateFromMetadata(var17), 0, 10, 2, box);
         this.setBlockState(world, Blocks.SANDSTONE_STAIRS.getStateFromMetadata(var7), 4, 10, 2, box);
         this.fillWithOutline(world, box, this.width - 5, 0, 0, this.width - 1, 9, 4, Blocks.SANDSTONE.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, this.width - 4, 10, 1, this.width - 2, 10, 3, Blocks.SANDSTONE.defaultState(), Blocks.SANDSTONE.defaultState(), false);
         this.setBlockState(world, Blocks.SANDSTONE_STAIRS.getStateFromMetadata(var15), this.width - 3, 10, 0, box);
         this.setBlockState(world, Blocks.SANDSTONE_STAIRS.getStateFromMetadata(var16), this.width - 3, 10, 4, box);
         this.setBlockState(world, Blocks.SANDSTONE_STAIRS.getStateFromMetadata(var17), this.width - 5, 10, 2, box);
         this.setBlockState(world, Blocks.SANDSTONE_STAIRS.getStateFromMetadata(var7), this.width - 1, 10, 2, box);
         this.fillWithOutline(world, box, 8, 0, 0, 12, 4, 4, Blocks.SANDSTONE.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, 9, 1, 0, 11, 3, 4, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()), 9, 1, 1, box);
         this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()), 9, 2, 1, box);
         this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()), 9, 3, 1, box);
         this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()), 10, 3, 1, box);
         this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()), 11, 3, 1, box);
         this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()), 11, 2, 1, box);
         this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()), 11, 1, 1, box);
         this.fillWithOutline(world, box, 4, 1, 1, 8, 3, 3, Blocks.SANDSTONE.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, 4, 1, 2, 8, 2, 2, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, 12, 1, 1, 16, 3, 3, Blocks.SANDSTONE.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, 12, 1, 2, 16, 2, 2, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, 5, 4, 5, this.width - 6, 4, this.depth - 6, Blocks.SANDSTONE.defaultState(), Blocks.SANDSTONE.defaultState(), false);
         this.fillWithOutline(world, box, 9, 4, 9, 11, 4, 11, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(
            world,
            box,
            8,
            1,
            8,
            8,
            3,
            8,
            Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()),
            Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()),
            false
         );
         this.fillWithOutline(
            world,
            box,
            12,
            1,
            8,
            12,
            3,
            8,
            Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()),
            Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()),
            false
         );
         this.fillWithOutline(
            world,
            box,
            8,
            1,
            12,
            8,
            3,
            12,
            Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()),
            Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()),
            false
         );
         this.fillWithOutline(
            world,
            box,
            12,
            1,
            12,
            12,
            3,
            12,
            Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()),
            Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()),
            false
         );
         this.fillWithOutline(world, box, 1, 1, 5, 4, 4, 11, Blocks.SANDSTONE.defaultState(), Blocks.SANDSTONE.defaultState(), false);
         this.fillWithOutline(world, box, this.width - 5, 1, 5, this.width - 2, 4, 11, Blocks.SANDSTONE.defaultState(), Blocks.SANDSTONE.defaultState(), false);
         this.fillWithOutline(world, box, 6, 7, 9, 6, 7, 11, Blocks.SANDSTONE.defaultState(), Blocks.SANDSTONE.defaultState(), false);
         this.fillWithOutline(world, box, this.width - 7, 7, 9, this.width - 7, 7, 11, Blocks.SANDSTONE.defaultState(), Blocks.SANDSTONE.defaultState(), false);
         this.fillWithOutline(
            world,
            box,
            5,
            5,
            9,
            5,
            7,
            11,
            Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()),
            Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()),
            false
         );
         this.fillWithOutline(
            world,
            box,
            this.width - 6,
            5,
            9,
            this.width - 6,
            7,
            11,
            Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()),
            Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()),
            false
         );
         this.setBlockState(world, Blocks.AIR.defaultState(), 5, 5, 10, box);
         this.setBlockState(world, Blocks.AIR.defaultState(), 5, 6, 10, box);
         this.setBlockState(world, Blocks.AIR.defaultState(), 6, 6, 10, box);
         this.setBlockState(world, Blocks.AIR.defaultState(), this.width - 6, 5, 10, box);
         this.setBlockState(world, Blocks.AIR.defaultState(), this.width - 6, 6, 10, box);
         this.setBlockState(world, Blocks.AIR.defaultState(), this.width - 7, 6, 10, box);
         this.fillWithOutline(world, box, 2, 4, 4, 2, 6, 4, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, this.width - 3, 4, 4, this.width - 3, 6, 4, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.setBlockState(world, Blocks.SANDSTONE_STAIRS.getStateFromMetadata(var15), 2, 4, 5, box);
         this.setBlockState(world, Blocks.SANDSTONE_STAIRS.getStateFromMetadata(var15), 2, 3, 4, box);
         this.setBlockState(world, Blocks.SANDSTONE_STAIRS.getStateFromMetadata(var15), this.width - 3, 4, 5, box);
         this.setBlockState(world, Blocks.SANDSTONE_STAIRS.getStateFromMetadata(var15), this.width - 3, 3, 4, box);
         this.fillWithOutline(world, box, 1, 1, 3, 2, 2, 3, Blocks.SANDSTONE.defaultState(), Blocks.SANDSTONE.defaultState(), false);
         this.fillWithOutline(world, box, this.width - 3, 1, 3, this.width - 2, 2, 3, Blocks.SANDSTONE.defaultState(), Blocks.SANDSTONE.defaultState(), false);
         this.setBlockState(world, Blocks.SANDSTONE_STAIRS.defaultState(), 1, 1, 2, box);
         this.setBlockState(world, Blocks.SANDSTONE_STAIRS.defaultState(), this.width - 2, 1, 2, box);
         this.setBlockState(world, Blocks.STONE_SLAB.getStateFromMetadata(StoneSlabBlock.Variant.SAND.getIndex()), 1, 2, 2, box);
         this.setBlockState(world, Blocks.STONE_SLAB.getStateFromMetadata(StoneSlabBlock.Variant.SAND.getIndex()), this.width - 2, 2, 2, box);
         this.setBlockState(world, Blocks.SANDSTONE_STAIRS.getStateFromMetadata(var7), 2, 1, 2, box);
         this.setBlockState(world, Blocks.SANDSTONE_STAIRS.getStateFromMetadata(var17), this.width - 3, 1, 2, box);
         this.fillWithOutline(world, box, 4, 3, 5, 4, 3, 18, Blocks.SANDSTONE.defaultState(), Blocks.SANDSTONE.defaultState(), false);
         this.fillWithOutline(world, box, this.width - 5, 3, 5, this.width - 5, 3, 17, Blocks.SANDSTONE.defaultState(), Blocks.SANDSTONE.defaultState(), false);
         this.fillWithOutline(world, box, 3, 1, 5, 4, 2, 16, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.fillWithOutline(world, box, this.width - 6, 1, 5, this.width - 5, 2, 16, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);

         for(int var10 = 5; var10 <= 17; var10 += 2) {
            this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()), 4, 1, var10, box);
            this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.CHISELED.getIndex()), 4, 2, var10, box);
            this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()), this.width - 5, 1, var10, box);
            this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.CHISELED.getIndex()), this.width - 5, 2, var10, box);
         }

         this.setBlockState(world, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var8), 10, 0, 7, box);
         this.setBlockState(world, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var8), 10, 0, 8, box);
         this.setBlockState(world, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var8), 9, 0, 9, box);
         this.setBlockState(world, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var8), 11, 0, 9, box);
         this.setBlockState(world, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var8), 8, 0, 10, box);
         this.setBlockState(world, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var8), 12, 0, 10, box);
         this.setBlockState(world, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var8), 7, 0, 10, box);
         this.setBlockState(world, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var8), 13, 0, 10, box);
         this.setBlockState(world, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var8), 9, 0, 11, box);
         this.setBlockState(world, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var8), 11, 0, 11, box);
         this.setBlockState(world, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var8), 10, 0, 12, box);
         this.setBlockState(world, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var8), 10, 0, 13, box);
         this.setBlockState(world, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var9), 10, 0, 10, box);

         for(int var18 = 0; var18 <= this.width - 1; var18 += this.width - 1) {
            this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()), var18, 2, 1, box);
            this.setBlockState(world, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var8), var18, 2, 2, box);
            this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()), var18, 2, 3, box);
            this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()), var18, 3, 1, box);
            this.setBlockState(world, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var8), var18, 3, 2, box);
            this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()), var18, 3, 3, box);
            this.setBlockState(world, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var8), var18, 4, 1, box);
            this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.CHISELED.getIndex()), var18, 4, 2, box);
            this.setBlockState(world, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var8), var18, 4, 3, box);
            this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()), var18, 5, 1, box);
            this.setBlockState(world, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var8), var18, 5, 2, box);
            this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()), var18, 5, 3, box);
            this.setBlockState(world, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var8), var18, 6, 1, box);
            this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.CHISELED.getIndex()), var18, 6, 2, box);
            this.setBlockState(world, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var8), var18, 6, 3, box);
            this.setBlockState(world, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var8), var18, 7, 1, box);
            this.setBlockState(world, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var8), var18, 7, 2, box);
            this.setBlockState(world, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var8), var18, 7, 3, box);
            this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()), var18, 8, 1, box);
            this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()), var18, 8, 2, box);
            this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()), var18, 8, 3, box);
         }

         for(int var19 = 2; var19 <= this.width - 3; var19 += this.width - 3 - 2) {
            this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()), var19 - 1, 2, 0, box);
            this.setBlockState(world, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var8), var19, 2, 0, box);
            this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()), var19 + 1, 2, 0, box);
            this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()), var19 - 1, 3, 0, box);
            this.setBlockState(world, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var8), var19, 3, 0, box);
            this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()), var19 + 1, 3, 0, box);
            this.setBlockState(world, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var8), var19 - 1, 4, 0, box);
            this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.CHISELED.getIndex()), var19, 4, 0, box);
            this.setBlockState(world, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var8), var19 + 1, 4, 0, box);
            this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()), var19 - 1, 5, 0, box);
            this.setBlockState(world, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var8), var19, 5, 0, box);
            this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()), var19 + 1, 5, 0, box);
            this.setBlockState(world, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var8), var19 - 1, 6, 0, box);
            this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.CHISELED.getIndex()), var19, 6, 0, box);
            this.setBlockState(world, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var8), var19 + 1, 6, 0, box);
            this.setBlockState(world, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var8), var19 - 1, 7, 0, box);
            this.setBlockState(world, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var8), var19, 7, 0, box);
            this.setBlockState(world, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var8), var19 + 1, 7, 0, box);
            this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()), var19 - 1, 8, 0, box);
            this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()), var19, 8, 0, box);
            this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()), var19 + 1, 8, 0, box);
         }

         this.fillWithOutline(
            world,
            box,
            8,
            4,
            0,
            12,
            6,
            0,
            Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()),
            Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()),
            false
         );
         this.setBlockState(world, Blocks.AIR.defaultState(), 8, 6, 0, box);
         this.setBlockState(world, Blocks.AIR.defaultState(), 12, 6, 0, box);
         this.setBlockState(world, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var8), 9, 5, 0, box);
         this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.CHISELED.getIndex()), 10, 5, 0, box);
         this.setBlockState(world, Blocks.STAINED_HARDENED_CLAY.getStateFromMetadata(var8), 11, 5, 0, box);
         this.fillWithOutline(
            world,
            box,
            8,
            -14,
            8,
            12,
            -11,
            12,
            Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()),
            Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()),
            false
         );
         this.fillWithOutline(
            world,
            box,
            8,
            -10,
            8,
            12,
            -10,
            12,
            Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.CHISELED.getIndex()),
            Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.CHISELED.getIndex()),
            false
         );
         this.fillWithOutline(
            world,
            box,
            8,
            -9,
            8,
            12,
            -9,
            12,
            Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()),
            Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()),
            false
         );
         this.fillWithOutline(world, box, 8, -8, 8, 12, -1, 12, Blocks.SANDSTONE.defaultState(), Blocks.SANDSTONE.defaultState(), false);
         this.fillWithOutline(world, box, 9, -11, 9, 11, -1, 11, Blocks.AIR.defaultState(), Blocks.AIR.defaultState(), false);
         this.setBlockState(world, Blocks.STONE_PRESSURE_PLATE.defaultState(), 10, -11, 10, box);
         this.fillWithOutline(world, box, 9, -13, 9, 11, -13, 11, Blocks.TNT.defaultState(), Blocks.AIR.defaultState(), false);
         this.setBlockState(world, Blocks.AIR.defaultState(), 8, -11, 10, box);
         this.setBlockState(world, Blocks.AIR.defaultState(), 8, -10, 10, box);
         this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.CHISELED.getIndex()), 7, -10, 10, box);
         this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()), 7, -11, 10, box);
         this.setBlockState(world, Blocks.AIR.defaultState(), 12, -11, 10, box);
         this.setBlockState(world, Blocks.AIR.defaultState(), 12, -10, 10, box);
         this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.CHISELED.getIndex()), 13, -10, 10, box);
         this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()), 13, -11, 10, box);
         this.setBlockState(world, Blocks.AIR.defaultState(), 10, -11, 8, box);
         this.setBlockState(world, Blocks.AIR.defaultState(), 10, -10, 8, box);
         this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.CHISELED.getIndex()), 10, -10, 7, box);
         this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()), 10, -11, 7, box);
         this.setBlockState(world, Blocks.AIR.defaultState(), 10, -11, 12, box);
         this.setBlockState(world, Blocks.AIR.defaultState(), 10, -10, 12, box);
         this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.CHISELED.getIndex()), 10, -10, 13, box);
         this.setBlockState(world, Blocks.SANDSTONE.getStateFromMetadata(SandstoneBlock.Type.SMOOTH.getIndex()), 10, -11, 13, box);

         for(Direction var11 : Direction.Plane.HORIZONTAL) {
            if (!this.hasChest[var11.getIdHorizontal()]) {
               int var12 = var11.getOffsetX() * 2;
               int var13 = var11.getOffsetZ() * 2;
               this.hasChest[var11.getIdHorizontal()] = this.placeChestWithLoot(
                  world,
                  box,
                  random,
                  10 + var12,
                  -11,
                  10 + var13,
                  LootEntry.addAll(LOOT_ENTRIES, Items.ENCHANTED_BOOK.getRandomChestEntry(random)),
                  2 + random.nextInt(5)
               );
            }
         }

         return true;
      }
   }

   public static class JungleTemple extends TemplePieces.ScatteredStructurePiece {
      private boolean hasMainChest;
      private boolean hasHiddenChest;
      private boolean hasPrimaryTrap;
      private boolean hasSecondaryTrap;
      private static final List TREASURE_LOOT_ENTRIES = Lists.newArrayList(
         new LootEntry[]{
            new LootEntry(Items.DIAMOND, 0, 1, 3, 3),
            new LootEntry(Items.IRON_INGOT, 0, 1, 5, 10),
            new LootEntry(Items.GOLD_INGOT, 0, 2, 7, 15),
            new LootEntry(Items.EMERALD, 0, 1, 3, 2),
            new LootEntry(Items.BONE, 0, 4, 6, 20),
            new LootEntry(Items.ROTTEN_FLESH, 0, 3, 7, 16),
            new LootEntry(Items.SADDLE, 0, 1, 1, 3),
            new LootEntry(Items.IRON_HORSE_ARMOR, 0, 1, 1, 1),
            new LootEntry(Items.GOLDEN_HORSE_ARMOR, 0, 1, 1, 1),
            new LootEntry(Items.DIAMOND_HORSE_ARMOR, 0, 1, 1, 1)
         }
      );
      private static final List TRAP_LOOT_ENTRIES = Lists.newArrayList(new LootEntry[]{new LootEntry(Items.ARROW, 0, 2, 7, 30)});
      private static TemplePieces.JungleTemple.CobblestonePicker COBBLESTONE_PICKER = new TemplePieces.JungleTemple.CobblestonePicker();

      public JungleTemple() {
      }

      public JungleTemple(Random random, int x, int z) {
         super(random, x, 64, z, 12, 10, 15);
      }

      @Override
      protected void writeNbt(NbtCompound nbt) {
         super.writeNbt(nbt);
         nbt.putBoolean("placedMainChest", this.hasMainChest);
         nbt.putBoolean("placedHiddenChest", this.hasHiddenChest);
         nbt.putBoolean("placedTrap1", this.hasPrimaryTrap);
         nbt.putBoolean("placedTrap2", this.hasSecondaryTrap);
      }

      @Override
      protected void readNbt(NbtCompound nbt) {
         super.readNbt(nbt);
         this.hasMainChest = nbt.getBoolean("placedMainChest");
         this.hasHiddenChest = nbt.getBoolean("placedHiddenChest");
         this.hasPrimaryTrap = nbt.getBoolean("placedTrap1");
         this.hasSecondaryTrap = nbt.getBoolean("placedTrap2");
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         if (!this.updateHPos(world, box, 0)) {
            return false;
         } else {
            int var4 = this.postProcessBlockMetadata(Blocks.STONE_STAIRS, 3);
            int var5 = this.postProcessBlockMetadata(Blocks.STONE_STAIRS, 2);
            int var6 = this.postProcessBlockMetadata(Blocks.STONE_STAIRS, 0);
            int var7 = this.postProcessBlockMetadata(Blocks.STONE_STAIRS, 1);
            this.fill(world, box, 0, -4, 0, this.width - 1, 0, this.depth - 1, false, random, COBBLESTONE_PICKER);
            this.fill(world, box, 2, 1, 2, 9, 2, 2, false, random, COBBLESTONE_PICKER);
            this.fill(world, box, 2, 1, 12, 9, 2, 12, false, random, COBBLESTONE_PICKER);
            this.fill(world, box, 2, 1, 3, 2, 2, 11, false, random, COBBLESTONE_PICKER);
            this.fill(world, box, 9, 1, 3, 9, 2, 11, false, random, COBBLESTONE_PICKER);
            this.fill(world, box, 1, 3, 1, 10, 6, 1, false, random, COBBLESTONE_PICKER);
            this.fill(world, box, 1, 3, 13, 10, 6, 13, false, random, COBBLESTONE_PICKER);
            this.fill(world, box, 1, 3, 2, 1, 6, 12, false, random, COBBLESTONE_PICKER);
            this.fill(world, box, 10, 3, 2, 10, 6, 12, false, random, COBBLESTONE_PICKER);
            this.fill(world, box, 2, 3, 2, 9, 3, 12, false, random, COBBLESTONE_PICKER);
            this.fill(world, box, 2, 6, 2, 9, 6, 12, false, random, COBBLESTONE_PICKER);
            this.fill(world, box, 3, 7, 3, 8, 7, 11, false, random, COBBLESTONE_PICKER);
            this.fill(world, box, 4, 8, 4, 7, 8, 10, false, random, COBBLESTONE_PICKER);
            this.fillAir(world, box, 3, 1, 3, 8, 2, 11);
            this.fillAir(world, box, 4, 3, 6, 7, 3, 9);
            this.fillAir(world, box, 2, 4, 2, 9, 5, 12);
            this.fillAir(world, box, 4, 6, 5, 7, 6, 9);
            this.fillAir(world, box, 5, 7, 6, 6, 7, 8);
            this.fillAir(world, box, 5, 1, 2, 6, 2, 2);
            this.fillAir(world, box, 5, 2, 12, 6, 2, 12);
            this.fillAir(world, box, 5, 5, 1, 6, 5, 1);
            this.fillAir(world, box, 5, 5, 13, 6, 5, 13);
            this.setBlockState(world, Blocks.AIR.defaultState(), 1, 5, 5, box);
            this.setBlockState(world, Blocks.AIR.defaultState(), 10, 5, 5, box);
            this.setBlockState(world, Blocks.AIR.defaultState(), 1, 5, 9, box);
            this.setBlockState(world, Blocks.AIR.defaultState(), 10, 5, 9, box);

            for(int var8 = 0; var8 <= 14; var8 += 14) {
               this.fill(world, box, 2, 4, var8, 2, 5, var8, false, random, COBBLESTONE_PICKER);
               this.fill(world, box, 4, 4, var8, 4, 5, var8, false, random, COBBLESTONE_PICKER);
               this.fill(world, box, 7, 4, var8, 7, 5, var8, false, random, COBBLESTONE_PICKER);
               this.fill(world, box, 9, 4, var8, 9, 5, var8, false, random, COBBLESTONE_PICKER);
            }

            this.fill(world, box, 5, 6, 0, 6, 6, 0, false, random, COBBLESTONE_PICKER);

            for(int var10 = 0; var10 <= 11; var10 += 11) {
               for(int var9 = 2; var9 <= 12; var9 += 2) {
                  this.fill(world, box, var10, 4, var9, var10, 5, var9, false, random, COBBLESTONE_PICKER);
               }

               this.fill(world, box, var10, 6, 5, var10, 6, 5, false, random, COBBLESTONE_PICKER);
               this.fill(world, box, var10, 6, 9, var10, 6, 9, false, random, COBBLESTONE_PICKER);
            }

            this.fill(world, box, 2, 7, 2, 2, 9, 2, false, random, COBBLESTONE_PICKER);
            this.fill(world, box, 9, 7, 2, 9, 9, 2, false, random, COBBLESTONE_PICKER);
            this.fill(world, box, 2, 7, 12, 2, 9, 12, false, random, COBBLESTONE_PICKER);
            this.fill(world, box, 9, 7, 12, 9, 9, 12, false, random, COBBLESTONE_PICKER);
            this.fill(world, box, 4, 9, 4, 4, 9, 4, false, random, COBBLESTONE_PICKER);
            this.fill(world, box, 7, 9, 4, 7, 9, 4, false, random, COBBLESTONE_PICKER);
            this.fill(world, box, 4, 9, 10, 4, 9, 10, false, random, COBBLESTONE_PICKER);
            this.fill(world, box, 7, 9, 10, 7, 9, 10, false, random, COBBLESTONE_PICKER);
            this.fill(world, box, 5, 9, 7, 6, 9, 7, false, random, COBBLESTONE_PICKER);
            this.setBlockState(world, Blocks.STONE_STAIRS.getStateFromMetadata(var4), 5, 9, 6, box);
            this.setBlockState(world, Blocks.STONE_STAIRS.getStateFromMetadata(var4), 6, 9, 6, box);
            this.setBlockState(world, Blocks.STONE_STAIRS.getStateFromMetadata(var5), 5, 9, 8, box);
            this.setBlockState(world, Blocks.STONE_STAIRS.getStateFromMetadata(var5), 6, 9, 8, box);
            this.setBlockState(world, Blocks.STONE_STAIRS.getStateFromMetadata(var4), 4, 0, 0, box);
            this.setBlockState(world, Blocks.STONE_STAIRS.getStateFromMetadata(var4), 5, 0, 0, box);
            this.setBlockState(world, Blocks.STONE_STAIRS.getStateFromMetadata(var4), 6, 0, 0, box);
            this.setBlockState(world, Blocks.STONE_STAIRS.getStateFromMetadata(var4), 7, 0, 0, box);
            this.setBlockState(world, Blocks.STONE_STAIRS.getStateFromMetadata(var4), 4, 1, 8, box);
            this.setBlockState(world, Blocks.STONE_STAIRS.getStateFromMetadata(var4), 4, 2, 9, box);
            this.setBlockState(world, Blocks.STONE_STAIRS.getStateFromMetadata(var4), 4, 3, 10, box);
            this.setBlockState(world, Blocks.STONE_STAIRS.getStateFromMetadata(var4), 7, 1, 8, box);
            this.setBlockState(world, Blocks.STONE_STAIRS.getStateFromMetadata(var4), 7, 2, 9, box);
            this.setBlockState(world, Blocks.STONE_STAIRS.getStateFromMetadata(var4), 7, 3, 10, box);
            this.fill(world, box, 4, 1, 9, 4, 1, 9, false, random, COBBLESTONE_PICKER);
            this.fill(world, box, 7, 1, 9, 7, 1, 9, false, random, COBBLESTONE_PICKER);
            this.fill(world, box, 4, 1, 10, 7, 2, 10, false, random, COBBLESTONE_PICKER);
            this.fill(world, box, 5, 4, 5, 6, 4, 5, false, random, COBBLESTONE_PICKER);
            this.setBlockState(world, Blocks.STONE_STAIRS.getStateFromMetadata(var6), 4, 4, 5, box);
            this.setBlockState(world, Blocks.STONE_STAIRS.getStateFromMetadata(var7), 7, 4, 5, box);

            for(int var11 = 0; var11 < 4; ++var11) {
               this.setBlockState(world, Blocks.STONE_STAIRS.getStateFromMetadata(var5), 5, 0 - var11, 6 + var11, box);
               this.setBlockState(world, Blocks.STONE_STAIRS.getStateFromMetadata(var5), 6, 0 - var11, 6 + var11, box);
               this.fillAir(world, box, 5, 0 - var11, 7 + var11, 6, 0 - var11, 9 + var11);
            }

            this.fillAir(world, box, 1, -3, 12, 10, -1, 13);
            this.fillAir(world, box, 1, -3, 1, 3, -1, 13);
            this.fillAir(world, box, 1, -3, 1, 9, -1, 5);

            for(int var12 = 1; var12 <= 13; var12 += 2) {
               this.fill(world, box, 1, -3, var12, 1, -2, var12, false, random, COBBLESTONE_PICKER);
            }

            for(int var13 = 2; var13 <= 12; var13 += 2) {
               this.fill(world, box, 1, -1, var13, 3, -1, var13, false, random, COBBLESTONE_PICKER);
            }

            this.fill(world, box, 2, -2, 1, 5, -2, 1, false, random, COBBLESTONE_PICKER);
            this.fill(world, box, 7, -2, 1, 9, -2, 1, false, random, COBBLESTONE_PICKER);
            this.fill(world, box, 6, -3, 1, 6, -3, 1, false, random, COBBLESTONE_PICKER);
            this.fill(world, box, 6, -1, 1, 6, -1, 1, false, random, COBBLESTONE_PICKER);
            this.setBlockState(
               world,
               Blocks.TRIPWIRE_HOOK
                  .getStateFromMetadata(this.postProcessBlockMetadata(Blocks.TRIPWIRE_HOOK, Direction.EAST.getIdHorizontal()))
                  .set(TripwireHookBlock.ATTACHED, true),
               1,
               -3,
               8,
               box
            );
            this.setBlockState(
               world,
               Blocks.TRIPWIRE_HOOK
                  .getStateFromMetadata(this.postProcessBlockMetadata(Blocks.TRIPWIRE_HOOK, Direction.WEST.getIdHorizontal()))
                  .set(TripwireHookBlock.ATTACHED, true),
               4,
               -3,
               8,
               box
            );
            this.setBlockState(world, Blocks.TRIPWIRE.defaultState().set(TripwireBlock.ATTACHED, true), 2, -3, 8, box);
            this.setBlockState(world, Blocks.TRIPWIRE.defaultState().set(TripwireBlock.ATTACHED, true), 3, -3, 8, box);
            this.setBlockState(world, Blocks.REDSTONE_WIRE.defaultState(), 5, -3, 7, box);
            this.setBlockState(world, Blocks.REDSTONE_WIRE.defaultState(), 5, -3, 6, box);
            this.setBlockState(world, Blocks.REDSTONE_WIRE.defaultState(), 5, -3, 5, box);
            this.setBlockState(world, Blocks.REDSTONE_WIRE.defaultState(), 5, -3, 4, box);
            this.setBlockState(world, Blocks.REDSTONE_WIRE.defaultState(), 5, -3, 3, box);
            this.setBlockState(world, Blocks.REDSTONE_WIRE.defaultState(), 5, -3, 2, box);
            this.setBlockState(world, Blocks.REDSTONE_WIRE.defaultState(), 5, -3, 1, box);
            this.setBlockState(world, Blocks.REDSTONE_WIRE.defaultState(), 4, -3, 1, box);
            this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.defaultState(), 3, -3, 1, box);
            if (!this.hasPrimaryTrap) {
               this.hasPrimaryTrap = this.placeDispenserWithLoot(world, box, random, 3, -2, 1, Direction.NORTH.getId(), TRAP_LOOT_ENTRIES, 2);
            }

            this.setBlockState(world, Blocks.VINE.getStateFromMetadata(15), 3, -2, 2, box);
            this.setBlockState(
               world,
               Blocks.TRIPWIRE_HOOK
                  .getStateFromMetadata(this.postProcessBlockMetadata(Blocks.TRIPWIRE_HOOK, Direction.NORTH.getIdHorizontal()))
                  .set(TripwireHookBlock.ATTACHED, true),
               7,
               -3,
               1,
               box
            );
            this.setBlockState(
               world,
               Blocks.TRIPWIRE_HOOK
                  .getStateFromMetadata(this.postProcessBlockMetadata(Blocks.TRIPWIRE_HOOK, Direction.SOUTH.getIdHorizontal()))
                  .set(TripwireHookBlock.ATTACHED, true),
               7,
               -3,
               5,
               box
            );
            this.setBlockState(world, Blocks.TRIPWIRE.defaultState().set(TripwireBlock.ATTACHED, true), 7, -3, 2, box);
            this.setBlockState(world, Blocks.TRIPWIRE.defaultState().set(TripwireBlock.ATTACHED, true), 7, -3, 3, box);
            this.setBlockState(world, Blocks.TRIPWIRE.defaultState().set(TripwireBlock.ATTACHED, true), 7, -3, 4, box);
            this.setBlockState(world, Blocks.REDSTONE_WIRE.defaultState(), 8, -3, 6, box);
            this.setBlockState(world, Blocks.REDSTONE_WIRE.defaultState(), 9, -3, 6, box);
            this.setBlockState(world, Blocks.REDSTONE_WIRE.defaultState(), 9, -3, 5, box);
            this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.defaultState(), 9, -3, 4, box);
            this.setBlockState(world, Blocks.REDSTONE_WIRE.defaultState(), 9, -2, 4, box);
            if (!this.hasSecondaryTrap) {
               this.hasSecondaryTrap = this.placeDispenserWithLoot(world, box, random, 9, -2, 3, Direction.WEST.getId(), TRAP_LOOT_ENTRIES, 2);
            }

            this.setBlockState(world, Blocks.VINE.getStateFromMetadata(15), 8, -1, 3, box);
            this.setBlockState(world, Blocks.VINE.getStateFromMetadata(15), 8, -2, 3, box);
            if (!this.hasMainChest) {
               this.hasMainChest = this.placeChestWithLoot(
                  world,
                  box,
                  random,
                  8,
                  -3,
                  3,
                  LootEntry.addAll(TREASURE_LOOT_ENTRIES, Items.ENCHANTED_BOOK.getRandomChestEntry(random)),
                  2 + random.nextInt(5)
               );
            }

            this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.defaultState(), 9, -3, 2, box);
            this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.defaultState(), 8, -3, 1, box);
            this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.defaultState(), 4, -3, 5, box);
            this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.defaultState(), 5, -2, 5, box);
            this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.defaultState(), 5, -1, 5, box);
            this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.defaultState(), 6, -3, 5, box);
            this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.defaultState(), 7, -2, 5, box);
            this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.defaultState(), 7, -1, 5, box);
            this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.defaultState(), 8, -3, 5, box);
            this.fill(world, box, 9, -1, 1, 9, -1, 5, false, random, COBBLESTONE_PICKER);
            this.fillAir(world, box, 8, -3, 8, 10, -1, 10);
            this.setBlockState(world, Blocks.STONE_BRICKS.getStateFromMetadata(StonebrickBlock.CHISELED_INDEX), 8, -2, 11, box);
            this.setBlockState(world, Blocks.STONE_BRICKS.getStateFromMetadata(StonebrickBlock.CHISELED_INDEX), 9, -2, 11, box);
            this.setBlockState(world, Blocks.STONE_BRICKS.getStateFromMetadata(StonebrickBlock.CHISELED_INDEX), 10, -2, 11, box);
            this.setBlockState(
               world,
               Blocks.LEVER
                  .getStateFromMetadata(LeverBlock.getMetadataForFacing(Direction.byId(this.postProcessBlockMetadata(Blocks.LEVER, Direction.NORTH.getId())))),
               8,
               -2,
               12,
               box
            );
            this.setBlockState(
               world,
               Blocks.LEVER
                  .getStateFromMetadata(LeverBlock.getMetadataForFacing(Direction.byId(this.postProcessBlockMetadata(Blocks.LEVER, Direction.NORTH.getId())))),
               9,
               -2,
               12,
               box
            );
            this.setBlockState(
               world,
               Blocks.LEVER
                  .getStateFromMetadata(LeverBlock.getMetadataForFacing(Direction.byId(this.postProcessBlockMetadata(Blocks.LEVER, Direction.NORTH.getId())))),
               10,
               -2,
               12,
               box
            );
            this.fill(world, box, 8, -3, 8, 8, -3, 10, false, random, COBBLESTONE_PICKER);
            this.fill(world, box, 10, -3, 8, 10, -3, 10, false, random, COBBLESTONE_PICKER);
            this.setBlockState(world, Blocks.MOSSY_COBBLESTONE.defaultState(), 10, -2, 9, box);
            this.setBlockState(world, Blocks.REDSTONE_WIRE.defaultState(), 8, -2, 9, box);
            this.setBlockState(world, Blocks.REDSTONE_WIRE.defaultState(), 8, -2, 10, box);
            this.setBlockState(world, Blocks.REDSTONE_WIRE.defaultState(), 10, -1, 9, box);
            this.setBlockState(world, Blocks.STICKY_PISTON.getStateFromMetadata(Direction.UP.getId()), 9, -2, 8, box);
            this.setBlockState(
               world, Blocks.STICKY_PISTON.getStateFromMetadata(this.postProcessBlockMetadata(Blocks.STICKY_PISTON, Direction.WEST.getId())), 10, -2, 8, box
            );
            this.setBlockState(
               world, Blocks.STICKY_PISTON.getStateFromMetadata(this.postProcessBlockMetadata(Blocks.STICKY_PISTON, Direction.WEST.getId())), 10, -1, 8, box
            );
            this.setBlockState(
               world, Blocks.REPEATER.getStateFromMetadata(this.postProcessBlockMetadata(Blocks.REPEATER, Direction.NORTH.getIdHorizontal())), 10, -2, 10, box
            );
            if (!this.hasHiddenChest) {
               this.hasHiddenChest = this.placeChestWithLoot(
                  world,
                  box,
                  random,
                  9,
                  -3,
                  10,
                  LootEntry.addAll(TREASURE_LOOT_ENTRIES, Items.ENCHANTED_BOOK.getRandomChestEntry(random)),
                  2 + random.nextInt(5)
               );
            }

            return true;
         }
      }

      static class CobblestonePicker extends StructurePiece.BlockPicker {
         private CobblestonePicker() {
         }

         @Override
         public void pick(Random randomm, int x, int y, int z, boolean isNonAir) {
            if (randomm.nextFloat() < 0.4F) {
               this.state = Blocks.COBBLESTONE.defaultState();
            } else {
               this.state = Blocks.MOSSY_COBBLESTONE.defaultState();
            }
         }
      }
   }

   abstract static class ScatteredStructurePiece extends StructurePiece {
      protected int width;
      protected int height;
      protected int depth;
      protected int hPos = -1;

      public ScatteredStructurePiece() {
      }

      protected ScatteredStructurePiece(Random random, int x, int y, int z, int width, int height, int depth) {
         super(0);
         this.width = width;
         this.height = height;
         this.depth = depth;
         this.facing = Direction.Plane.HORIZONTAL.pick(random);
         switch(this.facing) {
            case NORTH:
            case SOUTH:
               this.box = new StructureBox(x, y, z, x + width - 1, y + height - 1, z + depth - 1);
               break;
            default:
               this.box = new StructureBox(x, y, z, x + depth - 1, y + height - 1, z + width - 1);
         }
      }

      @Override
      protected void writeNbt(NbtCompound nbt) {
         nbt.putInt("Width", this.width);
         nbt.putInt("Height", this.height);
         nbt.putInt("Depth", this.depth);
         nbt.putInt("HPos", this.hPos);
      }

      @Override
      protected void readNbt(NbtCompound nbt) {
         this.width = nbt.getInt("Width");
         this.height = nbt.getInt("Height");
         this.depth = nbt.getInt("Depth");
         this.hPos = nbt.getInt("HPos");
      }

      protected boolean updateHPos(World world, StructureBox box, int offset) {
         if (this.hPos >= 0) {
            return true;
         } else {
            int var4 = 0;
            int var5 = 0;

            for(int var6 = this.box.minZ; var6 <= this.box.maxZ; ++var6) {
               for(int var7 = this.box.minX; var7 <= this.box.maxX; ++var7) {
                  BlockPos var8 = new BlockPos(var7, 64, var6);
                  if (box.contains(var8)) {
                     var4 += Math.max(world.getSurfaceHeight(var8).getY(), world.dimension.getMinSpawnY());
                     ++var5;
                  }
               }
            }

            if (var5 == 0) {
               return false;
            } else {
               this.hPos = var4 / var5;
               this.box.move(0, this.hPos - this.box.minY + offset, 0);
               return true;
            }
         }
      }
   }

   public static class WitchHut extends TemplePieces.ScatteredStructurePiece {
      private boolean hasWitch;

      public WitchHut() {
      }

      public WitchHut(Random random, int x, int z) {
         super(random, x, 64, z, 7, 5, 9);
      }

      @Override
      protected void writeNbt(NbtCompound nbt) {
         super.writeNbt(nbt);
         nbt.putBoolean("Witch", this.hasWitch);
      }

      @Override
      protected void readNbt(NbtCompound nbt) {
         super.readNbt(nbt);
         this.hasWitch = nbt.getBoolean("Witch");
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         if (!this.updateHPos(world, box, 0)) {
            return false;
         } else {
            this.fillWithOutline(
               world,
               box,
               1,
               1,
               1,
               5,
               1,
               7,
               Blocks.PLANKS.getStateFromMetadata(PlanksBlock.Variant.SPRUCE.getIndex()),
               Blocks.PLANKS.getStateFromMetadata(PlanksBlock.Variant.SPRUCE.getIndex()),
               false
            );
            this.fillWithOutline(
               world,
               box,
               1,
               4,
               2,
               5,
               4,
               7,
               Blocks.PLANKS.getStateFromMetadata(PlanksBlock.Variant.SPRUCE.getIndex()),
               Blocks.PLANKS.getStateFromMetadata(PlanksBlock.Variant.SPRUCE.getIndex()),
               false
            );
            this.fillWithOutline(
               world,
               box,
               2,
               1,
               0,
               4,
               1,
               0,
               Blocks.PLANKS.getStateFromMetadata(PlanksBlock.Variant.SPRUCE.getIndex()),
               Blocks.PLANKS.getStateFromMetadata(PlanksBlock.Variant.SPRUCE.getIndex()),
               false
            );
            this.fillWithOutline(
               world,
               box,
               2,
               2,
               2,
               3,
               3,
               2,
               Blocks.PLANKS.getStateFromMetadata(PlanksBlock.Variant.SPRUCE.getIndex()),
               Blocks.PLANKS.getStateFromMetadata(PlanksBlock.Variant.SPRUCE.getIndex()),
               false
            );
            this.fillWithOutline(
               world,
               box,
               1,
               2,
               3,
               1,
               3,
               6,
               Blocks.PLANKS.getStateFromMetadata(PlanksBlock.Variant.SPRUCE.getIndex()),
               Blocks.PLANKS.getStateFromMetadata(PlanksBlock.Variant.SPRUCE.getIndex()),
               false
            );
            this.fillWithOutline(
               world,
               box,
               5,
               2,
               3,
               5,
               3,
               6,
               Blocks.PLANKS.getStateFromMetadata(PlanksBlock.Variant.SPRUCE.getIndex()),
               Blocks.PLANKS.getStateFromMetadata(PlanksBlock.Variant.SPRUCE.getIndex()),
               false
            );
            this.fillWithOutline(
               world,
               box,
               2,
               2,
               7,
               4,
               3,
               7,
               Blocks.PLANKS.getStateFromMetadata(PlanksBlock.Variant.SPRUCE.getIndex()),
               Blocks.PLANKS.getStateFromMetadata(PlanksBlock.Variant.SPRUCE.getIndex()),
               false
            );
            this.fillWithOutline(world, box, 1, 0, 2, 1, 3, 2, Blocks.LOG.defaultState(), Blocks.LOG.defaultState(), false);
            this.fillWithOutline(world, box, 5, 0, 2, 5, 3, 2, Blocks.LOG.defaultState(), Blocks.LOG.defaultState(), false);
            this.fillWithOutline(world, box, 1, 0, 7, 1, 3, 7, Blocks.LOG.defaultState(), Blocks.LOG.defaultState(), false);
            this.fillWithOutline(world, box, 5, 0, 7, 5, 3, 7, Blocks.LOG.defaultState(), Blocks.LOG.defaultState(), false);
            this.setBlockState(world, Blocks.FENCE.defaultState(), 2, 3, 2, box);
            this.setBlockState(world, Blocks.FENCE.defaultState(), 3, 3, 7, box);
            this.setBlockState(world, Blocks.AIR.defaultState(), 1, 3, 4, box);
            this.setBlockState(world, Blocks.AIR.defaultState(), 5, 3, 4, box);
            this.setBlockState(world, Blocks.AIR.defaultState(), 5, 3, 5, box);
            this.setBlockState(world, Blocks.FLOWER_POT.defaultState().set(FlowerPotBlock.CONTENTS, FlowerPotBlock.Contents.MUSHROOM_RED), 1, 3, 5, box);
            this.setBlockState(world, Blocks.CRAFTING_TABLE.defaultState(), 3, 2, 6, box);
            this.setBlockState(world, Blocks.CAULDRON.defaultState(), 4, 2, 6, box);
            this.setBlockState(world, Blocks.FENCE.defaultState(), 1, 2, 1, box);
            this.setBlockState(world, Blocks.FENCE.defaultState(), 5, 2, 1, box);
            int var4 = this.postProcessBlockMetadata(Blocks.OAK_STAIRS, 3);
            int var5 = this.postProcessBlockMetadata(Blocks.OAK_STAIRS, 1);
            int var6 = this.postProcessBlockMetadata(Blocks.OAK_STAIRS, 0);
            int var7 = this.postProcessBlockMetadata(Blocks.OAK_STAIRS, 2);
            this.fillWithOutline(
               world, box, 0, 4, 1, 6, 4, 1, Blocks.SPRUCE_STAIRS.getStateFromMetadata(var4), Blocks.SPRUCE_STAIRS.getStateFromMetadata(var4), false
            );
            this.fillWithOutline(
               world, box, 0, 4, 2, 0, 4, 7, Blocks.SPRUCE_STAIRS.getStateFromMetadata(var6), Blocks.SPRUCE_STAIRS.getStateFromMetadata(var6), false
            );
            this.fillWithOutline(
               world, box, 6, 4, 2, 6, 4, 7, Blocks.SPRUCE_STAIRS.getStateFromMetadata(var5), Blocks.SPRUCE_STAIRS.getStateFromMetadata(var5), false
            );
            this.fillWithOutline(
               world, box, 0, 4, 8, 6, 4, 8, Blocks.SPRUCE_STAIRS.getStateFromMetadata(var7), Blocks.SPRUCE_STAIRS.getStateFromMetadata(var7), false
            );

            for(int var8 = 2; var8 <= 7; var8 += 5) {
               for(int var9 = 1; var9 <= 5; var9 += 4) {
                  this.fillColumnDown(world, Blocks.LOG.defaultState(), var9, -1, var8, box);
               }
            }

            if (!this.hasWitch) {
               int var12 = this.transformX(2, 5);
               int var13 = this.transformY(2);
               int var10 = this.transformZ(2, 5);
               if (box.contains(new BlockPos(var12, var13, var10))) {
                  this.hasWitch = true;
                  WitchEntity var11 = new WitchEntity(world);
                  var11.refreshPositionAndAngles((double)var12 + 0.5, (double)var13, (double)var10 + 0.5, 0.0F, 0.0F);
                  var11.initialize(world.getLocalDifficulty(new BlockPos(var12, var13, var10)), null);
                  world.addEntity(var11);
               }
            }

            return true;
         }
      }
   }
}
