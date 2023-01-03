package net.minecraft.item;

import com.google.common.collect.Multimap;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.attribute.AttributeModifier;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class ToolItem extends Item {
   private Set effectiveBlocks;
   protected float miningSpeed = 4.0F;
   private float attackDamage;
   protected Item.ToolMaterial material;

   protected ToolItem(float baseAttackDamage, Item.ToolMaterial material, Set effectiveBlocks) {
      this.material = material;
      this.effectiveBlocks = effectiveBlocks;
      this.maxStackSize = 1;
      this.setMaxDamage(material.getMaxDurability());
      this.miningSpeed = material.getMiningSpeed();
      this.attackDamage = baseAttackDamage + material.getAttackDamage();
      this.setItemGroup(ItemGroup.TOOLS);
   }

   @Override
   public float getMiningSpeed(ItemStack stack, Block block) {
      return this.effectiveBlocks.contains(block) ? this.miningSpeed : 1.0F;
   }

   @Override
   public boolean attackEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      stack.damageAndBreak(2, attacker);
      return true;
   }

   @Override
   public boolean mineBlock(ItemStack stack, World world, Block block, BlockPos pos, LivingEntity entity) {
      if ((double)block.getMiningSpeed(world, pos) != 0.0) {
         stack.damageAndBreak(1, entity);
      }

      return true;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean isHandheld() {
      return true;
   }

   public Item.ToolMaterial getMaterial() {
      return this.material;
   }

   @Override
   public int getEnchantability() {
      return this.material.getEnchantability();
   }

   public String getMaterialAsString() {
      return this.material.toString();
   }

   @Override
   public boolean isReparable(ItemStack stack, ItemStack ingredient) {
      return this.material.getRepairIngredient() == ingredient.getItem() ? true : super.isReparable(stack, ingredient);
   }

   @Override
   public Multimap getDefaultAttributeModifiers() {
      Multimap var1 = super.getDefaultAttributeModifiers();
      var1.put(EntityAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER_UUID, "Tool modifier", (double)this.attackDamage, 0));
      return var1;
   }
}
