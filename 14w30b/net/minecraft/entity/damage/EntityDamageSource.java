package net.minecraft.entity.damage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.locale.I18n;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class EntityDamageSource extends DamageSource {
   protected Entity entity;

   public EntityDamageSource(String name, Entity entity) {
      super(name);
      this.entity = entity;
   }

   @Override
   public Entity getAttacker() {
      return this.entity;
   }

   @Override
   public Text getDeathMessage(LivingEntity livingEntity) {
      ItemStack var2 = this.entity instanceof LivingEntity ? ((LivingEntity)this.entity).getStackInHand() : null;
      String var3 = "death.attack." + this.name;
      String var4 = var3 + ".item";
      return var2 != null && var2.hasCustomHoverName() && I18n.hasTranslation(var4)
         ? new TranslatableText(var4, livingEntity.getDisplayName(), this.entity.getDisplayName(), var2.getDisplayName())
         : new TranslatableText(var3, livingEntity.getDisplayName(), this.entity.getDisplayName());
   }

   @Override
   public boolean isScaledWithDifficulty() {
      return this.entity != null && this.entity instanceof LivingEntity && !(this.entity instanceof PlayerEntity);
   }
}
