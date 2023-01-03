package net.minecraft.entity.living.mob.passive.animal.tamable;

import net.minecraft.entity.Entity;

public interface Tameable {
   String getOwnerName();

   Entity getOwner();
}
