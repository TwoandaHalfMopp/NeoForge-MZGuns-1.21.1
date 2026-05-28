package net.moppzarella.mzguns.item.custom;


import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.moppzarella.mzguns.Config;
import net.moppzarella.mzguns.component.ModDataComponents;
import net.moppzarella.mzguns.entity.custom.HitscanPelletEntity;

public class BetaShotgunItem extends GunItem {
    public final float baseDamage = 0.76F;


    public final int firingRate = 12; //The amount of ticks between shots. Must be divisible by 4
    public final int clipSize = 6;
    public final int clipReloadSpeed = 23;
    public final int clipReloadAmount = 6;

    public BetaShotgunItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        return InteractionResultHolder.fail(player.getItemInHand(usedHand));
    }
    @Override
    public void primaryFire(Level level, Player player, InteractionHand usedHand) {
        ItemStack stackInHand = player.getItemInHand(usedHand);
        if(!level.isClientSide) {
            if (isOnFiringCooldown(stackInHand) || getCurrentClip(stackInHand) <= 0) return;
            level.playSound(
                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.BREEZE_WIND_CHARGE_BURST,
                    SoundSource.NEUTRAL,
                    0.25F,
                    0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

            spawnPellet(level, player, usedHand, 0, 0, this.baseDamage);
            spawnPellet(level, player, usedHand, 3, 0, this.baseDamage);
            spawnPellet(level, player, usedHand, -3, 0, this.baseDamage);
            spawnPellet(level, player, usedHand, 0, 3, this.baseDamage);
            spawnPellet(level, player, usedHand, 0, -3, this.baseDamage);

            spawnPellet(level, player, usedHand, 2.25, 2.25, this.baseDamage);
            spawnPellet(level, player, usedHand, 2.25, -2.25, this.baseDamage);
            spawnPellet(level, player, usedHand, -2.25, 2.25, this.baseDamage);
            spawnPellet(level, player, usedHand, -2.25, -2.25, this.baseDamage);

            this.setFiringCooldown(stackInHand, firingRate);
            if (!(Config.INFINITE_CLIP_IN_CREATIVE.getAsBoolean() && player.isCreative())) this.setCurrentClip(stackInHand, this.getCurrentClip(stackInHand) - 1);
            this.setReloadProgress(stackInHand, 0);

        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {

        if (oldStack.getItem() instanceof BetaShotgunItem gunItem && oldStack.getItem().equals(newStack.getItem())) {
            int firing_cooldown = gunItem.getFiringCooldown(oldStack); //if this is true, that would imply it was just fired.
            return firing_cooldown == firingRate;
        }
        return !oldStack.equals(newStack);
    }



    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide) {

            this.shouldCauseReequipAnimation(stack, stack, false);

            int firing_cooldown = this.getFiringCooldown(stack);
            if (firing_cooldown > 0) {
                this.setFiringCooldown(stack, firing_cooldown - 1);
            }

            if ((getCurrentClip(stack) < clipSize) && this.getFiringCooldown(stack) <= 0D && !this.isOnFiringCooldown(stack) && (((Player) entity).getMainHandItem() == stack || ((Player) entity).getOffhandItem() == stack)) {
                int reloadProgress = getReloadProgress(stack);
                if (reloadProgress >= clipReloadSpeed) {
                    setReloadProgress(stack, 0);
                    int currentClip = getCurrentClip(stack);
                    int clipToAdd = Math.clamp(currentClip + clipReloadAmount, 0, clipSize);
                    setCurrentClip(stack, clipToAdd);
                } else {
                    setReloadProgress(stack, reloadProgress + 1);}
            } else if (getReloadProgress(stack) != 0 && !(((Player) entity).getMainHandItem() == stack || ((Player) entity).getOffhandItem() == stack)) {
                setReloadProgress(stack, 0);
            }

        }
    }
}

