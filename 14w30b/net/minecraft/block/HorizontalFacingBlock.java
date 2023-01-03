package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.property.DirectionProperty;
import net.minecraft.util.math.Direction;

public abstract class HorizontalFacingBlock extends Block {
   public static final DirectionProperty FACING = DirectionProperty.of("facing", Direction.Plane.HORIZONTAL);

   protected HorizontalFacingBlock(Material c_57ywipuwq) {
      super(c_57ywipuwq);
   }
}
