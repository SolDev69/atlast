package net.minecraft.block.entity;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.living.effect.StatusEffect;
import net.minecraft.entity.living.effect.StatusEffectInstance;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.menu.BeaconMenu;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.stat.achievement.Achievements;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class BeaconBlockEntity extends InventoryBlockEntity implements Tickable, Inventory {
   public static final StatusEffect[][] EFFECTS = new StatusEffect[][]{
      {StatusEffect.SPEED, StatusEffect.HASTE}, {StatusEffect.RESISTANCE, StatusEffect.JUMP_BOOST}, {StatusEffect.STRENGTH}, {StatusEffect.REGENERATION}
   };
   @Environment(EnvType.CLIENT)
   private long lastBeamRenderTime;
   @Environment(EnvType.CLIENT)
   private float beamAngle;
   private boolean active;
   private int levels = -1;
   private int primaryEffect;
   private int secondaryEffect;
   private ItemStack inventory;
   private String customName;

   @Override
   public void tick() {
      if (this.world.getTime() % 80L == 0L) {
         this.updateLevels();
         this.spreadEffects();
      }
   }

   private void spreadEffects() {
      if (this.active && this.levels > 0 && !this.world.isClient && this.primaryEffect > 0) {
         double var1 = (double)(this.levels * 10 + 10);
         byte var3 = 0;
         if (this.levels >= 4 && this.primaryEffect == this.secondaryEffect) {
            var3 = 1;
         }

         int var4 = this.pos.getX();
         int var5 = this.pos.getY();
         int var6 = this.pos.getZ();
         Box var7 = new Box((double)var4, (double)var5, (double)var6, (double)(var4 + 1), (double)(var5 + 1), (double)(var6 + 1))
            .expand(var1, var1, var1)
            .grow(0.0, (double)this.world.getHeight(), 0.0);
         List var8 = this.world.getEntities(PlayerEntity.class, var7);

         for(PlayerEntity var10 : var8) {
            var10.addStatusEffect(new StatusEffectInstance(this.primaryEffect, 180, var3, true, true));
         }

         if (this.levels >= 4 && this.primaryEffect != this.secondaryEffect && this.secondaryEffect > 0) {
            for(PlayerEntity var12 : var8) {
               var12.addStatusEffect(new StatusEffectInstance(this.secondaryEffect, 180, 0, true, true));
            }
         }
      }
   }

   private void updateLevels() {
      int var1 = this.levels;
      int var2 = this.pos.getX();
      int var3 = this.pos.getY();
      int var4 = this.pos.getZ();
      if (!this.world.hasSkyAccess(this.pos.up())) {
         this.active = false;
         this.levels = 0;
      } else {
         this.active = true;
         this.levels = 0;

         for(int var5 = 1; var5 <= 4; this.levels = var5++) {
            int var6 = var3 - var5;
            if (var6 < 0) {
               break;
            }

            boolean var7 = true;

            for(int var8 = var2 - var5; var8 <= var2 + var5 && var7; ++var8) {
               for(int var9 = var4 - var5; var9 <= var4 + var5; ++var9) {
                  Block var10 = this.world.getBlockState(new BlockPos(var8, var6, var9)).getBlock();
                  if (var10 != Blocks.EMERALD_BLOCK && var10 != Blocks.GOLD_BLOCK && var10 != Blocks.DIAMOND_BLOCK && var10 != Blocks.IRON_BLOCK) {
                     var7 = false;
                     break;
                  }
               }
            }

            if (!var7) {
               break;
            }
         }

         if (this.levels == 0) {
            this.active = false;
         }
      }

      if (!this.world.isClient && this.levels == 4 && var1 < this.levels) {
         for(PlayerEntity var12 : this.world
            .getEntities(
               PlayerEntity.class, new Box((double)var2, (double)var3, (double)var4, (double)var2, (double)(var3 - 4), (double)var4).expand(10.0, 5.0, 10.0)
            )) {
            var12.incrementStat(Achievements.ACTIVATE_MAX_BEACON);
         }
      }
   }

   @Environment(EnvType.CLIENT)
   public float getBeamAngle() {
      if (!this.active) {
         return 0.0F;
      } else {
         int var1 = (int)(this.world.getTime() - this.lastBeamRenderTime);
         this.lastBeamRenderTime = this.world.getTime();
         if (var1 > 1) {
            this.beamAngle -= (float)var1 / 40.0F;
            if (this.beamAngle < 0.0F) {
               this.beamAngle = 0.0F;
            }
         }

         this.beamAngle += 0.025F;
         if (this.beamAngle > 1.0F) {
            this.beamAngle = 1.0F;
         }

         return this.beamAngle;
      }
   }

   @Override
   public Packet createUpdatePacket() {
      NbtCompound var1 = new NbtCompound();
      this.writeNbt(var1);
      return new BlockEntityUpdateS2CPacket(this.pos, 3, var1);
   }

   @Environment(EnvType.CLIENT)
   @Override
   public double getSquaredViewDistance() {
      return 65536.0;
   }

   @Override
   public void readNbt(NbtCompound nbt) {
      super.readNbt(nbt);
      this.primaryEffect = nbt.getInt("Primary");
      this.secondaryEffect = nbt.getInt("Secondary");
      this.levels = nbt.getInt("Levels");
   }

   @Override
   public void writeNbt(NbtCompound nbt) {
      super.writeNbt(nbt);
      nbt.putInt("Primary", this.primaryEffect);
      nbt.putInt("Secondary", this.secondaryEffect);
      nbt.putInt("Levels", this.levels);
   }

   @Override
   public int getSize() {
      return 1;
   }

   @Override
   public ItemStack getStack(int slot) {
      return slot == 0 ? this.inventory : null;
   }

   @Override
   public ItemStack removeStack(int slot, int amount) {
      if (slot != 0 || this.inventory == null) {
         return null;
      } else if (amount >= this.inventory.size) {
         ItemStack var3 = this.inventory;
         this.inventory = null;
         return var3;
      } else {
         this.inventory.size -= amount;
         return new ItemStack(this.inventory.getItem(), amount, this.inventory.getMetadata());
      }
   }

   @Override
   public ItemStack removeStackQuietly(int slot) {
      if (slot == 0 && this.inventory != null) {
         ItemStack var2 = this.inventory;
         this.inventory = null;
         return var2;
      } else {
         return null;
      }
   }

   @Override
   public void setStack(int slot, ItemStack stack) {
      if (slot == 0) {
         this.inventory = stack;
      }
   }

   @Override
   public String getName() {
      return this.hasCustomName() ? this.customName : "container.beacon";
   }

   @Override
   public boolean hasCustomName() {
      return this.customName != null && this.customName.length() > 0;
   }

   public void setCustomName(String name) {
      this.customName = name;
   }

   @Override
   public int getMaxStackSize() {
      return 1;
   }

   @Override
   public boolean isValid(PlayerEntity player) {
      if (this.world.getBlockEntity(this.pos) != this) {
         return false;
      } else {
         return !(player.getSquaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) > 64.0);
      }
   }

   @Override
   public void onOpen(PlayerEntity player) {
   }

   @Override
   public void onClose(PlayerEntity player) {
   }

   @Override
   public boolean canSetStack(int slot, ItemStack stack) {
      return stack.getItem() == Items.EMERALD || stack.getItem() == Items.DIAMOND || stack.getItem() == Items.GOLD_INGOT || stack.getItem() == Items.IRON_INGOT;
   }

   @Override
   public String getMenuType() {
      return "minecraft:beacon";
   }

   @Override
   public InventoryMenu createMenu(PlayerInventory playerInventory, PlayerEntity player) {
      return new BeaconMenu(playerInventory, this);
   }

   @Override
   public int getData(int id) {
      switch(id) {
         case 0:
            return this.levels;
         case 1:
            return this.primaryEffect;
         case 2:
            return this.secondaryEffect;
         default:
            return 0;
      }
   }

   @Override
   public void setData(int id, int value) {
      switch(id) {
         case 0:
            this.levels = value;
            break;
         case 1:
            this.primaryEffect = value;
            break;
         case 2:
            this.secondaryEffect = value;
      }
   }

   @Override
   public int getDataCount() {
      return 3;
   }

   @Override
   public void clear() {
      this.inventory = null;
   }
}
