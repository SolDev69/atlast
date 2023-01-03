package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Blocks;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.block.state.BlockState;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.locale.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class MusicDiscItem extends Item {
   private static final Map RECORD_TYPES = Maps.newHashMap();
   public final String recordType;

   protected MusicDiscItem(String recordType) {
      this.recordType = recordType;
      this.maxStackSize = 1;
      this.setItemGroup(ItemGroup.MISC);
      RECORD_TYPES.put("records." + recordType, this);
   }

   @Override
   public boolean use(ItemStack stack, PlayerEntity player, World world, BlockPos pos, Direction face, float dx, float dy, float dz) {
      BlockState var9 = world.getBlockState(pos);
      if (var9.getBlock() != Blocks.JUKEBOX || var9.get(JukeboxBlock.HAS_RECORD)) {
         return false;
      } else if (world.isClient) {
         return true;
      } else {
         ((JukeboxBlock)Blocks.JUKEBOX).setRecord(world, pos, var9, stack);
         world.doEvent(null, 1005, pos, Item.getRawId(this));
         --stack.size;
         return true;
      }
   }

   @Environment(EnvType.CLIENT)
   @Override
   public void addHoverText(ItemStack stack, PlayerEntity player, List tooltip, boolean advanced) {
      tooltip.add(this.getDescription());
   }

   @Environment(EnvType.CLIENT)
   public String getDescription() {
      return I18n.translate("item.record." + this.recordType + ".desc");
   }

   @Override
   public Rarity getRarity(ItemStack stack) {
      return Rarity.RARE;
   }

   @Environment(EnvType.CLIENT)
   public static MusicDiscItem getByName(String name) {
      return (MusicDiscItem)RECORD_TYPES.get(name);
   }
}
