package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.NoteBlockBlockEntity;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class NoteBlock extends BlockWithBlockEntity {
   private static final List INSTRUMENTS = Lists.newArrayList(new String[]{"harp", "bd", "snare", "hat", "bassattack"});

   public NoteBlock() {
      super(Material.WOOD);
      this.setItemGroup(ItemGroup.REDSTONE);
   }

   @Override
   public void update(World world, BlockPos pos, BlockState state, Block neighborBlock) {
      boolean var5 = world.isReceivingPower(pos);
      BlockEntity var6 = world.getBlockEntity(pos);
      if (var6 instanceof NoteBlockBlockEntity) {
         NoteBlockBlockEntity var7 = (NoteBlockBlockEntity)var6;
         if (var7.powered != var5) {
            if (var5) {
               var7.playNote(world, pos);
            }

            var7.powered = var5;
         }
      }
   }

   @Override
   public boolean use(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction face, float dx, float dy, float dz) {
      if (world.isClient) {
         return true;
      } else {
         BlockEntity var9 = world.getBlockEntity(pos);
         if (var9 instanceof NoteBlockBlockEntity) {
            NoteBlockBlockEntity var10 = (NoteBlockBlockEntity)var9;
            var10.tunePitch();
            var10.playNote(world, pos);
         }

         return true;
      }
   }

   @Override
   public void startMining(World world, BlockPos pos, PlayerEntity player) {
      if (!world.isClient) {
         BlockEntity var4 = world.getBlockEntity(pos);
         if (var4 instanceof NoteBlockBlockEntity) {
            ((NoteBlockBlockEntity)var4).playNote(world, pos);
         }
      }
   }

   @Override
   public BlockEntity createBlockEntity(World world, int metadata) {
      return new NoteBlockBlockEntity();
   }

   private String getInstrument(int index) {
      if (index < 0 || index >= INSTRUMENTS.size()) {
         index = 0;
      }

      return (String)INSTRUMENTS.get(index);
   }

   @Override
   public boolean doEvent(World world, BlockPos pos, BlockState state, int type, int data) {
      float var6 = (float)Math.pow(2.0, (double)(data - 12) / 12.0);
      world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, "note." + this.getInstrument(type), 3.0F, var6);
      world.addParticle(ParticleType.NOTE, (double)pos.getX() + 0.5, (double)pos.getY() + 1.2, (double)pos.getZ() + 0.5, (double)data / 24.0, 0.0, 0.0);
      return true;
   }

   @Override
   public int getRenderType() {
      return 3;
   }
}
