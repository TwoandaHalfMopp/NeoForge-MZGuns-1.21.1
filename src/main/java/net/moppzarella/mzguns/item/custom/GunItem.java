package net.moppzarella.mzguns.item.custom;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.moppzarella.mzguns.MZGuns;
import net.moppzarella.mzguns.component.ModDataComponents;
import net.moppzarella.mzguns.entity.custom.HitscanPelletEntity;

public class GunItem extends Item {
    public float baseDamage = 8F;

    public float bloomRadius = 0.7F; //I don't know if this is measured in degrees or radians
    public double bloomDecayRate = 0.04D; //The amount to subtract from current_bloom (if it's above 0) each tick
    public int firingRate = 10; //The amount of ticks between shots

    public GunItem(Properties properties) {
        super(properties);
    }

    public void primaryFire(Level level, Player player, InteractionHand usedHand) {
        ItemStack stackInHand = player.getItemInHand(usedHand);


        if(!level.isClientSide && usedHand == InteractionHand.MAIN_HAND) {
            if (isOnFiringCooldown(stackInHand)) return;
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

        }
    }




    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide) {

            double current_bloom = this.getCurrentBloom(stack);
            int firing_cooldown = this.getFiringCooldown(stack);

            if (current_bloom != 0 || firing_cooldown != 0) {
                MZGuns.LOGGER.info("\nFiring Cooldown: {}\nBloom: {}", firing_cooldown, current_bloom);
            }

            if (this.getCurrentBloom(stack) > 0D && this.getFiringCooldown(stack) <= 0) {
                this.setCurrentBloom(stack, Math.clamp((current_bloom - bloomDecayRate), 0, 1));
            }
            if (firing_cooldown > 0) {
                this.setFiringCooldown(stack, firing_cooldown - 1);
            }
        }
    }

    public void spawnPellet(Level level, Player player, InteractionHand usedHand, float yawOffset, float pitchOffset) {
        HitscanPelletEntity temp_bullet = new HitscanPelletEntity(level, player, this.baseDamage);
        temp_bullet.setPos(player.getX(), player.getEyeY() - 0.25, player.getZ());

        temp_bullet.setYRot(player.getYRot());
        temp_bullet.setXRot(player.getXRot());

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

    public final Vec3 calculateViewVector(float xRot, float yRot) {
        float f = xRot * (float) (Math.PI / 180.0);
        float f1 = -yRot * (float) (Math.PI / 180.0);
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return new Vec3((double)(f3 * f4), (double)(-f5), (double)(f2 * f4));
    }

}

