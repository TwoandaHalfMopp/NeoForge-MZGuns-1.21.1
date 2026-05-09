package net.moppzarella.mzguns.item.custom;


import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.moppzarella.mzguns.Config;
import net.moppzarella.mzguns.MZGuns;
import net.moppzarella.mzguns.component.ModDataComponents;
import net.moppzarella.mzguns.entity.custom.HitscanPelletEntity;

public class GunItem extends Item {
    public final float baseDamage = 8F;

    public final float bloomRadius = 0.7F; //I don't know if this is measured in degrees or radians
    public final double bloomDecayRate = 0.04D; //The amount to subtract from current_bloom (if it's above 0) each tick
    public final int firingRate = 12; //The amount of ticks between shots. Must be divisible by 4
    public final int clipSize = 6;
    public final int clipReloadSpeed = 23;
    public final int clipReloadAmount = 6;

    public GunItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
            if (!level.isClientSide && usedHand == InteractionHand.OFF_HAND){
                primaryFire(level, player, usedHand);
                return InteractionResultHolder.pass(player.getItemInHand(usedHand));
            }
            return InteractionResultHolder.fail(player.getItemInHand(usedHand));

    }
    public void primaryFire(Level level, Player player, InteractionHand usedHand) {
        ItemStack stackInHand = player.getItemInHand(usedHand);
        if(!level.isClientSide) {
            if (isOnFiringCooldown(stackInHand) || getCurrentClip(stackInHand) <= 0) return;
            double current_bloom = this.getCurrentBloom(stackInHand);
            level.playSound(
                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.BREEZE_WIND_CHARGE_BURST,
                    SoundSource.NEUTRAL,
                    0.25F,
                    0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

            float randPitch = (float) ((Math.random() - 0.5) * 2 * (current_bloom * bloomRadius));
            float randYaw = (float) ((Math.random() - 0.5) * 2 * (current_bloom * bloomRadius));

            spawnPellet(level, player, usedHand, randYaw, randPitch);

            this.setFiringCooldown(stackInHand, firingRate);
            this.setCurrentBloom(stackInHand, Math.clamp((current_bloom + 1.0F), 0F, 1F));
            if (!(Config.INFINITE_CLIP_IN_CREATIVE.getAsBoolean() && player.isCreative())) this.setCurrentClip(stackInHand, this.getCurrentClip(stackInHand) - 1);
            this.setReloadProgress(stackInHand, 0);

        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {

        if (oldStack.getItem() instanceof GunItem gunItem && oldStack.getItem().equals(newStack.getItem())) {
            int firing_cooldown = gunItem.getFiringCooldown(oldStack); //if this is true, that would imply it was just fired.
            return firing_cooldown == firingRate;
        }
        return !oldStack.equals(newStack);
    }




    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide) {

            this.shouldCauseReequipAnimation(stack, stack, false);

            double current_bloom = this.getCurrentBloom(stack);
            int firing_cooldown = this.getFiringCooldown(stack);

            if (this.getCurrentBloom(stack) > 0D && this.getFiringCooldown(stack) <= 0) {
                this.setCurrentBloom(stack, Math.clamp((current_bloom - bloomDecayRate), 0, 1));
            }
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

    public void spawnPellet(Level level, Player player, InteractionHand usedHand, float yawOffset, float pitchOffset) {
        HitscanPelletEntity temp_bullet = new HitscanPelletEntity(level, player, this.baseDamage);
        temp_bullet.setPos(player.getX(), player.getEyeY() - 0.25, player.getZ());

        temp_bullet.setYRot(player.getYRot() + yawOffset);
        temp_bullet.setXRot(player.getXRot() + pitchOffset);

        level.addFreshEntity(temp_bullet);
    }


    public void setFiringCooldown(ItemStack stack, int newValue) {
        stack.set(ModDataComponents.CURRENT_FIRING_COOLDOWN, newValue);
    }

    public int getFiringCooldown(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.CURRENT_FIRING_COOLDOWN, 0);
    }

    public void setCurrentBloom(ItemStack stack, double newValue) {
        stack.set(ModDataComponents.CURRENT_BLOOM, newValue);
    }

    public double getCurrentBloom(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.CURRENT_BLOOM, 0D);
    }

    public boolean isOnFiringCooldown(ItemStack stack) {
        return getFiringCooldown(stack) > 0;
    }

    public void setCurrentClip(ItemStack stack, int newValue) {
        stack.set(ModDataComponents.CURRENT_CLIP, newValue);
    }

    public int getCurrentClip(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.CURRENT_CLIP, 0);
    }

    public void setReloadProgress(ItemStack stack, int newValue) {
        stack.set(ModDataComponents.RELOAD_PERCENTILE, newValue);
    }

    public int getReloadProgress(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.RELOAD_PERCENTILE, 0);
    }




}

