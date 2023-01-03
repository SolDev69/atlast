package net.minecraft.block.material;

public class LiquidMaterial extends Material {
   public LiquidMaterial(MaterialColor c_71wxkaaxh) {
      super(c_71wxkaaxh);
      this.setReplaceable();
      this.setDestroyOnPistonMove();
   }

   @Override
   public boolean isLiquid() {
      return true;
   }

   @Override
   public boolean blocksMovement() {
      return false;
   }

   @Override
   public boolean isSolid() {
      return false;
   }
}
