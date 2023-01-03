package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.property.EnumProperty;
import net.minecraft.util.math.Direction;

public abstract class AxisBlock extends Block {
   public static final EnumProperty AXIS = EnumProperty.of("axis", Direction.Axis.class);

   protected AxisBlock(Material c_57ywipuwq) {
      super(c_57ywipuwq);
   }
}
