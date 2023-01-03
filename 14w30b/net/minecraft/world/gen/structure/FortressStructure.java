package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.living.mob.MagmaCubeEntity;
import net.minecraft.entity.living.mob.hostile.BlazeEntity;
import net.minecraft.entity.living.mob.hostile.SkeletonEntity;
import net.minecraft.entity.living.mob.hostile.ZombiePigmanEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class FortressStructure extends StructureFeature {
   private List monsterSpawns = Lists.newArrayList();

   public FortressStructure() {
      this.monsterSpawns.add(new Biome.SpawnEntry(BlazeEntity.class, 10, 2, 3));
      this.monsterSpawns.add(new Biome.SpawnEntry(ZombiePigmanEntity.class, 5, 4, 4));
      this.monsterSpawns.add(new Biome.SpawnEntry(SkeletonEntity.class, 10, 4, 4));
      this.monsterSpawns.add(new Biome.SpawnEntry(MagmaCubeEntity.class, 3, 4, 4));
   }

   @Override
   public String getName() {
      return "Fortress";
   }

   public List getMonsterSpawns() {
      return this.monsterSpawns;
   }

   @Override
   protected boolean isFeatureChunk(int chunkX, int chunkZ) {
      int var3 = chunkX >> 4;
      int var4 = chunkZ >> 4;
      this.random.setSeed((long)(var3 ^ var4 << 4) ^ this.world.getSeed());
      this.random.nextInt();
      if (this.random.nextInt(3) != 0) {
         return false;
      } else if (chunkX != (var3 << 4) + 4 + this.random.nextInt(8)) {
         return false;
      } else {
         return chunkZ == (var4 << 4) + 4 + this.random.nextInt(8);
      }
   }

   @Override
   protected StructureStart createStart(int x, int z) {
      return new FortressStructure.Start(this.world, this.random, x, z);
   }

   public static class Start extends StructureStart {
      public Start() {
      }

      public Start(World world, Random random, int chunkX, int chunkZ) {
         super(chunkX, chunkZ);
         FortressPieces.Start var5 = new FortressPieces.Start(random, (chunkX << 4) + 2, (chunkZ << 4) + 2);
         this.pieces.add(var5);
         var5.addChildren(var5, this.pieces, random);
         List var6 = var5.children;

         while(!var6.isEmpty()) {
            int var7 = random.nextInt(var6.size());
            StructurePiece var8 = (StructurePiece)var6.remove(var7);
            var8.addChildren(var5, this.pieces, random);
         }

         this.calculateBoundingBox();
         this.moveBetweenYCoords(world, random, 48, 70);
      }
   }
}
