package net.minecraft.block.material;

public class PortalMaterial extends Material {
   public PortalMaterial(MaterialColor c_71wxkaaxh) {
      super(c_71wxkaaxh);
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
