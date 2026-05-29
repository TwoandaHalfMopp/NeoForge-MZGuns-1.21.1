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
    public void shootPellets(Level level, Player player, InteractionHand usedHand, ItemStack stack) {
        float randPitch = (float) ((Math.random() - 0.5) * 2 * (this.getCurrentBloom(stack) * this.getMaxBloomRadius()));
        float randYaw = (float) ((Math.random() - 0.5) * 2 * (this.getCurrentBloom(stack) * getMaxBloomRadius()));

        spawnPellet(level, player, usedHand, randYaw, randPitch, getBaseDamage());
    }

    @Override
    public void playFiringSound(Level level, Player player, InteractionHand usedHand) {
        level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.BREEZE_WIND_CHARGE_BURST,
                SoundSource.NEUTRAL,
                0.25F,
                0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
        );
    }
}
