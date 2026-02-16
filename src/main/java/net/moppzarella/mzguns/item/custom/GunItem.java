package net.moppzarella.mzguns.item.custom;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.moppzarella.mzguns.MZGuns;
import net.moppzarella.mzguns.entity.custom.HitscanPelletEntity;

public class GunItem extends Item {
    public float baseDamage = 8F;

    public float bloomPerShot = 0.7F;
    public float maxBloom = 0.7F; //I don't know if this is measured in degrees or radians
    public float bloomDecayRate = 0.0625F; //The amount to subtract from current_bloom (if it's above 0) each tick
    public int firingRate = 24; //The amount of ticks between shots

    public float current_bloom = 0F;
    public int firing_cooldown = 0;

    public GunItem(Properties properties) {
        super(properties);
    }

    public void primaryFire(Level level, Player player, InteractionHand usedHand) {
        ItemStack stackInHand = player.getItemInHand(usedHand);
        //if (player.getCooldowns().isOnCooldown(stackInHand.getItem())) return;
        //player.getCooldowns().addCooldown(this, 20);

        if (this.firing_cooldown > 0) return;
        if(!level.isClientSide && usedHand == InteractionHand.MAIN_HAND) {

            level.playSound(
                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.SNOWBALL_THROW,
                    SoundSource.NEUTRAL,
                    0.5F,
                    0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

            float randPitch = (float) ((Math.random() - 0.5) * 2 * current_bloom);
            float randYaw = (float) ((Math.random() - 0.5) * 2 * current_bloom);

            //MZGuns.LOGGER.info(String.valueOf(current_bloom));

            spawnPellet(level, player, usedHand, randYaw, randPitch);

            this.firing_cooldown = firingRate;
            this.current_bloom = Math.clamp((this.current_bloom + bloomPerShot), 0F, maxBloom);

        }
    }


    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (this.current_bloom > 0F && this.firing_cooldown == 0) {
            this.current_bloom = Math.clamp(this.current_bloom - bloomDecayRate, 0F, maxBloom);
        }
        if (this.firing_cooldown > 0) {
            this.firing_cooldown -= 1;
        }
    }

    public void spawnPellet(Level level, Player player, InteractionHand usedHand, float yawOffset, float pitchOffset) {
        HitscanPelletEntity temp_bullet = new HitscanPelletEntity(level, player, this.baseDamage);
        temp_bullet.setPos(player.getX(), player.getEyeY(), player.getZ());
        temp_bullet.setXRot(player.getXRot() + pitchOffset);
        temp_bullet.setYRot(player.getYRot() + yawOffset);
        level.addFreshEntity(temp_bullet);
    }

}

