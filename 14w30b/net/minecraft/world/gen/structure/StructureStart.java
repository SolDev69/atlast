package net.minecraft.world.gen.structure;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public abstract class StructureStart {
   protected LinkedList pieces = new LinkedList();
   protected StructureBox box;
   private int chunkX;
   private int chunkZ;

   public StructureStart() {
   }

   public StructureStart(int chunkX, int chunkZ) {
      this.chunkX = chunkX;
      this.chunkZ = chunkZ;
   }

   public StructureBox getBoundingBox() {
      return this.box;
   }

   public LinkedList getPieces() {
      return this.pieces;
   }

   public void postProcess(World world, Random random, StructureBox box) {
      Iterator var4 = this.pieces.iterator();

      while(var4.hasNext()) {
         StructurePiece var5 = (StructurePiece)var4.next();
         if (var5.getBoundingBox().intersects(box) && !var5.postProcess(world, random, box)) {
            var4.remove();
         }
      }
   }

   protected void calculateBoundingBox() {
      this.box = StructureBox.infinite();

      for(StructurePiece var2 : this.pieces) {
         this.box.union(var2.getBoundingBox());
      }
   }

   public NbtCompound toNbt(int chunkX, int chunkZ) {
      NbtCompound var3 = new NbtCompound();
      var3.putString("id", StructureManager.getId(this));
      var3.putInt("ChunkX", chunkX);
      var3.putInt("ChunkZ", chunkZ);
      var3.put("BB", this.box.toNbt());
      NbtList var4 = new NbtList();

      for(StructurePiece var6 : this.pieces) {
         var4.add(var6.toNbt());
      }

      var3.put("Children", var4);
      this.writeValidityNbt(var3);
      return var3;
   }

   public void writeValidityNbt(NbtCompound nbt) {
   }

   public void readNbt(World world, NbtCompound nbt) {
      this.chunkX = nbt.getInt("ChunkX");
      this.chunkZ = nbt.getInt("ChunkZ");
      if (nbt.contains("BB")) {
         this.box = new StructureBox(nbt.getIntArray("BB"));
      }

      NbtList var3 = nbt.getList("Children", 10);

      for(int var4 = 0; var4 < var3.size(); ++var4) {
         this.pieces.add(StructureManager.getPieceFromNbt(var3.getCompound(var4), world));
      }

      this.readValidityNbt(nbt);
   }

   public void readValidityNbt(NbtCompound nbt) {
   }

   protected void moveBelowSeaLevel(World world, Random random, int y) {
      int var4 = 63 - y;
      int var5 = this.box.getSpanY() + 1;
      if (var5 < var4) {
         var5 += random.nextInt(var4 - var5);
      }

      int var6 = var5 - this.box.maxY;
      this.box.move(0, var6, 0);

      for(StructurePiece var8 : this.pieces) {
         var8.getBoundingBox().move(0, var6, 0);
      }
   }

   protected void moveBetweenYCoords(World world, Random random, int yMin, int yMax) {
      int var5 = yMax - yMin + 1 - this.box.getSpanY();
      int var6 = 1;
      if (var5 > 1) {
         var6 = yMin + random.nextInt(var5);
      } else {
         var6 = yMin;
      }

      int var7 = var6 - this.box.minY;
      this.box.move(0, var7, 0);

      for(StructurePiece var9 : this.pieces) {
         var9.getBoundingBox().move(0, var7, 0);
      }
   }

   public boolean isValid() {
      return true;
   }

   public boolean isValid(ChunkPos chunkPos) {
      return true;
   }

   public void postPlacement(ChunkPos chunkPos) {
   }

   public int getChunkX() {
      return this.chunkX;
   }

   public int getChunkZ() {
      return this.chunkZ;
   }
}
