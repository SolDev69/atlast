package net.minecraft.block.material;

public class PlantMaterial extends Material {
   public PlantMaterial(MaterialColor c_71wxkaaxh) {
      super(c_71wxkaaxh);
      this.setCanBeBrokenInAdventureMode();
   }

   @Override
   public boolean isSolid() {
      return false;
   }

   @Override
   public boolean isOpaque() {
      return false;
   }

   @Override
   public boolean blocksMovement() {
      return false;
   }
}
