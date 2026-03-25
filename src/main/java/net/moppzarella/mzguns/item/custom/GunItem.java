package net.moppzarella.mzguns.item.custom;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.moppzarella.mzguns.MZGuns;
import net.moppzarella.mzguns.component.ModDataComponents;
import net.moppzarella.mzguns.entity.custom.HitscanPelletEntity;

public class GunItem extends Item {
    public final float baseDamage = 8F;

    public final float bloomRadius = 0.7F; //I don't know if this is measured in degrees or radians
    public final double bloomDecayRate = 0.04D; //The amount to subtract from current_bloom (if it's above 0) each tick
    public final int firingRate = 10; //The amount of ticks between shots
    public final int clipSize = 6;
    public final int clipReloadSpeed = 1;
    public final int clipReloadThreshold = 23;
    public final int clipReloadAmount = 6;

    public GunItem(Properties properties) {
        super(properties);
    }

    public void primaryFire(Level level, Player player, InteractionHand usedHand) {
        ItemStack stackInHand = player.getItemInHand(usedHand);


        if(!level.isClientSide && usedHand == InteractionHand.MAIN_HAND) {
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
            this.setCurrentClip(stackInHand, this.getCurrentClip(stackInHand) - 1);
            this.setReloadPercentile(stackInHand, 0);

        }
    }




    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide) {

            double current_bloom = this.getCurrentBloom(stack);
            int firing_cooldown = this.getFiringCooldown(stack);

            if (this.getCurrentBloom(stack) > 0D && this.getFiringCooldown(stack) <= 0) {
                this.setCurrentBloom(stack, Math.clamp((current_bloom - bloomDecayRate), 0, 1));
            }
            if (firing_cooldown > 0) {
                this.setFiringCooldown(stack, firing_cooldown - 1);
            }

            if ((getCurrentClip(stack) < clipSize) && this.getFiringCooldown(stack) <= 0D && !this.isOnFiringCooldown(stack)) {
                int reloadPercentile = getReloadPercentile(stack);
                if (reloadPercentile >= clipReloadThreshold) {
                    //HOW DO I MAKE IT NOT RESTART THE COOLDOWN ANIMATION
                    setReloadPercentile(stack, 0);
                    int currentClip = getCurrentClip(stack);
                    int clipToAdd = Math.clamp(currentClip + clipReloadAmount, 0, clipSize);
                    setCurrentClip(stack, clipToAdd);
                } else {setReloadPercentile(stack, reloadPercentile + 1);}
            }

        } else {
            LocalPlayer local_player = (LocalPlayer) entity;

            double current_bloom = this.getCurrentBloom(stack);
            int firing_cooldown = this.getFiringCooldown(stack);

            if (current_bloom > 0 || firing_cooldown > 0) {
                //DON'T DO THE USE ANIMATION LIKE SERIOUSLY HOW DO I STOP IT FROM DOING THE USE ANIMATION

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

    public void setReloadPercentile(ItemStack stack, int newValue) {
        stack.set(ModDataComponents.RELOAD_PERCENTILE, newValue);
    }

    public int getReloadPercentile(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.RELOAD_PERCENTILE, 0);
    }




}

