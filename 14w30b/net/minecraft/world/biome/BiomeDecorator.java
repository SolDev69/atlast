package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.StoneBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.chunk.GeneratorOptions;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.BlockPatchFeature;
import net.minecraft.world.gen.feature.CactusFeature;
import net.minecraft.world.gen.feature.ClayPatchFeature;
import net.minecraft.world.gen.feature.DeadBushFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FlowerFeature;
import net.minecraft.world.gen.feature.HugeMushroomFeature;
import net.minecraft.world.gen.feature.LilyPadFeature;
import net.minecraft.world.gen.feature.LiquidFallFeature;
import net.minecraft.world.gen.feature.PlantFeature;
import net.minecraft.world.gen.feature.PumpkinPatchFeature;
import net.minecraft.world.gen.feature.SugarcaneFeature;
import net.minecraft.world.gen.feature.VeinFeature;

public class BiomeDecorator {
   protected World world;
   protected Random random;
   protected BlockPos pos;
   protected GeneratorOptions options;
   protected Feature clayPatch = new ClayPatchFeature(4);
   protected Feature sandPatch = new BlockPatchFeature(Blocks.SAND, 7);
   protected Feature gravelPatch = new BlockPatchFeature(Blocks.GRAVEL, 6);
   protected Feature dirtVein;
   protected Feature gravelVein;
   protected Feature graniteVein;
   protected Feature dioriteVein;
   protected Feature andesiteVein;
   protected Feature coalOreVein;
   protected Feature ironOreVein;
   protected Feature goldOreVein;
   protected Feature redstoneOreVein;
   protected Feature diomandOreVein;
   protected Feature lapisOreVein;
   protected FlowerFeature flower = new FlowerFeature(Blocks.YELLOW_FLOWER, FlowerBlock.Type.DANDELION);
   protected Feature brownMushroom = new PlantFeature(Blocks.BROWN_MUSHROOM);
   protected Feature redMushroom = new PlantFeature(Blocks.RED_MUSHROOM);
   protected Feature hugeMushroom = new HugeMushroomFeature();
   protected Feature sugarcane = new SugarcaneFeature();
   protected Feature cactus = new CactusFeature();
   protected Feature lilyPad = new LilyPadFeature();
   protected int lilyPadAttempts;
   protected int treeAttempts;
   protected int flowerAttempts = 2;
   protected int grassAttempts = 1;
   protected int deadBushAttempts;
   protected int mushroomAttempts;
   protected int sugarcaneAttempts;
   protected int cactusAttempts;
   protected int gravelPatchAttempts = 1;
   protected int sandPatchAttempts = 3;
   protected int clayPatchAttempts = 1;
   protected int hugeMushroomAttempts;
   public boolean placeLakes = true;

   public void decorate(World world, Random random, Biome biome, BlockPos pos) {
      if (this.world != null) {
         throw new RuntimeException("Already decorating");
      } else {
         this.world = world;
         String var5 = world.getData().getGeneratorOptions();
         if (var5 != null) {
            this.options = GeneratorOptions.Factory.fromJson(var5).create();
         } else {
            this.options = GeneratorOptions.Factory.fromJson("").create();
         }

         this.random = random;
         this.pos = pos;
         this.dirtVein = new VeinFeature(Blocks.DIRT.defaultState(), this.options.dirtSize);
         this.gravelVein = new VeinFeature(Blocks.GRAVEL.defaultState(), this.options.gravelSize);
         this.graniteVein = new VeinFeature(Blocks.STONE.defaultState().set(StoneBlock.VARIANT, StoneBlock.Variant.GRANITE), this.options.graniteSize);
         this.dioriteVein = new VeinFeature(Blocks.STONE.defaultState().set(StoneBlock.VARIANT, StoneBlock.Variant.DIORITE), this.options.dioriteSize);
         this.andesiteVein = new VeinFeature(Blocks.STONE.defaultState().set(StoneBlock.VARIANT, StoneBlock.Variant.ANDESITE), this.options.andesiteSize);
         this.coalOreVein = new VeinFeature(Blocks.COAL_ORE.defaultState(), this.options.coalSize);
         this.ironOreVein = new VeinFeature(Blocks.IRON_ORE.defaultState(), this.options.ironSize);
         this.goldOreVein = new VeinFeature(Blocks.GOLD_ORE.defaultState(), this.options.goldSize);
         this.redstoneOreVein = new VeinFeature(Blocks.REDSTONE_ORE.defaultState(), this.options.redstoneSize);
         this.diomandOreVein = new VeinFeature(Blocks.DIAMOND_ORE.defaultState(), this.options.diamondSize);
         this.lapisOreVein = new VeinFeature(Blocks.LAPIS_ORE.defaultState(), this.options.lapisSize);
         this.decorate(biome);
         this.world = null;
         this.random = null;
      }
   }

   protected void decorate(Biome biome) {
      this.placeVeins();

      for(int var2 = 0; var2 < this.sandPatchAttempts; ++var2) {
         int var3 = this.random.nextInt(16) + 8;
         int var4 = this.random.nextInt(16) + 8;
         this.sandPatch.place(this.world, this.random, this.world.getSurfaceHeight(this.pos.add(var3, 0, var4)));
      }

      for(int var10 = 0; var10 < this.clayPatchAttempts; ++var10) {
         int var13 = this.random.nextInt(16) + 8;
         int var30 = this.random.nextInt(16) + 8;
         this.clayPatch.place(this.world, this.random, this.world.getSurfaceHeight(this.pos.add(var13, 0, var30)));
      }

      for(int var11 = 0; var11 < this.gravelPatchAttempts; ++var11) {
         int var14 = this.random.nextInt(16) + 8;
         int var31 = this.random.nextInt(16) + 8;
         this.gravelPatch.place(this.world, this.random, this.world.getSurfaceHeight(this.pos.add(var14, 0, var31)));
      }

      int var12 = this.treeAttempts;
      if (this.random.nextInt(10) == 0) {
         ++var12;
      }

      for(int var15 = 0; var15 < var12; ++var15) {
         int var32 = this.random.nextInt(16) + 8;
         int var5 = this.random.nextInt(16) + 8;
         AbstractTreeFeature var6 = biome.getRandomTree(this.random);
         var6.prepare();
         BlockPos var7 = this.world.getHeight(this.pos.add(var32, 0, var5));
         if (var6.place(this.world, this.random, var7)) {
            var6.placeSoil(this.world, this.random, var7);
         }
      }

      for(int var16 = 0; var16 < this.hugeMushroomAttempts; ++var16) {
         int var33 = this.random.nextInt(16) + 8;
         int var48 = this.random.nextInt(16) + 8;
         this.hugeMushroom.place(this.world, this.random, this.world.getHeight(this.pos.add(var33, 0, var48)));
      }

      for(int var17 = 0; var17 < this.flowerAttempts; ++var17) {
         int var34 = this.random.nextInt(16) + 8;
         int var49 = this.random.nextInt(16) + 8;
         int var61 = this.random.nextInt(this.world.getHeight(this.pos.add(var34, 0, var49)).getY() + 32);
         BlockPos var70 = this.pos.add(var34, var61, var49);
         FlowerBlock.Type var8 = biome.getRandomFlower(this.random, var70);
         FlowerBlock var9 = var8.getGroup().getBlock();
         if (var9.getMaterial() != Material.AIR) {
            this.flower.set(var9, var8);
            this.flower.place(this.world, this.random, var70);
         }
      }

      for(int var18 = 0; var18 < this.grassAttempts; ++var18) {
         int var35 = this.random.nextInt(16) + 8;
         int var50 = this.random.nextInt(16) + 8;
         int var62 = this.random.nextInt(this.world.getHeight(this.pos.add(var35, 0, var50)).getY() * 2);
         biome.getRandomGrass(this.random).place(this.world, this.random, this.pos.add(var35, var62, var50));
      }

      for(int var19 = 0; var19 < this.deadBushAttempts; ++var19) {
         int var36 = this.random.nextInt(16) + 8;
         int var51 = this.random.nextInt(16) + 8;
         int var63 = this.random.nextInt(this.world.getHeight(this.pos.add(var36, 0, var51)).getY() * 2);
         new DeadBushFeature().place(this.world, this.random, this.pos.add(var36, var63, var51));
      }

      for(int var20 = 0; var20 < this.lilyPadAttempts; ++var20) {
         int var37 = this.random.nextInt(16) + 8;
         int var52 = this.random.nextInt(16) + 8;
         int var64 = this.random.nextInt(this.world.getHeight(this.pos.add(var37, 0, var52)).getY() * 2);

         BlockPos var71;
         BlockPos var73;
         for(var71 = this.pos.add(var37, var64, var52); var71.getY() > 0; var71 = var73) {
            var73 = var71.down();
            if (!this.world.isAir(var73)) {
               break;
            }
         }

         this.lilyPad.place(this.world, this.random, var71);
      }

      for(int var21 = 0; var21 < this.mushroomAttempts; ++var21) {
         if (this.random.nextInt(4) == 0) {
            int var38 = this.random.nextInt(16) + 8;
            int var53 = this.random.nextInt(16) + 8;
            BlockPos var65 = this.world.getHeight(this.pos.add(var38, 0, var53));
            this.brownMushroom.place(this.world, this.random, var65);
         }

         if (this.random.nextInt(8) == 0) {
            int var39 = this.random.nextInt(16) + 8;
            int var54 = this.random.nextInt(16) + 8;
            int var66 = this.random.nextInt(this.world.getHeight(this.pos.add(var39, 0, var54)).getY() * 2);
            BlockPos var72 = this.pos.add(var39, var66, var54);
            this.redMushroom.place(this.world, this.random, var72);
         }
      }

      if (this.random.nextInt(4) == 0) {
         int var22 = this.random.nextInt(16) + 8;
         int var40 = this.random.nextInt(16) + 8;
         int var55 = this.random.nextInt(this.world.getHeight(this.pos.add(var22, 0, var40)).getY() * 2);
         this.brownMushroom.place(this.world, this.random, this.pos.add(var22, var55, var40));
      }

      if (this.random.nextInt(8) == 0) {
         int var23 = this.random.nextInt(16) + 8;
         int var41 = this.random.nextInt(16) + 8;
         int var56 = this.random.nextInt(this.world.getHeight(this.pos.add(var23, 0, var41)).getY() * 2);
         this.redMushroom.place(this.world, this.random, this.pos.add(var23, var56, var41));
      }

      for(int var24 = 0; var24 < this.sugarcaneAttempts; ++var24) {
         int var42 = this.random.nextInt(16) + 8;
         int var57 = this.random.nextInt(16) + 8;
         int var67 = this.random.nextInt(this.world.getHeight(this.pos.add(var42, 0, var57)).getY() * 2);
         this.sugarcane.place(this.world, this.random, this.pos.add(var42, var67, var57));
      }

      for(int var25 = 0; var25 < 10; ++var25) {
         int var43 = this.random.nextInt(16) + 8;
         int var58 = this.random.nextInt(16) + 8;
         int var68 = this.random.nextInt(this.world.getHeight(this.pos.add(var43, 0, var58)).getY() * 2);
         this.sugarcane.place(this.world, this.random, this.pos.add(var43, var68, var58));
      }

      if (this.random.nextInt(32) == 0) {
         int var26 = this.random.nextInt(16) + 8;
         int var44 = this.random.nextInt(16) + 8;
         int var59 = this.random.nextInt(this.world.getHeight(this.pos.add(var26, 0, var44)).getY() * 2);
         new PumpkinPatchFeature().place(this.world, this.random, this.pos.add(var26, var59, var44));
      }

      for(int var27 = 0; var27 < this.cactusAttempts; ++var27) {
         int var45 = this.random.nextInt(16) + 8;
         int var60 = this.random.nextInt(16) + 8;
         int var69 = this.random.nextInt(this.world.getHeight(this.pos.add(var45, 0, var60)).getY() * 2);
         this.cactus.place(this.world, this.random, this.pos.add(var45, var69, var60));
      }

      if (this.placeLakes) {
         for(int var28 = 0; var28 < 50; ++var28) {
            BlockPos var46 = this.pos.add(this.random.nextInt(16) + 8, this.random.nextInt(this.random.nextInt(248) + 8), this.random.nextInt(16) + 8);
            new LiquidFallFeature(Blocks.FLOWING_WATER).place(this.world, this.random, var46);
         }

         for(int var29 = 0; var29 < 20; ++var29) {
            BlockPos var47 = this.pos
               .add(this.random.nextInt(16) + 8, this.random.nextInt(this.random.nextInt(this.random.nextInt(240) + 8) + 8), this.random.nextInt(16) + 8);
            new LiquidFallFeature(Blocks.FLOWING_LAVA).place(this.world, this.random, var47);
         }
      }
   }

   protected void placeVeinLinear(int count, Feature vein, int minHeight, int maxHeight) {
      if (maxHeight < minHeight) {
         int var5 = minHeight;
         minHeight = maxHeight;
         maxHeight = var5;
      } else if (maxHeight == minHeight) {
         if (minHeight < 255) {
            ++maxHeight;
         } else {
            --minHeight;
         }
      }

      for(int var7 = 0; var7 < count; ++var7) {
         BlockPos var6 = this.pos.add(this.random.nextInt(16), this.random.nextInt(maxHeight - minHeight) + minHeight, this.random.nextInt(16));
         vein.place(this.world, this.random, var6);
      }
   }

   protected void placeVeinTriangular(int count, Feature vein, int minHeight, int maxHeight) {
      for(int var5 = 0; var5 < count; ++var5) {
         BlockPos var6 = this.pos
            .add(this.random.nextInt(16), this.random.nextInt(maxHeight) + this.random.nextInt(maxHeight) + minHeight - maxHeight, this.random.nextInt(16));
         vein.place(this.world, this.random, var6);
      }
   }

   protected void placeVeins() {
      this.placeVeinLinear(this.options.dirtCount, this.dirtVein, this.options.dirtMinHeight, this.options.dirtMaxHeight);
      this.placeVeinLinear(this.options.gravelCount, this.gravelVein, this.options.gravelMinHeight, this.options.gravelMaxHeight);
      this.placeVeinLinear(this.options.dioriteCount, this.dioriteVein, this.options.dioriteMinHeight, this.options.dioriteMaxHeight);
      this.placeVeinLinear(this.options.graniteCount, this.graniteVein, this.options.graniteMinHeight, this.options.graniteMaxHeight);
      this.placeVeinLinear(this.options.andesiteCount, this.andesiteVein, this.options.andesiteMinHeight, this.options.andesiteMaxHeight);
      this.placeVeinLinear(this.options.coalCount, this.coalOreVein, this.options.coalMinHeight, this.options.coalMaxHeight);
      this.placeVeinLinear(this.options.ironCount, this.ironOreVein, this.options.ironMinHeight, this.options.ironMaxHeight);
      this.placeVeinLinear(this.options.goldCount, this.goldOreVein, this.options.goldMinHeight, this.options.goldMaxHeight);
      this.placeVeinLinear(this.options.redstoneCount, this.redstoneOreVein, this.options.redstoneMinHeight, this.options.redstoneMaxHeight);
      this.placeVeinLinear(this.options.diamondCount, this.diomandOreVein, this.options.diamondMinHeight, this.options.diamondMaxHeight);
      this.placeVeinTriangular(this.options.lapisCount, this.lapisOreVein, this.options.lapisMinHeight, this.options.lapisMaxHeight);
   }
}
