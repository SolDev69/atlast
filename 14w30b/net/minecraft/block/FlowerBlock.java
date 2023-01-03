package net.minecraft.block;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.StateDefinition;
import net.minecraft.block.state.property.EnumProperty;
import net.minecraft.block.state.property.Property;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.StringRepresentable;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public abstract class FlowerBlock extends PlantBlock {
   protected EnumProperty type;

   protected FlowerBlock() {
      super(Material.PLANT);
      this.setDefaultState(
         this.stateDefinition
            .any()
            .set(this.getTypeProperty(), this.getFlowerGroup() == FlowerBlock.Group.RED ? FlowerBlock.Type.POPPY : FlowerBlock.Type.DANDELION)
      );
   }

   @Override
   public int getDropItemMetadata(BlockState state) {
      return ((FlowerBlock.Type)state.get(this.getTypeProperty())).getIndex();
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addToCreativeMenu(Item item, ItemGroup group, List stacks) {
      for(FlowerBlock.Type var7 : FlowerBlock.Type.ofGroup(this.getFlowerGroup())) {
         stacks.add(new ItemStack(item, 1, var7.getIndex()));
      }
   }

   @Override
   public BlockState getStateFromMetadata(int metadata) {
      return this.defaultState().set(this.getTypeProperty(), FlowerBlock.Type.byIndex(this.getFlowerGroup(), metadata));
   }

   public abstract FlowerBlock.Group getFlowerGroup();

   public Property getTypeProperty() {
      if (this.type == null) {
         this.type = EnumProperty.of("type", FlowerBlock.Type.class, new Predicate() {
            public boolean apply(FlowerBlock.Type c_71umdcnft) {
               return c_71umdcnft.getGroup() == FlowerBlock.this.getFlowerGroup();
            }
         });
      }

      return this.type;
   }

   @Override
   public int getMetadataFromState(BlockState state) {
      return ((FlowerBlock.Type)state.get(this.getTypeProperty())).getIndex();
   }

   @Override
   protected StateDefinition createStateDefinition() {
      return new StateDefinition(this, this.getTypeProperty());
   }

   @Environment(EnvType.CLIENT)
   @Override
   public Block.OffsetType getOffsetType() {
      return Block.OffsetType.XZ;
   }

   public static enum Group {
      YELLOW,
      RED;

      public FlowerBlock getBlock() {
         return this == YELLOW ? Blocks.YELLOW_FLOWER : Blocks.RED_FLOWER;
      }
   }

   public static enum Type implements StringRepresentable {
      DANDELION(FlowerBlock.Group.YELLOW, 0, "dandelion"),
      POPPY(FlowerBlock.Group.RED, 0, "poppy"),
      BLUE_ORCHID(FlowerBlock.Group.RED, 1, "blue_orchid", "blueOrchid"),
      ALLIUM(FlowerBlock.Group.RED, 2, "allium"),
      HOUSTONIA(FlowerBlock.Group.RED, 3, "houstonia"),
      RED_TULIP(FlowerBlock.Group.RED, 4, "red_tulip", "tulipRed"),
      ORANGE_TULIP(FlowerBlock.Group.RED, 5, "orange_tulip", "tulipOrange"),
      WHITE_TULIP(FlowerBlock.Group.RED, 6, "white_tulip", "tulipWhite"),
      PINK_TULIP(FlowerBlock.Group.RED, 7, "pink_tulip", "tulipPink"),
      OXEY_DAISY(FlowerBlock.Group.RED, 8, "oxeye_daisy", "oxeyeDaisy");

      private static final FlowerBlock.Type[][] PER_GROUP = new FlowerBlock.Type[FlowerBlock.Group.values().length][];
      private final FlowerBlock.Group group;
      private final int index;
      private final String id;
      private final String name;

      private Type(FlowerBlock.Group group, int index, String id) {
         this(group, index, id, id);
      }

      private Type(FlowerBlock.Group group, int index, String id, String name) {
         this.group = group;
         this.index = index;
         this.id = id;
         this.name = name;
      }

      public FlowerBlock.Group getGroup() {
         return this.group;
      }

      public int getIndex() {
         return this.index;
      }

      public static FlowerBlock.Type byIndex(FlowerBlock.Group group, int index) {
         FlowerBlock.Type[] var2 = PER_GROUP[group.ordinal()];
         if (index < 0 || index >= var2.length) {
            index = 0;
         }

         return var2[index];
      }

      @Environment(EnvType.CLIENT)
      public static FlowerBlock.Type[] ofGroup(FlowerBlock.Group group) {
         return PER_GROUP[group.ordinal()];
      }

      @Override
      public String toString() {
         return this.id;
      }

      @Override
      public String getStringRepresentation() {
         return this.id;
      }

      public String getName() {
         return this.name;
      }

      static {
         for(final FlowerBlock.Group var3 : FlowerBlock.Group.values()) {
            Collection var4 = Collections2.filter(Lists.newArrayList(values()), new Predicate() {
               public boolean apply(FlowerBlock.Type c_71umdcnft) {
                  return c_71umdcnft.getGroup() == var3;
               }
            });
            PER_GROUP[var3.ordinal()] = var4.toArray(new FlowerBlock.Type[var4.size()]);
         }
      }
   }
}
