package net.moppzarella.mzguns.item.custom;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ShotgunItem extends BaseGunItem{

    public final float baseDamage;
    public final int clipSize;
    public final int firingInterval;
    public final int reloadTimeInitial;
    public final int reloadTimeConsecutive;
    public final int deployTime;
    public final int shotsPerReload;

    public final float spread;

    public ShotgunItem(float baseDamage, int clipSize, int firingInterval, int reloadTimeInitial, int reloadTimeConsecutive, float spread, int deployTime, int shotsPerReload, Properties properties) {
        super(baseDamage, clipSize, firingInterval, reloadTimeInitial, reloadTimeConsecutive, deployTime, shotsPerReload, properties);

        this.baseDamage = baseDamage;
        this.clipSize = clipSize;
        this.firingInterval = firingInterval;
        this.reloadTimeInitial = reloadTimeInitial;
        this.reloadTimeConsecutive = reloadTimeConsecutive;
        this.deployTime = deployTime;
        this.shotsPerReload = shotsPerReload;

        this.spread = spread;
    }

    @Override
    public void shootPellets(Level level, Player player, ItemStack stack) {
        double square_factor = 0.75;
        spawnPellet(level, player,stack, 0, 0, this.baseDamage);

        spawnPellet(level, player, stack, this.spread, 0, this.baseDamage);
        spawnPellet(level, player, stack, -this.spread, 0, this.baseDamage);
        spawnPellet(level, player, stack, 0, this.spread, this.baseDamage);
        spawnPellet(level, player, stack, 0, -this.spread, this.baseDamage);

        spawnPellet(level, player, stack, this.spread * square_factor, this.spread * square_factor, this.baseDamage);
        spawnPellet(level, player, stack, this.spread * square_factor, -this.spread * square_factor, this.baseDamage);
        spawnPellet(level, player, stack, -this.spread * square_factor, this.spread * square_factor, this.baseDamage);
        spawnPellet(level, player, stack, -this.spread * square_factor, -this.spread * square_factor, this.baseDamage);
    }
}
