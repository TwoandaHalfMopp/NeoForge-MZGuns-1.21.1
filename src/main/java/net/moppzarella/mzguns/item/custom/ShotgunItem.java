package net.moppzarella.mzguns.item.custom;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ShotgunItem extends BaseGunItem{
    public ShotgunItem(int clipSize, float baseDamage, int firingInterval, int reloadInitialCycleLength, int reloadConsecutiveCycleLength, int reloadCycleAmount, Properties properties) {
        super(clipSize, baseDamage, firingInterval, reloadInitialCycleLength, reloadConsecutiveCycleLength, reloadCycleAmount, 0, 0, 0, properties);
    }

    @Override
    public void shootPellets(Level level, Player player, InteractionHand usedHand, ItemStack stack) {
        double pellet_radius = 2;
        double square_factor = 0.75;
        spawnPellet(level, player, usedHand, 0, 0, getBaseDamage());

        spawnPellet(level, player, usedHand, pellet_radius, 0, getBaseDamage());
        spawnPellet(level, player, usedHand, -pellet_radius, 0, getBaseDamage());
        spawnPellet(level, player, usedHand, 0, pellet_radius, getBaseDamage());
        spawnPellet(level, player, usedHand, 0, -pellet_radius, getBaseDamage());

        spawnPellet(level, player, usedHand, pellet_radius * square_factor, pellet_radius * square_factor, getBaseDamage());
        spawnPellet(level, player, usedHand, pellet_radius * square_factor, -pellet_radius * square_factor, getBaseDamage());
        spawnPellet(level, player, usedHand, -pellet_radius * square_factor, pellet_radius * square_factor, getBaseDamage());
        spawnPellet(level, player, usedHand, -pellet_radius * square_factor, -pellet_radius * square_factor, getBaseDamage());
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
