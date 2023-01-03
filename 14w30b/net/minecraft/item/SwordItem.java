package net.minecraft.item;

import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.entity.living.attribute.AttributeModifier;
import net.minecraft.entity.living.attribute.EntityAttributes;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.group.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.ornithemc.api.EnvType;
import net.ornithemc.api.Environment;

public class SwordItem extends Item {
   private float attackMultiplier;
   private final Item.ToolMaterial material;

   public SwordItem(Item.ToolMaterial material) {
      this.material = material;
      this.maxStackSize = 1;
      this.setMaxDamage(material.getMaxDurability());
      this.setItemGroup(ItemGroup.COMBAT);
      this.attackMultiplier = 4.0F + material.getAttackDamage();
   }

   public float getAttackDamage() {
      return this.material.getAttackDamage();
   }

   @Override
   public float getMiningSpeed(ItemStack stack, Block block) {
      if (block == Blocks.WEB) {
         return 15.0F;
      } else {
         Material var3 = block.getMaterial();
         return var3 != Material.PLANT && var3 != Material.REPLACEABLE_PLANT && var3 != Material.CORAL && var3 != Material.LEAVES && var3 != Material.PUMPKIN
            ? 1.0F
            : 1.5F;
      }
   }

   @Override
   public boolean attackEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      stack.damageAndBreak(1, attacker);
      return true;
   }

   @Override
   public boolean mineBlock(ItemStack stack, World world, Block block, BlockPos pos, LivingEntity entity) {
      if ((double)block.getMiningSpeed(world, pos) != 0.0) {
         stack.damageAndBreak(2, entity);
      }

      return true;
   }

   @Environment(EnvType.CLIENT)
   @Override
   public boolean isHandheld() {
      return true;
   }

   @Override
   public UseAction getUseAction(ItemStack stack) {
      return UseAction.BLOCK;
   }

   @Override
   public int getUseDuration(ItemStack stack) {
      return 72000;
   }

   @Override
   public ItemStack startUsing(ItemStack stack, World world, PlayerEntity player) {
      player.setUseItem(stack, this.getUseDuration(stack));
      return stack;
   }

   @Override
   public boolean canEffectivelyMine(Block block) {
      return block == Blocks.WEB;
   }

   @Override
   public int getEnchantability() {
      return this.material.getEnchantability();
   }

   public String getToolMaterial() {
      return this.material.toString();
   }

   @Override
   public boolean isReparable(ItemStack stack, ItemStack ingredient) {
      return this.material.getRepairIngredient() == ingredient.getItem() ? true : super.isReparable(stack, ingredient);
   }

   @Override
   public Multimap getDefaultAttributeModifiers() {
      Multimap var1 = super.getDefaultAttributeModifiers();
      var1.put(
         EntityAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER_UUID, "Weapon modifier", (double)this.attackMultiplier, 0)
      );
      return var1;
   }
}
