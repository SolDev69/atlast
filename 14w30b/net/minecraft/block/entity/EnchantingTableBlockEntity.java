package net.minecraft.block.entity;

import java.util.Random;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.menu.EnchantingTableMenu;
import net.minecraft.inventory.menu.InventoryMenu;
import net.minecraft.inventory.menu.MenuProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.MathHelper;

public class EnchantingTableBlockEntity extends BlockEntity implements Tickable, MenuProvider {
   public int ticks;
   public float pageAngle;
   public float lastPageAngle;
   public float flip;
   public float lastFlip;
   public float pageTurningSpeed;
   public float lastPageTurningSpeed;
   public float pageRotation;
   public float lastPageRotation;
   public float rotation;
   private static Random RANDOM = new Random();
   private String customName;

   @Override
   public void writeNbt(NbtCompound nbt) {
      super.writeNbt(nbt);
      if (this.hasCustomName()) {
         nbt.putString("CustomName", this.customName);
      }
   }

   @Override
   public void readNbt(NbtCompound nbt) {
      super.readNbt(nbt);
      if (nbt.isType("CustomName", 8)) {
         this.customName = nbt.getString("CustomName");
      }
   }

   @Override
   public void tick() {
      this.lastPageTurningSpeed = this.pageTurningSpeed;
      this.lastPageRotation = this.pageRotation;
      PlayerEntity var1 = this.world
         .getClosestPlayer((double)((float)this.pos.getX() + 0.5F), (double)((float)this.pos.getY() + 0.5F), (double)((float)this.pos.getZ() + 0.5F), 3.0);
      if (var1 != null) {
         double var2 = var1.x - (double)((float)this.pos.getX() + 0.5F);
         double var4 = var1.z - (double)((float)this.pos.getZ() + 0.5F);
         this.rotation = (float)Math.atan2(var4, var2);
         this.pageTurningSpeed += 0.1F;
         if (this.pageTurningSpeed < 0.5F || RANDOM.nextInt(40) == 0) {
            float var6 = this.flip;

            do {
               this.flip += (float)(RANDOM.nextInt(4) - RANDOM.nextInt(4));
            } while(var6 == this.flip);
         }
      } else {
         this.rotation += 0.02F;
         this.pageTurningSpeed -= 0.1F;
      }

      while(this.pageRotation >= (float) Math.PI) {
         this.pageRotation -= (float) (Math.PI * 2);
      }

      while(this.pageRotation < (float) -Math.PI) {
         this.pageRotation += (float) (Math.PI * 2);
      }

      while(this.rotation >= (float) Math.PI) {
         this.rotation -= (float) (Math.PI * 2);
      }

      while(this.rotation < (float) -Math.PI) {
         this.rotation += (float) (Math.PI * 2);
      }

      float var7 = this.rotation - this.pageRotation;

      while(var7 >= (float) Math.PI) {
         var7 -= (float) (Math.PI * 2);
      }

      while(var7 < (float) -Math.PI) {
         var7 += (float) (Math.PI * 2);
      }

      this.pageRotation += var7 * 0.4F;
      this.pageTurningSpeed = MathHelper.clamp(this.pageTurningSpeed, 0.0F, 1.0F);
      ++this.ticks;
      this.lastPageAngle = this.pageAngle;
      float var3 = (this.flip - this.pageAngle) * 0.4F;
      float var9 = 0.2F;
      var3 = MathHelper.clamp(var3, -var9, var9);
      this.lastFlip += (var3 - this.lastFlip) * 0.9F;
      this.pageAngle += this.lastFlip;
   }

   @Override
   public String getName() {
      return this.hasCustomName() ? this.customName : "container.enchant";
   }

   @Override
   public boolean hasCustomName() {
      return this.customName != null && this.customName.length() > 0;
   }

   public void setCustomName(String name) {
      this.customName = name;
   }

   @Override
   public Text getDisplayName() {
      return (Text)(this.hasCustomName() ? new LiteralText(this.getName()) : new TranslatableText(this.getName()));
   }

   @Override
   public InventoryMenu createMenu(PlayerInventory playerInventory, PlayerEntity player) {
      return new EnchantingTableMenu(playerInventory, this.world, this.pos);
   }

   @Override
   public String getMenuType() {
      return "minecraft:enchanting_table";
   }
}
