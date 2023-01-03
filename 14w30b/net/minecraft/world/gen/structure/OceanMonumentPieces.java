package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.PrismarineBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.living.mob.hostile.GuardianEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class OceanMonumentPieces {
   public static void register() {
      StructureManager.registerPiece(OceanMonumentPieces.OceanMonument.class, "OMB");
      StructureManager.registerPiece(OceanMonumentPieces.CoreRoom.class, "OMCR");
      StructureManager.registerPiece(OceanMonumentPieces.DoubleWidthXRoom.class, "OMDXR");
      StructureManager.registerPiece(OceanMonumentPieces.DoubleHeightWidthXRoom.class, "OMDXYR");
      StructureManager.registerPiece(OceanMonumentPieces.DoubleHeightRoom.class, "OMDYR");
      StructureManager.registerPiece(OceanMonumentPieces.DoubleHeightWidthZRoom.class, "OMDYZR");
      StructureManager.registerPiece(OceanMonumentPieces.DoubleWidthZRoom.class, "OMDZR");
      StructureManager.registerPiece(OceanMonumentPieces.MonumentEntry.class, "OMEntry");
      StructureManager.registerPiece(OceanMonumentPieces.Penthouse.class, "OMPenthouse");
      StructureManager.registerPiece(OceanMonumentPieces.SimpleRoom.class, "OMSimple");
      StructureManager.registerPiece(OceanMonumentPieces.SimpleTopRoom.class, "OMSimpleT");
   }

   public static class CoreRoom extends OceanMonumentPieces.OceanMonumentPiece {
      public CoreRoom() {
      }

      public CoreRoom(Direction facing, OceanMonumentPieces.PieceDefinition pieceDefinition, Random random) {
         super(1, facing, pieceDefinition, 2, 2, 2);
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         this.replaceFiller(world, box, 1, 8, 0, 14, 8, 14, BASE);
         byte var4 = 7;
         BlockState var5 = BASE_LIGHT;
         this.fillWithOutline(world, box, 0, var4, 0, 0, var4, 15, var5, var5, false);
         this.fillWithOutline(world, box, 15, var4, 0, 15, var4, 15, var5, var5, false);
         this.fillWithOutline(world, box, 1, var4, 0, 15, var4, 0, var5, var5, false);
         this.fillWithOutline(world, box, 1, var4, 15, 14, var4, 15, var5, var5, false);

         for(int var7 = 1; var7 <= 6; ++var7) {
            var5 = BASE_LIGHT;
            if (var7 == 2 || var7 == 6) {
               var5 = BASE;
            }

            for(int var6 = 0; var6 <= 15; var6 += 15) {
               this.fillWithOutline(world, box, var6, var7, 0, var6, var7, 1, var5, var5, false);
               this.fillWithOutline(world, box, var6, var7, 6, var6, var7, 9, var5, var5, false);
               this.fillWithOutline(world, box, var6, var7, 14, var6, var7, 15, var5, var5, false);
            }

            this.fillWithOutline(world, box, 1, var7, 0, 1, var7, 0, var5, var5, false);
            this.fillWithOutline(world, box, 6, var7, 0, 9, var7, 0, var5, var5, false);
            this.fillWithOutline(world, box, 14, var7, 0, 14, var7, 0, var5, var5, false);
            this.fillWithOutline(world, box, 1, var7, 15, 14, var7, 15, var5, var5, false);
         }

         this.fillWithOutline(world, box, 6, 3, 6, 9, 6, 9, BASE_DARK, BASE_DARK, false);
         this.fillWithOutline(world, box, 7, 4, 7, 8, 5, 8, Blocks.GOLD_BLOCK.defaultState(), Blocks.GOLD_BLOCK.defaultState(), false);

         for(int var8 = 3; var8 <= 6; var8 += 3) {
            for(int var10 = 6; var10 <= 9; var10 += 3) {
               this.setBlockState(world, LIGHT_SOURCE, var10, var8, 6, box);
               this.setBlockState(world, LIGHT_SOURCE, var10, var8, 9, box);
            }
         }

         this.fillWithOutline(world, box, 5, 1, 6, 5, 2, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 5, 1, 9, 5, 2, 9, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 10, 1, 6, 10, 2, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 10, 1, 9, 10, 2, 9, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 6, 1, 5, 6, 2, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 9, 1, 5, 9, 2, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 6, 1, 10, 6, 2, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 9, 1, 10, 9, 2, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 5, 2, 5, 5, 6, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 5, 2, 10, 5, 6, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 10, 2, 5, 10, 6, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 10, 2, 10, 10, 6, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 5, 7, 1, 5, 7, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 10, 7, 1, 10, 7, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 5, 7, 9, 5, 7, 14, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 10, 7, 9, 10, 7, 14, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 1, 7, 5, 6, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 1, 7, 10, 6, 7, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 9, 7, 5, 14, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 9, 7, 10, 14, 7, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 2, 1, 2, 2, 1, 3, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 3, 1, 2, 3, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 13, 1, 2, 13, 1, 3, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 12, 1, 2, 12, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 2, 1, 12, 2, 1, 13, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 3, 1, 13, 3, 1, 13, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 13, 1, 12, 13, 1, 13, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 12, 1, 13, 12, 1, 13, BASE_LIGHT, BASE_LIGHT, false);
         return true;
      }
   }

   public static class DoubleHeightRoom extends OceanMonumentPieces.OceanMonumentPiece {
      public DoubleHeightRoom() {
      }

      public DoubleHeightRoom(Direction facing, OceanMonumentPieces.PieceDefinition pieceDefinition, Random random) {
         super(1, facing, pieceDefinition, 1, 2, 1);
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         if (this.pieceDefinition.index / 25 > 0) {
            this.generateFloor(world, box, 0, 0, this.pieceDefinition.hasEntrance[Direction.DOWN.getId()]);
         }

         OceanMonumentPieces.PieceDefinition var4 = this.pieceDefinition.connections[Direction.UP.getId()];
         if (var4.connections[Direction.UP.getId()] == null) {
            this.replaceFiller(world, box, 1, 8, 1, 6, 8, 6, BASE);
         }

         this.fillWithOutline(world, box, 0, 4, 0, 0, 4, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 7, 4, 0, 7, 4, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 1, 4, 0, 6, 4, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 1, 4, 7, 6, 4, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 2, 4, 1, 2, 4, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 1, 4, 2, 1, 4, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 5, 4, 1, 5, 4, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 6, 4, 2, 6, 4, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 2, 4, 5, 2, 4, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 1, 4, 5, 1, 4, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 5, 4, 5, 5, 4, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 6, 4, 5, 6, 4, 5, BASE_LIGHT, BASE_LIGHT, false);
         OceanMonumentPieces.PieceDefinition var5 = this.pieceDefinition;

         for(int var6 = 1; var6 <= 5; var6 += 4) {
            byte var7 = 0;
            if (var5.hasEntrance[Direction.SOUTH.getId()]) {
               this.fillWithOutline(world, box, 2, var6, var7, 2, var6 + 2, var7, BASE_LIGHT, BASE_LIGHT, false);
               this.fillWithOutline(world, box, 5, var6, var7, 5, var6 + 2, var7, BASE_LIGHT, BASE_LIGHT, false);
               this.fillWithOutline(world, box, 3, var6 + 2, var7, 4, var6 + 2, var7, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.fillWithOutline(world, box, 0, var6, var7, 7, var6 + 2, var7, BASE_LIGHT, BASE_LIGHT, false);
               this.fillWithOutline(world, box, 0, var6 + 1, var7, 7, var6 + 1, var7, BASE, BASE, false);
            }

            var7 = 7;
            if (var5.hasEntrance[Direction.NORTH.getId()]) {
               this.fillWithOutline(world, box, 2, var6, var7, 2, var6 + 2, var7, BASE_LIGHT, BASE_LIGHT, false);
               this.fillWithOutline(world, box, 5, var6, var7, 5, var6 + 2, var7, BASE_LIGHT, BASE_LIGHT, false);
               this.fillWithOutline(world, box, 3, var6 + 2, var7, 4, var6 + 2, var7, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.fillWithOutline(world, box, 0, var6, var7, 7, var6 + 2, var7, BASE_LIGHT, BASE_LIGHT, false);
               this.fillWithOutline(world, box, 0, var6 + 1, var7, 7, var6 + 1, var7, BASE, BASE, false);
            }

            byte var8 = 0;
            if (var5.hasEntrance[Direction.WEST.getId()]) {
               this.fillWithOutline(world, box, var8, var6, 2, var8, var6 + 2, 2, BASE_LIGHT, BASE_LIGHT, false);
               this.fillWithOutline(world, box, var8, var6, 5, var8, var6 + 2, 5, BASE_LIGHT, BASE_LIGHT, false);
               this.fillWithOutline(world, box, var8, var6 + 2, 3, var8, var6 + 2, 4, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.fillWithOutline(world, box, var8, var6, 0, var8, var6 + 2, 7, BASE_LIGHT, BASE_LIGHT, false);
               this.fillWithOutline(world, box, var8, var6 + 1, 0, var8, var6 + 1, 7, BASE, BASE, false);
            }

            var8 = 7;
            if (var5.hasEntrance[Direction.EAST.getId()]) {
               this.fillWithOutline(world, box, var8, var6, 2, var8, var6 + 2, 2, BASE_LIGHT, BASE_LIGHT, false);
               this.fillWithOutline(world, box, var8, var6, 5, var8, var6 + 2, 5, BASE_LIGHT, BASE_LIGHT, false);
               this.fillWithOutline(world, box, var8, var6 + 2, 3, var8, var6 + 2, 4, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.fillWithOutline(world, box, var8, var6, 0, var8, var6 + 2, 7, BASE_LIGHT, BASE_LIGHT, false);
               this.fillWithOutline(world, box, var8, var6 + 1, 0, var8, var6 + 1, 7, BASE, BASE, false);
            }

            var5 = var4;
         }

         return true;
      }
   }

   static class DoubleHeightRoomFitter implements OceanMonumentPieces.RoomFitter {
      private DoubleHeightRoomFitter() {
      }

      @Override
      public boolean fits(OceanMonumentPieces.PieceDefinition pieceDefinition) {
         return pieceDefinition.hasEntrance[Direction.UP.getId()] && !pieceDefinition.connections[Direction.UP.getId()].claimed;
      }

      @Override
      public OceanMonumentPieces.OceanMonumentPiece create(Direction facing, OceanMonumentPieces.PieceDefinition pieceDefinition, Random random) {
         pieceDefinition.claimed = true;
         pieceDefinition.connections[Direction.UP.getId()].claimed = true;
         return new OceanMonumentPieces.DoubleHeightRoom(facing, pieceDefinition, random);
      }
   }

   public static class DoubleHeightWidthXRoom extends OceanMonumentPieces.OceanMonumentPiece {
      public DoubleHeightWidthXRoom() {
      }

      public DoubleHeightWidthXRoom(Direction facing, OceanMonumentPieces.PieceDefinition pieceDefinition, Random random) {
         super(1, facing, pieceDefinition, 2, 2, 1);
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         OceanMonumentPieces.PieceDefinition var4 = this.pieceDefinition.connections[Direction.EAST.getId()];
         OceanMonumentPieces.PieceDefinition var5 = this.pieceDefinition;
         OceanMonumentPieces.PieceDefinition var6 = var5.connections[Direction.UP.getId()];
         OceanMonumentPieces.PieceDefinition var7 = var4.connections[Direction.UP.getId()];
         if (this.pieceDefinition.index / 25 > 0) {
            this.generateFloor(world, box, 8, 0, var4.hasEntrance[Direction.DOWN.getId()]);
            this.generateFloor(world, box, 0, 0, var5.hasEntrance[Direction.DOWN.getId()]);
         }

         if (var6.connections[Direction.UP.getId()] == null) {
            this.replaceFiller(world, box, 1, 8, 1, 7, 8, 6, BASE);
         }

         if (var7.connections[Direction.UP.getId()] == null) {
            this.replaceFiller(world, box, 8, 8, 1, 14, 8, 6, BASE);
         }

         for(int var8 = 1; var8 <= 7; ++var8) {
            BlockState var9 = BASE_LIGHT;
            if (var8 == 2 || var8 == 6) {
               var9 = BASE;
            }

            this.fillWithOutline(world, box, 0, var8, 0, 0, var8, 7, var9, var9, false);
            this.fillWithOutline(world, box, 15, var8, 0, 15, var8, 7, var9, var9, false);
            this.fillWithOutline(world, box, 1, var8, 0, 15, var8, 0, var9, var9, false);
            this.fillWithOutline(world, box, 1, var8, 7, 14, var8, 7, var9, var9, false);
         }

         this.fillWithOutline(world, box, 2, 1, 3, 2, 7, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 3, 1, 2, 4, 7, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 3, 1, 5, 4, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 13, 1, 3, 13, 7, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 11, 1, 2, 12, 7, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 11, 1, 5, 12, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 5, 1, 3, 5, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 10, 1, 3, 10, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 5, 7, 2, 10, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 5, 5, 2, 5, 7, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 10, 5, 2, 10, 7, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 5, 5, 5, 5, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 10, 5, 5, 10, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.setBlockState(world, BASE_LIGHT, 6, 6, 2, box);
         this.setBlockState(world, BASE_LIGHT, 9, 6, 2, box);
         this.setBlockState(world, BASE_LIGHT, 6, 6, 5, box);
         this.setBlockState(world, BASE_LIGHT, 9, 6, 5, box);
         this.fillWithOutline(world, box, 5, 4, 3, 6, 4, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 9, 4, 3, 10, 4, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.setBlockState(world, LIGHT_SOURCE, 5, 4, 2, box);
         this.setBlockState(world, LIGHT_SOURCE, 5, 4, 5, box);
         this.setBlockState(world, LIGHT_SOURCE, 10, 4, 2, box);
         this.setBlockState(world, LIGHT_SOURCE, 10, 4, 5, box);
         if (var5.hasEntrance[Direction.SOUTH.getId()]) {
            this.fillWithOutline(world, box, 3, 1, 0, 4, 2, 0, FILLER, FILLER, false);
         }

         if (var5.hasEntrance[Direction.NORTH.getId()]) {
            this.fillWithOutline(world, box, 3, 1, 7, 4, 2, 7, FILLER, FILLER, false);
         }

         if (var5.hasEntrance[Direction.WEST.getId()]) {
            this.fillWithOutline(world, box, 0, 1, 3, 0, 2, 4, FILLER, FILLER, false);
         }

         if (var4.hasEntrance[Direction.SOUTH.getId()]) {
            this.fillWithOutline(world, box, 11, 1, 0, 12, 2, 0, FILLER, FILLER, false);
         }

         if (var4.hasEntrance[Direction.NORTH.getId()]) {
            this.fillWithOutline(world, box, 11, 1, 7, 12, 2, 7, FILLER, FILLER, false);
         }

         if (var4.hasEntrance[Direction.EAST.getId()]) {
            this.fillWithOutline(world, box, 15, 1, 3, 15, 2, 4, FILLER, FILLER, false);
         }

         if (var6.hasEntrance[Direction.SOUTH.getId()]) {
            this.fillWithOutline(world, box, 3, 5, 0, 4, 6, 0, FILLER, FILLER, false);
         }

         if (var6.hasEntrance[Direction.NORTH.getId()]) {
            this.fillWithOutline(world, box, 3, 5, 7, 4, 6, 7, FILLER, FILLER, false);
         }

         if (var6.hasEntrance[Direction.WEST.getId()]) {
            this.fillWithOutline(world, box, 0, 5, 3, 0, 6, 4, FILLER, FILLER, false);
         }

         if (var7.hasEntrance[Direction.SOUTH.getId()]) {
            this.fillWithOutline(world, box, 11, 5, 0, 12, 6, 0, FILLER, FILLER, false);
         }

         if (var7.hasEntrance[Direction.NORTH.getId()]) {
            this.fillWithOutline(world, box, 11, 5, 7, 12, 6, 7, FILLER, FILLER, false);
         }

         if (var7.hasEntrance[Direction.EAST.getId()]) {
            this.fillWithOutline(world, box, 15, 5, 3, 15, 6, 4, FILLER, FILLER, false);
         }

         return true;
      }
   }

   static class DoubleHeightWidthXRoomFitter implements OceanMonumentPieces.RoomFitter {
      private DoubleHeightWidthXRoomFitter() {
      }

      @Override
      public boolean fits(OceanMonumentPieces.PieceDefinition pieceDefinition) {
         if (pieceDefinition.hasEntrance[Direction.EAST.getId()]
            && !pieceDefinition.connections[Direction.EAST.getId()].claimed
            && pieceDefinition.hasEntrance[Direction.UP.getId()]
            && !pieceDefinition.connections[Direction.UP.getId()].claimed) {
            OceanMonumentPieces.PieceDefinition var2 = pieceDefinition.connections[Direction.EAST.getId()];
            return var2.hasEntrance[Direction.UP.getId()] && !var2.connections[Direction.UP.getId()].claimed;
         } else {
            return false;
         }
      }

      @Override
      public OceanMonumentPieces.OceanMonumentPiece create(Direction facing, OceanMonumentPieces.PieceDefinition pieceDefinition, Random random) {
         pieceDefinition.claimed = true;
         pieceDefinition.connections[Direction.EAST.getId()].claimed = true;
         pieceDefinition.connections[Direction.UP.getId()].claimed = true;
         pieceDefinition.connections[Direction.EAST.getId()].connections[Direction.UP.getId()].claimed = true;
         return new OceanMonumentPieces.DoubleHeightWidthXRoom(facing, pieceDefinition, random);
      }
   }

   public static class DoubleHeightWidthZRoom extends OceanMonumentPieces.OceanMonumentPiece {
      public DoubleHeightWidthZRoom() {
      }

      public DoubleHeightWidthZRoom(Direction facing, OceanMonumentPieces.PieceDefinition pieceDefinition, Random random) {
         super(1, facing, pieceDefinition, 1, 2, 2);
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         OceanMonumentPieces.PieceDefinition var4 = this.pieceDefinition.connections[Direction.NORTH.getId()];
         OceanMonumentPieces.PieceDefinition var5 = this.pieceDefinition;
         OceanMonumentPieces.PieceDefinition var6 = var4.connections[Direction.UP.getId()];
         OceanMonumentPieces.PieceDefinition var7 = var5.connections[Direction.UP.getId()];
         if (this.pieceDefinition.index / 25 > 0) {
            this.generateFloor(world, box, 0, 8, var4.hasEntrance[Direction.DOWN.getId()]);
            this.generateFloor(world, box, 0, 0, var5.hasEntrance[Direction.DOWN.getId()]);
         }

         if (var7.connections[Direction.UP.getId()] == null) {
            this.replaceFiller(world, box, 1, 8, 1, 6, 8, 7, BASE);
         }

         if (var6.connections[Direction.UP.getId()] == null) {
            this.replaceFiller(world, box, 1, 8, 8, 6, 8, 14, BASE);
         }

         for(int var8 = 1; var8 <= 7; ++var8) {
            BlockState var9 = BASE_LIGHT;
            if (var8 == 2 || var8 == 6) {
               var9 = BASE;
            }

            this.fillWithOutline(world, box, 0, var8, 0, 0, var8, 15, var9, var9, false);
            this.fillWithOutline(world, box, 7, var8, 0, 7, var8, 15, var9, var9, false);
            this.fillWithOutline(world, box, 1, var8, 0, 6, var8, 0, var9, var9, false);
            this.fillWithOutline(world, box, 1, var8, 15, 6, var8, 15, var9, var9, false);
         }

         for(int var10 = 1; var10 <= 7; ++var10) {
            BlockState var11 = BASE_DARK;
            if (var10 == 2 || var10 == 6) {
               var11 = LIGHT_SOURCE;
            }

            this.fillWithOutline(world, box, 3, var10, 7, 4, var10, 8, var11, var11, false);
         }

         if (var5.hasEntrance[Direction.SOUTH.getId()]) {
            this.fillWithOutline(world, box, 3, 1, 0, 4, 2, 0, FILLER, FILLER, false);
         }

         if (var5.hasEntrance[Direction.EAST.getId()]) {
            this.fillWithOutline(world, box, 7, 1, 3, 7, 2, 4, FILLER, FILLER, false);
         }

         if (var5.hasEntrance[Direction.WEST.getId()]) {
            this.fillWithOutline(world, box, 0, 1, 3, 0, 2, 4, FILLER, FILLER, false);
         }

         if (var4.hasEntrance[Direction.NORTH.getId()]) {
            this.fillWithOutline(world, box, 3, 1, 15, 4, 2, 15, FILLER, FILLER, false);
         }

         if (var4.hasEntrance[Direction.WEST.getId()]) {
            this.fillWithOutline(world, box, 0, 1, 11, 0, 2, 12, FILLER, FILLER, false);
         }

         if (var4.hasEntrance[Direction.EAST.getId()]) {
            this.fillWithOutline(world, box, 7, 1, 11, 7, 2, 12, FILLER, FILLER, false);
         }

         if (var7.hasEntrance[Direction.SOUTH.getId()]) {
            this.fillWithOutline(world, box, 3, 5, 0, 4, 6, 0, FILLER, FILLER, false);
         }

         if (var7.hasEntrance[Direction.EAST.getId()]) {
            this.fillWithOutline(world, box, 7, 5, 3, 7, 6, 4, FILLER, FILLER, false);
            this.fillWithOutline(world, box, 5, 4, 2, 6, 4, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 6, 1, 2, 6, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 6, 1, 5, 6, 3, 5, BASE_LIGHT, BASE_LIGHT, false);
         }

         if (var7.hasEntrance[Direction.WEST.getId()]) {
            this.fillWithOutline(world, box, 0, 5, 3, 0, 6, 4, FILLER, FILLER, false);
            this.fillWithOutline(world, box, 1, 4, 2, 2, 4, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 1, 1, 2, 1, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 1, 1, 5, 1, 3, 5, BASE_LIGHT, BASE_LIGHT, false);
         }

         if (var6.hasEntrance[Direction.NORTH.getId()]) {
            this.fillWithOutline(world, box, 3, 5, 15, 4, 6, 15, FILLER, FILLER, false);
         }

         if (var6.hasEntrance[Direction.WEST.getId()]) {
            this.fillWithOutline(world, box, 0, 5, 11, 0, 6, 12, FILLER, FILLER, false);
            this.fillWithOutline(world, box, 1, 4, 10, 2, 4, 13, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 1, 1, 10, 1, 3, 10, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 1, 1, 13, 1, 3, 13, BASE_LIGHT, BASE_LIGHT, false);
         }

         if (var6.hasEntrance[Direction.EAST.getId()]) {
            this.fillWithOutline(world, box, 7, 5, 11, 7, 6, 12, FILLER, FILLER, false);
            this.fillWithOutline(world, box, 5, 4, 10, 6, 4, 13, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 6, 1, 10, 6, 3, 10, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 6, 1, 13, 6, 3, 13, BASE_LIGHT, BASE_LIGHT, false);
         }

         return true;
      }
   }

   static class DoubleHeightWidthZRoomFitter implements OceanMonumentPieces.RoomFitter {
      private DoubleHeightWidthZRoomFitter() {
      }

      @Override
      public boolean fits(OceanMonumentPieces.PieceDefinition pieceDefinition) {
         if (pieceDefinition.hasEntrance[Direction.NORTH.getId()]
            && !pieceDefinition.connections[Direction.NORTH.getId()].claimed
            && pieceDefinition.hasEntrance[Direction.UP.getId()]
            && !pieceDefinition.connections[Direction.UP.getId()].claimed) {
            OceanMonumentPieces.PieceDefinition var2 = pieceDefinition.connections[Direction.NORTH.getId()];
            return var2.hasEntrance[Direction.UP.getId()] && !var2.connections[Direction.UP.getId()].claimed;
         } else {
            return false;
         }
      }

      @Override
      public OceanMonumentPieces.OceanMonumentPiece create(Direction facing, OceanMonumentPieces.PieceDefinition pieceDefinition, Random random) {
         pieceDefinition.claimed = true;
         pieceDefinition.connections[Direction.NORTH.getId()].claimed = true;
         pieceDefinition.connections[Direction.UP.getId()].claimed = true;
         pieceDefinition.connections[Direction.NORTH.getId()].connections[Direction.UP.getId()].claimed = true;
         return new OceanMonumentPieces.DoubleHeightWidthZRoom(facing, pieceDefinition, random);
      }
   }

   public static class DoubleWidthXRoom extends OceanMonumentPieces.OceanMonumentPiece {
      public DoubleWidthXRoom() {
      }

      public DoubleWidthXRoom(Direction facing, OceanMonumentPieces.PieceDefinition pieceDefinition, Random random) {
         super(1, facing, pieceDefinition, 2, 1, 1);
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         OceanMonumentPieces.PieceDefinition var4 = this.pieceDefinition.connections[Direction.EAST.getId()];
         OceanMonumentPieces.PieceDefinition var5 = this.pieceDefinition;
         if (this.pieceDefinition.index / 25 > 0) {
            this.generateFloor(world, box, 8, 0, var4.hasEntrance[Direction.DOWN.getId()]);
            this.generateFloor(world, box, 0, 0, var5.hasEntrance[Direction.DOWN.getId()]);
         }

         if (var5.connections[Direction.UP.getId()] == null) {
            this.replaceFiller(world, box, 1, 4, 1, 7, 4, 6, BASE);
         }

         if (var4.connections[Direction.UP.getId()] == null) {
            this.replaceFiller(world, box, 8, 4, 1, 14, 4, 6, BASE);
         }

         this.fillWithOutline(world, box, 0, 3, 0, 0, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 15, 3, 0, 15, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 1, 3, 0, 15, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 1, 3, 7, 14, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 0, 2, 0, 0, 2, 7, BASE, BASE, false);
         this.fillWithOutline(world, box, 15, 2, 0, 15, 2, 7, BASE, BASE, false);
         this.fillWithOutline(world, box, 1, 2, 0, 15, 2, 0, BASE, BASE, false);
         this.fillWithOutline(world, box, 1, 2, 7, 14, 2, 7, BASE, BASE, false);
         this.fillWithOutline(world, box, 0, 1, 0, 0, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 15, 1, 0, 15, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 1, 1, 0, 15, 1, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 1, 1, 7, 14, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 5, 1, 0, 10, 1, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 6, 2, 0, 9, 2, 3, BASE, BASE, false);
         this.fillWithOutline(world, box, 5, 3, 0, 10, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.setBlockState(world, LIGHT_SOURCE, 6, 2, 3, box);
         this.setBlockState(world, LIGHT_SOURCE, 9, 2, 3, box);
         if (var5.hasEntrance[Direction.SOUTH.getId()]) {
            this.fillWithOutline(world, box, 3, 1, 0, 4, 2, 0, FILLER, FILLER, false);
         }

         if (var5.hasEntrance[Direction.NORTH.getId()]) {
            this.fillWithOutline(world, box, 3, 1, 7, 4, 2, 7, FILLER, FILLER, false);
         }

         if (var5.hasEntrance[Direction.WEST.getId()]) {
            this.fillWithOutline(world, box, 0, 1, 3, 0, 2, 4, FILLER, FILLER, false);
         }

         if (var4.hasEntrance[Direction.SOUTH.getId()]) {
            this.fillWithOutline(world, box, 11, 1, 0, 12, 2, 0, FILLER, FILLER, false);
         }

         if (var4.hasEntrance[Direction.NORTH.getId()]) {
            this.fillWithOutline(world, box, 11, 1, 7, 12, 2, 7, FILLER, FILLER, false);
         }

         if (var4.hasEntrance[Direction.EAST.getId()]) {
            this.fillWithOutline(world, box, 15, 1, 3, 15, 2, 4, FILLER, FILLER, false);
         }

         return true;
      }
   }

   static class DoubleWidthXRoomFitter implements OceanMonumentPieces.RoomFitter {
      private DoubleWidthXRoomFitter() {
      }

      @Override
      public boolean fits(OceanMonumentPieces.PieceDefinition pieceDefinition) {
         return pieceDefinition.hasEntrance[Direction.EAST.getId()] && !pieceDefinition.connections[Direction.EAST.getId()].claimed;
      }

      @Override
      public OceanMonumentPieces.OceanMonumentPiece create(Direction facing, OceanMonumentPieces.PieceDefinition pieceDefinition, Random random) {
         pieceDefinition.claimed = true;
         pieceDefinition.connections[Direction.EAST.getId()].claimed = true;
         return new OceanMonumentPieces.DoubleWidthXRoom(facing, pieceDefinition, random);
      }
   }

   public static class DoubleWidthZRoom extends OceanMonumentPieces.OceanMonumentPiece {
      public DoubleWidthZRoom() {
      }

      public DoubleWidthZRoom(Direction facing, OceanMonumentPieces.PieceDefinition pieceDefinition, Random random) {
         super(1, facing, pieceDefinition, 1, 1, 2);
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         OceanMonumentPieces.PieceDefinition var4 = this.pieceDefinition.connections[Direction.NORTH.getId()];
         OceanMonumentPieces.PieceDefinition var5 = this.pieceDefinition;
         if (this.pieceDefinition.index / 25 > 0) {
            this.generateFloor(world, box, 0, 8, var4.hasEntrance[Direction.DOWN.getId()]);
            this.generateFloor(world, box, 0, 0, var5.hasEntrance[Direction.DOWN.getId()]);
         }

         if (var5.connections[Direction.UP.getId()] == null) {
            this.replaceFiller(world, box, 1, 4, 1, 6, 4, 7, BASE);
         }

         if (var4.connections[Direction.UP.getId()] == null) {
            this.replaceFiller(world, box, 1, 4, 8, 6, 4, 14, BASE);
         }

         this.fillWithOutline(world, box, 0, 3, 0, 0, 3, 15, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 7, 3, 0, 7, 3, 15, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 1, 3, 0, 7, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 1, 3, 15, 6, 3, 15, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 0, 2, 0, 0, 2, 15, BASE, BASE, false);
         this.fillWithOutline(world, box, 7, 2, 0, 7, 2, 15, BASE, BASE, false);
         this.fillWithOutline(world, box, 1, 2, 0, 7, 2, 0, BASE, BASE, false);
         this.fillWithOutline(world, box, 1, 2, 15, 6, 2, 15, BASE, BASE, false);
         this.fillWithOutline(world, box, 0, 1, 0, 0, 1, 15, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 7, 1, 0, 7, 1, 15, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 1, 1, 0, 7, 1, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 1, 1, 15, 6, 1, 15, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 1, 1, 1, 1, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 6, 1, 1, 6, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 1, 3, 1, 1, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 6, 3, 1, 6, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 1, 1, 13, 1, 1, 14, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 6, 1, 13, 6, 1, 14, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 1, 3, 13, 1, 3, 14, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 6, 3, 13, 6, 3, 14, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 2, 1, 6, 2, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 5, 1, 6, 5, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 2, 1, 9, 2, 3, 9, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 5, 1, 9, 5, 3, 9, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 3, 2, 6, 4, 2, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 3, 2, 9, 4, 2, 9, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 2, 2, 7, 2, 2, 8, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 5, 2, 7, 5, 2, 8, BASE_LIGHT, BASE_LIGHT, false);
         this.setBlockState(world, LIGHT_SOURCE, 2, 2, 5, box);
         this.setBlockState(world, LIGHT_SOURCE, 5, 2, 5, box);
         this.setBlockState(world, LIGHT_SOURCE, 2, 2, 10, box);
         this.setBlockState(world, LIGHT_SOURCE, 5, 2, 10, box);
         this.setBlockState(world, BASE_LIGHT, 2, 3, 5, box);
         this.setBlockState(world, BASE_LIGHT, 5, 3, 5, box);
         this.setBlockState(world, BASE_LIGHT, 2, 3, 10, box);
         this.setBlockState(world, BASE_LIGHT, 5, 3, 10, box);
         if (var5.hasEntrance[Direction.SOUTH.getId()]) {
            this.fillWithOutline(world, box, 3, 1, 0, 4, 2, 0, FILLER, FILLER, false);
         }

         if (var5.hasEntrance[Direction.EAST.getId()]) {
            this.fillWithOutline(world, box, 7, 1, 3, 7, 2, 4, FILLER, FILLER, false);
         }

         if (var5.hasEntrance[Direction.WEST.getId()]) {
            this.fillWithOutline(world, box, 0, 1, 3, 0, 2, 4, FILLER, FILLER, false);
         }

         if (var4.hasEntrance[Direction.NORTH.getId()]) {
            this.fillWithOutline(world, box, 3, 1, 15, 4, 2, 15, FILLER, FILLER, false);
         }

         if (var4.hasEntrance[Direction.WEST.getId()]) {
            this.fillWithOutline(world, box, 0, 1, 11, 0, 2, 12, FILLER, FILLER, false);
         }

         if (var4.hasEntrance[Direction.EAST.getId()]) {
            this.fillWithOutline(world, box, 7, 1, 11, 7, 2, 12, FILLER, FILLER, false);
         }

         return true;
      }
   }

   static class DoubleWidthZRoomFitter implements OceanMonumentPieces.RoomFitter {
      private DoubleWidthZRoomFitter() {
      }

      @Override
      public boolean fits(OceanMonumentPieces.PieceDefinition pieceDefinition) {
         return pieceDefinition.hasEntrance[Direction.NORTH.getId()] && !pieceDefinition.connections[Direction.NORTH.getId()].claimed;
      }

      @Override
      public OceanMonumentPieces.OceanMonumentPiece create(Direction facing, OceanMonumentPieces.PieceDefinition pieceDefinition, Random random) {
         OceanMonumentPieces.PieceDefinition var4 = pieceDefinition;
         if (!pieceDefinition.hasEntrance[Direction.NORTH.getId()] || pieceDefinition.connections[Direction.NORTH.getId()].claimed) {
            var4 = pieceDefinition.connections[Direction.SOUTH.getId()];
         }

         var4.claimed = true;
         var4.connections[Direction.NORTH.getId()].claimed = true;
         return new OceanMonumentPieces.DoubleWidthZRoom(facing, var4, random);
      }
   }

   public static class MonumentEntry extends OceanMonumentPieces.OceanMonumentPiece {
      public MonumentEntry() {
      }

      public MonumentEntry(Direction facing, OceanMonumentPieces.PieceDefinition pieceDefinition) {
         super(1, facing, pieceDefinition, 1, 1, 1);
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         this.fillWithOutline(world, box, 0, 3, 0, 2, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 5, 3, 0, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 0, 2, 0, 1, 2, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 6, 2, 0, 7, 2, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 0, 1, 0, 0, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 7, 1, 0, 7, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 0, 1, 7, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 1, 1, 0, 2, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 5, 1, 0, 6, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
         if (this.pieceDefinition.hasEntrance[Direction.NORTH.getId()]) {
            this.fillWithOutline(world, box, 3, 1, 7, 4, 2, 7, FILLER, FILLER, false);
         }

         if (this.pieceDefinition.hasEntrance[Direction.WEST.getId()]) {
            this.fillWithOutline(world, box, 0, 1, 3, 1, 2, 4, FILLER, FILLER, false);
         }

         if (this.pieceDefinition.hasEntrance[Direction.EAST.getId()]) {
            this.fillWithOutline(world, box, 6, 1, 3, 7, 2, 4, FILLER, FILLER, false);
         }

         return true;
      }
   }

   public static class OceanMonument extends OceanMonumentPieces.OceanMonumentPiece {
      private OceanMonumentPieces.PieceDefinition source;
      private OceanMonumentPieces.PieceDefinition core;
      private List children = Lists.newArrayList();

      public OceanMonument() {
      }

      public OceanMonument(Random random, int x, int z, Direction facing) {
         super(0);
         this.facing = facing;
         switch(this.facing) {
            case NORTH:
            case SOUTH:
               this.box = new StructureBox(x, 39, z, x + 58 - 1, 61, z + 58 - 1);
               break;
            default:
               this.box = new StructureBox(x, 39, z, x + 58 - 1, 61, z + 58 - 1);
         }

         List var5 = this.createPieceLayout(random);
         this.source.claimed = true;
         this.children.add(new OceanMonumentPieces.MonumentEntry(this.facing, this.source));
         this.children.add(new OceanMonumentPieces.CoreRoom(this.facing, this.core, random));
         ArrayList var6 = Lists.newArrayList();
         var6.add(new OceanMonumentPieces.DoubleHeightWidthXRoomFitter());
         var6.add(new OceanMonumentPieces.DoubleHeightWidthZRoomFitter());
         var6.add(new OceanMonumentPieces.DoubleWidthZRoomFitter());
         var6.add(new OceanMonumentPieces.DoubleWidthXRoomFitter());
         var6.add(new OceanMonumentPieces.DoubleHeightRoomFitter());
         var6.add(new OceanMonumentPieces.SimpleTopRoomFitter());
         var6.add(new OceanMonumentPieces.SimpleRoomFitter());

         for(OceanMonumentPieces.PieceDefinition var8 : var5) {
            if (!var8.claimed && !var8.isSpecial()) {
               for(OceanMonumentPieces.RoomFitter var10 : var6) {
                  if (var10.fits(var8)) {
                     this.children.add(var10.create(this.facing, var8, random));
                     break;
                  }
               }
            }
         }

         int var14 = this.box.minY;
         int var15 = this.transformX(9, 22);
         int var16 = this.transformZ(9, 22);

         for(OceanMonumentPieces.OceanMonumentPiece var11 : this.children) {
            var11.getBoundingBox().move(var15, var14, var16);
         }

         StructureBox var18 = StructureBox.of(
            this.transformX(1, 1), this.transformY(1), this.transformZ(1, 1), this.transformX(23, 21), this.transformY(8), this.transformZ(23, 21)
         );
         StructureBox var19 = StructureBox.of(
            this.transformX(34, 1), this.transformY(1), this.transformZ(34, 1), this.transformX(56, 21), this.transformY(8), this.transformZ(56, 21)
         );
         StructureBox var12 = StructureBox.of(
            this.transformX(22, 22), this.transformY(13), this.transformZ(22, 22), this.transformX(35, 35), this.transformY(17), this.transformZ(35, 35)
         );
         int var13 = random.nextInt();
         this.children.add(new OceanMonumentPieces.WingRoom(this.facing, var18, var13++));
         this.children.add(new OceanMonumentPieces.WingRoom(this.facing, var19, var13++));
         this.children.add(new OceanMonumentPieces.Penthouse(this.facing, var12));
      }

      private List createPieceLayout(Random random) {
         OceanMonumentPieces.PieceDefinition[] var2 = new OceanMonumentPieces.PieceDefinition[75];

         for(int var3 = 0; var3 < 5; ++var3) {
            for(int var4 = 0; var4 < 4; ++var4) {
               byte var5 = 0;
               int var6 = getPieceIndex(var3, var5, var4);
               var2[var6] = new OceanMonumentPieces.PieceDefinition(var6);
            }
         }

         for(int var15 = 0; var15 < 5; ++var15) {
            for(int var19 = 0; var19 < 4; ++var19) {
               byte var23 = 1;
               int var27 = getPieceIndex(var15, var23, var19);
               var2[var27] = new OceanMonumentPieces.PieceDefinition(var27);
            }
         }

         for(int var16 = 1; var16 < 4; ++var16) {
            for(int var20 = 0; var20 < 2; ++var20) {
               byte var24 = 2;
               int var28 = getPieceIndex(var16, var24, var20);
               var2[var28] = new OceanMonumentPieces.PieceDefinition(var28);
            }
         }

         this.source = var2[SOURCE_INDEX];

         for(int var17 = 0; var17 < 5; ++var17) {
            for(int var21 = 0; var21 < 5; ++var21) {
               for(int var25 = 0; var25 < 3; ++var25) {
                  int var29 = getPieceIndex(var17, var25, var21);
                  if (var2[var29] != null) {
                     for(Direction var10 : Direction.values()) {
                        int var11 = var17 + var10.getOffsetX();
                        int var12 = var25 + var10.getOffsetY();
                        int var13 = var21 + var10.getOffsetZ();
                        if (var11 >= 0 && var11 < 5 && var13 >= 0 && var13 < 5 && var12 >= 0 && var12 < 3) {
                           int var14 = getPieceIndex(var11, var12, var13);
                           if (var2[var14] != null) {
                              if (var13 != var21) {
                                 var2[var29].updateConnection(var10.getOpposite(), var2[var14]);
                              } else {
                                 var2[var29].updateConnection(var10, var2[var14]);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         OceanMonumentPieces.PieceDefinition var18;
         var2[TOP_CONNECT_INDEX].updateConnection(Direction.UP, var18 = new OceanMonumentPieces.PieceDefinition(1003));
         OceanMonumentPieces.PieceDefinition var22;
         var2[LEFT_CONNECT_INDEX].updateConnection(Direction.SOUTH, var22 = new OceanMonumentPieces.PieceDefinition(1001));
         OceanMonumentPieces.PieceDefinition var26;
         var2[RIGHT_CONNECT_INDEX].updateConnection(Direction.SOUTH, var26 = new OceanMonumentPieces.PieceDefinition(1002));
         var18.claimed = true;
         var22.claimed = true;
         var26.claimed = true;
         this.source.source = true;
         this.core = var2[getPieceIndex(random.nextInt(4), 0, 2)];
         this.core.claimed = true;
         this.core.connections[Direction.EAST.getId()].claimed = true;
         this.core.connections[Direction.NORTH.getId()].claimed = true;
         this.core.connections[Direction.EAST.getId()].connections[Direction.NORTH.getId()].claimed = true;
         this.core.connections[Direction.UP.getId()].claimed = true;
         this.core.connections[Direction.EAST.getId()].connections[Direction.UP.getId()].claimed = true;
         this.core.connections[Direction.NORTH.getId()].connections[Direction.UP.getId()].claimed = true;
         this.core.connections[Direction.EAST.getId()].connections[Direction.NORTH.getId()].connections[Direction.UP.getId()].claimed = true;
         ArrayList var30 = Lists.newArrayList();

         for(OceanMonumentPieces.PieceDefinition var37 : var2) {
            if (var37 != null) {
               var37.updateEntrances();
               var30.add(var37);
            }
         }

         var18.updateEntrances();
         Collections.shuffle(var30, random);
         int var32 = 1;

         for(OceanMonumentPieces.PieceDefinition var36 : var30) {
            int var38 = 0;
            int var39 = 0;

            while(var38 < 2 && var39 < 5) {
               ++var39;
               int var40 = random.nextInt(6);
               if (var36.hasEntrance[var40]) {
                  int var41 = Direction.byId(var40).getOpposite().getId();
                  var36.hasEntrance[var40] = false;
                  var36.connections[var40].hasEntrance[var41] = false;
                  if (var36.findSource(var32++) && var36.connections[var40].findSource(var32++)) {
                     ++var38;
                  } else {
                     var36.hasEntrance[var40] = true;
                     var36.connections[var40].hasEntrance[var41] = true;
                  }
               }
            }
         }

         var30.add(var18);
         var30.add(var22);
         var30.add(var26);
         return var30;
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         this.generateWing(false, 0, world, random, box);
         this.generateWing(true, 33, world, random, box);
         this.generateEntranceArch(world, random, box);
         this.generateEntranceWall(world, random, box);
         this.generateRoof(world, random, box);
         this.generateLowerWall(world, random, box);
         this.generateMiddleWall(world, random, box);
         this.generateUpperWall(world, random, box);

         for(int var4 = 0; var4 < 7; ++var4) {
            int var5 = 0;

            while(var5 < 7) {
               if (var5 == 0 && var4 == 3) {
                  var5 = 6;
               }

               int var6 = var4 * 9;
               int var7 = var5 * 9;

               for(int var8 = 0; var8 < 4; ++var8) {
                  for(int var9 = 0; var9 < 4; ++var9) {
                     this.setBlockState(world, BASE_LIGHT, var6 + var8, 0, var7 + var9, box);
                     this.fillColumnDown(world, BASE_LIGHT, var6 + var8, -1, var7 + var9, box);
                  }
               }

               if (var4 != 0 && var4 != 6) {
                  var5 += 6;
               } else {
                  ++var5;
               }
            }
         }

         for(int var10 = 0; var10 < 5; ++var10) {
            this.fillWithOutline(world, box, -1 - var10, 0 + var10 * 2, -1 - var10, -1 - var10, 23, 58 + var10, FILLER, FILLER, false);
            this.fillWithOutline(world, box, 58 + var10, 0 + var10 * 2, -1 - var10, 58 + var10, 23, 58 + var10, FILLER, FILLER, false);
            this.fillWithOutline(world, box, 0 - var10, 0 + var10 * 2, -1 - var10, 57 + var10, 23, -1 - var10, FILLER, FILLER, false);
            this.fillWithOutline(world, box, 0 - var10, 0 + var10 * 2, 58 + var10, 57 + var10, 23, 58 + var10, FILLER, FILLER, false);
         }

         for(OceanMonumentPieces.OceanMonumentPiece var12 : this.children) {
            if (var12.getBoundingBox().intersects(box)) {
               var12.postProcess(world, random, box);
            }
         }

         return true;
      }

      private void generateWing(boolean right, int x, World world, Random random, StructureBox box) {
         boolean var6 = true;
         if (this.intersects(box, x, 0, x + 23, 20)) {
            this.fillWithOutline(world, box, x + 0, 0, 0, x + 24, 0, 20, BASE, BASE, false);
            this.fillWithOutline(world, box, x + 0, 1, 0, x + 24, 10, 20, FILLER, FILLER, false);

            for(int var7 = 0; var7 < 4; ++var7) {
               this.fillWithOutline(world, box, x + var7, var7 + 1, var7, x + var7, var7 + 1, 20, BASE_LIGHT, BASE_LIGHT, false);
               this.fillWithOutline(world, box, x + var7 + 7, var7 + 5, var7 + 7, x + var7 + 7, var7 + 5, 20, BASE_LIGHT, BASE_LIGHT, false);
               this.fillWithOutline(world, box, x + 17 - var7, var7 + 5, var7 + 7, x + 17 - var7, var7 + 5, 20, BASE_LIGHT, BASE_LIGHT, false);
               this.fillWithOutline(world, box, x + 24 - var7, var7 + 1, var7, x + 24 - var7, var7 + 1, 20, BASE_LIGHT, BASE_LIGHT, false);
               this.fillWithOutline(world, box, x + var7 + 1, var7 + 1, var7, x + 23 - var7, var7 + 1, var7, BASE_LIGHT, BASE_LIGHT, false);
               this.fillWithOutline(world, box, x + var7 + 8, var7 + 5, var7 + 7, x + 16 - var7, var7 + 5, var7 + 7, BASE_LIGHT, BASE_LIGHT, false);
            }

            this.fillWithOutline(world, box, x + 4, 4, 4, x + 6, 4, 20, BASE, BASE, false);
            this.fillWithOutline(world, box, x + 7, 4, 4, x + 17, 4, 6, BASE, BASE, false);
            this.fillWithOutline(world, box, x + 18, 4, 4, x + 20, 4, 20, BASE, BASE, false);
            this.fillWithOutline(world, box, x + 11, 8, 11, x + 13, 8, 20, BASE, BASE, false);
            this.setBlockState(world, BASE_DECO, x + 12, 9, 12, box);
            this.setBlockState(world, BASE_DECO, x + 12, 9, 15, box);
            this.setBlockState(world, BASE_DECO, x + 12, 9, 18, box);
            int var11 = right ? x + 19 : x + 5;
            int var8 = right ? x + 5 : x + 19;

            for(int var9 = 20; var9 >= 5; var9 -= 3) {
               this.setBlockState(world, BASE_DECO, var11, 5, var9, box);
            }

            for(int var12 = 19; var12 >= 7; var12 -= 3) {
               this.setBlockState(world, BASE_DECO, var8, 5, var12, box);
            }

            for(int var13 = 0; var13 < 4; ++var13) {
               int var10 = right ? x + (24 - (17 - var13 * 3)) : x + 17 - var13 * 3;
               this.setBlockState(world, BASE_DECO, var10, 5, 5, box);
            }

            this.setBlockState(world, BASE_DECO, var8, 5, 5, box);
            this.fillWithOutline(world, box, x + 11, 1, 12, x + 13, 7, 12, BASE, BASE, false);
            this.fillWithOutline(world, box, x + 12, 1, 11, x + 12, 7, 13, BASE, BASE, false);
         }
      }

      private void generateEntranceArch(World world, Random random, StructureBox box) {
         if (this.intersects(box, 22, 5, 35, 17)) {
            this.fillWithOutline(world, box, 25, 0, 0, 32, 8, 20, FILLER, FILLER, false);

            for(int var4 = 0; var4 < 4; ++var4) {
               this.fillWithOutline(world, box, 24, 2, 5 + var4 * 4, 24, 4, 5 + var4 * 4, BASE_LIGHT, BASE_LIGHT, false);
               this.fillWithOutline(world, box, 22, 4, 5 + var4 * 4, 23, 4, 5 + var4 * 4, BASE_LIGHT, BASE_LIGHT, false);
               this.setBlockState(world, BASE_LIGHT, 25, 5, 5 + var4 * 4, box);
               this.setBlockState(world, BASE_LIGHT, 26, 6, 5 + var4 * 4, box);
               this.setBlockState(world, LIGHT_SOURCE, 26, 5, 5 + var4 * 4, box);
               this.fillWithOutline(world, box, 33, 2, 5 + var4 * 4, 33, 4, 5 + var4 * 4, BASE_LIGHT, BASE_LIGHT, false);
               this.fillWithOutline(world, box, 34, 4, 5 + var4 * 4, 35, 4, 5 + var4 * 4, BASE_LIGHT, BASE_LIGHT, false);
               this.setBlockState(world, BASE_LIGHT, 32, 5, 5 + var4 * 4, box);
               this.setBlockState(world, BASE_LIGHT, 31, 6, 5 + var4 * 4, box);
               this.setBlockState(world, LIGHT_SOURCE, 31, 5, 5 + var4 * 4, box);
               this.fillWithOutline(world, box, 27, 6, 5 + var4 * 4, 30, 6, 5 + var4 * 4, BASE, BASE, false);
            }
         }
      }

      private void generateEntranceWall(World world, Random random, StructureBox box) {
         if (this.intersects(box, 15, 20, 42, 21)) {
            this.fillWithOutline(world, box, 15, 0, 21, 42, 0, 21, BASE, BASE, false);
            this.fillWithOutline(world, box, 26, 1, 21, 31, 3, 21, FILLER, FILLER, false);
            this.fillWithOutline(world, box, 21, 12, 21, 36, 12, 21, BASE, BASE, false);
            this.fillWithOutline(world, box, 17, 11, 21, 40, 11, 21, BASE, BASE, false);
            this.fillWithOutline(world, box, 16, 10, 21, 41, 10, 21, BASE, BASE, false);
            this.fillWithOutline(world, box, 15, 7, 21, 42, 9, 21, BASE, BASE, false);
            this.fillWithOutline(world, box, 16, 6, 21, 41, 6, 21, BASE, BASE, false);
            this.fillWithOutline(world, box, 17, 5, 21, 40, 5, 21, BASE, BASE, false);
            this.fillWithOutline(world, box, 21, 4, 21, 36, 4, 21, BASE, BASE, false);
            this.fillWithOutline(world, box, 22, 3, 21, 26, 3, 21, BASE, BASE, false);
            this.fillWithOutline(world, box, 31, 3, 21, 35, 3, 21, BASE, BASE, false);
            this.fillWithOutline(world, box, 23, 2, 21, 25, 2, 21, BASE, BASE, false);
            this.fillWithOutline(world, box, 32, 2, 21, 34, 2, 21, BASE, BASE, false);
            this.fillWithOutline(world, box, 28, 4, 20, 29, 4, 21, BASE_LIGHT, BASE_LIGHT, false);
            this.setBlockState(world, BASE_LIGHT, 27, 3, 21, box);
            this.setBlockState(world, BASE_LIGHT, 30, 3, 21, box);
            this.setBlockState(world, BASE_LIGHT, 26, 2, 21, box);
            this.setBlockState(world, BASE_LIGHT, 31, 2, 21, box);
            this.setBlockState(world, BASE_LIGHT, 25, 1, 21, box);
            this.setBlockState(world, BASE_LIGHT, 32, 1, 21, box);

            for(int var4 = 0; var4 < 7; ++var4) {
               this.setBlockState(world, BASE_DARK, 28 - var4, 6 + var4, 21, box);
               this.setBlockState(world, BASE_DARK, 29 + var4, 6 + var4, 21, box);
            }

            for(int var5 = 0; var5 < 4; ++var5) {
               this.setBlockState(world, BASE_DARK, 28 - var5, 9 + var5, 21, box);
               this.setBlockState(world, BASE_DARK, 29 + var5, 9 + var5, 21, box);
            }

            this.setBlockState(world, BASE_DARK, 28, 12, 21, box);
            this.setBlockState(world, BASE_DARK, 29, 12, 21, box);

            for(int var6 = 0; var6 < 3; ++var6) {
               this.setBlockState(world, BASE_DARK, 22 - var6 * 2, 8, 21, box);
               this.setBlockState(world, BASE_DARK, 22 - var6 * 2, 9, 21, box);
               this.setBlockState(world, BASE_DARK, 35 + var6 * 2, 8, 21, box);
               this.setBlockState(world, BASE_DARK, 35 + var6 * 2, 9, 21, box);
            }

            this.fillWithOutline(world, box, 15, 13, 21, 42, 15, 21, FILLER, FILLER, false);
            this.fillWithOutline(world, box, 15, 1, 21, 15, 6, 21, FILLER, FILLER, false);
            this.fillWithOutline(world, box, 16, 1, 21, 16, 5, 21, FILLER, FILLER, false);
            this.fillWithOutline(world, box, 17, 1, 21, 20, 4, 21, FILLER, FILLER, false);
            this.fillWithOutline(world, box, 21, 1, 21, 21, 3, 21, FILLER, FILLER, false);
            this.fillWithOutline(world, box, 22, 1, 21, 22, 2, 21, FILLER, FILLER, false);
            this.fillWithOutline(world, box, 23, 1, 21, 24, 1, 21, FILLER, FILLER, false);
            this.fillWithOutline(world, box, 42, 1, 21, 42, 6, 21, FILLER, FILLER, false);
            this.fillWithOutline(world, box, 41, 1, 21, 41, 5, 21, FILLER, FILLER, false);
            this.fillWithOutline(world, box, 37, 1, 21, 40, 4, 21, FILLER, FILLER, false);
            this.fillWithOutline(world, box, 36, 1, 21, 36, 3, 21, FILLER, FILLER, false);
            this.fillWithOutline(world, box, 35, 1, 21, 35, 2, 21, FILLER, FILLER, false);
            this.fillWithOutline(world, box, 33, 1, 21, 34, 1, 21, FILLER, FILLER, false);
         }
      }

      private void generateRoof(World world, Random random, StructureBox box) {
         if (this.intersects(box, 21, 21, 36, 36)) {
            this.fillWithOutline(world, box, 21, 0, 22, 36, 0, 36, BASE, BASE, false);
            this.fillWithOutline(world, box, 21, 1, 22, 36, 23, 36, FILLER, FILLER, false);

            for(int var4 = 0; var4 < 4; ++var4) {
               this.fillWithOutline(world, box, 21 + var4, 13 + var4, 21 + var4, 36 - var4, 13 + var4, 21 + var4, BASE_LIGHT, BASE_LIGHT, false);
               this.fillWithOutline(world, box, 21 + var4, 13 + var4, 36 - var4, 36 - var4, 13 + var4, 36 - var4, BASE_LIGHT, BASE_LIGHT, false);
               this.fillWithOutline(world, box, 21 + var4, 13 + var4, 22 + var4, 21 + var4, 13 + var4, 35 - var4, BASE_LIGHT, BASE_LIGHT, false);
               this.fillWithOutline(world, box, 36 - var4, 13 + var4, 22 + var4, 36 - var4, 13 + var4, 35 - var4, BASE_LIGHT, BASE_LIGHT, false);
            }

            this.fillWithOutline(world, box, 25, 16, 25, 32, 16, 32, BASE, BASE, false);
            this.fillWithOutline(world, box, 25, 17, 25, 25, 19, 25, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 32, 17, 25, 32, 19, 25, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 25, 17, 32, 25, 19, 32, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 32, 17, 32, 32, 19, 32, BASE_LIGHT, BASE_LIGHT, false);
            this.setBlockState(world, BASE_LIGHT, 26, 20, 26, box);
            this.setBlockState(world, BASE_LIGHT, 27, 21, 27, box);
            this.setBlockState(world, LIGHT_SOURCE, 27, 20, 27, box);
            this.setBlockState(world, BASE_LIGHT, 26, 20, 31, box);
            this.setBlockState(world, BASE_LIGHT, 27, 21, 30, box);
            this.setBlockState(world, LIGHT_SOURCE, 27, 20, 30, box);
            this.setBlockState(world, BASE_LIGHT, 31, 20, 31, box);
            this.setBlockState(world, BASE_LIGHT, 30, 21, 30, box);
            this.setBlockState(world, LIGHT_SOURCE, 30, 20, 30, box);
            this.setBlockState(world, BASE_LIGHT, 31, 20, 26, box);
            this.setBlockState(world, BASE_LIGHT, 30, 21, 27, box);
            this.setBlockState(world, LIGHT_SOURCE, 30, 20, 27, box);
            this.fillWithOutline(world, box, 28, 21, 27, 29, 21, 27, BASE, BASE, false);
            this.fillWithOutline(world, box, 27, 21, 28, 27, 21, 29, BASE, BASE, false);
            this.fillWithOutline(world, box, 28, 21, 30, 29, 21, 30, BASE, BASE, false);
            this.fillWithOutline(world, box, 30, 21, 28, 30, 21, 29, BASE, BASE, false);
         }
      }

      private void generateLowerWall(World world, Random random, StructureBox box) {
         if (this.intersects(box, 0, 21, 6, 58)) {
            this.fillWithOutline(world, box, 0, 0, 21, 6, 0, 57, BASE, BASE, false);
            this.fillWithOutline(world, box, 0, 1, 21, 6, 7, 57, FILLER, FILLER, false);
            this.fillWithOutline(world, box, 4, 4, 21, 6, 4, 53, BASE, BASE, false);

            for(int var4 = 0; var4 < 4; ++var4) {
               this.fillWithOutline(world, box, var4, var4 + 1, 21, var4, var4 + 1, 57 - var4, BASE_LIGHT, BASE_LIGHT, false);
            }

            for(int var5 = 23; var5 < 53; var5 += 3) {
               this.setBlockState(world, BASE_DECO, 5, 5, var5, box);
            }

            this.setBlockState(world, BASE_DECO, 5, 5, 52, box);

            for(int var6 = 0; var6 < 4; ++var6) {
               this.fillWithOutline(world, box, var6, var6 + 1, 21, var6, var6 + 1, 57 - var6, BASE_LIGHT, BASE_LIGHT, false);
            }

            this.fillWithOutline(world, box, 4, 1, 52, 6, 3, 52, BASE, BASE, false);
            this.fillWithOutline(world, box, 5, 1, 51, 5, 3, 53, BASE, BASE, false);
         }

         if (this.intersects(box, 51, 21, 58, 58)) {
            this.fillWithOutline(world, box, 51, 0, 21, 57, 0, 57, BASE, BASE, false);
            this.fillWithOutline(world, box, 51, 1, 21, 57, 7, 57, FILLER, FILLER, false);
            this.fillWithOutline(world, box, 51, 4, 21, 53, 4, 53, BASE, BASE, false);

            for(int var7 = 0; var7 < 4; ++var7) {
               this.fillWithOutline(world, box, 57 - var7, var7 + 1, 21, 57 - var7, var7 + 1, 57 - var7, BASE_LIGHT, BASE_LIGHT, false);
            }

            for(int var8 = 23; var8 < 53; var8 += 3) {
               this.setBlockState(world, BASE_DECO, 52, 5, var8, box);
            }

            this.setBlockState(world, BASE_DECO, 52, 5, 52, box);
            this.fillWithOutline(world, box, 51, 1, 52, 53, 3, 52, BASE, BASE, false);
            this.fillWithOutline(world, box, 52, 1, 51, 52, 3, 53, BASE, BASE, false);
         }

         if (this.intersects(box, 0, 51, 57, 57)) {
            this.fillWithOutline(world, box, 7, 0, 51, 50, 0, 57, BASE, BASE, false);
            this.fillWithOutline(world, box, 7, 1, 51, 50, 10, 57, FILLER, FILLER, false);

            for(int var9 = 0; var9 < 4; ++var9) {
               this.fillWithOutline(world, box, var9 + 1, var9 + 1, 57 - var9, 56 - var9, var9 + 1, 57 - var9, BASE_LIGHT, BASE_LIGHT, false);
            }
         }
      }

      private void generateMiddleWall(World world, Random random, StructureBox box) {
         if (this.intersects(box, 7, 21, 13, 50)) {
            this.fillWithOutline(world, box, 7, 0, 21, 13, 0, 50, BASE, BASE, false);
            this.fillWithOutline(world, box, 7, 1, 21, 13, 10, 50, FILLER, FILLER, false);
            this.fillWithOutline(world, box, 11, 8, 21, 13, 8, 53, BASE, BASE, false);

            for(int var4 = 0; var4 < 4; ++var4) {
               this.fillWithOutline(world, box, var4 + 7, var4 + 5, 21, var4 + 7, var4 + 5, 54, BASE_LIGHT, BASE_LIGHT, false);
            }

            for(int var5 = 21; var5 <= 45; var5 += 3) {
               this.setBlockState(world, BASE_DECO, 12, 9, var5, box);
            }
         }

         if (this.intersects(box, 44, 21, 50, 54)) {
            this.fillWithOutline(world, box, 44, 0, 21, 50, 0, 50, BASE, BASE, false);
            this.fillWithOutline(world, box, 44, 1, 21, 50, 10, 50, FILLER, FILLER, false);
            this.fillWithOutline(world, box, 44, 8, 21, 46, 8, 53, BASE, BASE, false);

            for(int var6 = 0; var6 < 4; ++var6) {
               this.fillWithOutline(world, box, 50 - var6, var6 + 5, 21, 50 - var6, var6 + 5, 54, BASE_LIGHT, BASE_LIGHT, false);
            }

            for(int var7 = 21; var7 <= 45; var7 += 3) {
               this.setBlockState(world, BASE_DECO, 45, 9, var7, box);
            }
         }

         if (this.intersects(box, 8, 44, 49, 54)) {
            this.fillWithOutline(world, box, 14, 0, 44, 43, 0, 50, BASE, BASE, false);
            this.fillWithOutline(world, box, 14, 1, 44, 43, 10, 50, FILLER, FILLER, false);

            for(int var8 = 12; var8 <= 45; var8 += 3) {
               this.setBlockState(world, BASE_DECO, var8, 9, 45, box);
               this.setBlockState(world, BASE_DECO, var8, 9, 52, box);
               if (var8 == 12 || var8 == 18 || var8 == 24 || var8 == 33 || var8 == 39 || var8 == 45) {
                  this.setBlockState(world, BASE_DECO, var8, 9, 47, box);
                  this.setBlockState(world, BASE_DECO, var8, 9, 50, box);
                  this.setBlockState(world, BASE_DECO, var8, 10, 45, box);
                  this.setBlockState(world, BASE_DECO, var8, 10, 46, box);
                  this.setBlockState(world, BASE_DECO, var8, 10, 51, box);
                  this.setBlockState(world, BASE_DECO, var8, 10, 52, box);
                  this.setBlockState(world, BASE_DECO, var8, 11, 47, box);
                  this.setBlockState(world, BASE_DECO, var8, 11, 50, box);
                  this.setBlockState(world, BASE_DECO, var8, 12, 48, box);
                  this.setBlockState(world, BASE_DECO, var8, 12, 49, box);
               }
            }

            for(int var9 = 0; var9 < 3; ++var9) {
               this.fillWithOutline(world, box, 8 + var9, 5 + var9, 54, 49 - var9, 5 + var9, 54, BASE, BASE, false);
            }

            this.fillWithOutline(world, box, 11, 8, 54, 46, 8, 54, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 14, 8, 44, 43, 8, 53, BASE, BASE, false);
         }
      }

      private void generateUpperWall(World world, Random random, StructureBox box) {
         if (this.intersects(box, 14, 21, 20, 43)) {
            this.fillWithOutline(world, box, 14, 0, 21, 20, 0, 43, BASE, BASE, false);
            this.fillWithOutline(world, box, 14, 1, 22, 20, 14, 43, FILLER, FILLER, false);
            this.fillWithOutline(world, box, 18, 12, 22, 20, 12, 39, BASE, BASE, false);
            this.fillWithOutline(world, box, 18, 12, 21, 20, 12, 21, BASE_LIGHT, BASE_LIGHT, false);

            for(int var4 = 0; var4 < 4; ++var4) {
               this.fillWithOutline(world, box, var4 + 14, var4 + 9, 21, var4 + 14, var4 + 9, 43 - var4, BASE_LIGHT, BASE_LIGHT, false);
            }

            for(int var5 = 23; var5 <= 39; var5 += 3) {
               this.setBlockState(world, BASE_DECO, 19, 13, var5, box);
            }
         }

         if (this.intersects(box, 37, 21, 43, 43)) {
            this.fillWithOutline(world, box, 37, 0, 21, 43, 0, 43, BASE, BASE, false);
            this.fillWithOutline(world, box, 37, 1, 22, 43, 14, 43, FILLER, FILLER, false);
            this.fillWithOutline(world, box, 37, 12, 22, 39, 12, 39, BASE, BASE, false);
            this.fillWithOutline(world, box, 37, 12, 21, 39, 12, 21, BASE_LIGHT, BASE_LIGHT, false);

            for(int var6 = 0; var6 < 4; ++var6) {
               this.fillWithOutline(world, box, 43 - var6, var6 + 9, 21, 43 - var6, var6 + 9, 43 - var6, BASE_LIGHT, BASE_LIGHT, false);
            }

            for(int var7 = 23; var7 <= 39; var7 += 3) {
               this.setBlockState(world, BASE_DECO, 38, 13, var7, box);
            }
         }

         if (this.intersects(box, 15, 37, 42, 43)) {
            this.fillWithOutline(world, box, 21, 0, 37, 36, 0, 43, BASE, BASE, false);
            this.fillWithOutline(world, box, 21, 1, 37, 36, 14, 43, FILLER, FILLER, false);
            this.fillWithOutline(world, box, 21, 12, 37, 36, 12, 39, BASE, BASE, false);

            for(int var8 = 0; var8 < 4; ++var8) {
               this.fillWithOutline(world, box, 15 + var8, var8 + 9, 43 - var8, 42 - var8, var8 + 9, 43 - var8, BASE_LIGHT, BASE_LIGHT, false);
            }

            for(int var9 = 21; var9 <= 36; var9 += 3) {
               this.setBlockState(world, BASE_DECO, var9, 13, 38, box);
            }
         }
      }
   }

   public abstract static class OceanMonumentPiece extends StructurePiece {
      protected static final BlockState BASE = Blocks.PRISMARINE.getStateFromMetadata(PrismarineBlock.ROUGH_VARIANT);
      protected static final BlockState BASE_LIGHT = Blocks.PRISMARINE.getStateFromMetadata(PrismarineBlock.BRICKS_VARIANT);
      protected static final BlockState BASE_DARK = Blocks.PRISMARINE.getStateFromMetadata(PrismarineBlock.DARK_VARIANT);
      protected static final BlockState BASE_DECO = BASE_LIGHT;
      protected static final BlockState LIGHT_SOURCE = Blocks.SEA_LANTERN.defaultState();
      protected static final BlockState FILLER = Blocks.WATER.defaultState();
      protected static final int SOURCE_INDEX = getPieceIndex(2, 0, 0);
      protected static final int TOP_CONNECT_INDEX = getPieceIndex(2, 2, 0);
      protected static final int LEFT_CONNECT_INDEX = getPieceIndex(0, 1, 0);
      protected static final int RIGHT_CONNECT_INDEX = getPieceIndex(4, 1, 0);
      protected OceanMonumentPieces.PieceDefinition pieceDefinition;

      protected static final int getPieceIndex(int rx, int ry, int rz) {
         return ry * 25 + rz * 5 + rx;
      }

      public OceanMonumentPiece() {
         super(0);
      }

      public OceanMonumentPiece(int i) {
         super(i);
      }

      public OceanMonumentPiece(Direction facing, StructureBox box) {
         super(1);
         this.facing = facing;
         this.box = box;
      }

      protected OceanMonumentPiece(int generationDepth, Direction facing, OceanMonumentPieces.PieceDefinition roomDefinition, int rx, int ry, int rz) {
         super(generationDepth);
         this.facing = facing;
         this.pieceDefinition = roomDefinition;
         int var7 = roomDefinition.index;
         int var8 = var7 % 5;
         int var9 = var7 / 5 % 5;
         int var10 = var7 / 25;
         if (facing != Direction.NORTH && facing != Direction.SOUTH) {
            this.box = new StructureBox(0, 0, 0, rz * 8 - 1, ry * 4 - 1, rx * 8 - 1);
         } else {
            this.box = new StructureBox(0, 0, 0, rx * 8 - 1, ry * 4 - 1, rz * 8 - 1);
         }

         switch(facing) {
            case NORTH:
               this.box.move(var8 * 8, var10 * 4, -(var9 + rz) * 8 + 1);
               break;
            case SOUTH:
               this.box.move(var8 * 8, var10 * 4, var9 * 8);
               break;
            case WEST:
               this.box.move(-(var9 + rz) * 8 + 1, var10 * 4, var8 * 8);
               break;
            default:
               this.box.move(var9 * 8, var10 * 4, var8 * 8);
         }
      }

      @Override
      protected void writeNbt(NbtCompound nbt) {
      }

      @Override
      protected void readNbt(NbtCompound nbt) {
      }

      protected void generateFloor(World world, StructureBox box, int x, int z, boolean fancy) {
         if (fancy) {
            this.fillWithOutline(world, box, x + 0, 0, z + 0, x + 2, 0, z + 8 - 1, BASE, BASE, false);
            this.fillWithOutline(world, box, x + 5, 0, z + 0, x + 8 - 1, 0, z + 8 - 1, BASE, BASE, false);
            this.fillWithOutline(world, box, x + 3, 0, z + 0, x + 4, 0, z + 2, BASE, BASE, false);
            this.fillWithOutline(world, box, x + 3, 0, z + 5, x + 4, 0, z + 8 - 1, BASE, BASE, false);
            this.fillWithOutline(world, box, x + 3, 0, z + 2, x + 4, 0, z + 2, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, x + 3, 0, z + 5, x + 4, 0, z + 5, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, x + 2, 0, z + 3, x + 2, 0, z + 4, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, x + 5, 0, z + 3, x + 5, 0, z + 4, BASE_LIGHT, BASE_LIGHT, false);
         } else {
            this.fillWithOutline(world, box, x + 0, 0, z + 0, x + 8 - 1, 0, z + 8 - 1, BASE, BASE, false);
         }
      }

      protected void replaceFiller(World world, StructureBox box, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockState state) {
         for(int var10 = minY; var10 <= maxY; ++var10) {
            for(int var11 = minX; var11 <= maxX; ++var11) {
               for(int var12 = minZ; var12 <= maxZ; ++var12) {
                  if (this.getBlockState(world, var11, var10, var12, box) == FILLER) {
                     this.setBlockState(world, state, var11, var10, var12, box);
                  }
               }
            }
         }
      }

      protected boolean intersects(StructureBox box, int minX, int minZ, int maxX, int maxZ) {
         int var6 = this.transformX(minX, minZ);
         int var7 = this.transformZ(minX, minZ);
         int var8 = this.transformX(maxX, maxZ);
         int var9 = this.transformZ(maxX, maxZ);
         return box.intersects(Math.min(var6, var8), Math.min(var7, var9), Math.max(var6, var8), Math.max(var7, var9));
      }

      protected boolean spawnElderGuardian(World world, StructureBox box, int x, int y, int z) {
         int var6 = this.transformX(x, z);
         int var7 = this.transformY(y);
         int var8 = this.transformZ(x, z);
         if (box.contains(new BlockPos(var6, var7, var8))) {
            GuardianEntity var9 = new GuardianEntity(world);
            var9.setElder(true);
            var9.heal(var9.getMaxHealth());
            var9.refreshPositionAndAngles((double)var6 + 0.5, (double)var7, (double)var8 + 0.5, 0.0F, 0.0F);
            var9.initialize(world.getLocalDifficulty(new BlockPos(var9)), null);
            world.addEntity(var9);
            return true;
         } else {
            return false;
         }
      }
   }

   public static class Penthouse extends OceanMonumentPieces.OceanMonumentPiece {
      public Penthouse() {
      }

      public Penthouse(Direction c_69garkogr, StructureBox c_70gletrex) {
         super(c_69garkogr, c_70gletrex);
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         this.fillWithOutline(world, box, 2, -1, 2, 11, -1, 11, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 0, -1, 0, 1, -1, 11, BASE, BASE, false);
         this.fillWithOutline(world, box, 12, -1, 0, 13, -1, 11, BASE, BASE, false);
         this.fillWithOutline(world, box, 2, -1, 0, 11, -1, 1, BASE, BASE, false);
         this.fillWithOutline(world, box, 2, -1, 12, 11, -1, 13, BASE, BASE, false);
         this.fillWithOutline(world, box, 0, 0, 0, 0, 0, 13, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 13, 0, 0, 13, 0, 13, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 1, 0, 0, 12, 0, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 1, 0, 13, 12, 0, 13, BASE_LIGHT, BASE_LIGHT, false);

         for(int var4 = 2; var4 <= 11; var4 += 3) {
            this.setBlockState(world, LIGHT_SOURCE, 0, 0, var4, box);
            this.setBlockState(world, LIGHT_SOURCE, 13, 0, var4, box);
            this.setBlockState(world, LIGHT_SOURCE, var4, 0, 0, box);
         }

         this.fillWithOutline(world, box, 2, 0, 3, 4, 0, 9, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 9, 0, 3, 11, 0, 9, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 4, 0, 9, 9, 0, 11, BASE_LIGHT, BASE_LIGHT, false);
         this.setBlockState(world, BASE_LIGHT, 5, 0, 8, box);
         this.setBlockState(world, BASE_LIGHT, 8, 0, 8, box);
         this.setBlockState(world, BASE_LIGHT, 10, 0, 10, box);
         this.setBlockState(world, BASE_LIGHT, 3, 0, 10, box);
         this.fillWithOutline(world, box, 3, 0, 3, 3, 0, 7, BASE_DARK, BASE_DARK, false);
         this.fillWithOutline(world, box, 10, 0, 3, 10, 0, 7, BASE_DARK, BASE_DARK, false);
         this.fillWithOutline(world, box, 6, 0, 10, 7, 0, 10, BASE_DARK, BASE_DARK, false);
         byte var7 = 3;

         for(int var5 = 0; var5 < 2; ++var5) {
            for(int var6 = 2; var6 <= 8; var6 += 3) {
               this.fillWithOutline(world, box, var7, 0, var6, var7, 2, var6, BASE_LIGHT, BASE_LIGHT, false);
            }

            var7 = 10;
         }

         this.fillWithOutline(world, box, 5, 0, 10, 5, 2, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 8, 0, 10, 8, 2, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 6, -1, 7, 7, -1, 8, BASE_DARK, BASE_DARK, false);
         this.fillWithOutline(world, box, 6, -1, 3, 7, -1, 4, FILLER, FILLER, false);
         this.spawnElderGuardian(world, box, 6, 1, 6);
         return true;
      }
   }

   static class PieceDefinition {
      int index;
      OceanMonumentPieces.PieceDefinition[] connections = new OceanMonumentPieces.PieceDefinition[6];
      boolean[] hasEntrance = new boolean[6];
      boolean claimed;
      boolean source;
      int scanIndex;

      public PieceDefinition(int index) {
         this.index = index;
      }

      public void updateConnection(Direction dir, OceanMonumentPieces.PieceDefinition connection) {
         this.connections[dir.getId()] = connection;
         connection.connections[dir.getOpposite().getId()] = this;
      }

      public void updateEntrances() {
         for(int var1 = 0; var1 < 6; ++var1) {
            this.hasEntrance[var1] = this.connections[var1] != null;
         }
      }

      public boolean findSource(int index) {
         if (this.source) {
            return true;
         } else {
            this.scanIndex = index;

            for(int var2 = 0; var2 < 6; ++var2) {
               if (this.connections[var2] != null
                  && this.hasEntrance[var2]
                  && this.connections[var2].scanIndex != index
                  && this.connections[var2].findSource(index)) {
                  return true;
               }
            }

            return false;
         }
      }

      public boolean isSpecial() {
         return this.index >= 75;
      }

      public int countEntrances() {
         int var1 = 0;

         for(int var2 = 0; var2 < 6; ++var2) {
            if (this.hasEntrance[var2]) {
               ++var1;
            }
         }

         return var1;
      }
   }

   interface RoomFitter {
      boolean fits(OceanMonumentPieces.PieceDefinition pieceDefinition);

      OceanMonumentPieces.OceanMonumentPiece create(Direction facing, OceanMonumentPieces.PieceDefinition pieceDefinition, Random random);
   }

   public static class SimpleRoom extends OceanMonumentPieces.OceanMonumentPiece {
      private int design;

      public SimpleRoom() {
      }

      public SimpleRoom(Direction facing, OceanMonumentPieces.PieceDefinition pieceDefinition, Random random) {
         super(1, facing, pieceDefinition, 1, 1, 1);
         this.design = random.nextInt(3);
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         if (this.pieceDefinition.index / 25 > 0) {
            this.generateFloor(world, box, 0, 0, this.pieceDefinition.hasEntrance[Direction.DOWN.getId()]);
         }

         if (this.pieceDefinition.connections[Direction.UP.getId()] == null) {
            this.replaceFiller(world, box, 1, 4, 1, 6, 4, 6, BASE);
         }

         boolean var4 = this.design != 0
            && random.nextBoolean()
            && !this.pieceDefinition.hasEntrance[Direction.DOWN.getId()]
            && !this.pieceDefinition.hasEntrance[Direction.UP.getId()]
            && this.pieceDefinition.countEntrances() > 1;
         if (this.design == 0) {
            this.fillWithOutline(world, box, 0, 1, 0, 2, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 0, 3, 0, 2, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 0, 2, 0, 0, 2, 2, BASE, BASE, false);
            this.fillWithOutline(world, box, 1, 2, 0, 2, 2, 0, BASE, BASE, false);
            this.setBlockState(world, LIGHT_SOURCE, 1, 2, 1, box);
            this.fillWithOutline(world, box, 5, 1, 0, 7, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 5, 3, 0, 7, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 7, 2, 0, 7, 2, 2, BASE, BASE, false);
            this.fillWithOutline(world, box, 5, 2, 0, 6, 2, 0, BASE, BASE, false);
            this.setBlockState(world, LIGHT_SOURCE, 6, 2, 1, box);
            this.fillWithOutline(world, box, 0, 1, 5, 2, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 0, 3, 5, 2, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 0, 2, 5, 0, 2, 7, BASE, BASE, false);
            this.fillWithOutline(world, box, 1, 2, 7, 2, 2, 7, BASE, BASE, false);
            this.setBlockState(world, LIGHT_SOURCE, 1, 2, 6, box);
            this.fillWithOutline(world, box, 5, 1, 5, 7, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 5, 3, 5, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 7, 2, 5, 7, 2, 7, BASE, BASE, false);
            this.fillWithOutline(world, box, 5, 2, 7, 6, 2, 7, BASE, BASE, false);
            this.setBlockState(world, LIGHT_SOURCE, 6, 2, 6, box);
            if (this.pieceDefinition.hasEntrance[Direction.SOUTH.getId()]) {
               this.fillWithOutline(world, box, 3, 3, 0, 4, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.fillWithOutline(world, box, 3, 3, 0, 4, 3, 1, BASE_LIGHT, BASE_LIGHT, false);
               this.fillWithOutline(world, box, 3, 2, 0, 4, 2, 0, BASE, BASE, false);
               this.fillWithOutline(world, box, 3, 1, 0, 4, 1, 1, BASE_LIGHT, BASE_LIGHT, false);
            }

            if (this.pieceDefinition.hasEntrance[Direction.NORTH.getId()]) {
               this.fillWithOutline(world, box, 3, 3, 7, 4, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.fillWithOutline(world, box, 3, 3, 6, 4, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
               this.fillWithOutline(world, box, 3, 2, 7, 4, 2, 7, BASE, BASE, false);
               this.fillWithOutline(world, box, 3, 1, 6, 4, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            }

            if (this.pieceDefinition.hasEntrance[Direction.WEST.getId()]) {
               this.fillWithOutline(world, box, 0, 3, 3, 0, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.fillWithOutline(world, box, 0, 3, 3, 1, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
               this.fillWithOutline(world, box, 0, 2, 3, 0, 2, 4, BASE, BASE, false);
               this.fillWithOutline(world, box, 0, 1, 3, 1, 1, 4, BASE_LIGHT, BASE_LIGHT, false);
            }

            if (this.pieceDefinition.hasEntrance[Direction.EAST.getId()]) {
               this.fillWithOutline(world, box, 7, 3, 3, 7, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.fillWithOutline(world, box, 6, 3, 3, 7, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
               this.fillWithOutline(world, box, 7, 2, 3, 7, 2, 4, BASE, BASE, false);
               this.fillWithOutline(world, box, 6, 1, 3, 7, 1, 4, BASE_LIGHT, BASE_LIGHT, false);
            }
         } else if (this.design == 1) {
            this.fillWithOutline(world, box, 2, 1, 2, 2, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 2, 1, 5, 2, 3, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 5, 1, 5, 5, 3, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 5, 1, 2, 5, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.setBlockState(world, LIGHT_SOURCE, 2, 2, 2, box);
            this.setBlockState(world, LIGHT_SOURCE, 2, 2, 5, box);
            this.setBlockState(world, LIGHT_SOURCE, 5, 2, 5, box);
            this.setBlockState(world, LIGHT_SOURCE, 5, 2, 2, box);
            this.fillWithOutline(world, box, 0, 1, 0, 1, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 0, 1, 1, 0, 3, 1, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 0, 1, 7, 1, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 0, 1, 6, 0, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 6, 1, 7, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 7, 1, 6, 7, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 6, 1, 0, 7, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 7, 1, 1, 7, 3, 1, BASE_LIGHT, BASE_LIGHT, false);
            this.setBlockState(world, BASE, 1, 2, 0, box);
            this.setBlockState(world, BASE, 0, 2, 1, box);
            this.setBlockState(world, BASE, 1, 2, 7, box);
            this.setBlockState(world, BASE, 0, 2, 6, box);
            this.setBlockState(world, BASE, 6, 2, 7, box);
            this.setBlockState(world, BASE, 7, 2, 6, box);
            this.setBlockState(world, BASE, 6, 2, 0, box);
            this.setBlockState(world, BASE, 7, 2, 1, box);
            if (!this.pieceDefinition.hasEntrance[Direction.SOUTH.getId()]) {
               this.fillWithOutline(world, box, 1, 3, 0, 6, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
               this.fillWithOutline(world, box, 1, 2, 0, 6, 2, 0, BASE, BASE, false);
               this.fillWithOutline(world, box, 1, 1, 0, 6, 1, 0, BASE_LIGHT, BASE_LIGHT, false);
            }

            if (!this.pieceDefinition.hasEntrance[Direction.NORTH.getId()]) {
               this.fillWithOutline(world, box, 1, 3, 7, 6, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
               this.fillWithOutline(world, box, 1, 2, 7, 6, 2, 7, BASE, BASE, false);
               this.fillWithOutline(world, box, 1, 1, 7, 6, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            }

            if (!this.pieceDefinition.hasEntrance[Direction.WEST.getId()]) {
               this.fillWithOutline(world, box, 0, 3, 1, 0, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
               this.fillWithOutline(world, box, 0, 2, 1, 0, 2, 6, BASE, BASE, false);
               this.fillWithOutline(world, box, 0, 1, 1, 0, 1, 6, BASE_LIGHT, BASE_LIGHT, false);
            }

            if (!this.pieceDefinition.hasEntrance[Direction.EAST.getId()]) {
               this.fillWithOutline(world, box, 7, 3, 1, 7, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
               this.fillWithOutline(world, box, 7, 2, 1, 7, 2, 6, BASE, BASE, false);
               this.fillWithOutline(world, box, 7, 1, 1, 7, 1, 6, BASE_LIGHT, BASE_LIGHT, false);
            }
         } else if (this.design == 2) {
            this.fillWithOutline(world, box, 0, 1, 0, 0, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 7, 1, 0, 7, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 1, 1, 0, 6, 1, 0, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 1, 1, 7, 6, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 0, 2, 0, 0, 2, 7, BASE_DARK, BASE_DARK, false);
            this.fillWithOutline(world, box, 7, 2, 0, 7, 2, 7, BASE_DARK, BASE_DARK, false);
            this.fillWithOutline(world, box, 1, 2, 0, 6, 2, 0, BASE_DARK, BASE_DARK, false);
            this.fillWithOutline(world, box, 1, 2, 7, 6, 2, 7, BASE_DARK, BASE_DARK, false);
            this.fillWithOutline(world, box, 0, 3, 0, 0, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 7, 3, 0, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 1, 3, 0, 6, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 1, 3, 7, 6, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 0, 1, 3, 0, 2, 4, BASE_DARK, BASE_DARK, false);
            this.fillWithOutline(world, box, 7, 1, 3, 7, 2, 4, BASE_DARK, BASE_DARK, false);
            this.fillWithOutline(world, box, 3, 1, 0, 4, 2, 0, BASE_DARK, BASE_DARK, false);
            this.fillWithOutline(world, box, 3, 1, 7, 4, 2, 7, BASE_DARK, BASE_DARK, false);
            if (this.pieceDefinition.hasEntrance[Direction.SOUTH.getId()]) {
               this.fillWithOutline(world, box, 3, 1, 0, 4, 2, 0, FILLER, FILLER, false);
            }

            if (this.pieceDefinition.hasEntrance[Direction.NORTH.getId()]) {
               this.fillWithOutline(world, box, 3, 1, 7, 4, 2, 7, FILLER, FILLER, false);
            }

            if (this.pieceDefinition.hasEntrance[Direction.WEST.getId()]) {
               this.fillWithOutline(world, box, 0, 1, 3, 0, 2, 4, FILLER, FILLER, false);
            }

            if (this.pieceDefinition.hasEntrance[Direction.EAST.getId()]) {
               this.fillWithOutline(world, box, 7, 1, 3, 7, 2, 4, FILLER, FILLER, false);
            }
         }

         if (var4) {
            this.fillWithOutline(world, box, 3, 1, 3, 4, 1, 4, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 3, 2, 3, 4, 2, 4, BASE, BASE, false);
            this.fillWithOutline(world, box, 3, 3, 3, 4, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
         }

         return true;
      }
   }

   static class SimpleRoomFitter implements OceanMonumentPieces.RoomFitter {
      private SimpleRoomFitter() {
      }

      @Override
      public boolean fits(OceanMonumentPieces.PieceDefinition pieceDefinition) {
         return true;
      }

      @Override
      public OceanMonumentPieces.OceanMonumentPiece create(Direction facing, OceanMonumentPieces.PieceDefinition pieceDefinition, Random random) {
         pieceDefinition.claimed = true;
         return new OceanMonumentPieces.SimpleRoom(facing, pieceDefinition, random);
      }
   }

   public static class SimpleTopRoom extends OceanMonumentPieces.OceanMonumentPiece {
      public SimpleTopRoom() {
      }

      public SimpleTopRoom(Direction facing, OceanMonumentPieces.PieceDefinition pieceDefinition, Random random) {
         super(1, facing, pieceDefinition, 1, 1, 1);
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         if (this.pieceDefinition.index / 25 > 0) {
            this.generateFloor(world, box, 0, 0, this.pieceDefinition.hasEntrance[Direction.DOWN.getId()]);
         }

         if (this.pieceDefinition.connections[Direction.UP.getId()] == null) {
            this.replaceFiller(world, box, 1, 4, 1, 6, 4, 6, BASE);
         }

         for(int var4 = 1; var4 <= 6; ++var4) {
            for(int var5 = 1; var5 <= 6; ++var5) {
               if (random.nextInt(3) != 0) {
                  int var6 = 2 + (random.nextInt(4) == 0 ? 0 : 1);
                  this.fillWithOutline(
                     world, box, var4, var6, var5, var4, 3, var5, Blocks.SPONGE.getStateFromMetadata(1), Blocks.SPONGE.getStateFromMetadata(1), false
                  );
               }
            }
         }

         this.fillWithOutline(world, box, 0, 1, 0, 0, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 7, 1, 0, 7, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 1, 1, 0, 6, 1, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 1, 1, 7, 6, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 0, 2, 0, 0, 2, 7, BASE_DARK, BASE_DARK, false);
         this.fillWithOutline(world, box, 7, 2, 0, 7, 2, 7, BASE_DARK, BASE_DARK, false);
         this.fillWithOutline(world, box, 1, 2, 0, 6, 2, 0, BASE_DARK, BASE_DARK, false);
         this.fillWithOutline(world, box, 1, 2, 7, 6, 2, 7, BASE_DARK, BASE_DARK, false);
         this.fillWithOutline(world, box, 0, 3, 0, 0, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 7, 3, 0, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 1, 3, 0, 6, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 1, 3, 7, 6, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.fillWithOutline(world, box, 0, 1, 3, 0, 2, 4, BASE_DARK, BASE_DARK, false);
         this.fillWithOutline(world, box, 7, 1, 3, 7, 2, 4, BASE_DARK, BASE_DARK, false);
         this.fillWithOutline(world, box, 3, 1, 0, 4, 2, 0, BASE_DARK, BASE_DARK, false);
         this.fillWithOutline(world, box, 3, 1, 7, 4, 2, 7, BASE_DARK, BASE_DARK, false);
         if (this.pieceDefinition.hasEntrance[Direction.SOUTH.getId()]) {
            this.fillWithOutline(world, box, 3, 1, 0, 4, 2, 0, FILLER, FILLER, false);
         }

         return true;
      }
   }

   static class SimpleTopRoomFitter implements OceanMonumentPieces.RoomFitter {
      private SimpleTopRoomFitter() {
      }

      @Override
      public boolean fits(OceanMonumentPieces.PieceDefinition pieceDefinition) {
         return !pieceDefinition.hasEntrance[Direction.WEST.getId()]
            && !pieceDefinition.hasEntrance[Direction.EAST.getId()]
            && !pieceDefinition.hasEntrance[Direction.NORTH.getId()]
            && !pieceDefinition.hasEntrance[Direction.SOUTH.getId()]
            && !pieceDefinition.hasEntrance[Direction.UP.getId()];
      }

      @Override
      public OceanMonumentPieces.OceanMonumentPiece create(Direction facing, OceanMonumentPieces.PieceDefinition pieceDefinition, Random random) {
         pieceDefinition.claimed = true;
         return new OceanMonumentPieces.SimpleTopRoom(facing, pieceDefinition, random);
      }
   }

   public static class WingRoom extends OceanMonumentPieces.OceanMonumentPiece {
      private int design;

      public WingRoom() {
      }

      public WingRoom(Direction facing, StructureBox box, int design) {
         super(facing, box);
         this.design = design & 1;
      }

      @Override
      public boolean postProcess(World world, Random random, StructureBox box) {
         if (this.design == 0) {
            for(int var4 = 0; var4 < 4; ++var4) {
               this.fillWithOutline(world, box, 10 - var4, 3 - var4, 20 - var4, 12 + var4, 3 - var4, 20, BASE_LIGHT, BASE_LIGHT, false);
            }

            this.fillWithOutline(world, box, 7, 0, 6, 15, 0, 16, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 6, 0, 6, 6, 3, 20, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 16, 0, 6, 16, 3, 20, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 7, 1, 7, 7, 1, 20, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 15, 1, 7, 15, 1, 20, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 7, 1, 6, 9, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 13, 1, 6, 15, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 8, 1, 7, 9, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 13, 1, 7, 14, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 9, 0, 5, 13, 0, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 10, 0, 7, 12, 0, 7, BASE_DARK, BASE_DARK, false);
            this.fillWithOutline(world, box, 8, 0, 10, 8, 0, 12, BASE_DARK, BASE_DARK, false);
            this.fillWithOutline(world, box, 14, 0, 10, 14, 0, 12, BASE_DARK, BASE_DARK, false);

            for(int var8 = 18; var8 >= 7; var8 -= 3) {
               this.setBlockState(world, LIGHT_SOURCE, 6, 3, var8, box);
               this.setBlockState(world, LIGHT_SOURCE, 16, 3, var8, box);
            }

            this.setBlockState(world, LIGHT_SOURCE, 10, 0, 10, box);
            this.setBlockState(world, LIGHT_SOURCE, 12, 0, 10, box);
            this.setBlockState(world, LIGHT_SOURCE, 10, 0, 12, box);
            this.setBlockState(world, LIGHT_SOURCE, 12, 0, 12, box);
            this.setBlockState(world, LIGHT_SOURCE, 8, 3, 6, box);
            this.setBlockState(world, LIGHT_SOURCE, 14, 3, 6, box);
            this.setBlockState(world, BASE_LIGHT, 4, 2, 4, box);
            this.setBlockState(world, LIGHT_SOURCE, 4, 1, 4, box);
            this.setBlockState(world, BASE_LIGHT, 4, 0, 4, box);
            this.setBlockState(world, BASE_LIGHT, 18, 2, 4, box);
            this.setBlockState(world, LIGHT_SOURCE, 18, 1, 4, box);
            this.setBlockState(world, BASE_LIGHT, 18, 0, 4, box);
            this.setBlockState(world, BASE_LIGHT, 4, 2, 18, box);
            this.setBlockState(world, LIGHT_SOURCE, 4, 1, 18, box);
            this.setBlockState(world, BASE_LIGHT, 4, 0, 18, box);
            this.setBlockState(world, BASE_LIGHT, 18, 2, 18, box);
            this.setBlockState(world, LIGHT_SOURCE, 18, 1, 18, box);
            this.setBlockState(world, BASE_LIGHT, 18, 0, 18, box);
            this.setBlockState(world, BASE_LIGHT, 9, 7, 20, box);
            this.setBlockState(world, BASE_LIGHT, 13, 7, 20, box);
            this.fillWithOutline(world, box, 6, 0, 21, 7, 4, 21, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 15, 0, 21, 16, 4, 21, BASE_LIGHT, BASE_LIGHT, false);
            this.spawnElderGuardian(world, box, 11, 2, 16);
         } else if (this.design == 1) {
            this.fillWithOutline(world, box, 9, 3, 18, 13, 3, 20, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 9, 0, 18, 9, 2, 18, BASE_LIGHT, BASE_LIGHT, false);
            this.fillWithOutline(world, box, 13, 0, 18, 13, 2, 18, BASE_LIGHT, BASE_LIGHT, false);
            byte var9 = 9;
            byte var5 = 20;
            byte var6 = 5;

            for(int var7 = 0; var7 < 2; ++var7) {
               this.setBlockState(world, BASE_LIGHT, var9, var6 + 1, var5, box);
               this.setBlockState(world, LIGHT_SOURCE, var9, var6, var5, box);
               this.setBlockState(world, BASE_LIGHT, var9, var6 - 1, var5, box);
               var9 = 13;
            }

            this.fillWithOutline(world, box, 7, 3, 7, 15, 3, 14, BASE_LIGHT, BASE_LIGHT, false);
            var9 = 10;

            for(int var12 = 0; var12 < 2; ++var12) {
               this.fillWithOutline(world, box, var9, 0, 10, var9, 6, 10, BASE_LIGHT, BASE_LIGHT, false);
               this.fillWithOutline(world, box, var9, 0, 12, var9, 6, 12, BASE_LIGHT, BASE_LIGHT, false);
               this.setBlockState(world, LIGHT_SOURCE, var9, 0, 10, box);
               this.setBlockState(world, LIGHT_SOURCE, var9, 0, 12, box);
               this.setBlockState(world, LIGHT_SOURCE, var9, 4, 10, box);
               this.setBlockState(world, LIGHT_SOURCE, var9, 4, 12, box);
               var9 = 12;
            }

            var9 = 8;

            for(int var13 = 0; var13 < 2; ++var13) {
               this.fillWithOutline(world, box, var9, 0, 7, var9, 2, 7, BASE_LIGHT, BASE_LIGHT, false);
               this.fillWithOutline(world, box, var9, 0, 14, var9, 2, 14, BASE_LIGHT, BASE_LIGHT, false);
               var9 = 14;
            }

            this.fillWithOutline(world, box, 8, 3, 8, 8, 3, 13, BASE_DARK, BASE_DARK, false);
            this.fillWithOutline(world, box, 14, 3, 8, 14, 3, 13, BASE_DARK, BASE_DARK, false);
            this.spawnElderGuardian(world, box, 11, 5, 13);
         }

         return true;
      }
   }
}
