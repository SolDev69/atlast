package net.minecraft.entity.damage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.locale.I18n;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ProjectileDamageSource extends EntityDamageSource {
   private Entity projectile;

   public ProjectileDamageSource(String name, Entity entity, Entity projectile) {
      super(name, entity);
      this.projectile = projectile;
   }

   @Override
   public Entity getSource() {
      return this.entity;
   }

   @Override
   public Entity getAttacker() {
      return this.projectile;
   }

   @Override
   public Text getDeathMessage(LivingEntity livingEntity) {
      Text var2 = this.projectile == null ? this.entity.getDisplayName() : this.projectile.getDisplayName();
      ItemStack var3 = this.projectile instanceof LivingEntity ? ((LivingEntity)this.projectile).getStackInHand() : null;
      String var4 = "death.attack." + this.name;
      String var5 = var4 + ".item";
      return var3 != null && var3.hasCustomHoverName() && I18n.hasTranslation(var5)
         ? new TranslatableText(var5, livingEntity.getDisplayName(), var2, var3.getDisplayName())
         : new TranslatableText(var4, livingEntity.getDisplayName(), var2);
   }
}
