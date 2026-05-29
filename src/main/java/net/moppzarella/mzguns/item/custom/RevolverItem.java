package net.moppzarella.mzguns.item.custom;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.moppzarella.mzguns.Config;
import net.moppzarella.mzguns.MZGuns;

public class RevolverItem extends BaseGunItem{
    public RevolverItem(int clipSize, float baseDamage, int firingInterval, int reloadCycleLength, int reloadCycleAmount, double bloomPerShot, double bloomRefreshRate, double maxBloomRadius, Properties properties) {
        super(clipSize, baseDamage, firingInterval, reloadCycleLength, reloadCycleAmount, bloomPerShot, bloomRefreshRate, maxBloomRadius, properties);
    }

    @Override
    public void primaryFire(Level level, Player player, InteractionHand usedHand) {
        ItemStack stackInHand = player.getItemInHand(usedHand);
        if(!level.isClientSide) {
            MZGuns.LOGGER.info(isOnFiringCooldown(stackInHand) + ", " + getCurrentClip(stackInHand));
            if (isOnFiringCooldown(stackInHand) || getCurrentClip(stackInHand) == 0) return;
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

            float randPitch = (float) ((Math.random() - 0.5) * 2 * (current_bloom * getMaxBloomRadius()));
            float randYaw = (float) ((Math.random() - 0.5) * 2 * (current_bloom * getMaxBloomRadius()));

            spawnPellet(level, player, usedHand, randYaw, randPitch, getBaseDamage());

            this.setFiringCooldown(stackInHand, getFiringInterval());
            this.setCurrentBloom(stackInHand, Math.clamp((current_bloom + 1.0F), 0F, 1F));
            if (!(Config.INFINITE_CLIP_IN_CREATIVE.getAsBoolean() && player.isCreative() && getClipSize() != -1)) this.setCurrentClip(stackInHand, this.getCurrentClip(stackInHand) - 1);
            this.setReloadProgress(stackInHand, 0);

        }
    }
}
