package net.minecraft.entity.living.mob.passive.animal;

import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.living.mob.passive.PassiveEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.particle.ParticleType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class MooshroomEntity extends CowEntity {
   public MooshroomEntity(World c_54ruxjwzt) {
      super(c_54ruxjwzt);
      this.setDimensions(0.9F, 1.3F);
      this.f_92ebgqdsn = Blocks.MYCELIUM;
   }

   @Override
   public boolean canInteract(PlayerEntity player) {
      ItemStack var2 = player.inventory.getMainHandStack();
      if (var2 != null && var2.getItem() == Items.BOWL && this.getBreedingAge() >= 0) {
         if (var2.size == 1) {
            player.inventory.setStack(player.inventory.selectedSlot, new ItemStack(Items.MUSHROOM_STEW));
            return true;
         }

         if (player.inventory.insertStack(new ItemStack(Items.MUSHROOM_STEW)) && !player.abilities.creativeMode) {
            player.inventory.removeStack(player.inventory.selectedSlot, 1);
            return true;
         }
      }

      if (var2 != null && var2.getItem() == Items.SHEARS && this.getBreedingAge() >= 0) {
         this.remove();
         this.world.addParticle(ParticleType.EXPLOSION_LARGE, this.x, this.y + (double)(this.height / 2.0F), this.z, 0.0, 0.0, 0.0);
         if (!this.world.isClient) {
            CowEntity var3 = new CowEntity(this.world);
            var3.refreshPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
            var3.setHealth(this.getHealth());
            var3.bodyYaw = this.bodyYaw;
            if (this.hasCustomName()) {
               var3.setCustomName(this.getCustomName());
            }

            this.world.addEntity(var3);

            for(int var4 = 0; var4 < 5; ++var4) {
               this.world.addEntity(new ItemEntity(this.world, this.x, this.y + (double)this.height, this.z, new ItemStack(Blocks.RED_MUSHROOM)));
            }

            var2.damageAndBreak(1, player);
            this.playSound("mob.sheep.shear", 1.0F, 1.0F);
         }

         return true;
      } else {
         return super.canInteract(player);
      }
   }

   public MooshroomEntity makeChild(PassiveEntity c_19nmglwmx) {
      return new MooshroomEntity(this.world);
   }
}
